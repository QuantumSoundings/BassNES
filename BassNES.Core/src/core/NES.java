package core;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
//import java.util.Enumeration;
//import java.util.zip.ZipEntry;
//import java.util.zip.ZipFile;

import core.exceptions.UnSupportedMapperException;
import core.mappers.Mapper;
import core.mappers.MapperSetting;
import core.mappers.MapperSetting.SettingType;
import core.video.NesColors;
/**
 * Object representing an NES console. This and NesSettings are the only two files that should be interacted with.
 * Object is a runnable, so it can occupy its own thread or be controlled through a main program.
 * @author Jordan Howe
 *
 */
public class NES implements Runnable, NESAccess {
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
		case "nsfe": loadNSFe(rom);break;
		//case "zip": loadZip(rom);break;
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
			byte[] PRG_ROM = new byte[16384*(0xff&header[4])];
			sx.read(PRG_ROM);
			byte[] CHR_ROM = new byte[8192*(0xff&header[5])];
			sx.read(CHR_ROM);
			batteryExists = (header[6]&2)!=0?true:false;
			int id = (0xff&header[6])>>4;
			id|= (0xff&header[7])&0xf0;
			map = Mapper.getmapper(id);
			System.out.println("PRG_ROM:"+(PRG_ROM.length/0x400)+"KB");
			System.out.println("CHR_ROM:"+(CHR_ROM.length/0x400)+"KB");
			
