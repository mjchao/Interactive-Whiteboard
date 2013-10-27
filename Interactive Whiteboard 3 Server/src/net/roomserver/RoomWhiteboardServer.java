package net.roomserver;

import java.io.IOException;
import java.math.BigInteger;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

import database.ConnectionEndedException;
import database.DatabaseConnection;

import net.MESSAGES;
import net.Server;
import net.ServerSideErrorException;
import net.TamperedClientException;

final public class RoomWhiteboardServer extends RoomFeatureServer
{	
	
	final public static int DEFAULT_RED = 250;
	final public static int DEFAULT_GREEN = 250;
	final public static int DEFAULT_BLUE = 250;
	
	final public static BigInteger DEFAULT_PRIORITY = new BigInteger("-1000000000");
	final public static BigInteger ONE = new BigInteger("1");
	protected BigInteger m_currentGreatestPriority = DEFAULT_PRIORITY;
	
	private int m_length;
	private int m_width;
	
	protected Pixel[][] m_pixels;
	
	final private RoomUserListServer m_userList;
	
	public RoomWhiteboardServer(int stepPort, int concurrencyPort, int roomID, String modificationPassword, String joinPassword, int length, int width, RoomUserListServer userList) throws IOException, ConnectionEndedException 
	{
		super(stepPort, concurrencyPort, roomID, modificationPassword, joinPassword);
		super.m_clients = new RoomWhiteboardSubServer[Server.MAX_CLIENTS];
		this.m_userList = userList;
		this.m_length = length;
		this.m_width = width;
		setWhiteboardDimensions(this.m_length, this.m_width);
		loadPixels();
	}

	@Override
	final protected SubServer assignClientToSubServer(Socket aClientConnection, Socket aClientConcurrencyConnection) throws IOException 
	{
		return new RoomWhiteboardSubServer(aClientConnection, aClientConcurrencyConnection);
	}

	@Override
	final protected boolean isClientLoggedIn(String username) 
	{
		for (int clientIndex = 0; clientIndex < this.m_clients.length; clientIndex++)
		{
			RoomWhiteboardSubServer aClient = (RoomWhiteboardSubServer) this.m_clients[clientIndex];
			if (aClient != null)
			{
				if (aClient.getUsername().equals(username))
				{
					if (aClient.isLoggedIn())
					{
						System.out.println("Still logged in");
						return true;
					}
				}
			}
		}
		return false;
	}
	
	final protected int getLength()
	{
		return this.m_length;
	}
	
	final protected int getWidth()
	{
		return this.m_width;
	}
	
	final protected boolean doesClientHaveWhiteboard(String username)
	{
		return this.m_userList.doesUserHaveWhiteboard(username);
	}
	
	final private void loadPixels() throws ConnectionEndedException
	{
		String messageToDatabase = database.MESSAGES.ROOMDATA.HEADING + database.MESSAGES.DELIMITER + database.MESSAGES.ROOMDATA.ROOMSERVER.ROOM_SERVER_HEADING + database.MESSAGES.DELIMITER + database.MESSAGES.ROOMDATA.ROOMSERVER.GET_WHITEBOARD_PIXELS + database.MESSAGES.DELIMITER + this.m_roomID + database.MESSAGES.DELIMITER + this.m_joinPassword;
		String response = DatabaseConnection.sendMessageAndGetResponse(messageToDatabase);
		//look through the response
		Scanner scanResponse = new Scanner(response);
		//first part should be result
		String result = scanResponse.next();
		if (result.equals(database.MESSAGES.ROOMDATA.ROOMSERVER.GET_WHITEBOARD_PIXELS_SUCCESS))
		{
			//if successful, load the pixels
			//first part should be number of pixels
			int numberOfPixels = scanResponse.nextInt();
			for (int pixelsRead = 0; pixelsRead < numberOfPixels; pixelsRead++)
			{
				//read x coordinate
				int x = scanResponse.nextInt();
				//read y coordinate
				int y = scanResponse.nextInt();
				//read red value
				int r = scanResponse.nextInt();
				//read green value
				int g = scanResponse.nextInt();
				//read blue value
				int b = scanResponse.nextInt();
				//read priority
				BigInteger priority = scanResponse.nextBigInteger();
				this.m_pixels[x][y] = new Pixel(x, y, r, g, b, priority);
			}
		} else
		{
			//if not successful, assume no pixels on this whiteboard
		}
	}
	
