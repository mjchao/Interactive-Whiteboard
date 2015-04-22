package mc.iwclient.util;

import javax.swing.JOptionPane;

final public class Dialogs {
	
	final public static int YES_OPTION = JOptionPane.YES_OPTION;
	final public static int NO_OPTION = JOptionPane.NO_OPTION;
	
	/**
	 * asks the user to confirm to some message and returns the user's confirm
	 * response
	 * 
	 * @param message			the message for the user
	 * @return					the user's response, represented by an int
	 */
	final public static int displayConfirmMessage( String message ) {
		return JOptionPane.showConfirmDialog( null , message , Text.Dialogs.CONFIRM_DIALOG_TITLE , JOptionPane.YES_NO_OPTION );
	}
	
	final public static void displayErrorMessage( String message ) {
		JOptionPane.showMessageDialog( null , message , Text.Dialogs.ERROR_DIALOG_TITLE , JOptionPane.OK_OPTION );
	}

}
