package net;

/**
 * Documentation Format:
 * 
 * MESSAGE FROM THE SERVER
 * 		A DATABASE RESPONSE
 * 			DESCRIPTION OF A RESPONSE CODE THAT MAY ACCOMPANY THE RESPONSE/VALUE THAT ACCOMPANIES THE RESPONSE
 * 				A RESPONSE CODE (NONE IF IT IS A VALUE)
 *			DESCRIPTION OF A RESPONSE CODE THAT MAY ACCOMPANY THE RESPONSE/VALUE THAT ACCOMPANIES THE RESPONSE
 *				A RESPONSE CODE (NONE IF IT IS A VALUE)
 *			...
 *		...
 *
 * MESSAGE FROM THE SERVER
 * 		A DATABASE RESPONSE
 * 			DESCRIPTION OF A RESPONE CODE THAT MAY ACCOMPANY THE RESPONSE/VALUE THAT ACCOMPANIES THE RESPONSE
 * 				A RESPONSE CODE (NONE IF IT IS A VALUE)
 * 			DESCRIPTION OF A RESPONSE CODE THAT MAY ACCOMPANY THE RESPONSE/VALUE THAT ACCOMPANIES THE RESPONSE
 * 				A RESPONSE CODE (NONE IF IT IS A VALUE)
 * 			...
 * 		...
 * ...
 * 		
 * NOTE THAT IF THE RESPONSE CODE HAS BEEN PUT IN GENERAL (THE VALUE IS NEGATIVE), THE RESPONSE CODE
 * WILL NOT FOLLOW THE DESCRIPTION.
 * 
 *  MESSAGE FROM THE SERVER
 *  	A DATABASE RESPONSE
 *  		DESCRIPTION OF A GENERALIZED RESPONSE CODE THAT MAY ACCOMPANY THE RESPONSE
 *  		DESCRIPTION OF A GENERALIZED RESPONSE CODE THAT MAY ACCOMPANY THE RESPONSE
 *  		DESCRIPTION OF A RESPONSE CODE THAT MAY ACCOMPANY THE RESPONSE/VALUE THAT ACCOMPANIES THE RESPONSE
 *  			A RESPONSE CODE (NONE IF IT IS A VALUE)
 *  		...
 *  	...
 * 	...
 */

//this class contains all messages the networking part of this database may send to the server
//error messages (anything with the word ERROR in it's name declaration) will also send a code to identify the error
//the messages are grouped by function.
final public class MESSAGES 
{
	final public static String DELIMITER = " ";
	//bad connection password error code to be used for every error reporting with code
	//since everything sent to the server must contain the connection password, 
	//we will just put this message here, where it is accessible by all subclasses.
	//anything may reply with this error, so this error is not documented below
	final public static String BAD_CONNECTION_PASSWORD = "<BAD_CONNECTION_PASSWORD>";
	final public static int BAD_CONNECTION_PASSWORD_ERROR = 0;
	
	final public static class CONNECTION
	{
		//this heading categorizes the message as a connection message
		final public static String HEADING = "<CONNECTION>";
		//database will first request the IP of the machine wishing to connect 
		final public static String REQUEST_IP = "<REQUEST_IP>";
		//if the IP is correct, database will continue. if it is wrong, database will terminate abruptly
			//the server will then respond with this message, followed by it's IP Address
			final public static String RETURN_IP = "<RETURN_IP>";
				//this means the IP is valid and the database accepts is
				final public static String GOOD_IP = "<GOOD_IP>";
				//this means the IP has been blocked, or does not exist and the machine is lying. the connection is terminated
				final public static String BAD_IP = "<BAD_IP>";
		//Database will then send this message to server when the server makes a connection request
		final public static String REQUEST_CONNECTION_PASSWORD = "<REQUEST_CONNECTION_PASSWORD>";
			//The server will then respond with the connection password
			final public static String RETURN_CONNECTION_PASSWORD = "<RETURN_CONNECTION_PASSWORD>";
				//if the connection password is right, the connection is accepted
				final public static String CONNECTION_ACCEPTED = "<CONNECTION_ACCEPTED>";
				//this may also reply with a bad connection password error
	}
	
	final public static class USERDATA
	{
		//this heading identifies the message as a message relating to obtaining/modifying user data
		final public static String HEADING = "<USERDATA>";
		
