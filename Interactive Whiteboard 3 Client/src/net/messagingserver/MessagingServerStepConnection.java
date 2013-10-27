package net.messagingserver;

import gui.login.Login;

import java.io.IOException;
import java.util.Scanner;

import util.CommonMethods;
import util.Text;

import net.InvalidMessageException;
import net.MESSAGES;
import net.OperationErrorCode;
import net.OperationFailedException;
import net.StepConnection;

final public class MessagingServerStepConnection extends StepConnection
{
	
	public MessagingServerStepConnection(String ip, int port) throws IOException
	{
		super(ip, port);
	}
	
	@Override
	final protected synchronized String sendMessageAndGetResponse(String messageToMessagingServer) throws IOException
	{
		System.out.println("Messaging Server: Sent message to server: " + messageToMessagingServer);
		String response = super.sendMessageAndGetResponse(messageToMessagingServer);
		System.out.println("Messaging Server: Received message from server: " + response);
		return response;
	}
	
	final public void attemptLogin()
	{
		//only load the messaging client if we can connect to the messaging server
		try
		{
			if (attemptLogin(Login.m_username, Login.m_password))
			{
				//continue
			} else
			{
				CommonMethods.displayErrorMessage(Text.NET.GENERAL.LOGIN_FAILED_ERROR_MESSAGE);
				return;
			}
		} catch (IOException connectionError)
		{
			CommonMethods.displayErrorMessage(Text.NET.getConnectionLostMessage(Text.NET.MESSAGING_SERVER_NAME));
			return;
		} catch (InvalidMessageException invalidMessage)
		{
			this.handleInvalidMessageException(invalidMessage);
			return;
		}
	}
	
	final public static String DEFAULT_DISPLAY_NAME = "<Failed to Retreive Display Name>";
	final public String getDisplayNameOfUser(String username, String password, String targetUsername)
	{
		String message = MESSAGES.GENERAL.GET_DISPLAY_NAME_OF_OTHER_USER 
				+ MESSAGES.DELIMITER + username + MESSAGES.DELIMITER + password + MESSAGES.DELIMITER + targetUsername;
		String response;
		try
		{
			response = this.sendMessageAndGetResponse(message);
		} catch (IOException e)
		{
			//if could not communicate with server, just default the display name
			return DEFAULT_DISPLAY_NAME;
		}
		Scanner scanResponse = new Scanner(response);
		//the first part should be the result
		String result = scanResponse.next();
		if (result.equals(MESSAGES.GENERAL.GET_DISPLAY_NAME_OF_OTHER_USER_SUCCESS))
		{
			//if succeeded, then display name should follow
			String displayName = scanResponse.next();
			//remove space replacements from the display name
			displayName = MESSAGES.unsubstituteForMessageDelimiters(displayName);
			return displayName;
		} else if (result.equals(MESSAGES.GENERAL.GET_DISPLAY_NAME_OF_OTHER_USER_FAILED))
		{
			//if failed, then default the display name
			return DEFAULT_DISPLAY_NAME;
		} else
		{
			return DEFAULT_DISPLAY_NAME;
		}
	}
	
	final public boolean isFriendOnline(String username, String friendUsername) throws IOException, InvalidMessageException
	{
		String message = MESSAGES.MESSAGINGSERVER.IS_FRIEND_ONLINE + MESSAGES.DELIMITER 
				+ username + MESSAGES.DELIMITER + friendUsername;
		String response = this.sendMessageAndGetResponse(message);
		//look through the response
		Scanner scanResponse = new Scanner(response);
		//the result should follow
		String result = scanResponse.next();
		if (result.equals(MESSAGES.MESSAGINGSERVER.RETURN_IS_FRIEND_ONLINE))
		{
			//if the messaging server returned a value, read the value
			//a boolean should follow
			boolean isFriendOnline = scanResponse.nextBoolean();
			return isFriendOnline;
		}
		throw new InvalidMessageException(response);
	}
	
