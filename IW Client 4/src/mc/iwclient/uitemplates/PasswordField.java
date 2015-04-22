package mc.iwclient.uitemplates;

import javax.swing.JPasswordField;

/**
 * Provides a description of some input and allows that user to enter that input
 * where the input is masked
 * 
 * @author mjchao
 *
 */
public class PasswordField extends InputField {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PasswordField() {
		this( DEFAULT_DESCRIPTION , DEFAULT_INPUT_SIZE );
	}
	
	public PasswordField( String description ) {
		this( description , DEFAULT_INPUT_SIZE );
	}
	
	public PasswordField( String description , int sizeOfInput ) {
		super( description , sizeOfInput );
		super.remove( this.txtInput );
		super.txtInput = new JPasswordField( sizeOfInput );
		super.add( this.txtInput );
	}
}