	final protected BigInteger getCurrentGreatestPriorty()
	{
		return this.m_currentGreatestPriority;
	}
	
	final protected void setNewGreatestPriority(BigInteger newGreatestPriority)
	{
		this.m_currentGreatestPriority = newGreatestPriority;
	}
	
	final protected Pixel getPixel(int xCoordinate, int yCoordinate)
	{
		return this.m_pixels[xCoordinate][yCoordinate];
	}
	
	final protected void setPixel(int xCoordinate, int yCoordinate, int red, int green, int blue, BigInteger priority)
	{
		this.m_pixels[xCoordinate][yCoordinate] = new Pixel(xCoordinate, yCoordinate, red, green, blue, priority);
		for (int clientIndex = 0; clientIndex < this.m_clients.length; clientIndex++)
		{
			RoomWhiteboardSubServer aClient = (RoomWhiteboardSubServer) this.m_clients[clientIndex];
			if (aClient != null)
			{
				aClient.sendPixelDataAt(xCoordinate, yCoordinate);
			}
		}
	}
	
	final protected void setWhiteboardDimensions(int newLength, int newWidth)
	{
		this.m_length = newLength;
		this.m_width = newWidth;
		Pixel[][] newPixels = new Pixel[newLength][newWidth];
		for (int x = 0; x < newLength; x++)
		{
			for (int y = 0; y < newWidth; y++)
			{
				try
				{
					newPixels[x][y] = this.m_pixels[x][y];
				} catch (ArrayIndexOutOfBoundsException e)
				{
					newPixels[x][y] = new Pixel(x, y);
				} catch (NullPointerException e)
				{
					newPixels[x][y] = new Pixel(x, y);
				}
			}
		}
		this.m_pixels = newPixels;
	}
	
	@Override
	public void closeConnectionWithClient(String username)
	{
		for (int clientIndex = 0; clientIndex < this.m_clients.length; clientIndex++)
		{
			RoomWhiteboardSubServer aClient = (RoomWhiteboardSubServer) this.m_clients[clientIndex];
			if (aClient != null)
			{
				if (aClient.getUsername().equals(username))
				{
					aClient.closeAndStop();
					this.removeAClient(aClient.getSubServerID());
					return;
				}
			}
		}
	}

	final private class RoomWhiteboardSubServer extends RoomFeatureSubServer
	{

		public RoomWhiteboardSubServer(Socket s, Socket concurrentConnection) throws IOException 
		{
			super(s, concurrentConnection);
		}

		@Override
		protected void decode(String messageFromClient) throws ConnectionEndedException, ServerSideErrorException, TamperedClientException, NoSuchElementException 
		{
			Scanner scanMessage = new Scanner(messageFromClient);
			String command = scanMessage.next();
			if (command.equals(MESSAGES.GENERAL.LOGIN))
			{
				//expect a username, a password and a join password
				String username = scanMessage.next();
				String password = scanMessage.next();
				String joinPassword = scanMessage.next();
				super.login(username, password, joinPassword);
				if (this.loggedIn)
				{
					sendAllPixelDataToClient();
				}
			} else if (this.loggedIn)
			{
				if (command.equals(MESSAGES.ROOMSERVER.WHITEBOARD_SERVER.DRAW_PIXEL))
				{
					//expect a username, a join password, x coordinate, y coordinate, and three 
					//integers for red, green and blue
					String username = scanMessage.next();
					String joinPassword = scanMessage.next();
					int xCoordinate = scanMessage.nextInt();
					int yCoordinate = scanMessage.nextInt();
					int red = scanMessage.nextInt();
					int green = scanMessage.nextInt();
					int blue = scanMessage.nextInt();
					setPixelColor(username, joinPassword, xCoordinate, yCoordinate, red, green, blue);
				}
			} else
			{
				super.closeClientPerformingUnauthorizedActions();
			}
		}
		
