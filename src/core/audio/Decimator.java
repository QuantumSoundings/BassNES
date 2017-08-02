package core.audio;

import core.mappers.Mapper;
import ui.AudioInterface;

import java.io.*;



public class Decimator implements Serializable {
  
  private static final long serialVersionUID = 0;
  
  private static final double POS_ZERO = 1E-32;
  private static final double NEG_ZERO = -1E-32;
  
  private static final float I2 = (float)(1.0 / 2.0);
  private static final float I3 = (float)(1.0 / 3.0);
  private static final float I6 = (float)(1.0 / 6.0);
  
  private static final long[] LONG_NTSC_A1 = {
    0xbfff67701c84a3deL,
    0xbfff8b62242e56e4L,
    0xbfffaf25694786a4L,
    0xbfffc959eca889e2L,
    0xbfffda7652351a06L,
    0xbfffe6495d699ef1L,
    0xbfef576a39439bdaL,
  };
  
  private static final long[] LONG_NTSC_A2 = {
    0x3feed6d89d961d3bL,
    0x3fef28cd096bf76aL,
    0x3fef7a3a53cd4f67L,
    0x3fefb5a2213e3abbL,
    0x3fefdbe39ed2f06eL,
    0x3feff554ab2f0006L,    
  };
  
  private static final long[] LONG_NTSC_B1 = {
    0xbfff2a2df4fd2ad2L,
    0xbfffbe3738d86ac8L,
    0xbfffd94c2258d55cL,
    0xbfffe22ba0cf6805L,
    0xbfffe5b14c11db72L,
    0xbfffe6ffa75a14ebL,
  };
  
  private static final long LONG_NTSC_G = 0x3ecc208a2d079678L;
  
  private static final double[] NTSC_A1 = toDoubleArray(LONG_NTSC_A1);
  private static final double[] NTSC_A2 = toDoubleArray(LONG_NTSC_A2);
  private static final double[] NTSC_B1 = toDoubleArray(LONG_NTSC_B1);
  private static final double NTSC_G = Double.longBitsToDouble(LONG_NTSC_G);
  
  private static final long[] LONG_PAL_A1 = {
    0xbfff5baa1b81a309L,
    0xbfff81d9b2eb15feL,
    0xbfffa7df44761c9dL,
    0xbfffc3c2600f5556L,
    0xbfffd5ff6fbab417L,
    0xbfffe2a551b417d0L,
    0xbfef4aa6ceb6c246L,
  };
  
  private static final long[] LONG_PAL_A2 = {
    0x3feec08d9ac7eb51L,
    0x3fef18943f900329L,
    0x3fef7018a2a55ed4L,
    0x3fefaffb4199e44aL,
    0x3fefd9237788b71bL,
    0x3feff4844bf56456L,  
  };
  
  private static final long[] LONG_PAL_B1 = {
    0xbfff08b370e2ff76L,
    0xbfffb3ce724731b7L,
    0xbfffd3295d0112b5L,
    0xbfffdd7036a20286L,
    0xbfffe184a77ef413L,
    0xbfffe307f8bb95fdL,
  };
  
  private static final long LONG_PAL_G = 0x3ece3d98b822795aL;
  
  private static final double[] PAL_A1 = toDoubleArray(LONG_PAL_A1);
  private static final double[] PAL_A2 = toDoubleArray(LONG_PAL_A2);
  private static final double[] PAL_B1 = toDoubleArray(LONG_PAL_B1);
  private static final double PAL_G = Double.longBitsToDouble(LONG_PAL_G);
  
  private static final long[] LONG_DENDY_A1 = {
    0xbfff66059108b139L,
    0xbfff8a3d96b7e7c8L,
    0xbfffae475f0924b3L,
    0xbfffc8b05527ce7aL,
    0xbfffd9efd36ee392L,
    0xbfffe5dc67f7851fL,
    0xbfef55e0bcf90cfdL,
  };
  
  private static final long[] LONG_DENDY_A2 = {
    0x3feed428f05caf28L,
    0x3fef26d8c6798da7L,
    0x3fef7901f736676fL,
    0x3fefb4f3ea72096aL,
    0x3fefdb8ed8ad7cd2L,
    0x3feff53b95b7d4a9L,
  };
  
  private static final long[] LONG_DENDY_B1 = {
    0xbfff26462b398ac9L,
    0xbfffbd00c20c6ed6L,
    0xbfffd8952be0a1d8L,
    0xbfffe19e88a5e178L,
    0xbfffe534d5d43d1dL,
    0xbfffe6895c81f50fL,
  };
  
  private static final long LONG_DENDY_G = 0x3ecc61a4569367a0L;
  
  private static final double[] DENDY_A1 = toDoubleArray(LONG_DENDY_A1);
  private static final double[] DENDY_A2 = toDoubleArray(LONG_DENDY_A2);
  private static final double[] DENDY_B1 = toDoubleArray(LONG_DENDY_B1);
  private static final double DENDY_G = Double.longBitsToDouble(LONG_DENDY_G);
  
  private final double g;
  private final double[] a1;
  private final double[] a2;
  private final double[] b1;
  private final double[] d1 = new double[7];
  private final double[] d2 = new double[6];
  private final double[] ys = new double[4];
  private final float inputSamplingFrequency;
  private final float inputSamplingPeriod;
  private final float outputSamplingFrequency;
  private final float outputSamplingPeriod;
  
