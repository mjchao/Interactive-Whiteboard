package net.messagingserver;

import java.io.IOException;
import java.util.Scanner;

import net.ConcurrentConnection;
import net.MESSAGES;
import gui.messaging.Messaging;

final public class MessagingServerConcurrentConnection extends ConcurrentConnection
{

	//TODO Add in concurrency features
	final private Messaging m_gui;
	
	public MessagingServerConcurrentConnection(Messaging gui, String ip, int port) throws IOException
	{
		super(ip, port);
		System.out.println("Messaging Server connected on port " + port);
		this.m_gui = gui;
		start();
	}

	@Override
	final protected synchronized void decode(String copyOfMessage) 
	{
		String message = copyOfMessage;
		//look through the message
		Scanner scanMessage = new Scanner(message);
		//the next part should be a command
		String command = scanMessage.next();
		if (command.equals(MESSAGES.MESSAGINGSERVER.CHANGE_FRIEND_STATUS))
		{
			String friendUsername = scanMessage.next();
			boolean isFriendOnline = scanMessage.nextBoolean();
			this.m_gui.setFriendStatus(friendUsername, isFriendOnline);
			//TODO figure out how to not keep on relying on this
			//TODO figure out what to do if someone changes their display name
			this.m_gui.updateFriendsList();
		} else if (command.equals(MESSAGES.MESSAGINGSERVER.ADD_PRIVATE_MESSAGE))
		{
			String senderUsername = scanMessage.next();
			String recipientUsername = scanMessage.next();
			String privateMessageContents = scanMessage.next();
			this.m_gui.addPrivateMessage(senderUsername, recipientUsername, privateMessageContents);
		}
		scanMessage.close();
	}
}
