package net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import util.CommonMethods;
import util.Text;
import util.log.ConnectionLog;

import data.AccessDeniedException;
import data.User;

import managers.blockip.BlockIPManager;
import managers.roomdata.RoomDataManager;
import managers.roomdata.RoomListManager;
import managers.roomdata.RoomNotFoundException;
import managers.userdata.UserDataManager;
import managers.userdata.UserListManager;
import managers.userdata.UserNotFoundException;

final public class Networking extends Thread
{
	final public static int PORT = 9000;
	final public static int TIME_UNTIL_TIMEOUT = 10000;			//client will have 10 seconds to certify they are the whiteboard server
	final public static String CONNECTION_PASSWORD = "hi";		//this is the connection password. do not insert message delimiters into this!
	final public static String LOCALHOST = "127.0.0.1";			//this is the IP Address of the machine running this database server.
	
	//networking components
	final private ServerSocket m_serverSocket;
	private Socket m_socket;
	private DataInputStream m_in;
	private DataOutputStream m_out;
	
	public Networking() throws IOException
	{
		this.m_serverSocket = new ServerSocket(PORT);
	}
	
	private boolean isServerStarted = false;
	@Override
	final public synchronized void start()
	{
		super.start();
		this.isServerStarted = true;
	}
	
	private boolean m_isConnectedToAClient = false;
	@Override
	final public void run()
	{
		boolean foundValidIP = false;
		boolean foundValidConnectionPassword = false;
		//we are constantly either communicating with a client or searching for one if the
		//server is started.
		while (this.isServerStarted)
		{
			//as long as we are not connected to a client or we have not found a client with a valid IP 
			//found a client with a valid connection password, we must keep searching for one
			//but we have to check if the server is started here or not in case the user terminates the
			//networking components when we have not yet found a client
			String IPAddress = "127.0.0.1";
			while (this.isServerStarted && (!this.m_isConnectedToAClient || !foundValidIP || !foundValidConnectionPassword))
			{
				//begin looking for a new client. at the start, we have not found anything valid
				CommonMethods.logConnectionMessage(Text.NETWORKING.WAITING_FOR_A_CLIENT_LOG_MESSAGE);
				this.m_isConnectedToAClient = false;
				foundValidIP = false;
				foundValidConnectionPassword = false;
				//wait for a connection request
				try
				{
					Socket aConnection = acceptAClient();
					DataInputStream in = new DataInputStream(aConnection.getInputStream());
					DataOutputStream out = new DataOutputStream(aConnection.getOutputStream());
					this.m_isConnectedToAClient = true;
					//we will be waiting only 10 seconds for the next few messages
					aConnection.setSoTimeout(10000);		
					//now ask the client for it's IP Address
					if (this.m_isConnectedToAClient)
					{

						//now ask for the client's connection password
						try
						{
							requestIPAddress(out);
							IPAddress = getClientIPAddress(in);
							//make sure the IP is valid and it's not blocked
							if (BlockIPManager.isValidIPAddress(IPAddress) && !BlockIPManager.isIPInList(IPAddress))
							{
								//if the IP matches those conditions, then we've found a valid IP and can continue
								foundValidIP = true;
								notifyClientOfGoodIP(out);
							} else
							{
								notifyClientOfBadIP(out);
								CommonMethods.logConnectionMessage(Text.NETWORKING.getBadIPAddressLogMessage(IPAddress));
							}
						} catch (SocketTimeoutException e)
						{
							//log bad behavior if the client does not reply
							CommonMethods.logConnectionMessage(Text.NETWORKING.getUnresponsiveClientIpPhaseLogMessage(IPAddress));
						} catch (IOException e)
						{
							//just continue to the end because the client failed this test
							CommonMethods.logConnectionMessage(Text.NETWORKING.getConnectionLostIpPhaseLogMessage(IPAddress));
						}
						if (foundValidIP)
						{
							try
							{
								//ask the client for the connection password
								requestConnectionPassword(out);
								//read the response
								String connectionPassword = getClientConnectionPassword(in);
								//check to see if the connection password is valid
								if (connectionPassword.equals(CONNECTION_PASSWORD))
								{
									//if we've found a valid connection password
									foundValidConnectionPassword = true;
									//let the client know it's all good
									notifyClientOfGoodConnectionPassword(out);
									//we will communicate with this client
									setUpSocketConnection(aConnection, in, out);
									if (!IPAddress.equals(LOCALHOST))
									{
										CommonMethods.logConnectionMessage(Text.NETWORKING.getConnectionSuccessfulLogMessage(IPAddress));
										//now we can wait infinitely long for the whiteboard server to respond
										this.m_socket.setSoTimeout(0);
									}
								} else
								{
									//if we did not find a valid connection password, just continue
									notifyClientOfBadConnectionPassword(out);
									CommonMethods.logConnectionMessage(Text.NETWORKING.getBadConnectionPasswordLogMessage(IPAddress, connectionPassword));
								}
							} catch (SocketTimeoutException e)
							{
								//log bad behavior if the client does not respond
								CommonMethods.logConnectionMessage(Text.NETWORKING.getUnresponsiveClientConnectionPasswordPhaseLogMessage(IPAddress));
							} catch (IOException e)
							{
								//ignore, and just go to the end because the client failed this test
								CommonMethods.logConnectionMessage(Text.NETWORKING.getConnectionLostConnectionPasswordPhaseLogMessage(IPAddress));
							}
						}	
					}
				} catch (IOException e)
				{
					//ignore and keep trying - this catch clause is empty because the compiler forces us to catch it
					//we should never reach this clause, however.
				}	
			}
			while (this.isServerStarted && this.m_isConnectedToAClient)
			{
				try
				{
					//read messages from the client
					String message = read();
					decode(message);
				} catch (IOException e)
				{
					CommonMethods.logConnectionMessage(Text.NETWORKING.getConnectionLostWhiteboardServerLogMessage(IPAddress));
					//close networking devices
					closeClientConnection();
					//no longer connected to client - go back and look for a new one
					this.m_isConnectedToAClient = false;
				} catch (NoSuchElementException e)
				{
					CommonMethods.logConnectionMessage(Text.NETWORKING.getImproperCommunicationWhiteboardServerLogMessage(IPAddress));
				}
			}
		}
	}
	
	final private Socket acceptAClient() throws IOException
	{
		return this.m_serverSocket.accept();
	}
	
	final private void setUpSocketConnection(Socket aSocket, DataInputStream anInput, DataOutputStream anOutput) 
	{
		this.m_socket = aSocket;
		this.m_in = anInput;
		this.m_out = anOutput;
	}
	
	//this method asks the server for its IP Address
	@SuppressWarnings("static-method")	//we are not making this method static because the constructor should be
										//called before we use this method
	final public void requestIPAddress(DataOutputStream out) throws IOException
	{
		out.writeUTF(MESSAGES.CONNECTION.HEADING + MESSAGES.DELIMITER + MESSAGES.CONNECTION.REQUEST_IP);
	}
	
	final public String getClientIPAddress(DataInputStream in) throws IOException
	{
		//read the whole message
		String message = in.readUTF();
		//remove the connection heading
		String possibleIP = snipHeading(message, MESSAGES.CONNECTION.HEADING);
		//remove the return IP label
		possibleIP = snipHeading(possibleIP, MESSAGES.CONNECTION.RETURN_IP);
		//figure out the numerical IP
		String numericalIP = findIPAddress(possibleIP);
		//return the IP
		return numericalIP;
	}
	
	//this method looks for the "/" in the IP Address and returns just the numbers and decimals in an IP Address
	//if the given possible IP does not contain a valid IP Address, then we return null
	@SuppressWarnings("static-method")	//we are not making this method static because the constructor
										//should be called before this method is used
	final public String findIPAddress(String aPossibleIP)
	{
		for (int index = 0; index < aPossibleIP.length(); index++)
		{
			if (aPossibleIP.charAt(index) == '/')
			{
				String numericalIP = aPossibleIP.substring(index + 1, aPossibleIP.length());
				if (BlockIPManager.isValidIPAddress(numericalIP))
				{
					return numericalIP;
				}
				return null;
			}
		}
		return null;
	}
	
	/**
	 * This method sends a message to a client telling it that it has provided an unacceptable IP Address.
	 * 
	 * Suppressed Warnings:
	 * static-method: Although it is only accessed in the Networking class, we do not make this method static 
	 * because the constructor should be called before this this method is used.
	 * @param out The stream through which the message will be sent
	 * @throws IOException Thrown when the connection unexpectedly ends.
	 */
	@SuppressWarnings("static-method")
	final private void notifyClientOfBadIP(DataOutputStream out) throws IOException
	{
		out.writeUTF(MESSAGES.CONNECTION.HEADING + MESSAGES.DELIMITER + MESSAGES.CONNECTION.BAD_IP);
	}
	
	@SuppressWarnings("static-method")
	final private void notifyClientOfGoodIP(DataOutputStream out) throws IOException
	{
		out.writeUTF(MESSAGES.CONNECTION.HEADING + MESSAGES.DELIMITER + MESSAGES.CONNECTION.GOOD_IP);
	}
	
	//this method asks the server for its connection password
	@SuppressWarnings("static-method")
	final private void requestConnectionPassword(DataOutputStream out) throws IOException
	{
		out.writeUTF(MESSAGES.CONNECTION.HEADING + MESSAGES.DELIMITER + MESSAGES.CONNECTION.REQUEST_CONNECTION_PASSWORD);
	}
	
	//this method decodes the server's response after the database requests a connection password
	//and finds the connection password it provided
	final private String getClientConnectionPassword(DataInputStream in) throws IOException
	{
		//read the whole message
		String connectionPasswordResponse = in.readUTF();
		//remove the connection heading
		String connectionPassword = snipHeading(connectionPasswordResponse, MESSAGES.CONNECTION.HEADING);
		//remove the return connection password label
		connectionPassword = snipHeading(connectionPassword, MESSAGES.CONNECTION.RETURN_CONNECTION_PASSWORD);
		return connectionPassword;
	}
	
	@SuppressWarnings("static-method")
	final private void notifyClientOfBadConnectionPassword(DataOutputStream out) throws IOException
	{
		out.writeUTF(MESSAGES.CONNECTION.HEADING + MESSAGES.DELIMITER + MESSAGES.BAD_CONNECTION_PASSWORD);
	}
	
	@SuppressWarnings("static-method")
	final private void notifyClientOfGoodConnectionPassword(DataOutputStream out) throws IOException
	{
		out.writeUTF(MESSAGES.CONNECTION.HEADING + MESSAGES.DELIMITER + MESSAGES.CONNECTION.CONNECTION_ACCEPTED);
	}
	
