package gui.roomdata;

import gui.login.Login;
import gui.room.Room;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.InvalidMessageException;
import net.messagingserver.MessagingServerStepConnection;
import net.roomdataserver.RoomDataServerStepConnection;
import net.roomserver.RoomServerConcurrentConnection;
import net.roomserver.RoomServerStepConnection;

import util.CommonMethods;
import util.Text;

public class RoomInfoInterface extends JFrame
{
	private static final long serialVersionUID = 1L;
	final private static int UNINITIALIZED_MAXIMUM_ROOMS = 0;
	//TODO get this value from the server instead
	final private static int MAX_USERS_PER_ROOM = 25;
	private int m_maximumRooms = UNINITIALIZED_MAXIMUM_ROOMS;
	
	//graphical components and their properties
	final private static int LENGTH = 700;
	@SuppressWarnings("hiding")
	final private static int WIDTH = 700;
	final private BorderLayout LAYOUT = new BorderLayout();
	final private JPanel pnlCommands = new JPanel();
	final private JScrollPane scrollCommands = new JScrollPane(this.pnlCommands);
	final private FlowLayout PNL_COMMANDS_LAYOUT = new FlowLayout(FlowLayout.CENTER);
		final private JButton cmdCreateRoom = new JButton();
		final private JButton cmdModifyRoom = new JButton();
		final private JButton cmdDeleteRoom = new JButton();
		final private JButton cmdJoinRoom = new JButton();
		final private JButton cmdLeaveCurrentRoom = new JButton();
	final private JPanel pnlMain = new JPanel();
	final private GridLayout PNL_MAIN_LAYOUT = new GridLayout(1, 1);
	final private JScrollPane scrollMain = new JScrollPane(this.pnlMain);
	
	//data displaying graphical components
	final private RoomList pnlRoomList;
	final private RoomPropertiesInterface pnlRoomProperties = new RoomPropertiesInterface(this);
	
	//networking devices
	final private RoomDataServerStepConnection m_roomDataServerStepConnection;
	final private MessagingServerStepConnection m_messagingServerStepConnection;
	
	private Room currentRoom;
	private RoomServerStepConnection currentRoomStepConnection;
	private RoomServerConcurrentConnection currentRoomConcurrentConnection;
	
	/**
	 * Constructor
	 * @throws InvalidMessageException 
	 * @throws IOException 
	 */
	public RoomInfoInterface(RoomDataServerStepConnection roomDataServerStepConnection, MessagingServerStepConnection messagingServerStepConnection)
	{
		//save crucial parts
		this.m_roomDataServerStepConnection = roomDataServerStepConnection;
		this.m_messagingServerStepConnection = messagingServerStepConnection;
		//create the room list
		this.pnlRoomList = new RoomList(this, roomDataServerStepConnection);
		//add components to the commands panel
		this.pnlCommands.setLayout(this.PNL_COMMANDS_LAYOUT);
		this.cmdCreateRoom.setText(Text.GUI.ROOM_DATA.CREATE_ROOM_STRING);
		this.pnlCommands.add(this.cmdCreateRoom);
		this.cmdModifyRoom.setText(Text.GUI.ROOM_DATA.MODIFY_ROOM_STRING);
		this.pnlCommands.add(this.cmdModifyRoom);
		this.cmdDeleteRoom.setText(Text.GUI.ROOM_DATA.DELETE_ROOM_STRING);
		this.pnlCommands.add(this.cmdDeleteRoom);
		this.cmdJoinRoom.setText(Text.GUI.ROOM_DATA.JOIN_ROOM_STRING);
		this.pnlCommands.add(this.cmdJoinRoom);
		this.cmdLeaveCurrentRoom.setText(Text.GUI.ROOM_DATA.LEAVE_ROOM_STRING);
		this.pnlCommands.add(this.cmdLeaveCurrentRoom);
		
		this.pnlMain.setLayout(this.PNL_MAIN_LAYOUT);
		//add the commands panel
		setLayout(this.LAYOUT);
		add(this.scrollCommands, BorderLayout.NORTH);
		add(this.scrollMain, BorderLayout.CENTER);
		//define some properties of the frame as a whole
		setSize(LENGTH, WIDTH);
		setVisible(false);
	}
	
	final public void addRoomInfoInterfaceListener(RoomInfoInterfaceListener l)
	{
		this.cmdCreateRoom.addActionListener(l);
		this.cmdModifyRoom.addActionListener(l);
		this.cmdDeleteRoom.addActionListener(l);
		this.cmdJoinRoom.addActionListener(l);
		this.cmdLeaveCurrentRoom.addActionListener(l);
		//TODO set up networking device
		this.pnlRoomProperties.addRoomPropertiesInterfaceListener(new RoomPropertiesInterfaceListener(this.pnlRoomProperties, this.m_roomDataServerStepConnection));
	}
	
	final public int getMaximumRooms()
	{
		return this.m_maximumRooms;
	}
	
	/**
	 * Removes everything from the main panel
	 */
	final public void loadPnlMain()
	{
		this.pnlMain.removeAll();
		this.pnlMain.revalidate();
	}
	
