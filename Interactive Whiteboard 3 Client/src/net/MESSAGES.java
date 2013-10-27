package net;

final public class MESSAGES
{
	final public static String SPACE_SUBSTITUTE = "\u00D0";
	final public static String DELIMITER = " ";

	/**
	 * given a string, this message replaces all message delimiters with a substitute, so that the
	 * other side does not read those in. For example, if you wanted to send "No Name" across to the server,
	 * the server would read that as "No" and then "Name" because of the space between "No" and "Name". To
	 * solve the problem, we substitute in "\u00D0" for the space, so the server ignores the space.
	 * Later, the "\u00D0" can be replaced on client-side for displaying text to the user.
	 * 
	 * @param aMessage			a String, the message with message delimiters that need to be substituted
	 * @return					a String, the message with all message delimiters substituted out.
	 * @see						#unsubstituteForMessageDelimiters(String)
	 * @see						#SPACE_SUBSTITUTE
	 * @see						#DELIMITER
	 */
	final public static String substituteForMessageDelimiters(String aMessage)
	{
		String rtn = aMessage;
		for (int charIndex = 0; charIndex < aMessage.length(); charIndex++)
		{
			if (rtn.substring(charIndex, charIndex + 1).equals(MESSAGES.DELIMITER))
			{
				rtn = rtn.substring(0, charIndex) + SPACE_SUBSTITUTE + rtn.substring(charIndex + 1, rtn.length());
			}
		}
		return rtn;
	}
	
	/**
	 * Replaces all space substitutes in the message with the space. For example, to register
	 * your display name as "No Name", you had to send to the server "No\u00D0Name" because the "\u00D0"
	 * represents a space and the server uses the space to separate out different parts of messages. Now,
	 * if the name is sent to a client, the client will need to remove the "\u00D0" and replace it with " ",
	 * so that is the purpose of this method.
	 * 
	 * @param aMessage			a String, the message that needs space substitutions replaced with spaces
	 * @return					a String, the message, back to normal now, with spaces.
	 * @see						#substituteForMessageDelimiters(String)
	 * @see						#SPACE_SUBSTITUTE
	 * @see						#DELIMITER
	 */
	final public static String unsubstituteForMessageDelimiters(String aMessage)
	{
		String rtn = aMessage;
		for (int charIndex = 0; charIndex < aMessage.length(); charIndex++)
		{
			if (rtn.substring(charIndex, charIndex + 1).equals(MESSAGES.SPACE_SUBSTITUTE))
			{
				rtn = rtn.substring(0, charIndex) + DELIMITER + rtn.substring(charIndex + 1, rtn.length());
			}
		}
		return rtn;
	}
		
	final public static boolean containsBadCharacters(String password)
	{
		return password.contains(MESSAGES.SPACE_SUBSTITUTE);
	}
	
	final public static boolean isAllSpaces(String aMessage)
	{
		for (int characterIndex = 0; characterIndex < aMessage.length(); characterIndex++)
		{
			if (aMessage.charAt(characterIndex) != ' ')
			{
				return false;
			}
		}
		return true;
	}
	
	final public static class CONNECTION
	{
		final public static String CLOSING_MAXIMUM_CLIENTS_REACHED = "<CLOSING_MAXIMUM_CLIENTS_REACHED>";
		final public static String CLOSING_LOGIN_FAILED = "<CLOSING_LOGIN_FAILED>";
		final public static String CLOSING_SERVER_SIDE = "<CLOSING_SERVER_SIDE>";
		final public static String CLOSING_SERVER_SIDE_ERROR = "<SERVER_SIDE_ERROR>";
	}
	
	final public static class GENERAL
	{	
		//this error code means a non-existing user was mentioned
		final public static int NONEXISTING_USER_ERROR_CODE = -1;
		//this error code means a bad password was provided
		final public static int BAD_USER_PASSWORD_ERROR_CODE = -2;
		
