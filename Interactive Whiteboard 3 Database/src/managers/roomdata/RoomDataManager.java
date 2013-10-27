package managers.roomdata;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import util.CommonMethods;
import util.Text;

import managers.userdata.UserListManager;
import managers.userdata.UserNotFoundException;

import data.AccessDeniedException;
import data.Room;

/**
 * contains methods that deal with modifying/obtaining room data. also responsible
 * for making sure that whatever wants to modify/obtain room data is allowed to.
 */
final public class RoomDataManager
{
	/**
	 * maximum rooms for which this server will store data
	 */
	final public static int MAXIMUM_ROOMS = 3000;
	
	/**
	 * the rooms that have been loaded
	 */
	final private static ArrayList<Room> m_roomData = new ArrayList<Room>();
	
	/**
	 * loads room data for every room ID. cycles from room ID 0 to the maximum number of rooms that
	 * can be stored and attempts to load them all. if room does not exist, then we just move on.
	 */
	final public static void load()
	{
		for (int roomID = 0; roomID < MAXIMUM_ROOMS; roomID++)
		{
			if (RoomListManager.isRoomCreated(roomID))
			{
				try
				{
					loadRoomData(roomID);
				} catch (IOException e)
				{
					//ignore - must work according to the room list manager
				} catch (NoSuchElementException e)
				{
					CommonMethods.logInternalMessage(Text.ROOM_DATA_MANAGER.getRoomDataFormattedIncorrectlyLogMessage(roomID));
					//ignore if the file was formatted improperly. nothing we can do about that
				}
			}
		}
	}
	
	/**
	 * loads room data for a specific room ID. the room data files have been saved in a specific format
	 * with a specific filename.
	 * 
	 * @param roomID						an integer, the room ID that is unique for every room
	 * @throws IOException					if the file is not found
	 * @throws NoSuchElementException		if there was an error reading in the room data
	 */
	final public static void loadRoomData(int roomID) throws IOException, NoSuchElementException
	{
		//make sure the data isn't already loaded
		boolean alreadyLoaded = false;
		for (int roomIndex = 0; roomIndex < m_roomData.size(); roomIndex++)
		{
			Room aRoom = m_roomData.get(roomIndex);
			if (aRoom.getRoomID() == roomID)
			{
				alreadyLoaded = true;
			}
		}
		//if the room data has already been loaded, then just ignore
		//if the room data has not already been loaded, then load it
		if (!alreadyLoaded)
		{
			String roomFilename = Room.ROOM_FILENAME_PREFIX + roomID;
			File file = new File(roomFilename);
			Room roomToLoad = new Room(file);
			m_roomData.add(roomToLoad);
		}
	}
	
	/**
	 * Creates a room. 
	 * Assumes:<br>
	 * 1) the room ID has not been taken<br>
	 * 
	 * @param roomID						an integer, the ID identifying the room
	 * @param roomName						a String, a user-defined name for the room
	 * @param creatorName					a String, the username of the creator
	 * @param creationDate					a String, when the room was created
	 * @param modificationPassword			a String, the password required to modify room properties
	 * @param passwordProtection			a boolean, if a join password is required to access this room
	 * @param joinPassword					a String, the join password required to access this room if password protection is on
	 * @param whiteboardLength				an integer, the length of the whiteboard
	 * @param whiteboardWidth				an integer, the width of the whiteboard
	 * @throws IOException					if we could not write the room data to the save file
	 */
	final public static void createRoom(int roomID, String roomName, String creatorName, String creationDate, String modificationPassword, boolean passwordProtection, String joinPassword, int whiteboardLength, int whiteboardWidth) throws IOException
	{
		Room newRoom = new Room(roomID, roomName, creatorName, creationDate, modificationPassword, passwordProtection, joinPassword, whiteboardLength, whiteboardWidth);
		m_roomData.add(newRoom);
		RoomListManager.addCreatedRoom(roomID);
	}
		
		final public static boolean isRoomIDUsed(int roomID)
		{
			for (int roomIndex = 0; roomIndex < m_roomData.size(); roomIndex++)
			{
				Room aRoom = m_roomData.get(roomIndex);
				if (aRoom.getRoomID() == roomID)
				{
					return true;
				}
			}
			return false;
		}
	
	/**
	 * @param roomID						an integer, the ID identifying the room
	 * @return								the name of the room with the given ID
	 * @throws RoomNotFoundException		if the room was not found
	 */
	final public static String getRoomName(int roomID) throws RoomNotFoundException
	{
		Room targetRoom = locateRoom(roomID);
		return targetRoom.getRoomName();
	}
	
	/**
	 * @param roomID						an integer, the ID identifying the room
	 * @return								the username of the creator of the room with the given ID
	 * @throws RoomNotFoundException		if the room was not found
	 */
	final public static String getRoomCreatorName(int roomID) throws RoomNotFoundException
	{
		Room targetRoom = locateRoom(roomID);
		return targetRoom.getCreatorName();
	}
	
