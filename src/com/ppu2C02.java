package com;
import java.util.Arrays;

import mappers.Mapper;
import ui.UserSettings;
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
	//pal stuff
	int finalscanline;
	boolean palregion;
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
	public boolean PPUCTRL_bpta;//background pattern table base address
	boolean PPUCTRL_ss;//sprite size; 0 = 8x8;1= 8x16
	boolean PPUCTRL_ms;//master/slave select
	boolean PPUCTRL_genNmi;
	//0x2001
	int PPUMASK;
	boolean PPUMASK_grey;//Greyscale; 0= normal color; 1=greyscal
	boolean PPUMASK_bl;//show background in leftmost 8px of screen;1=show
	boolean PPUMASK_sl;//show sprites in leftmost 8px of screen;1=show;
	boolean PPUMASK_sb;//show background
	public boolean PPUMASK_ss=false;//show sprites
	boolean render;
	int PPUMASK_colorbits;//the three color emphasis bits; bit 1=red bit 2=green bit 3 = blue
	int leftmask_b=0;
	int leftmask_s=0;
	//0x2002
	byte PPUSTATUS;
	int PPUSTATUS_lsb;//bottom 5 bits last written to a ppu register
	boolean PPUSTATUS_so;//sprite overflow;
	boolean PPUSTATUS_sz;//sprite zero hit flag
	public boolean PPUSTATUS_vb;//vertical blank started
	//0x2003
	public byte OAMADDR;
	//0x2004
	int OAMDATA;
	//0x2005
	//0x2006
	//0x2007
	byte PPUDATA_readbuffer=0;
	
	public byte OPEN_BUS;
	public boolean spritefetch;
	
	public boolean doneFrame;
	//boolean oddskip = false;
	public int v,t,x;
	Renderer renderer;
	private int[] pixels;
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
	public void setpal(boolean pal){
		if(pal)
			finalscanline = 310;
		else
			finalscanline = 260;
		palregion=pal;
	}
	public boolean getSpriteSize(){
		return PPUCTRL_ss;
	}
	boolean even = true;
	public void writeRegisters(int index,byte b){
		OPEN_BUS = b;
		switch(index){
		case 0:
			t&=~0xc00;
			t|=(b&3)<<10;
			PPUCTRL=b;
			PPUCTRL_bna=b&0x3;
			PPUCTRL_vraminc = (b & 4) != 0;
			PPUCTRL_spta = (b & 8) != 0;
			PPUCTRL_bpta = (b & 16) != 0;	
			PPUCTRL_ss = (b & 32) != 0;
			PPUCTRL_ms = (b & 64) != 0;
			PPUCTRL_genNmi = (b & 128) != 0;
			if(scanline==-1&&pcycle==0)
				map.cpu.setNMI(false);
			else
				map.cpu.setNMI(PPUCTRL_genNmi&&PPUSTATUS_vb);

			break;
		case 1:
			PPUMASK=Byte.toUnsignedInt(b);
			PPUMASK_grey = (b & 1) != 0;
			PPUMASK_bl = (b & 2) != 0;
			leftmask_b = PPUMASK_bl?0:8;
			PPUMASK_sl = (b & 4) != 0;
			leftmask_s = PPUMASK_sl?0:8;
			PPUMASK_sb = (b & 8) != 0;
			PPUMASK_ss = (b & 16) != 0;
			render = PPUMASK_ss||PPUMASK_sb;
			PPUMASK_colorbits = (b&0b11100000)<<3;
			if(PPUMASK_grey)
				PPUMASK_colorbits|=0x800;
			break;
		case 3:
			OAMADDR = b;
			break;
		case 4:
			if(scanline>=240||(!dorender())){
				if((OAMADDR&0x03)==0x02)
					b&=0xe3;
				map.ppuwriteoam(Byte.toUnsignedInt(OAMADDR), b);
				OAMADDR++;
			}
			else
				OAMADDR+=4;
			break;
		case 5:
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
		case 6:
			if(even){
				t &=0xc0ff;
				t|= (Byte.toUnsignedInt(b)&0x3f)<<8;
				t&=0x3fff;
				even = false;
			}
			else{
				t &=0x7f00;
				t|= Byte.toUnsignedInt(b);
				tv=v;
				v=t;
				if((v&0x1000)!=0&&(tv&0x1000)==0)
					map.scanlinecounter();
				even = true;
			}
			break;
		case 7:
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

	}
	boolean block;
	public byte readRegister(int index){
		byte b = 0;
		tv=v;
		switch(index){
		case 2:
			b |= PPUSTATUS_so?0x20:0;
			b |= PPUSTATUS_sz?0x40:0;
			if(!(scanline==241&&(pcycle==0)))
				b |= PPUSTATUS_vb?0x80:0;
			if(scanline==241&&(pcycle==0||pcycle==1||pcycle==2))
				map.cpu.nmi=false;
			b|= (OPEN_BUS&0x1f);
			even=true;
			PPUSTATUS_vb = false;
			map.cpu.setNMI(false);
			OPEN_BUS = b;
			break;
		case 4:
			OPEN_BUS = map.ppureadoam(Byte.toUnsignedInt(OAMADDR));
			if(dorender()&&pcycle<=65&&scanline<241)
				return (byte)0xff;
			else{
				return map.ppureadoam(Byte.toUnsignedInt(OAMADDR));
			}
		case 7:
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
		switch((pcycle-1)%8){
		case 0://name table
			shiftreg16a = (shiftreg16a<<8)|ptablemap1;			
			shiftreg16b = (shiftreg16b<<8)|ptablemap0;
			palettelatchold = palettelatchnew;
			palettelatchnew = (atablebyte<<2);
			cyclepart=0;
			nametablebyte = Byte.toUnsignedInt(map.ppureadNT(0x2000|(v&0x0fff)))<<4;
			nametablebyte+=(PPUCTRL_bpta?0x1000:0);
			break;
		case 1: cyclepart=1;break;
		case 2://attribute table
			int tempx =0x23C0 | (v & 0x0C00) | ((v >> 4) & 0x38) | ((v >> 2) & 0x07);
			byte attbyte = map.ppureadAT(tempx);
			int sel = ((v & 2) >> 1) | ((v & 0x40) >> 5);
			atablebyte = ((0xff&attbyte)>>(sel*2))&3;
			cyclepart=2;
			break;
		case 3://tile low
			nametablebyte = nametablebyte+((v&0x7000)>>>12);
			ptablemap0 = Byte.toUnsignedInt(map.ppureadPT(nametablebyte));	
			cyclepart=3;
			break;
		case 4:cyclepart=4;break;
		case 5://tile high
			ptablemap1 = Byte.toUnsignedInt(map.ppureadPT(nametablebyte+8));
			cyclepart=5;
			break;
		case 6: cyclepart=6;break;
		case 7:
			if(pcycle !=256)
				incx();
			else
				incy();
			cyclepart=7;
			break;
		}	
	}
	boolean prevrender;
	public void doCycle(){
		if(pcycle>339){
			pcycle=0;
			if(scanline==finalscanline){
				scanline=-1;
				oddframe=!oddframe;
				doneFrame=true;
			}
			else
				scanline++;
			if(scanline==-1){
				PPUSTATUS_so=false;
				PPUSTATUS_sz = false;
			}
			else if(scanline==241)
				genFrame();
			oldspritezero = spritezero;
			spritezero=false;
		}
		else{
			pcycle++;
			if(scanline<240)
				render();		
		}
		prevrender = render;
	}
	void render(){
		int cycle = pcycle;
		if(cycle<=256){
			if(prevrender)
				getBG();
			if(scanline>=0){
				drawpixel();
				if((pcycle&1)==0)
					spriteEvaluationNew();
			}
			else if(pcycle==1){
				PPUSTATUS_vb = false;
				map.cpu.setNMI(false);
			}
			
		}
		else if(cycle==257){
			if(prevrender){
				v &=~0x41f;
               	v|=t&0x41f;
			}
			oamBCounter=0;
			numsprites=0;
			spritec=0;
		}
		else if(cycle<=320){
			if(prevrender){
				OAMADDR=0;
				if(scanline==-1&&cycle>=280&&cycle<=304)
					v=t;
				if(cycle%8==4)
					loadSprites();	
			}
		}
		else if(cycle<=336){
			if(prevrender)
				getBG();
		}
		else if(cycle==339||cycle==337){
			map.ppureadNT(v&0xfff);
			if(scanline==-1&&cycle==339&&!oddframe&&prevrender&&!palregion){
				pcycle=340;
			}
		}
	}
	private void genFrame(){
		PPUSTATUS_vb = true;
		map.cpu.doNMI=PPUCTRL_genNmi;
		renderer.buildFrame(pixels, 2);
		pixelnum = 0;
	}
	private void drawpixel(){
		if(render||(v&0x3f00)!=0x3f00){
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
					int off = 7-(cycle-spriteco[i]-1);
					if(off>=0&&off<8){
						int bit =  (((spritebm[i]>>(off))&1)<<1)|((spritebm[i]>>((off)+8))&1);
						if(bit!=0){
							if(oldspritezero&&!PPUSTATUS_sz&&i==0&&PPUMASK_sb&&backgroundcolor!=0&&leftmask_s<cycle&&cycle<256){
								PPUSTATUS_sz = true;
							}
							if(UserSettings.RenderSprites&&(spritepriority[i]||backgroundcolor==0)){
								pixels[pixelnum++] = (PPUMASK_colorbits)|(0xff&map.ppu_palette[0x10+4*spritepalette[i]+bit]);
								return;
							}
							break;
						}
					}
				}
			}
			pixels[pixelnum++] = (PPUMASK_colorbits)|(0xff&map.ppu_palette[backgroundcolor]);
		}
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
		if(numsprites>0&&PPUMASK_ss){
			for(int i = 0;i < numsprites;i++){
				int off = (cycle-spriteco[i]-1);
				if(off>=0&&off<8){
					int bit =  (((spritebm[i]>>(7-off))&1)<<1)|((spritebm[i]>>((7-off)+8))&1);
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
		int x = scanline-y;
		if(x>=0)
			return x;
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
		if((PPUCTRL_ss?inrange(y)<16:inrange(y)<8)){
			if((PPUMASK_ss||PPUMASK_sb)&&y<240){
				PPUSTATUS_so = true;
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
				int y = Byte.toUnsignedInt(map.ppu_oam[n*4]);//ppureadoam(4*n));
				oambuffer[4*oamBCounter] = (byte) y;
				if(PPUCTRL_ss?inrange(y)<16:inrange(y)<8)
					stage = 2; // Continue writing sprite data
				else{
					oambuffer[4*oamBCounter] = (byte)0xff;
					stage2(); // move on to next sprite
				}
				return;
			case 2://second write of stage 1
				oambuffer[4*oamBCounter+1]=map.ppu_oam[4*n+1];
				stage = 3;
				return;
			case 3://third write of stage 1
				oambuffer[4*oamBCounter+2]=map.ppu_oam[4*n+2];
				stage = 4;
				return;
			case 4://fourth write of stage 1
				oambuffer[4*oamBCounter+3]=map.ppu_oam[4*n+3];
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
			case 8: case 9: case 10: case 11:case 12:case 13:case 14:case 15:case 16:case 17:case 18:case 19:case 20:case 21:
				return;
			}
		}
	}
	int[] BitReverseTable256 = new int[]
		{
		  0x00, 0x80, 0x40, 0xC0, 0x20, 0xA0, 0x60, 0xE0, 0x10, 0x90, 0x50, 0xD0, 0x30, 0xB0, 0x70, 0xF0, 
		  0x08, 0x88, 0x48, 0xC8, 0x28, 0xA8, 0x68, 0xE8, 0x18, 0x98, 0x58, 0xD8, 0x38, 0xB8, 0x78, 0xF8, 
		  0x04, 0x84, 0x44, 0xC4, 0x24, 0xA4, 0x64, 0xE4, 0x14, 0x94, 0x54, 0xD4, 0x34, 0xB4, 0x74, 0xF4, 
		  0x0C, 0x8C, 0x4C, 0xCC, 0x2C, 0xAC, 0x6C, 0xEC, 0x1C, 0x9C, 0x5C, 0xDC, 0x3C, 0xBC, 0x7C, 0xFC, 
		  0x02, 0x82, 0x42, 0xC2, 0x22, 0xA2, 0x62, 0xE2, 0x12, 0x92, 0x52, 0xD2, 0x32, 0xB2, 0x72, 0xF2, 
		  0x0A, 0x8A, 0x4A, 0xCA, 0x2A, 0xAA, 0x6A, 0xEA, 0x1A, 0x9A, 0x5A, 0xDA, 0x3A, 0xBA, 0x7A, 0xFA,
		  0x06, 0x86, 0x46, 0xC6, 0x26, 0xA6, 0x66, 0xE6, 0x16, 0x96, 0x56, 0xD6, 0x36, 0xB6, 0x76, 0xF6, 
		  0x0E, 0x8E, 0x4E, 0xCE, 0x2E, 0xAE, 0x6E, 0xEE, 0x1E, 0x9E, 0x5E, 0xDE, 0x3E, 0xBE, 0x7E, 0xFE,
		  0x01, 0x81, 0x41, 0xC1, 0x21, 0xA1, 0x61, 0xE1, 0x11, 0x91, 0x51, 0xD1, 0x31, 0xB1, 0x71, 0xF1,
		  0x09, 0x89, 0x49, 0xC9, 0x29, 0xA9, 0x69, 0xE9, 0x19, 0x99, 0x59, 0xD9, 0x39, 0xB9, 0x79, 0xF9, 
		  0x05, 0x85, 0x45, 0xC5, 0x25, 0xA5, 0x65, 0xE5, 0x15, 0x95, 0x55, 0xD5, 0x35, 0xB5, 0x75, 0xF5,
		  0x0D, 0x8D, 0x4D, 0xCD, 0x2D, 0xAD, 0x6D, 0xED, 0x1D, 0x9D, 0x5D, 0xDD, 0x3D, 0xBD, 0x7D, 0xFD,
		  0x03, 0x83, 0x43, 0xC3, 0x23, 0xA3, 0x63, 0xE3, 0x13, 0x93, 0x53, 0xD3, 0x33, 0xB3, 0x73, 0xF3, 
		  0x0B, 0x8B, 0x4B, 0xCB, 0x2B, 0xAB, 0x6B, 0xEB, 0x1B, 0x9B, 0x5B, 0xDB, 0x3B, 0xBB, 0x7B, 0xFB,
		  0x07, 0x87, 0x47, 0xC7, 0x27, 0xA7, 0x67, 0xE7, 0x17, 0x97, 0x57, 0xD7, 0x37, 0xB7, 0x77, 0xF7, 
		  0x0F, 0x8F, 0x4F, 0xCF, 0x2F, 0xAF, 0x6F, 0xEF, 0x1F, 0x9F, 0x5F, 0xDF, 0x3F, 0xBF, 0x7F, 0xFF
		};
	void loadSprites(){
		//if(Byte.toUnsignedInt(oambuffer[4*oamBCounter])!=0xff){
			spriteco[spritec] = Byte.toUnsignedInt(oambuffer[4*oamBCounter+3]);
			byte attributes = oambuffer[4*oamBCounter+2];
			spritepalette[spritec] = attributes&3;
			spritepriority[spritec] = (attributes & 32) <= 0;
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
			spritefetch= true;
			if((attributes&0x40)!=0)
				spritebm[spritec] = (BitReverseTable256[Byte.toUnsignedInt(map.ppureadPT(tileindex))]<<8)|(BitReverseTable256[Byte.toUnsignedInt(map.ppureadPT(tileindex+8))]);
			else
				spritebm[spritec] = (Byte.toUnsignedInt((map.ppureadPT(tileindex)))<<8)|Byte.toUnsignedInt((map.ppureadPT(tileindex+8)));	
			spritefetch=false;
			if(tileindex<0)tileindex*=-1;
			//spritehorizontal[spritec]=(attributes&0x40)!=0;
			//spritebm[spritec] = (Byte.toUnsignedInt((map.ppuread(tileindex)))<<8)|Byte.toUnsignedInt((map.ppuread(tileindex+8)));
			if(scanline>=0&&Byte.toUnsignedInt(oambuffer[4*oamBCounter])!=0xff)
				numsprites++;
			spritec++;
			oamBCounter++;
			return;
		//}
	}
}