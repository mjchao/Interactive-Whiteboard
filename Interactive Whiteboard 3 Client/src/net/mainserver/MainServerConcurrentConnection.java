package net.mainserver;

import gui.main.Client;

import java.io.IOException;
import java.util.Scanner;


import net.ConcurrentConnection;
import net.MESSAGES;

//this is to be used for future updates. we may have to add a concurrent connection
//for the main server.
final public class MainServerConcurrentConnection extends ConcurrentConnection
{

	@SuppressWarnings("unused")
	final private Client m_gui;
	
	public MainServerConcurrentConnection(Client gui, String ip, int port) throws IOException 
	{
		super(ip, port);
		this.m_gui = gui;
	}

	@Override
	final protected synchronized void decode(String copyOfMessage)
	{
		String message = copyOfMessage;
		//look through the message
		Scanner scanMessage = new Scanner(message);
		//the next part should be a command
		String command = scanMessage.next();
		if (command.equals(MESSAGES.MAINSERVER.UPDATE_DISPLAY_NAME))
		{
			//TODO
		}
		scanMessage.close();
	}	
}
