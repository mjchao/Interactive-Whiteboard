package mc.iwclient.messaging;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import mc.iwclient.account.AccountUI;
import mc.iwclient.structs.UserData;
import mc.iwclient.uitemplates.CommandMenu;
import mc.iwclient.uitemplates.OptionMenu;
import mc.iwclient.util.Dialogs;
import mc.iwclient.util.Text;

/**
 * user interface that displays messaging-related lists, which are the friends
 * list, non-friends list, and pests list 
 * 
 * @author mjchao
 *
 */
public class MessagingUserLists extends SearchableGraphicList {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	final private AccountUI m_mainFrame;
	
	/**
	 * displays a list of friends of the user
	 */
	final protected FilterableUserGraphicList pnlFriendDisplay;
	
	/**
	 * displays information about a specific friend
	 */
	final private UserInfoPanel pnlFriendInfo;
	
	/**
	 * displays a list of non-friend users from which the user can search
	 * for a specific user
	 */
	final protected FilterableUserGraphicList pnlNonFriendDisplay;
	
	/**
	 * displays information about a specific non-friend user
	 */
	final private UserInfoPanel pnlNonFriendInfo;
	
	/**
	 * displays a list of pested users from whiht he user can search
	 * for a specific pest
	 */
	final protected FilterableUserGraphicList pnlPestDisplay;
	
	/**
	 * dispays information about a specific pested user
	 */
	final private UserInfoPanel pnlPestInfo;
	
	/**
	 * creates a FriendsList interface
	 * 
	 * @param mainFrame			the main frame of the user interface
	 * 							which is necessary because we may need to access
	 * 							other components of the main frame from this 
	 * 							user interface
	 */
	public MessagingUserLists( AccountUI mainFrame ) {
		
		this.m_mainFrame = mainFrame;
		
		this.pnlFriendDisplay = new FilterableUserGraphicList();
		for ( int i=0 ; i<25 ; i++ ) {
			addFriendToDisplayWithoutRefresh( "abcdefg"+i , "abcdefg"+i );
		}
		addFriendToDisplayWithoutRefresh( "abcdefghijklm" , "opqrstuvwxyz" );
		this.pnlFriendDisplay.refresh();
		
		//by default, show the friends list
		showFriendsList();
		
		this.pnlFriendInfo = new UserInfoPanel();
		this.pnlFriendInfo.addCommand( Text.Messaging.MessagingUserLists.REMOVE_COMMAND );
		this.pnlFriendInfo.addCommand( Text.Messaging.MessagingUserLists.BACK_COMMAND );
		this.pnlFriendInfo.addUserInfoPanelListener( new FriendInfoPanelListener( this.pnlFriendInfo ) );
		
		this.pnlNonFriendDisplay = new FilterableUserGraphicList();
		for ( int i=0; i<25 ; i++ ) {
			addNonFriendToDisplayWithoutRefresh( "abcdefgh"+i , "abcdefgh"+i );
		}
		addNonFriendToDisplayWithoutRefresh( "opqrstuvw" , "opqrstdsfjhdsjfkhdsjfhdsjfhdsajhfdsajfdfsdfuvw" );
		this.pnlNonFriendDisplay.refresh();
		
		this.pnlNonFriendInfo = new UserInfoPanel();
		this.pnlNonFriendInfo.addCommand( Text.Messaging.MessagingUserLists.ADD_FRIEND_COMMAND );
		this.pnlNonFriendInfo.addCommand( Text.Messaging.MessagingUserLists.BACK_COMMAND );
		this.pnlNonFriendInfo.addCommand( Text.Messaging.MessagingUserLists.ADD_PEST_COMMAND );
		this.pnlNonFriendInfo.addUserInfoPanelListener( new NonFriendInfoPanelListener( this.pnlNonFriendInfo ) );
		
		this.pnlPestDisplay = new FilterableUserGraphicList();
		for ( int i=0 ; i<25 ; i++ ) {
			addPestToDisplayWithoutRefresh( "testing_pest"+i , "testing_pest"+i );
		}
		refreshPestDisplay();
		loadInDisplay( this.pnlPestDisplay );
		
		this.pnlPestInfo = new UserInfoPanel();
		this.pnlPestInfo.addCommand( Text.Messaging.MessagingUserLists.REMOVE_COMMAND );
		this.pnlPestInfo.addCommand( Text.Messaging.MessagingUserLists.BACK_COMMAND );
		this.pnlPestInfo.addUserInfoPanelListener( new PestInfoPanelListener( this.pnlPestInfo ) );
	}
	
