package gui.login;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.net.InetAddress;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import net.MESSAGES;

import util.Text;

//this is the user interface for logging in. note that all graphical elements are class members
final public class Login extends JFrame
{

	public static String m_username;
	public static String m_password;
	public static String m_serverIP;
	
	private static final long serialVersionUID = 1L;
	
	//frame properties:
	final private static BorderLayout LAYOUT = new BorderLayout();
	final private static int LENGTH = 500;
	@SuppressWarnings("hiding")
	final private static int WIDTH = 200;
	//frame components:
	final private JLabel lblInfo = new JLabel();
	final private GridLayout PNL_LOGIN_LAYOUT = new GridLayout(5, 2);
	final private JPanel pnlLogin = new JPanel();
		final private JLabel lblServerIP = new JLabel();
		final private JTextField txtServerIP = new JTextField("localhost");
		final private JLabel lblServerPort = new JLabel();
		final private JTextField txtServerPort = new JTextField("9999");
		final private JLabel lblUsername = new JLabel();
		final private JTextField txtUsername = new JTextField("mjchao");
		final private JLabel lblPassword = new JLabel();
		final private JPasswordField txtPassword = new JPasswordField("abcde");
		final private JButton cmdRegister = new JButton();
		final private JButton cmdLogin = new JButton();
	
	public Login() throws IOException
	{
		//add textual properties
		this.lblInfo.setText(Text.GUI.LOGIN.DISPLAY_IP_STRING + InetAddress.getLocalHost());
		this.lblServerIP.setText(Text.GUI.LOGIN.REQUEST_SERVER_IP_STRING);
		this.lblServerPort.setText(Text.GUI.LOGIN.REQUEST_SERVER_PORT_STRING);
		this.lblUsername.setText(Text.GUI.LOGIN.REQUEST_USERNAME_STRING);
		this.lblPassword.setText(Text.GUI.LOGIN.REQUEST_PASSWORD_STRING);
		this.cmdRegister.setText(Text.GUI.LOGIN.REGISTER_STRING);
		this.cmdLogin.setText(Text.GUI.LOGIN.LOGIN_STRING);
		//add graphical components
		setLayout(LAYOUT);
		add(this.lblInfo, BorderLayout.NORTH);
			this.pnlLogin.setLayout(this.PNL_LOGIN_LAYOUT);
			this.pnlLogin.add(this.lblServerIP);
			this.pnlLogin.add(this.txtServerIP);
			this.pnlLogin.add(this.lblServerPort);
			this.pnlLogin.add(this.txtServerPort);
			this.pnlLogin.add(this.lblUsername);
			this.pnlLogin.add(this.txtUsername);
			this.pnlLogin.add(this.lblPassword);
			this.pnlLogin.add(this.txtPassword);
			this.pnlLogin.add(this.cmdRegister);
			this.pnlLogin.add(this.cmdLogin);
		add(this.pnlLogin, BorderLayout.CENTER);
		//define some frame properties
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle(Text.GUI.LOGIN.FRAME_TITLE);
		setSize(LENGTH, WIDTH);
		setVisible(true);
	}
	
	final public void addLoginListener(LoginListener l)
	{
		this.cmdRegister.addActionListener(l);
		this.cmdLogin.addActionListener(l);
	}
	
	final public String getServerIP()
	{
		return this.txtServerIP.getText();
	}
	
	final public String getServerPort()
	{
		return this.txtServerPort.getText();
	}
	
	final public String getUsername()
	{
		return MESSAGES.substituteForMessageDelimiters(this.txtUsername.getText());
	}
	
	final public String getPassword()
	{
		char[] password = this.txtPassword.getPassword();
		String rtn = "";
		for (int charIndex = 0; charIndex < password.length; charIndex++)
		{
			rtn += password[charIndex];
			password[charIndex] = '<';
		}
		return MESSAGES.substituteForMessageDelimiters(rtn);
	}
	
	final public void hideUserInterface()
	{
		setVisible(false);
	}
}
