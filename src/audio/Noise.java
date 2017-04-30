package audio;


public class Noise extends Channel{
	public Noise(){
		super();		
	}
	int noiseperiod;
	boolean mode;
	int shiftreg=1;
	int[] noiselookup= new int[]{
			4, 8, 16, 32, 64, 96, 128, 160, 202, 254, 380, 508, 762, 1016, 2034, 4068};
	public void registerWrite(int index,byte b,int clock){
		switch(index%4){
		case 0: 
			if(clock ==14195)
				delayedchange=(b&16)!=0?2:1;
			else
				loop = (b&32)==0?false:true;
			constantvolume = (b&16)==0?false:true;
			volume = b&0xf;
			break;
		case 1: 

			break;			
		case 2: 
			mode = (b&0x80)==0?false:true;	
			noiseperiod= b&0xf;
			timer = noiselookup[noiseperiod];
			shiftreg=1;
			break;
		case 3: 
			if(enable)
					if(clock==14915){
						if(lengthcount==0){
							lengthcount = (b&0b11111000)>>>3;
							lengthcount = lengthlookup[lengthcount];
							block=true;
						}
					}
					else{
						lengthcount = (b&0b11111000)>>>3;
						lengthcount = lengthlookup[lengthcount];
					}
			decay = volume;
			break;
		default: break;
		}		
	}
	int feedback;
	@Override
	public void clockTimer(){
		if(tcount==0){
			tcount =timer;
			if(mode)
				feedback = ((shiftreg&0b1000000)>>6)^(shiftreg&1);
			else
				feedback = ((shiftreg&2)>>1)^(shiftreg&1);
			shiftreg>>=1;
			shiftreg|= (feedback<<14);
		}
		else
			tcount--;
	}
	@Override
	public int getOutput(){
		if(lengthcount==0||(shiftreg&1)==0)
			return 0;
		if(constantvolume)
			return volume;
		else
			return decay;
	}

}
