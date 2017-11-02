package core.mappers;

import java.util.Arrays;

public class MMC4 extends Mapper {
	private static final long serialVersionUID = 5235723987173641102L;
	public MMC4(){
		super();
		System.out.println("Making an MMC4!");
	}
	@Override
	protected void setPRG(byte[] prg){
		PRG_ROM = new byte[2][0x4000];
		PRGbanks = new byte[prg.length/0x4000][0x4000];
		for(int i=0;i*0x4000<prg.length;i++){
			PRGbanks[i]=Arrays.copyOfRange(prg, i*0x4000, (i*0x4000)+0x4000);
		}
		PRG_ROM[0]=PRGbanks[0];
		PRG_ROM[1]=PRGbanks[PRGbanks.length-1];
		PRG_RAM = new byte[0x2000];
	}
	@Override
	protected void setCHR(byte[] chr){
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
	int latch0;
	byte[][] latch0bank = new byte[2][0x1000];
	int latch1;
	byte[][] latch1bank = new byte[2][0x1000];
	@Override
	public void cartridgeWrite(int index, byte b){
		if(index>=0x6000&&index<=0x7fff){
			PRG_RAM[index%0x2000]=b;
		}
		else if(index>=0xa000&&index<=0xafff){
			PRG_ROM[0] = PRGbanks[(b&0xf)&(PRGbanks.length-1)];
		}
		else if(index>=0xb000&&index<=0xbfff){
			latch0bank[0] = CHRbanks[(b&0x1f)&(CHRbanks.length-1)];
		}
		else if(index>=0xc000&&index<=0xcfff){
			latch0bank[1] = CHRbanks[(b&0x1f)&(CHRbanks.length-1)];
		}
		else if(index>=0xd000&&index<=0xdfff){
			latch1bank[0] = CHRbanks[(b&0x1f)&(CHRbanks.length-1)];
		}
		else if(index>=0xe000&&index<=0xefff){
			latch1bank[1] = CHRbanks[(b&0x1f)&(CHRbanks.length-1)];
		}
		else if(index>=0xf000&&index<=0xffff){
			mirrormode = (b&1)==1;
			if(mirrormode)
				setNameTable(Mirror.Horizontal);
			else
				setNameTable(Mirror.Vertical);
		}
	}
	@Override
	public byte cartridgeRead(int index){
		if(index<0x8000)
			return PRG_RAM[index%0x2000];
		else
			return PRG_ROM[(index-0x8000)/0x4000][index%0x4000];
	}
	@Override
	public byte ppureadPT(int index){
		byte r;
		if(index<0x1000)
			r= latch0bank[latch0==0xfd?0:1][index%0x1000];
		else
			r=latch1bank[latch1==0xfd?0:1][index%0x1000];
		if(index>=0xfd8&&index<=0xfdf)
			latch0 = 0xfd;
		else if(index>=0xfe8&&index<=0xfef)
			latch0 = 0xfe;
		else if(index>=0x1fd8&&index<=0x1fdf)
			latch1 = 0xfd;
		else if(index>=0x1fe8&&index<=0x1fef)
			latch1 = 0xfe;
		return r;
		
	}
	@Override
	public byte ppuread(int index){
		if(index<0x2000){
			return ppureadPT(index);
		}
		else if(index>=0x2000&&index<=0x3eff){
			index&=0xfff;
			return nametables[index/0x400][index%0x400];
		}
		else{
			index = index&0x1f;
			index-= (index>=0x10&&(index&3)==0)?0x10:0;
			return ppu_palette[index];
		}
		
	}
}
