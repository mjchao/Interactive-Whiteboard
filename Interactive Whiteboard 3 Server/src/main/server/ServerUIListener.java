package main.server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import database.ConnectionEndedException;
import database.DatabaseConnection;

import main.init.StartUp;
import managers.blockip.BlockIPManager;
import net.Server;
import net.ServerSideErrorException;
import net.mainserver.MainServer;
import net.messagingserver.MessagingServer;
import net.roomdataserver.RoomDataServer;

import util.CommonMethods;
import util.Text;

public class ServerUIListener implements ActionListener
{

	//these fields are static only because we need to add an emergency terminate that 
	//some networking object may need to call when there is an unexpected error.
	private static ServerUI m_gui;
	private static MainServer m_mainServer;
	private static MessagingServer m_messagingServer;
	private static RoomDataServer m_roomDataServer;
	
	public ServerUIListener(ServerUI gui)
	{
		m_gui = gui;
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		String command = e.getActionCommand();
		//figure out what the user wants to do
		if (command.equals(Text.SERVERUI.START_STRING))
		{
			String outputMessage = Text.SERVERUILISTENER.MAIN_SERVER_START_FAILED_ERROR_MESSAGE;
			String outputLogMessage = Text.SERVERUILISTENER.MAIN_SERVER_START_FAILED_LOG_MESSAGE;
			//try to start the server
			try
			{
				//first attempt to start the database connection
				if (!DatabaseConnection.isInitialized())
				{
					DatabaseConnection.initialize(StartUp.DATABASE_IP, StartUp.DATABASE_PORT);
				}
				//then attempt to start the main server
				m_mainServer = new MainServer();
				m_mainServer.start();
				//then attempt to start the messaging server
				outputMessage = Text.SERVERUILISTENER.MESSAGING_SERVER_START_FAILED_ERROR_MESSAGE;
				outputLogMessage = Text.SERVERUILISTENER.MESSAGING_SERVER_START_FAILED_LOG_MESSAGE;
				m_messagingServer = new MessagingServer();
				m_messagingServer.start();
				//try to start the room data server
				outputMessage = Text.SERVERUILISTENER.ROOM_DATA_SERVER_START_FAILED_ERROR_MESSAGE;
				outputLogMessage = Text.SERVERUILISTENER.ROOM_DATA_SERVER_START_FAILED_LOG_MESSAGE;
				m_roomDataServer = new RoomDataServer();
				m_roomDataServer.start();
				//try to start the room server
				outputMessage = Text.SERVERUILISTENER.ROOM_SERVER_START_FAILED_ERROR_MESSAGE;
				outputLogMessage = Text.SERVERUILISTENER.ROOM_SERVER_START_FAILED_LOG_MESSAGE;
				m_gui.reflectServerState(true);
			} catch (IOException connectionError)
			{
				//if anything goes wrong, output the error
				CommonMethods.displayErrorMessage(outputMessage);
				CommonMethods.logConnectionMessage(outputLogMessage);
				//we're done. we use return here to avoid outputting a success message, because this clearly
				//was not successful.
				return;
			} catch (ConnectionEndedException connectionError)
			{
				CommonMethods.displayErrorMessage(Text.SERVERUILISTENER.DATABASE_CONNECTION_START_FAILED_ERROR_MESSAGE);
				CommonMethods.logConnectionMessage(Text.SERVERUILISTENER.DATABASE_CONNECTION_START_FAILED_LOG_MESSAGE);
			} catch (ServerSideErrorException serverSideError) 
			{
				//ignore - this error is used in debugging
			}
			//if all is okay, output a success message
			CommonMethods.displaySuccessMessage(Text.SERVERUILISTENER.SERVER_STARTED_SUCCESS_MESSAGE);
			CommonMethods.logConnectionMessage(Text.SERVERUILISTENER.SERVER_STARTED_LOG_MESSAGE);
		} else if (command.equals(Text.SERVERUI.STOP_STRING))
		{
			m_mainServer.closeAndStop();
			m_messagingServer.closeAndStop();
			m_roomDataServer.closeAndStop();
			m_gui.reflectServerState(false);
			DatabaseConnection.close();
			CommonMethods.displaySuccessMessage(Text.SERVERUILISTENER.SERVER_STOPPED_SUCCESS_MESSAGE);
			CommonMethods.logConnectionMessage(Text.SERVERUILISTENER.SERVER_STOPPED_LOG_MESSAGE);
		} else if (command.equals(Text.SERVERUI.BLOCK_IP_STRING))
		{
			//ask the user for an IP to block
			String anIPAddress = CommonMethods.requestInputMessage(Text.SERVERUILISTENER.REQUEST_BLOCK_IP_INPUT_MESSAGE);
			//attempt to block the IP as long as it is not localhost 127.0.0.1
			if (anIPAddress.equals(Server.LOCALHOST))
			{
				CommonMethods.displayErrorMessage(Text.SERVERUILISTENER.CANNOT_BLOCK_LOCAL_HOST_ERROR_MESSAGE);
			} else
			{
				blockIP(anIPAddress);
			}
		} else if (command.equals(Text.SERVERUI.UNBLOCK_IP_STRING))
		{
			//ask the user for an IP to unblock
			String anIPAddress = CommonMethods.requestInputMessage(Text.SERVERUILISTENER.REQUEST_UNBLOCK_IP_INPUT_MESSAGE);
			//attempt to unblock the IP
			unblockIP(anIPAddress);
		} else if (command.equals(Text.SERVERUI.NAME_CHANGE_STRING))
		{
			if (m_gui.isServerStarted())
			{
				String username = CommonMethods.requestInputMessage(Text.SERVERUILISTENER.REQUEST_USERNAME_INPUT_MESSAGE);
				String password = CommonMethods.requestInputMessage(Text.SERVERUILISTENER.REQUEST_PASSWORD_INPUT_MESSAGE);
				if (username != null && !username.equals("") && password != null && !password.equals(""))
				{
					try 
					{
						String nameChangeCode = DatabaseConnection.requestNewNameChangeCode(username, password);
						if (nameChangeCode == null)
						{
							CommonMethods.displayErrorMessage(Text.SERVERUILISTENER.INVALID_USERNAME_OR_PASSWORD_ERROR_MESSAGE);
						} else
						{
							CommonMethods.displaySuccessMessage(Text.SERVERUILISTENER.getNameChangeCodeSuccessMessage(nameChangeCode));
						}
					} catch (ConnectionEndedException e1)
					{
						CommonMethods.displayErrorMessage(Text.SERVERUILISTENER.CANNOT_GENERATE_NAME_CHANGE_CODE_ERROR_MESSAGE);
					}
				}
			} else
			{
				CommonMethods.displayErrorMessage(Text.SERVERUILISTENER.CANNOT_GENERATE_NAME_CHANGE_CODE_ERROR_MESSAGE);
			}
		} else if (command.equals(Text.SERVERUI.EXIT_STRING))
		{
			//make sure the server has been stopped
			if (areAllServersStopped())
			{
				//then make sure the user wants to exit
				int choice = CommonMethods.displayConfirmDialog(Text.SERVERUILISTENER.EXIT_CONFIRM_MESSAGE);
				if (choice == CommonMethods.CONFIRM_YES_RESPONSE)
				{
					//then exit
					CommonMethods.terminate();
				}
			} else
			{
				CommonMethods.displayErrorMessage(Text.SERVERUILISTENER.EXIT_ERROR_MESSAGE);
			}
		}
	}
	
