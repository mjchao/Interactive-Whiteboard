package net.roomserver.whiteboard;

import gui.login.Login;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Scanner;

import util.CommonMethods;
import util.Text;

import net.InvalidMessageException;
import net.MESSAGES;
import net.OperationFailedException;
import net.StepConnection;

public class WhiteboardStepConnection extends StepConnection
{

	final private String m_joinPassword;
	public WhiteboardStepConnection(String ip, int port, String joinPassword) throws IOException 
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
		System.out.println("Room Whiteboard Server: Sent message to server: " + messageToRoomDataServer);
		String response = super.sendMessageAndGetResponse(messageToRoomDataServer);
		System.out.println("Room Whiteboard Server: Received message from server: " + response);
		return response;
	}

	final public boolean setPixel(int x, int y, int r, int g, int b, BigInteger priority) throws IOException, InvalidMessageException
	{
		String messageToServer = MESSAGES.ROOMSERVER.WHITEBOARD_SERVER.DRAW_PIXEL + MESSAGES.DELIMITER 
				+ Login.m_username + MESSAGES.DELIMITER + this.m_joinPassword + MESSAGES.DELIMITER + x + MESSAGES.DELIMITER + y + MESSAGES.DELIMITER + r + MESSAGES.DELIMITER + g + MESSAGES.DELIMITER + b + MESSAGES.DELIMITER + priority;
		String response = sendMessageAndGetResponse(messageToServer);
		//look through the response
		Scanner scanResponse = new Scanner(response);
		//first part should be result
		String result = scanResponse.next();
		if (result.equals(MESSAGES.ROOMSERVER.WHITEBOARD_SERVER.DRAW_PIXEL_SUCCESS))
		{
			//if successful, continue
			scanResponse.close();
			return true;
		} else if (result.equals(MESSAGES.ROOMSERVER.WHITEBOARD_SERVER.DRAW_PIXEL_FAILED))
		{
			//if failed, figure out why
			//error code should follow
			int errorCode = scanResponse.nextInt();
			scanResponse.close();
			if (errorCode == MESSAGES.ROOMSERVER.INVALID_JOIN_PASSWORD_ERROR_CODE)
			{
				//ignore, should not happen unless client was tampered with
			} else if (errorCode == MESSAGES.ROOMSERVER.YOU_LACK_WHITEBOARD_ERROR_CODE)
			{
				CommonMethods.displayErrorMessage(Text.NET.ROOMSERVER.WHITEBOARD.YOU_LACK_WHITEBOARD_ERROR_MESSAGE);
			} else if (errorCode == MESSAGES.ROOMSERVER.WHITEBOARD_SERVER.OUT_OF_BOUNDS_ERROR_CODE)
			{
				//ignore
			}
			return false;
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
		// ignore
	}

	@Override
	public void handleInvalidMessageException(InvalidMessageException e) 
	{
		// ignore
	}
}
