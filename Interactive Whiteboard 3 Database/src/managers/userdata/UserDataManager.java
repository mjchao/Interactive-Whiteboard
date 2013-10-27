package managers.userdata;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import data.User;
import data.AccessDeniedException;

/**
 * This class is responsible for obtaining and modifying user data and only user data.
 * It is also responsible for ensuring that whatever wants to obtain/modify user data
 * is allowed to.
 */
final public class UserDataManager
{
	final static ArrayList<User> userData = new ArrayList<User>();
	
	final public static void loadUserData(String username) throws IOException
	{
		//make sure the data isn't already loaded
		boolean alreadyLoaded = false;
		for (int userIndex = 0; userIndex < userData.size(); userIndex++)
		{
			User aUser = userData.get(userIndex);
			if (aUser.getUsername().equals(username))
			{
				alreadyLoaded = true;
				//exit loop because we already found the user
				break;
			}
		}
		//ignore if the data is already loaded
		//if the data is not loaded, then load it
		if (!alreadyLoaded)
		{
			String filename = User.USER_FILENAME_PREFIX + username;
			File file = new File(filename);
			User userToLoad = new User(file);
			userData.add(userToLoad);
		}
	}
	
	final public static void unloadUserData(String username) throws UserNotFoundException
	{
		//go through all the user data and remove the first user with the given username from the list
		for (int userIndex = 0; userIndex < userData.size(); userIndex++)
		{
			User aUser = userData.get(userIndex);
			if (aUser.getUsername().equals(username))
			{
				userData.remove(userIndex);
				return;
			}
		}
		throw new UserNotFoundException(username);
	}
	
	//this method finds the password of a user
	final public static String getUserPassword(String username) throws UserNotFoundException
	{
		User targetUser = locateUser(username);
		return targetUser.getPassword();
	}
	
	//this method finds the display name of a user
	//only if what called this method can provide the password of this user
	final public static String getUserDisplayName(String username, String password) throws UserNotFoundException, AccessDeniedException
	{
		User targetUser = locateUser(username);
		return targetUser.getDisplayName(password);
	}
	
	//this method finds the name change code of a user
	//only if what called this method can provide the password of the user
	final public static String getUserNameChangeCode(String username, String password) throws UserNotFoundException, AccessDeniedException
	{
		User targetUser = locateUser(username);
		return targetUser.getNameChangeCode(password);
	}
	
	//this method finds the friends list of a user
	//only if what called this method can provide the password of the user
	final public static ArrayList<String> getUserFriendsList(String username, String password) throws UserNotFoundException, AccessDeniedException
	{
		User targetUser = locateUser(username);
		ArrayList<String> friendsList = new ArrayList<String>();
		//get the friends list
		//first find the number of friends
		int numberOfFriends = targetUser.getNumberOfFriends(password);
		//then add every pest to the list
		for (int friendIndex = 0; friendIndex < numberOfFriends; friendIndex++)
		{
			friendsList.add(targetUser.getFriendsListUsernameAt(password, friendIndex));
		}
		return friendsList;
	}
	
	//this method finds the pests list of a user
	//only if the what called this method can provide the password of the user
	final public static ArrayList<String> getUserPestsList(String username, String password) throws UserNotFoundException, AccessDeniedException
	{
		User targetUser = locateUser(username);
		ArrayList<String> pestsList = new ArrayList<String>();
		//get the pests list
		//first find the number of pests
		int numberOfPests = targetUser.getNumberOfPests(password);
		//then add every pest to the list
		for (int pestIndex = 0; pestIndex < numberOfPests; pestIndex++)
		{
			pestsList.add(targetUser.getPestsListUsernameAt(password, pestIndex));
		}
		return pestsList;
	}
	
	//this method finds the PM history of a user
	//only if what called this method can provide the password of the user
	final public static ArrayList<PrivateMessage> getUserPmHistory(String username, String password) throws UserNotFoundException, AccessDeniedException
	{
		User targetUser = locateUser(username);
		ArrayList<PrivateMessage> pmHistory = new ArrayList<PrivateMessage>();
		//get the PM history
		//first find the number of lines of PM history
		int numberOfLines = targetUser.getNumberOfLinesOfPmHistory(password);
		//then add every line to the list
		for (int lineIndex = 0; lineIndex < numberOfLines; lineIndex++)
		{
			String sender = targetUser.getPmHistorySenderAt(password, lineIndex);
			String recipient = targetUser.getPmHistoryRecipientAt(password, lineIndex);
			String message = targetUser.getPmHistoryMessageAt(password, lineIndex);
			boolean isMessageRead = targetUser.isPmHistoryReadAt(password, lineIndex);
			PrivateMessage aPrivateMessage = new PrivateMessage(sender, recipient, message, isMessageRead);
			pmHistory.add(aPrivateMessage);
		}
		return pmHistory;
	}
	
	
	final public static String getAndSetNewNameChangeCode(String username, String password) throws UserNotFoundException, AccessDeniedException
	{
		User targetUser = locateUser(username);
		String aRandomNameChangeCode = User.getRandomNameChangeCode();
		targetUser.setNameChangeCode(password, aRandomNameChangeCode);
		return aRandomNameChangeCode;
	}
	
	final public static class PrivateMessage
	{
		final private String m_sender;
		final private String m_recipient;
		final private String m_message;
		final private boolean m_isMessageRead;
		
