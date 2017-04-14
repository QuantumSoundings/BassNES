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
	Mapper map;
	byte status;
	boolean stepmode4=true;
	int stepcycle;
	boolean IRQFlag;
	boolean doFrameStep;
	int stepNumber;
	
	
	public APU(Mapper m){
		map = m;
		synth = JSyn.createSynthesizer();
		addGenerators();
		//synth.add(lag = new LinearRamp());
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
		
		/*timer = new Timer(0,new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				doFrameStep = true;
			}
		});*/
		//timer.setDelay(4);
		//timer.start();

		
	}
	void addGenerators(){
		synth.add(triangle.wave);
		synth.add(pulse1.wave);
		synth.add(pulse2.wave);
		synth.add(noise.wave);
	}
	public void writeRegister(int index,byte b){
		if(index>=0x4000&&index<0x4004){
			pulse1.registerWrite(index, b);
		}
		else if(index>=0x4004&&index<0x4008){
			pulse2.registerWrite(index, b);
		}
		else if(index>=0x4008&&index<0x400c){
			triangle.registerWrite(index, b);
		}
		else if(index>=0x400c&&index<0x4010){
			noise.registerWrite(index, b);			
		}
		else if(index>=0x4010&&index<0x4014){
			
		}
		else if(index==0x4015){
			status = b;
			//System.out.println("Writing to the control reg"+Integer.toBinaryString(Byte.toUnsignedInt(b)));
			if((b&1)==0){
				pulse1.wave.setEnabled(false);
				pulse1.lengthcount=0;
			}
			else
				pulse1.wave.setEnabled(true);
			if((b&2)==0){
				pulse2.wave.setEnabled(false);
				pulse2.lengthcount=0;
			}
			else
				pulse2.wave.setEnabled(true);
			if((b&4)==0){
				triangle.wave.setEnabled(false);
				triangle.lengthcount=0;
			}
			else
				triangle.wave.setEnabled(true);
			if((b&8)==0){
				noise.wave.setEnabled(false);
				noise.lengthcount=0;
			}
			else
				noise.wave.setEnabled(true);
		}
		else if(index==0x4017){
			stepmode4 = (b&0x80)==0?true:false;
			stepNumber = 0;
			IRQFlag = (b&0x40)==0?false:true;
			if(IRQFlag)
				map.cpu.doIRQ=false;
		}
	}
	public void readRegisters(int index){
		if(index == 0x4015){
			map.cpu.doIRQ=false;
		}
	}
	public void update(){
		pulse1.updateWave();
		pulse2.updateWave();
		triangle.updateWave();
		noise.updateWave();
	}
	public void frameClock(){
		//System.out.println("In framestep mode4:"+stepmode4);
		if(stepmode4){
			//map.cpu.doIRQ=false;
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
			doFrameStep = false;
			stepNumber++;
			if(stepNumber%4==3&&!IRQFlag){
				//System.out.println("DOing an IRQ");
				map.cpu.doIRQ=true;
			}
			
		}
		else{
			if(stepNumber%5==0||stepNumber%5==2){
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
			else if(stepNumber%5==1||stepNumber%5==3){
				pulse1.envelopeClock();
				pulse2.envelopeClock();
				triangle.linearClock();
				noise.envelopeClock();
			}
			else{}
			stepNumber++;
			doFrameStep=false;
		}
		update();
	}
	public void doCycle(int cycle){
		//triangle.linearClock();
		//triangle.lengthClock();
		if(cycle%2==0){
			//pulse1.clockTimer();
			//pulse2.clockTimer();
			noise.clockTimer();
			//noise.updateWave();
		}
		if(doFrameStep){
			frameClock();
		}
		
		
	}

}
