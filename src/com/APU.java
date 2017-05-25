package com;
import java.util.ArrayList;

import audio.*;
import mappers.Mapper;
import ui.UserSettings;


public class APU implements java.io.Serializable{
	private static final long serialVersionUID = 2400733667863038798L;
	
	private final Mapper map;
	//Default Audio Channels
	private final Triangle triangle = new Triangle();
	private final Pulse pulse1 = new Pulse(true);
	private final Pulse pulse2 = new Pulse(false);
	private final Noise noise = new Noise();
	private final DMC dmc;
	//Expansion Audio Channels
	private ArrayList<Channel> expansionAudio;
	private Channel[] expansionAudioArray;
	//public AudioInterface audio;
	
	//Mixing Variables
	private double cyclespersample;
	private int intcyclespersample;
	private int samplenum;
	//public final AudioMixer mix;
	
	
	

	private boolean stepmode4=true;
	int stepcycle;
	private boolean irqInhibit;
	private boolean frameInterrupt;
	boolean doFrameStep;
	private boolean evenclock=false;
	private int block;
	private int stepNumber;
	private int delay=-1;
	public int framecounter;
	private int cpucounter;
	public long cyclenum;
	private boolean expansion;
	
	
	public APU(Mapper m){
		
		map = m;
		dmc =new DMC(map);
		expansionAudio = new ArrayList<Channel>();
		expansionAudioArray = new Channel[0];
		cyclespersample = 1789773.0/UserSettings.sampleRate;
		intcyclespersample = (int)cyclespersample;
		cpucounter = 10;
		expansion = false;
		writeRegister(0x4015,(byte)0);
	}
	public void addExpansionChannel(Channel chan){
		expansionAudio.add(chan);
		//expansionAudioArray = (Channel[]) expansionAudio.toArray();
		expansion = true;
	}
	public void updateaudio(){
		cyclespersample = 1789773.0/UserSettings.sampleRate;
		intcyclespersample = (int)cyclespersample;
	}
	public void writeRegister(int index,byte b){
		//System.out.println("Writing to apu register");
		if(index>=0x4000&&index<0x4004){
			pulse1.registerWrite(index, b,cpucounter);
		}
		else if(index>=0x4004&&index<0x4008){
			pulse2.registerWrite(index, b,cpucounter);
		}
		else if(index>=0x4008&&index<0x400c){
			triangle.registerWrite(index, b,cpucounter);
		}
		else if(index>=0x400c&&index<0x4010){
			noise.registerWrite(index, b,cpucounter);			
		}
		else if(index>=0x4010&&index<0x4014){
			dmc.registerWrite(index, b);
		}
		else if(index==0x4015){
			if((b&1)==0)
				pulse1.disable();
			else
				pulse1.enable();
			if((b&2)==0)
				pulse2.disable();
			else
				pulse2.enable();
			if((b&4)==0)
				triangle.disable();
			else
				triangle.enable();
			if((b&8)==0)
				noise.disable();
			else
				noise.enable();
			if((b&16)==0){
				dmc.sampleremaining=0;
				dmc.silence = true;
			}
			else{
				if(dmc.sampleremaining==0){
					dmc.restart();
				}
			}
			//if(dmc.irqflag){
			//	map.cpu.doIRQ--;
			//	dmc.irqflag=false;
			//}
			dmc.clearFlag();
		}
		else if(index==0x4017){
			stepmode4 = (b & 0x80) == 0;
			stepNumber = 0;
			if((b&0x80)!=0){
				stepNumber =2;
				frameClock();
				cpucounter=0;
				block=1;
			}
			irqInhibit = (b & 0x40) != 0;
			if(irqInhibit&&frameInterrupt){
				map.cpu.doIRQ--;
				frameInterrupt = false;
			}
			if(!evenclock)
				delay = 0;
			else
				delay = 1;
			cpucounter=0;
		}
	}
	public byte readRegisters(int index){
		if(index == 0x4015){			
			byte b = 0;
			b|= pulse1.lengthcount>0?1:0;
			b|= pulse2.lengthcount>0?2:0;
			b|= triangle.lengthcount>0?4:0;
			b|= noise.lengthcount>0?8:0;
			b|= dmc.sampleremaining>0?16:0;
			b|= frameInterrupt?64:0;
			if(frameInterrupt){
				map.cpu.doIRQ--;
				frameInterrupt = false;
			}
			b|= dmc.irqflag?128:0;
			return b;
		}
		return 0;
	}

