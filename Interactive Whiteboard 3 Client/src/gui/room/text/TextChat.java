package gui.room.text;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import util.Text;

final public class TextChat extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//graphical components
	final private BorderLayout TEXT_CHAT_LAYOUT = new BorderLayout();
	final private JTextArea txtHistory = new JTextArea(10, 5);
	final private JScrollPane scrollHistory = new JScrollPane(this.txtHistory);
	final private FlowLayout SEND_LAYOUT = new FlowLayout(FlowLayout.CENTER);
	final private JPanel pnlSend = new JPanel();
		final private JTextField txtSend = new JTextField(25);
		final private JButton cmdSend = new JButton();
	
	public TextChat()
	{
		setLayout(this.TEXT_CHAT_LAYOUT);
		add(this.scrollHistory, BorderLayout.CENTER);
		this.pnlSend.setLayout(this.SEND_LAYOUT);
			this.pnlSend.add(this.txtSend);
			this.cmdSend.setText(Text.GUI.ROOM.TEXT.SEND_MESSAGE_STRING);
			this.pnlSend.add(this.cmdSend);
		add(this.pnlSend, BorderLayout.SOUTH);
	}
	
	final public void addTextChatListener(TextChatListener l)
	{
		this.cmdSend.addActionListener(l);
		this.txtSend.addKeyListener(l);
	}
	
	final public void addMessage(String senderDisplayName, String senderMessage)
	{
		String lineToAdd = senderDisplayName + ": " + senderMessage;
		lineToAdd = formatMessage(lineToAdd);
		this.txtHistory.setText(this.txtHistory.getText() + "\n" + lineToAdd);
		this.scrollHistory.setViewportView(this.txtHistory);
		this.scrollHistory.getVerticalScrollBar().setValue(this.scrollHistory.getVerticalScrollBar().getMaximum());
	}
	
	final public static int MAX_CHARS_PER_LINE = 50;
	final public static String formatMessage(String message)
	{
		String rtn = message;
		int charCount = 0;
		for (int i = 0; i < rtn.length(); i++)
		{
			charCount++;
			if (charCount > MAX_CHARS_PER_LINE)
			{
				if (rtn.substring(i, i+1).equals(" "))
				{
					rtn = rtn.substring(0, i) + "\n     " + rtn.substring(i, rtn.length());
					charCount = 0;
				}
			}
			if (charCount > MAX_CHARS_PER_LINE + 25)
			{
				rtn = rtn.substring(0, i) + "\n     " + rtn.substring(i, rtn.length());
				charCount = 0;
			}
		}
		return rtn;
	}
	
	final public String getUserInputtedMessage()
	{
		return this.txtSend.getText();
	}
	
	final public void clearSendTextField()
	{
		this.txtSend.setText("");
	}
}
