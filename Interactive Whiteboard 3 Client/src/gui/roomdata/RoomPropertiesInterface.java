package gui.roomdata;

import gui.login.Login;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import util.CommonMethods;
import util.Text;

public class RoomPropertiesInterface extends JPanel
{
	private static final long serialVersionUID = 1L;

	//current mode:
	/**
	 * determines the mode this interface is in.
	 * 
	 * @see 		#CREATE_ROOM_MODE
	 * @see			#MODIFY_ROOM_MODE
	 * @see			#DELETE_ROOM_MODE
	 * @see			#JOIN_ROOM_MODE
	 */
	private int m_mode;
	/**
	 * identifies the interface as displaying room creation information
	 */
	final public static int CREATE_ROOM_MODE = 1;
	/**
	 * identifies the interface as displaying room modification information
	 */
	final public static int MODIFY_ROOM_MODE = 2;
	/**
	 * identifies the interface as displaying room deletion information
	 */
	final public static int DELETE_ROOM_MODE = 3;
	/**
	 * identifies the interface as displaying room joining information
	 */
	final public static int JOIN_ROOM_MODE = 4;
	
	//graphical components and their properties:
	final private GridLayout LAYOUT = new GridLayout(12, 2);
	final private JLabel lblRoomID = new JLabel();
	final private JTextField txtRoomID = new JTextField();
	final private JLabel lblRoomName = new JLabel();
	final private JTextField txtRoomName = new JTextField();
	final private JLabel lblCreatorName = new JLabel();
	final private JTextField txtCreatorName = new JTextField();
	final private JLabel lblCreationDate = new JLabel();
	final private JTextField txtCreationDate = new JTextField();
	final private JLabel lblModificationPassword = new JLabel();
	final private JPasswordField txtModificationPassword = new JPasswordField();
	final private JLabel lblConfirmModificationPassword = new JLabel();
	final private JPasswordField txtConfirmModificationPassword = new JPasswordField();
	final private JLabel lblPasswordProtection = new JLabel();
	final private JButton cmdTogglePasswordProtection = new JButton();
	final private JLabel lblJoinPassword = new JLabel();
	final private JLabel lblConfirmJoinPassword = new JLabel();
	final private JPasswordField txtJoinPassword = new JPasswordField();
	final private JPasswordField txtConfirmJoinPassword = new JPasswordField();
	final private JLabel lblWhiteboardLength = new JLabel();
	final private JTextField txtWhiteboardLength = new JTextField();
	final private JLabel lblWhiteboardWidth = new JLabel();
	final private JTextField txtWhiteboardWidth = new JTextField();
	final private JButton cmdPositiveCommand = new JButton();	//displays a positive command (e.g. create)
	final private JButton cmdNegativeCommand = new JButton();	//displays a negative command (e.g. delete)
	/**
	 * Used for cancel commands
	 */
	final private RoomInfoInterface m_roomInfoInterface;
	//data:
	/**
	 * data describing properties of the current room being displayed
	 */
	private RoomData m_roomData;
	/**
	 * constructor.
	 */
	public RoomPropertiesInterface(RoomInfoInterface roomInfoInterface)
	{
		this.m_roomInfoInterface = roomInfoInterface;
		//define graphical component's properties and add them
		setLayout(this.LAYOUT);
		
		this.lblRoomID.setText(Text.GUI.ROOM_DATA.ROOM_PROPERTIES.ROOM_ID_STRING);
		add(this.lblRoomID);
		add(this.txtRoomID);
		
		this.lblRoomName.setText(Text.GUI.ROOM_DATA.ROOM_PROPERTIES.ROOM_NAME_STRING);
		add(this.lblRoomName);
		add(this.txtRoomName);
		
		this.lblCreatorName.setText(Text.GUI.ROOM_DATA.ROOM_PROPERTIES.CREATOR_NAME_STRING);
		add(this.lblCreatorName);
		this.txtCreatorName.setEditable(false);
		add(this.txtCreatorName);
		
		this.lblCreationDate.setText(Text.GUI.ROOM_DATA.ROOM_PROPERTIES.CREATION_DATE_STRING);
		add(this.lblCreationDate);
		this.txtCreationDate.setEditable(false);
		add(this.txtCreationDate);
		
		this.lblModificationPassword.setText(Text.GUI.ROOM_DATA.ROOM_PROPERTIES.MODIFICATION_PASSWORD_STRING);
		add(this.lblModificationPassword);
		add(this.txtModificationPassword);
		
		this.lblConfirmModificationPassword.setText(Text.GUI.ROOM_DATA.ROOM_PROPERTIES.CONFIRM_MODIFICATION_PASSWORD_STRING);
		add(this.lblConfirmModificationPassword);
		add(this.txtConfirmModificationPassword);
		
		this.lblPasswordProtection.setText(Text.GUI.ROOM_DATA.ROOM_PROPERTIES.PASSWORD_PROTECTION_OFF_STRING);
		add(this.lblPasswordProtection);
		add(this.cmdTogglePasswordProtection);
		
		this.lblJoinPassword.setText(Text.GUI.ROOM_DATA.ROOM_PROPERTIES.JOIN_PASSWORD_STRING);
		add(this.lblJoinPassword);
		add(this.txtJoinPassword);
		
		this.lblConfirmJoinPassword.setText(Text.GUI.ROOM_DATA.ROOM_PROPERTIES.CONFIRM_JOIN_PASSWORD_STRING);
		add(this.lblConfirmJoinPassword);
		add(this.txtConfirmJoinPassword);
		
		this.lblWhiteboardLength.setText(Text.GUI.ROOM_DATA.ROOM_PROPERTIES.WHITEBOARD_LENGTH_STRING);
		//TODO allow varying whiteboard dimensions
		add(this.lblWhiteboardLength);
		this.txtWhiteboardLength.setEditable(false);
		add(this.txtWhiteboardLength);
		
		this.lblWhiteboardWidth.setText(Text.GUI.ROOM_DATA.ROOM_PROPERTIES.WHITEBOARD_WIDTH_STRING);
		add(this.lblWhiteboardWidth);
		this.txtWhiteboardWidth.setEditable(false);
		add(this.txtWhiteboardWidth);

		add(this.cmdPositiveCommand);
		add(this.cmdNegativeCommand);
	}
	
