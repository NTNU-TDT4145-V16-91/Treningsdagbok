package no.ntnu.stud.tdt4145.gruppe91;
import java.sql.*;
import java.util.List;

public class TreningsdagbokProgram {

	// Will not work before you've created Settings.java!
	public final static SettingsInterface SETTINGS = new Settings();
	
	/**
	 * Present the user with a choice, and have them pick one.
	 * @param items
	 * @return
	 */
	public <E> E pickOne(Iterable<E> items) {
		return null;
	}
	
	public static void main(String[] args) throws Exception {
		Class.forName(SETTINGS.getDriver());
		try (Connection conn = SETTINGS.getConnection()) {
			// Do something
		} catch (Exception e) {
			System.err.print(e);
		} 
	}
}
