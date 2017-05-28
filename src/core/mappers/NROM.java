package core.mappers;
//This is the standard mapper
public class NROM extends Mapper{
	private static final long serialVersionUID = 1013800148557615709L;

	public NROM(){
		super();
		System.out.println("MAKING AN NROM");
	}	
	@Override
	void cartridgeWrite(int index,byte b){
	}
	
}
