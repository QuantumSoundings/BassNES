#include "Mapper.h"
#include <3ds.h>
#include <stdlib.h>
#include <stdio.h>
#include <vector>
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
void fillbuffer(char* pixels, u8* buffer) {
	for (int i = 0; i<61440; i++) {
		buffer[i] = RGB565(pixels[i * 4 + 1], pixels[i * 4 + 2], pixels[i * 4 + 3]);
	}
}
int main_3ds() {
	gfxInitDefault();
	gfxSetDoubleBuffering(GFX_TOP, false);
	gfxSetScreenFormat(GFX_TOP, GSP_RGBA8_OES);
	u8* fb = gfxGetFramebuffer(GFX_TOP, GFX_LEFT, NULL, NULL);
	consoleInit(GFX_BOTTOM, NULL);
	printf("Hello 3ds world\n");

	FILE* file = fopen("sdmc:/3ds/smb.nes", "rb");
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
		char in;
		char* input = &in;
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


	while (aptMainLoop()) {
		gspWaitForVBlank();
		//Grab input
		map->runFrame();
		//fillbuffer((char*)(&map->ren->colorized),fb);
		hidScanInput();
		u32 kDown = hidKeysDown();
		if (kDown & KEY_START)
			break;
		//gfxFlushBuffers();
		gfxSwapBuffers();
	}

	gfxExit();
	return 0;
}