/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package video;

import java.util.Arrays;
import java.util.Scanner;

/**
 *
 * @author Andrew
 * Expanded by Jordan Howe
 */
public class NesColors {

    private NesColors() {}
    private static final int[] defaultPalette =    {0x606060,0x09268e,0x1a11bd,0x3409b6,0x5e0982,0x790939, 0x6f0c09, 0x511f09,0x293709, 0x0d4809, 0x094e09, 0x094b17, 0x093a5a, 0x000000, 0x000000, 0x000000,0xb1b1b1, 0x1658f7, 0x4433ff, 0x7d20ff, 0xb515d8, 0xcb1d73, 0xc62922, 0x954f09,0x5f7209, 0x28ac09, 0x099c09, 0x099032, 0x0976a2, 0x090909, 0x000000, 0x000000,0xffffff, 0x5dadff, 0x9d84ff, 0xd76aff, 0xff5dff, 0xff63c6, 0xff8150, 0xffa50d,0xccc409, 0x74f009, 0x54fc1c, 0x33f881, 0x3fd4ff, 0x494949, 0x000000, 0x000000,0xffffff, 0xc8eaff, 0xe1d8ff, 0xffccff, 0xffc6ff, 0xffcbfb, 0xffd7c2, 0xffe999,0xf0f986, 0xd6ff90, 0xbdffaf, 0xb3ffd7, 0xb3ffff, 0xbcbcbc, 0x000000, 0x000000};
    private static final int[] NTSCHardwareFBX =   {0x6A6D6A,0x001380,0x1E008A,0x39007A,0x550056,0x5A0018,0x4F1000,0x382100,0x213300,0x003D00,0x004000,0x003924,0x002E55,0x000000,0x000000,0x000000,0xB9BCB9,0x1850C7,0x4B30E3,0x7322D6,0x951FA9,0x9D285C,0x963C00,0x7A5100,0x5B6700,0x227700,0x027E02,0x007645,0x006E8A,0x000000,0x000000,0x000000,0xFFFFFF,0x68A6FF,0x9299FF,0xB085FF,0xD975FD,0xE377B9,0xE58D68,0xCFA22C,0xB3AF0C,0x7BC211,0x55CA47,0x46CB81,0x47C1C5,0x4A4D4A,0x000000,0x000000,0xFFFFFF,0xCCEAFF,0xDDDEFF,0xECDAFF,0xF8D7FE,0xFCD6F5,0xFDDBCF,0xF9E7B5,0xF1F0AA,0xDAFAA9,0xC9FFBC,0xC3FBD7,0xC4F6F6,0xBEC1BE,0x000000,0x000000};
    private static final int[] nesClassicFBX =     {0x60615F,0x000083,0x1D0195,0x340875,0x51055E,0x56000F,0x4C0700,0x372308,0x203A0B,0x0F4B0E,0x194C16,0x02421E,0x023154,0x000000,0x000000,0x000000,0xA9AAA8,0x104BBF,0x4712D8,0x6300CA,0x8800A9,0x930B46,0x8A2D04,0x6F5206,0x5C7114,0x1B8D12,0x199509,0x178448,0x206B8E,0x000000,0x000000,0x000000,0xFBFBFB,0x6699F8,0x8974F9,0xAB58F8,0xD557EF,0xDE5FA9,0xDC7F59,0xC7A224,0xA7BE03,0x75D703,0x60E34F,0x3CD68D,0x56C9CC,0x414240,0x000000,0x000000,0xFBFBFB,0xBED4FA,0xC9C7F9,0xD7BEFA,0xE8B8F9,0xF5BAE5,0xF3CAC2,0xDFCDA7,0xD9E09C,0xC9EB9E,0xC0EDB8,0xB5F4C7,0xB9EAE9,0xABABAB,0x000000,0x000000};
    private static final int[] compositeDirectFBX ={0x656565,0x00127D,0x18008E,0x360082,0x56005D,0x5A0018,0x4F0500,0x381900,0x1D3100,0x003D00,0x004100,0x003B17,0x002E55,0x000000,0x000000,0x000000,0xAFAFAF,0x194EC8,0x472FE3,0x6B1FD7,0x931BAE,0x9E1A5E,0x993200,0x7B4B00,0x5B6700,0x267A00,0x008200,0x007A3E,0x006E8A,0x000000,0x000000,0x000000,0xFFFFFF,0x64A9FF,0x8E89FF,0xB676FF,0xE06FFF,0xEF6CC4,0xF0806A,0xD8982C,0xB9B40A,0x83CB0C,0x5BD63F,0x4AD17E,0x4DC7CB,0x4C4C4C,0x000000,0x000000,0xFFFFFF,0xC7E5FF,0xD9D9FF,0xE9D1FF,0xF9CEFF,0xFFCCF1,0xFFD4CB,0xF8DFB1,0xEDEAA4,0xD6F4A4,0xC5F8B8,0xBEF6D3,0xBFF1F1,0xB9B9B9,0x000000,0x000000};
    private static final int[] sonypvmFBX =        {0x696B63,0x001774,0x1E0087,0x340073,0x560057,0x5E0013,0x531A00,0x3B2400,0x243000,0x063A00,0x003F00,0x003B1E,0x00334E,0x000000,0x000000,0x000000,0xB9BBB3,0x1453B9,0x4D2CDA,0x671EDE,0x98189C,0x9D2344,0xA03E00,0x8D5500,0x656D00,0x2C7900,0x008100,0x007D42,0x00788A,0x000000,0x000000,0x000000,0xFFFFFF,0x69A8FF,0x9691FF,0xB28AFA,0xEA7DFA,0xF37BC7,0xF28E59,0xE6AD27,0xD7C805,0x90DF07,0x64E53C,0x45E27D,0x48D5D9,0x4E5048,0x000000,0x000000,0xFFFFFF,0xD2EAFF,0xE2E2FF,0xE9D8FF,0xF5D2FF,0xF8D9EA,0xFADEB9,0xF9E89B,0xF3F28C,0xD3FA91,0xB8FCA8,0xAEFACA,0xCAF3F3,0xBEC0B8,0x000000,0x000000};
    public static int[] custom = Arrays.copyOf(defaultPalette, defaultPalette.length);
    public final static String[] palettes = {"defaultPalette","Custom","NTSCHardwareFBX","nesClassicFBX","compositeDirectFBX","sonypvmFBX"};
    private final static double att = 0.7;
    public static int[][] col = GetNESColors(defaultPalette);
    public static byte[][][] colbytes = NESColorsToBytes(col);
    
