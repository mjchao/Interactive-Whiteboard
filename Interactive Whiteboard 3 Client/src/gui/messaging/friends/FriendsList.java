package gui.messaging.friends;

import gui.login.Login;
import gui.messaging.List;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.event.AncestorEvent;

import util.CommonMethods;
import util.Text;

import net.InvalidMessageException;
import net.OperationFailedException;
import net.messagingserver.MessagingServerStepConnection;

final public class FriendsList extends List
{
	private static final long serialVersionUID = 1L;
	
	final public static int MAX_FRIENDS = 1000;
	
	//graphical components:
	final private JButton cmdAddFriend = new JButton();
	final private JButton cmdRelist = new JButton();
	
	final private FriendComparator comparator = new FriendComparator();
	
	//networking objects used:
	final protected MessagingServerStepConnection m_messagingServerStepConnection;
	
	public FriendsList(MessagingServerStepConnection messagingServerStepConnection)
	{
		super();
		this.elements = new Friend[MAX_FRIENDS];
		//set some textual properties
		this.cmdAddFriend.setText(Text.GUI.MESSAGING.FRIENDS.ADD_FRIEND_STRING);
		this.cmdRelist.setText(Text.GUI.MESSAGING.FRIENDS.RELIST_STRING);
		//add components
		this.pnlCommands.add(this.cmdAddFriend);
		this.pnlCommands.add(this.cmdRelist);
		//remember the device that is going to send messages to the messaging server
		this.m_messagingServerStepConnection = messagingServerStepConnection;
		refreshList();
	}
	
	final public Friend[] getFriendData()
	{
		return (Friend[])super.elements.clone();
	}
	
	final public void addFriendsListListener(FriendsListListener l)
	{
		this.cmdAddFriend.addActionListener(l);
		this.cmdRelist.addActionListener(l);
	}
	
	final public void addFriend(String username)
	{
		Friend newFriend = new Friend(username);
		newFriend.addFriendListener(new FriendListener(newFriend));
		if (super.addElementToGUI(newFriend, this.comparator))
		{
			//great - do nothing
		} else
		{
			CommonMethods.displayErrorMessage(Text.GUI.MESSAGING.FRIENDS.FRIENDS_LIST_FULL_ERROR_MESSAGE);
		}
		
	}
	
	final void removeFriend(Friend aFriend)
	{
		try 
		{
			this.m_messagingServerStepConnection.removeFriend(Login.m_username, Login.m_password, aFriend.getUsername());
		} catch (IOException e)
		{
			this.m_messagingServerStepConnection.displayConnectionLostMessage();
		} catch (OperationFailedException e)
		{
			this.m_messagingServerStepConnection.handleOperationFailedException(e);
		} catch (InvalidMessageException e) 
		{
			this.m_messagingServerStepConnection.handleInvalidMessageException(e);
		}
		updateFriends();
	}
	
	//this method removes everything from the list and adds every friend again
	final public void refreshList()
	{
		super.refreshList(this.comparator);
	}
	
	final public Friend getFriendAt(int indexOfFriend)
	{
		return (Friend)this.elements[indexOfFriend];
	}
	
	final public void setFriendStatus(String friendUsername, boolean isFriendOnline)
	{
		for (int friendIndex = 0; friendIndex < this.elements.length; friendIndex++)
		{
			Friend aFriend = getFriendAt(friendIndex);
			if (aFriend != null)
			{
				if (aFriend.getUsername().equals(friendUsername))
				{
					aFriend.setIsOnline(isFriendOnline);
				}
			}
		}
	}
	
	final public void updateFriends()
	{
		try 
		{
			String[] friendsList = this.m_messagingServerStepConnection.getFriendsList(Login.m_username, Login.m_password);
			this.elements = new ListElement[MAX_FRIENDS];
			for (int friendsAdded = 0; friendsAdded < friendsList.length; friendsAdded++)
			{
				Friend friendToAdd = new Friend(friendsList[friendsAdded]);
				friendToAdd.addFriendListener(new FriendListener(friendToAdd));
				this.elements[friendsAdded] = friendToAdd;
			}
		} catch (IOException e)
		{
			this.m_messagingServerStepConnection.displayConnectionLostMessage();
		} catch (OperationFailedException e)
		{
			this.m_messagingServerStepConnection.handleOperationFailedException(e);
		} catch (InvalidMessageException e) 
		{
			this.m_messagingServerStepConnection.handleInvalidMessageException(e);
		}
		refreshList();
	}

	
	final public class Friend extends ListElement
	{
		private static final long serialVersionUID = 1L;
		
		//panel properties:
		final private FlowLayout FRIEND_LAYOUT = new FlowLayout(FlowLayout.CENTER);
		private boolean isFloating = false;
		//components
		final private JLabel lblFriendName = new JLabel();
		//final private JButton cmdPrivateMessage = new JButton();
		final private JButton cmdRemoveFriend = new JButton();
		//other information
		final private String m_username;
		final private String m_displayName;
		private boolean m_isOnline = false;
	
		public Friend(String username)
		{
			this.m_username = username;
			this.m_displayName = getFriendDisplayName(this.m_username);
			this.lblFriendName.setText(this.m_displayName);
			//cmdPrivateMessage.setText(Text.GUI.MESSAGING.FRIENDS.SEND_PRIVATE_MESSAGE);
			this.cmdRemoveFriend.setText(Text.GUI.MESSAGING.FRIENDS.REMOVE_FRIEND_STRING);
			
			//add components
			setLayout(this.FRIEND_LAYOUT);
			add(this.lblFriendName);
			//add(cmdPrivateMessage);
			add(this.cmdRemoveFriend);
			updateIsOnline();
		}
		
