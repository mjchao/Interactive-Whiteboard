package mc.iwclient.messaging;

import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import mc.iwclient.structs.UserData;
import mc.iwclient.uitemplates.CommandMenu;
import mc.iwclient.uitemplates.DescriptionField;
import mc.iwclient.util.Text;

//TODO set up user info panel for friends list and pests list
public class UserInfoPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * the user currently displayed in this UserInfoPanel
	 */
	private UserData m_displayedUser;
	
	final private DescriptionField descDisplayName;
	
	final private DescriptionField descDOB;
	
	final private DescriptionField descHometown;
	
	final private DescriptionField descSchool;
	
	/**
	 * provides commands relating to this user
	 */
	final private CommandMenu pnlCommands;
	
	public UserInfoPanel() {
		
		setLayout( new GridLayout( 0 , 1 ) );
		this.descDisplayName = new DescriptionField( Text.Messaging.UserInfoPanel.DISPLAY_NAME );
		add( this.descDisplayName );
		
		this.descDOB = new DescriptionField( Text.Messaging.UserInfoPanel.DOB );
		add( this.descDOB );
		
		this.descHometown = new DescriptionField( Text.Messaging.UserInfoPanel.HOMETOWN );
		add( this.descHometown );
		
		this.descSchool = new DescriptionField( Text.Messaging.UserInfoPanel.SCHOOL );
		add( this.descSchool );
		
		this.pnlCommands = new CommandMenu( false );
		add( this.pnlCommands );
	}
	
	/**
	 * loads the information of the specified user in this panel
	 * 
	 * @param user			the user whose information is to be displayed in this panel
	 */
	public void loadUser( UserData user ) {
		this.m_displayedUser = user;
		this.descDisplayName.setValue( user.m_displayName );
		this.descDOB.setValue( user.m_dob );
		this.descHometown.setValue( user.m_hometown );
		this.descSchool.setValue( user.m_school );
	}
	
	/**
	 * @return			the user whose information is currently displayed, or null
	 * 					if no user is currently displayed
	 */
	public UserData getDisplayedUser() {
		return this.m_displayedUser;
	}
	
	/**
	 * adds the given action listener to all the commands available in
	 * the command panel of this user info panel
	 * 
	 * @param l
	 */
	public void addUserInfoPanelListener( ActionListener l ) {
		this.pnlCommands.addActionListener( l );
	}
	
	/**
	 * adds the given command to this panel
	 * 
	 * @param command			an additional command the user can use
	 */
	public void addCommand( String command ) {
		this.pnlCommands.addCommand( command );
	}
}
