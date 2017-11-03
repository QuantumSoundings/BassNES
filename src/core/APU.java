package core;
import java.util.ArrayList;

import core.CPU_6502.IRQSource;
import core.audio.*;
import core.mappers.Mapper;


public class APU implements java.io.Serializable{
	private static final long serialVersionUID = 2400733667863038798L;
	
	private final Mapper map;
	//Default Audio Channels
	private Triangle triangle;
	private Pulse pulse1;
	private Pulse pulse2;
	private Noise noise;
	private DMC dmc;
	//Expansion Audio Channels
	private ArrayList<Channel> expansionAudio;
	//private Channel[] expansionAudioArray;
	//public AudioInterface audio;
	
	//Mixing Variables
	private double cyclespersample;
	//private int intcyclespersample;
	private double samplenum;
	public int[] samples;
	int sampleptr;
	public final AudioMixer mixer;
	
	
	

	private boolean stepmode4=true;
	int stepcycle;
	private boolean irqInhibit;
	private boolean frameInterrupt;
	boolean doFrameStep;
	private boolean evenclock=false;
	private int block;
	private int stepNumber;
	private int delay=-1;
	public int framecounter;
	private int cpucounter;
	public long cyclenum;
	private boolean expansion;

	
	public APU(Mapper m){
		pulse1 = new Pulse(true,0);
		pulse2 = new Pulse(false,1);
		triangle = new Triangle(2);
		noise = new Noise(3);
		map = m;
		dmc =new DMC(map,4);
		expansionAudio = new ArrayList<Channel>();
		//expansionAudioArray = new Channel[0];
		cyclespersample = 1789773.0/NesSettings.sampleRate;
		//intcyclespersample = (int)cyclespersample;
		cpucounter = 10;
		expansion = false;
		writeRegister(0x4015,(byte)0);
		freq = new Object[3][2];
		samples = new int[1000];
		mixer = new AudioMixer(pulse1,pulse2,triangle,noise,dmc,expansionAudio,m);
	}

	public void addExpansionChannel(Channel chan){
		expansionAudio.add(chan);
		expansion = true;
		freq = new Object[3+expansionAudio.size()][];
	}
	public int requestNewOutputLocation(){
		return mixer.requestNewOutputLocation();
	}

	public void setSampleRate(int rate){
		cyclespersample = 1789773.0/rate;
		//intcyclespersample = (int)cyclespersample;
		mixer.updateAudioSettings();
		//resampler=new Decimator(map,rate);
	}
	public void writeRegister(int index,byte b){
		//System.out.println("Writing to apu register");
		if(index>=0x4000&&index<0x4004){
			pulse1.registerWrite(index, b,cpucounter);
		}
		else if(index>=0x4004&&index<0x4008){
			pulse2.registerWrite(index, b,cpucounter);
		}
		else if(index>=0x4008&&index<0x400c){
			triangle.registerWrite(index, b,cpucounter);
		}
		else if(index>=0x400c&&index<0x4010){
			noise.registerWrite(index, b,cpucounter);			
		}
		else if(index>=0x4010&&index<0x4014){
			dmc.registerWrite(index, b);
		}
		else if(index==0x4015){
			if((b&1)==0)
				pulse1.disable();
			else
				pulse1.enable();
			if((b&2)==0)
				pulse2.disable();
			else
				pulse2.enable();
			if((b&4)==0)
				triangle.disable();
			else
				triangle.enable();
			if((b&8)==0)
				noise.disable();
			else
				noise.enable();
			if((b&16)==0){
				dmc.sampleremaining=0;
				dmc.silence = true;
			}
			else{
				if(dmc.sampleremaining==0){
					dmc.restart();
				}
			}
			dmc.clearFlag();
		}
		else if(index==0x4017){
			stepmode4 = (b & 0x80) == 0;
			stepNumber = 0;
			if((b&0x80)!=0){
				stepNumber =2;
				frameClock();
				cpucounter=0;
				block=1;
			}
			irqInhibit = (b & 0x40) != 0;
			if(irqInhibit&&frameInterrupt){
				frameInterrupt = false;
				map.cpu.removeIRQ(IRQSource.FrameCounter);
			}
			if(!evenclock)
				delay = 0;
			else
				delay = 1;
			cpucounter=0;
		}
	}
	public byte readRegisters(int index){
		if(index == 0x4015){			
			byte b = 0;
			b|= pulse1.lengthCount >0?1:0;
			b|= pulse2.lengthCount >0?2:0;
			b|= triangle.lengthCount >0?4:0;
			b|= noise.lengthCount >0?8:0;
			b|= dmc.sampleremaining>0?16:0;
			b|= frameInterrupt?64:0;
			frameInterrupt = false;
			map.cpu.removeIRQ(IRQSource.FrameCounter);
			b|= dmc.irqflag?128:0;
			return b;
		}
		return 0;
	}

