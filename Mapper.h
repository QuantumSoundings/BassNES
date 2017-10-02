//
// Created by Jordan on 9/29/2017.
//
#include <cstdint>
#include <vector>
#include <SDL.h>
#include "CPU_6502.h"
#include "ppu2C02.h"
#include "APU.h"
#include "Controller.h"
#include "render.h"
#ifndef BASSNES_MAPPER_H
#define BASSNES_MAPPER_H
class Mapper{
public:
	SDL_Window* display;
    ppu2C02* ppu;
    CPU_6502* cpu;
    APU* apu;
    Controller* control;
    Controller* control2;
	render* ren;
    uint8_t PRG_ROM[2][0x4000];
	uint8_t PRG_RAM[0x2000] = {0};
    uint8_t CHR_ROM[2][0x1000];
    uint8_t cpu_mmr[0x14];

	uint8_t cpu_ram[0x800] = {0};

	uint8_t ppu_oam[256] = {0};
    uint8_t ppu_internal_ram[2][0x400];
	uint8_t* nametables[4];
    uint8_t ppu_palette[32]= {0x09, 0x01, 0x00, 0x01, 0x00, 0x02, 0x02, 0x0D,0x08, 0x10, 0x08,
                            0x24, 0x00, 0x00, 0x04, 0x2C, 0x09, 0x01, 0x34,
                           0x03, 0x00, 0x04, 0x00, 0x14, 0x08, 0x3A, 0x00, 0x02, 0x00, 0x20,
                           0x2C, 0x08};

    bool blockppu_b=false;
    bool CHR_ram = false;
    uint8_t openbus=0;


    Mapper(){
        ppu = new ppu2C02(this);
        cpu = new CPU_6502(this);
        apu = new APU(this);
		ren = new render();
    };
    bool blockppu();
    void cpuwrite(int index,uint8_t b);
    void cpuwriteoam(int i,uint8_t x);
    uint8_t cpuread(int index);
    uint8_t cpureadu(int index);
    void controllerWrite(int index,uint8_t b);
    uint8_t controllerRead(int index);
    void ppuwrite(int index,uint8_t b);
    uint8_t ppuread(int index);
    uint8_t ppureadNT(int index);
    uint8_t ppureadPT(int index);
    uint8_t ppureadAT(int index);
    uint8_t ppureadoam(int index);
    void ppuwriteoam(int index, uint8_t b);
    uint8_t ppuregisterhandler(int index,uint8_t b,bool write);
    void cartridgeWrite(int index,uint8_t b);
    uint8_t cartridgeRead(int index);
    void scanlinecounter(){};

    void setchr(std::vector<uint8_t> chr);
    void setprg(std::vector<uint8_t> prg);
    void runcycle();

	void updateWindow();

};
#endif //BASSNES_MAPPER_H