		//this message requires a username and a password
		final public static String LOGIN = "<LOGIN>";
			final public static String LOGIN_SUCCESS = "<LOGIN_SUCCESS>";
				//this messages does not reply with anything else
			final public static String LOGIN_FAILED = "<LOGIN_FAILED>";
				//this message may reply with a bad user information error code
				final public static int BAD_USER_INFORMATION_ERROR_CODE = 1;
				//this message may reply with an already logged in error code
				final public static int ALREADY_LOGGED_IN_ERROR_CODE = 2;
				/**
				 * signifies a join password for a room was incorrect
				 */
				final public static int INVALID_JOIN_PASSWORD_ERROR_CODE = 3;
				
		//this message requires a username, a password and another username
		final public static String GET_DISPLAY_NAME_OF_OTHER_USER = "<GET_DISPLAY_NAME_OF_OTHER_USER>";
			final public static String GET_DISPLAY_NAME_OF_OTHER_USER_SUCCESS = "<RETURN_DISPLAY_NAME_OF_OTHER_USER>";
				//this message replies with a String, the display name
			final public static String GET_DISPLAY_NAME_OF_OTHER_USER_FAILED = "<GET_DISPLAY_NAME_OF_OTHER_USER_FAILED>";
				//this message does not reply with anything - it's not a big deal if it's not found
	}	
	
	final public static class MAINSERVER
	{
		//no heading is required here, because we are using different sockets for different servers

		//this error means the new password is invalid
		final public static int INVALID_NEW_PASSWORD_ERROR_CODE = -3;
			
		//this message requires a username and a password
		final public static String REGISTER = "<REGISTER>";
			final public static String REGISTER_SUCCESS = "<REGISTER_SUCCESS>";
				//this message does not reply with anything else
			final public static String REGISTER_FAILED = "<REGISTER_FAILED>";
				//this message may reply with a username in user error code
				final public static int USERNAME_IN_USE_ERROR_CODE = 1;
				//this message may reply with an invalid new username error code
				final public static int INVALID_NEW_USERNAME_ERROR_CODE = 2;
				//this message may reply with an invalid password error code
				//this mesasge may reply with an IO Exception error code, which means the database server had an error
				final public static int IO_EXCEPTION = 3;
			
		//this message requires the current password of the user and the new password
		final public static String CHANGE_PASSWORD = "<CHANGE_PASSWORD>";
			final public static String CHANGE_PASSWORD_SUCCESS = "<CHANGE_PASSWORD_SUCCESS>";
				//this message does not reply with anything else
			final public static String CHANGE_PASSWORD_FAILED = "<CHANGE_PASSWORD_FAILED>";
				//this message may reply with a non-existing user error code
				//this message may reply with a bad user password error code
				//this message may reply with an invalid password error code
		
		//this message requires a password, name change code and a new display name
		final public static String CHANGE_DISPLAY_NAME = "<CHANGE_DISPLAY_NAME>";
			final public static String CHANGE_DISPLAY_NAME_SUCCESS = "<CHANGE_DISPLAY_NAME_SUCCESS>";
				//this message does not reply with anything else
			final public static String CHANGE_DISPLAY_NAME_FAILED = "<CHANGE_DISPLAY_NAME_FAILED>";
				//this message may reply a non-existing user error code
				//this message may reply with an bad user password error code
				//this message may reply with an invalid name change code error code
				final public static int BAD_NAME_CHANGE_CODE_ERROR_CODE = 1;
				//this message may reply with an invalid new display name error code
				final public static int INVALID_NEW_DISPLAY_NAME_ERROR_CODE = 2;
		
		final public static String GET_MAIN_SERVER_INFO = "<GET_MAIN_SERVER_INFO>";
			final public static String GET_MAIN_SERVER_INFO_SUCCESS = "<RETURN_MAIN_SERVER_INFO>";
				
		final public static String GET_MESSAGING_SERVER_INFO = "<GET_MESSAGING_SERVER_INFO>";
			final public static String GET_MESSAGING_SERVER_INFO_SUCCESS = "<RETURN_MESSAGING_SERVER_INFO>";
				//this message replies with an integer, the port on which the messaging server is running
			
		final public static String GET_ROOM_DATA_SERVER_INFO = "<GET_ROOM_DATA_SERVER_INFO>";
			final public static String GET_ROOM_DATA_SERVER_INFO_SUCCESS = "<RETURN_ROOM_DATA_SERVER_INFO>";
				//this message replies with an integer, the port on which the room data server is running
			
