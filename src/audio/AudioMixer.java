package audio;

import ui.UserSettings;

public class AudioMixer {
	public AudioInterface audio;
	Triangle triangle;
	Pulse pulse1;
	Pulse pulse2;
	Noise noise;
	DMC dmc;
	int samplenum;
	double cyclespersample;
	
	public AudioMixer(Pulse p1, Pulse p2, Triangle t, Noise n, DMC d){
		audio = new AudioInterface();
		cyclespersample = 1789773.0/audio.samplerate;
		pulse1 = p1;
		pulse2 = p2;
		triangle = t;
		noise = n;
		dmc = d;
	}
	public void sample(){
		if((samplenum%cyclespersample)<1){
			int sample = (int)((pulse1.total/cyclespersample)*(UserSettings.pulse1MixLevel/100.0));
			sample+=(int)((pulse2.total/cyclespersample)*(UserSettings.pulse2MixLevel/100.0));
			sample+=(int)((noise.total/cyclespersample)*(UserSettings.noiseMixLevel/100.0));
			sample+=(int)((triangle.total/cyclespersample)*(UserSettings.triangleMixLevel/100.0));
			sample+=(int)((dmc.total/cyclespersample)*(UserSettings.dmcMixLevel/100.0));
			pulse1.total=0;
			pulse2.total=0;
			noise.total=0;
			triangle.total=0;
			dmc.total=0;
			audio.outputSample((int) ((sample*400)*(UserSettings.masterMixLevel/100.0)));
			samplenum =0;
		}
		else{
			pulse1.buildOutput();
			pulse2.buildOutput();
			noise.buildOutput();
			triangle.buildOutput();
			dmc.buildOutput();
			
		}
		samplenum++;
	
	}
}
