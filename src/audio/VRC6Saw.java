package audio;

import ui.UserSettings;

public class VRC6Saw extends Channel {
	
	
	
	private static final long serialVersionUID = 6678747718079279169L;
	int accumRate;
	int accumulator;
	public VRC6Saw(){
		super();
	}
	@Override
	public int getUserMixLevel(){
		return UserSettings.vrc6MixLevel;
	}
	public void registerWrite(int index,byte b,int clock){
		//System.out.println("Write to pulse");
		switch(index%4){
		case 0: 
			accumRate = b&0b111111;
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
	boolean oddclock;
	int sawclock;
	@Override
	public final void clockTimer(){
		
		if(tcount==0){
			tcount=timer;
			if(oddclock){
				if(enable){
					sawclock++;
					if(sawclock>6){
						accumulator=0;
						sawclock = 0;
					}
					else
						accumulator +=accumRate;
					
				}
				else
					accumulator = 0;
			}
			oddclock=!oddclock;
		}
		else
			tcount--;
		if(!enable){
			return;
		}
		total+=accumulator>>3;
		//System.out.println("Clocking pulse");
	}
	@Override
	public int getOutputSettings(){
		return UserSettings.vrc6MixLevel;
	}
	@Override
	public double getFrequency(){
		if(!enable||accumRate==0)
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
	@Override
	public double getOutput(){
		return total * 0.00376;
		
	}
}
