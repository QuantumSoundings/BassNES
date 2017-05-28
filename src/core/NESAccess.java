package core;

import java.io.IOException;

public interface NESAccess {
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
	 * @return - A double representing the amount of frames proccessed in the last second.
	 */
	public double getFPS();
	
	/**
	 * It can also be controlled by the main program. This method advances the execution
	 * of the machine a single frame.
	 */
	public void runFrame();
	
	/**
	 * Sets the internal sampling rate for audio.
	 * @param rate- Rate in Hz.
	 */
	public void setSampleRate(int rate);
	
	
	public void saveState(String slot) throws IOException;
	public void restoreState(String slot) throws IOException, ClassNotFoundException;
	
	public void saveGame() throws IOException;
	public void loadSave() throws IOException;
	
	
	public Object[][] getAudioChannelInfo();
}
