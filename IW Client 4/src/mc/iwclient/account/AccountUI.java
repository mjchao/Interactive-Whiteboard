package mc.iwclient.account;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import mc.iwclient.login.LoginUI;
import mc.iwclient.messaging.MessagingUserLists;
import mc.iwclient.messaging.PrivateMessagingPanel;
import mc.iwclient.room.RoomSelectionPanel;
import mc.iwclient.util.Dialogs;
import mc.iwclient.util.Text;
import mc.iwclient.util.Text.Messaging;
import mc.iwclient.util.Text.Room;

public class AccountUI extends JFrame {

	final public static int DEFAULT_WIDTH = 500;
	final public static int DEFAULT_HEIGHT = 500;
	
	private static AccountUI ui;
	final public static void show( LoginUI login ) {
		login.setVisible( false );
		if ( ui == null ) {
			ui = new AccountUI( login );
		}
		ui.setSize( DEFAULT_WIDTH , DEFAULT_HEIGHT );
		ui.addAccountUIListener( new AccountUIListener( ui ) );
	}
	
	/*
	final public static void main( String[] args ) {
		LoginUI login = new LoginUI();
		login.setVisible( false );
		AccountUI i = new AccountUI( login );
		i.setSize( DEFAULT_WIDTH , DEFAULT_HEIGHT );
		i.addAccountUIListener( new AccountUIListener( i ) );
	}//*/
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * the login interface that gets displayed on logout
	 */
	final private LoginUI m_loginInterface;
	
	final private JMenuBar menuBar;
		final private JMenu mnuAccount;
			final private JMenuItem itmNews;
			final private JMenuItem itmAccountInfo;
			final private JMenuItem itmUserInfo;
			final private JMenuItem itmLogout;
		
		final private JMenu mnuMessaging;
			final private JMenuItem itmFriendsList;
			final private JMenuItem itmPestsList;
			final private JMenuItem itmPM;
			
		final private JMenu mnuCollaboration;
			final private JMenuItem itmBrowseRooms;
	
	/**
	 * user interface that displays news about the interactive whiteboard
	 */
	final private NewsPanel pnlNews;
	
	/**
	 * user interface for the user to modify his/her account information
	 * (e.g. password)
	 */
	final private AccountInfoPanel pnlAccountInfo;
	
	/**
	 * user interface for the user to modify his/her person information
	 * (e.g. display name)
	 */
	final private UserInfoPanel pnlUserInfo;
	
	/**
	 * messaging user lists interface for the user
	 */
	final private MessagingUserLists pnlMessagingLists;
	
	/**
	 * private messaging user interface for the user
	 */
	final private PrivateMessagingPanel pnlPM;
	
	final private RoomSelectionPanel pnlRoomSelection;
			
	/**
	 * main panel on which the user performs the various actions associated
	 * with the interactive whiteboard
	 */
	final private JPanel pnlMain;
	
	public AccountUI( LoginUI loginInterface ) {
		this.m_loginInterface = loginInterface;
		
		this.menuBar = new JMenuBar();	
			this.mnuAccount = new JMenu( Text.Account.ACCOUNT_MENU_TITLE );
				this.itmNews = new JMenuItem( Text.Account.VIEW_NEWS_COMMAND );
				this.mnuAccount.add( this.itmNews );
				
				this.itmAccountInfo = new JMenuItem( Text.Account.CHANGE_ACCOUNT_INFO_COMMAND );
				this.mnuAccount.add( this.itmAccountInfo );
				
				this.itmUserInfo = new JMenuItem( Text.Account.CHANGE_USER_INFO_COMMAND );
				this.mnuAccount.add( this.itmUserInfo );
				
				this.itmLogout = new JMenuItem( Text.Account.LOGOUT_COMMAND );
				this.mnuAccount.add( this.itmLogout );
			this.menuBar.add( this.mnuAccount );
			
			this.mnuMessaging = new JMenu( Messaging.MESSAGING_MENU_TITLE );
				this.itmFriendsList = new JMenuItem( Messaging.VIEW_FRIENDS_LIST_COMMAND );
				this.mnuMessaging.add( this.itmFriendsList );
				
				this.itmPestsList = new JMenuItem( Messaging.VIEW_PESTS_LIST_COMMAND );
				this.mnuMessaging.add( this.itmPestsList );
				
				this.itmPM = new JMenuItem( Messaging.VIEW_PM_COMMAND );
				this.mnuMessaging.add( this.itmPM );
			this.menuBar.add( this.mnuMessaging );
			
			this.mnuCollaboration = new JMenu( Room.COLLABORATION_MENU_TITLE );
				this.itmBrowseRooms = new JMenuItem( Room.BROWSE_ROOMS_COMMAND );
				this.mnuCollaboration.add( this.itmBrowseRooms );
			this.menuBar.add( this.mnuCollaboration );
		this.setJMenuBar( this.menuBar );
		
		this.pnlNews = new NewsPanel();
		this.pnlNews.addNewsPanelListener( new NewsPanelListener() );
		
		this.pnlAccountInfo = new AccountInfoPanel();
		this.pnlAccountInfo.addAccountInfoPanelListener( new AccountInfoPanelListener( this.pnlAccountInfo ) );
		
		this.pnlUserInfo = new UserInfoPanel();
		this.pnlUserInfo.addUserInfoPanelListener( new UserInfoPanelListener( this.pnlUserInfo ) );
		
		this.pnlMessagingLists = new MessagingUserLists( this );
		
		this.pnlPM = new PrivateMessagingPanel( this );
		
		this.pnlRoomSelection = new RoomSelectionPanel( this );
		
		setLayout( new BorderLayout() );
		this.pnlMain = new JPanel( new GridLayout( 1 , 1 ) );
		add( this.pnlMain , BorderLayout.CENTER );
		
		setTitle( Text.Account.ACCOUNT_UI_TITLE );
		setVisible( true );
		setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );
		pack();
		
