package core;

import java.io.File;
import java.io.IOException;

import core.exceptions.UnSupportedMapperException;

/**
 * Interface of guaranteed functions provided by a NES object.
 * @author Jordan Howe
 *
 */
public interface NESAccess extends Runnable {
	
	/*
	 * Nes Machine Setup
	 */
	/**
	 * Sets up the call back for the nes machine. MUST be called before loadROM().
	 * @param system - NESCallback Object
	 */
	void setCallback(NESCallback system);
	/**
	 * Loads the rom and initializes the nes machine.
	 * @param rom - File for the machine to execute.
	 * @throws IOException
	 * @throws UnSupportedMapperException - Triggered if a given rom is unsupported.
	 */
	void loadRom(File rom) throws IOException, UnSupportedMapperException;
	
	
	
	
	
	/*
	 * Runnable version oriented commands.
	 */
	/**
	 * NES is a runnable and can be treated as such.
	 * These are the relevant methods.
	 */
	void run();
	/**
	 * Causes the thread running the NES instance to terminate.
	 */
	void exit();
	/**
	 * Pauses a running NES instance. Returns when the nes has
	 * successfully paused.
	 */
	void pause();
	/**
	 * Unpauses a paused NES instance. Returns immediately.
	 */
	void unpause();
	/**
	 * If nes is currently paused, togglePause() will return immediately. Otherwise
	 * it will return when nes has been successfully paused.
	 */
	void togglePause();
	/**
	 * 
	 * @return - A double representing the amount of frames processed in the last second.
	 */
	double getFPS();
	
	
	
	/*
	 * Single step functions
	 */
	/**
	 * It can also be controlled by the main program. This method advances the execution
	 * of the machine a single frame.
	 */
	void runFrame();
	/**
	 * Runs a single cpu cycle on the nes machine. May or may not trigger audio or video callbacks.
	 */
	void runCPUCycle();
	
	
	
	
	/*
	 * Audio Configuration
	 */
	/**
	 * Sets the internal sampling rate for audio.
	 * @param rate- Rate in Hz.
	 */
	void setSampleRate(int rate);
	
	/*
	 * Palette manipulation.
	 */
	/**
	 * Sets the internal palette for rendering modes 1 and 2. 
	 * @param palette - Acceptable inputs can be found in NesSettings.palettes
	 */
	static void setInternalPalette(String palette) {}
	/**
	 * Returns an int[] containing the RGB values of the selected internal palette.
	 * @return
	 */
	static int[] getInternalPaletteRGB(String palette) {return null;}
	/**
	 * Sets the internal palette to the supplied palette.
	 * @param palette - int[] where the ints are in RGB format.
	 */
	static void setCustomPalette(int[] palette) {}
	
	
	
	
		
	
	void saveState(String slot) throws IOException;
	void restoreState(String slot) throws IOException, ClassNotFoundException;
	
	void saveGame() throws IOException;
	void loadSave() throws IOException;
	
	/*
	 * Debug and NES state functions.
	 */
	Object[][] getAudioChannelInfo();
	/**
	 * HIGHLY SUBJECT TO CHANGE.
	 * 
	 * @return - will return an array of CPU debugging information
	 */
	Object[] getCPUDebugInfo();
	/**
	 * HIGHLY SUBJECT TO CHANGE.
	 * 
	 * @return - will return an array of PPu debugging information
	 */
	Object[] getPPUDebugInfo();
	/**
	 * HIGHLY SUBJECT TO CHANGE.
	 * 
	 * @return - will return an array of APU debugging information
	 */
	int[] getAPUDebugInfo();
	/**
	 * HIGHLY SUBJECT TO CHANGE.
	 * 
	 * @return - will return an array of Mapper debugging information
	 */
	int[] getMapperDebugInfo();
}
