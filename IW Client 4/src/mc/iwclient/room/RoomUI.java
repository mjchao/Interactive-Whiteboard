package mc.iwclient.room;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

import mc.iwclient.messaging.FilterableUserGraphicList;
import mc.iwclient.messaging.SearchableGraphicList;
import mc.iwclient.uitemplates.PopupMenu;
import mc.iwclient.util.Text;

//TODO user list, private chat, general activity container
public class RoomUI extends JFrame {

	private static RoomUI ui = new RoomUI();
	public static void showUI() {
		ui.setVisible( true );
		ui.setSize( 500 , 500 );
	}
	
	
	final public static void main( String[] args ) {
		RoomUI ui = new RoomUI();
		ui.setVisible( true );
		ui.setSize( 500 , 500 );
	}//*/
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	final private UserList pnlUserList;
	
	public RoomUI() {
		setLayout( new BorderLayout() );
		
		Activity mainActivity = new Activity( "Untitled" , false );
		mainActivity.addChildActivity( 25 , 25 , new WhiteboardActivity() );
		mainActivity.addChildActivity( 100 ,  100 , new WhiteboardActivity() );
		mainActivity.addChildActivity( 250 ,  250 , new WhiteboardActivity() );
		mainActivity.setDraggable( false );
		JScrollPane scrollActivity = new JScrollPane( mainActivity );
		add( scrollActivity , BorderLayout.CENTER );
		
		this.pnlUserList = new UserList();
		add( this.pnlUserList , BorderLayout.EAST );
		mainActivity.ensureContainsChildActivities();
	}

	private class UserList extends SearchableGraphicList {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		final protected FilterableUserGraphicList pnlUsers;
		
		public UserList() {
			this.pnlUsers = new FilterableUserGraphicList();
			
			for ( int i=0 ; i<25 ; i++ ) {
				UserGraphicPopupPanel user = new UserGraphicPopupPanel( "user " + i , "user " + i );
				user.addUserGraphicPopupPanelListener( new UserGraphicPopupPanelListener( user ) );
				UserPopupMenu popup = new UserPopupMenu( user );
				popup.addUserPopupMenuListener( new UserPopupMenuListener( popup ) );
				user.setPopupMenu( popup );
				this.pnlUsers.add( user );
			}
			super.loadInDisplay( this.pnlUsers );
		}
		
		@Override
		protected SearchPanel createCustomSearchPanel() {
			UserListSearchPanel rtn = new UserListSearchPanel();
			rtn.addUserListSearchPanelListener( new UserListSearchPanelListener( rtn ) );
			return rtn;
		}
		
		@Override
		protected void handleSearchCommand(SearchPanel gui) {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected void handleCancelSearchCommand(SearchPanel gui) {
			// TODO Auto-generated method stub
			
		}
		
		private class UserListSearchPanel extends SearchPanel {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public UserListSearchPanel() {
				super.pnlSearchFilter.remove( super.cmdSearch );
				super.pnlSearchFilter.remove( super.cmdCancel );
				this.txtSearchFilter.setText( Text.Room.UserList.SEARCH_BOX_DESCRIPTION );
				revalidate();
				repaint();
			}
			
			public void addUserListSearchPanelListener( UserListSearchPanelListener l ) {
				super.txtSearchFilter.addKeyListener( l );
				super.txtSearchFilter.addFocusListener( l );
			}
			
			/**
			 * checks if the description "[search for user]" should appear
			 * in the search field or not (it appears if the user has not
			 * entered any search terms)
			 */
			public void displaySearchFieldDescriptionIfNecessary() {
				if ( this.txtSearchFilter.getText().equals( "" ) ) {
					this.txtSearchFilter.setText( Text.Room.UserList.SEARCH_BOX_DESCRIPTION );
				}
			}
			
			/**
			 * checks if the description "[search for user]" is in the search
			 * field or not, and if it is, then the search field is cleared.
			 */
			public void clearSearchFieldDescriptionIfNecessary() {
				if ( this.txtSearchFilter.getText().equals( Text.Room.UserList.SEARCH_BOX_DESCRIPTION ) ) {
					this.txtSearchFilter.setText( "" );
				}
			}
		}
		
		private class UserListSearchPanelListener implements KeyListener, FocusListener {

			final private UserListSearchPanel m_gui;
			
			public UserListSearchPanelListener( UserListSearchPanel gui ) {
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
		
		private class UserGraphicPopupPanel extends mc.iwclient.messaging.UserGraphicPopupPanel {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public UserGraphicPopupPanel( String username , String displayName ) {
				super(username, displayName);
				getUsernameLabel().setOpaque( true );
			}
			
			public void addUserGraphicPopupPanelListener( UserGraphicPopupPanelListener l ) {
				getUsernameLabel().addMouseListener( l );
			}
			
			@Override
			public void setPopupMenu( JPopupMenu menu ) {
				if ( menu instanceof UserPopupMenu ) {
					super.setPopupMenu( menu );
				}
				else {
					
					//error
					throw new RuntimeException();
				}
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
		
		private class UserGraphicPopupPanelListener implements MouseListener {
			
			final private UserGraphicPopupPanel m_gui;
			
			public UserGraphicPopupPanelListener( UserGraphicPopupPanel gui ) {
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
				//ignore
			}

			@Override
			public void mouseReleased( MouseEvent e ) {
				//ignore
			}
		}
		
		private class UserPopupMenu extends PopupMenu {
			
			final private JMenuItem itmWhisper;
			
			final private JMenuItem itmMoreOptions;
			
			final private JMenuItem itmKick;
			
			final private UserGraphicPopupPanel m_mainGui;
			
			public UserPopupMenu( UserGraphicPopupPanel mainGui ) {
				this.m_mainGui = mainGui;
				
				this.itmWhisper = new JMenuItem( Text.Room.UserList.WHISPER_COMMAND );
				add( this.itmWhisper );
				
				this.itmMoreOptions = new JMenuItem( Text.Room.UserList.MORE_OPTIONS_COMMAND );
				add( this.itmMoreOptions );
				
				this.itmKick = new JMenuItem( Text.Room.UserList.KICK_USER_COMMAND );
				add( this.itmKick );
			}
			
			public void addUserPopupMenuListener( UserPopupMenuListener l ) {
				this.itmWhisper.addActionListener( l );
				this.itmMoreOptions.addActionListener( l );
				this.itmKick.addActionListener( l );
			}
		}
		
		private class UserPopupMenuListener implements ActionListener {

			final private UserPopupMenu m_gui;
			
			public UserPopupMenuListener( UserPopupMenu gui ) {
				this.m_gui = gui;
			}
			
			@Override
			public void actionPerformed( ActionEvent e ) {
				String command = e.getActionCommand();
				if ( command.equals( Text.Room.UserList.WHISPER_COMMAND ) ) {
					//TODO
				}
				else if ( command.equals( Text.Room.UserList.MORE_OPTIONS_COMMAND ) ) {
					//TODO
				}
				else if ( command.equals( Text.Room.UserList.KICK_USER_COMMAND ) ) {
					//TODO
				}
			}
		}
	}

}
