package mc.iwclient.messaging;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import mc.iwclient.account.AccountUI;
import mc.iwclient.uitemplates.FormattedTextArea;
import mc.iwclient.uitemplates.PopupMenu;
import mc.iwclient.util.Text;

/**
 * user interface for sending and receiving private messages
 * 
 * @author mjchao
 *
 */
//TODO Tabbed pane with draggable JWindow conversations
public class PrivateMessagingPanel extends JPanel {

	final protected AccountUI m_mainFrame;
	
	final private MessagingList pnlFriendsList;
	
	final private ConversationDisplayPanel pnlConversations;
	
	/**
	 * stores the divider location the user wants for his/her private conversations
	 */
	protected int m_conversationDividerLocation = 0;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * creates a user interface on which the user can select friends to whom to
	 * send private messages, send those friends private messages, and manage
	 * private these private conversations
	 * 
	 * @param mainFrame
	 */
	public PrivateMessagingPanel( AccountUI mainFrame ) {
		this.m_mainFrame = mainFrame;
		
		setLayout( new BorderLayout() );
		
		this.pnlConversations = new ConversationDisplayPanel();
		add( this.pnlConversations , BorderLayout.CENTER );
		
		this.pnlFriendsList = new MessagingList();
		add( this.pnlFriendsList , BorderLayout.EAST );
	}
	
	/**
	 * loads the given private conversation in the maindisplay panel of this
	 * user interface
	 * 
	 * @param conversation			the private conversation to load
	 */
	public void loadConversation( ConversationPanel conversation ) {
		conversation.setDividerLocation( this.m_conversationDividerLocation );
		this.pnlConversations.loadConversation( conversation );
		conversation.requestSendTextFieldFocus();
	}
	
	/**
	 * resizes the main frame to completely contain all components, if possible.
	 * UPDATE: we choose to no longer resize the main frame, as it might
	 * annoy the user
	 */
	protected void resizeMainFrame() {
		this.m_mainFrame.pack();
		
		//TODO hacky, but seems required. Otherwise, scrollbars disappear!
		this.m_mainFrame.setSize( this.m_mainFrame.getWidth() - 1 , this.m_mainFrame.getHeight() - 1 );
		this.m_mainFrame.setSize( this.m_mainFrame.getWidth() + 1 , this.m_mainFrame.getHeight() + 1 );
	}
	
	/**
	 * provides a friends list from which the user can search for a specific friend
	 * and allows the user to send private messages to that user
	 * 
	 * @author mjchao
	 *
	 */
	private class MessagingList extends SearchableGraphicList {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * the list of friends
		 */
		final protected FilterableUserGraphicList pnlFriends;
		
		/**
		 * creates a default searchable friends list
		 */
		public MessagingList() {
			this.pnlFriends = new FilterableUserGraphicList();
			
			for ( int i=0 ; i<25 ; i++ ) {
				FriendGraphicPopupPanel friend = new FriendGraphicPopupPanel( "friend"+i , "friend"+i );
				MessagingPopupMenu popup = new MessagingPopupMenu( friend );
				popup.addMessagingPopupMenuListener( new MessagingPopupMenuListener( popup ) );
				friend.setPopupMenu( popup );
				ConversationPanel newConversation = new ConversationPanel( friend , "friend"+i , "friend"+i );
				newConversation.addConversationPanelListener( new ConversationPanelListener( newConversation ) );
				friend.setConversation( newConversation );
				friend.addFriendGraphicPopupPanelListener( new FriendGraphicPopupPanelListener( friend ) );
				this.pnlFriends.addElement( "friend" + i , friend );
			}
			super.loadInDisplay( this.pnlFriends );
		}
		
		@Override
		protected SearchPanel createCustomSearchPanel() {
			MessagingListSearchPanel rtn = new MessagingListSearchPanel();
			rtn.addMessagingListSearchPanelListener( new MessagingListSearchPanelListener( rtn ) );
			return rtn;
		}
		
