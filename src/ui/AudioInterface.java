package ui;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import core.NesSettings;

public class AudioInterface implements java.io.Serializable {
	private static final long serialVersionUID = 25781276857491755L;
	transient SourceDataLine sdl;
	SystemUI sys;
	transient byte[] audiobuffer;
	transient int[] audioints;
	int bufptr = 0;	
	public boolean lock;
	public boolean inaudio;
	Visualizer scope;
	public AudioInterface(SystemUI s){
		sys = s;
		//scope = new Visualizer();
		restartSDL();
	}
	public void restartSDL(){
		AudioFormat form = new AudioFormat(NesSettings.sampleRate,16,2,true,false);
		bufptr=0;
		audioints = new int[NesSettings.sampleRate/60];
		if(scope!=null)
			scope.setAudio(audioints);
		audiobuffer = new byte[audioints.length*4];
		try {
			if(sdl!=null)
				sdl.close();
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
		audioints[bufptr++] = (int)(sample*1);
		if(bufptr>=audioints.length){
			bufptr = 0;
			sendsample();
		}
	}
	public void setAudioFrame(int[] audiobuf){
		audioints = audiobuf;
		audiobuffer = new byte[audioints.length*4];
		sendsample();
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
		if((sdl.available()>=audiobuffer.length&&UISettings.AudioEnabled)||UISettings.lockVideoToAudio){
				sdl.write(audiobuffer,0,audiobuffer.length);
				if(scope!=null&&scope.isVisible()){
					if(scopefrequency==scopecount++){
						scope.setAudio(audioints);
						scope.setFreq(sys.AudioChannelInfoCallback());
						scope.paintscope();
						scopecount = 0;
					}
				}
				
		}
		bufptr=0;
	}
	int scopefrequency = 1;
	int scopecount = 0;
	public void showscope(){
		if(scope!=null)
			scope.setVisible(true);
		else {
			scope = new Visualizer();
			scope.setVisible(true);
		}
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
