package gui.roomdata;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import util.Text;

final public class RoomInfoInterfaceListener implements ActionListener
{
	final private RoomInfoInterface m_gui;
	
	public RoomInfoInterfaceListener(RoomInfoInterface gui)
	{
		this.m_gui = gui;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		String command = e.getActionCommand();
		if (command.equals(Text.GUI.ROOM_DATA.CREATE_ROOM_STRING))
		{
			this.m_gui.loadRoomPropertiesInterface(RoomPropertiesInterface.CREATE_ROOM_MODE);
		} else if (command.equals(Text.GUI.ROOM_DATA.MODIFY_ROOM_STRING))
		{
			this.m_gui.loadRoomPropertiesInterface(RoomPropertiesInterface.MODIFY_ROOM_MODE);
		} else if (command.equals(Text.GUI.ROOM_DATA.DELETE_ROOM_STRING))
		{
			this.m_gui.loadRoomPropertiesInterface(RoomPropertiesInterface.DELETE_ROOM_MODE);
		} else if (command.equals(Text.GUI.ROOM_DATA.JOIN_ROOM_STRING))
		{
			this.m_gui.loadJoinRoom();
		} else if (command.equals(Text.GUI.ROOM_DATA.LEAVE_ROOM_STRING))
		{
			this.m_gui.leaveRoom();
		}
	}

}
