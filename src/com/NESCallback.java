package com;

public interface NESCallback {
	
	
	/**
	 * Called once per invocation of runFrame().
	 * @param p - Array off pixels making up the current output of the nes display.
	 */
	public void videoCallback(int[] pixels);
	/**
	 * Called many times per invocation of runFrame().
	 * @param audiosample- variable length array containing the unmixed current output
	 * of all the audio channels.
	 */
	public void unmixedAudioSampleCallback(int[] audiosample);
	/**
	 * Called many times per invocationo of runFrame().
	 * @param audiosample- Premixed output off the audio channels.
	 */
	public void audioSampleCallback(int audiosample);
	/**
	 * Called once per invocation of runFrame().
	 * @param audioInts- An int[] of all audio samples generated during the execution
	 * of one invocation of runFrame().
	 */
	public void audioFrameCallback(int[] audioInts);
	/**
	 * 
	 * @return- boolean[][] indicating whether buttons are pressed. The order is:
	 * { 
	 * 	  Controller 1 { a , b , select , start , up , down , left , right }
	 *    Controller 2 { a , b , select , start , up , down , left , right }
	 * }
	 */
	public boolean[][] pollController();
}
