package gui.room.userlist;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;

import net.InvalidMessageException;
import net.roomserver.userlist.UserListStepConnection;

import util.Text;

public class UserListDisplay extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	final private UserList m_userList;
	final private JScrollPane scrollUserList;
	final private UserPermissionsDisplay m_userPermissionsDisplay;
	final private JScrollPane scrollPermissionsDisplay;
	
	private int m_myAuthority = -1;
	
	public UserListDisplay(int maximumUsersPerRoom)
	{
		this.m_userList = new UserList(maximumUsersPerRoom);
		this.scrollUserList = new JScrollPane(this.m_userList);
		this.m_userPermissionsDisplay = new UserPermissionsDisplay();
		this.scrollPermissionsDisplay = new JScrollPane(this.m_userPermissionsDisplay);
		loadUserList();
	}
	
	final public void addListeners(UserListStepConnection userListStepConnection)
	{
		this.m_userPermissionsDisplay.addUserPermissionsDisplayListener(new UserPermissionsDisplayListener(this.m_userPermissionsDisplay, userListStepConnection));
	}
	
	final public void load(Component c)
	{
		this.removeAll();
		this.add(c);
		this.revalidate();
		this.repaint();
	}
	
	final public void loadUserList()
	{
		this.load(this.scrollUserList);
	}
	
	final public void loadPermissionsDisplay()
	{
		this.load(this.scrollPermissionsDisplay);
	}
	
	final public int getAuthority()
	{
		return this.m_myAuthority;
	}
	
	final public UserList getUserList()
	{
		return this.m_userList;
	}
	
	final public void setAuthority(int authority)
	{
		this.m_myAuthority = authority;
	}
	
	final public void showUserPermissions(UserData permissionsToDisplay)
	{
		this.m_userPermissionsDisplay.loadUserData(permissionsToDisplay);
		loadPermissionsDisplay();
	}

	final private class UserPermissionsDisplay extends JPanel
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		//graphical elements
		final private GridLayout USER_PERMISSIONS_DISPLAY_LAYOUT = new GridLayout(7, 2);
		final private JLabel lblAudioParticipation = new JLabel();
		final private JButton cmdAudioParticipation = new JButton();
		final private JLabel lblAudioListening = new JLabel();
		final private JButton cmdAudioListening = new JButton();
		final private JLabel lblTextParticipation = new JLabel();
		final private JButton cmdTextParticipation = new JButton();
		final private JLabel lblTextUpdating = new JLabel();
		final private JButton cmdTextUpdating = new JButton();
		final private JLabel lblHasWhiteboard = new JLabel();
		final private JButton cmdWhiteboardCommand = new JButton();
		final private JLabel lblModificationPassword = new JLabel();
		final private JPasswordField txtModificationPassword = new JPasswordField();
		final private JButton cmdBack = new JButton();
		final private JButton cmdModify = new JButton();
		//user data
		private UserData m_currentUserData;
		
		public UserPermissionsDisplay()
		{
			setLayout(this.USER_PERMISSIONS_DISPLAY_LAYOUT);
			add(this.lblAudioParticipation);
			add(this.cmdAudioParticipation);
			
			add(this.lblAudioListening);
			add(this.cmdAudioListening);
			
			add(this.lblTextParticipation);
			add(this.cmdTextParticipation);
			
			add(this.lblTextUpdating);
			add(this.cmdTextUpdating);
			
			add(this.lblHasWhiteboard);
			add(this.cmdWhiteboardCommand);
			
			this.lblModificationPassword.setText(Text.GUI.ROOM.USER_LIST.USER_PERMISSIONS_DISPLAY.MODIFICATION_PASSWORD_STRING);
			add(this.lblModificationPassword);
			add(this.txtModificationPassword);
			
			this.cmdModify.setText(Text.GUI.ROOM.USER_LIST.USER_PERMISSIONS_DISPLAY.MODIFY_PERMISSIONS_COMMAND);
			add(this.cmdModify);
			this.cmdBack.setText(Text.GUI.ROOM.USER_LIST.USER_PERMISSIONS_DISPLAY.BACK_COMMAND);
			add(this.cmdBack);
		}
		
		final public void addUserPermissionsDisplayListener(UserPermissionsDisplayListener l)
		{
			this.cmdAudioParticipation.addActionListener(l);
			this.cmdAudioListening.addActionListener(l);
			this.cmdTextParticipation.addActionListener(l);
			this.cmdTextUpdating.addActionListener(l);
			this.cmdWhiteboardCommand.addActionListener(l);
			this.cmdModify.addActionListener(l);
			this.cmdBack.addActionListener(l);
		}
		
		final public UserData getCurrentData()
		{
			return this.m_currentUserData;
		}
		
		final public void loadUserData(UserData userData)
		{
			this.m_currentUserData = userData;
			boolean hasAudioParticipation = userData.hasAudioParticipation();
			if (hasAudioParticipation)
			{
				this.lblAudioParticipation.setText(Text.GUI.ROOM.USER_LIST.USER_PERMISSIONS_DISPLAY.AUDIO_PARTICIPATION_STRING + Text.GUI.ROOM.USER_LIST.USER_PERMISSIONS_DISPLAY.ENABLED_STRING);
				this.cmdAudioParticipation.setText(Text.GUI.ROOM.USER_LIST.USER_PERMISSIONS_DISPLAY.DISABLE_COMMAND);
			} else
			{
				this.lblAudioParticipation.setText(Text.GUI.ROOM.USER_LIST.USER_PERMISSIONS_DISPLAY.AUDIO_PARTICIPATION_STRING + Text.GUI.ROOM.USER_LIST.USER_PERMISSIONS_DISPLAY.DISABLED_STRING);
				this.cmdAudioParticipation.setText(Text.GUI.ROOM.USER_LIST.USER_PERMISSIONS_DISPLAY.ENABLE_COMMAND);
			}
			boolean hasAudioListening = userData.hasAudioListening();
			if (hasAudioListening)
			{
				this.lblAudioListening.setText(Text.GUI.ROOM.USER_LIST.USER_PERMISSIONS_DISPLAY.AUDIO_LISTENING_STRING + Text.GUI.ROOM.USER_LIST.USER_PERMISSIONS_DISPLAY.ENABLED_STRING);
				this.cmdAudioListening.setText(Text.GUI.ROOM.USER_LIST.USER_PERMISSIONS_DISPLAY.DISABLE_COMMAND);
			} else
			{
				this.lblAudioListening.setText(Text.GUI.ROOM.USER_LIST.USER_PERMISSIONS_DISPLAY.AUDIO_LISTENING_STRING + Text.GUI.ROOM.USER_LIST.USER_PERMISSIONS_DISPLAY.DISABLED_STRING);
				this.cmdAudioListening.setText(Text.GUI.ROOM.USER_LIST.USER_PERMISSIONS_DISPLAY.ENABLE_COMMAND);
			}
			boolean hasTextParticipation = userData.hasTextParticipation();
			if (hasTextParticipation)
			{
				this.lblTextParticipation.setText(Text.GUI.ROOM.USER_LIST.USER_PERMISSIONS_DISPLAY.TEXT_PARTICIPATION_STRING + Text.GUI.ROOM.USER_LIST.USER_PERMISSIONS_DISPLAY.ENABLED_STRING);
				this.cmdTextParticipation.setText(Text.GUI.ROOM.USER_LIST.USER_PERMISSIONS_DISPLAY.DISABLE_COMMAND);
			} else
			{
				this.lblTextParticipation.setText(Text.GUI.ROOM.USER_LIST.USER_PERMISSIONS_DISPLAY.TEXT_PARTICIPATION_STRING + Text.GUI.ROOM.USER_LIST.USER_PERMISSIONS_DISPLAY.DISABLED_STRING);
				this.cmdTextParticipation.setText(Text.GUI.ROOM.USER_LIST.USER_PERMISSIONS_DISPLAY.ENABLE_COMMAND);
			}
			boolean hasTextUpdating = userData.hasTextUpdating();
			if (hasTextUpdating)
			{
				this.lblTextUpdating.setText(Text.GUI.ROOM.USER_LIST.USER_PERMISSIONS_DISPLAY.TEXT_UPDATING_STRING + Text.GUI.ROOM.USER_LIST.USER_PERMISSIONS_DISPLAY.ENABLED_STRING);
				this.cmdTextUpdating.setText(Text.GUI.ROOM.USER_LIST.USER_PERMISSIONS_DISPLAY.DISABLE_COMMAND);
			} else
			{
				this.lblTextUpdating.setText(Text.GUI.ROOM.USER_LIST.USER_PERMISSIONS_DISPLAY.TEXT_UPDATING_STRING + Text.GUI.ROOM.USER_LIST.USER_PERMISSIONS_DISPLAY.DISABLED_STRING);
				this.cmdTextUpdating.setText(Text.GUI.ROOM.USER_LIST.USER_PERMISSIONS_DISPLAY.ENABLE_COMMAND);
			}
			boolean hasWhiteboard = userData.hasWhiteboard();
			if (hasWhiteboard)
			{
				this.lblHasWhiteboard.setText(Text.GUI.ROOM.USER_LIST.USER_PERMISSIONS_DISPLAY.HAS_WHITEBOARD_STRING);
				this.cmdWhiteboardCommand.setText(Text.GUI.ROOM.USER_LIST.USER_PERMISSIONS_DISPLAY.TAKE_WHITEBOARD_COMMAND);
			} else
			{
				this.lblHasWhiteboard.setText(Text.GUI.ROOM.USER_LIST.USER_PERMISSIONS_DISPLAY.DOES_NOT_HAVE_WHITEBOARD_STRING);
				this.cmdWhiteboardCommand.setText(Text.GUI.ROOM.USER_LIST.USER_PERMISSIONS_DISPLAY.GIVE_WHITEBOARD_COMMAND);
			}
			this.txtModificationPassword.setText("");
			if (UserListDisplay.this.getAuthority() <= this.m_currentUserData.getAuthority())
			{
				this.cmdAudioParticipation.setEnabled(false);
				this.cmdAudioListening.setEnabled(false);
				this.cmdTextParticipation.setEnabled(false);
				this.cmdTextUpdating.setEnabled(false);
				if (this.m_currentUserData.hasWhiteboard())
				{
					this.cmdWhiteboardCommand.setEnabled(false);
				}
				this.cmdModify.setEnabled(false);
			} else
			{
				this.cmdAudioParticipation.setEnabled(true);
				this.cmdAudioListening.setEnabled(true);
				this.cmdTextParticipation.setEnabled(true);
				this.cmdTextUpdating.setEnabled(true);
				this.cmdWhiteboardCommand.setEnabled(true);
				this.cmdModify.setEnabled(true);
			}
		}
		
		final public void handleButtonClicked(JButton buttonClicked)
		{
			if (buttonClicked == this.cmdAudioParticipation)
			{
				this.m_currentUserData.toggleAudioParticipation();
			} else if (buttonClicked == this.cmdAudioListening)
			{
				this.m_currentUserData.toggleAudioListening();
			} else if (buttonClicked == this.cmdTextParticipation)
			{
				this.m_currentUserData.toggleTextParticipation();
			} else if (buttonClicked == this.cmdTextUpdating)
			{
				this.m_currentUserData.toggleTextUpdating();
			}
			this.loadUserData(this.m_currentUserData);
		}
	}
	
	final private class UserPermissionsDisplayListener implements ActionListener
	{
		
		final private UserPermissionsDisplay m_gui;
		final private UserListStepConnection m_userListStepConnection;
		
		public UserPermissionsDisplayListener(UserPermissionsDisplay gui, UserListStepConnection userListStepConnection)
		{
			this.m_gui = gui;
			this.m_userListStepConnection = userListStepConnection;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			JButton buttonClicked = (JButton) e.getSource();
			String command = e.getActionCommand();
			if (command.equals(Text.GUI.ROOM.USER_LIST.USER_PERMISSIONS_DISPLAY.BACK_COMMAND))
			{
				UserListDisplay.this.loadUserList();
			} else if (command.equals(Text.GUI.ROOM.USER_LIST.USER_PERMISSIONS_DISPLAY.ENABLE_COMMAND))
			{
				this.m_gui.handleButtonClicked(buttonClicked);
			} else if (command.equals(Text.GUI.ROOM.USER_LIST.USER_PERMISSIONS_DISPLAY.DISABLE_COMMAND))
			{
				this.m_gui.handleButtonClicked(buttonClicked);
			} else if (command.equals(Text.GUI.ROOM.USER_LIST.USER_PERMISSIONS_DISPLAY.GIVE_WHITEBOARD_COMMAND))
			{
				try 
				{
					this.m_userListStepConnection.giveWhiteboardToUser(this.m_gui.getCurrentData());
				} catch (IOException e1) 
				{
					this.m_userListStepConnection.displayConnectionLostMessage();
				} catch (InvalidMessageException e1) 
				{
					this.m_userListStepConnection.handleInvalidMessageException(e1);
				}
			} else if (command.equals(Text.GUI.ROOM.USER_LIST.USER_PERMISSIONS_DISPLAY.TAKE_WHITEBOARD_COMMAND))
			{
				try 
				{
					this.m_userListStepConnection.takeWhiteboardFromUser(this.m_gui.getCurrentData());
				} catch (IOException e1) 
				{
					this.m_userListStepConnection.displayConnectionLostMessage();
				} catch (InvalidMessageException e1) 
				{
					this.m_userListStepConnection.handleInvalidMessageException(e1);
				}
			} else if (command.equals(Text.GUI.ROOM.USER_LIST.USER_PERMISSIONS_DISPLAY.MODIFY_PERMISSIONS_COMMAND))
			{
				try 
				{
					this.m_userListStepConnection.setUserPermissions(this.m_gui.getCurrentData());
				} catch (IOException e1) 
				{
					this.m_userListStepConnection.displayConnectionLostMessage();
				} catch (InvalidMessageException e1) 
				{
					this.m_userListStepConnection.handleInvalidMessageException(e1);
				}
			}
		}
	}
	
	//DEBUG
	/*
	final public static void main(String[] args)
	{
		JFrame testFrame = new JFrame();
		UserListDisplay display = new UserListDisplay(20, null);
		testFrame.add(display);
		testFrame.setVisible(true);
		testFrame.setSize(700, 700);
		UserData user1 = new UserData("1", "1", 0, false, false, false, true, false);
		UserData user2 = new UserData("2", "fdgjdfhgjdfhgdfgjdfg", 1, true, false, true, true, false);
		UserData user3 = new UserData("3", "Admin", 2, true, true, true, true, false);
		display.m_userList.setUser(user1, display, null);
		display.m_userList.setUser(user2, display, null);
		display.m_userList.setUser(user3, display, null);
	}*/
}
