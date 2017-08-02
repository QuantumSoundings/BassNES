package core.audio;

import core.NesSettings;

public class Triangle extends Channel {
	private static final long serialVersionUID = 4651788745714469245L;
	//Linear Variables
	private boolean linearReloadFlag;
	private boolean linearControl;
	private int linearReload;
	private int linearCount;

	public Triangle(int location){
		super(location);
	}
	public void registerWrite(int index,byte b,int clock){
		switch(index%4){
		case 0: 
			linearReload = b&0b01111111;
			if(clock==14915)
				delayedChange =(b&0x80)!=0?2:1;
			else
				linearControl = (b & 0x80) != 0;
			//System.out.println(Integer.toBinaryString(Byte.toUnsignedInt(b)));

			break;
		case 1: 
			
			break;			
		case 2: 
			timer &=0xff00;
			timer |=(b&0xff);
			break;
		case 3: 
			int x = Byte.toUnsignedInt(b)>>3;
			if(enable)
				if(clock==14915){
					if(lengthCount ==0){
						lengthCount = lengthlookup[x];
						block=true;
					}
				}
				else lengthCount = lengthlookup[x];
			
			timer&=0b11111111;
			timer |= (b&0b111)<<8;
			linearReloadFlag = true;
			break;
		}
	}
	int[] lengthlookup= new int[]{
			10,254, 20,  2, 40,  4, 80,  6, 160,  8, 60, 10, 14, 12, 26, 14,
			12, 16, 24, 18, 48, 20, 96, 22, 192, 24, 72, 26, 16, 28, 32, 30};
	public int[] sequencer = new int[]{
			15, 14, 13, 12, 11, 10,  9,  8,  7,  6,  5,  4,  3,  2,  1,  0,
			 0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15};
	public int sequenceNum=0;
	@Override
	public void lengthClock(){
		if(enable&&!block){
			if(lengthCount !=0){
				if(!linearControl)
					lengthCount--;
			}
		}
		if(delayedChange !=0){
			loop = delayedChange == 2;
			delayedChange =0;
		}
		block=false;
	}
	public void linearClock(){
		if(enable){
			if(linearReloadFlag){
				linearCount = linearReload;
			}
			else if(linearCount >0)
				linearCount--;
			if(!linearControl)
				linearReloadFlag =false;
		}
	}
	@Override
	public final void clockTimer(){
		if(tCount ==0){
			if(linearCount !=0&& lengthCount !=0&&timer>2)
				sequenceNum=(sequenceNum+1)%32;
			tCount =timer;
		}
		else
			tCount--;

		AudioMixer.audioLevels[outputLocation] += sequencer[sequenceNum];
		
	}
	//@Override
	public double getOutput(){
		return sequencer[sequenceNum]*(NesSettings.triangleMixLevel/100.0);
	}
	private final String name = "Triangle";
	@Override
	public Object[] getInfo(){
		return new Object[]{name,getFrequency()};
	}
	@Override
	public double getFrequency(){
		if(timer==0)
			return 0;
		return 1789773 / (32.0 * (timer + 1));
	}
	@Override
	public String getName(){
		return "Triangle";
	}
	public int getUserPanning(){ return NesSettings.trianglePanning;}
	public int getUserMixLevel(){return NesSettings.triangleMixLevel;}
	public double getChannelMixingRatio() {return .00851;}

}
