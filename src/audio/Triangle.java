package audio;

import com.jsyn.unitgen.TriangleOscillator;

public class Triangle extends Channel {
	public TriangleOscillator wave;
	public Triangle(TriangleOscillator w){
		super(w);
		wave = w;
		
	}
	public void registerWrite(int index,byte b){
		switch(index%4){
		case 0: 
			linearReload = b&0b01111111;
			linearcontrol = (b&0x80)!=0?true:false;
			
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
			lengthcount = lengthlookup[x];
			timer&=0b11111111;
			timer |= (b&0b111)<<8;
			linearhalt = true;
		}
		//System.out.println("Writing to pulse channel");
	}
	int[] lengthlookup= new int[]{
			10,254, 20,  2, 40,  4, 80,  6, 160,  8, 60, 10, 14, 12, 26, 14,
			12, 16, 24, 18, 48, 20, 96, 22, 192, 24, 72, 26, 16, 28, 32, 30};
	int length;
	@Override
	public void lengthClock(){
		if(lengthcount!=0){
			if(linearcontrol)
				lengthcount=1;
			else
				lengthcount--;
		}
	}
	public double frequency(){
		return 1789773/(32.0*(timer+1));
	}
	void debug(){
		System.out.println("Updating wave with frequency: "+frequency()
		+" Length: "+lengthcount
		+" timer: "+timer
		+" linearcount: "+linearcount
		+" lControl: "+linearcontrol);
	}
	public void updateWave(){
		//debug();
		if(linearcount==0||lengthcount==0)
			wave.setEnabled(false);
		else{
			if(!wave.isEnabled())
				wave.setEnabled(true);
			wave.amplitude.set(.35);
		}
		wave.frequency.set(frequency());
		
	}
}