	final public void addRoomPropertiesInterfaceListener(RoomPropertiesInterfaceListener l)
	{
		this.cmdPositiveCommand.addActionListener(l);
		this.cmdNegativeCommand.addActionListener(l);
		this.cmdTogglePasswordProtection.addActionListener(l);
	}
	
	final public void turnOnPasswordProtection()
	{
		this.lblPasswordProtection.setText(Text.GUI.ROOM_DATA.ROOM_PROPERTIES.PASSWORD_PROTECTION_ON_STRING);
		this.cmdTogglePasswordProtection.setText(Text.GUI.ROOM_DATA.ROOM_PROPERTIES.DISABLE_PASSWORD_PROTECTION_STRING);
	}
	
	final public void turnOffPasswordProtection()
	{
		this.lblPasswordProtection.setText(Text.GUI.ROOM_DATA.ROOM_PROPERTIES.PASSWORD_PROTECTION_OFF_STRING);
		this.cmdTogglePasswordProtection.setText(Text.GUI.ROOM_DATA.ROOM_PROPERTIES.ENABLE_PASSWORD_PROTECTION_STRING);
	}
	
	/**
	 * @return		the mode this room is currently in
	 */
	final public int getMode()
	{
		return this.m_mode;
	}
	
	/**
	 * changes the mode this room is in.
	 * 
	 * @param mode		an integer, the new mode for the room
	 */
	final public void setMode(int mode)
	{
		switch(mode)
		{
			case CREATE_ROOM_MODE:
				loadCreateRoomDisplay();
				break;
			case MODIFY_ROOM_MODE:
				loadModifyRoomDisplay();
				break;
			case DELETE_ROOM_MODE:
				loadDeleteRoomDisplay();
				break;
			case JOIN_ROOM_MODE:
				loadJoinRoomDisplay();
				break;
			default:
				//TODO
		}
	}
	
