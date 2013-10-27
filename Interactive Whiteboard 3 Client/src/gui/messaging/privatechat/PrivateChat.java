package gui.messaging.privatechat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.AncestorEvent;

import util.CommonMethods;
import util.Text;

import net.InvalidMessageException;
import net.MESSAGES;
import net.OperationFailedException;
import net.messagingserver.MessagingServerStepConnection;
import net.messagingserver.MessagingServerStepConnection.PrivateMessage;

import gui.login.Login;
import gui.messaging.List;
import gui.messaging.friends.FriendsList;
import gui.messaging.friends.FriendsList.Friend;

public class PrivateChat extends List
{
	final private static int MAX_CONVERSATIONS = FriendsList.MAX_FRIENDS;
	private static final long serialVersionUID = 1L;
	
	final public static boolean MESSAGE_IS_READ = true;
	final public static boolean MESSAGE_IS_NOT_READ = false;
	
	final private JLabel m_lblNewPms;
	//what we use to deal with recipients and sending messages
	final private FriendsList m_friendsList;
	final private RecipientsList m_recipientsList;
	
	final private ButtonGroup m_compareGroup = new ButtonGroup();
	final private JRadioButton m_optCompareByTime = new JRadioButton();
	final private JRadioButton m_optCompareByName = new JRadioButton();
	final private JButton m_cmdRelist = new JButton();
	
	final private ConversationCompareByTime m_compareTime = new ConversationCompareByTime();
	final private ConversationCompareByName m_compareName = new ConversationCompareByName();
	
	//networking objects used:
	final protected MessagingServerStepConnection m_messagingServerStepConnection;

	public PrivateChat(MessagingServerStepConnection messagingServerStepConnection, FriendsList friendsList, JLabel showNewPmsLabel)
	{
		super();
		this.elements = new Conversation[MAX_CONVERSATIONS];
		this.m_lblNewPms = showNewPmsLabel;
		//define some textual properties
		this.m_optCompareByTime.setText(Text.GUI.MESSAGING.PRIVATECHAT.SORT_BY_TIME_STRING);
		this.m_optCompareByTime.setSelected(true);
		this.m_optCompareByName.setText(Text.GUI.MESSAGING.PRIVATECHAT.SORT_BY_NAME_STRING);
		this.m_optCompareByName.setSelected(false);
		this.m_cmdRelist.setText(Text.GUI.MESSAGING.PRIVATECHAT.RELIST_STRING);
		//add some components
		this.m_compareGroup.add(this.m_optCompareByTime);
		this.m_compareGroup.add(this.m_optCompareByName);
		this.pnlCommands.add(this.m_optCompareByTime);
		this.pnlCommands.add(this.m_optCompareByName);
		this.pnlCommands.add(this.m_cmdRelist);
		//remember the device we will use to send messages to the messaging server
		this.m_messagingServerStepConnection = messagingServerStepConnection;
		//store the friends list. we'll import the list of possible recipients from here
		this.m_friendsList = friendsList;
		//create the recipients list
		this.m_recipientsList = new RecipientsList(this.m_friendsList);
		add(this.m_recipientsList, BorderLayout.EAST);
	}
	
	final public void addPrivateChatListener(PrivateChatListener l)
	{
		this.m_cmdRelist.addActionListener(l);
	}
	
