package core.mappers;
import java.util.Arrays;

import core.*;
import core.NES;
import core.exceptions.UnSupportedMapperException;
//import ui.SystemUI;
public class Mapper implements java.io.Serializable {//There will be class that inherit this class. Better to have all reads and writes go through this
	private static final long serialVersionUID = 6655950169350506050L;
	public static enum Mirror{Horizontal,Vertical,SingleScreenLow,SingleScreenHigh};
	
	//for callback
	public transient NESCallback system;
	
	//System Components
	transient NES nes;
	public CPU_6502 cpu;
	public APU apu;
	public ppu2C02 ppu;
	public Controller control;
	public Controller control2;

	byte[] cpu_ram= new byte[0x800];
	byte[][] ppu_internal_ram= new byte[2][0x400];
	byte[][] nametables = new byte[4][0x400];
	public byte[] ppu_palette = new byte[]{0x09, 0x01, 0x00, 0x01, 0x00, 0x02, 0x02, 0x0D,
            0x08, 0x10, 0x08, 0x24, 0x00, 0x00, 0x04, 0x2C, 0x09, 0x01, 0x34,
            0x03, 0x00, 0x04, 0x00, 0x14, 0x08, 0x3A, 0x00, 0x02, 0x00, 0x20,
            0x2C, 0x08};
	public byte[] ppu_oam = new byte[256];	
	byte[] cpu_mmr = new byte[0x18];

	byte[][] PRG_ROM = new byte[2][0x4000];
	byte[][] PRGbanks;
	byte[] PRG_RAM=new byte[0x2000];
	byte[][] CHR_ROM = new byte[2][0x1000];
	byte[][] CHRbanks;
	boolean CHR_ram = false;
	
	boolean mirrormode;
	public boolean blockppu=true;
	
	public boolean olda12;
	public byte openbus;
	public boolean lastcpuwrite;
	public int lastwriteaddress;
	public int lastreadaddress;
	