	private void frameClock(){
		if(stepmode4){
			if(stepNumber%4==1||stepNumber%4==3){
				pulse1.lengthClock();
				pulse1.sweepClock();
				pulse2.lengthClock();
				pulse2.sweepClock();			
				triangle.lengthClock();
				noise.lengthClock();
			}
			
			pulse1.envelopeClock();
			pulse2.envelopeClock();
			triangle.linearClock();
			noise.envelopeClock();
			if(stepNumber%4==3){
				setIRQ();
			}
		}
		else{
			if(stepNumber ==2){
				pulse1.lengthClock();
				pulse1.sweepClock();
				pulse2.lengthClock();
				pulse2.sweepClock();
				triangle.lengthClock();
				noise.lengthClock();
				
				pulse1.envelopeClock();
				pulse2.envelopeClock();
				triangle.linearClock();
				noise.envelopeClock();
			}
			else if(stepNumber ==1){
				pulse1.envelopeClock();
				pulse2.envelopeClock();
				triangle.linearClock();
				noise.envelopeClock();
			}
			else{}
		}
	}
	private void setIRQ(){
		if(!irqInhibit){
			frameInterrupt=true;
			map.cpu.setIRQ(IRQSource.FrameCounter);
		}
	}
	Object[][] freq;
	public Object[][] channelInfo(){
		freq[0] = pulse1.getInfo();
		freq[1] = pulse2.getInfo();
		freq[2] = triangle.getInfo();
		int i = 3;
		for(Channel chan: expansionAudio)
			freq[i++] = chan.getInfo();
		return freq;
	}
	//Audio Mixing methods
	/*final double getAverageSample(Channel chan,int UserMix){
		double d =((chan.total/cyclespersample)*(UserMix/100.0));
		chan.total=0;
		return d;
	}
	final double getAverageExpansion(Channel chan,int UserMix){
		double d =((chan.getOutput()/(NesSettings.highQualitySampling?1:cyclespersample))*(UserMix/100.0));
		chan.total = 0;
		//System.out.println(d);
		return d;
	}*/
	//public void resetAudioBuffer(){
	//	samples = new int[NesSettings.sampleRate/60+10];
	//	sampleptr = 0;
	//}
	/*public void sendOutput(){
		if(NesSettings.highQualitySampling){
			double pulse_out = (95.88/(8128.0/(pulse1.getOutput()+pulse2.getOutput())+100));//0.00752 * (p1+p2);
			double tnd_out = (163.67/(24329.0/(3*triangle.getOutput()+2*noise.getOutput()+dmc.getOutput())+100));//0.00851*t + 0.00494*n + 0.00335*d;
			double sample = pulse_out + tnd_out;
			double expansion = 0;
			for(Channel chan: expansionAudio)
				expansion+= getAverageExpansion(chan,chan.getUserMixLevel());
			sample+=expansion;
			resampler.addInputSample((sample * 30000) * (NesSettings.masterMixLevel / 100.0));
		}
		else {
			double p1 = getAverageSample(pulse1, NesSettings.pulse1MixLevel);
			double p2 = getAverageSample(pulse2, NesSettings.pulse2MixLevel);
			double t = getAverageSample(triangle, NesSettings.triangleMixLevel);
			double n = getAverageSample(noise, NesSettings.noiseMixLevel);
			double d = getAverageSample(dmc, NesSettings.dmcMixLevel);
			double pulse_out = (95.88/(8128.0/(p1+p2)+100));//0.00752 * (p1+p2);
			double tnd_out = (163.67/(24329.0/(3*t+2*n+d)+100));//0.00851*t + 0.00494*n + 0.00335*d;
			double sample = pulse_out + tnd_out;
			double expansion = 0;
			for(Channel chan: expansionAudio)
				expansion+= getAverageExpansion(chan,chan.getUserMixLevel());
			sample+=expansion;

			sample = ((sample*30000)*(NesSettings.masterMixLevel/100.0));
			map.system.audioSampleCallback((int)sample);
		}
		//sample = ((sample*30000)*(NesSettings.masterMixLevel/100.0));
		//try{
		//samples[sampleptr++] = (int) sample;
		//}catch(Exception e){
		//	System.out.println(sampleptr +" "+ (UserSettings.sampleRate/60.0));
		//}
		//map.system.audioSampleCallback((int)sample);
		//System.out.println(sample);
	}*/
	/*private void sendOutputUnMixed(){
		int[] output = new int[5+expansionAudioArray.length];
		output[0] = (int) getAverageSample(pulse1,UserSettings.pulse1MixLevel);
		output[1] = (int) getAverageSample(pulse2,UserSettings.pulse2MixLevel);
		output[2] = (int) getAverageSample(triangle,UserSettings.triangleMixLevel);
		output[3] = (int) getAverageSample(noise,UserSettings.noiseMixLevel);
		output[4] = (int) getAverageSample(dmc,UserSettings.dmcMixLevel);
		int i = 5;
		for(Channel chan: expansionAudioArray)
			output[i++] = (int) getAverageExpansion(chan,chan.getUserMixLevel());
		map.system.unmixedAudioSampleCallback(output);
	}*/
	public static int samplecounter=0;
	public void doCycle(){
		if(delay>0){
			delay--;
		}
		else if(delay ==0){
			cpucounter=0;
			delay =-1;
		}

		triangle.clockTimer();
		noise.clockTimer();
		dmc.clockTimer();
		if(evenclock){
			pulse1.clockTimer();
			pulse2.clockTimer();
			cyclenum++;
		}
		evenclock=!evenclock;
		if(expansion)
			for(Channel chan:expansionAudio)
				chan.clockTimer();
		if(stepmode4){
			switch(cpucounter){
			case 7459: stepNumber = 0;frameClock();break;
			case 14915:stepNumber = 1;frameClock();break;
			case 22373:stepNumber = 2;frameClock();break;
			case 29830:
                setIRQ();break;
			case 29831:stepNumber = 3;frameClock();break;
			case 29832:setIRQ();break;
			case 37289:stepNumber = 0;frameClock();cpucounter=7459;break;
			default: break;
			}
		}
		else{
			switch(cpucounter){
			case 1:
				if(block<=0){
					stepNumber = 2;
					frameClock();
				}
				else 
					block--;
				break;
			case 7459:  stepNumber = 1;frameClock();break;
			case 14915: stepNumber = 2;frameClock();break;
			case 22373: stepNumber = 1;frameClock();break;
			case 29829: break;
			case 37283: stepNumber = 2;frameClock();cpucounter = 1;break;
			default:break;
			}
		}
		cpucounter++;
		samplenum++;
		samplecounter++;
		if(NesSettings.highQualitySampling)
			mixer.mixHighQualitySample();
		else if(samplenum-cyclespersample>0){
			mixer.mixSample();
			samplecounter=0;
			samplenum-=cyclespersample;
		}
	}
}
