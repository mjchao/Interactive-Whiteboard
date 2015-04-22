package mc.iwclient.login;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import mc.iwclient.account.AccountUI;
import mc.iwclient.uitemplates.InputField;
import mc.iwclient.uitemplates.OptionMenu;
import mc.iwclient.uitemplates.PasswordField;
import mc.iwclient.util.Text;

/**
 * Main login user interface for connecting to the interactive whiteboard server.
 * Consists of three parts: login interface, register interface, connection settings
 * interface
 * 
 * @author mjchao
 *
 */
public class LoginUI extends JFrame {

	final public static void main( String[] args ) {
		LoginUI l = new LoginUI();
		LoginPreferencesManager.loadPreferences( l );
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * menu from which the user selects an interface (login, register, or
	 * modify connection settings) to view
	 */
	final private OptionMenu optsInterface;
	
	/**
	 * contains the interface that the user views (login, register, or modify
	 * conneciton settings)
	 */
	final protected JPanel pnlInterface;
	
	/**
	 * displays an error message if necessary
	 */
	final private ErrorMessageLabel lblErrorMessage;
	
	/**
	 * interface for logging in
	 */
	final private LoginPanel pnlLogin;
	
	/**
	 * interface for registering
	 */
	final private RegisterPanel pnlRegister;
	
	/**
	 * interface for modifying connection settings
	 */
	final protected SettingsPanel pnlSettings;
	
	public LoginUI() {
		setLayout( new BoxLayout( this.getContentPane() , BoxLayout.Y_AXIS ) );
		
		this.optsInterface = new OptionMenu( false );
			this.optsInterface.addOption( Text.Login.OPT_LOGIN );
			this.optsInterface.addOption( Text.Login.OPT_REGISTER );
			this.optsInterface.addOption( Text.Login.OPT_SETTINGS );
		add( this.optsInterface );
		
		this.lblErrorMessage = new ErrorMessageLabel();
		add( this.lblErrorMessage );
			
		//associate each of the interfaces with the corresponding 
		//option text that selects the interface
		//for example: "Login" corresponds to the login interface
		CardLayout selectInterfaceLayout = new CardLayout();
		this.pnlInterface = new JPanel( selectInterfaceLayout );
			this.pnlLogin = new LoginPanel();
			this.pnlLogin.addLoginPanelListener( new LoginPanelListener() );
			this.pnlInterface.add( this.pnlLogin , Text.Login.OPT_LOGIN );
			
			this.pnlRegister = new RegisterPanel();
			this.pnlInterface.add( this.pnlRegister , Text.Login.OPT_REGISTER );
			
			this.pnlSettings = new SettingsPanel();
			this.pnlSettings.addSettingsPanelListener( new SettingsPanelListener() );
			this.pnlInterface.add( this.pnlSettings , Text.Login.OPT_SETTINGS );
		add( this.pnlInterface );
		
		SelectInterfaceListener l = new SelectInterfaceListener( selectInterfaceLayout );
		this.optsInterface.addActionListener( l );
		
		//the default shown interface is the login interface,
		//so set the title accordingly
		setTitle( Text.Login.LOGIN_TITLE );
		setVisible( true );
		pack();
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
	}
	
	/**
	 * given some preferences data (for login or for connection settings), updates
	 * the login user interface to reflect those preferences
	 * 
	 * @param data			data read from the saved data file
	 */
	public void processPreferenceData( String data ) {
		StringTokenizer st = new StringTokenizer( data );
		String interfaceID = st.nextToken();
		String preference = "";
		while( st.hasMoreTokens() ) {
			preference += st.nextToken();
		}
		if ( interfaceID.equals( LoginPreferencesManager.LOGIN_PREFERENCE_TAG ) ) {
			try {
				this.pnlLogin.processPreferenceData( preference );
			}
			catch ( RuntimeException e ) {
				//ignore if there is an error with preference data
			}
		}
		else if ( interfaceID.equals( LoginPreferencesManager.CONNECTION_SETTINGS_PREFERENE_TAG ) ) {
			try {
				this.pnlSettings.processPreferenceData( preference );
			} 
			catch ( RuntimeException e ) {
				//ignore if there is an error with preference data
			}
		}
	}
	
	/**
	 * @return			preference data for the login interface
	 */
	public String getLoginInterfacePreferences() {
		if ( this.pnlLogin.shouldSaveUsername() ) {
			return LoginPreferencesManager.LOGIN_PREFERENCE_TAG + LoginPreferencesManager.PREFERENCE_DELIMITER +
					LoginPreferencesManager.USERNAME_TAG + LoginPreferencesManager.PREFERENCE_ASSIGNMENT_TAG +
					this.pnlLogin.getInputedUsername() + "\n";
		} 
		else {
			return "";
		}
	}
	
	/**
	 * @return			prefernece data for the connection settings interface
	 */
	public String getConnectionSettingsPreferences() {
		String rtn = "";
		rtn += LoginPreferencesManager.CONNECTION_SETTINGS_PREFERENE_TAG + LoginPreferencesManager.PREFERENCE_DELIMITER +
				LoginPreferencesManager.SERVER_IP_TAG + LoginPreferencesManager.PREFERENCE_ASSIGNMENT_TAG + 
				this.pnlSettings.getInputedServerIP() + "\n";
		rtn += LoginPreferencesManager.CONNECTION_SETTINGS_PREFERENE_TAG + LoginPreferencesManager.PREFERENCE_DELIMITER +
				LoginPreferencesManager.PORT_TAG + LoginPreferencesManager.PREFERENCE_ASSIGNMENT_TAG +
				this.pnlSettings.getInputedPort() + "\n";
		return rtn;
	}
	
	private class SelectInterfaceListener implements ActionListener {

		/**
		 * layout manager that shows and hides interfaces as needed
		 */
		final private CardLayout m_interfaceLayout;
		
		public SelectInterfaceListener( CardLayout interfaceLayout ) {
			this.m_interfaceLayout = interfaceLayout;
		}
		
		@Override
		public void actionPerformed( ActionEvent e ) {
			String selectedInterface = e.getActionCommand();
			this.m_interfaceLayout.show( LoginUI.this.pnlInterface , selectedInterface );
			if ( selectedInterface.equals( Text.Login.OPT_LOGIN ) ) {
				LoginUI.this.setTitle( Text.Login.LOGIN_TITLE );
			}
			else if ( selectedInterface.equals( Text.Login.OPT_REGISTER ) ) {
				LoginUI.this.setTitle( Text.Login.REGISTER_TITLE );
			}
			else if ( selectedInterface.equals( Text.Login.OPT_SETTINGS ) ) {
				LoginUI.this.setTitle( Text.Login.SETTINGS_TITLE );
			}
		}
		
	}
	
	/**
	 * Flashes error messages
	 * 
	 * @author mjchao
	 *
	 */
	private class ErrorMessageLabel extends JLabel {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		final public static int DEFAULT_FLASH_DURATION = 5000;
		final public static int DEFAULT_FLASH_FREQUENCY = 500;
		
		protected Timer m_timer;
		
		/**
		 * amount of time remaining for flash
		 */
		protected int m_flashDurationLeft = 0;
		
		public ErrorMessageLabel() {
			this.setForeground( Color.red );
		}
		
		/**
		 * flashes the given message on the error message label
		 * 
		 * @param message			the message to display
		 */
		public void flashMessage( String message ) {
			flashMessage( message , DEFAULT_FLASH_DURATION , DEFAULT_FLASH_FREQUENCY );
		}
		
		public void flashMessage( String message , int duration , int frequency ) {
			this.setText( message );
			this.setToolTipText( message );
			flash( duration , frequency );
			LoginUI.this.pack();
		}
		
		/**
		 * flashes the label for the specified duration and the given frequency
		 * 
		 * @param duration			how long to flash
		 * @param frequency			how often to flash
		 */
		private void flash( int duration , final int frequency ) {
			this.m_timer = new Timer();
			this.m_flashDurationLeft = duration;
			
			this.m_timer.schedule( new TimerTask() {
				
				private boolean m_blackColor = false;
				
				@Override
				public void run() {
					if ( ErrorMessageLabel.this.m_flashDurationLeft > 0 ) {
						ErrorMessageLabel.this.m_flashDurationLeft -= frequency;
						if ( this.m_blackColor ) {
							ErrorMessageLabel.this.setForeground( Color.red );
						}
						else {
							ErrorMessageLabel.this.setForeground( Color.black );
						}
						this.m_blackColor = !this.m_blackColor;
					} else {
						ErrorMessageLabel.this.m_timer.cancel();
					}
				}
				
			}, 0 , frequency );
		}
	}
	
	/**
	 * flashes an error message on the error messages label 
	 * 
	 * @param message			the message to flash
	 */
	public void flashErrorMessage( String message ) {
		this.lblErrorMessage.flashMessage( message );
	}

	/**
	 * interface for logging in to the interactive whiteboard client
	 * 
	 * @author mjchao
	 *
	 */
	private class LoginPanel extends JPanel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		final private InputField inptUsername;
		
		final private PasswordField inptPassword;
		
		/**
		 * can be checked to indicate that the user wants his/her
		 * username saved the next time s/he logs in
		 */
		final private JCheckBox chkSaveUsername;
		
		final private JButton cmdLogin;
		
		public LoginPanel() {
			//set the layout as a stack of input fields and buttons
			setLayout( new GridLayout( 0 , 1 ) );
			
			//add extra things just to make the input field sizes smaller and
			//look nicer
			add( Box.createVerticalGlue() );
			
			this.inptUsername = new InputField( Text.Login.INPUT_USERNAME );
			add( this.inptUsername );
			
			add( Box.createVerticalGlue() );
			
			this.inptPassword = new PasswordField( Text.Login.INPUT_PASSWORD );
			add( this.inptPassword );
			
			this.chkSaveUsername = new JCheckBox( Text.Login.OPT_SAVE_USERNAME );
			add( this.chkSaveUsername );
			
			this.cmdLogin = new JButton( Text.Login.LOGIN_COMMAND );
			add( this.cmdLogin );
		}
		
		public void addLoginPanelListener( LoginPanelListener l ) {
			this.cmdLogin.addActionListener( l );
		}
		
		/**
		 * processes some data about the user's login preferences
		 * and udpates the user interface to reflect those preferences
		 * 
		 * @param data			data about the user's login preferences
		 */
		public void processPreferenceData( String data ) {
			if ( data.trim().equals( "" ) ) {
				return;
			}
			else {
				String[] preference = data.split( LoginPreferencesManager.PREFERENCE_ASSIGNMENT_TAG );
				if ( preference[ 0 ].equals( LoginPreferencesManager.USERNAME_TAG ) ) {
					
					//don't load the username if the user had
					//clicked the "reset" in the connection settings interface
					//... a bit hacky...
					if ( !LoginUI.this.pnlSettings.isShowing() ) {
						this.chkSaveUsername.setSelected( true );
						this.inptUsername.setInput( preference[ 1 ] );
					}
				}
			}
		}
		
		/**
		 * @return			if the user checked the save username check box
		 */
		public boolean shouldSaveUsername() {
			return this.chkSaveUsername.isSelected();
		}
		
		/**
		 * @return			the username inputed by the user
		 */
		public String getInputedUsername() {
			return this.inptUsername.getInput();
		}
	}
	
