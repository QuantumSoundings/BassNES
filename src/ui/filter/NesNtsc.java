package ui.filter;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import core.NesSettings;
import core.video.NesColors;
import ui.UISettings;

// Port of nes_ntsc 0.2.2 by Blargg (Shay Green). 
// http://www.slack.net/~ant/libs/ntsc.html#nes_ntsc
// GNU Lesser General Public License


public final class NesNtsc{
  
  /* Image parameters, ranging from -1.0 to 1.0. Actual internal values shown
  in parenthesis and should remain fairly stable in future versions. */
	private final BufferedImage image;
	 protected final int[] out;
  public static class nes_ntsc_setup_t {

    public nes_ntsc_setup_t(
        final double hue, 
        final double saturation, 
        final double contrast, 
        final double brightness, 
        final double sharpness, 
        final double gamma, 
        final double resolution, 
        final double artifacts, 
        final double fringing, 
        final double bleed, 
        final boolean merge_fields, 
        final float[] decoder_matrix, 
        final int[] palette_out, 
        final int[] palette, 
        final int[] base_palette) {
      this.hue = hue;
      this.saturation = saturation;
      this.contrast = contrast;
      this.brightness = brightness;
      this.sharpness = sharpness;
      this.gamma = gamma;
      this.resolution = resolution;
      this.artifacts = artifacts;
      this.fringing = fringing;
      this.bleed = bleed;
      this.merge_fields = merge_fields;
      this.decoder_matrix = decoder_matrix;
      this.palette_out = palette_out;
      this.palette = palette;
      this.base_palette = base_palette;
    }

    /* Basic parameters */
    double hue;        /* -1 = -180 degrees     +1 = +180 degrees */
    double saturation; /* -1 = grayscale (0.0)  +1 = oversaturated colors (2) */
    double contrast;   /* -1 = dark (0.5)       +1 = light (1.5) */
    double brightness; /* -1 = dark (0.5)       +1 = light (1.5) */
    double sharpness;  /* edge contrast enhancement/blurring */

    /* Advanced parameters */
    double gamma;      /* -1 = dark (1.5)       +1 = light (0.5) */
    double resolution; /* image resolution */
    double artifacts;  /* artifacts caused by color changes */
    double fringing;   /* color artifacts caused by brightness changes */
    double bleed;      /* color bleed (color resolution reduction) */
    boolean merge_fields;  /* if true, merges even and odd fields together to 
                              reduce flicker */

    float[] decoder_matrix; /* optional RGB decoder matrix, 6 elements */

    int[] palette_out;  /* optional RGB palette out, 3 bytes per color */

    /* You can replace the standard NES color generation with an RGB palette. 
    The first replaces all color generation, while the second replaces only the 
    core 64-color generation and does standard color emphasis calculations on 
    it. */
    int[] palette;/* optional 512-entry RGB palette in, 3 bytes per color */
    int[] base_palette;/* optional 64-entry RGB palette in, 3 bytes per color */  
  }  
  
  private static class init_t {
    final float[] to_rgb = new float[burst_count * 6];
    final float[] to_float = new float[gamma_size];
    float contrast;
    float brightness;
    float artifacts;
    float fringing;
    final float[] kernel = new float[rescale_out * kernel_size * 2];
  }
  
  private static class pixel_info_t {
    
    public pixel_info_t(final int ntsc, final int scaled, 
        final float[] kernel) {
      if (rescale_in > 1) {
        offset = PIXEL_OFFSET_((ntsc - scaled / rescale_out * rescale_in), 
            (scaled + rescale_out * 10) % rescale_out);
      } else {
        offset = kernel_size / 2 + ntsc - scaled;        
      }
      negate = 1.0f - ((ntsc + 100) & 2);
      this.kernel = kernel;
    }
    
    private int PIXEL_OFFSET_(final int ntsc, final int scaled) {
      return (kernel_size / 2 + ntsc + ((scaled != 0) ? 1 : 0) 
          + (rescale_out - scaled) % rescale_out + (kernel_size * 2 * scaled));
    }
    
    int offset;
    float negate;
    final float[] kernel;
  }  
  
  private static final float[] default_decoder 
      = { 0.956f, 0.621f, -0.272f, -0.647f, -1.105f, 1.702f };
  private static final float[] lo_levels = { -0.12f, 0.00f, 0.31f, 0.72f };
  private static final float[] hi_levels = {  0.40f, 0.68f, 1.00f, 1.00f };
  
  /* phases [i] = cos( i * PI / 6 ) */
  private static final float[] phases = {
    -1.0f, -0.866025f, -0.5f, 0.0f,  0.5f,  0.866025f,
     1.0f,  0.866025f,  0.5f, 0.0f, -0.5f, -0.866025f,
    -1.0f, -0.866025f, -0.5f, 0.0f,  0.5f,  0.866025f,
     1.0f
  };
  
