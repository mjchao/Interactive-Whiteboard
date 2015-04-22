package mc.iwclient.account;

import java.awt.GridLayout;

import javax.swing.JPanel;

import mc.iwclient.uitemplates.CommandMenu;
import mc.iwclient.uitemplates.InputField;
import mc.iwclient.util.Text;

public class UserInfoPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	final private InputField inptDisplayName;
	
	final private InputField inptDOB;
	
	final private InputField inptHometown;
	
	final private InputField inptSchool;
	
	final private CommandMenu pnlCommands;
	
	public UserInfoPanel() {
		
		setLayout( new GridLayout( 0 , 1 ) );
		this.inptDisplayName = new InputField( Text.Account.UserInfo.INPUT_DISPLAY_NAME );
		add( this.inptDisplayName );
		
		this.inptDOB = new InputField( Text.Account.UserInfo.INPUT_DOB );
		add( this.inptDOB );
		
		this.inptHometown = new InputField( Text.Account.UserInfo.INPUT_HOMETOWN );
		add( this.inptHometown );
		
		this.inptSchool = new InputField( Text.Account.UserInfo.INPUT_SCHOOL );
		add( this.inptSchool );
		
		this.pnlCommands = new CommandMenu( false );
			this.pnlCommands.addCommand( Text.Account.UserInfo.RESET_COMMAND );
			this.pnlCommands.addCommand( Text.Account.UserInfo.UPDATE_COMMAND );
		add( this.pnlCommands );
	}
	
	public void addUserInfoPanelListener( UserInfoPanelListener l ) {
		this.pnlCommands.addActionListener( l );
	}
	
	/**
	 * clears all input fields in this user interface
	 */
	public void resetFields() {
		this.inptDisplayName.setInput( "" );
		this.inptDOB.setInput( "" );
		this.inptHometown.setInput( "" );
		this.inptSchool.setInput( "" );
	}
}
