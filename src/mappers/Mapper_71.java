package mappers;

import java.util.Arrays;

public class Mapper_71 extends Mapper{
	
	private static final long serialVersionUID = 2108208415488234374L;
	public Mapper_71(){
		super();
		System.out.println("Making a Mapper 71");
	}
	
	@Override
	public void setPRG(byte[] prg){
		PRGbanks = new byte[prg.length/0x4000][0x4000];
		for(int i=0;i*0x4000<prg.length;i++){
			PRGbanks[i]=Arrays.copyOfRange(prg, i*0x4000, (i*0x4000)+0x4000);
		}
		System.out.println("Bank size:" +PRGbanks.length);
		PRG_ROM[0]=PRGbanks[0];
		PRG_ROM[1]=PRGbanks[PRGbanks.length-1];
		//printMemory(0xff00, 0xff);
	}
	@Override
	public void setCHR(byte[] chr){
		if(chr.length>0){
			for(int i=0;i*0x1000<chr.length;i++){
				CHR_ROM[i]= Arrays.copyOfRange(chr, i*0x1000, (i*0x1000)+0x1000);
			}
		}
		else CHR_ram = true;
	}
	@Override
	public void cartridgeWrite(int index, byte b){
		if(index>=0x8000&&index<=0x9fff){
			if((b&16)!=0)
				setNameTable(Mirror.SingleScreenHigh);
			else
				setNameTable(Mirror.SingleScreenLow);
		}
		else if(index>=0xc000&&index<=0xffff){
			PRG_ROM[0] = PRGbanks[(b&0xf)&(PRGbanks.length-1)];
		}
	}
	
}
