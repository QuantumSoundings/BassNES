package core.audio;

import java.util.ArrayList;

import core.NesSettings;
import core.mappers.Mapper;

public class AudioMixer implements java.io.Serializable {

	private static final long serialVersionUID = -5418535414924993071L;
	private final Mapper map;
	private final Triangle triangle;
	private final Pulse pulse1;
	private final Pulse pulse2;
	private final Noise noise;
	private final DMC dmc;
	private ArrayList<Channel> expansionAudio;
	protected static double[] audioLevels;
	private Decimator resampler;

	private int[] audioBuffer;
	private int bufferPointer;
	private double cyclespersample;
	private int intcyclespersample;

	/*static double[] pulse_table = new double[]{0,
			0.01160914,0.022939481,0.034000949,0.044803002,0.055354659,0.065664528,0.075740825,0.085591398,0.095223748,0.104645048,0.113862159,0.122881647,0.131709801,0.140352645,0.148815953,0.157105263,0.165225885,
			0.173182917,0.180981252,0.188625592,0.196120454,0.203470178,0.210678941,0.21775076,0.224689499,0.231498881,0.23818249,0.244743777,0.251186072,0.257512581,0.263726398
	};
	static double[] tnd_table = new double[]{0,
			0.006699824,0.01334502,0.019936254,0.02647418,0.032959443,0.039392675,0.045774502,0.052105535,0.058386381,0.064617632,0.070799874,0.076933683,0.083019626,0.089058261,0.095050137,0.100995796,0.10689577,0.112750584,0.118560753,0.124326788,0.130049188,0.135728448,0.141365053,0.146959482,0.152512207,0.158023692,0.163494395,0.168924767,0.174315252,0.179666289,0.184978308
			,0.190251735,0.195486988,0.200684482,0.205844623,0.210967811,0.216054444,0.22110491,0.226119593,0.231098874,0.236043125,0.240952715,0.245828007,0.250669358,0.255477124,0.260251651,0.264993283,0.269702358,0.274379212,0.279024174,0.283637568,0.288219716,0.292770934,0.297291534,0.301781823,0.306242106,0.310672683,0.315073849,0.319445896,0.323789113,0.328103783
			,0.332390186,0.336648601,0.3408793,0.345082552,0.349258625,0.35340778,0.357530277,0.361626373,0.36569632,0.369740367,0.373758762,0.377751747
			,0.381719563,0.385662446,0.389580632,0.393474351,0.397343833,0.401189302,0.405010981,0.408809091,0.412583848,0.416335468,0.420064163,0.423770142,0.427453612,0.431114778,0.434753841,0.438371001,0.441966456,0.445540399,0.449093024,0.452624521,0.456135077,0.459624878,0.463094108,0.466542949,0.469971578,0.473380175,0.476768913,0.480137965
			,0.483487503,0.486817696,0.490128711,0.493420713,0.496693865,0.499948329,0.503184264,0.506401828,0.509601178,0.512782466,0.515945847,0.51909147,0.522219486,0.52533004,0.528423279,0.531499348,0.534558388,0.537600541,0.540625946,0.543634742,0.546627063,0.549603047,0.552562825,0.55550653,0.558434293,0.561346242,0.564242506,0.56712321
			,0.569988481,0.572838441,0.575673213,0.578492918,0.581297676,0.584087605,0.586862823,0.589623445,0.592369587,0.595101363,0.597818884,0.600522262,0.603211607,0.605887028,0.608548633,0.611196528,0.61383082,0.616451613,0.61905901
			,0.621653114,0.624234026,0.626801846,0.629356675,0.63189861,0.634427748,0.636944186,0.63944802,0.641939344,0.644418251,0.646884834,0.649339185,0.651781395,0.654211552,0.656629747,0.659036068,0.661430601,0.663813433,0.66618465,0.668544336,0.670892576,0.673229451,0.675555046,0.677869441,0.680172716,0.682464952,0.684746229,0.687016623,0.689276214,0.691525078
			,0.693763291,0.695990928,0.698208065,0.700414776,0.702611133,0.70479721,0.706973079,0.709138811,0.711294476,0.713440145,0.715575887,0.71770177,0.719817864,0.721924234,0.724020949,0.726108075,0.728185676,0.730253819,0.732312567,0.734361984,0.736402134,0.73843308,0.740454883,0.744471308,0.742467605
	};*/
	
	public AudioMixer(Pulse p1, Pulse p2, Triangle t, Noise n, DMC d,ArrayList<Channel> exp,Mapper m){
		map = m;
		pulse1 = p1;
		pulse2 = p2;
		triangle = t;
		noise = n;
		dmc = d;
		expansionAudio=exp;
		audioLevels=new double[5];
		updateSampleRate();
	}
	public int requestNewOutputLocation(){
		audioLevels = new double[audioLevels.length+1];
		return audioLevels.length-1;
	}
	public void updateSampleRate(){
		cyclespersample = 1789773.0/NesSettings.sampleRate;
		intcyclespersample = (int)cyclespersample;
		resampler = new Decimator(map,NesSettings.sampleRate);
		audioBuffer = new int[(int)((NesSettings.sampleRate/1000.0)*NesSettings.audioBufferSize)*2];
		bufferPointer=0;
	}

