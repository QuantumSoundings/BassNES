package core.audio;

import core.NesSettings;

public class VRC6Pulse extends Channel {

	private static final long serialVersionUID = -9117535976094772034L;
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
	private boolean chanone = false;
	public VRC6Pulse(boolean channelone,int location){
		super(location);
		chanone = channelone;
		name = "VRC6 Pulse "+ (chanone?"1":"2");
	}
	public void registerWrite(int index, byte b){
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
		
		if(tCount ==0){
			tCount =timer;
			dutynumber++;
			output = current_duty[dutynumber%16];
		}
		else
			tCount--;
		if(!dutymode)
			if(!enable||!output)
				return;
		AudioMixer.audioLevels[outputLocation]+=volume;
		//System.out.println("Clocking pulse");
	}

	@Override
	public double getFrequency(){
		if(!enable)
			return 0;
		return 1789773 / (16.0 * (timer + 1));
	}
	private final String name;;
	@Override
	public Object[] getInfo(){
		return new Object[]{name,getFrequency()};
	}
	@Override
	public String getName(){
		return "VRC6 Pulse";
	}

	public double getChannelMixingRatio() {return .00776;}
	public int getUserPanning(){
		return NesSettings.vrc6Panning;
	}
	public int getUserMixLevel(){
		return NesSettings.vrc6MixLevel;
	}
}
