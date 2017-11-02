package core.mappers;

import java.util.Arrays;

public class UxROM extends Mapper{
	private static final long serialVersionUID = 5199302433584064014L;
	byte[][] PRGbanks;
	public UxROM(){
		super();
		System.out.println("Mapper 2 (UxROM) Fully Supported!");
	}
	@Override
	public void cartridgeWrite(int index, byte b){
		if(index>=0x8000&&index<=0xffff){
			PRG_ROM[0]=PRGbanks[(b&0xf)%PRGbanks.length];
		}
	}
	@Override
	protected void setPRG(byte[] prg){
		PRGbanks = new byte[prg.length/0x4000][0x4000];
		for(int i=0;i*0x4000<prg.length;i++){
			PRGbanks[i]=Arrays.copyOfRange(prg, i*0x4000, (i*0x4000)+0x4000);
		}
		System.out.println("Bank size:" +PRGbanks.length);
		PRG_ROM[0]=PRGbanks[0];
		PRG_ROM[1]=PRGbanks[PRGbanks.length-1];
	}
	@Override
	protected void setCHR(byte[] chr){
		if(chr.length>0){
			for(int i=0;i*0x1000<chr.length;i++){
				CHR_ROM[i]= Arrays.copyOfRange(chr, i*0x1000, (i*0x1000)+0x1000);
			}
		}
		else CHR_ram = true;
	}
	
}
