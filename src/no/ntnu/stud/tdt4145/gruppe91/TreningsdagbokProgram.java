package no.ntnu.stud.tdt4145.gruppe91;
import java.io.PrintStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.stream.Collectors;
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
public class TreningsdagbokProgram {

	// Will not work before you've created Settings.java!
	public final static SettingsInterface SETTINGS = new Settings();
	// Used for reading user input
	private final UiUtility in = new InputHelper(System.in, System.out);
	// Used for printing to screen
	private final PrintStream out = System.out;
	// Used for formatting dates
	private final DateFormat df = DateFormat.getDateInstance();
	// Used for separating different dialogs. This creates a string that just repeats line-separator n times.
	private static final String seperator = String.join("", Collections.nCopies(6, System.lineSeparator())); 
	
	public void init() throws ClassNotFoundException {
		Class.forName(SETTINGS.getDriver());
	}
	
	/**
	 * Enum representing the different choices the user can do at the root level.
	 * @author Thorben Dahl
	 *
	 */
	private enum MainChoice {
		ADD_TRAINING_SESSION("Ny treningsøkt"),
		SEE_EXERCISES("Se øvelser og mål"),
		SEE_TRAINING_SESSIONS("Se treningsøkter og resultater"),
		SEE_LOG("Se treningslogg"),
		ORGANIZE_EXERCISES("Legg til, endre eller fjern øvelser"),
		ORGANIZE_GROUPS("Legg til, endre eller fjern grupper med øvelser"),
		RUN_EXAMPLE_PROGRAM("Kjør demonstrasjon av InputHelper");
		
		private String readableText;
		
		MainChoice(String readableText) {
			this.readableText = readableText;
		}
		
		@Override
		public String toString() {
			return this.readableText;
		}
	};
	
	public void run() {
		try (Connection conn = SETTINGS.getConnection()) {
			try {
				out.println("Velkommen til Treningsdagbok3000!");
				out.println();
				while (true) {
					out.println("== HOVEDMENY ==");
					out.println("Skriv EXIT for å avslutte");
					
					// Make the user pick one of the enums
					MainChoice mainChoice = in.pickOne(Arrays.asList(MainChoice.values()));

					if (mainChoice == MainChoice.ADD_TRAINING_SESSION) {
						// TODO legg inn Sondres ting her

					} else if (mainChoice == MainChoice.SEE_EXERCISES) {
						showExercises(conn);

					} else if (mainChoice == MainChoice.SEE_TRAINING_SESSIONS) {
						// TODO legg til logikk for å se tidligere treningsøkter og resultater

					} else if (mainChoice == MainChoice.SEE_LOG) {
						// TODO legg til logikk for å vise loggene

					} else if (mainChoice == MainChoice.ORGANIZE_EXERCISES) {
						// TODO legg til logikk for å se, endre og slette øvelser

					} else if (mainChoice == MainChoice.ORGANIZE_GROUPS) {
						// TODO legg til logikk for å se, endre og slette grupper

					} else if (mainChoice == MainChoice.RUN_EXAMPLE_PROGRAM) {
						try {
							example_run();
						} catch (Exception e) {
							out.println("An error occurred:");
							e.printStackTrace();
						}
					} else {
						throw new RuntimeException("Choice " + mainChoice + " was not recognized");
					}
					out.print(seperator);
				}
			} catch (UserCancelException e) {
				out.println("Ser deg senere!");
				System.exit(0);
			}
		} catch (SQLException e) {
			out.println("An error occurred: " + e.getMessage());
		}
	}
	
