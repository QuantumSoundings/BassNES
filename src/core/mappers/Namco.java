package core.mappers;

import java.util.Arrays;

import core.CPU_6502.IRQSource;
import core.audio.NamcoSound;

public class Namco extends Mapper{

	private static final long serialVersionUID = 6370867416895887534L;
	private int irqcounter;
	private boolean irqEnable;
	private boolean hasirq;
	private boolean irqstopped;
	private int mappertype;
	private boolean disablechrramlow;
	private boolean disablechrramhigh;
	private boolean soundEnable;
	private boolean soundAutoInc;
	private int soundAddress;
	private byte[] soundMemory = new byte[0x80];
	private NamcoSound soundChannel;
	private boolean PRGramenable;
	private boolean ramenable1;
	private boolean ramenable2;
	private boolean ramenable3;
	private boolean ramenable4;
	public Namco(int type){
		super();
		mappertype = type;
		System.out.println("Made a Namco "+mappertype+"!");
		if(type==163||type==129){
			hasirq=true;
			soundChannel = new NamcoSound(soundMemory);
			apu.addExpansionChannel(soundChannel);
		}
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
	@Override
	public final void cartridgeWrite(int index, byte b){
		if(index<0x4800);
		else if(index<=0x4fff){//sound data 129,163
			soundwrite(b);
		}
		else if(index<=0x57ff){//irqlow 129,163
			if((mappertype==129||mappertype==163)){
				irqcounter &= 0xff00;
				irqcounter |= b&0xff;
				if(irqstopped){
					cpu.removeIRQ(IRQSource.External);
					irqstopped=false;
				}
			}
		}
		else if(index<=0x5fff){//irqhigh 129,163
			if(mappertype==129||mappertype==163){
				irqcounter&=0xff;
				irqcounter|=(b&0x7f)<<8;
				irqEnable = (b&0x80)!=0;
				if(irqstopped){
					cpu.removeIRQ(IRQSource.External);
					irqstopped=false;
				}
			}
		}
		else if(index<=0x67ff){
			if(ramenable1)
				PRG_RAM[index%0x2000] = b;
		}
		else if(index<=0x6fff){
			if(ramenable2)
				PRG_RAM[index%0x2000] = b;
		}else if(index<=0x77ff){
			if(ramenable3)
				PRG_RAM[index%0x2000] = b;
		}else if(index<=0x7fff){
			if(ramenable4)
				PRG_RAM[index%0x2000] = b;
		}
		else if(index<=0xdfff){//chr and nt select
			int data = Byte.toUnsignedInt(b);
			if(data<=0xdf){
				switch(index/0x800){
				case 16:CHR_ROM[0] = CHRbanks[data&(CHRbanks.length-1)];break;
				case 17:CHR_ROM[1] = CHRbanks[data&(CHRbanks.length-1)];break;
				case 18:CHR_ROM[2] = CHRbanks[data&(CHRbanks.length-1)];break;
				case 19:CHR_ROM[3] = CHRbanks[data&(CHRbanks.length-1)];break;
				case 20:CHR_ROM[4] = CHRbanks[data&(CHRbanks.length-1)];break;
				case 21:CHR_ROM[5] = CHRbanks[data&(CHRbanks.length-1)];break;
				case 22:CHR_ROM[6] = CHRbanks[data&(CHRbanks.length-1)];break;
				case 23:CHR_ROM[7] = CHRbanks[data&(CHRbanks.length-1)];break;
				}
				if(mappertype==129||mappertype==163){
					switch(index/0x800){
					case 24:nametables[0] = CHRbanks[data&(CHRbanks.length-1)];break;
					case 25:nametables[0] = CHRbanks[data&(CHRbanks.length-1)];break;
					case 26:nametables[0] = CHRbanks[data&(CHRbanks.length-1)];break;
					case 27:nametables[0] = CHRbanks[data&(CHRbanks.length-1)];break;
					}
				}
			}
			else{
				if(mappertype==129||mappertype==163){
					switch(index/0x800){
					case 16:
						if(!disablechrramlow){
							if((data&1)==0)
								CHR_ROM[0] = ppu_internal_ram[0];
							else
								CHR_ROM[0] = ppu_internal_ram[1];
						}break;
					case 17:
						if(!disablechrramlow){
							if((data&1)==0)
								CHR_ROM[1] = ppu_internal_ram[0];
							else
								CHR_ROM[1] = ppu_internal_ram[1];
						}break;
					case 18:
						if(!disablechrramlow){
							if((data&1)==0)
								CHR_ROM[2] = ppu_internal_ram[0];
							else
								CHR_ROM[2] = ppu_internal_ram[1];
						}break;
					case 19:
						if(!disablechrramlow){
							if((data&1)==0)
								CHR_ROM[3] = ppu_internal_ram[0];
							else
								CHR_ROM[3] = ppu_internal_ram[1];
						}break;
					case 20:
						if(!disablechrramhigh){
							if((data&1)==0)
								CHR_ROM[4] = ppu_internal_ram[0];
							else
								CHR_ROM[4] = ppu_internal_ram[1];
						}break;
					case 21:
						if(!disablechrramhigh){
							if((data&1)==0)
								CHR_ROM[5] = ppu_internal_ram[0];
							else
								CHR_ROM[5] = ppu_internal_ram[1];
						}break;
					case 22:
						if(!disablechrramhigh){
							if((data&1)==0)
								CHR_ROM[6] = ppu_internal_ram[0];
							else
								CHR_ROM[6] = ppu_internal_ram[1];
						}break;
					case 23:
						if(!disablechrramhigh){
							if((data&1)==0)
								CHR_ROM[7] = ppu_internal_ram[0];
							else
								CHR_ROM[7] = ppu_internal_ram[1];
						}break; 
					}
					switch(index/0x800){
					case 24:nametables[0] = (data&1)==0?ppu_internal_ram[0]:ppu_internal_ram[1];break;
					case 25:nametables[1] = (data&1)==0?ppu_internal_ram[0]:ppu_internal_ram[1];break;
					case 26:nametables[2] = (data&1)==0?ppu_internal_ram[0]:ppu_internal_ram[1];break;
					case 27:nametables[3] = (data&1)==0?ppu_internal_ram[0]:ppu_internal_ram[1];break;
					}
				}
			}
			if(index>=0xc000&&index<=0xc7ff&&mappertype==175){//ext prg ram enable 175
				PRGramenable = (b&1)==1;
			}
		}
		else if(index<=0xe7ff){//prg select 1
			PRG_ROM[0] = PRGbanks[(b&0x3f)&(PRGbanks.length-1)];
			if(mappertype==129||mappertype==163){
				soundEnable = (b&0x40)!=0;
				if(!soundEnable){
					soundChannel.disable();
				}
			}
			else if(mappertype ==340){
				switch(Byte.toUnsignedInt(b)>>6){
				case 0:setNameTable(Mirror.SingleScreenLow);break;
				case 1:setNameTable(Mirror.Vertical);break;
				case 2:setNameTable(Mirror.Horizontal);break;
				case 3:setNameTable(Mirror.SingleScreenHigh);break;
				}
			}
		}
		else if(index<=0xefff){//prg select 2 chr-ram enable
			PRG_ROM[1] = PRGbanks[(b&0x3f)&(PRGbanks.length-1)];
			if(mappertype==129||mappertype==163){
				disablechrramlow = (b&0x40)!=0;
				disablechrramhigh= (b&0x80)!=0;
			}
		}
		else if(index<=0xf7ff){//prg select 3
			PRG_ROM[2] = PRGbanks[(b&0x3f)&(PRGbanks.length-1)];
		}
		else if(index<=0xffff){//write protect for ext ram and sound 129,163
			if(mappertype==129||mappertype==163){
				ramenable1 = ((b&0xf0)==0b01000000&&(b&1)==0);
				ramenable2 = ((b&0xf0)==0b01000000&&(b&2)==0);
				ramenable3 = ((b&0xf0)==0b01000000&&(b&4)==0);
				ramenable4 = ((b&0xf0)==0b01000000&&(b&8)==0);
				soundAddress = (b&0x7f);
				soundAutoInc = (b&0x80)!=0;
			}
		}
		
	}
	@Override
	final byte cartridgeRead(int index){
		if(index<0x4800);
		else if(index<=0x4fff){
			if(hasirq)
				return soundread();
		}
		else if(index<=0x57ff){
			if(hasirq)
				return (byte) (irqcounter&0xff);
		}
		else if(index<=0x5fff){
			if(hasirq)
				return (byte) ((irqcounter>>8)&0xff);
		}
		else if(index<0x8000){
			if(mappertype==175){
				if(PRGramenable)
					return PRG_RAM[index-0x6000];
			}
			else
				return PRG_RAM[index-0x6000];
		}
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
	@Override
	public byte ppureadPT(int index){
		return CHR_ROM[index/0x400][index%0x400];
	}
	private void soundwrite(byte b){
		if(soundAddress<0x40){
			soundMemory[soundAddress] = b;
		}
		else{
			soundChannel.registerWrite(soundAddress%8, b, (soundAddress/8)-8);
			soundMemory[soundAddress] = b;
			if(soundAddress==0x7f)
				soundChannel.setEnables((b&0x70)>>4);				
		}
		if(soundAutoInc){
			soundAddress++;
			if(soundAddress==0x80)
				soundAddress = 0;
		}
	}
	private byte soundread(){
		byte b = soundMemory[soundAddress];
		if(soundAutoInc){
			soundAddress++;
			if(soundAddress==0x80)
				soundAddress = 0;
		}
		return b;
	}
	private void clockirq(){
		if(irqEnable&&!irqstopped){
			irqcounter++;
			if(irqcounter==0x7fff){
				cpu.setIRQ(IRQSource.External);
				irqstopped=true;
			}
		}
	}
	@Override
	public void runFrame() {
		while(!ppu.doneFrame){
			ppu.doCycle();
			ppu.doCycle();
			ppu.doCycle();
			cpu.run_cycle();
			if(hasirq){
				clockirq();
			}
			apu.doCycle();	
		}
		ppu.doneFrame=false;
	}
}