		//general error codes that most error messages will contain are negative.
		//error codes specific to just one message will be positive.
		//a non-existing user error is sent if the data for that user has not been loaded or 
		//if the username just hasn't been registered yet
		final public static int NONEXISTING_USER_ERROR_CODE = -1;
		//a bad user password error code is sent if the password of the given user is not correct
		//to access user data, the server should also know the user's password - we check this just
		//to be safe
		final public static int BAD_USER_PASSWORD_ERROR_CODE = -2;
		
		//this message requires a username
		final public static String LOAD_USER_INFORMATION = "<LOAD_USER_INFORMATION>";
			final public static String LOAD_USER_INFORMATION_SUCCESS = "<LOAD_USER_INFORMATION_SUCCESS>";
				//this message does not reply with anything else
			final public static String LOAD_USER_INFORMATION_FAILED = "<LOAD_USER_INFORMATION_FAILED>";
				final public static int FILE_NOT_FOUND_ERROR_CODE = 1;
				//this is sent if the file for some reason has bad formatting and the database can
				//no longer read that format
				final public static int FILE_CORRUPTED_ERROR_CODE = 2;
			
		final public static String UNLOAD_USER_INFORMATION = "<UNLOAD_USER_INFORMATION>";
			final public static String UNLOAD_USER_INFORMATION_SUCCESS = "<UNLOAD_USER_INFORMATION_SUCCESS>";
				//this message does not reply with anything else
			final public static String UNLOAD_USER_INFORMATION_FAILED = "<UNLOAD_USER_INFORMATION_FAILED>";
				//this message may reply with a non-existing user error
		
		//this message requires a username and a password
		final public static String IS_LOGIN_OKAY = "<IS_LOGIN_OKAY>";
			final public static String LOGIN_IS_OKAY = "<LOGIN_OKAY>";
			final public static String LOGIN_NOT_OKAY = "<LOGIN_FAILED>";
				//this may reply with a non-existing user error
				//this may reply with a bad user password error
				//this may reply with an already logged in error
				final public static int ALREADY_LOGGED_IN_ERROR_CODE = 1;
			
		//this message requires a username and the password of that user
		final public static String GET_USER_DISPLAY_NAME = "<GET_USER_DISPLAY_NAME>";
			final public static String GET_USER_DISPLAY_NAME_SUCCESS = "<RETURN_USER_DISPLAY_NAME>";
				//this message will reply with a String, the display name of the user
			final public static String GET_USER_DISPLAY_NAME_FAILED = "<GET_USER_DISPLAY_NAME_FAILED>";
				//this may reply with a non-existing user error
				//this may reply with a bad user password error
		
		//this message requires a username and the password of that user
		final public static String GET_USER_NAME_CHANGE_CODE = "<GET_USER_NAME_CHANGE_CODE>";
			final public static String GET_USER_NAME_CHANGE_CODE_SUCCESS = "<RETURN_USER_NAME_CHANGE_CODE>";
				//this message will reply with a String, the name change code of the user
			final public static String GET_USER_NAME_CHANGE_CODE_FAILED = "<GET_USER_NAME_CHANGE_CODE_FAILED>";
				//this may reply with a non-existing user error
				//this may reply with a bad user password error
			
		//this message requires a username, the password of the user and the username of another user
		final public static String GET_DISPLAY_NAME_GIVEN_USERNAME = "<GET_DISPLAY_NAME_GIVEN_USERNAME>";
			final public static String GET_DISPLAY_NAME_OF_OTHER_USER_SUCCESS = "<RETURN_USER_DISPLAY_NAME>";
				//this message replies with a String, the display name of the second username
			final public static String GET_DISPLAY_NAME_OF_OTHER_USER_FAILED = "<GET_USER_DISPLAY_NAME_FAILED>";
				//this message may reply with a non-existing user error
				//this message may reply with a bad user password error
				//this message may reply with a non-existing target error
				final public static int NONEXISTING_TARGET_ERROR_CODE = 1;
				
		//this message requires a username and the password of that user
		final public static String GET_NEW_NAME_CHANGE_CODE = "<GET_NEW_NAME_CHANGE_CODE>";
			final public static String GET_NEW_NAME_CHANGE_CODE_SUCCESS = "<RETURN_NEW_NAME_CHANGE_CODE>";
				//this message should reply with a new name change code for the given user
			final public static String GET_NEW_NAME_CHANGE_CODE_FAILED = "<GET_NEW_NAME_CHANGE_CODE_FAILED>";
				//this message may reply with a non-existing user error
				//this message may reply with a bad user password error
			
		//this class contains all user data modifications the main server may send
		final public static class MAINSERVER
		{
			//this identifies the message as from the main server (it deals with modification of single
			//pieces of user data such as password, display names)
			final public static String MAIN_SERVER_HEADING = "<MAIN_SERVER>";
			
