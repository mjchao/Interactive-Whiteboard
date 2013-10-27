package gui.room.userlist;

/**
 * Stores information about a user and his/her permissions
 */
final public class UserData 
{

	final private String m_username;
	final private String m_displayName;
	private int m_authority;
	private boolean m_hasAudioParticipation;
	private boolean m_hasAudioListening;
	private boolean m_hasWhiteboard;
	private boolean m_hasTextParticipation;
	private boolean m_hasTextUpdating;
	
	public UserData(String username, String displayName, int authority, boolean hasAudioParticipation, boolean hasAudioListening, boolean hasTextParticipation, boolean hasTextUpdating, boolean hasWhiteboard)
	{
		this.m_username = username;
		this.m_displayName = displayName;
		this.m_authority = authority;
		this.m_hasAudioParticipation = hasAudioParticipation;
		this.m_hasAudioListening = hasAudioListening;
		this.m_hasTextParticipation = hasTextParticipation;
		this.m_hasTextUpdating = hasTextUpdating;
		this.m_hasWhiteboard = hasWhiteboard;
	}
	
	final public String getUsername()
	{
		return this.m_username;
	}
	
	final public String getDisplayName()
	{
		return this.m_displayName;
	}
	
	final public int getAuthority()
	{
		return this.m_authority;
	}
	
	final public boolean hasAudioParticipation()
	{
		return this.m_hasAudioParticipation;
	}
	
	final public void toggleAudioParticipation()
	{
		this.m_hasAudioParticipation = !this.m_hasAudioParticipation;
	}
	
	final public boolean hasAudioListening()
	{
		return this.m_hasAudioListening;
	}
	
	final public void toggleAudioListening()
	{
		this.m_hasAudioListening = !this.m_hasAudioListening;
	}
	
	final public boolean hasTextParticipation()
	{
		return this.m_hasTextParticipation;
	}
	
	final public void toggleTextParticipation()
	{
		this.m_hasTextParticipation = !this.m_hasTextParticipation;
	}
	
	final public boolean hasTextUpdating()
	{
		return this.m_hasTextUpdating;
	}
	
	final public void toggleTextUpdating()
	{
		this.m_hasTextUpdating = !this.m_hasTextUpdating;
	}
	
	final public boolean hasWhiteboard()
	{
		return this.m_hasWhiteboard;
	}
	
	final public void setHasWhiteboard(boolean b)
	{
		this.m_hasWhiteboard = b;
	}
}