	/**
	 * Adds a given IP to the list of blocked IPs.
	 * suppressed warnings:<br>
	 * 1) static-method			no IPs can be blocked until this listener is created to observe the main server
	 * 							user interface, so this method is not static
	 * 
	 * @param anIp		a String, the IP to block
	 * @see				BlockIPManager
	 */
	@SuppressWarnings("static-method")
	final private void blockIP(String anIp)
	{
		String ipToBlock = anIp;
		//remove all leading "."s
		while (ipToBlock.length() > 0 && ipToBlock.charAt(0) == '.')
		{
			ipToBlock = ipToBlock.substring(1, ipToBlock.length());
		}
		//remove all trailing "."s
		while (ipToBlock.length() > 0 && ipToBlock.charAt(ipToBlock.length() - 1) == '.')
		{
			ipToBlock = ipToBlock.substring(0, ipToBlock.length() - 1);
		}
		//make sure the IP is valid
		if (BlockIPManager.isValidIPAddress(ipToBlock))
		{
			//make sure the IP is not already in the list
			if (BlockIPManager.isIPInList(ipToBlock))
			{
				//add the IP to the blocked list
				BlockIPManager.addIP(ipToBlock);
				//try to save the list
				try
				{
					//save the list
					BlockIPManager.save();
					//notify the user
					CommonMethods.displaySuccessMessage(Text.SERVERUILISTENER.getIPSuccessfullyBlockedSuccessMessage(ipToBlock));
					CommonMethods.logInternalMessage(Text.SERVERUILISTENER.getIPSuccessfullyBlockedLogMessage(ipToBlock));
				} catch (IOException e)
				{
					//notify the user if we couldn't save
					CommonMethods.displayErrorMessage(Text.SERVERUILISTENER.COULD_NOT_SAVE_BLOCKED_IPS_ERROR_MESSAGE);
					CommonMethods.logInternalMessage(Text.SERVERUILISTENER.COULD_NOT_SAVE_BLOCKED_IPS_LOG_MESSAGE);
				}
			} else
			{
				CommonMethods.displayErrorMessage(Text.SERVERUILISTENER.getIPAlreadyBlockedErrorMessage(ipToBlock));
				CommonMethods.logInternalMessage(Text.SERVERUILISTENER.getIPAlreadyBlockedLogMessage(ipToBlock));
			}
		} else
		{
			CommonMethods.displayErrorMessage(Text.SERVERUILISTENER.getIPNotValidErrorMessage(ipToBlock));
			CommonMethods.logInternalMessage(Text.SERVERUILISTENER.getIPNotValidLogMessage(ipToBlock));
		}
	}
	