	private void frameClock(){
		if(stepmode4){
			if(stepNumber%4==1||stepNumber%4==3){
				pulse1.lengthClock();
				pulse1.sweepClock();
				pulse2.lengthClock();
				pulse2.sweepClock();			
				triangle.lengthClock();
				noise.lengthClock();
			}
			
			pulse1.envelopeClock();
			pulse2.envelopeClock();
			triangle.linearClock();
			noise.envelopeClock();
			if(stepNumber%4==3){
				setIRQ();
			}
		}
		else{
			if(stepNumber ==2){
				pulse1.lengthClock();
				pulse1.sweepClock();
				pulse2.lengthClock();
				pulse2.sweepClock();
				triangle.lengthClock();
				noise.lengthClock();
				
				pulse1.envelopeClock();
				pulse2.envelopeClock();
				triangle.linearClock();
				noise.envelopeClock();
			}
			else if(stepNumber ==1){
				pulse1.envelopeClock();
				pulse2.envelopeClock();
				triangle.linearClock();
				noise.envelopeClock();
			}
			else{}
		}
	}
	private void setIRQ(){
		if(!irqInhibit){
			if(!frameInterrupt){
				map.cpu.doIRQ++;
				frameInterrupt=true;
			}
		}
	}
	//Audio Mixing methods
	final double getAverageSample(Channel chan,int UserMix){
		double d =((chan.total/cyclespersample)*(UserMix/100.0));
		chan.total=0;
		return d;	
	}
	final double getAverageExpansion(Channel chan,int UserMix){
		double d =((chan.getOutput()/cyclespersample)*(UserMix/100.0));
		chan.total = 0;
		return d;
	}
	public void sendOutput(){
		double p1 = getAverageSample(pulse1,UserSettings.pulse1MixLevel);
		double p2 = getAverageSample(pulse2,UserSettings.pulse2MixLevel);
		double t = getAverageSample(triangle,UserSettings.triangleMixLevel);
		double n = getAverageSample(noise,UserSettings.noiseMixLevel);
		double d = getAverageSample(dmc,UserSettings.dmcMixLevel);
		double pulse_out = 0.00752 * (p1+p2);
		double tnd_out = 0.00851*t + 0.00494*n + 0.00335*d;
		double sample = pulse_out + tnd_out;
		double expansion = 0;
		for(Channel chan: expansionAudio)
			expansion+= getAverageExpansion(chan,chan.getUserMixLevel());
		sample+=expansion;
		sample = ((sample*30000)*(UserSettings.masterMixLevel/100.0));
		map.system.audioSampleCallback((int)sample);
		//System.out.println(sample);
	}
	private void sendOutputUnMixed(){
		int[] output = new int[5+expansionAudioArray.length];
		output[0] = (int) getAverageSample(pulse1,UserSettings.pulse1MixLevel);
		output[1] = (int) getAverageSample(pulse2,UserSettings.pulse2MixLevel);
		output[2] = (int) getAverageSample(triangle,UserSettings.triangleMixLevel);
		output[3] = (int) getAverageSample(noise,UserSettings.noiseMixLevel);
		output[4] = (int) getAverageSample(dmc,UserSettings.dmcMixLevel);
		int i = 5;
		for(Channel chan: expansionAudioArray)
			output[i++] = (int) getAverageExpansion(chan,chan.getUserMixLevel());
		map.system.unmixedAudioSampleCallback(output);
	}
	public void doCycle(){
		if(delay>0){
			delay--;
		}
		else if(delay ==0){
			cpucounter=0;
			delay =-1;
		}
		triangle.clockTimer();
		dmc.clockTimer();
		if(evenclock){
			pulse1.clockTimer();
			pulse2.clockTimer();
			noise.clockTimer();	
			cyclenum++;
		}
		evenclock=!evenclock;
		if(expansion)
			for(Channel chan:expansionAudio)
				chan.clockTimer();
		if(stepmode4){
			switch(cpucounter){
			case 7459: stepNumber = 0;frameClock();break;
			case 14915:stepNumber = 1;frameClock();break;
			case 22373:stepNumber = 2;frameClock();break;
			case 29830:
                setIRQ();break;
			case 29831:stepNumber = 3;frameClock();break;
			case 29832:setIRQ();break;
			case 37289:stepNumber = 0;frameClock();cpucounter=7459;break;
			default: break;
			}
		}
		else{
			switch(cpucounter){
			case 1:
				if(block<=0){
					stepNumber = 2;
					frameClock();
				}
				else 
					block--;
				break;
			case 7459:  stepNumber = 1;frameClock();break;
			case 14915: stepNumber = 2;frameClock();break;
			case 22373: stepNumber = 1;frameClock();break;
			case 29829: break;
			case 37283: stepNumber = 2;frameClock();cpucounter = 1;break;
			default:break;
			}
		}
		cpucounter++;
		samplenum++;
		if((samplenum%intcyclespersample)==0)
			sendOutput();
	}

}
