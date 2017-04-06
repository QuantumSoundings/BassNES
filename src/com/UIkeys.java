package com;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JPanel;

public class UIkeys extends JPanel{
	FileInputStream input;
	Properties prop;
	boolean awaitingkey;
	public  UIkeys(){
		try {
			setupUI();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	void setupUI() throws IOException{
		prop = new Properties();
		if(!(new File("config.properties")).exists()){
			FileOutputStream output = new FileOutputStream("config.properties");
			prop.setProperty("up", KeyEvent.VK_UP+"");
			prop.setProperty("down", KeyEvent.VK_DOWN+"");
			prop.setProperty("left", KeyEvent.VK_LEFT+"");
			prop.setProperty("right", KeyEvent.VK_RIGHT+"");
			prop.setProperty("a", KeyEvent.VK_A+"");
			prop.setProperty("b", KeyEvent.VK_S+"");
			prop.setProperty("start", KeyEvent.VK_Q+"");
			prop.setProperty("select", KeyEvent.VK_W+"");
			prop.store(output, null);
		}
		input = new FileInputStream("config.properties");
		prop.load(input);
		JButton up = new JButton("up = "+KeyEvent.getKeyText(Integer.parseInt(prop.getProperty("up"))));
		up.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				awaitingkey=true;
			}		
		});
		up.addKeyListener(new KeyListener(){
			@Override
			public void keyPressed(KeyEvent e) {
				if(awaitingkey){
					up.setText("up = "+KeyEvent.getKeyText(e.getKeyCode())+"");
					prop.setProperty("up", e.getKeyCode()+"");
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {
			}
			@Override
			public void keyTyped(KeyEvent e) {
			}
		});
		this.setLayout(new FlowLayout());
		JButton down = new JButton("down = "+KeyEvent.getKeyText(Integer.parseInt(prop.getProperty("down"))));
		down.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				awaitingkey=true;
			}		
		});
		down.addKeyListener(new KeyListener(){
			@Override
			public void keyPressed(KeyEvent e) {
				if(awaitingkey){
					down.setText("down = "+KeyEvent.getKeyText(e.getKeyCode())+"");
					prop.setProperty("down", e.getKeyCode()+"");
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {
			}
			@Override
			public void keyTyped(KeyEvent e) {
			}
		});
		JButton left = new JButton("left = "+KeyEvent.getKeyText(Integer.parseInt(prop.getProperty("left"))));
		left.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				awaitingkey=true;
			}		
		});
		left.addKeyListener(new KeyListener(){
			@Override
			public void keyPressed(KeyEvent e) {
				if(awaitingkey){
					left.setText("left = "+KeyEvent.getKeyText(e.getKeyCode())+"");
					prop.setProperty("left", e.getKeyCode()+"");
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {
			}
			@Override
			public void keyTyped(KeyEvent e) {
			}
		});
		JButton right = new JButton("right = "+KeyEvent.getKeyText(Integer.parseInt(prop.getProperty("right"))));
		right.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				awaitingkey=true;
			}		
		});
		right.addKeyListener(new KeyListener(){
			@Override
			public void keyPressed(KeyEvent e) {
				if(awaitingkey){
					right.setText("right = "+KeyEvent.getKeyText(e.getKeyCode())+"");
					prop.setProperty("right", e.getKeyCode()+"");
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {
			}
			@Override
			public void keyTyped(KeyEvent e) {
			}
		});
		JButton a = new JButton("a = "+KeyEvent.getKeyText(Integer.parseInt(prop.getProperty("a"))));
		a.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				awaitingkey=true;
			}		
		});
		a.addKeyListener(new KeyListener(){
			@Override
			public void keyPressed(KeyEvent e) {
				if(awaitingkey){
					a.setText("a = "+KeyEvent.getKeyText(e.getKeyCode())+"");
					prop.setProperty("a", e.getKeyCode()+"");
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {
			}
			@Override
			public void keyTyped(KeyEvent e) {
			}
		});
		JButton b = new JButton("b = "+KeyEvent.getKeyText(Integer.parseInt(prop.getProperty("b"))));
		b.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				awaitingkey=true;
			}		
		});
		b.addKeyListener(new KeyListener(){
			@Override
			public void keyPressed(KeyEvent e) {
				if(awaitingkey){
					b.setText("b = "+KeyEvent.getKeyText(e.getKeyCode())+"");
					prop.setProperty("b", e.getKeyCode()+"");
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {
			}
			@Override
			public void keyTyped(KeyEvent e) {
			}
		});
		JButton start = new JButton("start = "+KeyEvent.getKeyText(Integer.parseInt(prop.getProperty("start"))));
		start.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				awaitingkey=true;
			}		
		});
		start.addKeyListener(new KeyListener(){
			@Override
			public void keyPressed(KeyEvent e) {
				if(awaitingkey){
					start.setText("start = "+KeyEvent.getKeyText(e.getKeyCode())+"");
					prop.setProperty("start", e.getKeyCode()+"");
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {
			}
			@Override
			public void keyTyped(KeyEvent e) {
			}
		});
		JButton select = new JButton("select = "+KeyEvent.getKeyText(Integer.parseInt(prop.getProperty("select"))));
		select.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				awaitingkey=true;
			}		
		});
		select.addKeyListener(new KeyListener(){
			@Override
			public void keyPressed(KeyEvent e) {
				if(awaitingkey){
					select.setText("select = "+KeyEvent.getKeyText(e.getKeyCode())+"");
					prop.setProperty("select", e.getKeyCode()+"");
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {
			}
			@Override
			public void keyTyped(KeyEvent e) {
			}
		});
		this.add(up);
		this.add(down);
		this.add(left);
		this.add(right);
		this.add(start);
		this.add(select);
		this.add(a);
		this.add(b);
		this.setSize(250, 250);
	}

}
