package com;
import java.io.IOException;
import java.util.Arrays;

import mappers.Mapper;
import video.NTSC_Converter;
import video.NesDisplay;
import video.Renderer;
import mappers.MMC3;

public class ppu2C02 {
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
	int NMI_occured = 0;
	int NMI_output = 0;
	boolean doNMI = false;
	
	boolean oddframe = false;
	byte[] oam = new byte[256];//primary oam
	int oamcounter = 0;
	byte[] oambuffer = new byte[32];//secondary oam for current line sprites
	int oamBCounter = 0;
	boolean oamsignal = false;// signal to return 0xff during portion of sprite evaluation
	//sprite stuff
	int[] spritebm = new int[8];//bitmaps
	//int[] spritela = new int[8];//attribute bytes
	int[] spriteco = new int[8];//xpos counter
	boolean[] spritepriority = new boolean[8];
	int[] spritepalette = new int[8];
	
	
	int shiftreg16ah = 0;
	int shiftreg16a=0;
	int shiftreg16bh=0;
	int shiftreg16b = 0;
	int shiftreg8a = 0;
	int shiftreg8b = 0;
	int palettelatch = 0;
	public int scanline;
	public int pcycle;
	int scanlinephase;
	int nametablebyte;//16bit
	int atablebyte;//16bit
	int ptablemap0;//8bit
	int ptablemap1;//8bit
	int fineX;
	
	//byte[] pixels
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
	int PPUMASK_colorbits;//the three color emphasis bits; bit 1=red bit 2=green bit 3 = blue
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
	
	
	
