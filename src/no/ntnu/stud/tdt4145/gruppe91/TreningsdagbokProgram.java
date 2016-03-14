package no.ntnu.stud.tdt4145.gruppe91;
import java.io.PrintStream;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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
	
	private Calendar cal = Calendar.getInstance();
	private SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd");
	private static final List<String> VÆR = Arrays.asList("Klart", "Overskyet", "nedbør");
	
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
	newTrainingSession();
	/*	try (Connection conn = SETTINGS.getConnection()) {
			// Test out picking an option
			try (Statement stmt = conn.createStatement()) {
				ResultSet rs = stmt.executeQuery("SELECT navn, id FROM Ã¸velse");
				List<String> exercises = new ArrayList<>();
				while (rs.next()) {
					exercises.add(rs.getString(1));
				}
				out.println("You picked " + pickOne(exercises) + "!");
			}
		}*/
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
	
	public void newTrainingSession () throws Exception {
		try(Connection conn = SETTINGS.getConnection()){
			PreparedStatement pstmt = conn.prepareStatement("INSERT INTO treningsøkt"
					+ "(tidspunkt, varighet, personlig_form,"
					+ " prestasjon, notat, innendørs, luftscore "
					+ "antall_tilskuere, ute_værtype, temperatur)"
					+ " values(?,?,?,?,?,?,?,?,?,?)");
			
			out.println("Tid:");
			Timestamp timestamp = getUserTime();
			
			out.println("Varighet (min): ");
			int varighet = getUserInt();
			
			out.println("Notat: \n");
			String notat = getUserString();
			out.println("Personlig form: ");
			int persForm = getUserChoice(1, 10);
			out.println("Prestasjon:");
			int prestasjon = getUserChoice(1, 10);
			
			
			pstmt.setTimestamp(1, timestamp);
			pstmt.setInt(2, varighet);
			pstmt.setInt(3, persForm);
			pstmt.setInt(4, prestasjon);
			pstmt.setString(5, notat);
			
			//Variabler avhengig av innen/utendørs:
			
			out.println("Innendørs (Y/N): ");
			boolean inne = getUserBoolean("y", "n");
			
			pstmt.setBoolean(6, inne);

			if (inne){
				//innendørs
				out.println("Luftscore: ");
				int luftscore = getUserChoice(1, 10);
				out.println("Antall tilskuere: ");
				int tilskuere = getUserInt();
				
				pstmt.setInt(7, luftscore);
				pstmt.setInt(8, tilskuere);
				pstmt.setNull(9, Types.VARCHAR);
				pstmt.setNull(10, Types.TINYINT);
			}
			else{
				//utendørs
				out.println("Velg værtype:");
				String værtype = pickOne(VÆR);
				out.println("Temperatur: ");
				int temperatur= getUserInt();
				
				pstmt.setString(9, værtype);
				pstmt.setInt(10, temperatur);
				pstmt.setNull(7, Types.TINYINT);
				pstmt.setNull(8, Types.TINYINT);
			}

			//Utfører operasjonen:
			//pstmt.executeUpdate();
			
			//Tester:
			out.println("Økt lagt til!\n");
			out.println(pstmt.toString());			
			
		}catch(InputMismatchException ime){
			out.println(ime.getMessage());
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}

	public Timestamp getUserTime() throws UserCancelException{
		
		
		List<String> choices = new ArrayList<>();
		choices.add("I dag");
		choices.add("I går");
		choices.add("Skriv inn dato");
		int choice = pickOneIndex(choices);
		
		
		String date = "";
		switch (choice){
			case 0:
				//Case: today
				date = dateFormat.format(new Date());
				break;
				
			case 1:
				//case: yesterday
				cal.add(Calendar.DATE, -1);
				date =dateFormat.format(cal.getTime());
				break;
			case 2:
				//case: skriv inn dato. Kan nok gjøres mye bedre...
				/*out.println("År: ");
				int year = getUserChoice(1900, cal.get(Calendar.YEAR));
				out.println("Måned: ");
				int month = getUserChoice(1, 12);
				out.println("Dag:");
				int day = getUserChoice(1, 31);
				cal.set(year, month, day);
				date = dateFormat.format(cal.getTime());*/
				date = userDate();
				break;
			default:
				break;
		}
		//Tid på dagen:
		out.println("Time: ");
		int hour = getUserChoice(0,23);
		String hourStr = hour+"";
		if (hour<10){
			hourStr = "0".concat(hourStr);
		}
		out.println("Minutter: ");
		int min = getUserChoice(0, 59);
		String minStr = min+"";
		if (min<10){
			minStr = "0".concat(minStr);
		}
		String clock = " " + hourStr + ":"+minStr+":00";
		date = date.concat(clock);
		return Timestamp.valueOf(date);
	}
	
	//Lar brukeren skrive inn år, måned, dag:
	private String userDate() throws UserCancelException{
		out.println("År: ");
		int year = getUserChoice(1900, cal.get(Calendar.YEAR));
		out.println("Måned: ");
		int maxMonth = 12;
		if (year == cal.get(Calendar.YEAR)){
			maxMonth = cal.get(Calendar.MONTH)+1;	//.MONTH er 0-indeksert
		}
		int month = getUserChoice(1, maxMonth);
		out.println("Dag:");
		int maxDay = 31;
		if (month ==cal.get(Calendar.MONTH)+1 && year == cal.get(Calendar.YEAR)){
			maxDay = cal.get(Calendar.DAY_OF_MONTH);
		}
		else if (month == 4 || month== 6 || month == 9 || month ==11){
			maxDay = 30;
		}
		else if(month == 2){
			if ((year % 4 == 0) && (year % 100 != 0) || (year % 400 == 0)){
				maxDay =29;
			}
			else{
				maxDay = 28;
			}
		}
		else{
			maxDay = 31;
		}
			
		int day = getUserChoice(1, maxDay);
		cal.set(year, month, day);
		return dateFormat.format(cal.getTime());
	}
}
