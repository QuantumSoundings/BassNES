package mappers;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import audio.NamcoSound;
import audio.VRC6Pulse;
import audio.VRC6Saw;
import ui.UserSettings;

public class NSFPlayer extends Mapper{

	private static final long serialVersionUID = 2962869930880758980L;
	//Extra Audio Channels
	private VRC6Pulse vrc6pulse1;
	private VRC6Pulse vrc6pulse2;
	private VRC6Saw vrc6saw;
	private NamcoSound namco;
	private byte[] namcomemory;
	private int soundAddress;
	private boolean addrAutoInc;
	
	//NSF Header Information
	private byte[] initialbanks;
	private int initaddr;
	private int playaddr;
	private int playspeed;
	private int currentsong;
	private int totalsongs;
	private String title;
	private String artist;
	
	//Player Variables
	private byte irqstatus;
	private int irqcounter;
	private boolean irqEnable;
	private int irqreload;
	private boolean doingIRQ;
	private int nextirq;
	private int trackcutoff = 2*60*60;
	private int tracktimer= 0;
	private String tracktimestring = timeformat(trackcutoff);
	private boolean doingBanking;
	private Font largefont;
	private Font smallfont;
	
	//Player Control Variables
	boolean pause = false;
	boolean primednext;
	boolean primedprev;
	boolean looping = false;
	boolean playingforever = false;
	
	/*****************************************
	* NSF BIOS by Quietust, all credits to him!
	* Taken from the Nintendulator source code:
	* http://www.qmtpro.com/~nes/nintendulator/
	* See below for assembly source code
	* Found In the Mesen source code. Also credit
	* to him!
	******************************************/
	int[] nsfbios = {
		0xFF,0xFF,0xFF,0x78,0xA2,0xFF,0x8E,0x17,0x40,0xE8,0x20,0x30,0x3F,0x8E,0x00,0x20,
		0x8E,0x01,0x20,0x8E,0x12,0x3E,0x58,0x4C,0x17,0x3F,0x48,0x8A,0x48,0x98,0x48,0xAE,
		0x12,0x3E,0xF0,0x59,0xCA,0xF0,0xDC,0x20,0xF9,0x3F,0x68,0xA8,0x68,0xAA,0x68,0x40,
		0x8E,0x15,0x40,0xAD,0x13,0x3E,0x4A,0x90,0x09,0x8E,0x02,0x90,0x8E,0x02,0xA0,0x8E,
		0x02,0xB0,0x4A,0x90,0x0D,0xA0,0x20,0x8C,0x10,0x90,0x8E,0x30,0x90,0xC8,0xC0,0x26,
		0xD0,0xF5,0x4A,0x90,0x0B,0xA0,0x80,0x8C,0x83,0x40,0x8C,0x87,0x40,0x8C,0x89,0x40,
		0x4A,0x90,0x03,0x8E,0x15,0x50,0x4A,0x90,0x08,0xCA,0x8E,0x00,0xF8,0xE8,0x8E,0x00,
		0x48,0x4A,0x90,0x08,0xA0,0x07,0x8C,0x00,0xC0,0x8C,0x00,0xE0,0x60,0x20,0x30,0x3F,
		0x8A,0xCA,0x9A,0x8E,0xF7,0x5F,0xCA,0x8E,0xF6,0x5F,0xA2,0x7F,0x85,0x00,0x86,0x01,
		0xA8,0xA2,0x27,0x91,0x00,0xC8,0xD0,0xFB,0xCA,0x30,0x0A,0xC6,0x01,0xE0,0x07,0xD0,
		0xF2,0x86,0x01,0xF0,0xEE,0xA2,0x14,0xCA,0x9D,0x00,0x40,0xD0,0xFA,0xA2,0x07,0xBD,
		0x08,0x3E,0x9D,0xF8,0x5F,0xCA,0x10,0xF7,0xA0,0x0F,0x8C,0x15,0x40,0xAD,0x13,0x3E,
		0x29,0x04,0xF0,0x10,0xAD,0x0E,0x3E,0xF0,0x03,0x8D,0xF6,0x5F,0xAD,0x0F,0x3E,0xF0,
		0x03,0x8D,0xF7,0x5F,0xAE,0x11,0x3E,0xBD,0x04,0x3E,0x8D,0x10,0x3E,0xBD,0x06,0x3E,
		0x8D,0x11,0x3E,0x8C,0x12,0x3E,0xAD,0x12,0x3E,0x58,0xAD,0x10,0x3E,0x20,0xF6,0x3F,
		0x8D,0x13,0x3E,0x4C,0x17,0x3F,0x6C,0x00,0x3E,0x6C,0x02,0x3E,0x03,0x3F,0x1A,0x3F
	};
	
