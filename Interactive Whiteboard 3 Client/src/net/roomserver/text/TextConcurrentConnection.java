package net.roomserver.text;

import gui.login.Login;
import gui.room.text.TextChat;

import java.io.IOException;
import java.util.Scanner;

import util.Text;

import net.ConcurrentConnection;
import net.MESSAGES;
import net.messagingserver.MessagingServerStepConnection;

public class TextConcurrentConnection extends ConcurrentConnection
{

	private TextChat m_gui;
	final private MessagingServerStepConnection m_messagingServerStepConnection;
	
	public TextConcurrentConnection(String ip, int port, MessagingServerStepConnection messagingServerStepConnection) throws IOException 
	{
		super(ip, port);
		this.m_messagingServerStepConnection = messagingServerStepConnection;
	}
	
	final public void setTextChatGUI(TextChat gui)
	{
		this.m_gui = gui;
	}

	@Override
	protected void decode(String message) 
	{
		//look through the message
		Scanner scanMessage = new Scanner(message);
		//figure out what server wants to do
		String command = scanMessage.next();
		if (command.equals(MESSAGES.ROOMSERVER.TEXT_SERVER.ADD_CHAT))
		{
			//expect a sender username and a message
			String senderUsername = scanMessage.next();
			String senderMessage = scanMessage.next();
			String senderDisplayName = this.m_messagingServerStepConnection.getDisplayNameOfUser(Login.m_username, Login.m_password, senderUsername);
			String messageToAdd = MESSAGES.unsubstituteForMessageDelimiters(senderMessage);
			this.m_gui.addMessage(senderDisplayName, messageToAdd);
		} else if (command.equals(MESSAGES.ROOMSERVER.TEXT_SERVER.ADD_SYSTEM_MESSAGE))
		{
			//expect a message
			String systemMessage = scanMessage.next();
			String messageToAdd = MESSAGES.unsubstituteForMessageDelimiters(systemMessage);
			this.m_gui.addMessage(Text.GUI.ROOM.TEXT.SYSTEM_USERNAME, messageToAdd);
		}
	}

}
