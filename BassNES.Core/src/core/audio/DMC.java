package core.audio;
import core.CPU_6502.IRQSource;
import core.NesSettings;
import core.mappers.Mapper;

public class DMC extends Channel{
	private static final long serialVersionUID = -5904036727532211365L;

	final Mapper map;

	boolean irqEnable= false;
	public boolean irqflag = false;
	public boolean silence=true;
	boolean bufferempty = true;
	int outputlevel = 0;
	int sampleaddress=0xc000;
	int addressStart=0xc000;
	public int samplelength=1;
	public int sampleremaining=0;
	int samplebuffer=0;
	int rate=0x36;
	int temprate = 0x36;
	int bitsremaining=8;
	int shiftreg = 0;


	final int[] rateindex = new int[]{428, 380, 340, 320, 286, 254, 226, 214, 190, 160, 142, 128, 106,  84,  72,  54};
	public DMC(Mapper m,int location) {
		super(location);
		map = m;
		bitsremaining=8;
		loop = false;
	}

	public void registerWrite(int index,byte b){
		switch(index%4){
			case 0:
				irqEnable = (b & 0x80) == 0x80;
				if(!irqEnable)
					map.cpu.removeIRQ(IRQSource.DMC);
				loop = (b & 0x40) == 0x40;
				rate = rateindex[b&0xf];
				//System.out.println("Write to $4010: "+Integer.toBinaryString((0xff&b))+" rate: "+rate);
				break;
			case 1:
				outputlevel = b&0b1111111;
				//System.out.println("Writing to dmc Direct load: "+outputlevel);
				break;
			case 2:
				addressStart=((0xff&b)<<6) + 0xc000;
				//System.out.println("Writing to dmc sample address: "+Integer.toHexString(sampleaddress));
				break;
			case 3:
				samplelength= ((0xff&b)<<4)+1;
				//System.out.println("Writing to dmc sample length: "+samplelength);
				break;
		}
	}
	public void clearFlag(){
		map.cpu.removeIRQ(IRQSource.DMC);
	}
	@Override
	public final void clockTimer(){

		if(bufferempty && sampleremaining >0)
			memoryreader();
		temprate--;
		if(temprate <=0){
			if(!silence){
				outputlevel+=(shiftreg&1)==1?2:-2;
				if(outputlevel>127) outputlevel-=2;
				else if(outputlevel<0) outputlevel +=2;
				shiftreg>>=1;
				--bitsremaining;
			}
			if(bitsremaining<=0){
				bitsremaining = 8;
				if(bufferempty)
					silence = true;
				else{
					silence = false;
					shiftreg = samplebuffer;
					bufferempty = true;
				}
			}
			temprate = rate;
		}
		AudioMixer.audioLevels[outputLocation]+=(outputlevel);
	}
	public void memoryreader(){
		if(sampleremaining>0){
			//System.out.println("Fetching new Sample; Samples remaining: "+sampleremaining); 
			samplebuffer = map.cpureadu(sampleaddress++);
			bufferempty = false;
			if(map.cpu.writeDMA){
				/*System.out.println("DMAC: "+map.cpu.dmac);
				if(map.cpu.dmac == 2)
					map.cpu.stall(1);
				else if(map.cpu.dmac == 1)
					map.cpu.stall(3);
				else{
					System.out.println("Stalling 2");
					map.cpu.stall(2);
				}*/
				map.cpu.stall(2);//tallcpu(2);//stallcpu=2;
			}

			else{
				//System.out.println("Non DMA stall");
				/*if(map.lastcpuwrite)
					if(map.lastwriteaddress==0x4014)
						map.cpu.stall(2);
					else
						map.cpu.stall(3);
				else*/
				map.cpu.stall(4);
			}
			//	map.cpu.stall(4);//stallcpu(4);//stallcpu=4;

			if(sampleaddress>0xffff)
				sampleaddress = 0x8000;
			--sampleremaining;
			if(sampleremaining == 0){
				if(loop)
					restart();
				else if(irqEnable){
					map.cpu.setIRQ(IRQSource.DMC);
				}
			}
		}
		else
			silence = true;

	}
	public void restart(){
		sampleaddress = addressStart;
		sampleremaining = samplelength;
		silence=false;
	}
	void outputUnit(){
		//System.out.println("In the outputunit");

		if(bitsremaining<=0){
			bitsremaining = 8;
			if(bufferempty){
				silence=true;
			}
			else{
				silence=false;
				shiftreg=samplebuffer;
				samplebuffer=0;
				bufferempty = true;

			}
			//memoryreader();
		}
		if(!silence){
			outputlevel+=(shiftreg&1)==1?2:-2;
			if(outputlevel>127) outputlevel=126;
			else if(outputlevel<0) outputlevel =1;
			shiftreg>>>=1;
			bitsremaining--;
		}


	}
	//@Override
	public double getOutput(){
		return (outputlevel/2.0)*(NesSettings.dmcMixLevel/100.0);
	}
	public int getUserPanning(){ return NesSettings.dmcPanning;}
	public int getUserMixLevel(){return NesSettings.dmcMixLevel;}
	public double getChannelMixingRatio() {return .00335;}


}
