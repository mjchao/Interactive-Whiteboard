package mc.iwclient.util;

final public class Text {

	final public static class Login {
		
		public static String LOGIN_TITLE = "Login";
		
		public static String REGISTER_TITLE = "Register";
		
		public static String SETTINGS_TITLE = "Connection Settings";
		
		public static String OPT_LOGIN = "Login";
		
		public static String OPT_REGISTER = "Register";
		
		public static String OPT_SETTINGS = "Settings";
		
		public static String INPUT_USERNAME = "Username: ";
		
		public static String OPT_SAVE_USERNAME = "Save Username";
		
		public static String INPUT_DISPLAY_NAME = "Display Name: ";
		
		public static String INPUT_PASSWORD = "Password: ";
		
		public static String INPUT_CONFIRM_PASSWORD = "Confirm Password: ";
		
		public static String INPUT_EMAIL = "Email: ";
		
		public static String LOGIN_COMMAND = "Login";
		
		public static String REGISTER_COMMAND = "Register";
		
		public static String INPUT_MY_IP = "My IP: ";
		
		public static String INPUT_SERVER_IP = "Server IP: ";
		
		public static String INPUT_PORT = "Port: ";
		
		public static String RESET_COMMAND = "Reset";
		
		public static String SAVE_COMMAND = "Save";
		
		public static String LOAD_PREFERENCES_FAILED_ERROR = "Failed to load connection-settings preferences.";
		
		public static String SAVE_PREFERENCES_FAILED_ERROR = "Failed to save connection-settings preferences.";
	}
	
	final public static class Account {
		
		public static String ACCOUNT_UI_TITLE = "Account Management";
		
		public static String ACCOUNT_MENU_TITLE = "Account"; 
		
			public static String VIEW_NEWS_COMMAND = "IW News";
			public static String NEWS_UI_TITLE = ACCOUNT_UI_TITLE + " - IW4 News";
				
			public static String CHANGE_ACCOUNT_INFO_COMMAND = "Change Account Settings";
			public static String CHANGE_ACCOUNT_INFO_UI_TITLE = ACCOUNT_UI_TITLE + " > " + CHANGE_ACCOUNT_INFO_COMMAND;
				
				final public static class AccountInfo {
					
					public static String INPUT_USERNAME = "Username:";
					
					public static String INPUT_CURRENT_PASSWORD = "Current Password:";
					
					public static String INPUT_NEW_PASSWORD = "New Password:";
					
					public static String INPUT_CONFIRM_NEW_PASSWORD = "Confirm New Password:";
					
					public static String RESET_COMMAND = "Reset";
					
					public static String UPDATE_COMMAND = "Update";
				}
			
			public static String CHANGE_USER_INFO_COMMAND = "Change Personal Info";
			public static String CHANGE_USER_INFO_UI_TITLE = ACCOUNT_UI_TITLE + " > " + CHANGE_USER_INFO_COMMAND;
			
				final public static class UserInfo {
					
					public static String INPUT_DISPLAY_NAME = "Display Name:";
					
					public static String INPUT_DOB = "Date of Birth:";
					
					public static String INPUT_HOMETOWN = "Hometown:";
					
					public static String INPUT_SCHOOL = "School:";
					
					public static String RESET_COMMAND = "Reset";
					
					public static String UPDATE_COMMAND = "Update";
					
				}
			
			public static String LOGOUT_COMMAND = "Logout";
			
				final public static class Logout {
				
					public static String CONFIRM_LOGOUT_MESSAGE = "Are you sure you want to log out?";
				}
	}
	
	final public static class Messaging {

		public static String MESSAGING_MENU_TITLE = "Messaging";
		public static String MESSAGING_UI_TITLE = "Messaging";
		
		public static String VIEW_FRIENDS_LIST_COMMAND = "Friends List";
		public static String FRIENDS_LIST_UI_TITLE = MESSAGING_UI_TITLE + " - Friends List";
		
		public static String NON_FRIEND_LIST_UI_TITLE = MESSAGING_UI_TITLE + " - Non-friends List";
		
		public static String VIEW_PESTS_LIST_COMMAND = "Pests List";
		public static String PESTS_LIST_UI_TITLE = MESSAGING_UI_TITLE + " > Pests List";
		
		final public static class SearchableGraphicList {
		
			public static String SEARCH_COMMAND = "Search";
		
			public static String CANCEL_SEARCH_COMMAND = "View Full List";
			
			public static String INVALID_SEARCH_FILTER = "Search filter requires at least " + mc.iwclient.messaging.SearchableGraphicList.MINIMUM_SEARCH_FILTER_SIZE + " characters.";
		}
		