  /* Video format presets */
  
  /* desaturated + artifacts */
  private static final nes_ntsc_setup_t nes_ntsc_monochrome 
      = new nes_ntsc_setup_t(0, -1, 0, 0, .2, 0, .2, -.2, -.2, -1, true, null, 
          null, null, null);  
  
  /* color bleeding + artifacts */
  private static final nes_ntsc_setup_t nes_ntsc_composite 
      = new nes_ntsc_setup_t(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, true, null, null, 
          null, null);  
  
  /* color bleeding only */
  private static final nes_ntsc_setup_t nes_ntsc_svideo = new nes_ntsc_setup_t(
      0, 0, 0, 0, .2, 0, .2, -1, -1, 0, true, null, null, null, null);
  
  /* crisp image */
  private static final nes_ntsc_setup_t nes_ntsc_rgb = new nes_ntsc_setup_t(0, 
      0, 0, 0, .2, 0, .7, -1, -1,-1, true, null, null, null, null); 
  
  /* Set to true to enable emphasis support and use a 512 color palette instead
  of the base 64 color palette. */
  private static boolean NES_NTSC_EMPHASIS = true;

  private static final int alignment_count = 3;
  private static final int burst_count = 3;
  private static final int rescale_in = 8;
  private static final int rescale_out = 7;

  private static final float artifacts_mid = 1.0f;
  private static final float fringing_mid = 1.0f;
  private static final int std_decoder_hue = -15;
  
  private static final boolean DISABLE_CORRECTION = false;
  private static final int default_palette_contrast = 0;
  
  private static final float PI = 3.14159265358979323846f;
  private static final float LUMA_CUTOFF = 0.20f;
  private static final int gamma_size = 1;
  private static final int rgb_bits = 8;
  private static final float artifacts_max = artifacts_mid * 1.5f;
	private static final float fringing_max = fringing_mid * 2f;
  
  private static final int ext_decoder_hue = std_decoder_hue + 15;
  private static final int rgb_unit = 1 << rgb_bits;
	private static final float rgb_offset = rgb_unit * 2 + 0.5f;
  
  private static final int nes_ntsc_min_in_width = 256;
  private static final int nes_ntsc_min_out_width 
      = NES_NTSC_OUT_WIDTH(nes_ntsc_min_in_width);

  private static final int nes_ntsc_640_in_width  = 271;
  private static final int nes_ntsc_640_out_width 
      = NES_NTSC_OUT_WIDTH(nes_ntsc_640_in_width);
  private static final int nes_ntsc_640_overscan_left = 8;
  private static final int nes_ntsc_640_overscan_right = nes_ntsc_640_in_width 
      - 256 - nes_ntsc_640_overscan_left;

  private static final int nes_ntsc_full_in_width  = 283;
  private static final int nes_ntsc_full_out_width 
      = NES_NTSC_OUT_WIDTH(nes_ntsc_full_in_width);
  private static final int nes_ntsc_full_overscan_left = 16;
  private static final int nes_ntsc_full_overscan_right = nes_ntsc_full_in_width
      - 256 - nes_ntsc_full_overscan_left;  
  
  private static final int nes_ntsc_palette_size = NES_NTSC_EMPHASIS 
      ? 64 * 8 : 64;
  
  private static final float to_float = 1.0f / 0xFF;
  private static final float atten_mul = 0.79399f;
  private static final float atten_sub = 0.0782838f; 
  
  private static final int[] tints = { 0, 6, 10, 8, 2, 4, 0, 0 };
  
  /* Interface for user-defined custom blitters */
  
  /* number of input pixels read per chunk */
  private static final int nes_ntsc_in_chunk = 3; 
  
  /* number of output pixels generated per chunk */
  private static final int nes_ntsc_out_chunk = 7; 
  
  /* palette index for black */
  private static final int nes_ntsc_black = 15; 
  
  /* burst phase cycles through 0, 1, and 2 */  
  private static final int nes_ntsc_burst_count = 3; 
  
  /* private */
  private static final int nes_ntsc_entry_size = 128;
  private static final int nes_ntsc_burst_size = nes_ntsc_entry_size 
      / nes_ntsc_burst_count;    
  private static final int burst_size  = nes_ntsc_entry_size / burst_count;
  private static final int kernel_half = 16;
  private static final int kernel_size = kernel_half * 2 + 1;
  
  /* common ntsc macros */
  private static final int nes_ntsc_rgb_builder 
      = (1 << 21) | (1 << 11) | (1 << 1);
  private static final int nes_ntsc_clamp_mask = nes_ntsc_rgb_builder * 3 / 2;
  private static final int nes_ntsc_clamp_add = nes_ntsc_rgb_builder * 0x101;   

