package mc.iwclient.messaging;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class UserGraphicPopupPanelListener implements MouseListener {

	final protected UserGraphicPopupPanel m_gui;
	
	public UserGraphicPopupPanelListener( UserGraphicPopupPanel gui ) {
		this.m_gui = gui;
	}
	
	@Override
	public void mouseClicked( MouseEvent e ) {
		checkPopup( e );
	}

	@Override
	public void mouseEntered( MouseEvent e ) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited( MouseEvent e ) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed( MouseEvent e ) {
		checkPopup( e );
		
	}

	@Override
	public void mouseReleased( MouseEvent e ) {
		checkPopup( e );
	}
	
	public void checkPopup( MouseEvent e ) {
		if ( e.isPopupTrigger() ) {
			this.m_gui.displayPopupMenu( e.getX() , e.getY() );
		}
	}

}