	//this method decodes a message from the server or gives the message to
	//more specific decode methods for them to decode
	final private void decode(String copyOfMessage) throws NoSuchElementException
	{
		String message = copyOfMessage;
		//prepare to look through the message
		Scanner scanMessage = new Scanner(message);
		//first, we have to check the connection password
		String connectionPassword = scanMessage.next();
		if (connectionPassword.equals(CONNECTION_PASSWORD))
		{
			message = snipHeading(message, CONNECTION_PASSWORD);
		} else
		{
			write(MESSAGES.BAD_CONNECTION_PASSWORD);
			closeAndStop();
		} 
		//figure out what is the heading
		String heading = scanMessage.next();
		//remove the heading and send the rest of the message for further decoding
		if (heading.equals(MESSAGES.USERDATA.HEADING))
		{
			String partlyDecodedMessage = snipHeading(message, MESSAGES.USERDATA.HEADING);
			decodeUserData(partlyDecodedMessage);
		} else if (heading.equals(MESSAGES.ROOMDATA.HEADING))
		{
			String partlyDecodedMessage = snipHeading(message, MESSAGES.ROOMDATA.HEADING);
			decodeRoomData(partlyDecodedMessage);
		} else
		{
			ConnectionLog.log("Unexpected message: " + message);
		}
		scanMessage.close();
	}
	
	//If a message has a heading on it with the user data heading, it will be sent here for decoding
	final private void decodeUserData(String copyOfMessage) throws NoSuchElementException
	{
		String message = copyOfMessage;
		//prepare to look through the message
		Scanner scanMessage = new Scanner(message);
		//figure out what the whiteboard server wants (or if there is another heading, figure out which one it is)
		//if there was another heading, remove it for further decoding (we check for headings first)
		//if there was not another heading, then we are done (we check for command second)
		String nextPart = scanMessage.next();
		if (nextPart.equals(MESSAGES.USERDATA.MAINSERVER.MAIN_SERVER_HEADING))
		{
			String partlyDecodedMessage = snipHeading(message, MESSAGES.USERDATA.MAINSERVER.MAIN_SERVER_HEADING);
			decodeUserDataMainServer(partlyDecodedMessage);
		} else if (nextPart.equals(MESSAGES.USERDATA.MESSAGINGSERVER.MESSAGING_SERVER_HEADING))
		{
			String partlyDecodedMessage = snipHeading(message, MESSAGES.USERDATA.MESSAGINGSERVER.MESSAGING_SERVER_HEADING);
			decodeUserDataMessagingServer(partlyDecodedMessage);
		} else if (nextPart.equals(MESSAGES.USERDATA.LOAD_USER_INFORMATION))
		{
			//expect a username
			String username = scanMessage.next();
			loadUserInformation(username);
		} else if (nextPart.equals(MESSAGES.USERDATA.UNLOAD_USER_INFORMATION))
		{
			//expect a username
			String username = scanMessage.next();
			unloadUserInformation(username);
		} else if (nextPart.equals(MESSAGES.USERDATA.IS_LOGIN_OKAY))
		{
			//expect a username and a password
			String username = scanMessage.next();
			String password = scanMessage.next();
			sendLoginOkay(username, password);
		} else if (nextPart.equals(MESSAGES.USERDATA.GET_USER_DISPLAY_NAME))
		{
			//expect a username and password
			String username = scanMessage.next();
			String password = scanMessage.next();
			sendUserDisplayName(username, password);
		} else if (nextPart.equals(MESSAGES.USERDATA.GET_USER_NAME_CHANGE_CODE))
		{
			//expect a username and password
			String username = scanMessage.next();
			String password = scanMessage.next();
			sendUserNameChangeCode(username, password);
		} else if (nextPart.equals(MESSAGES.USERDATA.GET_DISPLAY_NAME_GIVEN_USERNAME))
		{
			//expect a username, a password and another username
			String username = scanMessage.next();
			String password = scanMessage.next();
			String targetUsername = scanMessage.next();
			sendDisplayNameOfOtherUser(username, password, targetUsername);
		} else if (nextPart.equals(MESSAGES.USERDATA.GET_NEW_NAME_CHANGE_CODE))
		{
			//expect a username and a password
			String username = scanMessage.next();
			String password = scanMessage.next();
			createAndSendNewNameChangeCode(username, password);
		}
		scanMessage.close();
	}
	
		//this method loads the data of a user, given the user's username
		//this method may refuse to do so if the user information the server provided is invalid
		final private void loadUserInformation(String username)
		{
			try
			{
				UserDataManager.loadUserData(username);
				write(MESSAGES.USERDATA.LOAD_USER_INFORMATION_SUCCESS);
			} catch (IOException e)
			{
				//if the file is not found, let the server know
				write(MESSAGES.USERDATA.LOAD_USER_INFORMATION_FAILED + MESSAGES.DELIMITER + MESSAGES.USERDATA.FILE_NOT_FOUND_ERROR_CODE);
			} catch (NoSuchElementException e)
			{
				//if the file is improperly saved/tampered with, let the server know
				write(MESSAGES.USERDATA.LOAD_USER_INFORMATION_FAILED + MESSAGES.DELIMITER + MESSAGES.USERDATA.FILE_CORRUPTED_ERROR_CODE);
			}
		}
		
		//this method removes the temporarily stored data of a user to make it easier on the server
		final private void unloadUserInformation(String username)
		{
			try
			{
				UserDataManager.unloadUserData(username);
				write(MESSAGES.USERDATA.UNLOAD_USER_INFORMATION_SUCCESS);
			} catch (UserNotFoundException badUsername)
			{
				write(MESSAGES.USERDATA.UNLOAD_USER_INFORMATION_FAILED + MESSAGES.DELIMITER + MESSAGES.USERDATA.NONEXISTING_USER_ERROR_CODE);
			}
		}
		
		//this method obtains the password given a username and sends it to the whiteboard server
		//this method may refuse to do so if the user information the server provided is invalid
		final private void sendLoginOkay(String username, String password)
		{
			try
			{
				String storedPassword = UserDataManager.getUserPassword(username);
				if (password.equals(storedPassword))
				{
					write(MESSAGES.USERDATA.LOGIN_IS_OKAY);
				} else
				{
					write(MESSAGES.USERDATA.LOGIN_NOT_OKAY + MESSAGES.DELIMITER + MESSAGES.USERDATA.BAD_USER_PASSWORD_ERROR_CODE);
				}
			} catch (UserNotFoundException badUsername)
			{
				write(MESSAGES.USERDATA.LOGIN_NOT_OKAY + MESSAGES.DELIMITER + MESSAGES.USERDATA.NONEXISTING_USER_ERROR_CODE);
			}
		}
		
		//this method obtains the display name given a username and sends it to the whiteboard server
		//this method may refuse to do so if the user information the server provided is invalid
		final private void sendUserDisplayName(String username, String password)
		{
			try
			{
				String displayName = UserDataManager.getUserDisplayName(username, password);
				write(MESSAGES.USERDATA.GET_USER_DISPLAY_NAME_SUCCESS + MESSAGES.DELIMITER + displayName);
			} catch (UserNotFoundException badUsername)
			{
				write(MESSAGES.USERDATA.GET_USER_DISPLAY_NAME_FAILED + MESSAGES.DELIMITER + MESSAGES.USERDATA.NONEXISTING_USER_ERROR_CODE);
			} catch (AccessDeniedException badPassword)
			{
				write(MESSAGES.USERDATA.GET_USER_DISPLAY_NAME_FAILED + MESSAGES.DELIMITER + MESSAGES.USERDATA.BAD_USER_PASSWORD_ERROR_CODE);
			}
		}
		
		//this method obtains the name change code given a username and sends it to the whiteboard server
		//this method may refuse to do so if the user information the server provided is invalid
		final private void sendUserNameChangeCode(String username, String password)
		{
			try
			{
				String nameChangeCode = UserDataManager.getUserNameChangeCode(username, password);
				write(MESSAGES.USERDATA.GET_USER_NAME_CHANGE_CODE_SUCCESS + MESSAGES.DELIMITER + nameChangeCode);
			} catch (UserNotFoundException badUsername)
			{
				write(MESSAGES.USERDATA.GET_USER_NAME_CHANGE_CODE_FAILED + MESSAGES.DELIMITER + MESSAGES.USERDATA.NONEXISTING_USER_ERROR_CODE);
			} catch (AccessDeniedException badPassword)
			{
				write(MESSAGES.USERDATA.GET_USER_NAME_CHANGE_CODE_FAILED + MESSAGES.DELIMITER + MESSAGES.USERDATA.BAD_USER_PASSWORD_ERROR_CODE);
			}
		}
		
		final private void sendDisplayNameOfOtherUser(String username, String password, String targetUsername)
		{
			try
			{
				String storedPassword = UserDataManager.getUserPassword(username);
				if (storedPassword.equals(password))
				{
					String rtnDisplayName = UserListManager.getDisplayNameOfUser(targetUsername);
					write(MESSAGES.USERDATA.GET_DISPLAY_NAME_OF_OTHER_USER_SUCCESS + MESSAGES.DELIMITER + rtnDisplayName);
				} else
				{
					write(MESSAGES.USERDATA.GET_DISPLAY_NAME_OF_OTHER_USER_FAILED + MESSAGES.DELIMITER + MESSAGES.USERDATA.BAD_USER_PASSWORD_ERROR_CODE);
				}
			} catch (UserNotFoundException badUsername)
			{
				if (badUsername.getTargetUsername().equals(username))
				{
					write(MESSAGES.USERDATA.GET_DISPLAY_NAME_OF_OTHER_USER_FAILED + MESSAGES.DELIMITER + MESSAGES.USERDATA.NONEXISTING_USER_ERROR_CODE);
				} else if (badUsername.getTargetUsername().equals(targetUsername))
				{
					write(MESSAGES.USERDATA.GET_DISPLAY_NAME_OF_OTHER_USER_FAILED + MESSAGES.DELIMITER + MESSAGES.USERDATA.NONEXISTING_TARGET_ERROR_CODE);
				} 
			}
		}
		
		final private void createAndSendNewNameChangeCode(String username, String password)
		{
			try
			{
				if (UserDataManager.isUserDataLoaded(username))
				{
					String nameChangeCode = UserDataManager.getAndSetNewNameChangeCode(username, password);
					write(MESSAGES.USERDATA.GET_NEW_NAME_CHANGE_CODE_SUCCESS + MESSAGES.DELIMITER + nameChangeCode);
				} else
				{
					try
					{
						UserDataManager.loadUserData(username);
						String nameChangeCode = UserDataManager.getAndSetNewNameChangeCode(username, password);
						write(MESSAGES.USERDATA.GET_NEW_NAME_CHANGE_CODE_SUCCESS + MESSAGES.DELIMITER + nameChangeCode);
						UserDataManager.unloadUserData(username);
					} catch (IOException e) 
					{
						write(MESSAGES.USERDATA.GET_NEW_NAME_CHANGE_CODE_FAILED + MESSAGES.USERDATA.NONEXISTING_USER_ERROR_CODE);
					}
					
				}
			} catch (UserNotFoundException badUsername)
			{
				write(MESSAGES.USERDATA.GET_NEW_NAME_CHANGE_CODE_FAILED + MESSAGES.USERDATA.NONEXISTING_USER_ERROR_CODE);
			} catch (AccessDeniedException badPassword)
			{
				write(MESSAGES.USERDATA.GET_NEW_NAME_CHANGE_CODE_FAILED + MESSAGES.USERDATA.BAD_USER_PASSWORD_ERROR_CODE);
			}
		}
	
