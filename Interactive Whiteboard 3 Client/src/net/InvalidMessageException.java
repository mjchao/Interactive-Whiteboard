package net;

//this exception is thrown when an invalid message from the server is received
final public class InvalidMessageException extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public InvalidMessageException(String message)
	{
		super("Invalid message received: " + message);
	}
}
