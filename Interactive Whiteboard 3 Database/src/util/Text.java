package util;

final public class Text
{
	final public static class MAIN
	{
		final public static String BLOCK_IPS_NOT_FOUND_ERROR_MESSAGE = "The list of blocked IPs cannot be found.";
		final public static String BLOCK_IPS_NOT_FOUND_LOG_MESSAGE = "UNEXPECTED ERROR: Blocked IPs cannot be loaded. Reason: File \"blocked.in\" was deleted, moved, or renamed.";
		
		final public static String IP_NOT_FOUND_ERROR_MESSAGE = "The program needs to be able to obtain this machine's IP Address in order to start. Terminating...";
		final public static String IP_NOT_FOUND_LOG_MESSAGE = "UNEXPECTED ERROR: No IP Address found for connections.";
	}
	
	final public static class UI
	{
		//This text is displayed before the IP Address of the machine on which this program runs
		final public static String IDENTIFY_IP_STRING = "IP Address: ";
		//This text the view log button will display
		final public static String VIEW_LOG_STRING = "View Log";
		//These are the two possible words that may be displayed on the button for starting and stopping the server
		final public static String START_STRING = "Start";
		final public static String STOP_STRING = "Stop";
		//This is the text the button for blocking IP Addresses will display
		final public static String BLOCK_IP_STRING = "Block An IP";
		//This is the text the button for unblocking IP Addresses will display
		final public static String UNBLOCK_IP_STRING = "Unblock An IP";
		//This is the text the exit button will display
		final public static String EXIT_STRING = "Exit";
		final public static String DATABASE_TITLE = "Database Server";
	}
	
	final public static class UIListener
	{
		
		final public static String SERVER_STARTED_INFO_MESSAGE = "The server was successfully started.";
		final public static String SERVER_STARTED_LOG_MESSAGE = "Server started.";
		
		final public static String BAD_PORT_ERROR_MESSAGE = "The server cannot set up connection on port 9999. Please fix any internet issues, free the port and then restart. Terminating...";
		final public static String BAD_PORT_LOG_MESSAGE = "UNEXEPCTED ERROR: Unable to set up connections on port 9999.";
		
		final public static String SERVER_STOPPED_INFO_MESSAGE = "The server was successfully stopped.";
		final public static String SERVER_STOPPED_LOG_MESSAGE = "Server stopped.";
		
		final public static String REQUEST_BLOCK_IP_INPUT_MESSAGE = "Enter an IP to block: ";
		final public static String REQUEST_UNBLOCK_IP_INPUT_MESSAGE = "Enter an IP to unblock: ";
		
		final public static String CANNOT_TERMINATE_ERROR_MESSAGE = "Cannot terminate before networking components have been stopped. Please click the \"Stop\" button and then exit.";
		final public static String TERMINATE_CONFIRM_MESSAGE = "Are you sure you wish to terminate?";
		
		final public static String getIPAlreadyBlockedErrorMessage(String ip)
		{
			return ip + " is already contained in the blocked IPs list.";
		}
		final public static String getIPAlreadyBlockedLogMessage(String ip)
		{
			return "Could not block IP: \"" + ip + "\" Reason: Already blocked.";
		}
		final public static String getIPSuccessfullyBlockedSuccessMessage(String ip)
		{
			return "Successfully added \"" + ip + "\" to the blocked IPs list.";
		}
		final public static String getIPSuccessfullyBlockedLogMessage(String ip)
		{
			return "Added \"" + ip + "\" to the blocked IPs list.";
		}
		final public static String CANNOT_BLOCK_LOCALHOST_ERROR_MESSAGE = "You may not block localhost 127.0.0.1 because that IP Address is this machine's IP Address";
		
		final public static String COULD_NOT_SAVE_BLOCKED_IPS_ERROR_MESSAGE = "The program is unable to save the list of blocked IPs.\n Please manually edit the list and restart the program.";
		final public static String COULD_NOT_SAVE_BLOCKED_IPS_LOG_MESSAGE = "UNEXPECTED ERROR: Could not save blocked IPs. Reason: File \"blocked.in\" was deleted, moved, or renamed.";
		
