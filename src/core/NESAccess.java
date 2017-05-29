package core;

import java.io.File;
import java.io.IOException;

public interface NESAccess {
	
	/*
	 * Nes Machine Setup
	 */
	/**
	 * Sets up the call back for the nes machine. MUST be called before loadROM().
	 * @param system- NESCallback Object
	 */
	public void setCallback(NESCallback system);
	/**
	 * Loads the rom and initializes the nes machine.
	 * @param rom - File for the machine to execute.
	 * @throws IOException
	 */
	public void loadRom(File rom) throws IOException;
	
	/*
	 * Runnable version oriented commands.
	 */
	/**
	 * NES is a runnable and can be treated as such.
	 * These are the relevant methods.
	 */
	public void run();
	
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
	 * 
	 * @return - A double representing the amount of frames processed in the last second.
	 */
	public double getFPS();
	
	
	/*
	 * 
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
	/**
	 * Sets the internal sampling rate for audio.
	 * @param rate- Rate in Hz.
	 */
	public void setSampleRate(int rate);
	/**
	 * Causes the thread running the NES instance to terminate.
	 */
	public void exit();
	public void saveState(String slot) throws IOException;
	public void restoreState(String slot) throws IOException, ClassNotFoundException;
	
	public void saveGame() throws IOException;
	public void loadSave() throws IOException;
	
	
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
