package mc.iwclient.uitemplates;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Provides an unchangeable description of some field. it contains a description,
 * which describes the field, and a value, which is associated with the description
 * 
 * @author mjchao
 *
 */
public class DescriptionField extends JPanel {

	/**
	 * default description assigned to a description field
	 */
	final public static String DEFAULT_DESCRIPTION = "";
	
	/**
	 * default value assigned to a description field
	 */
	final public static String DEFAULT_VALUE = "";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	final private JLabel lblDescription;

	private String m_fieldDescription;
	
	private String m_fieldValue;
	
	/**
	 * creates an empty description field
	 */
	public DescriptionField() {
		this( DEFAULT_DESCRIPTION , DEFAULT_VALUE );
	}
	
	/**
	 * creates a description field with a description but not value
	 * 
	 * @param fieldDescription		description of this field
	 */
	public DescriptionField( String fieldDescription ) {
		this( fieldDescription , DEFAULT_VALUE );
	}
	
	/**
	 * creates a description field with the specified description and value
	 * 
	 * @param fieldDescription			description of this field
	 * @param fieldValue				value of this field
	 */
	public DescriptionField( String fieldDescription , String fieldValue ) {
		this.m_fieldDescription = fieldDescription;
		this.m_fieldValue = fieldValue;
		this.lblDescription = new JLabel();
		updateText();
		
		setLayout( new GridLayout( 1 , 1 ) );
		add( this.lblDescription );
	}
	
	/**
	 * sets the description of this field
	 * 
	 * @param description			description of this field
	 */
	public void setDescription( String description ) {
		this.m_fieldDescription = description;
		updateText();
	}
	
	/**
	 * sets the value of this field
	 * 
	 * @param value			value for this field
	 */
	public void setValue( String value ) {
		this.m_fieldValue = value;
		updateText();
	}
	
	/**
	 * updates the text displayed in this description field
	 */
	private void updateText() {
		this.lblDescription.setText( this.m_fieldDescription + this.m_fieldValue );
	}
}
