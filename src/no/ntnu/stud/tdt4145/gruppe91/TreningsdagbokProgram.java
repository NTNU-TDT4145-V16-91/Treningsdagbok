package no.ntnu.stud.tdt4145.gruppe91;
import java.io.PrintStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.InputMismatchException;
import java.util.List;
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
				while (true) {
					out.println("\nHva ønsker du å gjøre?");
					out.println("Skriv exit for å avslutte");
					
					// Make the user pick one of the enums
					MainChoice mainChoice = in.pickOne(Arrays.asList(MainChoice.values()));

					if (mainChoice == MainChoice.ADD_TRAINING_SESSION) {
						// TODO legg inn Sondres ting her

					} else if (mainChoice == MainChoice.SEE_EXERCISES) {
						// TODO legg til logikk for å se øvelser og målene deres

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
					in.waitForEnter();
				}
			} catch (UserCancelException e) {
				out.println("Ser deg senere!");
				System.exit(0);
			}
		} catch (SQLException e) {
			out.println("An error occurred: " + e.getMessage());
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