			//this message requires a username and a password
			final public static String REGISTER = "<REGISTER>";
				final public static String REGISTER_SUCCESS = "<REGISTER_SUCCESS>";
					//this message does not reply with anything else
				final public static String REGISTER_FAILED = "<REGISTER_FAILED>";
					//this message may reply with a username in use error
					final public static int USERNAME_IN_USER_ERROR = 1;
					final public static int IO_EXCEPTION = 2;
			
			//this message requires a username, the password of that user and the new password for the user
			final public static String SET_USER_PASSWORD = "<SET_USER_PASSWORD>";
				final public static String SET_USER_PASSWORD_SUCCESS = "<SET_USER_PASSWORD_SUCCEEDS>";
					//this message does not reply with anything else
				final public static String SET_USER_PASSWORD_FAILED = "<SET_USER_PASSWORD_FAILED>";
					//this message may reply with a non-existing user error
					//this message may reply with a bad user password error
					
			//this message requires a username, the password of that user, the name change code of that user
			//and the new display name for the user
			final public static String SET_USER_DISPLAY_NAME = "<SET_USER_DISPLAY_NAME>";
				final public static String SET_USER_DISPLAY_NAME_SUCCESS = "<SET_USER_DISPLAY_NAME_SUCCEEDS>";
					//this message does not reply with anything else
				final public static String SET_USER_DISPLAY_NAME_FAILED = "<SET_USER_DISPLAY_NAME_FAILED>";
					//this message may reply with a non-existing user error
					//this message may reply with a bad user password error
					//this message may reply with a bad name change code error (the name change code is incorrect)
					final public static int INVALID_NAME_CHANGE_CODE_ERROR_CODE = 1;
		}
		
		//this class contains all user data modifications the messaging server may send
		final public static class MESSAGINGSERVER
		{
			//this identifies the message as from the messaging server
			final public static String MESSAGING_SERVER_HEADING = "<MESSAGING_SERVER>";
			//a non-existing target error is sent if someone tries to friend, pest or message a non-existing user
			final public static int NONEXISTING_TARGET_ERROR = -3;
			
			//this message requires a username and the password of that user
			final public static String GET_USER_FRIENDS_LIST = "<GET_USER_FRIENDS_LIST>";
				final public static String GET_USER_FRIENDS_LIST_SUCCESS = "<RETURN_USER_FRIENDS_LIST>";
					//this message will reply with an integer, F, the number of friends on the friends list
					//and then N usernames of friends
				final public static String GET_USER_FRIENDS_LIST_FAILED = "<GET_USER_FRIENDS_LIST_FAILED>";
					//this message may reply with a non-existing user error
					//this message may reply with a bad user password error
			
			//this message requires a username, the password of that user and the username of a different user
			final public static String ADD_FRIEND = "<ADD_FRIEND>";
				final public static String ADD_FRIEND_SUCCESS = "<ADD_FRIEND_SUCCEEDS>";
					//this message does not reply with anything else
				final public static String ADD_FRIEND_FAILED = "<ADD_FRIEND_FAILED>";
					//this message may reply with a non-existing user error (the username of the person adding the friend)
					//this message may reply with a bad user password error
					//this message may reply with a non-existing target error (the username of the friend to add)
				
			//this message requires a username, the password of that user and the username of a different user
			final public static String REMOVE_FRIEND = "<REMOVE_FRIEND>";
				final public static String REMOVE_FRIEND_SUCCESS = "<REMOVE_FRIEND_SUCCESS>";
					//this message does not reply with anything else
				final public static String REMOVE_FRIEND_FAILED = "<REMOVE_FRIEND_FAILED>";
					//this message may reply with a non-existing user error (the username of the person removing the friend)
					//this message may reply with a bad user password error
					//this message may reply with a non-existing target error (the username of the friend to remove does not exist - includes not existing on the friends list)
				
			//this message requires a username and the password of that user
			final public static String 	GET_USER_PESTS_LIST = "<GET_USER_PESTS_LIST>";
				final public static String GET_USER_PESTS_LIST_SUCCESS = "<RETURN_USER_PESTS_LIST>";
					//this message will reply with an integer, P, the number of pests on the pests list
					//and then P usernames of pests
				final public static String GET_USER_PESTS_LIST_FAILED = "<GET_USER_PESTS_LIST_FAILED>";
					//this message may reply with a non-existing user error
					//this message may reply with a bad user password error	
				
