package net.mainserver;

import java.io.IOException;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

import util.CommonMethods;
import util.Text;

import database.ConnectionEndedException;
import database.DatabaseConnection;

import net.MESSAGES;
import net.Server;
import net.ServerSideErrorException;
import net.TamperedClientException;
import net.messagingserver.MessagingServer;
import net.roomdataserver.RoomDataServer;

/**
 * This object deals with connections regarding general user information (e.g. passwords) and serves as
 * a directory to other servers
 *
 */
final public class MainServer extends Server 
{
	final private static int MAIN_SERVER_PORT = 9999;
	final private static int MAIN_SERVER_CONCURRENCY_PORT = 9998;
	
	public MainServer() throws IOException
	{
		super(MAIN_SERVER_PORT, MAIN_SERVER_CONCURRENCY_PORT);
		super.m_clients = new MainSubServer[Server.MAX_CLIENTS];
	}
	
	@Override
	final protected boolean isClientLoggedIn(String username)
	{
		for (int clientIndex = 0; clientIndex < this.m_clients.length; clientIndex++)
		{
			MainSubServer aClient = (MainSubServer) this.m_clients[clientIndex];
			if (aClient != null)
			{
				if (aClient.getUsername().equals(username))
				{
					if (aClient.isLoggedIn())
					{
						return false;
					}
				}
			}
		}
		return false;
	}

	@Override
	final protected MainSubServer assignClientToSubServer(Socket aClientConnection, Socket aClientConcurrencyConnection) throws IOException
	{
		MainSubServer newSubServer = new MainSubServer(aClientConnection, aClientConcurrencyConnection);
		return newSubServer;
	}	

	final private class MainSubServer extends SubServer
	{	
		
		public MainSubServer(Socket stepConnection, Socket concurrencyConnection) throws IOException
		{
			super(stepConnection, concurrencyConnection);
		}
		
		@Override
		final protected void handleTerminatingConnection()
		{
			this.closeAndStop();
			MainServer.this.removeAClient(this.m_subServerID);
		}

		@Override
		protected void decode(String messageFromClient) throws ConnectionEndedException, ServerSideErrorException, TamperedClientException, NoSuchElementException
		{
			String message = messageFromClient;
			//prepare to look through the message. 
			Scanner scanMessage = new Scanner(message);
			String nextPart = scanMessage.next();
			if (nextPart.equals(MESSAGES.GENERAL.LOGIN))
			{
				//expect a username and a password
				String username = scanMessage.next();
				String password = scanMessage.next();
				loadUserInformation(username);
				super.login(username, password);
			} else if (nextPart.equals(MESSAGES.MAINSERVER.REGISTER))
			{
				//expect a username and a password
				String username = scanMessage.next();
				String password = scanMessage.next();
				register(username, password);
			} else if (this.loggedIn)
			{
				if (nextPart.equals(MESSAGES.MAINSERVER.CHANGE_PASSWORD))
				{
					//expect the username, the current password an the new password
					String username = scanMessage.next();
					String currentPassword = scanMessage.next();
					String newPassword = scanMessage.next();
					changePassword(username, currentPassword, newPassword);
				} else if (nextPart.equals(MESSAGES.MAINSERVER.CHANGE_DISPLAY_NAME))
				{
					//expect the username, a password, a name change code, and a new display name
					String username = scanMessage.next();
					String password = scanMessage.next();
					String nameChangeCode = scanMessage.next();
					String newDisplayName = scanMessage.next();
					changeDisplayName(username, password, nameChangeCode, newDisplayName);
				} else if (nextPart.equals(MESSAGES.MAINSERVER.GET_MAIN_SERVER_INFO))
				{
					sendMainServerInfo();
				} else if (nextPart.equals(MESSAGES.MAINSERVER.GET_MESSAGING_SERVER_INFO))
				{
					sendMessagingServerInfo();
				} else if (nextPart.equals(MESSAGES.MAINSERVER.GET_ROOM_DATA_SERVER_INFO))
				{
					sendRoomDataServerInfo();
				} else
				{
					super.logUnknownClientMessage(Text.MAINSERVER.NAME_OF_SERVER ,message, this.m_ip);
				}
			} else
			{
				super.closeClientPerformingUnauthorizedActions();
			}
			scanMessage.close();
		}
		
