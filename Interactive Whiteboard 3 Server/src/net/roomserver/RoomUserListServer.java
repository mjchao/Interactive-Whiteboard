package net.roomserver;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import net.MESSAGES;
import net.Server;
import net.ServerSideErrorException;
import net.TamperedClientException;
import database.ConnectionEndedException;
import database.DatabaseConnection;

final public class RoomUserListServer extends RoomFeatureServer 
{
	final private ArrayList<UserPermissions> m_userPermissions = new ArrayList<UserPermissions>();
	final private ArrayList<String> m_moderators = new ArrayList<String>();
	
	final private String m_creatorUsername;
	
	private RoomUserListSubServer m_clientWithWhiteboard = null;
	/**
	 * sends system messages
	 */
	private RoomTextServer m_roomTextServer;
	/**
	 * used for kicking clients out of the room
	 */
	private RoomServer m_roomServer;
	
	public RoomUserListServer(int stepPort, int concurrencyPort, int roomID, String creatorUsername, String modificationPassword, String joinPassword, RoomServer roomServer) throws IOException, ConnectionEndedException
	{
		super(stepPort, concurrencyPort, roomID, modificationPassword, joinPassword);
		super.m_clients = new RoomUserListSubServer[Server.MAX_CLIENTS];
		this.m_creatorUsername = creatorUsername;
		this.m_roomServer = roomServer;
		loadModerators();
	}
	
	final public void setRoomTextServer(RoomTextServer roomTextServer)
	{
		this.m_roomTextServer = roomTextServer;
	}

	@Override
	protected SubServer assignClientToSubServer(Socket aClientConnection, Socket aClientConcurrencyConnection) throws IOException 
	{
		return new RoomUserListSubServer(aClientConnection, aClientConcurrencyConnection);
	}

	@Override
	final protected boolean isClientLoggedIn(String username) 
	{
		for (int clientIndex = 0; clientIndex < this.m_clients.length; clientIndex++)
		{
			RoomUserListSubServer aClient = (RoomUserListSubServer) this.m_clients[clientIndex];
			if (aClient != null)
			{
				if (aClient.getUsername().equals(username))
				{
					if (aClient.isLoggedIn() == true)
					{
						System.out.println("Still logged in");
						return true;
					}
				}
			}
		}
		return false;
	}
	
	final protected String getCreatorUsername()
	{
		return this.m_creatorUsername;
	}
	
	final private void loadModerators() throws ConnectionEndedException
	{
		String messageToDatabase = database.MESSAGES.ROOMDATA.HEADING + database.MESSAGES.DELIMITER + database.MESSAGES.ROOMDATA.ROOMSERVER.ROOM_SERVER_HEADING + database.MESSAGES.DELIMITER + database.MESSAGES.ROOMDATA.ROOMSERVER.GET_ROOM_MODERATORS + database.MESSAGES.DELIMITER + this.m_roomID + database.MESSAGES.DELIMITER + this.m_joinPassword;
		String response = DatabaseConnection.sendMessageAndGetResponse(messageToDatabase);
		//look through the response
		Scanner scanResponse = new Scanner(response);
		//first part should be result
		String result = scanResponse.next();
		if (result.equals(database.MESSAGES.ROOMDATA.ROOMSERVER.GET_ROOM_MODERATORS_SUCCESS))
		{
			//if successful, read in moderators
			//first part is number of moderators
			int numberOfModerators = scanResponse.nextInt();
			//then followed by the moderator usernames
			for (int moderatorsRead = 0; moderatorsRead < numberOfModerators; moderatorsRead++)
			{
				this.m_moderators.add(scanResponse.next());
			}
		} else
		{
			//if failed, don't read in moderators
		}
		scanResponse.close();
	}
	
	final protected boolean isModerator(String aUsername)
	{
		for (int moderatorIndex = 0; moderatorIndex < this.m_moderators.size(); moderatorIndex++)
		{
			if (this.m_moderators.get(moderatorIndex).equals(aUsername))
			{
				return true;
			}
		}
		return false;
	}
	
	final protected void sendSystemMessage(String message)
	{
		this.m_roomTextServer.sendSystemMessage(message);
	}
	
