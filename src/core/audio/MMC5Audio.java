package core.audio;

import core.CPU_6502.IRQSource;
import core.mappers.Mapper;
import ui.UserSettings;

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
	int pcmdata;
	
	public MMC5Audio(Mapper m){
		map = m;
		pulse1 = new Pulse(true);
		pulse2 = new Pulse(true);
	}
	
	public void registerWrite(int index,byte b,int clock){
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
				//if(!doingIRQ){
				//	System.out.println("Triggering IRQ");
				//	map.cpu.doIRQ++;
				//}
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
			byte out = (byte) (pulse1.lengthcount>0?1:0);
			out|=pulse2.lengthcount>0?2:0;
			return out;
		}
		return 0;
	}
	int frameclockcounter;
	int frameclock = 7457;
	boolean odd = false;
	@Override
	public final void clockTimer(){
		frameclockcounter++;
		//System.out.println("Clocking mmc5audio");
		if(frameclockcounter==frameclock){
			frameclockcounter = 0;
			pulse1.envelopeClock();pulse1.lengthClock();
			pulse2.envelopeClock();pulse2.lengthClock();
		}
		if(odd){
			pulse1.clockTimer();
			pulse2.clockTimer();
		}
		odd=!odd;
		total+=pcmdata+pulse1.getOutput()+pulse2.getOutput();
		//System.out.println(total);
	}
	@Override
	public double getOutput(){
		//System.out.println("total: "+total +" p1: "+pulse1.total+" p2: "+pulse2.total);
		double out = total*0.00216;
		total = 0;
		out += pulse1.total*0.00376;
		pulse1.total=0;
		out += pulse2.total*0.00376;
		pulse2.total=0;
		return out;	
	}
	@Override
	public int getOutputSettings(){
		return UserSettings.mmc5MixLevel;
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
	@Override
	public int getUserMixLevel(){
		return UserSettings.mmc5MixLevel;
	}
}
