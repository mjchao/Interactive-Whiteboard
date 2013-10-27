package util.log;

import java.awt.GridLayout;
import java.util.Calendar;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

final public class Log extends JFrame
{
	private static final long serialVersionUID = 1L;

	final private static int LENGTH = 500;
	@SuppressWarnings("hiding")	//we will ignore this problem
	final public static int WIDTH = 700;
	final private JTextArea m_txtLog = new JTextArea();
	final private JScrollPane m_scrollLog = new JScrollPane(this.m_txtLog);
	
	public Log()
	{
		this.m_txtLog.setText("PROGRAM LOG     Start: " + Calendar.getInstance().getTime() + "\n");
		this.m_txtLog.setEditable(false);
		
		setLayout(new GridLayout(1, 1));
		add(this.m_scrollLog);
		
		setTitle("Program Log");
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		setSize(LENGTH, WIDTH);
		setVisible(false);
	}
	
	final public void logMessage(String message)
	{
		String messageToAdd = message;
		//add the time to the message
		String time = Calendar.getInstance().getTime().toString();
		messageToAdd = time + ":          " +  messageToAdd;
		//log the message
		this.m_txtLog.setText(this.m_txtLog.getText() + "\n" + messageToAdd);
		setVisible(true);
	}
}
