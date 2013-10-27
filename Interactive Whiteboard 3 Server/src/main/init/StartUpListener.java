package main.init;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import main.server.ServerUI;
import main.server.ServerUIListener;

import database.ConnectionEndedException;
import database.DatabaseConnection;

import util.CommonMethods;
import util.Text;

/**
 * This class listens to user generated events on the Start Up user interface.
 *
 */
final public class StartUpListener implements ActionListener
{
	final private StartUp m_gui;
	
	public StartUpListener(StartUp gui)
	{
		this.m_gui = gui;
	}

	@Override
	final public void actionPerformed(ActionEvent e)
	{
		String command = e.getActionCommand();
		if (command.equals(Text.STARTUP.EXIT_STRING))
		{
			CommonMethods.terminate();
		} else if (command.equals(Text.STARTUP.BEGIN_STRING))
		{
			if (startDatabaseConnection())
			{
				startServerUI();
			}
		}
	}
	
	//this method attempts to start the connection to the database server.
	//it returns true if it succeeds and false if it fails.
	final private boolean startDatabaseConnection()
	{
		try
		{
			String serverIP = this.m_gui.getInputedServerIP();
			int serverPort = Integer.parseInt(this.m_gui.getInputedServerPort());
			DatabaseConnection.initialize(serverIP, serverPort);
			StartUp.DATABASE_IP = serverIP;
			StartUp.DATABASE_PORT = serverPort;
		} catch (IOException e)	//respond is connection fails
		{
			CommonMethods.displayErrorMessage(Text.STARTUPLISTENER.getConnectionFailedErrorMessage(this.m_gui.getInputedServerIP(), this.m_gui.getInputedServerPort()));
			return false;
		} catch (NumberFormatException e) //respond if port is not an integer
		{
			CommonMethods.displayErrorMessage(Text.STARTUPLISTENER.getInvalidPortErrorMessage(this.m_gui.getInputedServerPort()));
			return false;
		} catch (IllegalArgumentException e) //respond if port is out of range
		{
			CommonMethods.displayErrorMessage(Text.STARTUPLISTENER.getInvalidPortErrorMessage(this.m_gui.getInputedServerPort()));
			return false;
		} catch (ConnectionEndedException e)
		{
			CommonMethods.displayErrorMessage(Text.STARTUPLISTENER.FAILED_TO_CONNECT_TO_DATABASE_SERVER);
			return false;
		}
		return true;
	}
	
	//this method loads the main whiteboard server user interface for starting and stopping
	final private void startServerUI()
	{
		this.m_gui.hideInterface();
		try
		{
			ServerUI nextUI = new ServerUI();
			nextUI.addServerListener(new ServerUIListener(nextUI));
		} catch (IOException e)
		{
			CommonMethods.displayErrorMessage(Text.STARTUPLISTENER.COULD_NOT_GET_IP_ADDRESS_ERROR_MESSAGE);
			CommonMethods.terminate();
		}
	}
}
