package com;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import javax.swing.JPanel;
public class NesDisplay extends JPanel {
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
		g.drawImage(frame, 0, 0,256,240, this);
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