		@Override
		protected void handleSearchCommand( SearchPanel gui ) {
			MessagingList.this.pnlFriends.filter( gui.getSearchFilter() );
		}

		@Override
		protected void handleCancelSearchCommand( SearchPanel gui ) {
			MessagingList.this.pnlFriends.removeFilter();
		}
		
		/**
		 * the search panel for the searchable friends list, that allows
		 * the user to filter his/her friends list
		 * 
		 * @author mjchao
		 *
		 */
		private class MessagingListSearchPanel extends SearchPanel {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			/**
			 * creates a default search panel for the private messaging
			 * friends list
			 */
			public MessagingListSearchPanel() {
				super.pnlSearchFilter.remove( super.cmdSearch );
				super.pnlSearchFilter.remove( super.cmdCancel );
				this.txtSearchFilter.setText( Text.Messaging.PrivateMessagingPanel.SEARCH_BOX_DESCRIPTION );
				revalidate();
				repaint();
			}
			
			public void addMessagingListSearchPanelListener( MessagingListSearchPanelListener l ) {
				super.txtSearchFilter.addKeyListener( l );
				super.txtSearchFilter.addFocusListener( l );
			}
			
			/**
			 * checks if the description "[search for friend]" should appear
			 * in the search field or not (it appears if the user has not
			 * entered any search terms)
			 */
			public void displaySearchFieldDescriptionIfNecessary() {
				if ( this.txtSearchFilter.getText().equals( "" ) ) {
					this.txtSearchFilter.setText( Text.Messaging.PrivateMessagingPanel.SEARCH_BOX_DESCRIPTION );
				}
			}
			
			/**
			 * checks if the description "[search for friend]" is in the search
			 * field or not, and if it is, then the search field is cleared.
			 */
			public void clearSearchFieldDescriptionIfNecessary() {
				if ( this.txtSearchFilter.getText().equals( Text.Messaging.PrivateMessagingPanel.SEARCH_BOX_DESCRIPTION ) ) {
					this.txtSearchFilter.setText( "" );
				}
			}
		}
		
		private class MessagingListSearchPanelListener implements KeyListener, FocusListener {

			final private MessagingListSearchPanel m_gui;
			
			public MessagingListSearchPanelListener( MessagingListSearchPanel gui ) {
				this.m_gui = gui;
			}
			
			@Override
			public void keyPressed( KeyEvent e ) {
				//ignore
			}

			@Override
			public void keyReleased( KeyEvent e ) {
				handleSearchCommand( this.m_gui );
			}

			@Override
			public void keyTyped( KeyEvent e ) {
				//ignore
			}

			@Override
			public void focusGained( FocusEvent e ) {
				this.m_gui.clearSearchFieldDescriptionIfNecessary();
			}

			@Override
			public void focusLost(FocusEvent arg0) {
				this.m_gui.displaySearchFieldDescriptionIfNecessary();
			}
			
		}
	}
	
	/**
	 * provides a popup menu of messaging-related commands for one specific friend
	 * 
	 * @author mjchao
	 *
	 */
	private class FriendGraphicPopupPanel extends UserGraphicPopupPanel {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private ConversationPanel m_conversation;
		
		public FriendGraphicPopupPanel( String username , String displayName ) {
			super( username , displayName );
			getUsernameLabel().setOpaque( true );
		}

		public void addFriendGraphicPopupPanelListener( FriendGraphicPopupPanelListener l ) {
			getUsernameLabel().addMouseListener( l );
		}
		
		/**
		 * sets the private conversation associated with the specific friend
		 * 
		 * @param conversation
		 */
		public void setConversation( ConversationPanel conversation ) {
			this.m_conversation = conversation;
		}
		
		/**
		 * @return			the private conversation associated with the
		 * 					specific friend
		 */
		public ConversationPanel getConversation() {
			return this.m_conversation;
		}
		
