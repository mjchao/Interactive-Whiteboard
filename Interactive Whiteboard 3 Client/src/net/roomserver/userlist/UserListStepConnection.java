package net.roomserver.userlist;

import gui.login.Login;
import gui.room.userlist.UserData;

import java.io.IOException;
import java.util.Scanner;

import util.CommonMethods;
import util.Text;

import net.InvalidMessageException;
import net.MESSAGES;
import net.OperationFailedException;
import net.StepConnection;

public class UserListStepConnection extends StepConnection
{

	final private String m_joinPassword;
	public UserListStepConnection(String ip, int port, String joinPassword) throws IOException 
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
	
	final public void kickUser(UserData targetUserData) throws IOException, InvalidMessageException
	{
		String usernameOfTarget = targetUserData.getUsername();
		String displayNameOfTarget = targetUserData.getDisplayName();
		String messageToServer = MESSAGES.ROOMSERVER.USER_LIST_SERVER.KICK_USER + MESSAGES.DELIMITER + Login.m_username + MESSAGES.DELIMITER + this.m_joinPassword + MESSAGES.DELIMITER + usernameOfTarget;
		String response = sendMessageAndGetResponse(messageToServer);
		//look through the response
		Scanner scanResponse = new Scanner(response);
		//first part should be result
		String result = scanResponse.next();
		if (result.equals(MESSAGES.ROOMSERVER.USER_LIST_SERVER.KICK_USER_SUCCESS))
		{
			//if successful let user know
			scanResponse.close();
			CommonMethods.displaySuccessMessage(Text.GUI.ROOM.USER_LIST.getKickSuccessMessage(displayNameOfTarget));
		} else if (result.equals(MESSAGES.ROOMSERVER.USER_LIST_SERVER.KICK_USER_FAILED))
		{
			//if failed, figure out why and let user know
			//error code should follow
			int errorCode = scanResponse.nextInt();
			scanResponse.close();
			if (errorCode == MESSAGES.ROOMSERVER.AUTHORITY_TOO_LOW_ERROR_CODE)
			{
				CommonMethods.displayErrorMessage(Text.GUI.ROOM.USER_LIST.AUTHORITY_TOO_LOW_ERROR_MESSAGE);
			} else if (errorCode == MESSAGES.ROOMSERVER.INVALID_JOIN_PASSWORD_ERROR_CODE)
			{
				//ignore - this means the client modified the join password stored
			} else
			{
				scanResponse.close();
				throw generateInvalidMessageException(response);
			}
		} else
		{
			scanResponse.close();
			throw generateInvalidMessageException(response);
		}
	}
	