		final public static String GET_ROOM_SERVER_INFO = "<GET_ROOM_SERVER_INFO>";
			final public static String GET_ROOM_SERVER_INFO_SUCCESS = "<RETURN_ROOM_SERVER_INFO>";	
				//this message replies with an integer, the port on which the room data server is running
			
		//NOTE: this message is sent by the server. this message is sent after a successful display name change. 
		final public static String UPDATE_DISPLAY_NAME = "<UPDATE_DISPLAY_NAME>";
			//this message is not sent with anything and no replies for this message
	}
	
	final public static class MESSAGINGSERVER
	{	
		//this error code means the target user does not exist. for example, if someone tries
		//to add a non-existing user to their friends list, they will get this error
		final public static int NONEXISTING_TARGET_ERROR_CODE = -3;
		
		//this message requires a username and a password
		final public static String GET_FRIENDS_LIST = "<GET_FRIENDS_LIST>";
			final public static String GET_FRIENDS_LIST_SUCCESS = "<RETURN_FRIENDS_LIST>";
				//this message replies with a String, a list of all friends
			final public static String GET_FRIENDS_LIST_FAILED = "<GET_FRIENDS_LIST_FAILED>";
				//this message may reply with a non-existing user error code
				//this message may reply with a bad user password error code
			
		//this message requires a username, a password and the username of a friend
		final public static String ADD_FRIEND = "<ADD_FRIEND>";
			final public static String ADD_FRIEND_SUCCESS = "<ADD_FRIEND_SUCCESS>";
				//this message does not reply with anything
			final public static String ADD_FRIEND_FAILED = "<ADD_FRIEND_FAILED>";
				//this message may reply with a non-existing user error code
				//this message may reply with a bad user password error code
				//this message may reply with a non-existing target error code
			
		//this message requires a username, a password and the username of a friend
		final public static String REMOVE_FRIEND = "<REMOVE_FRIEND>";
			final public static String REMOVE_FRIEND_SUCCESS = "<REMOVE_FRIEND_SUCCESS>";
				//this message does not reply with anything
			final public static String REMOVE_FRIEND_FAILED = "<REMOVE_FRIEND_FAILED>";
				//this message may reply with a non-existing user error code
				//this message may reply with a bad user password error code
				//this message may reply with a non-existing target error code
			
		//this message requires a username, a password, and the username of a friend
		final public static String IS_FRIEND_ONLINE = "<IS_FRIEND_ONLINE>";
			final public static String RETURN_IS_FRIEND_ONLINE = "<RETURN_IS_FRIEND_ONLINE>";
				//this message does not reply with anything
		
		//this message requires a username and a password
		final public static String GET_PESTS_LIST = "<GET_PESTS_LIST>";
			final public static String GET_PESTS_LIST_SUCCESS = "<RETURN_PESTS_LIST>";
				//this message replies with a String, a list of all pests
			final public static String GET_PESTS_LIST_FAILED = "<GET_PESTS_LIST_FAILED>";
				//this message may reply with a non-existing user error code
				//this message may reply with a bad user password error code
			
		//this message requires a username, a password and the username of a pest
		final public static String ADD_PEST = "<ADD_PEST>";
			final public static String ADD_PEST_SUCCESS = "<ADD_PEST_SUCCESS>";
				//this message does not reply with anything
			final public static String ADD_PEST_FAILED = "<ADD_PEST_FAILED>";
				//this message may reply with a non-existing user error code
				//this message may reply with a bad user password error code
				//this message may reply with a non-existing target error code
		
		//this message requires a username, a password and the username of a pest
		final public static String REMOVE_PEST = "<REMOVE_PEST>";
			final public static String REMOVE_PEST_SUCCESS = "<REMOVE_PEST_SUCCESS>";
				//this message does not reply with anything
			final public static String REMOVE_PEST_FAILED = "<REMOVE_PEST_FAILED>";
				//this message may reply with a non-existing user error code
				//this message may reply with a bad user password error code
				//this message may reply with a non-existing target error code
			
