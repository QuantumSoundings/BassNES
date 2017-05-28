package core.audio;

public class Triangle extends Channel {
	private static final long serialVersionUID = 4651788745714469245L;
	public Triangle(){}
	public void registerWrite(int index,byte b,int clock){
		switch(index%4){
		case 0: 
			linearReload = b&0b01111111;
			if(clock==14915)
				delayedchange=(b&0x80)!=0?2:1;
			else
				linearcontrol = (b & 0x80) != 0;
			//System.out.println(Integer.toBinaryString(Byte.toUnsignedInt(b)));

			break;
		case 1: 
			
			break;			
		case 2: 
			timer &=0xff00;
			timer |=(b&0xff);
			break;
		case 3: 
			int x = Byte.toUnsignedInt(b)>>3;
			if(enable)
				if(clock==14915){
					if(lengthcount==0){
						lengthcount = lengthlookup[x];
						block=true;
					}
				}
				else lengthcount = lengthlookup[x];
			
			timer&=0b11111111;
			timer |= (b&0b111)<<8;
			linearhalt = true;
		}
	}
	int[] lengthlookup= new int[]{
			10,254, 20,  2, 40,  4, 80,  6, 160,  8, 60, 10, 14, 12, 26, 14,
			12, 16, 24, 18, 48, 20, 96, 22, 192, 24, 72, 26, 16, 28, 32, 30};
	public int[] sequencer = new int[]{
			15, 14, 13, 12, 11, 10,  9,  8,  7,  6,  5,  4,  3,  2,  1,  0,
			 0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15};
	public int sequenceNum;
	int length;
	@Override
	public void lengthClock(){
		if(enable&&!block){
			if(lengthcount!=0){
				if(!linearcontrol)
					lengthcount--;
				//else
				//	lengthcount--;
			}
		}
		if(delayedchange!=0){
			loop = delayedchange == 2;
			delayedchange=0;
		}
		block=false;
	}
	boolean odd;
	@Override
	public final void clockTimer(){
		if(tcount==0){
			if(linearcount==0||lengthcount==0){}
			else
				sequenceNum=(sequenceNum+1)%32;
			tcount=timer;
		}
		else
			tcount--;
		//odd=!odd;
		//if(odd){	
			
			//sequenceNum = 8;
			//return;
			//}	
			total+= sequencer[sequenceNum];
		//}
		
	}
	@Override
	public double getOutput(){
		if(linearcount==0||lengthcount==0)
			return 0;
		else
			return sequencer[sequenceNum];
	}
	private final String name = "Triangle";
	@Override
	public Object[] getInfo(){
		return new Object[]{name,getFrequency()};
	}
	@Override
	public double getFrequency(){
		if(getOutput()==0)
			return 0;
		return 1789773 / (32.0 * (timer + 1));
	}
	@Override
	public String getName(){
		return "Triangle";
	}
	@Override
	public void buildOutput(){
		if(linearcount==0||lengthcount==0)
			return;
		total+= sequencer[sequenceNum];
	}

}
