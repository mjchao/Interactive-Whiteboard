package net.messagingserver;


import java.io.IOException;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

import util.Text;

import database.ConnectionEndedException;
import database.DatabaseConnection;

import net.MESSAGES;
import net.Server;
import net.ServerSideErrorException;
import net.TamperedClientException;

/**
 * This class deals with the communications aspect of the program. It manages friends list, pests lists and
 * private chat.
 *
 */
final public class MessagingServer extends Server
{
	final public static int MESSAGING_SERVER_PORT = 9997;
	final public static int MESSAGING_SERVER_CONCURRENCY_PORT = 9996;
	
	public MessagingServer() throws IOException
	{
		super(MESSAGING_SERVER_PORT, MESSAGING_SERVER_CONCURRENCY_PORT);
		super.m_clients = new MessagingSubServer[Server.MAX_CLIENTS];
	}
	
	@Override
	final protected boolean isClientLoggedIn(String username)
	{
		for (int clientIndex = 0; clientIndex < this.m_clients.length; clientIndex++)
		{
			MessagingSubServer aClient = (MessagingSubServer) this.m_clients[clientIndex];
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
	final protected MessagingSubServer assignClientToSubServer(Socket aClientConnection, Socket aClientConcurrencyConnection) throws IOException
	{
		return new MessagingSubServer(aClientConnection, aClientConcurrencyConnection);
	}
	
	final protected void sendFriendStatus(String usernameOfFriend, boolean isOnline) throws ConnectionEndedException, TamperedClientException
	{
		for (int clientIndex = 0; clientIndex < this.m_clients.length; clientIndex++)
		{
			MessagingSubServer aClient = (MessagingSubServer) this.m_clients[clientIndex];
			if (aClient != null)
			{
				if (aClient.isFriendsWithUser(aClient.getUsername(), usernameOfFriend) && !aClient.amIPestedByUser(aClient.getUsername(), usernameOfFriend))
				{
					aClient.sendFriendStatus(usernameOfFriend, isOnline);
				}
			}
		}
	}
	
	final protected void sendPrivateMessage(String senderUsername, String recipientUsername, String message)
	{
		//locate the sender and the recipient's subserver, and have them trigger the client to add the message
		for (int clientIndex = 0; clientIndex < this.m_clients.length; clientIndex++)
		{
			MessagingSubServer aClient = (MessagingSubServer) this.m_clients[clientIndex];
			if (aClient != null)
			{
				if (aClient.getUsername().equals(senderUsername) || aClient.getUsername().equals(recipientUsername))
				{
					aClient.addPmOnClientSide(senderUsername, recipientUsername, message);
				}
			}
		}
	}
	
	//this method returns if a client with the given username is connected
	final protected boolean isUserOnline(String username)
	{
		for (int clientIndex = 0; clientIndex < this.m_clients.length; clientIndex++)
		{
			MessagingSubServer aClient = (MessagingSubServer) this.m_clients[clientIndex];
			if (aClient != null)
			{
				if (aClient.getUsername().equals(username))
				{
					return true;
				}
			}
		}
		return false;
	}
	
	final private class MessagingSubServer extends SubServer
	{	
		public MessagingSubServer(Socket stepConnection, Socket concurrencyConnection) throws IOException 
		{
			super(stepConnection, concurrencyConnection);
		}
		
		@Override
		final protected void handleTerminatingConnection()
		{
			this.closeAndStop();
			MessagingServer.this.removeAClient(this.m_subServerID);
			try 
			{
				MessagingServer.this.sendFriendStatus(this.m_username, this.WENT_OFFLINE);
			} catch (ConnectionEndedException e) 
			{
				//ignore - it's not a big deal if the client doesn't update.
			} catch (TamperedClientException e) 
			{
				//ignore - I don't care
			}
		}

		@Override
		final protected synchronized void decode(String messageFromClient) throws ConnectionEndedException, ServerSideErrorException, TamperedClientException, NoSuchElementException
		{
			String message = messageFromClient;
			//look through the message
			Scanner scanMessage = new Scanner(message);
			String nextPart = scanMessage.next();
			if (nextPart.equals(MESSAGES.GENERAL.LOGIN))
			{
				//expect a username and a password
				String username = scanMessage.next();
				String password = scanMessage.next();
				this.login(username, password);
				MessagingServer.this.sendFriendStatus(this.m_username, this.WENT_ONLINE);
			} else if (this.loggedIn)
			{
				if (nextPart.equals(MESSAGES.MESSAGINGSERVER.GET_FRIENDS_LIST))
				{
					//expect a username and a password
					String username = scanMessage.next();
					String password = scanMessage.next();
					sendFriendsListToClient(username, password);
				} else if (nextPart.equals(MESSAGES.MESSAGINGSERVER.ADD_FRIEND))
				{
					//expect a username, a password and a friend's username
					String username = scanMessage.next();
					String password = scanMessage.next();
					String friendUsername = scanMessage.next();
					addFriend(username, password, friendUsername);
				} else if (nextPart.equals(MESSAGES.MESSAGINGSERVER.REMOVE_FRIEND))
				{
					//expect a username, a password and a friend's username
					String username = scanMessage.next();
					String password = scanMessage.next();
					String friendUsername = scanMessage.next();
					removeFriend(username, password, friendUsername);
				} else if (nextPart.equals(MESSAGES.MESSAGINGSERVER.IS_FRIEND_ONLINE))
				{
					//expect a username, a password and the username of a friend
					String username = scanMessage.next();
					String friendUsername = scanMessage.next();
					sendIsUserOnline(username, friendUsername);
				} else if (nextPart.equals(MESSAGES.MESSAGINGSERVER.GET_PESTS_LIST))
				{
					//expect a username and a password
					String username = scanMessage.next();
					String password = scanMessage.next();
					sendPestsListToClient(username, password);
				} else if (nextPart.equals(MESSAGES.MESSAGINGSERVER.ADD_PEST))
				{
					//expect a username, a password and a pest's username
					String username = scanMessage.next();
					String password = scanMessage.next();
					String pestUsername = scanMessage.next();
					addPest(username, password, pestUsername);
				} else if (nextPart.equals(MESSAGES.MESSAGINGSERVER.REMOVE_PEST))
				{
					//expect a username, a password and a pest's username
					String username = scanMessage.next();
					String password = scanMessage.next();
					String pestUsername = scanMessage.next();
					removePest(username, password, pestUsername);
				} else if (nextPart.equals(MESSAGES.MESSAGINGSERVER.GET_PM_HISTORY))
				{
					//expect a username and a password
					String username = scanMessage.next();
					String password = scanMessage.next();
					sendPmHistoryToClient(username, password);
				} else if (nextPart.equals(MESSAGES.MESSAGINGSERVER.SEND_PM))
				{
					//expect a username, password, another username involved, the sender of the PM
					//and the contents of the message
					String senderUsername = scanMessage.next();
					String senderPassword = scanMessage.next();
					String recipientUsername = scanMessage.next();
					String messageContents = scanMessage.next();
					addPmOnDatabase(senderUsername, senderPassword, recipientUsername, messageContents);
				} else if (nextPart.equals(MESSAGES.MESSAGINGSERVER.SET_PMS_WITH_FRIEND_AS_READ))
				{
					//expect a username, a password and a friend's username
					String username = scanMessage.next();
					String password = scanMessage.next();
					String friendUsername = scanMessage.next();
					setPMsWithFriendAsRead(username, password, friendUsername);
				} else if (nextPart.equals(MESSAGES.GENERAL.GET_DISPLAY_NAME_OF_OTHER_USER))
				{
					String username = scanMessage.next();
					String password = scanMessage.next();
					String targetUsername = scanMessage.next();
					sendDisplayNameOfOtherUser(username, password, targetUsername);
				} else
				{
					super.logUnknownClientMessage(Text.MESSAGINGSERVER.NAME_OF_SERVER, message, this.m_ip);
				}
			} else
			{
				super.closeClientPerformingUnauthorizedActions();
			}
			scanMessage.close();
		}
		
		final protected void sendFriendStatus(String usernameOfFriend, boolean isOnline)
		{
			writeConcurrent(MESSAGES.MESSAGINGSERVER.CHANGE_FRIEND_STATUS 
					+ MESSAGES.DELIMITER + usernameOfFriend + MESSAGES.DELIMITER + isOnline);
		}
		
		final protected boolean isFriendsWithUser(String userWithFriendsList, String usernameToCheck) throws ConnectionEndedException, TamperedClientException
		{
			assertUsernameIsCorrect(userWithFriendsList);
			String message = database.MESSAGES.USERDATA.HEADING + database.MESSAGES.DELIMITER + database.MESSAGES.USERDATA.MESSAGINGSERVER.MESSAGING_SERVER_HEADING + database.MESSAGES.DELIMITER
					+ database.MESSAGES.USERDATA.MESSAGINGSERVER.AM_I_ON_USER_FRIENDS_LIST + database.MESSAGES.DELIMITER + usernameToCheck + database.MESSAGES.DELIMITER + userWithFriendsList;
			String response = DatabaseConnection.sendMessageAndGetResponse(message);
			//look through the response
			Scanner scanResponse = new Scanner(response);
			//the first part should be the result
			String result = scanResponse.next();
			if (result.equals(database.MESSAGES.USERDATA.MESSAGINGSERVER.AM_I_ON_USER_FRIENDS_LIST_REQUEST_SUCCESS))
			{
				//if succeeded, then the answer to the question should be next
				boolean rtn = scanResponse.nextBoolean();
				scanResponse.close();
				return rtn;
			} else if (result.equals(database.MESSAGES.USERDATA.MESSAGINGSERVER.AM_I_ON_USER_FRIENDS_LIST_REQUEST_FAILED))
			{
				//if failed, use worst case scenario - not on friends list
				scanResponse.close();
				return false;
			}
			scanResponse.close();
			return false;
		}
		
		final private void sendFriendsListToClient(String username, String password) throws ConnectionEndedException, ServerSideErrorException, TamperedClientException
		{
			assertUsernameIsCorrect(username);
			//try to get the friends list from the database
			String message = database.MESSAGES.USERDATA.HEADING + database.MESSAGES.DELIMITER + database.MESSAGES.USERDATA.MESSAGINGSERVER.MESSAGING_SERVER_HEADING + database.MESSAGES.DELIMITER 
				+ database.MESSAGES.USERDATA.MESSAGINGSERVER.GET_USER_FRIENDS_LIST + database.MESSAGES.DELIMITER + username + database.MESSAGES.DELIMITER + password;
			String response = DatabaseConnection.sendMessageAndGetResponse(message);
			//look through the response
			Scanner scanResponse = new Scanner(response);
			String result = scanResponse.next();
			if (result.equals(database.MESSAGES.USERDATA.MESSAGINGSERVER.GET_USER_FRIENDS_LIST_SUCCESS))
			{
				//if the operation is successful, read in the friends list:
				String listOfFriends;
				//the first part should be an integer, the number of friends
				int numberOfFriends = scanResponse.nextInt();
				listOfFriends = numberOfFriends + MESSAGES.DELIMITER;
				for (int friendsScanned = 0; friendsScanned < numberOfFriends; friendsScanned++)
				{
					listOfFriends += scanResponse.next() + " ";
				}
				write(MESSAGES.MESSAGINGSERVER.GET_FRIENDS_LIST_SUCCESS + MESSAGES.DELIMITER + listOfFriends);
				scanResponse.close();
			} else if (result.equals(database.MESSAGES.USERDATA.MESSAGINGSERVER.GET_USER_FRIENDS_LIST_FAILED))
			{
				//if the operation is not successful, figure out what went wrong and let the client know
				int errorCode = scanResponse.nextInt();
				scanResponse.close();
				if (errorCode == database.MESSAGES.USERDATA.NONEXISTING_USER_ERROR_CODE)
				{
					write(MESSAGES.MESSAGINGSERVER.GET_FRIENDS_LIST_FAILED + MESSAGES.DELIMITER + MESSAGES.GENERAL.NONEXISTING_USER_ERROR_CODE);
				} else if (errorCode == database.MESSAGES.USERDATA.BAD_USER_PASSWORD_ERROR_CODE)
				{
					write(MESSAGES.MESSAGINGSERVER.GET_FRIENDS_LIST_FAILED + MESSAGES.DELIMITER + MESSAGES.GENERAL.BAD_USER_PASSWORD_ERROR_CODE);
				} else
				{
					throw generateDatabaseMiscommunicationError(response);
				}
			} else
			{
				scanResponse.close();
				throw generateDatabaseMiscommunicationError(response);
			}
		}
		
		final private void addFriend(String username, String password, String friendUsername) throws ConnectionEndedException, ServerSideErrorException, TamperedClientException
		{
			assertUsernameIsCorrect(username);
			//try to get the database to add the friend
			String message = database.MESSAGES.USERDATA.HEADING + database.MESSAGES.DELIMITER + database.MESSAGES.USERDATA.MESSAGINGSERVER.MESSAGING_SERVER_HEADING + database.MESSAGES.DELIMITER 
				+ database.MESSAGES.USERDATA.MESSAGINGSERVER.ADD_FRIEND + database.MESSAGES.DELIMITER + username + database.MESSAGES.DELIMITER + password + database.MESSAGES.DELIMITER + friendUsername;
			String response = DatabaseConnection.sendMessageAndGetResponse(message);
			//look through the response
			Scanner scanResponse = new Scanner(response);
			//the first part should be the result
			String result = scanResponse.next();
			//figure out what the result was
			if (result.equals(database.MESSAGES.USERDATA.MESSAGINGSERVER.ADD_FRIEND_SUCCESS))
			{
				//if the operation was successful let the client know
				write(MESSAGES.MESSAGINGSERVER.ADD_FRIEND_SUCCESS);
				scanResponse.close();
			} else if (result.equals(database.MESSAGES.USERDATA.MESSAGINGSERVER.ADD_FRIEND_FAILED))
			{
				//if the operation failed, figure out why and let the client know
				String messageToClient = MESSAGES.MESSAGINGSERVER.ADD_FRIEND_FAILED + MESSAGES.DELIMITER;
				//the next part will be the error code
				int errorCode = scanResponse.nextInt();
				scanResponse.close();
				//figure out which error occurred
				if (errorCode == database.MESSAGES.USERDATA.NONEXISTING_USER_ERROR_CODE)
				{
					messageToClient += MESSAGES.GENERAL.NONEXISTING_USER_ERROR_CODE;
				} else if (errorCode == database.MESSAGES.USERDATA.BAD_USER_PASSWORD_ERROR_CODE)
				{
					messageToClient += MESSAGES.GENERAL.BAD_USER_PASSWORD_ERROR_CODE;
				} else if (errorCode == database.MESSAGES.USERDATA.MESSAGINGSERVER.NONEXISTING_TARGET_ERROR)
				{
					messageToClient += MESSAGES.MESSAGINGSERVER.NONEXISTING_TARGET_ERROR_CODE;
				} else
				{
					throw generateDatabaseMiscommunicationError(response);
				}
				write(messageToClient);
			} else
			{
				scanResponse.close();
				throw generateDatabaseMiscommunicationError(response);
			}
		}
		
		final private void removeFriend(String username, String password, String friendUsername) throws ConnectionEndedException, ServerSideErrorException, TamperedClientException
		{
			assertUsernameIsCorrect(username);
			//try to get the database to remove the friend
			String message = database.MESSAGES.USERDATA.HEADING + database.MESSAGES.DELIMITER + database.MESSAGES.USERDATA.MESSAGINGSERVER.MESSAGING_SERVER_HEADING + database.MESSAGES.DELIMITER 
					+ database.MESSAGES.USERDATA.MESSAGINGSERVER.REMOVE_FRIEND + database.MESSAGES.DELIMITER + username + database.MESSAGES.DELIMITER + password + database.MESSAGES.DELIMITER + friendUsername;
			String response = DatabaseConnection.sendMessageAndGetResponse(message);
			//look through the response
			Scanner scanResponse = new Scanner(response);
			//the first part should be the result
			String result = scanResponse.next();
			if (result.equals(database.MESSAGES.USERDATA.MESSAGINGSERVER.REMOVE_FRIEND_SUCCESS))
			{
				//if the operation was successful, then let the client know
				write(MESSAGES.MESSAGINGSERVER.REMOVE_FRIEND_SUCCESS);
				scanResponse.close();
			} else if (result.equals(database.MESSAGES.USERDATA.MESSAGINGSERVER.REMOVE_FRIEND_FAILED))
			{
				//if the operation failed, then figure out why and let the client know
				String messageToClient = database.MESSAGES.USERDATA.MESSAGINGSERVER.REMOVE_FRIEND_FAILED + database.MESSAGES.DELIMITER;
				//the next part will be the error code
				int errorCode = scanResponse.nextInt();
				scanResponse.close();
				//figure out which error occurred
				if (errorCode == database.MESSAGES.USERDATA.NONEXISTING_USER_ERROR_CODE)
				{
					messageToClient += MESSAGES.GENERAL.NONEXISTING_USER_ERROR_CODE;
				} else if (errorCode == database.MESSAGES.USERDATA.BAD_USER_PASSWORD_ERROR_CODE)
				{
					messageToClient += MESSAGES.GENERAL.BAD_USER_PASSWORD_ERROR_CODE;
				} else if (errorCode == database.MESSAGES.USERDATA.NONEXISTING_TARGET_ERROR_CODE)
				{
					messageToClient += MESSAGES.MESSAGINGSERVER.NONEXISTING_TARGET_ERROR_CODE;
				} else
				{
					throw generateDatabaseMiscommunicationError(response);
				}
				write(messageToClient);
			} else
			{
				scanResponse.close();
				throw generateDatabaseMiscommunicationError(response);
			}
		}
		
		final private void sendIsUserOnline(String username, String targetUsername) throws ConnectionEndedException, TamperedClientException
		{
			assertUsernameIsCorrect(username);
			if (MessagingServer.this.isUserOnline(targetUsername) && !amIPestedByUser(username, targetUsername))
			{
				write(MESSAGES.MESSAGINGSERVER.RETURN_IS_FRIEND_ONLINE + MESSAGES.DELIMITER + true);
			} else
			{
				write(MESSAGES.MESSAGINGSERVER.RETURN_IS_FRIEND_ONLINE + MESSAGES.DELIMITER + false);
			}
		}
		
		final protected boolean amIPestedByUser(String usernameToCheck, String usernameOfPestsListOwner) throws ConnectionEndedException, TamperedClientException
		{
			assertUsernameIsCorrect(usernameToCheck);
			//try to get the database to tell if this client is on another client's pests list or not
			String message = database.MESSAGES.USERDATA.HEADING + database.MESSAGES.DELIMITER + database.MESSAGES.USERDATA.MESSAGINGSERVER.MESSAGING_SERVER_HEADING + database.MESSAGES.DELIMITER 
				+ database.MESSAGES.USERDATA.MESSAGINGSERVER.AM_I_ON_USER_PESTS_LIST + database.MESSAGES.DELIMITER + usernameToCheck + database.MESSAGES.DELIMITER + usernameOfPestsListOwner;
			String response = DatabaseConnection.sendMessageAndGetResponse(message);
			//look through the response
			Scanner scanResponse = new Scanner(response);
			//the first part should be the result of the operation
			String result = scanResponse.next();
			//figure out what the result is
			if (result.equals(database.MESSAGES.USERDATA.MESSAGINGSERVER.AM_I_ON_USER_PESTS_LIST_REQUEST_SUCCESS))
			{
				boolean rtn = scanResponse.nextBoolean();
				scanResponse.close();
				return rtn;
			} else if (result.equals(database.MESSAGES.USERDATA.MESSAGINGSERVER.AM_I_ON_USER_PESTS_LIST_REQUEST_FAILED))
			{
				//if the request was failed, use the worst case scenario, which is the client is pested
				scanResponse.close();
				return true;
			}
			scanResponse.close();
			return true;
		}
		
		final private void sendPestsListToClient(String username, String password) throws ConnectionEndedException, ServerSideErrorException, TamperedClientException
		{
			assertUsernameIsCorrect(username);
			//try to get the pests list from the database
			String message = database.MESSAGES.USERDATA.HEADING + database.MESSAGES.DELIMITER + database.MESSAGES.USERDATA.MESSAGINGSERVER.MESSAGING_SERVER_HEADING + database.MESSAGES.DELIMITER + database.MESSAGES.USERDATA.MESSAGINGSERVER.GET_USER_PESTS_LIST +
								database.MESSAGES.DELIMITER + username + database.MESSAGES.DELIMITER + password;
			String response = DatabaseConnection.sendMessageAndGetResponse(message);
			//look through the response
			Scanner scanResponse = new Scanner(response);
			//the first part should be the result of the operation
			String result = scanResponse.next();
			//figure out what the result was
			if (result.equals(database.MESSAGES.USERDATA.MESSAGINGSERVER.GET_USER_PESTS_LIST_SUCCESS))
			{
				//if the operation is successful, then read in the list of pests
				//the first part will be the number of pests;
				int numberOfPests = scanResponse.nextInt();
				String listOfPests = numberOfPests + MESSAGES.DELIMITER;
				for (int numberOfPestsRead = 0; numberOfPestsRead < numberOfPests; numberOfPestsRead++)
				{
					listOfPests += scanResponse.next() + MESSAGES.DELIMITER;
				}
				write(MESSAGES.MESSAGINGSERVER.GET_PESTS_LIST_SUCCESS + MESSAGES.DELIMITER + listOfPests);
				scanResponse.close();
			} else if (result.equals(database.MESSAGES.USERDATA.MESSAGINGSERVER.GET_USER_FRIENDS_LIST_FAILED))
			{
				//if the operation failed, figure out what went wrong and let the client know
				int errorCode = scanResponse.nextInt();
				if (errorCode == database.MESSAGES.USERDATA.NONEXISTING_USER_ERROR_CODE)
				{
					write(MESSAGES.MESSAGINGSERVER.GET_PESTS_LIST_FAILED + MESSAGES.DELIMITER + MESSAGES.GENERAL.NONEXISTING_USER_ERROR_CODE);
				} else if (errorCode == database.MESSAGES.USERDATA.BAD_USER_PASSWORD_ERROR_CODE)
				{
					write(MESSAGES.MESSAGINGSERVER.GET_PESTS_LIST_FAILED + MESSAGES.DELIMITER + MESSAGES.GENERAL.BAD_USER_PASSWORD_ERROR_CODE);
				} else
				{
					scanResponse.close();
					throw generateDatabaseMiscommunicationError(response);
				}
			} else
			{
				scanResponse.close();
				throw generateDatabaseMiscommunicationError(response);
			}
		}
		
		final private void addPest(String username, String password, String pestUsername) throws ConnectionEndedException, ServerSideErrorException, TamperedClientException
		{
			assertUsernameIsCorrect(username);
			//try to get the database to add the pest
			String message = database.MESSAGES.USERDATA.HEADING + database.MESSAGES.DELIMITER + database.MESSAGES.USERDATA.MESSAGINGSERVER.MESSAGING_SERVER_HEADING + database.MESSAGES.DELIMITER 
				+ database.MESSAGES.USERDATA.MESSAGINGSERVER.ADD_PEST + database.MESSAGES.DELIMITER + username + database.MESSAGES.DELIMITER + password + database.MESSAGES.DELIMITER + pestUsername;
			String response = DatabaseConnection.sendMessageAndGetResponse(message);
			//look through the response
			Scanner scanResponse = new Scanner(response);
			//the first part will be the result
			String result = scanResponse.next();
			if (result.equals(database.MESSAGES.USERDATA.MESSAGINGSERVER.ADD_PEST_SUCCESS))
			{
				//if the operation was successful, let the client know
				write(MESSAGES.MESSAGINGSERVER.ADD_PEST_SUCCESS);
				scanResponse.close();
			} else if (result.equals(database.MESSAGES.USERDATA.MESSAGINGSERVER.ADD_PEST_FAILED))
			{
				//if the operation failed, figure out why and then let the client know
				//begin writing the message. it just needs an error code now
				String messageToClient = MESSAGES.MESSAGINGSERVER.ADD_PEST_FAILED + MESSAGES.DELIMITER;
				//there will be an error code after
				int errorCode = scanResponse.nextInt();
				if(errorCode == database.MESSAGES.USERDATA.NONEXISTING_USER_ERROR_CODE)
				{
					messageToClient += MESSAGES.GENERAL.NONEXISTING_USER_ERROR_CODE;
				} else if (errorCode == database.MESSAGES.USERDATA.BAD_USER_PASSWORD_ERROR_CODE)
				{
					messageToClient += MESSAGES.GENERAL.BAD_USER_PASSWORD_ERROR_CODE;
				} else if (errorCode == database.MESSAGES.USERDATA.MESSAGINGSERVER.NONEXISTING_TARGET_ERROR)
				{
					messageToClient += MESSAGES.MESSAGINGSERVER.NONEXISTING_TARGET_ERROR_CODE;
				} else
				{
					scanResponse.close();
					throw generateDatabaseMiscommunicationError(response);
				}
				write(messageToClient);
			} else
			{
				scanResponse.close();
				throw generateDatabaseMiscommunicationError(response);
			}
		}
		
		final private void removePest(String username, String password, String pestUsername) throws ConnectionEndedException, ServerSideErrorException, TamperedClientException
		{
			assertUsernameIsCorrect(username);
			//try to get the database to remove the pest
			String message = database.MESSAGES.USERDATA.HEADING + database.MESSAGES.DELIMITER + database.MESSAGES.USERDATA.MESSAGINGSERVER.MESSAGING_SERVER_HEADING + database.MESSAGES.DELIMITER 
					+ database.MESSAGES.USERDATA.MESSAGINGSERVER.REMOVE_PEST + database.MESSAGES.DELIMITER + username + database.MESSAGES.DELIMITER + password + database.MESSAGES.DELIMITER + pestUsername;
			String response = DatabaseConnection.sendMessageAndGetResponse(message);
			//look through the response
			Scanner scanResponse = new Scanner(response);
			//the first part should be the result
			String result = scanResponse.next();
			if (result.equals(database.MESSAGES.USERDATA.MESSAGINGSERVER.REMOVE_PEST_SUCCESS))
			{
				//if the operation was successful, then let the client know
				write(MESSAGES.MESSAGINGSERVER.REMOVE_PEST_SUCCESS);
				scanResponse.close();
			} else if (result.equals(database.MESSAGES.USERDATA.MESSAGINGSERVER.REMOVE_PEST_FAILED))
			{
				//if the operation was successful, then figure out why and let the client know
				String messageToClient = MESSAGES.MESSAGINGSERVER.REMOVE_PEST_FAILED + MESSAGES.DELIMITER;
				//and error code should be next
				int errorCode = scanResponse.nextInt();
				scanResponse.close();
				if (errorCode == database.MESSAGES.USERDATA.NONEXISTING_USER_ERROR_CODE)
				{
					messageToClient += MESSAGES.GENERAL.NONEXISTING_USER_ERROR_CODE;
				} else if (errorCode == database.MESSAGES.USERDATA.BAD_USER_PASSWORD_ERROR_CODE)
				{
					messageToClient += MESSAGES.GENERAL.BAD_USER_PASSWORD_ERROR_CODE;
				} else if (errorCode == database.MESSAGES.USERDATA.NONEXISTING_TARGET_ERROR_CODE)
				{
					messageToClient += MESSAGES.MESSAGINGSERVER.NONEXISTING_TARGET_ERROR_CODE;
				} else
				{
					throw generateDatabaseMiscommunicationError(response);
				}
				write(messageToClient);
			} else
			{
				scanResponse.close();
				throw generateDatabaseMiscommunicationError(response);
			}
		}
		
		final private void sendPmHistoryToClient(String username, String password) throws ConnectionEndedException, ServerSideErrorException, TamperedClientException
		{
			assertUsernameIsCorrect(username);
			//try to get the PM history from the database
			String message = database.MESSAGES.USERDATA.HEADING + database.MESSAGES.DELIMITER + database.MESSAGES.USERDATA.MESSAGINGSERVER.MESSAGING_SERVER_HEADING + database.MESSAGES.DELIMITER + database.MESSAGES.USERDATA.MESSAGINGSERVER.GET_USER_PM_HISTORY
					+ database.MESSAGES.DELIMITER + username + database.MESSAGES.DELIMITER + password;
			String response = DatabaseConnection.sendMessageAndGetResponse(message);
			//look through the response
			Scanner scanResponse = new Scanner(response);
			//the first part should be the result
			String result = scanResponse.next();
			//figure out what the result was
			if (result.equals(database.MESSAGES.USERDATA.MESSAGINGSERVER.GET_USER_PM_HISTORY_SUCCESS))
			{
				//if the operation was successful, replace the database's heading with the server's heading
				String pmHistory = snipHeading(response, database.MESSAGES.USERDATA.MESSAGINGSERVER.GET_USER_PM_HISTORY_SUCCESS);
				write(MESSAGES.MESSAGINGSERVER.GET_PM_HISTORY_SUCCESS + MESSAGES.DELIMITER + pmHistory);
				scanResponse.close();
			} else if (result.equals(database.MESSAGES.USERDATA.MESSAGINGSERVER.GET_USER_PM_HISTORY_FAILED))
			{
				//if the operation failed, figure out what went wrong and let the client know
				int errorCode = scanResponse.nextInt();
				scanResponse.close();
				if (errorCode == database.MESSAGES.USERDATA.NONEXISTING_USER_ERROR_CODE)
				{
					write(MESSAGES.MESSAGINGSERVER.GET_PM_HISTORY_FAILED + MESSAGES.DELIMITER + MESSAGES.GENERAL.NONEXISTING_USER_ERROR_CODE);
				} else if (errorCode == database.MESSAGES.USERDATA.BAD_USER_PASSWORD_ERROR_CODE)
				{
					write(MESSAGES.MESSAGINGSERVER.GET_PM_HISTORY_FAILED + MESSAGES.DELIMITER + MESSAGES.GENERAL.BAD_USER_PASSWORD_ERROR_CODE);
				} else
				{
					throw generateDatabaseMiscommunicationError(response);
				}
			} else
			{
				scanResponse.close();
				throw generateDatabaseMiscommunicationError(response);
			}
		}
		
		final private void addPmOnDatabase(String senderUsername, String senderPassword, String recipientUsername, String messageContents) throws ConnectionEndedException, ServerSideErrorException, TamperedClientException
		{
			assertUsernameIsCorrect(senderUsername);
			//try to get the database to add the private message history
			String message = database.MESSAGES.USERDATA.HEADING + database.MESSAGES.DELIMITER + database.MESSAGES.USERDATA.MESSAGINGSERVER.MESSAGING_SERVER_HEADING + database.MESSAGES.DELIMITER 
				+ database.MESSAGES.USERDATA.MESSAGINGSERVER.ADD_PM_HISTORY + database.MESSAGES.DELIMITER + senderUsername + database.MESSAGES.DELIMITER + senderPassword + database.MESSAGES.DELIMITER + recipientUsername + database.MESSAGES.DELIMITER + messageContents + database.MESSAGES.DELIMITER + false;
			String response = DatabaseConnection.sendMessageAndGetResponse(message);
			//look through the response
			Scanner scanResponse = new Scanner(response);
			//the first part should be the result
			String result = scanResponse.next();
			if (result.equals(database.MESSAGES.USERDATA.MESSAGINGSERVER.ADD_PM_HISTORY_SUCCESS))
			{
				//if the operation is successful, let the client know
				write(MESSAGES.MESSAGINGSERVER.SEND_PM_SUCCESS);
				//we also have to update the recipient of this message
				//send a copy of the message to the recipient and a copy of the message to the sender
				MessagingServer.this.sendPrivateMessage(senderUsername, recipientUsername, messageContents);
				scanResponse.close();
			} else if (result.equals(database.MESSAGES.USERDATA.MESSAGINGSERVER.ADD_PM_HISTORY_FAILED))
			{
				//if the operation failed, figure out why and let the client know
				//begin writing the message. we just need an error code to be found later
				String messageToClient = MESSAGES.MESSAGINGSERVER.SEND_PM_FAILED + MESSAGES.DELIMITER;
				//the next part will be an error code
				int errorCode = scanResponse.nextInt();
				scanResponse.close();
				if (errorCode == database.MESSAGES.USERDATA.NONEXISTING_USER_ERROR_CODE)
				{
					messageToClient += MESSAGES.GENERAL.NONEXISTING_USER_ERROR_CODE;
				} else if (errorCode == database.MESSAGES.USERDATA.BAD_USER_PASSWORD_ERROR_CODE)
				{
					messageToClient += MESSAGES.GENERAL.BAD_USER_PASSWORD_ERROR_CODE;
				} else if (errorCode == database.MESSAGES.USERDATA.MESSAGINGSERVER.NONEXISTING_TARGET_ERROR)
				{
					messageToClient += MESSAGES.MESSAGINGSERVER.NONEXISTING_TARGET_ERROR_CODE;
				} else
				{
					throw generateDatabaseMiscommunicationError(response);
				}
				write(messageToClient);
			} else
			{
				scanResponse.close();
				throw generateDatabaseMiscommunicationError(response);
			}
		}
		
		final protected void addPmOnClientSide(String senderUsername, String recipientUsername, String message)
		{
			this.writeConcurrent(MESSAGES.MESSAGINGSERVER.ADD_PRIVATE_MESSAGE + MESSAGES.DELIMITER + senderUsername + MESSAGES.DELIMITER + recipientUsername + MESSAGES.DELIMITER + message);
		}
		
		final private void setPMsWithFriendAsRead(String username, String password, String friendUsername) throws ConnectionEndedException, ServerSideErrorException, TamperedClientException
		{
			assertUsernameIsCorrect(username);
			//try to get the database to do the operation
			String message = database.MESSAGES.USERDATA.HEADING + database.MESSAGES.DELIMITER + database.MESSAGES.USERDATA.MESSAGINGSERVER.MESSAGING_SERVER_HEADING + database.MESSAGES.DELIMITER + database.MESSAGES.USERDATA.MESSAGINGSERVER.SET_CONVERSATION_READ + database.MESSAGES.DELIMITER 
					+ username + database.MESSAGES.DELIMITER + password + database.MESSAGES.DELIMITER + friendUsername;
			String response = DatabaseConnection.sendMessageAndGetResponse(message);
			//look through the response
			Scanner scanResponse = new Scanner(response);
			//the first part will be the result
			String result = scanResponse.next();
			//figure out what the result it
			if (result.equals(database.MESSAGES.USERDATA.MESSAGINGSERVER.SET_CONVERSATION_READ_SUCCESS))
			{
				//if the operation was successful, let the client know
				write(MESSAGES.MESSAGINGSERVER.SET_PMS_WITH_FRIEND_AS_READ_SUCCESS);
				scanResponse.close();
			} else if (result.equals(database.MESSAGES.USERDATA.MESSAGINGSERVER.SET_CONVERSATION_READ_FAILED))
			{
				//if the operation failed, figure out why and let the client know
				//begin writing the message. we will then just need an error code
				String messageToClient = MESSAGES.MESSAGINGSERVER.SET_PMS_WITH_FRIEND_AS_READ_FAILED + MESSAGES.DELIMITER;
				//the next part of the response should be an error code
				int errorCode = scanResponse.nextInt();
				scanResponse.close();
				if (errorCode == database.MESSAGES.USERDATA.NONEXISTING_USER_ERROR_CODE)
				{
					messageToClient += MESSAGES.GENERAL.NONEXISTING_USER_ERROR_CODE;
				} else if (errorCode == database.MESSAGES.USERDATA.BAD_USER_PASSWORD_ERROR_CODE)
				{
					messageToClient += MESSAGES.GENERAL.BAD_USER_PASSWORD_ERROR_CODE;
				} else if (errorCode == database.MESSAGES.USERDATA.MESSAGINGSERVER.NONEXISTING_TARGET_ERROR)
				{
					messageToClient += MESSAGES.MESSAGINGSERVER.NONEXISTING_TARGET_ERROR_CODE;
				} else
				{
					throw generateDatabaseMiscommunicationError(response);
				}
				write(messageToClient);
			} else
			{
				scanResponse.close();
				throw generateDatabaseMiscommunicationError(response);
			}
		}
		
		@Override
		final protected String read() throws IOException
		{
			String message = super.read();
			System.out.println("Messaging Server received from client: " + message);
			return message;
		}
		
		@Override
		final protected void write(String message)
		{
			System.out.println("Messaging Server wrote to client: " + message);
			super.write(message);
		}
	}
}