	/**
	 * @param roomID						an integer, the ID identifying the room
	 * @return								when the room was created
	 * @throws RoomNotFoundException		if the room could not be found
	 */
	final public static String getRoomCreationDate(int roomID) throws RoomNotFoundException
	{
		Room targetRoom = locateRoom(roomID);
		return targetRoom.getCreationDate();
	}
	
	/**
	 * @param roomID						an integer, the ID identifying the room
	 * @return								the modification password of the room
	 * @throws RoomNotFoundException		if the room could not be found
	 */
	final public static String getRoomModificationPassword(int roomID) throws RoomNotFoundException
	{
		Room targetRoom = locateRoom(roomID);
		return targetRoom.getModificationPassword();
	}
	
	/**
	 * @param roomID						an integer, the ID identifying the room
	 * @return								true of the room is password protected, false if not
	 * @throws RoomNotFoundException		if the room could not be found
	 */
	final public static boolean getRoomPasswordProtection(int roomID) throws RoomNotFoundException
	{
		Room targetRoom = locateRoom(roomID);
		return targetRoom.isPasswordProtected();
	}
	
	/**
	 * @param roomID						an integer, the ID identifying the room
	 * @return								the password required to join and use the room's features
	 * @throws RoomNotFoundException		if the room could not be found
	 */
	final public static String getRoomJoinPassword(int roomID) throws RoomNotFoundException
	{
		Room targetRoom = locateRoom(roomID);
		return targetRoom.getJoinPassword();
	}
	
	/**
	 * @param roomID						an integer, the ID identifying the room
	 * @return								the length of the whiteboard of the room
	 * @throws RoomNotFoundException		if the room could not be found
	 */
	final public static int getWhiteboardLength(int roomID) throws RoomNotFoundException
	{
		Room targetRoom = locateRoom(roomID);
		return targetRoom.getWhiteboardLength();
	}
	
	/**
	 * @param roomID						an integer, the ID identifying the room
	 * @return								the width of the whiteboard of the room
	 * @throws RoomNotFoundException		if the room could not be found
	 */
	final public static int getWhiteboardWidth(int roomID) throws RoomNotFoundException
	{
		Room targetRoom = locateRoom(roomID);
		return targetRoom.getWhiteboardWidth();
	}
	
	/**
	 * @param roomID						an integer, the ID identifying the room
	 * @param joinPassword					a String, the password required to join the room
	 * @return								a list containing information describing the pixels on the room's whiteboard
	 * @throws RoomNotFoundException		if the room could not be found		
	 * @throws AccessDeniedException		if the join password was incorrect.
	 */
	final public static ArrayList<Pixel> getWhiteboardPixels(int roomID, String joinPassword) throws RoomNotFoundException, AccessDeniedException
	{
		Room targetRoom = locateRoom(roomID);
		ArrayList<Pixel> pixels = new ArrayList<Pixel>();
		//get all the pixel information
		//start by finding out how many pixels there are
		int maximumX = targetRoom.getWhiteboardLength();
		int maximumY = targetRoom.getWhiteboardWidth();
		//then get the data for every pixel
		//store it and then add the stored data to the list of pixel data
		for (int x = 0; x < maximumX; x++)
		{
			for (int y = 0; y < maximumY; y++)
			{
				int pixelX = x;
				int pixelY = y;
				int pixelRed = targetRoom.getPixelRedAt(joinPassword, x, y);
				int pixelGreen = targetRoom.getPixelGreenAt(joinPassword, x, y);
				int pixelBlue = targetRoom.getPixelBlueAt(joinPassword, x, y);
				if (pixelRed == Room.Pixel.DEFAULT_RED && pixelGreen == Room.Pixel.DEFAULT_GREEN && pixelBlue == Room.Pixel.DEFAULT_BLUE)
				{
					//don't add the pixel
				} else
				{
					BigInteger pixelPriority = targetRoom.getPixelPriorityAt(joinPassword, x, y);
					Pixel pixelToAdd = new Pixel(pixelX, pixelY, pixelRed, pixelGreen, pixelBlue, pixelPriority);
					pixels.add(pixelToAdd);
				}
			}
		}
		return pixels;	
	}
	
	final public static ArrayList<String> getRoomModerators(int roomID, String joinPassword) throws RoomNotFoundException, AccessDeniedException
	{
		Room targetRoom = locateRoom(roomID);
		ArrayList<String> moderatorUsernames = new ArrayList<String>();
		//get the usernames of all moderators
		//start by figuring out how many moderators there are
		int numberOfModerators = targetRoom.getNumberOfModerators(joinPassword);
		//then get the username of each one
		for (int moderatorIndex = 0; moderatorIndex < numberOfModerators; moderatorIndex++)
		{
			moderatorUsernames.add(targetRoom.getModeratorUsernameAt(joinPassword, moderatorIndex));
		}
		return moderatorUsernames;
	}
	