		@Override
		public void setPopupMenu( JPopupMenu menu ) {
			if ( menu instanceof MessagingPopupMenu ) {
				super.setPopupMenu( menu );
			}
			else {
				
				//error
				throw new RuntimeException();
			}
		}
		
		@Override
		public MessagingPopupMenu getPopupMenu() {
			return ( MessagingPopupMenu ) super.getPopupMenu();
		}
		
		/**
		 * highlights this panel to indicate that the user's cursor is over
		 * this panel
		 */
		public void highlight() {
			getUsernameLabel().setBackground( Color.gray );
		}
		
		/**
		 * unhighlights this panel to indicate that the user's cursor has
		 * left this panel
		 */
		public void unhighlight() {
			getUsernameLabel().setBackground( UIManager.getDefaults().getColor( "JPanel.background" ) );
		}
	}
	
	private class FriendGraphicPopupPanelListener implements MouseListener {
		
		final private FriendGraphicPopupPanel m_gui;
		
		public FriendGraphicPopupPanelListener( FriendGraphicPopupPanel gui ) {
			this.m_gui = gui;
		}

		@Override
		public void mouseClicked( MouseEvent e ) {
			//ignore
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			this.m_gui.highlight();
		}

		@Override
		public void mouseExited( MouseEvent e ) {
			this.m_gui.unhighlight();
		}

		@Override
		public void mousePressed( MouseEvent e ) {
			if ( e.isPopupTrigger() ) {
				//allow the popup to appear
			} 
			else {
				loadConversation( this.m_gui.getConversation() );
				this.m_gui.getConversation().markAsRead();
			}
		}

		@Override
		public void mouseReleased( MouseEvent e ) {
			//ignore
		}
	}
	
	/**
	 * a popup menu of commands associated with one friend
	 * 
	 * @author mjchao
	 *
	 */
	private class MessagingPopupMenu extends PopupMenu {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private FriendGraphicPopupPanel m_mainGUI;
		
		final private JMenuItem itmMarkAsRead;
		
		final private JMenuItem itmMarkAsUnread;
		
		final private JMenuItem itmMessage;
		
		final private JMenuItem itmCancel;
		
		public MessagingPopupMenu( FriendGraphicPopupPanel mainGUI ) {
			this.m_mainGUI = mainGUI;
			
			this.itmMarkAsRead = new JMenuItem( Text.Messaging.PrivateMessagingPanel.MARK_AS_READ_COMMAND );
			add( this.itmMarkAsRead );
			
			this.itmMarkAsUnread = new JMenuItem( Text.Messaging.PrivateMessagingPanel.MARK_AS_UNREAD_COMMAND );
			add( this.itmMarkAsUnread );
			
			this.itmMessage = new JMenuItem( Text.Messaging.PrivateMessagingPanel.MESSAGE_COMMAND );
			add( this.itmMessage );
			
			this.itmCancel = new JMenuItem( Text.Messaging.PrivateMessagingPanel.CANCEL_COMMAND );
			add( this.itmCancel );
		}
		
		public void addMessagingPopupMenuListener( MessagingPopupMenuListener l ) {
			this.itmMarkAsRead.addActionListener( l );
			this.itmMarkAsUnread.addActionListener( l );
			this.itmMessage.addActionListener( l );
			this.itmCancel.addActionListener( l );
		}
		
		/**
		 * @return			the FriendGraphicPopupPanel for which this popup menu provides commands
		 */
		public FriendGraphicPopupPanel getMainGUI() {
			return this.m_mainGUI;
		}
	}
	
	private class MessagingPopupMenuListener implements ActionListener {

		final private MessagingPopupMenu m_gui;
		
		public MessagingPopupMenuListener( MessagingPopupMenu gui ) {
			this.m_gui = gui;
		}
		
