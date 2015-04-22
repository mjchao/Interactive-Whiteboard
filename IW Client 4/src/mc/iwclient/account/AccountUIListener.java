package mc.iwclient.account;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import mc.iwclient.util.Text;
import mc.iwclient.util.Text.Messaging;
import mc.iwclient.util.Text.Room;

public class AccountUIListener implements ActionListener {

	final private AccountUI m_gui;
	
	public AccountUIListener( AccountUI gui ) {
		this.m_gui = gui;
	}
	
	@Override
	public void actionPerformed( ActionEvent e ) {
		String command = e.getActionCommand();
		if ( command.equals( Text.Account.VIEW_NEWS_COMMAND ) ) {
			this.m_gui.loadNewsInterface();
		}
		else if ( command.equals( Text.Account.CHANGE_ACCOUNT_INFO_COMMAND ) ) {
			this.m_gui.loadAccountInfoInterface();
		}
		else if ( command.equals( Text.Account.CHANGE_USER_INFO_COMMAND ) ) {
			this.m_gui.loadUserInfoInterface();
		}
		else if ( command.equals( Text.Account.LOGOUT_COMMAND ) ) {
			this.m_gui.confirmLogout();
		}
		else if ( command.equals( Messaging.VIEW_FRIENDS_LIST_COMMAND ) ) {
			this.m_gui.loadFriendsListInterface();
		}
		else if ( command.equals( Messaging.VIEW_PESTS_LIST_COMMAND ) ) {
			this.m_gui.loadPestsListInterface();
		}
		else if ( command.equals( Messaging.VIEW_PM_COMMAND ) ) {
			this.m_gui.loadPrivateMessagingInterface();
		}
		else if ( command.equals( Room.BROWSE_ROOMS_COMMAND ) ) {
			this.m_gui.loadRoomSelectionInterface();
		}
	}

	
}
