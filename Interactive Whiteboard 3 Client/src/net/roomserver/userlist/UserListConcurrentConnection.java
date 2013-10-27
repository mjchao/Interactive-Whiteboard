package net.roomserver.userlist;

import gui.room.userlist.UserData;
import gui.room.userlist.UserList;
import gui.room.userlist.UserListDisplay;
import gui.roomdata.RoomInfoInterface;

import java.io.IOException;
import java.util.Scanner;

import util.CommonMethods;
import util.Text;

import net.ConcurrentConnection;
import net.MESSAGES;

public class UserListConcurrentConnection extends ConcurrentConnection
{

	private UserList m_gui;
	private UserListDisplay m_mainGUI;
	private UserListStepConnection m_userListStepConnection;
	final private RoomInfoInterface m_roomListGUI;
	
	public UserListConcurrentConnection(String ip, int port, RoomInfoInterface roomListGUI) throws IOException 
	{
		super(ip, port);
		this.m_roomListGUI = roomListGUI;
	}
	
	final public void setUserListDisplay(UserListDisplay mainGUI)
	{
		this.m_mainGUI = mainGUI;
		this.m_gui = this.m_mainGUI.getUserList();
	}
	
	final public void setUserListStepConnection(UserListStepConnection userListStepConnection)
	{
		this.m_userListStepConnection = userListStepConnection;
	}

	@Override
	protected void decode(String message) 
	{
		System.out.println("User List Concurrent Connection received from server: " + message);
		Scanner scanMessage = new Scanner(message);
		String command = scanMessage.next();
		//figure out what the server wants
		if (command.equals(MESSAGES.ROOMSERVER.USER_LIST_SERVER.SHOW_USER_PERMISSIONS_FOR_USER))
		{
			//expect a username, a display name, an authority and five booleans
			String username = scanMessage.next();
			String displayName = scanMessage.next();
			int authority = scanMessage.nextInt();
			boolean hasAudioParticipation = scanMessage.nextBoolean();
			boolean hasAudioListening = scanMessage.nextBoolean();
			boolean hasTextParticipation = scanMessage.nextBoolean();
			boolean hasTextUpdating = scanMessage.nextBoolean();
			boolean hasWhiteboard = scanMessage.nextBoolean();
			UserData userData = new UserData(username, displayName, authority, hasAudioParticipation, hasAudioListening, hasTextParticipation, hasTextUpdating, hasWhiteboard);
			this.m_gui.setUser(userData, this.m_mainGUI, this.m_userListStepConnection);
		} else if (command.equals(MESSAGES.ROOMSERVER.USER_LIST_SERVER.RECEIVED_WHITEBOARD))
		{
			//expect a display name
			String displayName = scanMessage.next();
			CommonMethods.displayInformationMessage(Text.GUI.ROOM.USER_LIST.USER_PERMISSIONS_DISPLAY.getReceivedWhiteboardMessage(displayName));
		} else if (command.equals(MESSAGES.ROOMSERVER.USER_LIST_SERVER.USER_LEFT))
		{
			//expect a username
			String username = scanMessage.next();
			this.m_gui.removeUser(username);
		} else if (command.equals(MESSAGES.ROOMSERVER.USER_LIST_SERVER.KICKED))
		{
			//expect a display name
			String displayName = scanMessage.next();
			CommonMethods.displayInformationMessage(Text.GUI.ROOM.USER_LIST.getKickedMessage(displayName));
			this.m_roomListGUI.handleKickedOutOfRoom();
		}
	}

}