  private static final int rgb_kernel_size = burst_size / alignment_count;
  private static final int rgb_bias = rgb_unit * 2 * nes_ntsc_rgb_builder;
  
  public static final int[] nes_ntsc_t 
      = new int[nes_ntsc_palette_size * nes_ntsc_entry_size];

  /* 3 input pixels -> 8 composite samples */
  private static final pixel_info_t[] nes_ntsc_pixels = {
    new pixel_info_t(-4, -9, new float[] { 1f, 1f, .6667f, 0f }),  
    new pixel_info_t(-2, -7, new float[] { .3333f, 1f, 1f, .3333f }),  
    new pixel_info_t( 0, -5, new float[] { 0f, .6667f, 1f, 1f }),  
  };
  
  private static float TO_ANGLE_SIN(final int color) {
    return phases[color];
  }
  
  private static float TO_ANGLE_COS(final int color) {
    return phases[color + 3];
  }
  
  private static int NES_NTSC_CLAMP_(int io, final int shift) {
    final int sub = io >> (9 - shift) & nes_ntsc_clamp_mask;
    int clamp = nes_ntsc_clamp_add - sub;
    io |= clamp;
    clamp -= sub;
    return io & clamp;
  }   
  
  private static boolean STD_HUE_CONDITION(nes_ntsc_setup_t setup) {
    return setup.base_palette == null && setup.palette == null;
  }
  
  /* Number of output pixels written by blitter for given input width. Width 
  might be rounded down slightly; use NES_NTSC_IN_WIDTH() on result to find 
  rounded value. Guaranteed not to round 256 down at all. */  
  private static int NES_NTSC_OUT_WIDTH(final int in_width) {
    return ((in_width - 1) / nes_ntsc_in_chunk + 1) * nes_ntsc_out_chunk;
  }
  
  /* Number of input pixels that will fit within given output width. Might be
  rounded down slightly; use NES_NTSC_OUT_WIDTH() on result to find rounded
  value. */
  private static int NES_NTSC_IN_WIDTH(final int out_width) {
    return (out_width / nes_ntsc_out_chunk - 1) * nes_ntsc_in_chunk + 1;
  }
  
  private static int PACK_RGB(final int r, final int g, final int b) {
    return (r << 21) | (g << 11) | (b << 1);
  }
  
  private static void init_filters(final init_t impl, 
      final nes_ntsc_setup_t setup) {
    
    final float[] kernels;
    if (rescale_out > 1) {
      kernels = new float[kernel_size * 2];
    } else {
      kernels = impl.kernel;
    }

    /* generate luma (y) filter using sinc kernel */
    {
      /* sinc with rolloff (dsf) */
      final float rolloff = 1f + (float)setup.sharpness * 0.032f;
      final float maxh = 32f;
      final float pow_a_n = (float)Math.pow(rolloff, maxh);
      float sum;
      int i;
      /* quadratic mapping to reduce negative (blurring) range */
      float to_angle = (float)setup.resolution + 1f;
      to_angle = PI / maxh * (float)LUMA_CUTOFF * (to_angle * to_angle + 1);
		
      kernels[kernel_size * 3 / 2] = maxh; /* default center value */
      for(i = 0; i < kernel_half * 2 + 1; i++) {
        final int x = i - kernel_half;
        final float angle = x * to_angle;
        /* instability occurs at center point with rolloff very close to 1.0 */
        if (x != 0 || pow_a_n > 1.056f || pow_a_n < 0.981f) {
          float rolloff_cos_a = rolloff * (float)Math.cos(angle);
          final float num = 1f - rolloff_cos_a 
              - pow_a_n * (float)Math.cos(maxh * angle) 
              + pow_a_n * rolloff * (float)Math.cos((maxh - 1f) * angle);
          final float den = 1f - rolloff_cos_a - rolloff_cos_a + rolloff 
              * rolloff;
          final float dsf = num / den;
          kernels[kernel_size * 3 / 2 - kernel_half + i] = dsf - 0.5f;
        }
      }
		
      /* apply blackman window and find sum */
      sum = 0;
      for(i = 0; i < kernel_half * 2 + 1; i++) {
        final float x = PI * 2 / (kernel_half * 2) * i;
        final float blackman = 0.42f - 0.5f * (float)Math.cos(x) 
            + 0.08f * (float)Math.cos(x * 2);
        sum += (kernels[kernel_size * 3 / 2 - kernel_half + i] *= blackman);
      }
		
      /* normalize kernel */
      sum = 1.0f / sum;
      for (i = 0; i < kernel_half * 2 + 1; i++) {
        final int x = kernel_size * 3 / 2 - kernel_half + i;
        kernels[x] *= sum;        
      }
    }

    /* generate chroma (iq) filter using gaussian kernel */
    {
      final float cutoff_factor = -0.03125f;
      float cutoff = (float)setup.bleed;
      int i;

      if (cutoff < 0) {
        /* keep extreme value accessible only near upper end of scale (1.0) */
        cutoff *= cutoff;
        cutoff *= cutoff;
        cutoff *= cutoff;
        cutoff *= -30.0f / 0.65f;
      }
      cutoff = cutoff_factor - 0.65f * cutoff_factor * cutoff;

      for(i = -kernel_half; i <= kernel_half; i++) {
        kernels[kernel_size / 2 + i] = (float)Math.exp(i * i * cutoff);
      }

      /* normalize even and odd phases separately */
      for(i = 0; i < 2; i++) {
        float sum = 0;
        int x;
        for (x = i; x < kernel_size; x += 2) {
          sum += kernels[x];
        }

        sum = 1.0f / sum;
        for (x = i; x < kernel_size; x += 2) {
          kernels[x] *= sum;
        }
      }
    }
	
    /* generate linear rescale kernels */
    if (rescale_out > 1) {
      float weight = 1.0f;
      final float[] out = impl.kernel;
      int outIndex = 0;
      int n = rescale_out;
      do {
        float remain = 0;
        int i;
        weight -= 1.0f / rescale_in;
        for(i = 0; i < kernel_size * 2; i++) {
          final float cur = kernels[i];
          final float m = cur * weight;
          out[outIndex++] = m + remain;
          remain = cur - m;
        }
      } while(--n != 0);
    }
  }
  