			map.configureMapper(new MapperSetting(SettingType.PRG_data,PRG_ROM),
								new MapperSetting(SettingType.CHR_data,CHR_ROM),
								new MapperSetting(SettingType.BatteryExists,batteryExists),
								new MapperSetting(SettingType.Mirroring,header[6]&1));
			
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
			int totalsongs = (0xff&header[6]);
			int startsong = (0xff&header[7]);
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
			map.configureMapper(new MapperSetting(SettingType.NSF_data,data),
								new MapperSetting(SettingType.BankSwitch,bankswitch),
								new MapperSetting(SettingType.ExtraSoundChips,extrasoundchips),
								new MapperSetting(SettingType.DataPlayAddr,dataplayaddr),
								new MapperSetting(SettingType.DataInitAddr,datainitaddr),
								new MapperSetting(SettingType.DataLoadAddr,dataloadaddr),
								new MapperSetting(SettingType.PlaySpeed,playspeed),
								new MapperSetting(SettingType.StartSong,startsong),
								new MapperSetting(SettingType.TotalSongs,totalsongs),
								new MapperSetting(SettingType.TuneRegion,tuneregion),
								new MapperSetting(SettingType.SongName,songname),
								new MapperSetting(SettingType.ArtistName,artistname));
			map.setSystem(system);
			nsfplayer = true;
		}
		sx.close();
	}
	private void loadNSFe(File rom) throws IOException, UnSupportedMapperException{
		FileInputStream sx = new FileInputStream(rom);
		byte[] header = new byte[4];
		sx.read(header);
		if(header[0]==0x4e&&header[1]==0x53&&header[2]==0x46&&header[3]==0x45){
			map = Mapper.getmapper(1002);
			byte[] data;
			int songnum=0;
			String ncname="";
			java.util.ArrayList<MapperSetting> settings = new ArrayList<MapperSetting>();
			while(!ncname.equals("NEND")){
				byte[] ncheader = new byte[8];
				sx.read(ncheader);
				int nclength = ((ncheader[3]&0xff)<<24)|((ncheader[2]&0xff)<<16)|((ncheader[1]&0xff)<<8)|(ncheader[0]&0xff);
				ncname = ((char) (0xff&ncheader[4])+"")+((char) (0xff&ncheader[5])+"")+((char) (0xff&ncheader[6])+"")+((char) (0xff&ncheader[7])+"");
				switch(ncname){
				case "INFO":
					System.out.println("Reading INFO Chunk");
					data = new byte[nclength];
					sx.read(data);
					int dataloadaddr = ((data[1]&0xff)<<8)|(data[0]&0xff);
					int datainitaddr = ((data[3]&0xff)<<8)|(data[2]&0xff);
					int dataplayaddr = ((data[5]&0xff)<<8)|(data[4]&0xff);
					byte extrasoundchips = data[7];
					int totalsongs = (0xff&data[8]);
					songnum=totalsongs;
					int startsong = (0xff&data[9]);
					
					settings.add(new MapperSetting(SettingType.DataPlayAddr,dataplayaddr));
					settings.add(new MapperSetting(SettingType.DataInitAddr,datainitaddr));
					settings.add(new MapperSetting(SettingType.DataLoadAddr,dataloadaddr));
					settings.add(new MapperSetting(SettingType.StartSong,startsong));
					settings.add(new MapperSetting(SettingType.TotalSongs,totalsongs));
					settings.add(new MapperSetting(SettingType.ExtraSoundChips,extrasoundchips));
					settings.add(new MapperSetting(SettingType.PlaySpeed,0));
					break;
				case "DATA":
					System.out.println("Reading DATA Chunk");
					data = new byte[nclength];
					sx.read(data);
					settings.add(new MapperSetting(SettingType.NSF_data,data));
					break;
				case "BANK":
					System.out.println("Reading BANK Chunk");
					byte[] out = new byte[8];
					data = new byte[nclength];
					sx.read(data);
					if(nclength<8){
						int i = 0;
						for(byte b:data)
							out[i] = b;
					}
					else if(nclength>8){
						for(int i = 0; i<8;i++)
							out[i] = data[i];
					}
					else
						out = data;
					settings.add(new MapperSetting(SettingType.BankSwitch,out));
					break;
				case "tlbl":
					System.out.println("Reading tlbl Chunk");
					String[] tracknames= new String[songnum];
					data = new byte[nclength];
					sx.read(data);
					int i = 0;
					String name="";
					for(byte b:data){
						if(b==0){
							tracknames[i++] = name;
							name="";
						}
						else{
							name+=(char)(0xff&b);
						}
					}
					settings.add(new MapperSetting(SettingType.TrackNames,tracknames));
					break;
				case "time":
					System.out.println("Reading time Chunk");
					int[] tracktimes= new int[songnum];
					data = new byte[nclength];
					sx.read(data);
					for(int x = 0;x<songnum;x++){
						int length = ((data[x*4+3]&0xff)<<24)|((data[x*4+2]&0xff)<<16)|((data[x*4+1]&0xff)<<8)|(data[x*4+0]&0xff);
						if((int)(length/16.6666) ==0){
							tracktimes[x] = NesSettings.nsfPlayerSongLength;
						}
						else
							tracktimes[x] = (int)(length/16.6666);
					}
					settings.add(new MapperSetting(SettingType.TrackTimes,tracktimes));
					break;
				case "auth":
					System.out.println("Reading auth Chunk");
					String[] info = new String[4];
					data = new byte[nclength];
					sx.read(data);
					int z = 0;
					String nameinfo="";
					for(byte b:data){
						if(b==0){
							info[z++] = nameinfo;
							nameinfo="";
						}
						else{
							nameinfo+=(char)(0xff&b);
						}
					}
					settings.add(new MapperSetting(SettingType.AuthInfo,info));
					break;
				case "fade":
					System.out.println("Reading fade chunk");
					int[] fadetimes = new int[songnum];
					data = new byte[nclength];
					sx.read(data);
					for(int x = 0;x<songnum;x++){
						int length = ((data[x*4+3]&0xff)<<24)|((data[x*4+2]&0xff)<<16)|((data[x*4+1]&0xff)<<8)|(data[x*4+0]&0xff);
						if((int)(length/16.6666) <0){
							fadetimes[x] = NesSettings.nsfPlayerFadeLength;
						}
						else
							fadetimes[x] = (int)(length/16.6666);
					}
					settings.add(new MapperSetting(SettingType.FadeTimes,fadetimes));
					break;
				case "NEND":
					break;
				default: 
					System.out.println("Unsupported Chunk type: "+ncname );
					data = new byte[nclength]; 
					sx.read(data);
					break;
				}
			}
			MapperSetting[] set = new MapperSetting[settings.size()];
			for(int i = 0; i < set.length;i++)
				set[i] = settings.get(i);
			map.configureMapper(set);
			map.setSystem(system);
			nsfplayer = true;
		}
		sx.close();
	}
	/*private void loadZip(File rom) throws IOException, UnSupportedMapperException{
		ZipFile romzip = new ZipFile(rom);
		Enumeration<? extends ZipEntry> entries = romzip.entries();
		while(entries.hasMoreElements()){
			ZipEntry entry = entries.nextElement();
			File r ;
			String ext = entry.getName().toLowerCase().substring(entry.getName().lastIndexOf(".")+1);
			switch(ext){
				case "nes": loadiNES(rom);break;
				case "nsf": loadNSF(rom);break;
				case "nsfe": loadNSFe(rom);break;
				default:
			}
		}
	}*/
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
	private int flextimer=16100000;
	private void autoRunFrame(){
		frameStartTime = System.nanoTime();
		map.runFrame();
		frameStopTime = System.nanoTime() - frameStartTime;
		if(NesSettings.frameLimit)
			if(frameStopTime<flextimer)
				try {
					
					int waittime = (int) (flextimer-frameStopTime);
					if(NesSettings.politeFrameTiming)
						Thread.sleep(waittime/1000000,waittime%1000000 );
					else
						while(System.nanoTime()-frameStartTime<16500000){
						//if(NesSettings.politeFrameTiming)
							//Thread.sleep(0,10000);
						}
				} catch ( InterruptedException e){
					e.printStackTrace();
				}
			
		if(framecount%60==0){
			double x = 1000.0/(System.currentTimeMillis()-fpsStartTime);
			currentFPS = x*60;
			if(NesSettings.frameLimit&&NesSettings.politeFrameTiming)
				if(currentFPS>60.1)
					flextimer+=50000;
				else
					flextimer-=50000;
			if(flextimer>16600000)
				flextimer =16600000;
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
		NesSettings.logSampleRate(rate);
		map.apu.setSampleRate(rate);
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