		public PrivateMessage(String sender, String recipient, String message, boolean isMessageRead)
		{
			this.m_sender = sender;
			this.m_recipient = recipient;
			this.m_message = message;
			this.m_isMessageRead = isMessageRead;
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
		
		final public boolean isMessageRead()
		{
			return this.m_isMessageRead;
		}
	}
	
	//this class stores methods that may be accessed as a result of a message the main server part of the
	//whiteboard server sends
	final public static class MainServer
	{
		
		final public static void createNewUser(String username, String password) throws IOException
		{
			User newUser = new User(username, password);
			userData.add(newUser);
		}
		
		final public static void setUserPassword(String username, String currentPassword, String newPassword) throws UserNotFoundException, AccessDeniedException
		{
			User targetUser = locateUser(username);
			targetUser.setPassword(currentPassword, newPassword);
		}
		
		final public static void setUserDisplayName(String username, String password, String nameChangeCode, String newDisplayName) throws UserNotFoundException, AccessDeniedException
		{
			User targetUser = locateUser(username);
			targetUser.setDisplayName(password, nameChangeCode, newDisplayName);
			//if the display name was successfully changed in the data stored on the server
			//then update the user list to reflect that as well
			UserListManager.changeDisplayNameOfUser(username, newDisplayName);
		}
	}
	
	//this class stores methods that may be used as a result of a message sent by the
	//messaging server
	final public static class MessagingServer
	{
		//this method adds a username to the given user's friends list
		//only if what called this method can provide the first user's password
		final public static void addUserFriend(String username, String password, String friendUsername) throws UserNotFoundException, AccessDeniedException
		{
			User targetUser = locateUser(username);
			UserListManager.assertUsernameIsRegistered(friendUsername);
			targetUser.addFriend(password, friendUsername);
		}
		
		//this method removes a friend's username from the given user's friends list
		//only if what called this method can provide the user's password
		final public static void removeUserFriend(String username, String password, String friendUsername) throws UserNotFoundException, AccessDeniedException
		{
			User targetUser = locateUser(username);
			UserListManager.assertUsernameIsRegistered(friendUsername);
			targetUser.removeFriend(password, friendUsername);
			
		}
		
		//this method adds a username to the given user's pests list
		//only if what called this method can provide the first user's password
		final public static void addUserPest(String username, String password, String pestUsername) throws UserNotFoundException, AccessDeniedException
		{
			User targetUser = locateUser(username);
			UserListManager.assertUsernameIsRegistered(pestUsername);
			targetUser.addPest(password, pestUsername);
		}
		
		//this method removes a pest's username from the given user's pests list
		//only if what called this method can provide the user's password
		final public static void removeUserPest(String username, String password, String pestUsername) throws UserNotFoundException, AccessDeniedException
		{
			User targetUser = locateUser(username);
			UserListManager.assertUsernameIsRegistered(pestUsername);
			targetUser.removePest(password, pestUsername);
		}
		
		//this method adds a line of private chat history to the given user's PM history
		//only if what called this method can provide the password of the given user
		final public static void addPmHistory(String senderUsername, String senderPassword, String recipientUsername, String messageContents) throws UserNotFoundException, AccessDeniedException
		{
			User sender = locateUser(senderUsername);
			UserListManager.assertUsernameIsRegistered(recipientUsername);
			//add the pm to the sender's PM history
			sender.addToPmHistory(senderUsername, senderPassword, recipientUsername, messageContents);
			//add the pm to the recipient's PM history as well
			User recipient = locateUser(recipientUsername);
			//NOTICE - It is not required to supply a password to add a PM to a recipient's PM history
			//therefore, we can pass null as the password. the method will not use the password field
			recipient.addToPmHistory(senderUsername, null, recipientUsername, messageContents);
		}
		
		//this method sets the conversation of a user with another user as read
		final public static void setPrivateMessagesRead(String username, String password, String otherUsernameInvolved) throws UserNotFoundException, AccessDeniedException
		{
			User targetUser = locateUser(username);
			UserListManager.assertUsernameIsRegistered(otherUsernameInvolved);
			targetUser.setPrivateMessagesRead(password, otherUsernameInvolved);
		}
		
		final public static boolean isUsernameOnFriendsList(String username, String usernameToCheck) throws UserNotFoundException
		{
			User targetUser = locateUser(username);
			UserListManager.assertUsernameIsRegistered(usernameToCheck);
			if (targetUser.isUsernameOnFriendsList(usernameToCheck))
			{
				return true;
			}
			return false;
		}
		
		final public static boolean isUsernameOnPestsList(String username, String usernameToCheck) throws UserNotFoundException
		{
			User targetUser = locateUser(username);
			UserListManager.assertUsernameIsRegistered(usernameToCheck);
			if (targetUser.isUsernameOnPestsList(usernameToCheck))
			{
				return true;
			}
			return false;
		}
	}
	
	final public static boolean isUserDataLoaded(String username)
	{
		try
		{
			locateUser(username);
			return true;
		} catch (UserNotFoundException notFound)
		{
			return false;
		}
	}
	
	//this method returns the user data of a user with the given username
	final static User locateUser(String username) throws UserNotFoundException
	{
		//go through all user data and look for a user with the given username
		for (int userIndex = 0; userIndex < userData.size(); userIndex++)
		{
			User aUser = userData.get(userIndex);
			if (aUser.getUsername().equals(username))
			{
				return aUser;
			}
		}
		throw new UserNotFoundException(username);
	}
}
