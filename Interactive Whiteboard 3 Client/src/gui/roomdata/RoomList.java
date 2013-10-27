package gui.roomdata;

import gui.login.Login;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.InvalidMessageException;
import net.MESSAGES;
import net.OperationFailedException;
import net.roomdataserver.RoomDataServerStepConnection;

import util.Text;

/**
 * Displays a list of rooms
 * 
 * @author Mickey
 */
final public class RoomList extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//room data
	private RoomElement[] rooms;
	final private RoomIDComparator roomIDComparator = new RoomIDComparator();
	//graphical elements
	/**
	 * layout of the whole room list interface
	 */
	final private GridLayout ROOM_LIST_LAYOUT = new GridLayout(1, 1);
	/**
	 * layout of just the list
	 */
	private GridLayout LIST_LAYOUT;
	/**
	 * the minimum number of slots for rooms to be displayed at a time. for example, if we have two rooms,
	 * the user interface looks ugly if we just have 2 slots for rooms - that means each room takes up half
	 * the screen. we will divide the screen into at least fifteen slots
	 */
	final private static int MINIMUM_ROOM_LIST_SIZE = 15;
	/**
	 * displays a list of rooms the user can join
	 */
	final private JPanel pnlList = new JPanel(this.LIST_LAYOUT);
	
	
	
	/**
	 * reference to the room info interface. required to update the room info interface
	 * when the user clicks to join a room
	 */
	final protected RoomInfoInterface m_roomInfoInterface;
	
	/**
	 * reference to the room data server step connection. required to update this list of rooms
	 */
	final protected RoomDataServerStepConnection m_roomDataServerStepConnection;
	/**
	 * Constructor
	 * @param roomInfoInterface			a reference to the room info interface
	 */
	public RoomList(RoomInfoInterface roomInfoInterface, RoomDataServerStepConnection roomDataServerStepConnection)
	{
		this.m_roomInfoInterface = roomInfoInterface;
		this.m_roomDataServerStepConnection = roomDataServerStepConnection;
		setLayout(this.ROOM_LIST_LAYOUT);
		add(new JScrollPane(this.pnlList));
	}
	
	/**
	 * Requests room data from the server again and then relists everything
	 */
	final public void refreshRoomList()
	{
		//update how many slots for rooms should be on the room list
		int numberOfExistingRooms = this.m_roomDataServerStepConnection.getNumberOfExistingRooms();
		if (numberOfExistingRooms < MINIMUM_ROOM_LIST_SIZE)
		{
			this.LIST_LAYOUT = new GridLayout(MINIMUM_ROOM_LIST_SIZE, 1);
		} else
		{
			this.LIST_LAYOUT = new GridLayout(numberOfExistingRooms, 1);
		}
		this.pnlList.setLayout(this.LIST_LAYOUT);
		
		try 
		{
			this.m_roomDataServerStepConnection.updateExistingRooms();
		} catch (IOException e) 
		{
			//ignore
		} catch (InvalidMessageException e) 
		{
			this.m_roomDataServerStepConnection.handleInvalidMessageException(e);
		}
		this.rooms = new RoomElement[this.m_roomInfoInterface.getMaximumRooms()];
		for (int roomID = 0; roomID < this.m_roomInfoInterface.getMaximumRooms(); roomID++)
		{
			if (this.m_roomDataServerStepConnection.doesRoomExist(roomID) == true)
			{
				try 
				{
					RoomData roomData = this.m_roomDataServerStepConnection.getRoomData(Login.m_username, roomID);
					addRoomElement(roomData);
				} catch (IOException e) 
				{
					RoomDataServerStepConnection.displayConnectionLost();
				} catch (OperationFailedException e) 
				{
					//ignore - this means room does not exist
				} catch (InvalidMessageException e) 
				{
					this.m_roomDataServerStepConnection.handleInvalidMessageException(e);
				}
			}
		}
		relistRoomsByRoomID();
	}
	
	/**
	 * Adds room data to the currently stored list
	 * @param roomData
	 */
	final private void addRoomElement(RoomData roomData)
	{
		RoomElement roomToAdd = new RoomElement(roomData);
		for (int roomIndex = 0; roomIndex < this.rooms.length; roomIndex++)
		{
			if (this.rooms[roomIndex] == null)
			{
				this.rooms[roomIndex] = roomToAdd;
				break;
			}
		}
	}
	
	/**
	 * relists all rooms in room ID order
	 */
	final public void relistRoomsByRoomID()
	{
		relistRooms(this.roomIDComparator);
	}
	
	final private void relistRooms(Comparator<RoomElement> comparator)
	{
		//remove everything from the current list
		this.pnlList.removeAll();
		RoomElement[] copyOfRoomElements = cloneRoomElementArray(this.rooms);
		Arrays.sort(copyOfRoomElements, comparator);
		for (int roomIndex = 0; roomIndex < this.rooms.length; roomIndex++)
		{
			RoomElement roomToAdd = copyOfRoomElements[roomIndex];
			if (roomToAdd != null)
			{
				roomToAdd.addRoomElementListener(new RoomElementListener(roomToAdd));
				this.pnlList.add(roomToAdd);
			}
		}
		this.revalidate();
		this.repaint();
	}	
	
	final private static RoomElement[] cloneRoomElementArray(RoomElement[] roomArray)
	{
		RoomElement[] rtn = new RoomElement[roomArray.length];
		for (int roomIndex = 0; roomIndex < roomArray.length; roomIndex++)
		{
			if (roomArray[roomIndex] != null)
			{
				rtn[roomIndex] = roomArray[roomIndex].cloneRoomElement();
			}
		}
		return rtn;
	}
	
	/**
	 * Elements of the room list
	 * @author Mickey
	 *
	 */
	final private class RoomElement extends JPanel
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		//room data
		final private RoomData m_roomData;
		//graphical components
		final private FlowLayout ROOM_ELEMENT_LAYOUT = new FlowLayout(FlowLayout.CENTER);
		final private JLabel lblRoomInfo;
		final private JButton cmdViewRoomProperties;
		
		public RoomElement(RoomData roomData)
		{
			this.m_roomData = roomData;
			this.lblRoomInfo = new JLabel();
			int roomID = this.m_roomData.getRoomID();
			String roomName = this.m_roomData.getRoomName();
			String creatorDisplayName = MESSAGES.unsubstituteForMessageDelimiters(RoomList.this.m_roomDataServerStepConnection.getDisplayNameOfUser(Login.m_username, Login.m_password, this.m_roomData.getCreatorUsername()));
			this.lblRoomInfo.setText(Text.GUI.ROOM_DATA.ROOM_LIST.ROOM_ID_HEADING + roomID + Text.GUI.ROOM_DATA.ROOM_LIST.ROOM_INFO_SEPARATOR + Text.GUI.ROOM_DATA.ROOM_LIST.ROOM_NAME_HEADING + roomName 
									+ Text.GUI.ROOM_DATA.ROOM_LIST.ROOM_INFO_SEPARATOR + Text.GUI.ROOM_DATA.ROOM_LIST.ROOM_CREATOR_HEADING + creatorDisplayName);
			this.cmdViewRoomProperties = new JButton();
			this.cmdViewRoomProperties.setText(Text.GUI.ROOM_DATA.ROOM_LIST.VIEW_ROOM_PROPERTIES_COMMAND);
			setLayout(this.ROOM_ELEMENT_LAYOUT);
			add(this.lblRoomInfo);
			add(this.cmdViewRoomProperties);
		}
		
		final public void addRoomElementListener(RoomElementListener l)
		{
			this.cmdViewRoomProperties.addActionListener(l);
		}
		
		final public int getRoomID()
		{
			return this.m_roomData.getRoomID();
		}
		
		final public void loadJoinRoomInterface()
		{
			System.out.println("Loading join interface with data");
			RoomList.this.m_roomInfoInterface.loadJoinRoomInterfaceWithData(this.m_roomData);
		}
		
		final public RoomElement cloneRoomElement()
		{
			return new RoomElement(this.m_roomData.cloneRoomData());
		}
	}
	
	final private class RoomElementListener implements ActionListener
	{
		
		final private RoomElement m_gui;
		
		public RoomElementListener(RoomElement gui)
		{
			this.m_gui = gui;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			String command = e.getActionCommand();
			if (command.equals(Text.GUI.ROOM_DATA.ROOM_LIST.VIEW_ROOM_PROPERTIES_COMMAND))
			{
				this.m_gui.loadJoinRoomInterface();
			}
		}	
	}
	
	final private class RoomIDComparator implements Comparator<RoomElement>
	{

		public RoomIDComparator() 
		{
			// nothing needs to be done
		}

		@Override
		public int compare(RoomElement room1, RoomElement room2)
		{
			if (room1 == null && room2 == null)
			{
				return 0;
			} else if (room1 == null && room2 != null)
			{
				return 1;
			} else if (room1 != null && room2 == null)
			{
				return -1;
			} else if (room1 != null && room2 != null)
			{
				return(room1.getRoomID() - room2.getRoomID());
			}
			return 0;
		}
	}
}
