package net;

public class TamperedClientException extends Exception
{
	private static final long serialVersionUID = 1L;

	final public static int INVALID_USERNAME = 1;
	final public static int INVALID_JOIN_PASSWORD = 2;
	
	final private int m_erorrID;
	final private String m_correctInformation;
	final private String m_incorrectProvidedInformation;
	final private String m_ip;
	
	public TamperedClientException(int errorID, String correctInformation, String incorrectProvidedInformation, String ip)
	{
		this.m_erorrID = errorID;
		this.m_correctInformation = correctInformation;
		this.m_incorrectProvidedInformation = incorrectProvidedInformation;
		this.m_ip = ip;
	}
	
	final public int getErrorID()
	{
		return this.m_erorrID;
	}
	
	final public String getCorrectInformation()
	{
		return this.m_correctInformation;
	}
	
	final public String getIncorrectProvidedInformation()
	{
		return this.m_incorrectProvidedInformation;
	}
	
	final public String getIP()
	{
		return this.m_ip;
	}
}
