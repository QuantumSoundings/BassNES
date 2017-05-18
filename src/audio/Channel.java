package audio;


public class Channel implements java.io.Serializable {

	private static final long serialVersionUID = 1054848637029892308L;
	public boolean enable;
	boolean output;
	public int tcount;
	public int timer;
	//Sweep Variables
	boolean dosweep;
	int targetperiod;
	int sdivider;
	int dividerperiod;
	boolean sweepreload=false;
	int shift;
	boolean negate;
		
	//Envelope Variables
	boolean estart;
	boolean constantvolume;
	public int decay;
	int edivider;
	int volume;
	boolean loop;
	
	//Linear Variables
	boolean linearhalt;
	boolean linearcontrol;
	int linearReload;
	public int linearcount;
	
	
	//Length Counter Variables
	public int lengthcount;
	final int[] lengthlookup= new int[]{
			10,254, 20,  2, 40,  4, 80,  6, 160,  8, 60, 10, 14, 12, 26, 14,
			12, 16, 24, 18, 48, 20, 96, 22, 192, 24, 72, 26, 16, 28, 32, 30};
	int delayedchange;
	boolean block;
	
	//Output Variables
	int collectedsamples;
	public int total;
	public Channel(){
	}
	public void write(int i){
		
	}
	public void clockTimer(){
		if(tcount==0)
			tcount=timer;
		else
			tcount--;
	}
	public void lengthClock(){
		if(enable&&!block){
			if(lengthcount!=0){
				if(!loop)
					lengthcount--;
				//else
				//	lengthcount=0;
			}
		}
		if(delayedchange!=0){
			loop = delayedchange == 2;
			delayedchange=0;
		}
		block=false;
	}
	public void disable(){
		enable=false;
		lengthcount=0;
	}
	public void enable(){
		enable = true;
	}
	public double getOutput(){
		return 0;
	}
	public void envelopeClock(){
		if(enable){
			if(estart){
				estart=false;
				decay = 15;
				edivider = volume+1;
			}
			else {
				if(edivider==0){
					edivider=volume+1;
					if(decay==0){
						if(loop)
							decay=15;
	
					}
					else
						decay--;
				}
				edivider--;
			}
			if(constantvolume)
				decay = volume;
		
		}
	}
	/*public void sweepClock(){
		if(dosweep){
			if(sweepreload){
				sdivider = dividerperiod+1;
				targetperiod=timer;
				sweepreload=false;
			}
			else if(sdivider ==0){
				timer = targetperiod;
				//divider--;
			}
			else{
				int change = timer>>shift;
				if(negate)
					targetperiod =  timer - change;
				else
					targetperiod= timer + change;
				sdivider--;
			}
	
		}
	}*/
	public void linearClock(){
		if(enable){
			if(linearhalt){
				linearcount = linearReload;
				output=true;
			}
			else{
				if(linearcount!=0){
					linearcount--;
				}
				else{
					output = false;
				}
			}
			if(!linearcontrol)
				linearhalt=false;
			}
	}
	public void buildOutput(){
		total+=getOutput();
	}
}
