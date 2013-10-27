package util;

final public class Text
{
	final public static class GUI
	{
		final public static class LOGIN
		{
			final public static String DISPLAY_IP_STRING = "Your IP Address: ";
			final public static String IP_NOT_FOUND_ERROR_STRING = "Could not determine your computer's IP address. A valid IP address is required to connect. Terminating...";
			final public static String REQUEST_SERVER_IP_STRING = "Main Server IP Address: ";
			final public static String REQUEST_SERVER_PORT_STRING = "Main Server Port: ";
			final public static String REQUEST_USERNAME_STRING = "Username: ";
			final public static String REQUEST_PASSWORD_STRING = "Password: ";
			final public static String REGISTER_STRING = "Register";
			final public static String LOGIN_STRING = "Login";
			final public static String FRAME_TITLE = "Login";
		}
		
		final public static class LOGINLISTENER
		{
			final public static String INVALID_PORT_ERROR_MESSAGE = "Invalid port entered.";
			final public static String CONNECTION_FAILED_ERROR_MESSAGE = "Could not connect to server. Please make sure the IP address and port are correct.";
			final public static String CONNECTION_SUCCEEDS_SUCCESS_MESSAGE = "Successfully connect to server.";
			
			final public static String getUsernameTakenErrorMessage(String username)
			{
				return "The username \"" + username + "\" has already been taken. Please use a different one.";
			}
			final public static String getUsernameNotValidErrorMessage(String username)
			{
				return "The username \"" + username + "\" is not valid. It may contain an invalid character or may not meet the username selection criteria.";
			}
			final public static String getPasswordNotValidErrorMessage(String password)
			{
				return "The password \"" + password + "\" is not valid. It may contain an invalid character or may not meet the password selection criteria.";
			}
		}
		
		final public static class MAIN
		{
			//account menu
			final public static String ACCOUNT_MENU_STRING = "Account";
			final public static String CHANGE_PASSWORD_STRING = "Change Password";
			final public static String CHANGE_DISPLAY_NAME_STRING = "Change Display Name";
			final public static String EXIT_STRING = "Exit";
			//messaging menu
			final public static String MESSAGING_MENU_STRING = "Messaging";
			final public static String FRIENDS_LIST_STRING = "View Friends List";
			final public static String PESTS_LIST_STRING = "View Pests List";
			final public static String PRIVATE_CHAT_STRING = "View Private Chat";
			//room menu
			final public static String ROOM_MENU_STRING = "Room";
			final public static String CREATE_ROOM_STRING = "Create a Room";
			final public static String MODIFY_ROOM_STRING = "Modify a Room";
			final public static String JOIN_ROOM_STRING = "Join a Room";
			final public static String DELETE_ROOM_STRING = "Delete a Room";
			
			final public static class PASSWORDCHANGE
			{
				final public static String REQUEST_USERNAME_STRING = "Enter username: ";
				final public static String REQUEST_CURRENT_PASSWORD_STRING = "Enter current password: ";
				final public static String REQUEST_NEW_PASSWORD_STRING = "Enter new password: ";
				final public static String REQUEST_CONFIRM_NEW_PASSWORD_STRING = "Confirm new password: ";
				final public static String CLEAR_CONFIRM_MESSAGE = "Really reset all fields?";
				final public static String CLEAR_STRING = "Clear All Fields";
				final public static String CHANGE_PASSWORD_COMMAND_STRING = "Change Password";
				
				final public static String CONFIRM_PASSWORD_NOT_MATCHING_ERROR_MESSAGE = "The new password and the confirmation of the new password do not match.";
				final public static String NEW_PASSWORD_CONTAINS_BAD_CHARACTERS_ERROR_MESSAGE = "The password you entered contains invalid characters. Please use a different one without unusual symbols.";
				final public static String USERNAME_DOES_NOT_EXIST = "The username you provided at login no longer exists. Please contact your server to find out why it was deleted.";
				final public static String INVALID_CURRENT_PASSWORD_ERROR_MESSAGE = "The current password you entered was incorrect. Please try again.";
				final public static String INVALID_NEW_PASSWORD_ERROR_MESSAGE = "The new password you entered does not meet the criteria for valid passwords. Please contact your server for password selection details.";
				final public static String CHANGE_PASSWORD_SUCCESS_MESSAGE = "Your password has been successfully changed.";
			}
			
