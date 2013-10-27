package main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import net.Networking;

import managers.blockip.BlockIPManager;

import util.CommonMethods;
import util.Text;

/**
 * observes the user interface of the database server.
 */
final public class UIListener implements ActionListener
{

	/**
	 * user interface this listener observes
	 */
	final private UI m_gui;	//this is the user interface this object is observing.
	
	/**
	 * networking device. the networking device might be used when the user clicks on certain buttons
	 * on the user interface
	 */
	private Networking m_net;
	
	/**
	 * Constructor
	 * 
	 * @param gui		a <code>UI</code>, the user interface to observe
	 */
	public UIListener(UI gui)
	{
		this.m_gui = gui;
	}
	
	@Override
	final public void actionPerformed(ActionEvent e)
	{
		//record the text on the button the user clicked
		String c = e.getActionCommand();
		//find out what the user clicked
		if (c.equals(Text.UI.START_STRING))
		{
			//have the user interface show that the server has been started
			try
			{
				this.m_net = new Networking();
				CommonMethods.logInternalMessage(Text.UIListener.SERVER_STARTED_LOG_MESSAGE);
				this.m_net.start();
				this.m_gui.reflectServerStarted();
				//let the user know the server was started
				CommonMethods.displayInformationMessage(Text.UIListener.SERVER_STARTED_INFO_MESSAGE);
			} catch (IOException anError)
			{
				anError.printStackTrace();
				CommonMethods.logConnectionMessage(Text.UIListener.BAD_PORT_LOG_MESSAGE);
				CommonMethods.displayErrorMessage(Text.UIListener.BAD_PORT_ERROR_MESSAGE);
			}
		} else if (c.equals(Text.UI.STOP_STRING))
		{
			//have the user interface show that the server has been stopped
			this.m_net.closeAndStop();
			this.m_gui.reflectServerStopped();
			//let the user know the server was stopped
			CommonMethods.displayInformationMessage(Text.UIListener.SERVER_STOPPED_INFO_MESSAGE);
			CommonMethods.logInternalMessage(Text.UIListener.SERVER_STOPPED_LOG_MESSAGE);
		} else if (c.equals(Text.UI.VIEW_LOG_STRING))
		{
			//show the log if it's not already visible
			CommonMethods.showLog();
		} else if (c.equals(Text.UI.BLOCK_IP_STRING))
		{
			//ask the user for an IP address
			String ip = CommonMethods.requestInputMessage(Text.UIListener.REQUEST_BLOCK_IP_INPUT_MESSAGE);
			//block the IP address as long as it is not localhost 127.0.0.1
			if (ip.equals(Networking.LOCALHOST))
			{
				CommonMethods.displayErrorMessage(Text.UIListener.CANNOT_BLOCK_LOCALHOST_ERROR_MESSAGE);
			} else
			{
				blockIP(ip);
			}
		} else if (c.equals(Text.UI.UNBLOCK_IP_STRING))
		{
			//ask the user for an IP address
			String ip = CommonMethods.requestInputMessage(Text.UIListener.REQUEST_UNBLOCK_IP_INPUT_MESSAGE);
			//unblock the IP address
			unblockIP(ip);
		} else if (c.equals(Text.UI.EXIT_STRING))
		{
			//if the networking part has been created, make sure networking part of the program has been stopped
			if (this.m_net != null)
			{
				if (this.m_net.isServerStarted())
				{
					CommonMethods.displayErrorMessage(Text.UIListener.CANNOT_TERMINATE_ERROR_MESSAGE);
				} else
				{
					//Make sure the user wants to exit before terminating
					int userResponse = CommonMethods.displayConfirmDialog(Text.UIListener.TERMINATE_CONFIRM_MESSAGE);
					if (userResponse == CommonMethods.CONFIRM_YES_RESPONSE)
					{
						CommonMethods.terminate();
					}
				}
			} else
			{
				CommonMethods.terminate();
			}
		}
	}
	
