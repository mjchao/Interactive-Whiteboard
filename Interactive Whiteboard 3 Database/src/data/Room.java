package data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import managers.userdata.UserNotFoundException;

import util.CommonMethods;

final public class Room
{
	/**
	 * separates data to be saved into pieces. e.g. [part 1] [DELIMITER] [part 2]
	 */
	final public static String DELIMITER = " ";
	
	/**
	 * unique integer identifying this room.
	 */
	final private int m_roomID;
	
	/**
	 * name of this room. for the users' convenience.
	 */
	private String m_roomName;
	
	/**
	 * username of the user who created this room.
	 */
	final private String m_creatorUsername;
	
	/**
	 * date on which this room was created.
	 */
	final private String m_creationDate;
	
	/**
	 * password required to modify room properties (e.g. room name).
	 */
	private String m_modificationPassword;
	
	/**
	 * determines if a join password is required to access room features (e.g. whiteboard, audio chat). true
	 * if a join password is required. false if a join password is not required.
	 * 
	 * @see #m_joinPassword
	 */
	private boolean m_passwordProtected;
	
	/**
	 * password required to join this room if room is password protected.
	 * 
	 * @see #m_passwordProtected
	 */
	private String m_joinPassword;
	
	/**
	 * length of whiteboard (pixels).
	 * 
	 * @see #m_passwordProtected
	 */
	private int m_whiteboardLength;
	
	
	/**
	 * width of whiteboard (pixels).
	 */
	private int m_whiteboardWidth;
	
	/**
	 * information about the individual pixels of the whiteboard of this room.
	 * 
	 * @see Pixel
	 */
	private Pixel[][] m_pixels;
	/**
	 * usernames of moderators assigned to room.
	 */
	final private ArrayList<ListUser> m_moderators = new ArrayList<ListUser>();
	
	/**
	 * text chat history of this room.
	 * 
	 * @see ChatHistory
	 */
	final private ArrayList<ChatHistory> m_chatHistory = new ArrayList<ChatHistory>();
	
	/**
	 * permissions of users. people who have not lost permissions
	 * (e.g. Permissions to use text chat) are not stored here. only people who have lost permissions are stored.
	 * 
	 * @see UserPermissions
	 */
	final private ArrayList<UserPermissions> m_userPermissions = new ArrayList<UserPermissions>();
	
	/**
	 * constructor. called when this room is created. all general room properties
	 * must be provided, except join password.
	 * 
	 * @param roomID 				an integer that is the unique ID associated with this room. 
	 * @param roomName 				a String that is the name of this room.
	 * @param creatorUsername 		a String that is the username of the creator of this room.
	 * @param creationDate 			a String that is the date of creation of this room.
	 * @param modificationPassword 	a String that is the password required to modify any properties of this room
	 * or to delete this room.
	 * @param passwordProtected 	a boolean that is true if a join password is required to join this room and
	 * 								false if a join password is not required to join this room.
	 * @param joinPassword 			a String that is the password required to join this room and access its functions
	 * 								such as text chat and the whiteboard.
	 * @param whiteboardLength 		an integer that is the length of the whiteboard in pixels.
	 * @param whiteboardWidth 		an integer that is the width of the whiteboard in pixels.
	 * @throws IOException 			if room data cannot be saved to a file.
	 */
	public Room(int roomID, String roomName, String creatorUsername, String creationDate, 
				String modificationPassword, boolean passwordProtected, String joinPassword,
				int whiteboardLength, int whiteboardWidth) throws IOException
	{
		//Store this room creation data
		this.m_roomID = roomID;
		this.m_roomName = roomName;
		this.m_creatorUsername = creatorUsername;
		this.m_creationDate = creationDate;
		this.m_modificationPassword = modificationPassword;
		this.m_passwordProtected = passwordProtected;
		this.m_joinPassword = joinPassword;
		this.m_whiteboardLength = whiteboardLength;
		this.m_whiteboardWidth = whiteboardWidth;
		this.m_pixels = new Pixel[this.m_whiteboardLength][this.m_whiteboardWidth];
		//Set all pixels to be the default color
		for (int x = 0; x < this.m_whiteboardLength; x++)
		{
			for (int y = 0; y < this.m_whiteboardWidth; y++)
			{
				this.m_pixels[x][y] = new Pixel(x, y);
			}
		}
		//Everything else is to be defined later, by the people using this room
		save();
	}
	
