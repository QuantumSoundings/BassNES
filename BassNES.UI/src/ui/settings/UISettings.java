package ui.settings;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;


public class UISettings {
	static Properties prop;
	public static final String version = "0.4.3";
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
	

	//Control Settings
	public static boolean controlwhilenotfocused=false;


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
