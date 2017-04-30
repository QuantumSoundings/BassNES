package mappers;
import java.util.Arrays;

import com.APU;
import com.CPU_6502;
import com.Controller;
import com.NES;
import com.ppu2C02;
public class Mapper {//There will be class that inheriet this class. Better to have all reads and writes go through this
	
	public CPU_6502 cpu;
	byte[] cpu_ram= new byte[0x800];
	NES nes;
	public ppu2C02 ppu;
	byte[] ppu_ram= new byte[0x1fff];
	public byte[] ppu_palette = new byte[]{63,63,63,63,63,63,63,63,63,63,63,63,63,63,63,63,63,63,63,63,63,63,63,63,63,63,63,63,63,63,63,63};
	public byte[] ppu_oam = new byte[256];
	
	public APU apu;
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
	
	public Controller control;
	public Controller control2;
	
	public boolean olda12;
	public byte openbus;
	
	public Mapper(){
		ppu_palette[0]=0xf;		
	}
	public void setcomponents(CPU_6502 c,ppu2C02 p,Controller cont,Controller cont2, APU a){
		cpu = c;
		ppu = p;
		apu = a;
		control = cont;
		control2 = cont2;
	}
	public void setNes(NES n){ nes = n;}
	public void setMirror(int i){
		mirrormode=(i==0)?true:false;
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
		//System.out.println("WRITING: "+b+" to oam");
		ppu_oam[index]=b;
		//ppuregisterhandler(0x2004,b,true);
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
		else if(index>=0x2000&&index<=0x2fff)
			ppu_ram[ppuNameTableMirror(index)]=b;
		else if(index>=0x3000&&index<=0x3eff){
			ppu_ram[ppuNameTableMirror(index-0x1000)]=b;
		}
		else{
			int i = (index&0x1f);//%0x20;
			if(i%4==0)
				i+= i>=0x10?-0x10:0;
			ppu_palette[i]=b;
		}
	}
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
	public byte ppuread(int index){
		if(index<0x2000)
			if(index<0x1000)
				return CHR_ROM[0][index];
			else
				return CHR_ROM[1][index%0x1000];
		else if(index>=0x2000&&index<=0x2fff)
			return ppu_ram[ppuNameTableMirror(index)];
		else if(index>=0x3000&&index<=0x3eff)
			return ppu_ram[ppuNameTableMirror(index-0x1000)];
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
		case 7:
			return new AxROM();
		case 9:
			return new MMC2();
		default:
			System.err.println("Unsupported Mapper id: "+i);
		}
		return null;
	}
}
