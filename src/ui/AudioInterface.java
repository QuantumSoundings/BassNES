package ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

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
		audioints = new int[(int)((NesSettings.sampleRate/1000.0)*NesSettings.audioBufferSize)*2];
		if(scope!=null)
			scope.setAudio(audioints);
		audiobuffer = new byte[audioints.length*2];
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
		audiobuffer = new byte[audioints.length*2];
		sendsample();
	}
	public void sendsample(){
		//lowpass();
		/*for(int i: audioints){
			audiobuffer[bufptr] = (byte) (i&0xff);
			audiobuffer[bufptr+1] = (byte) ((i>>8)&0xff);
			audiobuffer[bufptr+2] = (byte) (i&0xff);
			audiobuffer[bufptr+3] = (byte) ((i>>8)&0xff);
			bufptr+=4;
		}*/
		bufptr=0;
		for(int i:audioints){
			audiobuffer[bufptr]=(byte)(i&0xff);
			audiobuffer[bufptr+1] = (byte) ((i>>8)&0xff);
			bufptr+=2;
		}
		if((sdl.available()>=audiobuffer.length&&UISettings.AudioEnabled)){//||UISettings.lockVideoToAudio){
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
		if(recording){
			if(recordingpointer >= savedRecording.length){
				byte[] temp = savedRecording;
				savedRecording = new byte[savedRecording.length*2];
				System.arraycopy(temp, 0, savedRecording, 0, temp.length);
			}
			System.arraycopy(audiobuffer, 0, savedRecording, recordingpointer, audiobuffer.length);
			recordingpointer+= audiobuffer.length;
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
	boolean recording=false;
	
	byte[] savedRecording;
	int recordingpointer;
	public void startRecording(){
		System.out.println("Starting Audio Recording");
		OSD.addOSDMessage("Starting Audio Recording...", 120);
		savedRecording = new byte[audiobuffer.length];
		recordingpointer = 0;
		recording = true;		
	}
	public void stopRecording(){
		System.out.print("Stopping Audio Recording...");
		OSD.addOSDMessage("Stopping Audio Recording...", 120);
		File wave = new File(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE)+".wav");
		if(wave.exists()){
			int i = 1;
			while(wave.exists()){
				wave = new File(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE)+" ("+(i++)+").wav");
			}
		}
		byte[] temp = savedRecording;
		savedRecording = new byte[recordingpointer];
		System.arraycopy(temp, 0, savedRecording, 0, recordingpointer);
		AudioFileFormat.Type type = AudioFileFormat.Type.WAVE;
		AudioFormat format = new AudioFormat(NesSettings.sampleRate,16,2,true,false);
		ByteArrayInputStream in = new ByteArrayInputStream(savedRecording);
		AudioInputStream stream = new AudioInputStream(in,format,savedRecording.length/4);
		try {
			AudioSystem.write(stream, type, wave);
			System.out.println("Saved!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		recording = false;
	}
}
