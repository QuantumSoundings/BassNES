package ui;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import video.NesColors;

public class UserSettings {
	static Properties prop;
	public final static String version = "0.2.4";
	//Emulation Settings
	public static boolean politeFrameTiming = true;
	public static boolean frameLimit = true;
	public static boolean autoLoad = true;
	public static boolean lockVideoToAudio=false;
	
	
	//Graphics Settings
	public static boolean RenderBackground=true;
	public static boolean RenderSprites=true;
	public static int RenderMethod=2;
	public static boolean ShowFPS=true;
	public static String selectedPalette= "defaultPalette";
	
	//Audio Settings
	public static boolean AudioEnabled=true;
	public static int masterMixLevel=100;
	public static int pulse1MixLevel=100;
	public static int pulse2MixLevel=100;
	public static int triangleMixLevel=100;
	public static int noiseMixLevel=100;
	public static int dmcMixLevel=100;
	public static int vrc6MixLevel=100;
	public static int namcoMixLevel=100;
	public static int sampleRate = 48000;
	
	//Controller Bindings
	public static int c1a;
	public static int c1b;
	public static int c1up;
	public static int c1down;
	public static int c1left;
	public static int c1right;
	public static int c1start;
	public static int c1select;
	public static int c2a;
	public static int c2b;
	public static int c2up;
	public static int c2down;
	public static int c2left;
	public static int c2right;
	public static int c2start;
	public static int c2select;
	public static int nsfpause=KeyEvent.VK_SPACE;
	public static int nsfnext=KeyEvent.VK_RIGHT;
	public static int nsfprev=KeyEvent.VK_LEFT;
	public static int nsfplayforever = KeyEvent.VK_F;
	public static int nsfloop=KeyEvent.VK_L;
	

