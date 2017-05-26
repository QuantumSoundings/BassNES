package audio;

import ui.UserSettings;

public class NamcoSound extends Channel {

	

	private static final long serialVersionUID = -5491358056873773710L;


	private byte[] soundmemory;
	
	private int[] phase;
	private int[] wavelength;
	private int[] waveout;
	private int[] timers;
	private int[] waveaddress;
	private int[] vol;
	private boolean[] enables;
	private int currentchannel;
	private int outputlevel;
	
	public NamcoSound(byte[] sound){
		phase = new int[8];
		wavelength = new int[8];
		waveout = new int[8];
		waveaddress = new int[8];
		vol = new int[8];
		enables = new boolean[8];
		timers = new int[8];
		currentchannel = 0;
		soundmemory = sound;
	}
	int cpucounter;
	@Override
	public final void clockTimer(){
		if(cpucounter%15==0){
			do{
				currentchannel = (currentchannel+1)%8;
				if(currentchannel==0)
					break;
				if(enables[currentchannel])
					break;
			}while(true);
			
			//currentchannel = (currentchannel+1)%8;
			clockChannel(currentchannel);
			//outputlevel = waveout[currentchannel];
		}
		total+=outputlevel;
		cpucounter++;
	}
	public void registerWrite(int index,byte b,int chan){
		//System.out.println("Write to pulse");
		switch(index){
		case 0:
			timers[chan]&=0x3ff00;
			timers[chan]|=(0xff&b);
			break;
		case 1:
			phase[chan]&=0xffff00;
			phase[chan]|=(0xff&b);
			break;
		case 2:
			timers[chan]&=0x300ff;
			timers[chan]|=(0xff&b)<<8;
			break;
		case 3:
			phase[chan]&=0xff00ff;
			phase[chan]|=(0xff&b)<<8;
			break;
		case 4:
			timers[chan]&=0xffff;
			timers[chan]|=(0x3&b)<<16;
			wavelength[chan] = 0xff&b;
			break;
		case 5:
			phase[chan]&=0xffff;
			phase[chan]|=(0xff&b)<<16;
			break;
		case 6:
			waveaddress[chan] = b&0xff;
			break;
		case 7:
			vol[chan] = b&0xf;
			break;
		}
		//System.out.println("sound reg write :"+index);
	}
	public void setmem(byte[] mem){
		soundmemory = mem;
	}
	public final void clockChannel(int chan){
		if(enables[chan]){
			int length = (64 - (wavelength[chan]>>2))*4;
			phase[chan] = (phase[chan] + timers[chan]) % (length<<16);
			waveout[chan] = (sample(((phase[chan] >> 16)+ waveaddress[chan])& 0xff)-8)*vol[chan];	
			sumoutput();
		}

	}
	private int sample(int x){
		return (soundmemory[x/2] >> ((x&1)*4))&0x0f;
	}
	public void setEnables(int c){
		for(int i = 7; i>=0;i--){
			if(i>=7-c)
				enables[i]=true;
			else
				enables[i]=false;
		}
	}
	private void sumoutput(){
		outputlevel = 0;
		for(int out:waveout)
			outputlevel+=out;
	}
	@Override
	public void disable(){
		enable=false;
	}
	@Override
	public void enable(){
		enable = true;
	}
	@Override
	public int getUserMixLevel(){
		return UserSettings.namcoMixLevel;
	}
	@Override
	public double getOutput(){
		//System.out.println(total);
		double out = total *.00047;
		total = 0;
		return out;
	}
}