	/**
	 * Attempts to retrieve the friends list from the server.
	 * 
	 * @param username							a String, the username of the user whose friends list is to be
	 * 											retrieved
	 * @param password							a String, the password of the user whose friends list is to be
	 * 											retrieved
	 * @return									an array of String, the usernames of everyone on the user's friends list
	 * @throws IOException						if the connection unexpectedly ends
	 * @throws OperationFailedException			if the server refuses to get the friends list
	 * @throws InvalidMessageException			if the server responds with an unexpected message
	 */
	final public String[] getFriendsList(String username, String password) throws IOException, OperationFailedException, InvalidMessageException
	{
		String message = MESSAGES.MESSAGINGSERVER.GET_FRIENDS_LIST + MESSAGES.DELIMITER + username + MESSAGES.DELIMITER + password + MESSAGES.DELIMITER;
		String response = this.sendMessageAndGetResponse(message);
		//look through the response
		Scanner scanResponse = new Scanner(response);
		//the first part should be the result
		String result = scanResponse.next();
		if (result.equals(MESSAGES.MESSAGINGSERVER.GET_FRIENDS_LIST_SUCCESS))
		{
			//if successful, the friends list should follow
			//the first part is an integer, the number of usernames that follow
			int numberOfFriends = scanResponse.nextInt();
			String[] usernamesOfFriends = new String[numberOfFriends];
			for (int numberOfUsernamesRead = 0; numberOfUsernamesRead < numberOfFriends; numberOfUsernamesRead++)
			{
				usernamesOfFriends[numberOfUsernamesRead] = scanResponse.next();
			}
			scanResponse.close();
			//return the usernames
			return usernamesOfFriends;
		} else if (result.equals(MESSAGES.MESSAGINGSERVER.GET_FRIENDS_LIST_FAILED))
		{
			//if unsuccessful, an error code should follow
			int errorCode = scanResponse.nextInt();
			scanResponse.close();
			if (errorCode == MESSAGES.GENERAL.NONEXISTING_USER_ERROR_CODE)
			{
				throw new OperationFailedException(OperationErrorCode.USERNAME_DOES_NOT_EXIST);
			} else if (errorCode == MESSAGES.GENERAL.BAD_USER_PASSWORD_ERROR_CODE)
			{
				throw new OperationFailedException(OperationErrorCode.PASSWORD_IS_INCORRECT);
			} else
			{
				throw new InvalidMessageException(response);
			}
		} else
		{
			scanResponse.close();
			throw new InvalidMessageException(response);
		}
	}
	
	final public void addFriend(String username, String password, String friendUsername) throws IOException, OperationFailedException, InvalidMessageException
	{
		String message = MESSAGES.MESSAGINGSERVER.ADD_FRIEND + MESSAGES.DELIMITER + username + MESSAGES.DELIMITER + password + MESSAGES.DELIMITER + friendUsername;
		String response = this.sendMessageAndGetResponse(message);
		//look through the response
		Scanner scanResponse = new Scanner(response);
		//this first part should be the result
		String result = scanResponse.next();
		if (result.equals(MESSAGES.MESSAGINGSERVER.ADD_FRIEND_SUCCESS))
		{
			//if succeeded, great
			scanResponse.close();
		} else if (result.equals(MESSAGES.MESSAGINGSERVER.ADD_FRIEND_FAILED))
		{
			//if failed, figure out why and let the client know
			//there should be an error code next
			int errorCode = scanResponse.nextInt();
			scanResponse.close();
			if (errorCode == MESSAGES.GENERAL.NONEXISTING_USER_ERROR_CODE)
			{
				throw new OperationFailedException(OperationErrorCode.USERNAME_DOES_NOT_EXIST);
			} else if (errorCode == MESSAGES.GENERAL.BAD_USER_PASSWORD_ERROR_CODE)
			{
				throw new OperationFailedException(OperationErrorCode.PASSWORD_IS_INCORRECT);
			} else if (errorCode == MESSAGES.MESSAGINGSERVER.NONEXISTING_TARGET_ERROR_CODE)
			{
				throw new OperationFailedException(OperationErrorCode.TARGET_USER_DOES_NOT_EXIST);
			} else
			{
				throw new InvalidMessageException(response);
			}
		} else
		{
			scanResponse.close();
			throw new InvalidMessageException(response);
		}
	}
	
	final public void removeFriend(String username, String password, String friendUsername) throws IOException, OperationFailedException, InvalidMessageException
	{
		String message = MESSAGES.MESSAGINGSERVER.REMOVE_FRIEND + MESSAGES.DELIMITER + username + MESSAGES.DELIMITER + password + MESSAGES.DELIMITER + friendUsername;
		String response = this.sendMessageAndGetResponse(message);
		//look through the response
		Scanner scanResponse = new Scanner(response);
		//the first part should be the result
		String result = scanResponse.next();
		if (result.equals(MESSAGES.MESSAGINGSERVER.REMOVE_FRIEND_SUCCESS))
		{
			//if the operation succeeded, great
			scanResponse.close();
		} else if (result.equals(MESSAGES.MESSAGINGSERVER.REMOVE_FRIEND_FAILED))
		{
			//if the operation failed, figure out why and let the user know
			int errorCode = scanResponse.nextInt();
			scanResponse.close();
			if (errorCode == MESSAGES.GENERAL.NONEXISTING_USER_ERROR_CODE)
			{
				throw new OperationFailedException(OperationErrorCode.USERNAME_DOES_NOT_EXIST);
			} else if (errorCode == MESSAGES.GENERAL.BAD_USER_PASSWORD_ERROR_CODE)
			{
				throw new OperationFailedException(OperationErrorCode.PASSWORD_IS_INCORRECT);
			} else if (errorCode == MESSAGES.MESSAGINGSERVER.NONEXISTING_TARGET_ERROR_CODE)
			{
				throw new OperationFailedException(OperationErrorCode.TARGET_USER_DOES_NOT_EXIST);
			} else
			{
				throw new InvalidMessageException(response);
			}
		} else
		{
			scanResponse.close();
			throw new InvalidMessageException(response);
		}
	}
	