			//this message requires a username, the password of the user and the username of a different user
			final public static String ADD_PEST = "<ADD_PEST>";
				final public static String ADD_PEST_SUCCESS = "<ADD_PEST_SUCCEEDS>";
					//this message does not reply with anything else
				final public static String ADD_PEST_FAILED = "<ADD_PEST_FAILED>";
					//this message may reply with a non-existing user error
					//this message may reply with a bad user password error
					//this message may reply with a non-existing target error
				
			//this message requires a username, the password of the user and the username of a different user
			final public static String REMOVE_PEST = "<REMOVE_PEST>";
				final public static String REMOVE_PEST_SUCCESS = "<REMOVE_PEST_SUCCEEDS>";
					//this message does not reply with anything else
				final public static String REMOVE_PEST_FAILED = "<REMOVE_PEST_FAILED>";
					//this message may reply with a non-existing user error
					//this message may reply with a bad user password error
					//this message may reply with a non-existing target error
				
			/**
			 * Determines if a given username is on another user's friends list.<br>
			 * <br>
			 * Format:<br>
			 * 1) username we are going to look for on someone else's friends list<br>
			 * 2) username of the owner of the friend's list through which we are going to look<br>
			 * <br>
			 * Results:<br>
			 * 1) Success: returns a boolean, true if the user is on the friends list, false otherwise<br>
			 * 2) non-existing user error<br>
			 * 3) non-existing target error
			 * 
			 * @see			#AM_I_ON_USER_FRIENDS_LIST_REQUEST_SUCCESS
			 * @see			#AM_I_ON_USER_FRIENDS_LIST_REQUEST_FAILED
			 */
			final public static String AM_I_ON_USER_FRIENDS_LIST = "<AM_I_ON_USER_FRIENDS_LIST>";
				final public static String AM_I_ON_USER_FRIENDS_LIST_REQUEST_SUCCESS = "<RETURN_AM_I_ON_USER_FRIENDS_LIST>";
					//this message replies with a boolean, true if the user is on the friends list, false otherwise
				final public static String AM_I_ON_USER_FRIENDS_LIST_REQUEST_FAILED = "<AM_I_ON_USER_PESTS_LIST_REQUEST_FAILED>";
					//this message may reply with a non-existing user error
					//this message may reply with a non-existing target error
				
				
			//this message requires a username and the username of a different user
			final public static String AM_I_ON_USER_PESTS_LIST = "<AM_I_ON_USER_PESTS_LIST>";
				final public static String AM_I_ON_USER_PESTS_LIST_REQUEST_SUCCESS = "<RETURN_AM_I_ON_USER_PESTS_LIST>";
					//this message replies with a boolean, true if the user is on the pests list, false otherwise
				final public static String AM_I_ON_USER_PESTS_LIST_REQUEST_FAILED = "<AM_I_ON_USER_PESTS_LIST_REQUEST_FAILED>";
					//this message may reply with a non-existing user error
					//this message may reply with a non-existing target error
					
			//this message requires a username and the password of that user
			final public static String GET_USER_PM_HISTORY = "<GET_USER_PM_HISTORY>";
				final public static String GET_USER_PM_HISTORY_SUCCESS = "<RETURN_USER_PM_HISTORY>";
					//this message will reply with an integer, M, the number of messages in the user's
					//private chat history, and then M bundles of private messaging history. Each private
					//messaging history bundle consists of a username (the other user involved in the message)
					//the sender of the message and the message
				final public static String GET_USER_PM_HISTORY_FAILED = "<GET_USER_PM_HISTORY_FAILED>";
					//this message may reply with a non-existing user error
					//this message may reply with a bad user password error
				
			//this message requires a username, the password of the user, the username of the other user involved in the conversation, 
			//the sender of the message and a message
			final public static String ADD_PM_HISTORY = "<ADD_PM_HISTORY>";
				final public static String ADD_PM_HISTORY_SUCCESS = "<ADD_PM_HISTORY_SUCCEEDS>";
					//this message does not reply with anything else
				final public static String ADD_PM_HISTORY_FAILED = "<ADD_PM_HISTORY_FAILED>";
					//this message may reply with a non-existing user error
					//this message may reply with a bad user password error
					//this message may reply with a non-existing target error
				
			//this message requires a username, the password of the user and the username of the other user involved in the conversation
			final public static String SET_CONVERSATION_READ = "<SET_CONVERSATION_READ>";
				final public static String SET_CONVERSATION_READ_SUCCESS = "<SET_CONVERSATION_READ_SUCCES>";
					//this message does not reply with anything else
				final public static String SET_CONVERSATION_READ_FAILED = "<SET_CONVERSATION_READ_FAILED>";
					//this message may reply with a non-existing user error
					//this message may reply with a bad user password error
					//this message may reply with a non-existing target error
		}
	}
	