	final public void promoteUser(UserData targetUserData, String modificationPassword) throws IOException, InvalidMessageException
	{
		String messageToServer = MESSAGES.ROOMSERVER.USER_LIST_SERVER.PROMOTE_USER + MESSAGES.DELIMITER + Login.m_username + MESSAGES.DELIMITER + modificationPassword + MESSAGES.DELIMITER + this.m_joinPassword + MESSAGES.DELIMITER + targetUserData.getUsername();
		String response = sendMessageAndGetResponse(messageToServer);
		//look through the response
		Scanner scanResponse = new Scanner(response);
		//first part should be result
		String result = scanResponse.next();
		if (result.equals(MESSAGES.ROOMSERVER.USER_LIST_SERVER.PROMOTE_USER_SUCCESS))
		{
			//if successful, let user know
			scanResponse.close();
			CommonMethods.displaySuccessMessage(Text.GUI.ROOM.USER_LIST.getPromoteSuccessMessage(targetUserData.getDisplayName()));
		} else if (result.equals(MESSAGES.ROOMSERVER.USER_LIST_SERVER.PROMOTE_USER_FAILED))
		{
			//if failed, figure out why and let user know
			//an error code should follow
			int errorCode = scanResponse.nextInt();
			scanResponse.close();
			if (errorCode == MESSAGES.ROOMSERVER.INVALID_MODIFICATION_PASSWORD_ERROR_CODE)
			{
				CommonMethods.displayErrorMessage(Text.GUI.ROOM.INVALID_MODIFICATION_PASSWORD_ERROR_MESSAGE);
			} else if (errorCode == MESSAGES.ROOMSERVER.AUTHORITY_TOO_LOW_ERROR_CODE)
			{
				CommonMethods.displayErrorMessage(Text.GUI.ROOM.USER_LIST.AUTHORITY_TOO_LOW_ERROR_MESSAGE);
			} else if (errorCode == MESSAGES.ROOMSERVER.INVALID_JOIN_PASSWORD_ERROR_CODE)
			{
				//ignore
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
	
	final public void demoteUser(UserData targetUserData, String modificationPassword) throws IOException, InvalidMessageException
	{
		String messageToServer = MESSAGES.ROOMSERVER.USER_LIST_SERVER.DEMOTE_USER + MESSAGES.DELIMITER + Login.m_username + MESSAGES.DELIMITER + modificationPassword + MESSAGES.DELIMITER + this.m_joinPassword + MESSAGES.DELIMITER + targetUserData.getUsername();
		String response = sendMessageAndGetResponse(messageToServer);
		//look through the response
		Scanner scanResponse = new Scanner(response);
		//first part should be result
		String result = scanResponse.next();
		if (result.equals(MESSAGES.ROOMSERVER.USER_LIST_SERVER.DEMOTE_USER_SUCCESS))
		{
			//if successful, let user know
			scanResponse.close();
			CommonMethods.displaySuccessMessage(Text.GUI.ROOM.USER_LIST.getDemoteSuccessMessage(targetUserData.getDisplayName()));
		} else if (result.equals(MESSAGES.ROOMSERVER.USER_LIST_SERVER.DEMOTE_USER_FAILED))
		{
			//if failed, figure out why and let user know
			//an error code should follow
			int errorCode = scanResponse.nextInt();
			scanResponse.close();
			if (errorCode == MESSAGES.ROOMSERVER.INVALID_MODIFICATION_PASSWORD_ERROR_CODE)
			{
				CommonMethods.displayErrorMessage(Text.GUI.ROOM.INVALID_MODIFICATION_PASSWORD_ERROR_MESSAGE);
			} else if (errorCode == MESSAGES.ROOMSERVER.AUTHORITY_TOO_LOW_ERROR_CODE)
			{
				CommonMethods.displayErrorMessage(Text.GUI.ROOM.USER_LIST.AUTHORITY_TOO_LOW_ERROR_MESSAGE);
			} else if (errorCode == MESSAGES.ROOMSERVER.INVALID_JOIN_PASSWORD_ERROR_CODE)
			{
				//ignore
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
	
	final public void giveWhiteboardToUser(UserData targetUserData) throws IOException, InvalidMessageException
	{
		String messageToServer = MESSAGES.ROOMSERVER.USER_LIST_SERVER.GIVE_WHITEBOARD + MESSAGES.DELIMITER + Login.m_username + MESSAGES.DELIMITER + this.m_joinPassword + MESSAGES.DELIMITER + targetUserData.getUsername();
		String response = sendMessageAndGetResponse(messageToServer);
		//look through the response
		Scanner scanResponse = new Scanner(response);
		//first part should be result
		String result = scanResponse.next();
		if (result.equals(MESSAGES.ROOMSERVER.USER_LIST_SERVER.GIVE_WHITEBOARD_SUCCESS))
		{
			//if successful, let user know
			scanResponse.close();
			CommonMethods.displaySuccessMessage(Text.GUI.ROOM.USER_LIST.USER_PERMISSIONS_DISPLAY.getGiveWhiteboardSuccessMessage(targetUserData.getDisplayName()));
		} else if (result.equals(MESSAGES.ROOMSERVER.USER_LIST_SERVER.GIVE_WHITEBOARD_FAILED))
		{
			//if failed, figure out why and let user know
			//error code should follow
			int errorCode = scanResponse.nextInt();
			scanResponse.close();
			if (errorCode == MESSAGES.ROOMSERVER.YOU_LACK_WHITEBOARD_ERROR_CODE)
			{
				CommonMethods.displayErrorMessage(Text.GUI.ROOM.USER_LIST.USER_PERMISSIONS_DISPLAY.getYouDontHaveWhiteboardToGiveErrorMessage(targetUserData.getDisplayName()));
			} else if (errorCode == MESSAGES.ROOMSERVER.INVALID_JOIN_PASSWORD_ERROR_CODE)
			{
				//ignore - should not happen
			} else if (errorCode == MESSAGES.ROOMSERVER.NON_EXISTING_TARGET_ERROR_CODE)
			{
				//ignore - should not happen
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
	
	final public void takeWhiteboardFromUser(UserData targetUserData) throws IOException, InvalidMessageException
	{
		String messageToServer = MESSAGES.ROOMSERVER.USER_LIST_SERVER.TAKE_WHITEBOARD + MESSAGES.DELIMITER + Login.m_username + MESSAGES.DELIMITER + this.m_joinPassword + MESSAGES.DELIMITER + targetUserData.getUsername();
		String response = sendMessageAndGetResponse(messageToServer);
		//look through the response
		Scanner scanResponse = new Scanner(response);
		//first part should be result
		String result = scanResponse.next();
		if (result.equals(MESSAGES.ROOMSERVER.USER_LIST_SERVER.TAKE_WHITEBOARD_SUCCESS))
		{
			//if successful, let user know
			scanResponse.close();
			CommonMethods.displaySuccessMessage(Text.GUI.ROOM.USER_LIST.USER_PERMISSIONS_DISPLAY.getTakeWhiteboardSuccessMessage(targetUserData.getDisplayName()));
		} else if (result.equals(MESSAGES.ROOMSERVER.USER_LIST_SERVER.TAKE_WHITEBOARD_FAILED))
		{
			//if failed, figure out why and let user know
			//error code should follow
			int errorCode = scanResponse.nextInt();
			scanResponse.close();
			if (errorCode == MESSAGES.ROOMSERVER.USER_LIST_SERVER.TARGET_LACKS_WHITEBOARD_ERROR)
			{
				CommonMethods.displayErrorMessage(Text.GUI.ROOM.USER_LIST.USER_PERMISSIONS_DISPLAY.getTargetDoesNotHaveWhiteboardErrorMessage(targetUserData.getDisplayName()));
			} else if (errorCode == MESSAGES.ROOMSERVER.INVALID_JOIN_PASSWORD_ERROR_CODE)
			{
				//ignore - should not happen
			} else if (errorCode == MESSAGES.ROOMSERVER.NON_EXISTING_TARGET_ERROR_CODE)
			{
				//ignore - should not happen
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
	
	final public void setUserPermissions(UserData targetUserData) throws IOException, InvalidMessageException
	{
		String targetUsername = targetUserData.getUsername();
		boolean hasAudioParticipation = targetUserData.hasAudioParticipation();
		boolean hasAudioListening = targetUserData.hasAudioListening();
		boolean hasTextParticipation = targetUserData.hasTextParticipation();
		boolean hasTextUpdating = targetUserData.hasTextUpdating();
		String messageToServer = MESSAGES.ROOMSERVER.USER_LIST_SERVER.SET_USER_PERMISSIONS + MESSAGES.DELIMITER + Login.m_username + MESSAGES.DELIMITER + this.m_joinPassword + MESSAGES.DELIMITER + targetUsername + MESSAGES.DELIMITER + hasAudioParticipation + MESSAGES.DELIMITER + hasAudioListening + MESSAGES.DELIMITER + hasTextParticipation + MESSAGES.DELIMITER + hasTextUpdating;
		String response = sendMessageAndGetResponse(messageToServer);
		//look through response
		Scanner scanResponse = new Scanner(response);
		//first part should be result
		String result = scanResponse.next();
		if (result.equals(MESSAGES.ROOMSERVER.USER_LIST_SERVER.SET_USER_PERMISSIONS_SUCCESS))
		{
			//if successful, then let client know
			scanResponse.close();
		} else if (result.equals(MESSAGES.ROOMSERVER.USER_LIST_SERVER.SET_USER_PERMISSIONS_FAILED))
		{
			//if failed, figure out why and let user know
			//error code should follow
			int errorCode = scanResponse.nextInt();
			scanResponse.close();
			if (errorCode == MESSAGES.ROOMSERVER.AUTHORITY_TOO_LOW_ERROR_CODE)
			{
				CommonMethods.displayErrorMessage(Text.GUI.ROOM.USER_LIST.AUTHORITY_TOO_LOW_ERROR_MESSAGE);
			} else if (errorCode == MESSAGES.ROOMSERVER.INVALID_JOIN_PASSWORD_ERROR_CODE)
			{
				//ignore
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
	
	final public void sendLeaveRequest() throws IOException, InvalidMessageException
	{
		String messageToServer = MESSAGES.ROOMSERVER.USER_LIST_SERVER.LEAVE;
		String response = sendMessageAndGetResponse(messageToServer);
		//look through response
		Scanner scanResponse = new Scanner(response);
		//first part should be result
		String result = scanResponse.next();
		scanResponse.close();
		if (result.equals(MESSAGES.ROOMSERVER.USER_LIST_SERVER.LEAVE_SUCCESS))
		{
			//continue
		} else
		{
			throw generateInvalidMessageException(response);
		}
	}
	
	@Override
	final protected synchronized String sendMessageAndGetResponse(String messageToRoomDataServer) throws IOException
	{
		System.out.println("Room User List Server: Sent message to server: " + messageToRoomDataServer);
		String response = super.sendMessageAndGetResponse(messageToRoomDataServer);
		System.out.println("Room User List Server: Received message from server: " + response);
		return response;
	}
	
	final public void displayConnectionLostMessage()
	{
		super.displayConnectionLostMessage(Text.NET.ROOM_SERVER_NAME);
	}

	@Override
	public void handleOperationFailedException(OperationFailedException e) 
	{
		//nothing needs to be done as of right now
		
	}

	@Override
	public void handleInvalidMessageException(InvalidMessageException e) 
	{
		//nothign needs to be done as of right now
		
	}

}
