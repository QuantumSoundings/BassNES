package audio;

import com.jsyn.ports.UnitInputPort;
import com.jsyn.unitgen.PulseOscillator;
import com.jsyn.unitgen.UnitGenerator;

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
	
	
	public void registerWrite(int index,byte b){
		//System.out.println("Writing to pulse1 index: "+Integer.toHexString(index)+" byte:"+Integer.toBinaryString(Byte.toUnsignedInt(b)));
		switch(index%4){
		case 0: 
			duty = b>>6;
			loop=(b&0b100000)!=0?true:false;
			constantvolume=(b&0b10000)!=0?true:false;
			volume=b&0xf;
			estart = true;
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
			lengthcount = lengthlookup[x];
			timer&=0b11111111;
			timer |= (b&0b111)<<8;
			targetperiod = timer;
		}
		//System.out.println("Writing to pulse channel");
		
	}
	public void sweepClock(){
		if(dosweep){
			if(sweepreload){
				sdivider = dividerperiod+1;
				targetperiod=timer;
				sweepreload=false;
			}
			else if(sdivider ==0){
				timer = targetperiod;
				//divider--;
			}
			else{
				int change = timer>>shift;
				if(negate){
					if(p1)
						targetperiod =  timer - change -1;
					else
						targetperiod = timer - change;
				}
				else
					targetperiod= timer + change;
				sdivider--;
			}
			timer = targetperiod;
			updateWave();
		}
	}
	public double frequency(){
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
		if(lengthcount==0&&!loop)
			wave.setEnabled(false);
		else{
			wave.setEnabled(true);
			wave.amplitude.set(decay/30.0);
		}
		//System.out.println(frequency());
		wave.frequency.set(frequency());
		
		
	}
}
