//
// Created by Jordan on 9/29/2017.
//
#pragma once
#include <cstdint>


class Mapper;
class Triangle;
class Pulse;
class Noise;
class DMC;
class APU{
private:
	double output[5] = {};
	
	double cyclespersample;
	int intcyclespersample;
	uint8_t buffer[1024];
	int bpointer = 0;
	double samplenum = 0.0;
	bool stepmode4 = true;
	int stepcycle;
	bool irqInhibit;
	bool frameInterrupt;
	bool doFrameStep;
	bool evenclock = false;
	int block;
	int stepNumber;
	int delay = -1;
	int framecounter;
	int cpucounter;
public:
	Triangle* triangle;
	Pulse* pulse1;
	Pulse* pulse2;
	Noise* noise;
	DMC* dmc;
	uint32_t dev;
	int samplecounter = 0;
    long cyclenum=0;
    Mapper* map;
	APU(Mapper* m);
	void setSampleRate(int rate);
	void writeRegister(int index, uint8_t b);
	uint8_t readRegisters(int index);
	void mixAudio();
	void frameClock();
	void setIRQ();
	void doCycle();
};