  private static void init(final init_t impl, final nes_ntsc_setup_t setup) {
    impl.brightness = (float)setup.brightness * (0.5f * rgb_unit) + rgb_offset;
    impl.contrast = (float)setup.contrast * (0.5f * rgb_unit) + rgb_unit;
    if (default_palette_contrast != 0 && setup.palette == null) {
      impl.contrast *= default_palette_contrast;
    }

    impl.artifacts = (float)setup.artifacts;
    if (impl.artifacts > 0) {
      impl.artifacts *= artifacts_max - artifacts_mid;
    }
    impl.artifacts = impl.artifacts * artifacts_mid + artifacts_mid;

    impl.fringing = (float) setup.fringing;
    if (impl.fringing > 0) {
      impl.fringing *= fringing_max - fringing_mid;
    }
    impl.fringing = impl.fringing * fringing_mid + fringing_mid;

    init_filters(impl, setup);

    /* generate gamma table */
    if (gamma_size > 1) {
      final float to_float = 1.0f / (gamma_size - (gamma_size > 1 ? 1 : 0));
      final float gamma = 1.1333f - (float) setup.gamma * 0.5f;
      /* match common PC's 2.2 gamma to TV's 2.65 gamma */
      int i;
      for(i = 0; i < gamma_size; i++) {
        impl.to_float[i] = (float)Math.pow(i * to_float, gamma) * impl.contrast
            + impl.brightness;
      }
    }

    /* setup decoder matricies */
    {
      float hue = (float)setup.hue * PI + PI / 180 * ext_decoder_hue;
      float sat = (float)setup.saturation + 1;
      float[] decoder = setup.decoder_matrix;
      if (decoder == null) {
        decoder = default_decoder;
        if (STD_HUE_CONDITION(setup)) {
          hue += PI / 180 * (std_decoder_hue - ext_decoder_hue);
        }
      }

      {
        float s = (float)Math.sin(hue) * sat;
        float c = (float)Math.cos(hue) * sat;
        final float[] out = impl.to_rgb;
        int outIndex = 0;
        int n = burst_count;
        do {
          final float[] in = decoder;
          int inIndex = 0;
          int m = 3;
          do {
            float i = in[inIndex++];
            float q = in[inIndex++];
            out[outIndex++] = i * c - q * s;
            out[outIndex++] = i * s + q * c;
          }
          while(--m != 0);
          if (burst_count <= 1) {
            break;
          }
          
          {
            /* rotate IQ +120 degrees */
            final float t = s * -0.5f - c * 0.866025f;
            c = s * 0.866025f + c * -0.5f;
            s = t;
          }          
        } while (--n != 0);
      }
    }
  }  
  