	//this class contains messages for obtaining/modifying room data
	final public static class ROOMDATA
	{
		//this identifies the message as relating to room data
		final public static String HEADING = "<ROOM_DATA>";
		//a non-existing room error means the room data has not yet been loaded
		//or the room just hasn't been created yet
		final public static int NONEXISTING_ROOM_ERROR_CODE = -1;
		//an invalid join password code means the join password provided does not match the stored join password
		final public static int INVALID_JOIN_PASSWORD_ERROR_CODE = -2;
		//an invalid modification password error means the modification password is wrong
		final public static int INVALID_MODIFICATION_PASSWORD_ERROR_CODE = -3;
		
		final public static String GET_MAXIMUM_ROOMS_STORED = "<GET_MAXIMUM_ROOMS_STORED>";
			final public static String GET_MAXIMUM_ROOMS_STORED_SUCCESS = "<RETURN_MAXIMUM_ROOMS_STORED>";
		
		//this message requires a room ID
		final public static String LOAD_ROOM_DATA = "<LOAD_ROOM_DATA>";
			final public static String LOAD_ROOM_DATA_SUCCESS = "<LOAD_ROOM_DATA_SUCCEEDS>";
				//this message does not reply with anything else
			final public static String LOAD_ROOM_DATA_FAILED = "<LOAD_ROOM_DATA_FAILED>";
				//this message may reply with a file not found error, meaning this room does not have
				//saved data
				final public static int FILE_NOT_FOUND_ERROR_CODE = 1;
				//this message may reply with a file corrupted error, meaning the save file
				//has been saved improperly or someone has modified it improperly
				final public static int FILE_CORRUPTED_ERROR_CODE = 2;
				
		//this message does not require anything
		final public static String GET_EXISTING_ROOM_LIST = "<GET_EXISTING_ROOM_LIST>";
			final public static String GET_EXISTRING_ROOM_LIST_SUCCESS = "<RETURN_EXISTING_ROOM_LIST>";
				//this message replies with a list of integers, the IDs of all rooms that have been created
		
		//this message requires a room ID
		final public static String GET_ROOM_NAME = "<GET_ROOM_NAME>";
			final public static String GET_ROOM_NAME_SUCCESS = "<RETURN_ROOM_NAME>";
				//this message replies with a String, the name of the room
			final public static String GET_ROOM_NAME_FAILED = "<GET_ROOM_NAME_FAILED>";
				//this message may reply with a non-existing room error
				
		//this message requires a room ID
		final public static String GET_ROOM_CREATOR_USERNAME = "<GET_ROOM_CREATOR_USERNAME>";
			final public static String GET_ROOM_CREATOR_USERNAME_SUCCESS = "<RETURN_ROOM_CREATOR_USERNAME>";
				//this message replies with a String, the username of the creator
			final public static String GET_ROOM_CREATOR_USERNAME_FAILED = "<GET_ROOM_CREATOR_USERNAME_FAILED>";
				//this message may reply with a non-existing room error
			
		//this message requires a room ID
		final public static String GET_ROOM_CREATION_DATE = "<GET_ROOM_CREATION_DATE>";
			final public static String GET_ROOM_CREATION_DATE_SUCCESS = "<RETURN_ROOM_CREATION_DATE>";
				//this message replies with a String, the creation date of the room
			final public static String GET_ROOM_CREATION_DATE_FAILED = "<GET_ROOM_CREATION_DATE_FAILED>";
				//this message may reply with a non-existing room error
			
		//this message requires a room ID
		final public static String GET_ROOM_MODIFICATION_PASSWORD = "<GET_ROOM_MODIFICATION_PASSWORD>";
			final public static String GET_ROOM_MODIFICATION_PASSWORD_SUCCESS = "<RETURN_ROOM_MODIFICATION_PASSWORD>";
				//this message replies with a String, the modification password of the room
			final public static String GET_ROOM_MODIFICATION_PASSWORD_FAILED = "<GET_ROOM_MODIFICATION_PASSWORD_FAILED>";
				//this message may reply with a non-existing room error
			
