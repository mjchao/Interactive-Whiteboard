package net.roomserver;

import gui.login.Login;
import gui.room.Room;
import gui.roomdata.RoomInfoInterface;

import java.io.IOException;
import java.util.Scanner;

import util.CommonMethods;
import util.Text;

import net.InvalidMessageException;
import net.MESSAGES;
import net.OperationFailedException;
import net.StepConnection;
import net.messagingserver.MessagingServerStepConnection;
import net.roomserver.audio.AudioConcurrentConnection;
import net.roomserver.audio.AudioStepConnection;
import net.roomserver.text.TextConcurrentConnection;
import net.roomserver.text.TextStepConnection;
import net.roomserver.userlist.UserListConcurrentConnection;
import net.roomserver.userlist.UserListStepConnection;
import net.roomserver.whiteboard.WhiteboardConcurrentConnection;
import net.roomserver.whiteboard.WhiteboardStepConnection;

final public class RoomServerStepConnection extends StepConnection
{

	final private String m_joinPassword;
	//graphical elements
	final private Room m_room;
	//audio connection
	private AudioConcurrentConnection m_audioConcurrentConnection;
	private AudioStepConnection m_audioStepConnection;
	//whiteboard connection
	private WhiteboardConcurrentConnection m_whiteboardConcurrentConnection;
	private WhiteboardStepConnection m_whiteboardStepConnection;
	//text connection
	private TextConcurrentConnection m_textConcurrentConnection;
	private TextStepConnection m_textStepConnection;
	//user list connection
	private UserListConcurrentConnection m_userListConcurrentConnection;
	private UserListStepConnection m_userListStepConnection;
	private RoomInfoInterface m_gui;
	/**
	 * used to convert usernames to display names
	 */
	final private MessagingServerStepConnection m_messagingServerStepConnection;
	
	public RoomServerStepConnection(String ip, int port, Room room, String joinPassword, MessagingServerStepConnection messagingServerStepConnection, RoomInfoInterface gui) throws IOException 
	{
		super(ip, port);
		this.m_joinPassword = joinPassword;
		this.m_room = room;
		this.m_messagingServerStepConnection = messagingServerStepConnection;
		this.m_gui = gui;
	}
	
	@Override
	final protected synchronized String sendMessageAndGetResponse(String messageToRoomDataServer) throws IOException
	{
		System.out.println("Room Server: Sent message to server: " + messageToRoomDataServer);
		String response = super.sendMessageAndGetResponse(messageToRoomDataServer);
		System.out.println("Room Server: Received message from server: " + response);
		return response;
	}

	final public boolean attemptLogin()
	{
		try
		{
			if (super.attemptRoomLogin(Login.m_username, Login.m_password, this.m_joinPassword))
			{
				//continue
				setUpUserListConnection();
				setUpAudioConnection();
				setUpWhiteboardConnection();
				setUpTextConnection();
				
				this.m_room.addListeners(this.m_audioStepConnection, this.m_textStepConnection, this.m_whiteboardStepConnection, this.m_userListStepConnection);
				this.m_audioConcurrentConnection.setPlaybackDevice(this.m_room.getAudioPlayback());
				this.m_whiteboardConcurrentConnection.setWhiteboardCanvas(this.m_room.getWhiteboardCanvas());
				this.m_textConcurrentConnection.setTextChatGUI(this.m_room.getTextChatInterface());
				this.m_userListConcurrentConnection.setUserListDisplay(this.m_room.getUserListDisplay());
				this.m_userListConcurrentConnection.setUserListStepConnection(this.m_userListStepConnection);
				
				this.m_audioConcurrentConnection.start();
				this.m_whiteboardConcurrentConnection.start();
				this.m_textConcurrentConnection.start();
				this.m_userListConcurrentConnection.start();
				
				this.m_audioStepConnection.attemptLogin();
				this.m_textStepConnection.attemptLogin();
				this.m_whiteboardStepConnection.attemptLogin();
				this.m_userListStepConnection.attemptLogin();
				return true;
			}
			CommonMethods.displayErrorMessage(Text.NET.GENERAL.LOGIN_FAILED_ERROR_MESSAGE);
			return false;
		} catch (IOException connectionError)
		{
			CommonMethods.displayErrorMessage(Text.NET.ROOMSERVER.LOGIN_FAILED_ERROR_MESSAGE);
			return false;
		} catch (InvalidMessageException invalidMessage)
		{
			handleInvalidMessageException(invalidMessage);
			return false;
		}
	}
	
	/**
	 * closes all connections managed by this connection device
	 */
	final public void closeAllConnections()
	{
		this.m_audioConcurrentConnection.close();
		this.m_audioStepConnection.close();
		this.m_whiteboardConcurrentConnection.close();
		this.m_whiteboardStepConnection.close();
		this.m_textConcurrentConnection.close();
		this.m_textStepConnection.close();
		this.m_userListConcurrentConnection.close();
		this.m_userListStepConnection.close();
		this.close();
	}
	
	final private void setUpAudioConnection() throws IOException, InvalidMessageException
	{
		int[] audioPorts = getAudioPortData();
		this.m_audioStepConnection = new AudioStepConnection(Login.m_serverIP, audioPorts[0], this.m_joinPassword);
		this.m_audioConcurrentConnection = new AudioConcurrentConnection(Login.m_serverIP, audioPorts[1]);
	}
	
	final public AudioStepConnection getAudioStepConnection()
	{
		return this.m_audioStepConnection;
	}
	
