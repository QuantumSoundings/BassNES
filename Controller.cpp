#include "Controller.h"
#include "Mapper.h"
#include <cstdint>


uint8_t Controller::getControllerStatus() {
	int getNextKey = 0;
	if (nextKey>7)
		getNextKey = 1;
	else
		getNextKey = map->input[controllerNum][nextKey] ? 1 : 0;
	if (!strobe)
		nextKey++;
	output &= 0x11111110;
	output |= getNextKey;
	return output;
}
void Controller::inputRegister(uint8_t b) {
	if ((b & 1) == 1) {
		nextKey = 0;
		strobe = true;
	}
	else {
		strobe = false;
		nextKey = 0;
	}
}