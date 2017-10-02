//
// Created by Jordan on 9/30/2017.
//
#include <iostream>
#include <cassert>
#include "Mapper.h"
using namespace std;
void Mapper::runcycle() {
    cpu->run_cycle();
	apu->doCycle();
    ppu->doCycle();
    ppu->doCycle();
    ppu->doCycle();
}
void Mapper::updateWindow() {
	SDL_LockSurface(SDL_GetWindowSurface(display));
	ren->buildImageRGBnoEmp(ppu->pixels);
	//SDL_Delay(50);
	SDL_UnlockSurface(SDL_GetWindowSurface(display));
	assert(SDL_UpdateWindowSurface(display)==0);
}
void Mapper::setNameTable(Mirror mirroringType) {
	switch (mirroringType) {
	case Horizontal:
		nametables[0] = ppu_internal_ram[0];
		nametables[1] = ppu_internal_ram[0];
		nametables[2] = ppu_internal_ram[1];
		nametables[3] = ppu_internal_ram[1];
		break;
	case Vertical:
		nametables[0] = ppu_internal_ram[0];
		nametables[1] = ppu_internal_ram[1];
		nametables[2] = ppu_internal_ram[0];
		nametables[3] = ppu_internal_ram[1];
		break;
	}
}
void Mapper::setMirror(int i) {
	if (i==0)
		setNameTable(Horizontal);
	else
		setNameTable(Vertical);
}
void Mapper::setprg(std::vector<uint8_t> prg) {
    if(prg.size()==16384*2){
        for(int i = 0; i<0x4000;i++) {
            PRG_ROM[0][i] = prg[i];
            PRG_ROM[1][i] = prg[0x4000+i];
        }
    }
    else{
        for(int i = 0; i<0x4000;i++) {
            PRG_ROM[0][i] = prg[i];
            PRG_ROM[1][i] = prg[i];
        }
    }
}
void Mapper::setchr(std::vector<uint8_t> chr) {
    if(chr.size()==0)
        CHR_ram=true;
    else{
        for(int i = 0; i<0x1000;i++){
            CHR_ROM[0][i] = chr[i];
            CHR_ROM[1][i] = chr[0x1000+i];
        }
    }
	nametables[0] = ppu_internal_ram[0];
	nametables[1] = ppu_internal_ram[0];
	nametables[2] = ppu_internal_ram[1];
	nametables[3] = ppu_internal_ram[1];
}
bool Mapper::blockppu() {return (*apu).cyclenum>14700;}
void Mapper::cpuwrite(int index, uint8_t b) {
    if(index<0x2000)
        cpu_ram[index%0x800] = b;
    else if(index<0x4000)
        ppuregisterhandler(index%8,b,true);
    else if(index<=0x4017){
        if(index==0x4014){
            cpu_mmr[0x14]=b;
            cpu->dxx=b<<8;
            cpu->writeDMA = true;
        }
        else if(index==0x4016)
            controllerWrite(index,b);
        else if(index<0x4013)
            apu->writeRegister(index,b);
        else if(index==0x4015||index==0x4017)
            apu->writeRegister(index,b);
        openbus = b;
    }
    else
        cartridgeWrite(index,b);
}
void Mapper::cpuwriteoam(int index,uint8_t b){ ppu_oam[index]=b; }
uint8_t Mapper::cpuread(int index) {
    if(index<0x2000)
        return cpu_ram[index%0x800];
    else if(index>=0x2000 && index<0x4000)
        return ppuregisterhandler(index%8,0,false);
    else if(index>=0x4000 && index<=0x40ff){
        if(index ==0x4015){
            return apu->readRegisters(index);
        }
        if(index ==0x4016)
            return ((openbus&0b11100000)|control->getControllerStatus());
        else if(index==0x4017)
            return ((openbus&0b11100000)|control2->getControllerStatus());
        return openbus;
    }
    else
        return cartridgeRead(index);
}
uint8_t Mapper::cpureadu(int index){return (uint8_t)cpuread(index);}
uint8_t Mapper::controllerRead(int index){
    if(index ==0x4016)
        return control->getControllerStatus();
    else
        return control2->getControllerStatus();
}
void Mapper::controllerWrite(int index, uint8_t b) {
    control->inputRegister(b);
    control2->inputRegister(b);
}
void Mapper::ppuwrite(int index, uint8_t b) {
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
uint8_t Mapper::ppuread(int index) {
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
uint8_t Mapper::ppureadNT(int index) {
    index&=0xfff;
    return nametables[index/0x400][index%0x400];
}
uint8_t Mapper::ppureadPT(int index) {return CHR_ROM[(index&0x1000)!=0?1:0][index%0x1000];}
uint8_t Mapper::ppureadAT(int index) {return ppureadNT(index);}
uint8_t Mapper::ppureadoam(int index) {return ppu_oam[index%256];}
void Mapper::ppuwriteoam(int index, uint8_t b) {ppu_oam[index]=b;}
uint8_t Mapper::ppuregisterhandler(int index, uint8_t x, bool write) {
    switch(index){
        case 0:
            if(write&&blockppu())
                ppu->writeRegisters(index, x);
            else if(write&&!blockppu())
                ppu->OPEN_BUS=x;
            else
                return ppu->OPEN_BUS;
            break;
        case 1:
            if(write&&blockppu())
                ppu->writeRegisters(index, x);
            else if(write&&!blockppu())
                ppu->OPEN_BUS=x;
            else
                return ppu->OPEN_BUS;
            break;
        case 2:
            if(write)
                ppu->OPEN_BUS=x;
            else
                return ppu->readRegister(index);
            break;
        case 3:
            if(write)
                ppu->writeRegisters(index, x);
            else
                return ppu->OPEN_BUS;
            break;
        case 4:
            if(write)
                ppu->writeRegisters(index, x);
            else
                return ppu->readRegister(index);
            break;
        case 5:
            if(write&&blockppu())
                ppu->writeRegisters(index, x);
            else if(write&&!blockppu())
                ppu->OPEN_BUS=x;
            else
                return ppu->OPEN_BUS;
            break;
        case 6:
            if(write&&blockppu())
                ppu->writeRegisters(index, x);
            else if(write&&!blockppu())
                ppu->OPEN_BUS=x;
            else
                return ppu->OPEN_BUS;
            break;
        case 7:
            if(write)
                ppu->writeRegisters(index, x);
            else
                return ppu->readRegister(index);
            break;
    }
    return 0;
}
void Mapper::cartridgeWrite(int index, uint8_t b) {
    if(index<0x8000&&index>=0x6000)
        PRG_RAM[index-0x6000]=b;
}
uint8_t Mapper::cartridgeRead(int index) {
    if(index<0x8000&&index>=0x6000)
        return PRG_RAM[index-0x6000];
    else if(index<0xc000)
        return PRG_ROM[0][index%0x4000];
    else
        return PRG_ROM[1][index%0x4000];
}
