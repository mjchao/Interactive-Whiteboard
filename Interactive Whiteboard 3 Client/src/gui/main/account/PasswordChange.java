package gui.main.account;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import util.Text;

public class PasswordChange extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	//this UI's properties
	final private static GridLayout LAYOUT = new GridLayout(4, 2);
	//graphical components:
	final private JLabel lblCurrentPassword = new JLabel();
	final private JPasswordField txtCurrentPassword = new JPasswordField();
	final private JLabel lblNewPassword = new JLabel();
	final private JPasswordField txtNewPassword = new JPasswordField();
	final private JLabel lblConfirmNewPassword = new JLabel();
	final private JPasswordField txtConfirmNewPassword = new JPasswordField();
	final private JButton cmdClear = new JButton();
	final private JButton cmdChange = new JButton();
	
	public PasswordChange()
	{
		//set textual properties of components:
		this.lblCurrentPassword.setText(Text.GUI.MAIN.PASSWORDCHANGE.REQUEST_CURRENT_PASSWORD_STRING);
		this.lblNewPassword.setText(Text.GUI.MAIN.PASSWORDCHANGE.REQUEST_NEW_PASSWORD_STRING);
		this.lblConfirmNewPassword.setText(Text.GUI.MAIN.PASSWORDCHANGE.REQUEST_CONFIRM_NEW_PASSWORD_STRING);
		this.cmdClear.setText(Text.GUI.MAIN.PASSWORDCHANGE.CLEAR_STRING);
		this.cmdChange.setText(Text.GUI.MAIN.PASSWORDCHANGE.CHANGE_PASSWORD_COMMAND_STRING);
		//add the components
		setLayout(LAYOUT);
		add(this.lblCurrentPassword);
		add(this.txtCurrentPassword);
		add(this.lblNewPassword);
		add(this.txtNewPassword);
		add(this.lblConfirmNewPassword);
		add(this.txtConfirmNewPassword);
		add(this.cmdClear);
		add(this.cmdChange);
	}
	
	final public void addPasswordChangeListener(PasswordChangeListener l)
	{
		this.cmdClear.addActionListener(l);
		this.cmdChange.addActionListener(l);
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
	
	final public String getCurrentPassword()
	{
		return getPasswordFromPasswordField(this.txtCurrentPassword);
	}
	
	final public String getNewPassword()
	{
		return getPasswordFromPasswordField(this.txtNewPassword);
	}
	
	final public String getConfirmNewPassword()
	{
		return getPasswordFromPasswordField(this.txtConfirmNewPassword);
	}
	
	final public void clearAllFields()
	{
		this.txtCurrentPassword.setText("");
		this.txtNewPassword.setText("");
		this.txtConfirmNewPassword.setText("");
	}

}
