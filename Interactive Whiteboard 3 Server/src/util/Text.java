package util;

import main.init.StartUp;

final public class Text 
{
	final public static class MAIN
	{
		final public static String PORT9999_TAKEN_ERROR = "Port 9999 is currently in use. Please free the port then restart the program.";
	}
	final public static class DATABASECONNECTION
	{
		final public static String GOOD_IP_LOG_MESSAGE = "The database server has accepted this machine's IP address.";
		final public static String getBadIPLogMessage(String ip)
		{
			return "The database server declined the connection with this machine with IP address \"" + ip + "\".";
		}
		final public static String GOOD_CONNECTION_PASSWORD_LOG_MESSAGE = "The database server has accepted this machine's connection password.";
		final public static String getBadConnectionPasswordLogMessage(String connectionPassword)
		{
			return "The database server declined the connection because the connection password \"" + connectionPassword + "\" is not correct.";
		}
		final public static String getConnectedToWrongServerLogMessage(String serverIP, int port)
		{
			return "Failed to connect to the database server. \"" + serverIP + "\" on port " + port + " does not appear to be the correct server.";
		}
		final public static String getSuccessfullyConnectedToServerLogMessage(String serverIP, int port)
		{
			return "Successfully connected to the database server \"" + serverIP + "\" on port " + port + ".";
		}
	}

	final public static class LOG
	{
		final public static String getFileNotFoundLogMessage(String filename, String message)
		{
			return "File not found exception for \"" + filename + "\". Could not log the following message: " + message;
		}
	}
	
	final public static class STARTUP
	{
		final public static String STARTUP_TITLE_BAR = "Whiteboard Server Setup";
		final public static String REQUEST_IP_STRING = "Enter IP Address of Database Server: ";
		final public static String REQUEST_PORT_STRING = "Enter Port of Database Server: ";
		final public static String EXIT_STRING = "Exit";
		final public static String BEGIN_STRING = "Load Server";
	}
	
	final public static class STARTUPLISTENER
	{
		final public static String FAILED_TO_CONNECT_TO_DATABASE_SERVER = "The connection to the database server was lost. Please see log for errors. Terminating...";
		final public static String getInvalidPortErrorMessage(String port)
		{
			return "\"" + port + "\" is not a valid port number";
		}
		final public static String getConnectionFailedErrorMessage(String ip, String port)
		{
			return "Could not connect to database server \"" + ip + "\" on port " + port;
		}
		final public static String COULD_NOT_GET_IP_ADDRESS_ERROR_MESSAGE = "Failed to obtain this machine's IP Address. A visible IP Address is required to start this whiteboard server.";
	}
	
	final public static class SERVERUI
	{
		final public static String SERVER_TITLE_BAR = "Whiteboard Server";
		final public static String SHOW_IP_STRING = "IP Address: ";
		final public static String START_STRING = "Start";
		final public static String STOP_STRING = "Stop";
		final public static String BLOCK_IP_STRING = "Block IP";
		final public static String UNBLOCK_IP_STRING = "Unblock IP";
		final public static String NAME_CHANGE_STRING = "Generate Name Change Code";
		final public static String EXIT_STRING = "Exit";
	}
	
	final public static class SERVERUILISTENER
	{	
		final public static String DATABASE_CONNECTION_START_FAILED_ERROR_MESSAGE = "The database connection could not be started. Please make sure the port is free.";
		final public static String DATABASE_CONNECTION_START_FAILED_LOG_MESSAGE = "Failed to start database connection. Reason: Database connection unable to use port " + StartUp.DATABASE_PORT;
		
		final public static String MAIN_SERVER_START_FAILED_ERROR_MESSAGE = "The main server (port 9999) could not be started. Please make sure the port is free.";
		final public static String MAIN_SERVER_START_FAILED_LOG_MESSAGE = "Failed to start server. Reason: Main server unable to use port 9999.";
		
		final public static String MESSAGING_SERVER_START_FAILED_ERROR_MESSAGE = "The messaging server (port 9998) could not be started. Please make sure the port is free.";
		final public static String MESSAGING_SERVER_START_FAILED_LOG_MESSAGE = "Failed to start server. Reason: Messaging server unable to use port 9998.";
		
		final public static String ROOM_DATA_SERVER_START_FAILED_ERROR_MESSAGE = "The room data server (port 9997) could not be started. Please make sure the port is free.";
		final public static String ROOM_DATA_SERVER_START_FAILED_LOG_MESSAGE = "Failed to start server. Reason: Room data server unable to use port 9997";
		
		final public static String ROOM_SERVER_START_FAILED_ERROR_MESSAGE = "The room server (port 9996) could not be started. Please make sure the port is free.";
		final public static String ROOM_SERVER_START_FAILED_LOG_MESSAGE = "Failed to start server. Reason: Room server unable to use port 9996";
		
		final public static String SERVER_STARTED_SUCCESS_MESSAGE = "The server was successfully started.";
		final public static String SERVER_STARTED_LOG_MESSAGE = "Server started.";
		
		final public static String SERVER_STOPPED_SUCCESS_MESSAGE = "The server was successfully stopped.";
		final public static String SERVER_STOPPED_LOG_MESSAGE = "Server stopped.";
		