	public Mapper(){
		//ppu_palette[0]=0xf;
		ppu = new ppu2C02(this);
		cpu = new CPU_6502(this);
		apu = new APU(this);
		control = new Controller(this,0);
		control2 = new Controller(this,1);
		

	}
	public void setcomponents(CPU_6502 c,ppu2C02 p,Controller cont,Controller cont2, APU a){
		cpu = c;
		ppu = p;
		apu = a;
		control = cont;
		control2 = cont2;
	}
	public void configureMapper(MapperSetting...settings){
		for(MapperSetting s:settings){
			switch(s.setting){
			case PRG_data: setPRG((byte[])s.value);break;
			case CHR_data: setCHR((byte[])s.value);break;
			case BatteryExists:setPRGRAM((boolean)s.value);break;
			case Mirroring: setMirror((int)s.value);break;
			default:
				System.err.println("Warning: Unsupported MapperSetting!");
			}
		}
	}
	public void setSystem(NESCallback system2){system = system2;}
	public void setNes(NES n){ nes = n;}
	protected void setMirror(int i){
		mirrormode= (i == 0);
		if(mirrormode)
			setNameTable(Mirror.Horizontal);
		else
			setNameTable(Mirror.Vertical);
		System.out.println("Mode set to:"+mirrormode);
	}
	protected final void setNameTable(final Mapper.Mirror mirroringType){
		switch(mirroringType){
		case Horizontal:
			nametables[0]=ppu_internal_ram[0];
			nametables[1]=ppu_internal_ram[0];
			nametables[2]=ppu_internal_ram[1];
			nametables[3]=ppu_internal_ram[1];break;
		case Vertical:
			nametables[0]=ppu_internal_ram[0];
			nametables[1]=ppu_internal_ram[1];
			nametables[2]=ppu_internal_ram[0];
			nametables[3]=ppu_internal_ram[1];break;
		case SingleScreenLow:
			nametables[0]=ppu_internal_ram[0];
			nametables[1]=ppu_internal_ram[0];
			nametables[2]=ppu_internal_ram[0];
			nametables[3]=ppu_internal_ram[0];break;
		case SingleScreenHigh:
			nametables[0]=ppu_internal_ram[1];
			nametables[1]=ppu_internal_ram[1];
			nametables[2]=ppu_internal_ram[1];
			nametables[3]=ppu_internal_ram[1];break;
		}
	}
	boolean blockppu(){
		return apu.cyclenum>14700;
	}
	public void cpuwrite(int index,byte b){
		if(index<0x2000){
			cpu_ram[index%0x800]=b;
		}
		else if(index>=0x2000&&index<0x4000)
			ppuregisterhandler(index%8,b,true);
		else if(index>=0x4000 && index<=0x4017){
			if(index==0x4014){
				 cpu_mmr[0x14]=b;
				 cpu.dxx=(0xff&b)<<8;
				cpu.writeDMA=true;
			}
			else if(index==0x4016)
				controllerWrite(index,b);
			else if(index>=0x4000&&index<=0x4013)
				apu.writeRegister(index, b);
			else if(index==0x4015||index==0x4017)
				apu.writeRegister(index, b);
			openbus=b;
		}
		else
			cartridgeWrite(index,b);
	}
	public void cpuwriteoam(int index,byte b){
		ppu_oam[index]=b;
	}
	public byte cpuread(int index){
		if(index<0x2000)
			return cpu_ram[index%0x800];
		else if(index>=0x2000 && index<0x4000)
			return ppuregisterhandler(index%8,(byte)0,false);
		else if(index>=0x4000 && index<=0x40ff){
			if(index ==0x4015){
				return apu.readRegisters(index);
			}
			if(index ==0x4016)
				return (byte) ((openbus&0b11100000)|control.getControllerStatus());
			else if(index==0x4017)
				return (byte) ((openbus&0b11100000)|control2.getControllerStatus());
			return openbus;
		}
		else
			return cartridgeRead(index);
	}
	public int cpureadu(int index){
		return (0xff&cpuread(index));
	}
	public byte controllerRead(int index){
		if(index ==0x4016)
			return control.getControllerStatus();
		else
			return control2.getControllerStatus();
	}
	public void controllerWrite(int index, byte b){
		control.inputRegister(b);
		control2.inputRegister(b);
	}
	//This method is reserved for general ppu writes through the data register
	public void ppuwrite(int index,byte b){
		if(index<0x2000&&CHR_ram){
			if(index<0x1000)
				CHR_ROM[0][index]=b;
			else
				CHR_ROM[1][index%0x1000]=b;
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
	}
	//This method is reserved for general ppu reads through the data register
	public byte ppuread(int index){
		if(index<0x2000)
			return CHR_ROM[(index&0x1000)!=0?1:0][index%0x1000];
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
	public byte ppureadNT(int index){
		index&=0xfff;
		return nametables[index/0x400][index%0x400];
	}
	public byte ppureadPT(int index){
		return CHR_ROM[(index&0x1000)!=0?1:0][index%0x1000];
	}
	public byte ppureadAT(int index){
		return ppureadNT(index);
	}
	public byte ppureadoam(int index){
		return ppu_oam[index%256];
	}
	public void ppuwriteoam(int index,byte b){
		ppu_oam[index]=b;
	}
	protected byte ppuregisterhandler(int index,byte x,boolean write){
		switch(index){
		case 0:
			if(write&&blockppu())
				ppu.writeRegisters(index, x);
			else if(write&&!blockppu())
				ppu.OPEN_BUS=x;
			else
				return ppu.OPEN_BUS;
			break;
		case 1:
			if(write&&blockppu())
				ppu.writeRegisters(index, x);
			else if(write&&!blockppu())
				ppu.OPEN_BUS=x;
			else
				return ppu.OPEN_BUS;
			break;
		case 2:
			if(write)
				ppu.OPEN_BUS=x;
			else
				return ppu.readRegister(index);
			break;
		case 3:
			if(write)
				ppu.writeRegisters(index, x);
			else
				return ppu.OPEN_BUS;
			break;
		case 4:
			if(write)
				ppu.writeRegisters(index, x);
			else
				return ppu.readRegister(index);
			break;
		case 5:
			if(write&&blockppu())
				ppu.writeRegisters(index, x);
			else if(write&&!blockppu())
				ppu.OPEN_BUS=x;
			else
				return ppu.OPEN_BUS;
			break;
		case 6:
			if(write&&blockppu())
				ppu.writeRegisters(index, x);
			else if(write&&!blockppu())
				ppu.OPEN_BUS=x;
			else
				return ppu.OPEN_BUS;
			break;
		case 7:
			if(write)
				ppu.writeRegisters(index, x);
			else
				return ppu.readRegister(index);
			break;
		}
		return 0;
	}
	void cartridgeWrite(int index,byte b){
		if(index<0x8000&&index>=0x6000)
			PRG_RAM[index-0x6000]=b;
	}
	byte cartridgeRead(int index){
		if(index<0x8000&&index>=0x6000)
			return PRG_RAM[index-0x6000];
		else if(index<0xc000)
			return PRG_ROM[0][index%0x4000];
		else
			return PRG_ROM[1][index%0x4000];
	}
	public void restoreSave(byte[] save){
		PRG_RAM = save;
	}
	public byte[] getSave(){
		return PRG_RAM;
	}
	protected void setPRG(byte[] prg){
		if(prg.length ==16384*2){
			PRG_ROM[0]=Arrays.copyOfRange(prg, 0,0x4000);
			PRG_ROM[1]=Arrays.copyOfRange(prg, 0x4000, 0x8000);
		}
		else{
			PRG_ROM[0]=Arrays.copyOfRange(prg, 0,0x4000);
			PRG_ROM[1]=Arrays.copyOfRange(prg, 0,0x4000);
		}
	}
	public void check(int i){}
	protected void setCHR(byte[] chr){
		if(chr.length==0){
			CHR_ROM = new byte[2][0x1000];
			CHR_ram = true;
		}
		else{
			CHR_ROM[0]=Arrays.copyOfRange(chr,0,0x1000);
			CHR_ROM[1]=Arrays.copyOfRange(chr, 0x1000, 0x2000);
		}
	}
	protected void setPRGRAM(boolean present){}
	//Debug functions
	public void printMemory(int offset,int length){
		System.out.print("[");
		for(int i = offset;i<offset+length-1;i++){
			System.out.print(Integer.toHexString((0xff&cpuread(i)))+", ");
		}
		System.out.println("]");
	}
	public void printMemoryPPU(int offset,int length){
		System.out.print("[");
		for(int i = offset;i<offset+length-1;i++){
			System.out.print(Integer.toHexString((0xff&ppuread(i)))+", ");
		}
		System.out.println("]");
	}
	public void printOAMPPU(int offset,int length){
		System.out.print("[");
		for(int i = offset;i<offset+length-1;i++){
			System.out.print(Integer.toHexString((0xff&ppureadoam(i)))+", ");
		}
		System.out.println("]");
	}
	boolean dodebug;
	//Scanner s = new Scanner(System.in);
	/*void debug(){
		//Scanner s = new Scanner(System.in);
		System.out.println("Timing: "
				+" PPU scanline:"+ppu.scanline
				+" VRAM ADDR: " +Integer.toHexString(ppu.v)
				+" Ticks: "+ ppu.pcycle//+"/"+(Integer.toHexString((int)c))
				+" Rendering?: "+ppu.dorender()
				+" vBlank:"+ppu.PPUSTATUS_vb);
				//+" PPUSTATUS:"+Integer.toBinaryString(ppu.PPUSTATUS));
		cpu.debug(0);
		String st = s.nextLine();
		if(st.equals("c"))
			dodebug=false;
		else
			dodebug=true;
	}*/
	
	public void scanlinecounter(){}
	
	public static Mapper getmapper(int i) throws UnSupportedMapperException{
		switch(i){
		case 0:
			return new NROM();
		case 1:
			return new MMC1();
		case 2:
			return new UxROM();
		case 3:
			return new CNROM();
		case 4:
			return new MMC3();
		case 5:
			return new MMC5();
		case 7:
			return new AxROM();
		case 9:
			return new MMC2();
		case 10:
			return new MMC4();
		case 11:
			return new Mapper_11();
		case 13:
			return new Mapper_13();
		case 19:
			return new Namco(163);
		case 24:
			return new VRC6(24);
		case 26:
			return new VRC6(26);
		case 66:
			return new Mapper_66();
		case 69:
			return new FME_7();
		case 71:
			return new Mapper_71();
		case 73:
			return new VRC3();
		case 75:
			return new VRC1();
		case 210:
			return new Namco(175);
		case 1001:
			return new NSFPlayer(0);
		case 1002:
			return new NSFPlayer(1);
		default:
			throw new UnSupportedMapperException(i);
		}
	}
	public void runFrame() {
		while(!ppu.doneFrame){
			ppu.doCycle();
			ppu.doCycle();
			ppu.doCycle();
			cpu.run_cycle();
			apu.doCycle();				
		}
		ppu.doneFrame=false;
	}
	public void runCPUCycle() {
		ppu.doCycle();
		ppu.doCycle();
		ppu.doCycle();
		cpu.run_cycle();
		apu.doCycle();
		
	}
	public void setInitialPC() {
		cpu.setPC(((cpureadu(0xfffd)<<8)|(cpureadu(0xfffc))));	
	}
	
}
