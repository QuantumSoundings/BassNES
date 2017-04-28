package mappers;

import java.util.Arrays;

public class MMC3 extends Mapper {
	//byte[][] PRGbanks;
	//byte[][] CHRbanks;
	public MMC3(){
		System.out.println("Making an MMC3!");
	}
	@Override
	public void setPRG(byte[] prg){
		PRG_ROM = new byte[4][0x2000];
		PRGbanks = new byte[prg.length/0x2000][0x2000];
		for(int i=0;i*0x2000<prg.length;i++){
			PRGbanks[i]=Arrays.copyOfRange(prg, i*0x2000, (i*0x2000)+0x2000);
		}
		PRG_ROM[0]=PRGbanks[0];
		PRG_ROM[1]=PRGbanks[1];
		PRG_ROM[2]=PRGbanks[PRGbanks.length-2];
		PRG_ROM[3]=PRGbanks[PRGbanks.length-1];
		PRG_RAM = new byte[0x2000];
	}
	@Override
	public void setCHR(byte[] chr){
		CHR_ROM = new byte[8][0x400];
		if(chr.length>0){
			CHRbanks = new byte[chr.length/0x400][0x400];
			for(int i=0;i*0x400<chr.length;i++)
				CHRbanks[i]= Arrays.copyOfRange(chr, i*0x400, (i*0x400)+0x400);
			CHR_ROM[0]=CHRbanks[0];
			CHR_ROM[1]=CHRbanks[1];
			CHR_ROM[2]=CHRbanks[2];
			CHR_ROM[3]=CHRbanks[3];
			CHR_ROM[4]=CHRbanks[4];
			CHR_ROM[5]=CHRbanks[5];
			CHR_ROM[6]=CHRbanks[6];
			CHR_ROM[7]=CHRbanks[7];
		}
		else{
			CHR_ram = true;
		}
	}
	//Registers
	boolean PRG_mode;
	boolean CHR_mode;
	boolean mirrormode;//true = horizontal; false = vertical;
	boolean reloadirq;
	boolean irqenable;
	boolean doingIRQ;
	boolean oldsignal=false;
	boolean currentsignal;
	int scanlinecount;
	int irqreload;
	int bankselect;
	int bankdata;
	@Override
	public void cartridgeWrite(int index, byte b){
		if(index<0x8000)
			PRG_RAM[index-0x6000]=b;
		else if(index>=0x8000&&index<0xa000){
			if(index%2==0){
				bankselect = b;
				PRG_mode = (b&0x40)!=0?true:false;
				CHR_mode = (b&0x80)!=0?true:false;
			}
			else
				selectBank(b);	
		}
		else if(index>=0xa000&&index<0xc000){
			if(index%2==0)
				mirrormode = (b&1)!=0?true:false;
			else{
				//maybe don't implement
			}
		}
		else if(index>=0xc000&&index<0xe000){
			if(index%2==0){
				irqreload = Byte.toUnsignedInt(b);
				if(control.checkDebug())
					System.out.println("Setting reload to : "+irqreload+" SL: "+ppu.scanline);
			}
			else{
				scanlinecount=0;
			}
		}
		else if(index>=0xe000&&index<=0xffff){
			if(index%2==0){
				if(control.checkDebug())
				System.out.println("setting irq enable to false scanline: "+ppu.scanline);
				if(doingIRQ)
					cpu.doIRQ--;
				doingIRQ=false;
				irqenable = false;
			}
			else{
				if(control.checkDebug())
				System.out.println("Setting irq enable to true scanline: "+ppu.scanline);
				irqenable = true;
			}
		}
	}
	
	void selectBank(byte b){
		switch(bankselect&0b111){
		case 0://select 2kb chr bank at CHR_ROM[0-1] (4-5)
			if(CHR_mode){
				CHR_ROM[4] = CHRbanks[(b&(CHRbanks.length-1))];
				CHR_ROM[5] = CHRbanks[(b&(CHRbanks.length-1))+1];
			}
			else{
				CHR_ROM[0] = CHRbanks[(b&(CHRbanks.length-1))];
				CHR_ROM[1] = CHRbanks[(b&(CHRbanks.length-1))+1];
			}
			break;
		case 1://select 2kb chr bank at CHR_ROM[2-3] (6-7)
			if(CHR_mode){
				CHR_ROM[6] = CHRbanks[(b&(CHRbanks.length-1))];
				CHR_ROM[7] = CHRbanks[(b&(CHRbanks.length-1))+1];
			}
			else{
				CHR_ROM[2] = CHRbanks[(b&(CHRbanks.length-1))];
				CHR_ROM[3] = CHRbanks[(b&(CHRbanks.length-1))+1];
			}
			break;
		case 2://select 1kb chr bank at CHR_ROM[4] (0)
			if(CHR_mode)
				CHR_ROM[0] = CHRbanks[(b&(CHRbanks.length-1))];
			else
				CHR_ROM[4] = CHRbanks[(b&(CHRbanks.length-1))];
			break;
		case 3://select 1kb chr bank at CHR_ROM[5] (1)
			if(CHR_mode)
				CHR_ROM[1] = CHRbanks[(b&(CHRbanks.length-1))];
			else
				CHR_ROM[5] = CHRbanks[(b&(CHRbanks.length-1))];
			break;
		case 4://select 1kb chr bank at CHR_ROM[6] (2)
			if(CHR_mode)
				CHR_ROM[2] = CHRbanks[(b&(CHRbanks.length-1))];
			else
				CHR_ROM[6] = CHRbanks[(b&(CHRbanks.length-1))];
			break;
		case 5://select 1kb chr bank at CHR_ROM[7] (3)
			if(CHR_mode)
				CHR_ROM[3] = CHRbanks[(b&(CHRbanks.length-1))];
			else
				CHR_ROM[7] = CHRbanks[(b&(CHRbanks.length-1))];
			break;
		case 6://select 8kb prg bank at PRG_ROM[0] (2)
			if(PRG_mode){
				PRG_ROM[2] = PRGbanks[(b&(PRGbanks.length-1))];
				PRG_ROM[0] = PRGbanks[PRGbanks.length-2];
			}
			else{
				PRG_ROM[0] = PRGbanks[(b&(PRGbanks.length-1))];
				PRG_ROM[2] = PRGbanks[PRGbanks.length-2];
			}
			break;
		case 7://select 8kb prg bank at PRG_rom[1]
			PRG_ROM[1] = PRGbanks[(b&(PRGbanks.length-1))];
			break;
		default:break;
		}
		
	}
	@Override
	byte cartridgeRead(int index){
		if(index<0x8000&&index>=0x6000)
			return PRG_RAM[index-0x6000];
		else if(index>=0x8000&&index<0xa000)
			return PRG_ROM[0][index-0x8000];
		else if(index>=0xa000&&index<0xc000)
			return PRG_ROM[1][index-0xa000];
		else if(index>=0xc000&&index<0xe000)
			return PRG_ROM[2][index-0xc000];
		else if(index>=0xe000&&index<=0xffff)
			return PRG_ROM[3][index-0xe000];
		else
			return 0;
	}
	