		//this message requires a username and a password
		final public static String GET_PM_HISTORY = "<GET_PM_HISTORY>";
			final public static String GET_PM_HISTORY_SUCCESS = "<RETURN_PM_HISTORY>";
				//this message replies with a String, a list that describes the user's PM history
			final public static String GET_PM_HISTORY_FAILED = "<GET_PM_HISTORY_FAILED>";
				//this message may reply with a non-existing user error code
				//this message may reply with a bad user password error code
		
		//this message requires a username, a password, a target username and a message
		final public static String SEND_PM = "<SEND_PM>";
			final public static String SEND_PM_SUCCESS = "<SEND_PM_SUCCESS>";
				//this message does not reply with anything
			final public static String SEND_PM_FAILED = "<SEND_PM_FAILED>";
				//this message may reply with a non-existing user error code
				//this message may reply with a bad user password error code
				//this message may reply with a non-existing target error code
				
		//this message requires a username, a password and a username of a friend
		final public static String SET_PMS_WITH_FRIEND_AS_READ = "<SET_PMS_WITH_FRIEND_AS_READ>";
			final public static String SET_PMS_WITH_FRIEND_AS_READ_SUCCESS = "<SET_PMS_WITH_FRIEND_AS_READ_SUCCESS>";
				//this message does not reply with anything
			final public static String SET_PMS_WITH_FRIEND_AS_READ_FAILED = "<SET_PMS_WITH_FRIEND_AS_READ_FAILED>";
				//this message may reply with a non-existing user error code
				//this message may reply with a bad user password error code
				//this message may reply with a non-existing target error code
			
		//this message requires a username, a password and a username of another person
		final public static String GET_USER_DISPLAY_NAME = "<GET_USER_DISPLAY_NAME>";
			final public static String GET_USER_DISPLAY_NAME_SUCCESS = "<RETURN_USER_DISPLAY_NAME>";
				//this message replies with a String, the display name
			final public static String GET_USER_DISPLAY_NAME_FAILED = "<GET_USER_DISPLAY_NAME_FAILED>";
				//this message may reply with a non-existing user error code
				//this message may reply with a bad user password error code
				//this message may reply with a non-existing target error code
			
			
		/**
		 * Triggers a refresh of the friends list on the client side. ONLY SENT BY SERVER TO CLIENT. SENT
		 * WHEN A CLIENT'S FRIENDS LIST MAY BE OUT OF DATE - I.E. A FRIEND HAS LOGGED ON.<br>
		 * Format:<br>
		 * 1) username of someone whose online status changed
		 * 2) boolean: true if the person went online, false if the person went offline
		 */
		final public static String CHANGE_FRIEND_STATUS = "<CHANGE_FRIEND_STATUS>";
		/**
		 * Triggers the client to add a private message from another user. ONLY SENT BY SERVER TO CLIENT.
		 * SENT WHEN ONE USER MESSAGES ANOTHER.<br>
		 * Format:<br>
		 * 1) username of sender<br>
		 * 2) username of recipient<br>
		 * 3) message to be sent<br>
		 */
		final public static String ADD_PRIVATE_MESSAGE = "<ADD_PRIVATE_MESSAGE>";
	}
	
	final public static class ROOMDATASERVER
	{
		final public static int NONEXISTING_ROOM_ERROR_CODE = -1;
		final public static int BAD_MODIFICATION_PASSWORD_ERROR_CODE = -2;
		final public static int BAD_JOIN_PASSWORD_ERROR_CODE = -3;
		
		
		final public static String GET_MAXIMUM_ROOMS_STORED = "<GET_MAXIMUM_ROOMS_STORED>";
			final public static String GET_MAXIMUM_ROOMS_STORED_SUCCESS = "<RETURN_MAXIMUM_ROOMS_STORED>";
				//this message replies with an integer, the maximum number of rooms stored
			
		final public static String GET_EXISTING_ROOM_LIST = "<GET_EXISTING_ROOM_LIST>";
			final public static String GET_EXISTING_ROOM_LIST_SUCCESS = "<RETURN_EXISTING_ROOM_LIST>";
				//this message replies with a list of integers, the list of all the room IDs of existing rooms
			
