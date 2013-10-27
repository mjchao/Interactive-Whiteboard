package database;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

import util.CommonMethods;
import util.Text;

/**
 * This class provides methods for this whiteboard server to communicate with the database.
 * This class will be able to send specific messages to the server an decode specific messages
 * from the server and obtain any information the message may contain.
 *
 * This class is all global because almost all parts of the server will need to communicate with the
 * database, so rather than give out references to a single database connection object, we will just
 * make it global with the proper encapsulation.
 */
final public class DatabaseConnection
{
	final private static String CONNECTION_PASSWORD = "hi";
	
	private static Socket m_socket;
	private static DataInputStream m_in;
	private static DataOutputStream m_out;
	
	private static boolean isInitialized = false;
	
	final public static void initialize(String serverIP, int serverPort) throws IOException, ConnectionEndedException
	{
		//initialize networking objects
		m_socket = new Socket(serverIP, serverPort);
		m_in = new DataInputStream(m_socket.getInputStream());
		m_out = new DataOutputStream(m_socket.getOutputStream());
		//first, the server will ask for an IP address, but make sure it actually sends that message
		String requestIPMessage = m_in.readUTF();
		if (requestIPMessage.equals(MESSAGES.CONNECTION.HEADING + MESSAGES.DELIMITER + MESSAGES.CONNECTION.REQUEST_IP))
		{
			//send the server our IP Address
			sendIpAddress(m_out);
			//the database should let us know our IP Address is okay
			String databaseResponse = m_in.readUTF();
			//if the IP Address is okay, continue, if not we should stop
			if (databaseResponse.equals(MESSAGES.CONNECTION.HEADING + MESSAGES.DELIMITER + MESSAGES.CONNECTION.GOOD_IP))
			{
				CommonMethods.logConnectionMessage(Text.DATABASECONNECTION.GOOD_IP_LOG_MESSAGE);
			} else
			{
				CommonMethods.logConnectionMessage(Text.DATABASECONNECTION.getBadIPLogMessage(getIpAddress()));
				close();
				throw new ConnectionEndedException(ConnectionEndedException.BAD_IP_ADDRESS);
			}
		} else
		{
			CommonMethods.logConnectionMessage(Text.DATABASECONNECTION.getConnectedToWrongServerLogMessage(serverIP, serverPort));
			close();
			throw new ConnectionEndedException(ConnectionEndedException.WRONG_SERVER);
		}
		//then, the server will ask for a connection password, but make sure it actually sends that message
		String requestConnectionPasswordMessage = m_in.readUTF();
		if (requestConnectionPasswordMessage.equals(MESSAGES.CONNECTION.HEADING + MESSAGES.DELIMITER + MESSAGES.CONNECTION.REQUEST_CONNECTION_PASSWORD))
		{
			//send the connection password to the server
			sendConnectionPassword(m_out);
			//the database should then let us know our connection password is okay
			//if it's not okay, then we stop the connection
			String databaseResponse = m_in.readUTF();
			if (databaseResponse.equals(MESSAGES.CONNECTION.HEADING + MESSAGES.DELIMITER + MESSAGES.CONNECTION.CONNECTION_ACCEPTED))
			{
				CommonMethods.logConnectionMessage(Text.DATABASECONNECTION.GOOD_CONNECTION_PASSWORD_LOG_MESSAGE);
			} else
			{
				CommonMethods.logConnectionMessage(Text.DATABASECONNECTION.getBadConnectionPasswordLogMessage(CONNECTION_PASSWORD));
				close();
				throw new ConnectionEndedException(ConnectionEndedException.BAD_CONNECTION_PASSWORD);
			}
		} else
		{
			CommonMethods.logConnectionMessage(Text.DATABASECONNECTION.getConnectedToWrongServerLogMessage(serverIP, serverPort));
			close();
			throw new ConnectionEndedException(ConnectionEndedException.WRONG_SERVER);
		}
		isInitialized = true;
		CommonMethods.logConnectionMessage(Text.DATABASECONNECTION.getSuccessfullyConnectedToServerLogMessage(serverIP, serverPort));
	}
	
	final private static void sendIpAddress(DataOutputStream out) throws IOException
	{
		String IP = getIpAddress();
		out.writeUTF(MESSAGES.CONNECTION.HEADING + MESSAGES.DELIMITER + MESSAGES.CONNECTION.RETURN_IP + MESSAGES.DELIMITER + IP);
	}
	
	final private static String getIpAddress() throws IOException
	{
		return InetAddress.getLocalHost().toString();
	}
	
