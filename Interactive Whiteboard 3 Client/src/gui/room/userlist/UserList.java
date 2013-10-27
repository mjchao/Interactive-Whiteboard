package gui.room.userlist;

import gui.login.Login;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.InvalidMessageException;
import net.MESSAGES;
import net.roomserver.userlist.UserListStepConnection;

import util.CommonMethods;
import util.Text;

/**
 * Shows a list of users in a given room
 */
final public class UserList extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	final private static UserComparator userComparator = new UserComparator();
	final private User[] m_users;
	private GridLayout userListLayout;
	/**
	 * we don't want a single user element taking up the whole list, because that looks ugly,
	 * so we will define a minimum number of rows that must be on this list
	 */
	final private int MINIMUM_ROWS_ON_LAYOUT = 10;
	
	public UserList(int maximumUsersPerRoom)
	{
		this.m_users = new User[maximumUsersPerRoom];
	}

	/**
	 * given data for a user, adds the user to the list if the user is not already on the list and
	 * modifies the data for the user if the user is already on the list
	 * 
	 * @param userData						the new data for a given user in this room
	 * @param mainGUI						the main <code>UserListDisplay</code> interface
	 * @param userListStepConnection		networking device used for the listener for the user to add
	 */
	final public void setUser(UserData userData, UserListDisplay mainGUI, UserListStepConnection userListStepConnection)
	{
		if (userData.getUsername().equals(Login.m_username))
		{
			mainGUI.setAuthority(userData.getAuthority());
		}
		for (int userIndex = 0; userIndex < this.m_users.length; userIndex++)
		{
			User aUser = this.m_users[userIndex];
			if (aUser != null)
			{
				if (aUser.getUserData().getUsername().equals(userData.getUsername()))
				{
					System.out.println("Set user data");
					aUser.setUserData(userData);
					return;
				}
			}
		}
		for (int userIndex = 0; userIndex < this.m_users.length; userIndex++)
		{
			if (this.m_users[userIndex] == null)
			{
				User userToAdd = new User(userData);
				userToAdd.addUserListener(new UserListener(userToAdd, mainGUI, userListStepConnection));
				this.m_users[userIndex] = userToAdd;
				break;
			}
		}
		relistUsers();
	}
	
	final public void removeUser(String username)
	{
		for (int userIndex = 0; userIndex < this.m_users.length; userIndex++)
		{
			User aUser = this.m_users[userIndex];
			if (aUser != null)
			{
				if (aUser.getUserData().getUsername().equals(username))
				{
					this.m_users[userIndex] = null;
					break;
				}
			}
		}
		relistUsers();
	}
	
	final public void relistUsers()
	{
		//remove everything
		this.removeAll();
		//count the number of users to determine the layout
		int numberOfUsers = 0;
		for (int userIndex = 0; userIndex < this.m_users.length; userIndex++)
		{
			if (this.m_users[userIndex] != null)
			{
				numberOfUsers++;
			}
		}
		if (numberOfUsers < this.MINIMUM_ROWS_ON_LAYOUT)
		{
			this.userListLayout = new GridLayout(this.MINIMUM_ROWS_ON_LAYOUT, 1);
		} else
		{
			this.userListLayout = new GridLayout(numberOfUsers, 1);
		}
		setLayout(this.userListLayout);
		//now add the users onto the list
		Arrays.sort(this.m_users, userComparator);
		for (int userIndex = 0; userIndex < this.m_users.length; userIndex++)
		{
			User aUserToAdd = this.m_users[userIndex];
			if (aUserToAdd != null)
			{
				this.add(aUserToAdd);
			}
		}
		this.revalidate();
		this.repaint();
	}
	
	final private class User extends JPanel
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		final public Color NORMAL_USER_COLOR = new Color(0, 0, 250);
		final public int NORMAL_USER_AUTHORITY = 0;

		final public Color MODERATOR_COLOR = new Color(250, 150, 0);
		final public int MODERATOR_AUTHORITY = 1;
		
		final public Color ADMINISTRATOR_COLOR = new Color(250, 0, 0);
		final public int ADMINISTRATOR_AUTHORITY = 2;
		
		//graphical components
		final private GridLayout USER_LAYOUT = new GridLayout(1, 3);
		final private JLabel lblDisplayName = new JLabel();
		final private JButton cmdPromote = new JButton();
		final private JButton cmdDemote = new JButton();
		final private JButton cmdPermissions = new JButton();
		final private JButton cmdKick = new JButton();
		
		//user properties
		private UserData m_userData;
		
		public User(UserData userData)
		{
			this.m_userData = userData;
			//define graphical properties
			updateGraphicalUserInterface();
			setLayout(this.USER_LAYOUT);
			add(this.lblDisplayName);
			add(this.cmdPromote);
			add(this.cmdDemote);
			add(this.cmdPermissions);
			add(this.cmdKick);
		}
		
		final public void addUserListener(UserListener l)
		{
			this.cmdPromote.addActionListener(l);
			this.cmdDemote.addActionListener(l);
			this.cmdPermissions.addActionListener(l);
			this.cmdKick.addActionListener(l);
		}
		
		final public UserData getUserData()
		{
			return this.m_userData;
		}
		
		final public void setUserData(UserData data)
		{
			this.m_userData = data;
			updateGraphicalUserInterface();
		}
		
		@SuppressWarnings("unqualified-field-access")
		final public void updateGraphicalUserInterface()
		{
			System.out.println("Updating graphical user interface");
			this.lblDisplayName.setText(MESSAGES.unsubstituteForMessageDelimiters(this.m_userData.getDisplayName()));
			switch (this.m_userData.getAuthority())
			{
				case NORMAL_USER_AUTHORITY:
					this.lblDisplayName.setForeground(NORMAL_USER_COLOR);
					break;
				case MODERATOR_AUTHORITY:
					this.lblDisplayName.setForeground(MODERATOR_COLOR);
					break;
				case ADMINISTRATOR_AUTHORITY:
					this.lblDisplayName.setForeground(ADMINISTRATOR_COLOR);
					break;
				default:
					//ignore
			}
			this.cmdPromote.setText(Text.GUI.ROOM.USER_LIST.PROMOTE_COMMAND);
			this.cmdDemote.setText(Text.GUI.ROOM.USER_LIST.DEMOTE_COMMAND);
			this.cmdPermissions.setText(Text.GUI.ROOM.USER_LIST.VIEW_PERMISSIONS_COMMAND);
			this.cmdKick.setText(Text.GUI.ROOM.USER_LIST.KICK_COMMAND);
			//do not allow user to kick him/herself out of room
			if (Login.m_username.equals(this.m_userData.getUsername()))
			{
				this.cmdKick.setEnabled(false);
			}
		}
	}
	
	final private class UserListener implements ActionListener
	{

		final private User m_gui;
		final private UserListDisplay m_mainGUI;
		final private UserListStepConnection m_userListStepConnection;
		
		public UserListener(User gui, UserListDisplay mainGUI, UserListStepConnection userListStepConnection)
		{
			this.m_gui = gui;
			this.m_mainGUI = mainGUI;
			this.m_userListStepConnection = userListStepConnection;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			String command = e.getActionCommand();
			if (command.equals(Text.GUI.ROOM.USER_LIST.PROMOTE_COMMAND))
			{
				promoteUser();
			} else if (command.equals(Text.GUI.ROOM.USER_LIST.DEMOTE_COMMAND))
			{
				demoteUser();
			} else if (command.equals(Text.GUI.ROOM.USER_LIST.VIEW_PERMISSIONS_COMMAND))
			{
				this.m_mainGUI.showUserPermissions(this.m_gui.getUserData());
			} else if (command.equals(Text.GUI.ROOM.USER_LIST.KICK_COMMAND))
			{
				kickUser();
			}
		}
		
		final private void promoteUser()
		{
			try 
			{
				String modificationPassword = CommonMethods.requestInputMessage(Text.GUI.ROOM.USER_LIST.REQUEST_MODIFICATION_PASSWORD_MESSAGE);
				if (modificationPassword != null)
				{
					if (!modificationPassword.trim().equals(""))
					{
						this.m_userListStepConnection.promoteUser(this.m_gui.getUserData(), modificationPassword);
					}
				}
			} catch (IOException e) 
			{
				this.m_userListStepConnection.displayConnectionLostMessage();
			} catch (InvalidMessageException e) 
			{
				this.m_userListStepConnection.handleInvalidMessageException(e);
			}
		}
		
		final private void demoteUser()
		{
			try 
			{
				String modificationPassword = CommonMethods.requestInputMessage(Text.GUI.ROOM.USER_LIST.REQUEST_MODIFICATION_PASSWORD_MESSAGE);
				if (modificationPassword != null)
				{
					if (!modificationPassword.trim().equals(""))
					{
						this.m_userListStepConnection.demoteUser(this.m_gui.getUserData(), modificationPassword);
					}
				}
			} catch (IOException e) 
			{
				this.m_userListStepConnection.displayConnectionLostMessage();
			} catch (InvalidMessageException e) 
			{
				this.m_userListStepConnection.handleInvalidMessageException(e);
			}
		}
		
		final private void kickUser()
		{
			try 
			{
				this.m_userListStepConnection.kickUser(this.m_gui.getUserData());
			} catch (IOException e) 
			{
				this.m_userListStepConnection.displayConnectionLostMessage();
			} catch (InvalidMessageException e) 
			{
				this.m_userListStepConnection.handleInvalidMessageException(e);
			}
		}
	}
	
	final private static class UserComparator implements Comparator<User>
	{
		
		public UserComparator()
		{
			//do nothing - we don't need to do anything
		}
		
		@Override
		public int compare(User user1, User user2) 
		{
			if (user1 == null && user2 == null)
			{
				return 0;
			} else if (user1 == null && user2 != null)
			{
				return -1;
			} else if (user1 != null && user2 == null)
			{
				return 1;
			} else if (user1 != null && user2 != null)
			{
				return user1.getUserData().getDisplayName().compareTo(user2.getUserData().getDisplayName());
			} else
			{
				return 0;
			}
		}
		
	}
}
