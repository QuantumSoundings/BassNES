package core;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

import core.exceptions.UnSupportedMapperException;
import core.mappers.Mapper;
import core.video.NesColors;
/**
 * Object representing an NES console. This and NesSettings are the only two files that should be interacted with.
 * Object is a runnable, so it can occupy its own thread or be controlled through a main program.
 * @author Jordan Howe
 *
 */
public class NES implements Runnable,NESAccess {
	private volatile Mapper map;
	private String romName;
	private NESCallback system;
	private File save;
	
	private boolean batteryExists;
	private boolean pal;
	private volatile boolean flag = true;
	private volatile boolean pause = false;
	private volatile boolean pauseConfirmed = false;
	private boolean nsfplayer = false;
	
	//Frame rate/timing variables
	private long frameStartTime;
	private long frameStopTime;
	private long fpsStartTime;
	private double currentFPS;
	private int framecount=0;
	
	
	public NES(NESCallback sys){
		system = sys;
	}
	public void setCallback(NESCallback system){
		this.system=system;
	}
	public final void loadRom(File rom) throws IOException, UnSupportedMapperException{
		//rom = new File(System.getProperty("user.dir")+"/cv3j.nsf");
		romName = rom.getName().substring(0,rom.getName().length()-4);
		String ext = rom.getName().toLowerCase().substring(rom.getName().lastIndexOf(".")+1);
		switch(ext){
		case "nes": loadiNES(rom);break;
		case "nsf": loadNSF(rom);break;
		default:
		}
		map.setNes(this);
		map.setSystem(system);
		map.setInitialPC();
		
	}
	
	private void loadiNES(File rom) throws IOException, UnSupportedMapperException{
		FileInputStream sx = new FileInputStream(rom); 
		byte[] header = new byte[16];
		sx.read(header);
		if(header[0]==0x4e&&header[1]==0x45&&header[2]==0x53&&header[3]==0x1a){//verified header
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
			map.setPRGRAM(batteryExists);
			map.setMirror(header[6]&1);
			if(header[9]==1||rom.getName().contains("(E)")){
				pal = true;
				System.out.println("Pal Game");
			}
			map.ppu.setpal(pal);
			
			if(batteryExists)
				loadSave();
		}
		sx.close();
	}
	
	private void loadNSF(File rom) throws IOException, UnSupportedMapperException{
		FileInputStream sx = new FileInputStream(rom); 
		byte[] header = new byte[0x80];
		sx.read(header);
		if(header[0]==0x4e&&header[1]==0x45
				&&header[2]==0x53&&header[3]==0x4d&&header[4]==0x1a){//verified header
			//int versionNumber = header[5];
			int totalsongs = Byte.toUnsignedInt(header[6]);
			int startsong = Byte.toUnsignedInt(header[7]);
			int dataloadaddr = ((header[9]&0xff)<<8)|(header[8]&0xff);
			int datainitaddr = ((header[0xb]&0xff)<<8)|(header[0xa]&0xff);
			int dataplayaddr = ((header[0xd]&0xff)<<8)|(header[0xc]&0xff);
			String songname = new String(Arrays.copyOfRange(header, 0xe,0x2d));
			String artistname = new String(Arrays.copyOfRange(header, 0x2e, 0x4d));
			int playspeed = ((header[0x6f]&0xff)<<8)|(header[0x6e]&0xff);
			byte[] bankswitch = Arrays.copyOfRange(header, 0x70, 0x78);
			//int palplayspeed = ((header[0x79]&0xff)<<8)|(header[0x78]&0xff);
			int tuneregion = header[0x7a]&3;
			byte extrasoundchips = header[0x7b];
			long size = rom.length()-0x80;
			System.out.println(size/0x400 +"KB");
			byte[] data = new byte[(int) size];
			
			map = Mapper.getmapper(1001);
			sx.read(data);
			map.loadData(data, bankswitch, dataloadaddr);
			map.addExtraAudio(extrasoundchips);
			map.setNSFVariables(dataplayaddr, datainitaddr, playspeed,startsong,totalsongs,tuneregion,songname,artistname);
			map.setCHR(new byte[0]);
			map.setSystem(system);
			nsfplayer = true;
		}
		sx.close();
	}
	public final void run(){
		System.out.println("NES STARTED RUNNING");
		while(flag){
			autoRunFrame();
			if(pause){
				pauseConfirmed = true;
				while(pause){
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				pauseConfirmed = false;
			}
		}
		if(batteryExists)
			try {
				saveGame();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	public final void exit(){
		flag = false;
	}
	private void autoRunFrame(){
		frameStartTime = System.nanoTime();
		map.runFrame();
		frameStopTime = System.nanoTime() - frameStartTime;
		if(frameStopTime<16000000&&NesSettings.frameLimit)
			try {
				while(System.nanoTime()-frameStartTime<16000000){
					if(NesSettings.politeFrameTiming)
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
	public final void runFrame(){
		map.runFrame();
		system.videoCallback(map.ppu.renderer.colorized);
	}
	public final double getFPS(){
		return currentFPS;
	}
	public final void saveState(String slot) throws IOException{
		if(!nsfplayer){
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
	}
	public final void restoreState(String slot) throws IOException, ClassNotFoundException{
		if(!nsfplayer){
		FileInputStream fin = new FileInputStream(slot);
		ObjectInputStream in = new ObjectInputStream(fin);
		pause = true;
		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		map = (Mapper) in.readObject();
		map.apu = (APU) in.readObject();
		map.cpu = (CPU_6502) in.readObject();
		map.ppu = (ppu2C02) in.readObject();
		map.setSystem(system);
		in.close();
		pause = false;
		}
	}
	public final void pause(){
		pause = true;
		while(!pauseConfirmed){
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		};
	}
	public final void unpause(){pause=false;}
	public final void togglePause(){
		if(pause)
			unpause();
		else
			pause();
	}
	public final void loadSave() throws IOException{
		save = new File(romName+".sav");
		System.out.println(save.getAbsolutePath());
		if(save.exists()){
				System.out.println("Save Found! Loading...");
				FileInputStream sx = new FileInputStream(save);
				byte[] savearray = new byte[(int)save.length()];
				sx.read(savearray);
				map.restoreSave(savearray);
				sx.close();
		}
		else
			System.out.println("Save not found!");
	}
	public final void saveGame() throws IOException{
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
	public final Object[][] getAudioChannelInfo(){
		return map.apu.channelInfo();
	}
	public final void setSampleRate(int rate){
		map.apu.setSampleRate(rate);
		NesSettings.logSampleRate(rate);
	}
	public final void runCPUCycle() {
		map.runCPUCycle();	
		if(map.ppu.doneFrame){
			system.videoCallback(map.ppu.renderer.colorized);
			map.ppu.doneFrame=false;
		}
		
	}
	public static final void setInternalPalette(String palette){
		NesColors.updatePalette(palette);
		NesSettings.logInternalPalette(palette);
	}
	public static final int[] getInternalPaletteRGB(String palette){
		return NesColors.getpalette(palette);
	}
	public static final void setCustomPalette(int[] palette){
		NesColors.setCustomPalette(palette);
	}
	public final Object[] getCPUDebugInfo() {
		return map.cpu.getDebugInfo();
	}
	public final Object[] getPPUDebugInfo() {
		return map.ppu.getDebugInfo();
	}
	public final int[] getAPUDebugInfo() {
		// TODO Auto-generated method stub
		return null;
	}
	public final int[] getMapperDebugInfo() {
		// TODO Auto-generated method stub
		return null;
	}
	

}
