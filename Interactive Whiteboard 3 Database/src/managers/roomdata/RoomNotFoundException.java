package managers.roomdata;

final public class RoomNotFoundException extends Exception
{

	private static final long serialVersionUID = 1L;
	
	final private int m_roomID;
	
	public RoomNotFoundException(int roomID)
	{
		super("No existing data for a room with the following room ID: " + roomID);
		this.m_roomID = roomID;
	}
	
	final public int getRoomID()
	{
		return this.m_roomID;
	}
}
