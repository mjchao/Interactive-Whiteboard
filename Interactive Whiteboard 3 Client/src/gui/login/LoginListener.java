package gui.login;

import gui.main.Client;
import gui.main.ClientListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import net.InvalidMessageException;
import net.OperationErrorCode;
import net.OperationFailedException;
import net.StepConnection;
import net.mainserver.MainServerConcurrentConnection;
import net.mainserver.MainServerStepConnection;
import net.messagingserver.MessagingServerConcurrentConnection;
import net.messagingserver.MessagingServerStepConnection;
import net.roomdataserver.RoomDataServerConcurrentConnection;
import net.roomdataserver.RoomDataServerStepConnection;

import util.CommonMethods;
import util.Text;

public class LoginListener implements ActionListener
{

	final private Login m_gui;
	private Client m_client;
	
	//Networking objects:
	private MainServerStepConnection m_mainServerStepConnection;
	private MessagingServerStepConnection m_messagingServerStepConnection;
	private RoomDataServerStepConnection m_roomDataServerStepConnection;
	
	public LoginListener(Login gui)
	{
		this.m_gui = gui;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		String command = e.getActionCommand();
		if (command.equals(Text.GUI.LOGIN.REGISTER_STRING))
		{
			attemptMainServerConnection();
			attemptRegister();
		} else if (command.equals(Text.GUI.LOGIN.LOGIN_STRING))
		{
			attemptMainServerConnection();
			attemptLogin();
		}
	}
	
	/**
	 * Connects to the main server
	 */
	final public void attemptMainServerConnection()
	{
		try
		{
			String mainServerIP = this.m_gui.getServerIP();
			int port = Integer.parseInt(this.m_gui.getServerPort());
			this.m_mainServerStepConnection = new MainServerStepConnection(mainServerIP, port);
			new MainServerConcurrentConnection(this.m_client, mainServerIP, port - 1).start();
			CommonMethods.displaySuccessMessage(Text.GUI.LOGINLISTENER.CONNECTION_SUCCEEDS_SUCCESS_MESSAGE);
		} catch (NumberFormatException badPort)
		{
			CommonMethods.displayErrorMessage(Text.GUI.LOGINLISTENER.INVALID_PORT_ERROR_MESSAGE);
		} catch (IllegalArgumentException badPort)
		{
			CommonMethods.displayErrorMessage(Text.GUI.LOGINLISTENER.INVALID_PORT_ERROR_MESSAGE);
		} catch (IOException e)
		{
			CommonMethods.displayErrorMessage(Text.GUI.LOGINLISTENER.CONNECTION_FAILED_ERROR_MESSAGE);
		}
	}
	
	/**
	 * Attempts to log in. Note there is a specific order to the actions that are performed:<br>
	 * 0) Main server must have already been set up<br>
	 * 1) Log in to main server<br>
	 * 2) Set up Messaging Server Step Connection<br>
	 * 3) Set up Room Data Server Step Connection<br>
	 * 4) Set up Messaging Server Concurrent Connection<br>
	 * 5) Set up Room Data Server Concurrent Connection<br>
	 * 6) Log in to other servers
	 */
	final public void attemptLogin()
	{
		String username = this.m_gui.getUsername();
		String password = this.m_gui.getPassword();
		try
		{
			if (this.m_mainServerStepConnection.attemptLogin(username, password))
			{
				System.out.println("Logged in");
				Login.m_username = username;
				Login.m_password = password;
				Login.m_serverIP = this.m_gui.getServerIP();
				setupMessagingServerStepConnection();
				setUpRoomDataServerStepConnection();
				loadMainClient();
				this.m_gui.setVisible(false);
				CommonMethods.displaySuccessMessage(Text.NET.GENERAL.LOGIN_SUCCEEDS_SUCCESS_MESSAGE);
				setupMessagingServerConcurrentConnection();
				setUpRoomDataServerConcurrentConnection();
				performLogins();
			} else
			{
				CommonMethods.displayErrorMessage(Text.NET.GENERAL.LOGIN_FAILED_ERROR_MESSAGE);
			}
		} catch (IOException e)
		{
			CommonMethods.displayErrorMessage(e.getMessage());
			//MainServerStepConnection.displayConnectionLostMessage(Text.NET.MAIN_SERVER_NAME);
		} catch (InvalidMessageException badMessage)
		{
			this.m_mainServerStepConnection.handleInvalidMessageException(badMessage);
		} catch (NullPointerException e)
		{
			//ignore - we just can't connect and the client knows why
		}
	}
	