	final public static class Pixel
	{
		final private int m_x;
		final private int m_y;
		final private int m_r;
		final private int m_g;
		final private int m_b;
		final private BigInteger m_priority;
		
		public Pixel(int x, int y, int r, int g, int b, BigInteger priority)
		{
			this.m_x = x;
			this.m_y = y;
			this.m_r = r;
			this.m_g = g;
			this.m_b = b;
			this.m_priority = priority;
		}
		
		final public int getXCoordinate()
		{
			return this.m_x;
		}
		
		final public int getYCoordinate()
		{
			return this.m_y;
		}
		
		final public int getRed()
		{
			return this.m_r;	
		}
		
		final public int getGreen()
		{
			return this.m_g;
		}
		
		final public int getBlue()
		{
			return this.m_b;
		}
		
		final public BigInteger getPriority()
		{
			return this.m_priority;
		}
	}
	
	
	final public static ArrayList<ChatHistory> getRoomChatHistory(int roomID, String joinPassword) throws RoomNotFoundException, AccessDeniedException
	{
		Room targetRoom = locateRoom(roomID);
		ArrayList<ChatHistory> chatHistory = new ArrayList<ChatHistory>();
		//get the chat history data
		//start by figuring out how many lines of chat history there are
		int linesOfChatHistory = targetRoom.getNumberOfLinesOfChatHistory(joinPassword);
		//then get the sender and the message of each line of chat history
		for (int lineIndex = 0; lineIndex < linesOfChatHistory; lineIndex++)
		{
			//store the data
			String sender = targetRoom.getChatHistorySenderAt(joinPassword, lineIndex);
			String message = targetRoom.getChatHistoryMessageAt(joinPassword, lineIndex);
			ChatHistory historyToAdd = new ChatHistory(sender, message);
			//add the data to the list of chat history data
			chatHistory.add(historyToAdd);
		}
		return chatHistory;
	}
	
	final public static class ChatHistory
	{
		final private String m_sender;
		final private String m_message;
		
		public ChatHistory(String sender, String message)
		{
			this.m_sender = sender;
			this.m_message = message;
		}
		
		final public String getSender()
		{
			return this.m_sender;
		}
		
		final public String getMessage()
		{
			return this.m_message;
		}
	}
	
	final public static ArrayList<UserPermissions> getUserPermissions(int roomID, String joinPassword) throws RoomNotFoundException, AccessDeniedException
	{
		Room targetRoom = locateRoom(roomID);
		ArrayList<UserPermissions> userPermissions = new ArrayList<UserPermissions>();
		//get the user permissions
		//first find the number of users that have lost permissions
		int numberOfUserPermissions = targetRoom.getNumberOfUserPermissionsStored(joinPassword);
		//go through all the user permissions stored
		for (int permissionsIndex = 0; permissionsIndex < numberOfUserPermissions; permissionsIndex++)
		{
			//copy the data and store that separately
			String username = targetRoom.getUserPermissionUsernameAt(joinPassword, permissionsIndex);
			boolean audioParticipation = targetRoom.getUserPermissionsAudioParticipationAt(joinPassword, permissionsIndex);
			boolean audioListening = targetRoom.getUserPermissionsAudioListeningAt(joinPassword, permissionsIndex);
			boolean chatParticipation = targetRoom.getUserPermissionsChatParticipationAt(joinPassword, permissionsIndex);
			boolean chatUpdating = targetRoom.getUserPermissionsChatUpdatingAt(joinPassword, permissionsIndex);
			UserPermissions permissionsToAdd = new UserPermissions(username, audioParticipation, audioListening, chatParticipation, chatUpdating);
			//and add the stored data to the list of user permissions
			userPermissions.add(permissionsToAdd);
		}
		return userPermissions;
	}
	
	final public static class UserPermissions
	{
		final private String m_username;
		final private boolean m_audioParticipation;
		final private boolean m_audioListening;
		final private boolean m_chatParticipation;
		final private boolean m_chatUpdating;
		
		public UserPermissions(String username, boolean hasAudioParticipation, boolean hasAudioListening, 
												boolean hasChatParticipation, boolean hasChatUpdating)
		{
			this.m_username = username;
			this.m_audioParticipation = hasAudioParticipation;
			this.m_audioListening = hasAudioListening;
			this.m_chatParticipation = hasChatParticipation;
			this.m_chatUpdating = hasChatUpdating;
		}
		
		final public String getUsername()
		{
			return this.m_username;
		}
		
		final public boolean getAudioParticipation()
		{
			return this.m_audioParticipation;
		}
		
