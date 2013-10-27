package net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import util.CommonMethods;
import util.Text;

//this connection does things step by step - no concurrency is allowed.
//this connection allows communication with the server. it sends a message and gets a response.
//because this does not depends on a UI - rather, the UI depends on it - it was decided that
//this class should not be instantiated and it provides methods available to all UIs with
//needing to call a constructor.

abstract public class StepConnection
{
	protected String m_ip;
	protected int m_port;
	protected Socket m_socket;
	protected DataInputStream m_in;
	protected DataOutputStream m_out;
	
	public StepConnection(String ip, int port) throws IOException
	{
		this.m_ip = ip;
		this.m_port = port;
		this.m_socket = new Socket(ip, port);
		this.m_in = new DataInputStream(this.m_socket.getInputStream());
		this.m_out = new DataOutputStream(this.m_socket.getOutputStream());
	}
	
	/**
	 * reads from a stream containing data sent from a server.
	 * suppressed warnings:<br>
	 * 
	 * @return						a message sent from a server
	 * @throws IOException			if the connection to the server unexpectedly ends
	 */
	final protected synchronized String read() throws IOException
	{
		return this.m_in.readUTF();
	}
	
	/**
	 * writes a given message to a server.
	 * suppressed warnings:<br>
	 * 
	 * @param message				a String, the message to write to the server
	 * @throws IOException			if the connection to the server unexpectedly ends
	 */
	final protected synchronized void write(String message) throws IOException
	{
		this.m_out.writeUTF(message);
	}
	
	protected synchronized String sendMessageAndGetResponse(String message) throws IOException
	{
		write(message);
		String response = read();
		return response;
	}
	
	final public boolean attemptLogin(String username, String password) throws IOException, InvalidMessageException
	{
		String message = MESSAGES.GENERAL.LOGIN + MESSAGES.DELIMITER + username + MESSAGES.DELIMITER + password;
		String response = sendMessageAndGetResponse(message);
		//look through the response
		Scanner scanResponse = new Scanner(response);
		//the first part should be the result
		String result = scanResponse.next();
		scanResponse.close();
		if (result.equals(MESSAGES.GENERAL.LOGIN_SUCCESS))
		{
			//if the operation was successful, return true
			return true;
		} else if (result.equals(MESSAGES.GENERAL.LOGIN_FAILED))
		{
			return false;
		} else
		{
			throw generateInvalidMessageException(response);
		}
	}
	
	final public boolean attemptRoomLogin(String username, String password, String joinPassword) throws IOException, InvalidMessageException
	{
		String message = MESSAGES.GENERAL.LOGIN + MESSAGES.DELIMITER + username + MESSAGES.DELIMITER + password + MESSAGES.DELIMITER + joinPassword;
		String response = sendMessageAndGetResponse(message);
		//look through the response
		Scanner scanResponse = new Scanner(response);
		//the first part should be the result
		String result = scanResponse.next();
		scanResponse.close();
		if (result.equals(MESSAGES.GENERAL.LOGIN_SUCCESS))
		{
			//if the operation was successful, return true
			return true;
		} else if (result.equals(MESSAGES.GENERAL.LOGIN_FAILED))
		{
			return false;
		} else
		{
			throw generateInvalidMessageException(response);
		}
	}
	
	final public static void displayConnectionLostMessage(String serverName)
	{
		CommonMethods.displayErrorMessage(Text.NET.getConnectionLostMessage(serverName));
	}
	
	final protected static InvalidMessageException generateInvalidMessageException(String message)
	{
		return new InvalidMessageException(message);
	}
	
	abstract public void handleOperationFailedException(OperationFailedException e);
	
	abstract public void handleInvalidMessageException(InvalidMessageException e);
	
	
	/**
	 * closes this connection to a server.
	 * suppressed warnings:<br>
	 */
	final public void close()
	{
		try
		{
			this.m_in.close();
		} catch (IOException e)
		{
			//ignore
		}
		try
		{
			this.m_out.close();
		} catch (IOException e)
		{
			//ignore
		}
		try
		{
			this.m_socket.close();
		} catch (IOException e)
		{
			//ignore
		}
	}
}
