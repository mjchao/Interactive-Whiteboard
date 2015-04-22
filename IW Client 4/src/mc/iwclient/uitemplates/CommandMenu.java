package mc.iwclient.uitemplates;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Provides a menu of buttons that the user can click to perform an
 * action
 * 
 * @author mjchao
 *
 */
public class CommandMenu extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * default orientation for the command menu is vertical
	 */
	final public static boolean DEFAULT_ORIENTATION = true;
	
	/**
	 * stores all commands associated with this command menu
	 */
	final private ArrayList< JButton > m_commands;
	
	/**
	 * the event listeners for this menu
	 */
	private ArrayList< ActionListener > m_menuListeners;
	
	/**
	 * creates a command menu with default orientation and
	 * no initial commands
	 */
	public CommandMenu() {
		this( DEFAULT_ORIENTATION , new String[ 0 ] );
	}
	
	/**
	 * creates a command menu with the specified orientation and
	 * no initial commands
	 * 
	 * @param verticalOrientation			whether layout of the menu should be oriented
	 * 										vertically or horizontally
	 */
	public CommandMenu( boolean verticalOrientation ) {
		this( verticalOrientation , new String[ 0 ] );
	}
	
	/**
	 * creates a command menu with default layout orientation and
	 * the specified initial commands
	 * 
	 * @param initialCommands				the initial commands to be placed in the menu
	 */
	public CommandMenu( String[] initialCommands ) {
		this( DEFAULT_ORIENTATION , initialCommands );
	}
	
	/**
	 * creates a command menu with the specified orientation and
	 * initial commands
	 * 
	 * @param verticalOrientation		whether to orient the menu vertically or horizontally
	 * @param initialCommands			the initial commands contained in the menu
	 */
	public CommandMenu( boolean verticalOrientation , String[] initialCommands ) {
		this.m_commands = new ArrayList< JButton >();
		this.m_menuListeners = new ArrayList< ActionListener >();
		
		if ( verticalOrientation ) {
			setLayout( new GridLayout( 0 , 1 ) );
		}
		else {
			setLayout( new GridLayout( 1 , 0 ) );
		}
		
		for ( String command : initialCommands ) {
			addCommand( command );
		}
	}
	
	/**
	 * adds the given event listener to all the buttons in this menu
	 * 
	 * @param l				an ActionListener event listener
	 */
	public void addActionListener( ActionListener l ) {
		this.m_menuListeners.add( l );
		for ( JButton cmd : this.m_commands ) {
			cmd.addActionListener( l );
		}
	}
	
	/**
	 * adds the specified command to the command menu and has
	 * any existing event listeners watch the new command button
	 * 
	 * @param command		the command to add
	 */
	public void addCommand( String command ) {
		JButton cmdNewCommand = new JButton( command );
		this.m_commands.add( cmdNewCommand );
		this.add( cmdNewCommand );
		
		//have any currently registered event listeners
		//to also watch the new button just added to the menu
		for ( ActionListener l : this.m_menuListeners ) {
			cmdNewCommand.addActionListener( l );
		}
		
		this.repaint();
		this.revalidate();
	}
}