		final public static String getIPNotValidErrorMessage(String ip)
		{
			return "The IP Address \"" + ip + "\" is not valid.";
		}
		final public static String getIPNotValidLogMessage(String ip)
		{
			return "Could not block IP: \"" + ip + "\" Reason: not a valid IP address.";
		}
		
		final public static String getIPSuccessfullyUnblockedSuccessMessage(String ip)
		{
			return "Successfully removed \"" + ip + "\" from the blocked IPs list.";
		}
		final public static String getIPSuccessfullyUnblockedLogMessage(String ip)
		{
			return "Removed \"" + ip + "\" from the blocked IPs list.";
		}
		
		final public static String getNonexistingIPToUnblockErrorMessage(String ip)
		{
			return "\"" + ip + "\" is not in the list of blocked IPs.";
		}
		final public static String getNonexistingIPToUnblockLogMessage(String ip)
		{
			return "Failed to block IP: \"" + ip + "\". Reason: Not on blocked list.";
		}
		
		final public static String getInvalidIPToUnblockErrorMessage(String ip)
		{
			return "The IP Address \"" + ip + "\" is not valid.";
		}
		final public static String getInvalidIPToUnblockLogMessage(String ip)
		{
			return "Failed to block IP: \"" + ip + "\". Reason: Not on blocked list.";
		}
	}
	
	final public static class ROOM_DATA_MANAGER
	{
		final public static String getRoomDataFormattedIncorrectlyLogMessage(int roomID)
		{
			return "The room data for room with ID " + roomID + " is formatted incorrectly.";
		}
	}
	
	final public static class NETWORKING
	{
		final public static String WAITING_FOR_A_CLIENT_LOG_MESSAGE = "Waiting for an incoming connection.";
		final public static String getBadIPAddressLogMessage(String ip)
		{
			return "Rejected \"" + ip + "\". Reason: blocked or invalid IP address.";
		}
		
		final public static String getUnresponsiveClientIpPhaseLogMessage(String ip)
		{
			return "Rejected \"" + ip + "\". Reason: unresponsive at request IP phase.";
		}
		
		final public static String getConnectionLostIpPhaseLogMessage(String ip)
		{
			return "Rejected \"" + ip + "\". Reason: lost connection at request IP phase.";
		}
		
		final public static String getConnectionSuccessfulLogMessage(String ip)
		{
			return "Accepted \"" + ip + "\". Reason: all tests passed.";
		}
		
		final public static String getBadConnectionPasswordLogMessage(String ip, String connectionPassword)
		{
			return "Rejected \"" + ip + "\". Reason: bad connection password \"" + connectionPassword + "\".";
		}
		
		final public static String getUnresponsiveClientConnectionPasswordPhaseLogMessage(String ip)
		{
			return "Rejected \"" + ip + "\". Reason: unresponsive at request connection password phase.";
		}
		
		final public static String getConnectionLostConnectionPasswordPhaseLogMessage(String ip)
		{
			return "Rejected \"" + ip + "\". Reason: lost connection at request connection password phase.";
		}
		
		final public static String CONNECTION_LOST_ERROR_MESSAGE = "The connection to the Whiteboard Server has been lost. Closing networking connections...";
		
		final public static String getConnectionLostWhiteboardServerLogMessage(String ip)
		{
			return "Connection to whiteboard server \"" + ip + "\" was lost.";
		}
		
		final public static String getImproperCommunicationWhiteboardServerLogMessage(String ip)
		{
			return "Warning: Whiteboard server \"" + ip + "\" is communicating incorrectly.";
		}
		
		final public static String getUnknownErrorLogMessage(String location)
		{
			return "Unknown error at \"" + location + "\"";
		}
	}
	
	final public static class LOG
	{
		final public static String getFileNotFoundLogMessage(String filename, String message)
		{
			return "File not found exception for \"" + filename + "\". Could not log the following message: " + message;
		}
	}

}
