package net.roomdataserver;

import gui.login.Login;
import gui.roomdata.RoomData;

import java.io.IOException;
import java.util.Scanner;

import util.CommonMethods;
import util.Text;

import net.InvalidMessageException;
import net.MESSAGES;
import net.OperationErrorCode;
import net.OperationFailedException;
import net.StepConnection;
import net.messagingserver.MessagingServerStepConnection;

public class RoomDataServerStepConnection extends StepConnection
{

	/**
	 * Stores which rooms have been created and which have not
	 */
	private boolean[] m_existingRooms;
	/**
	 * Used for determining the display names of room creators
	 */
	final private MessagingServerStepConnection m_messagingServerStepConnection;
	
	public RoomDataServerStepConnection(String ip, int port, MessagingServerStepConnection messagingServerStepConnection) throws IOException 
	{
		super(ip, port);
		this.m_messagingServerStepConnection = messagingServerStepConnection;
	}
	
	@Override
	final protected synchronized String sendMessageAndGetResponse(String messageToRoomDataServer) throws IOException
	{
		System.out.println("Room Data Server: Sent message to server: " + messageToRoomDataServer);
		String response = super.sendMessageAndGetResponse(messageToRoomDataServer);
		System.out.println("Room Data Server: Received message from server: " + response);
		return response;
	}
	
	final public void attemptLogin()
	{
		//only load the client gui if we can connect to the room data server server
		try
		{
			if (attemptLogin(Login.m_username, Login.m_password))
			{
				//continue
				this.m_existingRooms = new boolean[getMaximumRoomsStored(Login.m_username)];
			} else
			{
				CommonMethods.displayErrorMessage(Text.NET.GENERAL.LOGIN_FAILED_ERROR_MESSAGE);
				return;
			}
		} catch (IOException connectionError)
		{
			CommonMethods.displayErrorMessage(Text.NET.getConnectionLostMessage(Text.NET.ROOM_DATA_SERVER_NAME));
			System.out.println("Lost connection");
			return;
		} catch (InvalidMessageException invalidMessage)
		{
			this.handleInvalidMessageException(invalidMessage);
			return;
		}
	}
	
	final public int getMaximumRoomsStored(String username) throws IOException, InvalidMessageException
	{
		String message = MESSAGES.ROOMDATASERVER.GET_MAXIMUM_ROOMS_STORED + MESSAGES.DELIMITER + username;
		String response = sendMessageAndGetResponse(message);
		//look through the response
		Scanner scanResponse = new Scanner(response);
		//the first part should be the result
		String result = scanResponse.next();
		if (result.equals(MESSAGES.ROOMDATASERVER.GET_MAXIMUM_ROOMS_STORED_SUCCESS))
		{
			//an integer should follow
			int maxRoomsStored = scanResponse.nextInt();
			scanResponse.close();
			return maxRoomsStored;
		}
		scanResponse.close();
		throw generateInvalidMessageException(response);
	}
	
	final public RoomData getRoomData(String username, int roomID) throws IOException, OperationFailedException, InvalidMessageException
	{
		String message = MESSAGES.ROOMDATASERVER.GET_ROOM_INFORMATION + MESSAGES.DELIMITER + username + MESSAGES.DELIMITER + roomID;
		String response = sendMessageAndGetResponse(message);
		//look through the response
		Scanner scanResponse = new Scanner(response);
		//the first part should be the result
		String result = scanResponse.next();
		if (result.equals(MESSAGES.ROOMDATASERVER.GET_ROOM_INFORMATION_SUCCESS))
		{
			//if successful, then read in the information
			String roomName = MESSAGES.unsubstituteForMessageDelimiters(scanResponse.next());
			String creatorName = MESSAGES.unsubstituteForMessageDelimiters(scanResponse.next());
			String creationDate = MESSAGES.unsubstituteForMessageDelimiters(scanResponse.next());
			boolean passwordProtected = scanResponse.nextBoolean();
			int whiteboardLength = scanResponse.nextInt();
			int whiteboardWidth = scanResponse.nextInt();
			scanResponse.close();
			RoomData dataOfRoom = new RoomData(roomID, roomName, creatorName, creationDate, 
												passwordProtected, whiteboardLength, whiteboardWidth);
			return dataOfRoom;
		} else if (result.equals(MESSAGES.ROOMDATASERVER.GET_ROOM_INFORMATION_FAILED))
		{
			//if failed, then the room must not exist
			scanResponse.close();
			throw new OperationFailedException(OperationErrorCode.ROOM_ID_DOES_NOT_EXIST);
		} else
		{
			scanResponse.close();
			throw generateInvalidMessageException(response);
		}
	}
	
	final public String getDisplayNameOfUser(String username, String password, String targetUsername)
	{
		return this.m_messagingServerStepConnection.getDisplayNameOfUser(username, password, targetUsername);
	}
	
