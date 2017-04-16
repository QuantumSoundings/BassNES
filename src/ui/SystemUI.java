package ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import com.NES;

import video.NesDisplay;

public class SystemUI {
	public final String version = "0.1.3";
	public NES nes;
	final JFileChooser fc = new JFileChooser();
	public JFrame frame,debugframe,keyconfig;
	File rom;
	NesDisplay display;
	JPanel panel;
	JButton b1;
	JButton b2;
	boolean awaitingkey;
	JMenuBar menu;
	JMenu system,cpu,audio,graphics,control,debug;
	Thread current;
	Properties prop;
	public boolean begin;
	boolean autoload = true;
	
	public SystemUI(){
		try {
			loadProperties();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		frame = new MainUI(this);
		//debugframe = new DebugUI();
		keyconfig = new ControlUI(prop,this);
		addapply();
		rom = new File("zelda.nes"); 
		display = new NesDisplay();
		panel = new JPanel();
		panel.setLayout(new FlowLayout());
		display.setSize(256, 240);
		display.setFocusable(true);
		frame.getContentPane().add(display);
		display.requestFocusInWindow();
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent evt){
				if(nes!=null)
					nes.flag=false;
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				try {
					FileOutputStream output = new FileOutputStream("config.properties");
					//URL resourceUrl = getClass().getResource("config.properties");
					//File file = new File(resourceUrl.toURI().toString());
					//OutputStream output = new FileOutputStream(file);
					prop.store(output, null);
				} catch (IOException e) {
					e.printStackTrace();
				} 
				System.exit(0);
			}
		});
		frame.pack();
		frame.setBounds(100, 100, 256+10, 240+60);
		frame.setVisible(true);
		//debugWindow();
	}
	public void start(){
		
	}
	private void addapply(){
		JButton btnNewButton_16 = new JButton("Apply");
		btnNewButton_16.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(nes!=null){
					nes.controller.updateKeys(prop);
					nes.controller2.updateKeys(prop);
				}
			}
		});
		GridBagConstraints gbc_btnNewButton_16 = new GridBagConstraints();
		gbc_btnNewButton_16.insets = new Insets(0, 0, 0, 5);
		gbc_btnNewButton_16.gridx = 3;
		gbc_btnNewButton_16.gridy = 9;
		keyconfig.getContentPane().add(btnNewButton_16, gbc_btnNewButton_16);
	}
	public void loadProperties() throws IOException{
		prop = new Properties();
		//InputStream input =this.getClass().getResourceAsStream("config.properties");
		//prop.load(input);
		File t = new File("config.properties");
		if(!t.exists()){
			prop.setProperty("version", version);
			prop.setProperty("c1up", KeyEvent.VK_UP+"");
			prop.setProperty("c1down", KeyEvent.VK_DOWN+"");
			prop.setProperty("c1left", KeyEvent.VK_LEFT+"");
			prop.setProperty("c1right", KeyEvent.VK_RIGHT+"");
			prop.setProperty("c1a", KeyEvent.VK_A+"");
			prop.setProperty("c1b", KeyEvent.VK_S+"");
			prop.setProperty("c1start", KeyEvent.VK_Q+"");
			prop.setProperty("c1select", KeyEvent.VK_W+"");
			prop.setProperty("c2up", KeyEvent.VK_UP+"");
			prop.setProperty("c2down", KeyEvent.VK_DOWN+"");
			prop.setProperty("c2left", KeyEvent.VK_LEFT+"");
			prop.setProperty("c2right", KeyEvent.VK_RIGHT+"");
			prop.setProperty("c2a", KeyEvent.VK_A+"");
			prop.setProperty("c2b", KeyEvent.VK_S+"");
			prop.setProperty("c2start", KeyEvent.VK_Q+"");
			prop.setProperty("c2select", KeyEvent.VK_W+"");
		}
		else{
			FileInputStream input = new FileInputStream("config.properties");
			prop.load(input);
			if(!prop.getProperty("version").equals(version)||!prop.containsKey("version")){
				t.delete();
				loadProperties();
			}
		}
	}
}
