package no.ntnu.stud.tdt4145.gruppe91;
import java.sql.*;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import javax.swing.tree.VariableHeightLayoutCache;

import sun.rmi.runtime.NewThreadAction;

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
		newTrainingSession();
/*		try (Connection conn = SETTINGS.getConnection()) {
			// Do something
		} catch (Exception e) {
			System.err.print(e);
		}*/ 
	}
	
	public static void newTrainingSession () throws Exception {
		try(Connection conn = SETTINGS.getConnection()){
			PreparedStatement pstmt = conn.prepareStatement("INSERT INTO treningsøkt"
					+ "(tidspunkt, varighet, personlig_form,"
					+ " prestasjon, notat, innendørs, luftscore "
					+ "antall_tilskuere, ute_værtype, temperatur)"
					+ " values(?,?,?,?,?,?,?,?,?,?)");
			
			
			//Input from user:
			Scanner reader = new Scanner(System.in);
			System.out.println("Varighet (min): ");
			int varighet = reader.nextInt();
			System.out.println("Notat: \n");
			String notat = reader.next();
			notat+=reader.nextLine();
			System.out.println("Personlig form (1-10): ");
			int persForm = reader.nextInt();
			System.out.println("Prestasjon (1-10):");
			int prestasjon = reader.nextInt();
			
			//Variabler avhengig av innen/utendørs:
			int luftscore =0;
			int tilskuere=0;	
			int værtype=0;
			int temperatur=0;
			System.out.println("Innendørs (Y/N): ");
			boolean inne = (reader.next().toLowerCase().equals("y"))? true:false;
			if (inne){
				//innendørs
				System.out.println("Luftscore (1-10): ");
				luftscore = reader.nextInt();
				System.out.println("Antall tilskuere: ");
				tilskuere = reader.nextInt();
				
				pstmt.setInt(7, luftscore);
				pstmt.setInt(8, tilskuere);
				pstmt.setNull(9, Types.VARCHAR);
				pstmt.setNull(10, Types.TINYINT);
			}
			else{
				//utendørs
				System.out.println("Værtype: 1 = klart, 2 = overskyet, 3 = nedbør ");
				værtype = reader.nextInt();
				System.out.println("Temperatur: ");
				temperatur= reader.nextInt();
				
				pstmt.setString(9, getVærtype(værtype));
				pstmt.setInt(10, temperatur);
				pstmt.setNull(7, Types.TINYINT);
				pstmt.setNull(8, Types.TINYINT);
			}
			
			pstmt.setTimestamp(1, null);//Legg til timestamp her!
			pstmt.setInt(2, varighet);
			pstmt.setInt(3, persForm);
			pstmt.setInt(4, prestasjon);
			pstmt.setString(5, notat);
			pstmt.setBoolean(6, inne);

			//Utfører operasjonen:
			//pstmt.executeUpdate();
			//Tester:
			System.out.println("Økt lagt til!\n");
			System.out.println(pstmt.toString());
			
			
		}catch(InputMismatchException ime){
			System.out.println(ime.getMessage());
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	private static String getVærtype(int index){
		switch(index){
		case 1:
			return "Klart";
		case 2:
			return "Overskyet";
		case 3:
			return "Nedbør";
		default:
			return "";
		}
	}
}
