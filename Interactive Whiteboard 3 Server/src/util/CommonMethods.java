package util;

import javax.swing.JOptionPane;

import util.log.ConnectionLog;
import util.log.InternalLog;
import util.log.Log;
import util.log.SuspiciousLog;


final public class CommonMethods
{
	final public static void displaySuccessMessage(String successMessage)
	{
		JOptionPane.showMessageDialog(null, successMessage, "Success", JOptionPane.WARNING_MESSAGE);
	}
	
	final public static void displayInformationMessage(String informationMessage)
	{
		JOptionPane.showMessageDialog(null, informationMessage, "Information", JOptionPane.INFORMATION_MESSAGE);
	}
	
	final public static void displayErrorMessage(String errorMessage)
	{
		JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	final public static String requestInputMessage(String inputMessage)
	{
		String rtn = JOptionPane.showInputDialog(inputMessage);
		return rtn;
	}
	
	final public static int CONFIRM_YES_RESPONSE = JOptionPane.YES_OPTION;
	final public static int CONFIRM_NO_RESPONSE = JOptionPane.NO_OPTION;
	final public static int displayConfirmDialog(String inputMessage)
	{
		int rtn = JOptionPane.showConfirmDialog(null, inputMessage, "Confirmation", JOptionPane.WARNING_MESSAGE);
		return rtn;
	}
	
	final public static void terminate()
	{
		//just exit
		System.exit(0);
	}
	
	private static Log log = new Log();
	
	final public static void showLog()
	{
		log.setVisible(true);
	}
	
	final public static void logInternalMessage(String message)
	{
		log.logMessage(message);
		InternalLog.log(message);
	}
	
	final public static void logConnectionMessage(String message)
	{
		log.logMessage(message);
		ConnectionLog.log(message);
	}
	
	final public static void logSuspiciousMessage(String message)
	{
		log.logMessage(message);
		SuspiciousLog.log(message);
	}
}