	@Override
	public byte ppuread(int index){
		if(index<0x2000){
			if(index<0x400)
				return CHR_ROM[0][index];
			else if(index>=0x400&&index<0x800)
				return CHR_ROM[1][index-0x400];
			else if(index>=0x800&&index<0xc00)
				return CHR_ROM[2][index-0x800];
			else if(index>=0xc00&&index<0x1000)
				return CHR_ROM[3][index-0xc00];
			else if(index>=0x1000&&index<0x1400)
				return CHR_ROM[4][index-0x1000];
			else if(index>=0x1400&&index<0x1800)
				return CHR_ROM[5][index-0x1400];
			else if(index>=0x1800&&index<0x1c00)
				return CHR_ROM[6][index-0x1800];
			else
				return CHR_ROM[7][index-0x1c00];
		}
		else if(index>=0x2000&&index<=0x2fff)
			return ppu_ram[ppuNameTableMirror(index)];
		else if(index>=0x3000&&index<=0x3eff)
			return ppu_ram[ppuNameTableMirror(index-0x1000)];
		else
			return ppu_palette[(index&0xff)%0x20];
	}
	@Override
	public void ppuwrite(int index,byte b){
		if(index<0x2000&&CHR_ram){
			//check(index);
			switch(index/0x400){
			case 0: CHR_ROM[0][index%0x400] =b;break;
			case 1: CHR_ROM[1][index%0x400] =b;break;
			case 2: CHR_ROM[2][index%0x400] =b;break;
			case 3: CHR_ROM[3][index%0x400] =b;break;
			case 4: CHR_ROM[4][index%0x400] =b;break;
			case 5: CHR_ROM[5][index%0x400] =b;break;
			case 6: CHR_ROM[6][index%0x400] =b;break;
			case 7: CHR_ROM[7][index%0x400] =b;break;
			}
		}
		else if(index>=0x2000&&index<=0x2fff)
			ppu_ram[ppuNameTableMirror(index)]=b;
		else if(index>=0x3000&&index<=0x3eff){
			ppu_ram[ppuNameTableMirror(index-0x1000)]=b;
		}
		else{
			int i = (index&0xff)%0x20;
			if(i%4==0){
				ppu_palette[i]=b;
				i+= i<0x10?0x10:-0x10;
				ppu_palette[i]=b;
			}
			ppu_palette[(index&0xff)%0x20]=b;
		}
	}
	
	@Override
	int ppuNameTableMirror(int index){
		if(mirrormode){//default is horizontal
			if(index>=0x2000&&index<0x2400)
				return index-0x2000;
			else if(index>=0x2400&&index<0x2800)
				return index-0x2400;
			else if(index>=0x2800&&index<0x2c00)
				return index-0x2400;
			else
				return index-0x2800;
		}
		else{
			if(index>=0x2000&&index<0x2400)
				return index-0x2000;
			else if(index>=0x2400&&index<0x2800)
				return index-0x2000;
			else if(index>=0x2800&&index<0x2c00)
				return index-0x2800;
			else
				return index-0x2800;
		}
	}
	boolean cura12;
	public boolean olda12;
	int countdown;
	//@Override
	public void check(int x){
		
		cura12 = (x&0x1000)!=0;
		if(cura12&&(!olda12)){
			if(countdown<=0)
				scanlinecounter();
			}
		else if(!cura12&&olda12){
			countdown=8;
		}
		countdown--;
		olda12 = cura12;
	}
	boolean x;
	@Override
	public void scanlinecounter(){
		//int t = scanlinecount;
		
		//System.out.println("scanline: " + ppu.scanline+" counter: "+scanlinecount+" rendering: "+ppu.dorender()+ " v:"+Integer.toHexString(ppu.v));
		if(scanlinecount--==0){
			scanlinecount = irqreload;
		}
		if(scanlinecount==0&&irqenable){
			if(!doingIRQ){
				//System.out.println("Generating IRQ at scanline: "+ppu.scanline+" pcycle: "+ppu.pcycle+" iflag: "+cpu.IFlag );
				cpu.doIRQ++;
			}
			doingIRQ=true;
			if(control.checkDebug())
				System.out.println("Generating IRQ at scanline: "+ppu.scanline+" pcycle: "+ppu.pcycle+" iflag: "+cpu.IFlag );
		}
		
		
		
	}
}