		//this message requires a room ID
		final public static String GET_ROOM_INFORMATION = "<GET_ROOM_INFORMATION>";
			final public static String GET_ROOM_INFORMATION_SUCCESS = "<GET_ROOM_INFORMATION_SUCCESS>";
				//this message replies with a String, that is all the room information:
					//Room Name
					//Creator Name
					//Creation Date
					//Password Protection
					//Whiteboard Length
					//Whiteboard Width
			final public static String GET_ROOM_INFORMATION_FAILED = "<GET_ROOM_INFORMATION_FAILED>";
				//this message does not reply with anything. it is implied that the room does not exist
			
		//this message requires a room ID, a modification password and a new room name
		final public static String SET_ROOM_NAME = "<SET_ROOM_NAME>";
			final public static String SET_ROOM_NAME_SUCCESS = "<SET_ROOM_NAME_SUCCESS>";
				//this message does not reply with anything
			final public static String SET_ROOM_NAME_FAILED = "<SET_ROOM_NAME_FAILED>";
				//this message may reply with a non-existing room error
				//this message may reply with an invalid modification password error
				//this message may reply with an invalid new room name error
				final public static int INVALID_NEW_ROOM_NAME_ERROR_CODE = 1;
		
		//this message requires a room ID, a modification password and a new password protection boolean
		final public static String SET_ROOM_PASSWORD_PROTECTION = "<SET_ROOM_PASSWORD_PROTECTION>";
			final public static String SET_ROOM_PASSWORD_PROTECTION_SUCCESS = "<SET_ROOM_PASSWORD_PROTECTION_SUCCESS>";
				//this message does not reply with anything
			final public static String SET_ROOM_PASSWORD_PROTECTION_FAILED = "<SET_ROOM_PASSWORD_PROTECTION_FAILED>";
				//this message may reply with a non-existing room error
				//this message may reply with an invalid modification password error
				//this message may reply with an invalid new room name error
			
		//this message requires a room ID, a modification password the old join password and the new join password
		final public static String SET_ROOM_JOIN_PASSWORD = "<SET_ROOM_JOIN_PASSWORD>";
			final public static String SET_ROOM_JOIN_PASSWORD_SUCCESS = "<SET_ROOM_JOIN_PASSWORD_SUCCESS>";
				//this message does not reply with anything
			final public static String SET_ROOM_JOIN_PASSWORD_FAILED = "<SET_ROOM_JOIN_PASSWORD_FAILED>";
				//this message may reply with a non-existing room error
				//this message may reply with an invalid modification password error
				//this message may reply with an invalid join password error
				//this message may reply with an invalid new join password error
				final public static int INVALID_NEW_JOIN_PASSWORD_ERROR_CODE = 1;
				
		//this message requires a room ID, the old modification password and the new modification password
		final public static String SET_ROOM_MODIFICATION_PASSWORD = "<SET_ROOM_MODIFICATION_PASSWORD>";
			final public static String SET_ROOM_MODIFICATION_PASSWORD_SUCCESS = "<SET_ROOM_MODIFICATION_PASSWORD_SUCCESS>";
				//this message does not reply with anything
			final public static String SET_ROOM_MODIFICATION_PASSWORD_FAILED = "<SET_ROOM_MODIFICATION_PASSWORD_FAILED>";
				//this message may reply with a non-existing room error
				//this message may reply with an invalid modification password error
				//this message may reply with an invalid new modification password error
				final public static int INVALID_NEW_MODIFICATION_PASSWORD_ERROR_CODE = 1;
				
		//this message requires a room ID, a room name, a creator username, a modification password
		//a password protected boolean, a join password, a whiteboard length and a whiteboard width
		final public static String CREATE_ROOM = "<CREATE_ROOM>";
			final public static String CREATE_ROOM_SUCCESS = "<CREATE_ROOM_SUCCESS>";
				//this message does not reply with anything
			final public static String CREATE_ROOM_FAILED = "<CREATE_ROOM_FAILED>";
				//this message may reply with a room ID already taken error
				final public static int ROOM_ID_TAKEN_ERROR_CODE = 1;
				//this message may reply with an invalid room name error code
				final public static int INVALID_ROOM_NAME_ERROR_CODE = 2;
				//this message may reply with an invalid modification password error code
				final public static int INVALID_MODIFICATION_PASSWORD_ERROR_CODE = 3;
				//this message may reply with an invalid join password error code
				final public static int INVALID_JOIN_PASSWORD_ERROR_CODE = 4;
				//this message may reply with an invalid whiteboard dimensions error code
				final public static int INVALID_DIMENSIONS_ERROR_CODE = 5;
				//this may reply with an IO Exception error code
				final public static int IO_EXCEPTION_ERROR_CODE = 6;
				
