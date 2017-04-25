package audio;

import com.jsyn.unitgen.UnitGenerator;

import mappers.Mapper;

public class DMC extends Channel{
	Mapper map;
	boolean irqEnable;
	public boolean irqflag;
	int directload;
	int sampleaddress;
	public int samplelength;
	int samplebuffer;
	public int stallcpu;
	int rate;
	int temprate;
	int ratelook;
	int[] rateindex = new int[]{428, 380, 340, 320, 286, 254, 226, 214, 190, 160, 142, 128, 106,  84,  72,  54};
	double[] pitchtable = new double[]{4181.71,4709.93,5264.04,5593.04,6257.95,7046.35,7919.35,8363.42,9419.86,11186.1,12604.0,13982.6,16884.6,21306.8,24858.0,33143.9};
	public DMC(UnitGenerator gen, Mapper m) {
		super(gen);
		map = m;
	}
	
	public void registerWrite(int index,byte b){
		switch(index%4){
		case 0: 
			irqEnable = (b&0x80)!=0?true:false;
			//if(!irqEnable&&irqflag)
			//	map.cpu.doIRQ--;
			loop = (b&0x40)!=0?true:false;
			ratelook = b&0xf;
			rate = rateindex[b&0xf];
			temprate = rate;
		break;
		case 1: 
			directload = b&0b1111111;			
			break;			
		case 2: 
			sampleaddress=Byte.toUnsignedInt(b);
			break;
		case 3: 
			samplelength= Byte.toUnsignedInt(b);
		}		
	}
	public void clock(){
		temprate--;
		if(temprate==0){
			outputUnit();
			temprate=rate;
		}
	}
	void memoryreader(){
		if(map.cpu.writeDMA)
			stallcpu=2;
		else
			stallcpu=4;
		samplebuffer = map.cpuread(sampleaddress);
		if(sampleaddress==0xffff)
			sampleaddress = 0x8000;
		else
			sampleaddress++;
		samplelength--;
		if(samplelength==0&&loop);//restart sample
		else if(samplelength==0&&irqEnable){
			//map.cpu.doIRQ++;
			//irqflag=true;
		}	
	}
	int bitsremaining;
	int shiftreg;
	boolean silence;
	void outputUnit(){
		if(!silence){
			if((shiftreg&1)!=0)
				directload += directload+2>127?0:2;
			else
				directload -= directload-2<0?0:2;		
		}
		shiftreg>>=1;
		bitsremaining--;
		if(bitsremaining==0){
			bitsremaining = 8;
			if(samplebuffer==0)
				silence=true;
			else{
				silence=false;
				shiftreg=samplebuffer;
				memoryreader();
			}
		}
	}

}
