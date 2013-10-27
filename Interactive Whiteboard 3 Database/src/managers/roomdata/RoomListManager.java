package managers.roomdata;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

final public class RoomListManager 
{

	final private static RoomData[] roomData = new RoomData[RoomDataManager.MAXIMUM_ROOMS];
	
	final public static File ROOM_LIST_FILE = new File("roomlist.in");
	
	/**
	 * Reads through the stored list of created rooms
	 * 
	 * @throws IOException		if the file with the room list data cannot be found
	 */
	final public static void load() throws IOException
	{
		FileReader roomListFileReader = new FileReader(ROOM_LIST_FILE);
		BufferedReader roomListBufferedReader = new BufferedReader(roomListFileReader);
		Scanner roomListScanner = new Scanner(roomListBufferedReader);
		int numberOfRooms = roomListScanner.nextInt();
		for (int roomsRead = 0; roomsRead < numberOfRooms; roomsRead++)
		{
			roomData[roomsRead] = new RoomData(roomListScanner.nextInt());
		}
		roomListFileReader.close();
		roomListBufferedReader.close();
		roomListScanner.close();
	}
	
	final public static int getNumberOfExistingRooms()
	{
		int numberOfExistingRooms = 0;
		for (int roomIndex = 0; roomIndex < roomData.length; roomIndex++)
		{
			if (roomData[roomIndex] != null)
			{
				numberOfExistingRooms++;
			}
		}
		return numberOfExistingRooms;
	}
	
	/**
	 * @param roomID		an integer, the ID of a room
	 * @return				if the room exists or not
	 */
	final public static boolean isRoomCreated(int roomID)
	{
		if (roomData[roomID] != null)
		{
			return true;
		}
		return false;
	}
	
	/**
	 * Adds a room to the current list of created rooms
	 * 
	 * @param roomID		an integer, the ID of the newly created room.
	 */
	final public static void addCreatedRoom(int roomID)
	{
		roomData[roomID] = new RoomData(roomID);
		attemptSave();
	}
	
	/**
	 * Saves the data.
	 * @throws IOException
	 */
	final public static void save() throws IOException
	{
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(ROOM_LIST_FILE)));
		//count the number of created rooms
		int roomCount = getNumberOfExistingRooms();
		out.println(roomCount);
		//then print the room data
		for (int roomIndex = 0; roomIndex < roomData.length; roomIndex++)
		{
			RoomData roomDataToPrint = roomData[roomIndex];
			if (roomDataToPrint != null)
			{
				//save the room data
				try 
				{
					RoomDataManager.locateRoom(roomIndex).attemptSave();
				} catch (RoomNotFoundException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//save the room list
				out.println(roomDataToPrint.getRoomID());
			}
		}
		out.close();
	}
	
	final public static void attemptSave()
	{
		try
		{
			save();
			load();
		} catch (IOException e)
		{
			//ignore - must work or else file was deleted by user
		}
	}
	
	final private static class RoomData
	{
		/**
		 * integer ID that is unique for each room
		 */
		final private int m_roomID;
		
		public RoomData(int roomID)
		{
			this.m_roomID = roomID;
		}
		
		final public int getRoomID()
		{
			return this.m_roomID;
		}
		
	}
}