	private class LoginPanelListener implements ActionListener {

		public LoginPanelListener() {
			
		}

		@Override
		public void actionPerformed( ActionEvent e ) {
			String command = e.getActionCommand();
			if ( command.equals( Text.Login.LOGIN_COMMAND ) ) {
				LoginUI.this.login();
			}
		}
	}
	
	/**
	 * attempts to log in to the interactive whiteboard server
	 */
	public void login() {
		
		//save the connection preferences if the login was successful
		//that way, the user doesn't need to re-input information
		//the next time s/he wants to log in
		LoginPreferencesManager.savePreferenceData( this );
		
		//TODO load account ui
		AccountUI.show( this );
	}
	
	/**
	 * interface for registering
	 * 
	 * @author mjchao
	 *
	 */
	private class RegisterPanel extends JPanel {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		final private InputField inptUsername;
		
		final private InputField inptDisplayName;
		
		final private InputField inptPassword;
		
		final private InputField inptConfirmPassword;
		
		final private InputField inptEmail;
		
		final private JButton cmdRegister;
		
		public RegisterPanel() {
			setLayout( new GridLayout( 0 , 1 ) );
			
			this.inptUsername = new InputField( Text.Login.INPUT_USERNAME );
			add( this.inptUsername );
			
			this.inptDisplayName = new InputField( Text.Login.INPUT_DISPLAY_NAME );
			add( this.inptDisplayName );
			
			this.inptPassword = new InputField( Text.Login.INPUT_PASSWORD );
			add( this.inptPassword );
			
			this.inptConfirmPassword = new InputField( Text.Login.INPUT_CONFIRM_PASSWORD );
			add( this.inptConfirmPassword );
			
			this.inptEmail = new InputField( Text.Login.INPUT_EMAIL );
			add( this.inptEmail );
			
			this.cmdRegister = new JButton( Text.Login.REGISTER_COMMAND );
			add( this.cmdRegister );
		}

	}
	
