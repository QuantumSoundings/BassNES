package core.audio;

import core.NesSettings;

public class Sunsoft5B extends Channel {
	
	
	private static final long serialVersionUID = 4661386185611176379L;
	int channelATimer;int tempa;boolean channelAEnableE;boolean toneA;int channelAVolume;boolean cAOut;
	int channelBTimer;int tempb;boolean channelBEnableE;boolean toneB;int channelBVolume;boolean cBOut;
	int channelCTimer;int tempc;boolean channelCEnableE;boolean toneC;int channelCVolume;boolean cCOut;
	boolean[] duty = {true,true,true,true,false,false,false,false};
	int[] volumes = {0,1,2,4,6,9,12,15,18,21,25,28,32,36,40,44};
	int cAduty=0;
	int cBduty=0;
	int cCduty=0;
	int dutynumber=0;
	int noisePeriod;
	int envelopePeriod;
	int enVolume = 0;
	boolean eContinue,eAttack,eAlternate,eHold;
	public Sunsoft5B(int location){
		super(0);
		outputLocation=location;
	}
	public void registerWrite(int index,byte b){
		switch(index){
		case 0:
			channelATimer&=0xf00;
			channelATimer|=(0xff&b);break;
		case 1:
			channelATimer&=0xff;
			channelATimer|=(0xf&b)<<8;break;
		case 2:
			channelBTimer&=0xf00;
			channelBTimer|=(0xff&b);break;
		case 3:
			channelBTimer&=0xff;
			channelBTimer|=(0xf&b)<<8;break;
		case 4:
			channelCTimer&=0xf00;
			channelCTimer|=(0xff&b);break;
		case 5:
			channelCTimer&=0xff;
			channelCTimer|=(0xf&b)<<8;break;
		case 6:
			noisePeriod=b&0x1f;break;
		case 7:
			toneA = (b&1)==0;
			toneB = (b&2)==0;
			toneC = (b&4)==0;
			//System.out.println("Writing to noise/tone reg");
			break;
		case 8:
			channelAEnableE = (b&0x10)!=0;
			channelAVolume = (b&0xf);break;
		case 9:
			channelBEnableE = (b&0x10)!=0;
			channelBVolume = (b&0xf);break;
		case 0xa:
			channelCEnableE = (b&0x10)!=0;
			channelCVolume = (b&0xf);break;
		case 0xb:
			envelopePeriod&=0xff00;
			envelopePeriod|=0xff&b;break;
		case 0xc:
			envelopePeriod&=0xff;
			envelopePeriod|= (0xff&b)<<8;break;
		case 0xd:
			eContinue = (b&8)!=0;eAttack= (b&4)!=0;
			eAlternate = (b&2)!=0;eHold = (b&1)!=0;break;
		case 0xe:
		case 0xf:
		}
	}
	int divider;
	@Override
	public final void clockTimer(){
		if(divider==0){
			if(tempa==0){
				tempa = channelATimer;
				cAduty++;
				cAOut = duty[cAduty%8];
			}
			else
				tempa--;
			if(tempb==0){
				tempb = channelBTimer;
				cBduty++;
				cBOut = duty[cBduty%8];
			}
			else
				tempb--;
			if(tempc==0){
				tempc = channelCTimer;
				cCduty++;
				cCOut = duty[cCduty%8];
			}
			else
				tempc--;
		}
		divider = (++divider%4);
			
		AudioMixer.audioLevels[outputLocation] += cAOut&&toneA?(channelAEnableE?enVolume:volumes[channelAVolume]):0;
		AudioMixer.audioLevels[outputLocation] += cBOut&&toneB?(channelBEnableE?enVolume:volumes[channelBVolume]):0;
		AudioMixer.audioLevels[outputLocation] += cCOut&&toneC?(channelCEnableE?enVolume:volumes[channelCVolume]):0;
	}

	public double getChannelMixingRatio() {return .00276;}
	public int getUserPanning(){ return NesSettings.sunsoft5BPanning;}
	public int getUserMixLevel(){return NesSettings.sunsoft5BMixLevel;}
}
