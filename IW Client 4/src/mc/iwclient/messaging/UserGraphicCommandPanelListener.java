package mc.iwclient.messaging;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

abstract public class UserGraphicCommandPanelListener implements ActionListener {

	final protected UserGraphicCommandPanel m_gui;
	
	public UserGraphicCommandPanelListener( UserGraphicCommandPanel gui ) {
		this.m_gui = gui;
	}
	
	@Override
	abstract public void actionPerformed( ActionEvent e );

}