	/**
	 * adds an IP address to the blocked list.
	 * Suppressed warnings:<br>
	 * 1) static-method		this method should not be static because it's only used when this listener has been created.
	 * 						the user cannot add a blocked IP address until something is observing the block IP button
	 * 
	 * @param anIP			a String, the IP Address to block
	 */
	@SuppressWarnings("static-method")
	final private void blockIP(String anIP)
	{
		String ip = anIP;
		//remove all leading "."s
		while (ip.length() > 0 && ip.charAt(0) == '.')
		{
			ip = ip.substring(1, ip.length());
		}
		//remove all trailing "."s
		while (ip.length() > 0 && ip.charAt(ip.length() - 1) == '.')
		{
			ip = ip.substring(0, ip.length() - 1);
		}
		//if the IP is valid, then add the IP address to the block list.
		if (BlockIPManager.isValidIPAddress(ip))
		{
			//make sure the IP has not already been added
			if (BlockIPManager.isIPInList(ip))
			{
				CommonMethods.displayErrorMessage(Text.UIListener.getIPAlreadyBlockedErrorMessage(ip));
				CommonMethods.logInternalMessage(Text.UIListener.getIPAlreadyBlockedLogMessage(ip));
			} else
			{
				//add the IP to the blocked list
				BlockIPManager.addIP(ip);
				try 
				{
					//save the blocked list
					BlockIPManager.save();
					//notify the user
					CommonMethods.displaySuccessMessage(Text.UIListener.getIPSuccessfullyBlockedSuccessMessage(ip));
					CommonMethods.logInternalMessage(Text.UIListener.getIPSuccessfullyBlockedLogMessage(ip));
				} catch (IOException e)
				{
					CommonMethods.displayErrorMessage(Text.UIListener.COULD_NOT_SAVE_BLOCKED_IPS_ERROR_MESSAGE);
					CommonMethods.logInternalMessage(Text.UIListener.COULD_NOT_SAVE_BLOCKED_IPS_LOG_MESSAGE);
				}
			}
		} else
		{
			CommonMethods.displayErrorMessage(Text.UIListener.getIPNotValidErrorMessage(ip));
			CommonMethods.logInternalMessage(Text.UIListener.getIPNotValidLogMessage(ip));
		}
	}
	
	/**
	 * removes an IP address from the blocked list
	 * Suppressed warnings:<br>
	 * 1) static-method 		not static because the user will not be removing blocked IPs until this
	 * 							listener has been created.
	 * 
	 * @param ip				a String, the IP address to remove from the blocked list
	 */
	@SuppressWarnings("static-method")	//This method should not be static because it is only used when
										//this object has been created
	final private void unblockIP(String ip)
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
					CommonMethods.displaySuccessMessage(Text.UIListener.getIPSuccessfullyUnblockedSuccessMessage(ip));
					CommonMethods.logInternalMessage(Text.UIListener.getIPSuccessfullyUnblockedLogMessage(ip));
				} catch (IOException e)
				{
					CommonMethods.displayErrorMessage(Text.UIListener.COULD_NOT_SAVE_BLOCKED_IPS_ERROR_MESSAGE);
					CommonMethods.logInternalMessage(Text.UIListener.COULD_NOT_SAVE_BLOCKED_IPS_LOG_MESSAGE);
				}	
			} else
			{
				CommonMethods.displayErrorMessage(Text.UIListener.getNonexistingIPToUnblockErrorMessage(ip));
				CommonMethods.logInternalMessage(Text.UIListener.getNonexistingIPToUnblockLogMessage(ip));
			}
		} else
		{
			CommonMethods.displayErrorMessage(Text.UIListener.getInvalidIPToUnblockErrorMessage(ip));
			CommonMethods.logInternalMessage(Text.UIListener.getInvalidIPToUnblockLogMessage(ip));
		}
	}
}
