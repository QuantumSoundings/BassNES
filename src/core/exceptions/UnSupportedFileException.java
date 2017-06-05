package core.exceptions;


public class UnSupportedFileException extends Exception {
	private static final long serialVersionUID = 1L;
	public final String extension;
	public UnSupportedFileException(String ext){
		super("UnSupportedFileExtension: "+ext);
		extension = ext;
	}
}
