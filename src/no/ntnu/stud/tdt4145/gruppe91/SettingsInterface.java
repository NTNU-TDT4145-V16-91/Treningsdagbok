package no.ntnu.stud.tdt4145.gruppe91;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public interface SettingsInterface {
	/**
	 * Returns the username you will log into the database as.
	 * @return Username you will use to connect to the database.
	 */
	public String getUser();
	
	/**
	 * Returns the password for this database user.
	 * @return Password for the user you will connect as.
	 */
	public String getPass();
	
	/**
	 * Returns the URI for the host which the DB resides on.
	 * It must not start with protocol, just the host part.
	 * @return Host name of the DB server.
	 */
	public String getServerAddress();
	
	/**
	 * Returns the name of the database which will be used.
	 * @return Name of the database to use.
	 */
	public String getDatabase();
	
	// The following methods are optional to implement
	
	/**
	 * Input to Class.forName, that is, the package path to the Driver.
	 * The default implementation returns the package path for the MySQL driver.
	 * @return Package path to the jdbc driver.
	 */
	default public String getDriver() {
		return "com.mysql.jdbc.Driver";
	}
	
	/**
	 * Returns an URL with the correct protocol, host address and database name.
	 * The default implementation assumes MySQL is used.
	 * @return URL which can be used to connect to the server and the correct database. 
	 */
	default public String getConnectionURL() {
		return "jdbc:mysql://" + this.getServerAddress() + "/" + this.getDatabase();
	}
	
	/**
	 * Returns a new connection to the database, using the contents of this object.
	 * @return New connection, established through DriverManager.getConnection()
	 * @throws SQLException in case of database error.
	 */
	default public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(getConnectionURL(), getUser(), getPass());
	}
}
