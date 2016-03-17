package no.ntnu.stud.tdt4145.gruppe91;
import java.io.PrintStream;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Deque;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.text.DateFormat;
import java.text.ParseException;

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
	
	private Calendar cal = Calendar.getInstance();
	private  SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd");
	private static final List<String> VÆR = Arrays.asList("Klart", "Overskyet", "nedbør");
	
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
		ORGANIZE_GROUPS("Legg til, endre eller fjern grupper med øvelser");
		
		private String readableText;
		
		MainChoice(String readableText) {
			this.readableText = readableText;
		}
		
		@Override
		public String toString() {
			return this.readableText;
		}
	};
	
	/**
	 * Runs the main menu for the program.
	 */
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
						try {
							newTrainingSession(conn);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else if (mainChoice == MainChoice.SEE_EXERCISES) {
						showExercises(conn);

					} else if (mainChoice == MainChoice.SEE_TRAINING_SESSIONS) {
						// TODO legg til logikk for å se tidligere treningsøkter og resultater
						out.println("Ikke implementert!");
						in.waitForEnter();
					} else if (mainChoice == MainChoice.SEE_LOG) {
						// TODO legg til logikk for å vise loggene
						out.println("Ikke implementert!");
						in.waitForEnter();
					} else if (mainChoice == MainChoice.ORGANIZE_EXERCISES) {
						organizeExercises(conn);
						// TODO legg til logikk for å se, endre og slette øvelser

					} else if (mainChoice == MainChoice.ORGANIZE_GROUPS) {
						// TODO legg til logikk for å se, endre og slette grupper
						out.println("Ikke implementert!");
						in.waitForEnter();
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
	 * Create a new training session.
	 * @param conn
	 */
	public void newTrainingSession (Connection conn){
	 	try(PreparedStatement pstmt = conn.prepareStatement("INSERT INTO treningsøkt"
				+ "(tidspunkt, varighet, personlig_form,"
				+ " prestasjon, notat, innendørs, luftscore, "
				+ "antall_tilskuere, ute_værtype, ute_temperatur)"
				+ " values(?,?,?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
	 			){
	
			out.println("Tid:");
			Timestamp timestamp = getUserTime();
			
			out.println("Varighet (min): ");
			int varighet = in.getUserInt();
			
			out.println("Notat: \n");
			String notat = in.getUserString();
			out.println("Personlig form: ");
			int persForm = in.getUserChoice(1, 10);
			out.println("Prestasjon:");
			int prestasjon = in.getUserChoice(1, 10);
			
			
			pstmt.setTimestamp(1, timestamp);
			pstmt.setInt(2, varighet);
			pstmt.setInt(3, persForm);
			pstmt.setInt(4, prestasjon);
			pstmt.setString(5, notat);
			
			//Variabler avhengig av innen/utendørs:
			
			out.println("Innendørs? ");
			boolean inne = in.getUserBoolean("ja", "nei");
			
			pstmt.setBoolean(6, inne);

			if (inne){
				//innendørs
				out.println("Luftscore: ");
				int luftscore = in.getUserChoice(1, 10);
				out.println("Antall tilskuere: ");
				int tilskuere = in.getUserInt();
				
				pstmt.setInt(7, luftscore);
				pstmt.setInt(8, tilskuere);
				pstmt.setNull(9, Types.VARCHAR);
				pstmt.setNull(10, Types.TINYINT);
			}
			else{
				//utendørs
				out.println("Velg værtype:");
				String værtype = in.pickOne(VÆR);
				out.println("Temperatur: ");
				int temperatur= in.getUserInt();
				
				pstmt.setString(9, værtype);
				pstmt.setInt(10, temperatur);
				pstmt.setNull(7, Types.TINYINT);
				pstmt.setNull(8, Types.TINYINT);
			}

			pstmt.executeUpdate();
			ResultSet rs = pstmt.getGeneratedKeys();
			rs.next();
			int trening_id = rs.getInt(1); 
				
		 	addExerciseInTraining(conn, trening_id);
		 	
		 	out.println("Treningsøkt lagt til!");
		 	String getExes = "SELECT øvelse.navn FROM øvelse JOIN øvelse_i_trening ON øvelse_id = id WHERE trening_id = ?;";
		 	String fetchString = "SELECT notat, tidspunkt, varighet FROM treningsøkt WHERE id = ?;";
		 	try(PreparedStatement fetchStmt = conn.prepareStatement(fetchString);PreparedStatement exStmt = conn.prepareStatement(getExes)){
		 		exStmt.setInt(1, trening_id);
		 		fetchStmt.setInt(1, trening_id);
		 		ResultSet fetchResult = fetchStmt.executeQuery();
		 		ResultSet exResult = exStmt.executeQuery();
		 		while(fetchResult.next()){
		 			out.println(fetchResult.getString(1));
		 		}
		 		while(exResult.next()){
		 			out.println(exResult.getString(1));
		 		}
		 	}
		 	catch(SQLException e){
		 		e.printStackTrace();
		 	}
		 	in.waitForEnter();
	 	}
	 	catch(UserCancelException e){
	 		// TODO: handle exception
	 		return;
	 	}
	 	catch (SQLException sqlE) {
			// TODO: handle exception
	 		sqlE.printStackTrace();
		}
	 	

		
	}
	/**
	 * Hjelpemetode til newTrainingSession. 
	 * @return innskrevet tidspunkt som Timestamp
	 * @throws UserCancelException ved avbrytelse fra bruker.
	 */
	private Timestamp getUserTime() throws UserCancelException{
		
		List<String> choices = new ArrayList<>();
		choices.add("I dag");
		choices.add("I går");
		choices.add("Skriv inn dato");
		int choice = in.pickOneIndex(choices);
		
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
				//case: skriv inn dato. Kan nok gjøres bedre...
				date = userDate();
				break;
			default:
				break;
		}
		//Tid på dagen:
		out.println("Time: ");
		int hour = in.getUserChoice(0,23);
		String hourStr = hour+"";
		if (hour<10){
			hourStr = "0".concat(hourStr);
		}
		out.println("Minutter: ");
		int min = in.getUserChoice(0, 59);
		String minStr = min+"";
		if (min<10){
			minStr = "0".concat(minStr);
		}
		String clock = " " + hourStr + ":"+minStr+":00";
		date = date.concat(clock);
		return Timestamp.valueOf(date);
	}
	/**
	 * Legger inn relasjon mellom øvelse og trening.
	 * @param conn
	 * @param trening_id Treningsøkten som registreres
	 */
	private void addExerciseInTraining(Connection conn, int trening_id){
		try(PreparedStatement addExStmt = conn.prepareStatement("INSERT INTO øvelse_i_trening "
						+ "(trening_id, øvelse_id, plassering) values(?, ?, ?)", Statement.RETURN_GENERATED_KEYS);){
			Set<Integer> exercises = new HashSet<Integer>();
			navigateExercises(conn, (i) -> 
			{
				try{
					exercises.add(i);
					addExStmt.setInt(1, trening_id);
					addExStmt.setInt(2, i);
					addExStmt.setInt(3, exercises.size());	//plassering
					addExStmt.executeUpdate();
					addResult(conn, trening_id, i);
				}catch(SQLException e){
					out.println("Den øvelsen er allerede del av treningsøkta. Vil du fjerne den?");
					String sqlDelete = "DELETE FROM øvelse_i_trening WHERE øvelse_id = ? AND trening_id = ?";
					try (PreparedStatement deleteStmt = conn.prepareStatement(sqlDelete);){
						if (in.getUserBoolean("Fjern den", "Avbryt")) {
							exercises.remove(i);
							deleteStmt.setInt(1, i);
							deleteStmt.setInt(2, trening_id);
							deleteStmt.executeUpdate();
						}
					} catch (UserCancelException e1) {
						return;
					}catch (SQLException e2){
						e2.printStackTrace();
					}
				}
				String fetchString = "Select øvelse.navn FROM  øvelse JOIN øvelse_i_trening ON øvelse_id = id WHERE trening_id = ?";
				try (PreparedStatement listØvelser  = conn.prepareStatement(fetchString)){
					
					listØvelser.setInt(1, trening_id);
					ResultSet rs = listØvelser.executeQuery();
					out.print("Øvelser denne økten:");
					while(rs.next()){
						out.print(rs.getString(1)+ ", ");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			},
			"Legg til øvelser");
			exercises.clear();
		}
		catch(SQLException e){
			e.printStackTrace();
		}
	}
	/**
	 * Adds result to a training
	 * @param conn
	 * @param trening_id
	 * @param øvelse_id
	 */
	public void addResult(Connection conn, int trening_id, int øvelse_id)throws SQLException{
		try(PreparedStatement resultStmt = conn.prepareStatement("INSERT INTO resultat "
				+ "(trening_id, øvelse_id, belastning, repetisjoner,"
				+ " sett, utholdenhet_distanse, utholdenhet_varighet)"
				+ "values(?,?,?,?,?,?,?)")){
			out.println("Belastning: ");
			int belastning = in.getUserInt();
			out.println("Repetisjoner: ");
			int repetisjoner = in.getUserInt();
			out.println("Sett: ");
			int sett = in.getUserInt();
			
			float distanse = 0;
			while(true){
				out.println("Distanse (utholdenhet):");
				String distStr = in.getUserString();
				try{
					distanse = Float.parseFloat(distStr);
					break;
				}
				catch(NumberFormatException e){
					out.println("Skriv inn et tall på formen '0.0'");
				}
				catch (NullPointerException e) {
					break;
				}
			}
			out.println("Varighet (utholdenhet):");
			int varighet = in.getUserInt();
			
			resultStmt.setInt(1, trening_id);
			resultStmt.setInt(2, øvelse_id);
			resultStmt.setInt(3, belastning);
			resultStmt.setInt(4, repetisjoner);
			resultStmt.setInt(5, sett);
			resultStmt.setFloat(6, distanse);
			resultStmt.setInt(7, varighet);
			
			resultStmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (UserCancelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * Hjelpemetode til newTraingingSession for å få dato som brukerinput
	 * @return dato på formatet deklarert i dateFormat
	 * @throws UserCancelException
	 */
	private String userDate() throws UserCancelException{
		out.println("År: ");
		int year = in.getUserChoice(1900, cal.get(Calendar.YEAR));
		out.println("Måned: ");
		int maxMonth = 12;
		if (year == cal.get(Calendar.YEAR)){
			maxMonth = cal.get(Calendar.MONTH)+1;	//.MONTH er 0-indeksert
		}
		int month = in.getUserChoice(1, maxMonth);
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
			
		int day = in.getUserChoice(1, maxDay);
		cal.set(year, month, day);
		return dateFormat.format(cal.getTime());
	}
	/**
	 * Different actions you may pick when organizing exercises.
	 */
	private enum OrganizeExercisesChoice {
		ADD("Opprett ny"),
		EDIT("Endre"),
		REMOVE("Slett");
		
		String label;
		
		private OrganizeExercisesChoice(String label) {
			this.label = label;
		}
		
		@Override
		public String toString() {
			return this.label;
		}
	}
	
	/**
	 * Handles menu for what the user wants to do with exercises.
	 * @param conn Connection to the database.
	 */
	private void organizeExercises(Connection conn) {
		try {
			while (true) {
				out.println(seperator + "== ORGANISER ØVELSER ==");
				out.println("Skriv EXIT for å gå tilbake til hovedmenyen.");
				OrganizeExercisesChoice choice = in.pickOne(Arrays.asList(OrganizeExercisesChoice.values()));
				switch (choice) {
					case ADD:
						addExercise(conn);
						break;
					case EDIT:
						editExercises(conn);
						break;
					case REMOVE:
						removeExercises(conn);
						break;
					default:
						throw new RuntimeException("Unrecognized choice " + choice);	
				}
			}
		} catch (UserCancelException e) {
			// exit to main menu
			return;	
		}
	}
	
	private void addExercise(Connection conn) {
		String insertQuery = "INSERT INTO øvelse (navn, beskrivelse, repetisjoner, sett, type, "+
						"utholdenhet_default_distanse, utholdenhet_default_varighet, belastning) VALUES ("
						+"?, ?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
			out.println("Navn: ");	String navn = in.getUserString();
			out.println("Lang beskrivelse: "); 	String beskrivelse = in.getUserString(t->{}, true);
			Consumer<Integer> testNotNegative = i->{if (i < 0) throw new RuntimeException("Må være tom eller >= 0");};
			Consumer<String> dummy = o->{};
			out.println("Antall repetisjoner: "); 	Integer repetisjoner = in.getUserInput(dummy, s->Integer.valueOf(s), testNotNegative, true);
			out.println("Antall sett: ");	Integer sett = in.getUserInput(dummy, s->Integer.valueOf(s), testNotNegative, true);
			out.println("Type øvelse: ");	ExerciseType type = in.pickOne(Arrays.asList(ExerciseType.values()), e -> e.getReadable());
			Double utholdenhet_default_distanse = null;
			Integer utholdenhet_default_varighet = null;
			if (type == ExerciseType.ENDURANCE) {
				out.println("Forvalgt distanse: ");
				utholdenhet_default_distanse = in.getUserInput(dummy, s->Double.valueOf(s), d->{if(d<0){throw new RuntimeException("Må være positiv");}}, true);
				out.println("Forvalgt varighet: ");
				utholdenhet_default_varighet = in.getUserInput(dummy, s->Integer.valueOf(s), testNotNegative, true);
			}
			out.println("Belastning: ");	Integer belastning = in.getUserInput(dummy, s->Integer.valueOf(s), testNotNegative, true);
			
			// Sett verdier
			insertStmt.setString(1, navn);
			// jdbc alene suger kuk
			// min argumentasjon:
			try {insertStmt.setString(2, beskrivelse);} catch (NullPointerException e) { insertStmt.setNull(2, Types.LONGVARCHAR);}
			try {insertStmt.setInt(3, repetisjoner);} catch (NullPointerException e) { insertStmt.setNull(3, Types.INTEGER); }
			try {insertStmt.setInt(4, sett);} catch (NullPointerException e) { insertStmt.setNull(4, Types.INTEGER); }
			try {insertStmt.setString(5, type.toString());} catch (NullPointerException e) { insertStmt.setNull(5, Types.VARCHAR); }
			try {insertStmt.setDouble(6, utholdenhet_default_distanse);} catch (NullPointerException e) { insertStmt.setNull(6, Types.DOUBLE);}
			try {insertStmt.setInt(7, utholdenhet_default_varighet);} catch (NullPointerException e) { insertStmt.setNull(7, Types.INTEGER); }
			try {insertStmt.setInt(8, belastning);} catch (NullPointerException e) { insertStmt.setNull(8, Types.INTEGER); }
			// *mic drop*
			
			insertStmt.executeUpdate();
			
			ResultSet keys = insertStmt.getGeneratedKeys();
			keys.next();
			int exerciseId = keys.getInt(1);
			
			out.println("Øvelsen er lagt til!");
			out.println("Vil du legge den til i en gruppe?");
			while (in.getUserBoolean("Ja!", "Nei")) {
				out.println("Velg en gruppe du vil legge øvelsen til i.");
				int gruppeId = pickGroup(conn);
				String addGroupQuery = "INSERT INTO øvelse_i_gruppe (øvelse_id, gruppe_id) VALUES (?, ?)";
				try (PreparedStatement addGroupStmt = conn.prepareStatement(addGroupQuery)) {
					addGroupStmt.setInt(1, exerciseId);
					addGroupStmt.setInt(2, gruppeId);
					addGroupStmt.executeUpdate();
					out.println("Lagt til!");
				} catch (SQLException e) {
					out.println("En feil oppstod. Øvelsen ligger sannsynligvis allerede i den gruppa.");
				}
				out.println("Vil du legge den i en gruppe til?");
			}
			out.println("Ferdig!");
			in.waitForEnter();

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (UserCancelException e) {}
	}
	
	private void editExercises(Connection conn) {
		navigateExercises(conn, i -> editExercise(i, conn), "== ENDRE EN ØVELSE ==");
	}
	
	private enum ExerciseColumn {
		ID("id", "ID"),
		NAME("navn", "Navn"),
		DESCRIPTION("beskrivelse", "Lang beskrivelse"),
		REPETITIONS("repetisjoner", "Antall repetisjoner"),
		SETS("sett", "Antall sett"),
		TYPE("type", "Type øvelse"),
		ENDURANCE_DISTANCE("utholdenhet_default_distanse", "Anbefalt distanse (km)"),
		ENDURANCE_DURATION("utholdenhet_default_varighet", "Anbefalt varighet (min)"),
		CAPACITY("belastning", "Belastning");
		
		private String columnName, humanName;
		
		ExerciseColumn(String columnName, String humanName) {
			this.columnName = columnName;
			this.humanName = humanName;
		}
		
		@Override
		public String toString() {
			return this.columnName;
		}
		
		public String getReadableName() {
			return this.humanName;
		}
	}
	
	private enum ExerciseType {
		UNDEFINED(null, "udefinert"),
		ENDURANCE("utholdenhet"),
		STRENGTH("styrke");
		
		private String value, readable;
		
		ExerciseType(String value) {
			this(value, value);
		}
		
		ExerciseType(String value, String readable) {
			this.value = value;
			this.readable = readable;
		}
		
		@Override
		public String toString() {
			return this.value;
		}
		
		public String getReadable() {
			return this.readable;
		}
		
		public static ExerciseType fromString(String name) {
			for (ExerciseType type : ExerciseType.values()) {
				if ((type.toString() == null && name == null) || 
						(name.equalsIgnoreCase(type.toString()))) {
					return type;
				}
			}
			throw new NoSuchElementException(name);
		}
	}
	
	private void editExercise(int i, Connection conn) {
		// Fetch data about this exercise
		String fetchQuery = "SELECT * FROM øvelse WHERE id = ? LIMIT 1";
		try (PreparedStatement fetchStmt = conn.prepareStatement(fetchQuery, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
			fetchStmt.setInt(1, i);
			ResultSet rs = fetchStmt.executeQuery();
			rs.next();
			
			// Store the values in values, so we can display the new values when changes are made
			Map<ExerciseColumn, String> values = new HashMap<>();
			for (ExerciseColumn column : ExerciseColumn.values()) {
				values.put(column, rs.getString(column.toString()));
			}
			// What should each property look like in the list?
			Function<ExerciseColumn, String> mapping = (e) -> {
				try {
					// Assume it is set
					String str = values.get(e);
					String prefix = "";
					if (!str.equals(rs.getString(e.toString()))) {
						// The value is changed from that of the database
						prefix = "* ";
					}
																// Use only the 50 first characters of the value
					return prefix + e.getReadableName() + ": " + str.substring(0, Math.min(str.length(), 50));
				} catch (SQLException e1) {
					e1.printStackTrace();
					return null;
				} catch (NullPointerException el) {
					// The value was null, so str.equals failed
					// Has this field been changed from what it was? (That is, is it null in the DB?)
					try {
						if (!rs.wasNull()) {
							// Yes, it is changed
							return "* " + e.getReadableName() + ": (tom)";
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					// Not changed
					return e.getReadableName() + ": (tom)";
				}
			};
			
			
			try {
				
				while (true) {
					
					// What column will the user edit?
					out.println(seperator + "== REDIGERER " + rs.getString(ExerciseColumn.NAME.toString()) + " ==");
					out.println("Hvilket felt vil du endre?");
					out.println("Skriv FERDIG for å lagre eller forkaste endringene.");
					
					boolean isEndurance = false;
					try {
						isEndurance = ExerciseType.fromString(values.get(ExerciseColumn.TYPE)) == ExerciseType.ENDURANCE;
					} catch (NullPointerException e) {} // no type was specified
					
					// workaround because lambda functions can only reference static/final variables
					final boolean isEndurance2 = isEndurance;
					// Limit what columns are shown
					List<ExerciseColumn> editableColumns = Arrays.asList(ExerciseColumn.values()).stream()
							// don't include the ID or TYPE column
							.filter(c -> c != ExerciseColumn.ID && c != ExerciseColumn.TYPE)
							// don't include ENDURANCE-columns if this isn't an endurance type
							.filter(c -> isEndurance2 || (c != ExerciseColumn.ENDURANCE_DISTANCE && c != ExerciseColumn.ENDURANCE_DURATION))
							.collect(Collectors.toList());
					
					// Make the user pick one
					ExerciseColumn column = in.pickOne(editableColumns, mapping);
					
					// Make the user input a new value
					try {
						try {
							out.println("Nåværende verdi: " + rs.getString(column.toString()));
						} catch (NullPointerException e) {
							out.println("Nåværende verdi: (tom)");
						}
						out.println("Ny " + column.getReadableName().toLowerCase() + ":");
						String newValue;
						switch (column) {
						
						// String, cannot be empty
						case NAME:
							String newName = in.getUserString();
							rs.updateString(column.toString(), newName);
							newValue = newName;
							break;
						
						// String, can be empty
						case DESCRIPTION:
							String newString = in.getUserString(s -> {}, true);
							if (newString == null) {
								rs.updateNull(column.toString());
							} else {
								rs.updateString(column.toString(), newString);
							}
							newValue = newString;
							break;
						
						// Integer, can be empty
						case REPETITIONS:
						case SETS:
						case ENDURANCE_DURATION:
						case CAPACITY:
							Integer newInteger = in.getUserInput(null, str -> Integer.valueOf(str), value -> {if (value <= 0) throw new IllegalArgumentException("Kun positive tall er tillatt");}, true);
							if (newInteger == null) {
								rs.updateNull(column.toString());
							} else {
								rs.updateInt(column.toString(), newInteger);
							}
							newValue = (newInteger == null) ? null : String.valueOf(newInteger);
							break;
							
						// Double, can be empty
						case ENDURANCE_DISTANCE:
							Double newDouble = in.getUserInput(null, str -> Double.valueOf(str), value -> {if (value <= 0) throw new IllegalArgumentException("Kun positive tall er tillatt");}, true);
							if (newDouble == null) {
								rs.updateNull(column.toString());
							} else {
								rs.updateDouble(column.toString(), newDouble);
							}
							newValue = (newDouble == null) ? null : String.valueOf(newDouble);
							break;
							
						// Special case, can be one of multiple types
						case TYPE:
							ExerciseType newType = in.pickOne(Arrays.asList(ExerciseType.values()), e -> e.getReadable());
							if (newType.toString() == null) {
								rs.updateNull(column.toString());
							} else {
								rs.updateString(column.toString(), newType.toString());
							}
							
							newValue = newType.toString();
							break;
							
						default:
							throw new IllegalStateException("Unrecognized column " + column.toString());
							
						}
						values.replace(column, newValue);
					} catch (UserCancelException e) {

					}
					
				}
			} catch (UserCancelException e) {
				try {
					out.print(seperator);
					out.println("Lagre endringene?");
					if (in.getUserBoolean("Lagre", "Forkast")) {
						rs.updateRow();
						out.println("Endringene er lagret!");
					}
				} catch (UserCancelException f) {
					out.println("Endringene ble forkastet.");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		in.waitForEnter();
	}
	
	private void removeExercises(Connection conn) {
		navigateExercises(conn, e -> removeExercise(conn, e), "== VELG EN ØVELSE DU VIL SLETTE ==");
	}

	private void removeExercise(Connection conn, int id) {
		String removeQuery = "DELETE FROM øvelse WHERE id = ?";
		try (PreparedStatement removeStmt = conn.prepareStatement(removeQuery)) {
			out.println(seperator + "Sletter du denne øvelsen, mister du også alle resultater og mål for denne øvelsen.");
			out.println("ER DU SIKKER PÅ AT DU VIL GJØRE DETTE?");
			if (!in.getUserBoolean("Slett øvelsen og alle tilhørende data", "Avbryt")) {
				return;
			}
			removeStmt.setInt(1, id);
			removeStmt.executeUpdate();
			out.println("Øvelsen er slettet.");
			in.waitForEnter();
		} catch (UserCancelException e) {
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Lets the user browse exercises by group and display their details and current goal.
	 * @param conn Active connection to the database.
	 */
	private void showExercises(Connection conn) {
		// Fetch everything about a given exercise
		String singleExerciseQuery = "SELECT navn, beskrivelse, repetisjoner, sett, type, utholdenhet_default_distanse, "+
				"utholdenhet_default_varighet, belastning FROM øvelse WHERE id = ? LIMIT 1";
		// Fetch a goal for a given exercise
		String goalQuery = "SELECT opprettet_tid, belastning, repetisjoner, sett, utholdenhet_distanse, utholdenhet_varighet"+
				" FROM mål WHERE oppnådd_tid = NULL AND øvelseID = ? LIMIT 1";
		
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
			}, "== SE ØVELSER OG MÅL ==");
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
	private void navigateExercises(Connection conn, Consumer<Integer> exerciseHandler, String heading) {
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
				out.println(seperator + heading);
				out.println("Velg en øvelse eller gruppe, eller skriv FERDIG.");
				// Print "you are here"-string consisting of the path to this group
				out.println(groupPath.stream()
						// Use the human-readable name for this group
						.map(i -> groupNames.get(i))
						// Join the elements, with prefix and delimiter
						.collect(Collectors.joining(" > ", "Du er her: ", "")));
				
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
	
	public int pickGroup(Connection conn) throws UserCancelException, SQLException {
		// Generate list of groups
		String groupsQuery = "SELECT id, navn FROM gruppe ORDER BY navn";
		Map<Integer, String> groups = new HashMap<>();
		try (PreparedStatement groupsStmt = conn.prepareStatement(groupsQuery)) {
			ResultSet rs = groupsStmt.executeQuery();
			while (rs.next()) {
				groups.put(rs.getInt(1), rs.getString(2));
			}
		}
		return in.pickOneKey(groups);
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
