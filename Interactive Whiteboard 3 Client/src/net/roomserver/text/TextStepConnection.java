package net.roomserver.text;

import gui.login.Login;

import java.io.IOException;
import java.util.Scanner;

import util.CommonMethods;
import util.Text;

import net.InvalidMessageException;
import net.MESSAGES;
import net.OperationFailedException;
import net.StepConnection;

public class TextStepConnection extends StepConnection
{

	final private String m_joinPassword;
	
	public TextStepConnection(String ip, int port, String joinPassword) throws IOException 
	{
		super(ip, port);
		this.m_joinPassword = joinPassword;
	}
	
	final public void attemptLogin()
	{
		//only load the client gui if we can connect to the room data server server
		try
		{
			if (attemptRoomLogin(Login.m_username, Login.m_password, this.m_joinPassword))
			{
				//continue
			} else
			{
				CommonMethods.displayErrorMessage(Text.NET.GENERAL.LOGIN_FAILED_ERROR_MESSAGE);
				return;
			}
		} catch (IOException connectionError)
		{
			CommonMethods.displayErrorMessage(Text.NET.getConnectionLostMessage(Text.NET.ROOM_SERVER_NAME));
			return;
		} catch (InvalidMessageException invalidMessage)
		{
			this.handleInvalidMessageException(invalidMessage);
			return;
		}
	}
	
	@Override
	final protected synchronized String sendMessageAndGetResponse(String messageToRoomDataServer) throws IOException
	{
		System.out.println("Room Text Server: Sent message to server: " + messageToRoomDataServer);
		String response = super.sendMessageAndGetResponse(messageToRoomDataServer);
		System.out.println("Room Text Server: Received message from server: " + response);
		return response;
	}
	
	final public void addLineOfChatHistory(String message) throws IOException, InvalidMessageException
	{
		String username = Login.m_username;
		String joinPassword = this.m_joinPassword;
		String messageToSend = MESSAGES.substituteForMessageDelimiters(message);
		String messageToServer = MESSAGES.ROOMSERVER.TEXT_SERVER.SEND_CHAT + MESSAGES.DELIMITER 
				+ username + MESSAGES.DELIMITER + joinPassword + MESSAGES.DELIMITER + messageToSend; 
		String response = sendMessageAndGetResponse(messageToServer);
		//look through the response
		Scanner scanResponse = new Scanner(response);
		//first part should be result
		String result = scanResponse.next();
		if (result.equals(MESSAGES.ROOMSERVER.TEXT_SERVER.SEND_CHAT_SUCCESS))
		{
			//if successful, continue
		} else if (result.equals(MESSAGES.ROOMSERVER.TEXT_SERVER.SEND_CHAT_FAILED))
		{
			//if failed, ignore
		} else
		{
			throw generateInvalidMessageException(response);
		}
	}
	
	final public void displayConnectionLostMessage()
	{
		super.displayConnectionLostMessage(Text.NET.ROOM_SERVER_NAME);
	}

	@Override
	public void handleOperationFailedException(OperationFailedException e) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleInvalidMessageException(InvalidMessageException e) 
	{
		// TODO Auto-generated method stub
		
	}

}
