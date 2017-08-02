package core.audio;

import core.NesSettings;

public class VRC6Saw extends Channel {
	
	
	
	private static final long serialVersionUID = 6678747718079279169L;
	private int accumulatorRate;
	private int accumulator;
	public VRC6Saw(int location){
		super(location);
	}

	public void registerWrite(int index, byte b){
		switch(index%4){
		case 0: 
			accumulatorRate = b&0b111111;
			break;
		case 1: 
			timer &=0xff00;
			timer |=(b&0xff);
			break;			
		case 2: 
			timer&=0b11111111;
			timer |= (b&0b1111)<<8;
			enable = b<0;
			if(!enable)
				accumulator = 0;
			
			break;
		case 3: 
		}
		
	}
	private boolean oddClock;
	private int sawClock;
	@Override
	public final void clockTimer(){
		
		if(tCount ==0){
			tCount =timer;
			if(oddClock){
				if(enable){
					sawClock++;
					if(sawClock >6){
						accumulator=0;
						sawClock = 0;
					}
					else
						accumulator += accumulatorRate;
					
				}
				else
					accumulator = 0;
			}
			oddClock =!oddClock;
		}
		else
			tCount--;
		if(!enable){
			return;
		}
		AudioMixer.audioLevels[outputLocation]+=accumulator>>3;
	}

	@Override
	public double getFrequency(){
		if(!enable|| accumulatorRate ==0)
			return 0;
		return 1789773 / (14.0 * (timer + 1));
	}
	private final String name = "VRC6 Saw";
	@Override
	public Object[] getInfo(){
		return new Object[]{name,getFrequency()};
	}
	@Override
	public String getName(){
		return "VRC6 Saw";
	}

	public double getChannelMixingRatio() {return .01200;}
	public int getUserMixLevel(){ return NesSettings.vrc6MixLevel; }
	public int getUserPanning(){
		return NesSettings.vrc6Panning;
	}
}
