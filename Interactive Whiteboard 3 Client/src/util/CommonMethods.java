package util;

import javax.swing.JOptionPane;

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
		int rtn = JOptionPane.showConfirmDialog(null, inputMessage, "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		return rtn;
	}
	
	final public static void terminate()
	{
		//just exit
		System.exit(0);
	}
}