		//this message requires a room ID
		final public static String GET_ROOM_PASSWORD_PROTECTION = "<GET_ROOM_PASSWORD_PROTECTION>";
			final public static String GET_ROOM_PASSWORD_PROTECTION_SUCCESS = "<RETURN_ROOM_PASSWORD_PROTECTION>";
				//this message replies with a boolean, true if the room is password protected, false otherwise
			final public static String GET_ROOM_PASSWORD_PROTECTION_FAILED = "<GET_ROOM_PASSWORD_PROTECTION_FAILED>";
				//this message may reply with a non-existing room error
			
		//this message requires a room ID
		final public static String GET_ROOM_JOIN_PASSWORD = "<GET_ROOM_JOIN_PASSWORD>";
			final public static String GET_ROOM_JOIN_PASSWORD_SUCCESS = "<RETURN_ROOM_JOIN_PASSWORD>";
				//this message replies with a String, the join password of the room
			final public static String GET_ROOM_JOIN_PASSWORD_FAILED = "<GET_ROOM_JOIN_PASSWORD_FAILED>";
				//this message may reply with a non-existing room error
			
		//this message requires a room ID
		final public static String GET_WHITEBOARD_LENGTH = "<GET_WHITEBOARD_LENGTH>";
			final public static String GET_WHITEBOARD_LENGTH_SUCCESS = "<RETURN_WHITEBOARD_LENGTH>";
				//this message replies with an integer, the length of the whiteboard
			final public static String GET_WHITEBOARD_LENGTH_FAILED = "<GET_WHITEBOARDLENGTH_FAILED>";
				//this message may reply with an non-existing room error
		
		//this message requires a room ID
		final public static String GET_WHITEBOARD_WIDTH = "<GET_WHITEBOARD_WIDTH>";
			final public static String GET_WHITEBOARD_WIDTH_SUCCESS = "<RETURN_WHITEBOARD_WIDTH>";
				//this message replies with an integer, the width of the whiteboard
			final public static String GET_WHITEBOARD_WIDTH_FAILED = "<GET_WHITEBOARDWIDTH_FAILED>";
				//this message may reply with a non-existing room error		
			
		//this class contains messages that the room data server may send
		//all these messages deal specifically with modifying room properties
		//(e.g. room name or join password)
		final public static class ROOMDATASERVER
		{
			//this identifies the message as from the room data server
			final public static String ROOM_DATA_SERVER_HEADING = "<ROOM_DATA_SERVER>";
			
			//this message requires a room ID, a room modification password and the new name of the room
			final public static String SET_ROOM_NAME = "<SET_ROOM_NAME>";
				final public static String SET_ROOM_NAME_SUCCESS = "<SET_ROOM_NAME_SUCCEEDS>";
					//this message does not reply with anything else
				final public static String SET_ROOM_NAME_FAILED = "<SET_ROOM_NAME_FAILED>";
					//this message may reply with a non-existing room error
					//this message may reply with an invalid room modification password error
			
			//this message requires a room ID, a room modification password, and the new room modification password
			//of the room
			final public static String SET_ROOM_MODIFICATION_PASSWORD = "<SET_ROOM_MODIFICATION_PASSWORD>";
				final public static String SET_ROOM_MODIFICATION_PASSWORD_SUCCESS = "<SET_ROOM_MODIFICATION_PASSWORD_SUCCEEDS>";
					//this message does not reply with anything else
				final public static String SET_ROOM_MODIFICATION_PASSWORD_FAILED = "<SET_ROOM_MODIFICATION_PASSWORD_FAILED>";
					//this message may reply with a non-existing room error
					//this message may reply with an invalid room modification password error
			
			//this message requires a room ID, a room modification password and a boolean
			final public static String SET_ROOM_PASSWORD_PROTECTION = "<SET_ROOM_PASSWORD_PROTECTION>";
				final public static String SET_ROOM_PASSWORD_PROTECTION_SUCCESS = "<SET_ROOM_PASSWORD_PROTECTION_SUCCEEDS>";
					//this message does not reply with anything else
				final public static String SET_ROOM_PASSWORD_PROTECTION_FAILED = "<SET_ROOM_PASSWORD_PROTECTION_FAILED>";
					//this message may reply with a non-existing room error
					//this message may reply with an invalid room modification password error
			
			//this message requires a room ID, a room modification password, the room join password and the new room join password
			final public static String SET_ROOM_JOIN_PASSWORD = "<SET_ROOM_JOIN_PASSWORD>";
				final public static String SET_ROOM_JOIN_PASSWORD_SUCCESS = "<SET_ROOM_JOIN_PASSWORD_SUCCEEDS>";
					//this message does not reply with anything else
				final public static String SET_ROOM_JOIN_PASSWORD_FAILED = "<SET_ROOM_JOIN_PASSWORD_FAILED>";
					//this message may reply with a non-existing room error
					//this message may reply with an invalid room modification password error
					//this message may reply with an invalid room join password error
				