	@Override
	protected SearchPanel createCustomSearchPanel() {
		MessagingUserListsSearchPanel rtn = new MessagingUserListsSearchPanel();
		rtn.addSearchPanelListener( new MessagingUserListsSearchPanelListener( rtn ) );
		return rtn;
	}

	@Override
	protected MessagingUserListsSearchPanel getSearchPanel() {
		return ( MessagingUserListsSearchPanel ) super.getSearchPanel();
	}
	
	/**
	 * provides the user with options for searching on the friends list user
	 * interface
	 * 
	 * @author mjchao
	 *
	 */
	protected class MessagingUserListsSearchPanel extends SearchPanel {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		/**
		 * allows the user to select to search through his/her friends list
		 * or to search through a list of all possible users
		 */
		private OptionMenu mnuSearchType;
		
		public MessagingUserListsSearchPanel() {

		}
		
		@Override
		public void setupSearchPanel() {
			this.mnuSearchType = new OptionMenu();
			this.mnuSearchType.add( new JLabel( Text.Messaging.MessagingUserLists.SELECT_SEARCH_FILTER , SwingConstants.CENTER ) );
			this.mnuSearchType.addOption( Text.Messaging.MessagingUserLists.OPT_SEARCH_FRIENDS );
			this.mnuSearchType.addOption( Text.Messaging.MessagingUserLists.OPT_SEARCH_NON_FRIENDS );
			this.mnuSearchType.addOption( Text.Messaging.MessagingUserLists.OPT_SEARCH_PESTS );
			add( this.mnuSearchType );
			super.setupSearchPanel();
		}
		
		@Override
		public void addSearchPanelListener( SearchPanelListener l ) {
			super.addSearchPanelListener( l );
			this.mnuSearchType.addActionListener( l );
		}
		
		public void selectFriendsListSearch() {
			this.mnuSearchType.select( Text.Messaging.MessagingUserLists.OPT_SEARCH_FRIENDS );
		}
		
		public void selectNonFriendsListSearch() {
			this.mnuSearchType.select( Text.Messaging.MessagingUserLists.OPT_SEARCH_NON_FRIENDS );
		}
		
		public void selectPestsListSearch() {
			this.mnuSearchType.select( Text.Messaging.MessagingUserLists.OPT_SEARCH_PESTS );
		}
		
		/**
		 * @return			the type of search: searching through friends or
		 * 					searching through non-friends
		 */
		public String getSearchType() {
			return this.mnuSearchType.getSelectedString();
		}
	}
	
	
	/**
	 * @return				the search list that is currently selected by the user in the search panel
	 */
	public FilterableUserGraphicList getSelectedUserList() {
		String searchType = getSearchPanel().getSearchType();
		if ( searchType.equals( Text.Messaging.MessagingUserLists.OPT_SEARCH_FRIENDS ) ) {
			return this.pnlFriendDisplay;
		}
		else if ( searchType.equals( Text.Messaging.MessagingUserLists.OPT_SEARCH_NON_FRIENDS ) ) {
			return this.pnlNonFriendDisplay;
		}
		else if ( searchType.equals( Text.Messaging.MessagingUserLists.OPT_SEARCH_PESTS ) ){
			return this.pnlPestDisplay;
		}
		else {
			throw new RuntimeException();
		}
	}
	
	protected class MessagingUserListsSearchPanelListener extends SearchPanelListener {

		public MessagingUserListsSearchPanelListener( SearchPanel gui ) {
			super( gui );
		}
		
