package mc.iwclient.messaging;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;

/**
 * displays information about one other user and provides a popup menu of options
 * from which the user of the program can select
 * 
 * @author mjchao
 *
 */
public class UserGraphicPopupPanel extends UserGraphicPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private PopupMenuLabel lblUsername;
	
	private JPopupMenu m_popupMenu;
	
	public UserGraphicPopupPanel( String username , String displayName ) {
		super( username , displayName );
	}
	
	public PopupMenuLabel getUsernameLabel() {
		return this.lblUsername;
	}
	
	public void setPopupMenu( JPopupMenu menu ) {
		this.m_popupMenu = menu;
	}
	
	public JPopupMenu getPopupMenu() {
		return this.m_popupMenu;
	}
	
	@Override
	protected JPanel createUserInfoPanel() {
		JPanel userInfoPanel = new JPanel( new BorderLayout() );
		this.lblUsername = new PopupMenuLabel( getDisplayName() );
		userInfoPanel.add( this.lblUsername , BorderLayout.CENTER );
		this.lblUsername.addMouseListener( new UserGraphicPopupPanelListener( this ) );
		return userInfoPanel;
	}
	
	@Override
	protected void setupGraphics() {
		setLayout( new BorderLayout() );
		add( createUserInfoPanel() , BorderLayout.CENTER );
	}
	
	/**
	 * displays the popup menu associated with this user graphic popup panel
	 * at the specified location
	 * 
	 * @param x			x-coordinate of the location to display the popup menu
	 * @param y			y-coordinate of the location to display the popup menu
	 */
	public void displayPopupMenu( int x , int y ) {
		
		//make sure the popup menu exists
		if ( this.m_popupMenu != null ) {
			this.m_popupMenu.show( this ,  x , y );
		}
	}
	
	/**
	 * hides the popup menu associated with this user graphic popup panel
	 */
	public void hidePopupMenu() {
		
		//make sure the popup menu exists
		if ( this.m_popupMenu != null ) {
			this.m_popupMenu.setVisible( false );
		}
	}
	
	protected class PopupMenuLabel extends JLabel {
		
		final public static int MINIMUM_LABEL_HEIGHT = 50;
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public PopupMenuLabel( String displayName ) {
			super( displayName , SwingConstants.CENTER );
			
			if ( displayName.length() > MAX_DISPLAY_NAME_SIZE ) {
				setText( displayName.substring( 0 , MAX_DISPLAY_NAME_SIZE ) + "..." );
			}
			else {
				setText( displayName );
			}
			setToolTipText( displayName );
		}
		
		@Override
		public Dimension getPreferredSize() {
			return new Dimension( super.getPreferredSize().width , MINIMUM_LABEL_HEIGHT );
		}
	}
}
