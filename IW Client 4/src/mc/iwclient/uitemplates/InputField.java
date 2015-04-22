package mc.iwclient.uitemplates;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Provides a description of some input, and allows the user to enter that input
 * 
 * @author mjchao
 *
 */
public class InputField extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * a default description for input fields
	 */
	final public static String DEFAULT_DESCRIPTION = "";
	
	/**
	 * a default number of columns for input fields
	 */
	final public static int DEFAULT_INPUT_SIZE = 10;
	
	/**
	 * description of the input
	 */
	final private JLabel lblDescription;
	
	/**
	 * text field for entering input
	 */
	protected JTextField txtInput;

	/**
	 * Creates an empty input field with a default input size
	 */
	public InputField() {
		this( DEFAULT_DESCRIPTION , DEFAULT_INPUT_SIZE );
	}
	
	/**
	 * Creates an input field with the given description and a default input size
	 * 
	 * @param description		description of what the input should be
	 */
	public InputField( String description ) {
		this( description , DEFAULT_INPUT_SIZE );
	}
	
	/**
	 * Creates an input field with the given description and sets the 
	 * input text field to the specified size
	 * 
	 * @param description		description of what the input should be
	 * @param sizeOfInput		number of columns for the input text field to have
	 */
	public InputField( String description , int sizeOfInput ) {
		this.lblDescription = new JLabel( description );
		this.lblDescription.setToolTipText( description );
		
		this.txtInput = new JTextField( sizeOfInput );
		this.txtInput.setColumns( sizeOfInput );
		
		setLayout( new GridLayout( 1 , 2 ) );
		add( this.lblDescription );
		add( this.txtInput );
	}
	
	/**
	 * Creates an input field with the given description, and sets the input
	 * field text to contain the given initial input
	 * 
	 * @param description		description of what the input should be
	 * @param input				the initial input for the input field to display
	 */
	public InputField( String description , String input ) {
		this( description , input.length() );
		this.txtInput.setText( input );
	}
	
	/**
	 * Creates an input field with the given description and size, and
	 * puts the given initial input into the input field
	 * 
	 * @param description		description of what the input should be
	 * @param sizeOfInput		the size of the input field
	 * @param input				the initial input contained by the input field
	 */
	public InputField( String description , int sizeOfInput , String input ) {
		this( description , sizeOfInput );
		this.txtInput.setText( input );
	}
	
	/**
	 * disables the input field so that the user can no longer enter text
	 * into the input field
	 */
	public void disableInput() {
		this.txtInput.setEnabled( false );
	}
	
	/**
	 * enables the input field so that the user can enter text into the input 
	 * field
	 */
	public void enableInput() {
		this.txtInput.setEnabled( true );
	}
	
	/**
	 * @return		if the user is permitted to enter input or not
	 */
	public boolean isInputEnabled() {
		return this.txtInput.isEnabled();
	}
	
	/**
	 * sets the input of this input field
	 * 
	 * @param input		the input to be displayed in this input field
	 */
	public void setInput( String input ) {
		this.txtInput.setText( input );
	}
	/**
	 * @return		the input that the user entered into the input field
	 */
	public String getInput() {
		return this.txtInput.getText();
	}
}
