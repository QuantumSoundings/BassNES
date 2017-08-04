package core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import core.video.NesColors;
/**
 * Contains settings vital to the functioning of an NES object. All changes are reflected immediately in
 * all running NES objects unless otherwise noted.
 * @author Jordan Howe
 *
 */
public class NesSettings {
	static Properties prop;
	//Emulation Settings
	/**
	 * Determines whether a threaded NES instance will sleep between frames.
	 */
	public static boolean politeFrameTiming = true;
	/**
	 * Determines whether a threaded NES instance will limit its framerate.
	 */
	public static boolean frameLimit = true;
	
	
	//Graphics Settings
	/**
	 * Determines whether a NES instance will output background graphics.
	 */
	public static boolean RenderBackground=true;
	/**
	 * Determines whether a NES instance will output sprite graphics.
	 */
	public static boolean RenderSprites=true;
	/**
	 * Allows for more than 8 sprites at a time.
	 */
	public static boolean disableSpriteLimit=false;
	/**
	 * Internal rendering method. This variable determines the type of information present in the 
	 * video callback. 
	 * 1: No color emphasis. RGB.
	 * 2: Color emphasis. RGB.
	 * 3: Raw nes values.
	 */
	public static int RenderMethod=2;
	/**
	 * The core comes with several internal palettes. This variable holds the name of the 
	 * currently selected palette. READ ONLY.
	 */
	public static String selectedPalette= "defaultPalette";
	/**
	 * Array of internal palette names to be used when setting the internal palette.
	 */
	public final static String[] palettes = {"defaultPalette","Custom","ntscHardwareFBX","nesClassicFBX","compositeDirectFBX","sonypvmFBX","vc_3ds","asq_reality_c","av_famicom","bmf_final_3","consumer","dougeff","drag3","fceux","fceux_15","gameboy","grayscale","kizul","nesticle","nestopia_rgb","nestopia_yuv","nintendulator_ntsc","rinao","rockman_9","rp2c04_0001","rp2c04_0002","rp2c04_0003","rp2c04_0004","terratec_cinergy","trebor","vc_wii"};
	public static enum AudioChannels{Pulse1,Pulse2,Triangle,Noise,DMC,VRC6,VRC6_Pulse1,VRC6_Pulse2,VRC6_Saw,Namco,MMC5,MMC5_Pulse1,MMC5_Pulse2,MMC5_PCM};
	//Audio Settings
	/**
	 * PreMix master level. 
	 */
	public static int masterMixLevel=100;
	/**
	 * PreMix level of APU pulse channel 1.
	 */
	public static int pulse1MixLevel=100;
	/**
	 * PreMix level of APU pulse channel 2.
	 */
	public static int pulse2MixLevel=100;
	/**
	 * PreMix level of APU triangle channel.
	 */
	public static int triangleMixLevel=100;
	/**
	 * PreMix level of APU noise channel.
	 */
	public static int noiseMixLevel=100;
	/**
	 * PreMix level of APU DMC channel.
	 */
	public static int dmcMixLevel=100;
	/**
	 * PreMix level of VRC6 expansion audio chip.
	 */
	public static int vrc6MixLevel=100;
	/**
	 * PreMix level of Namco expansion audio chip.
	 */
	public static int namcoMixLevel=100;
	/**
	 * PreMix level of MMC5 expansion audio chip.
	 */
	public static int mmc5MixLevel=100;
	/**
	 * PreMix level of Sunsoft5B expansion audio.
	 */
	public static int sunsoft5BMixLevel=100;
	/**
	 * Internal sampling rate of a NES instance. READ ONLY. Changes to this value will not
	 * effect a NES instance. Changes should be made using NES.setSampleRate(int rate).
	 */
	public static int sampleRate = 44100;
	/**
	 * Size of the internal audio buffer in milliseconds.
	 */
	public static int audioBufferSize = 20;

	public static int pulse1Panning=0;
	public static int pulse2Panning=0;
	public static int trianglePanning=0;
	public static int noisePanning=0;
	public static int dmcPanning=0;
	public static int vrc6Panning=0;
	public static int namcoPanning=0;
	public static int mmc5Panning=0;
	public static int sunsoft5BPanning=0;

	public static boolean highQualitySampling = false;
	/**
	 * Cut off time for a track in the NSF Player. Integer value in frames. 60 * (# of seconds)
	 */
	public static int nsfPlayerSongLength = 7200;
	
	

	public NesSettings(){}
	
