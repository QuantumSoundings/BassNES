#pragma once
#include <cstdint>
class Pulse {
private:
	int lengthLookupTable[32] = {
		10, 254, 20, 2, 40, 4, 80, 6, 160, 8, 60, 10, 14, 12, 26, 14,
			12, 16, 24, 18, 48, 20, 96, 22, 192, 24, 72, 26, 16, 28, 32, 30};
	bool duty0[8] = { false,true,false,false,false,false,false,false };
	bool duty1[8] = { false,true,true,false,false,false,false,false };
	bool duty2[8] = { false,true,true,true,true,false,false,false };
	bool duty3[8] = { true,false,false,true,true,true,true,true };
	bool* current_duty = duty0;
	int duty;
	bool p1;
	int dutynumber = 7;

	//sweep
	bool doSweep;
	int targetPeriod;
	int sDivider;
	int dividerPeriod;
	bool sweepReload = false;
	int shift;
	bool negate;
	int* location;
	int tCount=0,timer=0,decay=0;
	bool constantVolume,delayedChange,loop,eStart,block,enable;
	int volume;
public:
	int lengthCount = 0;
	bool output;
	Pulse(bool number, int* loc) {
		location = loc;
		p1 = number;
	}
	void clockTimer() {
		if (tCount == 0) {
			tCount = timer;
			dutynumber++;
			output = current_duty[dutynumber % 8];
		}
		else
			tCount--;
		if (lengthCount == 0 || !output || decay == 0 || timer<8)
			return;
		*location += decay;
	}
	void registerWrite(int index, uint8_t b, int clock) {
		switch (index % 4) {
		case 0:{
			duty = b >> 6;
			switch (duty) {
			case 0: current_duty = duty0; break;
			case 1: current_duty = duty1; break;
			case 2: current_duty = duty2; break;
			case 3: current_duty = duty3; break;
			}
			if (clock == 14915) {
				delayedChange = (b & 0b10000) != 0 ? 2 : 1;
			}
			else
				loop = (b & 0b100000) != 0;
			constantVolume = (b & 0b10000) != 0;
			volume = b & 0xf;
			eStart = true;
			break;
		}
		case 1:{
			doSweep = (b & 0x80) != 0;
			if (!doSweep)
				targetPeriod = timer;
			dividerPeriod = (b & 0b1110000) >> 4;
			sDivider = dividerPeriod + 1;
			negate = (b & 0b1000) != 0;
			shift = (b & 0b111);
			sweepReload = true;
			break;
		}
		case 2:{
			timer &= 0xff00;
			timer |= (b & 0xff);
			targetPeriod = timer;
			break;
		}
		case 3:{
			int x = b >> 3;
			if (enable)
				if (clock == 14915) {
					if (lengthCount == 0) {
						lengthCount = lengthLookupTable[x];
						block = true;
					}
				}
				else
					lengthCount = lengthLookupTable[x];
			dutynumber = 0;
			timer &= 0b11111111;
			timer |= (b & 0b111) << 8;
			targetPeriod = timer;
			eStart = true;
		}
		}

	}
	void sweepClock() {
		if (enable) {
			if (doSweep) {
				if (sweepReload) {
					sDivider = dividerPeriod + 1;
					if (sDivider == 0)
						targetPeriod = timer;
					sweepReload = false;
				}
				else if (sDivider != 0) {
					sDivider--;
				}
				else {
					sDivider = dividerPeriod + 1;
					int change = targetPeriod >> shift;
					if (negate) {
						if (p1)
							targetPeriod = targetPeriod - change - 1;
						else
							targetPeriod = targetPeriod - change;
					}
					else
						targetPeriod = targetPeriod + change;
				}
				timer = targetPeriod & 0b111111111111;
			}
		}
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