		@Override
		public void actionPerformed( ActionEvent e ) {
			String command = e.getActionCommand();
			if ( command.equals( Text.Messaging.PrivateMessagingPanel.MARK_AS_READ_COMMAND ) ) {
				this.m_gui.getMainGUI().getConversation().markAsRead();
			}
			else if ( command.equals( Text.Messaging.PrivateMessagingPanel.MARK_AS_UNREAD_COMMAND ) ) {
				this.m_gui.getMainGUI().getConversation().markAsUnread();
			}
			else if ( command.equals( Text.Messaging.PrivateMessagingPanel.MESSAGE_COMMAND ) ) {
				loadConversation( this.m_gui.getMainGUI().getConversation() );
			}
		}
	}
	
	/**
	 * displays a private conversation with one friend
	 * 
	 * @author mjchao
	 *
	 */
	private class ConversationPanel extends JPanel {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		final private JSplitPane pnlContainer;
		
		final private FriendGraphicPopupPanel m_friendsListLabel;
		
		final private JPanel pnlTopContainer;
			final private JLabel lblConversationWith;
			final private FormattedTextArea txtConversation;
			
		final private JPanel pnlBottomContainer;
			final private JPanel pnlSend;
				final protected FormattedTextArea txtSend;
				final protected JCheckBox chkEnterSend;
				final private JButton cmdSend;
			
		final private String m_friendUsername;
		
		final private String m_friendDisplayName;
			
		public ConversationPanel( FriendGraphicPopupPanel friendsListLabel , String friendUsername , String friendDisplayName ) {
			this.m_friendsListLabel = friendsListLabel;
			this.m_friendUsername = friendUsername;
			this.m_friendDisplayName = friendDisplayName;
			
			setLayout( new BorderLayout() );
			
			this.pnlTopContainer = new JPanel( new BorderLayout() );
				this.lblConversationWith = new JLabel( Text.Messaging.PrivateMessagingPanel.CONVERSATION_WITH_LABEL + this.m_friendDisplayName );
				this.pnlTopContainer.add( this.lblConversationWith , BorderLayout.NORTH );
				
				this.txtConversation = new FormattedTextArea();
				this.pnlTopContainer.add( this.txtConversation , BorderLayout.CENTER );
			
			this.pnlBottomContainer = new JPanel( new BorderLayout() );
				this.txtSend = new FormattedTextArea();
				this.pnlBottomContainer.add( new JScrollPane( this.txtSend ) , BorderLayout.CENTER );
				
				this.pnlSend = new JPanel( new FlowLayout( FlowLayout.CENTER ) );
					this.chkEnterSend = new JCheckBox( Text.Messaging.PrivateMessagingPanel.OPT_SEND_ON_ENTER );
					this.pnlSend.add( this.chkEnterSend );
					
					this.cmdSend = new JButton( Text.Messaging.PrivateMessagingPanel.SEND_COMMAND );
					this.pnlSend.add( this.cmdSend );
				this.pnlBottomContainer.add( this.pnlSend , BorderLayout.SOUTH );
			
			this.pnlContainer = new JSplitPane( JSplitPane.VERTICAL_SPLIT , this.pnlTopContainer , this.pnlBottomContainer );
			this.pnlContainer.setOneTouchExpandable( true );
			this.pnlContainer.setResizeWeight( 1.0 );
			PrivateMessagingPanel.this.m_conversationDividerLocation = this.pnlContainer.getDividerLocation();
			add( this.pnlContainer , BorderLayout.CENTER );
			
			this.pnlContainer.addPropertyChangeListener( JSplitPane.DIVIDER_LOCATION_PROPERTY , new PropertyChangeListener() {

				@Override
				public void propertyChange(PropertyChangeEvent arg0) {
					PrivateMessagingPanel.this.m_conversationDividerLocation = ( (Integer) arg0.getNewValue() ).intValue();
				}
			});
		}
		
		public void addConversationPanelListener( ConversationPanelListener l ) {
			this.txtSend.addKeyListener( l );
			this.cmdSend.addActionListener( l );
			this.chkEnterSend.addActionListener( l );
		}
		
		/**
		 * sets the location of the divider that splits the private messaging
		 * history from the send-new-message panel
		 * @param location
		 */
		public void setDividerLocation( int location ) {
			this.pnlContainer.setDividerLocation( location );
		}
		