	/**
	 * Saves NES settings to a configuration file.
	 * @param config - File to which settings will be saved.
	 */
	public static void saveSettings(File config){
		FileOutputStream output;
		try {
			output = new FileOutputStream(config);
			saveAudio();
			saveGraphics();
			saveEmulation();
			prop.store(output, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Loads NES settings from a saved configuration file. Or loads default values.
	 * @param config - File to load settings from.
	 * @throws IOException
	 */
	public static void loadSettings(File config) throws IOException{
		prop = new Properties();
		if(!config.exists()){
		}
		else{
			FileInputStream input = new FileInputStream(config);
			prop.load(input);
			loadAudio();
			loadGraphics();
			loadEmulation();		
		}
	}	
	private static void loadAudio(){
		masterMixLevel = Integer.parseInt(prop.getProperty("mastermixlevel","100"));
		pulse1MixLevel = Integer.parseInt(prop.getProperty("pulse1mixlevel","100"));
		pulse2MixLevel = Integer.parseInt(prop.getProperty("pulse2mixlevel","100"));
		triangleMixLevel = Integer.parseInt(prop.getProperty("trianglemixlevel","100"));
		noiseMixLevel = Integer.parseInt(prop.getProperty("noisemixlevel","100"));
		dmcMixLevel = Integer.parseInt(prop.getProperty("dmcmixlevel", "100"));
		vrc6MixLevel = Integer.parseInt(prop.getProperty("vrc6mixlevel", "100"));
		namcoMixLevel = Integer.parseInt(prop.getProperty("namcomixlevel", "100"));
		mmc5MixLevel = Integer.parseInt(prop.getProperty("mmc5mixlevel", "100"));
		sunsoft5BMixLevel = Integer.parseInt(prop.getProperty("sunsoft5bmixlevel", "100"));

		pulse1Panning = Integer.parseInt(prop.getProperty("pulse1panning", "0"));
		pulse2Panning = Integer.parseInt(prop.getProperty("pulse2panning", "0"));
		trianglePanning = Integer.parseInt(prop.getProperty("trianglepanning", "0"));
		noisePanning = Integer.parseInt(prop.getProperty("noisepanning", "0"));
		dmcPanning = Integer.parseInt(prop.getProperty("dmcpanning", "0"));
		vrc6Panning = Integer.parseInt(prop.getProperty("vrc6panning", "1"));
		mmc5Panning = Integer.parseInt(prop.getProperty("mmc5panning", "0"));
		namcoPanning = Integer.parseInt(prop.getProperty("namcopanning", "0"));
		sunsoft5BPanning = Integer.parseInt(prop.getProperty("sunsoft5bpanning", "0"));

		audioBufferSize = Integer.parseInt(prop.getProperty("audiobuffersize", "20"));
		sampleRate = Integer.parseInt(prop.getProperty("samplerate", "44100"));
		highQualitySampling = prop.getProperty("highqualitysampling", "true").equals("true");
		nsfPlayerSongLength = Integer.parseInt(prop.getProperty("nsfplayersonglength", "7200"));
	}
	private static void saveAudio(){
		prop.setProperty("mastermixlevel", masterMixLevel+"");
		prop.setProperty("pulse1mixlevel", pulse1MixLevel+"");
		prop.setProperty("pulse2mixlevel", pulse2MixLevel+"");
		prop.setProperty("trianglemixlevel", triangleMixLevel+"");
		prop.setProperty("noisemixlevel", noiseMixLevel+"");
		prop.setProperty("dmcmixlevel", dmcMixLevel+"");
		prop.setProperty("vrc6mixlevel", vrc6MixLevel+"");
		prop.setProperty("namcomixlevel", namcoMixLevel+"");
		prop.setProperty("mmc5mixlevel", mmc5MixLevel+"");
		prop.setProperty("sunsoft5bmixlevel", sunsoft5BMixLevel+"");

		prop.setProperty("pulse1panning", pulse1Panning+"");
		prop.setProperty("pulse2panning", pulse2Panning+"");
		prop.setProperty("trianglepanning", trianglePanning+"");
		prop.setProperty("noisepanning", noisePanning+"");
		prop.setProperty("dmcpanning", dmcPanning+"");
		prop.setProperty("vrc6panning", vrc6Panning+"");
		prop.setProperty("mmc5panning", mmc5Panning+"");
		prop.setProperty("namcopanning", namcoPanning+"");
		prop.setProperty("sunsoft5bpanning", sunsoft5BPanning+"");

		prop.setProperty("audiobuffersize", audioBufferSize+"");
		prop.setProperty("samplerate", sampleRate+"");
		prop.setProperty("highqualitysampling",highQualitySampling+"");
		prop.setProperty("nsfplayersonglength", nsfPlayerSongLength+"");
	}
	private static void loadGraphics(){
		RenderBackground = prop.getProperty("renderbackground", "true").equals("true");
		RenderSprites = prop.getProperty("rendersprites", "true").equals("true");
		RenderMethod = Integer.parseInt(prop.getProperty("rendermethod", "2"));
		selectedPalette = prop.getProperty("selectedpalette","defaultPalette");
		NesColors.setCustomPalette(prop.getProperty("custompalette",""));
		NesColors.updatePalette(selectedPalette);
	}
	private static void saveGraphics(){
		prop.setProperty("renderbackground", RenderBackground+"");
		prop.setProperty("rendersprites", RenderSprites+"");
		prop.setProperty("rendermethod", RenderMethod+"");
		prop.setProperty("selectedpalette", selectedPalette);
		prop.setProperty("custompalette", NesColors.getCustomPalette());
	}
	private static void loadEmulation(){
		politeFrameTiming = prop.getProperty("politeframetiming", "true").equals("true");
		frameLimit = prop.getProperty("framelimit", "true").equals("true");
	}
	private static void saveEmulation(){
		prop.setProperty("politeframetiming", politeFrameTiming+"");
		prop.setProperty("framelimit", frameLimit+"");
	}
	static void logSampleRate(int rate){
		sampleRate = rate;
	}

	static void logInternalPalette(String palette) {
		selectedPalette = palette;
	}
}
