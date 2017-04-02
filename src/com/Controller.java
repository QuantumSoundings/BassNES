package com;

import java.awt.event.*;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Controller {
	Keychecker keys;
	boolean strobe;
	int output;
	int keysPressed;
	int nextKey;
	boolean debug;
	public Controller(){
		strobe = false;
		output = 0;
		keysPressed=0;
		nextKey = 0;
		keys = new Keychecker();
	}
	
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
class Keychecker implements KeyListener{
	boolean a=false;
	boolean b=false;
	boolean up=false;
	boolean down=false;
	boolean start=false;
	boolean select=false;
	boolean left=false;
	boolean right=false;
	boolean debug=false;
	@Override
	public void keyPressed(KeyEvent event){
		//System.out.println("I pressed:"+event.getKeyChar());
		switch(event.getKeyCode()){
		case KeyEvent.VK_UP:
			up = true;
			//System.out.println(up);
			break;
		case KeyEvent.VK_DOWN:
			down = true;
			break;
		case KeyEvent.VK_LEFT:
			left = true;
			break;
		case KeyEvent.VK_RIGHT:
			right = true;
			break;
		case KeyEvent.VK_A:
			a = true;
			break;
		case KeyEvent.VK_S:
			start = true;
			break;
		case KeyEvent.VK_D:
			select = true;
		case KeyEvent.VK_Z:
			b = true;
			break;
		case KeyEvent.VK_P:
			debug = !debug;
			break;
		default:break;
		}	
	}
	@Override
	public void keyReleased(KeyEvent event){
		switch(event.getKeyCode()){
		case KeyEvent.VK_UP:
			up = false;
			break;
		case KeyEvent.VK_DOWN:
			down = false;
			break;
		case KeyEvent.VK_LEFT:
			left = false;
			break;
		case KeyEvent.VK_RIGHT:
			right = false;
			break;
		case KeyEvent.VK_A:
			a = false;
			break;
		case KeyEvent.VK_S:
			start = false;
			break;
		case KeyEvent.VK_D:
			select = false;
		case KeyEvent.VK_Z:
			b = false;
			break;
		default:break;
		}
	}
	public boolean[] currentKeys(){
		//System.out.println("Reading the controller register");
		return new boolean[]{a,b,select,start,up,up?false:down,left,left?false:right};
	}
	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		//System.out.println("I PRESSED A KEY BOYS");
		
		
	}
}