	/** 
	 * constructor. called when room data is loaded from a file.
	 * 
	 * @param file 						a File containing all this room data to be loaded.
	 * @throws IOException 				if a problem occurs with opening the file (e.g. file not found).
	 * @throws NoSuchElementException 	if a line of data is expected, but no line found.
	 * @throws NumberFormatException 	if a number is expected but an integer is expected but no integer read.
	 */
	public Room(File file) throws IOException, NoSuchElementException, NumberFormatException
	{
		//open and prepare to read the file
		FileReader roomDataFileReader = new FileReader(file);
		BufferedReader roomDataBufferedReader = new BufferedReader(roomDataFileReader);
		Scanner s = new Scanner(roomDataBufferedReader);
		//first there is a room id
		int roomID = Integer.parseInt(s.nextLine());
		this.m_roomID = roomID;
		//then there is this room name
		String roomName = s.nextLine();
		this.m_roomName = roomName;
		//then there is the creator's username
		String creatorUsername = s.nextLine();
		this.m_creatorUsername = creatorUsername;
		//then there is the creation date
		String creationDate = s.nextLine();
		this.m_creationDate = creationDate;
		//then there is the modification password
		String modificationPassword = s.nextLine();
		this.m_modificationPassword = modificationPassword;
		//then there is the password protection enabled/disabled
		boolean passwordProtection = Boolean.parseBoolean(s.nextLine());
		this.m_passwordProtected = passwordProtection;
		//then there is the join password
		String joinPassword = s.nextLine();
		this.m_joinPassword = joinPassword;
		//then there is the number of moderators, M
		int numberOfModerators = Integer.parseInt(s.nextLine());
		//then there are M lines of names of moderators
		for (int numberOfModeratorsRead = 0; numberOfModeratorsRead < numberOfModerators; numberOfModeratorsRead++)
		{
			String aModerator = s.nextLine();
			this.m_moderators.add(new ListUser(aModerator));
		}
		//then there is the number of lines of chat history, N
		int linesOfChatHistory = Integer.parseInt(s.nextLine());
		//then there are N lines of chat history (a username and the message sent)
		for (int numberOfLinesRead = 0; numberOfLinesRead < linesOfChatHistory; numberOfLinesRead++)
		{
			String chatHistoryData = s.nextLine();
			Scanner scanChatHistoryData = new Scanner(chatHistoryData);
			String username = scanChatHistoryData.next();
			String message = scanChatHistoryData.next();
			this.m_chatHistory.add(new ChatHistory(username, message));
			scanChatHistoryData.close();
		}
		//then there is the length and width, L and W, of the whiteboard
		String whiteboardDimensions = s.nextLine();
		Scanner scanDimensions = new Scanner(whiteboardDimensions);
		int whiteboardLength = scanDimensions.nextInt();
		this.m_whiteboardLength = whiteboardLength;
		int whiteboardWidth = scanDimensions.nextInt();
		this.m_whiteboardWidth = whiteboardWidth;
		this.m_pixels = new Pixel[whiteboardLength][whiteboardWidth];
		//then there are L*W pixels (x-coordinate, y-coordinate, red, green, blue)
		scanDimensions.close();
		int numberOfPixels = whiteboardLength * whiteboardWidth;
		for (int pixelsRead = 0; pixelsRead < numberOfPixels; pixelsRead++)
		{
			String pixelInformation = s.nextLine();
			Scanner scanPixelInformation = new Scanner(pixelInformation);
			int pixelXCoordinate = scanPixelInformation.nextInt();
			int pixelYCoordinate = scanPixelInformation.nextInt();
			int pixelRed = scanPixelInformation.nextInt();
			int pixelGreen = scanPixelInformation.nextInt();
			int pixelBlue = scanPixelInformation.nextInt();
			String pixelPriority = scanPixelInformation.next();
			Pixel newPixel = new Pixel(pixelXCoordinate, pixelYCoordinate, pixelRed, pixelGreen, pixelBlue, pixelPriority);
			this.m_pixels[pixelXCoordinate][pixelYCoordinate] = newPixel;
			scanPixelInformation.close();
		}
		//then there is the number of people who have lost permissions in this room, U
		int numberOfLostPermissionsUsers = Integer.parseInt(s.nextLine());
		//then there are U lines of usernames and the permissions they have/don't have
		for (int usersRead = 0; usersRead < numberOfLostPermissionsUsers; usersRead++)
		{
			String userPermissions = s.nextLine();
			Scanner scanUserPermissions = new Scanner(userPermissions);
			String userUsername = scanUserPermissions.next();
			boolean userAudioParticipation = scanUserPermissions.nextBoolean();
			boolean userAudioListening = scanUserPermissions.nextBoolean();
			boolean userChatParticipation = scanUserPermissions.nextBoolean();
			boolean userChatUpdating = scanUserPermissions.nextBoolean();
			UserPermissions newUserPermissions = new UserPermissions(userUsername, userAudioParticipation, userAudioListening, userChatParticipation, userChatUpdating);
			this.m_userPermissions.add(newUserPermissions);
			scanUserPermissions.close();
		}
		roomDataFileReader.close();
		roomDataBufferedReader.close();
		s.close();
	}
	