		@Override
		public void actionPerformed( ActionEvent e ) {
			String command = e.getActionCommand();
			if ( command.equals( Text.Messaging.MessagingUserLists.OPT_SEARCH_FRIENDS ) ) {
				showFriendsList();
			}
			else if ( command.equals( Text.Messaging.MessagingUserLists.OPT_SEARCH_NON_FRIENDS ) ) {
				showNonFriendsList();
			}
			else if ( command.equals( Text.Messaging.MessagingUserLists.OPT_SEARCH_PESTS ) ) {
				showPestsList();
			}
		}
		
	}
	
	@Override
	protected void handleSearchCommand( SearchPanel gui ) {
		String searchFilter = gui.getSearchFilter().trim();
		
		//we only search if the user provided a large enough search filter
		//otherwise, there could be too many results
		FilterableUserGraphicList currentList = MessagingUserLists.this.getSelectedUserList();
		if ( searchFilter.length() >= MINIMUM_SEARCH_FILTER_SIZE ) {
			currentList.filter( gui.getSearchFilter() );
			resizeMainFrame();
		}
		else {
			Dialogs.displayErrorMessage( Text.Messaging.SearchableGraphicList.INVALID_SEARCH_FILTER );
		}
	}
	
	@Override
	protected void handleCancelSearchCommand( SearchPanel gui ) {
		FilterableUserGraphicList currentList = MessagingUserLists.this.getSelectedUserList();
		currentList.removeFilter();
		resizeMainFrame();
	}
	
	/**
	 * resizes the main frame to completely contain all components, if possible
	 * UPDATE: No longer does anything, as we do not want to annoy the user
	 * if s/he prefers his/her own frame size
	 */
	protected void resizeMainFrame() {
		//this.m_mainFrame.pack();
		
		//TODO hacky, but seems required. Otherwise, scrollbars disappear!
		//this.m_mainFrame.setSize( this.m_mainFrame.getWidth() - 1 , this.m_mainFrame.getHeight() - 1 );
		//this.m_mainFrame.setSize( this.m_mainFrame.getWidth() + 1 , this.m_mainFrame.getHeight() + 1 );
	}
	
	/**
	 * loads the given graphical component in the main display panel of this 
	 * user interface
	 * 
	 * @param c				the graphical component to display
	 */
	@Override
	protected void loadInDisplay( Component c ) {
		super.loadInDisplay( c );
		resizeMainFrame();
	}

	protected void showFriendsList() {
		refreshFriendDisplay();
		loadInDisplay( this.pnlFriendDisplay );
		this.m_mainFrame.setTitle( Text.Messaging.FRIENDS_LIST_UI_TITLE );
	}
	
	/**
	 * updates this user interface to reflect as if the user were searching the 
	 * friends list
	 */
	public void reflectSearchFriendsList() {
		getSearchPanel().selectFriendsListSearch(); 
	}
	
	protected void showNonFriendsList() {
		refreshNonFriendDisplay();
		loadInDisplay( this.pnlNonFriendDisplay );
		this.m_mainFrame.setTitle( Text.Messaging.NON_FRIEND_LIST_UI_TITLE );
	}
	
	protected void showPestsList() {
		refreshPestDisplay();
		loadInDisplay( this.pnlPestDisplay );
		this.m_mainFrame.setTitle( Text.Messaging.PESTS_LIST_UI_TITLE );
	}
	
	/**
	 * updates this user interface to reflect as if the user were searching the
	 * pests list
	 */
	public void reflectSearchPestsList() {
		getSearchPanel().selectPestsListSearch();
	}
	
	
	
	/**
	 * adds the friend with the given username and display name to the friend list
	 * but does not update the user interface to reflect the change
	 * 
	 * @param username				username of the friend
	 * @param displayName			display name of the friend
	 */
	public void addFriendToDisplayWithoutRefresh( String username , String displayName ) {
		FriendPanel friendToAdd = new FriendPanel( username , displayName );
		friendToAdd.addUserGraphicCommandPanelListener( new FriendPanelListener( friendToAdd ) );
		this.pnlFriendDisplay.addElementWithoutRefresh( username , friendToAdd );
	}
	
