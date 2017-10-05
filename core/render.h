#include <cstdint>
using namespace std;

class render {

public:
	int colorized[61440] = {};
	//3ds version
	//int* colorized;
	unsigned int defaultPalette[64] = { 0x606060,0x09268e,0x1a11bd,0x3409b6,0x5e0982,0x790939, 0x6f0c09, 0x511f09,0x293709, 0x0d4809, 0x094e09, 0x094b17, 0x093a5a, 0x000000, 0x000000, 0x000000,0xb1b1b1, 0x1658f7, 0x4433ff, 0x7d20ff, 0xb515d8, 0xcb1d73, 0xc62922, 0x954f09,0x5f7209, 0x28ac09, 0x099c09, 0x099032, 0x0976a2, 0x090909, 0x000000, 0x000000,0xffffff, 0x5dadff, 0x9d84ff, 0xd76aff, 0xff5dff, 0xff63c6, 0xff8150, 0xffa50d,0xccc409, 0x74f009, 0x54fc1c, 0x33f881, 0x3fd4ff, 0x494949, 0x000000, 0x000000,0xffffff, 0xc8eaff, 0xe1d8ff, 0xffccff, 0xffc6ff, 0xffcbfb, 0xffd7c2, 0xffe999,0xf0f986, 0xd6ff90, 0xbdffaf, 0xb3ffd7, 0xb3ffff, 0xbcbcbc, 0x000000, 0x000000 };
	int col[64];
	double att = .7;
	render() {
		for (int i = 0; i < 64; i++) {
			defaultPalette[i] |= 0xff000000;
		}
	}

	void buildImageRGBnoEmp(uint8_t* pixels) {
		for (int i = 0; i < 61440; i++)
			//colorized[i] = col[pixels[i]];
			colorized[i] =  defaultPalette[pixels[i]];
		/*for (int y = 0; y<240; y++)
			for (int x = 0; x<256; x++)
				colorized[240 * x + (240 - y)] = defaultPalette[pixels[y * 256 + x]] << 8;
		*/
	}
	int rm(int col) {
		return (col >> 16) & 0xff;
	};

	int gm(int col) {
		return (col >> 8) & 0xff;
	};

	int bm(int col) {
		return col & 0xff;
	};

	int compose_col(double r, double g, double b) {
		return (((int)r & 0xff) << 16) + (((int)g & 0xff) << 8) + ((int)b & 0xff) + 0xff000000;
	};
	void GetNESColors(int* colorarray) {
		//just or's all the colors with opaque alpha and does the color emphasis calcs
		//This set of colors matches current version of ntsc filter output
		for (int i = 0; i < 64; ++i) {
			colorarray[i] |= 0xff000000;
		}
		int colors[16][64];
		for (int j = 0; j < 64; ++j) {
			int col = colorarray[j];
			int r = rm(col);
			int b = bm(col);
			int g = gm(col);
			colors[0][j] = col;
			//emphasize red
			colors[1][j] = compose_col(r, g * att, b * att);
			//emphasize green
			colors[2][j] = compose_col(r * att, g, b * att);
			//emphasize yellow
			colors[3][j] = compose_col(r, g, b * att);
			//emphasize blue
			colors[4][j] = compose_col(r * att, g * att, b);
			//emphasize purple
			colors[5][j] = compose_col(r, g * att, b);
			//emphasize cyan?
			colors[6][j] = compose_col(r * att, g, b);
			//de-emph all 3 colors
			colors[7][j] = compose_col(r * att, g * att, b * att);
			for (int i = 8; i < 16; i++) {
				for (int x = 0; x < 64; x++)
					colors[i][x] = colors[i - 8][x & 0x30];

			}

		}
		for (int i = 0; i < 64; i++) {
			col[i] = colors[0][i];
		};

	}
};
