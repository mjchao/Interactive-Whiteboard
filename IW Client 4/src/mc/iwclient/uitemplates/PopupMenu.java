package mc.iwclient.uitemplates;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JPopupMenu;

public class PopupMenu extends JPopupMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public PopupMenu() {
		this.addFocusListener( new FocusListener() {

			@Override
			public void focusGained( FocusEvent e ) {
				//ignore
				System.out.println( "gained focus" );
			}

			@Override
			public void focusLost( FocusEvent e ) {
				System.out.println( "ok" );
				setVisible( false );
				revalidate();
			}
			
		});
	}
	
	

}