	//This method removes an IP Address from the blocked IPs list
	final public static void unblockIP(String ip)
	{
		//make sure the given IP is valid (is an IP Address or can be a part of an IP Address)
		if (BlockIPManager.isValidIPAddress(ip))
		{
			//make sure the given IP has been blocked
			if (BlockIPManager.isIPInList(ip))
			{
				//remove the IP from the blocked list
				BlockIPManager.removeIP(ip);
				//save the blocked list
				try
				{
					BlockIPManager.save();
					//notify the user
					CommonMethods.displaySuccessMessage(Text.SERVERUILISTENER.getIPSuccessfullyUnblockedSuccessMessage(ip));
					CommonMethods.logInternalMessage(Text.SERVERUILISTENER.getIPSuccessfullyUnblockedLogMessage(ip));
				} catch (IOException e)
				{
					CommonMethods.displayErrorMessage(Text.SERVERUILISTENER.COULD_NOT_SAVE_BLOCKED_IPS_ERROR_MESSAGE);
					CommonMethods.logInternalMessage(Text.SERVERUILISTENER.COULD_NOT_SAVE_BLOCKED_IPS_LOG_MESSAGE);
				}	
			} else
			{
				CommonMethods.displayErrorMessage(Text.SERVERUILISTENER.getNonexistingIPToUnblockErrorMessage(ip));
				CommonMethods.logInternalMessage(Text.SERVERUILISTENER.getNonexistingIPToUnblockLogMessage(ip));
			}
		} else
		{
			CommonMethods.displayErrorMessage(Text.SERVERUILISTENER.getInvalidIPToUnblockErrorMessage(ip));
			CommonMethods.logInternalMessage(Text.SERVERUILISTENER.getInvalidIPToUnblockLogMessage(ip));
		}
	}
	
	/**
	 * suppressed warnings:<br>
	 * 1) static-method			this method is not static because no servers exists until this listener is
	 * 							created (constructor called)
	 * @return		true if all servers are stopped, false otherwise
	 */
	@SuppressWarnings("static-method")
	final public boolean areAllServersStopped()
	{
		return (!m_mainServer.isServerRunning() && !m_messagingServer.isServerRunning() && 
				!m_roomDataServer.isServerRunning());
	}
	
	/**
	 * forces all networking objects to stop. used when the server unexpectedly can't close networking connections
	 */
	final public static void emergencyStop()
	{
		try
		{
			m_mainServer.closeAndStop();
			m_messagingServer.closeAndStop();
			m_roomDataServer.closeAndStop();
			m_gui.reflectServerState(false);
		} catch (Exception e)
		{
			//if we could not stop, just terminate the program
			CommonMethods.logInternalMessage(Text.SERVERUILISTENER.EMERGENCY_STOP_FAILED);
			CommonMethods.terminate();
		}
	}

}
