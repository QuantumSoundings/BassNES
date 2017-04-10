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
import java.io.PrintStream;
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
import com.NesDisplay;

public class SystemUI {
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
		//frame.setTitle("Nes Emulator");

		//panel.add(display);
		//panel.setFocusable(true);
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
					prop.store(output, null);
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.exit(0);
			}
		});
		frame.pack();
		frame.setBounds(100, 100, 256+20, 240+60);
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
		File t = new File("config.properties");
		if(!t.exists()){
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
		}
	}
	/*void menuSetup(){
		menu = new JMenuBar();
		system = new JMenu("System");cpu = new JMenu("CPU");audio = new JMenu("Audio");graphics = new JMenu("Graphics");control = new JMenu("Controls");debug = new JMenu("Debug");
		menu.add(system);menu.add(cpu);menu.add(audio);menu.add(graphics);menu.add(control);menu.add(debug);
		JMenuItem item = new JMenuItem("Load Rom");
		item.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int returnval = fc.showOpenDialog(frame);
				if(returnval == JFileChooser.APPROVE_OPTION){
					rom = fc.getSelectedFile();
					if(autoload){
						if(nes!=null)
							nes.flag=false;
						nes = new NES(display,frame,rom,prop);
						current = new Thread(nes);
						current.start();
					}
				}
			}			
		});
		system.add(item);
		item = new JMenuItem("Start CPU");
		item.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				if(!rom.equals(null)){
					begin = true;
					if(nes!=null)
						nes.flag=false;
					nes = new NES(display,frame,rom,prop);
					current = new Thread(nes);
					current.start();
					//System.out.println(begin);
				}
				else
					begin = false;	
			}	
		});
		cpu.add(item);
		JCheckBoxMenuItem xitem = new JCheckBoxMenuItem("Enable Audio");
		audio.add(xitem);		
		xitem = new JCheckBoxMenuItem("Auto Load Rom");
		xitem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				autoload = !autoload;
			}	
		});
		xitem.setSelected(true);
		system.add(xitem);
		xitem= new JCheckBoxMenuItem("Show Debug Window");
		xitem.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				debugframe.setVisible(true);
			}
			
		});
		debug.add(xitem);
		item = new JMenuItem("Configure");
		item.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				keyconfig.setVisible(true);
			}
			
		});
		control.add(item);
		
	}*/
	/*void keyconfigsetup(){
		UIkeys key = new UIkeys();
		prop = key.prop;
		JButton apply = new JButton("Apply");
		apply.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				if(nes!=null){
					
						nes.controller.updateKeys(prop);
					
				}
			}
			
		});
		key.add(apply);
		//keyconfig.setSize(250, 250);
		//keyconfig.add(key);
		
	}*/
}
