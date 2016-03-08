package no.ntnu.stud.tdt4145.gruppe91;

public class ConnectionTesting {

	final static String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://mysql.stud.ntnu.no/twdahl_tdt4145prj";
	
	final static String USER = "twdahl_tdt4145";
	final static String PASS = "sondrethorbenvilde";
	
	public static void main(String[] args) {
		try {
			Class.forName(JDBC_DRIVER);
			System.out.println("Det funket..?");
		} catch (Exception e) {
			System.err.print(e);
		}
	}

}
