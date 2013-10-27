package data;

/**
 * This exception is thrown when the server cannot provide valid information
 * required to access certain data. For example, to access the display name of
 * a user, the server should be able to provide the correct password of that user.
 * If not, then the database will not grant it access to the display name of
 * the user. This exception is used for extra security and as a side-effect
 * will help with debugging.
 */
final public class AccessDeniedException extends Exception
{
	private static final long serialVersionUID = 1L;

	//Possible error codes:
	final public static int BAD_PASSWORD_ERROR = 1;
	final public static int BAD_NAME_CHANGE_CODE_ERROR = 2;
	final public static int BAD_JOIN_PASSWORD_ERROR = 3;
	final public static int BAD_MODIFICATION_PASSWORD_ERROR = 4;
	
	/**
	 * error code that describes why this error occurred.
	 */
	final private int m_errorCode;
	
	/**
	 * Constructor.
	 * 
	 * @param message 			A message describing why and/or where access was denied should be provided.
	 * @param errorCode 		An integer corresponding to the error that occurred.
	 */
	public AccessDeniedException(String message, int errorCode)
	{
		super(message);
		this.m_errorCode = errorCode;
	}
	
	/**
	 * This method returns an integer that corresponds to a specific error.
	 * 
	 * @return	 	An integer corresponding to an error.
	 * @see			#m_errorCode
	 */
	final public int getErrorCode()
	{
		return this.m_errorCode;
	}

}