	/**
	 * sets the audio and text chat permissions for a given user
	 * 
	 * @param permissionSetter				a <code>RoomUserListSubServer</code>, the person setting the permissions
	 * @param username						a String, the username of the person who is getting his/her permissions
	 * 										modified
	 * @param audioParticipation			a boolean, if the target should be allowed to participate in audio chat
	 * @param audioListening				a boolean, if the target should be allowed to listen to audio chat
	 * @param textChatParticipation			a boolean, if the target should be allowed to participate in text chat
	 * @param textChatHistory				a boolean, if the target should be allowed to update text chat history
	 * @return								if operation was successful
	 * @throws ConnectionEndedException 	if lost connection to database
	 */
	final protected boolean setUserPermissions(RoomUserListSubServer permissionSetter, String username, boolean audioParticipation, boolean audioListening, boolean textChatParticipation, boolean textChatHistory) throws ConnectionEndedException
	{
		RoomUserListSubServer target = (RoomUserListSubServer) locateSubServer(username);
		if (target == null)
		{
			return false;
		}
		int setterAuthority = permissionSetter.getAuthority();
		int targetAuthority = target.getAuthority();
		if (setterAuthority > targetAuthority)
		{
			this.sendSystemMessage(MESSAGES.ROOMSERVER.USER_LIST_SERVER.getSetPermissionsSystemMessage(permissionSetter.getDisplayName(), target.getDisplayName(), audioParticipation, audioListening, textChatParticipation, textChatHistory));
			return target.setPermissionsOfThisUser(audioParticipation, audioListening, textChatParticipation, textChatHistory);
		}
		return false;
	}
	
	final protected void addPermissionsForUser(UserPermissions permissions)
	{
		this.m_userPermissions.add(permissions);
	}
	
	final protected void removePermissionsForUser(String username)
	{
		for (int permissionsIndex = 0; permissionsIndex < this.m_userPermissions.size(); permissionsIndex++)
		{
			UserPermissions permissions = this.m_userPermissions.get(permissionsIndex);
			if (permissions.getUsername().equals(username))
			{
				this.m_userPermissions.remove(permissionsIndex);
			}
		}
	}
	
	final public boolean doesUserHaveAudioParticipation(String username)
	{
		UserPermissions permissions = locateUserPermissions(username);
		//if permissions not stored, that means user has not lost any permissions
		if (permissions == null)
		{
			return true;
		}
		return permissions.hasAudioParticipation();
	}
	
	final public boolean doesUserHaveAudioListening(String username)
	{
		UserPermissions permissions = locateUserPermissions(username);
		//if permissions not stored, that means user has not lost any permissions
		if (permissions == null)
		{
			return true;
		}
		return permissions.hasAudioListening();
	}
	
	final public boolean doesUserHaveTextChatParticipation(String username)
	{
		UserPermissions permissions = locateUserPermissions(username);
		//if permissions not stored, that means user has not lost any permissions
		if (permissions == null)
		{
			return true;
		}
		return permissions.hasTextParticipation();
	}
	
	final public boolean doesUserHaveTextChatUpdating(String username)
	{
		UserPermissions permissions = locateUserPermissions(username);
		//if permissions not stored, that means user has not lost any permissions
		if (permissions == null)
		{
			return true;
		}
		return permissions.hasTextUpdating();
	}
	
	final public boolean doesUserHaveWhiteboard(String username)
	{
		if (this.m_clientWithWhiteboard != null)
		{
			return this.m_clientWithWhiteboard.getUsername().equals(username);
		}
		return true;
	}
	
	/**
	 * locates the user permissions of a given user
	 * 
	 * @param username			a String, username to look for 
	 * @return					a <code>UserPermissions</code>, the user permissions of the given username
	 * 							or <code>null</code> if the user permissions was not found	
	 */
	final protected UserPermissions locateUserPermissions(String username)
	{
		for (int userPermissionsIndex = 0; userPermissionsIndex < this.m_userPermissions.size(); userPermissionsIndex++)
		{
			UserPermissions aUserPermissions = this.m_userPermissions.get(userPermissionsIndex);
			if (aUserPermissions.getUsername().equals(username))
			{
				return aUserPermissions;
			}
		}
		return null;
	}
	
	/**
	 * attempts to find and remove a user from the room.
	 * 
	 * @param userThatIsKicking				a <code>RoomUserListSubServer</code>, the user trying to kick out
	 * 										another user
	 * @param userBeingKicked				a String, the username of the user being kicked
	 * @return								true if user that is doing the kicking has the authority to
	 * 										remove the other user from the room, false if the kicking
	 * 										was unsuccessful
	 * @throws ConnectionEndedException 	if the connection to the database was lost
	 */
	final protected boolean kickUser(RoomUserListSubServer userThatIsKicking, String userBeingKicked) throws ConnectionEndedException
	{
		int kickerAuthority = userThatIsKicking.getAuthority();
		for (int clientIndex = 0; clientIndex < this.m_clients.length; clientIndex++)
		{
			RoomUserListSubServer aClient = (RoomUserListSubServer) this.m_clients[clientIndex];
			if (aClient != null)
			{
				if (aClient.getUsername().equals(userBeingKicked))
				{
					RoomUserListSubServer userToKick = aClient;
					int targetAuthority = userToKick.getAuthority();
					if (kickerAuthority > targetAuthority)
					{
						sendSystemMessage(MESSAGES.ROOMSERVER.USER_LIST_SERVER.getKickedSystemMessage(userThatIsKicking.getDisplayName(), userToKick.getDisplayName()));
						userToKick.kickFromRoom(userThatIsKicking.getUsername());
						return true;
					}
					return false;
				}
			}
		}
		return false;
	}
	