	boolean vfresh = false;
	boolean oddskip = false;
	/*yyy NN YYYYY XXXXX
	||| || ||||| +++++-- coarse X scroll
	||| || +++++-------- coarse Y scroll
	||| ++-------------- nametable select
	+++----------------- fine Y scroll*/
	public int v,t,x,w;
	Renderer renderer;
	NesDisplay display;
	int[] pixels;
	int pixelnum;
	//registers	
	Mapper map;
	//Memory mem;
	int framec;
	int tempX;
	int tv;
	public ppu2C02(Mapper m,NesDisplay disp) {
		//mem = new Memory(0);
		map = m;
		display = disp;
		pixels = new int[256*240];
		maskpixels= new int[256*240];
		framec=0;
		scanline = 0;
		pcycle = 0;
		scanlinephase = 0;
		ptablemap0=0;
		//PPUSTATUS_so=true;
		//PPUSTATUS_vb=true;
		renderer= new Renderer();
	}
	public void setmapper(Mapper m){
		map = m;
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
			break;
		case 0x2001:
			PPUMASK=Byte.toUnsignedInt(b);
			PPUMASK_grey = (b&1)==0?false:true;
			PPUMASK_bl = (b&2)==0?false:true;
			PPUMASK_sl = (b&4)==0?false:true;
			PPUMASK_sb = (b&8)==0?false:true;
			//System.out.println("Setting background to "+PPUMASK_sb);
			PPUMASK_ss = (b&16)==0?false:true;
			PPUMASK_colorbits = (b&0b11100000)>>>5;
			break;
		case 0x2003:
			//System.out.println("Setting OAMADDR to: "+Byte.toUnsignedInt(b));
			OAMADDR = b;
			break;
		case 0x2004:
			//OAMDATA = Byte.toUnsignedInt(b);
			//System.out.println("Writing :"+b+" at: "+Byte.toUnsignedInt(OAMADDR));
			map.ppuwriteoam(Byte.toUnsignedInt(OAMADDR), b);
			OAMADDR++;
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
			if(!(scanline==241&&pcycle==2))
				b |= PPUSTATUS_vb?0x80:0; 
			if(scanline==241&&(pcycle==3||pcycle==4)){
				//System.out.println(PPUSTATUS_vb);
				map.cpu.nmi=false;
			}
			b|= (OPEN_BUS&0x1f);
			even=true;
			PPUSTATUS_vb = false;
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
		return (PPUMASK_ss||PPUMASK_sb);
	}
	boolean drawBG(){
		return PPUMASK_sb;
	}
	boolean drawSprites(){
		return PPUMASK_ss;
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
	
	int test =0;
	int tempx;
	public void getBG(){
		shiftreg8a |= (palettelatch>>1)&1;
		shiftreg8b |= (palettelatch)&1;
		switch((pcycle-1)&7){
		case 1:{//name table
			nametablebyte = Byte.toUnsignedInt(map.ppuread(0x2000|(v&0x0fff)))<<4;
			nametablebyte+=(PPUCTRL_bpta?0x1000:0);
		};break;
		case 3:{//attribute table
			tempx =0x23C0 | (v & 0x0C00) | ((v >> 4) & 0x38) | ((v >> 2) & 0x07);
			byte attbyte = map.ppuread(tempx);
			int sel = ((v & 2) >> 1) | ((v & 0x40) >> 5);
			switch (sel){
			case 0: atablebyte = Byte.toUnsignedInt(attbyte) & 3; break;
			case 1: atablebyte = (Byte.toUnsignedInt(attbyte)>>2) & 3; break;
			case 2: atablebyte = (Byte.toUnsignedInt(attbyte)>>4) & 3; break;
			case 3: atablebyte = (Byte.toUnsignedInt(attbyte)>>6) & 3; break;
			}
		};break;
		case 5:{//tile low
				ptablemap0 = Byte.toUnsignedInt(map.ppuread((nametablebyte+((v&0x7000)>>>12))));	
		};break;
		case 7:{//tile high
				ptablemap1 = Byte.toUnsignedInt(map.ppuread((nametablebyte)+8+((v&0x7000)>>>12)));
				
			//	dodebug=true;
			/*if(map.control.checkDebug()&&scanline>230)
				try {
					System.in.read();
	
					System.out.println("v: "+Integer.toHexString(v)
							+" NT: "+PPUCTRL_bna
							+" yfine: "+ ((v&0x7000)>12)
							+" nametablebyte: "+Integer.toHexString(nametablebyte)
							+" upper pt: "+PPUCTRL_bpta
							//+" NT attempt: " +Integer.toHexString((0x2000)|((v&0xFFF)%0x400))
							//+" atablebyte:"+atablebyte
							+" AtrributeB:"+Integer.toBinaryString(Byte.toUnsignedInt(map.ppuread(0x23C0 | (v & 0x0C00) | ((v >> 4) & 0x38) | ((v >> 2) & 0x07))))
							+" attbyte:"
							//+" pt attempt:"+Integer.toHexString(((nametablebyte<<4)+(v>>12)))
							+" ptablemap1:"+ptablemap1
							+" sr16a: "+shiftreg16a
							+" scanline: "+scanline
							+" pcycle: "+pcycle);
					map.printMemoryPPU(0x2ff0, 0x15);
				} catch (IOException e) {
					e.printStackTrace();
				}*/
			if(pcycle !=256){
				incx();
			}
			else
				incy();
			shiftreg16a |= ptablemap1;			
			shiftreg16b |= ptablemap0;
			palettelatch = atablebyte;
			
		};break;
		default:break;
		}
		if(pcycle>=321&&pcycle<=336){
			updateShiftRegisters();
		}
		
	}
	boolean thiscycle= false;
	boolean olda12;
	boolean cura12;
	public boolean doscanline;
	long start = 0;
	long stop = 0;
	void render(){
		map.cpu.doNMI= PPUCTRL_genNmi&&PPUSTATUS_vb;
		if(scanline<240){
			spriteEvaluation();
			//if(pcycle==0){}//idle
            if(dorender()){
            	if(((pcycle>=1 &&pcycle<=256)||(pcycle>=321&&pcycle<=336))){
            		getBG();
            	}
                else if(pcycle>257&&pcycle<=320){
                   	OAMADDR=0;
                }   
                else if(pcycle==257){
                  	v &=~0x41f;
                   	v|=t&0x41f;
                }
                if(pcycle ==260)//&&scanline>-1)
                	map.scanlinecounter();
            }
			if(pcycle<=256&&pcycle>=1&&scanline>=0)
				drawpixel();
			if(scanline == -1){
				if(pcycle==2){
					PPUSTATUS_vb = false;
					PPUSTATUS_sz = false;
					PPUSTATUS_so = false;
				}
				else if(pcycle >=280 && pcycle<=304 && dorender()){
					v = t;
				}
			}
			if(pcycle ==340){
				oldspritezero = spritezero;
				oldszhl=szhl;
				spritezero=false;
				szhl=-1;
			}
			//if(pcycle ==260&&dorender())//&&scanline>-1)
			//	map.scanlinecounter();

			
		}
		else if(scanline==241 &&pcycle == 1){
			PPUSTATUS_vb = true;
			renderer.buildFrame(pixels, maskpixels, 2);
			pixelnum = 0;
			
			stop = System.currentTimeMillis()-start;
			display.sendFrame(renderer.frame);
			if(stop<16)
				try {
					Thread.sleep(16-stop);
				} catch ( InterruptedException e){
					e.printStackTrace();
				}
				//System.out.println(stop+"ms");
			start = System.currentTimeMillis();
			
		}
		if(pcycle<340){
			pcycle++;
			if(!oddframe&&scanline==-1&&pcycle==340&&dorender()){
				oddskip = true;
				pcycle =0;
				scanline=0;			
			}
		}
		else if(pcycle ==340){
			if(scanline==260){
				oddframe=!oddframe;
				scanline = -1;
				pcycle=0;
			}
			else{
				scanline++;	
				pcycle = 0;
			}
		}	
	}
	void updateShiftRegisters(){
		shiftreg16a<<=1;
		shiftreg16b<<=1;
		shiftreg8a<<=1;
		shiftreg8b<<=1;	
	}
	int[] maskpixels;
	void drawpixel(){
		byte bgc = map.ppuread((v>=0x3f00&&v<=0x3fff&&!dorender())?v:0x3f00);
		byte bgp =0;
		int bx = 0;
		int left = (!PPUMASK_bl)?8:0;
		if(PPUMASK_sb&&left<pcycle){
			int x = (((shiftreg16a>>-fineX + 16)&1)<<1);
			x+=((shiftreg16b>>-fineX +16)&1);
			int y = ((shiftreg8a>>>-fineX+8)&1)<<1;
			y |= ((shiftreg8b>>>-fineX+8)&1)&1;
			int p = (y<<2)|x;
			bx = x;
			if(x!=0)
				bgp =map.ppuread(0x3f00+p);			
		}
		updateShiftRegisters();
		byte sp = bgc;
		int sx = 0;
		boolean spp=false;
		left = (!PPUMASK_sl||!PPUMASK_bl)?8:0;
		if(PPUMASK_ss){
			for(int i = 0;i<8;i++){
				if(spriteco[i]==0){
					x = (spritebm[i]&0x80)!=0?1:0;
					x<<=1;
					x=(spritebm[i]&0x8000)!=0?(x|1):(x);
					if(x>0){
						if(oldspritezero&&i==0&&(oldszhl==scanline)&&PPUMASK_sb&&bx!=0&&left<pcycle&&pcycle<256){
							PPUSTATUS_sz = true;
							oldspritezero=false;
						}
						
						spp = spritepriority[i];
						sx = x;
						sp =map.ppuread(0x3f10+4*spritepalette[i]+x);
						spritebm[i]&=0b0111111101111111;
						break;
					
					}
				}
			}
		}
		shiftSprites();	

		if(bx==0&&sx==0){
			maskpixels[pixelnum]= PPUMASK;
			pixels[pixelnum++] = Byte.toUnsignedInt(bgc);			
		}
		else if(bx==0&&sx>0&&PPUMASK_ss){
			maskpixels[pixelnum]= PPUMASK;
			pixels[pixelnum++] = Byte.toUnsignedInt(sp);
		}
		else if(bx>0&&sx==0){
			maskpixels[pixelnum]= PPUMASK;
			pixels[pixelnum++] = Byte.toUnsignedInt(bgp);
		}
		else if(spp&&PPUMASK_ss){
			maskpixels[pixelnum]= PPUMASK;
			pixels[pixelnum++] = Byte.toUnsignedInt(sp);
		}
		else{
			maskpixels[pixelnum]= PPUMASK;
			pixels[pixelnum++] = Byte.toUnsignedInt(bgp);
		}			
		
	}
	void shiftSprites(){
		for(int i = 0;i<8;i++){
			if(spriteco[i]==0){
				spritebm[i]&=0b0111111101111111;
				spritebm[i]<<=1;
			}
			else{
				spriteco[i]--;
			}
		}
	}
	private int inrange(int y){
		if((scanline)-(y)>=0)
			return scanline-(y);
		else
			return 20;
	}
	boolean dodebug=false;
	private int stage = 1;
	private int n,m = 0;
	int spritec = 0;
	int c3=0;
	boolean spriteeven=false;
	boolean cont = false;
	boolean oldspritezero;
	boolean spritezero;
	int oldszhl;
	int szhl;
	boolean doingSprites;
	public void spriteEvaluation(){
		if(pcycle>=1 &&pcycle<=64){
			if(pcycle==1)
				Arrays.fill(oambuffer,(byte) 0xff);
		}
		else if(pcycle>=65 &&pcycle<=256){
			oamsignal = false;
			int y = 0;
			switch(stage){//1
			case 1:
				//System.out.println("in stage 1: " +inrange(y));
				y=Byte.toUnsignedInt(map.ppureadoam(4*n));
				oambuffer[4*oamBCounter]=map.ppureadoam(4*n);
				if((PPUCTRL_ss?inrange(y)<16:inrange(y)<8)){//&&y<scanline){
					oambuffer[4*oamBCounter+1]=map.ppureadoam(4*n+1);
					oambuffer[4*oamBCounter+2]=map.ppureadoam(4*n+2);
					oambuffer[4*oamBCounter+3]=map.ppureadoam(4*n+3);
					oamBCounter++;
					if(n==0){
						spritezero= true;
						szhl = scanline+1;
					}
				}
				
				stage = 2;
				break;
			case 2: //2
				//System.out.println("in stage 2");
				if(n==63){
					n =0;
					cont = true;
				}
				else
					n++;
				if(cont){
					stage = 4;
				}
				else if(oamBCounter<8){
					stage = 1;
				}
				else if(oamBCounter ==8){
					//disable writes
					m=0;
					c3=0;
					stage = 3;
				}
				
				break;
			case 3: //3
				//System.out.println("in stage 3");
				y = Byte.toUnsignedInt(map.ppureadoam((4*n+m)));
				if((PPUCTRL_ss?inrange(y)<16:inrange(y)<8)){//&&y<scanline){
					if((PPUMASK_ss||PPUMASK_sb)&&y<240)
						PPUSTATUS_so = true;
					if(m==3){
						n++;
						m=0;
						c3++;
					}
					else{
						m++;
						c3++;
					}
				}
				else{
					if(n==63){
						stage = 4;
						n = 0;
						m = 0;
						c3++;
					}
					else{
						n++;
						c3++;
						if(m==3)
							m=0;
						else
							m++;
					}
				}
				
				break;
			case 4: //4
				//System.out.println("in stage 4");

				//do nothing forever
				oamBCounter=0;
				break;
			default: break;
			}
		}
		else if(pcycle>=257&&pcycle<=320){
			if(pcycle==257){
				oamBCounter=0;
				doingSprites=true;
			}
			switch((pcycle)%8){
			case 0:
				oamBCounter++;
				spritec++;
				break;
			case 4:
				if(oambuffer[4*oamBCounter]==0xff){
					spriteco[spritec]=0;
					spritepalette[spritec]=0;
					spritepriority[spritec]=false;
					spritebm[spritec]=0;
				}
				else{
					spriteco[spritec] = Byte.toUnsignedInt(oambuffer[4*oamBCounter+3]);
					byte b = oambuffer[4*oamBCounter+2];
					spritepalette[spritec] = b&3;
					spritepriority[spritec] = (b&32)>0?false:true;
					int y = inrange(Byte.toUnsignedInt(oambuffer[4*oamBCounter]));
					int tileindex;
					int temp = 0;
					if(PPUCTRL_ss){
						temp = Byte.toUnsignedInt(oambuffer[4*oamBCounter+1]);
						tileindex = (temp&1)*0x1000+(temp&0xfe)*16;
					}
					else{
						tileindex=Byte.toUnsignedInt(oambuffer[4*oamBCounter+1])<<4;
						tileindex+= PPUCTRL_spta?0x1000:0;
					}
					if(y>=8&&PPUCTRL_ss){
						tileindex+=0x10;
					}
					
					if((b&0x80)!=0){
						if(y<8&&PPUCTRL_ss)
							tileindex+=0x10;
						else if(y>=8&&PPUCTRL_ss){
							tileindex-=0x10;
						}
						y=y%8;
						tileindex+=((7)-y);
					}
					else{
							y%=8;
							tileindex+=y;
					}
					if((b&0x40)!=0){
						int z = Byte.toUnsignedInt(map.ppuread(tileindex));
						int flip = 0;
						flip|=z&1;z>>>=1;flip<<=1;
						flip|=z&1;z>>>=1;flip<<=1;
						flip|=z&1;z>>>=1;flip<<=1;
						flip|=z&1;z>>>=1;flip<<=1;
						flip|=z&1;z>>>=1;flip<<=1;
						flip|=z&1;z>>>=1;flip<<=1;
						flip|=z&1;z>>>=1;flip<<=1;
						flip|=z&1;
						x = flip<<8;
						
						z = Byte.toUnsignedInt(map.ppuread(tileindex+(8)));
						flip=0;
						flip|=z&1;z>>>=1;flip<<=1;
						flip|=z&1;z>>>=1;flip<<=1;
						flip|=z&1;z>>>=1;flip<<=1;
						flip|=z&1;z>>>=1;flip<<=1;
						flip|=z&1;z>>>=1;flip<<=1;
						flip|=z&1;z>>>=1;flip<<=1;
						flip|=z&1;z>>>=1;flip<<=1;
						flip|=z&1;
						//flip<<=1;
						x|=flip;
						spritebm[spritec]=x;
					}
					else
						spritebm[spritec] = (Byte.toUnsignedInt((map.ppuread(tileindex)))<<8)|Byte.toUnsignedInt((map.ppuread(tileindex+8)));
				}
				break;
			default: break;
			}
			
		}
		else{
			doingSprites=false;
			stage=1;
			n = 0;
			m =0;
			cont=false;
			oamBCounter=0;
			spritec=0;
		}
		/*if(dodebug){
		try {
			System.in.read();

			System.out.println("v: "+Integer.toHexString(v)
					+" yfine: "+ (v>>12)
					+" scanline: "+scanline
					+" pcycle: "+pcycle
					+" n: "+n
					+" Sprites found: "+oamBCounter);
			//debugSprites();
			System.out.println(Arrays.toString(oambuffer));
			System.out.println(Arrays.toString(map.ppu_oam));
		//	mem.printMemoryoam();
			//cpu.debug();
		} catch (IOException e) {
			e.printStackTrace();
		}
		}*/
	}
	
}
