package core.mappers;


public class Mapper_13 extends Mapper {
	private static final long serialVersionUID = -8418650826653647062L;
	public Mapper_13(){
		super();
		System.out.println("Making a mapper 13");
	}
	
	@Override
	public void setCHR(byte[] chr){
		CHRbanks = new byte[4][0x1000];
		CHR_ROM[0] = CHRbanks[0];
		CHR_ROM[1] = CHRbanks[1];
		CHR_ram = true;
	}
	@Override
	public void cartridgeWrite(int index, byte b){
		if(index>=0x8000&&index<=0xffff){
			CHR_ROM[1] = CHRbanks[b&3];
		}	
	}

}
