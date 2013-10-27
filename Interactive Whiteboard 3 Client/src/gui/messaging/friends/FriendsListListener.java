package gui.messaging.friends;

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

public class FriendsListListener implements ActionListener
{
	
	final private FriendsList m_gui;
	
	//networking objects used:
	final private MessagingServerStepConnection m_messagingServerStepConnection;
	public FriendsListListener(FriendsList gui, MessagingServerStepConnection messagingServerStepConnection)
	{
		this.m_gui = gui;
		this.m_messagingServerStepConnection = messagingServerStepConnection;
	}
	
	@Override
	final public void actionPerformed(ActionEvent e)
	{
		String command = e.getActionCommand();
		if (command.equals(Text.GUI.MESSAGING.FRIENDS.ADD_FRIEND_STRING))
		{
			addFriend();
			this.m_gui.updateFriends();
		} else if (command.equals(Text.GUI.MESSAGING.FRIENDS.RELIST_STRING))
		{
			this.m_gui.refreshList();
		}
	}
	
	final private void addFriend()
	{
		String friendUsername = CommonMethods.requestInputMessage(Text.GUI.MESSAGING.FRIENDS.REQUEST_FRIEND_USERNAME_STRING);
		if (friendUsername != null && !friendUsername.equals("") && !MESSAGES.containsBadCharacters(friendUsername) && !MESSAGES.isAllSpaces(friendUsername))
		{
			friendUsername = MESSAGES.substituteForMessageDelimiters(friendUsername);
			try
			{
				this.m_messagingServerStepConnection.addFriend(Login.m_username, Login.m_password, friendUsername);
			} catch (IOException e)
			{
				this.m_messagingServerStepConnection.displayConnectionLostMessage();
			} catch (OperationFailedException e)
			{
				this.m_messagingServerStepConnection.handleOperationFailedException(e);
			} catch (InvalidMessageException badMessage)
			{
				this.m_messagingServerStepConnection.handleInvalidMessageException(badMessage);
			}
		} else
		{
			CommonMethods.displayErrorMessage(Text.GUI.MESSAGING.FRIENDS.INVALID_FRIEND_USERNAME_STRING);
		}
	}

}
