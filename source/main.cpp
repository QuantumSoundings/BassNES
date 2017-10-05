//
// Created by Jordan on 9/29/2017.
//
//Launch config
// 0 - SDL, 1 - 3ds
#ifdef SDL
#include "sdl_wrapper\sdlwrapper.h"
#else
#include"3ds_wrapper\3dswrapper.h"
#include"core\CPU_6502.cpp"
#include"core\APU.cpp"
#include"core\ppu2C02.cpp"
#include"core\Controller.cpp"
#include"core\Mapper.cpp"
#endif


#ifdef main
#undef main
#endif
using namespace std;

int main( int argc, char* args[] ){
#ifdef SDL
	main_sdl();
#else
	main_3ds();
#endif
    return 0;
}