package core.mappers;

import java.util.Arrays;

public class MMC1 extends Mapper {
	private static final long serialVersionUID = 5115571780542961923L;
	int shiftregister;
	int PRG_ROM_mode;
	int CHR_ROM_mode;
	int Mirror_mode;
	byte[][] PRGbanks;
	byte[][] CHRbanks;
	int banknumber;
	int lastbank;
	boolean bigrom;
	boolean smallchrrom;
	int lastwrite=0;
	boolean prgramdisable;
	public MMC1(){
		super();
		System.out.println("Mapper 1 (SNROM) Fully Supported!"); 
		PRG_RAM = new byte[0x2000];
	}
	
	@Override
	public void cartridgeWrite(int index, byte b){
		if(index>=0x6000&&index<0x8000&&!prgramdisable)
			PRG_RAM[index%0x2000]=b; 
		if(lastwrite == cpuclock-1)
			return;
		if(index>=0x8000&&index<=0xffff){
			if(b<0){
				shiftregister=0b10000;
				shiftregister= ((Mirror_mode)|(PRG_ROM_mode<<2)|(CHR_ROM_mode<<4))|0xc;
				writeRegister(0x8000);
				shiftregister= 0b10000;
				PRG_ROM[1] = PRGbanks[lastbank];
			}
			else if((shiftregister&1)==0){
				shiftregister>>=1;
				int x = ((0xff&b)&1)<<4;
				shiftregister|=x;
			}
			else if((shiftregister&1)==1){
				shiftregister>>=1;
				int x = ((0xff&b)&1)<<4;
				shiftregister|=x;
				writeRegister(index);
				shiftregister = 0b10000;
			}
			lastwrite = cpuclock;
		}	
		
	}
	
	@Override
	protected void setPRG(byte[] prg){
		PRGbanks = new byte[prg.length/0x4000][0x4000];
		for(int i=0;i*0x4000<prg.length;i++){
			PRGbanks[i]=Arrays.copyOfRange(prg, i*0x4000, (i*0x4000)+0x4000);
		}
		PRG_ROM[0]=PRGbanks[0];
		PRG_ROM[1]=PRGbanks[PRGbanks.length-1];
		if((prg.length/0x400)==512){
			bigrom = true;
			lastbank = 0xf;
		}
		else{
			lastbank = PRGbanks.length-1;
		}
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
		smallchrrom = CHR_ram || CHRbanks.length==2;
		
	}
	@Override
	byte cartridgeRead(int index){
		if(index<0x8000&&index>=0x6000)
			return PRG_RAM[index%0x2000];
		else if(index>=0x8000&&index<0xc000)
			return PRG_ROM[0][index%0x4000];
		else if(index>=0xc000)
			return PRG_ROM[1][index%0x4000];
		return 0;
	}
	public void writeRegister(int index){
		if(index>=0x8000&&index<=0x9fff){// Control register
			//System.out.println("Control reg write "+Integer.toBinaryString(shiftregister));
			Mirror_mode = shiftregister&0b11;
			switch(Mirror_mode){
			case 0:
				setNameTable(Mirror.SingleScreenLow);break;
			case 1:
				setNameTable(Mirror.SingleScreenHigh);break;
			case 2:
				setNameTable(Mirror.Vertical);break;
			case 3:
				setNameTable(Mirror.Horizontal);break;
			}
			PRG_ROM_mode = (shiftregister&0b1100)>>2;
			CHR_ROM_mode = (shiftregister&0b10000)>>4;
			updatePRGbanks();
		}
		else if(index>=0xa000&&index<=0xbfff){// CHR bank 0 select
			if(!CHR_ram)
				if(CHR_ROM_mode ==0){
					CHR_ROM[0]=CHRbanks[((shiftregister&0b11110)&(CHRbanks.length-1))];
					CHR_ROM[1]=CHRbanks[((shiftregister&0b11110)&(CHRbanks.length-1))+1];			
				}
				else
					CHR_ROM[0]=CHRbanks[shiftregister&(CHRbanks.length-1)];
			if(bigrom){
				prgswitch = ((shiftregister&0b10000)>0?0b10000:0);
				banknumber&=0xf;banknumber|=prgswitch;
				lastbank&=0xf;lastbank|=prgswitch;
				updatePRGbanks();
			}
		}
		else if(index>=0xc000&&index<=0xdfff){// CHR bank 1 select
			if(!CHR_ram)
				if(CHR_ROM_mode==1)
					CHR_ROM[1]=CHRbanks[shiftregister&(CHRbanks.length-1)];
			if(bigrom&&CHR_ROM_mode==1){
				prgswitch = ((shiftregister&0b10000)>0?0b10000:0);
				banknumber&=0xf;banknumber|=prgswitch;
				lastbank&=0xf;lastbank|=prgswitch;
				updatePRGbanks();
			}
		}
		else if(index>=0xe000&&index<=0xffff){
			banknumber = shiftregister&0xf;
			updatePRGbanks();
			
		}
		
	}
	private void updatePRGbanks(){
		switch(PRG_ROM_mode){
		case 0: case 1:
			//System.out.println("Switching prgrom case 0/1");
			PRG_ROM[0] = PRGbanks[(banknumber)&(lastbank)];
			PRG_ROM[1] = PRGbanks[((banknumber+1)&(lastbank))];
			break;
		case 2:
			//System.out.println("Switching prgrom case 2");
			PRG_ROM[0]= PRGbanks[0];
			PRG_ROM[1] = PRGbanks[(banknumber)&(lastbank)];
			break;
		case 3:
			//System.out.println("Switching prgrom case 3 "+prgswitch+" "+ ((shiftregister&0b1111)|prgswitch));
			PRG_ROM[0] = PRGbanks[(banknumber)&(lastbank)];
			PRG_ROM[1] = PRGbanks[lastbank];
			break;
			
		}
	}
	int prgswitch;
	int cpuclock;
	@Override
	public void runFrame() {
		while(!ppu.doneFrame){
			ppu.doCycle();
			ppu.doCycle();
			ppu.doCycle();
			cpu.run_cycle();
			cpuclock++;
			apu.doCycle();	
		}
		ppu.doneFrame=false;
	}
}