  /* Generate pixel at all burst phases and column alignments */
  private static void gen_kernel(final init_t impl, float y, float i, float q, 
      final int[] out, int outIndex) {
    
    /* generate for each scanline burst phase */
    final float[] to_rgb = impl.to_rgb;
    int to_rgbIndex = 0;
    int burst_remain = burst_count;
    y -= rgb_offset;
    do {
      /* Encode yiq into *two* composite signals (to allow control over 
      artifacting). Convolve these with kernels which: filter respective 
      components, apply sharpening, and rescale horizontally. Convert resulting
      yiq to rgb and pack into integer. Based on algorithm by NewRisingSun. */
      
      int pixelIndex = 0;      
      int alignment_remain = alignment_count;
      do {
        /* negate is -1 when composite starts at odd multiple of 2 */
        final pixel_info_t pixel = nes_ntsc_pixels[pixelIndex++];
        final float yy = y * impl.fringing * pixel.negate;
        final float ic0 = (i + yy) * pixel.kernel[0];
        final float qc1 = (q + yy) * pixel.kernel[1];
        final float ic2 = (i - yy) * pixel.kernel[2];
        final float qc3 = (q - yy) * pixel.kernel[3];

        final float factor = impl.artifacts * pixel.negate;
        final float ii = i * factor;
        final float yc0 = (y + ii) * pixel.kernel[0];
        final float yc2 = (y - ii) * pixel.kernel[2];

        final float qq = q * factor;
        final float yc1 = (y + qq) * pixel.kernel[1];
        final float yc3 = (y - qq) * pixel.kernel[3];

        final float[] k = impl.kernel;
        int kOffset = pixel.offset;
        int n;
        for(n = rgb_kernel_size; n != 0; n--) {
          float I = k[kOffset] * ic0 + k[kOffset + 2] * ic2;
          float Q = k[kOffset + 1] * qc1 + k[kOffset + 3] * qc3;
          float Y = k[kOffset + kernel_size] * yc0 
                  + k[kOffset + kernel_size + 1] * yc1 
                  + k[kOffset + kernel_size + 2] * yc2 
                  + k[kOffset + kernel_size + 3] * yc3 
                  + rgb_offset;
          if (rescale_out <= 1) {
            kOffset--;
          } else if (kOffset < kernel_size * 2 * (rescale_out - 1)) {
            kOffset += kernel_size * 2 - 1;
          } else {
            kOffset -= kernel_size * 2 * (rescale_out - 1) + 2;
          }
          {
            final int r = (int)(Y + to_rgb[to_rgbIndex] * I 
                + to_rgb[to_rgbIndex + 1] * Q);
            final int g = (int)(Y + to_rgb[to_rgbIndex + 2] * I 
                + to_rgb[to_rgbIndex + 3] * Q);
            final int b = (int)(Y + to_rgb[to_rgbIndex + 4] * I 
                + to_rgb[to_rgbIndex + 5] * Q);            
            out[outIndex++] = PACK_RGB(r, g, b) - rgb_bias;
          }
        }
      } while (alignment_count > 1 && --alignment_remain != 0);

      if (burst_count <= 1) {
        break;
      }

      to_rgbIndex += 6;

      /* ROTATE_IQ -120 degrees */
      {
        float t = i * -0.5f - q * -0.866025f;
        q = i * -0.866025f + q * -0.5f;
        i = t;
      }      
      
    } while (--burst_remain != 0);
  }  
  
  private static void merge_kernel_fields(final int[] io, int ioIndex) {
    for(int n = burst_size; n != 0; n--) {
      final int p0 = io[ioIndex] + rgb_bias;
      final int p1 = io[ioIndex + burst_size * 1] + rgb_bias;
      final int p2 = io[ioIndex + burst_size * 2] + rgb_bias;
      /* merge colors without losing precision */
      io[ioIndex]
          = ((p0 + p1 - ((p0 ^ p1) & nes_ntsc_rgb_builder)) >> 1) - rgb_bias;
      io[ioIndex + burst_size]
          = ((p1 + p2 - ((p1 ^ p2) & nes_ntsc_rgb_builder)) >> 1) - rgb_bias;
      io[ioIndex + burst_size * 2] 
          = ((p2 + p0 - ((p2 ^ p0) & nes_ntsc_rgb_builder)) >> 1) - rgb_bias;
      ioIndex++;
    }
  } 
  
  private static void correct_errors(final int color, final int[] out,
      int outIndex) {    
    for(int n = burst_count; n != 0; n--) {
      for(int i = 0; i < rgb_kernel_size / 2; i++) {
        if (DISABLE_CORRECTION) {
          out[outIndex + i] += rgb_bias;
        } else {
          final int error = color 
              - out[outIndex + i] 
              - out[outIndex + (i + 12) % 14 + 14] 
              - out[outIndex + (i + 10) % 14 + 28] 
              - out[outIndex + i + 7] 
              - out[outIndex + i + 19] 
              - out[outIndex + i + 31];
          int fourth = (error + 2 * nes_ntsc_rgb_builder) >> 2;
          fourth &= (rgb_bias >> 1) - nes_ntsc_rgb_builder;
          fourth -= rgb_bias >> 2;
          out[outIndex + i + 31] += fourth;
          out[outIndex + i + 19] += fourth;
          out[outIndex + i + 7] += fourth;
          out[outIndex + i] += error - (fourth * 3);
        }
      }
      outIndex += alignment_count * rgb_kernel_size;
    }
  }

