package mc.iwclient.account;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import mc.iwclient.util.Text;

public class UserInfoPanelListener implements ActionListener {

	final private UserInfoPanel m_gui;
	
	public UserInfoPanelListener( UserInfoPanel gui ) {
		this.m_gui = gui;
	}
	
	@Override
	public void actionPerformed( ActionEvent e ) {
		String command = e.getActionCommand();
		if ( command.equals( Text.Account.UserInfo.RESET_COMMAND ) ) {
			this.m_gui.resetFields();
		}
		else if ( command.equals( Text.Account.UserInfo.UPDATE_COMMAND ) ) {
			//TODO networking
		}
		
	}

}