	final public void attemptRegister()
	{
		String username = this.m_gui.getUsername();
		String password = this.m_gui.getPassword();
		try
		{
			this.m_mainServerStepConnection.register(username, password);
			CommonMethods.displaySuccessMessage(Text.NET.MAINSERVER.REGISTER_SUCCEEDS_SUCCESS_MESSAGE);
		} catch (IOException e)
		{
			StepConnection.displayConnectionLostMessage(Text.NET.MAIN_SERVER_NAME);
		} catch (OperationFailedException e)
		{
			OperationErrorCode errorCode = e.getErrorCode();
			handleErrorCode(errorCode);
		} catch (InvalidMessageException badMessage)
		{
			this.m_mainServerStepConnection.handleInvalidMessageException(badMessage);
		}
	}
	
	final public void handleErrorCode(OperationErrorCode errorCode)
	{
		switch(errorCode)
		{
			case NEW_USERNAME_IS_INVALID:
				CommonMethods.displayErrorMessage(Text.GUI.LOGINLISTENER.getUsernameNotValidErrorMessage(this.m_gui.getUsername()));
				break;
			case USERNAME_IN_USE:
				CommonMethods.displayErrorMessage(Text.GUI.LOGINLISTENER.getUsernameTakenErrorMessage(this.m_gui.getUsername()));
				break;
			case NEW_PASSWORD_IS_INVALID:
				CommonMethods.displayErrorMessage(Text.GUI.LOGINLISTENER.getPasswordNotValidErrorMessage(this.m_gui.getPassword()));
				break;
			default:
				//TODO
		}
	}
	
	final private void loadMainClient()
	{
		this.m_client = new Client(this.m_mainServerStepConnection, this.m_messagingServerStepConnection, this.m_roomDataServerStepConnection);
		this.m_client.addClientListener(new ClientListener(this.m_client));
		this.m_client.showUserInterface();
	}
	
	final private void setupMessagingServerStepConnection() throws IOException, InvalidMessageException
	{
		//get the information required to set up connection
		String messagingServerIP = this.m_gui.getServerIP();
		int[] messagingServerPorts = this.m_mainServerStepConnection.getMessagingServerPorts();
		int messagingServerStepConnectionPort = messagingServerPorts[0];
		//set up the step connection
		System.out.println("Trying to connect on port " + messagingServerStepConnectionPort);
		this.m_messagingServerStepConnection = new MessagingServerStepConnection(messagingServerIP, messagingServerStepConnectionPort);
	}
	
	@SuppressWarnings("unused")
	final private void setupMessagingServerConcurrentConnection() throws IOException, InvalidMessageException
	{
		//get the IP address to which we will connect
		String messagingServerIP = this.m_gui.getServerIP();
		int[] messagingServerPorts = this.m_mainServerStepConnection.getMessagingServerPorts();
		int messagingServerConcurrentConnectionPort = messagingServerPorts[1];
		
		//set up the concurrent connection
		new MessagingServerConcurrentConnection(this.m_client.getMessagingUserInterface(), messagingServerIP, messagingServerConcurrentConnectionPort);
	}
	
	final private void setUpRoomDataServerStepConnection() throws IOException, InvalidMessageException
	{
		//get the information required to set up connection
		String roomDataServerIP = this.m_gui.getServerIP();
		int[] roomDataServerPorts = this.m_mainServerStepConnection.getRoomDataServerPorts();
		int roomDataServerStepConnectionPort = roomDataServerPorts[0];
		//set up the step connection
		this.m_roomDataServerStepConnection = new RoomDataServerStepConnection(roomDataServerIP, roomDataServerStepConnectionPort, this.m_messagingServerStepConnection);
	}
	
	@SuppressWarnings("unused")
	final private void setUpRoomDataServerConcurrentConnection() throws IOException, InvalidMessageException
	{
		//get the information required to set up connection
		String roomDataServerIP = this.m_gui.getServerIP();
		int[] roomDataServerPorts = this.m_mainServerStepConnection.getRoomDataServerPorts();
		int roomDataServerConcurrentConnectionPort = roomDataServerPorts[1];
		
		//set up the concurrent connection
		new RoomDataServerConcurrentConnection(this.m_client.getRoomInfoInterface(), roomDataServerIP, roomDataServerConcurrentConnectionPort);
	}
	
	final private void performLogins()
	{
		//log in to the messaging server
		this.m_messagingServerStepConnection.attemptLogin();
		this.m_roomDataServerStepConnection.attemptLogin();
	}
}
