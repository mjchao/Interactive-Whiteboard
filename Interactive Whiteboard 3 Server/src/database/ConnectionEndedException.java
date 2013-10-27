package database;

public class ConnectionEndedException extends Exception
{
	private static final long serialVersionUID = 1L;
	
	final public static int BAD_IP_ADDRESS = 1;
	final public static int BAD_CONNECTION_PASSWORD = 1;
	final public static int WRONG_SERVER = 3;
	final public static int IOEXCEPTION = 2;

	final private int m_errorID;
	
	public ConnectionEndedException(int errorID)
	{
		super("Connection to database terminated unexpectedly.");
		this.m_errorID = errorID;
	}
	
	final public int getErrorID()
	{
		return this.m_errorID;
	}
}
