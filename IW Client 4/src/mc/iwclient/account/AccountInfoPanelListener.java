package mc.iwclient.account;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import mc.iwclient.util.Text;

public class AccountInfoPanelListener implements ActionListener {

	final private AccountInfoPanel m_gui;
	
	public AccountInfoPanelListener( AccountInfoPanel gui ) {
		this.m_gui = gui;
	}

	@Override
	public void actionPerformed( ActionEvent e ) {
		String command = e.getActionCommand();
		if ( command.equals( Text.Account.AccountInfo.RESET_COMMAND ) ) {
			this.m_gui.resetFields();
		}
		else if ( command.equals( Text.Account.AccountInfo.UPDATE_COMMAND ) ) {
			//TODO networking
		}
	}
}
