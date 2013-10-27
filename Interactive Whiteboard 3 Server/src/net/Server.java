package net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

import main.server.ServerUIListener;
import util.CommonMethods;
import util.Text;
import database.ConnectionEndedException;
import database.DatabaseConnection;

/**
 * Template class for a server that is accepting client connections
 *
 */
abstract public class Server extends Thread
{
	final public static String LOCALHOST_IP = "127.0.0.1";
	final public static String LOCALHOST = "localhost";
	
	final protected static int MAX_CLIENTS = 3000;	//the maximum number of clients this server is willing to serve
	final protected ServerSocket m_stepServerSocket;
	final protected ServerSocket m_concurrencyServerSocket;
	final protected int m_stepPort;
	final protected int m_concurrencyPort;
	protected boolean m_isServerRunning = false;		//this boolean should be used in child classes to determine 
													//if the server should keep waiting for connections or not
	protected SubServer[] m_clients;					//this is a list of all clients connected to the server
	
	public Server(int stepPort, int concurrencyPort) throws IOException
	{
		this.m_stepPort = stepPort;
		this.m_concurrencyPort = concurrencyPort;
		this.m_stepServerSocket = new ServerSocket(stepPort);
		this.m_concurrencyServerSocket = new ServerSocket(concurrencyPort);
	}
	
	final protected Socket acceptAStepConnection() throws IOException
	{
		return this.m_stepServerSocket.accept();
	}
	
	final protected Socket acceptAConcurrencyConnection() throws IOException
	{
		return this.m_concurrencyServerSocket.accept();
	}
	
	@Override
	public void run()
	{
		this.m_isServerRunning = true;
		while (this.m_isServerRunning)
		{
			try
			{
				Socket aClientConnection = this.m_stepServerSocket.accept();
				Socket aClientConcurrencyConnection = this.m_concurrencyServerSocket.accept();
				SubServer aSubServer = assignClientToSubServer(aClientConnection, aClientConcurrencyConnection);
				addSubServerToList(aSubServer);
				aSubServer.start();
			} catch (IOException e)
			{
				//ignore because we should never reach this block. we never close the server socket
				//while it is accepting
			} catch (NoSuchElementException e)
			{
				//ignore - this means client is not communicating properly, so we don't care
			}
		}
	}
	
	abstract protected SubServer assignClientToSubServer(Socket aClientConnection, Socket aClientConcurrencyConnection) throws IOException;
	
	final protected void addSubServerToList(SubServer aSubServer)
	{
		boolean addedSubServer = false;
		//go through the whole list
		for (int listIndex = 0; listIndex < this.m_clients.length; listIndex++)
		{
			//look for an empty spot in the list
			if (this.m_clients[listIndex] == null)
			{
				//assign the sub-server to that spot
				aSubServer.setSubServerID(listIndex);
				this.m_clients[listIndex] = aSubServer;
				addedSubServer = true;
				break;
			}
		}
		//if we could not find a spot, then we have to reject the client, unfortunately
		if (!addedSubServer)
		{
			aSubServer.rejectDueToMaximumClients();
		}
	}
	
	/**
	 * returns a pointer to the <code>SubServer</code> serving the client with the given username
	 * 
	 * @param username			a String, the username to look for
	 * @return					a pointer to the <code>SubServer</code> with the given username or null
	 * 							if it wasn't found
	 */
	final protected SubServer locateSubServer(String username)
	{
		for (int clientIndex = 0; clientIndex < this.m_clients.length; clientIndex++)
		{
			SubServer aClient = this.m_clients[clientIndex];
			if (aClient != null)
			{
				if (aClient.getUsername().equals(username))
				{
					return aClient;
				}
			}
		}
		return null;
	}
	
	final public boolean isServerRunning()
	{
		return this.m_isServerRunning;
	}
	
	abstract protected boolean isClientLoggedIn(String username);
	
	//this method causes this object to stop accepting clients and stops the thread
	final public void closeAndStop()
	{
		//close all client connections
		for (int clientIndex = 0; clientIndex < this.m_clients.length; clientIndex++)
		{
			if (this.m_clients[clientIndex] != null)
			{
				this.m_clients[clientIndex].closeAndStop(MESSAGES.CONNECTION.CLOSING_SERVER_SIDE);
			}
		}
		//if the server has been started, we need to do something about the accept() that is blocking
		if (this.m_isServerRunning)
		{
			try
			{
				//set up a connection to this server so the serverSocket.accept() method stops blocking
				this.m_isServerRunning = false;
				Socket stepConnectionSocket = new Socket(LOCALHOST, this.m_stepPort);
				Socket concurrentConnectionSocket = new Socket(LOCALHOST, this.m_concurrencyPort);
				stepConnectionSocket.close();
				concurrentConnectionSocket.close();
			} catch (IOException e)
			{
				//ignore, the try body is just to attempt to end a blocking accept() method. if the connection is
				//refused, it just means the accept() method is no longer blocking, so both ways, we have achieved
				//what we want.
			}
		}
		try
		{
			this.m_stepServerSocket.close();
			this.m_concurrencyServerSocket.close();
		} catch (IOException e)
		{
			//ignore, because if it's already closed, then we're still fine
		}
	}
	
