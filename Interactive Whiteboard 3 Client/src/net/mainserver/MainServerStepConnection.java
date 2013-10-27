package net.mainserver;

import java.io.IOException;
import java.util.Scanner;

import net.InvalidMessageException;
import net.MESSAGES;
import net.OperationErrorCode;
import net.OperationFailedException;
import net.StepConnection;

final public class MainServerStepConnection extends StepConnection
{
	
	public MainServerStepConnection(String ip, int port) throws IOException
	{
		super(ip, port);
		System.out.println("Set up main server connection on port " + port);
	}
	
	@Override
	final protected synchronized String sendMessageAndGetResponse(String messageToMainServer) throws IOException
	{
		System.out.println("Main Server: Sent message to server: " + messageToMainServer);
		String response = super.sendMessageAndGetResponse(messageToMainServer);
		System.out.println("Main Server: Received message from server: " + response);
		return response;
	}

	final public void register(String username, String password) throws IOException, OperationFailedException, InvalidMessageException
	{
		String messageToMainServer = MESSAGES.MAINSERVER.REGISTER + MESSAGES.DELIMITER + username + MESSAGES.DELIMITER + password;
		System.out.println("Sending: " + messageToMainServer);
		String responseFromMainServer = this.sendMessageAndGetResponse(messageToMainServer);
		//look through the response
		Scanner scanResponse = new Scanner(responseFromMainServer);
		//the first part should be the result
		String result = scanResponse.next();
		scanResponse.close();
		if (result.equals(MESSAGES.MAINSERVER.REGISTER_SUCCESS))
		{
			//if operation succeeded, then great
		} else if (result.equals(MESSAGES.MAINSERVER.REGISTER_FAILED))
		{
			//if the operation failed, there should be an error code, so figure out why
			int errorCode = scanResponse.nextInt();
			if (errorCode == MESSAGES.MAINSERVER.INVALID_NEW_USERNAME_ERROR_CODE)
			{
				throw new OperationFailedException(OperationErrorCode.NEW_USERNAME_IS_INVALID);
			} else if (errorCode == MESSAGES.MAINSERVER.USERNAME_IN_USE_ERROR_CODE)
			{
				throw new OperationFailedException(OperationErrorCode.USERNAME_IN_USE);
			} else if (errorCode == MESSAGES.MAINSERVER.INVALID_NEW_PASSWORD_ERROR_CODE)
			{
				throw new OperationFailedException(OperationErrorCode.NEW_PASSWORD_IS_INVALID);
			} else
			{
				throw generateInvalidMessageException(responseFromMainServer);
			}
		} else
		{
			throw generateInvalidMessageException(responseFromMainServer);
		}
	}
	
	final public void changePassword(String username, String currentPassword, String newPassword) throws IOException, OperationFailedException, InvalidMessageException
	{
		String messageToMainServer = MESSAGES.MAINSERVER.CHANGE_PASSWORD + MESSAGES.DELIMITER + username + MESSAGES.DELIMITER + currentPassword + MESSAGES.DELIMITER + newPassword;
		String responseFromMainServer = this.sendMessageAndGetResponse(messageToMainServer);
		//look through the response
		Scanner scanResponse = new Scanner(responseFromMainServer);
		//the first part should be the result
		String result = scanResponse.next();
		if (result.equals(MESSAGES.MAINSERVER.CHANGE_PASSWORD_SUCCESS))
		{
			//if operation succeeds, then great
			scanResponse.close();
		} else if (result.equals(MESSAGES.MAINSERVER.CHANGE_PASSWORD_FAILED))
		{
			//if operation failed, then figure out why and tell the user
			//there should be an error code next
			int errorCode = scanResponse.nextInt();
			scanResponse.close();
			if (errorCode == MESSAGES.GENERAL.NONEXISTING_USER_ERROR_CODE)
			{
				throw new OperationFailedException(OperationErrorCode.USERNAME_DOES_NOT_EXIST);
			} else if (errorCode == MESSAGES.GENERAL.BAD_USER_PASSWORD_ERROR_CODE)
			{
				throw new OperationFailedException(OperationErrorCode.PASSWORD_IS_INCORRECT);
			} else if (errorCode == MESSAGES.MAINSERVER.INVALID_NEW_PASSWORD_ERROR_CODE)
			{
				throw new OperationFailedException(OperationErrorCode.NEW_PASSWORD_IS_INVALID);
			} else
			{
				throw generateInvalidMessageException(responseFromMainServer);
			}
		} else
		{
			scanResponse.close();
			throw generateInvalidMessageException(responseFromMainServer);
		}
	}
	
