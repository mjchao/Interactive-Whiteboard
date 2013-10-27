package gui.roomdata;

/**
 * Stores information about an individual room
 * @author Mickey
 *
 */
final public class RoomData
{
	final private int m_roomID;
	final private String m_name;
	private String m_creatorUsername;
	final private String m_creationDate;
	private String m_modificationPassword;
	final private boolean m_passwordProtection;
	private String m_joinPassword;
	final private int m_whiteboardLength;
	final private int m_whiteboardWidth;
	
	/**
	 * Constructor
	 * 
	 * @param roomID
	 * @param roomName
	 * @param creatorUsername
	 * @param creationDate
	 * @param passwordProtected
	 * @param whiteboardLength
	 * @param whiteboardWidth
	 * @param messagingServerStepConnection			a <code>MessagingServerStepConnection</code> used to determine the display name of the creator
	 */
	public RoomData(int roomID, String roomName, String creatorUsername, String creationDate,
			boolean passwordProtected, int whiteboardLength, int whiteboardWidth)
	{
		this.m_roomID = roomID;
		this.m_name = roomName;
		this.m_creatorUsername = creatorUsername;
		this.m_creationDate = creationDate;
		this.m_modificationPassword = "";
		this.m_passwordProtection = passwordProtected;
		this.m_joinPassword = "";
		this.m_whiteboardLength = whiteboardLength;
		this.m_whiteboardWidth = whiteboardWidth;
	}
	
	final public int getRoomID()
	{
		return this.m_roomID;
	}
	
	final public String getRoomName()
	{
		return this.m_name;
	}
	
	final public String getCreatorUsername()
	{
		return this.m_creatorUsername;
	}
	
	final public void setCreatorUsername(String aUsername)
	{
		if (this.m_creatorUsername == null)
		{
			this.m_creatorUsername = aUsername;
		}
	}
	
	final public String getCreationDate()
	{
		return this.m_creationDate;
	}
	
	final public String getModificationPassword()
	{
		return this.m_modificationPassword;
	}
	
	final public void setModificationPassword(String aPassword)
	{
		this.m_modificationPassword = aPassword;
	}
	
	final public boolean getPasswordProtected()
	{
		return this.m_passwordProtection;
	}
	
	final public String getJoinPassword()
	{
		return this.m_joinPassword;
	}
	
	final public void setJoinPassword(String aPassword)
	{
		this.m_joinPassword = aPassword;
	}
	
	final public int getWhiteboardLength()
	{
		return this.m_whiteboardLength;
	}
	
	final public int getWhiteboardWidth()
	{
		return this.m_whiteboardWidth;
	}
	
	final public RoomData cloneRoomData()
	{
		return new RoomData(this.m_roomID,  this.m_name, this.m_creatorUsername, this.m_creationDate,
							this.m_passwordProtection, this.m_whiteboardLength, this.m_whiteboardWidth);
	}
}
