package core.mappers;

import java.util.Arrays;

import core.CPU_6502.IRQSource;
import core.audio.VRC6Pulse;
import core.audio.VRC6Saw;

public class VRC6 extends Mapper {
	private static final long serialVersionUID = 478999998259356806L;
	private int[] Rvals = new int[8];
	private int ppubankmode;
	VRC6Pulse pulse1;// = new VRC6Pulse(true);
	VRC6Pulse pulse2;// = new VRC6Pulse(false);
	VRC6Saw saw;// = new VRC6Saw();
	private final boolean maptype;
	public VRC6(int type){
		super();
		pulse1 = new VRC6Pulse(true,apu.requestNewOutputLocation());
		pulse2 = new VRC6Pulse(false,apu.requestNewOutputLocation());
		saw = new VRC6Saw(apu.requestNewOutputLocation());
		apu.addExpansionChannel(pulse1);
		apu.addExpansionChannel(pulse2);
		apu.addExpansionChannel(saw);
		System.out.println("Making a VRC6!");
		maptype = type==26;
	}
	@Override
	protected void setPRG(byte[] prg){
		PRG_ROM = new byte[4][0x2000];
		PRGbanks = new byte[prg.length/0x2000][0x2000];
		for(int i=0;i*0x2000<prg.length;i++){
			PRGbanks[i]=Arrays.copyOfRange(prg, i*0x2000, (i*0x2000)+0x2000);
		}
		PRG_ROM[0] = PRGbanks[0];
		PRG_ROM[1] = PRGbanks[1];
		PRG_ROM[2] = PRGbanks[2];
		PRG_ROM[3] = PRGbanks[PRGbanks.length-1];
	}
	@Override
	protected void setCHR(byte[] chr){
		CHR_ROM = new byte[8][0x400];
		if(chr.length>0){
			CHRbanks = new byte[chr.length/0x400][0x400];
			for(int i=0;i*0x400<chr.length;i++)
				CHRbanks[i]= Arrays.copyOfRange(chr, i*0x400, (i*0x400)+0x400);
		}
		else{
			CHR_ram = true;
		}
	}
	@Override
	public byte cartridgeRead(int index){
		if(index>=0x6000&&index<0x8000)
			return PRG_RAM[index%0x2000];
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
	@Override
	public void cartridgeWrite(int index,byte b){
		if(index>=0x6000&&index<=0x7fff)
			PRG_RAM[index%0x2000] = b;
		else{
			if(maptype)
				index&=0xf003;
			switch(index){
			case 0x8000:case 0x8001:case 0x8002:case 0x8003:
				int bank = (b&0xf)<<1;
				PRG_ROM[0] = PRGbanks[bank&(PRGbanks.length-1)];
				PRG_ROM[1] = PRGbanks[(bank&(PRGbanks.length-1))+1];
				break;
			case 0x9000:pulse1.registerWrite(0, b);break;
			case 0x9001:pulse1.registerWrite(1, b);break;
			case 0x9002:pulse1.registerWrite(2, b);break;
			case 0xa000:pulse2.registerWrite(0, b);break;
			case 0xa001:pulse2.registerWrite(1, b);break;
			case 0xa002:pulse2.registerWrite(2, b);break;
			case 0xb000:saw.registerWrite(0, b);break;
			case 0xb001:saw.registerWrite(1, b);break;
			case 0xb002:saw.registerWrite(2, b);break;
			case 0xb003:
				ppubankmode = b&3;
				setCHR();
				switch((b>>2)&3){
				case 0:
					setNameTable(Mirror.Vertical);break;
				case 1:
					setNameTable(Mirror.Horizontal);break;
				case 2:
					setNameTable(Mirror.SingleScreenLow);break;
				case 3:
					setNameTable(Mirror.SingleScreenHigh);break;
				}
				break;
			case 0xc000:case 0xc001:case 0xc002:case 0xc003:
				PRG_ROM[2] = PRGbanks[(b&0x1f)&(PRGbanks.length-1)];
				break;
			case 0xd000:case 0xd001:case 0xd002:case 0xd003:
				Rvals[index&3] = (0xff&b);
				setCHR();
				break;
			case 0xe000:case 0xe001:case 0xe002:case 0xe003:
				Rvals[(index&3)+4] = (0xff&b);
				setCHR();
				break;
			case 0xf000:irqlatch = (0xff&b);break;
			case 0xf001:
				irqEnable = (b&0x2)>0;
				irqacknowledge = (b&1)==1;
				irqmode = (b&4)>>2;
				if(irqEnable){
					irqcounter = irqlatch;
					prescaler = 341;
				}
				if(doingIRQ){
					cpu.removeIRQ(IRQSource.External);
					doingIRQ=false;
				}
				break;
			case 0xf002:
				if(doingIRQ){
					cpu.removeIRQ(IRQSource.External);
					doingIRQ=false;
				}
				irqEnable = irqacknowledge;
				break;
			}
		}
			
			
		
	}
	@Override
	public byte ppureadPT(int index){
		return CHR_ROM[index/0x400][index%0x400];
	}
	private void setCHR(){
		switch(ppubankmode){
		case 0:
			CHR_ROM[0] = CHRbanks[Rvals[0]&(CHRbanks.length-1)];
			CHR_ROM[1] = CHRbanks[Rvals[1]&(CHRbanks.length-1)];
			CHR_ROM[2] = CHRbanks[Rvals[2]&(CHRbanks.length-1)];
			CHR_ROM[3] = CHRbanks[Rvals[3]&(CHRbanks.length-1)];
			CHR_ROM[4] = CHRbanks[Rvals[4]&(CHRbanks.length-1)];
			CHR_ROM[5] = CHRbanks[Rvals[5]&(CHRbanks.length-1)];
			CHR_ROM[6] = CHRbanks[Rvals[6]&(CHRbanks.length-1)];
			CHR_ROM[7] = CHRbanks[Rvals[7]&(CHRbanks.length-1)];break;
		case 1:
			CHR_ROM[0] = CHRbanks[Rvals[0]&(CHRbanks.length-1)];
			CHR_ROM[1] = CHRbanks[Rvals[0]&(CHRbanks.length-1)];
			CHR_ROM[2] = CHRbanks[Rvals[1]&(CHRbanks.length-1)];
			CHR_ROM[3] = CHRbanks[Rvals[1]&(CHRbanks.length-1)];
			CHR_ROM[4] = CHRbanks[Rvals[2]&(CHRbanks.length-1)];
			CHR_ROM[5] = CHRbanks[Rvals[2]&(CHRbanks.length-1)];
			CHR_ROM[6] = CHRbanks[Rvals[3]&(CHRbanks.length-1)];
			CHR_ROM[7] = CHRbanks[Rvals[3]&(CHRbanks.length-1)];break;
		case 2:case 3:
			CHR_ROM[0] = CHRbanks[Rvals[0]&(CHRbanks.length-1)];
			CHR_ROM[1] = CHRbanks[Rvals[1]&(CHRbanks.length-1)];
			CHR_ROM[2] = CHRbanks[Rvals[2]&(CHRbanks.length-1)];
			CHR_ROM[3] = CHRbanks[Rvals[3]&(CHRbanks.length-1)];
			CHR_ROM[4] = CHRbanks[Rvals[4]&(CHRbanks.length-1)];
			CHR_ROM[5] = CHRbanks[Rvals[4]&(CHRbanks.length-1)];
			CHR_ROM[6] = CHRbanks[Rvals[5]&(CHRbanks.length-1)];
			CHR_ROM[7] = CHRbanks[Rvals[5]&(CHRbanks.length-1)];break;
		}
	}
	// VRC IRQ MODULE
	private int irqlatch;
	private int irqmode=0;
	private boolean irqacknowledge;
	private boolean irqEnable=false;
	private int prescaler=341;
	private boolean doingIRQ;
	private int irqcounter;
	private void clockIRQ(){
		if(irqEnable){
			prescaler-=3;
			if(irqmode==1||(prescaler <=0&& irqmode==0)){
				if(irqcounter==0xff){
					if(!doingIRQ){
						cpu.setIRQ(IRQSource.External);
					}
					doingIRQ=true;
					irqcounter=irqlatch;
				}
				else{
					irqcounter++;
				}
				prescaler+=341;
			}
		}
	}
	@Override
	public void runFrame(){
		while(!ppu.doneFrame){
			ppu.doCycle();
			ppu.doCycle();
			ppu.doCycle();
			cpu.run_cycle();
			clockIRQ();
			apu.doCycle();	
		}
		ppu.doneFrame=false;
	}
}