	public final void sendOutput(){
		if(NesSettings.highQualitySampling){
			double pulse_out = (95.88/(8128.0/(pulse1.getOutput()+pulse2.getOutput())+100));//0.00752 * (p1+p2);
			double tnd_out = (163.67/(24329.0/(3*triangle.getOutput()+2*noise.getOutput()+dmc.getOutput())+100));//0.00851*t + 0.00494*n + 0.00335*d;
			double sample = pulse_out + tnd_out;
			double expansion = 0;
			for(Channel chan: expansionAudio)
				expansion+= getAverageExpansion(chan,chan.getUserMixLevel());
			sample+=expansion;
			resampler.addInputSample((sample * 30000) * (NesSettings.masterMixLevel / 100.0));
		}
		else {
			for(int i = 0; i<audioLevels.length;i++)
				audioLevels[i]/=intcyclespersample;
			double p1 = audioLevels[0] * (NesSettings.pulse1MixLevel/100.0);
			double p2 = audioLevels[1] * (NesSettings.pulse2MixLevel/100.0);
			double t  = audioLevels[2] * (NesSettings.triangleMixLevel/100.0);
			double n  = audioLevels[3] * (NesSettings.noiseMixLevel/100.0);
			double d  = audioLevels[4] * (NesSettings.dmcMixLevel/100.0);
			//Get Left Channel
			double pulse_out = (95.88 / (8128.0 / (p1*(NesSettings.pulse1Panning>0?(100-NesSettings.pulse1Panning)/100.0:1) + p2*(NesSettings.pulse2Panning>0?(100-NesSettings.pulse2Panning)/100.0:1)) + 100));//0.00752 * (p1+p2);
			double tnd_out = (163.67 / (24329.0 / (3 * t*(NesSettings.trianglePanning>0?(100-NesSettings.trianglePanning)/100.0:1) + 2 * n*(NesSettings.noisePanning>0?(100-NesSettings.noisePanning)/100.0:1) + d*(NesSettings.dmcPanning>0?(100-NesSettings.dmcPanning)/100.0:1)) + 100));//0.00851*t + 0.00494*n + 0.00335*d;
			double sample = pulse_out + tnd_out ;
			double expansion = 0;
			for (Channel chan : expansionAudio)
				expansion += audioLevels[chan.outputLocation]*(chan.getUserMixLevel()/100.0)*chan.getOutput()*(chan.getUserPanning()>0?(100-chan.getUserPanning())/100.0:1);
			sample += expansion;
			sample = ((sample * 30000) * (NesSettings.masterMixLevel / 100.0));
			addSample((int)sample);
			//System.out.println("Left channel audio: "+sample);
			//Get Right Channel
			pulse_out = (95.88 / (8128.0 / (p1*(NesSettings.pulse1Panning<0?(NesSettings.pulse1Panning+100)/100.0:1) + p2*(NesSettings.pulse2Panning<0?(NesSettings.pulse2Panning+100)/100.0:1)) + 100));//0.00752 * (p1+p2);
			tnd_out = (163.67 / (24329.0 / (3 * t*(NesSettings.trianglePanning<0?(NesSettings.trianglePanning+100)/100.0:1) + 2 * n*(NesSettings.noisePanning<0?(NesSettings.noisePanning+100)/100.0:1) + d*(NesSettings.dmcPanning<0?(NesSettings.dmcPanning+100)/100.0:1)) + 100));//0.00851*t + 0.00494*n + 0.00335*d;
			sample = pulse_out + tnd_out;
			expansion = 0;
			for (Channel chan : expansionAudio) {
				expansion += audioLevels[chan.outputLocation]*(chan.getUserMixLevel()/100.0)*chan.getOutput() * (chan.getUserPanning() < 0 ? (chan.getUserPanning() + 100) / 100.0 : 1);
			}
			sample += expansion;
			sample = ((sample * 30000) * (NesSettings.masterMixLevel / 100.0));
			//System.out.println("Right channel audio: "+sample);
			addSample((int)sample);
			for(int i =0;i<audioLevels.length;i++)
				audioLevels[i] = 0;
		}
	}
	public final void addSample(int sample){
		audioBuffer[bufferPointer]=lowpass_filter(highpass_filter(sample));
		if(++bufferPointer==audioBuffer.length){
			bufferPointer=0;
			map.system.audioFrameCallback(audioBuffer);
		}
	}
	public void updateOutput(double[] out){
		audioLevels=out;
	}
	int dckiller=0;
	int lpaccum=0;
	private int highpass_filter(int sample) {
		//for killing the dc in the signal
		sample += dckiller;
		dckiller -= sample >> 8;//the actual high pass part
		dckiller += (sample > 0 ? -1 : 1);//guarantees the signal decays to exactly zero
		return sample;
	}

	private int lowpass_filter(int sample) {
		sample += lpaccum;
		lpaccum -= sample * 0.9;
		return lpaccum;
	}
	private double getAverageSample(Channel chan,int UserMix){
		double d =((audioLevels[chan.outputLocation]/intcyclespersample)*(UserMix/100.0));
		return d;
	}
	final double getAverageExpansion(Channel chan,int UserMix){
		double d =audioLevels[chan.outputLocation]*(UserMix/100.0)*chan.getOutput();
		audioLevels[chan.outputLocation] = 0;
		//System.out.println(d);
		return d;
	}
}
