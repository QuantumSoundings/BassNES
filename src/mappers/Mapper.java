package mappers;
import java.util.Arrays;
import java.util.Scanner;

import com.APU;
import com.CPU_6502;
import com.Controller;
import com.NES;
import com.ppu2C02;

import ui.SystemUI;
public class Mapper implements java.io.Serializable {//There will be class that inherit this class. Better to have all reads and writes go through this
	private static final long serialVersionUID = 6655950169350506050L;
	//for callback
	public transient SystemUI system;
	
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
	public byte[] ppu_palette = new byte[]{63,63,63,63,63,63,63,63,63,63,63,63,63,63,63,63,63,63,63,63,63,63,63,63,63,63,63,63,63,63,63,63};
	public byte[] ppu_oam = new byte[256];	
	byte[] cpu_mmr = new byte[0x18];

	byte[][] PRG_ROM = new byte[2][0x4000];
	byte[][] PRGbanks;
	//boolean PRG_32k;
	byte[] PRG_RAM=new byte[0x2000];
	byte[][] CHR_ROM = new byte[2][0x1000];
	byte[][] CHRbanks;
	boolean CHR_ram = false;
	
	boolean mirrormode;
	public boolean blockppu=true;
	
	public boolean olda12;
	public byte openbus;
	
	public Mapper(){
		ppu_palette[0]=0xf;
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
	public void setSystem(SystemUI sys){system = sys;}
	public void setNes(NES n){ nes = n;}
	public void setMirror(int i){
		mirrormode= (i == 0);
		if(mirrormode){
			nametables[0]=ppu_internal_ram[0];
			nametables[1]=ppu_internal_ram[0];
			nametables[2]=ppu_internal_ram[1];
			nametables[3]=ppu_internal_ram[1];
		}
		else{
			nametables[0]=ppu_internal_ram[0];
			nametables[1]=ppu_internal_ram[1];
			nametables[2]=ppu_internal_ram[0];
			nametables[3]=ppu_internal_ram[1];
		}
		System.out.println("Mode set to:"+mirrormode);
	}
	boolean blockppu(){
		return apu.cyclenum>14700;
	}
	public void cpuwrite(int index,byte b){
		if(index<0x2000){
			cpu_ram[index%0x800]=b;
		}
		else if(index>=0x2000&&index<0x4000)
			ppuregisterhandler((index%8)+0x2000,b,true);
		else if(index>=0x4000 && index<=0x4017){
			if(index==0x4014){
				 cpu_mmr[0x14]=b;
				 cpu.dxx=Byte.toUnsignedInt(b)<<8;
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
			return ppuregisterhandler((index%8)+0x2000,(byte)0,false);
		else if(index>=0x4000 && index<=0x40ff){
			//if(index ==0x4014)
			//	return cpu_mmr[0x14];
			if(index ==0x4015){
				return apu.readRegisters(index);
			}
			if(index ==0x4016||index==0x4017)
				return (byte) ((openbus&0b11100000)|controllerRead(index));
			return openbus;
		}
		else
			return cartridgeRead(index);
	}
	public int cpureadu(int index){
		return Byte.toUnsignedInt(cpuread(index));
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
	public void ppuwrite(int index,byte b){
		if(index<0x2000&&CHR_ram){
			//System.out.println("Wwriting to char ram");
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
	public byte ppuread(int index){
		if(index<0x2000)
			if(index<0x1000)
				return CHR_ROM[0][index];
			else
				return CHR_ROM[1][index%0x1000];
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
	public byte ppureadoam(int index){
		return ppu_oam[index%256];
	}
	public void ppuwriteoam(int index,byte b){
		ppu_oam[index]=b;
	}
	byte ppuregisterhandler(int index,byte x,boolean write){
		if(index ==0x2000){//PPUCTRL
			if(write&&blockppu())
				ppu.writeRegisters(index, x);
			else if(write&&!blockppu())
				ppu.OPEN_BUS=x;
			else
				return ppu.OPEN_BUS;
		}
		else if(index ==0x2001){//PPUMASK
			if(write&&blockppu())
				ppu.writeRegisters(index, x);
			else if(write&&!blockppu())
				ppu.OPEN_BUS=x;
			else
				return ppu.OPEN_BUS;
		}
		else if(index ==0x2002){//PPUSTATUS
			if(write)
				ppu.OPEN_BUS=x;
			else
				return ppu.readRegister(index);
			
		}
		else if(index ==0x2003){//OAMADDR
			if(write)
				ppu.writeRegisters(index, x);
			else
				return ppu.OPEN_BUS;
		}
		else if(index ==0x2004){//needs work for full accuracy OAMDATA
			if(write)
				ppu.writeRegisters(index, x);
			else
				return ppu.readRegister(index);
		}
		else if(index ==0x2005){//PPUSCRL
			if(write&&blockppu())
				ppu.writeRegisters(index, x);
			else if(write&&!blockppu())
				ppu.OPEN_BUS=x;
			else
				return ppu.OPEN_BUS;
		}
		else if(index ==0x2006){//PPUADDR
			if(write&&blockppu())
				ppu.writeRegisters(index, x);
			else if(write&&!blockppu())
				ppu.OPEN_BUS=x;
			else
				return ppu.OPEN_BUS;
		}
		else if(index ==0x2007){
			if(write)
				ppu.writeRegisters(index, x);
			else
				return ppu.readRegister(index);
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
	public void setPRG(byte[] prg){
		if(prg.length ==16384*2){
			PRG_ROM[0]=Arrays.copyOfRange(prg, 0,0x4000);
			PRG_ROM[1]=Arrays.copyOfRange(prg, 0x4000, 0x8000);
			//PRG_32k = true;
		}
		else{
			PRG_ROM[0]=Arrays.copyOfRange(prg, 0,0x4000);
			PRG_ROM[1]=Arrays.copyOfRange(prg, 0,0x4000);
		}

	}
	public void check(int i){}
	public void setCHR(byte[] chr){
		if(chr.length==0){
			CHR_ROM = new byte[2][0x1000];
			CHR_ram = true;
		}
		else{
			CHR_ROM[0]=Arrays.copyOfRange(chr,0,0x1000);
			CHR_ROM[1]=Arrays.copyOfRange(chr, 0x1000, 0x2000);
		}
	}
	//Debug functions
	public void printMemory(int offset,int length){
		System.out.print("[");
		for(int i = offset;i<offset+length-1;i++){
			System.out.print(Integer.toHexString(Byte.toUnsignedInt(cpuread(i)))+", ");
		}
		System.out.println("]");
	}
	public void printMemoryPPU(int offset,int length){
		System.out.print("[");
		for(int i = offset;i<offset+length-1;i++){
			System.out.print(Integer.toHexString(Byte.toUnsignedInt(ppuread(i)))+", ");
		}
		System.out.println("]");
	}
	public void printOAMPPU(int offset,int length){
		System.out.print("[");
		for(int i = offset;i<offset+length-1;i++){
			System.out.print(Integer.toHexString(Byte.toUnsignedInt(ppureadoam(i)))+", ");
		}
		System.out.println("]");
	}
	public void scanlinecounter(){}
	public static Mapper getmapper(int i){
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
		case 24:
			return new VRC6_24();
		default:
			System.err.println("Unsupported Mapper id: "+i);
		}
		return null;
	}
	public void runFrame() {
		while(!ppu.doneFrame){
			//if((map.cpu.program_counter==0x5c00)||dodebug||false)//0xe2c5||dodebug||false)
			//	debug();
			//if(map.ppu.scanline == 240)
			//	map.printMemoryPPU(0x3f00, 0x20);
			ppu.doCycle();
			ppu.doCycle();
			ppu.doCycle();
			cpu.run_cycle();
			apu.doCycle();				
		}
		
	}
	boolean dodebug;
	void debug(){
		Scanner s = new Scanner(System.in);
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
	}
}
