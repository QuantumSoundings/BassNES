package core.audio;

import core.NesSettings;
import core.CPU_6502.IRQSource;
import core.mappers.Mapper;

public class MMC5Audio extends Channel{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7799320230787869546L;
	Mapper map;
	Pulse pulse1;
	Pulse pulse2;
	
	boolean irqEnable;
	boolean pcmMode;
	boolean doingIRQ;
	private int pcmdata;

	private final double mixRatio = 0.0226;
	private final double pcmRatio = 0.00376;
	private final double pcmAddRatio = pcmRatio/mixRatio;
	
	public MMC5Audio(Mapper m){
		super(0);
		map = m;
		outputLocation = m.apu.mixer.requestNewOutputLocation();
		pulse1 = new Pulse(true,outputLocation);
		pulse2 = new Pulse(true,outputLocation);
	}
	
	public void registerWrite(int index, byte b){
		//System.out.println("Write to MMC5Audio index: 0x50"+Integer.toHexString(index));
		switch(index){
		case 0x5000: 
			pulse1.registerWrite(0, b, 0);
			break;
		case 0x5001: 
			break;			
		case 0x5002: 
			pulse1.registerWrite(2, b, 0);
			break;
		case 0x5003:
			pulse1.registerWrite(3, b, 0);
			break;
		case 0x5004: 
			pulse2.registerWrite(0, b, 0);
			break;
		case 0x5005: 
			break;			
		case 0x5006: 
			pulse2.registerWrite(2, b, 0);
			break;
		case 0x5007: 
			pulse2.registerWrite(3, b, 0);
			break;
		case 0x5010:
			pcmMode = (b&1)==1;
			irqEnable = (b&0x80)!=0;
			break;
		case 0x5011:
			//System.out.println("Writing pcm data: "+Integer.toBinaryString(Byte.toUnsignedInt(b)));
			if(b==0&&irqEnable){
				map.cpu.setIRQ(IRQSource.External);
				doingIRQ=true;
			}
			else
				pcmdata = Byte.toUnsignedInt(b);
			break;
		case 0x5015:
			if((b&1)==1)
				pulse1.enable();
			else
				pulse1.disable();
			if((b&2)==2)
				pulse2.enable();
			else
				pulse2.disable();
			break;
		}
		
	}

	public byte readRegister(int index){
		if(index==0x5010){
			byte out = (byte) (doingIRQ?0x80:0);
			map.cpu.removeIRQ(IRQSource.External);
			doingIRQ=false;
			return out;
		}
		if(index==0x5015){
			byte out = (byte) (pulse1.lengthCount >0?1:0);
			out|=pulse2.lengthCount >0?2:0;
			return out;
		}
		return 0;
	}
	private int frameClockCounter;
	private int frameClock = 7457;
	boolean odd = false;
	@Override
	public final void clockTimer(){
		frameClockCounter++;
		//System.out.println("Clocking mmc5audio");
		if(frameClockCounter == frameClock){
			frameClockCounter = 0;
			pulse1.envelopeClock();pulse1.lengthClock();
			pulse2.envelopeClock();pulse2.lengthClock();
		}
		if(odd){
			pulse1.clockTimer();
			pulse2.clockTimer();
		}
		odd=!odd;
		AudioMixer.audioLevels[outputLocation]+=pcmdata*(pcmAddRatio);
		//System.out.println(total);
	}
	private final String name1 = "MMC5 Pulse 1";
	private final String name2 = "MMC5 Pulse 2";

	@Override
	public Object[] getInfo(){
		return new Object[]{name1,pulse1.getFrequency(),name2,pulse2.getFrequency()};
	}
	@Override
	public String getName(){
		return "MMC5 Audio";
	}

	public int getUserMixLevel(){ return NesSettings.mmc5MixLevel; }
	public int getUserPanning() {return NesSettings.mmc5Panning;}
	public double getChannelMixingRatio() {return mixRatio;}
}