		//this message requires a username, a password, a room ID, a creator username and a modification password
		final public static String DELETE_ROOM = "<DELETE_ROOM>";
			final public static String DELETE_ROOM_SUCCESS = "<DELETE_ROOM_SUCCESS>";
				//this message does not reply with anything
			final public static String DELETE_ROOM_FAILED = "<DELETE_ROOM_FAILED>";
				//this message may reply with a non existing room error
				//this message may reply with an incorrect modification password
	}
	
	final public static class ROOMSERVER
	{
		final public static int ROOM_DOES_NOT_EXIST_ERROR_CODE = -1;
		final public static int INVALID_MODIFICATION_PASSWORD_ERROR_CODE = -2;
		final public static int INVALID_JOIN_PASSWORD_ERROR_CODE = -3;
		final public static int NON_EXISTING_TARGET_ERROR_CODE = -4;
		final public static int AUTHORITY_TOO_LOW_ERROR_CODE = -5;
		final public static int YOU_LACK_WHITEBOARD_ERROR_CODE = -6; //this error means you don't have the whiteboard
		
		
		final public static String GET_AUDIO_PORT_DATA = "<GET_AUDIO_PORT_DATA>";
			/**
			 * this message replies with two integers, the step and concurrent connection ports for
			 * the audio server of a room.
			 */
			final public static String GET_AUDIO_PORT_DATA_SUCCESS = "<RETURN_AUDIO_PORT_DATA>";
			
		final public static String GET_WHITEBOARD_PORT_DATA = "<GET_WHITEBOARD_PORT_DATA>";
			/**
			 * this message replies with two integers, the step and concurrent connection ports for
			 * the whiteboard server of a room.
			 */
			final public static String GET_WHITEBOARD_PORT_DATA_SUCCESS = "<RETURN_WHITEBOARD_PORT_DATA>";
			
		final public static String GET_TEXT_PORT_DATA = "<GET_TEXT_PORT_DATA>";
			/**
			 * this message replies with two integers, the step and concurrent connection ports for
			 * the text chat server of a room.
			 */
			final public static String GET_TEXT_PORT_DATA_SUCCESS = "<RETURN_TEXT_PORT_DATA>";
			
		final public static String GET_USER_LIST_PORT_DATA = "<GET_USER_LIST_PORT_DATA>";
			/**
			 * this message replies with two integers, the step and concurrent connection ports for
			 * the user list server of a room.
			 */
			final public static String GET_USER_LIST_PORT_DATA_SUCCESS = "<RETURN_USER_LIST_PORT_DATA>";
		
		final public static class WHITEBOARD_SERVER
		{
			/**
			 * this message requires a username, a join password, an x-coordinate, y-coordinate, red, green and blue
			 */
			final public static String DRAW_PIXEL = "<DRAW_PIXEL>";
				final public static String DRAW_PIXEL_SUCCESS = "<DRAW_PIXEL_SUCCESS>";
					//this message does not reply with anything
				final public static String DRAW_PIXEL_FAILED = "<DRAW_PIXEL_FAILED>";
					//this message may reply with an invalid join password error code
					//this message may reply with a no whiteboard error
					//this message may reply with an out of bounds error (user tried to draw off the whiteboard)
					final public static int OUT_OF_BOUNDS_ERROR_CODE = 1;
					//this message may reply with an invalid color error 
					final public static int INVALID_COLOR_ERROR_CODE = 2;
					
			final public static String START_UPDATING = "<START_UPDATING>";
					
			/**
			 *	this message requires 6 integers: x-coordinate, a y-coordinate, red, green, blue and priority.
			 *  sent using concurrent connection by room whiteboard server to client only. 
			 */
			final public static String UPDATE_PIXEL = "<UPDATE_PIXEL>";
			
