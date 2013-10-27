package gui.main;

import gui.main.account.DisplayNameChange;
import gui.main.account.DisplayNameChangeListener;
import gui.main.account.PasswordChange;
import gui.main.account.PasswordChangeListener;
import gui.messaging.Messaging;
import gui.messaging.MessagingListener;
import gui.roomdata.RoomInfoInterface;
import gui.roomdata.RoomInfoInterfaceListener;
import gui.roomdata.RoomPropertiesInterface;

import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import net.mainserver.MainServerStepConnection;
import net.messagingserver.MessagingServerStepConnection;
import net.roomdataserver.RoomDataServerStepConnection;


import util.Text;

public class Client extends JFrame
{
	private static final long serialVersionUID = 1L;
	
	final private static int LENGTH = 700;
	@SuppressWarnings("hiding")
	final private static int WIDTH = 700;
	final private static GridLayout LAYOUT = new GridLayout(1, 1);
	
	//menus:
	final private JMenuBar menu = new JMenuBar();
		final private JMenu mnuAccount = new JMenu();
			final private JMenuItem itmChangePassword = new JMenuItem();
			final private JMenuItem itmChangeDisplayName = new JMenuItem();
			final private JMenuItem itmExit = new JMenuItem();
		final private JMenu mnuMessaging = new JMenu();
			final private JMenuItem itmFriendsList = new JMenuItem();
			final private JMenuItem itmPestsList = new JMenuItem();
			final private JMenuItem itmPrivateChat = new JMenuItem();
		final private JMenu mnuRoom = new JMenu();
			final private JMenuItem itmCreateRoom = new JMenuItem();
			final private JMenuItem itmModifyRoom = new JMenuItem();
			final private JMenuItem itmJoinRoom = new JMenuItem();
			final private JMenuItem itmDeleteRoom = new JMenuItem();
	//graphical components and containers:
	final private static GridLayout MAIN_LAYOUT = new GridLayout(1, 1);
	final private JPanel pnlMain = new JPanel(MAIN_LAYOUT);
	
	final private PasswordChange pnlPasswordChange = new PasswordChange();
	final private JScrollPane scrollPasswordChange = new JScrollPane(this.pnlPasswordChange);
	final private DisplayNameChange pnlDisplayNameChange = new DisplayNameChange();
	final private JScrollPane scrollDisplayNameChange = new JScrollPane(this.pnlDisplayNameChange);
	
	final private Messaging messaging;
	final private RoomInfoInterface roomInfoInterface;
	
	public Client(MainServerStepConnection mainServerStepConnection, MessagingServerStepConnection messagingServerStepConnection, RoomDataServerStepConnection roomDataServerStepConnection)
	{
		//first deal with the menus
			//set textual properties of menus
			this.mnuAccount.setText(Text.GUI.MAIN.ACCOUNT_MENU_STRING);
			this.itmChangePassword.setText(Text.GUI.MAIN.CHANGE_PASSWORD_STRING);
			this.itmChangeDisplayName.setText(Text.GUI.MAIN.CHANGE_DISPLAY_NAME_STRING);
			this.itmExit.setText(Text.GUI.MAIN.EXIT_STRING);
			
			this.mnuMessaging.setText(Text.GUI.MAIN.MESSAGING_MENU_STRING);
			this.itmFriendsList.setText(Text.GUI.MAIN.FRIENDS_LIST_STRING);
			this.itmPestsList.setText(Text.GUI.MAIN.PESTS_LIST_STRING);
			this.itmPrivateChat.setText(Text.GUI.MAIN.PRIVATE_CHAT_STRING);
			
			this.mnuRoom.setText(Text.GUI.MAIN.ROOM_MENU_STRING);
			this.itmCreateRoom.setText(Text.GUI.MAIN.CREATE_ROOM_STRING);
			this.itmModifyRoom.setText(Text.GUI.MAIN.MODIFY_ROOM_STRING);
			this.itmJoinRoom.setText(Text.GUI.MAIN.JOIN_ROOM_STRING);
			this.itmDeleteRoom.setText(Text.GUI.MAIN.DELETE_ROOM_STRING);
			//add the menus to the user interface
			this.mnuAccount.add(this.itmChangePassword);
			this.mnuAccount.add(this.itmChangeDisplayName);
			this.mnuAccount.add(this.itmExit);
			
			this.mnuMessaging.add(this.itmFriendsList);
			this.mnuMessaging.add(this.itmPestsList);
			this.mnuMessaging.add(this.itmPrivateChat);
			
			this.mnuRoom.add(this.itmCreateRoom);
			this.mnuRoom.add(this.itmModifyRoom);
			this.mnuRoom.add(this.itmJoinRoom);
			this.mnuRoom.add(this.itmDeleteRoom);
			
			this.menu.add(this.mnuAccount);
			this.menu.add(this.mnuMessaging);
			this.menu.add(this.mnuRoom);
			
			setJMenuBar(this.menu);
			
		//add listeners to the password and display name changing interfaces
			this.pnlPasswordChange.addPasswordChangeListener(new PasswordChangeListener(this.pnlPasswordChange, mainServerStepConnection));
			this.pnlDisplayNameChange.addDisplayNameChangeListener(new DisplayNameChangeListener(this.pnlDisplayNameChange, mainServerStepConnection));
		//create add listeners to the messaging interface
			this.messaging = new Messaging(messagingServerStepConnection);
			this.messaging.addMessagingListener(new MessagingListener(this.messaging));
		//create room info interface and add listeners
			this.roomInfoInterface = new RoomInfoInterface(roomDataServerStepConnection, messagingServerStepConnection);
			this.roomInfoInterface.addRoomInfoInterfaceListener(new RoomInfoInterfaceListener(this.roomInfoInterface));
		//add the graphical components
		setLayout(LAYOUT);
		add(this.pnlMain);
		//define some frame properties
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setSize(LENGTH, WIDTH);
	}
	
