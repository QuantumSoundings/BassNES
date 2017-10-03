//
// Created by Jordan on 9/29/2017.
//
//Launch config
// 0 - SDL, 1 - 3ds
#define DS3 1
#define SDL 0
#define PLATFORM_TARGET SDL
#if PLATFORM_TARGET == SDL
#include "sdlwrapper.h"
#
#elif PLATFORM_TARGET == DS3
#endif


#ifdef main
#undef main
#endif
using namespace std;

int main( int argc, char* args[] ){
#if PLATFORM_TARGET == SDL
	main_sdl();
#elif PLATFORM_TARGET == DS3
	main_3ds();
#endif
    return 0;
}