package core.mappers;

import java.util.Arrays;

import core.CPU_6502.IRQSource;

public class VRC3 extends Mapper {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5509863799463095751L;
	public VRC3(){
		super();
		System.out.println("Making a VRC3!");
	}
	@Override
	public void setPRG(byte[] prg){
		PRG_ROM = new byte[2][0x4000];
		PRGbanks = new byte[prg.length/0x4000][0x4000];
		for(int i=0;i*0x4000<prg.length;i++){
			PRGbanks[i]=Arrays.copyOfRange(prg, i*0x4000, (i*0x4000)+0x4000);
		}
		PRG_ROM[0] = PRGbanks[0];
		PRG_ROM[1] = PRGbanks[PRGbanks.length-1];
	}
	@Override
	void cartridgeWrite(int index,byte b){
		if(index<0x6000);
		else if(index<=0x7fff){
			PRG_RAM[index%0x2000] = b;
		}
		else if(index<=0x8fff){
			irqlatch&=0xfff0;
			irqlatch|=(b&0xf);
		}
		else if(index<=0x9fff){
			irqlatch&=0xff0f;
			irqlatch|=(b&0xf)<<4;
		}
		else if(index<=0xafff){
			irqlatch&=0xf0ff;
			irqlatch|=(b&0xf)<<8;
		}
		else if(index<=0xbfff){
			irqlatch&=0xfff;
			irqlatch|=(b&0xf)<<12;
		}
		else if(index<=0xcfff){
			irqEnable = (b&0x2)>0;
			irqacknowledge = (b&1)==1;
			irqmode = (b&4)>>2;
			if(irqEnable){
				irqcounter = irqlatch;
			}
			if(doingIRQ){
				cpu.removeIRQ(IRQSource.External);
				doingIRQ=false;
			}
		}
		else if(index<=0xdfff){
			if(doingIRQ){
				cpu.removeIRQ(IRQSource.External);
				doingIRQ=false;
			}
			irqEnable = irqacknowledge;
		}
		else if(index>=0xf000&&index<=0xffff){
			PRG_ROM[0] = PRGbanks[(b&7)&(PRGbanks.length-1)];
		}
	}
	// VRC IRQ MODULE
		private int irqlatch;
		private int irqmode=0;
		private boolean irqacknowledge;
		private boolean irqEnable=false;
		private boolean doingIRQ;
		private int irqcounter;
		private void clockIRQ(){
			if(irqEnable){
				if(irqmode==0){
					irqcounter++;
					if(irqcounter>0xffff){
						if(!doingIRQ)
							cpu.setIRQ(IRQSource.External);
						doingIRQ=true;
						irqcounter=irqlatch;
					}
				}
				else{
					if((irqcounter&0xff)==0xff){
						if(!doingIRQ)
							cpu.setIRQ(IRQSource.External);
						doingIRQ=true;
						irqcounter=irqlatch;
					}
					else
						irqcounter++;
				}
				
			}
		}
		public void runFrame() {
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
