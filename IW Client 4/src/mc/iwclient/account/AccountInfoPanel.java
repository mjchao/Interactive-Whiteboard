package mc.iwclient.account;

import java.awt.GridLayout;

import javax.swing.JPanel;

import mc.iwclient.uitemplates.CommandMenu;
import mc.iwclient.uitemplates.InputField;
import mc.iwclient.util.Text;

public class AccountInfoPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	final private InputField inptUsername;
	
	final private InputField inptCurrentPassword;
	final private InputField inptNewPassword;
	
	final private InputField inptConfirmNewPassword;
	
	final private CommandMenu pnlCommands;
		
	public AccountInfoPanel() {
		
		setLayout( new GridLayout( 0 , 1 ) );
		this.inptUsername = new InputField( Text.Account.AccountInfo.INPUT_USERNAME );
		this.inptUsername.disableInput();
		add( this.inptUsername );
		
		this.inptCurrentPassword = new InputField( Text.Account.AccountInfo.INPUT_CURRENT_PASSWORD );
		add( this.inptCurrentPassword );
		
		this.inptNewPassword = new InputField( Text.Account.AccountInfo.INPUT_NEW_PASSWORD );
		add( this.inptNewPassword );
		
		this.inptConfirmNewPassword = new InputField( Text.Account.AccountInfo.INPUT_CONFIRM_NEW_PASSWORD );
		add( this.inptConfirmNewPassword );
		
		this.pnlCommands = new CommandMenu( false );
			this.pnlCommands.addCommand( Text.Account.AccountInfo.RESET_COMMAND );
			this.pnlCommands.addCommand( Text.Account.AccountInfo.UPDATE_COMMAND );
		add( this.pnlCommands );
	}
	
	public void addAccountInfoPanelListener( AccountInfoPanelListener l ) {
		this.pnlCommands.addActionListener( l );
	}
	
	/**
	 * resets all fields in this user interface
	 */
	public void resetFields() {
		this.inptCurrentPassword.setInput( "" );
		this.inptNewPassword.setInput( "" );
		this.inptConfirmNewPassword.setInput( "" );
	}

}
