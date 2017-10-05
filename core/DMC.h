#pragma once
class DMC {
public:
	bool irqflag = false;
	bool silence = false;
	bool sampleremaining = 0;
	void registerWrite(int index, uint8_t b){};
	void clearFlag() {};
	void restart() {};
	void clockTimer() {};
};