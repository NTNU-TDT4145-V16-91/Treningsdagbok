package no.ntnu.stud.tdt4145.gruppe91;

import java.util.function.Consumer;
import java.util.function.Function;

public interface UiUtility {

	/**
	 * Get and validate input from the user, and convert it to an object.
	 * 
	 * Any exceptions raised in the provided Consumer and Function objects will be treated as user errors and will be displayed to the user.
	 * The user may then try again. 
	 * @param testRawInput Throws an exception if there is an error, does nothing otherwise.
	 * @param converter Converts the user input from String to the specified type.
	 * @param testConvertedObject Tests the object resulting from the conversion and throws an exception if it is invalid.
	 * @return The converted and tested object.
	 * @throws UserCancelException if the user cancels the input.
	 */
	public <E> E getUserInput(Consumer<String> testRawInput, Function<String, E> converter, Consumer<E> testConvertedObject) throws UserCancelException;
	
	/**
	 * Get and validate input from the user, and convert it to an object.
	 * 
	 * Any exceptions raised in the provided Consumer and Function class will be treated as errors caused by the user, and
	 * the exception message will be shown and the user will be able to try again.
	 * @param testRawInput Throws an exception if there is an error in the trimmed input string (before conversion).
	 * @param converter Converts the user input from String to the specified type.
	 * @return The converted object.
	 * @throws UserCancelException if the user cancels the input.
	 */
	public <E> E getUserInput(Consumer<String> testRawInput, Function<String, E> converter) throws UserCancelException;
	
	/**
	 * Get and validate input from the user, and convert it to an object.
	 * 
	 * Any exceptions raised in the provided Consumer and Function classes will be treated as errors caused by the user,
	 * and the exception message will be shown and the user will be able to try again.
	 * @param converter Converts the user input from String to the specified type.
	 * @param testConvertedObject Tests the result of the conversion.
	 * @return The converted object.
	 * @throws UserCancelException if the user cancels the input.
	 */
	public <E> E getUserInput(Function<String, E> converter, Consumer<E> testConvertedObject) throws UserCancelException;
	
	/**
	 * Get input from the user, and convert it to an object.
	 * 
	 * Any exceptions raised in the provided Function class will be treated as errors caused by the user,
	 * and the exception message will be shown and the user will be able to try again.
	 * @param converter Converts the user input from String to the specified type.
	 * @return The converted object.
	 * @throws UserCancelException if the user cancels the input.
	 */
	public <E> E getUserInput(Function<String, E> converter) throws UserCancelException;
	
	/**
	 * Get non-empty string from the user.
	 * @return The trimmed and tested input from the user.
	 * @throws UserCancelException if the user cancels the input.
	 */
	public String getUserString() throws UserCancelException;
	
	/**
	 * Get non-empty string from user, tested using the provided Consumer object.
	 * 
	 * @param testRawInput Tests the trimmed input from the user. Any exceptions raised will be shown to the user, and the user
	 * will be able to retry.
	 * @return The trimmed and tested input from the user.
	 * @throws UserCancelException if the user cancels the input.
	 */
	public String getUserString(Consumer<String> testRawInput) throws UserCancelException;
	
	/**
	 * Get an integer from the user.
	 * @return The integer provided by the user.
	 * @throws UserCancelException if the user cancels the input.
	 */
	public int getUserInt() throws UserCancelException;
	
	/**
	 * Get an integer from the user, and test it.
	 * @param testInteger Tests the provided integer from the user. Any exceptions raised will be shown to the user,
	 * and the user will be able to retry.
	 * @return The integer provided by the user.
	 * @throws UserCancelException if the user cancels the input.
	 */
	public int getUserInt(Consumer<Integer> testInteger) throws UserCancelException;
	
	/**
	 * Get a boolean from the user.
	 * @param trueText Text representing the true value (e.g. "yes" or "OK").
	 * @param falseText Text representing the false value (e.g. "no" or "Cancel").
	 * @return The boolean chosen by the user.
	 * @throws UserCancelException if the user cancels the input.
	 */
	public boolean getUserBoolean(String trueText, String falseText) throws UserCancelException;
}