	final public void createRoom(RoomData dataForNewRoom) throws IOException, OperationFailedException, InvalidMessageException
	{
		int roomID = dataForNewRoom.getRoomID();
		String roomName = MESSAGES.substituteForMessageDelimiters(dataForNewRoom.getRoomName());
		String creatorName = MESSAGES.substituteForMessageDelimiters(dataForNewRoom.getCreatorUsername());
		String modificationPassword = MESSAGES.substituteForMessageDelimiters(dataForNewRoom.getModificationPassword());
		boolean passwordProtected = dataForNewRoom.getPasswordProtected();
		String joinPassword = MESSAGES.substituteForMessageDelimiters(dataForNewRoom.getJoinPassword());
		int whiteboardLength = dataForNewRoom.getWhiteboardLength();
		int whiteboardWidth = dataForNewRoom.getWhiteboardWidth();
		String messageToServer = MESSAGES.ROOMDATASERVER.CREATE_ROOM + MESSAGES.DELIMITER
				+ roomID + MESSAGES.DELIMITER + roomName + MESSAGES.DELIMITER + creatorName + MESSAGES.DELIMITER + modificationPassword + MESSAGES.DELIMITER + passwordProtected + MESSAGES.DELIMITER + joinPassword + MESSAGES.DELIMITER + whiteboardLength + MESSAGES.DELIMITER + whiteboardWidth;
		String response = sendMessageAndGetResponse(messageToServer);
		//look through the response
		Scanner scanResponse = new Scanner(response);
		//first part should be result
		String result = scanResponse.next();
		if (result.equals(MESSAGES.ROOMDATASERVER.CREATE_ROOM_SUCCESS))
		{
			//if successful, let user know
			scanResponse.close();
		} else if (result.equals(MESSAGES.ROOMDATASERVER.CREATE_ROOM_FAILED))
		{
			//if failed, error code should follow
			int errorCode = scanResponse.nextInt();
			scanResponse.close();
			if (errorCode == MESSAGES.ROOMDATASERVER.ROOM_ID_TAKEN_ERROR_CODE)
			{
				throw new OperationFailedException(OperationErrorCode.ROOM_ID_TAKEN);
			} else if (errorCode == MESSAGES.ROOMDATASERVER.IO_EXCEPTION_ERROR_CODE)
			{
				throw new OperationFailedException(OperationErrorCode.DATABASE_IO_EXCEPTION);
			} else if (errorCode == MESSAGES.ROOMDATASERVER.INVALID_DIMENSIONS_ERROR_CODE)
			{
				throw new OperationFailedException(OperationErrorCode.WHITEBOARD_DIMENSIONS_INVALID);
			} else if (errorCode == MESSAGES.ROOMDATASERVER.INVALID_JOIN_PASSWORD_ERROR_CODE)
			{
				throw new OperationFailedException(OperationErrorCode.NEW_JOIN_PASSWORD_IS_INVALID);
			} else if (errorCode == MESSAGES.ROOMDATASERVER.INVALID_MODIFICATION_PASSWORD_ERROR_CODE)
			{
				throw new OperationFailedException(OperationErrorCode.NEW_MODIFICATION_PASSWORD_IS_INVALID);
			} else if (errorCode == MESSAGES.ROOMDATASERVER.INVALID_ROOM_NAME_ERROR_CODE)
			{
				throw new OperationFailedException(OperationErrorCode.NEW_ROOM_NAME_IS_INVALID);
			} else
			{
				throw generateInvalidMessageException(response);
			}
		} else
		{
			scanResponse.close();
			throw generateInvalidMessageException(response);
		}
	}
	
	final public void deleteRoom(int roomID)
	{
		//TODO
	}
	
	final public void updateExistingRooms() throws IOException, InvalidMessageException
	{
		String messageToServer = MESSAGES.ROOMDATASERVER.GET_EXISTING_ROOM_LIST + MESSAGES.DELIMITER + Login.m_username;
		String response = sendMessageAndGetResponse(messageToServer);
		//look through the response
		Scanner scanResponse = new Scanner(response);
		//first part should be result
		String result = scanResponse.next();
		if (result.equals(MESSAGES.ROOMDATASERVER.GET_EXISTING_ROOM_LIST_SUCCESS))
		{
			//look through the data
			//first part is number of existing rooms
			int numberOfExistingRooms = scanResponse.nextInt();
			//then followed by IDs of existing rooms
			for (int roomsRead = 0; roomsRead < numberOfExistingRooms; roomsRead++)
			{
				this.m_existingRooms[scanResponse.nextInt()] = true;
			}
			scanResponse.close();
		} else
		{
			scanResponse.close();
			throw generateInvalidMessageException(response);
		}
	}
	
	final public boolean doesRoomExist(int roomID)
	{
		return this.m_existingRooms[roomID];
	}
	
	final public int getNumberOfExistingRooms()
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

	final public static void displayConnectionLost()
	{
		CommonMethods.displayErrorMessage(Text.NET.getConnectionLostMessage(Text.NET.ROOM_DATA_SERVER_NAME));
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

}