  public static void nes_ntsc_init(final int[] ntsc, nes_ntsc_setup_t setup) {
    
    boolean merge_fields;
    init_t impl = new init_t();
    float gamma_factor;

    if (setup == null) {
      setup = nes_ntsc_composite;
    }
    init(impl, setup);

    /* setup fast gamma */
    {
      float gamma = (float)setup.gamma * -0.5f;
      if (STD_HUE_CONDITION(setup)) {
        gamma += 0.1333f;
      }

      gamma_factor = (float)Math.pow((float)Math.abs(gamma), 0.73f);
      if (gamma < 0) {
        gamma_factor = -gamma_factor;
      }
    }

    merge_fields = setup.merge_fields;
    if (setup.artifacts <= -1 && setup.fringing <= -1) {
      merge_fields = true;
    }

    for(int entry = 0; entry < nes_ntsc_palette_size; entry++) {
      /* Base 64-color generation */      
      int level = entry >> 4 & 0x03;
      float lo = lo_levels [level];
      float hi = hi_levels [level];

      int color = entry & 0x0F;
      if (color == 0) {
        lo = hi;
      }
      if (color == 0x0D) {
        hi = lo;
      }
      if (color > 0x0D) {
        hi = lo = 0.0f;
      }

      {
        /* Convert raw waveform to YIQ */
        float sat = (hi - lo) * 0.5f;
        float i = TO_ANGLE_SIN(color) * sat;
        float q = TO_ANGLE_COS(color) * sat;
        float y = (hi + lo) * 0.5f;

        /* Optionally use base palette instead */
        if (setup.base_palette != null) {
          final int[] in = setup.base_palette;
          final int inOffset = (entry & 0x3F) * 3;          
          float r = to_float * in[inOffset];
          float g = to_float * in[inOffset + 1];
          float b = to_float * in[inOffset + 2];
          y = r * 0.299f + g * 0.587f + b * 0.114f;
          i = r * 0.596f - g * 0.275f - b * 0.321f;
          q = r * 0.212f - g * 0.523f + b * 0.311f;
        }

        /* Apply color emphasis */
        if (NES_NTSC_EMPHASIS) {
          final int tint = entry >> 6 & 7;
          if (tint != 0 && color <= 0x0D) {
            if (tint == 7) {
              y = y * (atten_mul * 1.13f) - (atten_sub * 1.13f);
            } else {
              final int tint_color = tints[tint];
              float _sat = hi * (0.5f - atten_mul * 0.5f) + atten_sub * 0.5f;
              y -= _sat * 0.5f;
              if (tint >= 3 && tint != 4) {
                /* combined tint bits */
                _sat *= 0.6f;
                y -= _sat;
              }
              i += TO_ANGLE_SIN(tint_color) * _sat;
              q += TO_ANGLE_COS(tint_color) * _sat;
            }
          }
        }

        /* Optionally use palette instead */
        if (setup.palette != null) {
          final int[] in = setup.palette;
          final int inOffset = entry * 3;
          float r = to_float * in[inOffset];
          float g = to_float * in[inOffset + 1];
          float b = to_float * in[inOffset + 2];
          y = r * 0.299f + g * 0.587f + b * 0.114f;
          i = r * 0.596f - g * 0.275f - b * 0.321f;
          q = r * 0.212f - g * 0.523f + b * 0.311f;
        }

        /* Apply brightness, contrast, and gamma */
        y *= (float)setup.contrast * 0.5f + 1;
        /* adjustment reduces error when using input palette */
        y += (float)setup.brightness * 0.5f - 0.5f / 256;

        {
          float r = (float)(y + default_decoder[0] * i 
              + default_decoder[1] * q);
          float g = (float)(y + default_decoder[2] * i 
              + default_decoder[3] * q);
          float b = (float)(y + default_decoder[4] * i 
              + default_decoder[5] * q);

          /* fast approximation of n = pow( n, gamma ) */
          r = (r * gamma_factor - gamma_factor) * r + r;
          g = (g * gamma_factor - gamma_factor) * g + g;
          b = (b * gamma_factor - gamma_factor) * b + b;

          y = r * 0.299f + g * 0.587f + b * 0.114f;
          i = r * 0.596f - g * 0.275f - b * 0.321f;
          q = r * 0.212f - g * 0.523f + b * 0.311f;
        }

        i *= rgb_unit;
        q *= rgb_unit;
        y *= rgb_unit;
        y += rgb_offset;

        /* Generate kernel */
        {
          int r = (int)(y + impl.to_rgb[0] * i + impl.to_rgb[1] * q);
          int g = (int)(y + impl.to_rgb[2] * i + impl.to_rgb[3] * q);
          int b = (int)(y + impl.to_rgb[4] * i + impl.to_rgb[5] * q);
          
          /* blue tends to overflow, so clamp it */
          int rgb = PACK_RGB(r, g, (b < 0x3E0 ? b: 0x3E0));

          if (setup.palette_out != null) {
            final int offset = entry * 3;
            final int clamped = NES_NTSC_CLAMP_(rgb, 8 - rgb_bits);
            setup.palette_out[offset] = (clamped >> 21) & 0xFF;
            setup.palette_out[offset + 1] = (clamped >> 11) & 0xFF;
            setup.palette_out[offset + 2] = (clamped >> 1) & 0xFF;            
          }

          if (ntsc != null) {
            final int kernelIndex = entry * nes_ntsc_entry_size;
            gen_kernel(impl, y, i, q, ntsc, kernelIndex);
            if (merge_fields) {
              merge_kernel_fields(ntsc, kernelIndex);
            }
            correct_errors(rgb, ntsc, kernelIndex);
          }
        }
      }
    }
  }
  