			//this message requires a room ID, a room name, a creator username, a creation date, a modification password,
			//a password protection boolean, a join password, a whiteboard length and whiteboard width
			final public static String CREATE_ROOM = "<CREATE_ROOM>";
				final public static String CREATE_ROOM_SUCCESS = "<CREATE_ROOM_SUCCEEDS>";
					//this message does not reply with anything else
				final public static String CREATE_ROOM_FAILED = "<CREATE_ROOM_FAILED>";
					//this message may reply with a room ID taken error
					final public static int ROOM_ID_TAKEN_ERROR_CODE = 1;
					//this message may reply with an IO problem error code
					final public static int IO_EXCEPTION_ERROR_CODE = 2;
		}
			
		//this class contains messages that the room server may send
		//all these messages deal specifically with changing how the room looks 
		//(e.g. the chat history or whiteboard)
		final public static class ROOMSERVER
		{
			//this identifies the message as from a room server
			final public static String ROOM_SERVER_HEADING = "<ROOM_SERVER>";
			
			//a non-existing user error means the user does not exist, so it could not have performed an operation
			final public static int NONEXISTING_USER_ERROR_CODE = -3;
			
			//this message requires a username and a target username
			final public static String GET_DISPLAY_NAME_OF_USER_IN_ROOM = "<GET_USERNAME_OF_OTHER_USER_IN_ROOM>";
				final public static String GET_DISPLAY_NAME_OF_USER_IN_ROOM_SUCCESS = "<RETURN_USERNAME_OF_OTHER_USER_IN_ROOM>";
					//this message replies with the display name of the given user
				final public static String GET_DISPLAY_NAME_OF_USER_IN_ROOM_FAILED = "<GET_USERNAME_OF_OTHER_USER_IN_ROOM_FAILED>";
					//this message may reply with a non-existing user error
				
			//this message requires a room ID, the room modification and join passwords and the username of a moderator to be added
			final public static String ADD_ROOM_MODERATOR = "<ADD_WHITEBOARD_MODERATOR>";
				final public static String ADD_ROOM_MODERATOR_SUCCESS = "<ADD_WHITEBOARD_MODERATOR_SUCCEEDS>";
					//this message does not reply with anything else
				final public static String ADD_ROOM_MODERATOR_FAILED = "<ADD_WHITEBOARD_MODERATOR_FAILED>";
					//this message may reply with a non-existing room error
					//this message may reply with an invalid authorization password
					
			//this message requires a room ID, the room modification and join passwords and the username of a moderator to be removed
			final public static String REMOVE_ROOM_MODERATOR = "<REMOVE_WHITEBOARD_MODERATOR>";
				final public static String REMOVE_ROOM_MODERATOR_SUCCESS = "<REMOVE_WHITEBOARD_MODERATOR_SUCCEEDS>";
				final public static String REMOVE_ROOM_MODERATOR_FAILED = "<REMOVE_WHITEBOARD_MODERATOR_FAILED>";
					//this message may reply with a non-existing room error
					//this message may reply with an invalid modification password
			
			/**
			 * this message requires a room ID, a join password, and six integers, x, y, r, g, b, p
			 */
			final public static String SET_WHITEBOARD_PIXEL = "<SET_WHITEBOARD_PIXEL>";
				final public static String SET_WHITEBOARD_PIXEL_SUCCESS = "<SET_WHITEBOARD_PIXEL_SUCCEEDS>";
					//this message does not reply with anything else
				final public static String SET_WHITEBOARD_PIXEL_FAILED = "<SET_WHITEBOARD_PIXEL_FAILED>";
					//this message may reply with a non-existing room error
					//this message may reply with an invalid join password error
					//this message may reply with an index out of bounds error, which means the x and/or y is too big
					final public static int INDEX_OUT_OF_BOUNDS_ERROR_CODE = 1;
					//this message may reply with an invalid color error, which means the color is not valid r and/or g and/or b is not >= 0 and <=255
					final public static int INVALID_COLOR_ERROR_CODE = 2;
			
