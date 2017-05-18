package audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import ui.UserSettings;

public class AudioInterface implements java.io.Serializable {
	private static final long serialVersionUID = 25781276857491755L;
	transient SourceDataLine sdl;
	transient byte[] audiobuffer;
	transient int[] audioints;
	int bufptr = 0;	
	
	public AudioInterface(){
		restartSDL();
	}
	public void restartSDL(){
		AudioFormat form = new AudioFormat(UserSettings.sampleRate,16,2,true,false);
		audioints = new int[UserSettings.sampleRate/60];
		audiobuffer = new byte[audioints.length*4];
		try {
			sdl = AudioSystem.getSourceDataLine(form);
			sdl.open(form,audiobuffer.length*3);
			sdl.start();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}	
	}
	public void outputSample(int sample){
		if(sample>32768)
			sample=32768;
		if(sample<-32768)
			sample=-32768;
		audioints[bufptr] = sample;
		bufptr++;
		if(bufptr>=audioints.length){
			bufptr = 0;
			sendsample();
		}
	}
	
	public void sendsample(){
		lowpass();	
		for(int i: audioints){
			audiobuffer[bufptr] = (byte) (i&0xff);
			audiobuffer[bufptr+1] = (byte) ((i>>8)&0xff);
			audiobuffer[bufptr+2] = (byte) (i&0xff);
			audiobuffer[bufptr+3] = (byte) ((i>>8)&0xff);
			bufptr+=4;
		}
		if((sdl.available()>=audiobuffer.length&&UserSettings.AudioEnabled)||UserSettings.lockvideotoaudio)
				sdl.write(audiobuffer,0,audiobuffer.length);
		bufptr=0;
	}
	int smoothing = 1;
	private void lowpass(){
		int value = audioints[0];
		int len = audioints.length;
		for(int i = 1;i<len;i++ ){
			int currentval = audioints[i];
			value += (currentval - value)/smoothing;
			audioints[i] = value;
		}
	}
	//public void blip_add_delta
	public void close(){
		sdl.close();
	}
}
