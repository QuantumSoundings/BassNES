package audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import ui.UserSettings;

public class AudioInterface implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 25781276857491755L;
	transient SourceDataLine sdl;
	transient byte[] audiobuffer;
	int bufptr = 0;
	public final int samplerate = 44100;
	
	public AudioInterface(){
		restartSDL();
	}
	public void restartSDL(){
		AudioFormat form = new AudioFormat(samplerate,16,2,true,false);
		audiobuffer = new byte[samplerate/60 *2 *4];
		try {
			sdl = AudioSystem.getSourceDataLine(form);
			sdl.open(form,audiobuffer.length*3);
			sdl.start();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}	
	}
	public void outputSample(int sample){
		if(sample>30000)
			sample=30000;
		if(sample<-30000)
			sample=-30000;
		audiobuffer[bufptr] = (byte)(sample&0xff);
		audiobuffer[bufptr+1]=(byte)((sample>>8)&0xff);
		audiobuffer[bufptr+2] = (byte)(sample&0xff);
		audiobuffer[bufptr+3]=(byte)((sample>>8)&0xff);
		bufptr+=4;
		if(bufptr+4>=audiobuffer.length-1){
			if((sdl.available()>bufptr-4&&UserSettings.AudioEnabled)||UserSettings.lockvideotoaudio)
				sdl.write(audiobuffer,0,bufptr-4);
			bufptr=0;
		}
	}
	//public void blip_add_delta
	public void close(){
		sdl.close();
	}
}
