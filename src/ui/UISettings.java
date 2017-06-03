package ui;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;


public class UISettings {
	static Properties prop;
	public static final String version = "0.4.0";
	//UI Settings
	public static boolean autoLoad = true;
	public static boolean ShowFPS=true;
	public static boolean lockVideoToAudio=false;
	public static boolean AudioEnabled=true;
	public static String lastLoadedDir = System.getProperty("user.dir");
	
	
	//Video Filter Settings
	public static enum VideoFilter{None,NTSC};
	public static VideoFilter currentFilter = VideoFilter.None;
	public static boolean scanlinesEnabled = false;
	public static double scanlineThickness=0.5;
	//NTSC settings
	public static double ntsc_hue = 0; //-1 to 1
	public static double ntsc_saturation = 0; //-1 to 1
	public static double ntsc_contrast = 0; //-1 to 1
	public static double ntsc_brightness = 0; //-1 to 1
	public static double ntsc_sharpness = 0;
	public static double ntsc_gamma = 0;
	public static double ntsc_resolution = 0;
	public static double ntsc_artifacts = 0;
	public static double ntsc_fringing = 0;
	public static double ntsc_bleed = 0;
	public static boolean ntsc_merge = true;
	//Visualizer Settings
	public static final String[] channelNames = {"Pulse 1","Pulse 2","Triangle","VRC6 Pulse 1","VRC6 Pulse 2","VRC6 Saw","MMC5 Pulse 1","MMC5 Pulse 2","N_Channel 0","N_Channel 1","N_Channel 2"
			,"N_Channel 3","N_Channel 4","N_Channel 5","N_Channel 6","N_Channel 7"};
	public static Color[] pianoColors = {Color.BLUE,Color.CYAN,Color.GREEN,Color.ORANGE,Color.RED,Color.YELLOW,Color.DARK_GRAY,Color.LIGHT_GRAY,Color.magenta,Color.magenta,Color.magenta,
			Color.magenta,Color.magenta,Color.MAGENTA,Color.MAGENTA,Color.magenta};
	public static final Color[] pianoColorsColorBlind = {new Color(0x6600),new Color(0x99ff33),new Color(0x3399ff),new Color(0xff),new Color(0x9900cc),new Color(0xff66ff),new Color(0xcc0000)
			,new Color(0xcecece),new Color(0x8c8c8c),new Color(0x8c8c8c),new Color(0x8c8c8c),new Color(0x8c8c8c),new Color(0x8c8c8c),new Color(0x8c8c8c),new Color(0x8c8c8c),new Color(0x8c8c8c)};
	public static boolean colorBlindMode = false;
	public static boolean allGreen = false;
	
	
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
			saveVideo();
			saveNtsc();
			saveVisualizer();
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
			loadVideo();
			loadNtsc();
			loadVisualizer();
		}
	}
	private static void loadKeys(){
		System.out.println("Loading keys");
		c1controls = new ControllerInfo[8];
		c1controls[0] = ControllerInfo.restoreInfo(prop.getProperty("c1a"),     "Standard PS/2 Keyboard;A;1.0");
		c1controls[1] = ControllerInfo.restoreInfo(prop.getProperty("c1b"),     "Standard PS/2 Keyboard;S;1.0");
		c1controls[2] = ControllerInfo.restoreInfo(prop.getProperty("c1select"),"Standard PS/2 Keyboard;W;1.0");
		c1controls[3] = ControllerInfo.restoreInfo(prop.getProperty("c1start"), "Standard PS/2 Keyboard;Q;1.0");
		c1controls[4] = ControllerInfo.restoreInfo(prop.getProperty("c1up"),    "Standard PS/2 Keyboard;Up;1.0");
		c1controls[5] = ControllerInfo.restoreInfo(prop.getProperty("c1down"),  "Standard PS/2 Keyboard;Down;1.0");
		c1controls[6] = ControllerInfo.restoreInfo(prop.getProperty("c1left"),  "Standard PS/2 Keyboard;Left;1.0");
		c1controls[7] = ControllerInfo.restoreInfo(prop.getProperty("c1right"), "Standard PS/2 Keyboard;Right;1.0");
		c2controls = new ControllerInfo[8];
		c2controls[0] = ControllerInfo.restoreInfo(prop.getProperty("c2a"),     "Standard PS/2 Keyboard;A;1.0");
		c2controls[1] = ControllerInfo.restoreInfo(prop.getProperty("c2b"),     "Standard PS/2 Keyboard;S;1.0");
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
	private static void loadVideo(){
		currentFilter = VideoFilter.valueOf(prop.getProperty("currentfilter", "None"));
		scanlineThickness = Double.parseDouble(prop.getProperty("scanlinethickness", "0.5"));
		scanlinesEnabled = prop.getProperty("scanlinesenabled", "false").equals("true");
	}
	private static void saveVideo(){
		prop.setProperty("currentfilter", currentFilter+"");
		prop.setProperty("scanlinethickness", scanlineThickness+"");
		prop.setProperty("scanlinesenabled", scanlinesEnabled+"");
	}
	private static void loadNtsc(){
		ntsc_hue = Double.parseDouble(prop.getProperty("ntsc_hue", "0"));
		ntsc_saturation = Double.parseDouble(prop.getProperty("ntsc_saturation", "0"));
		ntsc_contrast = Double.parseDouble(prop.getProperty("ntsc_contrast", "0"));
		ntsc_brightness = Double.parseDouble(prop.getProperty("ntsc_brightness", "0"));
		ntsc_sharpness = Double.parseDouble(prop.getProperty("ntsc_sharpness", "0"));
		ntsc_gamma = Double.parseDouble(prop.getProperty("ntsc_gamma", "0"));
		ntsc_resolution = Double.parseDouble(prop.getProperty("ntsc_resolution", "0"));
		ntsc_artifacts = Double.parseDouble(prop.getProperty("ntsc_artifacts", "0"));
		ntsc_fringing = Double.parseDouble(prop.getProperty("ntsc_fringing", "0"));
		ntsc_bleed = Double.parseDouble(prop.getProperty("ntsc_bleed", "0"));
		ntsc_merge = prop.getProperty("ntsc_merge", "true").equals("true");
	}
	private static void saveNtsc(){
		prop.setProperty("ntsc_hue", ntsc_hue+"");
		prop.setProperty("ntsc_saturation", ntsc_saturation+"");
		prop.setProperty("ntsc_contrast", ntsc_contrast+"");
		prop.setProperty("ntsc_brightness", ntsc_brightness+"");
		prop.setProperty("ntsc_sharpness", ntsc_sharpness+"");
		prop.setProperty("ntsc_gamma", ntsc_gamma+"");
		prop.setProperty("ntsc_resolution", ntsc_resolution+"");
		prop.setProperty("ntsc_artifacts", ntsc_artifacts+"");
		prop.setProperty("ntsc_fringing", ntsc_fringing+"");
		prop.setProperty("ntsc_bleed", ntsc_bleed+"");
		prop.setProperty("ntsc_merge", ntsc_merge+"");
	}
	private static void loadVisualizer(){
		String in = prop.getProperty("pianocolors", "");
		if(in.length()>1){
			String[] colors = in.split(" ");
			for(int i = 0; i<colors.length;i++)
				pianoColors[i] = new Color(Integer.parseInt(colors[i]));
		}
		colorBlindMode = prop.getProperty("colorblindmode", "false").equals("true");
		allGreen = prop.getProperty("allgreen", "false").equals("true");

	}
	private static void saveVisualizer(){
		String out = "";
		for(Color c : pianoColors){
			out += c.getRGB()+" ";
		}
		prop.setProperty("pianocolors", out);
		prop.setProperty("colorblindmode", colorBlindMode+"");
		prop.setProperty("allgreen", allGreen+"");
	}
		
}
