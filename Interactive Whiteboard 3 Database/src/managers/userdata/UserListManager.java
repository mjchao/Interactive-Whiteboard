package managers.userdata;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * This class is responsible for keeping track of every single registered username so that
 * we don't end up with duplicate usernames.
 */
public class UserListManager
{
	final public static String DELIMITER = " ";
	final public static File USER_LIST_FILE = new File("userlist.in");

	final private static ArrayList<UserData> m_registeredUsers = new ArrayList<UserData>();
	
	final public static void load() throws IOException, NoSuchElementException
	{
		//look for a file "userlist.in"
		FileReader userListFileReader = new FileReader(USER_LIST_FILE);
		BufferedReader userListBufferedReader = new BufferedReader(userListFileReader);
		Scanner s = new Scanner(userListBufferedReader);
		//the first line will be the number of users
		int numberOfUsers = Integer.parseInt(s.nextLine());
		//each following line will contains user information
		for (int numberOfUsersRead = 0; numberOfUsersRead < numberOfUsers; numberOfUsersRead++)
		{
			String nextUserInfo = s.nextLine();
			//look through the user info - there should be a username and a display name
			Scanner scanInfo = new Scanner(nextUserInfo);
			String username = scanInfo.next();
			//have the user data manager load the user data
			UserDataManager.loadUserData(username);
			String displayName = scanInfo.next();
			UserData userToAdd = new UserData(username, displayName);
			m_registeredUsers.add(userToAdd);
			scanInfo.close();
		}
		userListFileReader.close();
		userListBufferedReader.close();
		s.close();
	}
	
	final public static boolean isUsernameAlreadyRegistered(String username)
	{
		for (int usernameIndex = 0; usernameIndex < m_registeredUsers.size(); usernameIndex++)
		{
			String aUsername = m_registeredUsers.get(usernameIndex).getUsername();
			if (aUsername.equals(username))
			{
				return true;
			}
		}
		return false;
	}
	
	final public static void assertUsernameIsRegistered(String username) throws UserNotFoundException
	{
		for (int usernameIndex = 0; usernameIndex < m_registeredUsers.size(); usernameIndex++)
		{
			String aUsername = m_registeredUsers.get(usernameIndex).getUsername();
			if (aUsername.equals(username))
			{
				return;
			}
		}
		throw new UserNotFoundException(username);
	}
	
	//this method assumes the username is not already registered
	//it will add the given username to the list of registered username
	final public static void addRegisteredUser(String username, String displayName)
	{
		m_registeredUsers.add(new UserData(username, displayName));
		attemptSave();
	}
	
	final private static void save() throws IOException
	{
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(USER_LIST_FILE)));
		//the first line is the number of registered users
		int numberOfRegisteredUsers = m_registeredUsers.size();
		out.println(numberOfRegisteredUsers);
		//then we print every registered user's username
		for (int usernameIndex = 0; usernameIndex < numberOfRegisteredUsers; usernameIndex++)
		{
			String aUsername = m_registeredUsers.get(usernameIndex).getUsername();
			String aDisplayName = m_registeredUsers.get(usernameIndex).getDisplayName();
			out.println(aUsername + DELIMITER + aDisplayName);
		}
		//close the file
		out.close();
		//we are done
	}
	
	final public static void attemptSave()
	{
		try 
		{
			save();
			load();
		} catch (IOException e) 
		{
			//ignore - must work
		}
	}
	
	//this is not password and name change code protected, but it is also separate from the user data
	//which makes the protection not required. however, to be safe, we should not call this method
	//other than right after when the client has successfully changed its display name in the user data.
	final public static void changeDisplayNameOfUser(String username, String newDisplayName) throws UserNotFoundException
	{
		UserData targetUser = locateUser(username);
		targetUser.setDisplayName(newDisplayName);
		attemptSave();
		
	}
	
	final public static String getDisplayNameOfUser(String username) throws UserNotFoundException
	{
		UserData targetUser = locateUser(username);
		return targetUser.getDisplayName();
	}
	
	final private static UserData locateUser(String username) throws UserNotFoundException
	{
		for (int userIndex = 0; userIndex < m_registeredUsers.size(); userIndex++)
		{
			UserData aUser = m_registeredUsers.get(userIndex);
			if (aUser.getUsername().equals(username))
			{
				return aUser;
			}
		}
		throw new UserNotFoundException(username);
	}
	
	final private static class UserData
	{
		final private String m_username;
		private String m_displayName;
		
		public UserData(String username, String displayName)
		{
			this.m_username = username;
			this.m_displayName = displayName;
		}
		
		final public String getUsername()
		{
			return this.m_username;
		}
		
		final public String getDisplayName()
		{
			return this.m_displayName;
		}
		
		final public void setDisplayName(String newDisplayName)
		{
			this.m_displayName = newDisplayName;
		}
	}
}