		final public static String REQUEST_BLOCK_IP_INPUT_MESSAGE = "Enter an IP Address to block: ";
		final public static String CANNOT_BLOCK_LOCAL_HOST_ERROR_MESSAGE = "You may not block the localhost 127.0.0.1 because that IP address is this machine's IP address.";
		final public static String getBlockIPSuccessMessage(String ip)
		{
			return "Successfully added \"" + ip + "\" to the blocked IPs list.";
		}
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
		
		final public static String REQUEST_UNBLOCK_IP_INPUT_MESSAGE = "Enter an IP to unblock: ";
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
		
		final public static String CANNOT_GENERATE_NAME_CHANGE_CODE_ERROR_MESSAGE = "Cannot generate a name change code. The connection to the database must be working in order to perform this operation.";
		final public static String REQUEST_USERNAME_INPUT_MESSAGE = "Enter the username of the person who wishes to obtain a new name change code: ";
		final public static String REQUEST_PASSWORD_INPUT_MESSAGE = "Now enter the password associated with that username: ";
		final public static String INVALID_USERNAME_OR_PASSWORD_ERROR_MESSAGE = "The username, the password or both are incorrect.";
		final public static String getNameChangeCodeSuccessMessage(String nameChangeCode)
		{
			return "Name change code success. Your new name change code is: \n" + nameChangeCode;
		}
		
		
		final public static String EXIT_CONFIRM_MESSAGE = "Are you sure you wish to terminate?";
		final public static String EXIT_ERROR_MESSAGE = "Please first stop the server by clicking the \"Stop\" button and then terminate the program.";
		
		final public static String EMERGENCY_STOP_FAILED = "Emergency stop failed. Terminating...";
	}
	
	final public static class SUBSERVER
	{
		final public static String CONNECTION_ENDED_BAD_CONNECTION_PASSWORD_LOG_MESSAGE = "The connection to the database server has ended unexpectedly due to a bad connection password error. The server has been stopped.";
		final public static String CONNECTION_ENDED_IO_EXCEPTION_LOG_MESSAGE = "The connection to the database server he ended unexpectedly due to an internet error. The server has been stopped.";
		final public static String CONNECTION_ENDED_UNKNOWN_LOG_MESSAGE = "The connection to the database server has ended unexpectedly due to an unknown error. The server has been stopped.";
		
		final public static String getUnknownMessageLogMessage(String serverName, String anUnknownMessage, String ip)
		{
			return "Unexpected message received by " + serverName + ": " + anUnknownMessage + " from client with IP address \"" + ip + "\".";
		}
		final public static String getUsernameUnexpectedlyChanged(String goodUsername, String badUsername, String ip)
		{
			return "Client data of \"" + ip + "\" corrupted. It's username should be \"" + goodUsername + "\" but is instead \"" + badUsername + "\".";
		}
		final public static String getClientPerformingUnauthorizedActions(String ip)
		{
			return "\"" + ip + "\" is attempting to circumvent security by performing unauthorized actions before logging in.";
		}
	}
	
	final public static class MAINSERVER
	{
		final public static String NAME_OF_SERVER = "Main Server";
		final public static String getFailedLoginLogMessage(String username, String password, String ip)
		{
			return "Failed login from \"" + ip + "\"" + "with username \"" + username + "\" and password \"" + password + "\".";
		}
		final public static String getMultiLoginLogMessage(String username, String password, String ip)
		{
			return "Multi-login from \"" + ip + "\"" + " with username \"" + username + "\" and password \"" + password + "\".";
		}
		final public static String getFailedRegisterLogMessage(String username, String password, String ip)
		{
			return "Failed register attempt from \"" + ip + "\" with username \"" + username + "\" and password \"" + password + "\".";
		}
		final public static String getFailedPasswordChangeLogMessage(String username, String currentPassword, String ip)
		{
			return "Failed password change attempt from \"" + ip + "\". User \"" + username + "\" provided invalid current password \"" + currentPassword + "\".";
		}
		final public static String getInvalidNewPasswordLogMessage(String username, String invalidPassword, String ip)
		{
			return "Failed password change attempt from \"" + ip + "\". User \"" + username + "\" attempted to set an invalid password \"" + invalidPassword + "\".";
		}
		final public static String getFailedNameChangePasswordErrorLogMessage(String username, String password, String ip)
		{
			return "Failed name change attempt from \"" + ip + "\". User \"" + username + "\" provided invalid password \"" + password + "\".";
		}
		final public static String getFailedNameChangeCodeErrorLogMessage(String username, String nameChangeCode, String ip)
		{
			return "Failed name change attempt from \"" + ip + "\". User \"" + username + "\" provided invalid name change code \"" + nameChangeCode + "\".";
		}
	}
	
	final public static class MESSAGINGSERVER
	{
		final public static String NAME_OF_SERVER = "Messaging Server";
	}
	
	final public static class ROOMDATASERVER
	{
		final public static String NAME_OF_SERVER = "Room Data Server";
	}
	
	final public static class ROOMSERVER
	{
		final public static String NAME_OF_SERVER = "Room Server";
	}
}
