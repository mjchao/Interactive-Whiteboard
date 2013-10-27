package main.server;

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

final public class ServerUI extends JFrame
{
	private static final long serialVersionUID = 1L;

	//properties of the main frame
	final private static int LENGTH = 500;
	@SuppressWarnings("hiding")
	final private static int WIDTH = 500;
	final private static BorderLayout m_layout = new BorderLayout();
	
	final private JLabel m_lblInfo = new JLabel();			//displays information about the machine the server runs on		
	final private JButton m_cmdStartStop = new JButton();	//start/stop server
	final private JPanel m_pnlSecurity = new JPanel();		//panel that contains security options
		final private FlowLayout m_securityLayout = new FlowLayout(FlowLayout.CENTER);
		final private JButton m_cmdBlockIP = new JButton();	//blocks an IP address
		final private JButton m_cmdUnblockIP = new JButton(); //unblocks an IP address
		final private JButton m_cmdNameChange = new JButton();	//generates a new name change code
		final private JButton m_cmdExit = new JButton();		//exit safely
	
	public ServerUI() throws IOException
	{
		//set component textual properties
		String ipAddress = InetAddress.getLocalHost().toString();
		this.m_lblInfo.setText(Text.SERVERUI.SHOW_IP_STRING + ipAddress);
		this.m_cmdStartStop.setText(Text.SERVERUI.START_STRING);
		this.m_cmdBlockIP.setText(Text.SERVERUI.BLOCK_IP_STRING);
		this.m_cmdUnblockIP.setText(Text.SERVERUI.UNBLOCK_IP_STRING);
		this.m_cmdNameChange.setText(Text.SERVERUI.NAME_CHANGE_STRING);
		this.m_cmdExit.setText(Text.SERVERUI.EXIT_STRING);
		
		//set layouts
		setLayout(m_layout);
		this.m_pnlSecurity.setLayout(this.m_securityLayout);
		
		//add components
		add(this.m_lblInfo, BorderLayout.NORTH);
		add(this.m_cmdStartStop, BorderLayout.CENTER);
		this.m_pnlSecurity.add(this.m_cmdBlockIP);
		this.m_pnlSecurity.add(this.m_cmdUnblockIP);
		this.m_pnlSecurity.add(this.m_cmdNameChange);
		this.m_pnlSecurity.add(this.m_cmdExit);
		add(this.m_pnlSecurity, BorderLayout.SOUTH);
		
		//define some properties of the main frame
		setTitle(Text.SERVERUI.SERVER_TITLE_BAR);
		setSize(LENGTH, WIDTH);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setVisible(true);
	}
	
	final public void addServerListener(ServerUIListener l)
	{
		this.m_cmdStartStop.addActionListener(l);
		this.m_cmdBlockIP.addActionListener(l);
		this.m_cmdUnblockIP.addActionListener(l);
		this.m_cmdNameChange.addActionListener(l);
		this.m_cmdExit.addActionListener(l);
	}
	
	final public boolean isServerStarted()
	{
		return this.m_cmdStartStop.getText().equals(Text.SERVERUI.STOP_STRING);
	}
	
	final public void reflectServerState(boolean shouldReflectStarted)
	{
		if (shouldReflectStarted)
		{
			this.m_cmdStartStop.setText(Text.SERVERUI.STOP_STRING);
		} else
		{
			this.m_cmdStartStop.setText(Text.SERVERUI.START_STRING);
		}
	}
}
