package net.roomdataserver;

import java.io.IOException;
import java.net.Socket;
import java.util.Calendar;
import java.util.NoSuchElementException;
import java.util.Scanner;

import util.Text;

import database.ConnectionEndedException;
import database.DatabaseConnection;

import net.MESSAGES;
import net.Server;
import net.ServerSideErrorException;
import net.TamperedClientException;
import net.roomserver.RoomServer;

/**
 * This deals with requests for general information about a room (e.g. what is the room name?)
 *
 */
final public class RoomDataServer extends Server
{
	final public static int ROOM_DATA_SERVER_PORT = 9995;
	final public static int ROOM_DATA_SERVER_CONCURRENCY_PORT = 9994;
	/**
	 * maximum number of rooms stored in the database
	 */
	final private int m_maximumRoomsStored;
	/**
	 * Stores which rooms have been created and which have not
	 */
	final private boolean[] m_existingRooms;
	/**
	 * For each existing room, there must be a room server
	 */
	final private RoomServer[] m_roomServers;
	
	public RoomDataServer() throws IOException, ConnectionEndedException, ServerSideErrorException
	{
		super(ROOM_DATA_SERVER_PORT, ROOM_DATA_SERVER_CONCURRENCY_PORT);
		super.m_clients = new RoomDataSubServer[Server.MAX_CLIENTS];
		this.m_maximumRoomsStored = getMaximumRoomsStoredFromDatabase();
		this.m_existingRooms = new boolean[this.m_maximumRoomsStored];
		this.m_roomServers = new RoomServer[this.m_maximumRoomsStored];
		loadExistingRooms();
		System.out.println( "Finished laoding room data server." );
	}
	
