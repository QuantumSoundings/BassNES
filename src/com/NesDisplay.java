package com;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
public class NesDisplay extends JPanel {
	/**
	 * 
	 */
	int scaling=1;
	private static final long serialVersionUID = 1L;
	BufferedImage frame;
    public NesDisplay(BufferedImage img){
    	frame = new BufferedImage(256, 240, BufferedImage.TYPE_INT_RGB);
    }
    public NesDisplay(){
    	frame = new BufferedImage(256, 240, BufferedImage.TYPE_INT_RGB);
    }
    
	@Override
	public void paintComponent(Graphics g) {
		/*super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.drawImage(frame, 0, 0, this);
		//this.repaint();
		g2d.dispose();*/
		//if(scaling>1)
		//	frame = (BufferedImage) frame.getScaledInstance(256*scaling, 240*scaling, 0);
		g.setColor(Color.white);
		g.fillRect(0, 0, 256*scaling, 240*scaling);
		g.drawImage(frame.getScaledInstance(256*scaling, 240*scaling, 0), 0, 0,256*scaling,240*scaling, this);
	}
	public void updateScaling(int i){
		scaling = i;
		this.setMinimumSize(new Dimension(256*i,240*i));
	}
	
	public void sendFrame(BufferedImage f) {
		this.frame = f;
		this.repaint();
		//System.out.println("FRAME IS GOOD");
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
