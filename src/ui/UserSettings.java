package ui;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class UserSettings {
	static Properties prop;
	public final static String version = "0.2.0";
	//Graphics Settings
	public static boolean RenderBackground=true;
	public static boolean RenderSprites=true;
	public static int RenderMethod=2;
	
	//Audio Settings
	public static boolean AudioEnabled;
	public static int masterMixLevel=100;
	public static int pulse1MixLevel=0;
	public static int pulse2MixLevel=100;
	public static int triangleMixLevel=20;
	public static int noiseMixLevel=100;
	public static int dmcMixLevel=100;
	
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
			if(!prop.getProperty("version").equals(version)||!prop.containsKey("version")){
				t.delete();
				loadSettings();
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
}
