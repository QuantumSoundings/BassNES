package core;

import java.io.File;
import java.io.IOException;

import core.exceptions.UnSupportedFileException;
import core.exceptions.UnSupportedMapperException;

/**
 * Interface of guaranteed functions provided by a NES object.
 * @author Jordan Howe
 *
 */
public interface NESAccess {
	
	/*
	 * Nes Machine Setup
	 */
	/**
	 * Sets up the call back for the nes machine. MUST be called before loadROM().
	 * @param system - NESCallback Object
	 */
	public void setCallback(NESCallback system);
	/**
	 * Loads the rom and initializes the nes machine.
	 * @param rom - File for the machine to execute.
	 * @throws IOException
	 * @throws UnSupportedMapperException - Triggered if a given rom is unsupported.
	 * @throws UnSupportedFileException - Triggered if a given file is not supported.
	 */
	public void loadRom(File rom) throws IOException, UnSupportedMapperException, UnSupportedFileException;
	
	
	
	
	
	/*
	 * Runnable version oriented commands.
	 */
	/**
	 * NES is a runnable and can be treated as such.
	 * These are the relevant methods.
	 */
	public void run();
	/**
	 * Causes the thread running the NES instance to terminate.
	 */
	public void exit();
	/**
	 * Pauses a running NES instance. Returns when the nes has
	 * successfully paused.
	 */
	public void pause();
	/**
	 * Unpauses a paused NES instance. Returns immediately.
	 */
	public void unpause();
	/**
	 * If nes is currently paused, togglePause() will return immediately. Otherwise
	 * it will return when nes has been successfully paused.
	 */
	public void togglePause();
	/**
	 * 
	 * @return - A double representing the amount of frames processed in the last second.
	 */
	public double getFPS();
	
	
	
	/*
	 * Single step functions
	 */
	/**
	 * It can also be controlled by the main program. This method advances the execution
	 * of the machine a single frame.
	 */
	public void runFrame();
	/**
	 * Runs a single cpu cycle on the nes machine. May or may not trigger audio or video callbacks.
	 */
	public void runCPUCycle();
	
	
	
	
	/*
	 * Audio Configuration
	 */
	/**
	 * Sets the internal sampling rate for audio.
	 * @param rate- Rate in Hz.
	 */
	public void setSampleRate(int rate);
	
	/*
	 * Palette manipulation.
	 */
	/**
	 * Sets the internal palette for rendering modes 1 and 2. 
	 * @param palette - Acceptable inputs can be found in NesSettings.palettes
	 */
	public static void setInternalPalette(String palette) {}
	/**
	 * Returns an int[] containing the RGB values of the selected internal palette.
	 * @return
	 */
	public static int[] getInternalPaletteRGB(String palette) {return null;}
	/**
	 * Sets the internal palette to the supplied palette.
	 * @param palette - int[] where the ints are in RGB format.
	 */
	public static void setCustomPalette(int[] palette) {}
	
	
	
	
		
	
	public void saveState(String slot) throws IOException;
	public void restoreState(String slot) throws IOException, ClassNotFoundException;
	
	public void saveGame() throws IOException;
	public void loadSave() throws IOException;
	
	/*
	 * Debug and NES state functions.
	 */
	public Object[][] getAudioChannelInfo();
	/**
	 * HIGHLY SUBJECT TO CHANGE.
	 * 
	 * @return - will return an array of CPU debugging information
	 */
	public Object[] getCPUDebugInfo();
	/**
	 * HIGHLY SUBJECT TO CHANGE.
	 * 
	 * @return - will return an array of PPu debugging information
	 */
	public Object[] getPPUDebugInfo();
	/**
	 * HIGHLY SUBJECT TO CHANGE.
	 * 
	 * @return - will return an array of APU debugging information
	 */
	public int[] getAPUDebugInfo();
	/**
	 * HIGHLY SUBJECT TO CHANGE.
	 * 
	 * @return - will return an array of Mapper debugging information
	 */
	public int[] getMapperDebugInfo();
}
