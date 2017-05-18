package audio;

public class VRC6Saw extends Channel {
	
	
	int accumRate;
	int accumulator;
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
	public double getOutput(){
		return total * 0.00752;
		
	}
}