	@Override
	final protected boolean isClientLoggedIn(String username)
	{
		for (int clientIndex = 0; clientIndex < this.m_clients.length; clientIndex++)
		{
			RoomDataSubServer aClient = (RoomDataSubServer) this.m_clients[clientIndex];
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

	@Override
	final protected RoomDataSubServer assignClientToSubServer(Socket aClientConnection, Socket aConcurrencyConnection) throws IOException
	{
		return new RoomDataSubServer(aClientConnection, aConcurrencyConnection);
	}
	
	final protected static int getMaximumRoomsStoredFromDatabase() throws ConnectionEndedException, ServerSideErrorException
	{
		String messageToDatabase = database.MESSAGES.ROOMDATA.HEADING + MESSAGES.DELIMITER + database.MESSAGES.ROOMDATA.GET_MAXIMUM_ROOMS_STORED;
		String response = DatabaseConnection.sendMessageAndGetResponse(messageToDatabase);
		//look through the response
		Scanner scanResponse = new Scanner(response);
		//first part should be the result
		String result = scanResponse.next();
		if (result.equals(database.MESSAGES.ROOMDATA.GET_MAXIMUM_ROOMS_STORED_SUCCESS))
		{
			//look through the data
			int numberOfRoomsStored = scanResponse.nextInt();
			scanResponse.close();
			return numberOfRoomsStored;
		}
		scanResponse.close();
		throw new ServerSideErrorException(ServerSideErrorException.DATABASE_MISCOMMUNICATION_ERROR, response);
	}
	
	final protected void loadExistingRooms() throws ConnectionEndedException, ServerSideErrorException, IOException
	{
		String messageToDatabase = database.MESSAGES.ROOMDATA.HEADING + MESSAGES.DELIMITER + database.MESSAGES.ROOMDATA.GET_EXISTING_ROOM_LIST;
		String response = DatabaseConnection.sendMessageAndGetResponse(messageToDatabase);
		//look through the response
		Scanner scanResponse = new Scanner(response);
		//first part should be result
		String result = scanResponse.next();
		if (result.equals(database.MESSAGES.ROOMDATA.GET_EXISTRING_ROOM_LIST_SUCCESS))
		{
			//look through the data
			//first part is number of room IDs that follow
			int numberOfExistingRooms = scanResponse.nextInt();
			//then read in the IDs of all existing rooms
			for (int roomsRead = 0; roomsRead < numberOfExistingRooms; roomsRead++)
			{
				//read in a room ID
				int aRoomID = scanResponse.nextInt();
				try
				{
					this.m_roomServers[aRoomID] = new RoomServer(aRoomID, getRoomCreatorName(aRoomID), getRoomModificationPassword(aRoomID), getRoomJoinPassword(aRoomID), getRoomWhiteboardLength(aRoomID), getRoomWhiteboardWidth(aRoomID));
					System.out.println("1");
					this.m_roomServers[aRoomID].start();
					System.out.println("2");
					this.m_existingRooms[aRoomID] = true;
				} catch (RoomNotFoundException e)
				{
					//if the room is not on the server, then don't create it here either, just continue
				}
			}
			scanResponse.close();
		} else
		{
			scanResponse.close();
			throw new ServerSideErrorException(ServerSideErrorException.DATABASE_MISCOMMUNICATION_ERROR, response);
		}
	}
	
	final protected int getMaximumRoomsStored()
	{
		return this.m_maximumRoomsStored;
	}
	
	final protected boolean doesRoomExist(int roomID)
	{
		return this.m_existingRooms[roomID];
	}
	
	final protected int getNumberOfExistingRooms()
	{
		int numberOfExistingRooms = 0;
		for (int roomIndex = 0; roomIndex < this.m_existingRooms.length; roomIndex++)
		{
			if (this.m_existingRooms[roomIndex] == true)
			{
				numberOfExistingRooms++;
			}
		}
		return numberOfExistingRooms;
	}
	
	/**
	 * Updates the list of created rooms when a client creates a room to reflect that
	 * newly created room now exists. For example, if a client just created Room 5, then
	 * we now update the existing room data to show Room 5 has been created
	 * 
	 * @param roomID		an integer, the ID of the room that was just created
	 * @throws RoomNotFoundException 
	 * @throws ServerSideErrorException 
	 * @throws ConnectionEndedException 
	 * @throws IOException 
	 * @see					#m_existingRooms
	 */
	final protected void updateCreatedRoom(int roomID) throws ConnectionEndedException, ServerSideErrorException
	{
		try
		{
			this.m_roomServers[roomID] = new RoomServer(roomID, getRoomCreatorName(roomID), getRoomModificationPassword(roomID), getRoomJoinPassword(roomID), getRoomWhiteboardLength(roomID), getRoomWhiteboardWidth(roomID));
			this.m_roomServers[roomID].start();
			this.m_existingRooms[roomID] = true;
		} catch (RoomNotFoundException e)
		{
			//if room not found on database, then don't update it here either
		} catch (IOException e)
		{
			//if room server could not be started, then ignore
		}
	}
	
	final protected static String getRoomName(int roomID) throws ConnectionEndedException, ServerSideErrorException, RoomNotFoundException
	{
		//try to get the room name from the database
		String message = database.MESSAGES.ROOMDATA.HEADING + database.MESSAGES.DELIMITER + database.MESSAGES.ROOMDATA.GET_ROOM_NAME + database.MESSAGES.DELIMITER + roomID;
		String response = DatabaseConnection.sendMessageAndGetResponse(message);
		//look through the response
		Scanner scanResponse = new Scanner(response);
		//the first part should be the result
		String result = scanResponse.next();
		if (result.equals(database.MESSAGES.ROOMDATA.GET_ROOM_NAME_SUCCESS))
		{
			//if the operation was successful, then the next part should be the room name
			String roomName = scanResponse.next();
			return roomName;
		} else if (result.equals(database.MESSAGES.ROOMDATA.GET_ROOM_NAME_FAILED))
		{
			throw generateRoomNotFoundException();
		} else
		{
			throw generateDatabaseMiscommunicationError(response);
		}	
	}
	
	final protected static String getRoomCreatorName(int roomID) throws ConnectionEndedException, ServerSideErrorException, RoomNotFoundException
	{
		//try to get the room name from the database
		String message = database.MESSAGES.ROOMDATA.HEADING + database.MESSAGES.DELIMITER + database.MESSAGES.ROOMDATA.GET_ROOM_CREATOR_USERNAME + database.MESSAGES.DELIMITER + roomID;
		String response = DatabaseConnection.sendMessageAndGetResponse(message);
		//look through the response
		Scanner scanResponse = new Scanner(response);
		//the first part should be the result
		String result = scanResponse.next();
		if (result.equals(database.MESSAGES.ROOMDATA.GET_ROOM_CREATOR_USERNAME_SUCCESS))
		{
			//if the operation was successful, then the next part should be the creator username
			String creatorName = scanResponse.next();
			return creatorName;
		} else if (result.equals(database.MESSAGES.ROOMDATA.GET_ROOM_CREATOR_USERNAME_FAILED))
		{
			throw generateRoomNotFoundException();
		} else
		{
			throw generateDatabaseMiscommunicationError(response);
		}
	}
	
	final protected static String getRoomCreationDate(int roomID) throws ConnectionEndedException, ServerSideErrorException, RoomNotFoundException
	{
		//try to get the creation date from the database
		String message = database.MESSAGES.ROOMDATA.HEADING + database.MESSAGES.DELIMITER + database.MESSAGES.ROOMDATA.GET_ROOM_CREATION_DATE + database.MESSAGES.DELIMITER + roomID;
		String response = DatabaseConnection.sendMessageAndGetResponse(message);
		//look through the response
		Scanner scanResponse = new Scanner(response);
		//the first part should be the result
		String result = scanResponse.next();
		if (result.equals(database.MESSAGES.ROOMDATA.GET_ROOM_CREATION_DATE_SUCCESS))
		{
			//if the operation was successful, then the next part should be the creation date
			String creationDate = scanResponse.next();
			return creationDate;
		} else if (result.equals(database.MESSAGES.ROOMDATA.GET_ROOM_CREATION_DATE_FAILED))
		{
			throw generateRoomNotFoundException();
		} else
		{
			throw generateDatabaseMiscommunicationError(response);
		}
	}
	
	
	final protected static String getRoomModificationPassword(int roomID) throws ConnectionEndedException, RoomNotFoundException, ServerSideErrorException
	{
		String message = database.MESSAGES.ROOMDATA.HEADING + database.MESSAGES.DELIMITER + database.MESSAGES.ROOMDATA.GET_ROOM_MODIFICATION_PASSWORD + database.MESSAGES.DELIMITER + roomID;
		String response = DatabaseConnection.sendMessageAndGetResponse(message);
		//look through the response
		Scanner scanResponse = new Scanner(response);
		//first part should be result
		String result = scanResponse.next();
		if (result.equals(database.MESSAGES.ROOMDATA.GET_ROOM_MODIFICATION_PASSWORD_SUCCESS))
		{
			//if operation successful, then next part should be modification password
			String modificationPassword = scanResponse.next();
			return modificationPassword;
		} else if (result.equals(database.MESSAGES.ROOMDATA.GET_ROOM_MODIFICATION_PASSWORD_FAILED))
		{
			throw generateRoomNotFoundException();
		} else
		{
			throw generateDatabaseMiscommunicationError(response);
		}
	}
	
	final protected static boolean getRoomPasswordProtection(int roomID) throws ConnectionEndedException, ServerSideErrorException, RoomNotFoundException
	{
		//try to get the password protection boolean from the database
		String message = database.MESSAGES.ROOMDATA.HEADING + database.MESSAGES.DELIMITER + database.MESSAGES.ROOMDATA.GET_ROOM_PASSWORD_PROTECTION + database.MESSAGES.DELIMITER + roomID;
		String response = DatabaseConnection.sendMessageAndGetResponse(message);
		//look through the response
		Scanner scanResponse = new Scanner(response);
		//the first part should be the result
		String result = scanResponse.next();
		if (result.equals(database.MESSAGES.ROOMDATA.GET_ROOM_PASSWORD_PROTECTION_SUCCESS))
		{
			//if the operation was successful, then the next part should be the password protection
			boolean passwordProtection = scanResponse.nextBoolean();
			return passwordProtection;
		} else if (result.equals(database.MESSAGES.ROOMDATA.GET_ROOM_PASSWORD_PROTECTION_FAILED))
		{
			throw generateRoomNotFoundException();
		} else
		{
			throw generateDatabaseMiscommunicationError(response);
		}
	}
	
	
	final protected static String getRoomJoinPassword(int roomID) throws ConnectionEndedException, RoomNotFoundException, ServerSideErrorException
	{
		String message = database.MESSAGES.ROOMDATA.HEADING + database.MESSAGES.DELIMITER + database.MESSAGES.ROOMDATA.GET_ROOM_JOIN_PASSWORD + database.MESSAGES.DELIMITER + roomID;
		String response = DatabaseConnection.sendMessageAndGetResponse(message);
		//look through the response
		Scanner scanResponse = new Scanner(response);
		//first part should be result
		String result = scanResponse.next();
		if (result.equals(database.MESSAGES.ROOMDATA.GET_ROOM_JOIN_PASSWORD_SUCCESS))
		{
			//if operation successful, then next part should be modification password
			String modificationPassword = scanResponse.next();
			return modificationPassword;
		} else if (result.equals(database.MESSAGES.ROOMDATA.GET_ROOM_JOIN_PASSWORD_FAILED))
		{
			throw generateRoomNotFoundException();
		} else
		{
			throw generateDatabaseMiscommunicationError(response);
		}
	}
	
	final protected static int getRoomWhiteboardLength(int roomID) throws ConnectionEndedException, ServerSideErrorException, RoomNotFoundException
	{
		//try to get the whiteboard length from the database
		String message = database.MESSAGES.ROOMDATA.HEADING + database.MESSAGES.DELIMITER + database.MESSAGES.ROOMDATA.GET_WHITEBOARD_LENGTH + database.MESSAGES.DELIMITER + roomID;
		String response = DatabaseConnection.sendMessageAndGetResponse(message);
		//look through the response
		Scanner scanResponse = new Scanner(response);
		//the first part should be the result
		String result = scanResponse.next();
		if (result.equals(database.MESSAGES.ROOMDATA.GET_WHITEBOARD_LENGTH_SUCCESS))
		{
			//if the operation was successful, then the next part should be the length
			int length = scanResponse.nextInt();
			return length;
		} else if (result.equals(database.MESSAGES.ROOMDATA.GET_WHITEBOARD_LENGTH_FAILED))
		{
			throw generateRoomNotFoundException();
		} else
		{
			throw generateDatabaseMiscommunicationError(response);
		}
	}
	
	final protected static int getRoomWhiteboardWidth(int roomID) throws ConnectionEndedException, ServerSideErrorException, RoomNotFoundException
	{
		//try to get the whiteboard width from the database
		String message = database.MESSAGES.ROOMDATA.HEADING + database.MESSAGES.DELIMITER + database.MESSAGES.ROOMDATA.GET_WHITEBOARD_WIDTH + database.MESSAGES.DELIMITER + roomID;
		String response = DatabaseConnection.sendMessageAndGetResponse(message);
		//look through the response
		Scanner scanResponse = new Scanner(response);
		//the first part should be the result
		String result = scanResponse.next();
		if (result.equals(database.MESSAGES.ROOMDATA.GET_WHITEBOARD_WIDTH_SUCCESS))
		{
			//if the operation was successful, then the next part should be the width
			int width = scanResponse.nextInt();
			return width;
		} else if (result.equals(database.MESSAGES.ROOMDATA.GET_WHITEBOARD_WIDTH_FAILED))
		{
			throw generateRoomNotFoundException();
		} else
		{
			throw generateDatabaseMiscommunicationError(response);
		}
	}
	
	final private static RoomNotFoundException generateRoomNotFoundException()
	{
		return new RoomNotFoundException();
	}
	
	final private static ServerSideErrorException generateDatabaseMiscommunicationError(String message)
	{
		return new ServerSideErrorException(ServerSideErrorException.DATABASE_MISCOMMUNICATION_ERROR, message);
	}
	
	final private class RoomDataSubServer extends SubServer
	{
		
		public RoomDataSubServer(Socket stepConnection, Socket concurrencyConnection) throws IOException
		{
			super(stepConnection, concurrencyConnection);
		}
		
		@Override
		final protected void handleTerminatingConnection()
		{
			this.closeAndStop();
			RoomDataServer.this.removeAClient(this.m_subServerID);
		}
		
		@Override
		final protected void decode(String messageFromClient) throws ConnectionEndedException, ServerSideErrorException, TamperedClientException, NoSuchElementException
		{
			String message = messageFromClient;
			//look through the message
			Scanner scanMessage = new Scanner(message);
			String nextPart = scanMessage.next();
			if (nextPart.equals(MESSAGES.GENERAL.LOGIN))
			{
				//expect a username and password
				String username = scanMessage.next();
				String password = scanMessage.next();
				login(username, password);
			} else if (this.loggedIn)
			{
				if (nextPart.equals(MESSAGES.ROOMDATASERVER.GET_MAXIMUM_ROOMS_STORED))
				{
					//expect a username
					String username = scanMessage.next();
					sendMaximumRoomsStoredToClient(username);
				} else if (nextPart.equals(MESSAGES.ROOMDATASERVER.GET_EXISTING_ROOM_LIST))
				{
					String username = scanMessage.next();
					sendExistingRoomListToClient(username);
				} else if (nextPart.equals(MESSAGES.ROOMDATASERVER.GET_ROOM_INFORMATION))
				{
					//expect a username and a room ID
					String username = scanMessage.next();
					int roomID = scanMessage.nextInt();
					sendRoomDataToClient(username, roomID);
				} else if (nextPart.equals(MESSAGES.ROOMDATASERVER.SET_ROOM_NAME))
				{
					//expect username, a room ID, a modification password and a new room name
					String username = scanMessage.next();
					int roomID = scanMessage.nextInt();
					String modificationPassword = scanMessage.next();
					String newRoomName = scanMessage.next();
					setRoomName(username, roomID, modificationPassword, newRoomName);
				} else if (nextPart.equals(MESSAGES.ROOMDATASERVER.SET_ROOM_PASSWORD_PROTECTION))
				{
					//expect a username, a room ID, a modification password and a password protection boolean
					String username = scanMessage.next();
					int roomID = scanMessage.nextInt();
					String modificationPassword = scanMessage.next();
					boolean passwordProtection = scanMessage.nextBoolean();
					setRoomPasswordProtected(username, roomID, modificationPassword, passwordProtection);
				} else if (nextPart.equals(MESSAGES.ROOMDATASERVER.SET_ROOM_JOIN_PASSWORD))
				{
					//expect a username, a room ID, a modification password, the old join password and
					//the new join password
					String username = scanMessage.next();
					int roomID = scanMessage.nextInt();
					String modificationPassword = scanMessage.next();
					String oldJoinPassword = scanMessage.next();
					String newJoinPassword = scanMessage.next();
					setRoomJoinPassword(username, roomID, modificationPassword, oldJoinPassword, newJoinPassword);
				} else if (nextPart.equals(MESSAGES.ROOMDATASERVER.SET_ROOM_MODIFICATION_PASSWORD))
				{
					//expect a username, a room ID, an old modification password and a new modification password
					String username = scanMessage.next();
					int roomID = scanMessage.nextInt();
					String oldModificationPassword = scanMessage.next();
					String newModificationPassword = scanMessage.next();
					setRoomModificationPassword(username, roomID, oldModificationPassword, newModificationPassword);
				} else if (nextPart.equals(MESSAGES.ROOMDATASERVER.CREATE_ROOM))
				{
					int roomID = scanMessage.nextInt();
					String roomName = scanMessage.next();
					String creatorName = scanMessage.next();
					String creationDate = DatabaseConnection.substituteForMessageDelimiters(Calendar.getInstance().getTime().toString());
					String modificationPassword = scanMessage.next();
					boolean passwordProtection = scanMessage.nextBoolean();
					String joinPassword = scanMessage.next();
					int whiteboardLength = scanMessage.nextInt();
					int whiteboardWidth = scanMessage.nextInt();
					createRoom(roomID, roomName, creatorName, creationDate, modificationPassword, passwordProtection, joinPassword, whiteboardLength, whiteboardWidth);
				} else if (nextPart.equals(MESSAGES.ROOMDATASERVER.DELETE_ROOM))
				{
					String username = scanMessage.next();
					String password = scanMessage.next();
					int roomID = scanMessage.nextInt();
					//TODO implement room deletion
				} else
				{
					super.logUnknownClientMessage(Text.ROOMDATASERVER.NAME_OF_SERVER, message, this.m_ip);
				}
			} else
			{
				super.closeClientPerformingUnauthorizedActions();
			}
			scanMessage.close();
		}
		
		final private void createRoom(int roomID, String roomName, String creatorName, String creationDate, String modificationPassword, boolean passwordProtection, String joinPassword, int whiteboardLength, int whiteboardWidth) throws ConnectionEndedException, ServerSideErrorException, TamperedClientException
		{
			assertUsernameIsCorrect(creatorName);
			//make sure the room name is valid
			if (isRoomNameValid(roomName))
			{
				//make sure the modification password is valid
				if (isModificationPasswordValid(modificationPassword))
				{
					//make sure the join password is valid
					if (isJoinPasswordValid(joinPassword))
					{
						//make sure the whiteboard dimensions are valid
						if (isValidDimensions(whiteboardLength, whiteboardWidth))
						{
							//try to get the database to create the room
							String message = database.MESSAGES.ROOMDATA.HEADING + database.MESSAGES.DELIMITER + database.MESSAGES.ROOMDATA.ROOMDATASERVER.ROOM_DATA_SERVER_HEADING + database.MESSAGES.DELIMITER 
								+ database.MESSAGES.ROOMDATA.ROOMDATASERVER.CREATE_ROOM + database.MESSAGES.DELIMITER + roomID + database.MESSAGES.DELIMITER + roomName + database.MESSAGES.DELIMITER + creatorName + database.MESSAGES.DELIMITER + creationDate 
								+ database.MESSAGES.DELIMITER + modificationPassword + database.MESSAGES.DELIMITER + passwordProtection + database.MESSAGES.DELIMITER + joinPassword + database.MESSAGES.DELIMITER + whiteboardLength + database.MESSAGES.DELIMITER + whiteboardWidth;
							String response = DatabaseConnection.sendMessageAndGetResponse(message);
							//look through the response
							Scanner scanResponse = new Scanner(response);
							//the first part should be the result
							String result = scanResponse.next();
							if (result.equals(database.MESSAGES.ROOMDATA.ROOMDATASERVER.CREATE_ROOM_SUCCESS))
							{
								//if the operation was successful,
								//update
								RoomDataServer.this.updateCreatedRoom(roomID);
								write(MESSAGES.ROOMDATASERVER.CREATE_ROOM_SUCCESS);
							} else if (result.equals(database.MESSAGES.ROOMDATA.ROOMDATASERVER.CREATE_ROOM_FAILED))
							{
								//if the operation failed, figure out why and let the client know
								//begin writing the message. we will just need an error code
								String messageToClient = MESSAGES.ROOMDATASERVER.CREATE_ROOM_FAILED + MESSAGES.DELIMITER;
								//an error code should follow the result
								int errorCode = scanResponse.nextInt();
								if (errorCode == database.MESSAGES.ROOMDATA.ROOMDATASERVER.ROOM_ID_TAKEN_ERROR_CODE)
								{
									messageToClient += MESSAGES.ROOMDATASERVER.ROOM_ID_TAKEN_ERROR_CODE;
								} else if (errorCode == database.MESSAGES.ROOMDATA.ROOMDATASERVER.IO_EXCEPTION_ERROR_CODE)
								{
									messageToClient += MESSAGES.ROOMDATASERVER.IO_EXCEPTION_ERROR_CODE;
								} else
								{
									throw generateDatabaseMiscommunicationError(response);
								}
								write(messageToClient);
							} else
							{
								throw generateDatabaseMiscommunicationError(response);
							}
						} else
						{
							write(MESSAGES.ROOMDATASERVER.CREATE_ROOM_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMDATASERVER.INVALID_DIMENSIONS_ERROR_CODE);
						}
					} else
					{
						write(MESSAGES.ROOMDATASERVER.CREATE_ROOM_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMDATASERVER.INVALID_JOIN_PASSWORD_ERROR_CODE);
					}
				} else
				{
					write(MESSAGES.ROOMDATASERVER.CREATE_ROOM_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMDATASERVER.INVALID_MODIFICATION_PASSWORD_ERROR_CODE);
				}
			} else
			{
				write(MESSAGES.ROOMDATASERVER.CREATE_ROOM_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMDATASERVER.INVALID_ROOM_NAME_ERROR_CODE);
			}
		}
		
			final private boolean isValidDimensions(int whiteboardLength, int whiteboardWidth)
			{
				if (whiteboardLength < 500)
				{
					return false;
				}
				if (whiteboardLength > 1000)
				{
					return false;
				}
				if (whiteboardWidth < 500)
				{
					return false;
				}
				if (whiteboardWidth > 1000)
				{
					return false;
				}
				return true;
			}
		
		final private void setRoomName(String username, int roomID, String modificationPassword, String newRoomName) throws ConnectionEndedException, ServerSideErrorException, TamperedClientException
		{
			assertUsernameIsCorrect(username);
			//make sure the new room name is valid
			if (isRoomNameValid(newRoomName))
			{
				//try to get the database to set the room name
				String message = database.MESSAGES.ROOMDATA.HEADING + database.MESSAGES.DELIMITER + database.MESSAGES.ROOMDATA.ROOMDATASERVER.ROOM_DATA_SERVER_HEADING + database.MESSAGES.DELIMITER 
						+ database.MESSAGES.ROOMDATA.ROOMDATASERVER.SET_ROOM_NAME + database.MESSAGES.DELIMITER + roomID + database.MESSAGES.DELIMITER + modificationPassword + database.MESSAGES.DELIMITER + newRoomName;
				String response = DatabaseConnection.sendMessageAndGetResponse(message);
				//look through the response
				Scanner scanResponse = new Scanner(response);
				//the first part should be the result
				String result = scanResponse.next();
				if (result.equals(database.MESSAGES.ROOMDATA.ROOMDATASERVER.SET_ROOM_NAME_SUCCESS))
				{
					//if the operation was successful, let the client know
					write(MESSAGES.ROOMDATASERVER.SET_ROOM_NAME_SUCCESS);
				} else if (result.equals(database.MESSAGES.ROOMDATA.ROOMDATASERVER.SET_ROOM_NAME_FAILED))
				{
					//if the operation failed, then figure out why and let the client know
					//begin writing the message. we just need an error code
					String messageToClient = MESSAGES.ROOMDATASERVER.SET_ROOM_NAME_FAILED + MESSAGES.DELIMITER;
					//there should be an error code next
					int errorCode = scanResponse.nextInt();
					if (errorCode == database.MESSAGES.ROOMDATA.NONEXISTING_ROOM_ERROR_CODE)
					{
						messageToClient += MESSAGES.ROOMDATASERVER.NONEXISTING_ROOM_ERROR_CODE;
					} else if (errorCode == database.MESSAGES.ROOMDATA.INVALID_MODIFICATION_PASSWORD_ERROR_CODE)
					{
						messageToClient += MESSAGES.ROOMDATASERVER.BAD_MODIFICATION_PASSWORD_ERROR_CODE;
					} else
					{
						throw generateDatabaseMiscommunicationError(response);
					}
					write(messageToClient);
				} else
				{
					throw generateDatabaseMiscommunicationError(response);
				}
			} else
			{
				write(MESSAGES.ROOMDATASERVER.SET_ROOM_NAME_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMDATASERVER.INVALID_NEW_ROOM_NAME_ERROR_CODE);
			}
		}
			
			final private boolean isRoomNameValid(String roomName)
			{
				//TODO
				return true;
			}
			
		final private void setRoomPasswordProtected(String username, int roomID, String modificationPassword, boolean passwordProtection) throws ConnectionEndedException, ServerSideErrorException, TamperedClientException
		{
			assertUsernameIsCorrect(username);
			//try to get the database to set the password protection
			String message = database.MESSAGES.ROOMDATA.HEADING + database.MESSAGES.DELIMITER + database.MESSAGES.ROOMDATA.ROOMDATASERVER.ROOM_DATA_SERVER_HEADING + database.MESSAGES.DELIMITER 
				+ database.MESSAGES.ROOMDATA.ROOMDATASERVER.SET_ROOM_PASSWORD_PROTECTION + database.MESSAGES.DELIMITER + roomID + database.MESSAGES.DELIMITER + modificationPassword + database.MESSAGES.DELIMITER + passwordProtection;
			String response = DatabaseConnection.sendMessageAndGetResponse(message);
			//look through the response
			Scanner scanResponse = new Scanner(response);
			//the result should be the next part
			String result = scanResponse.next();
			if (result.equals(database.MESSAGES.ROOMDATA.ROOMDATASERVER.SET_ROOM_PASSWORD_PROTECTION_SUCCESS))
			{
				//if the operation was successful, let the client know
				write(MESSAGES.ROOMDATASERVER.SET_ROOM_PASSWORD_PROTECTION_SUCCESS);
			} else if (result.equals(database.MESSAGES.ROOMDATA.ROOMDATASERVER.SET_ROOM_PASSWORD_PROTECTION_FAILED))
			{
				//if the operation failed, figure out why and let the client know
				//begin writing the message. we need an error code after this
				String messageToClient = MESSAGES.ROOMDATASERVER.SET_ROOM_PASSWORD_PROTECTION_FAILED + MESSAGES.DELIMITER;
				//an error code should follow the result
				int errorCode = scanResponse.nextInt();
				if (errorCode == database.MESSAGES.ROOMDATA.NONEXISTING_ROOM_ERROR_CODE)
				{
					messageToClient += MESSAGES.ROOMDATASERVER.NONEXISTING_ROOM_ERROR_CODE;
				} else if (errorCode == database.MESSAGES.ROOMDATA.INVALID_MODIFICATION_PASSWORD_ERROR_CODE)
				{
					messageToClient += MESSAGES.ROOMDATASERVER.BAD_MODIFICATION_PASSWORD_ERROR_CODE;
				} else
				{
					throw generateDatabaseMiscommunicationError(response);
				}
				write(messageToClient);
			} else
			{
				throw generateDatabaseMiscommunicationError(response);
			}
		}
		
		final private void setRoomJoinPassword(String username, int roomID, String modificationPassword, String oldJoinPassword, String newJoinPassword) throws ConnectionEndedException, ServerSideErrorException, TamperedClientException
		{
			assertUsernameIsCorrect(username);
			//make sure the join password is valid
			if (isJoinPasswordValid(newJoinPassword))
			{
				//try to get the database to set the join password
				String message = database.MESSAGES.ROOMDATA.HEADING + database.MESSAGES.DELIMITER + database.MESSAGES.ROOMDATA.ROOMDATASERVER.ROOM_DATA_SERVER_HEADING + database.MESSAGES.DELIMITER
					+ database.MESSAGES.ROOMDATA.ROOMDATASERVER.SET_ROOM_JOIN_PASSWORD + database.MESSAGES.DELIMITER + roomID + database.MESSAGES.DELIMITER + modificationPassword + database.MESSAGES.DELIMITER + oldJoinPassword + database.MESSAGES.DELIMITER + newJoinPassword;
				String response = DatabaseConnection.sendMessageAndGetResponse(message);
				//look through the response
				Scanner scanResponse = new Scanner(response);
				//the first part should be the result
				String result = scanResponse.next();
				if (result.equals(database.MESSAGES.ROOMDATA.ROOMDATASERVER.SET_ROOM_JOIN_PASSWORD_SUCCESS))
				{
					//if the operation was successful, then let the client know
					write(MESSAGES.ROOMDATASERVER.SET_ROOM_JOIN_PASSWORD_SUCCESS);
				} else if (result.equals(database.MESSAGES.ROOMDATA.ROOMDATASERVER.SET_ROOM_JOIN_PASSWORD_FAILED))
				{
					//if the operation failed, figure out why, then let the client know
					//begin writing the message. we will still need an error code
					String messageToClient = MESSAGES.ROOMDATASERVER.SET_ROOM_JOIN_PASSWORD_FAILED + MESSAGES.DELIMITER;
					//there should be an error code following the result
					int errorCode = scanResponse.nextInt();
					if (errorCode == database.MESSAGES.ROOMDATA.NONEXISTING_ROOM_ERROR_CODE)
					{
						messageToClient += MESSAGES.ROOMDATASERVER.NONEXISTING_ROOM_ERROR_CODE;
					} else if (errorCode == database.MESSAGES.ROOMDATA.INVALID_MODIFICATION_PASSWORD_ERROR_CODE)
					{
						messageToClient += MESSAGES.ROOMDATASERVER.BAD_MODIFICATION_PASSWORD_ERROR_CODE;
					} else if (errorCode == database.MESSAGES.ROOMDATA.INVALID_JOIN_PASSWORD_ERROR_CODE)
					{
						messageToClient += MESSAGES.ROOMDATASERVER.BAD_JOIN_PASSWORD_ERROR_CODE;
					} else
					{
						throw generateDatabaseMiscommunicationError(response);
					}
					write(messageToClient);
				} else
				{
					throw generateDatabaseMiscommunicationError(response);
				}
			} else
			{
				write(MESSAGES.ROOMDATASERVER.SET_ROOM_JOIN_PASSWORD_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMDATASERVER.INVALID_NEW_JOIN_PASSWORD_ERROR_CODE);
			}
		}
		
			final private boolean isJoinPasswordValid(String joinPassword)
			{
				//TODO
				return true;
			}
		
		final private void setRoomModificationPassword(String username, int roomID, String oldModificationPassword, String newModificationPassword) throws ConnectionEndedException, ServerSideErrorException, TamperedClientException
		{
			assertUsernameIsCorrect(username);
			//make sure the new modification password is valid
			if (isModificationPasswordValid(newModificationPassword))
			{
				//try to get the database to set the modification password
				String message = database.MESSAGES.ROOMDATA.HEADING + database.MESSAGES.DELIMITER + database.MESSAGES.ROOMDATA.ROOMDATASERVER.ROOM_DATA_SERVER_HEADING + database.MESSAGES.DELIMITER 
					+ database.MESSAGES.ROOMDATA.ROOMDATASERVER.SET_ROOM_MODIFICATION_PASSWORD + database.MESSAGES.DELIMITER + roomID + database.MESSAGES.DELIMITER + oldModificationPassword + database.MESSAGES.DELIMITER + newModificationPassword;
				String response = DatabaseConnection.sendMessageAndGetResponse(message);
				//look through the response
				Scanner scanResponse = new Scanner(response);
				//the first part should be the result
				String result = scanResponse.next();
				if (result.equals(database.MESSAGES.ROOMDATA.ROOMDATASERVER.SET_ROOM_MODIFICATION_PASSWORD_SUCCESS))
				{
					//if the operation was successful, let the client know
					write(MESSAGES.ROOMDATASERVER.SET_ROOM_MODIFICATION_PASSWORD_SUCCESS);
				} else if (result.equals(database.MESSAGES.ROOMDATA.ROOMDATASERVER.SET_ROOM_MODIFICATION_PASSWORD_FAILED))
				{
					//if the operation failed, figure out why, then let the client know
					//begin writing the message. we will still need an error code
					String messageToClient = MESSAGES.ROOMDATASERVER.SET_ROOM_MODIFICATION_PASSWORD_FAILED + MESSAGES.DELIMITER;
					//an error code should follow the result
					int errorCode = scanResponse.nextInt();
					if (errorCode == database.MESSAGES.ROOMDATA.NONEXISTING_ROOM_ERROR_CODE)
					{
						messageToClient += MESSAGES.ROOMDATASERVER.NONEXISTING_ROOM_ERROR_CODE;
					} else if (errorCode == database.MESSAGES.ROOMDATA.INVALID_MODIFICATION_PASSWORD_ERROR_CODE)
					{
						messageToClient += MESSAGES.ROOMDATASERVER.BAD_MODIFICATION_PASSWORD_ERROR_CODE;
					} else
					{
						throw generateDatabaseMiscommunicationError(response);
					}
					write(messageToClient);
				} else
				{
					throw generateDatabaseMiscommunicationError(response);
				}
			} else
			{
				write(MESSAGES.ROOMDATASERVER.SET_ROOM_MODIFICATION_PASSWORD_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMDATASERVER.INVALID_NEW_MODIFICATION_PASSWORD_ERROR_CODE);
			}
		}
		
			final private boolean isModificationPasswordValid(String modificationPassword)
			{
				//TODO
				return true;
			}
			
		final private void sendMaximumRoomsStoredToClient(String username) throws ConnectionEndedException, ServerSideErrorException, TamperedClientException
		{
			assertUsernameIsCorrect(username);
			String message = database.MESSAGES.ROOMDATA.HEADING + database.MESSAGES.DELIMITER + database.MESSAGES.ROOMDATA.GET_MAXIMUM_ROOMS_STORED;
			String response = DatabaseConnection.sendMessageAndGetResponse(message);
			//look through the response
			Scanner scanResponse = new Scanner(response);
			String result = scanResponse.next();
			//the result should be successful
			if (result.equals(database.MESSAGES.ROOMDATA.GET_MAXIMUM_ROOMS_STORED_SUCCESS))
			{
				//the maximum number of rooms stored should follow
				int maxRoomsStored = scanResponse.nextInt();
				scanResponse.close();
				write(MESSAGES.ROOMDATASERVER.GET_MAXIMUM_ROOMS_STORED_SUCCESS + MESSAGES.DELIMITER + maxRoomsStored);
			} else
			{
				scanResponse.close();
				throw generateDatabaseMiscommunicationError(response);
			}
		}
		
		final private void sendExistingRoomListToClient(String username) throws TamperedClientException
		{
			assertUsernameIsCorrect(username);
			int numberOfExistingRooms = RoomDataServer.this.getNumberOfExistingRooms();
			String listOfExistingRooms = "";
			for (int roomIndex = 0; roomIndex < RoomDataServer.this.getMaximumRoomsStored(); roomIndex++)
			{
				if (RoomDataServer.this.doesRoomExist(roomIndex) == true)
				{
					listOfExistingRooms += roomIndex + MESSAGES.DELIMITER;
				}
			}
			write(MESSAGES.ROOMDATASERVER.GET_EXISTING_ROOM_LIST_SUCCESS + MESSAGES.DELIMITER + numberOfExistingRooms + MESSAGES.DELIMITER + listOfExistingRooms);
		}
		
		final private void sendRoomDataToClient(String username, int roomID) throws ConnectionEndedException, ServerSideErrorException, TamperedClientException
		{
			assertUsernameIsCorrect(username);
			//try to get the room information from the database
			if (roomID < RoomDataServer.this.getMaximumRoomsStored() && RoomDataServer.this.doesRoomExist(roomID) == true)
			{
				try
				{
					String roomName = getRoomName(roomID);
					String creatorName = getRoomCreatorName(roomID);
					String creationDate = getRoomCreationDate(roomID);
					boolean passwordProtection = getRoomPasswordProtection(roomID);
					int whiteboardLength = getRoomWhiteboardLength(roomID);
					int whiteboardWidth = getRoomWhiteboardWidth(roomID);
					write(MESSAGES.ROOMDATASERVER.GET_ROOM_INFORMATION_SUCCESS + MESSAGES.DELIMITER + roomName + MESSAGES.DELIMITER 
						+ creatorName + MESSAGES.DELIMITER + creationDate + MESSAGES.DELIMITER + passwordProtection + MESSAGES.DELIMITER + whiteboardLength + MESSAGES.DELIMITER + whiteboardWidth);
				} catch (RoomNotFoundException e)
				{
					write(MESSAGES.ROOMDATASERVER.GET_ROOM_INFORMATION_FAILED);
				}
			} else
			{
				write(MESSAGES.ROOMDATASERVER.GET_ROOM_INFORMATION_FAILED);
			}
		}
		
		@Override
		final protected String read() throws IOException
		{
			String message = super.read();
			System.out.println("Room Data Server received from client: " + message);
			return message;
		}
		
		@Override
		final protected void write(String message)
		{
			System.out.println("Room Data Server wrote to client: " + message);
			super.write(message);
		}
	}
}
