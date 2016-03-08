package no.ntnu.stud.tdt4145.gruppe91;

public class ConnectionTesting {

	// Will not work before you've created Settings.java!
	public final static SettingsInterface SETTINGS = new Settings();
	
	public static void main(String[] args) {
		try {
			Class.forName(SETTINGS.getDriver());
		} catch (Exception e) {
			System.err.print(e);
		}
	}

}
