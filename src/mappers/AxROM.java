package mappers;

import java.util.Arrays;

public class AxROM extends Mapper{
	private static final long serialVersionUID = -365861735346222319L;
	byte[][] CHRbanks;
	byte[][] PRGbanks;
	public AxROM(){
		super();
		System.out.println("Mapper 7 (AxROM) Fully Supported!");
		PRG_RAM = new byte[0x2000];
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
	int nametable;
	@Override
	public void cartridgeWrite(int index,byte b){
		if(index>=0x8000&&index<=0xffff){
			nametable = (b&0b10000)>>4;
			if(nametable==0){
				nametables[0]=ppu_internal_ram[0];
				nametables[1]=ppu_internal_ram[0];
				nametables[2]=ppu_internal_ram[0];
				nametables[3]=ppu_internal_ram[0];
			}
			else{
				nametables[0]=ppu_internal_ram[1];
				nametables[1]=ppu_internal_ram[1];
				nametables[2]=ppu_internal_ram[1];
				nametables[3]=ppu_internal_ram[1];
			}
			int x = ((b&0b111)*2)&(PRGbanks.length-1);
			PRG_ROM[0]=PRGbanks[x];
			PRG_ROM[1]=PRGbanks[x+1];
		}
		
	}
	/*@Override
	int ppuNameTableMirror(int index){
		switch(nametable){
		case 0:
			if(index>=0x2400&&index<0x2800)
				return index%0x400;
			else if(index>=0x2800&&index<0x2c00)
				return index%0x400;
			else if(index>=0x2c00)
				return index%0x400;
			else return index%0x400;
		case 1:
			if(index>=0x2000&&index<0x2400)
				return index-0x1600;
			else if(index>=0x2400&&index<0x2800)
				return index-0x2000;
			else if(index>=0x2800&&index<0x2c00)
				return index-0x2400;
			else return index-0x2800;
		default:
			System.out.println("Something went wrong in ppunametable mirroring");
			return 0;
		}
	}*/
	@Override
	public void setPRG(byte[] prg){
		PRGbanks = new byte[prg.length/0x4000][0x4000];
		for(int i=0;i*0x4000<prg.length;i++){
			PRGbanks[i]=Arrays.copyOfRange(prg, i*0x4000, (i*0x4000)+0x4000);
		}
		PRG_ROM[0]=PRGbanks[0];
		PRG_ROM[1]=PRGbanks[1];
	}

}
