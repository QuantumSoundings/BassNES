package mappers;

import java.util.Arrays;

public class MMC1 extends Mapper {
	int shiftregister;
	int PRG_ROM_mode;
	int CHR_ROM_mode;
	int Mirror_mode;
	byte[][] PRGbanks;
	byte[][] CHRbanks;
	public MMC1(){
		System.out.println("Mapper 1 (SNROM) Fully Supported!"); 
		PRG_RAM = new byte[0x2000];
	}
	
	@Override
	public void cartridgeWrite(int index, byte b){
		if(index>=0x6000&&index<0x8000)
			PRG_RAM[index-0x6000]=b; 
		if(index>=0x8000&&index<=0xffff){
			if(b<0){
				shiftregister=0b10000;
				PRG_ROM[1]=PRGbanks[PRGbanks.length-1];
			}
			else if((shiftregister&1)==0){
				shiftregister>>=1;
				int x = (Byte.toUnsignedInt(b)&1)<<4;
				shiftregister|=x;
			}
			else if((shiftregister&1)==1){
				shiftregister>>=1;
				int x = (Byte.toUnsignedInt(b)&1)<<4;
				shiftregister|=x;
				writeRegister(index);
				shiftregister = 0b10000;
			}
		}	
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
	int ppuNameTableMirror(int index){
		switch(Mirror_mode){
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
		case 2:
			mirrormode=false;
			return super.ppuNameTableMirror(index);
		case 3:
			mirrormode=true;
			return super.ppuNameTableMirror(index);
		default:
			System.out.println("Something went wrong in ppunametable mirroring");
			return 0;
		}
	}
	@Override
	byte cartridgeRead(int index){
		if(index<0x8000&&index>=0x6000)
			return PRG_RAM[index-0x6000];
		else if(index>=0x8000&&index<0xc000)
			return PRG_ROM[0][index-0x8000];
		else if(index>=0xc000)
			return PRG_ROM[1][index-0xc000];
		else
			return 0;
	}
	public void writeRegister(int index){
		if(index>=0x8000&&index<=0x9fff){// Control register
			Mirror_mode = shiftregister&0b11;
			PRG_ROM_mode = (shiftregister&0b1100)>>2;
			CHR_ROM_mode = (shiftregister&0b10000)>>4;	
		}
		else if(index>=0xa000&&index<=0xbfff){// CHR bank 0 select
			if(!CHR_ram)
				if(CHR_ROM_mode ==0){
					CHR_ROM[0]=CHRbanks[((shiftregister&0b11110)&(CHRbanks.length-1))];
					CHR_ROM[1]=CHRbanks[((shiftregister&0b11110)&(CHRbanks.length-1))+1];			
				}
				else
					CHR_ROM[0]=CHRbanks[shiftregister&(CHRbanks.length-1)];
		}
		else if(index>=0xc000&&index<=0xdfff){// CHR bank 1 select
			if(!CHR_ram)
				if(CHR_ROM_mode==1)
					CHR_ROM[1]=CHRbanks[shiftregister&(CHRbanks.length-1)];
		}
		else if(index>=0xe000&&index<=0xffff){
			switch(PRG_ROM_mode){
			case 0: case 1:
				PRG_ROM[0] = PRGbanks[(shiftregister&0b1110)];
				PRG_ROM[1] = PRGbanks[(shiftregister&0b1110)+1];
				break;
			case 2:
				PRG_ROM[0]= PRGbanks[0];
				PRG_ROM[1] = PRGbanks[(shiftregister&0b1111)&(PRGbanks.length-1)];
				break;
			case 3:
				PRG_ROM[0] = PRGbanks[shiftregister&(PRGbanks.length-1)];
				PRG_ROM[1] = PRGbanks[PRGbanks.length-1];
				break;
			}
		}
		
	}
}
