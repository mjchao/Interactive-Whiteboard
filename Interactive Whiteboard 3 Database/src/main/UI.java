package main;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.IOException;
import java.net.InetAddress;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import util.Text;

/**
 * Sets the user interface for starting and stopping the server
 */
final public class UI extends JFrame
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * displays the ip address of this machine
	 */
	final private JLabel m_lblIPAddress = new JLabel();	 	//This label displays the IP address of the machine
	
	/**
	 * button for starting and stopping server
	 */
	final private JButton m_cmdStartStop = new JButton(); 	//This button will display start/stop for starting/stopping the server
	
	/**
	 * panel with security options
	 */
	final private JPanel m_pnlSecurity = new JPanel();		//This panel contains buttons used for security commands
		
		/**
		 * button used to view the program log
		 */
		final private JButton m_cmdViewLog = new JButton();		//This button is to be used for viewing the program error log
		
		/**
		 * button used to block an ip address
		 */
		final private JButton m_cmdBlockIP = new JButton();		//This button is to be used for blocking IP Addresses
		
		/**
		 * button used to unblock an ip address
		 */
		final private JButton m_cmdUnblockIP = new JButton();	//This button is to be used to unblocking IP Addresses
		
		/**
		 * button used to exit safely
		 */
		final private JButton m_cmdExit = new JButton();		//This button is used to exit safely.
	
	/**
	 * Constructor.
	 * 
	 * @throws IOException 		if the IP Address of the machine cannot be determined
	 */
	public UI() throws IOException
	{
		setLayout(new BorderLayout());
		
		this.m_lblIPAddress.setText(Text.UI.IDENTIFY_IP_STRING + InetAddress.getLocalHost().toString());
		add(this.m_lblIPAddress, BorderLayout.NORTH);
		
		this.m_cmdStartStop.setText(Text.UI.START_STRING);
		add(this.m_cmdStartStop, BorderLayout.CENTER);
		
		this.m_pnlSecurity.setLayout(new FlowLayout(FlowLayout.CENTER));
			this.m_cmdViewLog.setText(Text.UI.VIEW_LOG_STRING);
			this.m_pnlSecurity.add(this.m_cmdViewLog);
		
			this.m_cmdBlockIP.setText(Text.UI.BLOCK_IP_STRING);
			this.m_pnlSecurity.add(this.m_cmdBlockIP);
			
			this.m_cmdUnblockIP.setText(Text.UI.UNBLOCK_IP_STRING);
			this.m_pnlSecurity.add(this.m_cmdUnblockIP);
			
			this.m_cmdExit.setText(Text.UI.EXIT_STRING);
			this.m_pnlSecurity.add(this.m_cmdExit);
		add(this.m_pnlSecurity, BorderLayout.SOUTH);
		
		setVisible(true);
		setTitle(Text.UI.DATABASE_TITLE);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setSize(500, 500);
	}
	
	/**
	 * adds listeners to each button
	 * 
	 * @param l			a <code>UIListener</code>, a listener for the buttons
	 */
	final public void addUIListener(UIListener l)
	{
		this.m_cmdStartStop.addActionListener(l);
		this.m_cmdViewLog.addActionListener(l);
		this.m_cmdBlockIP.addActionListener(l);
		this.m_cmdUnblockIP.addActionListener(l);
		this.m_cmdExit.addActionListener(l);
	}
	
	/**
	 * sets the interface to reflect a server that has been started
	 */
	final public void reflectServerStarted()
	{
		//Set the start/stop button to display the Stop text
		this.m_cmdStartStop.setText(Text.UI.STOP_STRING);
	}
	
	/**
	 * sets the interface to reflect a server that has been stopped
	 */
	final public void reflectServerStopped()
	{
		//Set the start/stop button to display the Start text
		this.m_cmdStartStop.setText(Text.UI.START_STRING);
	}

}
