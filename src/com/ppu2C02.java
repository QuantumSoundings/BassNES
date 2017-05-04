package com;
import java.util.Arrays;

import mappers.Mapper;
import ui.UserSettings;
import video.NesDisplay;
import video.Renderer;

public class ppu2C02 implements java.io.Serializable{

	private static final long serialVersionUID = -1721542574813003352L;
	//memory map
	//0x0000-0x0fff pattern table 0
	//0x1000-0x1fff pattern table 1
	//0x2000-0x23ff nametable 0
	//0x2400-0x27ff nametable 1
	//0x2800-0x2bff nametable 2
	//0x2c00-0x2fff nametable 3
	//0x3000-0x3eff mirrors of 0x2000-0x2eff
	//0x3f00-0x3f1f palette ram indexes
	//0x3f20-0x3fff mirrors of 0x3f00-0x3f1f
	//cycles per scanline = 341
	//one frame is 262 scanlines
	//memory access is 2 cycles long
	double currentFPS;
	boolean oddframe = false;
	int oamcounter = 0;
	byte[] oambuffer = new byte[32];//secondary oam for current line sprites
	int oamBCounter = 0;
	//sprite stuff
	int[] spritebm = new int[8];//bitmaps
	int[] spriteco = new int[8];//xpos counter
	boolean[] spritepriority = new boolean[8];
	boolean[] spritehorizontal=new boolean[8];
	int[] spritepalette = new int[8];
	
	public int scanline;
	public int pcycle;
	int cyclepart;
	//Rendering related Variables
	int shiftreg16a=0;
	int shiftreg16b = 0;
	int shiftreg8a = 0;
	int shiftreg8b = 0;
	int palettelatchnew = 0;
	int palettelatchold = 0;
	int nametablebyte;//16bit
	int atablebyte;//16bit
	int ptablemap0;//8bit
	int ptablemap1;//8bit
	int fineX;
	
	//registers 0x2000
	byte PPUCTRL;
	int PPUCTRL_bna;//base nametable address
	boolean PPUCTRL_vraminc;//vram increment on cpu read/write ppudata
	boolean PPUCTRL_spta;//sprite pattern table base address
	boolean PPUCTRL_bpta;//background pattern table base address
	boolean PPUCTRL_ss;//sprite size; 0 = 8x8;1= 8x16
	boolean PPUCTRL_ms;//master/slave select
	boolean PPUCTRL_genNmi;
	//0x2001
	int PPUMASK;
	boolean PPUMASK_grey;//Greyscale; 0= normal color; 1=greyscal
	boolean PPUMASK_bl;//show background in leftmost 8px of screen;1=show
	boolean PPUMASK_sl;//show sprites in leftmost 8px of screen;1=show;
	boolean PPUMASK_sb;//show background
	boolean PPUMASK_ss;//show sprites
	boolean render;
	int PPUMASK_colorbits;//the three color emphasis bits; bit 1=red bit 2=green bit 3 = blue
	int leftmask_b=0;
	int leftmask_s=0;
	//0x2002
	byte PPUSTATUS;
	int PPUSTATUS_lsb;//bottom 5 bits last written to a ppu register
	boolean PPUSTATUS_so;//sprite overflow;
	boolean PPUSTATUS_sz;//sprite zero hit flag
	boolean PPUSTATUS_vb;//vertical blank started
	//0x2003
	public byte OAMADDR;
	//0x2004
	int OAMDATA;
	//0x2005
	//0x2006
	//0x2007
	byte PPUDATA_readbuffer=0;
	