	/**
	 * adds the friend with the given username and display name to the friends list
	 * 
	 * @param username				username of the friend
	 * @param displayName			display name of the friend
	 */
	public void addFriendToDisplay( String username , String displayName ) {
		addFriendToDisplayWithoutRefresh( username , displayName );
		refreshFriendDisplay();
	}
	
	/**
	 * removes a friend from the friends list
	 * 
	 * @param username			username of the friend to remove
	 */
	public void removeFriendFromDisplay( String username ) {
		this.pnlFriendDisplay.remove( username );
		refreshFriendDisplay();
	}
	
	/**
	 * updates the graphics of the friends list after the graphical components
	 * have changed
	 */
	public void refreshFriendDisplay() {
		this.pnlFriendDisplay.sort( STRING_COMPARATOR );
		resizeMainFrame();
	}
	
	/**
	 * adds a non-friend to the non-friends list without refreshing the
	 * graphics
	 * 
	 * @param username				username of the non-friend
	 * @param displayName			display name of the non-friend
	 */
	public void addNonFriendToDisplayWithoutRefresh( String username , String displayName ) {
		NonFriendPanel userToAdd = new NonFriendPanel( username , displayName );
		userToAdd.addUserGraphicCommandPanelListener( new NonFriendPanelListener( userToAdd ) );
		this.pnlNonFriendDisplay.addElementWithoutRefresh( username , userToAdd );
	}
	
	/**
	 * adds a non-friend to the non-friends list and refreshes the graphics
	 * 
	 * @param username				username of the non-friend
	 * @param displayName			display name of the non-friend
	 */
	public void addNonFriendToDisplay( String username , String displayName ) {
		addNonFriendToDisplayWithoutRefresh( username , displayName );
		refreshNonFriendDisplay();
	}
	
	/**
	 * removes a non-friend from the non-friends list and refreshes the graphics
	 * 
	 * @param username			username of the non-friend to remove
	 */
	public void removeNonFriendFromDisplay( String username ) {
		this.pnlNonFriendDisplay.remove( username );
		refreshNonFriendDisplay();
	}
	
	/**
	 * updates the graphics of the non-friends list after the graphical components
	 * have changed
	 */
	public void refreshNonFriendDisplay() {
		this.pnlNonFriendDisplay.sort( SearchableGraphicList.STRING_COMPARATOR );
	}
	
	/**
	 * adds the user with the specified username and display name to the user's
	 * pest list, but does not update the graphics list to reflect
	 * this addition. the reason we provide a without-refresh option is for
	 * when the pest list loads a large quantity of users and it would be 
	 * very slow to refresh the graphics list after each user is added
	 * 
	 * @param username				the username of a user
	 * @param displayName			the display name of a user
	 */
	public void addPestToDisplayWithoutRefresh( String username , String displayName ) {
		PestPanel pestToAdd = new PestPanel( username , displayName );
		pestToAdd.addUserGraphicCommandPanelListener( new PestPanelListener( pestToAdd ) );
		this.pnlPestDisplay.addElementWithoutRefresh( username , pestToAdd );
	}
	
	/**
	 * adds the user with the specified username and display name ot the user's
	 * pest list, and updates the graphics list to refelct this addition.
	 * 
	 * @param username				the username of a user
	 * @param displayName			the display name of a user
	 */
	public void addPestToDisplay( String username , String displayName ) {
		addPestToDisplayWithoutRefresh( username , displayName );
		refreshPestDisplay();
	}
	
	/**
	 * removes a pest from the pests list and refreshes the graphics
	 * 
	 * @param username			username of the pest to remove
	 */
	public void removePestFromDisplay( String username ) {
		this.pnlPestDisplay.remove( username );
		refreshPestDisplay();
	}
	
	/**
	 * updates this graphics of this list to reflect any additions or deletions
	 * since the last graphics refresh
	 */
	protected void refreshPestDisplay() {
		this.pnlPestDisplay.sort( SearchableGraphicList.STRING_COMPARATOR );
	}
	
