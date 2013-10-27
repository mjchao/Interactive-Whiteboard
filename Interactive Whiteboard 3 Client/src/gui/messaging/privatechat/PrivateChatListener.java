package gui.messaging.privatechat;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import util.Text;

public class PrivateChatListener implements ActionListener
{

	final private PrivateChat m_gui;
	public PrivateChatListener(PrivateChat gui)
	{
		this.m_gui = gui;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		String command = e.getActionCommand();
		if (command.equals(Text.GUI.MESSAGING.PRIVATECHAT.RELIST_STRING))
		{
			this.m_gui.refreshConversations();
		}
		
	}

}
