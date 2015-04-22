package mc.iwclient.uitemplates;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * a group of select buttons in which one of the buttons must always be selected
 * and no more than one of the buttons can be selected at the same time
 * 
 * @author mjchao
 *
 */
public class SelectButtonGroup extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected ArrayList< ToolBarSelectButton > buttonsInGroup;
	
	final private ActionListener selectListener;
	
	public SelectButtonGroup() {
		this.buttonsInGroup = new ArrayList< ToolBarSelectButton >();
		setLayout( new FlowLayout( FlowLayout.LEFT ) );
		
		this.selectListener = new ActionListener() {

			@Override
			public void actionPerformed( ActionEvent e ) {
				for ( ToolBarSelectButton b : SelectButtonGroup.this.buttonsInGroup ) {
					if ( b == e.getSource() ) {
						b.setSelected();
					}
					else {
						b.setUnselected();
					}
				}
			}
		};
	}

	/**
	 * creates a select button group that displays the name of the group
	 * in a title border
	 * 
	 * @param groupName				name of the group
	 */
	public SelectButtonGroup( String groupName ) {
		this();
		setBorder( BorderFactory.createTitledBorder( groupName ) );
	}
	
	/**
	 * creates a select button group that arranges the buttons in
	 * the given number of rows and columns
	 * 
	 * @param rows
	 * @param columns
	 */
	public SelectButtonGroup( int rows , int columns , int hgap , int vgap ) {
		this();
		setLayout( new GridLayout( rows , columns , hgap , vgap ) );
	}
	
	/**
	 * creates a select button group that displays the name of the group in 
	 * a titled border and arranges the buttons in the given number of rows
	 * and columns
	 * 
	 * @param groupName
	 * @param rows
	 * @param columns
	 */
	public SelectButtonGroup( String groupName , int rows , int columns , int hgap , int vgap ) {
		this( rows , columns , hgap , vgap );
		setBorder( BorderFactory.createTitledBorder( groupName ) );
	}
	
	/**
	 * adds and registers the given select button to this select button group
	 * 
	 * @param b
	 */
	public void add( ToolBarSelectButton b ) {
		b.addActionListener( this.selectListener );
		this.add( (Component) b );
		this.buttonsInGroup.add( b );
		
		//if we were adding the first button to this group, then it needs
		//to be selected, so that one button in this group is always selected
		if ( this.buttonsInGroup.size() == 1 ) {
			b.setSelected();
		}
		
		//otherwise, by default, this button will be unselected
		else {
			b.setUnselected();
		}
	}
	
	/**
	 * @return			the button in this button group that is selected, or null
	 * 					if there are no buttons in this button group
	 */
	public ToolBarSelectButton getSelected() {
		for ( ToolBarSelectButton btn : this.buttonsInGroup ) {
			if ( btn.isSelected() ) {
				return btn;
			}
		}
		return null;
	}
}