	/**
	 * interface for modifying connection settings
	 * 
	 * @author mjchao
	 *
	 */
	private class SettingsPanel extends JPanel {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * displays the user's IP address
		 */
		final private InputField inptMyIP;
		
		final private InputField inptServerIP;
		
		final private InputField inptPort;
		
		/**
		 * holds some commands for modifying connection settings
		 */
		final private JPanel pnlCommands;
		
		final private JButton cmdReset;
		
		final private JButton cmdSave;
		
		public SettingsPanel() {
			setLayout( new GridLayout( 0 , 1 ) );
			
			this.inptMyIP = new InputField( Text.Login.INPUT_MY_IP );
			this.inptMyIP.disableInput();
			add( this.inptMyIP );
			
			add( Box.createVerticalGlue() );
			
			this.inptServerIP = new InputField( Text.Login.INPUT_SERVER_IP );
			add( this.inptServerIP );
			
			add( Box.createVerticalGlue() );
			
			this.inptPort = new InputField( Text.Login.INPUT_PORT );
			add( this.inptPort );
			
			this.pnlCommands = new JPanel( new GridLayout( 1 , 2 , 0 , 25 ) );
				this.cmdReset = new JButton( Text.Login.RESET_COMMAND );
				this.pnlCommands.add( this.cmdReset );
				
				this.cmdSave = new JButton( Text.Login.SAVE_COMMAND );
				this.pnlCommands.add( this.cmdSave );
			add( this.pnlCommands );
		}
		
