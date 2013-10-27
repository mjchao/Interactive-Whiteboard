package main.init;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import util.Text;

/**
 * We don't actually know the location of the database server, so we will have to ask the
 * user to input that on this startup UI.
 *
 */
public class StartUp extends JFrame
{
	public static String DATABASE_IP;
	public static int DATABASE_PORT;
	
	private static final long serialVersionUID = 1L;
	
	//layout of this UI
	final private GridLayout m_layout = new GridLayout(3, 2);
	final private int UI_LENGTH = 500;
	final private int UI_WIDTH = 150;
	//these components relate to requesting the IP address of the machine running the database server
	final private JLabel m_lblRequestIP = new JLabel();
	final private JTextField m_txtRequestIP = new JTextField("localhost");
	//these components relate to requesting the port on which the database server is running
	final private JLabel m_lblRequestPort = new JLabel();
	final private JTextField m_txtRequestPort = new JTextField("9000");
	//these components are options for the user
	final private JButton m_cmdExit = new JButton();
	final private JButton m_cmdBegin = new JButton();
	
	public StartUp()
	{
		//set the text each component displays
		this.m_lblRequestIP.setText(Text.STARTUP.REQUEST_IP_STRING);
		this.m_lblRequestPort.setText(Text.STARTUP.REQUEST_PORT_STRING);
		this.m_cmdExit.setText(Text.STARTUP.EXIT_STRING);
		this.m_cmdBegin.setText(Text.STARTUP.BEGIN_STRING);
		//add each graphical component to the UI
		setLayout(this.m_layout);
		add(this.m_lblRequestIP);
		add(this.m_txtRequestIP);
		add(this.m_lblRequestPort);
		add(this.m_txtRequestPort);
		add(this.m_cmdExit);
		add(this.m_cmdBegin);
		//define some UI properties
		setTitle(Text.STARTUP.STARTUP_TITLE_BAR);
		setSize(this.UI_LENGTH, this.UI_WIDTH);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);	//Do not allow users to just close the frame. they can use the exit button
		setVisible(true);
	}
	
	final public void addStartUpListener(StartUpListener l)
	{
		this.m_cmdExit.addActionListener(l);
		this.m_cmdBegin.addActionListener(l);
	}
	
	final public String getInputedServerIP()
	{
		return this.m_txtRequestIP.getText();
	}
	
	final public String getInputedServerPort()
	{
		return this.m_txtRequestPort.getText();
	}
	
	//called when there is no more need for the start up interface
	final public void hideInterface()
	{
		setVisible(false);
	}
}
