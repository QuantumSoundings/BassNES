package core.mappers;

import java.util.Arrays;

public class VRC1 extends Mapper{

	
	private static final long serialVersionUID = -774631191215076450L;
	public VRC1(){
		super();
		System.out.println("Made a VRC1!");
	}
	@Override
	protected void setPRG(byte[] prg){
		PRG_ROM = new byte[4][0x2000];
		PRGbanks = new byte[prg.length/0x2000][0x2000];
		for(int i=0;i*0x2000<prg.length;i++){
			PRGbanks[i]=Arrays.copyOfRange(prg, i*0x2000, (i*0x2000)+0x2000);
		}
		PRG_ROM[0]=PRGbanks[0];
		PRG_ROM[1]=PRGbanks[1];
		PRG_ROM[2]=PRGbanks[PRGbanks.length-2];
		PRG_ROM[3]=PRGbanks[PRGbanks.length-1];
	}
	@Override
	protected void setCHR(byte[] chr){
		CHR_ROM = new byte[2][0x1000];
		if(chr.length>0){
			CHRbanks = new byte[chr.length/0x1000][0x1000];
			for(int i=0;i*0x1000<chr.length;i++)
				CHRbanks[i]= Arrays.copyOfRange(chr, i*0x1000, (i*0x1000)+0x1000);
			CHR_ROM[0]=CHRbanks[0];
			CHR_ROM[1]=CHRbanks[1];
		}
		else{
			CHR_ram = true;
		}
	}
	int chrselect0;
	int chrselect1;
	@Override
	public final void cartridgeWrite(int index, byte b){
		if(index<0x8000);
		else if(index<=0x8fff){
			PRG_ROM[0] = PRGbanks[(b&0xf)&(PRGbanks.length-1)];
		}
		else if(index>=0x9000&&index<=0x9fff){
			if((b&1)==1)
				setNameTable(Mirror.Horizontal);
			else
				setNameTable(Mirror.Vertical);
			chrselect0&=0xf;
			chrselect0|= (b&2)<<3;
			chrselect1&=0xf;
			chrselect1|= (b&4)<<2;
			CHR_ROM[0] = CHRbanks[chrselect0&(CHRbanks.length-1)];
			CHR_ROM[1] = CHRbanks[chrselect1&(CHRbanks.length-1)];
		}
		else if(index>=0xa000&&index<=0xafff){
			PRG_ROM[1] = PRGbanks[(b&0xf)&(PRGbanks.length-1)];
		}
		else if(index>=0xc000&&index<=0xcfff){
			PRG_ROM[2] = PRGbanks[(b&0xf)&(PRGbanks.length-1)];
		}
		else if(index>=0xe000&&index<=0xefff){
			chrselect0&=0xf0;
			chrselect0|=b&0xf;
			CHR_ROM[0] = CHRbanks[chrselect0&(CHRbanks.length-1)];
		}
		else if(index>=0xf000&&index<=0xffff){
			chrselect1&=0xf0;
			chrselect1|=b&0xf;
			CHR_ROM[1] = CHRbanks[chrselect1&(CHRbanks.length-1)];
		}
	}
	@Override
	final byte cartridgeRead(int index){
		if(index<0x8000)
			return 0;
		else if(index<0xa000)
			return PRG_ROM[0][index-0x8000];
		else if(index<0xc000)
			return PRG_ROM[1][index-0xa000];
		else if(index<0xe000)
			return PRG_ROM[2][index-0xc000];
		else if(index<=0xffff)
			return PRG_ROM[3][index-0xe000];
		return 0;
	}
}