  public final int ACTIVITY_SAMPLES;
  public final int ACTIVITY_THRESHOLD;  
  

  
  private float volume = 1;
  private float time;
  private int index;
  
  private int lastSample;
  private int activitySum;
  private int activityCounter;
  private int inactiveSeconds;
  AudioInterface audio;
  Mapper map;
  
  // frequencies in Hz
  public Decimator( Mapper m,
      final double outputSamplingFrequency) {
    
    ACTIVITY_SAMPLES = (int)outputSamplingFrequency;
    ACTIVITY_THRESHOLD = 8 * ACTIVITY_SAMPLES;
        
    /*switch(tvSystem) {
      case PAL:
        a1 = PAL_A1;
        a2 = PAL_A2;
        b1 = PAL_B1;
        g = PAL_G;
        break;
      case Dendy:
        a1 = DENDY_A1;
        a2 = DENDY_A2;
        b1 = DENDY_B1;
        g = DENDY_G;
        break;
      default:*/
        a1 = NTSC_A1;
        a2 = NTSC_A2;
        b1 = NTSC_B1;
        g = NTSC_G;
    //    break;
    //}
    map = m;
    this.inputSamplingFrequency = 1789773.0f;//(float)tvSystem.getCyclesPerSecond();
    this.outputSamplingFrequency = (float)outputSamplingFrequency;
    inputSamplingPeriod = (float)(1.0 / inputSamplingFrequency);
    outputSamplingPeriod = (float)(1.0 / outputSamplingFrequency);
  }

  //public void setAudioProcessor(final AudioProcessor audioProcessor) {
    //this.audioProcessor = audioProcessor;
  //}
  
  private static double[] toDoubleArray(final long[] values) {
    double[] ds = new double[values.length];
    for(int i = values.length - 1; i >= 0; i--) {
      ds[i] = Double.longBitsToDouble(values[i]);
    }
    return ds;
  }  

  public float getVolume() {
    return volume;
  }
  
  public void clearInactiveSeconds() {
    inactiveSeconds = lastSample = activitySum = activityCounter = 0;
  }
  
  public int getInactiveSeconds() {
    return inactiveSeconds;
  }

  public void setVolume(final float volume) {
    if (volume < 0) {
      this.volume = 0f;
    } else if (volume > 1) {
      this.volume = 1f;
    } else {
      this.volume = volume;
    }
  }
  
  public void hold(final float duration) {
    time += duration;
    final int sampleCount = (int)Math.floor(time * outputSamplingFrequency);
    time -= sampleCount * outputSamplingPeriod;                          
    //for(int i = sampleCount - 1; i >= 0; i--) {
    //  audioProcessor.processOutputSample(lastSample);
    //}
  }
  
  public void addInputSample(double x) {
    
    double y;
    for(int i = 0; i < 6; i++) {
      y = x + d1[i];  
      d1[i] = b1[i] * x - a1[i] * y + d2[i];
      if (d1[i] != d1[i] || (d1[i] > NEG_ZERO && d1[i] < POS_ZERO)) {
        d1[i] = 0;
      }
      d2[i] = x - a2[i] * y;
      if (d2[i] != d2[i] || (d2[i] > NEG_ZERO && d2[i] < POS_ZERO)) {
        d2[i] = 0;
      }
      x = y;
      if (x != x || (x > NEG_ZERO && x < POS_ZERO)) {
        x = 0;
      }
    }
    y = x + d1[6];
    d1[6] = x - a1[6] * y;
    if (d1[6] != d1[6] || (d1[6] > NEG_ZERO && d1[6] < POS_ZERO)) {
      d1[6] = 0;
    }
    ys[index] = g * y;
    if (ys[index] != ys[index] 
        || (ys[index] > NEG_ZERO && ys[index] < POS_ZERO)) {
      ys[index] = 0;
    }
    
    time += inputSamplingPeriod;
    if (time >= outputSamplingPeriod) {      
      final int sample = interpolate(
          1f - (time - outputSamplingPeriod) * inputSamplingFrequency, 
          (float)ys[(index - 3) & 3],
          (float)ys[(index - 2) & 3],
          (float)ys[(index - 1) & 3],
          (float)ys[index]);      
      map.apu.mixer.addSample(sample);
      map.apu.mixer.addSample(sample);

      time -= outputSamplingPeriod;
      
      final int delta = sample - lastSample;
      if (delta < 0) {
        activitySum -= delta;
      } else {
        activitySum += delta;
      }
      if (++activityCounter == ACTIVITY_SAMPLES) {
        inactiveSeconds = (activitySum < 0 || activitySum >= ACTIVITY_THRESHOLD) 
            ? 0 : (inactiveSeconds + 1);
        activitySum = activityCounter = 0;      
      }
      lastSample = sample;
    }
    
    index = (index + 1) & 3;
  }
  
  private int interpolate(final float t, final float y0, final float y1, 
      final float y2, final float y3) {
    final float c0 = y1;
    final float c1 = y2 - I3 * y0 - I2 * y1 - I6 * y3;
    final float c2 = I2 * (y0 + y2) - y1;
    final float c3 = I6 * (y3 - y0) + I2 * (y1 - y2);
    int value = (int)((((c3 * t + c2) * t + c1) * t + c0) * volume);
    if (value < -32768) {
      value = -32768;
    } else if (value > 32767) {
      value = 32767;
    }
    return value;
  }
}