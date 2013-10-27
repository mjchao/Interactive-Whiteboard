package managers.userdata;

final public class UserNotFoundException extends Exception
{
	private static final long serialVersionUID = 1L;

	final private String m_targetUsername;
	public UserNotFoundException(String username)
	{
		super("No existing data for a user with the following username: " + username);
		this.m_targetUsername = username;
	}
	
	final public String getTargetUsername()
	{
		return this.m_targetUsername;
	}
}
