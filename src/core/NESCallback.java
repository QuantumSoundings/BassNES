package core;
/**
 * Interface for an NES object's callback functions.
 * @author Jordan Howe
 *
 */
public interface NESCallback {
	
	
	/**
	 * Called once per invocation of runFrame().
	 * @param p - Array of integers representing the nes display output. Depending on
	 * the value of NesSettings.RenderMethod, p will contain either RGB values for rendering to
	 * the screen, or raw nes internal values.
	 */
	void videoCallback(int[] pixels);
	/**
	 * Called many times per invocation of runFrame().
	 * @param audiosample- variable length array containing the unmixed current output
	 * of all the audio channels.
	 */
	void unmixedAudioSampleCallback(int[] audiosample);
	/**
	 * Called many times per invocation of runFrame().
	 * @param audiosample- Premixed output off the audio channels.
	 */
	void audioSampleCallback(int audiosample);
	/**
	 * Called once per invocation of runFrame().
	 * @param audioInts- An int[] with a size determined by the audioBufferSize and sampleRate settings.
	 * The array alternates left and right channels. Ex. [L,R,L,R...]
	 */
	void audioFrameCallback(int[] audioInts);
	/**
	 * 
	 * @return- boolean[][] indicating whether buttons are pressed. The order is:
	 * { 
	 * 	  Controller 1 { a , b , select , start , up , down , left , right }
	 *    Controller 2 { a , b , select , start , up , down , left , right }
	 * }
	 */
	boolean[][] pollController();
}
