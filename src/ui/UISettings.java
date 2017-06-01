package ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;


public class UISettings {
	static Properties prop;
	//UI Settings
	public static boolean autoLoad = true;
	public static boolean ShowFPS=true;
	public static boolean lockVideoToAudio=false;
	public static boolean AudioEnabled=true;
	public static String lastLoadedDir = System.getProperty("user.dir");
	
	
	//Controller Bindings
	public static ControllerInfo[] c1controls = new ControllerInfo[8];
	public static ControllerInfo[] c2controls = new ControllerInfo[8];
	
	public static void saveSettings(File config){
		FileOutputStream output;
		FileInputStream input;
		try {
			input = new FileInputStream(config);
			prop.load(input);
			input.close();
			output = new FileOutputStream(config);
			saveKeys();
			saveUI();
			prop.store(output, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Loads nes settings from saved config file. Or loads default values.
	 * @throws IOException
	 */
	public static void loadSettings(File config) throws IOException{
		prop = new Properties();
		//File t = new File("config.properties");
		if(!config.exists()){//default config
			loadKeys();
		}
		else{
			FileInputStream input = new FileInputStream(config);
			prop.load(input);
			loadKeys();	
			loadUI();
		}
	}
	private static void loadKeys(){
		System.out.println("Loading keys");
		c1controls = new ControllerInfo[8];
		c1controls[0] = ControllerInfo.restoreInfo(prop.getProperty("c1a"),     "Standard PS/2 Keyboard;A;1.0");
		c1controls[1] = ControllerInfo.restoreInfo(prop.getProperty("c1b"),     "Standard PS/2 Keyboard;B;1.0");
		c1controls[2] = ControllerInfo.restoreInfo(prop.getProperty("c1select"),"Standard PS/2 Keyboard;W;1.0");
		c1controls[3] = ControllerInfo.restoreInfo(prop.getProperty("c1start"), "Standard PS/2 Keyboard;Q;1.0");
		c1controls[4] = ControllerInfo.restoreInfo(prop.getProperty("c1up"),    "Standard PS/2 Keyboard;Up;1.0");
		c1controls[5] = ControllerInfo.restoreInfo(prop.getProperty("c1down"),  "Standard PS/2 Keyboard;Down;1.0");
		c1controls[6] = ControllerInfo.restoreInfo(prop.getProperty("c1left"),  "Standard PS/2 Keyboard;Left;1.0");
		c1controls[7] = ControllerInfo.restoreInfo(prop.getProperty("c1right"), "Standard PS/2 Keyboard;Right;1.0");
		c2controls = new ControllerInfo[8];
		c2controls[0] = ControllerInfo.restoreInfo(prop.getProperty("c2a"),     "Standard PS/2 Keyboard;A;1.0");
		c2controls[1] = ControllerInfo.restoreInfo(prop.getProperty("c2b"),     "Standard PS/2 Keyboard;B;1.0");
		c2controls[2] = ControllerInfo.restoreInfo(prop.getProperty("c2select"),"Standard PS/2 Keyboard;W;1.0");
		c2controls[3] = ControllerInfo.restoreInfo(prop.getProperty("c2start"), "Standard PS/2 Keyboard;Q;1.0");
		c2controls[4] = ControllerInfo.restoreInfo(prop.getProperty("c2up"),    "Standard PS/2 Keyboard;Up;1.0");
		c2controls[5] = ControllerInfo.restoreInfo(prop.getProperty("c2down"),  "Standard PS/2 Keyboard;Down;1.0");
		c2controls[6] = ControllerInfo.restoreInfo(prop.getProperty("c2left"),  "Standard PS/2 Keyboard;Left;1.0");
		c2controls[7] = ControllerInfo.restoreInfo(prop.getProperty("c2right"), "Standard PS/2 Keyboard;Right;1.0");		
	}
	private static void saveKeys(){
		prop.setProperty("c1a", c1controls[0].storeInfo()+"");prop.setProperty("c2a", c2controls[0].storeInfo()+"");
		prop.setProperty("c1b", c1controls[1].storeInfo()+"");prop.setProperty("c2b", c2controls[1].storeInfo()+"");
		prop.setProperty("c1up", c1controls[4].storeInfo()+"");prop.setProperty("c2up", c2controls[4].storeInfo()+"");
		prop.setProperty("c1down",c1controls[5].storeInfo()+"");prop.setProperty("c2down", c2controls[5].storeInfo()+"");
		prop.setProperty("c1left", c1controls[6].storeInfo()+"");prop.setProperty("c2left", c2controls[6].storeInfo()+"");
		prop.setProperty("c1right", c1controls[7].storeInfo()+"");prop.setProperty("c2right", c2controls[7].storeInfo()+"");
		prop.setProperty("c1start", c1controls[3].storeInfo()+"");prop.setProperty("c2start", c2controls[3].storeInfo()+"");
		prop.setProperty("c1select", c1controls[2].storeInfo()+"");prop.setProperty("c2select", c2controls[2].storeInfo()+"");
	}
	private static void loadUI(){
		autoLoad = prop.getProperty("autoload", "true").equals("true");
		ShowFPS = prop.getProperty("showfps", "true").equals("true");
		lockVideoToAudio = prop.getProperty("lockvideotoaudio", "true").equals("true");
		AudioEnabled = prop.getProperty("audioenabled", "true").equals("true");
		lockVideoToAudio = prop.getProperty("lockvideotoaudio", "true").equals("true");
		lastLoadedDir = prop.getProperty("lastloadeddir", System.getProperty("user.dir"));
	}
	private static void saveUI(){
		prop.setProperty("autoload", autoLoad+"");
		prop.setProperty("showfps", ShowFPS+"");
		prop.setProperty("lockvideotoaudio", lockVideoToAudio+"");
		prop.setProperty("audioenabled", AudioEnabled+"");
		prop.setProperty("lastloadeddir", lastLoadedDir+"");
	}
		
}