	final public String[] getPestsList(String username, String password) throws IOException, OperationFailedException, InvalidMessageException
	{
		String message = MESSAGES.MESSAGINGSERVER.GET_PESTS_LIST + MESSAGES.DELIMITER + username + MESSAGES.DELIMITER + password;
		String response = this.sendMessageAndGetResponse(message);
		//look through the response
		Scanner scanResponse = new Scanner(response);
		//first part should be result
		String result = scanResponse.next();
		if (result.equals(MESSAGES.MESSAGINGSERVER.GET_PESTS_LIST_SUCCESS))
		{
			//if successful, read in the pests list
			//first part will be an integer, the number of pest usernames to follow
			int numberOfPests = scanResponse.nextInt();
			String[] pestsList = new String[numberOfPests];
			//read in the pests
			for (int numberOfPestsRead = 0; numberOfPestsRead < numberOfPests; numberOfPestsRead++)
			{
				pestsList[numberOfPestsRead] = scanResponse.next();
			}
			return pestsList;
		} else if (result.equals(MESSAGES.MESSAGINGSERVER.GET_PESTS_LIST_FAILED))
		{
			//if failed, figure out why and let client know
			//an error code should follow
			int errorCode = scanResponse.nextInt();
			if (errorCode == MESSAGES.GENERAL.NONEXISTING_USER_ERROR_CODE)
			{
				throw new OperationFailedException(OperationErrorCode.USERNAME_DOES_NOT_EXIST);
			} else if (errorCode == MESSAGES.GENERAL.BAD_USER_PASSWORD_ERROR_CODE)
			{
				throw new OperationFailedException(OperationErrorCode.PASSWORD_IS_INCORRECT);
			} else
			{
				throw new InvalidMessageException(response);
			}
		} else
		{
			throw new InvalidMessageException(response);
		}
	}
	
	final public void addPest(String username, String password, String pestUsername) throws IOException, OperationFailedException, InvalidMessageException
	{
		String message = MESSAGES.MESSAGINGSERVER.ADD_PEST + MESSAGES.DELIMITER + username + MESSAGES.DELIMITER + password + MESSAGES.DELIMITER + pestUsername;
		String response = this.sendMessageAndGetResponse(message);
		//look through the response
		Scanner scanResponse = new Scanner(response);
		//the next part should be the result
		String result = scanResponse.next();
		if (result.equals(MESSAGES.MESSAGINGSERVER.ADD_PEST_SUCCESS))
		{
			//if all went well, then great
			scanResponse.close();
		} else if (result.equals(MESSAGES.MESSAGINGSERVER.ADD_PEST_FAILED))
		{
			//if the operation failed, figure out why
			//there should be an error code next
			int errorCode = scanResponse.nextInt();
			scanResponse.close();
			if (errorCode == MESSAGES.GENERAL.NONEXISTING_USER_ERROR_CODE)
			{
				throw new OperationFailedException(OperationErrorCode.USERNAME_DOES_NOT_EXIST);
			} else if (errorCode == MESSAGES.GENERAL.BAD_USER_PASSWORD_ERROR_CODE)
			{
				throw new OperationFailedException(OperationErrorCode.PASSWORD_IS_INCORRECT);
			} else if (errorCode == MESSAGES.MESSAGINGSERVER.NONEXISTING_TARGET_ERROR_CODE)
			{
				throw new OperationFailedException(OperationErrorCode.TARGET_USER_DOES_NOT_EXIST);
			} else
			{
				throw new InvalidMessageException(response);
			}
		} else
		{
			scanResponse.close();
			throw new InvalidMessageException(response);
		}
	}
	