	final protected void kickUserFromRoom(String username)
	{
		this.m_roomServer.kickClientFromRoom(username);
	}
	
	@Override
	public void closeConnectionWithClient(String username)
	{
		for (int clientIndex = 0; clientIndex < this.m_clients.length; clientIndex++)
		{
			RoomUserListSubServer aClient = (RoomUserListSubServer) this.m_clients[clientIndex];
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

	/**
	 * attempts to promote a user.
	 * 
	 * @param userThatIsPromoting			a <code>RoomUserListSubServer</code> the user that is promoting another user
	 * @param userBeingPromoted				a String, the username of the person to promote
	 * @return								true if the promotion was successful, false if not
	 * @throws ConnectionEndedException 	if the connection to the database was lost
	 */
	final protected boolean promoteUser(RoomUserListSubServer userThatIsPromoting, String userBeingPromoted) throws ConnectionEndedException
	{
		RoomUserListSubServer userToPromote = (RoomUserListSubServer) this.locateSubServer(userBeingPromoted);
		if (userToPromote == null)
		{
			return false;
		}
		int authorityOfUserThatIsPromoting = userThatIsPromoting.getAuthority();
		int authorityOfUserBeingPromoted = userToPromote.getAuthority();
		if (authorityOfUserThatIsPromoting > authorityOfUserBeingPromoted + 1)
		{
			sendSystemMessage(MESSAGES.ROOMSERVER.USER_LIST_SERVER.getPromotionSystemMessage(userThatIsPromoting.getDisplayName(), userToPromote.getDisplayName()));
			return userToPromote.promote();
		}
		return false;
	}
	
	final protected boolean demoteUser(RoomUserListSubServer userThatIsDemoting, String userToBeDemoted) throws ConnectionEndedException
	{
		RoomUserListSubServer userToDemote = (RoomUserListSubServer) this.locateSubServer(userToBeDemoted);
		if (userToDemote == null)
		{
			return false;
		}
		int authorityOfUserThatIsDemoting = userThatIsDemoting.getAuthority();
		int authorityOfUserBeingDemoted = userToDemote.getAuthority();
		if (authorityOfUserThatIsDemoting > authorityOfUserBeingDemoted)
		{
			sendSystemMessage(MESSAGES.ROOMSERVER.USER_LIST_SERVER.getDemotionSystemMessage(userThatIsDemoting.getDisplayName(), userToDemote.getDisplayName()));
			return userToDemote.demote();
		}
		return false;
	}
	
	final protected boolean giveWhiteboardToUser(RoomUserListSubServer userGivingWhiteboard, String userReceivingWhiteboard)
	{
		RoomUserListSubServer recipient = (RoomUserListSubServer) this.locateSubServer(userReceivingWhiteboard);
		if (recipient == null)
		{
			return false;
		}
		userGivingWhiteboard.setLostPossessionOfWhiteboard();
		recipient.setGainedPossessionOfWhiteboard();
		this.m_clientWithWhiteboard = recipient;
		sendSystemMessage(MESSAGES.ROOMSERVER.USER_LIST_SERVER.getGiveWhiteboardSystemMessage(userGivingWhiteboard.getDisplayName(), recipient.getDisplayName()));
		sendClientDataToAllUsers(userGivingWhiteboard);
		sendClientDataToAllUsers(recipient);
		return true;
	}
	
	final protected boolean takeWhiteboardFromUser(RoomUserListSubServer userTakingWhiteboard, String userLosingWhiteboard)
	{
		RoomUserListSubServer target = (RoomUserListSubServer) this.locateSubServer(userLosingWhiteboard);
		if (target == null)
		{
			return false;
		}
		if (userTakingWhiteboard.getAuthority() > target.getAuthority())
		{
			userTakingWhiteboard.setGainedPossessionOfWhiteboard();
			target.setLostPossessionOfWhiteboard();
			this.m_clientWithWhiteboard = userTakingWhiteboard;
			sendSystemMessage(MESSAGES.ROOMSERVER.USER_LIST_SERVER.getTakeWhiteboardSystemMessage(userTakingWhiteboard.getDisplayName(), target.getDisplayName()));
			sendClientDataToAllUsers(userTakingWhiteboard);
			sendClientDataToAllUsers(target);
			return true;
		}
		return false;
	}
	
	final protected void showUserLeft(RoomUserListSubServer userThatLeft)
	{
		String username = userThatLeft.getUsername();
		String messageToEachClient = MESSAGES.ROOMSERVER.USER_LIST_SERVER.USER_LEFT + MESSAGES.DELIMITER + username;
		for (int clientIndex = 0; clientIndex < this.m_clients.length; clientIndex++)
		{
			RoomUserListSubServer aClient = (RoomUserListSubServer) this.m_clients[clientIndex];
			if (aClient != null)
			{
				aClient.writeConcurrent(messageToEachClient);
			}
		}
		this.m_roomTextServer.sendSystemMessage(MESSAGES.ROOMSERVER.USER_LIST_SERVER.getLeaveSystemMessage(userThatLeft.getDisplayName()));
		this.m_roomServer.handleClientLeft(username);
	}
	
	final protected void sendCurrentUserDataToClient(RoomUserListSubServer clientToUpdate)
	{
		for (int clientIndex = 0; clientIndex < this.m_clients.length; clientIndex++)
		{
			RoomUserListSubServer aClient = (RoomUserListSubServer) this.m_clients[clientIndex];
			if (aClient != null)
			{
				String messageToClient = getShowUserPermissionsMessage(aClient);
				clientToUpdate.writeConcurrent(messageToClient);
			}
		}
	}
	
	final protected void sendClientDataToAllUsers(RoomUserListSubServer aClient)
	{
		for (int clientIndex = 0; clientIndex < this.m_clients.length; clientIndex++)
		{
			RoomUserListSubServer userToUpdate = (RoomUserListSubServer) this.m_clients[clientIndex];
			if (userToUpdate != null)
			{
				String messageToUserToUpdate = getShowUserPermissionsMessage(aClient);
				userToUpdate.writeConcurrent(messageToUserToUpdate);
			}
		}
	}
	
	final private String getShowUserPermissionsMessage(RoomUserListSubServer aClient)
	{
		String clientUsername = aClient.getUsername();
		String clientDisplayName = null;
		clientDisplayName = aClient.getDisplayName();
		if (clientDisplayName == null)
		{
			clientDisplayName = "UNKNOWN";
		}
		boolean audioParticipation = this.doesUserHaveAudioParticipation(clientUsername);
		boolean audioListening = this.doesUserHaveAudioListening(clientUsername);
		boolean textParticipation = this.doesUserHaveTextChatParticipation(clientUsername);
		boolean textUpdating = this.doesUserHaveTextChatUpdating(clientUsername);
		boolean hasWhiteboard = aClient.hasWhiteboard();
		String showUserPermissionsMessage = MESSAGES.ROOMSERVER.USER_LIST_SERVER.SHOW_USER_PERMISSIONS_FOR_USER + MESSAGES.DELIMITER + aClient.getUsername() + MESSAGES.DELIMITER + clientDisplayName + MESSAGES.DELIMITER + aClient.getAuthority() + MESSAGES.DELIMITER + audioParticipation + MESSAGES.DELIMITER + audioListening + MESSAGES.DELIMITER + textParticipation + MESSAGES.DELIMITER + textUpdating + MESSAGES.DELIMITER + hasWhiteboard;
		return showUserPermissionsMessage;
	}
	
	final protected RoomUserListSubServer getClientWithWhiteboard()
	{
		return this.m_clientWithWhiteboard;
	}
	
	final protected void setClientWithWhiteboard(RoomUserListSubServer aClient)
	{
		this.m_clientWithWhiteboard = aClient;
	}
	
	final private class RoomUserListSubServer extends RoomFeatureSubServer
	{
		final public static int DEFAULT_AUTHORITY = 0;
		final public static int MODERATOR_AUTHORITY = 1;
		final public static int ADMINISTRATOR_AUTHORITY = 2;
		
		private int m_authority = DEFAULT_AUTHORITY;
		private boolean m_hasWhiteboard = false;
		
		public RoomUserListSubServer(Socket s, Socket concurrentConnection) throws IOException 
		{
			super(s, concurrentConnection);
		}

		@Override
		protected void decode(String messageFromClient) throws ConnectionEndedException, ServerSideErrorException, TamperedClientException, NoSuchElementException 
		{
			//look through the message
			Scanner scanMessage = new Scanner(messageFromClient);
			//first part should be command
			String command = scanMessage.next();
			//figure out what client wants
			if (command.equals(MESSAGES.GENERAL.LOGIN))
			{
				//expect a username, a password and a join password
				String username = scanMessage.next();
				String password = scanMessage.next();
				String joinPassword = scanMessage.next();
				login(username, password, joinPassword);
				//if login successful, check if the creator of the room just logged in
				//the creator is always the administrator
				if (this.loggedIn)
				{
					if (RoomUserListServer.this.isModerator(username))
					{
						this.m_authority = MODERATOR_AUTHORITY;
					}
					if (RoomUserListServer.this.getCreatorUsername().equals(username))
					{
						this.m_authority = ADMINISTRATOR_AUTHORITY;
					}
					//if nobody is in the room yet (i.e. nobody has the whiteboard)
					//then the first person to enter the room gets the whiteboard
					if (RoomUserListServer.this.getClientWithWhiteboard() == null)
					{
						RoomUserListServer.this.setClientWithWhiteboard(this);
						setGainedPossessionOfWhiteboard();
					}
					sendClientDataToAllUsers(this);
					RoomUserListServer.this.sendSystemMessage(MESSAGES.ROOMSERVER.USER_LIST_SERVER.getJoinSystemMessage(getDisplayName()));
					updateClientUserList();
				}
			} else if (this.loggedIn)
			{
				if (command.equals(MESSAGES.ROOMSERVER.USER_LIST_SERVER.SET_USER_PERMISSIONS))
				{
					//expect a join password, a username and four booleans - the user permissions
					String username = scanMessage.next();
					String joinPassword = scanMessage.next();
					String targetUsername = scanMessage.next();
					boolean audioParticipation = scanMessage.nextBoolean();
					boolean audioListening = scanMessage.nextBoolean();
					boolean textParticipation = scanMessage.nextBoolean();
					boolean textHistory = scanMessage.nextBoolean();
					setUserPermissions(username, joinPassword, targetUsername, audioParticipation, audioListening, textParticipation, textHistory);
				} else if (command.equals(MESSAGES.ROOMSERVER.USER_LIST_SERVER.KICK_USER))
				{
					//expect a username, a join password and the username of someone to kick
					String username = scanMessage.next();
					String joinPassword = scanMessage.next();
					String usernameOfTargetToKick = scanMessage.next();
					kickAnotherUser(username, joinPassword, usernameOfTargetToKick);
				} else if (command.equals(MESSAGES.ROOMSERVER.USER_LIST_SERVER.PROMOTE_USER))
				{
					//expect a username, a join password and the username of someone to promote
					String username = scanMessage.next();
					String modificationPassword = scanMessage.next();
					String joinPassword = scanMessage.next();
					String usernameOfTargetToPromote = scanMessage.next();
					this.promoteAnotherUser(username, modificationPassword, joinPassword, usernameOfTargetToPromote);
				} else if (command.equals(MESSAGES.ROOMSERVER.USER_LIST_SERVER.DEMOTE_USER))
				{
					//expect a username, a join password and the username of someone to demote
					String username = scanMessage.next();
					String joinPassword = scanMessage.next();
					String modificationPassword = scanMessage.next();
					String usernameOfTargetToDemote = scanMessage.next();
					demoteAnotherUser(username, joinPassword, modificationPassword, usernameOfTargetToDemote);
				} else if (command.equals(MESSAGES.ROOMSERVER.USER_LIST_SERVER.GIVE_WHITEBOARD))
				{
					//expect a username, a join password and the username of the person receiving the whiteboard
					String username = scanMessage.next();
					String joinPassword = scanMessage.next();
					String recipientUsername = scanMessage.next();
					giveWhiteboardToUser(username, joinPassword, recipientUsername);
				} else if (command.equals(MESSAGES.ROOMSERVER.USER_LIST_SERVER.TAKE_WHITEBOARD))
				{
					//expect a username, a join password and the username of the person from whom to take the whiteboard
					String username = scanMessage.next();
					String joinPassword = scanMessage.next();
					String targetUsername = scanMessage.next();
					takeWhiteboardFromUser(username, joinPassword, targetUsername);
				} else if (command.equals(MESSAGES.ROOMSERVER.USER_LIST_SERVER.LEAVE))
				{
					leave();
				}
			} else
			{
				this.closeClientPerformingUnauthorizedActions();
			}
		}
		
		@Override
		protected void handleTerminatingConnection() 
		{
			this.closeAndStop();
			RoomUserListServer.this.removeAClient(this.m_subServerID);
		}
		
		final protected int getAuthority()
		{
			return this.m_authority;
		}
		
		final protected String getDisplayName() 
		{
			try
			{
				return getDisplayNameOfOtherUserInRoom(this.m_username, this.m_username);
			} catch (ConnectionEndedException e)
			{
				return "UNKNOWN";
			}
		}
		
		final protected void setLostPossessionOfWhiteboard()
		{
			this.m_hasWhiteboard = false;
		}
		
		final protected void setGainedPossessionOfWhiteboard()
		{
			this.m_hasWhiteboard = true;
		}
		
		final protected boolean hasWhiteboard()
		{
			return this.m_hasWhiteboard;
		}
		
		final private void setUserPermissions(String username, String joinPassword, String targetUsername, boolean audioParticipation, boolean audioListening, boolean textParticipation, boolean textHistory) throws TamperedClientException, ConnectionEndedException
		{
			assertUsernameIsCorrect(username);
			assertJoinPasswordIsCorrect(joinPassword);
			if (RoomUserListServer.this.setUserPermissions(this, targetUsername, audioParticipation, audioListening, textParticipation, textHistory))
			{
				write(MESSAGES.ROOMSERVER.USER_LIST_SERVER.SET_USER_PERMISSIONS_SUCCESS);
			} else
			{
				//we do not check for the case where the user does not exist
				//because we are certain the client can only kick users that are in the room
				//and if the client kicks someone not in the room, then the client
				//is abusing the client program, so we don't care.
				write(MESSAGES.ROOMSERVER.USER_LIST_SERVER.SET_USER_PERMISSIONS_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMSERVER.AUTHORITY_TOO_LOW_ERROR_CODE);
			}
		}
		
		/**
		 * sets the permissions for a user
		 * 
		 * @param audioParticipation				a boolean, true if audio participation is enabled, false if not
		 * @param audioListening					a boolean, true if audio listening is enabled, false if not
		 * @param textChatParticipation				a boolean, true if text chat participation is enabled, false if not
		 * @param textChatHistory					a boolean, true if text chat updates for the user, false if not
		 * @return									if operation successful or not
		 * @throws ConnectionEndedException			if lost connection with database
		 */
		final protected boolean setPermissionsOfThisUser(boolean audioParticipation, boolean audioListening, boolean textChatParticipation, boolean textChatHistory) throws ConnectionEndedException
		{
			//if the setter has greater authority, then set the permissions
			String messageToDatabase = database.MESSAGES.ROOMDATA.HEADING + database.MESSAGES.DELIMITER + database.MESSAGES.ROOMDATA.ROOMSERVER.ROOM_SERVER_HEADING + database.MESSAGES.DELIMITER + database.MESSAGES.ROOMDATA.ROOMSERVER.ADD_USER_PERMISSIONS 
					+ database.MESSAGES.DELIMITER + RoomUserListServer.this.getRoomID() + database.MESSAGES.DELIMITER + RoomUserListServer.this.getJoinPassword() + database.MESSAGES.DELIMITER + this.m_username + database.MESSAGES.DELIMITER + audioParticipation + database.MESSAGES.DELIMITER + audioListening + database.MESSAGES.DELIMITER + textChatParticipation + database.MESSAGES.DELIMITER + textChatHistory;
			String response = DatabaseConnection.sendMessageAndGetResponse(messageToDatabase);
			//look through the response
			Scanner scanResponse = new Scanner(response);
			//first part should be the result
			String result = scanResponse.next();
			if (result.equals(database.MESSAGES.ROOMDATA.ROOMSERVER.ADD_USER_PERMISSIONS_SUCCESS))
			{
				UserPermissions userPermissions = RoomUserListServer.this.locateUserPermissions(this.m_username);
				//if the target lost one or more permissions, then update
				if (audioParticipation == false || audioListening == false || textChatParticipation == false || textChatHistory == false)
				{
					if (userPermissions == null)
					{
						RoomUserListServer.this.addPermissionsForUser(new UserPermissions(this.m_username, audioParticipation, audioListening, textChatParticipation, textChatHistory));
					} else
					{
						userPermissions.setAudioParticipation(audioParticipation);
						userPermissions.setAudioListening(audioListening);
						userPermissions.setTextParticipation(textChatParticipation);
						userPermissions.setTextUpdating(textChatHistory);
					}
				//if the target has lost no permissions, remove the target from the user permissions list
				} else
				{
					RoomUserListServer.this.removePermissionsForUser(this.m_username);
				}
				RoomUserListServer.this.sendClientDataToAllUsers(this);
				return true;
			} else if (result.equals(database.MESSAGES.ROOMDATA.ROOMSERVER.ADD_USER_PERMISSIONS_FAILED))
			{
				//not checking for errors here because they should not occur
				//unless client has been tampered with
				return false;
			}
			return false;
		}
		
		/**
		 * kicks a user out of the room
		 * 
		 * @param username							a String, the username of this user
		 * @param joinPassword						a String, the join password for this room
		 * @param userToKick						a String, the username of the person to kick
		 * @throws TamperedClientException 			if the username or join password provided were incorrect
		 * @throws ConnectionEndedException 		if the connection to the database was lost
		 */
		final private void kickAnotherUser(String username, String joinPassword, String userToKick) throws TamperedClientException, ConnectionEndedException
		{
			assertUsernameIsCorrect(username);
			assertJoinPasswordIsCorrect(joinPassword);
			if(RoomUserListServer.this.kickUser(this, userToKick))
			{
				write(MESSAGES.ROOMSERVER.USER_LIST_SERVER.KICK_USER_SUCCESS);
			} else
			{
				//we do not check for the case where the user does not exist
				//because we are certain the client can only kick users that are in the room
				//and if the client kicks someone not in the room, then the client
				//is abusing the client program, so we don't care.
				write(MESSAGES.ROOMSERVER.USER_LIST_SERVER.KICK_USER_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMSERVER.AUTHORITY_TOO_LOW_ERROR_CODE);
			}
		}
		
		/**
		 * terminates this connection and removes the user from the room because s/he
		 * was kicked
		 */
		final protected void kickFromRoom(String usernameOfKicker)
		{
			writeConcurrent(MESSAGES.ROOMSERVER.USER_LIST_SERVER.KICKED + MESSAGES.DELIMITER + usernameOfKicker);
			//the room server, the server at the highest level, will handle this kick, not this server
			RoomUserListServer.this.kickUserFromRoom(this.getUsername());
			RoomUserListServer.this.showUserLeft(this);
		}
		
		final private void promoteAnotherUser(String username, String modificationPassword, String joinPassword, String userToPromote) throws TamperedClientException, ConnectionEndedException
		{
			assertUsernameIsCorrect(username);
			assertJoinPasswordIsCorrect(joinPassword);
			if (isModificationPasswordCorrect(modificationPassword))
			{
				if (RoomUserListServer.this.promoteUser(this, userToPromote))
				{
					write(MESSAGES.ROOMSERVER.USER_LIST_SERVER.PROMOTE_USER_SUCCESS);
				} else
				{
					write(MESSAGES.ROOMSERVER.USER_LIST_SERVER.PROMOTE_USER_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMSERVER.AUTHORITY_TOO_LOW_ERROR_CODE);
				}
			} else
			{
				write(MESSAGES.ROOMSERVER.USER_LIST_SERVER.PROMOTE_USER_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMSERVER.INVALID_MODIFICATION_PASSWORD_ERROR_CODE);
			}
		}
		
		
		/**
		 * promotes this user. assumes the modification password and join password provided by the promoter
		 * were correct
		 * 
		 * @return										true if the operation was successful, false otherwise
		 * @throws ConnectionEndedException				if the connection to the database was lost
		 */
		final protected boolean promote() throws ConnectionEndedException
		{
			int roomID = RoomUserListServer.this.getRoomID();
			String modificationPassword = RoomUserListServer.this.getModificationPassword();
			String joinPassword = RoomUserListServer.this.getJoinPassword();
			String messageToDatabase = database.MESSAGES.ROOMDATA.HEADING + database.MESSAGES.DELIMITER + database.MESSAGES.ROOMDATA.ROOMSERVER.ROOM_SERVER_HEADING + database.MESSAGES.DELIMITER + database.MESSAGES.ROOMDATA.ROOMSERVER.ADD_ROOM_MODERATOR + database.MESSAGES.DELIMITER + roomID + database.MESSAGES.DELIMITER + modificationPassword + database.MESSAGES.DELIMITER + joinPassword + database.MESSAGES.DELIMITER + this.m_username;
			String response = DatabaseConnection.sendMessageAndGetResponse(messageToDatabase);
			//look through response
			Scanner scanResponse = new Scanner(response);
			//first part should be result
			String result = scanResponse.next();
			if (result.equals(database.MESSAGES.ROOMDATA.ROOMSERVER.ADD_ROOM_MODERATOR_SUCCESS))
			{
				if (this.m_authority == DEFAULT_AUTHORITY)
				{
					this.m_authority = MODERATOR_AUTHORITY;
				}
				RoomUserListServer.this.sendClientDataToAllUsers(this);
				return true;
			}
			return false;
		}
		
		final private void demoteAnotherUser(String username, String modificationPassword, String joinPassword, String userToDemote) throws TamperedClientException, ConnectionEndedException
		{
			assertUsernameIsCorrect(username);
			assertJoinPasswordIsCorrect(joinPassword);
			if (isModificationPasswordCorrect(modificationPassword))
			{
				if (RoomUserListServer.this.demoteUser(this, userToDemote))
				{
					write(MESSAGES.ROOMSERVER.USER_LIST_SERVER.DEMOTE_USER_SUCCESS);
				} else
				{
					write(MESSAGES.ROOMSERVER.USER_LIST_SERVER.DEMOTE_USER_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMSERVER.AUTHORITY_TOO_LOW_ERROR_CODE);
				}
			} else
			{
				write(MESSAGES.ROOMSERVER.USER_LIST_SERVER.DEMOTE_USER_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMSERVER.INVALID_MODIFICATION_PASSWORD_ERROR_CODE);
			}
		}
		
		/**
		 * demotes this user. assumes the modification password and join password provided by the demoter
		 * were correct
		 * 
		 * @return										true if the operation was successful, false otherwise
		 * @throws ConnectionEndedException				if the connection to the database was lost
		 */
		final protected boolean demote() throws ConnectionEndedException
		{
			int roomID = RoomUserListServer.this.getRoomID();
			String modificationPassword = RoomUserListServer.this.getModificationPassword();
			String joinPassword = RoomUserListServer.this.getJoinPassword();
			String messageToDatabase = database.MESSAGES.ROOMDATA.HEADING + database.MESSAGES.DELIMITER + database.MESSAGES.ROOMDATA.ROOMSERVER.ROOM_SERVER_HEADING + database.MESSAGES.DELIMITER + database.MESSAGES.ROOMDATA.ROOMSERVER.REMOVE_ROOM_MODERATOR + database.MESSAGES.DELIMITER + roomID + database.MESSAGES.DELIMITER + modificationPassword + database.MESSAGES.DELIMITER + joinPassword + database.MESSAGES.DELIMITER + this.m_username;
			String response = DatabaseConnection.sendMessageAndGetResponse(messageToDatabase);
			//look through response
			Scanner scanResponse = new Scanner(response);
			//first part should be result
			String result = scanResponse.next();
			if (result.equals(database.MESSAGES.ROOMDATA.ROOMSERVER.REMOVE_ROOM_MODERATOR_SUCCESS))
			{
				if (this.m_authority == MODERATOR_AUTHORITY)
				{
					this.m_authority = DEFAULT_AUTHORITY;
				}
				RoomUserListServer.this.sendClientDataToAllUsers(this);
				return true;
			}
			return false;
		}
		
		final private void giveWhiteboardToUser(String usernameOfGiver, String joinPassword, String usernameOfRecipient) throws TamperedClientException
		{
			assertUsernameIsCorrect(usernameOfGiver);
			assertJoinPasswordIsCorrect(joinPassword);
			if (hasWhiteboard())
			{
				if (RoomUserListServer.this.giveWhiteboardToUser(this, usernameOfRecipient) == true)
				{
					write(MESSAGES.ROOMSERVER.USER_LIST_SERVER.GIVE_WHITEBOARD_SUCCESS);
				} else
				{
					//should not happen unless the user does not exist, but the client
					//cannot provide a non-existing username unless s/he tampered with the code,
					//which we do not care about
				}
			} else
			{
				write(MESSAGES.ROOMSERVER.USER_LIST_SERVER.GIVE_WHITEBOARD_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMSERVER.YOU_LACK_WHITEBOARD_ERROR_CODE);
			}
		}
		
		final private void takeWhiteboardFromUser(String usernameOfTaker, String joinPassword, String userToTakeFrom) throws TamperedClientException
		{
			assertUsernameIsCorrect(usernameOfTaker);
			assertJoinPasswordIsCorrect(joinPassword);
			//make sure client is taking from someone with the whiteboard
			if (RoomUserListServer.this.getClientWithWhiteboard().getUsername().equals(userToTakeFrom))
			{				
				if (RoomUserListServer.this.takeWhiteboardFromUser(this, userToTakeFrom) == true)
				{
					write(MESSAGES.ROOMSERVER.USER_LIST_SERVER.TAKE_WHITEBOARD_SUCCESS);
				} else
				{
					write(MESSAGES.ROOMSERVER.USER_LIST_SERVER.TAKE_WHITEBOARD_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMSERVER.AUTHORITY_TOO_LOW_ERROR_CODE);
				}
			} else
			{
				write(MESSAGES.ROOMSERVER.USER_LIST_SERVER.TAKE_WHITEBOARD_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMSERVER.USER_LIST_SERVER.TARGET_LACKS_WHITEBOARD_ERROR);
			}
		}
		
		final private void leave()
		{
			write(MESSAGES.ROOMSERVER.USER_LIST_SERVER.LEAVE_SUCCESS);
			this.closeAndStop();
			RoomUserListServer.this.removeAClient(this.m_subServerID);
			RoomUserListServer.this.showUserLeft(this);
		}
		
		final private void updateClientUserList()
		{
			RoomUserListServer.this.sendCurrentUserDataToClient(this);
		}
		
		@Override
		final protected String read() throws IOException
		{
			String message = super.read();
			System.out.println("Room User List Server received from client: " + message);
			return message;
		}
		
		@Override
		final protected void write(String message)
		{
			System.out.println("Room User List Server wrote to client: " + message);
			super.write(message);
		}
		
		@Override
		final protected void writeConcurrent(String message)
		{
			System.out.println("Room User List Server wrote concurrently to client: " + message);
			super.writeConcurrent(message);
		}
	}
	
	/**
	 * Permissions for users that are stored even when they are gone.
	 * They include audio and text chat permissions
	 */
	final private class UserPermissions
	{
		final private String m_username;
		//audio and text chat
		private boolean m_hasAudioParticipation;
		private boolean m_hasAudioListening;
		private boolean m_hasTextParticipation;
		private boolean m_hasTextUpdating;
		
		public UserPermissions(String username, boolean hasAudioParticipation, boolean hasAudioListening, boolean hasTextParticipation, boolean hasTextUpdating)
		{
			this.m_username = username;
			this.m_hasAudioParticipation = hasAudioParticipation;
			this.m_hasAudioListening = hasAudioListening;
			this.m_hasTextParticipation = hasTextParticipation;
			this.m_hasTextUpdating = hasTextUpdating;
		}
		
		final public String getUsername()
		{
			return this.m_username;
		}
		
		final public boolean hasAudioParticipation()
		{
			return this.m_hasAudioParticipation;
		}
		
		final public void setAudioParticipation(boolean b)
		{
			this.m_hasAudioParticipation = b;
		}
		
		final public boolean hasAudioListening()
		{
			return this.m_hasAudioListening;
		}
		
		final public void setAudioListening(boolean b)
		{
			this.m_hasAudioListening = b;
		}
		
		final public boolean hasTextParticipation()
		{
			return this.m_hasTextParticipation;
		}
		
		final public void setTextParticipation(boolean b)
		{
			this.m_hasTextParticipation = b;
		}
		
		final public boolean hasTextUpdating()
		{
			return this.m_hasTextUpdating;
		}
		
		final public void setTextUpdating(boolean b)
		{
			this.m_hasTextUpdating = b;
		}
	}
}