	/**
	 * Lets the user browse exercises by group and display their details and current goal.
	 * @param conn Active connection to the database.
	 */
	private void showExercises(Connection conn) {
		// Fetch everything about a given exercise
		String singleExerciseQuery = "SELECT navn, beskrivelse, repetisjoner, sett, type, utholdenhet_default_distanse, "+
				"utholdenhet_default_varighet, belastning FROM øvelse WHERE id = ?";
		// Fetch a goal for a given exercise
		String goalQuery = "SELECT opprettet_tid, belastning, repetisjoner, sett, utholdenhet_distanse, utholdenhet_varighet"+
				" FROM mål WHERE oppnådd_tid = NULL AND øvelseID = ?";
		
		try (PreparedStatement singleExerciseStmt = conn.prepareStatement(singleExerciseQuery);
			PreparedStatement goalStmt = conn.prepareStatement(goalQuery)) {
			// When an exercise is chosen, display that exercise
			navigateExercises(conn, (i) -> {
				try {
					// Fetch that exercise and eventual goal
					singleExerciseStmt.setInt(1, i);
					goalStmt.setInt(1, i);
					ResultSet exercise = singleExerciseStmt.executeQuery();
					ResultSet goal = goalStmt.executeQuery();
					exercise.next();
					
					// Print details about the exercise
					out.println(seperator + exercise.getString("navn"));
					out.println("========================================");
					out.println();
					out.println("Repetisjoner: " + exercise.getString("repetisjoner") + "\nAntall sett: " + exercise.getString("sett")+
							"\nBelastning: " + exercise.getString("belastning"));
					try {
						if (exercise.getString("type").equals("utholdenhet")) {
	
							out.println("Utholdenhetsøvelse med anbefalt distanse på " + exercise.getString("utholdenhet_default_distanse") + 
									" og varighet på " + exercise.getString("utholdenhet_default_varighet"));
						}
					} catch (NullPointerException e) {} // type was null
					
					in.waitForEnterOrCancel(); // wait before printing description
					out.println("\n" + exercise.getString("beskrivelse"));
					
					// If there is a goal associated with this exercise
					if (goal.next()) {
						// let the user cancel
						in.waitForEnterOrCancel();
						// and display that goal
						out.println("Du har dette uoppnådde målet fra " + df.format(goal.getDate("opprettet_tid")) + ":");
						out.println("Repetisjoner: " + goal.getString("repetisjoner") + "; Antall sett: " + goal.getString("sett") + 
								"; Belastning: " + goal.getString("belastning"));
						try {
							if (exercise.getString("type").equals("utholdenhet")) {
								out.println("Distanse: " + goal.getString("utholdenhet_distanse") + "; Varighet: " + goal.getString("utholdenhet_varighet"));
							}
						} catch (NullPointerException e) {} // type is still null
					}
					in.waitForEnter();
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (UserCancelException e) {
					return;
				}
			});
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Do something when the user chooses an exercise.
	 * <p>
	 * This method allows the user to navigate inside groups, much like a file explorer. Whenever the user picks
	 * an exercise, the provided Consumer will be called. This can be used to fetch and display information
	 * about that exercise, add it to a list, and so on. Afterwards, the user may continue
	 * to browse the exercises. The method will return only when the user
	 * writes "exit" to cancel.
	 * @param conn Active connection to the database.
	 * @param exerciseHandler Takes in the id of the exercise chosen by the user, and does something with it.
	 */
	private void navigateExercises(Connection conn, Consumer<Integer> exerciseHandler) {
		// The statements needed for this part of the program
		// Fetch groups with no supergroup
		String rootGroupsQuery = "SELECT id, navn FROM gruppe WHERE id NOT IN (SELECT subgruppe_id FROM undergruppe)";
		// Fetch groups with a given supergroup
		String subGroupsQuery = "SELECT gruppe.id, gruppe.navn FROM gruppe JOIN undergruppe ON gruppe.id = undergruppe.subgruppe_id "+
					"WHERE undergruppe.supergruppe_id = ?";
		// Common part for both exercise queries
		String exerciseQueryCommon = "SELECT øvelse.id, øvelse.navn FROM øvelse "+
				"LEFT JOIN øvelse_i_gruppe ON øvelse.id = øvelse_i_gruppe.øvelse_id "+
			"WHERE øvelse_i_gruppe.gruppe_id ";
		// Special part for fetching exercises in no group
		String rootExercisesQuery = exerciseQueryCommon + "IS NULL";
		// Special part for fetching exercises with a given group
		String groupExercisesQuery = exerciseQueryCommon + "= ?";
		
		
		// Remember the id of the groups we've been to (think Windows Explorer)
		Deque<Integer> groupPath = new LinkedList<>();
		// Remember what name goes with what id
		Map<Integer, String> groupNames = new HashMap<>();
		// Name of root category (identified as 0)
		groupNames.put(0, "Start");
		// Add root category to the path
		groupPath.add(0);
		// The current directory is always the last group in the path
		Integer currentGroup = groupPath.peekLast();
		
		// In the rest of this method, we will have these statements ready to run.
		// Save time by reusing the same prepared statement every time we ask the same query
		try (PreparedStatement rootGroupsStmt = conn.prepareStatement(rootGroupsQuery);
				PreparedStatement subGroupsStmt = conn.prepareStatement(subGroupsQuery);
				PreparedStatement rootExercisesStmt = conn.prepareStatement(rootExercisesQuery);
				PreparedStatement groupExercisesStmt = conn.prepareStatement(groupExercisesQuery);
				) {

			while (true) {
				out.println(seperator + "=== VELG EN ØVELSE ELLER GRUPPE, eller skriv FERDIG ===");
				// Print "you are here"-string consisting of the path to this group
				out.println(groupPath.stream()
						// Use the human-readable name for this group
						.map(i -> groupNames.get(i))
						// Join the elements, with prefix and delimiter
						.collect(Collectors.joining(" → ", "Du er her: ", "")));
				
				// Create and present options for the user.
				// These options include subgroups and exercises.
				
				// In this list, the unique IDs are stored in order (since Map has no guarantee when it comes to order).
				// The ids represent different choices.
				List<Integer> entriesOrdered = new ArrayList<>();
				// Remember the human-readable string for every id
				Map<Integer, String> entries = new HashMap<>();
				
				// Fetch subgroups
				ResultSet subGroupRows;
				if (currentGroup == 0) {
					// Fetch
					subGroupRows = rootGroupsStmt.executeQuery();
				} else {
					// We're not on the top-most level; add option to navigate one layer up
					entries.put(0, "Tilbake");
					entriesOrdered.add(0);
					// Set which group we want those subgroups to be subgroups of
					subGroupsStmt.setInt(1, currentGroup);
					// Fetch
					subGroupRows = subGroupsStmt.executeQuery();
				}
				// Iterate through the subgroups and add them as options for the user
				while (subGroupRows.next()) {
					int groupId = subGroupRows.getInt(1);
					String groupName = subGroupRows.getString(2);
					// multiply id with -1 so we can distinguish group ids from exercise ids
					entries.put(groupId * -1, "Gruppe: " + groupName);
					entriesOrdered.add(groupId * -1);
					// remember group name, so we can print it as part of the path
					if (!groupNames.containsKey(groupId)) {
						groupNames.put(groupId, groupName);
					}
				}
				
				// Fetch exercises
				ResultSet exerciseRows;
				// Are we at the top-most level?
				if (currentGroup == 0) {
					// Fetch exercises with no group
					exerciseRows = rootExercisesStmt.executeQuery();
				} else {
					// Fetch exercises in this group
					groupExercisesStmt.setInt(1, currentGroup);
					exerciseRows = groupExercisesStmt.executeQuery();
				}
				// Iterate through the exercises and add them as options for the user
				while (exerciseRows.next()) {
					// Storing id as it is, so it is not confused with group ids
					entries.put(exerciseRows.getInt(1), "Øvelse: " + exerciseRows.getString(2));
					entriesOrdered.add(exerciseRows.getInt(1));
				}
				
				// It's time to let the user pick
				int user_choice;
				try {
					// get the id, but use the entries map to get human-readable name for each option
					user_choice = in.pickOne(entriesOrdered, (e) -> entries.get(e));
				} catch (UserCancelException e) {
					return; // exit out to main menu
				}
				
				// If a group was chosen (remember that the group ids were multiplied by -1, so they are all negative)
				if (user_choice < 0) {
					// Set that group as the current group					
					currentGroup = user_choice * -1;
					// Add it to the path
					groupPath.addLast(currentGroup);
				} else 
					// Was an exercise was chosen?
					if (user_choice > 0){
						exerciseHandler.accept(user_choice);
				} else {
					// Navigate one level up by removing the current group from the groupPath
					groupPath.removeLast();
					// The last group in the path is the current group
					currentGroup = groupPath.peekLast();
				}
			}			
		} catch (SQLException e) {
			out.println("En feil har oppstått");
			e.printStackTrace();
		}
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
				out.println("You picked " + in.pickOne(exercises) + "!");
				
				// Kjør et ja/nei-spørsmål
				out.println("Are you sure you want to pick it?");
				out.println(in.getUserBoolean("Yes", "No"));
				
				// Gjør egne sjekker om bruker-input
				out.println("Please write two words separated by space.");
				out.println("You wrote " + in.getUserString((s) -> {
					if (!s.matches("^\\S+\\s+\\S+$")) {
						throw new IllegalArgumentException("Skriv to navn separert av mellomrom!");
					}
				}, true));
				
				// Konverter fra String til java.util.Date
				// Du kan hoppe over argumenter du ikke trenger, og du trenger heller ikke skrive eksplisitt
				// hvilke typer de forskjellige funksjonene tar inn eller hvilken type getUserInput bruker.
				out.println("Skriv en fremtidig dato");
				Date dato = new Date(in.<java.util.Date>getUserInput(
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
		program.run();
	}

}
