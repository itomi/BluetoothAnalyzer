package pl.itomi.bluetooth.gui;

public class UnsupportedOptionException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UnsupportedOptionException(String optionNotSupported) {
		super(optionNotSupported);
	}
}
