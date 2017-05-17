package mappers;

import java.util.Arrays;

public class MMC5 extends Mapper {
	private static final long serialVersionUID = -3309464442109236184L;
	private int PRG_mode;
	private int CHR_mode;
	private int PRG_ram_protect_1;//must be 0x2 to enable writes
	private int PRG_ram_protect_2;//must be 0x1 to enable writes
	private int EXT_ram_mode;//Extended ram mode;
	private byte[] EXT_ram = new byte[0x400];
	private byte[][] nametables = new byte[4][0x400];
	private int[] nametable_assignments = new int[4];
	private byte[][] ppu_internal_ram = new byte[2][0x400];
	private final byte[] all_zero = new byte[0x400];
	private byte[] fill_mode = new byte[0x400];
	private int fill_mode_tile;
	private int fill_mode_color;
	private int[] chrbanksa = new int[8];
	private int[] chrbanksb = new int[4];
	private byte[][] CHR_ROMB = new byte[4][0x400];
	private byte[][] PRG_RAM_banks = new byte[8][0x2000];
	private byte[][] PRG_MAP = new byte[4][0x2000];
	private boolean[] PRG_MAP_writable = new boolean[4]; 
	public MMC5(){
		super();
		System.out.println("Making an MMC5!");
		EXT_ram_mode = 2;
	}
	@Override
	public void setPRG(byte[] prg){
		PRGbanks = new byte[prg.length/0x2000][0x2000];
		for(int i=0;i*0x2000<prg.length;i++){
			PRGbanks[i]=Arrays.copyOfRange(prg, i*0x2000, (i*0x2000)+0x2000);
		}
		PRG_MAP[0]=PRGbanks[0];
		PRG_MAP[1]=PRGbanks[1];
		PRG_MAP[2]=PRGbanks[0];
		PRG_MAP[3]=PRGbanks[PRGbanks.length-1];
		PRG_RAM = PRG_RAM_banks[0];
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
	void cartridgeWrite(int index,byte b){
		if(index>=0x5100&&index<=0x5107)
			configuration(index,b);
		else if(index>=0x5113&&index<=0x5116)
			prgBankSwitching(index,b);
		else if(index>=0x5120&&index<=0x5130)
			chrBankSwitching(index,b);
		else if(index>=0x5200&&index<=0x5206)
			otherRegisters(index,b);
		else if(index>=0x5c00&&index<=0x5fff){
			switch(EXT_ram_mode){
			case 0: case 1:
				if(ppu.dorender()&&ppu.scanline<240)
					EXT_ram[index%0x400] = b; 
				break;
			case 2:
				EXT_ram[index%0x400] = b;
				break;
			default: break;
			}
		}
		else if(index>=0x6000&&index<=0x7fff)
			PRG_RAM[index%0x2000] = b;
		else if(index>=0x8000&&index<=0x9fff&&PRG_MAP_writable[0])
			PRG_MAP[0][index%0x2000] = b;
		else if(index>=0xa000&&index<=0xbfff&&PRG_MAP_writable[1])
			PRG_MAP[1][index%0x2000] = b;
		else if(index>=0xc000&&index<=0xdfff&&PRG_MAP_writable[2])
			PRG_MAP[2][index%0x2000] = b;
			
	}
	@Override
	byte cartridgeRead(int index){
		switch(index){
		case 0x5204:
			byte b = (byte) (irqpending?0x80:0);
			irqpending = false;
			if(doingIRQ){
				cpu.doIRQ--;
				doingIRQ=false;
			}
			b|= (ppu.dorender()&&ppu.scanline<240)?0x40:0;
			return b;
		case 0x5205:break;
		case 0x5206:break;
		}
		if(index>=0x5c00&&index<=0x5fff){
			switch(EXT_ram_mode){
			case 0: case 1:
				return openbus;
			case 2: case 3:
				return EXT_ram[index%0x400];
			}
		}
		else if(index>=0x6000&&index<=0x7fff)
			return PRG_RAM[index%0x2000];
		else if(index>=0x8000&&index<=0x9fff)
			return PRG_MAP[0][index%0x2000];
		else if(index>=0xa000&&index<=0xbfff)
			return PRG_MAP[1][index%0x2000];
		else if(index>=0xc000&&index<=0xdfff)
			return PRG_MAP[2][index%0x2000];
		else if(index>=0xe000&&index<=0xffff)
			return PRG_MAP[3][index%0x2000];	
		return 0;	
	}
	private void configuration(int index,byte b){
		switch(index){
		case 0x5100:
			System.out.println("PRG MODE: "+Integer.toBinaryString(b&3));
			PRG_mode = b&3;break;
		case 0x5101:
			System.out.println("CHR MODE: "+Integer.toBinaryString(b&3));
			CHR_mode = b&3;break;
		case 0x5102:
			PRG_ram_protect_1 = b&3;break;
		case 0x5103:
			PRG_ram_protect_2 = b&3;break;
		case 0x5104:
			System.out.println("EXTRAM MODE: "+Integer.toBinaryString(Byte.toUnsignedInt(b)));
			EXT_ram_mode = b&3;break;
		case 0x5105:
			//System.out.println("NameTable Assignments: "+Integer.toBinaryString(Byte.toUnsignedInt(b)));
			int pos = Byte.toUnsignedInt(b);
			for(int i = 0; i<4;i++){
				//pos= pos>>(i*2);
				switch(pos&3){
				case 0:
					nametables[i] = ppu_internal_ram[0];nametable_assignments[i] = 0;break;
				case 1:
					nametables[i] = ppu_internal_ram[1];nametable_assignments[i] = 1;break;
				case 2:
					if(EXT_ram_mode == 0 || EXT_ram_mode ==1)
						nametables[i] = EXT_ram;
					else
						nametables[i] = all_zero;
					nametable_assignments[i] = 2;break;
				case 3:
					nametables[i] = fill_mode;
					nametable_assignments[i] = 3;break;
				}
				pos>>=2;
			}
			break;
		case 0x5106:
			fill_mode_tile = Byte.toUnsignedInt(b);break;
		case 0x5107:
			fill_mode_color = b&3;break;
		}
			
	}
	private void prgBankSwitching(int index,byte b){
		if(index==0x5113)//PRG_Ram banking
			PRG_RAM = PRG_RAM_banks[((b&4)*4)+(b&3)];
		else if(index==0x5114){
			if(PRG_mode==3){
				if(b>0){
					PRG_MAP[0] = PRG_RAM_banks[((b&4)*4)+(b&3)];
					PRG_MAP_writable[0]=true;
				}
				else{
					PRG_MAP[0] = PRGbanks[(b&0b1111111)&(PRGbanks.length-1)];
					PRG_MAP_writable[0]=false;
				}
			}
		}
		else if(index==0x5115){
			//System.out.println("5115: "+Integer.toHexString(Byte.toUnsignedInt(b)));
			switch(PRG_mode){
			case 1: case 2:
				if(b>0){
					PRG_MAP[0] = PRG_RAM_banks[((b&4)*4)+(b&2)];
					PRG_MAP[1] = PRG_RAM_banks[((b&4)*4)+(b&2)+1];
					PRG_MAP_writable[0]=PRG_MAP_writable[1]=true;
				}
				else{
					PRG_MAP[0] = PRGbanks[(b&0b1111110)&(PRGbanks.length-1)];
					PRG_MAP[1] = PRGbanks[((b&0b1111110)&(PRGbanks.length-1))+1];
					PRG_MAP_writable[0]=PRG_MAP_writable[1]=false;
				}
				break;
			case 3:
				if(b>0){
					PRG_MAP[1] = PRG_RAM_banks[((b&4)*4)+(b&3)];
					PRG_MAP_writable[1]=true;
				}
				else{
					PRG_MAP[1] = PRGbanks[(b&0b1111111)&(PRGbanks.length-1)];
					PRG_MAP_writable[1]=false;
				}
				break;
			default: break;
			}
		}
		else if(index==0x5116){
			switch(PRG_mode){
			case 2: case 3:
				//System.out.println("5116: "+Integer.toHexString(Byte.toUnsignedInt(b)));
				if(b>0){
					PRG_MAP[2] = PRG_RAM_banks[((b&4)*4)+(b&3)];
					PRG_MAP_writable[2]=true;
				}
				else{
					PRG_MAP[2] = PRGbanks[(b&0b1111111)&(PRGbanks.length-1)];
					PRG_MAP_writable[2]=false;
				}
				break;
			default: break;
			}
		}
		else if(index==0x5117){
			b&=0b1111111;
			//System.out.println("Am i here?------------------------");
			switch(PRG_mode){
			case 0:
				PRG_MAP[0] = PRGbanks[((b&0b1111100)&(PRGbanks.length-1))+0];
				PRG_MAP_writable[0]=false;
				PRG_MAP[1] = PRGbanks[((b&0b1111100)&(PRGbanks.length-1))+1];
				PRG_MAP_writable[1]=false;
				PRG_MAP[2] = PRGbanks[((b&0b1111100)&(PRGbanks.length-1))+2];
				PRG_MAP_writable[2]=false;
				PRG_MAP[3] = PRGbanks[((b&0b1111100)&(PRGbanks.length-1))+3];
				PRG_MAP_writable[3]=false;
				break;
			case 1:
				PRG_MAP[2] = PRGbanks[((b&0b1111110)&(PRGbanks.length-1))+0];
				PRG_MAP_writable[2]=false;
				PRG_MAP[3] = PRGbanks[((b&0b1111110)&(PRGbanks.length-1))+1];
				PRG_MAP_writable[3]=false;
				break;
			case 2: case 3:
				PRG_MAP[3] = PRGbanks[b&(PRGbanks.length-1)];
				PRG_MAP_writable[3]=false;
			}
		}
		
		
	}
	private boolean lastbanksprite;
	private void chrBankSwitching(int index,byte b){//TODO - figure this shit out.
		int val = Byte.toUnsignedInt(b);
		//System.out.println(Integer.toHexString(index)+": "+Integer.toHexString(Byte.toUnsignedInt(b)));
		switch(index){
		case 0x5120:
			chrbanksa[0] = val;setupCHR();lastbanksprite = true;break;
		case 0x5121:
			chrbanksa[1] = val;setupCHR();lastbanksprite = true;break;
		case 0x5122:
			chrbanksa[2] = val;setupCHR();lastbanksprite = true;break;
		case 0x5123:
			chrbanksa[3] = val;setupCHR();lastbanksprite = true;break;
		case 0x5124:
			chrbanksa[4] = val;setupCHR();lastbanksprite = true;break;
		case 0x5125:
			chrbanksa[5] = val;setupCHR();lastbanksprite = true;break;
		case 0x5126:
			chrbanksa[6] = val;setupCHR();lastbanksprite = true;break;
		case 0x5127:
			chrbanksa[7] = val;setupCHR();lastbanksprite = true;break;
		case 0x5128:
			//System.out.println("BG 0: "+val+" "+CHRbanks.length);
			chrbanksb[0] = val;setupCHR();lastbanksprite = false;break;
		case 0x5129:
			//System.out.println("BG 1: "+val);

			chrbanksb[1] = val;setupCHR();lastbanksprite = false;break;
		case 0x512a:
			//System.out.println("BG 2: "+val);

			chrbanksb[2] = val;setupCHR();lastbanksprite = false;break;
		case 0x512b:
			//System.out.println("BG 3: "+val);

			chrbanksb[3] = val;setupCHR();lastbanksprite = false;break;

		default: break;
		}
	}
	private void setupCHR(){
		switch(CHR_mode){
		case 0:
			CHR_ROM[0] = CHRbanks[chrbanksa[7]&(CHRbanks.length-1)];
			CHR_ROM[1] = CHRbanks[(chrbanksa[7]+1)&(CHRbanks.length-1)];
			CHR_ROM[2] = CHRbanks[(chrbanksa[7]+2)&(CHRbanks.length-1)];
			CHR_ROM[3] = CHRbanks[(chrbanksa[7]+3)&(CHRbanks.length-1)];
			CHR_ROM[4] = CHRbanks[(chrbanksa[7]+4)&(CHRbanks.length-1)];
			CHR_ROM[5] = CHRbanks[(chrbanksa[7]+5)&(CHRbanks.length-1)];
			CHR_ROM[6] = CHRbanks[(chrbanksa[7]+6)&(CHRbanks.length-1)];
			CHR_ROM[7] = CHRbanks[(chrbanksa[7]+7)&(CHRbanks.length-1)];
			CHR_ROMB[0]= CHRbanks[(chrbanksb[3]<<3)&(CHRbanks.length-1)];
			CHR_ROMB[1]= CHRbanks[((chrbanksb[3]<<3)+1)&(CHRbanks.length-1)];
			CHR_ROMB[2]= CHRbanks[((chrbanksb[3]<<3)+2)&(CHRbanks.length-1)];
			CHR_ROMB[3]= CHRbanks[((chrbanksb[3]<<3)+3)&(CHRbanks.length-1)];
			break;
			
		case 3:
			CHR_ROM[0] = CHRbanks[chrbanksa[0]&(CHRbanks.length-1)];
			CHR_ROM[1] = CHRbanks[chrbanksa[1]&(CHRbanks.length-1)];
			CHR_ROM[2] = CHRbanks[chrbanksa[2]&(CHRbanks.length-1)];
			CHR_ROM[3] = CHRbanks[chrbanksa[3]&(CHRbanks.length-1)];
			CHR_ROM[4] = CHRbanks[chrbanksa[4]&(CHRbanks.length-1)];
			CHR_ROM[5] = CHRbanks[chrbanksa[5]&(CHRbanks.length-1)];
			CHR_ROM[6] = CHRbanks[chrbanksa[6]&(CHRbanks.length-1)];
			CHR_ROM[7] = CHRbanks[chrbanksa[7]&(CHRbanks.length-1)];
			CHR_ROMB[0] = CHRbanks[chrbanksb[0]&(CHRbanks.length-1)];
			CHR_ROMB[1] = CHRbanks[chrbanksb[1]&(CHRbanks.length-1)];
			CHR_ROMB[2] = CHRbanks[chrbanksb[2]&(CHRbanks.length-1)];
			CHR_ROMB[3] = CHRbanks[chrbanksb[3]&(CHRbanks.length-1)];
			break;
			default: break;
		}
	}
	private int irqscanline;
	private int irqcounter;
	private boolean doingIRQ;
	private boolean inFrame;
	private boolean irqEnable;
	private boolean irqpending;
	private void otherRegisters(int index,byte b){
		switch(index){
		case 0x5200:System.out.println("Vertical Split Write: "+Integer.toBinaryString(Byte.toUnsignedInt(b)));break;
		case 0x5201:System.out.println("VerticalS Scroll Write");break;
		case 0x5202:System.out.println("VSplit Bank Write");break;
		case 0x5203:irqscanline = Byte.toUnsignedInt(b);break;
		case 0x5204:irqEnable = b<0;break;
		case 0x5205:System.out.println("write to multiply");break;
		case 0x5206:System.out.println("write to multiply");break;
			default: break;
		}
	}
	private void clockirq(){
		if(!inFrame){
			inFrame=true;
			irqcounter=0;
			irqpending = false;
			if(doingIRQ){
				cpu.doIRQ--;
				doingIRQ=false;
			}
		}
		else{
			irqcounter++;
			if(irqcounter==irqscanline){
				irqpending = true;
				if(irqEnable&&!doingIRQ){
					cpu.doIRQ++;
					doingIRQ=true;
				}
			}
		}
		if(ppu.scanline==239)
			inFrame = false;
	}
	@Override
	public final byte ppureadNT(int index){
		if(ppu.pcycle==339)
			clockirq();
		index%=0x1000;
		return nametables[index/0x400][index%0x400];
	}
	@Override
	public final byte ppureadAT(int index){
		if(EXT_ram_mode==1)
			return EXT_ram[index%0x400];
		return ppureadNT(index);
	}
	@Override
	public void ppuwrite(int index,byte b){
		if(index<0x2000){
			if(lastbanksprite){
				CHR_ROM[index/0x400][index%0x400] = b;
			}
			else{
				index=index%0x1000;
				CHR_ROMB[index/0x400][index%0x400] = b;
			}
		}	
		else if(index>=0x2000&&index<=0x3eff){
			int i = index&0xfff;
			nametables[i/0x400][index%0x400] = b;
			/*int x = nametable_assignments[i/0x400];
			switch(x){
			case 0:
				internal_ram[0][i%0x400] = b;break;
			case 1:
				//System.out.println("IRAM 1 write");
				internal_ram[1][i%0x400] = b;break;
			case 2:
				EXT_ram[i%0x400] = b;break;
			}*/
		}
		else if(index>=0x3f00&&index<=0x3fff){
			int i = (index&0x1f);//%0x20;
			if(i%4==0)
				i+= i>=0x10?-0x10:0;
			ppu_palette[i]=b;
		}
	}
	@Override
	public byte ppuread(int index){
		if(index<0x2000){
			if(lastbanksprite)
				return CHR_ROM[index/0x400][index%0x400];
			else{
				index%=0x1000;
				return CHR_ROMB[index/0x400][index%0x400];
			}
		}
		else if(index>=0x2000&&index<=0x3eff){
			int i = index&0xfff;
			return nametables[i/0x400][i%0x400];
			/*int x = nametable_assignments[i/0x400];
			switch(x){
			case 0:
				return internal_ram[0][i%0x400];
			case 1:
				return internal_ram[1][i%0x400];
			case 2:
				return EXT_ram[i%0x400];
			}*/
		}
		else if(index>=0x3f00&&index<=0x3fff){
			index = index&0x1f;
			index-= (index>=0x10&&(index&3)==0)?0x10:0;
			return ppu_palette[index];
		}
		return 0;
	}
	@Override
	public byte ppureadPT(int index){
		if(ppu.getSpriteSize()){
			if(ppu.spritefetch)
				return CHR_ROM[index/0x400][index%0x400];
			else{
				index%=0x1000;
				return CHR_ROMB[index/0x400][index%0x400];
			}
		}
		return CHR_ROM[index/0x400][index%0x400];
	}

}
