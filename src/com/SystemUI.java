package com;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class SystemUI {
	public NES nes;
	final JFileChooser fc = new JFileChooser();
	private JFrame frame;
	File rom;
	NesDisplay display;
	JButton b1;
	JButton b2;
	Thread current;
	public boolean begin;
	
	public SystemUI(){
		frame = new JFrame();
		//rom = new File("ff"); 
		display = new NesDisplay();
		JPanel panel = new JPanel();
		b1 = new JButton("Pick Rom");
		b1.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				int returnval = fc.showOpenDialog(frame);
				if(returnval == JFileChooser.APPROVE_OPTION){
					rom = fc.getSelectedFile();
				}
				b1.setText(rom.getName());
			}
		});
		b2 = new JButton("Start NES");
		b2.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				//if(b2.getName().equals("Start NES")){
					if(!rom.equals(null)){
						begin = true;
						if(nes!=null)
							nes.flag=false;
						nes = new NES(display,frame,rom);
						current = new Thread(nes);
						current.start();
						//System.out.println(begin);
					}
					else
						begin = false;
			}
		});
		
		panel.setLayout(new FlowLayout());
		display.setSize(256, 240);
		frame.setTitle("Nes Emulator");
		panel.add(b1);
		panel.add(b2);
		panel.add(display);
		panel.setFocusable(true);
		//frame.getContentPane().setFocusable(true);
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		panel.requestFocusInWindow();
		frame.setSize(400, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
	public void start(){
		
	}
}