			/**
			 * tells the client to redraw the whiteboard
			 */
			final public static String DONE_UPDATING = "<DONE_UPDATING>";
			
		}

			
		final public static class TEXT_SERVER
		{
			/**
			 * this message is sent through the concurrent connection by the server to the client only.
			 * this message is sent to tell the user to add some line of chat to his/her chat history.  
			 * this message is sent with a username (sender of a message) and a message.
			 */
			final public static String ADD_CHAT = "<ADD_CHAT>";
			
			/**
			 * this message is sent through the concurrent connection by the server to the client only.
			 * this system message is seen by all, regardless of user permissions
			 * this message is sent with a String, the system message
			 */
			final public static String ADD_SYSTEM_MESSAGE = "<ADD_SYSTEM_MESSAGE>";
				
			//this message requires a username, a join password and a message
			final public static String SEND_CHAT = "<SEND_CHAT>";
				final public static String SEND_CHAT_SUCCESS = "<SEND_CHAT_SUCCESS>";
					//this message does not reply with anything
				final public static String SEND_CHAT_FAILED = "<SEND_CHAT_FAILED>";
					//this message may reply with an invalid join password error code
					//this message may reply with an authority too low error (if someone disabled your text chat)
		}
			
		final public static class USER_LIST_SERVER
		{
			
			/**
			 * this message is sent by the user list server to the client concurrently only.
			 * this message requires a username, a display name, an authority, four booleans (user permissions) and 
			 * if the user has the whiteboard or not
			 */
			final public static String SHOW_USER_PERMISSIONS_FOR_USER = "<SHOW_USER_PERMISSIONS_FOR_USER>";
			
			final public static String getJoinSystemMessage(String userThatJustJoined)
			{
				return userThatJustJoined + " joined the room.";
			}
			
			/**
			 * this message requires a username, a join password, a target username, and four booleans (user permissions)
			 */
			final public static String SET_USER_PERMISSIONS = "<SET_USER_PERMISSIONS>";
				final public static String SET_USER_PERMISSIONS_SUCCESS = "<SET_USER_PERMISSIONS_SUCCESS>";
					//this message does not reply with anything
				final public static String SET_USER_PERMISSIONS_FAILED = "<SET_USER_PERMISSIONS_FAILED>";
					//this message may reply with an invalid join password error code
					//this message may reply with an authority too low error
				
				final public static String getSetPermissionsSystemMessage(String permissionsSetter, String target, boolean hasAudioParticipation, boolean hasAudioListening, boolean hasTextParticipation, boolean hasTextHistory)
				{
					String rtn = permissionsSetter + " modified the permissions of " + target + ". ";
					if (hasAudioParticipation)
					{
						rtn += "Audio Participation: ON, ";
					} else
					{
						rtn += "Audio Participation: OFF, ";
					}
					if (hasAudioListening)
					{
						rtn += "Audio Listening: ON, ";
					} else
					{
						rtn += "Audio Listening: OFF, ";
					}
					if (hasTextParticipation)
					{
						rtn += "Text Chat Participation: ON, ";
					} else
					{
						rtn += "Text Chat Participation: OFF, ";
					}
					if (hasTextHistory)
					{
						rtn += "Text Chat Updating: ON.";
					} else
					{
						rtn += "Text Chat Updating: OFF.";
					}
					return rtn;
				}
					
			/**
			 * this message requires a username, a join password and a target username
			 */
			final public static String KICK_USER = "<KICK_USER>";
				final public static String KICK_USER_SUCCESS = "<KICK_USER_SUCCESS>";
					//this message does not reply with anything
				final public static String KICK_USER_FAILED = "<KICK_USER_FAILED>";
					//this message may reply with an invalid join password error
					//this message may reply with an authority too low error
				
				final public static String getKickedSystemMessage(String kickerUsername, String userThatWasKicked)
				{
					return kickerUsername + " kicked " + userThatWasKicked + " from the room.";
				}
				
