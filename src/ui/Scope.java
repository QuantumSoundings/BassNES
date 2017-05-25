package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class Scope extends JFrame {

	private static final long serialVersionUID = -6447949294197230811L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */

	/**
	 * Create the frame.
	 */
	public Scope() {
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 740, 371);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
	}
	int[] audiobuffer;
	public void setAudio(int[] buf){
		audiobuffer=buf;
	}
	int max = 32768;
	int min = 0;
	int max2 = 250;
	int min2 = -250;
	private int scaleint(int i){
		return ((max2-min2)*(i - min))/(max-min) + min2;
	}
	BufferedImage image = new BufferedImage(730,360,BufferedImage.TYPE_INT_RGB);
	public void paintscope(){
		Graphics g = contentPane.getGraphics();
		Graphics g2 = image.getGraphics();
		g2.setColor(Color.BLACK);
		g2.fillRect(0, 0, image.getWidth(), image.getHeight());
		g2.setColor(Color.GREEN);
		int prevscal = contentPane.getHeight()/2;
		int curscal = 0;
		for(int i = 0; i<audiobuffer.length;i++){
			curscal = scaleint(audiobuffer[i]);
			g2.drawLine(i, prevscal+300, i+1, curscal+300 );
			prevscal = curscal;	
		}
		g.drawImage(image, 0, 0, null);
		g.dispose();
		g2.dispose();
	}
}