	final public static String ROOM_FILENAME_PREFIX = "room_";
	/** 
	 * saves room data.
	 * 
	 * @throws IOException 	if there is a problem writing to the file.
	 */
	final private void save() throws IOException
	{
		//Determine the file name. It begins with the prefix "room_" followed by this roomID
		String filename = ROOM_FILENAME_PREFIX + this.m_roomID;
		//prepare to write to the file
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filename)));
		//print this room ID
		out.println(this.m_roomID);
		//print this room name
		out.println(this.m_roomName);
		//print the creator username
		out.println(this.m_creatorUsername);
		//print the creation date
		out.println(this.m_creationDate);
		//print the modification password
		out.println(this.m_modificationPassword);
		//print the password protection
		out.println(this.m_passwordProtected);
		//print the join password
		out.println(this.m_joinPassword);
		//print the number of moderators
		int numberOfModerators = this.m_moderators.size();
		out.println(numberOfModerators);
		//print all the moderator usernames, one on each line
		for (int moderatorsPrinted = 0; moderatorsPrinted < numberOfModerators; moderatorsPrinted++)
		{
			int moderatorIndex = moderatorsPrinted;
			out.println(this.m_moderators.get(moderatorIndex).getUsername());
		}
		//print the number of lines of chat history
		int linesOfChatHistory = this.m_chatHistory.size();
		out.println(linesOfChatHistory);
		//print all the lines of chat history
		for (int linesOfChatHistoryPrinted = 0; linesOfChatHistoryPrinted < linesOfChatHistory; linesOfChatHistoryPrinted++)
		{
			int lineIndex = linesOfChatHistoryPrinted;
			ChatHistory line = this.m_chatHistory.get(lineIndex);
			out.println(line.getUsername() + DELIMITER + line.getMessage());
		}
		//print the number of whiteboard length and width
		out.println(this.m_whiteboardLength + DELIMITER + this.m_whiteboardWidth);
		//print all the pixels of the whiteboard
		for (int x = 0; x < this.m_whiteboardLength; x++)
		{
			for (int y = 0; y < this.m_whiteboardWidth; y++)
			{
				Pixel pixelToPrint = this.m_pixels[x][y];
				String lineToPrint = "";
				lineToPrint += pixelToPrint.getXCoordinate() + DELIMITER;
				lineToPrint += pixelToPrint.getYCoordinate() + DELIMITER;
				lineToPrint += pixelToPrint.getRed() + DELIMITER;
				lineToPrint += pixelToPrint.getGreen() + DELIMITER;
				lineToPrint += pixelToPrint.getBlue() + DELIMITER;
				lineToPrint += pixelToPrint.getPriority().toString();
				out.println(lineToPrint);
			}
		}
		//print the number of users that lost permissions
		int usersThatLostPermissions = this.m_userPermissions.size();
		out.println(usersThatLostPermissions);
		//print all the users that lost permissions (username and then permissions)
		for (int usersPrinted = 0; usersPrinted < usersThatLostPermissions; usersPrinted++)
		{
			int userIndex = usersPrinted;
			UserPermissions userToPrint = this.m_userPermissions.get(userIndex);
			String lineToPrint = "";
			lineToPrint += userToPrint.getUsername() + DELIMITER;
			lineToPrint += userToPrint.isAudioParticipationOn() + DELIMITER;
			lineToPrint += userToPrint.isAudioListeningOn() + DELIMITER;
			lineToPrint += userToPrint.isChatParticipationOn() + DELIMITER;
			lineToPrint += userToPrint.isChatUpdatingOn() + DELIMITER;
			out.println(lineToPrint);
		}
		//close the file
		out.close();
		//we are done
	}
	
	/**
	 * attempts to save room data and handles exceptions that could occur while saving.
	 */
	final public void attemptSave()
	{
		try
		{
			save();
		} catch (IOException e)
		{
			//log the error
			CommonMethods.logInternalMessage("UNEXPECTED ERROR: Could not write to file \"" + ROOM_FILENAME_PREFIX + this.m_roomName + "\"");
		}
	}
	
	/**
	 * @return 		the unique room ID assigned to this room.
	 * @see 		#m_roomID
	 */
	final public int getRoomID()
	{
		return this.m_roomID;
	}
	
	/**
	 * @return 		the user-defined name of this room.
	 * @see 		#m_roomName
	 */
	final public String getRoomName()
	{
		return this.m_roomName;
	}
	
	
	/**
	 * attempts to change the name of this room.
	 * Assumes:
	 * 1) Given name is not null<br>
	 * 2) Given name is not empty String<br>
	 * 3) New name meets all naming requirements<br>
	 * 
	 * @param modificationPassword 		password that must be provided when attempting to change room properties
	 * @param newRoomName 				the new name for this room.
	 * @throws AccessDeniedException 	if the modification password is incorrect.
	 * @see 							#m_roomName
	 */
	final public void setRoomName(String modificationPassword, String newRoomName) throws AccessDeniedException
	{
		assertModificationPasswordIsCorrect(modificationPassword);
		this.m_roomName = newRoomName;
		//attemptSave();
	}
	
	/**
	 * @return 		the username of this room creator.
	 * @see 		#m_creatorUsername
	 */
	final public String getCreatorName()
	{
		return this.m_creatorUsername;
	}
	
	/**
	 * @return 		the creation date of this room.
	 * @see 		#m_creationDate
	 */
	final public String getCreationDate()
	{
		return this.m_creationDate;
	}
	
	/**
	 * @return 	the modification password of this room.
	 * @see 	#m_modificationPassword
	 */
	final public String getModificationPassword()
	{
		return this.m_modificationPassword;
	}
	
	/**
	 * attempts to change the modification password of this room.
	 * 
	 * assumes:<br>
	 * 1) password is not null<br>
	 * 2) password is not empty<br>
	 * 3) password meets all password requirements<br>
	 * 
	 * @param oldModificationPassword 	a String, the current modification password of this room. A user that wishes to 
	 * 									change the modification password should know the current modification password
	 * @param newModificationPassword 	a String, the new modification password for this room. This method assumes the
	 * 									new modification password meets all password requirements
	 * @throws AccessDeniedException 	if the old modification password is incorrect.
	 * @see 							#m_modificationPassword
	 */
	final public void setModificationPassword(String oldModificationPassword, String newModificationPassword) throws AccessDeniedException
	{
		assertModificationPasswordIsCorrect(oldModificationPassword);
		this.m_modificationPassword = newModificationPassword;
		//attemptSave();
	}
	
	/**
	 * @return 		true if this room is password protected and false if this room is not password protected
	 * @see 		#m_passwordProtected
	 */
	final public boolean isPasswordProtected()
	{
		return this.m_passwordProtected;
	}
	
	/**
	 * This method attempts to enable or disable the password protection of this room.
	 * @param modificationPassword a String, the current modification password of this room. A user is required
	 * to provide the modification password of a room in order to modify any properties of this room.
	 * @param passwordProtected a boolean, true if the password protection of this room should be enabled
	 * and false if the password protection of this room should be disabled
	 * @throws AccessDeniedException if the modification password provided is incorrect.
	 * @see #m_passwordProtected
	 * @see #m_modificationPassword
	 */
	final public void setPasswordProtected(String modificationPassword, boolean passwordProtected) throws AccessDeniedException
	{
		assertModificationPasswordIsCorrect(modificationPassword);
		this.m_passwordProtected = passwordProtected;
		//attemptSave();
	}
	
	/**
	 * This method returns a String, the join password associated with this room.
	 * @return the join password associated with this room.
	 */
	final public String getJoinPassword()
	{
		return this.m_joinPassword;
	}
	
	/**
	 * This method attempts to change the join password of this room. 
	 * 
	 * This assumes:<br>
	 * 1) the given password is not null<br>
	 * 2) the given password is not empty<br>
	 * 3) the given password meets all password requirements<br>
	 * 
	 * @param modificationPassword 		a String, the modification password of this room. The password is required
	 * 									to modify any of this room's properties
	 * @param oldJoinPassword 			a String, the current join password of this room. The user is required to provide
	 * 									the current join password of this room in order to change the join password.
	 * @param newJoinPassword 			a String, the new join password for this room.
	 * @throws AccessDeniedException 	if the modification password and/or the current join password provided are/is incorrect.
	 * 
	 * @see 							#m_joinPassword
	 */
	final public void setJoinPassword(String modificationPassword, String oldJoinPassword, String newJoinPassword) throws AccessDeniedException
	{
		assertModificationPasswordIsCorrect(modificationPassword);
		assertJoinPasswordIsCorrect(oldJoinPassword);
		this.m_joinPassword = newJoinPassword;
		//attemptSave();
	}
	
	/**
	 * @param joinPassword 					a String, the join password of this room, is required to view access information
	 * 										about the moderators of this room.
	 * @return 								an integer, the number of moderators in this room.
	 * @throws AccessDeniedException 		if the join password is incorrect.
	 * @see									#m_moderators
	 */
	final public int getNumberOfModerators(String joinPassword) throws AccessDeniedException
	{
		assertJoinPasswordIsCorrect(joinPassword);
		return this.m_moderators.size();
	}
	
	/**
	 * gets a specific moderator's username by going through the moderator data and retrieving the information
	 * about the moderator at a given index.
	 * 
	 * @param joinPassword 				a String, the join password for this room.
	 * @param indexOfModerator			an integer, the location of the moderator in the moderator data	
	 * @return							the username of the moderator in the moderator data stored at the given index
	 * @throws AccessDeniedException	if the join password was incorrect
	 * @see								#m_moderators
	 */
	final public String getModeratorUsernameAt(String joinPassword, int indexOfModerator) throws AccessDeniedException
	{
		assertJoinPasswordIsCorrect(joinPassword);
		return this.m_moderators.get(indexOfModerator).getUsername();
	}
	
	/**
	 * Adds a moderator to the stored moderator data. Only the administrator of this room does this.
	 * 
	 * @param modificationPassword			a String, the modification password of this room
	 * @param joinPassword					a String, the join password of this room
	 * @param moderatorUsername				a String, the username of the moderator to add
	 * @throws AccessDeniedException		if the modification password and/or the join password are/is incorrect
	 * @see									#m_moderators
	 */
	final public void addModerator(String modificationPassword, String joinPassword, String moderatorUsername) throws AccessDeniedException
	{
		assertModificationPasswordIsCorrect(modificationPassword);
		assertJoinPasswordIsCorrect(joinPassword);
		this.m_moderators.add(new ListUser(moderatorUsername));
	}
	
	/**
	 * Removes a moderator from the stored moderator data. Only the administrator of this room does this.
	 * 
	 * @param modificationPassword			a String, the modification password of this room
	 * @param joinPassword					a String, the join password of this room
	 * @param moderatorUsername				a String, the username of the moderator to remove
	 * @throws AccessDeniedException		if the modification password and/or the join password are/is incorrect
	 * @see									#m_moderators
	 */
	final public void removeModerator(String modificationPassword, String joinPassword, String moderatorUsername) throws AccessDeniedException, UserNotFoundException
	{
		assertModificationPasswordIsCorrect(modificationPassword);
		assertJoinPasswordIsCorrect(joinPassword);
		removeUserFromList(this.m_moderators, moderatorUsername);
	}
	
	/**
	 * @param joinPassword					a String, the join password of this room
	 * @return								the number of lines of chat history of this room that are stored
	 * @throws AccessDeniedException		if the join password is incorrect
	 * @see									#m_chatHistory
	 */
	final public int getNumberOfLinesOfChatHistory(String joinPassword) throws AccessDeniedException
	{
		assertJoinPasswordIsCorrect(joinPassword);
		return this.m_chatHistory.size();
	}
	
	/**
	 * Determines the sender of a line of chat history given the index of the chat history stored in the data.
	 * Assumes:<br>
	 * 1) The index is not out of range<br>
	 * 
	 * @param joinPassword					a String, the join password of this room
	 * @param indexOfChatHistory			an integer, determines where in the chat history data to look for this line of chat history
	 * @return								the sender of the message of the line of chat history at the given index in the chat history data
	 * @throws AccessDeniedException		if the join password was incorrect
	 * @see									#m_chatHistory
	 */
	final public String getChatHistorySenderAt(String joinPassword, int indexOfChatHistory) throws AccessDeniedException
	{
		assertJoinPasswordIsCorrect(joinPassword);
		return this.m_chatHistory.get(indexOfChatHistory).getUsername();
	}
	
	/**
	 * Determines the message sent in a line of chat history given the index of the chat history stored in this room's data.
	 * Assumes:<br>
	 * 1) The index is not out of range<br>
	 * 
	 * @param joinPassword					a String, the join password of this room
	 * @param indexOfChatHistory			an integer, determines where in the chat history data to look for this line of chat history
	 * @return								the message that was sent for this line of chat history at the given index in the chat history data
	 * @throws AccessDeniedException		if the join password was incorrect
	 * @see									#m_chatHistory
	 */
	final public String getChatHistoryMessageAt(String joinPassword, int indexOfChatHistory) throws AccessDeniedException
	{
		assertJoinPasswordIsCorrect(joinPassword);
		return this.m_chatHistory.get(indexOfChatHistory).getMessage();
	}
	
	/**
	 * Attempts to add a line of chat history to this room's chat history data.
	 * 
	 * @param joinPassword					a String, the join password of this room
	 * @param sender						a String, the person who sent the message
	 * @param message						a String, the message that was broadcasted to everyone in this room
	 * @throws AccessDeniedException		if the join password was incorrect
	 * @see									#m_chatHistory
	 */
	final public void addChatHistory(String joinPassword, String sender, String message) throws AccessDeniedException
	{
		assertJoinPasswordIsCorrect(joinPassword);
		this.m_chatHistory.add(new ChatHistory(sender, message));
		//attemptSave();
	}
	
	/**
	 * @return		the length of the whiteboard in pixels
	 * @see			#m_whiteboardLength
	 */
	final public int getWhiteboardLength()
	{
		return this.m_whiteboardLength;
	}
	
	/**
	 * @return		the width of the whiteboard in pixels
	 * @see			#m_whiteboardWidth
	 */
	final public int getWhiteboardWidth()
	{
		return this.m_whiteboardWidth;
	}
	
	/**
	 * Returns the red of the pixel defined by Red-Green-Blue with the given coordinates x and y.
	 * Assumes:<br>
	 * 1) x is not out of range<br>
	 * 2) y is not out of range<br>
	 * 
	 * @param joinPassword					a String, the join password of this room.
	 * @param x								an integer, the x-coordinate of the pixel
	 * @param y								an integer, the y-coordinate of the pixel
	 * @return								an integer, the red of the pixel at the location (x, y)
	 * @throws AccessDeniedException		if the join password was incorrect
	 * @see									#m_pixels
	 * @see									Pixel#m_red
	 */
	final public int getPixelRedAt(String joinPassword, int x, int y) throws AccessDeniedException
	{
		assertJoinPasswordIsCorrect(joinPassword);
		return this.m_pixels[x][y].getRed();
	}
	
	/**
	 * Returns the green of the pixel defined by Red-Green-Blue with the given coordinates x and y.
	 * Assumes:<br>
	 * 1) x is not out of range<br>
	 * 2) y is not out of range<br>
	 * 
	 * @param joinPassword					a String, the join password of this room.
	 * @param x								an integer, the x-coordinate of the pixel
	 * @param y								an integer, the y-coordinate of the pixel
	 * @return								an integer, the green of the pixel at the location (x, y)
	 * @throws AccessDeniedException		if the join password was incorrect
	 * @see									#m_pixels
	 * @see									Pixel#m_green
	 */
	final public int getPixelGreenAt(String joinPassword, int x, int y) throws AccessDeniedException
	{
		assertJoinPasswordIsCorrect(joinPassword);
		return this.m_pixels[x][y].getGreen();
	}
	
	/**
	 * Returns the blue of the pixel defined by Red-Green-Blue with the given coordinates x and y.
	 * Assumes:<br>
	 * 1) x is not out of range<br>
	 * 2) y is not out of range<br>
	 * 
	 * @param joinPassword					a String, the join password of this room.
	 * @param x								an integer, the x-coordinate of the pixel
	 * @param y								an integer, the y-coordinate of the pixel
	 * @return								the blue of the pixel at the location (x, y)
	 * @throws AccessDeniedException		if the join password was incorrect
	 * @see									#m_pixels
	 * @see									Pixel#m_blue
	 */
	final public int getPixelBlueAt(String joinPassword, int x, int y) throws AccessDeniedException
	{
		assertJoinPasswordIsCorrect(joinPassword);
		return this.m_pixels[x][y].getBlue();
	}
	
	/**
	 * Returns the priority of the pixel at the location (x, y).
	 * 
	 * @param joinPassword					a String, the join password of this room
	 * @param x								an integer, the x-coordinate of the pixel
	 * @param y								an integer, the y-coordinate of the pixel
	 * @return								an integer, the priority of the pixel 
	 * @throws AccessDeniedException		if the join password was incorrect
	 * @see									#m_pixels
	 * @see									Pixel#m_priority
	 */
	final public BigInteger getPixelPriorityAt(String joinPassword, int x, int y) throws AccessDeniedException
	{
		assertJoinPasswordIsCorrect(joinPassword);
		return this.m_pixels[x][y].getPriority();
	}
	
	/**
	 * Sets the pixel at a given location (x, y) with the given properties
	 * 
	 * @param joinPassword					a String, the join password of this room
	 * @param x								an integer, the x-coordinate of the pixel
	 * @param y								an integer, the y-coordinate of the pixel
	 * @param red							an integer, the red of the pixel
	 * @param green							an integer, the green of the pixel
	 * @param blue							an integer, the blue of the pixel
	 * @param priority						an integer, the priority of the pixel
	 * @throws AccessDeniedException		if the join password was incorrect
	 * @see									#m_pixels
	 * @see									Pixel#m_x
	 * @see									Pixel#m_red
	 * @see									Pixel#m_green
	 * @see									Pixel#m_blue
	 * @see									Pixel#m_priority
	 */
	final public void setPixelAt(String joinPassword, int x, int y, int red, int green, int blue, BigInteger priority) throws AccessDeniedException
	{
		assertJoinPasswordIsCorrect(joinPassword);
		this.m_pixels[x][y] = new Pixel(x, y, red, green, blue, priority.toString());
		//attemptSave();
	}
	
	/**
	 * @param joinPassword					a String, the join password of this room
	 * @return								the number of users who have their permissions for this room stored
	 * @throws AccessDeniedException		if the join password was incorrect
	 * @see									#m_userPermissions
	 */
	final public int getNumberOfUserPermissionsStored(String joinPassword) throws AccessDeniedException
	{
		assertJoinPasswordIsCorrect(joinPassword);
		return this.m_userPermissions.size();
	}
	
	/**
	 * Gets the username of a user who has lost permissions in this room given an index in the user permissions data
	 * Assumes:<br>
	 * 1) The index is not out of range<br>
	 * 
	 * @param joinPassword					a String, the join password of this room
	 * @param indexofUserPermission			an integer, the index of the user 
	 * @return								a String, the username of the user at the given index
	 * @throws AccessDeniedException		if the join password was incorrect
	 * @see									#m_userPermissions
	 */
	final public String getUserPermissionUsernameAt(String joinPassword, int indexofUserPermission) throws AccessDeniedException
	{
		assertJoinPasswordIsCorrect(joinPassword);
		return this.m_userPermissions.get(indexofUserPermission).getUsername();
	}
	
	/**
	 * Adds a set of user permissions. Each set of user permissions contains 4 booleans that show if
	 * the user is allowed to participate in audio chat, listen to audio chat, participate in text chat
	 * and get updated text chat. True means the user is allowed. False means the user lost that permission
	 * Assumes:<br>
	 * 1) The given username is of an existing user.
	 * 
	 * @param joinPassword					a String, the join password of this room.
	 * @param username						a String, the username of the user whose permissions will be stored
	 * @param audioParticipation			a boolean, true if the user can participate in audio chat, false if the user cannot
	 * @param audioListening				a boolean, true if the user can listen to audio chat, false if the user cannot
	 * @param chatParticipation				a boolean, true if the user can broadcast messages in text chat, false if the user cannot
	 * @param chatUpdating					a boolean, true if the user can see newly broadcasted messages in text chat, false if the user cannot
	 * @throws AccessDeniedException		if the join password was incorrect
	 */
	final public void addUserPermissions(String joinPassword, String username,
										boolean audioParticipation, boolean audioListening, 
										boolean chatParticipation, boolean chatUpdating) throws AccessDeniedException
	{
		assertJoinPasswordIsCorrect(joinPassword);
		removeUserPermissions(username);
		this.m_userPermissions.add(new UserPermissions(username, audioParticipation, audioListening, chatParticipation, chatUpdating));
		//attemptSave();
	}
	
	/**
	 * Removes all user permissions of the user with the given username. Called when a new set of user permissions
	 * is added to the current list because we do not know if there is already user permissions for the new
	 * person, so we might as well just remove all user permissions for this user and then add the newest version
	 * Assumes:<br>
	 * 1) The given username is of an existing user
	 * 
	 * @param username		a String, the username of the user whose permissions will be erased from the data.
	 * @see					UserPermissions
	 */
	//this method is used whenever we add another user permissions. since we do not know if there is already
	//user permissions for this person, we just remove all user permissions for this user and then
	//add the newest version.
	final private void removeUserPermissions(String username)
	{
		for (int permissionsIndex = 0; permissionsIndex < this.m_userPermissions.size(); permissionsIndex++)
		{
			if (this.m_userPermissions.get(permissionsIndex).getUsername().equals(username))
			{
				this.m_userPermissions.remove(permissionsIndex);
				permissionsIndex--;
			}
		}
	}
	
	/**
	 * Removes the permissions of a user given the username. Used
	 * instead of <code>removeUserPermissions(String username)</code> when certain
	 * that user exists, but not certain if user has permissions stored.
	 * 
	 * @param joinPassword						a String, the join password of this room
	 * @param username							a String, the username identifying the set of permissions to remove
	 * @throws AccessDeniedException			if the join password was incorrect
	 * @throws UserNotFoundException			if no permissions for the user were found.
	 * @see										#m_userPermissions
	 * @see										#removeUserPermissions(String)
	 * @see										UserPermissions
	 */
	final public void findAndRemoveUserPermissions(String joinPassword, String username) throws AccessDeniedException, UserNotFoundException
	{
		assertJoinPasswordIsCorrect(joinPassword);
		removePermissionFromList(this.m_userPermissions, username);
	}
	
	/**
	 * Determines if a user is allowed to participate in audio chat in this room when given an index.
	 * Assumes:<br>
	 * 1) the given index is not out of range<br>
	 * 
	 * @param joinPassword					a String, the join password of this room
	 * @param indexOfUserPermission			an integer, the location of the user in the user permissions data
	 * @return								if the user is permitted to participate in audio chat in this room, true if allowed, false if not allowed.
	 * @throws AccessDeniedException		if the join password was incorrect
	 * @see									#m_userPermissions
	 * @see									UserPermissions
	 */
	final public boolean getUserPermissionsAudioParticipationAt(String joinPassword, int indexOfUserPermission) throws AccessDeniedException
	{
		assertJoinPasswordIsCorrect(joinPassword);
		return this.m_userPermissions.get(indexOfUserPermission).isAudioParticipationOn();
	}
	
	/**
	 * Determines if a user is allowed to listen to audio chat in this room when given an index.
	 * Assumes:<br>
	 * 1) the given index is not out of range<br>
	 * 
	 * @param joinPassword					a String, the join password of this room
	 * @param indexOfUserPermission			an integer, the location of the user in the user permissions data
	 * @return								if the user is permitted to listen to audio chat in this room. true if allowed, false if not allowed.
	 * @throws AccessDeniedException		if the join password was incorrect
	 * @see									#m_userPermissions
	 * @see									UserPermissions
	 */
	final public boolean getUserPermissionsAudioListeningAt(String joinPassword, int indexOfUserPermission) throws AccessDeniedException
	{
		assertJoinPasswordIsCorrect(joinPassword);
		return this.m_userPermissions.get(indexOfUserPermission).isAudioListeningOn();
	}
	
	/**
	 * Determines if a user is allowed to participate in text chat in this room when given an index.
	 * Assumes:<br>
	 * 1) the given index is not out of range<br>
	 * 
	 * @param joinPassword					a String, the join password of the room
	 * @param indexOfUserPermission			an integer, the location of the user in the user permissions data
	 * @return								if the user is allowed to participate in text chat. true if the user is allowed to participate, false otherwise
	 * @throws AccessDeniedException		if the join password was incorrect
	 */
	final public boolean getUserPermissionsChatParticipationAt(String joinPassword, int indexOfUserPermission) throws AccessDeniedException
	{
		assertJoinPasswordIsCorrect(joinPassword);
		return this.m_userPermissions.get(indexOfUserPermission).isChatParticipationOn();
	}
	
	/**
	 * Determines if a user's text chat history will update given an index.
	 * Assumes:<br>
	 * 1) the given index is not out of range
	 * 
	 * @param joinPassword					a String, the join password of the room
	 * @param indexOfUserPermission			an integer, the location of the user in the user permissions data
	 * @return								if the user's text chat history will update. true if the chat history will update, false otherwise
	 * @throws AccessDeniedException		if the join password was incorrect
	 */
	final public boolean getUserPermissionsChatUpdatingAt(String joinPassword, int indexOfUserPermission) throws AccessDeniedException
	{
		assertJoinPasswordIsCorrect(joinPassword);
		return this.m_userPermissions.get(indexOfUserPermission).isChatUpdatingOn();
	}
	
	/**
	 * Asserts that a provided join password is correct. If not correct, an exception will be thrown
	 * 
	 * @param joinPassword					a String, an inputed password to be checked to make sure it is the correct join password
	 * @throws AccessDeniedException		if the join password was incorrect
	 */
	final private void assertJoinPasswordIsCorrect(String joinPassword) throws AccessDeniedException
	{
		if (!joinPassword.equals(this.m_joinPassword))
		{
			throw generateExceptionForBadJoinPassword(this.m_roomID, joinPassword);
		}
	}
	
	/**
	 * Asserts that a provided modification password is correct. If not correct, an exception will be thrown
	 * 
	 * @param modificationPassword			a String, an inputed password to be checked to make sure it is the correct modification password
	 * @throws AccessDeniedException		if the modification password was incorrect
	 */
	final private void assertModificationPasswordIsCorrect(String modificationPassword) throws AccessDeniedException
	{
		if(!modificationPassword.equals(this.m_modificationPassword))
		{
			throw generateExceptionForBadModificationPassword(this.m_roomID, modificationPassword);
		}
	}
	
	/**
	 * Generates an <code>AccessDeniedException</code>. Used by other methods that check if 
	 * certain join passwords are correct.
	 * 
	 * @param roomID			an integer, the ID of the room to be used in the error message
	 * @param joinPassword		a String, a join password that was incorrect
	 * @return					an exception that describes why access was denied
	 * @see						#assertJoinPasswordIsCorrect(String)
	 */
	final private static AccessDeniedException generateExceptionForBadJoinPassword(int roomID, String joinPassword)
	{
		String message = "One or more of the following fields are incorrect:"
							+ "Room ID: " + roomID
							+ "Room Join Password: " + joinPassword;
		return new AccessDeniedException(message, AccessDeniedException.BAD_JOIN_PASSWORD_ERROR);
	}
	
	/**
	 * Generated an <code>AccessDeniedException</code>. Used by other methods that check if certain
	 * modification passwords are correct.
	 * 
	 * @param roomID					an integer, the ID of the room to be used in the error message
	 * @param modificationPassword		a String, a modification password that was incorrect
	 * @return							an exception describing why access was denied
	 * @see								#assertModificationPasswordIsCorrect(String)
	 */
	final private static AccessDeniedException generateExceptionForBadModificationPassword(int roomID, String modificationPassword)
	{
		String message = "One or more of the following fields are incorrect:" + "\n"
							+ "Room ID: " + roomID + "\n"
							+ "Room Modification Password: " + modificationPassword;
		return new AccessDeniedException(message, AccessDeniedException.BAD_MODIFICATION_PASSWORD_ERROR);
	}
	
	/**
	 * Removes a specific user from a given list of users based on some given user data. Currently
	 * used to remove moderators from the moderators list, but could be expanded to remove from
	 * other lists in the future. Since each registered user has a unique username, only
	 * usernames are checked right now.<br>
	 * 
	 * suppressed warnings: <br>
	 * 1) static-method 	the method should not be static. this method cannot work until
	 * 						this room has been created. only created rooms have user lists.<br>
	 * 
	 * @param aList			an <code>ArrayList</code>, the list from which to remove users
	 * @param username		a String, the username of the person to be removed from the list
	 * @see					ListUser
	 */
	@SuppressWarnings("static-method") //This method should not be static. Only rooms that have been created
									   //should be able to remove users from lists
	final private void removeUserFromList(ArrayList<ListUser> aList, String username) throws UserNotFoundException
	{
		for (int userIndex = 0; userIndex < aList.size(); userIndex++)
		{
			ListUser aUser = aList.get(userIndex);
			if (aUser.getUsername().equals(username))
			{
				aList.remove(userIndex);
				return;
			}
		}
		throw new UserNotFoundException(username);
	}
	
	/**
	 * Removes a specific set of user permissions from a list based on some given user permissions data.
	 * Since usernames are unique, only the username in the user permissions data is currently used to
	 * determine the user permissions data to remove.<br>
	 * 
	 * suppressed-warnings:<br>
	 * 1) static-method						should not be static because no lists in this room can exist
	 * 										before this room has been created.<br>
	 * 
	 * 
	 * @param aList							an <code>ArrayList</code>, the list from which to remove the user permissions data						
	 * @param username						a String, the username of the person's whose user permissions data set should be removed
	 * @throws UserNotFoundException		if no user with the given username was found
	 * @see									UserPermissions
	 */
	@SuppressWarnings("static-method") //This method should not be static. Only rooms that have been created
									   //should be able to remove permissions from lists
	final private void removePermissionFromList(ArrayList<UserPermissions> aList, String username) throws UserNotFoundException
	{
		for (int userIndex = 0; userIndex < aList.size(); userIndex++)
		{
			UserPermissions aPermissions = aList.get(userIndex);
			if (aPermissions.getUsername().equals(username))
			{
				aList.remove(userIndex);
				return;
			}
		}
		throw new UserNotFoundException(username);
	}
	
	
	/**
	 * Stores data needed to identify a user
	 *
	 */
	final private class ListUser
	{
		/**
		 * username of the user
		 */
		final private String m_username;
		
		/**
		 * Constructor. Assumes:<br>
		 * 1) Given username is not null<br>
		 * 2) Given username is not empty<br>
		 * 
		 * @param username		a String, the username that identifies this user.
		 */
		public ListUser(String username)
		{
			this.m_username = username;
		}
		
		/**
		 * @return			the username of this user.
		 * @see				#m_username
		 */
		final public String getUsername()
		{
			return this.m_username;
		}
	}
	
	/**
	 * Stores chat history data. Every line of chat history stores the username of the person who sent the message
	 * and the message s/he sent.
	 *
	 */
	final private class ChatHistory
	{
		/**
		 * username of the sender of the message
		 */
		final private String m_sender;
		
		/**
		 * message contained in this line of chat history
		 */
		final private String m_message;
		
		
		/**
		 * Constructor. Assumes<br>
		 * 1) username is not null<br>
		 * 2) username is not emtpy<br>
		 * 3) message is not null<br>
		 * 4) message is not empty<br>
		 * 
		 * @param sender		a String, the username of the sender of the message
		 * @param message		a String, the message sent by the sender
		 */
		public ChatHistory(String sender, String message)
		{
			this.m_sender = sender;
			this.m_message = message;
		}
		
		/**
		 * @return		the username of the sender
		 * @see			#m_sender
		 */
		final public String getUsername()
		{
			return this.m_sender;
		}
		
		/**
		 * @return		the message sent by the sender
		 * @see			#m_message
		 */
		final public String getMessage()
		{
			return this.m_message;
		}
	}

	/**
	 * the default priority of a pixel, that defines when it should be drawn. note that pixels here overlap
	 * because Java does not draw rectangles that are 1 pixel by 1 pixel, so we have to resort to 2 by 2.
	 */
	final public static BigInteger DEFAULT_PRIORITY = new BigInteger("-1000000");
	//This stores pixel data. Each pixel has a unique coordinate set (x, y). Each pixel has a red, green, blue
	//that identifies which color it is and a priority that determines when the client side should draw it.
	final public class Pixel
	{
		//The default color is white.
		final public static int DEFAULT_RED = 250;
		final public static int DEFAULT_GREEN = 250;
		final public static int DEFAULT_BLUE = 250;

		/**
		 * x coordinate of this pixel
		 */
		final private int m_x;
		
		/**
		 * y coordinate of this pixel
		 */
		final private int m_y;
		
		/**
		 * how red this pixel is.
		 */
		final private int m_red;
		
		/**
		 * how green this pixel is.
		 */
		final private int m_green;
		
		/**
		 * how blue this pixel is.
		 */
		final private int m_blue;
		
		/**
		 * the priority of this pixel, or when it should be drawn. Java cannot draw rectangles that are
		 * 1 pixel by 1 pixel, so we have to draw 2 by 2 rectangles, so they overlap and we need to figure
		 * out which rectangles to draw first.
		 */
		private BigInteger m_priority;
		
		/**
		 * Constructor. Creates a pixel with default properties. The only required information is the x and
		 * y coordinates of the pixel.
		 * Assumes:<br>
		 * 1) x is not out of range<br>
		 * 2) y is not out of range<br>
		 * 
		 * @param x 		x-coordinate of the pixel
		 * @param y 		y-coordinate of the pixel
		 */
		public Pixel(int x, int y)
		{
			this.m_x = x;
			this.m_y = y;
			this.m_red = DEFAULT_RED;
			this.m_green = DEFAULT_GREEN;
			this.m_blue = DEFAULT_BLUE;
			this.m_priority = DEFAULT_PRIORITY;
		}
		
		/**
		 * Constructor. Creates a pixel that is not default (has been modified - e.g. a user changed its color).
		 * Assumes:<br>
		 * 1) x is not out of range<br>
		 * 2) y is not out of range<br>
		 * 3) r is not out of range (0-255)<br>
		 * 4) g is not out of range (0-255)<br>
		 * 5) b is not out of range (0-255)<br>
		 * 
		 * @param 								x an integer, x-coordinate of the pixel
		 * @param 								y an integer, y-coordinate of the pixel
		 * @param r 							an integer, how red the pixel is (0-255)
		 * @param g 							an integer, how green the pixel is (0-255)
		 * @param b 							an integer, how blue the pixel is (0-255)
		 * @param priority 						a String, the priority of the pixel represented in a String. 
		 * 										It will be changed to type <code>BigInteger</code> in case
		 * 										a lot of actions occur and the priorities get extremely high.
		 * 			
		 * @throws NumberFormatException 		if the given priority (as a String) was not a valid number
		 */
		public Pixel(int x, int y, int r, int g, int b, String priority) throws NumberFormatException
		{
			this.m_x = x;
			this.m_y = y;
			this.m_red = r;
			this.m_green = g;
			this.m_blue = b;
			this.m_priority = new BigInteger(priority);
		}
		
		/**
		 * @return 		the x-coordinate of the pixel
		 * @see			#m_x
		 */
		final public int getXCoordinate()
		{
			return this.m_x;
		}
		
		/**
		 * @return 		the y-coordinate of the pixel
		 * @see			#m_y
		 */
		final public int getYCoordinate()
		{
			return this.m_y;
		}
		
		/**
		 * @return 		how red the pixel is
		 * @see			#m_red
		 */
		final public int getRed()
		{
			return this.m_red;
		}
		
		/**
		 * @return 		how green the pixel is
		 * @see			#m_green
		 */
		final public int getGreen()
		{
			return this.m_green;
		}
		
		/**
		 * @return 		how blue the pixel is
		 * @see			#m_blue
		 */
		final public int getBlue()
		{
			return this.m_blue;
		}
		
		/**
		 * @return 		the priority of the pixel, used to determine the order in which pixels are drawn
		 * @see			#m_priority
		 */
		final public BigInteger getPriority()
		{
			return this.m_priority;
		}
	}
	
	/**
	 * 
	 * This stores user permissions. Every user that has lost one or more permissions in this room gets
	 * remembered here. 
	 */
	
	final private class UserPermissions
	{
		/**
		 * the username of this user who has lost permissions
		 */
		final private String m_username;
		
		/**
		 * true if this user can participate in audio chat. false if this user cannot participate in audio chat.
		 */
		private boolean m_isAudioParticipationOn;
		
		/**
		 * true if this user can listen to audio chat. false if this user cannot listen to audio chat.
		 */
		private boolean m_isAudioListeningOn;
		
		/**
		 * true if this user can broadcast messages in text chat. false if this user cannot broadcast messages in text chat
		 */
		private boolean m_isChatParticipationOn;
		
		/**
		 * true if this user can see new messages broadcasted in text chat. false if this user cannot see new messages broadcasted in text chat
		 */
		private boolean m_isChatUpdatingOn;
		
		/**
		 * Constructor.
		 * Assumes:<br>
		 * 1) username is not null<br>
		 * 2) username is not empty<br>
		 * 
		 * @param username						a String, the username of this user
		 * @param isAudioParticipationOn		a boolean, if this user is allowed to participate in audio chat
		 * @param isAudioListeningOn			a boolean, if this user is allowed to listen to audio chat
		 * @param isChatParticipationOn			a boolean, if this user is allowed to participate in text chat
		 * @param isChatUpdatingOn				a boolean, if this user is allowed to see newly broadcasted messages in text chat
		 */
		public UserPermissions(String username,
								boolean isAudioParticipationOn, boolean isAudioListeningOn,
								boolean isChatParticipationOn, boolean isChatUpdatingOn)
		{
			this.m_username = username;
			this.m_isAudioParticipationOn = isAudioParticipationOn;
			this.m_isAudioListeningOn = isAudioListeningOn;
			this.m_isChatParticipationOn = isChatParticipationOn;
			this.m_isChatUpdatingOn = isChatUpdatingOn;
		}
		
		/**
		 * @return 		the username of this user
		 * @see			#m_username
		 */
		final public String getUsername()
		{
			return this.m_username;
		}
		
		/**
		 * @return		if this user is allowed to participate in audio chat
		 * @see			#m_isAudioParticipationOn
		 */
		final public boolean isAudioParticipationOn()
		{
			return this.m_isAudioParticipationOn;
		}
		
		/**
		 * @return		if this user is allowed to listen to audio chat
		 * @see			#m_isAudioListeningOn
		 */
		final public boolean isAudioListeningOn()
		{
			return this.m_isAudioListeningOn;
		}
		
		/**
		 * @return		if this user is allowed to participate in text chat
		 * @see			#m_isChatParticipationOn
		 */
		final public boolean isChatParticipationOn()
		{
			return this.m_isChatParticipationOn;
		}
		
		/**
		 * @return		if this user's text chat will update
		 * @see			#m_isChatUpdatingOn
		 */
		final public boolean isChatUpdatingOn()
		{
			return this.m_isChatUpdatingOn;
		}
	}
	
	//DEBUG
	/*final public static void main(String[] args) throws IOException
	{
		File file = new File("DEBUG/data/room_testing");
		Room room = new Room(file);
		System.out.println(room.getRoomID());
		System.out.println(room.getRoomName());
		System.out.println(room.getCreatorName());
		System.out.println(room.getCreationDate());
		System.out.println(room.getModificationPassword());
		System.out.println(room.isPasswordProtected());
		System.out.println(room.getJoinPassword());
		System.out.println(room.getNumberOfModerators());
		for (int i = 0; i < room.getNumberOfModerators(); i++)
		{
			System.out.println(room.getModeratorUsernameAt(i));
		}
		System.out.println(room.getNumberOfLinesOfChatHistory());
		for (int i = 0; i < room.getNumberOfLinesOfChatHistory(); i++)
		{
			System.out.println(room.getChatHistoryUsernameAt(i) + " " + room.getChatHistoryMessageAt(i));
		}
		System.out.println(room.getWhiteboardLength() + " " + room.getWhiteboardWidth());
		for (int x = 0; x < room.getWhiteboardLength(); x++)
		{
			for (int y = 0; y < room.getWhiteboardWidth(); y++)
			{
				System.out.println(room.getPixelRedAt(x, y) + " " + room.getPixelGreenAt(x, y) + " " + room.getPixelBlueAt(x, y) + " " + room.getPixelPriorityAt(x, y));
			}
		}
		System.out.println(room.getNumberOfUserPermissionsStored());
		for (int i = 0; i < room.getNumberOfUserPermissionsStored(); i++)
		{
			System.out.println(room.getUserPermissionUsernameAt(i) + " "
					+ room.getUserPermissionsAudioParticipationAt(i) + " " 
					+ room.getUserPermissionsAudioListeningAt(i) + " " 
					+ room.getUserPermissionsChatParticipationAt(i) + " " 
					+ room.getUserPermissionsChatUpdatingAt(i));
		}
		room.save();
		System.out.println("Test finished");
	}*/
	//END DEBUG
}