	/**
	 * Removes a client from the list.
	 * 
	 * @param indexOfClient			an integer, the ID of the client, or where in the list of clients it is
	 */
	final public void removeAClient(int indexOfClient)
	{
		this.m_clients[indexOfClient] = null;
	}
	
	/**
	 * Template class for a sub-server that is dealing with messages to and from a single client
	 *
	 */
	abstract protected class SubServer extends Thread
	{
		final protected boolean WENT_ONLINE = true;
		final protected boolean WENT_OFFLINE = false;
		
		final protected Socket m_s;
		final protected DataInputStream m_in;
		final protected DataOutputStream m_out;
		final protected String m_ip;
		protected int m_subServerID = -1;
		protected String m_username;
		protected boolean loggedIn = false;
		
		protected Socket m_concurrencySocket;
		protected DataOutputStream m_concurrencyOut;
		
		public SubServer(Socket s, Socket concurrentConnection) throws IOException
		{
			this.m_s = s;
			this.m_in = new DataInputStream(s.getInputStream());
			this.m_out = new DataOutputStream(s.getOutputStream());
			this.m_ip = this.m_s.getInetAddress().toString();
			setUpConcurrency(concurrentConnection);
		}
		
		final public String getUsername()
		{
			return this.m_username;
		}
		
		final public boolean isLoggedIn()
		{
			return this.loggedIn;
		}
		
		final public int getSubServerID()
		{
			return this.m_subServerID;
		}
		
		final protected void setSubServerID(int id)
		{
			this.m_subServerID = id;
		}
		
		@Override
		public void run()
		{
			while (true)
			{
				try
				{
					String aMessageFromClient = read();
					try
					{
						decode(aMessageFromClient);
					} catch (ConnectionEndedException e)
					{
						//log an error and close connections
						if (e.getErrorID() == ConnectionEndedException.BAD_CONNECTION_PASSWORD)
						{
							CommonMethods.logConnectionMessage(Text.SUBSERVER.CONNECTION_ENDED_BAD_CONNECTION_PASSWORD_LOG_MESSAGE);
						} else if (e.getErrorID() == ConnectionEndedException.IOEXCEPTION)
						{
							CommonMethods.logConnectionMessage(Text.SUBSERVER.CONNECTION_ENDED_IO_EXCEPTION_LOG_MESSAGE);
						} else
						{
							CommonMethods.logConnectionMessage(Text.SUBSERVER.CONNECTION_ENDED_UNKNOWN_LOG_MESSAGE);
						}
						ServerUIListener.emergencyStop();
						break;
					} catch (ServerSideErrorException e)
					{
						if (e.getErrorID() == ServerSideErrorException.DATABASE_MISCOMMUNICATION_ERROR)
						{
							closeAndStop(MESSAGES.CONNECTION.CLOSING_SERVER_SIDE_ERROR);
							break;
						}
					} catch (TamperedClientException e)
					{
						try
						{
							closeBadConnection(this.m_username);
							break;
						} catch (ConnectionEndedException e2)
						{
							if (e2.getErrorID() == ConnectionEndedException.IOEXCEPTION)
							{
								CommonMethods.logConnectionMessage(Text.SUBSERVER.CONNECTION_ENDED_IO_EXCEPTION_LOG_MESSAGE);
							} else
							{
								CommonMethods.logConnectionMessage(Text.SUBSERVER.CONNECTION_ENDED_UNKNOWN_LOG_MESSAGE);
							}
						}
					}
				} catch (IOException e)
				{
					//exit this loop if we lose connection with the client. s/he will just have to reconnect
					closeAndStop();
					break;
				} 
			}
			handleTerminatingConnection();
		}
		
		abstract protected void handleTerminatingConnection();
		