	final private void setUpWhiteboardConnection() throws IOException, InvalidMessageException
	{
		int[] whiteboardPorts = getWhiteboardPortData();
		this.m_whiteboardStepConnection = new WhiteboardStepConnection(Login.m_serverIP, whiteboardPorts[0], this.m_joinPassword);
		this.m_whiteboardConcurrentConnection = new WhiteboardConcurrentConnection(Login.m_serverIP, whiteboardPorts[1]);
	}
	
	final public WhiteboardStepConnection getWhiteboardStepConnection()
	{
		return this.m_whiteboardStepConnection;
	}
	
	final private void setUpTextConnection() throws IOException, InvalidMessageException
	{
		int[] textPorts = getTextPortData();
		this.m_textStepConnection = new TextStepConnection(Login.m_serverIP, textPorts[0], this.m_joinPassword);
		this.m_textConcurrentConnection = new TextConcurrentConnection(Login.m_serverIP, textPorts[1], this.m_messagingServerStepConnection);
	}
	
	final public TextStepConnection getTextStepConnection()
	{
		return this.m_textStepConnection;
	}
	
	final private void setUpUserListConnection() throws IOException, InvalidMessageException
	{
		int[] userListPorts = getUserListPortData();
		this.m_userListStepConnection = new UserListStepConnection(Login.m_serverIP, userListPorts[0], this.m_joinPassword);
		this.m_userListConcurrentConnection = new UserListConcurrentConnection(Login.m_serverIP, userListPorts[1], this.m_gui);
	}
	
	final public UserListStepConnection getUserListStepConnection()
	{
		return this.m_userListStepConnection;
	}
	
	final private int[] getAudioPortData() throws IOException, InvalidMessageException
	{
		String messageToServer = MESSAGES.ROOMSERVER.GET_AUDIO_PORT_DATA;
		String response = sendMessageAndGetResponse(messageToServer);
		//look through the response
		Scanner scanResponse = new Scanner(response);
		//first part should be result
		String result = scanResponse.next();
		if (result.equals(MESSAGES.ROOMSERVER.GET_AUDIO_PORT_DATA_SUCCESS))
		{
			//expect two integers, the step and connection ports
			int stepConnectionPort = scanResponse.nextInt();
			int concurrentConnectionPort = scanResponse.nextInt();
			scanResponse.close();
			int[] ports = {stepConnectionPort, concurrentConnectionPort};
			return ports;
		}
		scanResponse.close();
		throw generateInvalidMessageException(response);
	}
	
	final private int[] getWhiteboardPortData() throws IOException, InvalidMessageException
	{
		String messageToServer = MESSAGES.ROOMSERVER.GET_WHITEBOARD_PORT_DATA;
		String response = sendMessageAndGetResponse(messageToServer);
		//look through the response
		Scanner scanResponse = new Scanner(response);
		//first part should be result
		String result = scanResponse.next();
		if (result.equals(MESSAGES.ROOMSERVER.GET_WHITEBOARD_PORT_DATA_SUCCESS))
		{
			//expect two integers, the step and connection ports
			int stepConnectionPort = scanResponse.nextInt();
			int concurrentConnectionPort = scanResponse.nextInt();
			scanResponse.close();
			int[] ports = {stepConnectionPort, concurrentConnectionPort};
			return ports;
		}
		scanResponse.close();
		throw generateInvalidMessageException(response);
	}
	
	final private int[] getTextPortData() throws IOException, InvalidMessageException
	{
		String messageToServer = MESSAGES.ROOMSERVER.GET_TEXT_PORT_DATA;
		String response = sendMessageAndGetResponse(messageToServer);
		//look through the response
		Scanner scanResponse = new Scanner(response);
		//first part should be result
		String result = scanResponse.next();
		if (result.equals(MESSAGES.ROOMSERVER.GET_TEXT_PORT_DATA_SUCCESS))
		{
			//expect two integers, the step and connection ports
			int stepConnectionPort = scanResponse.nextInt();
			int concurrentConnectionPort = scanResponse.nextInt();
			scanResponse.close();
			int[] ports = {stepConnectionPort, concurrentConnectionPort};
			return ports;
		}
		scanResponse.close();
		throw generateInvalidMessageException(response);
	}
	
	final private int[] getUserListPortData() throws IOException, InvalidMessageException
	{
		String messageToServer = MESSAGES.ROOMSERVER.GET_USER_LIST_PORT_DATA;
		String response = sendMessageAndGetResponse(messageToServer);
		//look through the response
		Scanner scanResponse = new Scanner(response);
		//first part should be result
		String result = scanResponse.next();
		if (result.equals(MESSAGES.ROOMSERVER.GET_USER_LIST_PORT_DATA_SUCCESS))
		{
			//expect two integers, the step and connection ports
			int stepConnectionPort = scanResponse.nextInt();
			int concurrentConnectionPort = scanResponse.nextInt();
			scanResponse.close();
			int[] ports = {stepConnectionPort, concurrentConnectionPort};
			return ports;
		}
		scanResponse.close();
		throw generateInvalidMessageException(response);
	}
	
	final public void sendLeaveRequest()
	{
		try 
		{
			this.m_userListStepConnection.sendLeaveRequest();
		} catch (IOException e) 
		{
			this.m_userListStepConnection.displayConnectionLostMessage();
		} catch (InvalidMessageException e) 
		{
			this.m_userListStepConnection.handleInvalidMessageException(e);
		}
	}
	
	@Override
	public void handleOperationFailedException(OperationFailedException e) 
	{
		//ignore
	}

	@Override
	public void handleInvalidMessageException(InvalidMessageException e) 
	{
		//ignore
	}

	
}
