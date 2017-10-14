//
// Created by Jordan on 9/30/2017.
//

#include "ppu2C02.h"
#include "Mapper.h"
#include <iostream>
using namespace std;
ppu2C02::ppu2C02(Mapper* m){
    map = m;
	pixelnum = 0;
	finalscanline = 260;
}

void ppu2C02::writeRegisters(int index, uint8_t b) {
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
                map->cpu->setNMI(false);
            else
                map->cpu->setNMI(PPUCTRL_genNmi&&PPUSTATUS_vb);

            break;
        case 1:
            PPUMASK=b;
            PPUMASK_grey = (b & 1) != 0;
            PPUMASK_bl = (b & 2) != 0;
            leftmask_b = PPUMASK_bl?0:8;
            PPUMASK_sl = (b & 4) != 0;
            leftmask_s = PPUMASK_sl?0:8;
            PPUMASK_sb = (b & 8) != 0;
            PPUMASK_ss = (b & 16) != 0;
            render_b = PPUMASK_ss||PPUMASK_sb;
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
                map->ppuwriteoam(OAMADDR, b);
                OAMADDR++;
            }
            else
                OAMADDR+=4;
            break;
        case 5:
            if (even){
                t&= ~0x1f;
                fineX = b&7;
                t|= b>>3;
                even = false;
            }
            else{
                t &=~0x7000;
                t|=((b&7)<<12);
                t&= ~0x3e0;
                t|= (b&0xf8)<<2;
                even = true;
            }
            break;
        case 6:
            if(even){
                t &=0xc0ff;
                t|= (b&0x3f)<<8;
                t&=0x3fff;
                even = false;
            }
            else{
                t &=0x7f00;
                t|= b;
                tv=v;
                v=t;
                if((v&0x1000)!=0&&(tv&0x1000)==0)
                    map->scanlinecounter();
                even = true;
            }
            break;
        case 7:
            tv=v;
            map->ppuwrite((v&0x3fff), b);
            if(!dorender()||(scanline>240&&scanline<=261))
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
                map->scanlinecounter();
            break;
    }
}
bool ppu2C02::dorender(){ return render_b; }
uint8_t ppu2C02::readRegister(int index) {
    uint8_t b = 0;
    tv=v;
    switch(index){
        case 2:
            b |= PPUSTATUS_so?0x20:0;
            b |= PPUSTATUS_sz?0x40:0;
            if(!(scanline==241&&(pcycle==0)))
                b |= PPUSTATUS_vb?0x80:0;
            if(scanline==241&&(pcycle==0||pcycle==1||pcycle==2))
                map->cpu->nmi=false;
            b|= (OPEN_BUS&0x1f);
            even=true;
            PPUSTATUS_vb = false;
            map->cpu->setNMI(false);
            OPEN_BUS = b;
            break;
        case 4:
            OPEN_BUS = map->ppureadoam(OAMADDR);
            if(dorender()&&pcycle<=65&&scanline<241)
                return 0xff;
            else{
                return map->ppureadoam(OAMADDR);
            }
        case 7:
            if((v&0x3fff)<0x3f00){
                b = PPUDATA_readbuffer;
                PPUDATA_readbuffer = map->ppuread((v&0x3fff));
            }
            else{
                PPUDATA_readbuffer = map->ppuread((v&0x3fff)-0x1000);
                b =map->ppuread((v&0x3fff));
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
                map->scanlinecounter();
            OPEN_BUS = b;
            break;
        default:
            return OPEN_BUS;
    }
    return OPEN_BUS;
}
void ppu2C02::incx() {
    if ((v & 0x001F) == 31){
        v &= ~0x001F;
        v ^= 0x0400;
    }
    else
        v += 1;
}
void ppu2C02::incy(){
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
inline void ppu2C02::fetchNT() {
	shiftreg16a = (shiftreg16a << 8) | ptablemap1;
	shiftreg16b = (shiftreg16b << 8) | ptablemap0;
	palettelatchold = palettelatchnew;
	palettelatchnew = (atablebyte << 2);
	nametablebyte = map->ppureadNT(0x2000 | (v & 0x0fff)) << 4;
	nametablebyte += (PPUCTRL_bpta ? 0x1000 : 0);
	cyclepart = 0;
}
inline void ppu2C02::fetchAT() {
	int tempx = 0x23C0 | (v & 0x0C00) | ((v >> 4) & 0x38) | ((v >> 2) & 0x07);
	uint8_t attbyte = map->ppureadAT(tempx);
	int sel = ((v & 2) >> 1) | ((v & 0x40) >> 5);
	atablebyte = ((0xff & attbyte) >> (sel * 2)) & 3;
}
inline void ppu2C02::fetchtb1() {
	nametablebyte = nametablebyte + ((v & 0x7000) >> 12);
	ptablemap0 = map->ppureadPT(nametablebyte);
}
inline void ppu2C02::fetchtb2() {
	ptablemap1 = map->ppureadPT(nametablebyte + 8);
}
inline void ppu2C02::fetchinc() {
	if (pcycle != 256)
		incx();
	else
		incy();
}
inline void ppu2C02::getBG(int x){
	++cyclepart;
	/*fetchNT();
	fetchAT();
	fetchtb1();
	fetchtb2();
	fetchinc();*/
	//(*this.*bgGet[pcycle & 7])();
    switch(x&7){
        case 0://name table
			fetchNT();
            break;
        case 1: break;
        case 2://attribute table
			fetchAT();
            break;
        case 3://tile low
			fetchtb1();
            break;
        case 4:break;
        case 5://tile high
			fetchtb2();
            break;
        case 6: break;
        case 7:
			fetchinc();
            break;
    }
}
void ppu2C02::doCycle(){
    if(pcycle<=339){
		++pcycle;
		if (scanline < 240)
			render();
    }
    else{
        
		pcycle = 0;
		if (scanline == finalscanline) {
			scanline = -1;
			oddframe = !oddframe;
			doneFrame = true;
		}
		else
			scanline++;
		if (scanline == -1) {
			PPUSTATUS_so = false;
			PPUSTATUS_sz = false;
		}
		else if (scanline == 241)
			genFrame();
		oldspritezero = spritezero;
		spritezero = false;

    }
    prevrender = render_b;
}
void ppu2C02::render(){
    int cycle = pcycle;
    if(cycle<=256){
		if (prevrender)//&&(cycle)%8==0)
            getBG(cycle-1);
        if(scanline>=0){
			 drawpixel();
            if((pcycle&1)==0)
                spriteEvaluationNew();
        }
        else if(pcycle==1){
            PPUSTATUS_vb = false;
            map->cpu->setNMI(false);
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
		if (prevrender)// && (cycle) % 8 == 0)
			getBG(cycle-1);
			//getBG(cycle);
			//getBG(cycle);
			//getBG(cycle);
		
    }
    else if(cycle==339||cycle==337){
        map->ppureadNT(v&0xfff);
        if(scanline==-1&&cycle==339&&!oddframe&&prevrender&&!palregion){
            pcycle=340;
        }
    }
}
void ppu2C02::genFrame(){
	PPUSTATUS_vb = true;
	//cout << " Finished with frame!" << endl;
	map->cpu->doNMI = PPUCTRL_genNmi;
	map->updateWindow();
	pixelnum = 0;
}
void ppu2C02::drawpixel(){
	
    if(render_b||(v&0x3f00)!=0x3f00){
        int backgroundcolor=0;
        int cycle = pcycle;
        int offset = 15-(fineX+cyclepart);
        if(PPUMASK_sb&&leftmask_b<cycle){
            int bit = (((shiftreg16a>>(offset-1))&2))|((shiftreg16b>>offset)&1);
            if(bit!=0)
                backgroundcolor =(offset>=8?palettelatchold:palettelatchnew)|bit;
        }
        if(PPUMASK_ss&&leftmask_s<cycle){
            for(int i = 0;i < numsprites;i++){
                int off = 7-(cycle-spriteco[i]-1);
                if(off>=0&&off<8){
                    int bit =  (((spritebm[i]>>(off))&1)<<1)|((spritebm[i]>>((off)+8))&1);
                    if(bit!=0){
                        if(oldspritezero&&!PPUSTATUS_sz&&i==0&&PPUMASK_sb&&backgroundcolor!=0&&cycle<256){
                            PPUSTATUS_sz = true;
                        }
                        if((spritepriority[i]||backgroundcolor==0)){
                            pixels[pixelnum++] = (PPUMASK_colorbits)|(map->ppu_palette[0x10+4*spritepalette[i]+bit]);
                            return;
                        }
                        break;
                    }
                }
            }
        }
        pixels[pixelnum++] = (map->ppu_palette[backgroundcolor]);
    }
    else
        pixels[pixelnum++] = map->ppuread(v);
	//cyclepart = (++cyclepart) % 8;
}

int ppu2C02::inrange(int y){
    int x = scanline-y;
    if(x>=0)
        return x;
    else
        return 20;
}
void ppu2C02::stage2(){
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
void ppu2C02::stage3(){
    int y = map->ppureadoam((4*n+m));
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
void ppu2C02::spriteEvaluationNew() {
    if (pcycle < 65) {
        if (pcycle == 2) {
            for(int i = 0; i<32;i++){
                oambuffer[i] = 0xff;
            }
            stage = 1;
            m = 0;
            n = 0;
            oamBCounter = 0;
            spritec = 0;
        }
        return;
    } else if (pcycle <= 256) {
        switch (stage) {
            case 1:{ // first write of stage 1.
                int y = map->ppu_oam[n * 4];//ppureadoam(4*n));
                oambuffer[4 * oamBCounter] = y;
                if (PPUCTRL_ss ? inrange(y) < 16 : inrange(y) < 8)
                    stage = 2; // Continue writing sprite data
                else {
                    oambuffer[4 * oamBCounter] = 0xff;
                    stage2(); // move on to next sprite
                }
                return;}
            case 2:{//second write of stage 1
                oambuffer[4 * oamBCounter + 1] = map->ppu_oam[4 * n + 1];
                stage = 3;
                return;}
            case 3:{//third write of stage 1
                oambuffer[4 * oamBCounter + 2] = map->ppu_oam[4 * n + 2];
                stage = 4;
                return;}
            case 4:{//fourth write of stage 1
                oambuffer[4 * oamBCounter + 3] = map->ppu_oam[4 * n + 3];
                oamBCounter++;
                if (n == 0) {
                    spritezero = true;
                }
                stage2();//move on the next sprite
                return;}
            case 5://stage 2
                return;
            case 6://stage 3
                stage3();
                return;
            case 7://stage 4 do nothing
                return;
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
                return;
        }
    }
}
void ppu2C02::loadSprites(){
    //if(Byte.toUnsignedInt(oambuffer[4*oamBCounter])!=0xff){
    spriteco[spritec] = oambuffer[4*oamBCounter+3];
    uint8_t attributes = oambuffer[4*oamBCounter+2];
    spritepalette[spritec] = attributes&3;
    spritepriority[spritec] = (attributes & 32) <= 0;
    int y = inrange(oambuffer[4*oamBCounter]);
    int tileindex;
    if(PPUCTRL_ss){//get index number for different sprite sizes
        int temp = oambuffer[4*oamBCounter+1];
        tileindex = (temp&1)*0x1000+(temp&0xfe)*16;
        if(y>=8)//correct it for 8x16 sprites
            tileindex+=0x10;
    }
    else{
        tileindex=oambuffer[4*oamBCounter+1]<<4;
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
        spritebm[spritec] = (BitReverseTable256[map->ppureadPT(tileindex)]<<8)|(BitReverseTable256[map->ppureadPT(tileindex+8)]);
    else
        spritebm[spritec] = ((map->ppureadPT(tileindex))<<8)|(map->ppureadPT(tileindex+8));
    spritefetch=false;
    if(tileindex<0)tileindex*=-1;
    //spritehorizontal[spritec]=(attributes&0x40)!=0;
    //spritebm[spritec] = (Byte.toUnsignedInt((map.ppuread(tileindex)))<<8)|Byte.toUnsignedInt((map.ppuread(tileindex+8)));
    if(scanline>=0&&oambuffer[4*oamBCounter]!=0xff)
        numsprites++;
    spritec++;
    oamBCounter++;
    return;
    //}
}