	final public void changeDisplayName(String username, String password, String nameChangeCode, String newDisplayName) throws IOException, OperationFailedException, InvalidMessageException
	{
		String messageToMainServer = MESSAGES.MAINSERVER.CHANGE_DISPLAY_NAME + MESSAGES.DELIMITER + username + MESSAGES.DELIMITER + password + MESSAGES.DELIMITER + nameChangeCode + MESSAGES.DELIMITER + newDisplayName;
		String responseFromMainServer = this.sendMessageAndGetResponse(messageToMainServer);
		//look through the response
		Scanner scanResponse = new Scanner(responseFromMainServer);
		//the first part should be the result
		String result = scanResponse.next();
		if (result.equals(MESSAGES.MAINSERVER.CHANGE_DISPLAY_NAME_SUCCESS))
		{
			//if the operation was successful, great
			scanResponse.close();
		} else if (result.equals(MESSAGES.MAINSERVER.CHANGE_DISPLAY_NAME_FAILED))
		{
			//if the operation failed, tell the user why
			//there should be an error code next
			int errorCode = scanResponse.nextInt();
			scanResponse.close();
			if (errorCode == MESSAGES.GENERAL.NONEXISTING_USER_ERROR_CODE)
			{
				throw new OperationFailedException(OperationErrorCode.USERNAME_DOES_NOT_EXIST);
			} else if (errorCode == MESSAGES.GENERAL.BAD_USER_PASSWORD_ERROR_CODE)
			{
				throw new OperationFailedException(OperationErrorCode.PASSWORD_IS_INCORRECT);
			} else if (errorCode == MESSAGES.MAINSERVER.BAD_NAME_CHANGE_CODE_ERROR_CODE)
			{
				throw new OperationFailedException(OperationErrorCode.NAME_CHANGE_CODE_IS_INCORRECT);
			} else if (errorCode == MESSAGES.MAINSERVER.INVALID_NEW_DISPLAY_NAME_ERROR_CODE)
			{
				throw new OperationFailedException(OperationErrorCode.NEW_DISPLAY_NAME_IS_INVALID);
			} else
			{
				throw generateInvalidMessageException(responseFromMainServer);
			}
		} else
		{
			scanResponse.close();
			throw generateInvalidMessageException(responseFromMainServer);
		}
	}
	
	//this method retrieves the concurrency port on which the main server is located from the main server.
	final public int getMainServerPorts() throws IOException, InvalidMessageException
	{
		String messageToMainServer = MESSAGES.MAINSERVER.GET_MESSAGING_SERVER_INFO;
		String responseFromMainServer = this.sendMessageAndGetResponse(messageToMainServer);
		//look through the response
		Scanner scanResponse = new Scanner(responseFromMainServer);
		//the first part should be the result
		String result = scanResponse.next();
		if (result.equals(MESSAGES.MAINSERVER.GET_MAIN_SERVER_INFO_SUCCESS))
		{
			//the main server concurrency port should follow
			int mainServerConcurrencyPort = scanResponse.nextInt();
			scanResponse.close();
			return mainServerConcurrencyPort;
		}
		scanResponse.close();
		throw new InvalidMessageException(responseFromMainServer);
	}
	
	//this method retrieves the port on which the messaging server is located from the server 
	//RETURN: an array with 2 integers. The first integer value is the step connection port.
	//		  the second integer value is the concurrent connection port.
	/**
	 * This method retrieves the ports associated with the messaging server.
	 * @return Two integers. The first integer is the step connection port. The second integer is the concurrent connection port.
	 * @throws IOException
	 * @throws InvalidMessageException
	 */
	final public int[] getMessagingServerPorts() throws IOException, InvalidMessageException
	{
		String messageToMainServer = MESSAGES.MAINSERVER.GET_MESSAGING_SERVER_INFO;
		String responseFromMainServer = this.sendMessageAndGetResponse(messageToMainServer);
		//look through the response
		Scanner scanResponse = new Scanner(responseFromMainServer);
		//the first part should be the result
		String result = scanResponse.next();
		if (result.equals(MESSAGES.MAINSERVER.GET_MESSAGING_SERVER_INFO_SUCCESS))
		{
			//the messaging server port should follow
			int messagingServerStepConnectionPort = scanResponse.nextInt();
			int messagingServerConcurrentConnectionPort = scanResponse.nextInt();
			int[] portData = {messagingServerStepConnectionPort, messagingServerConcurrentConnectionPort};
			scanResponse.close();
			return portData;
		}
		//if we did not get a success message, then something went wrong
		scanResponse.close();
		throw new InvalidMessageException(responseFromMainServer);
	}
	
	//this method gets the ports associated with the room data server
	//RETURN: an array of 2 integers. the first integer is the step connection port. the second integer
	//		  is the concurrent connection port
	final public int[] getRoomDataServerPorts() throws IOException, InvalidMessageException
	{
		String messageToMainServer = MESSAGES.MAINSERVER.GET_ROOM_DATA_SERVER_INFO;
		String responseFromMainServer = this.sendMessageAndGetResponse(messageToMainServer);
		//look through the response
		Scanner scanResponse = new Scanner(responseFromMainServer);
		//the first part will be the result
		String result = scanResponse.next();
		if (result.equals(MESSAGES.MAINSERVER.GET_ROOM_DATA_SERVER_INFO_SUCCESS))
		{
			//the room data server ports should follow
			int roomDataServerStepConnectionPort = scanResponse.nextInt();
			int roomDataServerConcurrentConnectionPort = scanResponse.nextInt();
			int[] portData = {roomDataServerStepConnectionPort, roomDataServerConcurrentConnectionPort};
			scanResponse.close();
			return portData;
		}
		//if we did not get a success message, then something went wrong
		scanResponse.close();
		throw new InvalidMessageException(responseFromMainServer);
	}

	@Override
	final public void handleOperationFailedException(OperationFailedException e)
	{
		//this does not need to handle any failed operations as of right now
	}

	@Override
	final public void handleInvalidMessageException(InvalidMessageException e)
	{
		System.out.println("Invalid message received: ");
		e.printStackTrace();
	}
}