	/**
	 * displays room creation options
	 */
	final private void loadCreateRoomDisplay()
	{
		this.cmdPositiveCommand.setText(Text.GUI.ROOM_DATA.ROOM_PROPERTIES.CREATE_ROOM_COMMAND);
		this.cmdNegativeCommand.setText(Text.GUI.ROOM_DATA.ROOM_PROPERTIES.RESET_COMMAND);
		this.cmdNegativeCommand.setEnabled(true);
		this.m_mode = CREATE_ROOM_MODE;
		
		//set up graphical properties
		this.txtRoomID.setText("");
		this.txtRoomID.setEditable(true);
		this.txtCreatorName.setText(Login.m_username);
		this.txtRoomName.setText("");
		this.txtRoomName.setEditable(true);
		this.txtModificationPassword.setText("");
		this.txtModificationPassword.setEditable(true);
		this.txtConfirmModificationPassword.setText("");
		this.txtConfirmModificationPassword.setEditable(false);
		this.lblPasswordProtection.setText(Text.GUI.ROOM_DATA.ROOM_PROPERTIES.PASSWORD_PROTECTION_OFF_STRING);
		this.cmdTogglePasswordProtection.setText(Text.GUI.ROOM_DATA.ROOM_PROPERTIES.ENABLE_PASSWORD_PROTECTION_STRING);
		this.cmdTogglePasswordProtection.setEnabled(true);
		this.txtJoinPassword.setText("");
		this.txtJoinPassword.setEditable(true);
		this.txtConfirmJoinPassword.setText("");
		this.txtConfirmJoinPassword.setEditable(true);
		//TODO in the future, enable whiteboard resizing
		this.txtWhiteboardLength.setText(Text.GUI.ROOM_DATA.ROOM_PROPERTIES.DEFAULT_WHITEBOARD_LENGTH_STRING);
		//this.txtWhiteboardLength.setEditable(true);
		this.txtWhiteboardWidth.setText(Text.GUI.ROOM_DATA.ROOM_PROPERTIES.DEFAULT_WHITEBOARD_WIDTH_STRING);
		//this.txtWhiteboardWidth.setEditable(true);
	}
		
		final public void clearRoomCreationFields()
		{
			//ask for a confirmation
			int response = CommonMethods.displayConfirmDialog(Text.GUI.ROOM_DATA.ROOM_PROPERTIES.CONFIRM_CLEAR_MESSAGE);
			if (response == CommonMethods.CONFIRM_YES_RESPONSE)
			{
				loadCreateRoomDisplay();
				/*//clear the fields
				this.txtRoomID.setText("");
				this.txtRoomName.setText("");
				this.txtModificationPassword.setText("");
				this.cmdTogglePasswordProtection.setText(Text.GUI.ROOM_DATA.ROOM_PROPERTIES.ENABLE_PASSWORD_PROTECTION_STRING);
				this.txtJoinPassword.setText("");
				this.txtWhiteboardLength.setText("");
				this.txtWhiteboardWidth.setText("");*/
			}
		}	
	
	/**
	 * displays room modification options
	 */
	final private void loadModifyRoomDisplay()
	{
		this.cmdPositiveCommand.setText(Text.GUI.ROOM_DATA.ROOM_PROPERTIES.MODIFY_ROOM_COMMAND);
		this.cmdNegativeCommand.setText(Text.GUI.ROOM_DATA.ROOM_PROPERTIES.RESET_COMMAND);
		this.cmdNegativeCommand.setEnabled(true);
		this.m_mode = MODIFY_ROOM_MODE;
		
		//set up graphical properties
		this.txtRoomID.setEditable(false);
		this.txtRoomName.setEditable(true);
		this.txtModificationPassword.setEditable(true);
		this.txtConfirmModificationPassword.setEditable(true);
		this.cmdTogglePasswordProtection.setEnabled(true);
		this.txtJoinPassword.setEditable(true);
		this.txtConfirmJoinPassword.setEditable(true);
		//TODO allow varying whiteboard dimensions
		//this.txtWhiteboardLength.setEditable(true);
		//this.txtWhiteboardWidth.setEditable(true);
	}
	
		final public void clearRoomModificationFields()
		{
			this.txtRoomName.setText(this.m_roomData.getRoomName());
			this.txtModificationPassword.setText("");
			this.txtConfirmModificationPassword.setText("");
			if (this.m_roomData.getPasswordProtected() == true)
			{
				turnOnPasswordProtection();
			} else
			{
				turnOffPasswordProtection();
			}
			this.txtJoinPassword.setText("");
			this.txtConfirmJoinPassword.setText("");
		}
		
	final public void loadDeleteRoomDisplay()
	{
		
	}
	
	/**
	 * displays room joining options
	 */
	final private void loadJoinRoomDisplay()
	{
		this.cmdPositiveCommand.setText(Text.GUI.ROOM_DATA.ROOM_PROPERTIES.JOIN_ROOM_COMMAND);
		this.cmdNegativeCommand.setText(Text.GUI.ROOM_DATA.ROOM_PROPERTIES.CANCEL_COMMAND);
		this.m_mode = JOIN_ROOM_MODE;
		
		//set up graphical properties
		this.txtRoomID.setEditable(false);
		this.txtRoomName.setEditable(false);
		this.txtModificationPassword.setEditable(false);
		this.txtConfirmModificationPassword.setEditable(false);
		this.cmdTogglePasswordProtection.setEnabled(false);
		this.txtJoinPassword.setEditable(true);
		this.txtConfirmJoinPassword.setEditable(false);
		this.txtWhiteboardLength.setEditable(false);
		this.txtWhiteboardWidth.setEditable(false);
	}
	
