package com;
//import java.awt.Graphics;
//import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.JFrame;
//import javax.swing.JPanel;

import mappers.Mapper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
//import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;
//import java.io.InputStream;
//import java.util.Arrays;
//import java.util.Scanner;
public class NES implements Runnable {
	//Different system components
	private CPU_6502 cpu;
	private ppu2C02 ppu;
	public APU apu;
	public Mapper map;
	private String romName;
	public Controller controller;
	public Controller controller2;
	File save;
	int systemclock = 21477272;
	boolean batteryExists;
	private NesDisplay display;
	//private JFrame frame;
	//private Graphics g;
	public volatile boolean flag = true;
	public volatile boolean doaudio = true;
	//Master clock speed.
	public NES(NesDisplay disp,JFrame f,File rom,Properties prop){
		romName = rom.getName().substring(0,rom.getName().length()-4);
		display = disp;
		controller = new Controller(prop,1);
		controller.setframe(f);
		controller2 = new Controller(prop,2);
		controller2.setframe(f);
		try {
			loadrom(rom);
		} catch (IOException e) {
			e.printStackTrace();
		}
		ppu = new ppu2C02(map);
		cpu = new CPU_6502(map);
		apu = new APU(map);
		map.setcomponents(cpu, ppu,controller,controller2,apu);
		cpu.setPC(((map.cpureadu(0xfffd)<<8)|(map.cpureadu(0xfffc))));
		//cpu.setPC(0xc000);//cpu.setPC(0x8706);
	}
	int p=0;
	double c =0.0;
	int cpuclock=0;
	int framecount=0;
	public void run(){
		System.out.println("NES STARTED RUNNING");
		if(batteryExists)
			try {
				loadSave();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		int i = 0;
		boolean skip = true;
		Scanner s = new Scanner(System.in);
		//boolean skip2=false;
		//int z = 0;
		long start = 0,stop = 0;
		
		//mem.printMemory(0x8000, 0x200);
		while(flag){
			if(!skip){
				
					if(i%3==0){
						System.out.println("Timing: "
								+" PPU scanline:"+ppu.scanline
								+" VRAM ADDR: " +Integer.toHexString(ppu.v)
								+" Ticks: "+ ppu.pcycle+"/"+(c%(341/3.0))
								+" Rendering?: "+ppu.dorender()
								+" PPUCTRL:"+Integer.toBinaryString(ppu.PPUCTRL)
								+" PPUSTATUS:"+Integer.toBinaryString(ppu.PPUSTATUS));
						cpu.debug(0);
						//map.printMemory(0x3a0, 0x10);
						//map.printMemoryPPU(0x3f00, 0x20);
						//map.printMemoryPPU(0x2040, 0x20);
						//map.printMemoryPPU(0, 0x200);
						map.printOAMPPU(0, 256);
						map.printMemoryPPU(0x2fc0, 0x50);
					}
					String t = s.nextLine();
					if(t.equals("c"))
						skip = true;
					else if(t.equals("i"))
						cpu.IFlag=false;
					else if(t.equals("e"))
						map.cpuwrite(0xe000, (byte)1);
					else
						skip = false;
					
				
				}
			if(cpu.current_instruction==0x12&&controller.checkDebug()){//cpu.program_counter==0xe018){//&&ppu.scanline>234){
				skip = false;
				//cpu.debug(0);
			}
				
		if(i%3==0){
			cpu.run_cycle();
			if(doaudio)
				apu.doCycle(cpuclock);
			cpuclock++;
			c++;
		}
		p++;
		ppu.render();
		if(ppu.oddskip){
			c+=(1/3.0);
			ppu.oddskip=false;
		}
		if(ppu.scanline%65==0&&ppu.pcycle==1&&doaudio){
			apu.doFrameStep=true;
		}
		if(i>29600){
			map.blockppu=false;
		}		
		//if(controller.checkDebug())
		//	ppu.dodebug=true;
		if(ppu.vfresh){
			stop = System.currentTimeMillis()-start;
			display.sendFrame(ppu.ntsc.bi);
			ppu.ntsc.submit();
			//stop =17;
			//if(framecount%2==0)
			//apu.update();
			//mem.printMemoryppu(0x2000, 0x400);
			if(stop<16)
				try {
					Thread.sleep(16-stop);
				} catch ( InterruptedException e){//  | IOException e) {
					e.printStackTrace();
				}
			
			ppu.vfresh=false;
			start = System.currentTimeMillis();
		}
		i++;
		}
		apu.synth.stop();
		if(batteryExists)
			try {
				saveGame();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
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
		rom = new File(System.getProperty("user.dir")+"/mmc3_test/4-scanline_timing.nes");
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
		sx.close();
	}
}