	public byte OPEN_BUS;
	
	
	public boolean doneFrame;
	//boolean oddskip = false;
	public int v,t,x;
	Renderer renderer;
	int[] pixels;
	int pixelnum;
	//registers	
	Mapper map;
	int tv;
	public ppu2C02(Mapper m) {
		map = m;
		//display = disp;
		pixels = new int[256*240];
		scanline = 0;
		pcycle = 0;
		renderer= new Renderer();
	}
	boolean even = true;
	public void writeRegisters(int index,byte b){
		OPEN_BUS = b;
		switch(index){
		case 0x2000:
			//System.out.println("Writeing PPUCTRL:"+Integer.toBinaryString(Byte.toUnsignedInt(b)));
			t&=~0xc00;
			t|=(b&3)<<10;
			PPUCTRL=b;
			PPUCTRL_bna=b&0x3;
			PPUCTRL_vraminc = (b&4)==0?false:true;
			PPUCTRL_spta = (b&8)==0?false:true;
			PPUCTRL_bpta = (b&16)==0?false:true;
			//System.out.println("Base Nametable:"+PPUCTRL_bna);
			PPUCTRL_ss = (b&32) ==0?false:true;
			PPUCTRL_ms = (b&64) ==0?false:true;
			PPUCTRL_genNmi = (b&128)==0?false:true;
			//PPUCTRL_genNmi=true;
			if(scanline==-1&&pcycle==2)
				map.cpu.setNMI(false);
			else
				map.cpu.setNMI(PPUCTRL_genNmi&&PPUSTATUS_vb);

			break;
		case 0x2001:
			PPUMASK=Byte.toUnsignedInt(b);
			PPUMASK_grey = (b&1)==0?false:true;
			PPUMASK_bl = (b&2)==0?false:true;
			leftmask_b = PPUMASK_bl?0:8;
			PPUMASK_sl = (b&4)==0?false:true;
			leftmask_s = PPUMASK_sl?0:8;
			PPUMASK_sb = (b&8)==0?false:true;
			//System.out.println("Setting background to "+PPUMASK_sb);
			PPUMASK_ss = (b&16)==0?false:true;
			render = PPUMASK_ss||PPUMASK_sb;
			PPUMASK_colorbits = (b&0b11100000)<<3;
			break;
		case 0x2003:
			//System.out.println("Setting OAMADDR to: "+Byte.toUnsignedInt(b));
			OAMADDR = b;
			break;
		case 0x2004:
			//OAMDATA = Byte.toUnsignedInt(b);
			//System.out.println("Writing :"+b+" at: "+Byte.toUnsignedInt(OAMADDR));
			if(scanline>=240||(!dorender())){
				if((OAMADDR&0x03)==0x02)
					b&=0xe3;
				map.ppuwriteoam(Byte.toUnsignedInt(OAMADDR), b);
				OAMADDR++;
			}
			else
				OAMADDR+=4;
			//OAMADDR%=256;
			break;
		case 0x2005:
			if (even){
				t&= ~0x1f;
				fineX = b&7;
				t|= Byte.toUnsignedInt(b)>>3;
				even = false;
			}
			else{
				t &=~0x7000;
				t|=((Byte.toUnsignedInt(b)&7)<<12);
				t&= ~0x3e0;
				t|= (Byte.toUnsignedInt(b)&0xf8)<<2;
				even = true;
			}
			break;
		case 0x2006:
			if(even){
				//System.out.println("FIRST WRITE!");
				t &=0xc0ff;
				t|= (Byte.toUnsignedInt(b)&0x3f)<<8;
				t&=0x3fff;
				even = false;
			}
			else{
				//System.out.println("Second WRITE!");
				t &=0x7f00;
				t|= Byte.toUnsignedInt(b);
				tv=v;
				v=t;
				if((v&0x1000)!=0&&(tv&0x1000)==0)
					map.scanlinecounter();
				even = true;
			}
			break;
		case 0x2007:
			tv=v;
			map.ppuwrite((v&0x3fff), b);
			if(!dorender()||scanline>240&&scanline<=261)
				v+= PPUCTRL_vraminc?32:1;
			else if((v&0x7000)==0x7000){
				int ys = v &0x3e0;
				v&=0xFFF;
				switch(ys){
				case 0x3a0:
					v^=0xba0;
					break;
				case 0x3e0:
					v^=0x3e0;
					break;
				default:
					v+=0x20;
				}
			}
			else
				v+=0x1000;
			if((v&0x1000)!=0&&(tv&0x1000)==0)
				map.scanlinecounter();
			break;
		default: System.out.println("Something went wrong in ppureg write");
		}
	//PPUSTATUS_lsb = b&0b11111;
	//OPEN_BUS=b;

	}
	boolean block;
	public byte readRegister(int index){
		byte b = 0;
		tv=v;
		switch(index){
		case 0x2002:
			//b = (byte) PPUSTATUS_lsb;
			b |= PPUSTATUS_so?0x20:0;
			b |= PPUSTATUS_sz?0x40:0;
			if(!(scanline==241&&(pcycle==2)))
				b |= PPUSTATUS_vb?0x80:0;
			if(scanline==241&&(pcycle==3||pcycle==4||pcycle==2))
				map.cpu.nmi=false;
			b|= (OPEN_BUS&0x1f);
			even=true;
			PPUSTATUS_vb = false;
			map.cpu.setNMI(PPUCTRL_genNmi&&PPUSTATUS_vb);
			OPEN_BUS = b;
			break;
		case 0x2004:
			OPEN_BUS = map.ppureadoam(Byte.toUnsignedInt(OAMADDR));
			if(dorender()&&pcycle<=65&&scanline<241)
				return (byte)0xff;
			else{
				return map.ppureadoam(Byte.toUnsignedInt(OAMADDR));
			}
		case 0x2007:
			if((v&0x3fff)<0x3f00){
				b = PPUDATA_readbuffer;
				PPUDATA_readbuffer = map.ppuread((v&0x3fff));
			}
			else{
				PPUDATA_readbuffer = map.ppuread((v&0x3fff)-0x1000);
				b =map.ppuread((v&0x3fff));
				b &= 0b111111;
				b|= OPEN_BUS&0b11000000;
			}
			if(!dorender()||(scanline>240&&scanline<=260)){
				v+=PPUCTRL_vraminc?32:1;
			}
			else{
				incx();
				incy();
			}
			if((v&0x1000)!=0&&(tv&0x1000)==0)
				map.scanlinecounter();
			OPEN_BUS = b;
			break;
		default: 
			System.out.println("Something broke in readreg");
			return OPEN_BUS;
		}
		return OPEN_BUS;
	}
	public boolean dorender(){
		return render;
	}
	boolean drawBG(){
		return PPUMASK_sb;
	}
	boolean drawSprites(){
		return PPUMASK_ss;
	}
	public double getFPS(){
		return currentFPS;
	}
	
