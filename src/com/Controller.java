package com;

import java.awt.event.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JFrame;

import ui.UserSettings;

public class Controller {
	Keycheckerc1 keys;
	int controllerNum;
	boolean strobe;
	int output;
	int keysPressed;
	int nextKey;
	boolean debug;
	
	public Controller(Properties prop,int num){
		strobe = false;
		output = 0;
		keysPressed=0;
		controllerNum=num;
		nextKey = 0;
		keys = new Keycheckerc1();
		//updateKeys(prop);
		
	}
	/*public void updateKeys(Properties prop){
		if(controllerNum==1){
			keys.akey = Integer.parseInt(prop.getProperty("c1a"));
			keys.bkey = Integer.parseInt(prop.getProperty("c1b"));
			keys.upkey = Integer.parseInt(prop.getProperty("c1up"));
			keys.downkey = Integer.parseInt(prop.getProperty("c1down"));
			keys.startkey = Integer.parseInt(prop.getProperty("c1start"));
			keys.selectkey = Integer.parseInt(prop.getProperty("c1select"));
			keys.leftkey = Integer.parseInt(prop.getProperty("c1left"));
			keys.rightkey = Integer.parseInt(prop.getProperty("c1right"));
		}
		else{
			keys.akey = Integer.parseInt(prop.getProperty("c2a"));
			keys.bkey = Integer.parseInt(prop.getProperty("c2b"));
			keys.upkey = Integer.parseInt(prop.getProperty("c2up"));
			keys.downkey = Integer.parseInt(prop.getProperty("c2down"));
			keys.startkey = Integer.parseInt(prop.getProperty("c2start"));
			keys.selectkey = Integer.parseInt(prop.getProperty("c2select"));
			keys.leftkey = Integer.parseInt(prop.getProperty("c2left"));
			keys.rightkey = Integer.parseInt(prop.getProperty("c2right"));
		}
		
	}*/
	
	public void setframe(JFrame f){
		f.addKeyListener(keys);
		f.setFocusable(true);
		f.requestFocusInWindow();
	}
	public byte getControllerStatus(){
		int getNextKey = keys.currentKeys()[nextKey]?1:0;
		//System.out.println(Arrays.toString(keys.currentKeys()));
		if(!strobe)
			nextKey++;
		output&=0x11111110;
		output|=getNextKey;
		//System.out.println("return controller status:"+output);
		return (byte)(output);
		//return (byte) (output& ((keys.currentKeys()[strobe?nextKey++:0])?1:0));
	}
	public void inputRegister(byte b){
		if((b&1)==1){
			nextKey = 0;
			strobe = true;
		}
		else{
			strobe = false;
			nextKey = 0;
		}
	}
	public boolean checkDebug(){
		return keys.debug;
	}
	
}
class Keycheckerc1 implements KeyListener{
	boolean a=false;
	boolean b=false;
	boolean up=false;
	boolean down=false;
	boolean start=false;
	boolean select=false;
	boolean left=false;
	boolean right=false;
	boolean debug=false;
	//controller bindings
	/*int startkey=UserSettings.c1start;
	int selectkey=UserSettings.
	int akey=KeyEvent.VK_A;
	int bkey=KeyEvent.VK_Z;
	int upkey=KeyEvent.VK_UP;
	int downkey=KeyEvent.VK_DOWN;
	int leftkey=KeyEvent.VK_LEFT;
	int rightkey=KeyEvent.VK_RIGHT;*/
	int debugkey=KeyEvent.VK_P;
	@Override
	public void keyPressed(KeyEvent event){
		int key = event.getKeyCode();
		if(key ==UserSettings.c1up)
			up = true;
		else if(key == UserSettings.c1down)
			down = true;
		else if(key == UserSettings.c1left)
			left = true;
		else if(key == UserSettings.c1right)
			right = true;
		else if(key == UserSettings.c1a)
			a = true;
		else if(key == UserSettings.c1b)
			b = true;
		else if(key == UserSettings.c1start)
			start = true;
		else if(key == UserSettings.c1select)
			select = true;
		else if(key == debugkey)
			debug = !debug;
	}
	@Override
	public void keyReleased(KeyEvent event){
		int key = event.getKeyCode();
		if(key ==UserSettings.c1up)
			up = false;
		else if(key == UserSettings.c1down)
			down = false;
		else if(key == UserSettings.c1left)
			left = false;
		else if(key == UserSettings.c1right)
			right = false;
		else if(key == UserSettings.c1a)
			a = false;
		else if(key == UserSettings.c1b)
			b = false;
		else if(key == UserSettings.c1start)
			start = false;
		else if(key == UserSettings.c1select)
			select = false;
		//else if(key == debugkey)
		//	debug = !debug;
	}
	public boolean[] currentKeys(){
		return new boolean[]{a,b,select,start,up,up?false:down,left,left?false:right};
	}
	@Override
	public void keyTyped(KeyEvent arg0) {
	}
}