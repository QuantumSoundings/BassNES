package core.audio;


import core.NesSettings;

public class Noise extends Channel{
	private static final long serialVersionUID = 7294397072264670989L;
	public Noise(int location){
		super(location);
	}
	//int noiseperiod;
	boolean mode=false;
	int shiftreg=1;
	int[] noiselookup= new int[]{
			4, 8, 16, 32, 64, 96, 128, 160, 202, 254, 380, 508, 762, 1016, 2034, 4068};
	public void registerWrite(int index,byte b,int clock){
		clockTimer();
		switch(index%4){
		case 0: 
			if(clock ==14195)
				delayedChange =(b&16)!=0?2:1;
			else
				loop = (b & 32) != 0;
			constantVolume = (b & 16) != 0;
			volume = b&0xf;
			break;
		case 1: 

			break;			
		case 2: 
			mode = (b & 0x80) != 0;
			int noiseperiod= b&0xf;
			timer = noiselookup[noiseperiod]-1;
			//System.out.println("Current Timer: "+timer+" New Timer: "+noiselookup[noiseperiod]);
			break;
		case 3: 
			if(enable)
					if(clock==14915){
						if(lengthCount ==0){
							lengthCount = (b&0xff)>>>3;
							lengthCount = lengthLookupTable[lengthCount];
							block=true;
						}
					}
					else{
						lengthCount = (b&0xff)>>>3;
						lengthCount = lengthLookupTable[lengthCount];
					}
			decay = volume;
			eStart = true;
			break;
		default: break;
		}		
	}
	@Override
	public final void clockTimer(){
		if(timer<=0) {
			tCount=timer;
			clockCount=0;
			return;
		}
		int x = clockCount - tCount;
		do {
			//System.out.println("Clock: "+clockCount +" t: "+tCount);
			if (x >= 0) {
				if(!(lengthCount ==0||(shiftreg&1)==1))
					AudioMixer.audioLevels[outputLocation]+= decay*tCount;
				int feedback;
				tCount =timer;
				if(mode)
					feedback = ((shiftreg>>6)&1)^(shiftreg&1);
				else
					feedback = ((shiftreg>>1)&1)^(shiftreg&1);
				shiftreg>>=1;
				shiftreg|= (feedback<<14);
				if(x==0||tCount==0)
					break;
			} else {
				if(!(lengthCount ==0||(shiftreg&1)==1))
					AudioMixer.audioLevels[outputLocation]+= decay*clockCount;
				tCount -= clockCount;
				break;
			}
			clockCount=x;
			x = clockCount-tCount;
		}while(true);
		clockCount=0;
		/*if(tCount ==0){
			int feedback;
			tCount =timer;
			if(mode)
				feedback = ((shiftreg>>6)&1)^(shiftreg&1);
			else
				feedback = ((shiftreg>>1)&1)^(shiftreg&1);
			shiftreg>>=1;
			shiftreg|= (feedback<<14);
		}
		else
			tCount--;
		if(lengthCount ==0||(shiftreg&1)==1)
			return;
		//if(constantVolume)
		//	AudioMixer.audioLevels[outputLocation]+=volume;
		//else
			AudioMixer.audioLevels[outputLocation]+= decay;
		return;
		*/
	}
	//@Override
	public double getOutput(){
		if(lengthCount ==0||(shiftreg&1)==1)
			return 0;
		if(constantVolume)
			return volume*(NesSettings.noiseMixLevel/100.0);
		else
			return decay*(NesSettings.noiseMixLevel/100.0);
	}

	public int getUserPanning(){ return NesSettings.noisePanning;}
	public int getUserMixLevel(){return NesSettings.noiseMixLevel;}
	public double getChannelMixingRatio() {return .00494;}

}
