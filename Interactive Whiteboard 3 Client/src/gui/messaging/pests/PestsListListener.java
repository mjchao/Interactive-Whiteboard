package gui.messaging.pests;

import gui.login.Login;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import net.InvalidMessageException;
import net.MESSAGES;
import net.OperationFailedException;
import net.messagingserver.MessagingServerStepConnection;

import util.CommonMethods;
import util.Text;

public class PestsListListener implements ActionListener
{

	final private PestsList m_gui;
	
	//networking objects used:
	final private MessagingServerStepConnection m_messagingServerStepConnection;
	
	public PestsListListener(PestsList gui, MessagingServerStepConnection messagingServerStepConnection)
	{
		this.m_gui = gui;
		//remember the device we will use to communicate with the server
		this.m_messagingServerStepConnection = messagingServerStepConnection;
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		String command = e.getActionCommand();
		if (command.equals(Text.GUI.MESSAGING.PESTS.ADD_PEST_STRING))
		{
			addPest();
		} else if (command.equals(Text.GUI.MESSAGING.PESTS.RELIST_STRING))
		{
			this.m_gui.refreshList();
		}
	}
	
	final private void addPest()
	{
		String pestUsername = CommonMethods.requestInputMessage(Text.GUI.MESSAGING.PESTS.REQUEST_PEST_USERNAME_STRING);
		if (pestUsername != null && !pestUsername.equals("") && !MESSAGES.containsBadCharacters(pestUsername) && !MESSAGES.isAllSpaces(pestUsername))
		{
			pestUsername = MESSAGES.substituteForMessageDelimiters(pestUsername);
			try
			{
				this.m_messagingServerStepConnection.addPest(Login.m_username, Login.m_password, pestUsername);
				this.m_gui.updatePests();
			} catch (IOException e)
			{
				this.m_messagingServerStepConnection.displayConnectionLostMessage();
			} catch (OperationFailedException e)
			{
				this.m_messagingServerStepConnection.handleOperationFailedException(e);
			} catch (InvalidMessageException e) 
			{
				this.m_messagingServerStepConnection.handleInvalidMessageException(e);
			}
		} else
		{
			CommonMethods.displayErrorMessage(Text.GUI.MESSAGING.PESTS.INVALID_PEST_USERNAME);
		}
	}

}
