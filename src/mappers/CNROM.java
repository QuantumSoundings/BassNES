package mappers;

import java.util.Arrays;

public class CNROM extends Mapper{
	byte[][] CHRbanks;
	public CNROM(){
		super();
		System.out.println("Mapper 3 Fully Supported!");
		PRG_RAM = new byte[0x2000];
	}
	@Override
	public void cartridgeWrite(int i, byte b){
		super.cartridgeWrite(i, b);
		//if(i>=0x6000&&i<0x8000)
		//	PRG_RAM[i-0x6000]=b;
		if(i>=0x8000&&i<=0xffff){
			//System.out.println("Changing chrom to: "+Byte.toUnsignedInt(b));
			CHR_ROM[0] = Arrays.copyOfRange(CHRbanks[b&(CHRbanks.length-1)],0,0x1000);
			CHR_ROM[1] = Arrays.copyOfRange(CHRbanks[b&(CHRbanks.length-1)],0x1000,0x2000);
		}	
	}
	@Override
	public void setPRG(byte[] prg){
		if(prg.length==16384*2){
			PRG_ROM[0]=Arrays.copyOfRange(prg, 0,0x4000);
			PRG_ROM[1]=Arrays.copyOfRange(prg, 0x4000, 0x8000);
		}
		else{
			System.out.println("smole rom");
			PRG_ROM[0]=prg;
			PRG_ROM[1]=prg;
		}

	}
	@Override
	public void setCHR(byte[] CHR){
		CHRbanks = new byte[CHR.length/0x2000][0x2000];
		CHR_ROM = new byte[2][0x1000];
		System.out.println("CHR SIZE"+CHR.length/0x2000);
		if(CHR.length==0){
			CHR_ROM = new byte[2][0x1000];
			CHR_ram = true;
		}
		for(int i=0;i*0x2000<CHR.length;i++){
			CHRbanks[i]=Arrays.copyOfRange(CHR, i*0x2000, (i*0x2000)+0x2000);
		}
		CHR_ROM[0] = Arrays.copyOfRange(CHRbanks[0], 0, 0x1000);
		CHR_ROM[1] = Arrays.copyOfRange(CHRbanks[0], 0x1000, 0x2000);
	}

}
