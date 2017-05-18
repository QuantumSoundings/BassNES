package audio;

public class VRC6Pulse extends Channel {

	int dutynumber;
	public boolean[] current_duty = new boolean[]{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false};
	boolean[] duty0 = new boolean[]{true,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false};
	boolean[] duty1 = new boolean[]{true,true,false,false,false,false,false,false,false,false,false,false,false,false,false,false};
	boolean[] duty2 = new boolean[]{true,true,true,false,false,false,false,false,false,false,false,false,false,false,false,false};
	boolean[] duty3 = new boolean[]{true,true,true,true,false,false,false,false,false,false,false,false,false,false,false,false};
	boolean[] duty4 = new boolean[]{true,true,true,true,true,false,false,false,false,false,false,false,false,false,false,false};
	boolean[] duty5 = new boolean[]{true,true,true,true,true,true,false,false,false,false,false,false,false,false,false,false};
	boolean[] duty6 = new boolean[]{true,true,true,true,true,true,true,false,false,false,false,false,false,false,false,false};
	boolean[] duty7 = new boolean[]{true,true,true,true,true,true,true,true,false,false,false,false,false,false,false,false};
	private boolean dutymode;
	public void registerWrite(int index,byte b,int clock){
		//System.out.println("Write to pulse");
		switch(index%4){
		case 0: 
			int duty = (Byte.toUnsignedInt(b)>>>4)&7;
			switch(duty){
			case 0: current_duty = duty0;break;
			case 1: current_duty = duty1;break;
			case 2: current_duty = duty2;break;
			case 3: current_duty = duty3;break;
			case 4: current_duty = duty4;break;
			case 5: current_duty = duty5;break;
			case 6: current_duty = duty6;break;
			case 7: current_duty = duty7;break;

			}
			dutymode=b<0;
			volume=b&0xf;
			break;
		case 1: 
			timer &=0xff00;
			timer |=(b&0xff);
			break;			
		case 2: 
			timer&=0b11111111;
			timer |= (b&0b1111)<<8;
			enable = b<0;
			break;
		case 3: 
		}
		
	}
	@Override
	public final void clockTimer(){
		
		if(tcount==0){
			tcount=timer;
			dutynumber++;
			output = current_duty[dutynumber%16];
		}
		else
			tcount--;
		if(!dutymode)
			if(!enable||!output)
				return;
		total+=volume;
		//System.out.println("Clocking pulse");
	}
	@Override
	public double getOutput(){
		return total * 0.00752;
		
	}
}
