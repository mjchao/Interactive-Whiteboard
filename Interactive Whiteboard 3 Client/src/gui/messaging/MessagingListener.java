package gui.messaging;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import util.Text;

public class MessagingListener implements ActionListener
{

	final private Messaging m_gui;
	public MessagingListener(Messaging gui)
	{
		this.m_gui = gui;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		String command = e.getActionCommand();
		if (command.equals(Text.GUI.MESSAGING.VIEW_FRIENDS_LIST_STRING))
		{
			this.m_gui.loadFriendsList();
		} else if (command.equals(Text.GUI.MESSAGING.VIEW_PESTS_LIST_STRING))
		{
			this.m_gui.loadPestsList();
		} else if (command.equals(Text.GUI.MESSAGING.VIEW_PRIVATE_CHAT_STRING))
		{
			this.m_gui.loadPrivateChat();
		}
	}

}
