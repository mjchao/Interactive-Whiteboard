package mc.iwclient.structs;

import mc.iwclient.util.Text;

public class RoomData {

	/**
	 * unique id of the room
	 */
	public String m_id = "";
	
	/**
	 * user-defined name of the room
	 */
	public String m_roomName = "";
	
	/**
	 * username of the creator of the room
	 */
	public String m_creatorUsername = "";
	
	/**
	 * display name of the creator of the room
	 */
	public String m_creatorDisplayName = "";
	
	/**
	 * creation date of the room
	 */
	public String m_creationDate = "";
	
	/**
	 * whether or not a password is required to access the room
	 */
	public boolean m_isPasswordProtected = false;
	
	public static String getPasswordProtectionString( boolean isPasswordProtected ) {
		if ( isPasswordProtected ) {
			return Text.Room.PASSWORD_PROTECTION_ON;
		}
		else {
			return Text.Room.PASSWORD_PROTECTION_OFF;
		}
	}
	
	/**
	 * the user-provided join password to join the room (it is whatever the user entered, and
	 * may or may not be correct)
	 */
	public String m_joinPassword = "";
	
	/**
	 * a description of the room given by the creator of the room
	 */
	public String m_description = "";
	
	public RoomData() {
		
	}
	
	@Override
	public RoomData clone() {
		RoomData rtn = new RoomData();
		rtn.m_id = this.m_id;
		rtn.m_roomName = this.m_roomName;
		rtn.m_creatorUsername = this.m_creatorUsername;
		rtn.m_creatorDisplayName = this.m_creatorDisplayName;
		rtn.m_creationDate = this.m_creationDate;
		rtn.m_isPasswordProtected = this.m_isPasswordProtected;
		rtn.m_joinPassword = this.m_joinPassword;
		rtn.m_description = this.m_description;
		return rtn;
	}
}
