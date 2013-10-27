package gui.main.account;

import gui.login.Login;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import net.InvalidMessageException;
import net.MESSAGES;
import net.OperationErrorCode;
import net.OperationFailedException;
import net.mainserver.MainServerStepConnection;

import util.CommonMethods;
import util.Text;

final public class PasswordChangeListener implements ActionListener
{

	final private PasswordChange m_gui;
	
	//networking objects used:
	final private MainServerStepConnection m_mainServerStepConnection;
	
	public PasswordChangeListener(PasswordChange gui, MainServerStepConnection mainServerStepConnection)
	{
		this.m_gui = gui;
		this.m_mainServerStepConnection = mainServerStepConnection;
	}
	
	@Override
	final public void actionPerformed(ActionEvent e)
	{
		String command = e.getActionCommand();
		//figure out what the command was and act appropriately
		if (command.equals(Text.GUI.MAIN.PASSWORDCHANGE.CLEAR_STRING))
		{
			clearAllFields();
		} else if (command.equals(Text.GUI.MAIN.PASSWORDCHANGE.CHANGE_PASSWORD_COMMAND_STRING))
		{
			String username = Login.m_username;
			String currentPassword = this.m_gui.getCurrentPassword();
			String newPassword = this.m_gui.getNewPassword();
			String confirmNewPassword = this.m_gui.getConfirmNewPassword();
			attemptChangePassword(username, currentPassword, newPassword, confirmNewPassword);
		}
	}
	
	final private void clearAllFields()
	{
		//make sure the client wants to clear all fields
		if (CommonMethods.displayConfirmDialog(Text.GUI.MAIN.PASSWORDCHANGE.CLEAR_CONFIRM_MESSAGE) == CommonMethods.CONFIRM_YES_RESPONSE)
		{
			this.m_gui.clearAllFields();
		}
	}
	
	final private void attemptChangePassword(String username, String currentPassword, String newPassword, String confirmNewPassword)
	{
		if (newPassword.equals(confirmNewPassword))
		{
			if (MESSAGES.containsBadCharacters(newPassword))
			{
				CommonMethods.displayErrorMessage(Text.GUI.MAIN.PASSWORDCHANGE.NEW_PASSWORD_CONTAINS_BAD_CHARACTERS_ERROR_MESSAGE);
			} else
			{
				String usernameToSend = MESSAGES.substituteForMessageDelimiters(username);
				String currentPasswordToSend = MESSAGES.substituteForMessageDelimiters(currentPassword);
				String newPasswordToSend = MESSAGES.substituteForMessageDelimiters(newPassword);
				try
				{
					this.m_mainServerStepConnection.changePassword(usernameToSend, currentPasswordToSend, newPasswordToSend);
					CommonMethods.displaySuccessMessage(Text.GUI.MAIN.PASSWORDCHANGE.CHANGE_PASSWORD_SUCCESS_MESSAGE);
					this.m_gui.clearAllFields();
				} catch (IOException e)
				{
					CommonMethods.displayErrorMessage(Text.NET.getConnectionLostMessage(Text.NET.MAIN_SERVER_NAME));
				} catch (OperationFailedException operationFailed) 
				{
					OperationErrorCode errorCode = operationFailed.getErrorCode();
					switch(errorCode)
					{
						case USERNAME_DOES_NOT_EXIST:
							CommonMethods.displayErrorMessage(Text.GUI.MAIN.PASSWORDCHANGE.USERNAME_DOES_NOT_EXIST);
							break;
						case PASSWORD_IS_INCORRECT:
							CommonMethods.displayErrorMessage(Text.GUI.MAIN.PASSWORDCHANGE.INVALID_CURRENT_PASSWORD_ERROR_MESSAGE);
							break;
						case NEW_PASSWORD_IS_INVALID:
							CommonMethods.displayErrorMessage(Text.GUI.MAIN.PASSWORDCHANGE.INVALID_NEW_PASSWORD_ERROR_MESSAGE);
							break;
						default:
							//TODO
					}
				} catch (InvalidMessageException badMessage) 
				{
					this.m_mainServerStepConnection.handleInvalidMessageException(badMessage);
				}
			}
		} else
		{
			CommonMethods.displayErrorMessage(Text.GUI.MAIN.PASSWORDCHANGE.CONFIRM_PASSWORD_NOT_MATCHING_ERROR_MESSAGE);
		}
	}
}
