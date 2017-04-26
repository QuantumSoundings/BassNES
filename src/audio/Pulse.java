package audio;


public class Pulse extends Channel {
	int dutynumber=7;
	boolean p1;
	int duty;
	boolean[] duty0 = new boolean[]{false,true,false,false,false,false,false,false};
	boolean[] duty1 = new boolean[]{false,true,true,false,false,false,false,false};
	boolean[] duty2 = new boolean[]{false,true,true,true,true,false,false,false};
	boolean[] duty3 = new boolean[]{true,false,false,true,true,true,true,true};
	boolean output;
	boolean halt=false;
	public Pulse(boolean number){
		super();
		p1=number;
		duty = 0;
	}
	@Override
	public void clockTimer(){
		super.clockTimer();
		if(tcount==timer){
			if(dutynumber==7)
				dutynumber = 0;
			else
				dutynumber++;
		}
		switch(duty){
		case 0: output = duty0[dutynumber];break;
		case 1: output = duty1[dutynumber];break;
		case 2: output = duty2[dutynumber];break;
		case 3: output = duty3[dutynumber];break;	
		}
	}
	
	public void registerWrite(int index,byte b,int clock){
		switch(index%4){
		case 0: 
			duty = Byte.toUnsignedInt(b)>>>6;
			if(clock==14915){
				delayedchange=(b&0b10000)!=0?2:1;
			}
			else
				loop=(b&0b100000)!=0?true:false;
			constantvolume=(b&0b10000)!=0?true:false;
			volume=b&0xf;
			estart = true;
			break;
		case 1: 
			dosweep = (b&0x80)!=0?true:false;
			if(!dosweep)
				targetperiod = timer;
			dividerperiod = (b&0b1110000)>>4;
			sdivider = dividerperiod+1;
			negate = (b&0b1000)!=0?true:false;
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
				else if(sdivider ==0){
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
	public int getOutput(){
		if(lengthcount==0||!output||decay==0||timer<8)
			return 0;
		return decay;
	}
}
