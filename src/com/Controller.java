package com;

import java.awt.event.*;

import javax.swing.JFrame;

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
	//controller bindings
	int startkey=KeyEvent.VK_S;
	int selectkey=KeyEvent.VK_D;
	int akey=KeyEvent.VK_A;
	int bkey=KeyEvent.VK_Z;
	int upkey=KeyEvent.VK_UP;
	int downkey=KeyEvent.VK_DOWN;
	int leftkey=KeyEvent.VK_LEFT;
	int rightkey=KeyEvent.VK_RIGHT;
	int debugkey=KeyEvent.VK_P;
	@Override
	public void keyPressed(KeyEvent event){
		//System.out.println("I pressed:"+event.getKeyChar());
		int key = event.getKeyCode();
		if(key ==upkey)
			up = true;
		else if(key == downkey)
			down = true;
		else if(key == leftkey)
			left = true;
		else if(key == rightkey)
			right = true;
		else if(key == akey)
			a = true;
		else if(key == bkey)
			b = true;
		else if(key == startkey)
			start = true;
		else if(key == selectkey)
			select = true;
		else if(key == debugkey)
			debug = !debug;
	}
	@Override
	public void keyReleased(KeyEvent event){
		int key = event.getKeyCode();
		if(key ==upkey)
			up = false;
		else if(key == downkey)
			down = false;
		else if(key == leftkey)
			left = false;
		else if(key == rightkey)
			right = false;
		else if(key == akey)
			a = false;
		else if(key == bkey)
			b = false;
		else if(key == startkey)
			start = false;
		else if(key == selectkey)
			select = false;
		else if(key == debugkey)
			debug = !debug;
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