		final public static class MessagingUserLists {
			
			public static String SELECT_SEARCH_FILTER = "Search in";
			
			public static String OPT_SEARCH_NON_FRIENDS = "Non-friends";
			
			public static String OPT_SEARCH_FRIENDS = "Friends";
			
			public static String OPT_SEARCH_PESTS = "Pests";
			
			public static String MORE_INFO_COMMAND = "More Info";
			
			public static String ADD_FRIEND_COMMAND = "Add Friend";
			
			public static String MESSAGE_COMMAND = "Message";
			
			public static String ADD_PEST_COMMAND = "Add Pest";
			
			public static String REMOVE_COMMAND = "Remove";
			
			public static String BACK_COMMAND = "Back";
		}
		
		final public static class UserInfoPanel {
			
			public static String DISPLAY_NAME = Account.UserInfo.INPUT_DISPLAY_NAME;
			
			public static String DOB = Account.UserInfo.INPUT_DOB;
			
			public static String HOMETOWN = Account.UserInfo.INPUT_HOMETOWN;
			
			public static String SCHOOL = Account.UserInfo.INPUT_SCHOOL;
		}
		
		public static String VIEW_PM_COMMAND = "Private Messages";
		public static String PM_UI_TITLE = MESSAGING_UI_TITLE + " > Private Messages";
		
		final public static class PrivateMessagingPanel {
			
			public static String SEARCH_BOX_DESCRIPTION = "[search for friend]";
			
			public static String MARK_AS_READ_COMMAND = "Mark as read";
			
			public static String MARK_AS_UNREAD_COMMAND = "Mark as unread";
			
			public static String MESSAGE_COMMAND = "Send message";
			
			public static String CANCEL_COMMAND = "Cancel";
			
			public static String CONVERSATION_WITH_LABEL = "Conversation with ";
			
			public static String OPT_SEND_ON_ENTER = "Send using Enter key";
			
			public static String SEND_COMMAND = "Send";
			
		}
		
	}
	
	final public static class Room {
		
		public static String COLLABORATION_MENU_TITLE = "Collaboration";
		
			public static String BROWSE_ROOMS_COMMAND = "Browse Rooms";
			
			public static String ROOM_SELECTION_UI_TITLE = COLLABORATION_MENU_TITLE + " - Room Selection";
			
			public static String ROOM_ID_LABEL = "ID";
			
			public static String ROOM_NAME_LABEL = "Name";
			
			public static String ROOM_CREATOR_LABEL = "Creator";
			
			public static String ROOM_COMMANDS_LABEL = "Commands";
			
			public static String MORE_INFO_COMMAND = "More Info";
			
				public static String ROOM_ID_DESCRIPTION = "Room ID:";
				
				public static String ROOM_NAME_DESCRIPTION = "Room Name:";
				
				public static String ROOM_CREATOR_DESCRIPTION = "Room Creator:";
				
				public static String ROOM_CREATION_DATE_DESCRIPTION = "Creation Date:" ;
				
				public static String PASSWORD_PROTECTION_DESCRIPTION = "Password Protection:";
					public static String PASSWORD_PROTECTION_ON = "On";
					public static String PASSWORD_PROTECTION_OFF = "Off";
				
				public static String JOIN_PASSWORD_DESCRIPTION = "Join Password";
				
				public static String CREATOR_DESCRIPTION_DESCRIPTION = "Creator Description:";
				
				public static String CLOSE_STRING = "Close";
				
				public static String MODIFY_STRING = "Modify";
			
			public static String JOIN_COMMAND = "Join";
			
			public static class Activity {
				
				public static String BRING_TO_FRONT_COMMAND = "Bring to Front";
				
				public static String SEND_TO_BACK_COMMAND = "Send to Back";
				
				public static String DELETE_COMMAND = "Delete";
			}
			
			public static class WhiteboardActivity {
				
				public static String TOOLS_TOOLBAR_DESCRIPTION = "Tools";
				public static String COLORS_TOOLBAR_DESCRIPTION = "Colors";
				public static String THICKNESS_TOOLBAR_DESCRIPTION = "Stroke Size";
			}
			
			public static class UserList {
				
				public static String SEARCH_BOX_DESCRIPTION = "[search for user]";
				
				public static String WHISPER_COMMAND = "Whisper";
				
				public static String MORE_OPTIONS_COMMAND = "More options";
				
				public static String KICK_USER_COMMAND = "Kick";
			}
	}
	
	final public static class Dialogs {
		
		public static String CONFIRM_DIALOG_TITLE = "Confirm";
		
		public static String ERROR_DIALOG_TITLE = "Error";
	}
}
