package no.ntnu.stud.tdt4145.gruppe91;
import java.io.PrintStream;
import java.sql.*;
import java.util.ArrayList;
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
		program.example_run();
	}

}
