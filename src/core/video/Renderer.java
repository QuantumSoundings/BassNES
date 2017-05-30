package core.video;

import core.NesSettings;

public class Renderer implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6820858889740836382L;
	public int[] colorized = new int[61440];
	
	public void buildFrame(int[] pixels){
		switch(NesSettings.RenderMethod){
		case 1:
			buildImageRGBnoEmp(pixels);break;
		case 2:
			buildImageRGBEmp(pixels);break;
		case 3:
			buildImageRawNes(pixels);break;
		default: break;
		}
	}
	
	void buildImageRawNes(int[] pixels){
		for(int i = 0;i<61440;i++)
			colorized[i] = pixels[i];
	}
	void buildImageRGBnoEmp(int[] pixels){
		for(int i=0;i<61440;i++)
			colorized[i]=NesColors.col[0][pixels[i]&(NesColors.col[0].length-1)];
	}
	void buildImageRGBEmp(int[] pixels){
		for(int i=0;i<61440;i++)
			colorized[i] = NesColors.col[pixels[i]>>8][pixels[i]&(NesColors.col[0].length-1)];
	}
	void buildImageNTSC(int[] pixels, int[] maskpixels){
		
	}
	

}