		/**
		 * marks this conversation as read by unbolding the conversation label 
		 * and the friend's display name on the friends list
		 */
		public void markAsRead() {
			this.lblConversationWith.setForeground( Color.black );
			Font currentFont = this.lblConversationWith.getFont();
			this.lblConversationWith.setFont( currentFont.deriveFont( Font.PLAIN ));
			
			this.m_friendsListLabel.getUsernameLabel().setForeground( Color.black );
			this.m_friendsListLabel.getUsernameLabel().setFont( this.m_friendsListLabel.getUsernameLabel().getFont().deriveFont( Font.PLAIN ) );
		}
		
		/**
		 * marks this conversation as unread by bolding the conversation label
		 * and the friend's display name on the friends list
		 */
		public void markAsUnread() {
			this.lblConversationWith.setForeground( Color.red );
			Font currentFont = this.lblConversationWith.getFont();
			this.lblConversationWith.setFont( currentFont.deriveFont( Font.BOLD ) );
			
			this.m_friendsListLabel.getUsernameLabel().setForeground( Color.red );
			this.m_friendsListLabel.getUsernameLabel().setFont( this.m_friendsListLabel.getUsernameLabel().getFont().deriveFont( Font.BOLD ) );
		}
		
		/**
		 * @return		if we should send a private message when the user types
		 * 				"ENTER"
		 */
		public boolean shouldSendMessageOnEnter() {
			return this.chkEnterSend.isSelected();
		}
		
		/**
		 * removes an extra newline at the end of the user's message, which
		 * occurs when the user presses send to send the private message
		 */
		public void removeExtraNewline() {
			this.txtSend.setText( this.txtSend.getText().substring( 0 , this.txtSend.getText().length()-1 ) );
		}
		
		/**
		 * sends the message the user has entered into the send text area to the
		 * user's friend
		 */
		public void sendMessage() {
			//TODO
			System.out.println( "Sending" );
			
			//clear the send text box so that the user can send a new message quickly
			this.txtSend.setText( "" );
		}
		
		/**
		 * attempts to set focus to the send text field so that the user can
		 * immediately continue writing his/her message
		 */
		public void requestSendTextFieldFocus() {
			SwingUtilities.invokeLater( new Runnable() {
				
				@Override
				public void run() {
					ConversationPanel.this.txtSend.requestFocus();
				}
			});
		}
	}
	
	private class ConversationPanelListener implements ActionListener , KeyListener {

		final private ConversationPanel m_gui;
		
		public ConversationPanelListener( ConversationPanel gui ) {
			this.m_gui = gui;
		}
		
		@Override
		public void keyPressed( KeyEvent e ) {
			//ignore
		}

		@Override
		public void keyReleased( KeyEvent e ) {
			if ( e.getKeyCode() == KeyEvent.VK_ENTER ) {
				if ( this.m_gui.shouldSendMessageOnEnter() ) {
					this.m_gui.removeExtraNewline();
					this.m_gui.sendMessage();
				}
			}
		}

		@Override
		public void keyTyped( KeyEvent e ) {
			//ignore
		}

		@Override
		public void actionPerformed( ActionEvent e ) {
			String command = e.getActionCommand();
			if ( command.equals( Text.Messaging.PrivateMessagingPanel.SEND_COMMAND ) ) {
				this.m_gui.sendMessage();
				this.m_gui.requestSendTextFieldFocus();
			}
			if ( e.getSource() == this.m_gui.chkEnterSend ) {
				this.m_gui.requestSendTextFieldFocus();
			}
		}
		
	}
	
	private class ConversationDisplayPanel extends JPanel {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public ConversationDisplayPanel() {
			setLayout( new GridLayout( 1 , 1 ) );
		}
		
		public void loadConversation( ConversationPanel conversation ) {
			removeAll();
			add( conversation );
			revalidate();
			repaint();
		}
	}
}