			final public static class DISPLAYNAMECHANGE
			{
				final public static String REQUEST_USERNAME_STRING = "Enter username: ";
				final public static String REQUEST_PASSWORD_STRING = "Enter password: ";
				final public static String REQUEST_NAME_CHANGE_CODE_STRING = "Enter name change code: ";
				final public static String REQUEST_NEW_DISPLAY_NAME_STRING = "Enter new name: ";
				final public static String REQUEST_CONFIRM_NEW_DISPLAY_NAME_STRING = "Confirm new name: ";
				final public static String CLEAR_CONFIRM_MESSAGE = "Really reset all fields?";
				final public static String CLEAR_STRING = "Clear All Fields";
				final public static String CHANGE_DISPLAY_NAME_COMMAND_STRING = "Change Display Name";
				
				final public static String CONFIRM_DISPLAY_NAME_NOT_MATCHING_ERROR_MESSAGE = "The new display name and the confirmation of the new display name do not match.";
				final public static String NEW_NAME_CONTAINS_INVALID_CHARACTERS_ERROR_MESSAGE = "The new display name you entered contains invalid characters. Please use a different one without unusual symbols.";
				final public static String USERNAME_DOES_NOT_EXIST_ERROR_MESSAGE = "The username you provided at login no longer exists. Please contact your server to find out why it was deleted.";
				final public static String INVALID_PASSWORD_ERROR_MESSAGE = "The password you entered was incorrect. Please try again.";
				final public static String INVALID_NAME_CHANGE_CODE_ERROR_MESSAGE = "The name change code you entered was incorrect. Please try again.";
				final public static String INVALID_NEW_NAME_ERROR_MESSAGE = "The new name you entered does not meet the criteria for valid new names. Please contact your server for display name selection details.";
				final public static String CHANGE_DISPLAY_NAME_SUCCESS_MESSAGE = "Your display name has been successfully changed.";
			}
		}
		
		final public static class CLIENTLISTENER
		{
			public static String EXIT_CONFIRM_MESSAGE = "Are you sure you want to exit?";
		}
		
		final public static class MESSAGING
		{
			final public static String VIEW_FRIENDS_LIST_STRING = "View Friends List";
			final public static String VIEW_PESTS_LIST_STRING = "View Pests List";
			final public static String VIEW_PRIVATE_CHAT_STRING = "View Private Chat";
			
			final public static class FRIENDS
			{
				final public static String REQUEST_FRIEND_USERNAME_STRING = "Enter the username of the friend you wish to add: ";
				final public static String INVALID_FRIEND_USERNAME_STRING = "The friend username you entered is invalid.";
				final public static String DEFAULT_FRIEND_DISPLAY_NAME = "Unknown Name";
				final public static String ADD_FRIEND_STRING = "Add A Friend";
				final public static String RELIST_STRING = "Relist Friends (Alphabetical Order)";
				final public static String REMOVE_FRIEND_STRING = "Remove Friend";
				final public static String USERNAME_DOES_NOT_EXIST_ERROR_MESSAGE = "The username you provided at login no longer exists. Please contact your server to find out why it was deleted.";
				final public static String INVALID_PASSWORD_ERROR_MESSAGE = "The password stored on your computer has been modified and is no longer correct.";
				final public static String FRIENDS_LIST_FULL_ERROR_MESSAGE = "Your friends list is full. Please remove a friend before adding a new one.";
				
				final public static String SEND_PRIVATE_MESSAGE = "Send Message";
				final public static String ONLINE_STRING = "\u2713 ";
				final public static String OFFLINE_STRING = "";
			}
			