	private void incx(){
		if ((v & 0x001F) == 31){
			v &= ~0x001F;
			v ^= 0x0400;
		}
		else
			v += 1;
	}
	private void incy(){
		if ((v & 0x7000) != 0x7000)
			  v += 0x1000;
		else{
			  v &= ~0x7000;
			  int y = (v & 0x03E0) >> 5;
			  if (y == 29){
			    y = 0;
			    v ^= 0x0800;
			  }
			  else{
				  y =(y+1)&31;
			  }
			  v = (v & ~0x03E0) | (y << 5);
		}
	}
	
	public void getBG(){
		cyclepart++;
		switch((pcycle-1)&7){
		case 0:
			shiftreg16a = (shiftreg16a<<8)|ptablemap1;			
			shiftreg16b = (shiftreg16b<<8)|ptablemap0;
			palettelatchold = palettelatchnew;
			palettelatchnew = (atablebyte<<2);
			cyclepart=0;
			break;
		case 1://name table
			nametablebyte = Byte.toUnsignedInt(map.ppuread(0x2000|(v&0x0fff)))<<4;
			nametablebyte+=(PPUCTRL_bpta?0x1000:0);
			break;
		case 3://attribute table
			int tempx =0x23C0 | (v & 0x0C00) | ((v >> 4) & 0x38) | ((v >> 2) & 0x07);
			byte attbyte = map.ppuread(tempx);
			int sel = ((v & 2) >> 1) | ((v & 0x40) >> 5);
			switch (sel){
			case 0: atablebyte = Byte.toUnsignedInt(attbyte) & 3; break;
			case 1: atablebyte = (Byte.toUnsignedInt(attbyte)>>2) & 3; break;
			case 2: atablebyte = (Byte.toUnsignedInt(attbyte)>>4) & 3; break;
			case 3: atablebyte = (Byte.toUnsignedInt(attbyte)>>6) & 3; break;
			}
			break;
		case 5://tile low
			ptablemap0 = Byte.toUnsignedInt(map.ppuread((nametablebyte+((v&0x7000)>>>12))));	
			break;
		case 7://tile high
			ptablemap1 = Byte.toUnsignedInt(map.ppuread((nametablebyte)+8+((v&0x7000)>>>12)));
			if(pcycle !=256){
				incx();
			}
			else
				incy();
			break;
		}		
	}
	boolean spriteoverdelay;
	public void doCycle(){
		if(delayset){// FIXME    //Sprite zero flag set 1 cycle too early
			delayset=false;
			PPUSTATUS_sz=true;
		}
		else if(spriteoverdelay){
			spriteoverdelay=false;
			PPUSTATUS_so=true;
		}
		if(pcycle>339){
			if(scanline==260){
				scanline=-1;pcycle=0;
				oddframe=!oddframe;
				doneFrame=true;
			}
			else{
				pcycle=0;
				scanline++;
			}
			oldspritezero = spritezero;
			spritezero=false;
		}
		else{
			if(scanline<240&&pcycle>0)
				render();
			else if(scanline==241&&pcycle==1){
				PPUSTATUS_vb = true;
				map.cpu.doNMI=(PPUCTRL_genNmi&&PPUSTATUS_vb);
				genFrame();
			}
			pcycle++;
		}
	}
	void render(){
		int cycle = pcycle;
		if(cycle<=256){
			if(render)
				getBG();
			if(scanline>=0){
				drawpixel();
				if((cycle&1)==0)
					spriteEvaluationNew();
			}
			else if(pcycle==1){
					PPUSTATUS_so=false;
					PPUSTATUS_sz = false;
			}
			else if(pcycle==2){
				PPUSTATUS_vb = false;
				map.cpu.setNMI(PPUCTRL_genNmi&&PPUSTATUS_vb);
			}
			
		}
		else if(cycle==257){
			if(render){
				v &=~0x41f;
               	v|=t&0x41f;
			}
			oamBCounter=0;
			numsprites=0;
			spritec=0;
		}
		else if(cycle<=320){
			if(render){
				OAMADDR=0;
				if(cycle==260)
					map.scanlinecounter();
				else if(scanline==-1&&cycle>=280&&cycle<=304)
					v=t;
				
			}
			if(cycle%8==4&&scanline>=0)
				loadSprites();
		}
		else if(cycle<=336){
			if(render)
				getBG();
		}
		else if(cycle==339){
			if(scanline==-1&&!oddframe&&dorender()){
				pcycle=-1;scanline=0;
			}
		}
	}
	private void genFrame(){
		renderer.buildFrame(pixels, 2);
		pixelnum = 0;
		map.system.videoCallback(renderer.colorized);
	}

