package audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import ui.UserSettings;

public class AudioInterface {
	SourceDataLine sdl;
	byte[] audiobuffer;
	int bufptr = 0;
	public final int samplerate = 48000;
	
	public AudioInterface(){
		audiobuffer = new byte[(samplerate*2)/22];
		AudioFormat form = new AudioFormat(samplerate,16,2,true,false);
		try {
			sdl = AudioSystem.getSourceDataLine(form);
			sdl.open(form, audiobuffer.length*8);
			sdl.start();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}
	public void outputSample(int sample){
		if(sample>32768)
			sample=32768;
		audiobuffer[bufptr] = (byte)(sample&0xff);
		audiobuffer[bufptr+1]=(byte)((sample>>8)&0xff);
		audiobuffer[bufptr+2] = (byte)(sample&0xff);
		audiobuffer[bufptr+3]=(byte)((sample>>8)&0xff);
		bufptr+=4;
		if(bufptr+4>=audiobuffer.length-1){
			if(sdl.available()>bufptr-4&&UserSettings.AudioEnabled)
				sdl.write(audiobuffer,0,bufptr-4);
			bufptr=0;
		}
	}
	public void close(){
		sdl.close();
	}
	/*public void flushFrame(){
		sdl.write(audiobuffer, 0, bufptr);
		bufptr = 0;
	}*/
}