		//If a message has a heading on it with the main server heading, 
		//it will be sent here for decoding
		final private void decodeUserDataMainServer(String copyOfMessage) throws NoSuchElementException
		{
			String message = copyOfMessage;
			//prepare to look through the message
			Scanner scanMessage = new Scanner(message);
			//there are no headings documented, so just see what the whiteboard server wants to do
			String nextPart = scanMessage.next();
			if (nextPart.equals(MESSAGES.USERDATA.MAINSERVER.REGISTER))
			{
				//expect a username and a password of a user to be registered
				String username = scanMessage.next();
				String password = scanMessage.next();
				registerUser(username, password);
			} else if (nextPart.equals(MESSAGES.USERDATA.MAINSERVER.SET_USER_PASSWORD))
			{
				//expect a username, the current user password and the new user password
				String username = scanMessage.next();
				String currentPassword = scanMessage.next();
				String newPassword = scanMessage.next();
				setUserPassword(username, currentPassword, newPassword);
			} else if (nextPart.equals(MESSAGES.USERDATA.MAINSERVER.SET_USER_DISPLAY_NAME))
			{
				//expect a username, the current user password, the current user name change code and a new display name
				String username = scanMessage.next();
				String password = scanMessage.next();
				String nameChangeCode = scanMessage.next();
				String newDisplayName = scanMessage.next();
				setUserDisplayName(username, password, nameChangeCode, newDisplayName);
			}
			scanMessage.close();
		}
		
			final private void registerUser(String username, String password)
			{
				//make sure the username is not already in user
				if (UserListManager.isUsernameAlreadyRegistered(username))
				{
					write(MESSAGES.USERDATA.MAINSERVER.REGISTER_FAILED + MESSAGES.DELIMITER + MESSAGES.USERDATA.MAINSERVER.USERNAME_IN_USER_ERROR);
				} else
				{
					try
					{
						UserDataManager.MainServer.createNewUser(username, password);
						UserListManager.addRegisteredUser(username, User.DEFAULT_DISPLAY_NAME);
						write(MESSAGES.USERDATA.MAINSERVER.REGISTER_SUCCESS);
					} catch (IOException e)
					{
						write(MESSAGES.USERDATA.MAINSERVER.REGISTER_FAILED + MESSAGES.DELIMITER + MESSAGES.USERDATA.MAINSERVER.IO_EXCEPTION);
					}
				}
			}
		
			//this method assumes the new password was checked by the whiteboard server and found to be
			//valid
			final private void setUserPassword(String username, String currentPassword, String newPassword)
			{
				try
				{
					UserDataManager.MainServer.setUserPassword(username, currentPassword, newPassword);
					write(MESSAGES.USERDATA.MAINSERVER.SET_USER_PASSWORD_SUCCESS);
				} catch (UserNotFoundException badUsername)
				{
					write(MESSAGES.USERDATA.MAINSERVER.SET_USER_PASSWORD_FAILED + MESSAGES.DELIMITER + MESSAGES.USERDATA.NONEXISTING_USER_ERROR_CODE);
				} catch (AccessDeniedException badPassword)
				{
					write(MESSAGES.USERDATA.MAINSERVER.SET_USER_PASSWORD_FAILED + MESSAGES.DELIMITER + MESSAGES.USERDATA.BAD_USER_PASSWORD_ERROR_CODE);
				}
			}
			
			//this method assumes the new display name was checked by the whiteboard server and found to be
			//valid
			final private void setUserDisplayName(String username, String password, String nameChangeCode, String newDisplayName)
			{
				try
				{
					UserDataManager.MainServer.setUserDisplayName(username, password, nameChangeCode, newDisplayName);
					UserListManager.changeDisplayNameOfUser(username, newDisplayName);
					write(MESSAGES.USERDATA.MAINSERVER.SET_USER_DISPLAY_NAME_SUCCESS);
				} catch (UserNotFoundException badUsername)
				{
					write(MESSAGES.USERDATA.MAINSERVER.SET_USER_DISPLAY_NAME_FAILED + MESSAGES.DELIMITER + MESSAGES.USERDATA.NONEXISTING_USER_ERROR_CODE);
				} catch (AccessDeniedException badInformation)
				{
					if (badInformation.getErrorCode() == AccessDeniedException.BAD_PASSWORD_ERROR)
					{
						write(MESSAGES.USERDATA.MAINSERVER.SET_USER_DISPLAY_NAME_FAILED + MESSAGES.DELIMITER + MESSAGES.USERDATA.BAD_USER_PASSWORD_ERROR_CODE);
					} else if (badInformation.getErrorCode() == AccessDeniedException.BAD_NAME_CHANGE_CODE_ERROR)
					{
						write(MESSAGES.USERDATA.MAINSERVER.SET_USER_DISPLAY_NAME_FAILED + MESSAGES.DELIMITER + MESSAGES.USERDATA.MAINSERVER.INVALID_NAME_CHANGE_CODE_ERROR_CODE);
					}
				}
			}
			
		//if a message has a heading on it with the messaging server heading, 
		//it will be sent here for decoding
		final private void decodeUserDataMessagingServer(String copyOfMessage) throws NoSuchElementException
		{
			String message = copyOfMessage;
			//prepare to look through the message
			Scanner scanMessage = new Scanner(message);
			//there are no more headings, so figure out what the whiteboard server wants to do
			String nextPart = scanMessage.next();
			if (nextPart.equals(MESSAGES.USERDATA.MESSAGINGSERVER.ADD_FRIEND))
			{
				//expect a username, a password, and a target friend
				String username = scanMessage.next();
				String password = scanMessage.next();
				String friendUsername = scanMessage.next();
				addUserFriend(username, password, friendUsername);
			} else if (nextPart.equals(MESSAGES.USERDATA.MESSAGINGSERVER.REMOVE_FRIEND))
			{
				//expect a username, a password and a target friend
				String username = scanMessage.next();
				String password = scanMessage.next();
				String friendUsername = scanMessage.next();
				removeUserFriend(username, password, friendUsername);
			} else if (nextPart.equals(MESSAGES.USERDATA.MESSAGINGSERVER.ADD_PEST))
			{
				//expect a username, a password, and a target pest
				String username = scanMessage.next();
				String password = scanMessage.next();
				String pestUsername = scanMessage.next();
				addUserPest(username, password, pestUsername);
			} else if (nextPart.equals(MESSAGES.USERDATA.MESSAGINGSERVER.REMOVE_PEST))
			{
				//expect a username, a password, and a target pest
				String username = scanMessage.next();
				String password = scanMessage.next();
				String pestUsername = scanMessage.next();
				removeUserPest(username, password, pestUsername);
			} else if (nextPart.equals(MESSAGES.USERDATA.MESSAGINGSERVER.ADD_PM_HISTORY))
			{
				//expect the sender's username, the sender's password, the recipient's username,
				//and a message
				String senderUsername = scanMessage.next();
				String senderPassword = scanMessage.next();
				String recipientUsername = scanMessage.next();
				String messageContents = scanMessage.next();
				addUserPmHistory(senderUsername, senderPassword, recipientUsername, messageContents);
			} else if (nextPart.equals(MESSAGES.USERDATA.MESSAGINGSERVER.SET_CONVERSATION_READ))
			{
				//expect a username, a password and another username
				String username = scanMessage.next();
				String password = scanMessage.next();
				String otherUsernameInvolved = scanMessage.next();
				setPrivateMessagesRead(username, password, otherUsernameInvolved);
			} else if (nextPart.equals(MESSAGES.USERDATA.MESSAGINGSERVER.AM_I_ON_USER_FRIENDS_LIST))
			{
				//expect a username and another username
				String usernameToCheck = scanMessage.next();
				String usernameOfFriendsListOwner = scanMessage.next();
				sendIsOnFriendsListOfUser(usernameToCheck, usernameOfFriendsListOwner);
			} else if (nextPart.equals(MESSAGES.USERDATA.MESSAGINGSERVER.AM_I_ON_USER_PESTS_LIST))
			{
				//expect a username and another username
				String usernameToCheck = scanMessage.next();
				String usernameOfFriendsListOwner = scanMessage.next();
				sendIsOnPestsListOfUser(usernameToCheck, usernameOfFriendsListOwner);
			}  else if (nextPart.equals(MESSAGES.USERDATA.MESSAGINGSERVER.GET_USER_FRIENDS_LIST))
			{
				//expect a username and password
				String username = scanMessage.next();
				String password = scanMessage.next();
				sendUserFriendsList(username, password);
			} else if (nextPart.equals(MESSAGES.USERDATA.MESSAGINGSERVER.GET_USER_PESTS_LIST))
			{
				//expect a username and password
				String username = scanMessage.next();
				String password = scanMessage.next();
				sendUserPestsList(username, password);
			} else if (nextPart.equals(MESSAGES.USERDATA.MESSAGINGSERVER.GET_USER_PM_HISTORY))
			{
				//expect a username and password
				String username = scanMessage.next();
				String password = scanMessage.next();
				sendUserPmHistory(username, password);
			}
			scanMessage.close();
		}
			
			final private void addUserFriend(String username, String password, String friendUsername)
			{
				try
				{
					UserDataManager.MessagingServer.addUserFriend(username, password, friendUsername);
					write(MESSAGES.USERDATA.MESSAGINGSERVER.ADD_FRIEND_SUCCESS);
				} catch (UserNotFoundException badUsername)
				{
					if (badUsername.getTargetUsername().equals(username))
					{
						write(MESSAGES.USERDATA.MESSAGINGSERVER.ADD_FRIEND_FAILED + MESSAGES.DELIMITER + MESSAGES.USERDATA.NONEXISTING_USER_ERROR_CODE);
					} else if (badUsername.getTargetUsername().equals(friendUsername))
					{
						write(MESSAGES.USERDATA.MESSAGINGSERVER.ADD_FRIEND_FAILED + MESSAGES.DELIMITER + MESSAGES.USERDATA.MESSAGINGSERVER.NONEXISTING_TARGET_ERROR);
					} else
					{
						CommonMethods.logInternalMessage(Text.NETWORKING.getUnknownErrorLogMessage("addUserFriend(String username, String password, String friendUsername)"));
					}
				} catch (AccessDeniedException badPassword)
				{
					write(MESSAGES.USERDATA.MESSAGINGSERVER.ADD_FRIEND_FAILED + MESSAGES.DELIMITER + MESSAGES.USERDATA.BAD_USER_PASSWORD_ERROR_CODE);
				}
			}
			
			final private void removeUserFriend(String username, String password, String friendUsername)
			{
				try
				{
					UserDataManager.MessagingServer.removeUserFriend(username, password, friendUsername);
					write(MESSAGES.USERDATA.MESSAGINGSERVER.REMOVE_FRIEND_SUCCESS);
				} catch (UserNotFoundException badUsername)
				{
					if (badUsername.getTargetUsername().equals(username))
					{
						write(MESSAGES.USERDATA.MESSAGINGSERVER.REMOVE_FRIEND_FAILED + MESSAGES.DELIMITER + MESSAGES.USERDATA.NONEXISTING_USER_ERROR_CODE);
					} else if (badUsername.getTargetUsername().equals(friendUsername))
					{
						write(MESSAGES.USERDATA.MESSAGINGSERVER.REMOVE_FRIEND_FAILED + MESSAGES.DELIMITER + MESSAGES.USERDATA.NONEXISTING_TARGET_ERROR_CODE);
					}
				} catch (AccessDeniedException badPassword)
				{
					write(MESSAGES.USERDATA.MESSAGINGSERVER.REMOVE_FRIEND_FAILED + MESSAGES.DELIMITER + MESSAGES.USERDATA.BAD_USER_PASSWORD_ERROR_CODE);
				}
			}