		public void addSettingsPanelListener( SettingsPanelListener l ) {
			this.cmdReset.addActionListener( l );
			this.cmdSave.addActionListener( l );
		}
		
		/**
		 * processes some data about the user's connection-settings preferences
		 * and updates the user interface to reflect those changes
		 * 
		 * @param data			data about the user's connection-settings
		 * 						preferences
		 */
		public void processPreferenceData( String data ) {
			String[] preference = data.split( LoginPreferencesManager.PREFERENCE_ASSIGNMENT_TAG );
			if ( preference[ 0 ].equals( LoginPreferencesManager.SERVER_IP_TAG ) ) {
				this.inptServerIP.setInput( preference[ 1 ] );
			}
			else if ( preference[ 0 ].equals( LoginPreferencesManager.PORT_TAG ) ) {
				this.inptPort.setInput( preference[ 1 ] );
			}
		}
		
		/**
		 * @return		the server IP inputed by the user
		 */
		public String getInputedServerIP() {
			return this.inptServerIP.getInput();
		}
		
		/**
		 * @return		the port inputed by the user
		 */
		public String getInputedPort() {
			return this.inptPort.getInput();
		}
	}
	
	private class SettingsPanelListener implements ActionListener {

		public SettingsPanelListener() {
			
		}

		@Override
		public void actionPerformed( ActionEvent e ) {
			String command = e.getActionCommand();
			if ( command.equals( Text.Login.SAVE_COMMAND ) ) {
				LoginPreferencesManager.savePreferenceData( LoginUI.this );
			}
			else if ( command.equals( Text.Login.RESET_COMMAND ) ) {
				LoginPreferencesManager.loadPreferences( LoginUI.this );
			}
		}
		
	}
}