	/**
	 * displays the display name of one non-friend and commands associated with that
	 * user (e.g. an "Add Friend" command for adding the user as a friend)
	 * 
	 * @author mjchao
	 *
	 */
	private class NonFriendPanel extends UserGraphicCommandPanel {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public NonFriendPanel( String username , String displayName ) {
			super( username , displayName );
		}

		@Override
		public void setupCommandMenu( CommandMenu commandsMenu ) {
			commandsMenu.addCommand( Text.Messaging.MessagingUserLists.MORE_INFO_COMMAND );
			commandsMenu.addCommand( Text.Messaging.MessagingUserLists.ADD_FRIEND_COMMAND );
			commandsMenu.addCommand( Text.Messaging.MessagingUserLists.ADD_PEST_COMMAND );
		}
	}
	
	/**
	 * displays the information about one non-friend in the non-friend information
	 * panel
	 * 
	 * @param nonFriend
	 */
	public void displayNonFriendInfo( UserData nonFriend ) {
		this.pnlNonFriendInfo.loadUser( nonFriend );
		loadInDisplay( this.pnlNonFriendInfo );
	}
	
	/**
	 * retrieves information about a non-friend and then displays all that
	 * information in the non-friend information panel
	 * 
	 * @param nonFriendUsername			username of a non-friend
	 */
	protected void retrieveAndDisplayNonFriendInfo( String nonFriendUsername ) {
		UserData nonFriendData = new UserData();
		nonFriendData.m_username = nonFriendUsername;
		
		//TODO retrieve rest of user information from networking
		nonFriendData.m_displayName = nonFriendUsername;
		displayNonFriendInfo( nonFriendData );
	}
	
	private class NonFriendInfoPanelListener implements ActionListener {

		final private UserInfoPanel m_gui;
		
		public NonFriendInfoPanelListener( UserInfoPanel gui ) {
			this.m_gui = gui;
		}
		
		@Override
		public void actionPerformed( ActionEvent e ) {
			String command = e.getActionCommand();
			if ( command.equals( Text.Messaging.MessagingUserLists.ADD_FRIEND_COMMAND ) ) {
				UserData currentDisplayedUser = this.m_gui.getDisplayedUser();
				
				//if there is currently a user displayed, then try to add the user
				//as a friend
				if ( currentDisplayedUser != null ) {
					processAddFriend( currentDisplayedUser.m_username , currentDisplayedUser.m_displayName );
				}
				
				//after the user adds the friend, we want to take him/her back
				//to the list s/he was just viewing
				loadInDisplay( getSelectedUserList() );
			}
			else if ( command.equals( Text.Messaging.MessagingUserLists.BACK_COMMAND ) ) {
				loadInDisplay( getSelectedUserList() );
			}
			else if ( command.equals( Text.Messaging.MessagingUserLists.ADD_PEST_COMMAND ) ) {
				UserData currentDisplayedUser = this.m_gui.getDisplayedUser();
				
				//if there is currently a user displayed, then try to add the user
				//as a pest
				if ( currentDisplayedUser != null ) {
					processAddPest( currentDisplayedUser.m_username , currentDisplayedUser.m_displayName );
				}
				loadInDisplay( getSelectedUserList() );
			}
		}
		
	}
	
	private class NonFriendPanelListener extends UserGraphicCommandPanelListener {
		
		public NonFriendPanelListener( NonFriendPanel gui ) {
			super( gui );
		}
		
		@Override
		public void actionPerformed( ActionEvent e ) {
			String command = e.getActionCommand();
			if ( command.equals( Text.Messaging.MessagingUserLists.MORE_INFO_COMMAND ) ) {
				retrieveAndDisplayNonFriendInfo( super.m_gui.getUsername() );
			}
			else if ( command.equals( Text.Messaging.MessagingUserLists.ADD_FRIEND_COMMAND ) ) {
				processAddFriend( super.m_gui.getUsername() , super.m_gui.getDisplayName() );
			}
			else if ( command.equals( Text.Messaging.MessagingUserLists.ADD_PEST_COMMAND ) ) {
				processAddPest( super.m_gui.getUsername() , super.m_gui.getDisplayName() );
			}
		}
	}

