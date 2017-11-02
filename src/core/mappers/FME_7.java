package core.mappers;

import java.util.Arrays;

import core.CPU_6502.IRQSource;
import core.audio.Sunsoft5B;

public class FME_7 extends Mapper {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8144050394409729548L;
	byte[][] PRG_RAMbanks;
	boolean hasaudio;
	Sunsoft5B audio;
	public FME_7(){
		super();
		hasaudio = true;
		audio = new Sunsoft5B(apu.mixer.requestNewOutputLocation());
		apu.addExpansionChannel(audio);
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
		PRG_RAMbanks = new byte[1][0x2000];
		PRG_RAM = PRG_RAMbanks[0];
		
	}
	@Override
	protected void setCHR(byte[] chr){
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
	@Override
	final byte cartridgeRead(int index){
		if(index>=0x6000&&index<=0x7fff){
			if(rominramspot)
				return PRG_RAM[index%0x2000];
			else if(prgramenable)
				return PRG_RAM[index%0x2000];
			else
				return openbus;
		}
		else if(index>=0x8000&&index<=0x9fff)
			return PRG_ROM[0][index%0x2000];
		else if(index>=0xa000&&index<=0xbfff)
			return PRG_ROM[1][index%0x2000];
		else if(index>=0xc000&&index<=0xdfff)
			return PRG_ROM[2][index%0x2000];
		else if(index>=0xe000&&index<=0xffff)
			return PRG_ROM[3][index%0x2000];
		return 0;
	}
	int command;
	int audiocommand;
	boolean prgramenable;
	boolean rominramspot;
	
	@Override
	public final void cartridgeWrite(int index, byte b){
		openbus = b;
		if(index>=0x6000&&index<=0x7fff){
			if(!rominramspot&&prgramenable)
				PRG_RAM[index%0x2000] = b;
		}
		else if(index>=0x8000&&index<=0x9fff)
			command = b&0xf;
		else if(index>=0xa000&&index<=0xbfff){
			doCommand(b&0xff);
		}
		else if(index>=0xc000&&index<=0xdfff&&hasaudio){
			audiocommand = b&0xf;
		}
		else if(index>=0xe000&&index<=0xffff&&hasaudio){
			audio.registerWrite(audiocommand, b);
		}
	}
	private final void doCommand(int param){
		switch(command){
		case 0:case 1:case 2:case 3:
		case 4:case 5:case 6:case 7:
			CHR_ROM[command] = CHRbanks[param&(CHRbanks.length-1)];break;
		case 8:
			prgramenable = (param&0x80)!=0;
			rominramspot = (param&0x40)==0;
			if(rominramspot){
				PRG_RAM = PRGbanks[(param&0x3f)&(PRGbanks.length-1)];
			}
			else{
				PRG_RAM = PRG_RAMbanks[(param&0x3f)&(PRGbanks.length-1)];
			}
			break;
		case 9:
			PRG_ROM[0] = PRGbanks[(param&0x3f)&(PRGbanks.length-1)];break;
		case 0xa:
			PRG_ROM[1] = PRGbanks[(param&0x3f)&(PRGbanks.length-1)];break;
		case 0xb:
			PRG_ROM[2] = PRGbanks[(param&0x3f)&(PRGbanks.length-1)];break;
		case 0xc:
			switch(param&3){
			case 0: setNameTable(Mirror.Vertical);break;
			case 1: setNameTable(Mirror.Horizontal);break;
			case 2: setNameTable(Mirror.SingleScreenLow);break;
			case 3: setNameTable(Mirror.SingleScreenHigh);break;
			}break;
		case 0xd:
			cpu.removeIRQ(IRQSource.External);
			irqEnabled = (param&1)!=0;
			irqCounterEnabled = (param&0x80)!=0;break;
		case 0xe:
			irqCounter&=0xff00;
			irqCounter|=param;break;
		case 0xf:
			irqCounter&=0xff;
			irqCounter|=(param<<8);break;
		}
	}
	
	@Override
	public byte ppureadPT(int index){
		return CHR_ROM[index/0x400][index%0x400];
	}
	/*@Override
	public void ppuwrite(int index,byte b){
		if(index<0x2000&&CHR_ram){
			CHR_ROM[index/0x400][index%0x400] = b;
		}
		else if(index>=0x2000&&index<=0x3eff){
			index&=0xfff;
			nametables[index/0x400][index%0x400] = b;
		}
		else{
			int i = (index&0x1f);//%0x20;
			if(i%4==0)
				i+= i>=0x10?-0x10:0;
			ppu_palette[i]=b;
		}
	}*/
	@Override
	public byte ppuread(int index){
		if(index<0x2000)
			return CHR_ROM[index/0x400][index%0x400];
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
	boolean irqEnabled;
	boolean irqCounterEnabled;
	int irqCounter;
	private void clockirq(){
		if(irqCounterEnabled){
			if(irqCounter==0){
				irqCounter = 0xffff;
				if(irqEnabled)
					cpu.setIRQ(IRQSource.External);
			}
			else
				irqCounter--;
		}
	}
	@Override
	public void runFrame() {
		while(!ppu.doneFrame){
			ppu.doCycle();
			ppu.doCycle();
			ppu.doCycle();
			cpu.run_cycle();
			clockirq();
			apu.doCycle();	
		}
		ppu.doneFrame=false;
	}
}
