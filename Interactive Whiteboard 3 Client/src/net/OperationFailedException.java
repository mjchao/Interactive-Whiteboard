package net;

final public class OperationFailedException extends Exception
{
	private static final long serialVersionUID = 1L;
	
	final private OperationErrorCode m_errorCode;
	
	public OperationFailedException(OperationErrorCode errorCode)
	{
		this.m_errorCode = errorCode;
	}
	
	final public OperationErrorCode getErrorCode()
	{
		return this.m_errorCode;
	}
}
