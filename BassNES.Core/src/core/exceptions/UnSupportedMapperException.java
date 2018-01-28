package core.exceptions;

public class UnSupportedMapperException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int mapperid;
	public UnSupportedMapperException(int i){
		super("UnSupportedMapper id: "+i);
		mapperid = i;
	}
}
