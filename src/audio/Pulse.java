package audio;

import com.jsyn.unitgen.PulseOscillator;

public class Pulse extends Channel {
	int dutynumber=7;
	boolean p1;
	public PulseOscillator wave;
	int duty;
	boolean[] duty0 = new boolean[]{false,true,false,false,false,false,false,false};
	boolean[] duty1 = new boolean[]{false,true,true,false,false,false,false,false};
	boolean[] duty2 = new boolean[]{false,true,true,true,true,false,false,false};
	boolean[] duty3 = new boolean[]{true,false,false,true,true,true,true,true};
	boolean output;
	boolean halt=false;
	public Pulse(PulseOscillator gen,boolean number) {
		super(gen);
		p1=number;
		wave = gen;
		duty = 0;
		wave.amplitude.set(.5);
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
			//dutynumber=dutynumber==8?0:dutynumber++;
		switch(duty){
		case 0: output = duty0[dutynumber];break;
		case 1: output = duty1[dutynumber];break;
		case 2: output = duty2[dutynumber];break;
		case 3: output = duty3[dutynumber];break;	
		}
	}
	
	//double[] dutylook = new double[]{10.0,5.0,0.0,5.0};
	public void registerWrite(int index,byte b){
		//System.out.println("Writing to pulse1 index: "+Integer.toHexString(index)+" byte:"+Integer.toBinaryString(Byte.toUnsignedInt(b)));
		switch(index%4){
		case 0: 
			duty = Byte.toUnsignedInt(b)>>>6;
			loop=(b&0b100000)!=0?true:false;
			constantvolume=(b&0b10000)!=0?true:false;
			volume=b&0xf;
			estart = true;
			//wave.width.set(dutylook[duty]/100);
			//System.out.println("Loop"+loop+" Volume:"+volume+" constV:"+constantvolume);
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
			//System.out.println("Timer update:"+timer);
			break;
		case 3: 
			int x = Byte.toUnsignedInt(b)>>3;
			if(enable)
				lengthcount = lengthlookup[x];
			timer&=0b11111111;
			timer |= (b&0b111)<<8;
			targetperiod = timer;
		}
		//System.out.println("Writing to pulse channel");
		
	}
	public void sweepClock(){
		if(enable){
			if(dosweep){
				//System.out.println("doing a sweep tp:"+targetperiod+" dividerp: "+dividerperiod
				//		+" Divider: "+sdivider
				//		+" timer: "+timer
				//		+" shift: "+shift
				//		+" negate: "+negate);
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
						targetperiod= targetperiod + change;
					//sdivider--;
					updateWave();
				}
				//timer = targetperiod&0b111111111111;
			}
		}
	}
	public double frequency(){
			if(targetperiod<1)
				targetperiod = 0; 
			return 1789773/(16*((targetperiod)+1));
	}
	public void updateWave(){
		//wave.start();
		//System.out.println("Updating wave with frequency: "+frequency()
		//+" Length: "+lengthcount
		//+" volume: "+decay
		//+" cVolume?: "+constantvolume
		//+" targetperiod: "+targetperiod
		//+" timer: "+timer);
		if((lengthcount==0&&!loop)||decay==0||targetperiod<8)
			wave.amplitude.set(0);
		else{
			wave.amplitude.set(decay/30.0);
		}
		//System.out.println(frequency());
		wave.frequency.set(frequency());
		
		
	}
}
