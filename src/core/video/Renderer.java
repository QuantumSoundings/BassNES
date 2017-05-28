package core.video;

public class Renderer implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6820858889740836382L;
	public int[] colorized = new int[61440];
	
	public void buildFrame(int[] pixels, int mode){
		switch(mode){
		case 1:
			buildImageRGBnoEmp(pixels);break;
		case 2:
			buildImageRGBEmp(pixels);break;
		default: break;
		}
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
