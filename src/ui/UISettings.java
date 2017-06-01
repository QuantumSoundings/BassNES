package ui;

import java.awt.event.KeyEvent;
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
		c1a = Integer.parseInt(prop.getProperty("c1a", KeyEvent.VK_A+""));
		c1b = Integer.parseInt(prop.getProperty("c1b", KeyEvent.VK_S+""));
		c1up = Integer.parseInt(prop.getProperty("c1up",KeyEvent.VK_UP+""));
		c1down = Integer.parseInt(prop.getProperty("c1down", KeyEvent.VK_DOWN+""));
		c1left = Integer.parseInt(prop.getProperty("c1left", KeyEvent.VK_LEFT+""));
		c1right = Integer.parseInt(prop.getProperty("c1right", KeyEvent.VK_RIGHT+""));
		c1start = Integer.parseInt(prop.getProperty("c1start", KeyEvent.VK_Q+""));
		c1select = Integer.parseInt(prop.getProperty("c1select", KeyEvent.VK_W+""));
		c2a = Integer.parseInt(prop.getProperty("c2a", KeyEvent.VK_A+""));
		c2b = Integer.parseInt(prop.getProperty("c2b", KeyEvent.VK_S+""));
		c2up = Integer.parseInt(prop.getProperty("c2up",KeyEvent.VK_UP+""));
		c2down = Integer.parseInt(prop.getProperty("c2down", KeyEvent.VK_DOWN+""));
		c2left = Integer.parseInt(prop.getProperty("c2left", KeyEvent.VK_LEFT+""));
		c2right = Integer.parseInt(prop.getProperty("c2right", KeyEvent.VK_RIGHT+""));
		c2start = Integer.parseInt(prop.getProperty("c2start", KeyEvent.VK_Q+""));
		c2select = Integer.parseInt(prop.getProperty("c2select", KeyEvent.VK_W+""));		
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
	private static void loadUI(){
		autoLoad = prop.getProperty("autoload", "true").equals("true");
		ShowFPS = prop.getProperty("showfps", "true").equals("true");
		lockVideoToAudio = prop.getProperty("lockvideotoaudio", "true").equals("true");
		AudioEnabled = prop.getProperty("audioenabled", "true").equals("true");
	}
	private static void saveUI(){
		prop.setProperty("autoload", autoLoad+"");
		prop.setProperty("showfps", ShowFPS+"");
		prop.setProperty("lockvideotoaudio", lockVideoToAudio+"");
		prop.setProperty("audioenabled", AudioEnabled+"");
	}
		
}
