package gui.roomdata;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import util.Text;

import net.InvalidMessageException;
import net.OperationErrorCode;
import net.OperationFailedException;
import net.roomdataserver.RoomDataServerStepConnection;

public class RoomPropertiesInterfaceListener implements ActionListener
{

	final private RoomPropertiesInterface m_gui;
	final private RoomDataServerStepConnection m_roomDataServerStepConnection;
	
	public RoomPropertiesInterfaceListener(RoomPropertiesInterface gui, RoomDataServerStepConnection roomDataServerStepConnection)
	{
		System.out.println(roomDataServerStepConnection == null);
		this.m_gui = gui;
		this.m_roomDataServerStepConnection = roomDataServerStepConnection;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		String command = e.getActionCommand();
		//determine which mode the room properties interface is in and then 
		//figure out what the user wants to do
		int mode = this.m_gui.getMode();
		switch(mode)
		{
			case RoomPropertiesInterface.CREATE_ROOM_MODE:
				if (command.equals(Text.GUI.ROOM_DATA.ROOM_PROPERTIES.CREATE_ROOM_COMMAND))
				{
					try 
					{
						this.m_roomDataServerStepConnection.createRoom(this.m_gui.getRoomData());
					} catch (IOException connectionEndedError) 
					{
						RoomDataServerStepConnection.displayConnectionLost();
					} catch (OperationFailedException operationFailedError) 
					{
						OperationErrorCode errorCode = operationFailedError.getErrorCode();
						switch(errorCode)
						{
							case ROOM_ID_TAKEN:
								break;
							default:
								//TODO
						}
					} catch (InvalidMessageException invalidMessageError) 
					{
						this.m_roomDataServerStepConnection.handleInvalidMessageException(invalidMessageError);
					}
				} else if (command.equals(Text.GUI.ROOM_DATA.ROOM_PROPERTIES.RESET_COMMAND))
				{
						this.m_gui.clearRoomCreationFields();
				}
				break;
			case RoomPropertiesInterface.MODIFY_ROOM_MODE:
				if (command.equals(Text.GUI.ROOM_DATA.ROOM_PROPERTIES.MODIFY_ROOM_COMMAND))
				{
					//modify the room
				} else if (command.equals(Text.GUI.ROOM_DATA.ROOM_PROPERTIES.RESET_COMMAND))
				{
					this.m_gui.clearRoomModificationFields();
				}
				break;
			case RoomPropertiesInterface.DELETE_ROOM_MODE:
				
				break;
			case RoomPropertiesInterface.JOIN_ROOM_MODE:
				if (command.equals(Text.GUI.ROOM_DATA.ROOM_PROPERTIES.JOIN_ROOM_COMMAND))
				{
					this.m_gui.joinRoom();
				} else if (command.equals(Text.GUI.ROOM_DATA.ROOM_PROPERTIES.CANCEL_COMMAND))
				{
					this.m_gui.loadRoomList();
				}
				break;
			default:
				//TODO
		}
		//commands regardless of mode:
		if (command.equals(Text.GUI.ROOM_DATA.ROOM_PROPERTIES.ENABLE_PASSWORD_PROTECTION_STRING))
		{
			this.m_gui.turnOnPasswordProtection();
		} else if (command.equals(Text.GUI.ROOM_DATA.ROOM_PROPERTIES.DISABLE_PASSWORD_PROTECTION_STRING))
		{
			this.m_gui.turnOffPasswordProtection();
		}
	}

}
