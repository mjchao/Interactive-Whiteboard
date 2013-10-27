package net.roomserver.audio;


import gui.login.Login;

import java.io.IOException;

import util.CommonMethods;
import util.Text;

import net.InvalidMessageException;
import net.OperationFailedException;
import net.StepConnection;

public class AudioStepConnection extends StepConnection
{

	final private String m_joinPassword;
	public AudioStepConnection(String ip, int port, String joinPassword) throws IOException 
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
		System.out.println("Room Audio Server: Sent message to server: " + messageToRoomDataServer);
		String response = super.sendMessageAndGetResponse(messageToRoomDataServer);
		System.out.println("Room Audio Server: Received message from server: " + response);
		return response;
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

	final public void writeBytes(byte[] audioData)
	{
		try
		{
			this.m_out.write(audioData);
		} catch (IOException e)
		{
			//ignore
		}
	}
}
