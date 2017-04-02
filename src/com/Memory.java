package com;
public class Memory {
	//memory regions
	//0-0x07ff internal cpu ram
	//0x0800-0x0fff mirror of ram
	//0x1000-0x17ff mirror of ram
	//0x1800-0x1fff mirror of ram
	//0x2000-0x2007 repeating is ppu registers
		/*0x2000 bit 0 - x scroll name table selection
			     bit 1 - y scroll name table selection.
			     bit 2 - increment ppu address by 1/32 (0/1) on access to port 7
				 bit 3 - object pattern table selection (if bit 5 = 0)
				 bit 4 - playfield pattern table selection
				 bit 5 - 8/16 scanline objects (0/1)
				 bit 6 - ext bus direction (0:input; 1:output)
				 bit 7 - /vbl disable (when 0)*/
		/*0x2001 bit 0 - disable composit colorburst (when 1). causes gfx to go black and white
		  		 bit 1 - left side screen column (8 px wide) playfield clipping (when 0).
		  		 bit 2 - left side screen column (8 px wide) object clipping (when 0).
		  		 bit 3 - enable playfield display (on 1)
		  		 bit 4 - enable objects display (on 1)
		  		 bit 5 - R
		  		 bit 6 - G
		  		 bit 7 - B*/
		/*0x2002 - bit 5 - more than 8 objects on a single scanline have been detected in the last frame
				   bit 6 - a primary object pixel has collided with a playfield pixel in the last frame
				   bit 7 - vblank flag*/
		//0x2003 - internal object attribute memory index pointer (64 attributes, 23 bits each, byte granular access). Stored value post-increments on access to port
		//0x2004 - returns object attribute memory location indexed  by port 3, then increments port 3.
		//0x2005 - scroll offset port.
		//0x2006 - ppu address port to access with port 7.
		//0x2007 - ppu memory write port.
				 
	//0x2008-0x3fff mirror of ppu registers every 8 bytes
	//0x4000-0x4017 NES APU and IO registers
	//0x4018-0x401f APU and I/O funcionality that is normally disabled
	//0x4020-0xffff Cartridge space.. up to the mapper.
	static byte[] mem;
	static byte[] ppumem;
	static byte[] oam;
	static byte[][] banks;
	static int currentbank;
	static ppu2C02 ppu;
	static boolean ppuaddrwrite1 = false;
	static boolean PPUADDRw = false;
	static boolean PPUDATAw = false;
	static boolean PPUDATAr = false;
	static boolean PPUSCRLw = false;
	static boolean blockppu = true;
	static boolean DMAflag = false;
	static boolean doNMI=false;
	static boolean even = true;
	public Memory(){
		mem = new byte[0xFFFF+1];
		ppumem = new byte[0x3fff];
		oam = new byte[256];
		mem[0x2002] = (byte)(0);
		banks = new byte[4][0x1fff];
		currentbank=0;
	}
	
	private void swaptables(byte b){
		System.out.println("SWAPPING TABLES!!!!!!!!!!!!!!!!!!!!!!!");
		int i = Byte.toUnsignedInt(b)&3;
		writeppu(0,banks[i]);
		currentbank = i;
		
	}
	public void setPPU(ppu2C02 pp){
		ppu =pp;
	}
	private int doppumirror(int i){
		i = i&0x3fff;
		if(i>=0x3f00&&i<=0x3fff){
			int t = i&0xff00;
			i= i&0xff;
			i=i%0x20;
			i = t|i;
		}
		else if(i>0x2000&&i<0x3000){
			int t = i&0xFFF;
			i = 0x2000+(t%0x400);
		}
		return i;
	}
	void writedma(int index,byte b){
		oam[index]=b;
	}
	byte readdma(int index){
		return oam[index];
	}
	void write(int index,byte[] arr){
		for(int i = 0;i<arr.length-1&&index<mem.length-1;i++){
			mem[index]=arr[i];
			index++;
		}
	}
	void writeppu(int index,byte[] arr){
		for(int i = 0;i<arr.length-1&&index<mem.length-1;i++){
			ppumem[index]=arr[i];
			index++;
		}
	}
	void superwrite(int index,byte x){
		index =domirroring(index);
		if(index == 0x2000){
			System.out.println("WARNING SETTING PPUCTRL to: "+x);
		}
		mem[index]=x;
	}
	int domirroring(int index){
		if(index>=0x2000&&index<=0x3fff)
			return (index%8)+0x2000;
		return index;
	}
	byte ppuregisterhandler(int index,byte x,boolean write){
		if(index ==0x2000){//PPUCTRL
			if(write&&!blockppu)
				ppu.writeRegisters(index, x);
		}
		else if(index ==0x2001){//PPUMASK
			if(write&&!blockppu)
				ppu.writeRegisters(index, x);
		}
		else if(index ==0x2002){//PPUSTATUS
			if(!write)
				return ppu.readRegister(index);
		}
		else if(index ==0x2003){//OAMADDR
			if(write)
				ppu.writeRegisters(index, x);
		}
		else if(index ==0x2004){//needs work for full accuracy OAMDATA
			if(write)
				ppu.writeRegisters(index, x);
			else
				return ppu.readRegister(index);
		}
		else if(index ==0x2005){//PPUSCRL
			if(write&&!blockppu)
				ppu.writeRegisters(index, x);
		}
		else if(index ==0x2006){//PPUADDR
			if(write&&!blockppu)
				ppu.writeRegisters(index, x);
		}
		else if(index ==0x2007){
			if(write)
				ppu.writeRegisters(index, x);
			else
				ppu.readRegister(index);
		}
		return 0;
	}
	void write(int index,byte x){
		/*if(index>0x4018){
			if(index<0x8000||index>0xffff){
				mem[index]=x;
			}

			swaptables(x);
		}*/
		if(index>=0x2000&&index<=0x2007){
			ppuregisterhandler(index,x,true);
		}
		else if(index ==0x4014){
			mem[index] = x;
			DMAflag = true;
		}
		else
			mem[index]=x;
		
	}	
	byte read(int index){
		index = domirroring(index);
		if(index>=0x2000&&index<=0x2007){
			return ppuregisterhandler(index,(byte)0,false);
		}
		return mem[index];
	}
	void writeppu(int index,byte x){
		//System.out.println("Doing a ppu write to ADDR: " + Integer.toHexString(index)
		//+" with value: "
		//		+Integer.toHexString(Byte.toUnsignedInt(x)));
		int i = doppumirror(index);
		ppumem[doppumirror(index)]=x;
	}
	byte readppud(int index){
		return ppumem[index];
	}
	byte readppu(int index){		
		return ppumem[doppumirror(index)];
	}
	int uread(int index){
		index= domirroring(index);
		if(index>=0x2000&&index<=0x2007)
			return Byte.toUnsignedInt(ppuregisterhandler(index,(byte)0,false));
		return Byte.toUnsignedInt(mem[index]);
	}
	void printMemory(int offset,int length){
		System.out.print("[");
		for(int i = offset;i<offset+length-1;i++){
			System.out.print(Integer.toHexString(Byte.toUnsignedInt(mem[i]))+", ");
		}
		System.out.println("]");
	}
	void printMemoryppu(int offset,int length){
		System.out.print("[");
		for(int i = offset;i<offset+length-1;i++){
			System.out.print(Integer.toHexString(Byte.toUnsignedInt(ppumem[i]))+", ");
		}
		System.out.println("]");
	}
	void printMemoryoam(){
		System.out.print("[");
		for(byte b:oam){
			System.out.print(Integer.toHexString(Byte.toUnsignedInt(b))+", ");
		}
		System.out.println("]");
		
	}
}