		//this method tells the database to load user information and return true if the database
		//successfully loaded the information and false otherwise
		final private void loadUserInformation(String username) throws ConnectionEndedException, ServerSideErrorException
		{
			//try to get the database to load the user information
			String message = database.MESSAGES.USERDATA.HEADING + database.MESSAGES.DELIMITER + database.MESSAGES.USERDATA.LOAD_USER_INFORMATION + database.MESSAGES.DELIMITER + username;
			String response = DatabaseConnection.sendMessageAndGetResponse(message);
			if (response.equals(database.MESSAGES.USERDATA.LOAD_USER_INFORMATION_SUCCESS))
			{
				//ignore
			} else if (response.equals(database.MESSAGES.USERDATA.LOAD_USER_INFORMATION_FAILED + database.MESSAGES.DELIMITER + database.MESSAGES.USERDATA.FILE_NOT_FOUND_ERROR_CODE))
			{
				//ignore - later, the user just won't be able to do anything
			} else if (response.equals(database.MESSAGES.USERDATA.LOAD_USER_INFORMATION_FAILED + database.MESSAGES.DELIMITER + database.MESSAGES.USERDATA.FILE_CORRUPTED_ERROR_CODE))
			{
				//ignore - later, the user just won't be able to do anything
			} else
			{
				throw generateDatabaseMiscommunicationError(response);
			}
		}
		
		final private void register(String username, String password) throws ConnectionEndedException, ServerSideErrorException
		{
			if (isUsernameValid(username))
			{
				if (isPasswordValid(password))
				{
					//try to get the database to register the new user
					String message = database.MESSAGES.USERDATA.HEADING + database.MESSAGES.DELIMITER + database.MESSAGES.USERDATA.MAINSERVER.MAIN_SERVER_HEADING + database.MESSAGES.DELIMITER + database.MESSAGES.USERDATA.MAINSERVER.REGISTER + database.MESSAGES.DELIMITER + username + database.MESSAGES.DELIMITER + password;
					String response = DatabaseConnection.sendMessageAndGetResponse(message);
					if (response.equals(database.MESSAGES.USERDATA.MAINSERVER.REGISTER_SUCCESS))
					{
						write(MESSAGES.MAINSERVER.REGISTER_SUCCESS);
					} else if (response.equals(database.MESSAGES.USERDATA.MAINSERVER.REGISTER_FAILED + database.MESSAGES.DELIMITER + database.MESSAGES.USERDATA.MAINSERVER.USERNAME_IN_USER_ERROR))
					{
						write(MESSAGES.MAINSERVER.REGISTER_FAILED + MESSAGES.DELIMITER + MESSAGES.MAINSERVER.USERNAME_IN_USE_ERROR_CODE);
					} else if (response.equals(database.MESSAGES.USERDATA.MAINSERVER.REGISTER_FAILED + database.MESSAGES.DELIMITER + database.MESSAGES.USERDATA.MAINSERVER.IO_EXCEPTION))
					{
						write(MESSAGES.MAINSERVER.REGISTER_FAILED + MESSAGES.DELIMITER + MESSAGES.MAINSERVER.IO_EXCEPTION);
					} else
					{
						throw generateDatabaseMiscommunicationError(response);
					}
				} else
				{
					write(MESSAGES.MAINSERVER.REGISTER_FAILED + MESSAGES.DELIMITER + MESSAGES.MAINSERVER.INVALID_NEW_PASSWORD_ERROR_CODE);
					CommonMethods.logSuspiciousMessage(Text.MAINSERVER.getFailedRegisterLogMessage(username, password, this.m_ip));
				}
			} else
			{
				write(MESSAGES.MAINSERVER.REGISTER_FAILED + MESSAGES.DELIMITER + MESSAGES.MAINSERVER.INVALID_NEW_USERNAME_ERROR_CODE);
				CommonMethods.logSuspiciousMessage(Text.MAINSERVER.getFailedRegisterLogMessage(username, password, this.m_ip));
			}
		}
		