			/**
			 * this message requires a username, a modification password, a join password and a target username
			 */
			final public static String PROMOTE_USER = "<PROMOTE_USER>";
				final public static String PROMOTE_USER_SUCCESS = "<PROMOTE_USER_SUCCESS>";
					//this message does not reply with anything
				final public static String PROMOTE_USER_FAILED = "<PROMOTE_USER_FAILED>";
					//this message may reply with an invalid modification password error
					//this message may reply with an invalid join password error
					//this message may reply with an authority too low error
				/**
				 * gets the promotion message detailing who was promoted and by whom
				 * 
				 * @param promoterUsername			a String, username of person who promoted someone else
				 * @param promoteeUsername			a String, username of person who was promoted
				 * @return							a String, the promotion message
				 */
				final public static String getPromotionSystemMessage(String promoterUsername, String promoteeUsername)
				{
					return promoteeUsername + " was promoted by " + promoterUsername + ".";
				}
				
			/**
			 * this message requires a username, a modification password, a join password and a target username
			 */
			final public static String DEMOTE_USER = "<DEMOTE_USER>";
				final public static String DEMOTE_USER_SUCCESS = "<DEMOTE_USER_SUCCESS>";
					//this message does not reply with anything
				final public static String DEMOTE_USER_FAILED = "<DEMOTE_USER_FAILED>";
					//this message may reply with an invalid modification password error
					//this message may reply with an invalid join password error
					//this message may reply with an authority too low error
				
				final public static String getDemotionSystemMessage(String demoterUsername, String demoteeUsername)
				{
					return demoteeUsername + " was demoted by " + demoterUsername + ".";
				}
				
			/**
			 * this message requires a username, a join password and a target username
			 */
			final public static String GIVE_WHITEBOARD = "<GIVE_WHITEBOARD>";
				final public static String GIVE_WHITEBOARD_SUCCESS = "<GIVE_WHITEBOARD_SUCCESS>";
					//this message does not reply with anything
				final public static String GIVE_WHITEBOARD_FAILED = "<GIVE_WHITEBOARD_FAILED>";
					//this message may reply with an invalid join password error
					//this message may reply with a non-existing target error
					//this message may reply with a no whiteboard error
				
				final public static String getGiveWhiteboardSystemMessage(String giverUsername, String recipientUsername)
				{
					return giverUsername + " gave the whiteboard to " + recipientUsername + ".";
				}
				
			/**
			 * this message requires a username, a join password and a target username
			 */
			final public static String TAKE_WHITEBOARD = "<TAKE_WHITEBOARD>";
				final public static String TAKE_WHITEBOARD_SUCCESS = "<TAKE_WHITEBOARD_SUCCESS>";
					//this message does not reply with anything
				final public static String TAKE_WHITEBOARD_FAILED = "<TAKE_WHITEBOARD_FAILED>";
					//this message may reply with an invalid join password error
					//this message may reply with a non-existing target error
					//this message may reply with an authority too low error
					//this message may reply with a target lacks whiteboard error
					final public static int TARGET_LACKS_WHITEBOARD_ERROR = 1;
					
				final public static String getTakeWhiteboardSystemMessage(String takerUsername, String targetUsername)
				{
					return takerUsername + " took the whiteboard from " + targetUsername + ".";
				}
				
			/**
			 * this message is sent to notify a client s/he received the whiteboard
			 * this message requires the display name of the person who gave the whiteboard
			 */
			final public static String RECEIVED_WHITEBOARD = "<RECEIVED_WHITEBOARD>";
				
			/**
			 * this message does not require anything
			 */
			final public static String LEAVE = "<LEAVE>";
				final public static String LEAVE_SUCCESS = "<LEAVE_SUCCESS>";
					//this message does not reply with anything
				
				final public static String getLeaveSystemMessage(String userThatLeft)
				{
					return userThatLeft + " left the room.";
				}
				
			/**
			 * this message is sent concurrently by the user list server to the client.
			 * it tells a client to remove a user from the user list because the user left.
			 * this message requires a username
			 */
			final public static String USER_LEFT = "<USER_LEFT>";
				
			/**
			 * sent when someone is kicked out of the room. this message requires a username,
			 * the person who did the kicking.
			 */
			final public static String KICKED = "<KICKED>";
		}
	}
}
