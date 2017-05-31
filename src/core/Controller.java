package core;

import core.mappers.Mapper;

public class Controller implements java.io.Serializable{

	private static final long serialVersionUID = 8353857861874602491L;
	//Keycheckerc1 keys;
	final Mapper map;
	final int controllerNum;
	boolean strobe;
	int output;
	int keysPressed;
	public int nextKey;
	boolean debug;
	
	public Controller(Mapper m,int num){
		map = m;
		strobe = false;
		output = 0;
		keysPressed=0;
		controllerNum=num;
		nextKey = 0;
		//keys = new Keycheckerc1();		
	}
	
	public byte getControllerStatus(){
		int getNextKey=0;
		if(nextKey>7)
			getNextKey = 1;
		else
			getNextKey = map.system.pollController()[controllerNum][nextKey]?1:0;
		if(!strobe)
			nextKey++;
		output&=0x11111110;
		output|=getNextKey;
		return (byte)(output);
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
	
}