		final private void setPixelColor(String username, String joinPassword, int xCoordinate, int yCoordinate, int red, int green, int blue) throws TamperedClientException, ConnectionEndedException, ServerSideErrorException
		{
			assertUsernameIsCorrect(username);
			assertJoinPasswordIsCorrect(joinPassword);
			//check that the user has the whiteboard
			if (RoomWhiteboardServer.this.doesClientHaveWhiteboard(username))
			{
				int roomID = RoomWhiteboardServer.this.getRoomID();
				//use the newest version of this room's join password
				String joinPasswordToUse = RoomWhiteboardServer.this.getJoinPassword();
				//add one to the current greatest priority - this new priority means this pixel should be drawn last
				BigInteger priorityToUse = RoomWhiteboardServer.this.getCurrentGreatestPriorty().add(ONE);
				//update the greatest priority
				RoomWhiteboardServer.this.setNewGreatestPriority(priorityToUse);
				String messageToDatabase = database.MESSAGES.ROOMDATA.HEADING + database.MESSAGES.DELIMITER + database.MESSAGES.ROOMDATA.ROOMSERVER.ROOM_SERVER_HEADING + database.MESSAGES.DELIMITER + database.MESSAGES.ROOMDATA.ROOMSERVER.SET_WHITEBOARD_PIXEL + database.MESSAGES.DELIMITER + roomID + database.MESSAGES.DELIMITER + joinPasswordToUse + database.MESSAGES.DELIMITER + xCoordinate + database.MESSAGES.DELIMITER + yCoordinate + database.MESSAGES.DELIMITER + red + database.MESSAGES.DELIMITER + green + database.MESSAGES.DELIMITER + blue + database.MESSAGES.DELIMITER + priorityToUse;
				String response = DatabaseConnection.sendMessageAndGetResponse(messageToDatabase);
				//look through the response
				Scanner scanResponse = new Scanner(response);
				//first part should be the result
				String result = scanResponse.next();
				if (result.equals(database.MESSAGES.ROOMDATA.ROOMSERVER.SET_WHITEBOARD_PIXEL_SUCCESS))
				{
					//if successful, great, update the data and then all the clients
					RoomWhiteboardServer.this.setPixel(xCoordinate, yCoordinate, red, green, blue, priorityToUse);
					write(MESSAGES.ROOMSERVER.WHITEBOARD_SERVER.DRAW_PIXEL_SUCCESS);
				} else if (result.equals(database.MESSAGES.ROOMDATA.ROOMSERVER.SET_WHITEBOARD_PIXEL_FAILED))
				{
					//if failed, figure out why and let client know
					//an error code should follow
					int errorCode = scanResponse.nextInt();
					scanResponse.close();
					if (errorCode == database.MESSAGES.ROOMDATA.NONEXISTING_ROOM_ERROR_CODE)
					{
						write(MESSAGES.ROOMSERVER.WHITEBOARD_SERVER.DRAW_PIXEL_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMSERVER.ROOM_DOES_NOT_EXIST_ERROR_CODE);
					} else if (errorCode == database.MESSAGES.ROOMDATA.INVALID_JOIN_PASSWORD_ERROR_CODE)
					{
						write(MESSAGES.ROOMSERVER.WHITEBOARD_SERVER.DRAW_PIXEL_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMSERVER.INVALID_JOIN_PASSWORD_ERROR_CODE);
					} else if (errorCode == database.MESSAGES.ROOMDATA.ROOMSERVER.INDEX_OUT_OF_BOUNDS_ERROR_CODE)
					{
						write(MESSAGES.ROOMSERVER.WHITEBOARD_SERVER.DRAW_PIXEL_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMSERVER.WHITEBOARD_SERVER.OUT_OF_BOUNDS_ERROR_CODE);
					} else if (errorCode == database.MESSAGES.ROOMDATA.ROOMSERVER.INVALID_COLOR_ERROR_CODE)
					{
						write(MESSAGES.ROOMSERVER.WHITEBOARD_SERVER.DRAW_PIXEL_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMSERVER.WHITEBOARD_SERVER.INVALID_COLOR_ERROR_CODE);
					} else
					{
						throw generateDatabaseMiscommunicationError(response);
					}
				} else
				{
					throw generateDatabaseMiscommunicationError(response);
				}
				scanResponse.close();
			} else
			{
				write(MESSAGES.ROOMSERVER.WHITEBOARD_SERVER.DRAW_PIXEL_FAILED + MESSAGES.DELIMITER + MESSAGES.ROOMSERVER.YOU_LACK_WHITEBOARD_ERROR_CODE);
			}
		}
		
		@Override
		protected void handleTerminatingConnection() 
		{
			this.closeAndStop();
			RoomWhiteboardServer.this.removeAClient(this.m_subServerID);
		}
		
