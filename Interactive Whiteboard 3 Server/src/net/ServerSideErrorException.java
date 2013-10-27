package net;

public class ServerSideErrorException extends Exception
{
	private static final long serialVersionUID = 1L;
	
	final public static int DATABASE_MISCOMMUNICATION_ERROR = 1;
	final public static int NETWORKING_ERROR = 2;
	
	final private int m_errorID;
	final private String m_invalidMessage;

	public ServerSideErrorException(int errorID, String message)
	{
		super("An unexpected server side error has occurrred. Error Message: " + message);
		this.m_errorID = errorID;
		this.m_invalidMessage = message;
		System.out.println("Invalid message received: " + getInvalidMessage());
	}
	
	final public int getErrorID()
	{
		return this.m_errorID;
	}
	
	final public String getInvalidMessage()
	{
		return this.m_invalidMessage;
	}
}
