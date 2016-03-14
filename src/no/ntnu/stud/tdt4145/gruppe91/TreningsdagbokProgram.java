package no.ntnu.stud.tdt4145.gruppe91;
import java.io.PrintStream;
import java.sql.*;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Represents a running instance of the Treningsdagbok program, allowing users to make
 * changes to their training diary.
 * 
 * <p>
 * Before you run this class, you must create no.ntnu.stud.tdt4145.gruppe91.Settings
 * which implements {@link SettingsInterface}. This way, the database login details
 * won't be made public (since Settings.java is present in .gitignore).
 * @author Thorben, Sondre, Vilde
 *
 */
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
	
	/**
	 * Alias for {@link #getUserChoice(int, int)}
	 * @param min Lower bound, inclusive
	 * @param max Upper bound, inclusive
	 * @return The number picked by the user.
	 * @throws UserCancelException if the user cancels the input.
	 * @see #getUserChoice(int, int)
	 */
	public int getUserIntInterval(int min, int max) throws UserCancelException {
		return getUserChoice(min, max);
	}
	
	public void init() throws ClassNotFoundException {
		Class.forName(SETTINGS.getDriver());
	}
	
	public void example_run() throws Exception {
		try (Connection conn = SETTINGS.getConnection()) {
			// Test out picking an option
			try (Statement stmt = conn.createStatement()) {
				
				// Hent øvelser fra databasen
				ResultSet rs = stmt.executeQuery("SELECT navn, id FROM øvelse");
				List<String> exercises = new ArrayList<>();
				while (rs.next()) {
					exercises.add(rs.getString(1));
				}
				
				// La brukeren velge en av dem
				out.println("You picked " + pickOne(exercises) + "!");
				
				// Kjør et ja/nei-spørsmål
				out.println("Are you sure you want to pick it?");
				out.println(getUserBoolean("Yes", "No"));
				
				// Gjør egne sjekker om bruker-input
				out.println("Please write two words separated by space.");
				out.println("You wrote " + getUserString((s) -> {
					if (!s.matches("^\\S+\\s+\\S+$")) {
						throw new IllegalArgumentException("Skriv to navn separert av mellomrom!");
					}
				}, true));
				
				// Konverter fra String til java.util.Date
				// Du kan hoppe over argumenter du ikke trenger, og du trenger heller ikke skrive eksplisitt
				// hvilke typer de forskjellige funksjonene tar inn.
				out.println("Skriv en fremtidig dato");
				Date dato = new Date(this.<java.util.Date>getUserInput(
						// første argument er en funksjon som tester strengen som brukeren skriver inn (etter at den er trimmet)
					(String r) -> {
						// Matcher input et datoformat?
						if (!r.matches("^\\d\\d\\.\\d\\d\\.\\d\\d\\d\\d$")) {
							throw new InputMismatchException("Vennligst skriv en dato på formatet DD.MM.ÅÅÅÅ");
						}
					}, 
						// Andre argument er en funksjon som gjør om fra String til klassen du ønsker
					(String s) -> {
						// Konverter fra string til dato
						DateFormat format = new SimpleDateFormat("dd.MM.yyyy");
						try {
							return format.parse(s);
						} catch (ParseException e) {
							// Vi har ikke lov til å kaste ParseException, kast noe vi har lov til å kaste
							throw new RuntimeException(e);
						}
					}, 
						// Tredje argument er en funksjon som tester objektet etter konverteringen.
					(java.util.Date o) -> {
						// Sjekk om datoen er i fremtiden
						if (!o.after(new java.util.Date())) {
							throw new IllegalArgumentException("Datoen må være i fremtiden!");
						}
					})
				.getTime()); // konverter fra java.util.Date til java.sql.Date ved å bruke Epoch time
				System.out.println("The date you entered will be saved in the DB as " + dato);
				
				System.out.println("Det konkluderer testingen av input-funksjonene.");
			}
			
			
		}
	}
	
	public static void main(String[] args) throws Exception {
		TreningsdagbokProgram program = new TreningsdagbokProgram();
		program.init();
		program.example_run();
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

}