	/**
	 * Removes everything from the main panel than adds a new component
	 * 
	 * @param c			a <code>Component</code>, what should be displayed in the main panel
	 */
	final private void loadPnlMain(Component c)
	{
		this.pnlMain.removeAll();
		this.pnlMain.add(c);
		this.pnlMain.revalidate();
		this.pnlMain.repaint();
	}
	
	final public void loadJoinRoom()
	{
		if (this.m_maximumRooms == UNINITIALIZED_MAXIMUM_ROOMS)
		{
			try 
			{
				this.m_maximumRooms = this.m_roomDataServerStepConnection.getMaximumRoomsStored(Login.m_username);
			} catch (IOException e) 
			{
				// ignore
			} catch (InvalidMessageException e) 
			{
				//ignore
			}
		}
		if (this.currentRoom == null)
		{
			loadRoomList();
		} else
		{
			this.loadPnlMain(this.currentRoom);
		}
	}
	
	final public void loadRoomList()
	{
		loadPnlMain(this.pnlRoomList);
		this.pnlRoomList.refreshRoomList();
	}
	
	/**
	 * Loads the room properties interface
	 *	
	 * @param mode			an integer, which mode the user wants, e.g. Create room mode
	 * @see					RoomPropertiesInterface#CREATE_ROOM_MODE
	 * @see					RoomPropertiesInterface#MODIFY_ROOM_MODE
	 * @see					RoomPropertiesInterface#JOIN_ROOM_MODE
	 */
	final public void loadRoomPropertiesInterface(int mode)
	{
		loadPnlMain(this.pnlRoomProperties);
		this.pnlRoomProperties.setMode(mode);
	}
	
	/**
	 * Loads the join room interface with the given room data
	 * 
	 * @param data		a </code>RoomData</code>, the data of the room to be displayed
	 */
	final public void loadJoinRoomInterfaceWithData(RoomData roomData)
	{
		this.loadRoomPropertiesInterface(RoomPropertiesInterface.JOIN_ROOM_MODE);
		this.pnlRoomProperties.loadAndDisplayRoomData(roomData);
	}
	
	final public void joinRoom(RoomData roomData)
	{
		this.currentRoom = new Room(MAX_USERS_PER_ROOM, roomData.getWhiteboardLength(), roomData.getWhiteboardWidth());
		int port = 10000 + roomData.getRoomID();
		String joinPassword = roomData.getJoinPassword();
		if (joinPassword.equals(""))
		{
			joinPassword = Text.GUI.ROOM_DATA.ROOM_PROPERTIES.DEFAULT_JOIN_PASSWORD;
		}
		try 
		{
			this.currentRoomStepConnection = new RoomServerStepConnection(Login.m_serverIP, port, this.currentRoom, joinPassword, this.m_messagingServerStepConnection, this);
			this.currentRoomConcurrentConnection = new RoomServerConcurrentConnection(Login.m_serverIP, port + 1);
			this.currentRoomConcurrentConnection.start();
			if (this.currentRoomStepConnection.attemptLogin())
			{
				this.loadPnlMain(this.currentRoom);
			} else
			{
				this.currentRoom = null;
				this.currentRoomConcurrentConnection = null;
				this.currentRoomStepConnection = null;
			}
		} catch (IOException e) 
		{
			CommonMethods.displayErrorMessage(Text.GUI.ROOM_DATA.FAILED_CONNECT_TO_ROOM_SERVER_ERROR_MESSAGE);
			this.currentRoom = null;
			if (this.currentRoomStepConnection != null)
			{
				this.currentRoomStepConnection.closeAllConnections();
				this.currentRoomStepConnection = null;
			}
			if (this.currentRoomConcurrentConnection != null)
			{
				this.currentRoomConcurrentConnection.close();
				this.currentRoomConcurrentConnection = null;
			}
		}
	}
	
	final public void handleKickedOutOfRoom()
	{
		this.currentRoom = null;
		this.currentRoomStepConnection.closeAllConnections();
		this.currentRoomStepConnection = null;
		this.currentRoomConcurrentConnection.close();
		this.currentRoomConcurrentConnection = null;
		this.loadJoinRoom();
	}
	
	final public void leaveRoom()
	{
		this.currentRoom = null;
		this.currentRoomStepConnection.sendLeaveRequest();
		if (this.currentRoomStepConnection != null)
		{
			this.currentRoomStepConnection.closeAllConnections();
			this.currentRoomStepConnection = null;
		}
		if (this.currentRoomConcurrentConnection != null)
		{
			this.currentRoomConcurrentConnection.close();
			this.currentRoomConcurrentConnection = null;
		}
		this.loadJoinRoom();
	}
	
	/**
	 * Test
	 * @param args
	 */
	/*
	final public static void main(String[] args)
	{
		RoomInfoInterface test = new RoomInfoInterface();
		test.addRoomInfoInterfaceListener(new RoomInfoInterfaceListener(test));
	}//*/
}
