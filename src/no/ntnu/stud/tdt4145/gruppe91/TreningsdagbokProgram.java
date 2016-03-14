package no.ntnu.stud.tdt4145.gruppe91;
import java.io.PrintStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class TreningsdagbokProgram implements UiUtility {

	// Will not work before you've created Settings.java!
	public final static SettingsInterface SETTINGS = new Settings();
	// Used for reading user input
	private final Scanner in = new Scanner(System.in);
	// Used for printing to screen
	private final PrintStream out = System.out;
	
	/**
	 * Present the user with a choice, and have them pick one.
	 * 
	 * Each object's toString method will be used to create a user-friendly representation.
	 * @param items The options the user must choose from.
	 * @return 
	 * @throws UserCancelException if the user cancels the choice
	 */
	public <E> E pickOne(Iterable<E> items) throws UserCancelException {
		return pickOne(items, (x) -> x.toString());
	}
	
	/**
	 * Make the user pick one of the values in items, based on the keys.
	 * 
	 * Only the keys will be shown to the user, while the value of the chosen key will be returned.
	 * @param items Map in which the key is the user-friendly description and value is what will be returned.
	 * @return The value which matches the key the user chose.
	 * @throws UserCancelException if the user cancels the choice
	 */
	public <K, E> E pickOneValue(Map<K, E> items) throws UserCancelException {
		K chosenKey = pickOne(items.keySet(), (x) -> x.toString());
		return items.get(chosenKey);
	}
	
	/**
	 * Make the user pick one of the keys in items, based on the values.
	 * 
	 * Only the values will be shown to the user, while the key associated with the chosen value will be returned.
	 * @param items Map in which the key is what will be returned and the value is the user-friendly description.
	 * @return The key which matches the value the user chose.
	 * @throws UserCancelException if the user cancels the choice
	 */
	public <K, E> K pickOneKey(Map<K, E> items) throws UserCancelException {
		return pickOne(items.keySet(), (x) -> items.get(x).toString());
	}
	/**
	 * Make the user pick one of the options presented in items, and return its index.
	 * @param items Options the user can pick from, represented by their toString() method.
	 * @return Index of the element the user picked.
	 * @throws UserCancelException if the user cancels the choice
	 */
	public <E> int pickOneIndex(List<E> items) throws UserCancelException {
		printOptions(items, (x) -> x.toString());
		return getUserChoice(1, items.size()) - 1;
	}
	
	/**
	 * Make the user pick one of the options presented in items, and return it.
	 * The user-friendly string is made by the provided mapping function.
	 * @param items Items that the user can choose from.
	 * @param mapping Mapping which returns the user-friendly representation of an item in items.
	 * @return The item chosen by the user.
	 * @throws UserCancelException if the user cancels the choice
	 */
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
	
	/**
	 * Make the user to input a number inside the given interval.
	 * @param min Lower bound, inclusive
	 * @param max Upper bound, inclusive
	 * @return The number the user picked.
	 * @throws UserCancelException if the user types exit to cancel the choice.
	 */
	private int getUserChoice(int min, int max) throws UserCancelException {
		if (max < min) {
			throw new IllegalArgumentException("min cannot be larger than max (min: " + min + ", max: " + max + ")");
		}
		while (true) {
			// Print prompt
			out.print("[" + min + "-" + max + "]: ");
			try {
				int choice = in.nextInt();
				// Is it in the interval?
				if (min <= choice && choice <= max) {
					return choice;
				} else {
					throw new IndexOutOfBoundsException("Choice " + choice + " is not between " + min + " and " + max);
				}
			} catch (InputMismatchException e) {
				String token = in.next().trim().toLowerCase();
				if (token.equals("exit") || token.equals("cancel")) {
					throw new UserCancelException();
				}
				out.println("Please write a number");
			} catch (IndexOutOfBoundsException e) {
				out.println("Please pick a number between " + min + " and " + max);
			} finally {
				// flush rest of the line
				in.nextLine();
			}
		}
	}
	
	public void init() throws ClassNotFoundException {
		Class.forName(SETTINGS.getDriver());
	}
	
	public void run() throws Exception {
		try (Connection conn = SETTINGS.getConnection()) {
			// Test out picking an option
			try (Statement stmt = conn.createStatement()) {
				ResultSet rs = stmt.executeQuery("SELECT navn, id FROM øvelse");
				List<String> exercises = new ArrayList<>();
				while (rs.next()) {
					exercises.add(rs.getString(1));
				}
				out.println("You picked " + pickOne(exercises) + "!");
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		TreningsdagbokProgram program = new TreningsdagbokProgram();
		program.init();
		program.run();
	}

	@Override
	public <E> E getUserInput(Consumer<String> testRawInput, Function<String, E> converter,
			Consumer<E> testConvertedObject) throws UserCancelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <E> E getUserInput(Consumer<String> testRawInput, Function<String, E> converter) throws UserCancelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <E> E getUserInput(Function<String, E> converter, Consumer<E> testConvertedObject)
			throws UserCancelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <E> E getUserInput(Function<String, E> converter) throws UserCancelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUserString() throws UserCancelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUserString(Consumer<String> testRawInput) throws UserCancelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getUserInt() throws UserCancelException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getUserInt(Consumer<Integer> testInteger) throws UserCancelException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean getUserBoolean(String trueText, String falseText) throws UserCancelException {
		// TODO Auto-generated method stub
		return false;
	}

}