	public UserSettings(){
		try {
			loadSettings();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	public void setSetting(String key,String value){
		prop.setProperty(key, value);
	}
	public String getSetting(String key){
		return prop.getProperty(key);
	}
	public static void saveSettings(){
		FileOutputStream output;
		try {
			output = new FileOutputStream("config.properties");
			saveKeys();
			saveAudio();
			saveGraphics();
			saveEmulation();
			prop.store(output, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void loadSettings() throws IOException{
		prop = new Properties();
		File t = new File("config.properties");
		if(!t.exists()){//default config
			prop.setProperty("version", version);
			prop.setProperty("c1up", KeyEvent.VK_UP+"");
			prop.setProperty("c1down", KeyEvent.VK_DOWN+"");
			prop.setProperty("c1left", KeyEvent.VK_LEFT+"");
			prop.setProperty("c1right", KeyEvent.VK_RIGHT+"");
			prop.setProperty("c1a", KeyEvent.VK_A+"");
			prop.setProperty("c1b", KeyEvent.VK_S+"");
			prop.setProperty("c1start", KeyEvent.VK_Q+"");
			prop.setProperty("c1select", KeyEvent.VK_W+"");
			prop.setProperty("c2up", KeyEvent.VK_UP+"");
			prop.setProperty("c2down", KeyEvent.VK_DOWN+"");
			prop.setProperty("c2left", KeyEvent.VK_LEFT+"");
			prop.setProperty("c2right", KeyEvent.VK_RIGHT+"");
			prop.setProperty("c2a", KeyEvent.VK_A+"");
			prop.setProperty("c2b", KeyEvent.VK_S+"");
			prop.setProperty("c2start", KeyEvent.VK_Q+"");
			prop.setProperty("c2select", KeyEvent.VK_W+"");
			loadKeys();
		}
		else{
			FileInputStream input = new FileInputStream("config.properties");
			prop.load(input);
			loadKeys();
			loadAudio();
			loadGraphics();
			loadEmulation();
			if(!prop.getProperty("version").equals(version)||!prop.containsKey("version")){
				//boolean x = t.delete();
				//System.out.println(x);
				//loadSettings();
			}
		}
	}

	private static void loadKeys(){
		c1a = Integer.parseInt(prop.getProperty("c1a"));
		c1b = Integer.parseInt(prop.getProperty("c1b"));
		c1up = Integer.parseInt(prop.getProperty("c1up"));
		c1down = Integer.parseInt(prop.getProperty("c1down"));
		c1left = Integer.parseInt(prop.getProperty("c1left"));
		c1right = Integer.parseInt(prop.getProperty("c1right"));
		c1start = Integer.parseInt(prop.getProperty("c1start"));
		c1select = Integer.parseInt(prop.getProperty("c1select"));
		c2a = Integer.parseInt(prop.getProperty("c2a"));
		c2b = Integer.parseInt(prop.getProperty("c2b"));
		c2up = Integer.parseInt(prop.getProperty("c2up"));
		c2down = Integer.parseInt(prop.getProperty("c2down"));
		c2left = Integer.parseInt(prop.getProperty("c2left"));
		c2right = Integer.parseInt(prop.getProperty("c2right"));
		c2start = Integer.parseInt(prop.getProperty("c2start"));
		c2select = Integer.parseInt(prop.getProperty("c2select"));		
	}
	private static void saveKeys(){
		prop.setProperty("c1a", c1a+"");prop.setProperty("c2a", c2a+"");
		prop.setProperty("c1b", c1b+"");prop.setProperty("c2b", c2b+"");
		prop.setProperty("c1up", c1up+"");prop.setProperty("c2up", c2up+"");
		prop.setProperty("c1down", c1down+"");prop.setProperty("c2down", c2down+"");
		prop.setProperty("c1left", c1left+"");prop.setProperty("c2left", c2left+"");
		prop.setProperty("c1right", c1right+"");prop.setProperty("c2right", c2right+"");
		prop.setProperty("c1start", c1start+"");prop.setProperty("c2start", c2start+"");
		prop.setProperty("c1select", c1select+"");prop.setProperty("c2select", c2select+"");
	}
	private static void loadAudio(){
		AudioEnabled = prop.getProperty("audioenabled", "true").equals("true");
		masterMixLevel = Integer.parseInt(prop.getProperty("mastermixlevel","100"));
		pulse1MixLevel = Integer.parseInt(prop.getProperty("pulse1mixlevel","100"));
		pulse2MixLevel = Integer.parseInt(prop.getProperty("pulse2mixlevel","100"));
		triangleMixLevel = Integer.parseInt(prop.getProperty("trianglemixlevel","100"));
		noiseMixLevel = Integer.parseInt(prop.getProperty("noisemixlevel","100"));
		dmcMixLevel = Integer.parseInt(prop.getProperty("dmcmixlevel", "100"));
		vrc6MixLevel = Integer.parseInt(prop.getProperty("vrc6mixlevel", "100"));
		namcoMixLevel = Integer.parseInt(prop.getProperty("namcomixlevel", "100"));
		sampleRate = Integer.parseInt(prop.getProperty("samplerate", "44100"));
	}
	private static void saveAudio(){
		prop.setProperty("audioenabled", AudioEnabled+"");
		prop.setProperty("mastermixlevel", masterMixLevel+"");
		prop.setProperty("pulse1mixlevel", pulse1MixLevel+"");
		prop.setProperty("pulse2mixlevel", pulse2MixLevel+"");
		prop.setProperty("trianglemixlevel", triangleMixLevel+"");
		prop.setProperty("noisemixlevel", noiseMixLevel+"");
		prop.setProperty("dmcmixlevel", dmcMixLevel+"");
		prop.setProperty("vrc6mixlevel", vrc6MixLevel+"");
		prop.setProperty("namcomixlevel", namcoMixLevel+"");
		prop.setProperty("samplerate", sampleRate+"");
	}
	private static void loadGraphics(){
		RenderBackground = prop.getProperty("renderbackground", "true").equals("true");
		RenderSprites = prop.getProperty("rendersprites", "true").equals("true");
		RenderMethod = Integer.parseInt(prop.getProperty("rendermethod", "2"));
		ShowFPS = prop.getProperty("showfps", "true").equals("true");
		selectedPalette = prop.getProperty("selectedpalette","defaultPalette");
		NesColors.setCustomPalette(prop.getProperty("custompalette",""));
		NesColors.updatePalette(selectedPalette);
	}
	private static void saveGraphics(){
		prop.setProperty("renderbackground", RenderBackground+"");
		prop.setProperty("rendersprites", RenderSprites+"");
		prop.setProperty("rendermethod", 2+"");
		prop.setProperty("showfps", ShowFPS+"");
		prop.setProperty("selectedpalette", selectedPalette);
		prop.setProperty("custompalette", NesColors.getCustomPalette());
	}
	private static void loadEmulation(){
		politeFrameTiming = prop.getProperty("politeframetiming", "true").equals("true");
		frameLimit = prop.getProperty("framelimit", "true").equals("true");
		autoLoad = prop.getProperty("autoload", "true").equals("true");
		lockVideoToAudio = prop.getProperty("lockvideotoaudio","false").equals("true");
	}
	private static void saveEmulation(){
		prop.setProperty("politeframetiming", politeFrameTiming+"");
		prop.setProperty("framelimit", frameLimit+"");
		prop.setProperty("autoload", autoLoad+"");
		prop.setProperty("lockvideotoaudio", lockVideoToAudio+"");
	}
}