			//this method checks to make sure a given username is valid
			final private boolean isUsernameValid(String username)
			{
				//TODO
				return true;
			}
			
			//this method checks to make sure a given password is valid
			final private boolean isPasswordValid(String password)
			{
				//TODO
				return true;
			}
		
		final private void changePassword(String username, String currentPassword, String newPassword) throws ConnectionEndedException, ServerSideErrorException, TamperedClientException
		{
			assertUsernameIsCorrect(username);
			if (isPasswordValid(newPassword))
			{
				//try to get the database to change the client's password
				String message = database.MESSAGES.USERDATA.HEADING + database.MESSAGES.DELIMITER + database.MESSAGES.USERDATA.MAINSERVER.MAIN_SERVER_HEADING + database.MESSAGES.DELIMITER + database.MESSAGES.USERDATA.MAINSERVER.SET_USER_PASSWORD + database.MESSAGES.DELIMITER + this.m_username + database.MESSAGES.DELIMITER + currentPassword + database.MESSAGES.DELIMITER + newPassword;
				String response = DatabaseConnection.sendMessageAndGetResponse(message);
				if (response.equals(database.MESSAGES.USERDATA.MAINSERVER.SET_USER_PASSWORD_SUCCESS))
				{
					write(MESSAGES.MAINSERVER.CHANGE_PASSWORD_SUCCESS);
				} else if (response.equals(database.MESSAGES.USERDATA.MAINSERVER.SET_USER_PASSWORD_FAILED + database.MESSAGES.DELIMITER + database.MESSAGES.USERDATA.NONEXISTING_USER_ERROR_CODE))
				{
					write(MESSAGES.MAINSERVER.CHANGE_PASSWORD_FAILED + MESSAGES.DELIMITER + MESSAGES.GENERAL.NONEXISTING_USER_ERROR_CODE);
					CommonMethods.logSuspiciousMessage(Text.SUBSERVER.getUsernameUnexpectedlyChanged(this.m_username, username, this.m_ip));
				} else if (response.equals(database.MESSAGES.USERDATA.MAINSERVER.SET_USER_PASSWORD_FAILED + database.MESSAGES.DELIMITER + database.MESSAGES.USERDATA.BAD_USER_PASSWORD_ERROR_CODE))
				{
					write(MESSAGES.MAINSERVER.CHANGE_PASSWORD_FAILED + MESSAGES.DELIMITER + MESSAGES.GENERAL.BAD_USER_PASSWORD_ERROR_CODE);
					CommonMethods.logSuspiciousMessage(Text.MAINSERVER.getFailedPasswordChangeLogMessage(username, currentPassword, this.m_ip));
				} else
				{
					throw generateDatabaseMiscommunicationError(response);
				}
			} else
			{
				write(MESSAGES.MAINSERVER.CHANGE_PASSWORD_FAILED + MESSAGES.DELIMITER + MESSAGES.MAINSERVER.INVALID_NEW_PASSWORD_ERROR_CODE);
				CommonMethods.logSuspiciousMessage(Text.MAINSERVER.getInvalidNewPasswordLogMessage(username, newPassword, this.m_ip));
			}
		}
		