		final public void loadRoomList()
		{
			this.m_roomInfoInterface.loadRoomList();
		}
	
	final public void disableAllFields()
	{
		this.txtRoomID.setEditable(false);
		this.txtRoomName.setEditable(false);
		this.txtModificationPassword.setEditable(false);
		this.cmdTogglePasswordProtection.setEnabled(false);
		this.txtJoinPassword.setEditable(false);
		this.txtWhiteboardLength.setEditable(false);
		this.txtWhiteboardWidth.setEditable(false);
	}
	
	/**
	 * Allows the user to edit all fields except creator name and creation date
	 */
	final public void enableAllFields()
	{
		this.txtRoomID.setEditable(true);
		this.txtRoomName.setEditable(true);
		this.txtModificationPassword.setEditable(true);
		this.cmdTogglePasswordProtection.setEnabled(true);
		this.txtJoinPassword.setEditable(true);
		this.txtWhiteboardLength.setEditable(true);
		this.txtWhiteboardWidth.setEditable(true);
	}
	
	final public void loadAndDisplayRoomData(RoomData dataToLoad)
	{
		this.m_roomData = dataToLoad;
		this.txtRoomID.setText(String.valueOf(this.m_roomData.getRoomID()));
		this.txtRoomName.setText(this.m_roomData.getRoomName());
		this.txtCreatorName.setText(this.m_roomData.getCreatorUsername());
		this.txtCreationDate.setText(this.m_roomData.getCreationDate());
		this.txtModificationPassword.setText(this.m_roomData.getModificationPassword());
		if (this.m_roomData.getPasswordProtected() == true)
		{
			this.lblPasswordProtection.setText(Text.GUI.ROOM_DATA.ROOM_PROPERTIES.PASSWORD_PROTECTION_ON_STRING);
			this.cmdTogglePasswordProtection.setText(Text.GUI.ROOM_DATA.ROOM_PROPERTIES.DISABLE_PASSWORD_PROTECTION_STRING);
		} else
		{
			this.lblPasswordProtection.setText(Text.GUI.ROOM_DATA.ROOM_PROPERTIES.PASSWORD_PROTECTION_OFF_STRING);
			this.cmdTogglePasswordProtection.setText(Text.GUI.ROOM_DATA.ROOM_PROPERTIES.ENABLE_PASSWORD_PROTECTION_STRING);
		}
		this.txtJoinPassword.setText(this.m_roomData.getJoinPassword());
		this.txtWhiteboardLength.setText(String.valueOf(this.m_roomData.getWhiteboardLength()));
		this.txtWhiteboardWidth.setText(String.valueOf(this.m_roomData.getWhiteboardWidth()));
	}
	
	final public void joinRoom()
	{
		this.m_roomInfoInterface.joinRoom(getRoomData());
	}
	
	/**
	 * Gets the current room data entered by the user in the text boxes
	 * @return
	 */
	final public RoomData getRoomData()
	{
		int roomID = Integer.parseInt(this.txtRoomID.getText());
		String creatorName = this.txtCreatorName.getText();
		String creationDate = this.txtCreationDate.getText();
		String roomName = this.txtRoomName.getText();
		String modificationPassword = String.valueOf(this.txtModificationPassword.getPassword());
		//if the password protection toggle reads disable password protection, then it is currently enabled (true)
		//otherwise it is disabled (false)
		boolean passwordProtected = this.cmdTogglePasswordProtection.getText().equals(Text.GUI.ROOM_DATA.ROOM_PROPERTIES.DISABLE_PASSWORD_PROTECTION_STRING);
		String joinPassword = String.valueOf(this.txtJoinPassword.getPassword());
		int whiteboardLength = Integer.parseInt(this.txtWhiteboardLength.getText());
		int whiteboardWidth = Integer.parseInt(this.txtWhiteboardWidth.getText());
		RoomData currentRoomData = new RoomData(roomID, roomName, creatorName, creationDate, passwordProtected, whiteboardLength, whiteboardWidth);
		currentRoomData.setModificationPassword(modificationPassword);
		currentRoomData.setJoinPassword(joinPassword);
		return currentRoomData;
	}
}
