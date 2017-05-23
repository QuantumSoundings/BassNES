package com;
import java.io.File;

import mappers.Mapper;
import ui.SystemUI;
import ui.UserSettings;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;

public class NES implements Runnable {
	private volatile Mapper map;
	private String romName;
	private SystemUI system;
	File save;
	
	//clock settings NTSC
	//Master clock speed.
	int systemclock = 21477272;
	final int cpudiv = 12;
	final int palcpudiv = 16;
	//final int apudiv = 24;
	final int ppudiv = 4;
	final int palppudiv = 5;
	public int cpuclock=0;
	private int mclock;
	
	boolean batteryExists;
	boolean pal;
	public volatile boolean flag = true;
	public volatile boolean doaudio = true;
	public volatile boolean pause = false;
	
	//Frame rate/timing variables
	private long frameStartTime;
	private long frameStopTime;
	private long fpsStartTime;
	//private long fpsStopTime;
	private double currentFPS;
	private int framecount=0;
	
	//debugging vars
	//private int p=0;
	//private double c =0.0;
	
	public NES(File rom,SystemUI s){
		system = s;
		romName = rom.getName().substring(0,rom.getName().length()-4);
		try {
			loadrom(rom);
		} catch (IOException e) {
			e.printStackTrace();
		}		
		map.setNes(this);
		map.setSystem(system);
		map.cpu.setPC(((map.cpureadu(0xfffd)<<8)|(map.cpureadu(0xfffc))));
	}
	public void run(){
		System.out.println("NES STARTED RUNNING");
		if(pal)
			palrun();
		while(flag){
			runFrame();
			if(pause){
				while(pause){
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
		if(batteryExists)
			try {
				saveGame();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	public void palrun(){
		while(flag){
			runPalFrame();
			if(pause){
				while(pause){
					try{
						Thread.sleep(200);
					} catch(InterruptedException e){
						e.printStackTrace();
					}
				}
			}
		}
	}
	public void runPalFrame(){
		frameStartTime = System.nanoTime();
		while(!map.ppu.doneFrame){
			if(mclock%palcpudiv==0){
				map.cpu.run_cycle();
				map.apu.doCycle();
			}
			if(mclock%palppudiv==0){
				map.ppu.doCycle();
			}
			mclock++;
		}
		map.ppu.doneFrame=false;
		frameStopTime = System.nanoTime() - frameStartTime;
		if(frameStopTime<20000000&&UserSettings.frameLimit)
			try {
				while(System.nanoTime()-frameStartTime<20000000){
					if(UserSettings.politeFrameTiming)
						Thread.sleep(0,100000);
				}
			} catch ( InterruptedException e){
				e.printStackTrace();
			}
		if(framecount%50==0){
			double x = 1000.0/(System.currentTimeMillis()-fpsStartTime);
			currentFPS = x*50;
			fpsStartTime=System.currentTimeMillis();
		}
		framecount++;
	}
	boolean dodebug;
	public void runFrame(){
		frameStartTime = System.nanoTime();
		map.runFrame();
		frameStopTime = System.nanoTime() - frameStartTime;
		if(frameStopTime<15800000&&UserSettings.frameLimit)
			try {
				while(System.nanoTime()-frameStartTime<15800000){
					if(UserSettings.politeFrameTiming)
						Thread.sleep(0,100000);
				}
			} catch ( InterruptedException e){
				e.printStackTrace();
			}
		if(framecount%60==0){
			double x = 1000.0/(System.currentTimeMillis()-fpsStartTime);
			currentFPS = x*60;
			fpsStartTime=System.currentTimeMillis();
		}
		framecount++;
		system.videoCallback(map.ppu.renderer.colorized);
	}
	public double getFPS(){
		return currentFPS;
	}
	public void saveState(String slot) throws IOException{
		FileOutputStream fout = new FileOutputStream(slot);
		ObjectOutputStream out = new ObjectOutputStream(fout);
		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		out.writeObject(map);
		out.writeObject(map.apu);
		out.writeObject(map.cpu);
		out.writeObject(map.ppu);
		out.close();
	}
	public void restoreState(String slot) throws IOException, ClassNotFoundException{
		FileInputStream fin = new FileInputStream(slot);
		ObjectInputStream in = new ObjectInputStream(fin);
		pause = true;
		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//map.apu.mix.audio.close();
		map = (Mapper) in.readObject();
		map.apu = (APU) in.readObject();
		//map.apu.mix.audio.restartSDL();
		map.cpu = (CPU_6502) in.readObject();
		map.ppu = (ppu2C02) in.readObject();
		map.setSystem(system);
		in.close();
		pause = false;
	}
	public void pause(){pause = true;}
	public void unpause(){pause=false;}
	public void loadSave() throws IOException{
		save = new File(romName+".sav");
		if(save.exists()){
				System.out.println("Save Found! Loading...");
				FileInputStream sx = new FileInputStream(save);
				byte[] savearray = new byte[0x2000];
				sx.read(savearray);
				map.restoreSave(savearray);
				sx.close();
		}
		else
			System.out.println("Save not found!");
	}
	public void restartaudio(){
		map.apu.updateaudio();
	}
	public void saveGame() throws IOException{
		System.out.println("Attempting to save game.");
		save = new File(romName+".sav");
		if(save.exists()){
			save.delete();
		}
		save.createNewFile();
		FileOutputStream sx = new FileOutputStream(save);
		byte[] savearray = map.getSave();
		sx.write(savearray);
		sx.close();
	}
	public void loadrom(File rom) throws IOException{
		//rom = new File(System.getProperty("user.dir")+"/cv3j.nes");
		FileInputStream sx = new FileInputStream(rom); 
		byte[] header = new byte[16];
		sx.read(header);
		byte[] PRG_ROM = new byte[16384*Byte.toUnsignedInt(header[4])];
		sx.read(PRG_ROM);
		byte[] CHR_ROM = new byte[8192*Byte.toUnsignedInt(header[5])];
		sx.read(CHR_ROM);
		batteryExists = (header[6]&2)!=0?true:false;
		int id = Byte.toUnsignedInt(header[6])>>4;
		id|= Byte.toUnsignedInt(header[7])&0xf0;
		map = Mapper.getmapper(id);
		System.out.println("PRG_ROM:"+(PRG_ROM.length/0x400)+"KB");
		map.setPRG(PRG_ROM);
		System.out.println("CHR_ROM:"+(CHR_ROM.length/0x400)+"KB");
		map.setCHR(CHR_ROM);
		map.setMirror(header[6]&1);
		if(header[9]==1||rom.getName().contains("(E)")){
			pal = true;
			System.out.println("Pal Game");
		}
		System.out.println(Integer.toBinaryString(Byte.toUnsignedInt(header[9])));
		map.ppu.setpal(pal);
		sx.close();
		if(batteryExists)
			loadSave();
	}
	
	@SuppressWarnings("unused")
	private void debug(){
		Scanner s = new Scanner(System.in);
		System.out.println("Timing: "
				+" PPU scanline:"+map.ppu.scanline
				+" VRAM ADDR: " +Integer.toHexString(map.ppu.v)
				+" Ticks: "+ map.ppu.pcycle//+"/"+(Integer.toHexString((int)c))
				+" Rendering?: "+map.ppu.dorender()
				+" vBlank:"+map.ppu.PPUSTATUS_vb
				+" PPUSTATUS:"+Integer.toBinaryString(map.ppu.PPUSTATUS));
		map.cpu.debug(0);
		String st =s.nextLine();
		if(st.equals("c"))
			dodebug=false;
		else
			dodebug=true;
		s.close();
	}
}
