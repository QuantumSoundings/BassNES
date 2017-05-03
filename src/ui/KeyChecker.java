package ui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyChecker implements KeyListener, java.io.Serializable {

	private static final long serialVersionUID = 6782112428112431713L;
	private boolean a=false;
	private boolean b=false;
	private boolean up=false;
	private boolean down=false;
	private boolean start=false;
	private boolean select=false;
	private boolean left=false;
	private boolean right=false;
	private boolean a2=false;
	private boolean b2=false;
	private boolean up2=false;
	private boolean down2=false;
	private boolean start2=false;
	private boolean select2=false;
	private boolean left2=false;
	private boolean right2=false;
	private boolean debug=false;
	//controller bindings
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
		else if(key ==UserSettings.c2up)
			up = true;
		else if(key == UserSettings.c2down)
			down = true;
		else if(key == UserSettings.c2left)
			left = true;
		else if(key == UserSettings.c2right)
			right = true;
		else if(key == UserSettings.c2a)
			a = true;
		else if(key == UserSettings.c2b)
			b = true;
		else if(key == UserSettings.c2start)
			start = true;
		else if(key == UserSettings.c2select)
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
		else if(key ==UserSettings.c2up)
			up = false;
		else if(key == UserSettings.c2down)
			down = false;
		else if(key == UserSettings.c2left)
			left = false;
		else if(key == UserSettings.c2right)
			right = false;
		else if(key == UserSettings.c2a)
			a = false;
		else if(key == UserSettings.c2b)
			b = false;
		else if(key == UserSettings.c2start)
			start = false;
		else if(key == UserSettings.c2select)
			select = false;
		//else if(key == debugkey)
		//	debug = !debug;
	}
	public boolean[][] currentKeys(){
		return new boolean[][]{{a,b,select,start,up,up?false:down,left,left?false:right},
		{a2,b2,select2,start2,up2,up2?false:down2,left2,left2?false:right2}};
	}
	@Override
	public void keyTyped(KeyEvent arg0) {
	}
}
