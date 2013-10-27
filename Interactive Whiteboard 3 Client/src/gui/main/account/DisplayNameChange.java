package gui.main.account;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import util.Text;

public class DisplayNameChange extends JPanel
{
	private static final long serialVersionUID = 1L;

	//this UI's properties:
	final private static GridLayout LAYOUT = new GridLayout(5, 2);
	//graphical components:
	final private JLabel m_lblPassword = new JLabel();
	final private JPasswordField m_txtPassword = new JPasswordField();
	final private JLabel m_lblNameChangeCode = new JLabel();
	final private JTextField m_txtNameChangeCode = new JTextField();
	final private JLabel m_lblNewName = new JLabel();
	final private JTextField m_txtNewName = new JTextField();
	final private JLabel m_lblConfirmNewName = new JLabel();
	final private JTextField m_txtConfirmNewName = new JTextField();
	final private JButton m_cmdClear = new JButton();
	final private JButton m_cmdChange = new JButton();
	
	public DisplayNameChange()
	{
		//set component textual properties
		this.m_lblPassword.setText(Text.GUI.MAIN.DISPLAYNAMECHANGE.REQUEST_PASSWORD_STRING);
		this.m_lblNameChangeCode.setText(Text.GUI.MAIN.DISPLAYNAMECHANGE.REQUEST_NAME_CHANGE_CODE_STRING);
		this.m_lblNewName.setText(Text.GUI.MAIN.DISPLAYNAMECHANGE.REQUEST_NEW_DISPLAY_NAME_STRING);
		this.m_lblConfirmNewName.setText(Text.GUI.MAIN.DISPLAYNAMECHANGE.REQUEST_CONFIRM_NEW_DISPLAY_NAME_STRING);
		this.m_cmdClear.setText(Text.GUI.MAIN.DISPLAYNAMECHANGE.CLEAR_STRING);
		this.m_cmdChange.setText(Text.GUI.MAIN.DISPLAYNAMECHANGE.CHANGE_DISPLAY_NAME_COMMAND_STRING);
		//add components
		setLayout(LAYOUT);
		add(this.m_lblPassword);
		add(this.m_txtPassword);
		add(this.m_lblNameChangeCode);
		add(this.m_txtNameChangeCode);
		add(this.m_lblNewName);
		add(this.m_txtNewName);
		add(this.m_lblConfirmNewName);
		add(this.m_txtConfirmNewName);
		add(this.m_cmdClear);
		add(this.m_cmdChange);
	}
	
	final public void addDisplayNameChangeListener(DisplayNameChangeListener l)
	{
		this.m_cmdClear.addActionListener(l);
		this.m_cmdChange.addActionListener(l);
	}
	
	final private static String getPasswordFromPasswordField(JPasswordField txtPassword)
	{
		char[] passwordChars = txtPassword.getPassword();
		String rtnPassword = "";
		for (int charIndex = 0; charIndex < passwordChars.length; charIndex++)
		{
			rtnPassword += passwordChars[charIndex];
			passwordChars[charIndex] = '0';
		}
		return rtnPassword;
	}
	
	final public String getPassword()
	{
		return getPasswordFromPasswordField(this.m_txtPassword);
	}
	
	final public String getNameChangeCode()
	{
		return this.m_txtNameChangeCode.getText();
	}
	
	final public String getNewName()
	{
		return this.m_txtNewName.getText();
	}
	
	final public String getConfirmNewName()
	{
		return this.m_txtConfirmNewName.getText();
	}
	
	final public void clearAllFields()
	{
		this.m_txtPassword.setText("");
		this.m_txtNameChangeCode.setText("");
		this.m_txtNewName.setText("");
		this.m_txtConfirmNewName.setText("");
	}
}
