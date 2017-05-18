package com;
import java.util.ArrayList;

import audio.*;
import mappers.Mapper;


public class APU implements java.io.Serializable{
	private static final long serialVersionUID = 2400733667863038798L;
	private final Triangle triangle = new Triangle();
	private final Pulse pulse1 = new Pulse(true);
	private final Pulse pulse2 = new Pulse(false);
	private final Noise noise = new Noise();
	private final DMC dmc;
	private ArrayList<Channel> expansionAudio;
	public AudioInterface audio;
	public final AudioMixer mix;
	private final Mapper map;
	//Vars for save state
	private boolean stepmode4=true;
	int stepcycle;
	private boolean irqInhibit;
	private boolean frameInterrupt;
	boolean doFrameStep;
	private boolean evenclock;
	private int block;
	private int stepNumber;
	private int delay=-1;
	public int framecounter;
	private int cpucounter;
	public long cyclenum;
	private int cycleper;
	private boolean expansion;
	
	
	public APU(Mapper m){
		
		map = m;
		dmc =new DMC(map);
		expansionAudio = new ArrayList<Channel>();
		mix = new AudioMixer(pulse1,pulse2,triangle,noise,dmc,expansionAudio);
		cycleper= mix.intcyclespersample;
		cpucounter = 10;
		
		expansion = false;
	}
	public void addExpansionChannel(Channel chan){
		expansionAudio.add(chan);
		expansion = true;
	}
	public void writeRegister(int index,byte b){
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
			if((b&16)==0)
				dmc.disable();
			else
				dmc.enable();
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
		//update();
	}
	private void setIRQ(){
		if(!irqInhibit){
			if(!frameInterrupt){
				map.cpu.doIRQ++;
				frameInterrupt=true;
			}
		}
	}
	private int samplenum;
	public void doCycle(){
		if(delay>0){
			delay--;
		}
		else if(delay ==0){
			cpucounter=0;
			delay =-1;
		}
		triangle.clockTimer();
		if(!evenclock){
			pulse1.clockTimer();
			pulse2.clockTimer();
			noise.clockTimer();	
			dmc.clockTimer();
			evenclock = true;
			cyclenum++;
			
			
		}
		else{
			evenclock = false;
			dmc.buildOutput();	
		}
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
		if((samplenum%cycleper)==0)
			mix.sendOutput();
	}

}