  private static int NES_NTSC_RGB_OUT(int raw) {
    final int sub = raw >> 9 & nes_ntsc_clamp_mask;
    int clamp = nes_ntsc_clamp_add - sub;
    raw |= clamp;
    clamp -= sub;
    raw &= clamp;
    return ((raw >> 5) & 0xFF0000) | ((raw >> 3) & 0x00FF00) 
        | ((raw >> 1) & 0x0000FF);
  }
  
  /* Filters one or more rows of pixels. Input pixels are 6/9-bit palette 
  indicies. In_row_width is the number of pixels to get to the next input row. 
  Out_pitch is the number of *bytes* to get to the next output row. */  
  public static void nes_ntsc_blit(final int[] ntsc, final int[] input, 
      final int in_row_width, int burst_phase, final int in_width, 
          int in_height, final int[] rgb_out, final int out_pitch,
              int inputIndex, int outputIndex) {
    
    int chunk_count = (in_width - 1) / nes_ntsc_in_chunk;
    
    for(; in_height != 0; in_height--) {
      int lineInIndex = inputIndex;
      final int k = burst_phase * nes_ntsc_burst_size;
      int k0 = k + nes_ntsc_black * nes_ntsc_entry_size;
      int k1 = k0;
      int k2 = k + (input[lineInIndex] & 0x1FF) * nes_ntsc_entry_size;

      int x0;
      int x1 = k0;
      int x2 = k0;
            
      int lineOutIndex = outputIndex;
      lineInIndex++;

      for(int n = chunk_count; n != 0; n--) {
        
        /* order of input and output pixels must not be altered */
        x0 = k0;
        k0 = k + (input[lineInIndex] & 0x1FF) * nes_ntsc_entry_size; 
        rgb_out[lineOutIndex] = NES_NTSC_RGB_OUT(ntsc[k0] + ntsc[k1 + 19] 
            + ntsc[k2 + 31] + ntsc[x0 + 7] + ntsc[x1 + 26] + ntsc[x2 + 38]);
        rgb_out[lineOutIndex + 1] = NES_NTSC_RGB_OUT(ntsc[k0 + 1]
            + ntsc[k1 + 20] + ntsc[k2 + 32] + ntsc[x0 + 8] + ntsc[x1 + 27] 
                + ntsc[x2 + 39]);

        x1 = k1;
        k1 = k + (input[lineInIndex + 1] & 0x1FF) * nes_ntsc_entry_size;
        rgb_out[lineOutIndex + 2] = NES_NTSC_RGB_OUT(ntsc[k0 + 2]
            + ntsc[k1 + 14] + ntsc[k2 + 33] + ntsc[x0 + 9] + ntsc[x1 + 21] 
                + ntsc[x2 + 40]);
        rgb_out[lineOutIndex + 3] = NES_NTSC_RGB_OUT(ntsc[k0 + 3]
            + ntsc[k1 + 15] + ntsc[k2 + 34] + ntsc[x0 + 10] + ntsc[x1 + 22] 
                + ntsc[x2 + 41]);
        
        x2 = k2;
        k2 = k + (input[lineInIndex + 2] & 0x1FF) * nes_ntsc_entry_size;
        rgb_out[lineOutIndex + 4] = NES_NTSC_RGB_OUT(ntsc[k0 + 4]
            + ntsc[k1 + 16] + ntsc[k2 + 28] + ntsc[x0 + 11] + ntsc[x1 + 23] 
                + ntsc[x2 + 35]);
        rgb_out[lineOutIndex + 5] = NES_NTSC_RGB_OUT(ntsc[k0 + 5]
            + ntsc[k1 + 17] + ntsc[k2 + 29] + ntsc[x0 + 12] + ntsc[x1 + 24] 
                + ntsc[x2 + 36]);
        rgb_out[lineOutIndex + 6] = NES_NTSC_RGB_OUT(ntsc[k0 + 6]
            + ntsc[k1 + 18] + ntsc[k2 + 30] + ntsc[x0 + 13] + ntsc[x1 + 25] 
                + ntsc[x2 + 37]);

        lineInIndex  += 3;
        lineOutIndex += 7;
      }

      /* finish final pixels */
      x0 = k0;
      k0 = k + nes_ntsc_black * nes_ntsc_entry_size;
      rgb_out[lineOutIndex] = NES_NTSC_RGB_OUT(ntsc[k0] + ntsc[k1 + 19] 
          + ntsc[k2 + 31] + ntsc[x0 + 7] + ntsc[x1 + 26] + ntsc[x2 + 38]);
      rgb_out[lineOutIndex + 1] = NES_NTSC_RGB_OUT(ntsc[k0 + 1]
          + ntsc[k1 + 20] + ntsc[k2 + 32] + ntsc[x0 + 8] + ntsc[x1 + 27] 
              + ntsc[x2 + 39]);

      x1 = k1;
      k1 = k + nes_ntsc_black * nes_ntsc_entry_size;
      rgb_out[lineOutIndex + 2] = NES_NTSC_RGB_OUT(ntsc[k0 + 2] 
          + ntsc[k1 + 14] + ntsc[k2 + 33] + ntsc[x0 + 9] + ntsc[x1 + 21] 
              + ntsc[x2 + 40]);
      rgb_out[lineOutIndex + 3] = NES_NTSC_RGB_OUT(ntsc[k0 + 3]
          + ntsc[k1 + 15] + ntsc[k2 + 34] + ntsc[x0 + 10] + ntsc[x1 + 22] 
              + ntsc[x2 + 41]);

      x2 = k2;
      k2 = k + nes_ntsc_black * nes_ntsc_entry_size;
      rgb_out[lineOutIndex + 4] = NES_NTSC_RGB_OUT(ntsc[k0 + 4]
          + ntsc[k1 + 16] + ntsc[k2 + 28] + ntsc[x0 + 11] + ntsc[x1 + 23] 
              + ntsc[x2 + 35]);
      rgb_out[lineOutIndex + 5] = NES_NTSC_RGB_OUT(ntsc[k0 + 5]
          + ntsc[k1 + 17] + ntsc[k2 + 29] + ntsc[x0 + 12] + ntsc[x1 + 24] 
              + ntsc[x2 + 36]);
      rgb_out[lineOutIndex + 6] = NES_NTSC_RGB_OUT(ntsc[k0 + 6]
          + ntsc[k1 + 18] + ntsc[k2 + 30] + ntsc[x0 + 13] + ntsc[x1 + 25] 
              + ntsc[x2 + 37]);

      burst_phase = (burst_phase + 1) % nes_ntsc_burst_count;
      
      inputIndex += in_row_width;
      outputIndex += out_pitch;
    }
  } 
  