	private void drawpixel(){
		if(render||(v&0x3f00)!=0x3f00)
			pixels[pixelnum++] = (PPUMASK_colorbits)|pixelColor();
		else
			pixels[pixelnum++] = (PPUMASK_colorbits)|Byte.toUnsignedInt(map.ppuread(v));
	}
	boolean delayset;
	int pixelColor(){
		int backgroundcolor=0;
		int cycle = pcycle;
		int offset = 15-(fineX+cyclepart);
		if(PPUMASK_sb&&leftmask_b<cycle){
			int bit = (((shiftreg16a>>(offset-1))&2))|((shiftreg16b>>offset)&1);
			if(UserSettings.RenderBackground&&bit!=0)
				backgroundcolor =(offset>=8?palettelatchold:palettelatchnew)|bit;			
		}
		if(PPUMASK_ss){
			for(int i = 0;i < numsprites;i++){
				int off = (cycle-spriteco[i]-1);
				if(off>=0&&off<8){
					int bit;
					if(spritehorizontal[i])
						bit = (((spritebm[i]>>(off))&1)<<1)|((spritebm[i]>>(off+8))&1);
					else
						bit = (((spritebm[i]>>(7-off))&1)<<1)|((spritebm[i]>>((7-off)+8))&1);
					if(bit!=0){
						if(oldspritezero&&!PPUSTATUS_sz&&i==0&&PPUMASK_sb&&backgroundcolor!=0&&leftmask_s<cycle&&cycle<256){
							delayset=true;
						}
						if(UserSettings.RenderSprites&&(spritepriority[i]||backgroundcolor==0)){
							return 0xff&map.ppu_palette[0x10+4*spritepalette[i]+bit];
						}
						break;
					}
				}
			}
		}
		return 0xff&map.ppu_palette[backgroundcolor];
	}
	private int inrange(int y){
		if((scanline)-(y)>=0)
			return scanline-(y);
		else
			return 20;
	}
	private int stage = 1;
	private int n,m = 0;
	int spritec = 0;
	boolean oldspritezero;
	boolean spritezero;
	int numsprites;
	void stage2(){
		if(n==63){
			n=0;
			stage = 7;
			return;
		}
		else
			n++;
		if(oamBCounter<8)
			stage = 1;
		else if(oamBCounter==8){
			m = 0;
			stage = 6;
		}
		return;
	}
	private void stage3(){
		int y = Byte.toUnsignedInt(map.ppureadoam((4*n+m)));
		if((PPUCTRL_ss?inrange(y)<16:inrange(y)<8)){//&&y<scanline){
			if((PPUMASK_ss||PPUMASK_sb)&&y<240){
				spriteoverdelay=true;
			}
			if(m==3){
				n++;
				m=0;
			}
			else
				m++;
		}
		else{
			if(n==63){
				stage = 7;
				n = 0;
				m = 0;
			}
			else{
				n++;
				m= (m+1)&3;
			}
		}
	}
	void clearSprites(){
		Arrays.fill(spriteco, 0);
		Arrays.fill(spritepriority, false);
		Arrays.fill(spritepalette, 0);
		Arrays.fill(spritebm, 0);
		numsprites=0;
	}
	public void spriteEvaluationNew(){
		if(pcycle<65){
			if(pcycle==2){
			Arrays.fill(oambuffer, (byte)0xff);
			stage = 1;
			m=0;
			n=0;
			oamBCounter=0;
			spritec=0;
			}
			return;
		}
		else if(pcycle<=256){
			switch(stage){
			case 1: // first write of stage 1.
				int y = Byte.toUnsignedInt(map.ppureadoam(4*n));
				oambuffer[4*oamBCounter] = (byte) y;
				if(PPUCTRL_ss?inrange(y)<16:inrange(y)<8)
					stage = 2; // Continue writing sprite data
				else{
					oambuffer[4*oamBCounter] = (byte)0xff;
					stage2(); // move on to next sprite
				}
				return;
			case 2://second write of stage 1
				oambuffer[4*oamBCounter+1]=map.ppureadoam(4*n+1);
				stage = 3;
				return;
			case 3://third write of stage 1
				oambuffer[4*oamBCounter+2]=map.ppureadoam(4*n+2);
				stage = 4;
				return;
			case 4://fourth write of stage 1
				oambuffer[4*oamBCounter+3]=map.ppureadoam(4*n+3);
				oamBCounter++;
				if(n==0){
					spritezero= true;
				}
				stage2();//move on the next sprite
				return;
			case 5://stage 2
				return;
			case 6://stage 3
				stage3();
				return;
			case 7://stage 4 do nothing
				return;			
			}
		}
	}
	void loadSprites(){
		if(Byte.toUnsignedInt(oambuffer[4*oamBCounter])!=0xff){
			spriteco[spritec] = Byte.toUnsignedInt(oambuffer[4*oamBCounter+3]);
			byte attributes = oambuffer[4*oamBCounter+2];
			spritepalette[spritec] = attributes&3;
			spritepriority[spritec] = (attributes&32)>0?false:true;
			int y = inrange(Byte.toUnsignedInt(oambuffer[4*oamBCounter]));
			int tileindex;
			if(PPUCTRL_ss){//get index number for different sprite sizes
				int temp = Byte.toUnsignedInt(oambuffer[4*oamBCounter+1]);
				tileindex = (temp&1)*0x1000+(temp&0xfe)*16;
				if(y>=8)//correct it for 8x16 sprites
					tileindex+=0x10;
			}
			else{
				tileindex=Byte.toUnsignedInt(oambuffer[4*oamBCounter+1])<<4;
				tileindex+= PPUCTRL_spta?0x1000:0;
			}
			if((attributes&0x80)!=0){//handle vertically flipped sprites
				if(PPUCTRL_ss){//case for large sprites
					if(y<8)
						tileindex+=0x10;
					else
						tileindex-=0x10;
					y=y%8;
				}	
				tileindex+=((7)-y);
			}
			else{
				y%=8;
				tileindex+=y;
			}
			if(tileindex<0)tileindex*=-1;
			spritehorizontal[spritec]=(attributes&0x40)!=0;
			spritebm[spritec] = (Byte.toUnsignedInt((map.ppuread(tileindex)))<<8)|Byte.toUnsignedInt((map.ppuread(tileindex+8)));
			numsprites++;
			spritec++;
			oamBCounter++;
			return;
		}
	}
}