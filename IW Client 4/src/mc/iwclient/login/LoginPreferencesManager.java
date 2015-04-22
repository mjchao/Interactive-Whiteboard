package mc.iwclient.login;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import mc.iwclient.util.Text;

/**
 * Saves login preferences to make it easier for the user
 * 
 * @author mjchao
 */
public class LoginPreferencesManager {
	
	final public static String PREFERENCES_FILENAME = "preferences.dat";
	
	final public static String PREFERENCE_DELIMITER = " ";
	
	final public static String PREFERENCE_ASSIGNMENT_TAG = "=";
	
	final public static String LOGIN_PREFERENCE_TAG = "login-interface:";
		final public static String USERNAME_TAG = "username";
	
	final public static String CONNECTION_SETTINGS_PREFERENE_TAG = "connection-settings:";
		final public static String SERVER_IP_TAG = "server-ip";
		final public static String PORT_TAG = "port";
	
	public static BufferedReader f;
	public static PrintWriter out;
	
	/**
	 * loads login preference data onto a login user interface
	 *  
	 * @param gui			the login user interface on which to load login preference data
	 */
	final public static void loadPreferences( LoginUI gui ) {
		try {
			f = new BufferedReader( new FileReader( PREFERENCES_FILENAME ) );
			String line = f.readLine();
			while( line != null && !line.equals( "" ) ) {
				gui.processPreferenceData( line );
				line = f.readLine();
			}
			f.close();
		}
		catch ( IOException e ) {
			gui.flashErrorMessage( Text.Login.LOAD_PREFERENCES_FAILED_ERROR );
		}
	}
	
	/**
	 * takes login preference data from a login user interface
	 * and saves it in a file
	 * 
	 * @param gui			the login user interface from which to extract
	 * 						login preference data
	 */
	final public static void savePreferenceData( LoginUI gui ) {
		try {
			out = new PrintWriter( new BufferedWriter( new FileWriter( PREFERENCES_FILENAME ) ) );
			out.print( gui.getLoginInterfacePreferences() );
			out.print( gui.getConnectionSettingsPreferences() );
			out.println();
			out.close();
		} 
		catch ( IOException e ) {
			gui.flashErrorMessage( Text.Login.SAVE_PREFERENCES_FAILED_ERROR );
		}
	}
}
