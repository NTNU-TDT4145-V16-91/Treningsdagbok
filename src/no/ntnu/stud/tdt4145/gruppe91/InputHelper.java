package no.ntnu.stud.tdt4145.gruppe91;

import java.io.Closeable;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Implements a command-line utility for easily getting user input.
 * @author Thorben Dahl
 */
public class InputHelper implements Closeable, UiUtility {
	private PrintStream out;
	private Scanner in;
	
	public InputHelper(InputStream input, OutputStream output) {
		in = new Scanner(input);
		out = new PrintStream(output);
	}
	
	public void close() {
		in.close();
	}


	@Override
	public <E> E pickOne(Iterable<E> items) throws UserCancelException {
		return pickOne(items, (x) -> x.toString());
	}
	
	@Override
	public <K, E> E pickOneValue(Map<K, E> items) throws UserCancelException {
		K chosenKey = pickOne(items.keySet(), (x) -> x.toString());
		return items.get(chosenKey);
	}
	
	@Override
	public <K, E> K pickOneKey(Map<K, E> items) throws UserCancelException {
		return pickOne(items.keySet(), (x) -> items.get(x).toString());
	}
	
	@Override
	public <E> int pickOneIndex(List<E> items) throws UserCancelException {
		printOptions(items, (x) -> x.toString());
		return getUserChoice(1, items.size()) - 1;
	}
	
	@Override
	public <E> E pickOne(Iterable<E> items, Function<E, String> mapping) throws UserCancelException {
		List<E> list = createList(items);
		
		printOptions(list, mapping);
		
		return list.get(getUserChoice(1, list.size()) - 1);
		
	}
	
	/**
	 * Create a list from the given iterable.
	 * @param items Iterable which the list will be based on.
	 * @return List with items from the iterable, in the order they were returned from the iterator.
	 */
	private <E> List<E> createList(Iterable<E> items) {
		// Create a list with these items (so that they have a number)
		ArrayList<E> list = new ArrayList<E>();
		for (E element : items) {
			list.add(element);
		}
		return list;
	}
	
	/**
	 * Prints all the options using the provided mapping function.
	 * @param options The options which should be printed.
	 * @param mapping The function that converts options from E to a user-friendly string.
	 */
	private <E> void printOptions(List<E> options, Function<E, String> mapping) {
		for (int index = 0, option = 1; index < options.size(); index++, option++) {
			out.println(option + ": " + mapping.apply(options.get(index)));
		}
	}
	
	@Override
	public int getUserChoice(int min, int max) throws UserCancelException {
		if (max < min) {
			throw new IllegalArgumentException("min cannot be larger than max (min: " + min + ", max: " + max + ")");
		}
		int choice = getUserInt((n) -> {
			if (!(min <= n && n <= max)) {
				throw new IndexOutOfBoundsException("Choice " + n + " is not between " + min + " and " + max);
			}
		}, "[" + min + "-" + max + "]: ");
		return choice;
	}
	
	@Override
	public int getUserIntInterval(int min, int max) throws UserCancelException {
		return getUserChoice(min, max);
	}
	

	@Override
	public <E> E getUserInput(Consumer<String> testRawInput, Function<String, E> converter,
			Consumer<E> testConvertedObject) throws UserCancelException {
		return getUserInput(testRawInput, converter, testConvertedObject, false);
	}
	@Override
	public <E> E getUserInput(Consumer<String> testRawInput, Function<String, E> converter,
			Consumer<E> testConvertedObject, boolean acceptEmpty) throws UserCancelException {
		while (true) {
				// Get from user
				String input = getUserString(testRawInput, acceptEmpty);
				if (input == null) {
					return null;
				}
			try {
				// Convert
				E converted = converter.apply(input);
				// Test converted
				if (testConvertedObject != null) {
					testConvertedObject.accept(converted);
				}
				// All well!
				return converted;
			} catch (Exception e) {
				out.println(e.getMessage());
			}
		}
	}
	
	@Override 
	public <E> E getUserInput(Consumer<String> testRawInput, Function<String, E> converter) throws UserCancelException {
		return getUserInput(testRawInput, converter, (o) -> {}, false);
	}

	@Override
	public <E> E getUserInput(Function<String, E> converter, Consumer<E> testConvertedObject)
			throws UserCancelException {
		return getUserInput((s) -> {}, converter, testConvertedObject, false);
	}

