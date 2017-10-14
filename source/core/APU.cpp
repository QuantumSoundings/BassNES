#include "APU.h"
#include "Mapper.h"
#include "Pulse.h"
#include "Triangle.h"
#include "Noise.h"
#include "DMC.h"
#include "CPU_6502.h"
#include <iostream>
#ifdef SDL
#include <SDL.h>
#endif
APU::APU(Mapper* m){
	pulse1 = new Pulse(true, &output[0]);
	pulse2 = new Pulse(false, &output[1]);
	triangle = new Triangle(&output[2]);
	noise = new Noise(&output[3]);
	dmc = new DMC();
	cyclenum = 0;
	map = m;
	cyclespersample = 1789773.0 / 44100;
	intcyclespersample = (int)cyclespersample;
	cpucounter = 10;
	writeRegister(0x4015, 0);
}

void APU::writeRegister(int index, uint8_t b) {
	if (index >= 0x4000 && index<0x4004) {
		pulse1->registerWrite(index, b, cpucounter);
	}
	else if (index >= 0x4004 && index<0x4008) {
		pulse2->registerWrite(index, b, cpucounter);
	}
	else if (index >= 0x4008 && index<0x400c) {
		triangle->registerWrite(index, b, cpucounter);
	}
	else if (index >= 0x400c && index<0x4010) {
		noise->registerWrite(index, b, cpucounter);
	}
	else if (index >= 0x4010 && index<0x4014) {
		dmc->registerWrite(index, b);
	}
	else if (index == 0x4015) {
		if ((b & 1) == 0)
			pulse1->disable_m();
		else
			pulse1->enable_m();
		if ((b & 2) == 0)
			pulse2->disable_m();
		else
			pulse2->enable_m();
		if ((b & 4) == 0)
			triangle->disable_m();
		else
			triangle->enable_m();
		if ((b & 8) == 0)
			noise->disable_m();
		else
			noise->enable_m();
		if ((b & 16) == 0) {
			dmc->sampleremaining = 0;
			dmc->silence = true;
		}
		else {
			if (dmc->sampleremaining == 0) {
				dmc->restart();
			}
		}
		dmc->clearFlag();
	}
	else if (index == 0x4017) {
		stepmode4 = (b & 0x80) == 0;
		stepNumber = 0;
		if ((b & 0x80) != 0) {
			stepNumber = 2;
			frameClock();
			cpucounter = 0;
			block = 1;
		}
		irqInhibit = (b & 0x40) != 0;
		if (irqInhibit&&frameInterrupt) {
			frameInterrupt = false;
			map->cpu->removeIRQ(CPU_6502::FrameCounter);
		}
		if (!evenclock)
			delay = 0;
		else
			delay = 1;
		cpucounter = 0;
	}
}
uint8_t APU::readRegisters(int index) {
	if (index == 0x4015) {
		uint8_t b = 0;
		b |= pulse1->lengthCount >0 ? 1 : 0;
		b |= pulse2->lengthCount >0 ? 2 : 0;
		b |= triangle->lengthCount >0 ? 4 : 0;
		b |= noise->lengthCount >0 ? 8 : 0;
		b |= dmc->sampleremaining>0 ? 16 : 0;
		b |= frameInterrupt ? 64 : 0;
		frameInterrupt = false;
		map->cpu->removeIRQ(CPU_6502::FrameCounter);
		b |= dmc->irqflag ? 128 : 0;
		return b;
	}
	return 0;
}
void APU::mixAudio() {
	output[0] /= samplecounter;
	output[1] /= samplecounter;
	output[2] /= samplecounter;
	output[3] /= samplecounter;
	output[4] /= samplecounter;
	double pulse_out = (95.88 / (8128.0 / (output[0] + output[1]) + 100));
	double tnd_out = (163.67 / (24329.0 / (3 * output[2]+ 2 * output[3] + output[4]) + 100));//0.00851*t + 0.00494*n + 0.00335*d;
	double out = pulse_out + tnd_out;
	out *= 250;
	int sample = (int)out;
	buffer[bpointer] = sample;
	bpointer++;
	//cout <<dec<< "added sample to buffer " <<(int)sample<< endl;
	
	if (bpointer >= 1024) {
#ifdef SDL
		//cout << "queueing audio" << endl;
		if(SDL_GetQueuedAudioSize(dev)<20000)
			SDL_QueueAudio(dev, buffer, 1024);
		
#endif
		bpointer = 0;
	}
	output[0] = 0;
	output[1] = 0;
	output[2] = 0;
	output[3] = 0;
	output[4] = 0;

}
void APU::frameClock() {
	if (stepmode4) {
		if (stepNumber % 4 == 1 || stepNumber % 4 == 3) {
			pulse1->lengthClock();
			pulse1->sweepClock();
			pulse2->lengthClock();
			pulse2->sweepClock();
			triangle->lengthClock();
			noise->lengthClock();
		}

		pulse1->envelopeClock();
		pulse2->envelopeClock();
		triangle->linearClock();
		noise->envelopeClock();
		if (stepNumber % 4 == 3) {
			setIRQ();
		}
	}
	else {
		if (stepNumber == 2) {
			pulse1->lengthClock();
			pulse1->sweepClock();
			pulse2->lengthClock();
			pulse2->sweepClock();
			triangle->lengthClock();
			noise->lengthClock();

			pulse1->envelopeClock();
			pulse2->envelopeClock();
			triangle->linearClock();
			noise->envelopeClock();
		}
		else if (stepNumber == 1) {
			pulse1->envelopeClock();
			pulse2->envelopeClock();
			triangle->linearClock();
			noise->envelopeClock();
		}
		else {}
	}
}
void APU::setIRQ() {
	if (!irqInhibit) {
		frameInterrupt = true;
		map->cpu->setIRQ(CPU_6502::FrameCounter);
	}
}
void APU::doCycle() {
	if (delay>0) {
		delay--;
	}
	else if (delay == 0) {
		cpucounter = 0;
		delay = -1;
	}

	triangle->clockTimer();
	noise->clockTimer();
	dmc->clockTimer();
	if (evenclock) {
		pulse1->clockTimer();
		pulse2->clockTimer();
		cyclenum++;
	}
	evenclock = !evenclock;
	//if (expansion)
	//	for (Channel chan : expansionAudio)
	//		chan.clockTimer();
	if (stepmode4) {
		switch (cpucounter) {
		case 7459: stepNumber = 0; frameClock(); break;
		case 14915:stepNumber = 1; frameClock(); break;
		case 22373:stepNumber = 2; frameClock(); break;
		case 29830:
			setIRQ(); break;
		case 29831:stepNumber = 3; frameClock(); break;
		case 29832:setIRQ(); break;
		case 37289:stepNumber = 0; frameClock(); cpucounter = 7459; break;
		default: break;
		}
	}
	else {
		switch (cpucounter) {
		case 1:
			if (block <= 0) {
				stepNumber = 2;
				frameClock();
			}
			else
				block--;
			break;
		case 7459:  stepNumber = 1; frameClock(); break;
		case 14915: stepNumber = 2; frameClock(); break;
		case 22373: stepNumber = 1; frameClock(); break;
		case 29829: break;
		case 37283: stepNumber = 2; frameClock(); cpucounter = 1; break;
		default:break;
		}
	}
	cpucounter++;
	samplenum++;
	samplecounter++;
	//cout << "samplenum: " <<samplenum<< " cyclesper: "<<cyclespersample<<" the math: "<<samplenum-cyclespersample<< endl;
	//if (NesSettings.highQualitySampling)
	//	mixer.mixHighQualitySample();
	//else 
	if ((samplenum - cyclespersample)>0) {
		//cout << "in the if" << endl;
		mixAudio();
		samplecounter = 0;
		samplenum -= cyclespersample;
	}
}