		final protected void login(String username, String password) throws ConnectionEndedException, ServerSideErrorException
		{
			this.m_username = username;
			//make sure the user is not already logged in
			if (Server.this.isClientLoggedIn(username) == false)
			{
				//try to get the user information from the database
				String message = database.MESSAGES.USERDATA.HEADING + database.MESSAGES.DELIMITER + database.MESSAGES.USERDATA.IS_LOGIN_OKAY + database.MESSAGES.DELIMITER + username + database.MESSAGES.DELIMITER + password;
				String response = DatabaseConnection.sendMessageAndGetResponse(message);
				if (response.equals(database.MESSAGES.USERDATA.LOGIN_IS_OKAY))
				{
					write(MESSAGES.GENERAL.LOGIN_SUCCESS);
					this.loggedIn = true;
				} else if (response.equals(database.MESSAGES.USERDATA.LOGIN_NOT_OKAY + database.MESSAGES.DELIMITER + database.MESSAGES.USERDATA.NONEXISTING_USER_ERROR_CODE))
				{
					write(MESSAGES.GENERAL.LOGIN_FAILED + MESSAGES.DELIMITER + MESSAGES.GENERAL.BAD_USER_INFORMATION_ERROR_CODE);
					closeBadConnection(username);
					CommonMethods.logSuspiciousMessage(Text.MAINSERVER.getFailedLoginLogMessage(username, password, this.m_ip));
				} else if (response.equals(database.MESSAGES.USERDATA.LOGIN_NOT_OKAY + database.MESSAGES.DELIMITER + database.MESSAGES.USERDATA.BAD_USER_PASSWORD_ERROR_CODE))
				{
					write(MESSAGES.GENERAL.LOGIN_FAILED + MESSAGES.DELIMITER + MESSAGES.GENERAL.BAD_USER_INFORMATION_ERROR_CODE);
					closeBadConnection(username);
					CommonMethods.logSuspiciousMessage(Text.MAINSERVER.getFailedLoginLogMessage(username, password, this.m_ip));
				} else
				{
					throw generateDatabaseMiscommunicationError(response);
				}
			} else
			{
				write(MESSAGES.GENERAL.LOGIN_FAILED + MESSAGES.DELIMITER + MESSAGES.GENERAL.ALREADY_LOGGED_IN_ERROR_CODE);
				CommonMethods.logSuspiciousMessage(Text.MAINSERVER.getMultiLoginLogMessage(username, password, this.m_ip));
			}
		}
		
		final protected void setUpConcurrency(Socket s) throws IOException
		{
			this.m_concurrencySocket = s;
			this.m_concurrencyOut = new DataOutputStream(s.getOutputStream());
		}
		
		protected String read() throws IOException
		{
			String message = this.m_in.readUTF();
			return message;
		}
		
		abstract protected void decode(String messageFromClient) throws ConnectionEndedException, ServerSideErrorException, TamperedClientException, NoSuchElementException;
		
		/**
		 * gets the display name of another user. used by the room servers only
		 *  
		 * @param targetUsername
		 * @return
		 * @throws ConnectionEndedException
		 */
		final protected String getDisplayNameOfOtherUserInRoom(String requesterUsername, String targetUsername) throws ConnectionEndedException
		{
			String messageToDatabase = database.MESSAGES.ROOMDATA.HEADING + database.MESSAGES.DELIMITER + database.MESSAGES.ROOMDATA.ROOMSERVER.ROOM_SERVER_HEADING + database.MESSAGES.DELIMITER + database.MESSAGES.ROOMDATA.ROOMSERVER.GET_DISPLAY_NAME_OF_USER_IN_ROOM + MESSAGES.DELIMITER
					+ requesterUsername + database.MESSAGES.DELIMITER + targetUsername;
			String response = DatabaseConnection.sendMessageAndGetResponse(messageToDatabase);
			//look through the response
			Scanner scanResponse = new Scanner(response);
			//first part should be result
			String result = scanResponse.next();
			if (result.equals(database.MESSAGES.ROOMDATA.ROOMSERVER.GET_DISPLAY_NAME_OF_USER_IN_ROOM_SUCCESS))
			{
				String displayName = scanResponse.next();
				return displayName;
			}
			//if not successful, then return unknown
			return "UNKNOWN";
		}
		