	final public void removePest(String username, String password, String pestUsername) throws IOException, OperationFailedException, InvalidMessageException
	{
		String message = MESSAGES.MESSAGINGSERVER.REMOVE_PEST + MESSAGES.DELIMITER + username + MESSAGES.DELIMITER + password + MESSAGES.DELIMITER + pestUsername;
		String response = this.sendMessageAndGetResponse(message);
		//look through the response
		Scanner scanResponse = new Scanner(response);
		//the first part should be the result
		String result = scanResponse.next();
		if (result.equals(MESSAGES.MESSAGINGSERVER.REMOVE_PEST_SUCCESS))
		{
			//if the operation succeeds, great
			scanResponse.close();
		} else if (result.equals(MESSAGES.MESSAGINGSERVER.REMOVE_PEST_FAILED))
		{
			//if the operation failed, figure out why and let the user know
			//there should be an error code next
			int errorCode = scanResponse.nextInt();
			scanResponse.close();
			if (errorCode == MESSAGES.GENERAL.NONEXISTING_USER_ERROR_CODE)
			{
				throw new OperationFailedException(OperationErrorCode.USERNAME_DOES_NOT_EXIST);
			} else if (errorCode == MESSAGES.GENERAL.BAD_USER_PASSWORD_ERROR_CODE)
			{
				throw new OperationFailedException(OperationErrorCode.PASSWORD_IS_INCORRECT);
			} else if (errorCode == MESSAGES.MESSAGINGSERVER.NONEXISTING_TARGET_ERROR_CODE)
			{
				throw new OperationFailedException(OperationErrorCode.TARGET_USER_DOES_NOT_EXIST);
			} else
			{
				throw new InvalidMessageException(response);
			}
		} else
		{
			scanResponse.close();
			throw new InvalidMessageException(response);
		}
	}
	
