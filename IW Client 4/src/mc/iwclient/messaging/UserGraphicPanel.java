package mc.iwclient.messaging;

import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class UserGraphicPanel extends JPanel {

	final public static int MAX_DISPLAY_NAME_SIZE = 20;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	final private String m_username;
	
	final private String m_displayName;
	
	private JPanel pnlUserInfo;
	
	public UserGraphicPanel( String username , String displayName ) {
		this.m_username = username;
		this.m_displayName = displayName;
		this.pnlUserInfo = new JPanel();
		setupGraphics();
	}
	
	/**
	 * @return			the username of the user this UserGraphicPanel represents
	 */
	public String getUsername() {
		return this.m_username;
	}
	
	/**
	 * @return			the display name of the user this UserGraphicPanel represents
	 */
	public String getDisplayName() {
		return this.m_displayName;
	}
	
	protected void setupGraphics() {
		setLayout( new FlowLayout( FlowLayout.CENTER ) );
		this.pnlUserInfo = createUserInfoPanel();
		add( this.pnlUserInfo );
	}
	
	/**
	 * by default, adds a display name label to the user info panel. this method
	 * can be overriden to provide a custom user info panel
	 * 
	 * @return 						the user info panel ready to be added to the
	 * 								user interface
	 */
	protected JPanel createUserInfoPanel() {
		JPanel userInfoPanel = new JPanel();
		userInfoPanel.setLayout( new FlowLayout( FlowLayout.CENTER ) );
		JLabel lblDisplayName = new JLabel();
		//cut off display name if it is too long
		if ( getDisplayName().length() > MAX_DISPLAY_NAME_SIZE ) {
			lblDisplayName.setText( getDisplayName().substring( 0 , MAX_DISPLAY_NAME_SIZE ) + "..." );
		}
		else {
			lblDisplayName.setText( getDisplayName() );
		}
		
		lblDisplayName.setToolTipText( getDisplayName() );
		userInfoPanel.add( lblDisplayName );
		return userInfoPanel;
	}

}