			//this message requires a room ID, a join password, a username, and a message
			final public static String ADD_CHAT_HISTORY = "<ADD_CHAT_HISTORY>";
				final public static String ADD_CHAT_HISTORY_SUCCESS = "<ADD_CHAT_HISTORY_SUCCEEDS>";
					//this message does not reply with anything else
				final public static String ADD_CHAT_HISTORY_FAILED = "<ADD_CHAT_HISTORY_FAILED>";
					//this message may reply with a non-existing room error
					//this message may reply with a non-existing user error
					//this message may reply with an invalid join password error
				
			//this message requires a room ID, a join password, a username and four booleans
			final public static String ADD_USER_PERMISSIONS = "<ADD_USER_PERMISSIONS>";
				final public static String ADD_USER_PERMISSIONS_SUCCESS = "<ADD_USER_PERMISSIONS_SUCCEEDS>";
					//this message does not reply with anything else
				final public static String ADD_USER_PERMISSIONS_FAILED = "<ADD_USER_PERMISSIONS_FAILED>";
					//this message may reply with a non-existing room error
					//this message may reply with a non-existing user error
					//this message may reply with an invalid join password error
				
			//this message requires a room ID, a join password, a username and four booleans
			final public static String REMOVE_USER_PERMISSIONS = "<REMOVE_USER_PERMISSIONS>";
				final public static String REMOVE_USER_PERMISSIONS_SUCCESS = "<REMOVE_USER_PERMISSIONS_SUCCEEDS>";
					//this message does not reply with anything else
				final public static String REMOVE_USER_PERMISSIONS_FAILED = "<REMOVE_USER_PERMISSIONS_FAILED>";
					//this message may reply with a non-existing room error
					//this message may reply with a non-existing user error
					//this message may reply with an invalid join password error
				
			//this message requires a room ID and a join password
			final public static String GET_WHITEBOARD_PIXELS = "<GET_WHITEBOARD_PIXELS>";
				final public static String GET_WHITEBOARD_PIXELS_SUCCESS = "<RETURN_WHITEBOARD_PIXELS>";
					//this message replies with an integer, P, the number of pixels on the whiteboard and
					//P bundles of pixel information. Each pixel information contains an x, y, r, g, b, p,
					//all integer values. x is the x-coordinate. y is the y-coordinate. r, g and b define
					//the color of the pixel (red, green, blue) and p is a BigInteger that is the priority
				final public static String GET_WHITEBOARD_PIXELS_FAILED = "<GET_WHITEBOARD_PIXELS_FAILED>";
					//this message may reply with a non-existing room error
					//this message may reply with an invalid join password error
				
			//this message requires a room ID and a join password
			final public static String GET_ROOM_MODERATORS = "<GET_ROOM_MODERATORS>";
				final public static String GET_ROOM_MODERATORS_SUCCESS = "<RETURN_ROOM_MODERATORS>";
					//this message replies with an integer, M, the number of moderators in this room and
					//a list of M usernames (Strings) of the M moderators.
				final public static String GET_ROOM_MODERATORS_FAILED = "<GET_ROOM_MODERATORS_FAILED>";
					//this message may reply with a non-existing room error
					//this message may reply with an invalid join password error
				
			//this message requires a room ID and a join password
			final public static String GET_ROOM_CHAT_HISTORY = "<GET_ROOM_CHAT_HISTORY>";
				final public static String GET_ROOM_CHAT_HISTORY_SUCCESS = "<RETURN_ROOM_CHAT_HISTORY>";
					//this message replies with an integer, L, the number of lines of chat history in this room
					//and a list of L lines of chat history. Each line of chat history is defined by
					//the username of the person who sent the message and the message
				final public static String GET_ROOM_CHAT_HISTORY_FAILED = "<GET_ROOM_CHAT_HISTORY_FAILED>";
					//this message may reply with a non-existing room error
					//this message may reply with an invalid join password error
				
			//this message requires a room ID and a join password
			final public static String GET_ROOM_USER_PERMISSIONS = "<GET_ROOM_USER_PERSMISSIONS>";
				final public static String GET_ROOM_USER_PERMISSIONS_SUCCESS = "<RETURN_ROOM_USER_PERMISSIONS>";
					//this message replies with an integer, U, the number of users who have lost permissions 
					//in this room and a list of U user permissions. Each user permission is defined by a
					//username (String) and 4 booleans, that represent audio participation on/off, audio
					//listening on/off, chat participation on/off, chat updating on/off. if the boolean is true,
					//then the given permission is on. if the boolean is false then it is off
				final public static String GET_ROOM_USER_PERMISSIONS_FAILED = "<GET_ROOM_USER_PERMISSIONS_FAILED>";
					//this message may reply with a non-existing room error
					//this message may reply with an invalid join password error
		}
	}
}
