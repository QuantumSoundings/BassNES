#pragma once
#include <cstdint>
class Noise {
private:
	int lengthLookupTable[32] = {
		10, 254, 20, 2, 40, 4, 80, 6, 160, 8, 60, 10, 14, 12, 26, 14,
			12, 16, 24, 18, 48, 20, 96, 22, 192, 24, 72, 26, 16, 28, 32, 30 };
	int* location;
	int delayedChange;
	bool loop, constantVolume, mode, enable, eStart, block;
	int volume = 0, timer = 0, tCount = 0, decay = 0;
	int noiselookup[16] = {
		4, 8, 16, 32, 64, 96, 128, 160, 202, 254, 380, 508, 762, 1016, 2034, 4068 };

public:
	int lengthCount = 0;
	Noise(int* loc) {
		location = loc;
	}
	void registerWrite(int index, uint8_t b, int clock) {
		switch (index % 4) {
		case 0: {
			if (clock == 14195)
				delayedChange = (b & 16) != 0 ? 2 : 1;
			else
				loop = (b & 32) != 0;
			constantVolume = (b & 16) != 0;
			volume = b & 0xf;
			break;
		}
		case 1:

			break;
		case 2: {
			mode = (b & 0x80) != 0;
			int noiseperiod = b & 0xf;
			timer = noiselookup[noiseperiod] - 1;
			//System.out.println("Current Timer: "+timer+" New Timer: "+noiselookup[noiseperiod]);
			break;
		}
		case 3:{
			if (enable) {
				if (clock == 14915) {
					if (lengthCount == 0) {
						lengthCount = (b & 0xff) >> 3;
						lengthCount = lengthLookupTable[lengthCount];
						block = true;
					}
				}
				else {
					lengthCount = (b & 0xff) >> 3;
					lengthCount = lengthLookupTable[lengthCount];
				}
			}
			decay = volume;
			eStart = true;
			break;
		}
		default: break;
		}
	}
	int shiftreg = 0;
	void clockTimer() {
		if (tCount == 0) {
			int feedback;
			tCount = timer;
			if (mode)
				feedback = ((shiftreg >> 6) & 1) ^ (shiftreg & 1);
			else
				feedback = ((shiftreg >> 1) & 1) ^ (shiftreg & 1);
			shiftreg >>= 1;
			shiftreg |= (feedback << 14);
		}
		else
			tCount--;
		if (lengthCount == 0 || (shiftreg & 1) == 1)
			return;
		//if(constantVolume)
		//	AudioMixer.audioLevels[outputLocation]+=volume;
		//else
		*location += decay;
		return;
	}

	int eDivider = 0;
	void envelopeClock() {
		if (enable) {
			if (eStart) {
				eStart = false;
				decay = 15;
				eDivider = volume + 1;
			}
			else {
				if (eDivider == 0) {
					eDivider = volume + 1;
					if (decay == 0) {
						if (loop)
							decay = 15;

					}
					else
						decay--;
				}
				eDivider--;
			}
			if (constantVolume)
				decay = volume;

		}
	}
	void lengthClock() {
		if (enable && !block) {
			if (lengthCount != 0) {
				if (!loop)
					lengthCount--;
			}
		}
		if (delayedChange != 0) {
			loop = delayedChange == 2;
			delayedChange = 0;
		}
		block = false;
	}
	void disable_m() {
		enable = false;
		lengthCount = 0;
	}
	void enable_m() {
		enable = true;
	}
};