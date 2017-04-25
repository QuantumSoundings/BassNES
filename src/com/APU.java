package com;
import audio.*;
import mappers.Mapper;

import com.jsyn.*;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.LinearRamp;
import com.jsyn.unitgen.PulseOscillator;
import com.jsyn.unitgen.TriangleOscillator;
import com.jsyn.unitgen.WhiteNoise;



public class APU {
	public Synthesizer synth;
	LineOut lineout;
	LinearRamp lag;
	Triangle triangle = new Triangle(new TriangleOscillator());
	Pulse pulse1 = new Pulse(new PulseOscillator(),true);
	Pulse pulse2 = new Pulse( new PulseOscillator(),false);
	Noise noise = new Noise(new WhiteNoise());
	DMC dmc;
	Mapper map;
	byte status;
	boolean stepmode4=true;
	int stepcycle;
	boolean irqInhibit;
	boolean frameInterrupt;
	boolean doFrameStep;
	boolean evenclock;
	int block;
	int stepNumber;
	public int delay=-1;
	public int framecounter;
	
	int cpucounter;
	
	
	public APU(Mapper m){
		map = m;
		synth = JSyn.createSynthesizer();
		addGenerators();
		//synth.add(lag = new LinearRamp());
		dmc =new DMC(new PulseOscillator(),map);
		synth.add(lineout=new LineOut());
		pulse1.wave.output.connect(0,lineout.input,0);
		pulse1.wave.output.connect(0,lineout.input,1);
		pulse2.wave.output.connect(0,lineout.input,0);
		pulse2.wave.output.connect(0,lineout.input,1);
		triangle.wave.output.connect(0,lineout.input,0);
		triangle.wave.output.connect(0,lineout.input,1);
		noise.wave.output.connect(0,lineout.input,0);
		noise.wave.output.connect(0,lineout.input,1);
		//lag.output.connect(pulse1.wave.amplitude);
		synth.start();
		lineout.start();
		cpucounter = 10;
	}
	void addGenerators(){
		synth.add(triangle.wave);
		synth.add(pulse1.wave);
		synth.add(pulse2.wave);
		synth.add(noise.wave);
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
			status = b;
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
		}
		else if(index==0x4017){
			stepmode4 = (b&0x80)==0?true:false;
			stepNumber = 0;
			if((b&0x80)!=0){
				stepNumber =2;
				frameClock();
				cpucounter=0;
				block=1;
			}
			irqInhibit = (b&0x40)==0?false:true;
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
			b|= dmc.samplelength>0?16:0;
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
	public void update(){
		pulse1.updateWave();
		pulse2.updateWave();
		triangle.updateWave();
		noise.updateWave();
	}
	public void frameClock(){
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
		update();
	}
	void setIRQ(){
		if(!irqInhibit){
			if(!frameInterrupt){
				map.cpu.doIRQ++;
				frameInterrupt=true;
			}
		}
	}
	public void doCycle(int cycle){
		if(delay>0){
			delay--;
		}
		else if(delay ==0){
			cpucounter=0;
			delay =-1;
		}
		if(cycle%2==0){
			noise.clockTimer();	
			dmc.clock();
			evenclock = true;
		}
		else
			evenclock = false;
		if(stepmode4){
			switch(cpucounter){
			case 7459: stepNumber = 0;frameClock();break;
			case 14915:stepNumber = 1;frameClock();break;
			case 22373:stepNumber = 2;frameClock();break;
			case 29830:;setIRQ();break;
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
		
		
	}

}
