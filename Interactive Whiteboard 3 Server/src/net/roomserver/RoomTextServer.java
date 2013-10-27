package net.roomserver;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import database.ConnectionEndedException;
import database.DatabaseConnection;

import net.MESSAGES;
import net.Server;
import net.ServerSideErrorException;
import net.TamperedClientException;

final public class RoomTextServer extends RoomFeatureServer 
{
	
	final private ArrayList<LineOfHistory> m_textChatHistory = new ArrayList<LineOfHistory>();
	final private RoomUserListServer m_userList;
	
	public RoomTextServer(int stepPort, int concurrencyPort, int roomID, String modificationPassword, String joinPassword, RoomUserListServer userList) throws IOException, ConnectionEndedException 
	{
		super(stepPort, concurrencyPort, roomID, modificationPassword, joinPassword);
		super.m_clients = new RoomTextSubServer[Server.MAX_CLIENTS];
		this.m_userList = userList;
		loadChatHistory();
	}
	
	@Override
	final protected boolean isClientLoggedIn(String username)
	{
		for (int clientIndex = 0; clientIndex < this.m_clients.length; clientIndex++)
		{
			RoomTextSubServer aClient = (RoomTextSubServer) this.m_clients[clientIndex];
			if (aClient != null)
			{
				if (aClient.getUsername().equals(username))
				{
					if (aClient.isLoggedIn())
					{
						System.out.println("text server still logged in.");
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	protected SubServer assignClientToSubServer(Socket aClientConnection, Socket aClientConcurrencyConnection) throws IOException 
	{
		return new RoomTextSubServer(aClientConnection, aClientConcurrencyConnection);
	}
	
	final private void loadChatHistory() throws ConnectionEndedException
	{
		String messageToDatabase = database.MESSAGES.ROOMDATA.HEADING + database.MESSAGES.DELIMITER + database.MESSAGES.ROOMDATA.ROOMSERVER.ROOM_SERVER_HEADING + database.MESSAGES.DELIMITER + database.MESSAGES.ROOMDATA.ROOMSERVER.GET_ROOM_CHAT_HISTORY + database.MESSAGES.DELIMITER + this.m_roomID + database.MESSAGES.DELIMITER + this.m_joinPassword;
		String response = DatabaseConnection.sendMessageAndGetResponse(messageToDatabase);
		//look through the response
		Scanner scanResponse = new Scanner(response);
		//first part should be result
		String result = scanResponse.next();
		if (result.equals(database.MESSAGES.ROOMDATA.ROOMSERVER.GET_ROOM_CHAT_HISTORY_SUCCESS))
		{
			//if successful, load the chat history
			//first part should be number of lines of chat history
			int numberOfLines = scanResponse.nextInt();
			for (int linesRead = 0; linesRead < numberOfLines; linesRead++)
			{
				LineOfHistory lineToAdd = new LineOfHistory(scanResponse.next(), scanResponse.next());
				this.m_textChatHistory.add(lineToAdd);
			}
		} else
		{
			//if not successful, assume there is no chat history
		}
	}
	
	final protected void sendChatToAllClients(String senderUsername, String message)
	{
		//make sure the user is allowed to participate in text chat
		if (this.m_userList.doesUserHaveTextChatParticipation(senderUsername))
		{
			for (int clientIndex = 0; clientIndex < this.m_clients.length; clientIndex++)
			{
				RoomTextSubServer aClient = (RoomTextSubServer) this.m_clients[clientIndex];
				if (aClient != null)
				{
					//make sure the recipient is allowed to have updating text chat history
					if (this.m_userList.doesUserHaveTextChatUpdating(aClient.getUsername()))
					{
						aClient.addChat(senderUsername, message);
					}
				}
			}
		}
		this.m_textChatHistory.add(new LineOfHistory(senderUsername, message));
	}
	
	final protected void sendSystemMessage(String message)
	{
		for (int clientIndex = 0; clientIndex < this.m_clients.length; clientIndex++)
		{
			RoomTextSubServer aClient = (RoomTextSubServer) this.m_clients[clientIndex];
			if (aClient != null)
			{
				aClient.addSystemMessage(message);
			}
		}
	}
	
	final protected boolean doesUserHaveChatParticipation(String username)
	{
		return this.m_userList.doesUserHaveTextChatParticipation(username);
	}
	
	final protected boolean doesUserHaveChatHistory(String username)
	{
		return this.m_userList.doesUserHaveTextChatUpdating(username);
	}
	
	final protected int getLinesOfChatHistory()
	{
		return this.m_textChatHistory.size();
	}
	
	final protected LineOfHistory getLineOfChatHistoryAt(int index)
	{
		return this.m_textChatHistory.get(index);
	}
	
	@Override
	public void closeConnectionWithClient(String username)
	{
		for (int clientIndex = 0; clientIndex < this.m_clients.length; clientIndex++)
		{
			RoomTextSubServer aClient = (RoomTextSubServer) this.m_clients[clientIndex];
			if (aClient != null)
			{
				if (aClient.getUsername().equals(username))
				{
					aClient.closeAndStop();
					this.removeAClient(aClient.getSubServerID());
					return;
				}
			}
		}
	}

	final private class RoomTextSubServer extends RoomFeatureSubServer
	{	
		public RoomTextSubServer(Socket s, Socket concurrentConnection) throws IOException 
		{
			super(s, concurrentConnection);
		}

		@Override
		protected void handleTerminatingConnection() 
		{
			this.closeAndStop();
			RoomTextServer.this.removeAClient(this.m_subServerID);
		}

		@Override
		protected void decode(String messageFromClient) throws ConnectionEndedException, ServerSideErrorException, TamperedClientException, NoSuchElementException 
		{
			Scanner scanMessage = new Scanner(messageFromClient);
			String command = scanMessage.next();
			if (command.equals(MESSAGES.GENERAL.LOGIN))
			{
				//expect a username and a password
				String username = scanMessage.next();
				String password = scanMessage.next();
				String joinPassword = scanMessage.next();
				login(username, password, joinPassword);
				if (this.loggedIn)
				{
					updateClientChatHistory();
				}
			} else if (this.loggedIn)
			{
				if (command.equals(MESSAGES.ROOMSERVER.TEXT_SERVER.SEND_CHAT))
				{
					//expect a username, a join password and a message
					String username = scanMessage.next();
					String joinPassword = scanMessage.next();
					String message = scanMessage.next();
					sendChat(username, joinPassword, message);
				}
			}
			scanMessage.close();
		}
		
		final private void updateClientChatHistory()
		{
			//make sure client can have updated chat history
			if (RoomTextServer.this.doesUserHaveChatHistory(this.m_username) == true)
			{
				int numberOfLinesOfChatHistory = RoomTextServer.this.getLinesOfChatHistory();
				for (int lineIndex = 0; lineIndex < numberOfLinesOfChatHistory; lineIndex++)
				{
					LineOfHistory aLine = RoomTextServer.this.getLineOfChatHistoryAt(lineIndex);
					this.writeConcurrent(MESSAGES.ROOMSERVER.TEXT_SERVER.ADD_CHAT + MESSAGES.DELIMITER + aLine.getSenderUsername() + MESSAGES.DELIMITER + aLine.getMessage());
				}
			}
		}
		
		final private void sendChat(String username, String joinPassword, String message) throws TamperedClientException, ConnectionEndedException, ServerSideErrorException
		{
			assertUsernameIsCorrect(username);
			assertJoinPasswordIsCorrect(joinPassword);
			if (RoomTextServer.this.doesUserHaveChatParticipation(this.m_username))
			{
				int roomID = RoomTextServer.this.getRoomID();
				String joinPasswordToProvide = RoomTextServer.this.getJoinPassword();
				String messageToDatabase = database.MESSAGES.ROOMDATA.HEADING + database.MESSAGES.DELIMITER + database.MESSAGES.ROOMDATA.ROOMSERVER.ROOM_SERVER_HEADING + database.MESSAGES.DELIMITER + database.MESSAGES.ROOMDATA.ROOMSERVER.ADD_CHAT_HISTORY 
						+ database.MESSAGES.DELIMITER + roomID + database.MESSAGES.DELIMITER + joinPasswordToProvide + database.MESSAGES.DELIMITER + username + database.MESSAGES.DELIMITER + message;
				String response = DatabaseConnection.sendMessageAndGetResponse(messageToDatabase);
				//look through the response
				Scanner scanResponse = new Scanner(response);
				//first part should be result
				String result = scanResponse.next();
				if (result.equals(database.MESSAGES.ROOMDATA.ROOMSERVER.ADD_CHAT_HISTORY_SUCCESS))
				{
					//if successful, update everyone that's connected
					RoomTextServer.this.sendChatToAllClients(username, message);
					write(MESSAGES.ROOMSERVER.TEXT_SERVER.SEND_CHAT_SUCCESS);
				} else if (result.equals(database.MESSAGES.ROOMDATA.ROOMSERVER.ADD_CHAT_HISTORY_FAILED))
				{
					//if failed, figure out why and let client know
					//an error code should follow
					int errorCode = scanResponse.nextInt();
					scanResponse.close();
					if (errorCode == database.MESSAGES.ROOMDATA.NONEXISTING_ROOM_ERROR_CODE)
					{
						write(MESSAGES.ROOMSERVER.TEXT_SERVER.SEND_CHAT_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMSERVER.ROOM_DOES_NOT_EXIST_ERROR_CODE);
					} else if (errorCode == database.MESSAGES.ROOMDATA.INVALID_JOIN_PASSWORD_ERROR_CODE)
					{
						write(MESSAGES.ROOMSERVER.TEXT_SERVER.SEND_CHAT_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMSERVER.INVALID_JOIN_PASSWORD_ERROR_CODE);
					} else if (errorCode == database.MESSAGES.ROOMDATA.ROOMSERVER.NONEXISTING_USER_ERROR_CODE)
					{
						write(MESSAGES.ROOMSERVER.TEXT_SERVER.SEND_CHAT_FAILED + MESSAGES.DELIMITER + MESSAGES.GENERAL.NONEXISTING_USER_ERROR_CODE);
					} else
					{
						throw generateDatabaseMiscommunicationError(response);
					}
				} else
				{
					throw generateDatabaseMiscommunicationError(response);
				}
				scanResponse.close();
			} else
			{
				write(MESSAGES.ROOMSERVER.TEXT_SERVER.SEND_CHAT_SUCCESS);
			}
		}
		
		final protected void addChat(String senderUsername, String message)
		{
			String messageToClient = MESSAGES.ROOMSERVER.TEXT_SERVER.ADD_CHAT + MESSAGES.DELIMITER + senderUsername + MESSAGES.DELIMITER + message;
			if (senderUsername.equals(this.m_username))
			{
				writeConcurrent(messageToClient);
			} else
			{
				if (RoomTextServer.this.doesUserHaveChatHistory(this.m_username))
				{
					writeConcurrent(messageToClient);
				}
			}
		}
		
		final protected void addSystemMessage(String message)
		{
			String messageToClient = MESSAGES.ROOMSERVER.TEXT_SERVER.ADD_SYSTEM_MESSAGE + MESSAGES.DELIMITER + MESSAGES.substituteForMessageDelimiters(message);
			writeConcurrent(messageToClient);
		}
		
		@Override
		final protected String read() throws IOException
		{
			String message = super.read();
			System.out.println("Room Text Server received from client: " + message);
			return message;
		}
		
		@Override
		final protected void write(String message)
		{
			System.out.println("Room Text Server wrote to client: " + message);
			super.write(message);
		}
	}
	
	final private class LineOfHistory
	{
		final private String m_senderUsername;
		final private String m_message;
		
		public LineOfHistory(String senderUsername, String message)
		{
			this.m_senderUsername = senderUsername;
			this.m_message = message;
		}
		
		final public String getSenderUsername()
		{
			return this.m_senderUsername;
		}
		
		final public String getMessage()
		{
			return this.m_message;
		}
	}
}
