/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.video;

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
    private static final int[] vc_3ds =            {0x737373,0x21188C,0x0000AD,0x42009C,0x8C0073,0xAD0010,0xA50000,0x7B0800,0x422900,0x004200,0x005200,0x003910,0x18395A,0x000000,0x000000,0x000000,0xBDBDBD,0x0073EF,0x2139EF,0x8400F7,0xBD00BD,0xE7005A,0xDE2900,0xCE4A08,0x8C7300,0x009400,0x00AD00,0x009439,0x00848C,0x101010,0x000000,0x000000,0xFFFFFF,0x39BDFF,0x5A94FF,0xA58CFF,0xF77BFF,0xFF73B5,0xFF7363,0xFF9C39,0xF7BD39,0x84D610,0x4ADE4A,0x5AFF9C,0x00EFDE,0x393939,0x000000,0x000000,0xFFFFFF,0xADE7FF,0xC6D6FF,0xD6CEFF,0xFFC6FF,0xFFC6DE,0xFFBDB5,0xFFDEAD,0xFFE7A5,0xE7FFA5,0xADF7BD,0xB5FFCE,0x9CFFF7,0x8C8C8C,0x000000,0x000000};
    private static final int[] asq_reality_c =     {0x6C6C6C,0x00268E,0x0000A8,0x400094,0x700070,0x780040,0x700000,0x621600,0x442400,0x343400,0x005000,0x004444,0x004060,0x000000,0x101010,0x101010,0xBABABA,0x205CDC,0x3838FF,0x8020F0,0xC000C0,0xD01474,0xD02020,0xAC4014,0x7C5400,0x586400,0x008800,0x007468,0x00749C,0x202020,0x101010,0x101010,0xFFFFFF,0x4CA0FF,0x8888FF,0xC06CFF,0xFF50FF,0xFF64B8,0xFF7878,0xFF9638,0xDBAB00,0xA2CA20,0x4ADC4A,0x2CCCA4,0x1CC2EA,0x585858,0x101010,0x101010,0xFFFFFF,0xB0D4FF,0xC4C4FF,0xE8B8FF,0xFFB0FF,0xFFB8E8,0xFFC4C4,0xFFD4A8,0xFFE890,0xF0F4A4,0xC0FFC0,0xACF4F0,0xA0E8FF,0xC2C2C2,0x202020,0x101010};
    private static final int[] av_famicom =        {0x6B6A6B,0x000A91,0x1500A1,0x37008E,0x59005E,0x5F0010,0x530000,0x351300,0x112700,0x003700,0x003C00,0x00340D,0x002554,0x000001,0x000001,0x000001,0xBCBBBD,0x1046E0,0x4522FC,0x700FEA,0x9E09B5,0xAA0D55,0x9F2900,0x7D4600,0x566600,0x1A7C00,0x008500,0x007C3C,0x006C95,0x000001,0x000001,0x000001,0xFFFFFF,0x67A8FF,0x9687FF,0xBF72FF,0xEC6BFF,0xFD6AC2,0xFA825F,0xDE9C20,0xBDBD03,0x84D408,0x59DE41,0x47D98B,0x4FCDDE,0x4D4C4E,0x000001,0x000001,0xFFFFFF,0xCEEAFF,0xDFDDFF,0xEDD5FF,0xFED1FF,0xFFD0F9,0xFFD8D3,0xFEE0BA,0xF6F0AE,0xE1F9AD,0xD0FEC0,0xC8FDDB,0xC9F9F9,0xC6C4C6,0x000001,0x000001};
    private static final int[] bmf_final_3 =       {0x686868,0x001299,0x1A08AA,0x51029A,0x7E0069,0x8E001C,0x7E0301,0x511800,0x1F3700,0x014E00,0x005A00,0x00501C,0x004061,0x000000,0x000000,0x000000,0xB9B9B9,0x0C5CD7,0x5035F0,0x8919E0,0xBB0CB3,0xCE0C61,0xC02B0E,0x954D01,0x616F00,0x1F8B00,0x01980C,0x00934B,0x00819B,0x000000,0x000000,0x000000,0xFFFFFF,0x63B4FF,0x9B91FF,0xD377FF,0xEF6AFF,0xF968C0,0xF97D6C,0xED9B2D,0xBDBD16,0x7CDA1C,0x4BE847,0x35E591,0x3FD9DD,0x606060,0x000000,0x000000,0xFFFFFF,0xACE7FF,0xD5CDFF,0xEDBAFF,0xF8B0FF,0xFEB0EC,0xFDBDB5,0xF9D28E,0xE8EB7C,0xBBF382,0x99F7A2,0x8AF5D0,0x92F4F1,0xBEBEBE,0x000000,0x000000};
    private static final int[] consumer =          {0x666666,0x001E9A,0x0E09A8,0x440093,0x710060,0x89011D,0x861300,0x692900,0x393E00,0x044C00,0x004F00,0x00472B,0x00356C,0x000000,0x000000,0x000000,0xADADAD,0x0050F1,0x3B34FF,0x8022E8,0xBB1EA5,0xDB294E,0xD74000,0xB15E00,0x737900,0x2D8B00,0x008F08,0x008460,0x006DB5,0x000000,0x000000,0x000000,0xFFFFFF,0x4BA0FF,0x8A84FF,0xD172FF,0xFF6DF7,0xFF799E,0xFF9047,0xFFAE0A,0xC4CA00,0x7DDC13,0x41E157,0x21D5B0,0x25BEFF,0x4F4F4F,0x000000,0x000000,0xFFFFFF,0xB6D8FF,0xD0CDFF,0xEDC6FF,0xFFC4FC,0xFFC8D8,0xFFD2B4,0xFFDE9C,0xE7E994,0xCAF19F,0xB2F3BB,0xA5EEDF,0xA6E5FF,0xB8B8B8,0x000000,0x000000};
    private static final int[] dougeff =           {0x787878,0x181878,0x32198C,0x4C0F96,0x5C0B40,0x6C0207,0x4D0E04,0x3C1505,0x1F2B02,0x004500,0x00450D,0x033D1C,0x17407A,0x050505,0x050505,0x050505,0xADADAD,0x3434E3,0x572ADE,0x7F33C6,0xCC14BD,0xCF0045,0xD12B15,0xA1541B,0x697D0D,0x0E9A04,0x019915,0x25A162,0x2B97CF,0x050505,0x050505,0x050505,0xF5F4EE,0x789AFE,0x7878FF,0x9A68FE,0xF874E4,0xFD6396,0xFD8B63,0xF2B643,0xBCDE35,0x78D129,0x2CDE5A,0x41E59A,0x4FD5F1,0x686868,0x050505,0x050505,0xFCFCF7,0xBAC7EE,0xC7C7EF,0xD4C5F5,0xF4C6F7,0xF6C7D9,0xF2CEC6,0xF5E4B6,0xD9EDA4,0xC3DDA3,0x9FD8A0,0xABDCC8,0xAED7DC,0xA6A6A6,0x050505,0x050505};
    private static final int[] drag3 =             {0x4C4C4C,0x000569,0x000085,0x0F007E,0x420055,0x630016,0x6B0000,0x560000,0x2B1500,0x002600,0x002D00,0x002900,0x001A31,0x000000,0x000000,0x000000,0x9C9C9C,0x003FC2,0x0522E6,0x4C0BDD,0x8E02A8,0xBB0756,0xC51B00,0xAA3700,0x715400,0x2A6A00,0x007400,0x006E20,0x005B79,0x000000,0x000000,0x000000,0xF7F7F7,0x2499FF,0x5E7BFF,0xA665FF,0xEA5BFF,0xFF61B0,0xFF7456,0xFF910C,0xCCAE00,0x84C500,0x40CF26,0x13C979,0x09B5D4,0x323232,0x000000,0x000000,0xF7F7F7,0xA2D1FF,0xB9C5FF,0xD6BCFF,0xF2B8FC,0xFFBADB,0xFFC2B6,0xFDCE98,0xE6DA89,0xC8E38D,0xADE7A2,0x9BE5C4,0x97DDE9,0xA7A7A7,0x000000,0x000000};
    private static final int[] fceux =             {0x747474,0x24188C,0x0000A8,0x44009C,0x8C0074,0xA80010,0xA40000,0x7C0800,0x402C00,0x004400,0x005000,0x003C14,0x183C5C,0x000000,0x000000,0x000000,0xBCBCBC,0x0070EC,0x2038EC,0x8000F0,0xBC00BC,0xE40058,0xD82800,0xC84C0C,0x887000,0x009400,0x00A800,0x009038,0x008088,0x000000,0x000000,0x000000,0xFCFCFC,0x3CBCFC,0x5C94FC,0xCC88FC,0xF478FC,0xFC74B4,0xFC7460,0xFC9838,0xF0BC3C,0x80D010,0x4CDC48,0x58F898,0x00E8D8,0x787878,0x000000,0x000000,0xFCFCFC,0xA8E4FC,0xC4D4FC,0xD4C8FC,0xFCC4FC,0xFCC4D8,0xFCBCB0,0xFCD8A8,0xFCE4A0,0xE0FCA0,0xA8F0BC,0xB0FCCC,0x9CFCF0,0xC4C4C4,0x000000,0x000000};
    private static final int[] fceux_15 =          {0x606060,0x000070,0x140080,0x2C006E,0x4A004E,0x6C0018,0x5A0302,0x511800,0x342400,0x003400,0x003200,0x003420,0x002C78,0x000000,0x020202,0x020202,0xC4C4C4,0x0058DE,0x301FFC,0x7F14E0,0xA800B0,0xC0065C,0xC02B0E,0xA64010,0x6F6100,0x308000,0x007C00,0x007C3C,0x006E84,0x141414,0x040404,0x040404,0xF0F0F0,0x4CAAFF,0x6F73F5,0xB070FF,0xDA5AFF,0xF060C0,0xF8836D,0xD09030,0xD4C030,0x66D000,0x26DD1A,0x2EC866,0x34C2BE,0x545454,0x060606,0x060606,0xFFFFFF,0xB6DAFF,0xC8CAFF,0xDAC2FF,0xF0BEFF,0xFCBCEE,0xFFD0B4,0xFFDA90,0xECEC92,0xDCF69E,0xB8FFA2,0xAEEABE,0x9EEFEF,0xBEBEBE,0x080808,0x080808};
    private static final int[] gameboy =           {0x306030,0x083808,0x083808,0x083808,0x083808,0x083808,0x083808,0x083808,0x083808,0x306030,0x306030,0x306030,0x083808,0x083808,0x083808,0x083808,0x88A808,0x306030,0x306030,0x306030,0x306030,0x306030,0x306030,0x306030,0x306030,0x306030,0x306030,0x306030,0x306030,0x083808,0x083808,0x083808,0xB7DC11,0x88A808,0x88A808,0x88A808,0x88A808,0x88A808,0x88A808,0x88A808,0x88A808,0x88A808,0x88A808,0x88A808,0x88A808,0x306030,0x083808,0x083808,0xB7DC11,0xB7DC11,0x88A808,0x88A808,0x88A808,0x88A808,0x88A808,0xB7DC11,0xB7DC11,0xB7DC11,0xB7DC11,0xB7DC11,0xB7DC11,0x88A808,0x083808,0x083808};
    private static final int[] grayscale       =   {0x666666,0x333333,0x333333,0x353535,0x353535,0x353535,0x333333,0x2F2F2F,0x333333,0x3C3C3C,0x454545,0x434343,0x3A3A3A,0x000000,0x000000,0x000000,0xACACAC,0x666666,0x616161,0x616161,0x646464,0x616161,0x616161,0x616161,0x686868,0x757575,0x7C7C7C,0x7A7A7A,0x707070,0x000000,0x000000,0x000000,0xFFFFFF,0xACACAC,0x9C9C9C,0x999999,0xA1A1A1,0xA4A4A4,0xA6A6A6,0xACACAC,0xB6B6B6,0xC1C1C1,0xC7C7C7,0xC4C4C4,0xBCBCBC,0x4E4E4E,0x000000,0x000000,0xFFFFFF,0xDADADA,0xD5D5D5,0xD2D2D2,0xD5D5D5,0xD5D5D5,0xD8D8D8,0xDADADA,0xE0E0E0,0xE3E3E3,0xE3E3E3,0xE3E3E3,0xE0E0E0,0xB6B6B6,0x000000,0x000000};
    private static final int[] kizul      =        {0x5C5C5C,0x00168B,0x210198,0x450087,0x6A0162,0x6F0117,0x600100,0x401900,0x1F3201,0x004400,0x004B01,0x004322,0x00385A,0x000000,0x0A0A0A,0x0A0A0A,0xA1A1A1,0x0049D0,0x4024ED,0x720DDE,0xA902B6,0xB60652,0xA92000,0x813F01,0x5B6001,0x0B7A01,0x048702,0x01803F,0x00758A,0x0A0A0A,0x0A0A0A,0x0A0A0A,0xEFEFEF,0x3EA0FF,0x7C77FF,0xAF5EFE,0xE954FF,0xFE4EB5,0xFB6453,0xDB8011,0xB8A301,0x67C003,0x37D125,0x1CCF74,0x28C9BD,0x484848,0x0A0A0A,0x0A0A0A,0xEFEFEF,0xA9D1FC,0xC1C1FF,0xD4B7FF,0xEBB2FF,0xF7ADDE,0xF8B7B4,0xE9C297,0xDBD08C,0xBDDD8A,0xA8E49C,0x9CE4BA,0x9DE0DA,0xABABAB,0x0A0A0A,0x0A0A0A};
    private static final int[] nesticle =          {0x7F7F7F,0x0000FF,0x0000BF,0x472BBF,0x970087,0xAB0023,0xAB1300,0x8B1700,0x533000,0x007800,0x006B00,0x005B00,0x004358,0x000000,0x000000,0x000000,0xBFBFBF,0x0078F8,0x0058F8,0x6B47FF,0xDB00CD,0xE7005B,0xF83800,0xE75F13,0xAF7F00,0x00B800,0x00AB00,0x00AB47,0x008B8B,0x000000,0x000000,0x000000,0xF8F8F8,0x3FBFFF,0x6B88FF,0x9878F8,0xF878F8,0xF85898,0xF87858,0xFFA347,0xF8B800,0xB8F818,0x5BDB57,0x58F898,0x00EBDB,0x787878,0x000000,0x000000,0xFFFFFF,0xA7E7FF,0xB8B8F8,0xD8B8F8,0xF8B8F8,0xFBA7C3,0xF0D0B0,0xFFE3AB,0xFBDB7B,0xD8F878,0xB8F8B8,0xB8F8D8,0x00FFFF,0xF8D8F8,0x000000,0x000000};
    private static final int[] nestopia_rgb =      {0x6D6D6D,0x002492,0x0000DB,0x6D49DB,0x92006D,0xB6006D,0xB62400,0x924900,0x6D4900,0x244900,0x006D24,0x009200,0x004949,0x000000,0x000000,0x000000,0xB6B6B6,0x006DDB,0x0049FF,0x9200FF,0xB600FF,0xFF0092,0xFF0000,0xDB6D00,0x926D00,0x249200,0x009200,0x00B66D,0x009292,0x242424,0x000000,0x000000,0xFFFFFF,0x6DB6FF,0x9292FF,0xDB6DFF,0xFF00FF,0xFF6DFF,0xFF9200,0xFFB600,0xDBDB00,0x6DDB00,0x00FF00,0x49FFDB,0x00FFFF,0x494949,0x000000,0x000000,0xFFFFFF,0xB6DBFF,0xDBB6FF,0xFFB6FF,0xFF92FF,0xFFB6B6,0xFFDB92,0xFFFF49,0xFFFF6D,0xB6FF49,0x92FF6D,0x49FFDB,0x92DBFF,0x929292,0x000000,0x000000};
    private static final int[] nestopia_yuv =      {0x666666,0x002A88,0x1412A7,0x3B00A4,0x5C007E,0x6E0040,0x6C0700,0x561D00,0x333500,0x0C4800,0x005200,0x004F08,0x00404D,0x000000,0x000000,0x000000,0xADADAD,0x155FD9,0x4240FF,0x7527FE,0xA01ACC,0xB71E7B,0xB53120,0x994E00,0x6B6D00,0x388700,0x0D9300,0x008F32,0x007C8D,0x000000,0x000000,0x000000,0xFFFFFF,0x64B0FF,0x9290FF,0xC676FF,0xF26AFF,0xFF6ECC,0xFF8170,0xEA9E22,0xBCBE00,0x88D800,0x5CE430,0x45E082,0x48CDDE,0x4F4F4F,0x000000,0x000000,0xFFFFFF,0xC0DFFF,0xD3D2FF,0xE8C8FF,0xFAC2FF,0xFFC4EA,0xFFCCC5,0xF7D8A5,0xE4E594,0xCFEF96,0xBDF4AB,0xB3F3CC,0xB5EBF2,0xB8B8B8,0x000000,0x000000};
    private static final int[] nintendulator_ntsc ={0x656565,0x002B9B,0x110EC0,0x3F00BC,0x66008F,0x7B0045,0x790100,0x601C00,0x363800,0x084F00,0x005A00,0x005702,0x004555,0x000000,0x000000,0x000000,0xAEAEAE,0x0761F5,0x3E3BFF,0x7C1DFF,0xAF0EE5,0xCB1383,0xC82A15,0xA74D00,0x6F7200,0x329100,0x009F00,0x009B2A,0x008498,0x000000,0x000000,0x000000,0xFFFFFF,0x56B1FF,0x8E8BFF,0xCC6CFF,0xFF5DFF,0xFF62D4,0xFF7964,0xF89D06,0xC0C300,0x81E200,0x4DF116,0x30EC7A,0x34D5EA,0x4E4E4E,0x000000,0x000000,0xFFFFFF,0xBADFFF,0xD1D0FF,0xEBC3FF,0xFFBDFF,0xFFBFEE,0xFFC8C0,0xFCD799,0xE5E784,0xCCF387,0xB6F9A0,0xAAF8C9,0xACEEF7,0xB7B7B7,0x000000,0x000000};
    private static final int[] rinao =             {0x696969,0x001ACD,0x1104EF,0x5200D8,0x84008D,0x990023,0x8B0000,0x5E1500,0x1E2B00,0x003C00,0x004300,0x003E0C,0x002F7A,0x000000,0x000000,0x000000,0xBBBBBB,0x0052FF,0x4737FF,0x9922FF,0xD81AE3,0xF1205E,0xE03200,0xA84D00,0x586800,0x067D00,0x008600,0x008042,0x006DCA,0x000000,0x000000,0x000000,0xFFFFFF,0x53AEFF,0xA393FF,0xF57EFF,0xFF76FF,0xFF7BBA,0xFF8E31,0xFFA900,0xB4C400,0x62D900,0x24E118,0x0ADC9D,0x1BC9FF,0x505050,0x000000,0x000000,0xFFFFFF,0xCCF1FF,0xECE6FF,0xFFDEFF,0xFFDAFF,0xFFDDF5,0xFFE4BF,0xFFEF95,0xF3FA84,0xD2FF90,0xB9FFB5,0xAFFFEA,0xB6FCFF,0xC8C8C8,0x000000,0x000000};
    private static final int[] rockman_9 =         {0x707070,0x0000A8,0x201888,0x400098,0x880070,0xA80010,0xA00000,0x780800,0x402800,0x004000,0x005000,0x003810,0x183858,0x000000,0x000000,0x000000,0xB8B8B8,0x0070E8,0x2038E8,0x8000F0,0xB800B8,0xE00058,0xD82800,0xC84808,0x887000,0x009000,0x00A800,0x009038,0x008088,0x000000,0x000000,0x000000,0xF8F8F8,0x38B8F8,0x5890F8,0xA088F8,0xF078F8,0xF870B0,0xF87060,0xF89838,0xF0B838,0x80D010,0x48D848,0x58F898,0x38B8F8,0x505050,0x000000,0x000000,0xF8F8F8,0xA8E0F8,0xC0D0F8,0xD0C8F8,0xF8C0F8,0xF8C0D8,0xF8B8B0,0xF8D8A8,0xF8E0A0,0xE0F8A0,0xA8F0B8,0xB0F8C8,0x98F8F0,0x989898,0x000000,0x000000};
    private static final int[] rp2c04_0001 =       {0xFFB6B6,0xDB6DFF,0xFF0000,0x9292FF,0x009292,0x244900,0x494949,0xFF0092,0xFFFFFF,0x6D6D6D,0xFFB600,0xB6006D,0x92006D,0xDBDB00,0x6D4900,0xFFFFFF,0x6DB6FF,0xDBB66D,0x6D2400,0x6DDB00,0x92DBFF,0xDBB6FF,0xFFDB92,0x0049FF,0xFFDB00,0x49FFDB,0x000000,0x490000,0xDBDBDB,0x929292,0xFF00FF,0x002492,0x00006D,0xB6DBFF,0xFFB6FF,0x00FF00,0x00FFFF,0x004949,0x00B66D,0xB600FF,0x000000,0x924900,0xFF92FF,0xB62400,0x9200FF,0x0000DB,0xFF9200,0x000000,0x000000,0x249200,0xB6B6B6,0x006D24,0xB6FF49,0x6D49DB,0xFFFF00,0xDB6D00,0x004900,0x006DDB,0x009200,0x242424,0xFFFF6D,0xFF6DFF,0x926D00,0x92FF6D};
    private static final int[] rp2c04_0002 =       {0x000000,0xFFB600,0x926D00,0xB6FF49,0x92FF6D,0xFF6DFF,0x009292,0xB6DBFF,0xFF0000,0x9200FF,0xFFFF6D,0xFF92FF,0xFFFFFF,0xDB6DFF,0x92DBFF,0x009200,0x004900,0x6DB6FF,0xB62400,0xDBDBDB,0x00B66D,0x6DDB00,0x490000,0x9292FF,0x494949,0xFF00FF,0x00006D,0x49FFDB,0xDBB6FF,0x6D4900,0x000000,0x6D49DB,0x92006D,0xFFDB92,0xFF9200,0xFFB6FF,0x006DDB,0x6D2400,0xB6B6B6,0x0000DB,0xB600FF,0xFFDB00,0x6D6D6D,0x244900,0x0049FF,0x000000,0xDBDB00,0xFFFFFF,0xDBB66D,0x242424,0x00FF00,0xDB6D00,0x004949,0x002492,0xFF0092,0x249200,0x000000,0x00FFFF,0x924900,0xFFFF00,0xFFB6B6,0xB6006D,0x006D24,0x929292};
    private static final int[] rp2c04_0003 =       {0xB600FF,0xFF6DFF,0x92FF6D,0xB6B6B6,0x009200,0xFFFFFF,0xB6DBFF,0x244900,0x002492,0x000000,0xFFDB92,0x6D4900,0xFF0092,0xDBDBDB,0xDBB66D,0x92DBFF,0x9292FF,0x009292,0xB6006D,0x0049FF,0x249200,0x926D00,0xDB6D00,0x00B66D,0x6D6D6D,0x6D49DB,0x000000,0x0000DB,0xFF0000,0xB62400,0xFF92FF,0xFFB6B6,0xDB6DFF,0x004900,0x00006D,0xFFFF00,0x242424,0xFFB600,0xFF9200,0xFFFFFF,0x6DDB00,0x92006D,0x6DB6FF,0xFF00FF,0x006DDB,0x929292,0x000000,0x6D2400,0x00FFFF,0x490000,0xB6FF49,0xFFB6FF,0x924900,0x00FF00,0xDBDB00,0x494949,0x006D24,0x000000,0xDBB6FF,0xFFFF6D,0x9200FF,0x49FFDB,0xFFDB00,0x004949};
    private static final int[] rp2c04_0004 =       {0x926D00,0x6D49DB,0x009292,0xDBDB00,0x000000,0xFFB6B6,0x002492,0xDB6D00,0xB6B6B6,0x6D2400,0x00FF00,0x00006D,0xFFDB92,0xFFFF00,0x009200,0xB6FF49,0xFF6DFF,0x490000,0x0049FF,0xFF92FF,0x000000,0x494949,0xB62400,0xFF9200,0xDBB66D,0x00B66D,0x9292FF,0x249200,0x92006D,0x000000,0x92FF6D,0x6DB6FF,0xB6006D,0x006D24,0x924900,0x0000DB,0x9200FF,0xB600FF,0x6D6D6D,0xFF0092,0x004949,0xDBDBDB,0x006DDB,0x004900,0x242424,0xFFFF6D,0x929292,0xFF00FF,0xFFB6FF,0xFFFFFF,0x6D4900,0xFF0000,0xFFDB00,0x49FFDB,0xFFFFFF,0x92DBFF,0x000000,0xFFB600,0xDB6DFF,0xB6DBFF,0x6DDB00,0xDBB6FF,0x00FFFF,0x244900};
    private static final int[] terratec_cinergy =  {0x535454,0x000671,0x0C0080,0x280071,0x47004E,0x4A0006,0x400000,0x280C00,0x0F2200,0x002E00,0x003200,0x002A0C,0x002044,0x000000,0x000000,0x000000,0x9EA09F,0x033DB9,0x341CD8,0x5A0BCC,0x8508A9,0x8F074C,0x8A2000,0x6D3900,0x4F5600,0x136900,0x007200,0x006B2C,0x006175,0x000000,0x000000,0x000000,0xF6F8F7,0x4B9BF9,0x787BFF,0x9E66FF,0xCC5DFF,0xE058BC,0xE36B5E,0xCC821D,0xB2A000,0x76B800,0x4BC622,0x37C363,0x37BDAB,0x3C3D3C,0x000000,0x000000,0xF7F8F7,0xB0D5F8,0xC3C7FF,0xD3BEFF,0xE7BBFF,0xF0B8E6,0xF2C0BE,0xE9C9A1,0xDFD592,0xC7E08D,0xB4E69F,0xABE6B9,0xAAE3D9,0xAAABAA,0x000000,0x000000};
    private static final int[] trebor =            {0x6C6C6C,0x002094,0x0000A8,0x3C0098,0x700070,0x6F0031,0x640000,0x4F1100,0x2F1900,0x1E3C00,0x004400,0x003937,0x00394F,0x000000,0x101010,0x101010,0xBABABA,0x2A58D6,0x3C32FF,0x8020F0,0xC000C0,0xB41464,0xBE280A,0x9E4B04,0x675100,0x436100,0x007800,0x007153,0x006996,0x101010,0x101010,0x101010,0xFFFFFF,0x5EA0FF,0x8C82FF,0xC470FF,0xFF5CFF,0xFF6295,0xFF8778,0xF4A541,0xD7B900,0x90C414,0x52D228,0x20C692,0x18BADC,0x585858,0x101010,0x101010,0xFFFFFF,0xC6D8FF,0xD4CAFF,0xF0C4FF,0xFFBCFF,0xFFC0C5,0xFFC8BE,0xFFD9C3,0xFCE090,0xE2EA98,0xCAF2A0,0xA0EAE2,0xA0E2FA,0xC2C2C2,0x101010,0x101010};
    private static final int[] vc_wii =            {0x494949,0x00006A,0x090063,0x290059,0x42004A,0x490000,0x420000,0x291100,0x182700,0x003010,0x003000,0x002910,0x012043,0x000000,0x000000,0x000000,0x747174,0x003084,0x3101AC,0x4B0194,0x64007B,0x6B0039,0x6B2101,0x5A2F00,0x424900,0x185901,0x105901,0x015932,0x01495A,0x101010,0x000000,0x000000,0xADADAD,0x4A71B6,0x6458D5,0x8450E6,0xA451AD,0xAD4984,0xB5624A,0x947132,0x7B722A,0x5A8601,0x388E31,0x318E5A,0x398E8D,0x383838,0x000000,0x000000,0xB6B6B6,0x8C9DB5,0x8D8EAE,0x9C8EBC,0xA687BC,0xAD8D9D,0xAE968C,0x9C8F7C,0x9C9E72,0x94A67C,0x84A77B,0x7C9D84,0x73968D,0xDEDEDE,0x000000,0x000000};
    private static int[] custom = Arrays.copyOf(defaultPalette, defaultPalette.length);    
    private final static double att = 0.7;
    public static int[][] col = GetNESColors(defaultPalette);
    public static byte[][][] colbytes = NESColorsToBytes(col);
    
    private static int[][] GetNESColors(int[] colorarray) {
        //just or's all the colors with opaque alpha and does the color emphasis calcs
        //This set of colors matches current version of ntsc filter output
        for (int i = 0; i < colorarray.length; ++i) {
            colorarray[i] |= 0xff000000;
        }
        int[][] colors = new int[16][colorarray.length];
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
            for(int i = 8; i<16;i++){
            	for(int x = 0; x< colorarray.length;x++)
            		colors[i][x] = colors[i-8][x&0x30];
            		
            }

        }
        return colors;
    }

    public static void updatePalette(String palettename){
    	switch(palettename){
    	case "ntscHardwareFBX": col = GetNESColors(NTSCHardwareFBX); colbytes = NESColorsToBytes(col);break;
    	case "nesClassicFBX":col = GetNESColors(nesClassicFBX); colbytes = NESColorsToBytes(col);break;
    	case "compositeDirectFBX":col = GetNESColors(compositeDirectFBX); colbytes = NESColorsToBytes(col);break;
    	case "sonypvmFBX":col = GetNESColors(sonypvmFBX); colbytes = NESColorsToBytes(col);break;
    	case "vc_3ds":col = GetNESColors(vc_3ds); colbytes = NESColorsToBytes(col);break;
    	case "asq_reality_c":col = GetNESColors(asq_reality_c); colbytes = NESColorsToBytes(col);break;
    	case "av_famicom":col = GetNESColors(av_famicom); colbytes = NESColorsToBytes(col);break;
    	case "bmf_final_3":col = GetNESColors(bmf_final_3); colbytes = NESColorsToBytes(col);break;
    	case "consumer":col = GetNESColors(consumer); colbytes = NESColorsToBytes(col);break;
    	case "dougeff":col = GetNESColors(dougeff); colbytes = NESColorsToBytes(col);break;
    	case "drag3":col = GetNESColors(drag3); colbytes = NESColorsToBytes(col);break;
    	case "fceux":col = GetNESColors(fceux); colbytes = NESColorsToBytes(col);break;
    	case "fceux_15":col = GetNESColors(fceux_15); colbytes = NESColorsToBytes(col);break;
    	case "gameboy":col = GetNESColors(gameboy); colbytes = NESColorsToBytes(col);break;
    	case "grayscale":col = GetNESColors(grayscale); colbytes = NESColorsToBytes(col);break;
    	case "kizul":col = GetNESColors(kizul); colbytes = NESColorsToBytes(col);break;
    	case "nesticle":col = GetNESColors(nesticle); colbytes = NESColorsToBytes(col);break;
    	case "nestopia_rgb":col = GetNESColors(nestopia_rgb); colbytes = NESColorsToBytes(col);break;
    	case "nestopia_yuv":col = GetNESColors(nestopia_yuv); colbytes = NESColorsToBytes(col);break;
    	case "nintendulator_ntsc":col = GetNESColors(nintendulator_ntsc); colbytes = NESColorsToBytes(col);break;
    	case "rinao":col = GetNESColors(rinao); colbytes = NESColorsToBytes(col);break;
    	case "rockman_9":col = GetNESColors(rockman_9); colbytes = NESColorsToBytes(col);break;
    	case "rp2c04_0001":col = GetNESColors(rp2c04_0001); colbytes = NESColorsToBytes(col);break;
    	case "rp2c04_0002":col = GetNESColors(rp2c04_0002); colbytes = NESColorsToBytes(col);break;
    	case "rp2c04_0003":col = GetNESColors(rp2c04_0003); colbytes = NESColorsToBytes(col);break;
    	case "rp2c04_0004":col = GetNESColors(rp2c04_0004); colbytes = NESColorsToBytes(col);break;
    	case "terratec_cinergy":col = GetNESColors(terratec_cinergy); colbytes = NESColorsToBytes(col);break;
    	case "trebor":col = GetNESColors(trebor); colbytes = NESColorsToBytes(col);break;
    	case "vc_wii":col = GetNESColors(vc_wii); colbytes = NESColorsToBytes(col);break;
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
    public static void setCustomPalette(int[] pal){
    	for(int i = 0; i< custom.length;i++)
    		custom[i]= pal[i];
    }
	public static int[] getpalette(String selectedPalette) {
		switch(selectedPalette){
    	case "ntscHardwareFBX": return NTSCHardwareFBX;
    	case "nesClassicFBX":return nesClassicFBX;
    	case "compositeDirectFBX":return compositeDirectFBX;
    	case "sonypvmFBX": return sonypvmFBX;
    	case "vc_3ds": return vc_3ds;
    	case "asq_reality_c":return asq_reality_c;
    	case "av_famicom": return av_famicom;
    	case "bmf_final_3":return bmf_final_3;
    	case "consumer": return consumer;
    	case "dougeff": return dougeff;
    	case "drag3": return drag3;
    	case "fceux": return fceux;
    	case "fceux_15":return fceux_15;
    	case "gameboy":return gameboy;
    	case "grayscale":return grayscale;
    	case "kizul": return kizul;
    	case "nesticle":return nesticle;
    	case "nestopia_rgb":return nestopia_rgb;
    	case "nestopia_yuv":return nestopia_yuv;
    	case "nintendulator_ntsc":return nintendulator_ntsc;
    	case "rinao":return rinao;
    	case "rockman_9":return rockman_9;
    	case "rp2c04_0001":return rp2c04_0001;
    	case "rp2c04_0002":return rp2c04_0002;
    	case "rp2c04_0003":return rp2c04_0003;
    	case "rp2c04_0004":return rp2c04_0004;
    	case "terratec_cinergy":return terratec_cinergy;
    	case "trebor":return trebor;
    	case "vc_wii":return vc_wii;
    	case "Custom" : return custom;
    	default: return defaultPalette;
    	}
	}
}