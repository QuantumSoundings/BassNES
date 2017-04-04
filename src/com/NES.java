package com;
//import java.awt.Graphics;
//import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.JFrame;
//import javax.swing.JPanel;

import mappers.Mapper;

import java.io.FileInputStream;
//import java.io.FileNotFoundException;
import java.io.IOException;
//import java.io.InputStream;
//import java.util.Arrays;
//import java.util.Scanner;
public class NES implements Runnable {
	//Different system components
	private CPU_6502 cpu;
	private ppu2C02 ppu;
	private APU apu;
	private Mapper map;
	private Controller controller;
	private Controller controller2;
	int systemclock = 21477272;
	private NesDisplay display;
	//private JFrame frame;
	//private Graphics g;
	public volatile boolean flag = true;
	//Master clock speed.
	public NES(NesDisplay disp,JFrame f,File rom){
		display = disp;
		controller = new Controller();
		controller.setframe(f);
		controller2 = new Controller();
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
		int i = 0;
		//boolean skip = true;
		//boolean skip2=false;
		//int z = 0;
		long start = 0,stop = 0;
		//mem.printMemory(0x8000, 0x200);
		while(flag){
			
			/*if(!skip){
				try {
					
					
					if(i%3==0){
						System.out.println("Timing: "
								+" PPU scanline:"+ppu.scanline
								+" VRAM ADDR: " +Integer.toHexString(ppu.v)
								+" Ticks: "+ ppu.pcycle+"/"+(c%(341/3.0))
								+" Rendering?: "+ppu.dorender()
								+" PPUCTRL:"+Integer.toBinaryString(map.cpureadu(0x2000))
								+" PPUSTATUS:"+Integer.toBinaryString(map.cpureadu(0x2002)));
						cpu.debug(0);
						//mem.printMemory(0, 0x6);

						//mem.printMemoryppu(0x3f00, 0x20);
					}
					//int g =System.in.read();
					//if(z<8)
					//skip=true;
					
					//mem.printMemoryppu(0x2000, 0x400);
					
				} catch (IOException e) {
					e.printStackTrace();
				}
				}
			//if(cpu.program_counter==0x0){//&&ppu.scanline>234){
				//mem.printMemorypu(0x3f00, 0x20);
				//mem.printMemoryppu(0x2000, 0x400);
				//skip = false;
				//cpu.debug(0);
				
				//z++;
				//mem.printMemory(0x3a0, 0x6);
				//mem.printMemory(0x6000, 0x200);
				//System.out.println(Integer.toHexString(cpu.current_instruction));
				//mem.printMemoryppu(0x3f00, 0x20);
				//mem.write(0x2000, (byte)(0));
				//mem.printMemoryppu(0x2000, 0x400);
			//}*/
				
		if(i%3==0){
			cpu.run_cycle();
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
		if(ppu.scanline%65==0&&ppu.pcycle==1){
			apu.doFrameStep=true;
		}
		if(i>29658){
			map.blockppu=false;
		}		
		if(controller.checkDebug())
			ppu.dodebug=true;
		if(ppu.vfresh){
			stop = System.currentTimeMillis()-start;
			display.sendFrame(ppu.ntsc.bi);
			ppu.ntsc.submit();
			//stop =17;
			//if(framecount%2==0)
			//apu.update();
			//mem.printMemoryppu(0x2000, 0x400);
			if(stop<17)
				try {
					Thread.sleep(17-stop);
				} catch ( InterruptedException e){//  | IOException e) {
					e.printStackTrace();
				}
			
			ppu.vfresh=false;
			start = System.currentTimeMillis();
		}
		i++;
		}
		apu.synth.stop();
	}
	public void loadrom(File rom) throws IOException{
		//File rom = new File(System.getProperty("user.dir")+"/dkj.nes");
		FileInputStream sx = new FileInputStream(rom);
		byte[] header = new byte[16];
		for(int i=0;i<header[4];i++){
			
		}
		sx.read(header);
		byte[] PRG_ROM = new byte[16384*Byte.toUnsignedInt(header[4])];
		sx.read(PRG_ROM);
		byte[] CHR_ROM = new byte[8192*Byte.toUnsignedInt(header[5])];
		sx.read(CHR_ROM);
		int id = Byte.toUnsignedInt(header[6])>>4;
		id|= Byte.toUnsignedInt(header[7])&0xf0;
		map = Mapper.getmapper(id);
		System.out.println("PRG_ROM:"+(PRG_ROM.length/0x400)+"KB");
		map.setPRG(PRG_ROM);
		System.out.println("CHR_ROM:"+(CHR_ROM.length/0x400)+"KB");
		map.setCHR(CHR_ROM);
		map.setMirror(header[6]&1);
		//mapperSetup();
		sx.close();
		//mem.writeppu(0, CHR_ROM);
		//mem.writeppu(0x1000, CHR_ROM2);
		//mem.printMemory(0xc000,50);
		//mem.printMemoryppu(0, 0x1fff);
		//System.out.println("Loaded the rom!");
	}
}