			final private void addUserPest(String username, String password, String pestUsername)
			{
				try
				{
					UserDataManager.MessagingServer.addUserPest(username, password, pestUsername);
					write(MESSAGES.USERDATA.MESSAGINGSERVER.ADD_PEST_SUCCESS);
				} catch (UserNotFoundException badUsername)
				{
					if (badUsername.getTargetUsername().equals(username))
					{
						write(MESSAGES.USERDATA.MESSAGINGSERVER.ADD_PEST_FAILED + MESSAGES.DELIMITER + MESSAGES.USERDATA.NONEXISTING_USER_ERROR_CODE);
					} else if (badUsername.getTargetUsername().equals(pestUsername))
					{
						write(MESSAGES.USERDATA.MESSAGINGSERVER.ADD_PEST_FAILED + MESSAGES.DELIMITER + MESSAGES.USERDATA.MESSAGINGSERVER.NONEXISTING_TARGET_ERROR);
					} else
					{
						CommonMethods.logInternalMessage(Text.NETWORKING.getUnknownErrorLogMessage("addUserPest(String username, String password, String pestUsername)"));
					}
				} catch (AccessDeniedException badPassword)
				{
					write(MESSAGES.USERDATA.MESSAGINGSERVER.ADD_PEST_FAILED + MESSAGES.DELIMITER + MESSAGES.USERDATA.BAD_USER_PASSWORD_ERROR_CODE);
				}
			}
			
			final private void removeUserPest(String username, String password, String pestUsername)
			{
				try
				{
					UserDataManager.MessagingServer.removeUserPest(username, password, pestUsername);
					write(MESSAGES.USERDATA.MESSAGINGSERVER.REMOVE_PEST_SUCCESS);
				} catch (UserNotFoundException badUsername)
				{
					if (badUsername.getTargetUsername().equals(username))
					{
						write(MESSAGES.USERDATA.MESSAGINGSERVER.REMOVE_PEST_FAILED + MESSAGES.DELIMITER + MESSAGES.USERDATA.NONEXISTING_USER_ERROR_CODE);
					} else if (badUsername.getTargetUsername().equals(pestUsername))
					{
						write(MESSAGES.USERDATA.MESSAGINGSERVER.REMOVE_PEST_FAILED + MESSAGES.DELIMITER + MESSAGES.USERDATA.NONEXISTING_TARGET_ERROR_CODE);
					}
				} catch (AccessDeniedException badPassword)
				{
					write(MESSAGES.USERDATA.MESSAGINGSERVER.REMOVE_PEST_FAILED + MESSAGES.DELIMITER + MESSAGES.USERDATA.BAD_USER_PASSWORD_ERROR_CODE);
				}
			}
	
			final private void addUserPmHistory(String senderUsername, String senderPassword, String recipientUsername, String message)
			{
				try
				{
					UserDataManager.MessagingServer.addPmHistory(senderUsername, senderPassword, recipientUsername, message);
					write(MESSAGES.USERDATA.MESSAGINGSERVER.ADD_PM_HISTORY_SUCCESS);
				} catch (UserNotFoundException badUsername)
				{
					if (badUsername.getTargetUsername().equals(senderUsername))
					{
						write(MESSAGES.USERDATA.MESSAGINGSERVER.ADD_PM_HISTORY_FAILED + MESSAGES.DELIMITER + MESSAGES.USERDATA.NONEXISTING_USER_ERROR_CODE);
					} else if (badUsername.getTargetUsername().equals(recipientUsername))
					{
						write(MESSAGES.USERDATA.MESSAGINGSERVER.ADD_PM_HISTORY_FAILED + MESSAGES.DELIMITER + MESSAGES.USERDATA.MESSAGINGSERVER.NONEXISTING_TARGET_ERROR);
					} else
					{
						CommonMethods.logInternalMessage(Text.NETWORKING.getUnknownErrorLogMessage("addUserPmHistory(String username, String password, String otherUsernameInvolved, String senderUsername, String message)"));
					}
				} catch (AccessDeniedException badPassword)
				{
					write(MESSAGES.USERDATA.MESSAGINGSERVER.ADD_PM_HISTORY_FAILED + MESSAGES.DELIMITER + MESSAGES.USERDATA.BAD_USER_PASSWORD_ERROR_CODE);
				}
			}
			
			final private void setPrivateMessagesRead(String username, String password, String otherUsernameInvolved)
			{
				try
				{
					UserDataManager.MessagingServer.setPrivateMessagesRead(username, password, otherUsernameInvolved);
					write(MESSAGES.USERDATA.MESSAGINGSERVER.SET_CONVERSATION_READ_SUCCESS);
				} catch (UserNotFoundException badUsername)
				{
					if (badUsername.getTargetUsername().equals(username))
					{
						write(MESSAGES.USERDATA.MESSAGINGSERVER.SET_CONVERSATION_READ_FAILED + MESSAGES.DELIMITER + MESSAGES.USERDATA.NONEXISTING_USER_ERROR_CODE);
					} else if (badUsername.getTargetUsername().equals(otherUsernameInvolved))
					{
						write(MESSAGES.USERDATA.MESSAGINGSERVER.SET_CONVERSATION_READ_FAILED + MESSAGES.DELIMITER + MESSAGES.USERDATA.MESSAGINGSERVER.NONEXISTING_TARGET_ERROR);
					}
				} catch (AccessDeniedException badPassword)
				{
					write(MESSAGES.USERDATA.MESSAGINGSERVER.SET_CONVERSATION_READ_FAILED + MESSAGES.DELIMITER + MESSAGES.USERDATA.BAD_USER_PASSWORD_ERROR_CODE);
				}
			}
			
			final private void sendIsOnFriendsListOfUser(String usernameToCheck, String usernameOfOwnerOfFriendsList)
			{
				try
				{
					boolean result = UserDataManager.MessagingServer.isUsernameOnFriendsList(usernameOfOwnerOfFriendsList, usernameToCheck);
					write(MESSAGES.USERDATA.MESSAGINGSERVER.AM_I_ON_USER_FRIENDS_LIST_REQUEST_SUCCESS + MESSAGES.DELIMITER + result);
				} catch (UserNotFoundException badUsername)
				{
					if (badUsername.equals(usernameToCheck))
					{
						write(MESSAGES.USERDATA.MESSAGINGSERVER.AM_I_ON_USER_FRIENDS_LIST_REQUEST_FAILED + MESSAGES.DELIMITER + MESSAGES.USERDATA.NONEXISTING_USER_ERROR_CODE);
					} else if (badUsername.equals(usernameOfOwnerOfFriendsList))
					{
						write(MESSAGES.USERDATA.MESSAGINGSERVER.AM_I_ON_USER_FRIENDS_LIST_REQUEST_FAILED + MESSAGES.DELIMITER + MESSAGES.USERDATA.MESSAGINGSERVER.NONEXISTING_TARGET_ERROR);
					}
				}
			}
			
			final private void sendIsOnPestsListOfUser(String username, String usernameOfOwnerOfPestsList)
			{
				try
				{
					boolean result = UserDataManager.MessagingServer.isUsernameOnPestsList(usernameOfOwnerOfPestsList, username);
					write(MESSAGES.USERDATA.MESSAGINGSERVER.AM_I_ON_USER_PESTS_LIST_REQUEST_SUCCESS + MESSAGES.DELIMITER + result);
				} catch (UserNotFoundException badUsername)
				{
					if (badUsername.getTargetUsername().equals(username))
					{
						write(MESSAGES.USERDATA.MESSAGINGSERVER.AM_I_ON_USER_PESTS_LIST_REQUEST_FAILED + MESSAGES.DELIMITER + MESSAGES.USERDATA.NONEXISTING_USER_ERROR_CODE);
					} else if (badUsername.getTargetUsername().equals(usernameOfOwnerOfPestsList))
					{
						write(MESSAGES.USERDATA.MESSAGINGSERVER.AM_I_ON_USER_PESTS_LIST_REQUEST_FAILED + MESSAGES.DELIMITER + MESSAGES.USERDATA.MESSAGINGSERVER.NONEXISTING_TARGET_ERROR);
					}
				}
			}
			
			//this method obtains the friends list given a username and sends it to the whiteboard server
			//this method may refuse to do so if the user information the server provided is invalid
			final private void sendUserFriendsList(String username, String password)
			{
				try
				{
					ArrayList<String> friendsList = UserDataManager.getUserFriendsList(username, password);
					//create a single line of text that will represent all the usernames of all friends
					//start with nothing
					String listOfFriends;
					//the first part is the number of usernames of friends that will follow this number
					int numberOfFriends = friendsList.size();
					listOfFriends = numberOfFriends + MESSAGES.DELIMITER;
					//then we add the usernames of all friends
					for (int friendIndex = 0; friendIndex < numberOfFriends; friendIndex++)
					{
						listOfFriends += friendsList.get(friendIndex) + MESSAGES.DELIMITER;
					}
					write(MESSAGES.USERDATA.MESSAGINGSERVER.GET_USER_FRIENDS_LIST_SUCCESS + MESSAGES.DELIMITER + listOfFriends);
				} catch (UserNotFoundException badUsername)
				{
					write(MESSAGES.USERDATA.MESSAGINGSERVER.GET_USER_FRIENDS_LIST_FAILED + MESSAGES.DELIMITER + MESSAGES.USERDATA.NONEXISTING_USER_ERROR_CODE);
				} catch (AccessDeniedException badPassword)
				{
					write(MESSAGES.USERDATA.MESSAGINGSERVER.GET_USER_FRIENDS_LIST_FAILED + MESSAGES.DELIMITER + MESSAGES.USERDATA.BAD_USER_PASSWORD_ERROR_CODE);
				}
			}
			
			//this method obtains the pests list given a username and sends it to the whiteboard server
			//this method may refuse to do so if the user information provided was invalid
			final private void sendUserPestsList(String username, String password)
			{
				try
				{
					ArrayList<String> pestsList = UserDataManager.getUserPestsList(username, password);
					//create a single line of text that will represent the pests list
					//start with nothing
					String listOfPests = "";
					//the first part is the number of pests, which tells how many pests follow this number
					int numberOfPests = pestsList.size();
					listOfPests += numberOfPests + MESSAGES.DELIMITER;
					//then there is the list of space separated pests
					for (int pestIndex = 0; pestIndex < numberOfPests; pestIndex++)
					{
						listOfPests += pestsList.get(pestIndex) + MESSAGES.DELIMITER;
					}
					write(MESSAGES.USERDATA.MESSAGINGSERVER.GET_USER_PESTS_LIST_SUCCESS + MESSAGES.DELIMITER + listOfPests);
				} catch (UserNotFoundException badUsername)
				{
					write(MESSAGES.USERDATA.MESSAGINGSERVER.GET_USER_PESTS_LIST_FAILED + MESSAGES.DELIMITER + MESSAGES.USERDATA.NONEXISTING_USER_ERROR_CODE);
				} catch (AccessDeniedException badPassword)
				{
					write(MESSAGES.USERDATA.MESSAGINGSERVER.GET_USER_PESTS_LIST_FAILED + MESSAGES.DELIMITER + MESSAGES.USERDATA.BAD_USER_PASSWORD_ERROR_CODE);
				}
			}
			