	@Override
	public <E> E getUserInput(Function<String, E> converter) throws UserCancelException {
		// Don't check anything, just convert and don't accept empty string
		return getUserInput((s) -> {}, converter, (o) -> {}, false);
	}

	@Override
	public String getUserString() throws UserCancelException {
		// don't perform any additional checks, and don't accept empty string
		return getUserString((s) -> {}, false);
	}

	@Override
	public String getUserString(Consumer<String> testRawInput) throws UserCancelException {
		// don't accept empty string
		return getUserString(testRawInput, false);
	}
	
	@Override
	public String getUserString(Consumer<String> testRawInput, boolean acceptEmpty) throws UserCancelException {
		String prompt = "> ";
		while (true) {
			// Print prompt
			out.print(prompt);
			// Get input
			String input = in.nextLine().trim();
			// Test if the user intends to cancel
			testIfCancel(input);
			
			try {
				// Check if the input is empty
				if (input.isEmpty()) {
					// It is, should we react or should we accept?
					if (!acceptEmpty) {
						throw new InputMismatchException("Du kan ikke la feltet stå tomt!");
					} else {
						// Return null (so it is easy to identify as being empty)
						// This skips testRawInput
						return null;
					}
				}
				// Test if this is an acceptable string
				if (testRawInput != null) {
					testRawInput.accept(input);
				}
				return input;
			} catch (Exception e) {
				out.println(e.getMessage());
			}
		}
	}

	@Override
	public int getUserInt() throws UserCancelException {
		// Don't do anything in the check
		return getUserInt((x) -> {});
	}
	
	@Override
	public int getUserInt(Consumer<Integer> testInteger) throws UserCancelException {
		return getUserInt(testInteger, "> ");
	}

	@Override
	public int getUserInt(Consumer<Integer> testInteger, String prompt) throws UserCancelException {
		while (true) {
			try {
				// Print prompt
				out.print(prompt);
				// Get input from the user, try to convert to integer
				int input = in.nextInt();
				// Test the resulting integer
				if (testInteger != null) {
					testInteger.accept(input);
				}
				// All's well!
				return input;
			} catch (InputMismatchException e) {
				// The user didn't output an integer, perhaps s/he intends to cancel?
				String token = in.next().trim().toLowerCase();
				testIfCancel(token);
				// Since a UserCancelException isn't thrown at this moment, we know the user just misbehaved
				out.println("Please write a number");
			} catch (Exception e) {
				out.println(e.getMessage());
			} finally {
				// Flush input buffer
				in.nextLine();
			}
		}
	}

	@Override
	public boolean getUserBoolean(String trueText, String falseText) throws UserCancelException {
		// Just present a choice between two alternatives, where one is true and one is false
		// Using list and not map to ensure the true alternative is first
		Boolean[] list = {true, false};
		// Mapping functions uses trueText for the true value, and falseText for the false value.
		return pickOne(Arrays.asList(list), (b) -> b ? trueText : falseText);
	}
	
	/**
	 * Tests if the user intends to cancel, using the given string. It is compared to a wide range of 
	 * trigger words in both English and Norwegian.
	 * 
	 * <p>
	 * The string is trimmed and converted to lower case automatically.
	 * 
	 * <p>
	 * Words that trigger UserCancelException:
	 * <ul>
	 * <li>exit
	 * <li>cancel
	 * <li>avbryt
	 * <li>avslutt
	 * <li>quit
	 * <li>stopp
	 * <li>stop
	 * <li>bye
	 * <li>q
	 * </ul>
	 * @param input Input from the user
	 * @throws UserCancelException if the user intends to cancel input
	 */
	private void testIfCancel(String input) throws UserCancelException {
		Set<String> search = new HashSet<>();
		search.add("exit");
		search.add("cancel");
		search.add("avbryt");
		search.add("avslutt");
		search.add("quit");
		search.add("stopp");
		search.add("stop");
		search.add("bye");
		search.add("q");
		String searchTerm = input.trim().toLowerCase();
		if (search.contains(searchTerm)) {
			throw new UserCancelException(searchTerm);
		}
	}

	@Override
	public void waitForEnter() {
		waitForEnter("--Trykk ENTER for å fortsette--");
	}

	@Override
	public void waitForEnter(String prompt) {
		if (prompt != null) {
			out.print(prompt);
		}
		in.nextLine();
	}


}