			final public static class PESTS
			{
				final public static String REQUEST_PEST_USERNAME_STRING = "Enter the username of the pest you wish to add.";
				final public static String INVALID_PEST_USERNAME = "The pest username you entered is invalid.";
				final public static String NON_EXISTING_PEST_USERNAME = "The username you entered does not exist";
				final public static String DEFAULT_PEST_DISPLAY_NAME = "Unknown Name";
				final public static String ADD_PEST_STRING = "Add A Pest";
				final public static String RELIST_STRING = "Relist Pests (Alphabetical Order)";
				final public static String REMOVE_PEST_STRING = "Remove Pest";
				final public static String PESTS_LIST_FULL_ERROR_MESSAGE = "Your pests list is full. Please remove a pest before adding a new one.";
			}
			
			final public static class PRIVATECHAT
			{
				//unread private message count
				final public static String getNewPmsString(int numberOfNewPms)
				{
					if (numberOfNewPms == 1)
					{
						return "New messages from 1 friend!";
					}
					return "New messages from " + numberOfNewPms + " friends!";
				}
				
				//recipients list:
				final public static String SEND_A_MESSAGE_STRING = "Send A Message";
				//individual conversations:
				final public static String VIEW_CONVERSATION_STRING = "View Conversation";
				final public static String CANNOT_VIEW_CONVERSATION_ERROR_MESSAGE = "You cannot view the conversation in this state. Please drag the conversation out of the frame first.";
				final public static String HIDE_CONVERSATION_STRING = "Hide Conversation";
				final public static String MARK_AS_READ_STRING = "Mark Conversation Read";
				final public static String REMOVE_CONVERSATION_STRING = "Remove Conversation";
				final public static String RECIPIENT_LABEL = "Conversation with: ";
				final public static String SEND_STRING = "Send";
				final public static String INVALID_MESSAGE_ERROR_MESSAGE = "Invalid message entered. Your message must not be blank and should not use esoteric characters.";
				
				//sorting conversations:
				final public static String RELIST_STRING = "Relist Conversations";
				final public static String SORT_BY_TIME_STRING = "Sort by time";
				final public static String SORT_BY_NAME_STRING = "Sort by name";
				
			}
		}
		
		final public static class ROOM_DATA
		{
			final public static String CREATE_ROOM_STRING = "Create Room";
			final public static String MODIFY_ROOM_STRING = "Modify Room";
			final public static String DELETE_ROOM_STRING = "Delete Room";
			final public static String JOIN_ROOM_STRING = "Join Room";
			final public static String LEAVE_ROOM_STRING = "Leave Room";
			
			final public static String FAILED_CONNECT_TO_ROOM_SERVER_ERROR_MESSAGE = "Failed to connect to the room. Please make sure the join password is correct.";
			
			final public static class ROOM_LIST
			{
				final public static String ROOM_ID_HEADING = "ID: ";
				final public static String ROOM_NAME_HEADING = "Name: ";
				final public static String ROOM_CREATOR_HEADING = "Creator: ";
				final public static String ROOM_INFO_SEPARATOR = "          ";
				
				final public static String VIEW_ROOM_PROPERTIES_COMMAND = "View";
			}
			
			final public static class ROOM_PROPERTIES
			{
				final public static String ROOM_ID_STRING = "Room ID (0-2999):";
				final public static String ROOM_NAME_STRING = "Room Name:";
				final public static String CREATOR_NAME_STRING = "Creator's Display Name:";
				final public static String CREATION_DATE_STRING = "Creation Date:";
				final public static String MODIFICATION_PASSWORD_STRING = "Modification Password:";
				final public static String CONFIRM_MODIFICATION_PASSWORD_STRING = "Confirm Modification Password: ";
				final public static String PASSWORD_PROTECTION_ON_STRING = "Password Protection: ON";
				final public static String DISABLE_PASSWORD_PROTECTION_STRING = "Disable";
				final public static String PASSWORD_PROTECTION_OFF_STRING = "Password Protection: OFF";
				final public static String ENABLE_PASSWORD_PROTECTION_STRING = "Enable";
				final public static String JOIN_PASSWORD_STRING = "Join Password:";
				final public static String DEFAULT_JOIN_PASSWORD = "-";
				final public static String CONFIRM_JOIN_PASSWORD_STRING = "Confirm Join Password: ";
				final public static String WHITEBOARD_LENGTH_STRING = "Whiteboard Length:";
				final public static String DEFAULT_WHITEBOARD_LENGTH_STRING = "500";
				final public static String WHITEBOARD_WIDTH_STRING = "Whiteboard Width";
				final public static String DEFAULT_WHITEBOARD_WIDTH_STRING = "500";
				final public static String CREATE_ROOM_COMMAND = "Create";
				final public static String RESET_COMMAND = "Reset";
					final public static String CONFIRM_CLEAR_MESSAGE = "Are you sure you want to clear all fields?";
				final public static String MODIFY_ROOM_COMMAND = "Modify";
				final public static String DELETE_ROOM_COMMAND = "Delete";
				final public static String JOIN_ROOM_COMMAND = "Join";
				final public static String CANCEL_COMMAND = "Cancel";
					
			}
		}
		