		final private void changeDisplayName(String username, String password, String nameChangeCode, String newName) throws ConnectionEndedException, ServerSideErrorException, TamperedClientException
		{
			assertUsernameIsCorrect(username);
			if (isDisplayNameValid(newName))
			{
				//try to get the database to change the client's display name
				String message = database.MESSAGES.USERDATA.HEADING + database.MESSAGES.DELIMITER + database.MESSAGES.USERDATA.MAINSERVER.MAIN_SERVER_HEADING + database.MESSAGES.DELIMITER + database.MESSAGES.USERDATA.MAINSERVER.SET_USER_DISPLAY_NAME + database.MESSAGES.DELIMITER + this.m_username + database.MESSAGES.DELIMITER + password + database.MESSAGES.DELIMITER + nameChangeCode + database.MESSAGES.DELIMITER + newName;
				String response = DatabaseConnection.sendMessageAndGetResponse(message);
				if (response.equals(database.MESSAGES.USERDATA.MAINSERVER.SET_USER_DISPLAY_NAME_SUCCESS))
				{
					write(MESSAGES.MAINSERVER.CHANGE_DISPLAY_NAME_SUCCESS);
				} else if (response.equals(database.MESSAGES.USERDATA.MAINSERVER.SET_USER_DISPLAY_NAME_FAILED + database.MESSAGES.DELIMITER + database.MESSAGES.USERDATA.NONEXISTING_USER_ERROR_CODE))
				{
					write(MESSAGES.MAINSERVER.CHANGE_DISPLAY_NAME_FAILED + MESSAGES.DELIMITER + MESSAGES.GENERAL.NONEXISTING_USER_ERROR_CODE);
					CommonMethods.logSuspiciousMessage(Text.SUBSERVER.getUsernameUnexpectedlyChanged(this.m_username, username, this.m_ip));
				} else if (response.equals(database.MESSAGES.USERDATA.MAINSERVER.SET_USER_DISPLAY_NAME_FAILED + database.MESSAGES.DELIMITER + database.MESSAGES.USERDATA.BAD_USER_PASSWORD_ERROR_CODE))
				{
					write(MESSAGES.MAINSERVER.CHANGE_DISPLAY_NAME_FAILED + MESSAGES.DELIMITER + MESSAGES.GENERAL.BAD_USER_PASSWORD_ERROR_CODE);
					CommonMethods.logSuspiciousMessage(Text.MAINSERVER.getFailedNameChangePasswordErrorLogMessage(username, password, this.m_ip));
				} else if (response.equals(database.MESSAGES.USERDATA.MAINSERVER.SET_USER_DISPLAY_NAME_FAILED + database.MESSAGES.DELIMITER + database.MESSAGES.USERDATA.MAINSERVER.INVALID_NAME_CHANGE_CODE_ERROR_CODE))
				{
					write(MESSAGES.MAINSERVER.CHANGE_DISPLAY_NAME_FAILED + MESSAGES.DELIMITER + MESSAGES.MAINSERVER.BAD_NAME_CHANGE_CODE_ERROR_CODE);
					CommonMethods.logSuspiciousMessage(Text.MAINSERVER.getFailedNameChangeCodeErrorLogMessage(username, nameChangeCode, this.m_ip));
				} else
				{
					throw generateDatabaseMiscommunicationError(response);
				}	
			} else
			{
				write(MESSAGES.MAINSERVER.CHANGE_DISPLAY_NAME_FAILED + MESSAGES.DELIMITER + MESSAGES.MAINSERVER.INVALID_NEW_DISPLAY_NAME_ERROR_CODE);
			}
		}
		
			final private boolean isDisplayNameValid(String displayName)
			{
				//TODO
				return true;
			}
		
		final private void sendMainServerInfo()
		{
			write(MESSAGES.MAINSERVER.GET_MAIN_SERVER_INFO_SUCCESS + MESSAGES.DELIMITER + MainServer.MAIN_SERVER_CONCURRENCY_PORT);
		}
			
		final private void sendMessagingServerInfo()
		{
			write(MESSAGES.MAINSERVER.GET_MESSAGING_SERVER_INFO_SUCCESS + MESSAGES.DELIMITER + MessagingServer.MESSAGING_SERVER_PORT + MESSAGES.DELIMITER + MessagingServer.MESSAGING_SERVER_CONCURRENCY_PORT);
		}
		
		final private void sendRoomDataServerInfo()
		{
			write(MESSAGES.MAINSERVER.GET_ROOM_DATA_SERVER_INFO_SUCCESS + MESSAGES.DELIMITER + RoomDataServer.ROOM_DATA_SERVER_PORT + MESSAGES.DELIMITER + RoomDataServer.ROOM_DATA_SERVER_CONCURRENCY_PORT);
		}
		
		@Override
		final protected String read() throws IOException
		{
			String message = super.read();
			System.out.println("Main Server received from client: " + message);
			return message;
		}
		
		@Override
		final protected void write(String message)
		{
			System.out.println("Main Server wrote to client: " + message);
			super.write(message);
		}
	}
}
