package com;
import java.io.IOException;
import java.util.Arrays;

import mappers.Mapper;
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
	NTSC_Converter ntsc;
	byte[] pixels;
	int pixelnum;
	//registers	
	Mapper map;
	//Memory mem;
	int framec;
	int tempX;
	int tv;
	public ppu2C02(Mapper m) {
		//mem = new Memory(0);
		map = m;
		pixels = new byte[256*240];
		framec=0;
		scanline = 0;
		pcycle = 0;
		scanlinephase = 0;
		ptablemap0=0;
		//PPUSTATUS_so=true;
		//PPUSTATUS_vb=true;
		ntsc = new NTSC_Converter();
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
			
				//else
			//	map.cpu.doNMI=false;
					
			//PPUSTATUS&=0b01111111;
			even=true;
			PPUSTATUS_vb = false;
			OPEN_BUS = b;
			break;
		case 0x2004:
			OPEN_BUS = map.ppureadoam(Byte.toUnsignedInt(OAMADDR));
			if(dorender() && scanline<=240){
				if(pcycle<64)
					return (byte) 0xff;
				else if(pcycle <=256)
					return 0;
				else if(pcycle <320)
					return (byte) 0xff;
				else
					return oambuffer[0];
			}
			//if(dorender()&&pcycle<=65&&scanline<241)
			//	return (byte)0xff;
			//else{
				//System.out.println("reading :"+map.ppureadoam(Byte.toUnsignedInt(OAMADDR))+" at:"+Byte.toUnsignedInt(OAMADDR));
			//	return map.ppureadoam(Byte.toUnsignedInt(OAMADDR));
			//}
			break;
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
			//check(v);
			OPEN_BUS = b;
			//return OPEN_BUS;
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
		if ((v & 0x001F) == 31){ // if coarse X == 31
			v &= ~0x001F;         // coarse X = 0
			v ^= 0x0400;           // switch horizontal nametable
		}
		else
			v += 1;                // increment coarse X
	}
	private void incy(){
		if ((v & 0x7000) != 0x7000)        // if fine Y < 7
			  v += 0x1000;                      // increment fine Y
		else{
			  v &= ~0x7000;                     // fine Y = 0
			  int y = (v & 0x03E0) >> 5;       // let y = coarse Y
			  if (y == 29){
			    y = 0;                   // coarse Y = 0
			    v ^= 0x0800;                    // switch vertical nametable
			  }
			  else{
				  y =(y+1)&31;
			  }                      // increment coarse Y
			  v = (v & ~0x03E0) | (y << 5);
		}
	}

	/*void debugSprites(){
		System.out.println("Sprite 1: "+spritebm[0] +" x:"+spriteco[0]+" palette:"+spritela[0]+"\n"
				+"Sprite 2: "+spritebm[1] +" x:"+spriteco[1]+" palette:"+spritela[1]+"\n"
				+"Sprite 3: "+spritebm[2] +" x:"+spriteco[2]+" palette:"+spritela[2]+"\n"
				+"Sprite 4: "+spritebm[3] +" x:"+spriteco[3]+" palette:"+spritela[3]+"\n"
				+"Sprite 5: "+spritebm[4] +" x:"+spriteco[4]+" palette:"+spritela[4]+"\n"
				+"Sprite 6: "+spritebm[5] +" x:"+spriteco[5]+" palette:"+spritela[5]+"\n"
				+"Sprite 7: "+spritebm[6] +" x:"+spriteco[6]+" palette:"+spritela[6]+"\n"
				+"Sprite 8: "+spritebm[7] +" x:"+spriteco[7]+" palette:"+spritela[7]+"\n"
				);
	}*/
	int test =0;
	int tempx;
	void getBG(){
		//System.out.println("IM IN GETBG!");
		shiftreg8a |= (palettelatch>>1)&1;
		shiftreg8b |= (palettelatch)&1;
		switch((pcycle-1)&7){
		case 1:{//name table
			//nametablebyte = map.ppuread()
			//nametablebyte = (0x2000|(v&(0x400*PPUCTRL_bna)));
			//v= v&0b111001111111111;
			//v|=(PPUCTRL_bna&3)<<10;
			nametablebyte = Byte.toUnsignedInt(map.ppuread(0x2000|(v&0x0fff)))<<4;
			nametablebyte+=(PPUCTRL_bpta?0x1000:0);
		};break;
		case 3:{//attribute table
			tempx =0x23C0 | (v & 0x0C00) | ((v >> 4) & 0x38) | ((v >> 2) & 0x07);
			//atablebyte=Byte.toUnsignedInt(map.ppuread(tempx));
			//System.out.println("Attr location"+Integer.toHexString(tempx));
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
				ptablemap0 = Byte.toUnsignedInt(map.ppuread((nametablebyte+((v&0x7000)>>>12))));//
				//map.check(nametablebyte);

				
		};break;
		case 7:{//tile high
				ptablemap1 = Byte.toUnsignedInt(map.ppuread((nametablebyte)+8+((v&0x7000)>>>12)));
				
			//	dodebug=true;
			/*if(map.control.checkDebug())
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
							+" ptablemap0:"+ptablemap0
							//+" pt attempt:"+Integer.toHexString(((nametablebyte<<4)+(v>>12)))
							+" ptablemap1:"+ptablemap1
							+" sr16a: "+shiftreg16a
							+" scanline: "+scanline
							+" pcycle: "+pcycle);
				} catch (IOException e) {
					e.printStackTrace();
				}*/
			if(pcycle !=256){
				incx();
			}
			else
				incy();
			//check(v);
			//v = (v&0xf000)|((v&0xFFF)%0x400);
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
	void render(){
		//if(scanline>=0)
		//if(!(pcycle>=257&&pcycle<=320)||!dorender()||scanline==-1)
		//	map.check(v);
		
		if(PPUCTRL_genNmi&&PPUSTATUS_vb)
			map.cpu.doNMI=true;		
		else
			map.cpu.doNMI=false;
		if(scanline<240){
			if(scanline>=0)
				spriteEvaluation();
			if(pcycle==0){}//idle
			else if(((pcycle>=1 &&pcycle<=256)||(pcycle>=321&&pcycle<=336))&&dorender()){
				getBG();
			}
			else if(pcycle==257&&dorender()){
				v &=~0x41f;
				v|=t&0x41f;
			}
			else if(pcycle>257&&pcycle<=320&&dorender()){
				OAMADDR=0;
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
					//check(v);
				}
			}
			if(pcycle ==340){
				oldspritezero = spritezero;
				oldszhl=szhl;
				spritezero=false;
				szhl=-1;
			}
			if(pcycle ==260&&dorender())//&&scanline>-1)
				map.scanlinecounter();

			
		}
		else if(scanline ==240){
			//clear sprites
			
			
		}
		else if(scanline==241 &&pcycle == 1){
			//if(!block)
			PPUSTATUS_vb = true;
			//submit frame
			ntsc.makeframe(pixels);
			pixelnum = 0;
			vfresh=true;
		}
		//if(!dorender()||scanline>240){
		//	map.check(v);
		//}
		
		if(pcycle ==340){
			if(scanline==260){
				oddframe=!oddframe;
				scanline = -1;
				pcycle=0;
			}
			else{
				if(scanline<240){
					//map.check(0);
				}	
			scanline++;
			
			pcycle = 0;
			}
		}
		else if(!oddframe&&scanline==-1&&pcycle==339&&dorender()){
			oddskip = true;
			//map.check(0);
			pcycle =0;
			scanline=0;			
		}
		else
			pcycle++;
		//block=false;
		
		
	}
	void updateShiftRegisters(){
		shiftreg16a<<=1;
		shiftreg16b<<=1;
		shiftreg8a<<=1;
		shiftreg8b<<=1;	
	}
	void drawpixel(){
		//Needs to have priority added
		//byte[] bgc = ntsc.ntsc_to_rgb((map.ppuread(0x3f00)),PPUMASK);
		//byte[] bgp = new byte[0];
		byte bgc = map.ppuread((v>=0x3f00&&v<=0x3fff)?v:0x3f00);
		byte bgp =bgc;
		int bx = 0;
		int left = (!PPUMASK_bl)?8:0;
		if(PPUMASK_sb&&left<pcycle){
			int x = (((shiftreg16a>>-fineX + 16)&1)<<1);//((shiftreg16a>>>-fineX+8)&0x8000)>>14;
			x+=((shiftreg16b>>-fineX +16)&1);
			int y = ((shiftreg8a>>>-fineX+8)&1)<<1;
			y |= ((shiftreg8b>>>-fineX+8)&1)&1;
			int p = (y<<2)|x;
			bx = x;
			if(x!=0)
				bgp =map.ppuread(0x3f00+p);//ntsc.ntsc_to_rgb((map.ppuread(0x3f00+p)),PPUMASK);
				//bgp =ntsc.ntsc_to_rgb((map.ppuread(0x3f00+p)),PPUMASK);			
		}
		updateShiftRegisters();
		//byte[] sp = new byte[0];
		byte sp = bgc;
		int sx = 0;
		boolean spp=false;
		left = (!PPUMASK_sl||!PPUMASK_bl)?8:0;
		if(PPUMASK_ss){
			//System.out.println("Drawing a sprite!");
			//debugSprites();
			for(int i = 0;i<8;i++){
				if(spriteco[i]==0){
					//System.out.println("FOUND A SPRITE IN THE X RANGE");
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
						//sp =ntsc.ntsc_to_rgb((map.ppuread(0x3f10+4*spritepalette[i]+x)),PPUMASK);
						sp =map.ppuread(0x3f10+4*spritepalette[i]+x);//ntsc.ntsc_to_rgb((map.ppuread(0x3f10+4*getSC(i)+x)),PPUMASK);

						spritebm[i]&=0b0111111101111111;
						break;
					
					}
				}
			}
		}
		shiftSprites();	

		//byte[] finalp = new byte[0];
		if(bx==0&&sx==0){
			//finalp=bgc;
			pixels[pixelnum++] = bgc;
		}
		else if(bx==0&&sx>0&&PPUMASK_ss){
			//finalp=sp;
			pixels[pixelnum++] = sp;
		}
		else if(bx>0&&sx==0){
			//finalp = bgp;
			pixels[pixelnum++] = bgp;
		}
		else if(spp&&PPUMASK_ss){
			//finalp = sp;
			pixels[pixelnum++] = sp;
		}
		else{
			//finalp = bgp;
			pixels[pixelnum++] = bgp;
		}
		//ntsc.pushPixel(finalp, pcycle, scanline);
			
		
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
	void check(int x){
		
		cura12 = (x&0x1000)!=0?true:false;
		if(cura12&&(!olda12)){
			//System.out.println("v :"+Integer.toHexString(v));
			map.scanlinecounter();
		}
		olda12 = cura12;
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
	void spriteEvaluation(){
		//PPUSTATUS_sz=false;
		if(pcycle>=1 &&pcycle<=64){
			if(pcycle==1){
				Arrays.fill(oambuffer,(byte) 0xff);
				//oamsignal = true;
			}
			
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
			//getBG();
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
					if(oamBCounter==0&&dorender()){
						//System.out.println("Calling the empty one");
						//map.check(0x1000);
					}
				}
				else{
					spriteco[spritec] = Byte.toUnsignedInt(oambuffer[4*oamBCounter+3]);
					//spritela[spritec] = oambuffer[4*oamBCounter+2];
					byte b = oambuffer[4*oamBCounter+2];
					spritepalette[spritec] = b&3;
					spritepriority[spritec] = (b&32)>0?false:true;
					int y = inrange(Byte.toUnsignedInt(oambuffer[4*oamBCounter]));
					int tileindex;
					int temp = 0;
					if(PPUCTRL_spta){
						//System.out.println("Bumping at sL: "+scanline);
						//v|=0x1000;
					}
					if(PPUCTRL_ss){
						temp = Byte.toUnsignedInt(oambuffer[4*oamBCounter+1]);
						tileindex = (temp&1)*0x1000+(temp&0xfe)*16;
						//check(tileindex);
						
					}
					else{
						tileindex=Byte.toUnsignedInt(oambuffer[4*oamBCounter+1])<<4;
						tileindex+= PPUCTRL_spta?0x1000:0;
						if(PPUCTRL_spta){
							//System.out.println("Bumping at sL: "+scanline);
							//v|=0x1000;
						}
					}
					//check(tileindex);
					if(y>=8&&PPUCTRL_ss){
						tileindex+=0x10;
					}
					
					if((b&0x80)!=0){
						if(y<8&&PPUCTRL_ss)
							tileindex+=0x10;
						else if(y>=8&&PPUCTRL_ss){
							//System.out.println("in here");
							tileindex-=0x10;
						}
						y=y%8;
						tileindex+=((7)-y);
					}
					else{
							y%=8;
							tileindex+=y;
					}
					if(oamBCounter==0&&dorender()){
						//System.out.println("Scanline: "+scanline+" ti>0x1000:"+Integer.toHexString(tileindex)+ " v?"+Integer.toHexString(v) + " olda12: "+map.olda12+" Sprite 16?"+PPUCTRL_ss);
						//map.check(tileindex);
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
				//int x = Integer.reverse(Byte.toUnsignedInt(map.ppuread(tileindex)))>>>24;
				//spritebm[spritec] = (x<<8)|(Integer.reverse(Byte.toUnsignedInt(map.ppuread(tileindex+8)))>>>24);
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