			//this method obtains the private chat history given a username and sends it to the whiteboard server
			//this method may refuse to do so if the information provided was invalid
			final private void sendUserPmHistory(String username, String password)
			{
				try
				{
					ArrayList<UserDataManager.PrivateMessage> pmHistory = UserDataManager.getUserPmHistory(username, password);
					//create a single line that contains all the information of the pm history
					//start with nothing
					String listOfMessages = "";
					//the first part is the number of messages that follow the number
					int numberOfMessages = pmHistory.size();
					listOfMessages += numberOfMessages + MESSAGES.DELIMITER;
					//then we add all the message data, which is the username of the other person in the conversation
					//followed by the username of the sender of the message followed by the message itself
					for (int messageIndex = 0; messageIndex < numberOfMessages; messageIndex++)
					{
						UserDataManager.PrivateMessage aMessage = pmHistory.get(messageIndex);
						String messageData = aMessage.getSender() + MESSAGES.DELIMITER + aMessage.getRecipient() + MESSAGES.DELIMITER + aMessage.getMessage() + MESSAGES.DELIMITER + aMessage.isMessageRead();
						listOfMessages += messageData + MESSAGES.DELIMITER;
					}
					write(MESSAGES.USERDATA.MESSAGINGSERVER.GET_USER_PM_HISTORY_SUCCESS + MESSAGES.DELIMITER + listOfMessages);
				} catch (UserNotFoundException badUsername)
				{
					write(MESSAGES.USERDATA.MESSAGINGSERVER.GET_USER_PM_HISTORY_FAILED + MESSAGES.DELIMITER + MESSAGES.USERDATA.NONEXISTING_USER_ERROR_CODE);
				} catch (AccessDeniedException badPassword)
				{
					write(MESSAGES.USERDATA.MESSAGINGSERVER.GET_USER_PM_HISTORY_FAILED + MESSAGES.DELIMITER + MESSAGES.USERDATA.BAD_USER_PASSWORD_ERROR_CODE);
				}
			}
			
	//if a message has a heading on it with the room data heading, it will be sent here for decoding
	final private void decodeRoomData(String copyOfMessage) throws NoSuchElementException
	{
		String message = copyOfMessage;
		//prepare to look through the message
		Scanner scanMessage = new Scanner(message);
		//figure out what the whiteboard server wants (of if there is another heading)
		String nextPart = scanMessage.next();
		//we'll look for headings first. if there's a heading, figure out which one it is
		//then we'll look for commands. if there's a command, figure out what the database needs to do
		if (nextPart.equals(MESSAGES.ROOMDATA.ROOMDATASERVER.ROOM_DATA_SERVER_HEADING))
		{
			String partlyDecodedMessage = snipHeading(message, MESSAGES.ROOMDATA.ROOMDATASERVER.ROOM_DATA_SERVER_HEADING);
			decodeRoomDataRoomDataServer(partlyDecodedMessage);
		} else if (nextPart.equals(MESSAGES.ROOMDATA.ROOMSERVER.ROOM_SERVER_HEADING))
		{
			String partlyDecodedMessage = snipHeading(message, MESSAGES.ROOMDATA.ROOMSERVER.ROOM_SERVER_HEADING);
			decodeRoomDataRoomServer(partlyDecodedMessage);
		} else if (nextPart.equals(MESSAGES.ROOMDATA.GET_MAXIMUM_ROOMS_STORED))
		{
			sendMaximumRoomsStored();
		} else if (nextPart.equals(MESSAGES.ROOMDATA.LOAD_ROOM_DATA))
		{
			//expect a room ID
			int roomID = scanMessage.nextInt();
			loadRoom(roomID);
		} else if (nextPart.equals(MESSAGES.ROOMDATA.GET_EXISTING_ROOM_LIST))
		{
			sendExistingRoomList();
		} else if (nextPart.equals(MESSAGES.ROOMDATA.GET_ROOM_NAME))
		{
			//expect a room ID
			int roomID = scanMessage.nextInt();
			sendRoomName(roomID);
		} else if (nextPart.equals(MESSAGES.ROOMDATA.GET_ROOM_CREATOR_USERNAME))
		{
			//expect a room ID
			int roomID = scanMessage.nextInt();
			sendRoomCreatorName(roomID);
		} else if (nextPart.equals(MESSAGES.ROOMDATA.GET_ROOM_CREATION_DATE))
		{
			//expect a room ID
			int roomID = scanMessage.nextInt();
			sendRoomCreationDate(roomID);
		} else if (nextPart.equals(MESSAGES.ROOMDATA.GET_ROOM_MODIFICATION_PASSWORD))
		{
			//expect a room ID
			int roomID = scanMessage.nextInt();
			sendRoomModificationPassword(roomID);
		} else if (nextPart.equals(MESSAGES.ROOMDATA.GET_ROOM_PASSWORD_PROTECTION))
		{
			//expect a room ID
			int roomID = scanMessage.nextInt();
			sendRoomPasswordProtection(roomID);
		} else if (nextPart.equals(MESSAGES.ROOMDATA.GET_ROOM_JOIN_PASSWORD))
		{
			//expect a room ID
			int roomID = scanMessage.nextInt();
			sendRoomJoinPassword(roomID);
		} else if (nextPart.equals(MESSAGES.ROOMDATA.GET_WHITEBOARD_LENGTH))
		{
			//expect a room ID
			int roomID = scanMessage.nextInt();
			sendWhiteboardLength(roomID);
		} else if (nextPart.equals(MESSAGES.ROOMDATA.GET_WHITEBOARD_WIDTH))
		{
			//expect a room ID
			int roomID = scanMessage.nextInt();
			sendWhiteboardWidth(roomID);
		}
		scanMessage.close();
	}
	
		final private void sendMaximumRoomsStored()
		{
			write(MESSAGES.ROOMDATA.GET_MAXIMUM_ROOMS_STORED_SUCCESS + MESSAGES.DELIMITER + RoomDataManager.MAXIMUM_ROOMS);
		}
		
		final private void loadRoom(int roomID)
		{
			try
			{
				RoomDataManager.loadRoomData(roomID);
				write(MESSAGES.ROOMDATA.LOAD_ROOM_DATA_SUCCESS);
			} catch (IOException badFilename)
			{
				write(MESSAGES.ROOMDATA.LOAD_ROOM_DATA_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMDATA.FILE_NOT_FOUND_ERROR_CODE);
			} catch (NoSuchElementException fileCorrupted)
			{
				write(MESSAGES.ROOMDATA.LOAD_ROOM_DATA_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMDATA.FILE_CORRUPTED_ERROR_CODE);
			}
		}
		
		final private void sendExistingRoomList()
		{
			int numberOfExistingRooms = RoomListManager.getNumberOfExistingRooms();
			String listOfExistingRooms = "";
			for (int roomIndex = 0; roomIndex < RoomDataManager.MAXIMUM_ROOMS; roomIndex++)
			{
				if (RoomListManager.isRoomCreated(roomIndex))
				{
					listOfExistingRooms += roomIndex + MESSAGES.DELIMITER;
				}
			}
			write(MESSAGES.ROOMDATA.GET_EXISTRING_ROOM_LIST_SUCCESS + MESSAGES.DELIMITER + numberOfExistingRooms + MESSAGES.DELIMITER + listOfExistingRooms);
		}
		
		final private void sendRoomName(int roomID)
		{
			try
			{
				String roomName = RoomDataManager.getRoomName(roomID);
				write(MESSAGES.ROOMDATA.GET_ROOM_NAME_SUCCESS + MESSAGES.DELIMITER + roomName);
			} catch (RoomNotFoundException badRoomID)
			{
				write(MESSAGES.ROOMDATA.GET_ROOM_NAME_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMDATA.NONEXISTING_ROOM_ERROR_CODE);
			}
		}
		
		final private void sendRoomCreatorName(int roomID)
		{
			try
			{
				String creatorName = RoomDataManager.getRoomCreatorName(roomID);
				write(MESSAGES.ROOMDATA.GET_ROOM_CREATOR_USERNAME_SUCCESS + MESSAGES.DELIMITER + creatorName);
			} catch (RoomNotFoundException badRoomID)
			{
				write(MESSAGES.ROOMDATA.GET_ROOM_CREATOR_USERNAME_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMDATA.NONEXISTING_ROOM_ERROR_CODE);
			}
		}
		
		final private void sendRoomCreationDate(int roomID)
		{
			try
			{
				String creationDate = RoomDataManager.getRoomCreationDate(roomID);
				write(MESSAGES.ROOMDATA.GET_ROOM_CREATION_DATE_SUCCESS + MESSAGES.DELIMITER + creationDate);
			} catch (RoomNotFoundException badRoomID)
			{
				write(MESSAGES.ROOMDATA.GET_ROOM_CREATION_DATE_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMDATA.NONEXISTING_ROOM_ERROR_CODE);
			}
		}
	
		final private void sendRoomModificationPassword(int roomID)
		{
			try
			{
				String modificationPassword = RoomDataManager.getRoomModificationPassword(roomID);
				write(MESSAGES.ROOMDATA.GET_ROOM_MODIFICATION_PASSWORD_SUCCESS + MESSAGES.DELIMITER + modificationPassword);
			} catch (RoomNotFoundException badRoomID)
			{
				write(MESSAGES.ROOMDATA.GET_ROOM_MODIFICATION_PASSWORD_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMDATA.NONEXISTING_ROOM_ERROR_CODE);
			}
		}
		
		final private void sendRoomPasswordProtection(int roomID)
		{
			try
			{
				boolean passwordProtection = RoomDataManager.getRoomPasswordProtection(roomID);
				write(MESSAGES.ROOMDATA.GET_ROOM_PASSWORD_PROTECTION_SUCCESS + MESSAGES.DELIMITER + passwordProtection);
			} catch (RoomNotFoundException badRoomID)
			{
				write(MESSAGES.ROOMDATA.GET_ROOM_PASSWORD_PROTECTION_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMDATA.NONEXISTING_ROOM_ERROR_CODE);
			}
		}
		
		final private void sendRoomJoinPassword(int roomID)
		{
			try
			{
				String joinPassword = RoomDataManager.getRoomJoinPassword(roomID);
				write(MESSAGES.ROOMDATA.GET_ROOM_JOIN_PASSWORD_SUCCESS + MESSAGES.DELIMITER + joinPassword);
			} catch (RoomNotFoundException badRoomID)
			{
				write(MESSAGES.ROOMDATA.GET_ROOM_JOIN_PASSWORD_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMDATA.NONEXISTING_ROOM_ERROR_CODE);
			}
		}
		
		final private void sendWhiteboardLength(int roomID)
		{
			try
			{
				int length = RoomDataManager.getWhiteboardLength(roomID);
				write(MESSAGES.ROOMDATA.GET_WHITEBOARD_LENGTH_SUCCESS + MESSAGES.DELIMITER + length);
			} catch (RoomNotFoundException badRoomID)
			{
				write(MESSAGES.ROOMDATA.GET_WHITEBOARD_LENGTH_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMDATA.NONEXISTING_ROOM_ERROR_CODE);
			}
		}
		