		final protected void sendAllPixelDataToClient()
		{
			writeConcurrent(MESSAGES.ROOMSERVER.WHITEBOARD_SERVER.START_UPDATING);
			for (int x = 0; x < RoomWhiteboardServer.this.getLength(); x++)
			{
				for (int y = 0; y < RoomWhiteboardServer.this.getWidth(); y++)
				{
					sendPixelDataIgnoringDefaultColorAt(x, y);
				}
			}
			writeConcurrent(MESSAGES.ROOMSERVER.WHITEBOARD_SERVER.DONE_UPDATING);
		}
		
		final protected void sendPixelDataIgnoringDefaultColorAt(int pixelX, int pixelY)
		{
			Pixel pixelToSend = RoomWhiteboardServer.this.getPixel(pixelX, pixelY);
			int x = pixelToSend.getXCoordinate();
			int y = pixelToSend.getYCoordinate();
			int red = pixelToSend.getRed();
			int green = pixelToSend.getGreen();
			int blue = pixelToSend.getBlue();
			BigInteger priority = pixelToSend.getPriority();
			if (red != DEFAULT_RED || green != DEFAULT_GREEN || blue != DEFAULT_BLUE || priority.compareTo(DEFAULT_PRIORITY) != 0)
			{
				this.writeConcurrent(MESSAGES.ROOMSERVER.WHITEBOARD_SERVER.UPDATE_PIXEL + MESSAGES.DELIMITER + x + MESSAGES.DELIMITER + y + MESSAGES.DELIMITER + red + MESSAGES.DELIMITER + green + MESSAGES.DELIMITER + blue + MESSAGES.DELIMITER + priority);
			}
		}
		
		final protected void sendPixelDataAt(int pixelX, int pixelY)
		{
			Pixel pixelToSend = RoomWhiteboardServer.this.getPixel(pixelX, pixelY);
			int x = pixelToSend.getXCoordinate();
			int y = pixelToSend.getYCoordinate();
			int red = pixelToSend.getRed();
			int green = pixelToSend.getGreen();
			int blue = pixelToSend.getBlue();
			BigInteger priority = pixelToSend.getPriority();
			this.writeConcurrent(MESSAGES.ROOMSERVER.WHITEBOARD_SERVER.UPDATE_PIXEL + MESSAGES.DELIMITER + x + MESSAGES.DELIMITER + y + MESSAGES.DELIMITER + red + MESSAGES.DELIMITER + green + MESSAGES.DELIMITER + blue + MESSAGES.DELIMITER + priority);
		}
		
		@Override
		final protected String read() throws IOException
		{
			String message = super.read();
			System.out.println("Room Whiteboard Server received from client: " + message);
			return message;
		}
		
		@Override
		final protected void write(String message)
		{
			System.out.println("Room Whiteboard Server wrote to client: " + message);
			super.write(message);
		}
		
	}
	
	final private class Pixel
	{
		final private int m_xCoordinate;
		final private int m_yCoordinate;
		final private int m_red;
		final private int m_green;
		final private int m_blue;
		final private BigInteger m_priority;
		
		/**
		 * Creates a pixel at (x, y) using the default colors and priority
		 * 
		 * @param xCoordinate		an integer, x coordinate of the pixel
		 * @param yCoordinate		an integer, y coordinate of the pixel
		 * 
		 * @see						#DEFAULT_RED
		 * @see						#DEFAULT_GREEN
		 * @see						#DEFAULT_BLUE
		 * @see						RoomWhiteboardServer#DEFAULT_PRIORITY
		 */
		public Pixel(int xCoordinate, int yCoordinate)
		{
			this.m_xCoordinate = xCoordinate;
			this.m_yCoordinate = yCoordinate;
			this.m_red = DEFAULT_RED;
			this.m_green = DEFAULT_GREEN;
			this.m_blue = DEFAULT_BLUE;
			this.m_priority = DEFAULT_PRIORITY;
		}
		
		public Pixel(int xCoordinate, int yCoordinate, int red, int green, int blue, BigInteger priority)
		{
			this.m_xCoordinate = xCoordinate;
			this.m_yCoordinate = yCoordinate;
			this.m_red = red;
			this.m_green = green;
			this.m_blue = blue;
			this.m_priority = priority;
		}
		
		final public int getXCoordinate()
		{
			return this.m_xCoordinate;
		}
		
		final public int getYCoordinate()
		{
			return this.m_yCoordinate;
		}
		
		final public int getRed()
		{
			return this.m_red;
		}
		
		final public int getGreen()
		{
			return this.m_green;
		}
		
		final public int getBlue()
		{
			return this.m_blue;
		}
		
		final public BigInteger getPriority()
		{
			return this.m_priority;
		}
	}
}
