#include "..\core\Mapper.h"
#include <SDL.h>
#include <iostream>
#include <cassert>
#include <fstream>
#include <vector>
using namespace std;
inline void printCPU(Mapper* map) {
	cout << hex;
	cout << "  Cycle: " << (unsigned int)map->cpu->instruction_cycle;
	cout << "  A: " << (unsigned int)map->cpu->accumulator;
	cout << "  X: " << (unsigned int)map->cpu->x_index_register;
	cout << "  Y: " << (unsigned int)map->cpu->y_index_register;
	cout << "  SP: " << (unsigned int)map->cpu->stack_pointer;
	cout << "  T: " << (unsigned int)map->cpu->tempregister;
	cout << "  addr: " << (unsigned int)map->cpu->address;
	cout << "  mem[0]: " << (unsigned int)map->cpu_ram[0];
	cout << "  mem[1]: " << (unsigned int)map->cpu_ram[1];
	cout << "  mem[2]: " << (unsigned int)map->cpu_ram[2];
	cout << "  mem[3]: " << (unsigned int)map->cpu_ram[3] << endl;

}
inline std::vector<char> ReadAllBytes(char const* filename)
{
	ifstream ifs(filename, ios::binary | ios::ate);
	ifstream::pos_type pos = ifs.tellg();

	std::vector<char>  result(pos);

	ifs.seekg(0, ios::beg);
	ifs.read(&result[0], pos);

	return result;
}
void SDLAudioCallback(void* UserData, uint8_t* AudioData, int length) {
	memset(AudioData, 0, length);
}
SDL_AudioSpec AudioSettings;
inline int main_sdl() {
	using namespace std;
	Mapper map;
	//Screen dimension constants
	const int SCREEN_WIDTH = 256 * 2;
	const int SCREEN_HEIGHT = 240 * 2;
	//The window we'll be rendering to
	SDL_Window* window = NULL;

	//The surface contained by the window
	SDL_Surface* screenSurface = NULL;

	//Initialize SDL
	if (SDL_Init(SDL_INIT_VIDEO|SDL_INIT_AUDIO) < 0)
	{
		printf("SDL could not initialize! SDL_Error: %s\n", SDL_GetError());
	}
	else
	{
		//Create window
		window = SDL_CreateWindow("BassNES C++ PoC", SDL_WINDOWPOS_UNDEFINED, SDL_WINDOWPOS_UNDEFINED, SCREEN_WIDTH, SCREEN_HEIGHT, SDL_WINDOW_SHOWN);
		if (window == NULL)
		{
			printf("Window could not be created! SDL_Error: %s\n", SDL_GetError());
		}
		else
		{
			//Get window surface
			screenSurface = SDL_GetWindowSurface(window);
			//screenSurface->format = SDL_PIXELFORMAT_ARGB4444;
			//Fill the surface white
			SDL_FillRect(screenSurface, NULL, SDL_MapRGB(screenSurface->format, 0xFF, 0xFF, 0xFF));

			//Update the surface
			SDL_UpdateWindowSurface(window);

			//Wait two seconds
			// SDL_Delay( 2000 );
		}
	}
	AudioSettings = { 0 };
	AudioSettings.freq = 44100;
	AudioSettings.format = AUDIO_U8;
	AudioSettings.channels = 1;
	//AudioSettings.samples = 1024;
	//AudioSettings.callback = &SDLAudioCallback;
	SDL_AudioSpec audiogot = { 0 };
	SDL_AudioDeviceID dev;
	dev = SDL_OpenAudioDevice(NULL, 0, &AudioSettings, 0, 0);
	//SDL_OpenAudio(&AudioSettings, &audiogot);
	/*if (audiogot.channels != 2)
		cout << "yo fam we didn't get 1 channel we got " <<(int) audiogot.channels << endl;
	if (audiogot.format != AUDIO_S16)
		cout << "yo fam we didn't get the right format we got " <<hex<< (int)audiogot.format <<dec<< endl;
	cout << "Test" << endl;*/
	if (dev == 0) {
		SDL_Log("Failed to open audio: %s", SDL_GetError());
	}
	SDL_PauseAudioDevice(dev, 0);
	map.apu->dev = (uint32_t)dev;

	cout << map.ppu->render_b << endl;
	char prg, chr;
	vector<char> data = ReadAllBytes("smb.nes");

	assert(data[0] == 'N');
	assert(data[1] == 'E');
	assert(data[2] == 'S');
	assert(data[3] == 0x1a);

	prg = data[4];
	chr = data[5];
	map.setMirror((int)(data[6] & 1));
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
	cout << "PRG READ : " << count << endl;
	for (unsigned int i = 0; i < chrrom.size(); i++) {
		chrrom[i] = (*iter);
		++iter;
	}

	map.setchr(chrrom);
	map.setprg(prgrom);
	//for(vector<uint8_t>::iterator iter = prgrom.begin();iter!=prgrom.end();++iter)
	//   cout<<hex<< (int)((*iter))<<", ";
	//cout<<endl;
	//for(int i = 0; i<0x4000;i++)
	//    cout <<hex<< (int) ( map.PRG_ROM[0][i])<<", " ;
	//cout<<endl;
	cout << hex;
	cout << chrrom.size() << " " << prgrom.size() << endl;
	cout << (int)map.cpuread(0xfffd) << " " << (int)map.cpuread(0xfffc) << endl;
	map.cpu->program_counter = map.cpuread(0xfffd);
	map.cpu->program_counter <<= 8;
	map.cpu->program_counter |= map.cpuread(0xfffc);
	cout << map.cpu->program_counter;
	//map.cpu->program_counter = 0xc000;
	bool print = false;
	bool quit = false;
	SDL_Event e;
	Uint32 startTime = 0;
	SDL_Renderer* gRender = NULL;
	gRender = SDL_CreateRenderer(window, -1, SDL_RENDERER_ACCELERATED | SDL_RENDERER_PRESENTVSYNC);
	SDL_Texture* texture = SDL_CreateTexture(gRender, SDL_PIXELFORMAT_ARGB8888, SDL_TEXTUREACCESS_STREAMING, 256, 240);
	double totalframetime=0;

	int framenum=0;
	while (!quit) {
		const Uint64 start = SDL_GetPerformanceCounter();
		SDL_SetRenderDrawColor(gRender, 0, 0, 0, SDL_ALPHA_OPAQUE);
		SDL_RenderClear(gRender);
		while (SDL_PollEvent(&e) != 0) {
			if (e.type == SDL_QUIT) {
				quit = true;
			}
			const Uint8* keystates = SDL_GetKeyboardState(NULL);
			//make input
			map.input[0][0] = keystates[SDL_SCANCODE_A];
			map.input[0][1] = keystates[SDL_SCANCODE_S];
			map.input[0][2] = keystates[SDL_SCANCODE_W];
			map.input[0][3] = keystates[SDL_SCANCODE_Q];
			map.input[0][4] = keystates[SDL_SCANCODE_UP];
			map.input[0][5] = keystates[SDL_SCANCODE_DOWN];
			map.input[0][6] = keystates[SDL_SCANCODE_LEFT];
			map.input[0][7] = keystates[SDL_SCANCODE_RIGHT];

		}
		map.runFrame();
		SDL_UpdateTexture(texture, NULL, map.ren->colorized, 256 * 4);
		SDL_RenderCopy(gRender, texture, NULL, NULL);
		SDL_RenderPresent(gRender);
		const Uint64 end = SDL_GetPerformanceCounter();
		const static Uint64 freq = SDL_GetPerformanceFrequency();
		const double seconds = (end - start) / static_cast< double >(freq);
		totalframetime += seconds;
		++framenum;
		if((framenum%60)==0)
			cout << "Average frame time: "<<(totalframetime/framenum)*1000<<"ms   FPS: "<<1000/((totalframetime/framenum)*1000)<<"  " << "Frame time: " << seconds * 1000.0 << "ms" << endl;
	}
	/*while (true) {
	if (print) {
	cout << map.cpu->program_counter << " ";
	printCPU(&map);
	}
	//if (map.cpu->program_counter== 0x1)
	//	print = true;
	map.runcycle();

	}*/

	//map.runcycle();
	//cin>>a;
	SDL_DestroyRenderer(gRender);
	SDL_DestroyWindow(window);
	SDL_Quit();
	return 0;
}