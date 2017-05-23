package mappers;

import java.util.Arrays;

public class Mapper_11 extends Mapper{

	private static final long serialVersionUID = -8539386315915429584L;
	public Mapper_11(){
		super();
		System.out.println("Making mapper 11");
	}
	@Override
	public void setPRG(byte[] prg){
		PRGbanks = new byte[prg.length/0x4000][0x4000];
		for(int i=0;i*0x4000<prg.length;i++){
			PRGbanks[i]=Arrays.copyOfRange(prg, i*0x4000, (i*0x4000)+0x4000);
		}
		PRG_ROM[0]=PRGbanks[0];
		PRG_ROM[1]=PRGbanks[PRGbanks.length-1];
	}
	@Override
	public void setCHR(byte[] chr){
		if(chr.length>0){
		CHRbanks = new byte[chr.length/0x1000][0x1000];
		for(int i=0;i*0x1000<chr.length;i++)
			CHRbanks[i]= Arrays.copyOfRange(chr, i*0x1000, (i*0x1000)+0x1000);
		CHR_ROM[0]=CHRbanks[0];
		CHR_ROM[1]=CHRbanks[1];
		}
		else{
			CHR_ROM[0]= new byte[0x1000];
			CHR_ROM[1]= new byte[0x1000];
			CHR_ram = true;
		}
	}
	@Override
	public void cartridgeWrite(int index, byte b){
		if(index>=0x8000&&index<=0xffff){
			int prg = (b&3)<<1;
			int chr = (b&0xf0)>>3;
			PRG_ROM[0] = PRGbanks[prg&(PRGbanks.length-1)];
			PRG_ROM[1] = PRGbanks[(prg&(PRGbanks.length-1))+1];
			CHR_ROM[0] = CHRbanks[(chr&(CHRbanks.length-1))];
			CHR_ROM[1] = CHRbanks[(chr&(CHRbanks.length-1))+1];
		}
			
		
	}
}