		final private void sendWhiteboardWidth(int roomID)
		{
			try
			{
				int width = RoomDataManager.getWhiteboardWidth(roomID);
				write(MESSAGES.ROOMDATA.GET_WHITEBOARD_WIDTH_SUCCESS + MESSAGES.DELIMITER + width);
			} catch (RoomNotFoundException badRoomID)
			{
				write(MESSAGES.ROOMDATA.GET_WHITEBOARD_WIDTH_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMDATA.NONEXISTING_ROOM_ERROR_CODE);
			}
		}
		

		
		//if a message has a heading on it with the room data server heading,
		//it will be sent here for decoding
		final private void decodeRoomDataRoomDataServer(String copyOfMessage) throws NoSuchElementException
		{
			String message = copyOfMessage;
			//prepare to look through the message
			Scanner scanMessage = new Scanner(message);
			//there are no headings left, so figure out what the whiteboard server wants to do
			String nextPart = scanMessage.next();
			if (nextPart.equals(MESSAGES.ROOMDATA.ROOMDATASERVER.SET_ROOM_NAME))
			{
				//expect a room ID, the modification password, and a new room name
				int roomID = scanMessage.nextInt();
				String modificationPassword = scanMessage.next();
				String newRoomName = scanMessage.next();
				setRoomName(roomID, modificationPassword, newRoomName);
			} else if (nextPart.equals(MESSAGES.ROOMDATA.ROOMDATASERVER.SET_ROOM_MODIFICATION_PASSWORD))
			{
				//expect a room ID, the old modification password, and a new modification password
				int roomID = scanMessage.nextInt();
				String oldModificationPassword = scanMessage.next();
				String newModificationPassword = scanMessage.next();
				setRoomModificationPassword(roomID, oldModificationPassword, newModificationPassword);
			} else if (nextPart.equals(MESSAGES.ROOMDATA.ROOMDATASERVER.SET_ROOM_PASSWORD_PROTECTION))
			{
				//expect a room ID, the modification password, and a password protection boolean
				int roomID = scanMessage.nextInt();
				String modificationPassword = scanMessage.next();
				boolean passwordProtection = scanMessage.nextBoolean();
				setRoomPasswordProtection(roomID, modificationPassword, passwordProtection);
			} else if (nextPart.equals(MESSAGES.ROOMDATA.ROOMDATASERVER.SET_ROOM_JOIN_PASSWORD))
			{
				//expect a room ID, the modification password, the old join password and the new join password
				int roomID = scanMessage.nextInt();
				String modificationPassword = scanMessage.next();
				String oldJoinPassword = scanMessage.next();
				String newJoinPassword = scanMessage.next();
				setRoomJoinPassword(roomID, modificationPassword, oldJoinPassword, newJoinPassword);
			} else if (nextPart.equals(MESSAGES.ROOMDATA.ROOMDATASERVER.CREATE_ROOM))
			{
				int roomID = scanMessage.nextInt();
				String roomName = scanMessage.next();
				String creatorName = scanMessage.next();
				String creationDate = scanMessage.next();
				String modificationPassword = scanMessage.next();
				boolean passwordProtection = scanMessage.nextBoolean();
				String joinPassword = scanMessage.next();
				int whiteboardLength = scanMessage.nextInt();
				int whiteboardWidth = scanMessage.nextInt();
				createRoom(roomID, roomName, creatorName, creationDate, modificationPassword, passwordProtection, joinPassword, whiteboardLength, whiteboardWidth);
			}
			scanMessage.close();
		}
		
			final private void setRoomName(int roomID, String modificationPassword, String newRoomName)
			{
				try
				{
					RoomDataManager.RoomDataServer.setRoomName(roomID, modificationPassword, newRoomName);
					write(MESSAGES.ROOMDATA.ROOMDATASERVER.SET_ROOM_NAME_SUCCESS);
				} catch (RoomNotFoundException badRoomID)
				{
					write(MESSAGES.ROOMDATA.ROOMDATASERVER.SET_ROOM_NAME_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMDATA.NONEXISTING_ROOM_ERROR_CODE);
				} catch (AccessDeniedException badPassword)
				{
					write(MESSAGES.ROOMDATA.ROOMDATASERVER.SET_ROOM_NAME_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMDATA.INVALID_MODIFICATION_PASSWORD_ERROR_CODE);
				}
			}
			
			final private void setRoomModificationPassword(int roomID, String oldModificationPassword, String newModificationPassword)
			{
				try
				{
					RoomDataManager.RoomDataServer.setRoomModificationPassword(roomID, oldModificationPassword, newModificationPassword);
					write(MESSAGES.ROOMDATA.ROOMDATASERVER.SET_ROOM_MODIFICATION_PASSWORD_SUCCESS);
				} catch (RoomNotFoundException badRoomID)
				{
					write(MESSAGES.ROOMDATA.ROOMDATASERVER.SET_ROOM_MODIFICATION_PASSWORD_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMDATA.NONEXISTING_ROOM_ERROR_CODE);
				} catch (AccessDeniedException badPassword)
				{
					write(MESSAGES.ROOMDATA.ROOMDATASERVER.SET_ROOM_MODIFICATION_PASSWORD_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMDATA.INVALID_MODIFICATION_PASSWORD_ERROR_CODE);
				}
			}
			
			final private void setRoomPasswordProtection(int roomID, String modificationPassword, boolean passwordProtection)
			{
				try
				{
					RoomDataManager.RoomDataServer.setRoomPasswordProtection(roomID, modificationPassword, passwordProtection);
					write(MESSAGES.ROOMDATA.ROOMDATASERVER.SET_ROOM_PASSWORD_PROTECTION_SUCCESS);
				} catch (RoomNotFoundException badRoomID)
				{
					write(MESSAGES.ROOMDATA.ROOMDATASERVER.SET_ROOM_PASSWORD_PROTECTION_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMDATA.NONEXISTING_ROOM_ERROR_CODE);
				} catch (AccessDeniedException badPassword)
				{
					write(MESSAGES.ROOMDATA.ROOMDATASERVER.SET_ROOM_PASSWORD_PROTECTION_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMDATA.INVALID_MODIFICATION_PASSWORD_ERROR_CODE);
				}
			}
			
			final private void setRoomJoinPassword(int roomID, String modificationPassword, String oldJoinPassword, String newJoinPassword)
			{
				try
				{
					RoomDataManager.RoomDataServer.setRoomJoinPassword(roomID, modificationPassword, oldJoinPassword, newJoinPassword);
					write(MESSAGES.ROOMDATA.ROOMDATASERVER.SET_ROOM_JOIN_PASSWORD_SUCCESS);
				} catch (RoomNotFoundException badRoomID)
				{
					write(MESSAGES.ROOMDATA.ROOMDATASERVER.SET_ROOM_JOIN_PASSWORD_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMDATA.NONEXISTING_ROOM_ERROR_CODE);
				} catch (AccessDeniedException badPassword)
				{
					if (badPassword.getErrorCode() == AccessDeniedException.BAD_MODIFICATION_PASSWORD_ERROR)
					{
						write(MESSAGES.ROOMDATA.ROOMDATASERVER.SET_ROOM_JOIN_PASSWORD_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMDATA.INVALID_MODIFICATION_PASSWORD_ERROR_CODE);
					} else if (badPassword.getErrorCode() == AccessDeniedException.BAD_JOIN_PASSWORD_ERROR)
					{
						write(MESSAGES.ROOMDATA.ROOMDATASERVER.SET_ROOM_JOIN_PASSWORD_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMDATA.INVALID_JOIN_PASSWORD_ERROR_CODE);
					} else
					{
						CommonMethods.logInternalMessage(Text.NETWORKING.getUnknownErrorLogMessage("setRoomJoinPassword(...)"));
					}
				}
			}
			
			final private void createRoom(int roomID, String roomName, String creatorName, String creationDate, String modificationPassword, boolean passwordProtection, String joinPassword, int whiteboardLength, int whiteboardWidth)
			{
				if (RoomDataManager.isRoomIDUsed(roomID))
				{
					write(MESSAGES.ROOMDATA.ROOMDATASERVER.CREATE_ROOM_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMDATA.ROOMDATASERVER.ROOM_ID_TAKEN_ERROR_CODE);
				} else
				{
					try
					{
						RoomDataManager.createRoom(roomID, roomName, creatorName, creationDate, modificationPassword, passwordProtection, joinPassword, whiteboardLength, whiteboardWidth);
						write(MESSAGES.ROOMDATA.ROOMDATASERVER.CREATE_ROOM_SUCCESS);
					} catch (IOException e)
					{
						write(MESSAGES.ROOMDATA.ROOMDATASERVER.CREATE_ROOM_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMDATA.ROOMDATASERVER.IO_EXCEPTION_ERROR_CODE);
					}
				}
			}
		
