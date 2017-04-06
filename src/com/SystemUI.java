package com;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
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

public class SystemUI {
	public NES nes;
	final JFileChooser fc = new JFileChooser();
	private JFrame frame,debugframe,keyconfig;
	File rom;
	NesDisplay display;
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
		prop = new Properties();
		frame = new JFrame();
		debugframe = new JFrame();
		keyconfig = new JFrame();
		rom = new File("zelda.nes"); 
		display = new NesDisplay();
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		display.setSize(256, 240);
		frame.setTitle("Nes Emulator");

		panel.add(display);
		menuSetup();
		keyconfigsetup();
		panel.setFocusable(true);
		frame.setJMenuBar(menu);
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		panel.requestFocusInWindow();
		frame.setSize(400, 400);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent evt){
				if(nes!=null)
					nes.flag=false;
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					FileOutputStream output = new FileOutputStream("config.properties");
					prop.store(output, null);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.exit(0);
			}
		});
		frame.pack();
		frame.setVisible(true);
		debugWindow();
	}
	public void start(){
		
	}
	void menuSetup(){
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
		
	}
	void keyconfigsetup(){
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
		keyconfig.setSize(250, 250);
		keyconfig.add(key);
		
	}
	void debugWindow(){
		JPanel p1 = new JPanel();
		JTextArea text = new JTextArea(30,35);
		text.setEditable(true);
		JScrollPane scroll = new JScrollPane(text);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		p1.add(scroll);
		
		debugframe.add(p1);
		debugframe.pack();
		PrintStream x = new PrintStream(System.out){
			@Override
			public void println(String x){
				text.append(x);
				text.append("\n");
			}
			@Override
			public void print(String x){
				text.append(x);
			}
		};
		System.setOut(x);
		System.setErr(x);
		//debugframe.setVisible(true);
		
		
	}
}
