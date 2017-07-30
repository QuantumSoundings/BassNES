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
		switch(index%4){
		case 0: 
			if(clock ==14195)
				delayedchange=(b&16)!=0?2:1;
			else
				loop = (b & 32) != 0;
			constantvolume = (b & 16) != 0;
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
						if(lengthcount==0){
							lengthcount = (b&0xff)>>>3;
							lengthcount = lengthlookup[lengthcount];
							block=true;
						}
					}
					else{
						lengthcount = (b&0xff)>>>3;
						lengthcount = lengthlookup[lengthcount];
					}
			decay = volume;
			estart = true;
			break;
		default: break;
		}		
	}
	@Override
	public final void clockTimer(){
		if(tcount==0){
			int feedback;
			tcount =timer;
			if(mode)
				feedback = ((shiftreg>>6)&1)^(shiftreg&1);
			else
				feedback = ((shiftreg>>1)&1)^(shiftreg&1);
			shiftreg>>=1;
			shiftreg|= (feedback<<14);
		}
		else
			tcount--;
		if(lengthcount==0||(shiftreg&1)==1)
			return;
		//if(constantvolume)
		//	AudioMixer.audioLevels[outputLocation]+=volume;
		//else
			AudioMixer.audioLevels[outputLocation]+= decay;
		return;
	}
	@Override
	public double getOutput(){
		if(lengthcount==0||(shiftreg&1)==1)
			return 0;
		if(constantvolume)
			return volume*(NesSettings.noiseMixLevel/100.0);
		else
			return decay*(NesSettings.noiseMixLevel/100.0);
	}
	
	@Override
	public void buildOutput(){
		if(lengthcount==0||(shiftreg&1)==0)
			return;
		total += decay;
	}

}