    private static int[][] GetNESColors(int[] colorarray) {
        //just or's all the colors with opaque alpha and does the color emphasis calcs
        //This set of colors matches current version of ntsc filter output
        for (int i = 0; i < colorarray.length; ++i) {
            colorarray[i] |= 0xff000000;
        }
        int[][] colors = new int[8][colorarray.length];
        for (int j = 0; j < colorarray.length; ++j) {
            int col = colorarray[j];
            int r = r(col);
            int b = b(col);
            int g = g(col);
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

        }
        return colors;
    }

    public static void updatePalette(String palettename){
    	switch(palettename){
    	case "NTSCHardwareFBX": col = GetNESColors(NTSCHardwareFBX); colbytes = NESColorsToBytes(col);break;
    	case "nesClassicFBX":col = GetNESColors(nesClassicFBX); colbytes = NESColorsToBytes(col);break;
    	case "compositeDirectFBX":col = GetNESColors(compositeDirectFBX); colbytes = NESColorsToBytes(col);break;
    	case "sonypvmFBX":col = GetNESColors(sonypvmFBX); colbytes = NESColorsToBytes(col);break;
    	case "Custom": col = GetNESColors(custom); colbytes = NESColorsToBytes(col);break;
    	default:col = GetNESColors(defaultPalette); colbytes = NESColorsToBytes(col);break;
    	}
    	
    }
    private static byte[][][] NESColorsToBytes(int[][] col) {
        byte[][][] colbytes = new byte[col.length][][];
        for (int i=0; i<col.length; i++) {
            int[] col2 = col[i];
            byte[][] colbytes2 = colbytes[i] = new byte[col2.length][3];
            for (int j=0; j<col2.length; j++) {
                colbytes2[j][0] = (byte) b(col2[j]);
                colbytes2[j][1] = (byte) g(col2[j]);
                colbytes2[j][2] = (byte) r(col2[j]);
            }
        }
        return colbytes;
    }
    
    private static int r(int col) {
        return (col >> 16) & 0xff;
    }

    private static int g(int col) {
        return (col >> 8) & 0xff;
    }

    private static int b(int col) {
        return col & 0xff;
    }

    private static int compose_col(double r, double g, double b) {
        return (((int) r & 0xff) << 16) + (((int) g & 0xff) << 8) + ((int) b & 0xff) + 0xff000000;
    }
    public static String getCustomPalette(){
    	String out="";
    	for(int i:custom)
    		out+=i + " ";
    	return out;
    }
    public static void setCustomPalette(String palette){
    	if(palette.length()>0){
	    	Scanner s = new Scanner(palette);
	    	for(int i = 0; i<custom.length;i++){
	    		custom[i] = s.nextInt();
	    	}
	    	s.close();
    	}
    }

	public static int[] getpalette(String selectedPalette) {
		switch(selectedPalette){
    	case "NTSCHardwareFBX": return NTSCHardwareFBX;
    	case "nesClassicFBX":return nesClassicFBX;
    	case "compositeDirectFBX":return compositeDirectFBX;
    	case "sonypvmFBX": return sonypvmFBX;
    	case "Custom" : return custom;
    	default: return defaultPalette;
    	}
	}
}