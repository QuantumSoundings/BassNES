package video;

import java.awt.image.BufferedImage;

public class Renderer {
	public BufferedImage frame = new BufferedImage(256,240,BufferedImage.TYPE_INT_RGB);
	int[] colorized = new int[61440];
	
	public void buildFrame(int[] pixels, int[]maskpixels, int mode){
		switch(mode){
		case 1:
			buildImageRGBnoEmp(pixels,maskpixels);break;
		case 2:
			buildImageRGBEmp(pixels,maskpixels);break;
		default: break;
		}
	}
	
	
	void buildImageRGBnoEmp(int[] pixels, int[] maskpixels){
		for(int i=0;i<61440;i++)
			colorized[i]=NesColors.col[0][pixels[i]&(NesColors.col[0].length-1)];
		frame.setRGB(0, 0, 256, 240,colorized, 0, 256);
	}
	void buildImageRGBEmp(int[] pixels, int[] maskpixels){
		for(int i=0;i<61440;i++)
			colorized[i] = NesColors.col[(maskpixels[i]&0b11100000)>>5][pixels[i]&(NesColors.col[0].length-1)];
		frame.setRGB(0, 0, 256, 240, colorized, 0, 256);
	}
	void buildImageNTSC(int[] pixels, int[] maskpixels){
		
	}
	

}
