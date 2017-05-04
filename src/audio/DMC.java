package audio;
import mappers.Mapper;

public class DMC extends Channel{
	private static final long serialVersionUID = -5904036727532211365L;

	Mapper map;
	
	boolean irqEnable;
	public boolean irqflag;
	boolean silence;
	int outputlevel;
	int sampleaddress;
	int addressStart;
	public int samplelength;
	public int sampleremaining=0;
	int samplebuffer=0;
	public int stallcpu;
	int rate;
	int temprate;
	int bitsremaining;
	int shiftreg;
	
	
	int[] rateindex = new int[]{428, 380, 340, 320, 286, 254, 226, 214, 190, 160, 142, 128, 106,  84,  72,  54};
	//double[] pitchtable = new double[]{4181.71,4709.93,5264.04,5593.04,6257.95,7046.35,7919.35,8363.42,9419.86,11186.1,12604.0,13982.6,16884.6,21306.8,24858.0,33143.9};
	public DMC(Mapper m) {
		super();
		map = m;
		bitsremaining=8;
	}
	
	public void registerWrite(int index,byte b){
		switch(index%4){
		case 0: 
			irqEnable = (b&0x80)!=0?true:false;
			if(!irqEnable&&irqflag){
				map.cpu.doIRQ--;
				irqflag=false;
			}
			loop = (b&0x40)!=0?true:false;
			rate = rateindex[b&0xf]/2;
			//System.out.println("Write to $4010: "+Integer.toBinaryString(Byte.toUnsignedInt(b))+" rate: "+rate);
			temprate = rate;
			break;
		case 1: 
			outputlevel = b&0b1111111;
			//System.out.println("Writing to dmc Direct load: "+outputlevel);
			break;			
		case 2: 
			sampleaddress=Byte.toUnsignedInt(b)*64 + 0xc000;
			addressStart = sampleaddress;
			//System.out.println("Writing to dmc sample address: "+Integer.toHexString(sampleaddress));
			break;
		case 3: 
			samplelength= Byte.toUnsignedInt(b)*16+1;
			//System.out.println("Writing to dmc sample length: "+samplelength);
			sampleremaining = samplelength;
			break;
		}		
	}
	public void clearFlag(){
		if(irqflag){
			irqflag = false;
			map.cpu.doIRQ--;
		}
	}
	@Override
	public final void clockTimer(){
		if(temprate==0){
			outputUnit();
			temprate=rate;
		}
		else
			temprate--;
		total+=outputlevel;
		return;
	}
	
	void memoryreader(){
		if(sampleremaining!=0 && samplebuffer==0){
			//System.out.println("Fetching new Sample; Samples remaining: "+sampleremaining); 
			if(map.cpu.writeDMA)
				stallcpu=2;
			else
				stallcpu=4;
			samplebuffer = map.cpureadu(sampleaddress);
			if(sampleaddress==0xffff)
				sampleaddress = 0x8000;
			else
				sampleaddress++;
			
			sampleremaining--;
		}
		else if(sampleremaining==0&&loop){
			//System.out.println("Looping sample");
			sampleaddress = addressStart;//restart sample
			sampleremaining = samplelength;
		}
		else if(sampleremaining==0&&irqEnable){
			if(!irqflag)
				map.cpu.doIRQ++;
			irqflag=true;
		}	
	}
	
	void stealCycles(int i){
		while(i>0){
			
		}
	}
	void outputUnit(){
		//System.out.println("In the outputunit");
		if(bitsremaining==0){
			bitsremaining = 8;
			if(samplebuffer==0){
				silence=true;
			}
			else{
				silence=false;
				shiftreg=samplebuffer;
				samplebuffer=0;	
			}
			memoryreader();
		}
		if(!silence){
			outputlevel+=(shiftreg&1)==1?2:-2;
			if(outputlevel>127) outputlevel=127;
			else if(outputlevel<0) outputlevel =0;
			//if((shiftreg&1)==1)
			//	outputlevel += (outputlevel+2>127?0:2);
			//else
				//outputlevel -= outputlevel-2<0?0:2;		
		}
		shiftreg>>=1;
		bitsremaining--;
		
	}
	@Override
	public int getOutput(){
		return outputlevel;	
	}
	@Override
	public void buildOutput(){
		total+=outputlevel;
	}
	@Override
	public void enable(){
		//System.out.println("Enabling dmc channel");
		if(sampleremaining==0){
			sampleremaining=samplelength;
		}
		//enable=true;
	}
	@Override
	public void disable(){
		sampleremaining = 0;
	}

}
