#include "..\core\Mapper.h"
#include <3ds.h>
#include <stdlib.h>
#include <stdio.h>
#include <vector>
#include <time.h>
#include <cassert>
#define SAMPLERATE 44100
#define SAMPLESPERBUF (SAMPLERATE / 30)
#define BYTESPERSAMPLE 2
using namespace std;
vector<char> ReadAllBytes(u8* ptr, off_t size) {
	vector<char> out;
	for (int i = 0; i < size; i++) {
		out.push_back((char)ptr[i]);
	}
	return out;
}
int main_3ds() {
	gfxInitDefault();
	gfxSetDoubleBuffering(GFX_TOP, false);
	gfxSetScreenFormat(GFX_TOP, GSP_RGBA8_OES);
	u8* fb = gfxGetFramebuffer(GFX_TOP, GFX_LEFT, NULL, NULL);
	consoleInit(GFX_BOTTOM, NULL);
	printf("Hello 3ds world\n");

	FILE* file = fopen("sdmc:/3ds/dkj.nes", "rb");
	Mapper* map = new Mapper();
	off_t bytesRead = 1;
	off_t size = 3;
	u8* buffer;
	bool read, finishedSetup;
	if (file == NULL) {
		printf("File is null or missing!");
	}
	else {
		fseek(file, 0, SEEK_END);
		size = ftell(file);
		fseek(file, 0, SEEK_SET);
		buffer = (u8*)malloc(size);
		if (buffer) {
			bytesRead = fread(buffer, 1, size, file);
			read = true;
		}
	}
	fclose(file);
	if (size == bytesRead&&read)
		printf("Successfully read the file!");
	if (size == bytesRead) {

		vector<char> data = ReadAllBytes(buffer, size);
		printf("Successfully Read the data! Now making mapper...\n");

		char prg, chr;
		assert(data[0] == 'N');
		assert(data[1] == 'E');
		assert(data[2] == 'S');
		assert(data[3] == 0x1a);
		printf("Verified file data as NES format!");

		prg = data[4];
		chr = data[5];
		map->setMirror((int)(data[6] & 1));
		vector<uint8_t> prgrom(16384 * prg, 0);
		vector<uint8_t> chrrom(8192 * chr, 0);
		int count = 0;
		vector<char>::iterator iter = data.begin();
		for (int i = 0; i < 16; i++)
			++iter;
		for (unsigned int i = 0; i < prgrom.size(); i++) {
			prgrom[i] = (*iter);
			++iter;
			count++;
		}
		printf("PRG READ :%d\n", count);
		for (unsigned int i = 0; i < chrrom.size(); i++) {
			chrrom[i] = (*iter);
			++iter;
		}
		map->setchr(chrrom);
		map->setprg(prgrom);
		map->cpu->program_counter = map->cpuread(0xfffd);
		map->cpu->program_counter <<= 8;
		map->cpu->program_counter |= map->cpuread(0xfffc);
		printf("Starting PC: %d\n", map->cpu->program_counter);

		printf("Mapper is setup! Ready to Run\n");
		finishedSetup = true;
		map->ren->colorized = (int*)fb;
	}

	int fps = 0;
	time_t oldTime, unixTime;
	while (aptMainLoop()) {
		//gspWaitForVBlank();
		//Grab input

		//fillbuffer((char*)(&map->ren->colorized),fb);
		hidScanInput();
		unixTime = time(NULL);
		if (oldTime != unixTime) {
			oldTime = unixTime;
			printf("Fps is %i \n", fps);
			fps = 1;		
		}
		else
			++fps;

		//struct tm* timeStruct = gmtime((const time_t *)& unixTime);
		u32 kDown = hidKeysDown();
		u32 okDown = hidKeysHeld();
		if (okDown & KEY_L &&okDown &KEY_R)
			break;
		if (finishedSetup) {
			map->input[0][0] = (okDown & KEY_A) | (kDown & KEY_A);
			map->input[0][1] = (okDown & KEY_B) | (kDown & KEY_B);
			map->input[0][2] = (okDown & KEY_SELECT) | (kDown & KEY_SELECT);
			map->input[0][3] = (okDown & KEY_START) | (kDown & KEY_START);
			map->input[0][4] = (okDown & KEY_UP) | (kDown & KEY_UP);
			map->input[0][5] = (okDown & KEY_DOWN) | (kDown & KEY_DOWN);
			map->input[0][6] = (okDown & KEY_LEFT) | (kDown & KEY_LEFT);
			map->input[0][7] = (okDown & KEY_RIGHT) | (kDown & KEY_RIGHT);
			map->runFrame();
		}
		//printf("Frame time is %d  \n", unixTime);
		//gfxFlushBuffers();
		//gfxSwapBuffers();
	}

	gfxExit();
	return 0;
}