		final public void addFriendListener(FriendListener l)
		{
			this.cmdRemoveFriend.addActionListener(l);
			this.addAncestorListener(l);
		}
		
		final private String getFriendDisplayName(String friendUsername)
		{
			String displayName = FriendsList.this.m_messagingServerStepConnection.getDisplayNameOfUser(Login.m_username, Login.m_password, friendUsername);
			return displayName;
		}
		
		final public void setIsOnline(boolean isOnline)
		{
			this.m_isOnline = isOnline;
			updateDisplayNameLabel();
		}
		
		final public void updateIsOnline()
		{
			try 
			{
				this.m_isOnline = FriendsList.this.m_messagingServerStepConnection.isFriendOnline(Login.m_username, this.m_username);
			} catch (IOException e) 
			{
				CommonMethods.displayErrorMessage(Text.NET.getConnectionLostMessage(Text.NET.MESSAGING_SERVER_NAME));
			} catch (InvalidMessageException e) 
			{
				FriendsList.this.m_messagingServerStepConnection.handleInvalidMessageException(e);
			}
			updateDisplayNameLabel();
		}
		
		final public void updateDisplayNameLabel()
		{
			if (this.m_isOnline)
			{
				this.lblFriendName.setText(Text.GUI.MESSAGING.FRIENDS.ONLINE_STRING + this.m_displayName);
			} else
			{
				this.lblFriendName.setText(Text.GUI.MESSAGING.FRIENDS.OFFLINE_STRING + this.m_displayName);
			}
		}
		
		final public String getDisplayName()
		{
			return this.m_displayName;
		}
		
		final public String getUsername()
		{
			return this.m_username;
		}
		
		final public boolean isOnline()
		{
			return this.m_isOnline;
		}
		
		final void setIsFloating(boolean b)
		{
			this.isFloating = b;
		}
		
		final boolean isFloating()
		{
			return this.isFloating;
		}
	}
	
	final private class FriendListener extends ListElementListener
	{
		final private Friend m_friendGui;	
		public FriendListener(Friend gui)
		{
			super(gui);
			this.m_friendGui = gui;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			String command = e.getActionCommand();
			if (command.equals(Text.GUI.MESSAGING.FRIENDS.REMOVE_FRIEND_STRING))
			{
				try
				{
					FriendsList.this.m_messagingServerStepConnection.removeFriend(Login.m_username, Login.m_password, this.m_friendGui.getUsername());
					FriendsList.this.removeFriend(this.m_friendGui);
				} catch (IOException connectionEnded)
				{
					CommonMethods.displayErrorMessage(Text.NET.getConnectionLostMessage(Text.NET.MESSAGING_SERVER_NAME));
				} catch (OperationFailedException operationFailed)
				{
					switch(operationFailed.getErrorCode())
					{
						case USERNAME_DOES_NOT_EXIST:
							CommonMethods.displayErrorMessage(Text.GUI.MESSAGING.FRIENDS.USERNAME_DOES_NOT_EXIST_ERROR_MESSAGE);
							break;
						case PASSWORD_IS_INCORRECT:
							CommonMethods.displayErrorMessage(Text.GUI.MESSAGING.FRIENDS.INVALID_PASSWORD_ERROR_MESSAGE);
							break;
						case TARGET_USER_DOES_NOT_EXIST:
							//we don't handle this error - the user knows what s/he is doing
							break;
						default:
							//TODO
							
					}
				} catch (InvalidMessageException invalidMessage)
				{
					FriendsList.this.m_messagingServerStepConnection.handleInvalidMessageException(invalidMessage);
				}
			}
		}

		@Override
		public void ancestorAdded(AncestorEvent e) 
		{
			if (e.getAncestor().getName() == null)
			{
				//we reach this block if the conversation this observes was 
				//placed into the original panel it was in or if it was added to the 
				//panel pnlList.
				//this first block is called when the conversation was dragged from outside the panel and
				//placed into the panel. Since we do want to refresh the conversation,
				//for the user's convenience, we do that here.
				//however, if this conversation was just created and it being added, we do not want to
				//refresh the list, or we will trigger a stackoverflow exception. that's when the other
				//part of the if block is used. everything conversation starts as non-floating, so
				//the first block is only called after the user as made it floating.
				if (this.m_friendGui.isFloating() == true)
				{
					this.m_friendGui.setIsFloating(false);
					FriendsList.this.refreshList();
				} else
				{
					this.m_friendGui.setIsFloating(false);
				}
			} else
			{
				//System.out.println(e.getAncestor());
				this.m_friendGui.setIsFloating(true);
			}
		}

		@Override
		public void ancestorMoved(AncestorEvent event) 
		{
			//ignore
		}

		@Override
		public void ancestorRemoved(AncestorEvent event) 
		{
			//ignore
		}
	}
	
	final private class FriendComparator extends ListElementComparator
	{
		public FriendComparator()
		{
			//nothing needs to be done as of right now
		}

		@Override
		final public int compare(ListElement arg0, ListElement arg1)
		{
			if (arg0 != null && arg1 != null)
			{
				return ((Friend) arg0).getDisplayName().compareTo(((Friend) arg1).getDisplayName());
			} else if (arg0 != null && arg1 == null)
			{
				return 1;
			} else if (arg0 == null && arg1 != null)
			{
				return -1;
			} else
			{
				return 0;
			}
		}
	}
}