		//if a message has a heading on it with the room server heading,
		//it will be sent here for decoding
		final private void decodeRoomDataRoomServer(String copyOfMessage) throws NoSuchElementException
		{
			String message = copyOfMessage;
			//prepare to look through the message
			Scanner scanMessage = new Scanner(message);
			//no headings left, so figure out what the whiteboard server wants to do
			String nextPart = scanMessage.next();
			if (nextPart.equals(MESSAGES.ROOMDATA.ROOMSERVER.GET_DISPLAY_NAME_OF_USER_IN_ROOM))
			{
				//expect a username and a target username
				String username = scanMessage.next();
				String targetUsername = scanMessage.next();
				this.sendDisplayNameOfUserInRoom(username, targetUsername);
			} else if (nextPart.equals(MESSAGES.ROOMDATA.ROOMSERVER.ADD_ROOM_MODERATOR))
			{
				//expect a room ID, a room modification password, a room join password, and a username
				int roomID = scanMessage.nextInt();
				String modificationPassword = scanMessage.next();
				String joinPassword = scanMessage.next();
				String username = scanMessage.next();
				addRoomModerator(roomID, modificationPassword, joinPassword, username);
			} else if (nextPart.equals(MESSAGES.ROOMDATA.ROOMSERVER.REMOVE_ROOM_MODERATOR))
			{
				//expect a room ID, a room modification password, a room join password, and a username
				int roomID = scanMessage.nextInt();
				String modificationPassword = scanMessage.next();
				String joinPassword = scanMessage.next();
				String username = scanMessage.next();
				removeRoomModerator(roomID, modificationPassword, joinPassword, username);
			} else if (nextPart.equals(MESSAGES.ROOMDATA.ROOMSERVER.SET_WHITEBOARD_PIXEL))
			{
				//expect a room ID, a join password and integers x, y, r, g, b, p
				int roomID = scanMessage.nextInt();
				String joinPassword = scanMessage.next();
				int x = scanMessage.nextInt();
				int y = scanMessage.nextInt();
				int r = scanMessage.nextInt();
				int g = scanMessage.nextInt();
				int b = scanMessage.nextInt();
				BigInteger priority = scanMessage.nextBigInteger();
				setWhiteboardPixel(roomID, joinPassword, x, y, r, g, b, priority);
			} else if (nextPart.equals(MESSAGES.ROOMDATA.ROOMSERVER.ADD_CHAT_HISTORY))
			{
				//expect a room ID, a join password a sender and a message
				int roomID = scanMessage.nextInt();
				String joinPassword = scanMessage.next();
				String sender = scanMessage.next();
				String messageContents = scanMessage.next();
				addChatHistory(roomID, joinPassword, sender, messageContents);
			} else if (nextPart.equals(MESSAGES.ROOMDATA.ROOMSERVER.ADD_USER_PERMISSIONS))
			{
				//expect a room ID, a join password and the four permissions
				//audio participation/listening and chat participation/updating
				int roomID = scanMessage.nextInt();
				String joinPassword = scanMessage.next();
				String username = scanMessage.next();
				boolean audioParticipation = scanMessage.nextBoolean();
				boolean audioListening = scanMessage.nextBoolean();
				boolean chatParticipation = scanMessage.nextBoolean();
				boolean chatListening = scanMessage.nextBoolean();
				addUserPermissions(roomID, joinPassword, username, audioParticipation, audioListening, chatParticipation, chatListening);
			} else if (nextPart.equals(MESSAGES.ROOMDATA.ROOMSERVER.REMOVE_USER_PERMISSIONS))
			{
				//expect a room ID, a join password and a username
				int roomID = scanMessage.nextInt();
				String joinPassword = scanMessage.next();
				String username = scanMessage.next();
				removeUserPermissions(roomID, joinPassword, username);
			} else if (nextPart.equals(MESSAGES.ROOMDATA.ROOMSERVER.GET_WHITEBOARD_PIXELS))
			{
				//expect a room ID and join password
				int roomID = scanMessage.nextInt();
				String joinPassword = scanMessage.next();
				sendWhiteboardPixels(roomID, joinPassword);
			} else if (nextPart.equals(MESSAGES.ROOMDATA.ROOMSERVER.GET_ROOM_MODERATORS))
			{
				//expect a room ID and join password
				int roomID = scanMessage.nextInt();
				String joinPassword = scanMessage.next();
				sendRoomModerators(roomID, joinPassword);
			}  else if (nextPart.equals(MESSAGES.ROOMDATA.ROOMSERVER.GET_ROOM_CHAT_HISTORY))
			{
				//expect a room ID and join password
				int roomID = scanMessage.nextInt();
				String joinPassword = scanMessage.next();
				sendRoomChatHistory(roomID, joinPassword);
			} else if (nextPart.equals(MESSAGES.ROOMDATA.ROOMSERVER.GET_ROOM_USER_PERMISSIONS))
			{
				//expect a room ID and join password
				int roomID = scanMessage.nextInt();
				String joinPassword = scanMessage.next();
				sendRoomUserPermissions(roomID, joinPassword);
			}
			scanMessage.close();
		}
		
		final private void sendDisplayNameOfUserInRoom(String usernameOfRequester, String targetUsername)
		{
			if (UserListManager.isUsernameAlreadyRegistered(usernameOfRequester) == true)
			{
				try 
				{
					String displayName = UserListManager.getDisplayNameOfUser(targetUsername);
					write(MESSAGES.ROOMDATA.ROOMSERVER.GET_DISPLAY_NAME_OF_USER_IN_ROOM_SUCCESS + MESSAGES.DELIMITER + displayName);
				} catch (UserNotFoundException e) 
				{
					write(MESSAGES.ROOMDATA.ROOMSERVER.GET_DISPLAY_NAME_OF_USER_IN_ROOM_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMDATA.ROOMSERVER.NONEXISTING_USER_ERROR_CODE);
				}
			} else
			{
				write(MESSAGES.ROOMDATA.ROOMSERVER.GET_DISPLAY_NAME_OF_USER_IN_ROOM_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMDATA.ROOMSERVER.NONEXISTING_USER_ERROR_CODE);
			}
		}
		
		final private void addRoomModerator(int roomID, String modificationPassword, String joinPassword, String username)
		{
			try
			{
				RoomDataManager.RoomServer.addModerator(roomID, modificationPassword, joinPassword, username);
				write(MESSAGES.ROOMDATA.ROOMSERVER.ADD_ROOM_MODERATOR_SUCCESS);
			} catch (RoomNotFoundException badRoomID)
			{
				write(MESSAGES.ROOMDATA.ROOMSERVER.ADD_ROOM_MODERATOR_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMDATA.NONEXISTING_ROOM_ERROR_CODE);
			} catch (AccessDeniedException badPassword)
			{
				if (badPassword.getErrorCode() == AccessDeniedException.BAD_MODIFICATION_PASSWORD_ERROR)
				{
					write(MESSAGES.ROOMDATA.ROOMSERVER.ADD_ROOM_MODERATOR_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMDATA.INVALID_MODIFICATION_PASSWORD_ERROR_CODE);
				} else if (badPassword.getErrorCode() == AccessDeniedException.BAD_JOIN_PASSWORD_ERROR)
				{
					write(MESSAGES.ROOMDATA.ROOMSERVER.ADD_ROOM_MODERATOR_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMDATA.INVALID_JOIN_PASSWORD_ERROR_CODE);
				}
			} catch (UserNotFoundException badUsername)
			{
				write(MESSAGES.ROOMDATA.ROOMSERVER.ADD_ROOM_MODERATOR_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMDATA.ROOMSERVER.NONEXISTING_USER_ERROR_CODE);
			}
		}
		
		final private void removeRoomModerator(int roomID, String modificationPassword, String joinPassword, String username)
		{
			try
			{
				RoomDataManager.RoomServer.removeModerator(roomID, modificationPassword, joinPassword, username);
				write(MESSAGES.ROOMDATA.ROOMSERVER.REMOVE_ROOM_MODERATOR_SUCCESS);
			} catch (RoomNotFoundException badRoomID)
			{
				write(MESSAGES.ROOMDATA.ROOMSERVER.REMOVE_ROOM_MODERATOR_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMDATA.NONEXISTING_ROOM_ERROR_CODE);
			} catch (AccessDeniedException badPassword)
			{
				if (badPassword.getErrorCode() == AccessDeniedException.BAD_MODIFICATION_PASSWORD_ERROR)
				{
					write(MESSAGES.ROOMDATA.ROOMSERVER.REMOVE_ROOM_MODERATOR_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMDATA.INVALID_MODIFICATION_PASSWORD_ERROR_CODE);
				} else if (badPassword.getErrorCode() == AccessDeniedException.BAD_JOIN_PASSWORD_ERROR)
				{
					write(MESSAGES.ROOMDATA.ROOMSERVER.REMOVE_ROOM_MODERATOR_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMDATA.INVALID_JOIN_PASSWORD_ERROR_CODE);
				}
			} catch (UserNotFoundException badUsername)
			{
				write(MESSAGES.ROOMDATA.ROOMSERVER.REMOVE_ROOM_MODERATOR_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMDATA.ROOMSERVER.NONEXISTING_USER_ERROR_CODE);
			}
		}
		
		final private void setWhiteboardPixel(int roomID, String joinPassword, int x, int y, int r, int g, int b, BigInteger priority)
		{
			try
			{
				if (isColorValid(r, g, b))
				{
					RoomDataManager.RoomServer.setWhiteboardPixel(roomID, joinPassword, x, y, r, g, b, priority);
					write(MESSAGES.ROOMDATA.ROOMSERVER.SET_WHITEBOARD_PIXEL_SUCCESS);
				} else
				{
					write(MESSAGES.ROOMDATA.ROOMSERVER.SET_WHITEBOARD_PIXEL_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMDATA.ROOMSERVER.INVALID_COLOR_ERROR_CODE);
				}
			} catch (RoomNotFoundException badRoomID)
			{
				write(MESSAGES.ROOMDATA.ROOMSERVER.SET_WHITEBOARD_PIXEL_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMDATA.NONEXISTING_ROOM_ERROR_CODE);
			} catch (AccessDeniedException badPassword)
			{
				write(MESSAGES.ROOMDATA.ROOMSERVER.SET_WHITEBOARD_PIXEL_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMDATA.INVALID_JOIN_PASSWORD_ERROR_CODE);
			} catch (ArrayIndexOutOfBoundsException badBounds)
			{
				write(MESSAGES.ROOMDATA.ROOMSERVER.SET_WHITEBOARD_PIXEL_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMDATA.ROOMSERVER.INDEX_OUT_OF_BOUNDS_ERROR_CODE);
			}
		}
		
			final private static int MIN_COLOR_VALUE = 0;
			final private static int MAX_COLOR_VALUE = 255;		
			final public static boolean isColorValid(int red, int green, int blue)
			{
				if (red <= MAX_COLOR_VALUE && green <= MAX_COLOR_VALUE && blue <= MAX_COLOR_VALUE && red >= MIN_COLOR_VALUE && green >= MIN_COLOR_VALUE && blue >= MIN_COLOR_VALUE)
				{
					return true;
				}
				return false;
			}
		
		final private void addChatHistory(int roomID, String joinPassword, String sender, String message)
		{
			try
			{
				RoomDataManager.RoomServer.addChatHistory(roomID, joinPassword, sender, message);
				write(MESSAGES.ROOMDATA.ROOMSERVER.ADD_CHAT_HISTORY_SUCCESS);
			} catch (RoomNotFoundException badRoomID)
			{
				write(MESSAGES.ROOMDATA.ROOMSERVER.ADD_CHAT_HISTORY_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMDATA.NONEXISTING_ROOM_ERROR_CODE);
			} catch (AccessDeniedException badPassword)
			{
				write(MESSAGES.ROOMDATA.ROOMSERVER.ADD_CHAT_HISTORY_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMDATA.INVALID_JOIN_PASSWORD_ERROR_CODE);
			} catch (UserNotFoundException badUsername)
			{
				write(MESSAGES.ROOMDATA.ROOMSERVER.ADD_CHAT_HISTORY_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMDATA.ROOMSERVER.NONEXISTING_USER_ERROR_CODE);
			}
		}
		
		final private void addUserPermissions(int roomID, String joinPassword, String username, boolean audioParticipation, boolean audioListening, boolean chatParticipation, boolean chatUpdating)
		{
			try
			{
				RoomDataManager.RoomServer.addUserPermissions(roomID, joinPassword, username, audioParticipation, audioListening, chatParticipation, chatUpdating);
				write(MESSAGES.ROOMDATA.ROOMSERVER.ADD_USER_PERMISSIONS_SUCCESS);
			} catch (RoomNotFoundException badRoomID)
			{
				write(MESSAGES.ROOMDATA.ROOMSERVER.ADD_USER_PERMISSIONS_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMDATA.NONEXISTING_ROOM_ERROR_CODE);
			} catch (AccessDeniedException badPassword)
			{
				write(MESSAGES.ROOMDATA.ROOMSERVER.ADD_USER_PERMISSIONS_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMDATA.INVALID_JOIN_PASSWORD_ERROR_CODE);
			} catch (UserNotFoundException badUsername)
			{
				write(MESSAGES.ROOMDATA.ROOMSERVER.ADD_USER_PERMISSIONS_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMDATA.ROOMSERVER.NONEXISTING_USER_ERROR_CODE);
			}
		}
		
