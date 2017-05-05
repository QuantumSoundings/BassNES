package audio;


public class Pulse extends Channel {

	private static final long serialVersionUID = -3321343541094080447L;
	public int dutynumber=7;
	boolean p1;
	int duty;
	public boolean[] current_duty = new boolean[]{false,false,false,false,false,false,false,false};
	boolean[] duty0 = new boolean[]{false,true,false,false,false,false,false,false};
	boolean[] duty1 = new boolean[]{false,true,true,false,false,false,false,false};
	boolean[] duty2 = new boolean[]{false,true,true,true,true,false,false,false};
	boolean[] duty3 = new boolean[]{true,false,false,true,true,true,true,true};
	public boolean output;
	boolean halt=false;
	public Pulse(boolean number){
		super();
		p1=number;
		duty = 0;
	}
	@Override
	public final void clockTimer(){
		if(tcount==0){
			tcount=timer;
			dutynumber++;
			output = current_duty[dutynumber%8];
		}
		else
			tcount--;
		if(lengthcount==0||!output||decay==0||timer<8)
			return;
		total+=decay;
		return;
	}
	
	public void registerWrite(int index,byte b,int clock){
		switch(index%4){
		case 0: 
			duty = Byte.toUnsignedInt(b)>>>6;
			switch(duty){
			case 0: current_duty = duty0;break;
			case 1: current_duty = duty1;break;
			case 2: current_duty = duty2;break;
			case 3: current_duty = duty3;break;	
			}
			if(clock==14915){
				delayedchange=(b&0b10000)!=0?2:1;
			}
			else
				loop= (b & 0b100000) != 0;
			constantvolume= (b & 0b10000) != 0;
			volume=b&0xf;
			estart = true;
			break;
		case 1: 
			dosweep = (b & 0x80) != 0;
			if(!dosweep)
				targetperiod = timer;
			dividerperiod = (b&0b1110000)>>4;
			sdivider = dividerperiod+1;
			negate = (b & 0b1000) != 0;
			shift= (b&0b111);
			sweepreload = true;
			break;			
		case 2: 
			timer &=0xff00;
			timer |=(b&0xff);
			targetperiod = timer;
			break;
		case 3: 
			int x = Byte.toUnsignedInt(b)>>3;
			if(enable)
				if(clock==14915){
					if(lengthcount==0){
						lengthcount = lengthlookup[x];
						block=true;
					}
				}
				else 
					lengthcount = lengthlookup[x];	
			timer&=0b11111111;
			timer |= (b&0b111)<<8;
			targetperiod = timer;
		}
		
	}
	public void sweepClock(){
		if(enable){
			if(dosweep){
				if(sweepreload){
					sdivider = dividerperiod+1;
					if(sdivider ==0)
						targetperiod=timer;
					sweepreload=false;
				}
				else if(sdivider !=0){
					sdivider--;
				}
				else {
					sdivider = dividerperiod+1;
					int change = targetperiod>>shift;
					if(negate){
						if(p1)
							targetperiod =  targetperiod - change -1;
						else
							targetperiod = targetperiod - change;
					}
					else
						targetperiod = targetperiod + change;
				}
				timer = targetperiod&0b111111111111;
			}
		}
	}
	@Override
	public int getOutput(){
		if(lengthcount==0||!output||decay==0||timer<8)
			return 0;
		return decay;
	}
	@Override
	public void buildOutput(){
		if(lengthcount==0||!output||decay==0||timer<8)
			return;
		total+=decay;
	}
}
