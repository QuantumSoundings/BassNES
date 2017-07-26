package ui;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

import ui.UISettings.VideoFilter;
import ui.filter.NesNtsc;

public class NesDisplay extends JPanel {
	int scaling=1;
	private static final long serialVersionUID = 1L;
	BufferedImage frame;
	private int width = 256;
	private int height = 240;
	NesNtsc ntsc = new NesNtsc();
    public NesDisplay(BufferedImage img){
    	if(UISettings.currentFilter==VideoFilter.NTSC){
    		frame = new BufferedImage(602, 240, BufferedImage.TYPE_INT_RGB);
    		width = 602; height = 240;
    	}
    	else
    		frame = new BufferedImage(256, 240, BufferedImage.TYPE_INT_RGB);
    }
    public NesDisplay(){
    	if(UISettings.currentFilter==VideoFilter.NTSC){
    		frame = new BufferedImage(602, 240, BufferedImage.TYPE_INT_RGB);
    		width = 602; height = 240;
    		NesNtsc.restartNTSC();
    	}
    	else
    		frame = new BufferedImage(256, 240, BufferedImage.TYPE_INT_RGB);
    	frame.setAccelerationPriority(1);
    }
    
	@Override
	public void paintComponent(Graphics g) {
		g.drawImage(frame.getScaledInstance(256*scaling, 240*scaling,0), 0, 0,256*scaling,240*scaling, this);
		g.dispose();
	}
	public void updateScaling(int i){
		scaling = i;
	}
	
	public void sendFrame(int[] pixels) {
		if(UISettings.currentFilter!=VideoFilter.None)
			pixels = dofilter(pixels);
		if(UISettings.scanlinesEnabled)
			pixels = scanline(pixels);
		frame.setRGB(0, 0, width, height, pixels, 0, width);
		this.repaint();
	}
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(256, 240);
	}
	@Override
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}
	private int[] dofilter(int[] pixels){
		switch(UISettings.currentFilter){
		case NTSC:
			return ntsc(pixels);
		default:
			return pixels;
		}
	}
	public void updateImage(int width,int height){
		this.width = width;
		this.height = height;
		frame = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
	}
	private int[] ntsc(int[] pixels){
		ntsc.filter(pixels, 0, 240);
		return ntsc.getImageData();
	}
	private int[] scanline(int[] pixels){
		for(int i = 0; i<height;i++){
			if(i%2==0){
				for(int j = width*i;j<(width*(i+1));j++){
					pixels[j] = darkenpixel(pixels[j]);
				}
			}
		}
		return pixels;
	}
	private int darkenpixel(int pixel){
		int high = ((int) (((pixel&0xff0000)>>16)*UISettings.scanlineThickness))<<16;
		int mid = ((int) (((pixel&0xff00)>>8)*UISettings.scanlineThickness))<<8;
		int low = ((int) (((pixel&0xff))*UISettings.scanlineThickness));
		return high|mid|low;
	}
}
