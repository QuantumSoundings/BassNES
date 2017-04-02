package com;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
public class NTSC_Converter{
	BufferedImage bi = new BufferedImage(256,240,BufferedImage.TYPE_INT_RGB);
	Graphics2D big = null;
	public NTSC_Converter(){
		bi = new BufferedImage(256,240,BufferedImage.TYPE_INT_RGB);
		big = bi.createGraphics();
	}
	// http://forums.nesdev.com/viewtopic.php?t=8209
	int[] colorarray = {
            0x606060, 0x09268e, 0x1a11bd, 0x3409b6, 0x5e0982, 0x790939, 0x6f0c09, 0x511f09,
            0x293709, 0x0d4809, 0x094e09, 0x094b17, 0x093a5a, 0x000000, 0x000000, 0x000000,
            0xb1b1b1, 0x1658f7, 0x4433ff, 0x7d20ff, 0xb515d8, 0xcb1d73, 0xc62922, 0x954f09,
            0x5f7209, 0x28ac09, 0x099c09, 0x099032, 0x0976a2, 0x090909, 0x000000, 0x000000,
            0xffffff, 0x5dadff, 0x9d84ff, 0xd76aff, 0xff5dff, 0xff63c6, 0xff8150, 0xffa50d,
            0xccc409, 0x74f009, 0x54fc1c, 0x33f881, 0x3fd4ff, 0x494949, 0x000000, 0x000000,
            0xffffff, 0xc8eaff, 0xe1d8ff, 0xffccff, 0xffc6ff, 0xffcbfb, 0xffd7c2, 0xffe999,
            0xf0f986, 0xd6ff90, 0xbdffaf, 0xb3ffd7, 0xb3ffff, 0xbcbcbc, 0x000000, 0x000000};
		public void makeframe(byte[] pixels){
			int i = 0;
			int y = -1;
			int[] p = new int[pixels.length];
			for(byte b:pixels){
				p[i]=colorarray[b];
				//bi.setRGB(i%255, i/256, colorarray[Byte.toUnsignedInt(b)]);
				i++;
			}
			bi.setRGB(0, 0, 256, 240,p, 0, 256);
			
		}
		public byte[] ntsc_to_rgb(int pixel, int PPUMASK) {
			int color = (pixel & 0x0F);
			if ((PPUMASK & 1) != 0) color = 0;
			int level = color < 0x0E ? (pixel>>4) & 3 : 1;
			final float black = .518F, white = 1.962F, attenuation = .746F;
			final float levels[] = {.350f,.518f,.962f,1.550f,1.094f,1.506f,1.962f,1.962f};
			final float lh[] = {levels[level+4*i2b(color==0x0)],levels[level+4*i2b(color<0xD)]};
			float y = 0.f, gamma = 1.8f, i = y, q = y;
			for (int p = 0; p < 12; p++) {
				float spot = lh[i2b(wave(p,color))];
				if (((PPUMASK & 0x20) != 0 && wave(p,12)) ||
						((PPUMASK & 0x40) != 0 && wave(p,4)) ||
						((PPUMASK & 0x80) != 0 && wave(p,8))) spot *= attenuation;
					
				float v = (spot-black) / (white-black) / 12f;
				y += v;
				i += v * Math.cos(Math.PI * p / 6.0);
				q += v * Math.sin(Math.PI * p / 6.0);
			}
			byte[] rgb = {(byte) clamp(255 * gammafix(y + 0.946882f*i + 0.623557f*q, gamma)),(byte) clamp(255 * gammafix(y + -0.274788f*i + -0.635691f*q,gamma)),(byte) clamp(255 * gammafix(y + -1.108545f*i +  1.709007f*q,gamma))};
			return rgb;
		}
		private boolean wave(int x, int y) {
			return ((x+y+8)%12)<6;
		}
		private double gammafix(float f, float gamma) {
			return f < 0.f ? 0.f : Math.pow(f, 2.2f / gamma);
		}
		private int clamp(double a) {
			return ((int) a < 0 ? 0 : ((int) a > 255 ? 255 : (int) a));
		}
		private int i2b(boolean b) {
			return b ? 1 : 0;
		}
		public void pushPixel(byte[] rgb, int x, int y) {
			//System.out.println(x+" "+y);
			bi.setRGB(x, y,0xFFffffff& (Byte.toUnsignedInt(rgb[0]) << 16) | (Byte.toUnsignedInt(rgb[1]) << 8) | Byte.toUnsignedInt(rgb[2]));
			//bi.setRGB(x, y, 0xFFFF00FF);
		}
		public void submit() {
			//NesDisplay.sendFrame(bi);
			bi.flush();
			//this.bi = new BufferedImage(256, 240, BufferedImage.TYPE_INT_RGB);
		}
		
}