	final public void showUserInterface()
	{
		setVisible(true);
	}
	
	final public void addClientListener(ClientListener l)
	{
		this.itmChangePassword.addActionListener(l);
		this.itmChangeDisplayName.addActionListener(l);
		this.itmExit.addActionListener(l);
		this.itmFriendsList.addActionListener(l);
		this.itmPestsList.addActionListener(l);
		this.itmPrivateChat.addActionListener(l);
		this.itmCreateRoom.addActionListener(l);
		this.itmModifyRoom.addActionListener(l);
		this.itmJoinRoom.addActionListener(l);
		this.itmDeleteRoom.addActionListener(l);
	}

	final private void loadPnlMain(Component c)
	{
		this.pnlMain.removeAll();
		this.pnlMain.add(c);
		this.validate();
		repaint();
	}
	
	final public void loadPasswordChange()
	{
		loadPnlMain(this.scrollPasswordChange);
	}
	
	final public void loadDisplayNameChange()
	{
		loadPnlMain(this.scrollDisplayNameChange);
	}
	
	final public void loadFriendsList()
	{
		this.messaging.setVisible(true);
		this.messaging.loadFriendsList();
	}
	
	final public void loadPestsList()
	{
		this.messaging.setVisible(true);
		this.messaging.loadPestsList();
	}
	
	final public void loadPrivateChat()
	{
		this.messaging.setVisible(true);
		this.messaging.loadPrivateChat();
	}
	
	final public void loadRoomCreationInterface()
	{
		this.roomInfoInterface.setVisible(true);
		this.roomInfoInterface.loadRoomPropertiesInterface(RoomPropertiesInterface.CREATE_ROOM_MODE);
	}
	
	final public void loadRoomModificationInterface()
	{
		this.roomInfoInterface.setVisible(true);
		this.roomInfoInterface.loadRoomPropertiesInterface(RoomPropertiesInterface.MODIFY_ROOM_MODE);
	}
	
	final public void loadJoinRoomInterface()
	{
		this.roomInfoInterface.setVisible(true);
		this.roomInfoInterface.loadJoinRoom();
	}
	
	/**
	 * This method returns a REFERENCE to the messaging user interface. This method is ONLY to be used to
	 * set up the concurrent connection for the messaging user interface. This method should not be used
	 * for anything else!
	 * 
	 * @return
	 */
	final public Messaging getMessagingUserInterface()
	{
		return this.messaging;
	}
	
	/**
	 * This method returns a REFERENCE to the room info interface. This method is ONLY to be used to
	 * set up the concurrent connection for the room info interface. This method should not be used
	 * for anything else!
	 * 
	 * @return
	 */
	final public RoomInfoInterface getRoomInfoInterface()
	{
		return this.roomInfoInterface;
	}
	
	//DEBUG
	/*
	final public static void main(String[] args)
	{
		Client client = new Client();
		client.addClientListener(new ClientListener(client));
	}//*/
}
