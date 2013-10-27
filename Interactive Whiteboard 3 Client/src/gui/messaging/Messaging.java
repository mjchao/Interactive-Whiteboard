package gui.messaging;

import gui.login.Login;
import gui.messaging.friends.FriendsList;
import gui.messaging.friends.FriendsListListener;
import gui.messaging.pests.PestsList;
import gui.messaging.pests.PestsListListener;
import gui.messaging.privatechat.PrivateChat;
import gui.messaging.privatechat.PrivateChatListener;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import net.messagingserver.MessagingServerStepConnection;

import util.Text;

final public class Messaging extends JFrame
{
	private static final long serialVersionUID = 1L;
	
	//frame properties:
	final private static BorderLayout LAYOUT = new BorderLayout();
	final private static int LENGTH = 700;
	@SuppressWarnings("hiding")
	final private static int WIDTH = 700;
	
	//at the top, we'll have a list of buttons (similar to tabs) that the user can click on to view
	//certain user interfaces
	final private JPanel pnlCommands = new JPanel();
	final private JScrollPane scrollCommands = new JScrollPane(this.pnlCommands);
	final private FlowLayout PNL_COMMANDS_LAYOUT = new FlowLayout(FlowLayout.LEFT);
	final private Dimension PNL_COMMANDS_SIZE = new Dimension(LENGTH - 100, 50);
		final private JButton cmdFriendsList = new JButton();
		final private JButton cmdPestsList = new JButton();
		final private JButton cmdPrivateChat = new JButton();
		final private JLabel lblShowNewPms = new JLabel();
	//and the more useful user interfaces that the user can actually interact with will go
	//in this panel
	final private JPanel pnlMain = new JPanel();
	//final private JScrollPane scrollMain = new JScrollPane(this.pnlMain);
	final private GridLayout PNL_MAIN_LAYOUT = new GridLayout(1, 1);
	
	//lists
	final private FriendsList pnlFriendsList;
	final private PestsList pnlPestsList;
	final private PrivateChat pnlPrivateChat;
		
	
	public Messaging(MessagingServerStepConnection messagingServerStepConnection)
	{
		//set some textual properties
		this.cmdFriendsList.setText(Text.GUI.MESSAGING.VIEW_FRIENDS_LIST_STRING);
		this.cmdPestsList.setText(Text.GUI.MESSAGING.VIEW_PESTS_LIST_STRING);
		this.cmdPrivateChat.setText(Text.GUI.MESSAGING.VIEW_PRIVATE_CHAT_STRING);
		//add the components
		this.pnlCommands.setLayout(this.PNL_COMMANDS_LAYOUT);
		this.pnlCommands.setPreferredSize(this.PNL_COMMANDS_SIZE);
		this.pnlCommands.add(this.cmdFriendsList);
		this.pnlCommands.add(this.cmdPestsList);
		this.pnlCommands.add(this.cmdPrivateChat);
		this.pnlCommands.add(this.lblShowNewPms);
		
		this.pnlMain.setLayout(this.PNL_MAIN_LAYOUT);
		
		setLayout(LAYOUT);
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		add(this.scrollCommands, BorderLayout.NORTH);
		add(this.pnlMain, BorderLayout.CENTER);
		
		//create and add listeners to the friends, pest and private chat displays
		this.pnlFriendsList = new FriendsList(messagingServerStepConnection);
		this.pnlPestsList = new PestsList(messagingServerStepConnection);
		this.pnlPrivateChat = new PrivateChat(messagingServerStepConnection, this.pnlFriendsList, this.lblShowNewPms);
		this.pnlFriendsList.addFriendsListListener(new FriendsListListener(this.pnlFriendsList, messagingServerStepConnection));
		this.pnlPestsList.addPestsListListener(new PestsListListener(this.pnlPestsList, messagingServerStepConnection));
		this.pnlPrivateChat.addPrivateChatListener(new PrivateChatListener(this.pnlPrivateChat));
		
		//define some UI properties
		setSize(LENGTH, WIDTH);
		setVisible(false);
	}
	
	final public void addMessagingListener(MessagingListener l)
	{
		this.cmdFriendsList.addActionListener(l);
		this.cmdPestsList.addActionListener(l);
		this.cmdPrivateChat.addActionListener(l);
	}
	
	final private void loadPnlMain(Component c)
	{
		this.pnlMain.removeAll();
		this.pnlMain.add(c);
		this.pnlMain.revalidate();
		this.pnlMain.repaint();
	}
	
	final public void loadFriendsList()
	{
		updateFriendsList();
		loadPnlMain(this.pnlFriendsList);
	}
	
	final public void updateFriendsList()
	{
		this.pnlFriendsList.updateFriends();
		this.pnlPrivateChat.updateRecipients();
	}
	
	final public void setFriendStatus(String friendUsername, boolean isOnline)
	{
		this.pnlFriendsList.setFriendStatus(friendUsername, isOnline);
	}
	
	final public void loadPestsList()
	{
		updatePestsList();
		loadPnlMain(this.pnlPestsList);
	}
	
	final public void updatePestsList()
	{
		this.pnlPestsList.updatePests();
	}
	
	final public void loadPrivateChat()
	{
		updateFriendsList();
		updatePrivateChatRecipients();
		updatePrivateChatMessages();
		loadPnlMain(this.pnlPrivateChat);
	}
	
	final public void updatePrivateChatRecipients()
	{
		this.pnlPrivateChat.updateRecipients();
	}
	
	final public void updatePrivateChatMessages()
	{
		this.pnlPrivateChat.loadPmHistory();
		this.pnlPrivateChat.countAndUpdateConversationsWithUnreadMessages();
	}
	
	final public void addPrivateMessage(String senderUsername, String recipientUsername, String message)
	{
		if (Login.m_username.equals(senderUsername))
		{
			this.pnlPrivateChat.addMessageToConversation(senderUsername, recipientUsername, message, PrivateChat.MESSAGE_IS_READ);
		} else if (Login.m_username.equals(recipientUsername))
		{
			this.pnlPrivateChat.addMessageToConversation(senderUsername, recipientUsername, message, PrivateChat.MESSAGE_IS_NOT_READ);
		}
	}
	
	//DEBUG
	
	final public static void main(String[] args)
	{
		Messaging messaging = new Messaging(null);
		messaging.setVisible(true);
		messaging.addMessagingListener(new MessagingListener(messaging));
	}//*/
}
