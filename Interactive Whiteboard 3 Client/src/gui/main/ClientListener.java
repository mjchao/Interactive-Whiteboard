package gui.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import util.CommonMethods;
import util.Text;

public class ClientListener implements ActionListener
{

	final private Client m_gui;
	
	public ClientListener(Client gui)
	{
		this.m_gui = gui;
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		String command = e.getActionCommand();
		if (command.equals(Text.GUI.MAIN.CHANGE_PASSWORD_STRING))
		{
			loadPasswordChange();
		} else if (command.equals(Text.GUI.MAIN.CHANGE_DISPLAY_NAME_STRING))
		{
			loadDisplayNameChange();
		} else if (command.equals(Text.GUI.MAIN.EXIT_STRING))
		{
			exit();
		} else if (command.equals(Text.GUI.MESSAGING.VIEW_FRIENDS_LIST_STRING))
		{
			loadFriendsList();
		} else if (command.equals(Text.GUI.MESSAGING.VIEW_PESTS_LIST_STRING))
		{
			loadPestsList();
		} else if (command.equals(Text.GUI.MESSAGING.VIEW_PRIVATE_CHAT_STRING))
		{
			loadPrivateChat();
		} else if (command.equals(Text.GUI.MAIN.CREATE_ROOM_STRING))
		{
			loadRoomCreationInterface();
		} else if (command.equals(Text.GUI.MAIN.MODIFY_ROOM_STRING))
		{
			loadRoomModificationInterface();
		} else if (command.equals(Text.GUI.MAIN.DELETE_ROOM_STRING))
		{
			loadRoomModificationInterface();
		} else if (command.equals(Text.GUI.MAIN.JOIN_ROOM_STRING))
		{
			loadJoinRoomInterface();
		}
	}
	
	final private void loadPasswordChange()
	{
		this.m_gui.loadPasswordChange();
	}
	
	final private void loadDisplayNameChange()
	{
		this.m_gui.loadDisplayNameChange();
	}
	
	final private void loadFriendsList()
	{
		this.m_gui.loadFriendsList();
	}
	
	final private void loadPestsList()
	{
		this.m_gui.loadPestsList();
	}
	
	final private void loadPrivateChat()
	{
		this.m_gui.loadPrivateChat();
	}
	
	final private void loadRoomCreationInterface()
	{
		this.m_gui.loadRoomCreationInterface();
	}
	
	final private void loadRoomModificationInterface()
	{
		this.m_gui.loadRoomModificationInterface();
	}
	
	final private void loadJoinRoomInterface()
	{
		this.m_gui.loadJoinRoomInterface();
	}
	
	/**
	 * exits the program.
	 * suppressed warnings:<br>
	 * 1) static-method			not static because the program should not be terminating if the user interface
	 * 							and this listener have not been created.
	 */
	@SuppressWarnings("static-method")
	final private void exit()
	{
		if (CommonMethods.displayConfirmDialog(Text.GUI.CLIENTLISTENER.EXIT_CONFIRM_MESSAGE) == CommonMethods.CONFIRM_YES_RESPONSE)
		{
			CommonMethods.terminate();
		}		
	}
}