		final private void removeUserPermissions(int roomID, String joinPassword, String username)
		{
			try
			{
				RoomDataManager.RoomServer.removeUserPermissions(roomID, joinPassword, username);
				write(MESSAGES.ROOMDATA.ROOMSERVER.REMOVE_USER_PERMISSIONS_SUCCESS);
			} catch (RoomNotFoundException badRoomID)
			{
				write(MESSAGES.ROOMDATA.ROOMSERVER.REMOVE_USER_PERMISSIONS_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMDATA.NONEXISTING_ROOM_ERROR_CODE);
			} catch (AccessDeniedException badPassword)
			{
				write(MESSAGES.ROOMDATA.ROOMSERVER.REMOVE_USER_PERMISSIONS_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMDATA.INVALID_JOIN_PASSWORD_ERROR_CODE);
			} catch (UserNotFoundException badUsername)
			{
				write(MESSAGES.ROOMDATA.ROOMSERVER.REMOVE_USER_PERMISSIONS_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMDATA.ROOMSERVER.NONEXISTING_USER_ERROR_CODE);
			}
		}
		
		final private void sendWhiteboardPixels(int roomID, String joinPassword)
		{
			try
			{
				ArrayList<RoomDataManager.Pixel> pixels = RoomDataManager.getWhiteboardPixels(roomID, joinPassword);
				//get a line of text representing all the pixels
				//start with nothing
				String lineToSend = "";
				//the first part is the number of pixels, P.
				int numberOfPixels = pixels.size();
				lineToSend += numberOfPixels + MESSAGES.DELIMITER;
				//then send all the pixels in P bundles. each bundle will contain an x, y, r, g, b, priority
				for (int pixelIndex = 0; pixelIndex < numberOfPixels; pixelIndex++)
				{
					System.out.println(pixelIndex + " of " + numberOfPixels);
					String pixelData = "";
					RoomDataManager.Pixel aPixel = pixels.get(pixelIndex);
					int x = aPixel.getXCoordinate();
					pixelData += x + MESSAGES.DELIMITER;
					int y = aPixel.getYCoordinate();
					pixelData += y + MESSAGES.DELIMITER;
					int r = aPixel.getRed();
					pixelData += r + MESSAGES.DELIMITER;
					int g = aPixel.getGreen();
					pixelData += g + MESSAGES.DELIMITER;
					int b = aPixel.getBlue();
					pixelData += b + MESSAGES.DELIMITER;
					BigInteger priority = aPixel.getPriority();
					pixelData += priority.toString();
					lineToSend += pixelData + MESSAGES.DELIMITER;
				}
				write(MESSAGES.ROOMDATA.ROOMSERVER.GET_WHITEBOARD_PIXELS_SUCCESS + MESSAGES.DELIMITER + lineToSend);
			} catch (RoomNotFoundException badRoomID)
			{
				write(MESSAGES.ROOMDATA.ROOMSERVER.GET_WHITEBOARD_PIXELS_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMDATA.NONEXISTING_ROOM_ERROR_CODE);
			} catch (AccessDeniedException badPassword)
			{
				write(MESSAGES.ROOMDATA.ROOMSERVER.GET_WHITEBOARD_PIXELS_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMDATA.INVALID_JOIN_PASSWORD_ERROR_CODE);
			}
		}
		
		final private void sendRoomModerators(int roomID, String joinPassword)
		{
			try
			{
				ArrayList<String> moderatorUsernames = RoomDataManager.getRoomModerators(roomID, joinPassword);
				//get a line with all moderator information
				//start with nothing
				String lineToSend = "";
				//the first part is the number of moderator usernames
				int numberOfModerators = moderatorUsernames.size();
				lineToSend += numberOfModerators + MESSAGES.DELIMITER;
				//then add all the moderator usernames
				for (int moderatorIndex = 0; moderatorIndex < numberOfModerators; moderatorIndex++)
				{
					lineToSend += moderatorUsernames.get(moderatorIndex) + MESSAGES.DELIMITER;
				}
				write(MESSAGES.ROOMDATA.ROOMSERVER.GET_ROOM_MODERATORS_SUCCESS + MESSAGES.DELIMITER + lineToSend);
			} catch (RoomNotFoundException badRoomID)
			{
				write(MESSAGES.ROOMDATA.ROOMSERVER.GET_ROOM_MODERATORS_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMDATA.NONEXISTING_ROOM_ERROR_CODE);
			} catch (AccessDeniedException badPassword)
			{
				write(MESSAGES.ROOMDATA.ROOMSERVER.GET_ROOM_MODERATORS_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMDATA.INVALID_JOIN_PASSWORD_ERROR_CODE);
			}
		}
		
		final private void sendRoomChatHistory(int roomID, String joinPassword)
		{
			try
			{
				ArrayList<RoomDataManager.ChatHistory> chatHistory = RoomDataManager.getRoomChatHistory(roomID, joinPassword);
				//get a line of text with all the chat history data
				//start with nothing
				String lineToSend = "";
				//the first part is the number of lines of chat history
				int linesOfChatHistory = chatHistory.size();
				lineToSend += linesOfChatHistory + MESSAGES.DELIMITER;
				//then add the chat history data. each line of chat history has a sender and a message
				for (int lineIndex = 0; lineIndex < linesOfChatHistory; lineIndex++)
				{
					String lineData = "";
					RoomDataManager.ChatHistory aLineOfChatHistory = chatHistory.get(lineIndex);
					String sender = aLineOfChatHistory.getSender();
					lineData += sender + MESSAGES.DELIMITER;
					String message = aLineOfChatHistory.getMessage();
					lineData += message;
					lineToSend += lineData + MESSAGES.DELIMITER;
				}
				write(MESSAGES.ROOMDATA.ROOMSERVER.GET_ROOM_CHAT_HISTORY_SUCCESS + MESSAGES.DELIMITER + lineToSend);
			} catch (RoomNotFoundException badRoomID)
			{
				write(MESSAGES.ROOMDATA.ROOMSERVER.GET_ROOM_CHAT_HISTORY_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMDATA.NONEXISTING_ROOM_ERROR_CODE);
			} catch (AccessDeniedException badPassword)
			{
				write(MESSAGES.ROOMDATA.ROOMSERVER.GET_ROOM_CHAT_HISTORY_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMDATA.INVALID_JOIN_PASSWORD_ERROR_CODE);
			}
		}
		
		final private void sendRoomUserPermissions(int roomID, String joinPassword)
		{
			try
			{
				ArrayList<RoomDataManager.UserPermissions> userPermissions = RoomDataManager.getUserPermissions(roomID, joinPassword);
				//get a line of text with all the user permissions data
				//start with nothing
				String lineToSend = "";
				//the first part is the number of user permissions that will be sent
				int numberOfPermissions = userPermissions.size();
				lineToSend += numberOfPermissions + MESSAGES.DELIMITER;
				//then add all the user permissions bundles, which is a username and the four permissions
				//audio participation, audio listening, chat participation, chat updating
				for (int permissionIndex = 0; permissionIndex < numberOfPermissions; permissionIndex++)
				{
					RoomDataManager.UserPermissions aPermissions = userPermissions.get(permissionIndex);
					String permissionData = "";
					String username = aPermissions.getUsername();
					permissionData += username + MESSAGES.DELIMITER;
					boolean audioParticipation = aPermissions.getAudioParticipation();
					permissionData += audioParticipation + MESSAGES.DELIMITER;
					boolean audioListening = aPermissions.getAudioListening();
					permissionData += audioListening + MESSAGES.DELIMITER;
					boolean chatParticipation = aPermissions.getChatParticipation();
					permissionData += chatParticipation + MESSAGES.DELIMITER;
					boolean chatUpdating = aPermissions.getChatUpdating();
					permissionData += chatUpdating;
					lineToSend += permissionData + MESSAGES.DELIMITER;
				}
				write(MESSAGES.ROOMDATA.ROOMSERVER.GET_ROOM_USER_PERMISSIONS_SUCCESS + MESSAGES.DELIMITER + lineToSend);
			} catch (RoomNotFoundException badRoomID)
			{
				write(MESSAGES.ROOMDATA.ROOMSERVER.GET_ROOM_USER_PERMISSIONS_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMDATA.NONEXISTING_ROOM_ERROR_CODE);
			} catch (AccessDeniedException badPassword)
			{
				write(MESSAGES.ROOMDATA.ROOMSERVER.GET_ROOM_USER_PERMISSIONS_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMDATA.INVALID_JOIN_PASSWORD_ERROR_CODE);
			}
		}
		
	@SuppressWarnings("static-method")
	final private String snipHeading(String aString, String aHeading)
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
	
	final public String read() throws IOException
	{
		String message = this.m_in.readUTF();
		System.out.println("Received: " + message);
		return message;
	}
	
	final private synchronized void write(String message)
	{
		try
		{
			System.out.println("Wrote: " + message);
			this.m_out.writeUTF(message);
		} catch (IOException e)
		{
			//Ignore because the connection problem will be handled in other places. 
		}
	}
	
	final public boolean isServerStarted()
	{
		return this.isServerStarted;
	}
	
	final private void closeClientConnection()
	{
		try
		{
			this.m_in.close();
		} catch (IOException e)
		{
			//ignore, this method assumes the input stream has been initialized
		}
		try
		{
			this.m_out.close();
		} catch (IOException e)
		{
			//ignore, this method assumes the output stream has been initialized
		}
		try
		{
			this.m_socket.close();
		} catch (IOException e)
		{
			//ignore, this method assumes the socket has been connected.
		}
		
	}
	
	//this method closes all networking objects and stops the server
	final public void closeAndStop()
	{
		this.isServerStarted = false;
		//if the client has been found
		if (this.m_isConnectedToAClient)
		{
			try
			{
				this.m_in.close();
			} catch (IOException e)
			{
				//Ignore because if it can't be closed, then it's already closed
			}
			try
			{
				this.m_out.close();
			} catch (IOException e)
			{
				//Ignore because if it can't be closed, then it's already closed
			}
			try
			{
				this.m_socket.close();
			} catch (IOException e)
			{
				//Ignore because if it can't be closed, then it's already closed
			}
			try
			{
				this.m_serverSocket.close();
			} catch (IOException e)
			{
				//ignore because if it can't be closed, then it's already closed
			}
		} else
		{
			//if the client has not been found, then we need to connect to ourself
			//to fake a client has been found
			try
			{
				Socket fakeConnection = new Socket("localhost", PORT);
				//now we have to fake an IP Address to make it stop
				DataOutputStream fakeOut = new DataOutputStream(fakeConnection.getOutputStream());
				fakeOut.writeUTF(MESSAGES.CONNECTION.HEADING + MESSAGES.DELIMITER + MESSAGES.CONNECTION.RETURN_IP + "/127.0.0.1");
				fakeOut.writeUTF(MESSAGES.CONNECTION.HEADING + MESSAGES.DELIMITER + MESSAGES.CONNECTION.RETURN_CONNECTION_PASSWORD + MESSAGES.DELIMITER + CONNECTION_PASSWORD);
				this.m_serverSocket.close();
				fakeConnection.close();
			} catch (IOException e)
			{
				//Ignore because this must work
			}
		}
	}
	
	//DEBUG
	/*
	final public static void main(String[] args) throws IOException
	{
		System.out.println(InetAddress.getByName(InetAddress.getLocalHost().toString()));
		Networking n = new Networking();
		System.out.println(n.snipHeading("<A HEaDING> hello my name is", "<A HEaDING>"));
	}//*/
	//END DEBUG
}