		final public static class ROOM
		{
			final public static String INVALID_MODIFICATION_PASSWORD_ERROR_MESSAGE = "The modification password you provided was incorrect.";
			
			final public static class AUDIO
			{
				final public static String START_PARTICIPATION_STRING = "Start Speaking";
				final public static String STOP_PARTICIPATION_STRING = "Stop Participating";
				final public static String START_LISTENING_STRING = "Start Listening";
				final public static String STOP_LISTENING_STRING = "Stop Listening";
				
				final public static String LINE_UNAVAILABLE_ERROR_MESSAGE = "Cannot capture sounds. Please configure a sound input device then restart.";
				final public static String FORMAT_NOT_SUPPORTED_ERROR_MESSAGE = "Your computer does not support this program's audio format. Please contact your server for more details.";
				final public static String SECURITY_EXCEPTION_ERROR_MESSAGE = "Cannot capture sounds. Please give the program permissions to use your sound input device, then restart.";
				final public static String getUnexpectedErrorMessage(String exception)
				{
					return "Unexpected error: " + exception;
				}
			}
			
			final public static class TEXT
			{
				final public static String SEND_MESSAGE_STRING = "Send";
				final public static String SYSTEM_USERNAME = "<SYSTEM>";
			}
			
			final public static class WHITEBOARD
			{
				final public static String DEFAULT_BRUSH_TYPE_STRING = "Brush Type: Marker";
				final public static String SELECT_MARKER_STRING = "Use Marker";
				final public static String SELECT_ERASER_STRING = "Use Eraser";
				final public static String DEFAULT_COLOR_STRING = "Color: R = 0, G = 0, B = 0";
				final public static String DEFAULT_RED_STRING = "0";
				final public static String DEFAULT_GREEN_STRING = "0";
				final public static String DEFAULT_BLUE_STRING = "0";
				final public static String SET_COLOR_STRING = "Set Color";
				
				final public static String getColorString(int red, int green, int blue)
				{
					return "Color: R = " + red + ", G = " + green + ", B = " + blue;
				}
			}
			
			final public static class USER_LIST
			{
				final public static String REQUEST_MODIFICATION_PASSWORD_MESSAGE = "Please enter the modification password for this room.";
				final public static String AUTHORITY_TOO_LOW_ERROR_MESSAGE = "You don't have the authority to do that.";
				final public static String PROMOTE_COMMAND = "Promote";
				final public static String getPromoteSuccessMessage(String target)
				{
					return "Successfully promoted " + target + ".";
				}
				final public static String DEMOTE_COMMAND = "Demote";
				final public static String getDemoteSuccessMessage(String target)
				{
					return "Successfully demoted " + target + ".";
				}
				final public static String VIEW_PERMISSIONS_COMMAND = "Permissions";
				final public static String getSetPermissionsSuccessMessage(String target)
				{
					return "Successfully modified the permissions of " + target + ".";
				}
				final public static String KICK_COMMAND = "Kick";
				final public static String getKickedMessage(String kicker)
				{
					return kicker + " has removed you from the room.";
				}
				final public static String getKickSuccessMessage(String target)
				{
					return "Successfully removed " + target + " from the room.";
				}
				
