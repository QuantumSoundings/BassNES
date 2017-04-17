package audio;

import com.jsyn.unitgen.WhiteNoise;

public class Noise extends Channel{
	public WhiteNoise wave;
	public Noise(WhiteNoise n){
		super(n);
		wave = n;
		
	}
	int noiseperiod;
	boolean mode;
	int shiftreg;
	int[] noiselookup= new int[]{
			4, 8, 16, 32, 64, 96, 128, 160, 202, 254, 380, 508, 762, 1016, 2034, 4068};
	public void registerWrite(int index,byte b){
		switch(index%4){
		case 0: 
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
			if(enable){
				lengthcount = (b&0b11111000)>>>3;
				lengthcount = lengthlookup[lengthcount];
			}
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
				feedback = ((shiftreg&0b100000)>>5)^(shiftreg&1);
			else
				feedback = ((shiftreg&2)>>1)^(shiftreg&1);
			shiftreg>>=1;
			shiftreg|= (feedback<<13);
		}
		else
			tcount--;
	}
	
	public void updateWave(){
		//System.out.println("Updating wave "
		//+" Length: "+lengthcount
		//+" volume: "+decay
		//+" cVolume?: "+constantvolume
		//+" targetperiod: "+targetperiod
		//+" timer: "+timer);
	if(lengthcount==0||volume==0)//(shiftreg&1)==0||volume==0)
		wave.amplitude.set(0);
	else
		//wave.setEnabled(true);
		wave.amplitude.set(decay/30.0);
	}
}
