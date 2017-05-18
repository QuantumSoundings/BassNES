package mappers;

import java.util.Arrays;

import audio.VRC6Pulse;
import audio.VRC6Saw;

public class VRC6_24 extends Mapper {
	private static final long serialVersionUID = 478999998259356806L;
	private int[] Rvals = new int[8];
	private int ppubankmode;
	VRC6Pulse pulse1 = new VRC6Pulse();
	VRC6Pulse pulse2 = new VRC6Pulse();
	VRC6Saw saw = new VRC6Saw();
	
	public VRC6_24(){
		super();
		apu.addExpansionChannel(pulse1);
		apu.addExpansionChannel(pulse2);
		apu.addExpansionChannel(saw);
		System.out.println("Making a VRC6!");
	}
	@Override
	public void setPRG(byte[] prg){
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
	public void setCHR(byte[] chr){
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
		else if(index>=0x8000&&index<=0x8003){
			//System.out.println("16k select: "+Integer.toBinaryString(Byte.toUnsignedInt(b)));
			int bank = (b&0xf)<<1;
			PRG_ROM[0] = PRGbanks[bank&(PRGbanks.length-1)];
			PRG_ROM[1] = PRGbanks[(bank&(PRGbanks.length-1))+1];
		}
		else if(index>=0xc000&&index<=0xc003){
			//System.out.println("8k select: "+Integer.toBinaryString(Byte.toUnsignedInt(b)));
			int bank = b&0x1f;
			PRG_ROM[2] = PRGbanks[bank&(PRGbanks.length-1)];
		}
		else if(index==0xb003){
			ppubankmode = b&3;
			setCHR();
			//System.out.println("Nametable: "+ ((b>>2)&3));
			switch((b>>2)&3){
			case 0:
				nametables[0]=ppu_internal_ram[0];
				nametables[1]=ppu_internal_ram[1];
				nametables[2]=ppu_internal_ram[0];
				nametables[3]=ppu_internal_ram[1];break;
			case 1:
				nametables[0]=ppu_internal_ram[0];
				nametables[1]=ppu_internal_ram[0];
				nametables[2]=ppu_internal_ram[1];
				nametables[3]=ppu_internal_ram[1];break;
			case 2:
				nametables[0]=ppu_internal_ram[0];
				nametables[1]=ppu_internal_ram[0];
				nametables[2]=ppu_internal_ram[0];
				nametables[3]=ppu_internal_ram[0];break;
			case 3:
				nametables[0]=ppu_internal_ram[1];
				nametables[1]=ppu_internal_ram[1];
				nametables[2]=ppu_internal_ram[1];
				nametables[3]=ppu_internal_ram[1];break;
			}
		}
		else if(index>=0xd000&&index<=0xd003){
			//System.out.println("R"+(index&3)+" value: "+Byte.toUnsignedInt(b)+" scanline: "+ppu.scanline);
			Rvals[index&3] = Byte.toUnsignedInt(b);
			setCHR();
		}
		else if(index>=0xe000&&index<=0xe003){
			//System.out.println("R"+((index&3)+4)+" value: "+Byte.toUnsignedInt(b)+" scanline: "+ppu.scanline);

			Rvals[(index&3)+4] = Byte.toUnsignedInt(b);
			setCHR();
		}
		else if(index==0xf000)
			irqlatch = Byte.toUnsignedInt(b);
		else if(index==0xf001){
			//System.out.println("irq control write: "+ Integer.toBinaryString(Byte.toUnsignedInt(b)));
			//int i = Byte.toUnsignedInt(b);
			irqEnable = (b&0x2)>0;
			//if(irqEnable)
			//	System.out.println("IRQ IS LIVE!!!!!!!!!!!!!!!!");
			irqacknowledge = (b&1)==1;
			irqmode = (b&4)>>2;
			if(irqEnable){
				irqcounter = irqlatch;
				prescaler = 341;
			}
			if(doingIRQ){
				cpu.doIRQ--;
				doingIRQ=false;
			}
		}	
		else if(index==0xf002){
			if(doingIRQ){
				cpu.doIRQ--;
				doingIRQ=false;
			}
			irqEnable = irqacknowledge;
		}
		else{
			switch(index){
			case 0x9000:pulse1.registerWrite(0, b, 0);break;
			case 0xa000:pulse2.registerWrite(0, b, 0);break;
			case 0xb000:saw.registerWrite(0, b, 0);break;
			case 0x9001:pulse1.registerWrite(1, b, 0);break;
			case 0xa001:pulse2.registerWrite(1, b, 0);break;
			case 0xb001:saw.registerWrite(1, b, 0);break;
			case 0x9002:pulse1.registerWrite(2, b, 0);break;
			case 0xa002:pulse2.registerWrite(2, b, 0);break;
			case 0xb002:saw.registerWrite(2, b, 0);break;
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
			//System.out.println("IRQ CLOCK+++++++++++++++++++++++++");
			prescaler-=3;
			if(irqmode==1||(prescaler <=0&& irqmode==0)){
				if(irqcounter==0xff){
					if(!doingIRQ){
						//System.out.println("Generating IRQ scanline: "+ppu.scanline +" =======================================");
						cpu.doIRQ++;
					}
					doingIRQ=true;
					irqcounter=irqlatch;
				}
				else{
					//System.out.println("IRQ COUNTER: "+irqcounter );
					irqcounter++;
				}
				prescaler+=341;
			}
			/*if(irqmode == 0){
				prescaler-=3;
				if(prescaler<=0){
					prescaler += 341;
					
					if(irqcounter==0xff){
						if(!doingIRQ)
							cpu.doIRQ++;
						doingIRQ= true;
						irqcounter = irqlatch;
					}
					else
						irqcounter++;
				}
				
			}
			else{
				
				if(irqcounter==0xff){
					if(!doingIRQ)
						cpu.doIRQ++;
					doingIRQ= true;
					irqcounter = irqlatch;
				}
				else
					irqcounter++;
			}*/
		}
	}
	@Override
	public void runFrame(){
		while(!ppu.doneFrame){
			//if((cpu.program_counter==0xa45e)||dodebug||false)//0xe2c5||dodebug||false)
			//	debug();
			//if(map.ppu.scanline == 240)
			//	map.printMemoryPPU(0x3f00, 0x20);
			ppu.doCycle();
			ppu.doCycle();
			ppu.doCycle();
			cpu.run_cycle();
			//debug();
			clockIRQ();
			apu.doCycle();	
		}
	}
}