	final private static void sendConnectionPassword(DataOutputStream out) throws IOException
	{
		out.writeUTF(MESSAGES.CONNECTION.HEADING + MESSAGES.DELIMITER + MESSAGES.CONNECTION.RETURN_CONNECTION_PASSWORD + MESSAGES.DELIMITER + CONNECTION_PASSWORD);
	}
	
	//this method ends the database connection
	final public static void close()
	{
		try
		{
			m_in.close();
		} catch (IOException e)
		{
			//Ignore this should work
		}
		try
		{
			m_out.close();
		} catch (IOException e)
		{
			//Ignore this should work
		}
		try
		{
			m_socket.close();
		} catch (IOException e)
		{
			//ignore this should work
		}
		isInitialized = false;
	}
	
	final public static boolean isInitialized()
	{
		return isInitialized;
	}
	
	//given a string, this message replaces all Database Message delimiters in the message with
	//a substitution
	final public static String DELIMITER_SUBSTITUTE = "\u00D0";
	final public static String substituteForMessageDelimiters(String aMessage)
	{
		String rtn = aMessage;
		for (int charIndex = 0; charIndex < aMessage.length(); charIndex++)
		{
			if (rtn.substring(charIndex, charIndex + 1).equals(MESSAGES.DELIMITER))
			{
				rtn = rtn.substring(0, charIndex) + DELIMITER_SUBSTITUTE + rtn.substring(charIndex + 1, rtn.length());
			}
		}
		return rtn;
	}
	
	/**
	 * sends a message to the database server and reads the database server's response. decoding will be
	 * done by whatever called this method.
	 * assumes:<br>
	 * 1) initialize() method has already been called. 
	 * 
	 * Reflection: At one point during the project, it turned out that multiple subservers for the messaging server
	 * were calling this method at the same time. In the debugging, I would keep getting messages saying that
	 * the databse server was sending bad messages and I would see "$"s when I printed out the messages that were
	 * read by the subservers - but I had never put any "$"s into any of the messages sent by the database
	 * server! And after a while, I realized the subservers were reading parts of TWO messages as ONE. And then
	 * - only then, did I think back to race conditions Mr. Sea taught us in CSC 999. And then I remembered
	 * the keyword <code>synchronized</code> and only after all that time did I realize I was missing a single
	 * world.
	 * 
	 * @param message							a String, the message to be sent to the database server
	 * @return									a String, the response from the database server
	 * @throws ConnectionEndedException			if the connection unexpectedly ends
	 */
	//all messages to the database server will receive a response. this method will
	//send a message and return the server's response. the decoding is up to whatever
	//called this method.
	//this method assumes the initialize() method has already been called. 
	
	final public static synchronized String sendMessageAndGetResponse(String message) throws ConnectionEndedException
	{
		try
		{
			m_out.writeUTF(CONNECTION_PASSWORD + MESSAGES.DELIMITER + message);
			String reply = m_in.readUTF();
			if (reply.equals(MESSAGES.BAD_CONNECTION_PASSWORD))
			{
				CommonMethods.displayErrorMessage(Text.DATABASECONNECTION.getBadConnectionPasswordLogMessage(CONNECTION_PASSWORD));
				close();
				throw new ConnectionEndedException(ConnectionEndedException.BAD_CONNECTION_PASSWORD);
			}
			System.out.println("Wrote to Database: " + message);
			System.out.println("Received from Database: " + reply);
			return reply;
		} catch (IOException e)
		{
			close();
			throw new ConnectionEndedException(ConnectionEndedException.IOEXCEPTION);
		}
	}
	
	final public static String requestNewNameChangeCode(String username, String password) throws ConnectionEndedException
	{	
		String usernameToSend = substituteForMessageDelimiters(username);
		String passwordToSend = substituteForMessageDelimiters(password);
		String message = MESSAGES.USERDATA.HEADING + MESSAGES.DELIMITER + MESSAGES.USERDATA.GET_NEW_NAME_CHANGE_CODE + MESSAGES.DELIMITER + usernameToSend + MESSAGES.DELIMITER + passwordToSend;
		String response = sendMessageAndGetResponse(message);
		//look through the response
		Scanner scanResponse = new Scanner(response);
		//the first part should be the result
		String result = scanResponse.next();
		if (result.equals(MESSAGES.USERDATA.GET_NEW_NAME_CHANGE_CODE_SUCCESS))
		{
			//if successful, then return the new name change code
			String nameChangeCode = scanResponse.next();
			scanResponse.close();
			return nameChangeCode;
		}
		//if not successful, then return null
		scanResponse.close();
		return null;
	}
	
	//DEBUG - Run the database server and then run this.
	/*
	final public static void main(String[] args) throws IOException
	{
		initialize("localhost", 9999);
	}//*/
}
