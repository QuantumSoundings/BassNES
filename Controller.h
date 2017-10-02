//
// Created by Jordan on 9/30/2017.
//
#include <cstdint>
#ifndef BASSNES_CONTROLLER_H
#define BASSNES_CONTROLLER_H
class Mapper;
class Controller{
public:
	Mapper* map;
	int controllerNum;
	bool strobe;
	int output;
	int keysPressed;
	int nextKey;
	Controller(Mapper* m, int num) {
		map = m;
		strobe = false;
		keysPressed = 0;
		controllerNum = num;
		nextKey = 0;
	}
	uint8_t getControllerStatus();
    void inputRegister(uint8_t b);
};
#endif //BASSNES_CONTROLLER_H