	final public PrivateMessage[] getPrivateMessages(String username, String password) throws IOException, OperationFailedException, InvalidMessageException
	{
		String message = MESSAGES.MESSAGINGSERVER.GET_PM_HISTORY + MESSAGES.DELIMITER
				+ username + MESSAGES.DELIMITER + password;
		String response = this.sendMessageAndGetResponse(message);
		//look through the response
		Scanner scanResponse = new Scanner(response);
		String result = scanResponse.next();
		if (result.equals(MESSAGES.MESSAGINGSERVER.GET_PM_HISTORY_SUCCESS))
		{
			//if succeeded, then record the private messages
			//the first piece will be the number of lines of private message history
			int numberOfLinesOfPmHistory = scanResponse.nextInt();
			PrivateMessage[] privateMessages = new PrivateMessage[numberOfLinesOfPmHistory];
			for (int linesRead = 0; linesRead < numberOfLinesOfPmHistory; linesRead++)
			{
				String senderUsername = scanResponse.next();
				String recipientUsername = scanResponse.next();
				String privateMessageContents = scanResponse.next();
				boolean isMessageRead = scanResponse.nextBoolean();
				privateMessages[linesRead] = new PrivateMessage(senderUsername, recipientUsername, privateMessageContents, isMessageRead);
			}
			return privateMessages;
		} else if (result.equals(MESSAGES.MESSAGINGSERVER.GET_PM_HISTORY_FAILED))
		{
			//if failed, figure out why
			//there should be an error code next
			int errorCode = scanResponse.nextInt();
			scanResponse.close();
			if (errorCode == MESSAGES.GENERAL.NONEXISTING_USER_ERROR_CODE)
			{
				throw new OperationFailedException(OperationErrorCode.USERNAME_DOES_NOT_EXIST);
			} else if (errorCode == MESSAGES.GENERAL.BAD_USER_PASSWORD_ERROR_CODE)
			{
				throw new OperationFailedException(OperationErrorCode.PASSWORD_IS_INCORRECT);
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
	
	final public void sendPrivateMessage(String senderUsername, String password, String recipientUsername, String messageToSend) throws IOException, OperationFailedException, InvalidMessageException
	{
		String message = MESSAGES.MESSAGINGSERVER.SEND_PM + MESSAGES.DELIMITER 
				+ senderUsername + MESSAGES.DELIMITER + password + MESSAGES.DELIMITER + recipientUsername + MESSAGES.DELIMITER + messageToSend;
		String response = this.sendMessageAndGetResponse(message);
		//look through the response
		Scanner scanResponse = new Scanner(response);
		//the first part should be the result
		String result = scanResponse.next();
		if (result.equals(MESSAGES.MESSAGINGSERVER.SEND_PM_SUCCESS))
		{
			//if succeeded, then great
			scanResponse.close();
		} else if (result.equals(MESSAGES.MESSAGINGSERVER.SEND_PM_FAILED))
		{
			//if failed, figure out why
			//there should be an error code next
			int errorCode = scanResponse.nextInt();
			scanResponse.close();
			if (errorCode == MESSAGES.GENERAL.NONEXISTING_USER_ERROR_CODE)
			{
				throw new OperationFailedException(OperationErrorCode.USERNAME_DOES_NOT_EXIST);
			} else if (errorCode == MESSAGES.GENERAL.BAD_USER_PASSWORD_ERROR_CODE)
			{
				throw new OperationFailedException(OperationErrorCode.PASSWORD_IS_INCORRECT);
			} else if (errorCode == MESSAGES.MESSAGINGSERVER.NONEXISTING_TARGET_ERROR_CODE)
			{
				throw new OperationFailedException(OperationErrorCode.TARGET_USER_DOES_NOT_EXIST);
			} else
			{
				throw new InvalidMessageException(response);
			}
		} else
		{
			scanResponse.close();
			throw generateInvalidMessageException(response);
		}
	}
	
	/**
	 * Tells the server to set all private messages with the given friend as read. 
	 * 
	 * @param username							a String, the username of the person who wants to set his/her PMs as read
	 * @param password							a String, the password of the person who wants to set his/her PMs as read
	 * @param usernameOfFriend					a String, the username of the friend in the conversation					
	 * @return									true if successful, false if unsuccessful
	 * @throws IOException						if the connection unexpectedly ends
	 * @throws InvalidMessageException			if an unexpected message was received
	 */
	final public boolean setPmsWithFriendAsRead(String username, String password, String usernameOfFriend) throws IOException, InvalidMessageException
	{
		String message = MESSAGES.MESSAGINGSERVER.SET_PMS_WITH_FRIEND_AS_READ + MESSAGES.DELIMITER 
				+ username + MESSAGES.DELIMITER + password + MESSAGES.DELIMITER + usernameOfFriend;
		String response = this.sendMessageAndGetResponse(message);
		//look through the response
		Scanner scanResponse = new Scanner(response);
		String result = scanResponse.next();
		scanResponse.close();
		if (result.equals(MESSAGES.MESSAGINGSERVER.SET_PMS_WITH_FRIEND_AS_READ_SUCCESS))
		{
			//if successful, then great
			return true;
		} else if (result.equals(MESSAGES.MESSAGINGSERVER.SET_PMS_WITH_FRIEND_AS_READ_FAILED))
		{
			//if failed, still don't care
			return false;
		} else
		{
			throw generateInvalidMessageException(response);
		}
	}
	
	final public static class PrivateMessage
	{
		final private String m_sender;
		final private String m_recipient;
		final private String m_message;
		final private boolean m_isRead;
		
		public PrivateMessage(String senderUsername, String recipientUsername, String message, boolean isRead)
		{
			this.m_sender = senderUsername;
			this.m_recipient = recipientUsername;
			this.m_message = message;
			this.m_isRead = isRead;
		}
		
		final public String getSender()
		{
			return this.m_sender;
		}
		
		final public String getRecipient()
		{
			return this.m_recipient;
		}
		
		final public String getMessage()
		{
			return this.m_message;
		}
		
		final public boolean isRead()
		{
			return this.m_isRead;
		}
	}
	
	/**
	 * notifies the user that connection has been lost.
	 * suppressed warnings:<br>
	 * 1) static-method			not static because connection cannot be lost unless this connection has been
	 * 							started (constructor called)
	 */
	@SuppressWarnings("static-method")
	final public void displayConnectionLostMessage()
	{
		CommonMethods.displayErrorMessage(Text.NET.getConnectionLostMessage(Text.NET.MESSAGING_SERVER_NAME));
	}
	
	@Override
	final public void handleOperationFailedException(OperationFailedException e)
	{
		switch(e.getErrorCode())
		{
			case USERNAME_DOES_NOT_EXIST:
				CommonMethods.displayErrorMessage(Text.NET.GENERAL.USERNAME_DOES_NOT_EXIST_ERROR_MESSAGE);
				break;
			case PASSWORD_IS_INCORRECT:
				CommonMethods.displayErrorMessage(Text.NET.GENERAL.PASSWORD_INCORRECT_ERROR_MESSAGE);
				break;
			case TARGET_USER_DOES_NOT_EXIST:
				CommonMethods.displayErrorMessage(Text.NET.MESSAGINGSERVER.USER_DOES_NOT_EXIST_ERROR_MESSAGE);
				break;
			default:
				//TODO
		}
	}
	
	@Override
	final public void handleInvalidMessageException(InvalidMessageException e)
	{
		e.printStackTrace();
	}
}
