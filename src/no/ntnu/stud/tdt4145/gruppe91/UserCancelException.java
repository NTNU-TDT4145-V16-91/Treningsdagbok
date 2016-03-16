package no.ntnu.stud.tdt4145.gruppe91;

/**
 * Thrown when a user decides to cancel an input dialog, by typing "exit", "bye" or similar phrases.
 * @author Thorben Dahl
 */
public class UserCancelException extends Exception {
	private static final long serialVersionUID = -8797868546157087113L;

	/**
	 * @see java.lang.Exception#Exception(String)
	 */
	
	public UserCancelException(String message) {
		super(message);
	}
}
