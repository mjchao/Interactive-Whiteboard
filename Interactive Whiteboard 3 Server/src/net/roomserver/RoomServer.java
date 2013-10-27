package net.roomserver;


import java.io.IOException;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

import database.ConnectionEndedException;

import util.Text;

import net.MESSAGES;
import net.Server;
import net.ServerSideErrorException;
import net.TamperedClientException;

/**
 * This deals with actions occurring inside the individual rooms, such as drawing on the whiteboard
 * or sending a chat message.
 *
 */
public class RoomServer extends RoomFeatureServer
{
	final public static int START_OF_ROOM_SERVER_PORTS = 10000;
	final public static int PORTS_USED_PER_ROOM = 10;
	
	final private String m_creatorUsername;
	private int m_whiteboardLength;
	private int m_whiteboardWidth;
	
	//port data:
	//each part of this server, audio, whiteboard, text, user list will have 
	//2 ports, one for step connections and one for concurrent connections
	final private int m_audioServerStepPort;
	final private int m_audioServerConcurrentPort;
	final private static int AUDIO_SERVER_PORT_OFFSET = 2;
	
	final private int m_whiteboardServerStepPort;
	final private int m_whiteboardServerConcurrentPort;
	final private static int WHITEBOARD_SERVER_PORT_OFFSET = 4;
	
	final private int m_textServerStepPort;
	final private int m_textServerConcurrentPort;
	final private static int TEXT_SERVER_PORT_OFFSET = 6;
	
	final private int m_userListServerStepPort;
	final private int m_userListConcurrentPort;
	final private static int USER_LIST_SERVER_PORT_OFFSET = 8;
	
	final private RoomAudioServer m_roomAudioServer;
	final private RoomWhiteboardServer m_roomWhiteboardServer;
	final private RoomTextServer m_roomTextServer;
	final private RoomUserListServer m_roomUserListServer;
	
	public RoomServer(int roomID, String creatorUsername, String modificationPassword, String joinPassword, int whiteboardLength, int whiteboardWidth) throws IOException, ConnectionEndedException
	{
		super(START_OF_ROOM_SERVER_PORTS + (roomID * PORTS_USED_PER_ROOM), START_OF_ROOM_SERVER_PORTS + (roomID * PORTS_USED_PER_ROOM) + 1, roomID, modificationPassword, joinPassword);
		super.m_clients = new RoomSubServer[Server.MAX_CLIENTS];
		//record room data
		this.m_creatorUsername = creatorUsername;
		this.m_whiteboardLength = whiteboardLength;
		this.m_whiteboardWidth = whiteboardWidth;
		//determine ports
		int portRangeLowerBound = START_OF_ROOM_SERVER_PORTS + (roomID * PORTS_USED_PER_ROOM);
		this.m_audioServerStepPort = portRangeLowerBound + AUDIO_SERVER_PORT_OFFSET;
		this.m_audioServerConcurrentPort = portRangeLowerBound + AUDIO_SERVER_PORT_OFFSET + 1;
		this.m_whiteboardServerStepPort = portRangeLowerBound + WHITEBOARD_SERVER_PORT_OFFSET;
		this.m_whiteboardServerConcurrentPort = portRangeLowerBound + WHITEBOARD_SERVER_PORT_OFFSET + 1;
		this.m_textServerStepPort = portRangeLowerBound + TEXT_SERVER_PORT_OFFSET;
		this.m_textServerConcurrentPort = portRangeLowerBound + TEXT_SERVER_PORT_OFFSET + 1;
		this.m_userListServerStepPort = portRangeLowerBound + USER_LIST_SERVER_PORT_OFFSET;
		this.m_userListConcurrentPort = portRangeLowerBound + USER_LIST_SERVER_PORT_OFFSET + 1;
		//create individual servers for the room features
		this.m_roomUserListServer = new RoomUserListServer(this.m_userListServerStepPort, this.m_userListConcurrentPort, this.m_roomID, this.m_creatorUsername, this.m_modificationPassword, this.m_joinPassword, this);
		this.m_roomAudioServer = new RoomAudioServer(this.m_audioServerStepPort, this.m_audioServerConcurrentPort, this.m_roomID, this.m_modificationPassword, this.m_joinPassword, this.m_roomUserListServer);
		this.m_roomWhiteboardServer = new RoomWhiteboardServer(this.m_whiteboardServerStepPort, this.m_whiteboardServerConcurrentPort, this.m_roomID, this.m_modificationPassword, this.m_joinPassword, this.m_whiteboardLength, this.m_whiteboardWidth, this.m_roomUserListServer);
		this.m_roomTextServer = new RoomTextServer(this.m_textServerStepPort, this.m_textServerConcurrentPort, this.m_roomID, this.m_modificationPassword, this.m_joinPassword, this.m_roomUserListServer);
		this.m_roomUserListServer.setRoomTextServer(this.m_roomTextServer);
		//start the servers
		this.m_roomAudioServer.start();
		this.m_roomWhiteboardServer.start();
		this.m_roomTextServer.start();
		this.m_roomUserListServer.start();
	}
	
