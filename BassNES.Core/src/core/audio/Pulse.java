package core.audio;


import core.NesSettings;

public class Pulse extends Channel {

	private static final long serialVersionUID = -3321343541094080447L;
	private int dutynumber=7;
	private boolean p1;
	private int duty;
	private boolean[] current_duty = new boolean[]{false,false,false,false,false,false,false,false};
	private boolean[] duty0 = new boolean[]{false,true,false,false,false,false,false,false};
	private boolean[] duty1 = new boolean[]{false,true,true,false,false,false,false,false};
	private boolean[] duty2 = new boolean[]{false,true,true,true,true,false,false,false};
	private boolean[] duty3 = new boolean[]{true,false,false,true,true,true,true,true};
	public boolean output;
	//Sweep Variables
	private boolean doSweep;
	private int targetPeriod;
	private int sDivider;
	private int dividerPeriod;
	private boolean sweepReload =false;
	private int shift;
	private boolean negate;

	public Pulse(boolean number,int location){
		super(location);
		p1=number;
		duty = 0;
		name = getName();
	}
	@Override
	public final void clockTimer(){
		if(timer<=0) {
			tCount=timer;
			clockCount=0;
			return;
		}
		int x = clockCount - tCount;
		/*if(x>=0){
			addOutput(tCount);
			onClockRollover();
			if(tCount==0)
				return;
			int times = x/tCount;
			int remain = x%tCount;
			if(times>=1){
				for(int i = 0; i < times;i++){
					addOutput(tCount);
					onClockRollover();
				}

			}
			if(remain>0){
				addOutput(remain);
				tCount-=remain;
			}
		}
		else{
			addOutput(clockCount);
			tCount-=clockCount;
		}
		clockCount=0;*/
		do {
			if (x >= 0) {
				if (!(lengthCount == 0 || !output || decay == 0 || timer < 8))
					AudioMixer.audioLevels[outputLocation] += decay * tCount;
				tCount = timer;
				dutynumber++;
				output = current_duty[dutynumber % 8];
				if(x==0||tCount==0)
					break;
			} else {
				if (!(lengthCount == 0 || !output || decay == 0 || timer < 8))
					AudioMixer.audioLevels[outputLocation] += decay * clockCount;
				tCount -= clockCount;
				break;
			}
			clockCount=x;
			x = clockCount-tCount;
		}while(true);
		clockCount=0;
		/*if(tCount ==0){
			tCount =timer;
			dutynumber++;
			output = current_duty[dutynumber%8];
		}
		else
			tCount--;
		if(!(lengthCount ==0||!output||decay==0||timer<8))
			AudioMixer.audioLevels[outputLocation] += decay;
			*/
	}
	protected void onClockRollover(){
		tCount = timer;
		dutynumber++;
		output = current_duty[dutynumber % 8];
	}
	protected void addOutput(int multiplier){
		if (!(lengthCount == 0 || !output || decay == 0 || timer < 8))
			AudioMixer.audioLevels[outputLocation] += decay * multiplier;
	}
	public void registerWrite(int index,byte b,int clock){
		clockTimer();
		switch(index%4){
		case 0: 
			duty = (0xff&b)>>>6;
			switch(duty){
			case 0: current_duty = duty0;break;
			case 1: current_duty = duty1;break;
			case 2: current_duty = duty2;break;
			case 3: current_duty = duty3;break;	
			}
			if(clock==14915){
				delayedChange =(b&0b10000)!=0?2:1;
			}
			else
				loop= (b & 0b100000) != 0;
			constantVolume = (b & 0b10000) != 0;
			volume=b&0xf;
			eStart = true;
			break;
		case 1: 
			doSweep = (b & 0x80) != 0;
			if(!doSweep)
				targetPeriod = timer;
			dividerPeriod = (b&0b1110000)>>4;
			sDivider = dividerPeriod +1;
			negate = (b & 0b1000) != 0;
			shift= (b&0b111);
			sweepReload = true;
			break;			
		case 2: 
			timer &=0xff00;
			timer |=(b&0xff);
			targetPeriod = timer;
			break;
		case 3: 
			int x = (0xff&b)>>3;
			if(enable)
				if(clock==14915){
					if(lengthCount ==0){
						lengthCount = lengthLookupTable[x];
						block=true;
					}
				}
				else 
					lengthCount = lengthLookupTable[x];
			dutynumber=0;
			timer&=0b11111111;
			timer |= (b&0b111)<<8;
			targetPeriod = timer;
			eStart =true;
		}
		
	}
	public void sweepClock(){
		if(enable){
			if(doSweep){
				if(sweepReload){
					sDivider = dividerPeriod +1;
					if(sDivider ==0)
						targetPeriod =timer;
					sweepReload =false;
				}
				else if(sDivider !=0){
					sDivider--;
				}
				else {
					sDivider = dividerPeriod +1;
					int change = targetPeriod >>shift;
					if(negate){
						if(p1)
							targetPeriod =  targetPeriod - change -1;
						else
							targetPeriod = targetPeriod - change;
					}
					else
						targetPeriod = targetPeriod + change;
				}
				timer = targetPeriod &0b111111111111;
			}
		}
	}
	//@Override
	public double getOutput(){
		if(lengthCount ==0||!output||decay==0||timer<8)
			return 0;
		return decay*((p1? NesSettings.pulse1MixLevel:NesSettings.pulse2MixLevel)/100.0);
	}
	@Override
	public double getFrequency(){
		if(getOutput()==0)
			return 0;
		return 1789773 / (16.0 * (timer + 1));
	}
	@Override
	public Object[] getInfo(){
		return new Object[]{name,getFrequency()};
	}
	private final String name;
	@Override
	public String getName(){
		return "Pulse "+(p1?"1":"2");
	}
	public int getUserPanning(){ return p1?NesSettings.pulse1Panning:NesSettings.pulse2Panning;}
	public int getUserMixLevel(){return p1?NesSettings.pulse1MixLevel:NesSettings.pulse2MixLevel;}
	public double getChannelMixingRatio() {return .00752;}
}