  static {
    nes_ntsc_init(nes_ntsc_t, nes_ntsc_composite);
  }
  
  private int burstPhase;
    
  public NesNtsc() {
	  image = new BufferedImage(NES_NTSC_OUT_WIDTH(256), 240, BufferedImage.TYPE_INT_RGB);
	  out = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
  }
 
  public BufferedImage getImage() {
	    return image;
	  }
  public int[] getImageData() {
	    return out;
	  }
  public void filter(final int[] in, final int yFirst, final int yLast) {
    nes_ntsc_blit(nes_ntsc_t, in, 256, burstPhase, 256, yLast - yFirst, out, 
        602, yFirst << 8, yFirst * 602);
    burstPhase ^= 1;
  }
  public static void restartNTSC(){
	NesNtsc.nes_ntsc_init(NesNtsc.nes_ntsc_t, new NesNtsc.nes_ntsc_setup_t(UISettings.ntsc_hue,
			UISettings.ntsc_saturation, UISettings.ntsc_contrast, UISettings.ntsc_brightness,
			UISettings.ntsc_sharpness, UISettings.ntsc_gamma, UISettings.ntsc_resolution,
			UISettings.ntsc_artifacts, UISettings.ntsc_fringing, UISettings.ntsc_bleed,
			UISettings.ntsc_merge, null,null, null, preppalette(NesColors.getpalette(NesSettings.selectedPalette))));
  }
  private static int[] preppalette(int[] pal){
  	int[] out = new int[pal.length*3];
  	for(int i = 0; i<pal.length;i++){
  		out[i*3] = (pal[i]&0xff0000)>>16;
  		out[i*3+1] = (pal[i]&0xff00)>>8;
  		out[i*3+2] = pal[i]&0xff;
  	}
  	return out;
  }
}
