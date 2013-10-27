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

public class DisplayNameChangeListener implements ActionListener
{

	final private DisplayNameChange m_gui;
	
	//networking objects used:
	final private MainServerStepConnection m_mainServerStepConnection;
	
	public DisplayNameChangeListener(DisplayNameChange gui, MainServerStepConnection mainServerStepConnection)
	{
		this.m_gui = gui;
		this.m_mainServerStepConnection = mainServerStepConnection;
	}
	
	@Override
	final public void actionPerformed(ActionEvent e)
	{
		String command = e.getActionCommand();
		//figure out what the command is and act appropriately
		if (command.equals(Text.GUI.MAIN.DISPLAYNAMECHANGE.CLEAR_STRING))
		{
			clearAllFields();
		} else if (command.equals(Text.GUI.MAIN.DISPLAYNAMECHANGE.CHANGE_DISPLAY_NAME_COMMAND_STRING))
		{
			String username = Login.m_username;
			String password = this.m_gui.getPassword();
			String nameChangeCode = this.m_gui.getNameChangeCode();
			String newName = this.m_gui.getNewName();
			String confirmNewName = this.m_gui.getConfirmNewName();
			changeDisplayName(username, password, nameChangeCode, newName, confirmNewName);
		}
	}
	
	final private void clearAllFields()
	{
		if (CommonMethods.displayConfirmDialog(Text.GUI.MAIN.DISPLAYNAMECHANGE.CLEAR_CONFIRM_MESSAGE) == CommonMethods.CONFIRM_YES_RESPONSE)
		{
			this.m_gui.clearAllFields();
		}
	}
	
	final private void changeDisplayName(String username, String password, String nameChangeCode, String newName, String confirmNewName)
	{
		if (newName.equals(confirmNewName))
		{
			if (MESSAGES.containsBadCharacters(newName))
			{
				CommonMethods.displayErrorMessage(Text.GUI.MAIN.DISPLAYNAMECHANGE.NEW_NAME_CONTAINS_INVALID_CHARACTERS_ERROR_MESSAGE);
			} else
			{
				String usernameToSend = MESSAGES.substituteForMessageDelimiters(username);
				String passwordToSend = MESSAGES.substituteForMessageDelimiters(password);
				String nameChangeCodeToSend = MESSAGES.substituteForMessageDelimiters(nameChangeCode);
				String newNameToSend = MESSAGES.substituteForMessageDelimiters(newName);
				try
				{
					this.m_mainServerStepConnection.changeDisplayName(usernameToSend, passwordToSend, nameChangeCodeToSend, newNameToSend);
					CommonMethods.displaySuccessMessage(Text.GUI.MAIN.DISPLAYNAMECHANGE.CHANGE_DISPLAY_NAME_SUCCESS_MESSAGE);
					this.m_gui.clearAllFields();
				} catch (IOException e)
				{
					CommonMethods.displayErrorMessage(Text.NET.getConnectionLostMessage(Text.NET.MAIN_SERVER_NAME));
				} catch (OperationFailedException e)
				{
					OperationErrorCode errorCode = e.getErrorCode();
					switch(errorCode)
					{
						case USERNAME_DOES_NOT_EXIST:
							CommonMethods.displayErrorMessage(Text.GUI.MAIN.DISPLAYNAMECHANGE.USERNAME_DOES_NOT_EXIST_ERROR_MESSAGE);
							break;
						case PASSWORD_IS_INCORRECT:
							CommonMethods.displayErrorMessage(Text.GUI.MAIN.DISPLAYNAMECHANGE.INVALID_PASSWORD_ERROR_MESSAGE);
							break;
						case NAME_CHANGE_CODE_IS_INCORRECT:
							CommonMethods.displayErrorMessage(Text.GUI.MAIN.DISPLAYNAMECHANGE.INVALID_NAME_CHANGE_CODE_ERROR_MESSAGE);
							break;
						case NEW_DISPLAY_NAME_IS_INVALID:
							CommonMethods.displayErrorMessage(Text.GUI.MAIN.DISPLAYNAMECHANGE.INVALID_NEW_NAME_ERROR_MESSAGE);
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
			CommonMethods.displayErrorMessage(Text.GUI.MAIN.DISPLAYNAMECHANGE.CONFIRM_DISPLAY_NAME_NOT_MATCHING_ERROR_MESSAGE);
		}
	}
}
