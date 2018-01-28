package ui;

import ui.settings.UISettings;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class Visualizer extends JFrame {

	private static final long serialVersionUID = -6447949294197230811L;
	private JPanel contentPane;
	private Object[][] freq;
	private BufferedImage pianoKeyboard;
	private int[] pianodata;
	public Visualizer() {
		setTitle("Visualizer");
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 915, 371);
		contentPane = new JPanel();
		contentPane.setBackground(Color.BLACK);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		image = new BufferedImage(906,360,BufferedImage.TYPE_INT_RGB);
		try{
			pianoKeyboard = ImageIO.read(this.getClass().getResourceAsStream("fullkeyboard1row.png"));
		} catch(IOException e){
			
		}
		pianodata = new int[pianoKeyboard.getHeight()*pianoKeyboard.getWidth()];
		pianoKeyboard.getRGB(0, 0, pianoKeyboard.getWidth(), pianoKeyboard.getHeight(),
				pianodata, 0 , pianoKeyboard.getWidth());
		
	}
	double[] notefreq = new double[]{0.0,
			16.35,17.32,18.35,19.45,20.6,21.83,23.12,24.5,25.96,27.5,29.14,30.87,32.7,34.65,36.71,38.89,
			41.2,43.65,46.25,49,51.91,55,58.27,61.74,65.41,69.3,73.42,77.78,82.41,87.31,92.5,98,103.83,110,116.54,123.47,130.81,
			138.59,146.83,155.56,164.81,174.61,185,196,207.65,220,233.08,246.94,261.63,277.18,293.66,311.13,329.63,349.23,369.99,392,415.3,440,
			466.16,493.88,523.25,554.37,587.33,622.25,659.25,698.46,739.99,783.99,830.61,880,932.33,987.77,1046.5,1108.73,1174.66,1244.51,
			1318.51,1396.91,1479.98,1567.98,1661.22,1760,1864.66,1975.53,2093,2217.46,2349.32,2489.02,2637.02,2793.83,
			2959.96,3135.96,3322.44,3520,3729.31,3951.07,4186.01,4434.92,4698.63,4978.03,
			5274.04,5587.65,5919.91,6271.93,6644.88,7040,7458.62,7902.13};
	String[] notename = new String[]{"",
			"C0"," C#0",
			"D0"," D#0","E0","F0"," F#0","G0"," G#0","A0"," A#0","B0","C1"," C#1","D1"," D#1","E1","F1"," F#1","G1"," G#1","A1"," A#1","B1","C2"," C#2",
			"D2"," D#2","E2","F2"," F#2","G2"," G#2","A2"," A#2","B2","C3"," C#3","D3"," D#3","E3","F3"," F#3","G3"," G#3","A3"," A#3","B3","C4"," C#4",
			"D4"," D#4","E4","F4"," F#4","G4"," G#4","A4"," A#4","B4","C5"," C#5","D5"," D#5","E5","F5"," F#5","G5"," G#5","A5"," A#5","B5","C6"," C#6",
			"D6"," D#6","E6","F6"," F#6","G6"," G#6","A6"," A#6","B6","C7"," C#7","D7"," D#7","E7","F7"," F#7","G7"," G#7","A7"," A#7","B7","C8"," C#8",
			"D8"," D#8","E8","F8"," F#8","G8"," G#8","A8"," A#8","B8"};
	final String[] channelNames = {"Pulse 1","Pulse 2","Triangle","VRC6 Pulse 1","VRC6 Pulse 2","VRC6 Saw","MMC5 Pulse 1","MMC5 Pulse 2","N_Channel 0","N_Channel 1","N_Channel 2"
			,"N_Channel 3","N_Channel 4","N_Channel 5","N_Channel 6","N_Channel 7"};
	final Color[] channelColors = {Color.BLUE,Color.CYAN,Color.GREEN,Color.ORANGE,Color.RED,Color.YELLOW,Color.DARK_GRAY,Color.LIGHT_GRAY,Color.magenta,Color.magenta,Color.magenta,
			Color.magenta,Color.magenta,Color.MAGENTA,Color.MAGENTA,Color.magenta};
	public void setFreq(Object[][] frequencies){
		freq = frequencies;
	}
	int[] audiobuffer;
	public void setAudio(int[] buf){
		audiobuffer=buf;
	}
	int max = 32768;
	int min = 0;
	int max2 = 350;
	int min2 = 0;
	private int scaleint(int i){
		return ((max2-min2)*(i - min))/(max-min) + min2;
	}
	public double search(double value) {
        int lo = 0;
        int hi = notefreq.length - 1;

        double lastValue = 0;

        while (lo <= hi) {
            int mid = (lo + hi) / 2;
            lastValue = notefreq[mid];
            if (value < lastValue) {
                hi = mid - 1;
            } else if (value > lastValue) {
                lo = mid + 1;
            } else {
                return lastValue;
            }
        }
        return lastValue;
    }
	public void colorKey(Graphics g,String note){
		//Graphics g = pianoKeyboard.getGraphics();
		//g.setXORMode(Color.green);
		if(note.contains("#")){
			colorBlack(g,note);
		}
		else{
			colorWhite(g,note);	
		}		
	}
	private void colorBlack(Graphics g,String note){
		char pitch = note.charAt(1);
		char octave = note.charAt(3);
		int xoffset = (Integer.parseInt(octave+""))*113;
		int xoffset2 = 0;
		switch(pitch){
		case 'C':
			xoffset+=14;
			xoffset2 = 23-14;break;
		case 'D':
			xoffset+=28;
			xoffset2=37-28;break;
		case 'F':
			xoffset+=61;
			xoffset2= 71-61;break;
		case 'G':
			xoffset+= 76;
			xoffset2 = 86-76;break;
		case 'A':
			xoffset+=92;
			xoffset2=102-92;break;
		}
		g.fillRect(xoffset, 0, xoffset2, 41);
	}
	private void colorWhite(Graphics g,String note){
		char octave = note.charAt(1);
		char pitch = note.charAt(0);
		int xoffset = (Integer.parseInt(octave+""))*113;
		int startingy=0;
		switch(octave){
		case '0':case '1':case '2':case '3':	
			//startingy = 61;
			//break;
		case '4':case'5':case'6':case'7':
			startingy = 0;
			break;
		}
		switch(pitch){
		case 'C':
			g.fillRect(xoffset+2, startingy, 10, 43);
			g.fillRect(xoffset+2, startingy+43, 15, 16);
			break;
		case 'D':
			g.fillRect(xoffset+24, startingy, 3, 43);
			g.fillRect(xoffset+19, startingy+43, 14, 16);
			break;
		case 'E':
			g.fillRect(xoffset+39, startingy, 9, 43);
			g.fillRect(xoffset+35, startingy+43, 13, 16);
			break;
		case 'F':
			g.fillRect(xoffset+50, startingy, 10,43);
			g.fillRect(xoffset+50, startingy+43, 15, 16);
			break;
		case 'G':
			g.fillRect(xoffset+72, startingy, 3, 43);
			g.fillRect(xoffset+67, startingy+43, 14, 16);
			break;
		case 'A':
			g.fillRect(xoffset+88, startingy, 3, 43);
			g.fillRect(xoffset+83, startingy+43, 14, 16);
			break;
		case 'B':
			g.fillRect(xoffset+103, startingy, 10, 43);
			g.fillRect(xoffset+99, startingy+43, 14, 16);
			break;
		}
	}
	BufferedImage image;
	StringBuffer freqs = new StringBuffer();
	public void paintscope(){
		Graphics g = contentPane.getGraphics();
		Graphics g2 = image.getGraphics();
		Graphics g3 = pianoKeyboard.getGraphics();
		g2.setColor(Color.BLACK);
		g2.fillRect(0, 0, image.getWidth(), image.getHeight());
		int y = 30;
		g2.setColor(Color.green);
		freqs.delete(0, freqs.length());
		for(Object[] chan: freq){
			for(int i = 0; i<chan.length;i+=2){
				double r = search((double)chan[i+1]);
				int index = Arrays.binarySearch(notefreq, r);
				if(!UISettings.allGreen){
					if(UISettings.colorBlindMode)
						g2.setColor(UISettings.pianoColorsColorBlind[Arrays.asList(UISettings.channelNames).indexOf((String)chan[i])]);
					else
						g2.setColor(UISettings.pianoColors[Arrays.asList(UISettings.channelNames).indexOf((String)chan[i])]);
				}
				if(notename[index].length()>0&&freqs.indexOf(notename[index])==-1){
					g3.setColor(g2.getColor());
					if(notename[index].contains("#"))
						colorBlack(g3,notename[index]);
					else
						colorWhite(g3,notename[index]);
					freqs.append(notename[index]);
				}
				g2.drawString((String)chan[i]+": "+notename[index], 0, y);
				y+=10;
			}
		}
		g2.setColor(Color.BLUE);
		g2.drawImage(pianoKeyboard, 0, 270, null);
		pianoKeyboard.setRGB(0, 0, pianoKeyboard.getWidth(), pianoKeyboard.getHeight(), pianodata, 0, pianoKeyboard.getWidth());
		g2.setColor(Color.GREEN);
		int prevscal = contentPane.getHeight()/2;
		int curscal = 0;
		for(int i = 0; i<audiobuffer.length;i+=2){
			curscal = scaleint(audiobuffer[i]);
			g2.drawLine(i, prevscal+100, i+1, curscal+100 );
			prevscal = curscal;	
		}
		g.drawImage(image, 0, 0, null);
		g.dispose();
		g2.dispose();
	}
}