		//by default, as soon as the user logs in, s/he should be taken
		//to the news page
		loadNewsInterface();
	}
	
	@Override
	public void setTitle( String title ) {
		super.setTitle( title );
	}
	
	public void addAccountUIListener( AccountUIListener l ) {
		this.itmNews.addActionListener( l );
		this.itmAccountInfo.addActionListener( l );
		this.itmUserInfo.addActionListener( l );
		this.itmLogout.addActionListener( l );
		this.itmFriendsList.addActionListener( l );
		this.itmPestsList.addActionListener( l );
		this.itmPM.addActionListener( l );
		this.itmBrowseRooms.addActionListener( l );
	}
	
	/**
	 * loads a user interface onto the main panel
	 * 
	 * @param c			the user interface to load in the main panel
	 */
	private void loadInterface( Component c ) {
		this.pnlMain.removeAll();
		this.pnlMain.add( c );
		
		//refresh graphics because some components have already been removed
		//and some new components have been added
		this.pnlMain.revalidate();
		this.pnlMain.repaint();
		//this.pack();
	}
	
	/**
	 * displays the news interface that tells the user about any new
	 * events related to the interactive whiteboard
	 */
	public void loadNewsInterface() {
		loadInterface( this.pnlNews );
		setTitle( Text.Account.NEWS_UI_TITLE );
	}
	
	/**
	 * displays the interface that allows the user to modify his/her account
	 * information (e.g. password)
	 */
	public void loadAccountInfoInterface() {
		loadInterface( this.pnlAccountInfo );
		setTitle( Text.Account.CHANGE_ACCOUNT_INFO_UI_TITLE );
	}
	
	/**
	 * displays the interface that allows the user to modify his/her personal
	 * information (e.g. display name)
	 */
	public void loadUserInfoInterface() {
		loadInterface( this.pnlUserInfo );
		setTitle( Text.Account.CHANGE_USER_INFO_UI_TITLE );
	}
	
	/**
	 * asks the user to confirm the logout request and logs out if the user
	 * does confirm
	 */
	public void confirmLogout() {
		int confirm = Dialogs.displayConfirmMessage( Text.Account.Logout.CONFIRM_LOGOUT_MESSAGE );
		if ( confirm == Dialogs.YES_OPTION ) {
			logout();
		}
	}
	
	/**
	 * logs out of the interactive whiteboard client and returns to the 
	 * login interface
	 */
	private void logout() {
		this.setVisible( false );
		this.m_loginInterface.setVisible( true );
	}
	
	/**
	 * displays the interface that allows the user to modify his/her friends
	 * (people s/he would like to see on the private messaging interface)
	 */
	public void loadFriendsListInterface() {
		this.pnlMessagingLists.reflectSearchFriendsList();
		loadInterface( this.pnlMessagingLists );
		setTitle( Text.Messaging.FRIENDS_LIST_UI_TITLE );
	}
	
	/**
	 * displays the interface that allows the user to modify his/her pests
	 * (people s/he has blocked from private messaging)
	 */
	public void loadPestsListInterface() {
		this.pnlMessagingLists.reflectSearchPestsList();
		loadInterface( this.pnlMessagingLists );
		setTitle( Text.Messaging.PESTS_LIST_UI_TITLE );
	}
	
	/**
	 * displays the interface that allows the user to private message
	 * his/her friends
	 */
	public void loadPrivateMessagingInterface() {
		loadInterface( this.pnlPM );
		setTitle( Text.Messaging.PM_UI_TITLE );
	}
	
	/**
	 * displays the interface that allows the user to select rooms to join
	 * for collaboration
	 */
	public void loadRoomSelectionInterface() {
		loadInterface( this.pnlRoomSelection );
		setTitle( Text.Room.ROOM_SELECTION_UI_TITLE );
	}
}
