package core.audio;


public abstract class Channel implements java.io.Serializable {

	private static final long serialVersionUID = 1054848637029892308L;
	int outputLocation;
	public boolean enable;
	boolean output;
	int tCount;
	int timer;

	//Envelope Variables
	boolean eStart;
	boolean constantVolume;
	public int decay;
	int eDivider;
	int volume;
	boolean loop;

	//Length Counter Variables
	public int lengthCount;
	final int[] lengthLookupTable = new int[]{
			10,254, 20,  2, 40,  4, 80,  6, 160,  8, 60, 10, 14, 12, 26, 14,
			12, 16, 24, 18, 48, 20, 96, 22, 192, 24, 72, 26, 16, 28, 32, 30};
	int delayedChange;
	boolean block;
	public int clockCount=0;

	public Channel(int location) {
		outputLocation = location;
	}

	//Each channel needs its own timer implementation
	public abstract void clockTimer();
	//protected abstract void onClockRollover();
	//protected abstract void addOutput(int multiplier);
	//Used by just about every channel
	public void lengthClock(){
		if(enable&&!block){
			if(lengthCount !=0){
				if(!loop)
					lengthCount--;
			}
		}
		if(delayedChange !=0){
			loop = delayedChange == 2;
			delayedChange =0;
		}
		block=false;
	}
	public void disable(){
		enable=false;
		lengthCount =0;
	}
	public void enable(){
		enable = true;
	}

	//Used in the Pulse and Noise channels
	public final void envelopeClock(){
		if(enable){
			if(eStart){
				eStart =false;
				decay = 15;
				eDivider = volume+1;
			}
			else {
				if(eDivider ==0){
					eDivider =volume+1;
					if(decay==0){
						if(loop)
							decay=15;
	
					}
					else
						decay--;
				}
				eDivider--;
			}
			if(constantVolume)
				decay = volume;
		
		}
	}

	//Methods for getting information to the Visualizer
	public double getFrequency(){ return 0; }
	public Object[] getInfo(){
		return new Object[0];
	}
	public String getName(){
		return "";
	}

	//Methods for providing mixing information
	public abstract double getChannelMixingRatio();
	public abstract int getUserMixLevel();
	public abstract int getUserPanning();
}