	/**
	 * displays the display name of a friend and
	 * provides commands the user can take (e.g. send private message)
	 * 
	 * @author mjchao
	 *
	 */
	private class FriendPanel extends UserGraphicCommandPanel {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		private JLabel lblIsOnline;
		
		public FriendPanel( String friendUsername , String friendDisplayName ) {
			super( friendUsername , friendDisplayName );
			setOnline( true );
		}
		
		public void setOnline( boolean isOnline ) {
			if ( isOnline ) {
				this.lblIsOnline.setText( "\u2713" );
				this.lblIsOnline.setForeground( Color.YELLOW );
			}
			else {
				this.lblIsOnline.setText( "" );
			}
		}
		
		@Override
		protected JPanel createUserInfoPanel() {
			JPanel pnlUserInfo = super.createUserInfoPanel();
			this.lblIsOnline = new JLabel( "" );
			pnlUserInfo.add( this.lblIsOnline );
			return pnlUserInfo;
		}
		
		@Override
		public void setupCommandMenu( CommandMenu commandsMenu ) {
			commandsMenu.addCommand( Text.Messaging.MessagingUserLists.MORE_INFO_COMMAND );
			commandsMenu.addCommand( Text.Messaging.MessagingUserLists.MESSAGE_COMMAND );
			commandsMenu.addCommand( Text.Messaging.MessagingUserLists.REMOVE_COMMAND );
		}
	}
	
	
	public void displayFriendInfo( UserData friend ) {
		this.pnlFriendInfo.loadUser( friend );
		loadInDisplay( this.pnlFriendInfo );
	}
	
	/**
	 * loads the information of one friend in the friend information
	 * panel
	 * 
	 * @param username			username of the friend
	 */
	public void retrieveAndDisplayFriendInfo( String username ) {
		UserData friendData = new UserData();
		friendData.m_username = username;
		
		//TODO retrieve rest of user data from networking
		friendData.m_displayName = username;
		displayFriendInfo( friendData );
	}
	
	private class FriendPanelListener extends UserGraphicCommandPanelListener {
		
		public FriendPanelListener( FriendPanel gui ) {
			super( gui );
		}
		
		@Override
		public void actionPerformed( ActionEvent e ) {
			String command = e.getActionCommand();
			if ( command.equals( Text.Messaging.MessagingUserLists.MORE_INFO_COMMAND ) ) {
				retrieveAndDisplayFriendInfo( super.m_gui.getUsername() );
			}
			else if ( command.equals( Text.Messaging.MessagingUserLists.MESSAGE_COMMAND ) ) {
				loadInDisplay( getSelectedUserList() );
			}
			else if ( command.equals( Text.Messaging.MessagingUserLists.REMOVE_COMMAND ) ) {
				processRemoveFriend( super.m_gui.getUsername() , super.m_gui.getDisplayName() );
			}
		}
	}
	
	/**
	 * processes the user's request to add a non-user as a friend
	 * 
	 * @param username				username of a user to add as a friend
	 * @param displayName			display name of the user to add as a friend
	 */
	protected void processAddFriend( String username , String displayName ) {
		removeNonFriendFromDisplay( username );
		addFriendToDisplay( username , displayName );
		//TODO tell server to add friend
	}
	
	/**
	 * processes the user's request to remove a friend from the friends list
	 * 
	 * @param username				username of the friend to remove	
	 * @param displayName			display name of the friend to remove
	 */
	protected void processRemoveFriend( String username , String displayName ) {
		removeFriendFromDisplay( username );
		addNonFriendToDisplay( username , displayName );
		//TODO notify server of friend removal
	}
	
	private class FriendInfoPanelListener implements ActionListener {

		final private UserInfoPanel m_gui;
		
		public FriendInfoPanelListener( UserInfoPanel gui ) {
			this.m_gui = gui;
		}