		final public boolean getAudioListening()
		{
			return this.m_audioListening;
		}
		
		final public boolean getChatParticipation()
		{
			return this.m_chatParticipation;
		}
		
		final public boolean getChatUpdating()
		{
			return this.m_chatUpdating;
		}
	}
	
	/**
	 * This class stores methods that may be called as a result of a message from the
	 * room data server. These methods generally apply to modification of room properties,
	 * such as the room name
	 *
	 */
	final public static class RoomDataServer
	{
		final public static void setRoomName(int roomID, String modificationPassword, String newRoomName) throws RoomNotFoundException, AccessDeniedException
		{
			Room targetRoom = locateRoom(roomID);
			targetRoom.setRoomName(modificationPassword, newRoomName);
		}
		
		final public static void setRoomModificationPassword(int roomID, String oldModificationPassword, String newModificationPassword) throws RoomNotFoundException, AccessDeniedException
		{
			Room targetRoom = locateRoom(roomID);
			targetRoom.setModificationPassword(oldModificationPassword, newModificationPassword);
		}
		
		final public static void setRoomPasswordProtection(int roomID, String modificationPassword, boolean passwordProtection) throws RoomNotFoundException, AccessDeniedException
		{
			Room targetRoom = locateRoom(roomID);
			targetRoom.setPasswordProtected(modificationPassword, passwordProtection);
		}
		
		final public static void setRoomJoinPassword(int roomID, String modificationPassword, String oldJoinPassword, String newJoinPassword) throws RoomNotFoundException, AccessDeniedException
		{
			Room targetRoom = locateRoom(roomID);
			targetRoom.setJoinPassword(modificationPassword, oldJoinPassword, newJoinPassword);
		}
	}
	
	/**
	 * This class contains methods that may be called as a result of a message from the room server.
	 * The methods here generally pertain to modifying things in specific rooms (e.g. adding a line
	 * of chat history).
	 */
	final public static class RoomServer
	{
		final public static void addModerator(int roomID, String modificationPassword, String joinPassword, String moderatorUsername) throws RoomNotFoundException, AccessDeniedException, UserNotFoundException
		{
			Room targetRoom = locateRoom(roomID);
			UserListManager.assertUsernameIsRegistered(moderatorUsername);
			targetRoom.addModerator(modificationPassword, joinPassword, moderatorUsername);
		}
		
		final public static void removeModerator(int roomID, String modificationPassword, String joinPassword, String moderatorUsername) throws RoomNotFoundException, AccessDeniedException, UserNotFoundException
		{
			Room targetRoom = locateRoom(roomID);
			UserListManager.assertUsernameIsRegistered(moderatorUsername);
			targetRoom.removeModerator(modificationPassword, joinPassword, moderatorUsername);
		}
		
		final public static void setWhiteboardPixel(int roomID, String joinPassword, int x, int y, int r, int g, int b, BigInteger priority) throws RoomNotFoundException, AccessDeniedException
		{
			Room targetRoom = locateRoom(roomID);
			targetRoom.setPixelAt(joinPassword, x, y, r, g, b, priority);
		}
		
		final public static void addChatHistory(int roomID, String joinPassword, String sender, String message) throws RoomNotFoundException, UserNotFoundException, AccessDeniedException
		{
			Room targetRoom = locateRoom(roomID);
			UserListManager.assertUsernameIsRegistered(sender);
			targetRoom.addChatHistory(joinPassword, sender, message);
		}
		
		final public static void addUserPermissions(int roomID, String joinPassword, String username, 
													boolean audioParticipation, boolean audioListening, 
													boolean chatParticipation, boolean chatUpdating) throws RoomNotFoundException, AccessDeniedException, UserNotFoundException
		{
			Room targetRoom = locateRoom(roomID);
			UserListManager.assertUsernameIsRegistered(username);
			targetRoom.addUserPermissions(joinPassword, username, audioParticipation, audioListening, chatParticipation, chatUpdating);
		}
		
		final public static void removeUserPermissions(int roomID, String joinPassword, String username) throws RoomNotFoundException, UserNotFoundException, AccessDeniedException
		{
			Room targetRoom = locateRoom(roomID);
			UserListManager.assertUsernameIsRegistered(username);
			targetRoom.findAndRemoveUserPermissions(joinPassword, username);
		}
	}
	
	//this method returns the data of a room with the given room ID
	final static Room locateRoom(int roomID) throws RoomNotFoundException
	{
		//go through all data and look for a room with the given room ID
		for (int roomIndex = 0; roomIndex < m_roomData.size(); roomIndex++)
		{
			Room aRoom = m_roomData.get(roomIndex);
			if (aRoom.getRoomID() == roomID)
			{
				return aRoom;
			}
		}
		//if we did not find the room, then we have a problem
		throw new RoomNotFoundException(roomID);
	}
}
