package mc.iwclient.uitemplates;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

/**
 * a tool bar button that is pushed inward to show that it is selected
 * and raised outward to show that it is not selected.
 * 
 * @author mjchao
 *
 */
public class ToolBarSelectButton extends JButton {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	final public static Border SELECTED_BORDER = BorderFactory.createBevelBorder(BevelBorder.LOWERED , Color.white, Color.black );
	
	final public static Border UNSELECTED_BORDER = BorderFactory.createRaisedBevelBorder();
	
	private boolean m_isSelected = false;
	
	/**
	 * creates an empty tool bar select button
	 */
	public ToolBarSelectButton() {
		
	}
	
	/**
	 * creates a tool bar select button with the given text
	 * 
	 * @param text			text to be displayed on the button
	 */
	public ToolBarSelectButton( String text ) {
		setText( text );
	}
	
	/**
	 * creates a tool bar select button with the given width and height
	 * 
	 * @param width			width of the button
	 * @param height		height of the button
	 */
	public ToolBarSelectButton( int width , int height ) {
		this.setPreferredSize( new Dimension( width , height ) );
	}
	
	/**
	 * creates a tool bar select button with the given image, width, and height
	 * 
	 * @param icon				image to be displayed on the button
	 * @param width				width of the button
	 * @param height			height of the button
	 */
	public ToolBarSelectButton( ImageIcon icon , int width , int height ) {
		Image scaledIcon = icon.getImage().getScaledInstance( width , height , Image.SCALE_SMOOTH );
		this.setIcon( new ImageIcon( scaledIcon ) );
	}
	
	/**
	 * changes this button into its selected state
	 */
	public void setSelected() {
		this.setBorder( SELECTED_BORDER );
		this.m_isSelected = true;
	}
	
	/**
	 * changes this button into its unselected state
	 */
	public void setUnselected() {
		this.setBorder( UNSELECTED_BORDER );
		this.m_isSelected = false;
	}
	
	@Override
	public boolean isSelected() {
		return this.m_isSelected;
	}

}