	public NSFPlayer(){
		super();
		System.out.println("Making an NSF Player!");
		Map<TextAttribute, Object> attributes = new HashMap<>();

		attributes.put(TextAttribute.FAMILY, "Default");
		attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_SEMIBOLD);
		attributes.put(TextAttribute.SIZE, 14);
		largefont = Font.getFont(attributes);
		attributes.put(TextAttribute.FAMILY, "Default");
		attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_SEMIBOLD);
		attributes.put(TextAttribute.SIZE, 10);
		smallfont = Font.getFont(attributes);
	}
	
	@Override
	public void loadData(byte[] data, byte[] banks,int loadaddr){
		initialbanks = banks;
		PRG_ROM = new byte[8][0x1000];
		doingBanking=false;
		for(byte b:banks){
			if(b!=0){
				doingBanking=true;
				break;
			}
		}
		if(!doingBanking){		
			int cBank = (loadaddr/0x1000) -8;
			int offset = 0x1000-(loadaddr%0x1000);
			System.out.println(Integer.toHexString(loadaddr)+" "+cBank+" "+Integer.toHexString(offset));
			System.arraycopy(data, 0, PRG_ROM[cBank], loadaddr%0x1000, offset);
			cBank++;
			while(offset+0x1000<data.length){
				System.arraycopy(data, offset, PRG_ROM[cBank], 0, 0x1000);
				offset+=0x1000;
				cBank++;
			}
			if(offset<data.length){
				System.arraycopy(data, offset, PRG_ROM[cBank], 0, data.length-offset);
			}
			
		}
		else{
			int padding = 0xfff&loadaddr;
			byte[] databanks = new byte[padding+data.length];
			System.arraycopy(data, 0, databanks, padding, data.length);
			PRGbanks = new byte[databanks.length/0x1000][0x1000];
			for(int i=0;i*0x1000<databanks.length;i++){
				PRGbanks[i]=Arrays.copyOfRange(databanks, i*0x1000, (i*0x1000)+0x1000);
			}
			PRG_ROM[0] = PRGbanks[banks[0]];PRG_ROM[1] = PRGbanks[banks[1]];
			PRG_ROM[2] = PRGbanks[banks[2]];PRG_ROM[3] = PRGbanks[banks[3]];
			PRG_ROM[4] = PRGbanks[banks[4]];PRG_ROM[5] = PRGbanks[banks[5]];
			PRG_ROM[6] = PRGbanks[banks[6]];PRG_ROM[7] = PRGbanks[banks[7]];
		}
	}
	byte soundchip;
	@Override
	public void addExtraAudio(byte b){
		soundchip=b;
		if((b&1)==1){//vrc6
			vrc6pulse1 = new VRC6Pulse();
			vrc6pulse2 = new VRC6Pulse();
			vrc6saw = new VRC6Saw();
			apu.addExpansionChannel(vrc6pulse1);
			apu.addExpansionChannel(vrc6pulse2);
			apu.addExpansionChannel(vrc6saw);
		}
		if((b&2)==2){}//vrc7
		if((b&4)==4){}//fds
		if((b&8)==8){}//mmc5
		if((b&16)==16){//namco
			namcomemory = new byte[0x80];
			namco = new NamcoSound(namcomemory);
			apu.addExpansionChannel(namco);
		}
	}
	@Override
	public void setNSFVariables(int play,int init,int speed,int startsong,int total, int tuneregion,String name, String artist){
		initaddr = init;
		playaddr = play;
		playspeed = speed;
		playspeed = (int) (playspeed*(1789773.0/1000000.0));
		currentsong = startsong-1;
		totalsongs = total;
		title = name;
		this.artist = artist;
	}
	@Override
	public void setCHR(byte[] chr){
		init();
		doingIRQ=true;
		cpu.doIRQ++;
	}
	private void soundwrite(byte b){
		if(soundAddress<0x40){
			namcomemory[soundAddress] = b;
		}
		else{
			namco.registerWrite(soundAddress%8, b, (soundAddress/8)-8);
			namcomemory[soundAddress] = b;
			if(soundAddress==0x7f)
				namco.setEnables((b&0x70)>>4);				
		}
		if(addrAutoInc){
			soundAddress++;
			if(soundAddress==0x80)
				soundAddress = 0;
		}
	}
	private byte soundread(){
		byte b = namcomemory[soundAddress];
		if(addrAutoInc){
			soundAddress++;
			if(soundAddress==0x80)
				soundAddress = 0;
		}
		return b;
	}
	private byte varread(int index){
		switch(index){
		case 0x3e00:return (byte) (initaddr&0xff);
		case 0x3e01:return (byte) ((initaddr>>8)&0xff);
		case 0x3e02:return (byte) (playaddr&0xff);
		case 0x3e03:return (byte) ((playaddr>>8)&0xff);
		case 0x3e04:return (byte) (playspeed&0xff);
		case 0x3e05:return (byte) (playspeed&0xff);
		case 0x3e06:return (byte) ((playspeed>>8)&0xff);
		case 0x3e07:return (byte) ((playspeed>>8)&0xff);
		case 0x3e08:return initialbanks[0];
		case 0x3e09:return initialbanks[1];
		case 0x3e0a:return initialbanks[2];
		case 0x3e0b:return initialbanks[3];
		case 0x3e0c:return initialbanks[4];
		case 0x3e0d:return initialbanks[5];
		case 0x3e0e:return initialbanks[6];
		case 0x3e0f:return initialbanks[7];
		case 0x3e10:return (byte) (currentsong);
		case 0x3e11:return 0;
		case 0x3e12:
			if(doingIRQ)
				cpu.doIRQ--;
			doingIRQ=false;
			return irqstatus;
		case 0x3e13:return (byte) (soundchip&0x3f);
		}
		return 0;
	}
	
	private void varwrite(int index,byte b){		
		switch(index){
		case 0x3e10:
			irqreload&=0xff00;
			irqreload|=(b&0xff);
			break;
		case 0x3e11:
			irqreload&=0xff;
			irqreload|=(b&0xff)<<8;
			break;
		case 0x3e12:
			irqcounter = irqreload*5;
			irqEnable = b!=0;
			break;
		case 0x3e13:
			irqcounter = irqreload;
			break;
		}
	}
	
	@Override
	public byte cpuread(int index){
		if(index<=0x800){
			return cpu_ram[index];
		}
		else if(index>=0x3e00&&index<=0x3e13)
			return varread(index);
		else if(index>=0x3f00&&index<=0x3fff)
			return (byte) nsfbios[index-0x3f00];
		else if(index==0x4015)
			return apu.readRegisters(index);
		else if(index>=0x4040&&index<=0x407f){}//FDS
		else if(index==0x4090){}//FDS
		else if(index==0x4092){}//FDS
		else if(index==0x4800)//Namco
			return soundread();
		else if(index==0x5205||index==0x5206){}//MMC5
		else if(index>=0x5c00&&index<=0x5ff5){}//MMC5
		else if(index>=0x6000&&index<=0xfff9){//PRG_ROM
			if(index<=0x7fff)
				return PRG_RAM[index%0x2000];
			return PRG_ROM[(index/0x1000)-8][index%0x1000]; 
		}
		else
			switch(index){
			case 0xffff: return 0x3f;
			case 0xfffe: return 0x1a;
			}
		return 0;
	}
	@Override
	public void cpuwrite(int index,byte b){
		if(index<=0x800)
			cpu_ram[index]=b;
		else if(index>=0x3e00&&index<=0x3e13)
			varwrite(index,b);
		else if(index>=0x3f00&&index<=0x3fff)  //NSF bios
			nsfbios[index-0x3f00] = b;		
		else if(index>=0x4000&&index<=0x4013)  //APU
			apu.writeRegister(index, b);
		else if(index==0x4015||index==0x4017)  //APU
			apu.writeRegister(index, b);
		else if(index>=0x4040&&index<=0x4080){}//FDS
		else if(index>=0x4082&&index<=0x408a){}//FDS
		else if(index==0x4800)                 //namco
			soundwrite(b);
		else if(index==0x5205||index==0x5206){}//MMC5 multiply
		else if(index>=0x5c00&&index<=0x5ff5){}//MMC5 exram
		else if(index==0x5ff6||index==0x5ff7){}//FDS bank switching
		else if(index>=0x5ff8&&index<=0x5fff){  //Bank Switching
			if(doingBanking)
				PRG_ROM[index%8] = PRGbanks[(b&0xff)];
		}
		else if(index>=0x6000&&index<=0x7fff)  //PRG_RAM
			PRG_RAM[index%0x2000] = b;
		else{                                  //Various registers
			switch(index){
			case 0x9000:vrc6pulse1.registerWrite(0, b, 0);break;
			case 0x9001:vrc6pulse1.registerWrite(1, b, 0);break;
			case 0x9002:vrc6pulse1.registerWrite(2, b, 0);break;
			case 0xa000:vrc6pulse2.registerWrite(0, b, 0);break;
			case 0xa001:vrc6pulse2.registerWrite(1, b, 0);break;
			case 0xa002:vrc6pulse2.registerWrite(2, b, 0);break;
			case 0xb000:vrc6saw.registerWrite(0, b, 0);break;
			case 0xb001:vrc6saw.registerWrite(1, b, 0);break;
			case 0xb002:vrc6saw.registerWrite(2, b, 0);break;
			case 0xf800:
				soundAddress = (b&0x7f);
				addrAutoInc = (b&0x80)!=0;
				break;	
			}
		}
	}
	private void init(){
		cpu.program_counter=0x3f03;	
	}
	private void nextTrack(){
		currentsong+=1;
		nextirq=0;
		tracktimer=0;
	}
	private void prevTrack(){
		if(tracktimer/60<5){
			currentsong-=1;
			if(currentsong<0)
				currentsong=0;
		}
		nextirq=0;
		tracktimer=0;
	}
	boolean initdone = false;
	
	
	
	BufferedImage image = new BufferedImage(256,240,BufferedImage.TYPE_INT_RGB);
	private String timeformat(int i){
		String out = i/(60*60)+":";
		int seconds = (i/60)%60;
		out += seconds<10?"0"+seconds:seconds;
		return out;
	}
	private void drawscreen(){
		Graphics g = image.getGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, 256, 240);
		g.setColor(Color.BLUE);
		g.setFont(largefont);
		g.drawString("Key Bindings", 0, 20);
		g.drawString("NSF Player Status:", 0, 100);
		g.drawString("Title: "+title, 0, 180);
		g.drawString("Artist: "+artist, 0, 210);
		g.drawString("Track "+(currentsong+1)+"/"+totalsongs+"         "+timeformat(tracktimer)+"/"+tracktimestring
				, 0, 230);
		g.setFont(smallfont);
		g.drawString("Next Track: "+KeyEvent.getKeyText(UserSettings.nsfnext), 0, 35);
		g.drawString("Prev Track: "+KeyEvent.getKeyText(UserSettings.nsfprev), 0, 45);
		g.drawString("Pause: "+KeyEvent.getKeyText(UserSettings.nsfpause), 0, 55);
		g.drawString("Loop: "+KeyEvent.getKeyText(UserSettings.nsfloop), 0, 65);
		g.drawString("Forever Mode: "+KeyEvent.getKeyText(UserSettings.nsfplayforever), 0, 75);
		g.drawString((!pause?"Playing":"Paused"), 0, 115);
		g.drawString((playingforever?"Playing Current Song Forever":""), 0, 125);
		g.drawString((looping?"Looping Current Song":""), 0, 135);
		g.dispose();
		image.getRGB(0, 0, 256, 240, ppu.renderer.colorized, 0, 256);	
	}
	private void pollPlayerControls(){
		boolean[] controls =system.pollController()[2];
		pause = controls[0];
		if(controls[1]){
			primednext = true;
		}
		else{
			if(primednext)
				nextTrack();
			primednext = false;
		}
		if(controls[2]){
			primedprev = true;
		}
		else{
			if(primedprev)
				prevTrack();
			primedprev = false;
		}
		playingforever = controls[3];
		looping = controls[4];
	}
	
	@Override
	public void runFrame() {
		if(!initdone)
			init();
		initdone = true;
		pollPlayerControls();
		while(cpu.program_counter!=0x3ff9&&!pause){
			cpu.run_cycle();
			if(irqEnable){
				irqcounter--;
				if(irqcounter==0){
					irqcounter=irqreload;
					if(!doingIRQ){
						cpu.doIRQ++;
						irqstatus = (byte) nextirq;
					}
					doingIRQ=true;
				}
			}
			apu.doCycle();
		}
		nextirq=2;
		cpu.run_cycle();cpu.run_cycle();cpu.run_cycle();cpu.run_cycle();cpu.run_cycle();
		apu.doCycle();apu.doCycle();apu.doCycle();apu.doCycle();apu.doCycle();
		if(!pause)
			if(++tracktimer==trackcutoff&&!playingforever){
				if(looping){
					nextirq=0;
					tracktimer=0;
				}
				else
					nextTrack();
			}
		drawscreen();
	}
}