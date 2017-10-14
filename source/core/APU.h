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
	int output[5] = {};
	double cyclespersample;
	int intcyclespersample;
	double samplenum;
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
	int samplecounter = 0;
    long cyclenum=0;
    Mapper* map;
	APU(Mapper* m);
	void setSampleRate(int rate);
	void writeRegister(int index, uint8_t b);
	uint8_t readRegisters(int index);
	void frameClock();
	void setIRQ();
	void doCycle();
};
