package mc.iwclient.messaging;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

import mc.iwclient.uitemplates.CommandMenu;

/**
 * displays information about one other user (e.g. display name) and 
 * provides a menu of options from which the user of the program can select
 * 
 * @author mjchao
 *
 */
public class UserGraphicCommandPanel extends UserGraphicPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	final public static Dimension MINIMUM_SIZE = new Dimension( 50 , 20 );
	
	private JPanel pnlUserInfo;
	
	private CommandMenu pnlCommands;
	
	public UserGraphicCommandPanel( String username , String displayName ) {
		super( username , displayName );
	}
	
	@Override
	protected void setupGraphics() {
		setLayout( new GridBagLayout() );
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.5;
		this.pnlUserInfo = createUserInfoPanel();
		add( this.pnlUserInfo , c );
		
		this.pnlCommands = new CommandMenu( false );
		setupCommandMenu( this.pnlCommands );
		
		c.gridwidth = GridBagConstraints.REMAINDER;
		add( this.pnlCommands );
	}
	
	/**
	 * adds the necessary commands to the command menu. by default, no commands are
	 * added
	 */
	@SuppressWarnings("unused")
	public void setupCommandMenu( CommandMenu commandsMenu ) {
		//do nothing
	}
	
	public void addUserGraphicCommandPanelListener( UserGraphicCommandPanelListener l ) {
		this.pnlCommands.addActionListener( l );
	}
}
