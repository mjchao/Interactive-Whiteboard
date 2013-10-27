package gui.room.text;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import util.Text;

import net.InvalidMessageException;
import net.roomserver.text.TextStepConnection;

final public class TextChatListener implements ActionListener, KeyListener 
{

	final private TextChat m_gui;
	final private TextStepConnection m_textStepConnection;
	
	public TextChatListener(TextChat gui, TextStepConnection textStepConnection)
	{
		this.m_textStepConnection = textStepConnection;
		this.m_gui = gui;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		String command = e.getActionCommand();
		if (command.equals(Text.GUI.ROOM.TEXT.SEND_MESSAGE_STRING))
		{
			sendMessage();
		}
	}

	@Override
	public void keyPressed(KeyEvent e) 
	{
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
		{
			sendMessage();
		}
	}
	
	final private void sendMessage()
	{
		String messageToSend = this.m_gui.getUserInputtedMessage();
		if (!messageToSend.equals(""))
		{
			try 
			{
				this.m_textStepConnection.addLineOfChatHistory(messageToSend);
			} catch (IOException e) 
			{
				this.m_textStepConnection.displayConnectionLostMessage();
			} catch (InvalidMessageException e) 
			{
				this.m_textStepConnection.handleInvalidMessageException(e);
			}
			this.m_gui.clearSendTextField();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) 
	{
		// TODO Auto-generated method stub	
	}

}