		final protected void sendDisplayNameOfOtherUser(String username, String password, String targetUsername) throws ConnectionEndedException, ServerSideErrorException, TamperedClientException
		{
			assertUsernameIsCorrect(username);
			//try to get the display name from the database
			String message = database.MESSAGES.USERDATA.HEADING + database.MESSAGES.DELIMITER + database.MESSAGES.USERDATA.GET_DISPLAY_NAME_GIVEN_USERNAME + database.MESSAGES.DELIMITER 
					+ username + database.MESSAGES.DELIMITER + password + database.MESSAGES.DELIMITER + targetUsername;
			String response = DatabaseConnection.sendMessageAndGetResponse(message);
			//look through the response
			Scanner scanResponse = new Scanner(response);
			//the first part should be the result
			String result = scanResponse.next();
			if (result.equals(database.MESSAGES.USERDATA.GET_DISPLAY_NAME_OF_OTHER_USER_SUCCESS))
			{
				//if success, then the display name should follow
				String displayName = scanResponse.next();
				//and let the client know the display name
				write(MESSAGES.GENERAL.GET_DISPLAY_NAME_OF_OTHER_USER_SUCCESS + MESSAGES.DELIMITER + displayName);
			} else if (result.equals(database.MESSAGES.USERDATA.GET_DISPLAY_NAME_OF_OTHER_USER_FAILED))
			{
				//if failed, just let the client know.
				//we are not figuring out error codes here because the client could get inundated
				//with error messages if a ton of these requests fail.
				write(MESSAGES.GENERAL.GET_DISPLAY_NAME_OF_OTHER_USER_FAILED);
			} else
			{
				scanResponse.close();
				throw generateDatabaseMiscommunicationError(response);
			}
			scanResponse.close();
		}
		
		final protected void logUnknownClientMessage(String serverName, String message, String ip)
		{
			CommonMethods.logSuspiciousMessage(Text.SUBSERVER.getUnknownMessageLogMessage(serverName, message, ip));
		}
		
		protected void write(String message)
		{
			try
			{
				this.m_out.writeUTF(message);
			} catch (IOException e)
			{
				System.out.println("Failed to send: " + message);
				//ignore. if the message can't get sent, well, whatever - that's not very important
			}
		}
		
		protected void writeConcurrent(String message)
		{
			try
			{
				this.m_concurrencyOut.writeUTF(message);
			} catch (IOException e)
			{
				//ignore. not the end of the world if the message isn't sent
			}
		}
		
		final protected String snipHeading(String aString, String aHeading)
		{
			//copy the string and heading
			String stringToModify = aString;
			String heading = aHeading + MESSAGES.DELIMITER;
			//locate the heading
			for (int index = 0; index < aString.length(); index++)
			{
				if (stringToModify.substring(0, index).equals(heading))
				{
					//remove the heading
					stringToModify = stringToModify.substring(index, stringToModify.length());
					//we are done
					break;
				}
			}
			return stringToModify;
			//return the modified string
			
		}
		
		//this notifies a client that s/he can't be served because the maximum clients has been reached
		final public void rejectDueToMaximumClients()
		{
			write(MESSAGES.CONNECTION.CLOSING_MAXIMUM_CLIENTS_REACHED);
		}
		
		final protected ServerSideErrorException generateDatabaseMiscommunicationError(String invalidMessage)
		{
			return new ServerSideErrorException(ServerSideErrorException.DATABASE_MISCOMMUNICATION_ERROR, invalidMessage);
		}
		
		final public void closeAndStop()
		{
			try
			{
				this.m_in.close();
			} catch (IOException e)
			{
				//ignore - this must work.
			}
			try
			{
				this.m_out.close();
			} catch (IOException e)
			{
				//ignore - this must work.
			}
			try
			{
				this.m_s.close();
			} catch (IOException e)
			{
				//ignore - this must work
			}

			//no further worries about the read method blocking. when we close the input stream, it'll throw
			//an exception, which we happen to handle by breaking free of the reading from client loop.
		}
		
		final protected void closeAndStop(String closeReason)
		{
			try
			{
				this.m_concurrencyOut.writeUTF(closeReason);
			} catch (IOException e)
			{
				//ignore
			}
			closeAndStop();
		}
		
		//this method closes a connection to a client that has provided bad information 
		final protected void closeBadConnection(String username) throws ConnectionEndedException
		{
			closeAndStop();
			DatabaseConnection.sendMessageAndGetResponse(database.MESSAGES.USERDATA.HEADING + database.MESSAGES.DELIMITER + database.MESSAGES.USERDATA.UNLOAD_USER_INFORMATION + database.MESSAGES.DELIMITER + username);
			//no further worries about the read method blocking. when we close the input stream, it'll throw
			//an exception, which we happen to handle by breaking free of the reading from client loop.
		}
		
		final protected void closeClientPerformingUnauthorizedActions()
		{
			closeAndStop();
			CommonMethods.logSuspiciousMessage(Text.SUBSERVER.getClientPerformingUnauthorizedActions(this.m_ip));
		}
		
		final protected void assertUsernameIsCorrect(String username) throws TamperedClientException
		{
			if (!username.equals(this.m_username))
			{
				throw new TamperedClientException(TamperedClientException.INVALID_USERNAME, this.m_username, username, this.m_ip);
			}
		}
	}
}