	@Override
	final protected boolean isClientLoggedIn(String username)
	{
		for (int clientIndex = 0; clientIndex < this.m_clients.length; clientIndex++)
		{
			RoomSubServer aClient = (RoomSubServer) this.m_clients[clientIndex];
			if (aClient != null)
			{
				if (aClient.getUsername().equals(username))
				{
					if (aClient.isLoggedIn())
					{
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * @return			two integers, the first is the step connection port, the second is the
	 * 					concurrent connection port
	 */
	final protected int[] getAudioServerPorts()
	{
		int[] ports = {this.m_audioServerStepPort, this.m_audioServerConcurrentPort};
		return ports;
	}
	
	/**
	 * @return			two integers, the first is the step connection port, the second is the
	 * 					concurrent connection port
	 */
	final protected int[] getWhiteboardServerPorts()
	{
		int[] ports = {this.m_whiteboardServerStepPort, this.m_whiteboardServerConcurrentPort};
		return ports;
	}
	
	/**
	 * @return			two integers, the first is the step connection port, the second is the
	 * 					concurrent connection port
	 */
	final protected int[] getTextChatServerPorts()
	{
		int[] ports = {this.m_textServerStepPort, this.m_textServerConcurrentPort};
		return ports;
	}
	
	/**
	 * @return			two integers, the first is the step connection port, the second is the
	 * 					concurrent connection port
	 */
	final protected int[] getUserListServerPorts()
	{
		int[] ports = {this.m_userListServerStepPort, this.m_userListConcurrentPort};
		return ports;
	}
	
	final public void kickClientFromRoom(String usernameOfClient)
	{
		this.closeConnectionWithClient(usernameOfClient);
		this.m_roomAudioServer.closeConnectionWithClient(usernameOfClient);
		this.m_roomWhiteboardServer.closeConnectionWithClient(usernameOfClient);
		this.m_roomTextServer.closeConnectionWithClient(usernameOfClient);
		this.m_roomUserListServer.closeConnectionWithClient(usernameOfClient);
	}

	@Override
	final protected SubServer assignClientToSubServer(Socket aClientConnection, Socket aConcurrencyConnection) throws IOException
	{
		return new RoomSubServer(aClientConnection, aConcurrencyConnection);
	}
	
	@Override
	public void closeConnectionWithClient(String username)
	{
		for (int clientIndex = 0; clientIndex < this.m_clients.length; clientIndex++)
		{
			RoomSubServer aClient = (RoomSubServer) this.m_clients[clientIndex];
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
	
	final public void handleClientLeft(String username)
	{
		this.m_roomAudioServer.closeConnectionWithClient(username);
	}
	
	final private class RoomSubServer extends RoomFeatureSubServer
	{
		public RoomSubServer(Socket stepConnection, Socket concurrencyConnection) throws IOException
		{
			super(stepConnection, concurrencyConnection);
		}

		@Override
		final protected void handleTerminatingConnection()
		{
			this.closeAndStop();
			RoomServer.this.removeAClient(this.m_subServerID);
		}
		
		@Override
		final protected void decode(String messageFromClient) throws ConnectionEndedException, ServerSideErrorException, TamperedClientException, NoSuchElementException
		{
			//look through the message
			Scanner scanMessage = new Scanner(messageFromClient);
			//first part will be command
			String command = scanMessage.next();
			//figure out what the client wants
			if (command.equals(MESSAGES.GENERAL.LOGIN))
			{
				System.out.println("Logging in");
				//expect a username, a password and a join password
				String username = scanMessage.next();
				String password = scanMessage.next();
				String joinPassword = scanMessage.next();
				super.login(username, password, joinPassword);
			} else if (this.loggedIn)
			{
				if (command.equals(MESSAGES.ROOMSERVER.GET_AUDIO_PORT_DATA))
				{
					sendAudioPortData();
				} else if (command.equals(MESSAGES.ROOMSERVER.GET_WHITEBOARD_PORT_DATA))
				{
					sendWhiteboardPortData();
				} else if (command.equals(MESSAGES.ROOMSERVER.GET_TEXT_PORT_DATA))
				{
					sendTextChatPortData();
				} else if (command.equals(MESSAGES.ROOMSERVER.GET_USER_LIST_PORT_DATA))
				{
					sendUserListPortData();
				} else
				{
					super.logUnknownClientMessage(Text.ROOMSERVER.NAME_OF_SERVER, messageFromClient, this.m_ip);
				}
			} else
			{
				super.closeClientPerformingUnauthorizedActions();
			}
			scanMessage.close();
		}
		
		final private void sendAudioPortData()
		{
			int[] audioPorts = RoomServer.this.getAudioServerPorts();
			write(MESSAGES.ROOMSERVER.GET_AUDIO_PORT_DATA_SUCCESS + MESSAGES.DELIMITER + audioPorts[0] + MESSAGES.DELIMITER + audioPorts[1]);
		}
		
		final private void sendWhiteboardPortData()
		{
			int[] whiteboardPorts = RoomServer.this.getWhiteboardServerPorts();
			write(MESSAGES.ROOMSERVER.GET_WHITEBOARD_PORT_DATA_SUCCESS + MESSAGES.DELIMITER + whiteboardPorts[0] + MESSAGES.DELIMITER + whiteboardPorts[1]);
		}
		
		final private void sendTextChatPortData()
		{
			int[] textChatPorts = RoomServer.this.getTextChatServerPorts();
			write(MESSAGES.ROOMSERVER.GET_TEXT_PORT_DATA_SUCCESS + MESSAGES.DELIMITER + textChatPorts[0] + MESSAGES.DELIMITER + textChatPorts[1]);
		}
		
		final private void sendUserListPortData()
		{
			int[] userListPorts = RoomServer.this.getUserListServerPorts();
			write(MESSAGES.ROOMSERVER.GET_USER_LIST_PORT_DATA_SUCCESS + MESSAGES.DELIMITER + userListPorts[0] + MESSAGES.DELIMITER + userListPorts[1]);
		}
		
		@Override
		final protected String read() throws IOException
		{
			String message = super.read();
			System.out.println("Room Server received from client: " + message);
			return message;
		}
		
		@Override
		final protected void write(String message)
		{
			System.out.println("Room Server wrote to client: " + message);
			super.write(message);
		}
	}
}
