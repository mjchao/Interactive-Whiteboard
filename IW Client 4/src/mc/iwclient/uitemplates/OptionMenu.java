package mc.iwclient.uitemplates;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 * Provides a list of options from which the user can select one
 * 
 * @author mjchao
 * 
 */
public class OptionMenu extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	final public static boolean DEFAULT_ORIENTATION = false;
	
	/**
	 * the button group to which all options in this menu belong
	 */
	final private ButtonGroup grpOptions;
	
	/**
	 * stores all the option buttons that belong to this menu
	 */
	final private ArrayList< JRadioButton > m_options;
	
	/**
	 * creates an option menu with default orientation and no options initially
	 */
	public OptionMenu() {
		this( DEFAULT_ORIENTATION , new String[ 0 ] );
	}
	
	/**
	 * creates an option menu with the specified layout orientation (vertical or
	 * horizontally spanning options)
	 * 
	 * @param verticalOrientation		true for vertical orientation, or false
	 * 									for horizontal orientation
	 */
	public OptionMenu( boolean verticalOrientation ) {
		this( verticalOrientation , new String[ 0 ] );
	}
	
	/**
	 * creates an option menu with the given options
	 * 
	 * @param options		the options of this menu
	 * 						from which the user can select
	 */
	public OptionMenu( String[] options ) {
		this( DEFAULT_ORIENTATION , options );
	}
	
	/**
	 * creates an option menu with the specified orientation
	 * and the specified options
	 * 
	 * @param verticalOrientation		true or false, corresponding to
	 * 									vertical or horizontal orientation
	 * @param options					the options of this menu 
	 * 									from which the user can select
	 */
	public OptionMenu( boolean verticalOrientation , String[] options ) {
		this.grpOptions = new ButtonGroup();
		this.m_options = new ArrayList< JRadioButton >();
		
		if ( verticalOrientation ) {
			setLayout( new GridLayout( 0 , 1 ) );
		}
		else {
			setLayout( new GridLayout( 1 , 0 ) );
		}
		
		for ( int i=0 ; i<options.length ; i++ ) {
			addOption( options[ i ] );
		}
	}
	
	/**
	 * adds an option to the menu
	 * 
	 * @param option		the option to add to the menu
	 */
	public void addOption( String option ) {
		JRadioButton optNewOption = new JRadioButton( option );
		this.grpOptions.add( optNewOption );
		this.m_options.add( optNewOption );
		this.add( optNewOption );
		
		//if there is currently no selected option,
		//then default to selecting the option we are currently adding
		if ( this.getSelectedString() == null ) {
			optNewOption.setSelected( true );
		}
		this.revalidate();
		this.repaint();
	}
	
	/**
	 * selects the specified option, as if the user had clicked on it
	 * 
	 * @param option		an option to select
	 */
	public void select( String option ) {
		for ( JRadioButton b : this.m_options ) {
			if ( b.getText().equals( option ) ) {
				b.doClick();
			}
		}
	}
	
	/**
	 * @return			the option that is currently seelcted
	 */
	public String getSelectedString() {
		for ( JRadioButton b : this.m_options ) {
			if ( b.isSelected() ) {
				return b.getText();
			}
		}
		return null;
	}
	
	public void addActionListener( ActionListener l ) {
		for ( JRadioButton b : this.m_options ) {
			b.addActionListener( l );
		}
	}
}
