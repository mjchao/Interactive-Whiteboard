package net;

public enum OperationErrorCode 
{
	NO_REASON_GIVEN,
	//user data errors:
		//username errors
			/**
			 * the username provided by the client does not exist on server side
			 */
			USERNAME_DOES_NOT_EXIST,		//this means the username stored has been changed
			
			/**
			 * the username provided at registration does not meet username requirements 
			 */
			NEW_USERNAME_IS_INVALID,		//this means a registering username is not valid
			
			/**
			 * the username provided at registration is already taken
			 */
			USERNAME_IN_USE,   			//this means the registering username is already taken
		//password errors
			/**
			 * the password provided is incorrect
			 */
			PASSWORD_IS_INCORRECT,			//this means the password is incorrect
			
			/**
			 * the new password for an existing user or the password provided at registration does not meet
			 * password requirements
			 */
			NEW_PASSWORD_IS_INVALID,		//this means a new password or a registering password is not valid
		//name change code errors
			/**
			 * name change code provided is incorrect
			 */
			NAME_CHANGE_CODE_IS_INCORRECT,
			
			/**
			 * new display name for an existing user does not meet display name requirements
			 */
			NEW_DISPLAY_NAME_IS_INVALID,
		//main server specific:
		//messaging server specific:
			/**
			 * the client requested information about a user that does not exist on server side
			 */
			TARGET_USER_DOES_NOT_EXIST,
	//room data errors:
		//room ID errors
			/**
			 * the client supplied the ID of a nonexistent room
			 */
			ROOM_ID_DOES_NOT_EXIST,
			
			/**
			 * a room with a given Room ID already eixsts 
			 */
			ROOM_ID_TAKEN,
			/**
			 * database could not create a new room data file
			 */
			DATABASE_IO_EXCEPTION,
		//room name errors
			/**
			 * the new room name for an existing room or provided at room creation does not meet room naming
			 * requirements
			 */
			NEW_ROOM_NAME_IS_INVALID,
		//join password errors
			/**
			 * the join password the client used to attempt to join a room is incorrect
			 */
			JOIN_PASSWORD_IS_INCORRECT,
			
			/**
			 * the new join password for an existing room or provided at room creation does not meet
			 * join password requirements
			 */
			NEW_JOIN_PASSWORD_IS_INVALID,
		//modification password errors
			/**
			 * the modification password the client used to attempt to modify a room is incorrect
			 */
			MODIFICATION_PASSWORD_IS_INCORRECT,
			
			/**
			 * the new modification password for an existing room or provided at room creation does not meet
			 * modification password requirements
			 */
			NEW_MODIFICATION_PASSWORD_IS_INVALID,
		//whiteboard dimension errors
			/**
			 * the provided length and/or width of the whiteboard is incorrect
			 */
			WHITEBOARD_DIMENSIONS_INVALID,
		//room data server specific:
		//room server specific:
			/**
			 * the client attempted to join a nonexisting room
			 */
			ROOM_DOES_NOT_EXIST,
			
			/**
			 * the client does not have permission to use a function
			 */
			AUTHORITY_TOO_LOW,
			//whiteboard:
				/**
				 * the client attempted to draw outside the whitebaord
				 */
				DRAWING_OUT_OF_BOUNDS,
				
				/**
				 * the client attempted to use an invalid color
				 */
				DRAWING_INVALID_COLOR,
				
				/**
				 * the client attempted to draw without possessing the whiteboard
				 */
				YOU_DO_NOT_HAVE_WHITEBOARD,
				
				/**
				 * the client attempted to take the whiteboard from someone who does not have the whiteboard
				 */
				TARGET_DOES_NOT_HAVE_WHITEBOARD,
				
		
	
}
