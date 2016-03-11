package no.ntnu.stud.tdt4145.gruppe91;

public interface SettingsInterface {
	public String getPass();
	public String getUser();
	public String getServerAddress();
	public String getDatabase();
	/**
	 * Input to Class.forName, that is, the package path to the Driver
	 * @return
	 */
	public String getDriver();
	default public String getConnectionURL() {
		return "jdbc:mysql://" + this.getServerAddress() + "/" + this.getDatabase();
	}
}