		@Override
		public void actionPerformed( ActionEvent e ) {
			String command = e.getActionCommand();
			if ( command.equals( Text.Messaging.MessagingUserLists.REMOVE_COMMAND ) ) {
				UserData currentDisplayedUser = this.m_gui.getDisplayedUser();
				
				//if there is currently a displayed user, then try to 
				//add that user as a friend
				if ( currentDisplayedUser != null ) {
					processRemoveFriend( currentDisplayedUser.m_username , currentDisplayedUser.m_displayName );
				}
				loadInDisplay( getSelectedUserList() );
			}
			else if ( command.equals( Text.Messaging.MessagingUserLists.BACK_COMMAND ) ) {
				loadInDisplay( getSelectedUserList() );
			}
		}
	}
	
	/**
	 * user interface that displays a list of pests
	 * 
	 * @author mjchao
	 *
	 */
	private class PestPanel extends UserGraphicCommandPanel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public PestPanel( String username , String displayName ) {
			super( username , displayName );
		}

		@Override
		public void setupCommandMenu( CommandMenu commandsMenu ) {
			commandsMenu.addCommand( Text.Messaging.MessagingUserLists.MORE_INFO_COMMAND );
			commandsMenu.addCommand( Text.Messaging.MessagingUserLists.REMOVE_COMMAND );
		}
	}
	
	private class PestPanelListener extends UserGraphicCommandPanelListener {

		public PestPanelListener( UserGraphicCommandPanel gui ) {
			super( gui );
		}

		@Override
		public void actionPerformed( ActionEvent e ) {
			String command = e.getActionCommand();
			if ( command.equals( Text.Messaging.MessagingUserLists.MORE_INFO_COMMAND ) ) {
				retrieveAndDisplayPestInfo( super.m_gui.getUsername() );
			}
			else if ( command.equals( Text.Messaging.MessagingUserLists.REMOVE_COMMAND ) ) {
				processRemovePest( super.m_gui.getUsername() , super.m_gui.getDisplayName() );
			}
		}	
	}
	
	/**
	 * displays inforamtion about a pest in the pest information panel
	 * 
	 * @param pestInfo				the pest information		
	 */
	protected void displayPestInfo( UserData pestInfo ) {
		this.pnlPestInfo.loadUser( pestInfo );
		loadInDisplay( this.pnlPestInfo );
	}
	
	/**
	 * retreives information about a pest and displays all that information
	 * in the pest information panel
	 * 
	 * @param username			username of a pest
	 */
	protected void retrieveAndDisplayPestInfo( String username ) {
		UserData pestInfo = new UserData();
		pestInfo.m_username = username;
		
		//TODO retrieve rest of pest information from networking
		pestInfo.m_displayName = username;
		displayPestInfo( pestInfo );
	}
	
	/**
	 * processes the user's request to add a user to the pest list
	 * 
	 * @param username				username of the pest to add
	 * @param displayName			display name of the pest to add
	 */
	protected void processAddPest( String username , String displayName ) {
		removeNonFriendFromDisplay( username );
		addPestToDisplay( username , displayName );
		//TODO tell server to add pest
	}
	
	protected void processRemovePest( String username , String displayName ) {
		removePestFromDisplay( username );
		addNonFriendToDisplay( username , displayName );
		//TODO tells serve to remove pest
	}
	
	private class PestInfoPanelListener implements ActionListener {
		
		final private UserInfoPanel m_gui;
		
		public PestInfoPanelListener( UserInfoPanel gui ) {
			this.m_gui = gui;
		}

		@Override
		public void actionPerformed( ActionEvent e ) {
			String command = e.getActionCommand();
			if ( command.equals( Text.Messaging.MessagingUserLists.REMOVE_COMMAND ) ) {
				UserData currentUser = this.m_gui.getDisplayedUser();
				
				//if there is a currently displayed pested user, then
				//try to remove that user from the pest list
				if ( currentUser != null ) {
					processRemovePest( currentUser.m_username , currentUser.m_displayName );
				}
				loadInDisplay( getSelectedUserList() );
			}
			else if ( command.equals( Text.Messaging.MessagingUserLists.BACK_COMMAND ) ) {
				loadInDisplay( getSelectedUserList() );
			}
		}
	}
}
