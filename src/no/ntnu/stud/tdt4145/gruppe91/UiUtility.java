package no.ntnu.stud.tdt4145.gruppe91;

import java.util.function.Consumer;
import java.util.function.Function;

public interface UiUtility {
	
	/**
	 * Checks user input, converts it, checks the converted object and possibly accepts empty input.
	 * <p>
	 * Any exceptions raised in the provided Consumer and Function objects will be treated as user errors and will be displayed to the user.
	 * The user may then try again. The provided functions will not run if the user input is empty.
	 * @param testRawInput Throws an exception if there is an error in the trimmed, non-empty user input. Does nothing otherwise.
	 * @param converter Converts the user input from a trimmed, non-empty String to the specified type.
	 * @param testConvertedObject Tests the object resulting from the conversion and throws an exception if it is invalid.
	 * @param acceptEmpty Set to true to accept empty user input, which will be converted to null and returned.
	 * @return The converted and tested object, or null if acceptEmpty is set to true and the input was empty.
	 * @throws UserCancelException if the user cancels the input.
	 */
	public <E> E getUserInput(Consumer<String> testRawInput, Function<String, E> converter, Consumer<E> testConvertedObject,
			boolean acceptEmpty) throws UserCancelException;

	/**
	 * Checks user input, converts it and checks the converted object.
	 * <p>
	 * Any exceptions raised in the provided Consumer and Function objects will be treated as user errors and will be displayed to the user.
	 * The user may then try again. The provided functions will not run if the user input is empty.
	 * @param testRawInput Throws an exception if there is an error in the trimmed, non-empty user input. Does nothing otherwise.
	 * @param converter Converts the user input from a trimmed, non-empty String to the specified type.
	 * @param testConvertedObject Tests the object resulting from the conversion and throws an exception if it is invalid.
	 * @return The converted and tested object.
	 * @throws UserCancelException if the user cancels the input.
	 */
	public <E> E getUserInput(Consumer<String> testRawInput, Function<String, E> converter, Consumer<E> testConvertedObject) throws UserCancelException;
	
	/**
	 * Checks user input and converts it.
	 * <p>
	 * Any exceptions raised in the provided Consumer and Function class will be treated as errors caused by the user, and
	 * the exception message will be shown and the user will be able to try again.
	 * @param testRawInput Throws an exception if there is an error in the trimmed input string (before conversion).
	 * @param converter Converts the user input from a trimmed, non-empty String to the specified type.
	 * @return The converted object.
	 * @throws UserCancelException if the user cancels the input.
	 */
	public <E> E getUserInput(Consumer<String> testRawInput, Function<String, E> converter) throws UserCancelException;
	
	/**
	 * Converts the user input and checks the converted object.
	 * <p>
	 * Any exceptions raised in the provided Consumer and Function classes will be treated as errors caused by the user,
	 * and the exception message will be shown and the user will be able to try again.
	 * @param converter Converts the user input from a trimmed, non-empty String to the specified type.
	 * @param testConvertedObject Tests the result of the conversion.
	 * @return The converted object.
	 * @throws UserCancelException if the user cancels the input.
	 */
	public <E> E getUserInput(Function<String, E> converter, Consumer<E> testConvertedObject) throws UserCancelException;
	
	/**
	 * Convert the user input.
	 * <p>
	 * Any exceptions raised in the provided Function class will be treated as errors caused by the user,
	 * and the exception message will be shown and the user will be able to try again.
	 * @param converter Converts the user input from a trimmed, non-empty String to the specified type.
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
	 * <p>
	 * @param testRawInput Tests the trimmed, non-empty input from the user. Any exceptions raised will be shown to the user, and the user
	 * will be able to retry.
	 * @return The trimmed, non-empty and tested input from the user.
	 * @throws UserCancelException if the user cancels the input.
	 */
	public String getUserString(Consumer<String> testRawInput) throws UserCancelException;
	
	/**
	 * Get a possibly empty string from the user, tested using the provided Consumer object.
	 * @param testRawInput Tests the trimmed, non-empty input from the user. Any exceptions raised will be shown to the user, and the user
	 * will be able to retry. This test is skipped if the input is empty.
	 * @param acceptEmpty Set to true to allow empty input. Empty input will be converted to null.
	 * @return The trimmed and tested user input if it's not empty, otherwise null if acceptEmpty is set to true.
	 * @throws UserCancelException if the user cancels the input.
	 */
	public String getUserString(Consumer<String> testRawInput, boolean acceptEmpty) throws UserCancelException;
	
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
	 * Get an integer from the user using the given prompt, and test it.
	 * @param testInteger Tests the provided integer from the user. Any exceptions raised will be shown to the user,
	 * and the user will be able to retry.
	 * @param prompt String which will be shown right before the user's input.
	 * @return The integer provided by the user.
	 * @throws UserCancelException if the user cancels the input.
	 */
	public int getUserInt(Consumer<Integer> testInteger, String prompt) throws UserCancelException;
	
	/**
	 * Get a boolean from the user.
	 * @param trueText Text representing the true value (e.g. "yes" or "OK").
	 * @param falseText Text representing the false value (e.g. "no" or "Cancel").
	 * @return The boolean chosen by the user.
	 * @throws UserCancelException if the user cancels the input.
	 */
	public boolean getUserBoolean(String trueText, String falseText) throws UserCancelException;

	
}
