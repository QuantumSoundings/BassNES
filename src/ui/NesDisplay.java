package ui;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

public class NesDisplay extends JPanel {
	int scaling=1;
	private static final long serialVersionUID = 1L;
	BufferedImage frame;
    public NesDisplay(BufferedImage img){
    	frame = new BufferedImage(256, 240, BufferedImage.TYPE_INT_RGB);
    }
    public NesDisplay(){
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
		frame.setRGB(0, 0, 256, 240, pixels, 0, 256);
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
}