	final public void loadPmHistory()
	{
		this.elements = new Conversation[MAX_CONVERSATIONS];
		try
		{
			PrivateMessage[] privateMessages = this.m_messagingServerStepConnection.getPrivateMessages(Login.m_username, Login.m_password);
			for (int privateMessageIndex = 0; privateMessageIndex < privateMessages.length; privateMessageIndex++)
			{
				PrivateMessage privateMessageToAdd = privateMessages[privateMessageIndex];
				String senderUsername = privateMessageToAdd.getSender();
				String recipientUsername = privateMessageToAdd.getRecipient();
				String messageContents = privateMessageToAdd.getMessage();
				boolean isMessageRead = privateMessageToAdd.isRead();
				addMessageToConversation(senderUsername, recipientUsername, messageContents, isMessageRead);
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
	}
	
	final public void addConversation(String username, boolean shouldDisplayOnGUI)
	{
		ListElementComparator comparator = null;
		if (this.m_optCompareByName.isSelected())
		{
			comparator = this.m_compareName;
		} else if (this.m_optCompareByTime.isSelected())
		{
			comparator = this.m_compareTime;
		}
		//make sure the conversation does not already exist
		for (int conversationIndex = 0; conversationIndex < this.elements.length; conversationIndex++)
		{
			Conversation aConversation = (Conversation) this.elements[conversationIndex];
			if (aConversation != null)
			{
				if (aConversation.getUsername().equals(username))
				{
					//if the conversation already exists and we should add it to the GUI, just add it and
					//return
					if (shouldDisplayOnGUI)
					{
						super.addElementToGUI(aConversation, comparator);
					}
					return;
				}
			}
		}
		Conversation newConversation = new Conversation(username);
		newConversation.addConversationListener(new ConversationListener(newConversation));
		if (shouldDisplayOnGUI)
		{
			super.addElementToGUI(newConversation, comparator);
		} else
		{
			super.addElementToData(newConversation, comparator);
		}
	}
	
	final public void addMessageToConversation(String senderUsername, String recipientUsername, String message, boolean isRead)
	{
		//determine if we are the sender of the recipient
		if (Login.m_username.equals(senderUsername))
		{
			//if we are the sender then
			//find the conversation with the given recipient
			for (int conversationIndex = 0; conversationIndex < this.elements.length; conversationIndex++)
			{
				Conversation aConversation = (Conversation) this.elements[conversationIndex];
				if (aConversation != null)
				{
					if (aConversation.getUsername().equals(recipientUsername))
					{
						aConversation.addMessageToDialog(senderUsername, message, isRead);
						return;
					}
				}
			}
		} else if (Login.m_username.equals(recipientUsername))
		{
			//if we are the recipient then
			//see if we already started a conversation with the given sender
			for (int conversationIndex = 0; conversationIndex < this.elements.length; conversationIndex++)
			{
				Conversation aConversation = (Conversation) this.elements[conversationIndex];
				if (aConversation != null)
				{
					if (aConversation.getUsername().equals(senderUsername))
					{
						//modify the conversation
						aConversation.addMessageToDialog(senderUsername, message, isRead);
						//stop everything - we are finished
						return;
					}
				}
			}
			if (isRead)
			{
				addConversation(senderUsername, false);
			} else
			{
				addConversation(senderUsername, true);
			}
			addMessageToConversation(senderUsername, recipientUsername, message, isRead);
		}
	}
	
	final public void refreshConversations()
	{
		if (this.m_optCompareByName.isSelected())
		{
			super.refreshList(this.m_compareName);
		} else if (this.m_optCompareByTime.isSelected())
		{
			super.refreshList(this.m_compareTime);
		}
	}
	
	final protected void refreshConversationsWithoutSort()
	{
		super.refreshList(null);
	}
	
	final public void updateRecipients()
	{
		this.m_recipientsList.loadData();
	}
	
	final public void countAndUpdateConversationsWithUnreadMessages()
	{
		int conversationsWithUnreadMessages = 0;
		for (int conversationIndex = 0; conversationIndex < this.elements.length; conversationIndex++)
		{
			Conversation aConversation = (Conversation) this.elements[conversationIndex];
			if (aConversation != null)
			{
				if (aConversation.containsUnreadMessages())
				{
					conversationsWithUnreadMessages++;
				}
			}
		}
		this.m_lblNewPms.setText(Text.GUI.MESSAGING.PRIVATECHAT.getNewPmsString(conversationsWithUnreadMessages));
		this.m_recipientsList.loadData();
	}
	
	/**
	 * Displays a list of everyone to whom the user can send a private message.
	 */
	final private class RecipientsList extends List
	{
		private static final long serialVersionUID = 1L;
		
		final private FriendsList m_dataSource;
		
		public RecipientsList(FriendsList dataSource)
		{
			this.elements = new Recipient[FriendsList.MAX_FRIENDS];
			this.m_dataSource = dataSource;
		}
		
		final protected void loadData()
		{
			this.elements = new Recipient[FriendsList.MAX_FRIENDS];
			//go through all the friends and get their usernames and display names
			for (int friendIndex = 0; friendIndex < FriendsList.MAX_FRIENDS; friendIndex++)
			{
				Friend friendToAdd = this.m_dataSource.getFriendAt(friendIndex);
				if (friendToAdd != null)
				{
					//load all the friends again
					addRecipient(friendToAdd.getUsername(), friendToAdd.getDisplayName(), friendToAdd.isOnline());
				}
			}
			super.refreshList(null);
		}
		
		final protected void addRecipient(String recipientUsername, String recipientDisplayName, boolean isRecipientOnline)
		{
			Recipient recipientToAdd = new Recipient(recipientUsername, recipientDisplayName, isRecipientOnline);
			recipientToAdd.addRecipientListener(new RecipientListener(recipientToAdd));
			super.addElementToGUI(recipientToAdd, null);
			//if the recipient is online, reflect that in the conversations list as well
			for (int conversationIndex = 0; conversationIndex < PrivateChat.this.getNumElements(); conversationIndex++)
			{
				Conversation aConversation = (Conversation) PrivateChat.this.getElementAt(conversationIndex);
				if (aConversation != null)
				{
					if (aConversation.getUsername().equals(recipientUsername))
					{
						recipientToAdd.reflectContainsUnreadMessages(aConversation.containsUnreadMessages());
						//TODO Would be nice to show if the recipient is online or not
					}
				}
			}

		}
		
		final private class Recipient extends ListElement
		{
			private static final long serialVersionUID = 1L;


			//user data:
			final private String m_username;
			
			//graphical components:
			final private FlowLayout RECIPIENT_LAYOUT = new FlowLayout(FlowLayout.CENTER);
			final private JLabel m_lblRecipientDisplayName = new JLabel();
			final private JButton m_cmdSendMessage = new JButton();
			//properties:
			private boolean m_isOnline;
			
			public Recipient(String username, String displayName, boolean isOnline)
			{
				this.m_username = username;
				//set the textual properties of the graphical components
				if (isOnline)
				{
					this.m_lblRecipientDisplayName.setText(Text.GUI.MESSAGING.FRIENDS.ONLINE_STRING + displayName);
				} else
				{
					this.m_lblRecipientDisplayName.setText(Text.GUI.MESSAGING.FRIENDS.OFFLINE_STRING + displayName);
				}
				this.m_cmdSendMessage.setText(Text.GUI.MESSAGING.PRIVATECHAT.SEND_A_MESSAGE_STRING);
				//add graphical components
				setLayout(this.RECIPIENT_LAYOUT);
				add(this.m_lblRecipientDisplayName);
				add(this.m_cmdSendMessage);
				//record properties
				this.m_isOnline = isOnline;
			}
			
			final public void addRecipientListener(RecipientListener l)
			{
				this.m_cmdSendMessage.addActionListener(l);
			}
			
			final protected String getRecipientUsername()
			{
				return this.m_username;
			}
			
			final protected void reflectContainsUnreadMessages(boolean areThereUnreadMessagesFromThisRecipient)
			{
				if (areThereUnreadMessagesFromThisRecipient == true)
				{
					this.m_lblRecipientDisplayName.setForeground(Color.RED);
				} else
				{
					this.m_lblRecipientDisplayName.setForeground(Color.BLACK);
				}
			}
			
			@SuppressWarnings("unused")
			final protected boolean isOnline()
			{
				return this.m_isOnline;
			}
		}
		
		final private class RecipientListener implements ActionListener
		{

			final private Recipient m_gui;
			public RecipientListener(Recipient gui)
			{
				this.m_gui = gui;
			}
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				String command = e.getActionCommand();
				if (command.equals(Text.GUI.MESSAGING.PRIVATECHAT.SEND_A_MESSAGE_STRING))
				{
					PrivateChat.this.addConversation(this.m_gui.getRecipientUsername(), true);
				}
			}
			
		}
	}
	
	final private class Conversation extends ListElement
	{
		private static final long serialVersionUID = 1L;
		
		//graphical components
		final private BorderLayout CONVERSATION_LAYOUT = new BorderLayout();
		final private Dialog pnlDialog;
		final private FlowLayout CONVERSATION_COMMANDS_LAYOUT = new FlowLayout(FlowLayout.CENTER);
		final private JPanel pnlConversationCommands = new JPanel(this.CONVERSATION_COMMANDS_LAYOUT);
		final private JLabel lblDisplayName = new JLabel();
		final protected JButton cmdConversationAction = new JButton();		//button for viewing/hiding conversation
		final protected JButton cmdMarkConversationRead = new JButton();
		final protected JButton cmdRemoveConversation = new JButton();
		//conversation information
		final private String m_username;			//username of other person in conversation
		final private String m_displayName;			//display name of other person in conversation
		//properties
		private boolean isFloating = false;
		private boolean m_containsUnreadMessages = false;
		
		public Conversation(String username)
		{
			//set textual properties
			this.m_username = username;
			this.pnlDialog = new Dialog(this.m_username);
			this.pnlDialog.addDialogListener(new DialogListener(this.pnlDialog));
			this.m_displayName = PrivateChat.this.m_messagingServerStepConnection.getDisplayNameOfUser(Login.m_username, Login.m_password, this.m_username);
			this.lblDisplayName.setText(Text.GUI.MESSAGING.PRIVATECHAT.RECIPIENT_LABEL + this.m_displayName);
			this.cmdConversationAction.setText(Text.GUI.MESSAGING.PRIVATECHAT.VIEW_CONVERSATION_STRING);
			this.cmdMarkConversationRead.setText(Text.GUI.MESSAGING.PRIVATECHAT.MARK_AS_READ_STRING);
			this.cmdRemoveConversation.setText(Text.GUI.MESSAGING.PRIVATECHAT.REMOVE_CONVERSATION_STRING);
			//add components
			setLayout(this.CONVERSATION_LAYOUT);
				//add command components
				this.pnlConversationCommands.add(this.lblDisplayName);
				this.pnlConversationCommands.add(this.cmdConversationAction);
				this.pnlConversationCommands.add(this.cmdMarkConversationRead);
				this.pnlConversationCommands.add(this.cmdRemoveConversation);
			//add conversation components
			add(this.pnlConversationCommands, BorderLayout.NORTH);	
		}
		
		final public void addConversationListener(ConversationListener l)
		{
			this.cmdConversationAction.addActionListener(l);
			this.cmdMarkConversationRead.addActionListener(l);
			this.cmdRemoveConversation.addActionListener(l);
			this.addAncestorListener(l);
		}
		
		final public void addMessageToDialog(String senderUsername, String message, boolean isMessageRead)
		{
			String messageToDisplay = MESSAGES.unsubstituteForMessageDelimiters(message);
			String senderDisplayName = PrivateChat.this.m_messagingServerStepConnection.getDisplayNameOfUser(Login.m_username, Login.m_password, senderUsername);
			this.pnlDialog.addMessage(senderDisplayName + ": " + messageToDisplay);
			if (!isMessageRead)
			{
				this.m_containsUnreadMessages = true;
				this.lblDisplayName.setForeground(Color.RED);
			}
		}
		
		final public void showDialog()
		{
			add(this.pnlDialog);
			this.cmdConversationAction.setText(Text.GUI.MESSAGING.PRIVATECHAT.HIDE_CONVERSATION_STRING);
			revalidate();
		}
		
		final public void hideDialog()
		{
			remove(this.pnlDialog);
			this.cmdConversationAction.setText(Text.GUI.MESSAGING.PRIVATECHAT.VIEW_CONVERSATION_STRING);
			revalidate();
		}
		
		final public boolean isFloating()
		{
			return this.isFloating;
		}
		
		final public void setIsFloating(boolean b)
		{
			this.isFloating = b;
			if (b == false)
			{
				hideDialog();
			}
		}
		
		final public boolean containsUnreadMessages()
		{
			return this.m_containsUnreadMessages;
		}
		
		final public void setConversationAsRead()
		{
			this.lblDisplayName.setForeground(Color.BLACK);
			this.m_containsUnreadMessages = false;
		}
		
		//TODO for future use
		/*
		final public void showRecipientOnline()
		{
			this.lblDisplayName.setText(Text.GUI.MESSAGING.PRIVATECHAT.RECIPIENT_LABEL + Text.GUI.MESSAGING.FRIENDS.ONLINE_STRING + this.m_displayName);
		}
		
		final public void showRecipientOffline()
		{
			this.lblDisplayName.setText(Text.GUI.MESSAGING.PRIVATECHAT.RECIPIENT_LABEL + Text.GUI.MESSAGING.FRIENDS.OFFLINE_STRING + this.m_displayName);
		}*/
		
		/**
		 * @return			the username of the person you are talking to
		 */
		final public String getUsername()
		{
			return this.m_username;
		}
		
		final public String getDisplayName()
		{
			return this.m_displayName;
		}
		
		final public Date getLastMessageTime()
		{
			return this.pnlDialog.getLastMessageTime();
		}
		
		final private class Dialog extends JPanel
		{
			private static final long serialVersionUID = 1L;
			
			final private BorderLayout DIALOG_LAYOUT = new BorderLayout();
			final private JTextArea txtDialog = new JTextArea(10, 10);
			final private JScrollPane scrollDialog = new JScrollPane(this.txtDialog);
			final private FlowLayout SEND_LAYOUT = new FlowLayout(FlowLayout.CENTER);
			final private JPanel pnlSend = new JPanel(this.SEND_LAYOUT);
				final private JTextField txtSend = new JTextField(25);
				final private JButton cmdSend = new JButton();
			
			
			private String m_targetUsername;
			private Date lastMessage = Calendar.getInstance().getTime();
			
			
			public Dialog(String targetUsername)
			{
				this.m_targetUsername = targetUsername;
				//set textual properties
				this.cmdSend.setText(Text.GUI.MESSAGING.PRIVATECHAT.SEND_STRING);
				//set some other properties
				this.txtDialog.setEditable(false);
				//add components
				this.pnlSend.add(this.txtSend);
				this.pnlSend.add(this.cmdSend);
				setLayout(this.DIALOG_LAYOUT);
				add(this.scrollDialog, BorderLayout.CENTER);
				add(this.pnlSend, BorderLayout.SOUTH);
			}
			
			final public void addDialogListener(DialogListener l)
			{
				this.cmdSend.addActionListener(l);
				this.txtSend.addKeyListener(l);
			}
			
			final public void addMessage(String message)
			{
				this.txtDialog.setText(this.txtDialog.getText() + message + "\n");
				this.lastMessage = Calendar.getInstance().getTime();
			}
			
			final public Date getLastMessageTime()
			{
				return this.lastMessage;
			}
			
			final public String getTargetUsername()
			{
				return this.m_targetUsername;
			}
			
			final public String getUserMessage()
			{
				return this.txtSend.getText();
			}
			
			final public void resetMessageTextField()
			{
				this.txtSend.setText("");
			}
		}
		
		final private class DialogListener implements ActionListener, KeyListener
		{
			private Dialog m_gui;
			public DialogListener(Dialog gui)
			{
				this.m_gui = gui;
			}
			
			@Override
			final public void actionPerformed(ActionEvent e)
			{
				String command = e.getActionCommand();
				if (command.equals(Text.GUI.MESSAGING.PRIVATECHAT.SEND_STRING))
				{
					//send the message
					attemptSendMessage();
				}
			}

			@Override
			public void keyPressed(KeyEvent e)
			{
				int c = e.getKeyCode();
				if (c == KeyEvent.VK_ENTER)
				{
					attemptSendMessage();
				}
			}
			
			final private void attemptSendMessage()
			{
				String messageToSend = this.m_gui.getUserMessage();
				if (messageToSend == null || messageToSend.equals("") || MESSAGES.containsBadCharacters(messageToSend))
				{
					CommonMethods.displayErrorMessage(Text.GUI.MESSAGING.PRIVATECHAT.INVALID_MESSAGE_ERROR_MESSAGE);
				} else
				{
					try
					{
						messageToSend = MESSAGES.substituteForMessageDelimiters(messageToSend);
						PrivateChat.this.m_messagingServerStepConnection.sendPrivateMessage(Login.m_username, Login.m_password, this.m_gui.getTargetUsername(), messageToSend);
						this.m_gui.resetMessageTextField();
					} catch (IOException connectionEnded)
					{
						CommonMethods.displayErrorMessage(Text.NET.getConnectionLostMessage(Text.NET.MESSAGING_SERVER_NAME));
					} catch (OperationFailedException operationFailed)
					{
						//this means the client was tampered with (memory modified), so we don't care.
					} catch (InvalidMessageException badMessage)
					{
						PrivateChat.this.m_messagingServerStepConnection.handleInvalidMessageException(badMessage);
					}
				}
			}

			@Override
			public void keyReleased(KeyEvent arg0)
			{
				// unused
				
			}

			@Override
			public void keyTyped(KeyEvent arg0)
			{
				// unused
				
			}
		}
	}
	
	final private class ConversationListener extends ListElementListener
	{
		final private Conversation m_conversationGui;
		public ConversationListener(ListElement gui) 
		{
			super(gui);
			this.m_conversationGui = (Conversation) gui;
		}

		@Override
		public void actionPerformed(ActionEvent e) 
		{
			String command = e.getActionCommand();
			if (command.equals(Text.GUI.MESSAGING.PRIVATECHAT.VIEW_CONVERSATION_STRING))
			{
				if (this.m_conversationGui.isFloating())
				{
					this.m_conversationGui.showDialog();
				} else
				{
					CommonMethods.displayErrorMessage(Text.GUI.MESSAGING.PRIVATECHAT.CANNOT_VIEW_CONVERSATION_ERROR_MESSAGE);
				}
			} else if (command.equals(Text.GUI.MESSAGING.PRIVATECHAT.HIDE_CONVERSATION_STRING))
			{
				this.m_conversationGui.hideDialog();
			} else if (command.equals(Text.GUI.MESSAGING.PRIVATECHAT.MARK_AS_READ_STRING))
			{
				try 
				{
					PrivateChat.this.m_messagingServerStepConnection.setPmsWithFriendAsRead(Login.m_username, Login.m_password, this.m_conversationGui.getUsername());
					this.m_conversationGui.setConversationAsRead();
					PrivateChat.this.countAndUpdateConversationsWithUnreadMessages();
				} catch (IOException connectionLost)
				{
					PrivateChat.this.m_messagingServerStepConnection.displayConnectionLostMessage();
				} catch (InvalidMessageException invalidMessage)
				{
					PrivateChat.this.m_messagingServerStepConnection.handleInvalidMessageException(invalidMessage);
				}
			} else if (command.equals(Text.GUI.MESSAGING.PRIVATECHAT.REMOVE_CONVERSATION_STRING))
			{
				PrivateChat.this.removeElement(this.m_gui, null);
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
				if (this.m_conversationGui.isFloating() == true)
				{
					this.m_conversationGui.setIsFloating(false);
					this.m_conversationGui.hideDialog();
					PrivateChat.this.refreshConversationsWithoutSort();
				} else
				{
					this.m_conversationGui.setIsFloating(false);
				}
			} else
			{
				//System.out.println(e.getAncestor());
				this.m_conversationGui.setIsFloating(true);
			}
		}

		@Override
		public void ancestorMoved(AncestorEvent e) 
		{
			//ignore - here for future updates
		}

		@Override
		public void ancestorRemoved(AncestorEvent e)
		{
			//ignore - here for future updates
		}
		
	}
	
	final private class ConversationCompareByTime extends ListElementComparator
	{

		public ConversationCompareByTime()
		{
			//nothing needs to be done as of right now
		}

		@Override
		public int compare(ListElement arg0, ListElement arg1) 
		{
			Conversation argument0 = (Conversation) arg0;
			Conversation argument1 = (Conversation) arg1;
			if (arg0 != null && arg1 != null)
			{
				if (argument0.getLastMessageTime() != null && argument1.getLastMessageTime() != null)
				{
					return argument0.getLastMessageTime().compareTo(argument1.getLastMessageTime());
				} else if (argument0.getLastMessageTime() != null && argument1.getLastMessageTime() == null)
				{
					return 1;
				} else if (argument0.getLastMessageTime() == null && argument1.getLastMessageTime() != null)
				{
					return -1;
				} else
				{
					return 0;
				}
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
	
	final private class ConversationCompareByName extends ListElementComparator
	{
		public ConversationCompareByName() 
		{
			//nothing needs to be done as of right now
		}

		@Override
		public int compare(ListElement arg0, ListElement arg1) 
		{
			if (arg0 != null && arg1 != null)
			{
				return ((Conversation) arg0).getDisplayName().compareTo(((Conversation) arg1).getDisplayName());
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