				final public static class USER_PERMISSIONS_DISPLAY
				{
					final public static String AUDIO_PARTICIPATION_STRING = "Audio Participation: ";
					final public static String AUDIO_LISTENING_STRING = "Audio Listening: ";
					final public static String TEXT_PARTICIPATION_STRING = "Text Participation: ";
					final public static String TEXT_UPDATING_STRING = "Text Updating: ";
					final public static String ENABLED_STRING = "ENABLED";
					final public static String DISABLED_STRING = "DISABLED";
					final public static String ENABLE_COMMAND = "Enable";
					final public static String DISABLE_COMMAND = "Disable";
					final public static String HAS_WHITEBOARD_STRING = "Has Whiteboard: YES";
					final public static String DOES_NOT_HAVE_WHITEBOARD_STRING = "Has Whiteboard: NO";
					final public static String TAKE_WHITEBOARD_COMMAND = "Take Whiteboard";
					final public static String getTargetDoesNotHaveWhiteboardErrorMessage(String target)
					{
						return target + " does not have a whiteboard for you to take.";
					}
					final public static String getTakeWhiteboardSuccessMessage(String target)
					{
						return "Successfully took whiteboard from " + target + ".";
					}
					final public static String GIVE_WHITEBOARD_COMMAND = "Give Whiteboard";
					final public static String getYouDontHaveWhiteboardToGiveErrorMessage(String target)
					{
						return "You don't have a whiteboard to give to " + target + ".";
					}
					final public static String getGiveWhiteboardSuccessMessage(String target)
					{
						return "Successfully gave whiteboard to " + target + ".";
					}
					final public static String getReceivedWhiteboardMessage(String giver)
					{
						return "You received the whiteboard from " + giver + ".";
					}
					
					final public static String MODIFICATION_PASSWORD_STRING = "Modification Password: ";
					final public static String MODIFY_PERMISSIONS_COMMAND = "Modify Permissions";
					final public static String BACK_COMMAND = "Back to User List";
				}
			}
			
		}
	}	

	final public static class NET
	{
		final public static String MAIN_SERVER_NAME = "Main Server";
		final public static String MESSAGING_SERVER_NAME = "Messaging Server";
		final public static String ROOM_DATA_SERVER_NAME = "Room Data Server";
		final public static String ROOM_SERVER_NAME = "Room Server";
		
		final public static String getConnectionLostMessage(String serverName)
		{
			return "The connection to " + serverName + " server was unexpectedly lost.";
		}
		
		final public static class GENERAL
		{
			final public static String LOGIN_SUCCEEDS_SUCCESS_MESSAGE = "Successfully logged in.";
			final public static String LOGIN_FAILED_ERROR_MESSAGE = "Login failed. Please make sure the username and password are correct and that you are not already logged in.";
			
			final public static String USERNAME_DOES_NOT_EXIST_ERROR_MESSAGE = "The username stored on this computer has been modified.\n This client will no longer work properly. For future reference, please do not modify this computer's memory.";
			final public static String PASSWORD_INCORRECT_ERROR_MESSAGE = "The password storedon this computer has been modified. \n This client will no longer work properly. For future reference, please do not modify this computer's memory.";
		}
		
		final public static class MAINSERVER
		{
			final public static String REGISTER_SUCCEEDS_SUCCESS_MESSAGE = "Register successful.";
		}
		
		final public static class MESSAGINGSERVER
		{
			final public static String USER_DOES_NOT_EXIST_ERROR_MESSAGE = "The target user no longer exists.";
		}
		
		final public static class ROOMSERVER
		{
			final public static String LOGIN_FAILED_ERROR_MESSAGE = "Login failed. Please make sure the join password is correct.";
			final public static class WHITEBOARD
			{
				final public static String YOU_LACK_WHITEBOARD_ERROR_MESSAGE = "You must have possession of the whiteboard before you can draw on it.";
			}
		}
	}
}
