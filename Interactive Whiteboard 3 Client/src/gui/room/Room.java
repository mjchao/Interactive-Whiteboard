package gui.room;

import java.awt.BorderLayout;

import gui.room.audio.Audio;
import gui.room.audio.AudioListener;
import gui.room.audio.Playback;
import gui.room.text.TextChat;
import gui.room.text.TextChatListener;
import gui.room.userlist.UserListDisplay;
import gui.room.whiteboard.Whiteboard;
import gui.room.whiteboard.WhiteboardCanvas;

import javax.swing.JPanel;

import net.roomserver.audio.AudioStepConnection;
import net.roomserver.text.TextStepConnection;
import net.roomserver.userlist.UserListStepConnection;
import net.roomserver.whiteboard.WhiteboardStepConnection;

public class Room extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	//graphical components
	final private BorderLayout ROOM_LAYOUT = new BorderLayout();
	final private Audio pnlAudio;
	final private TextChat pnlTextChat;
	final private Whiteboard pnlWhiteboard;
	final private UserListDisplay pnlUserListDisplay;
	//listeners
	private AudioListener m_audioListener;
	
	
	public Room(int maxUsersPerRoom, int whiteboardLength, int whiteboardWidth)
	{
		//create and add graphical components
		setLayout(this.ROOM_LAYOUT);
		this.pnlAudio = new Audio();
		add(this.pnlAudio, BorderLayout.NORTH);
		this.pnlWhiteboard = new Whiteboard(whiteboardLength, whiteboardWidth);
		add(this.pnlWhiteboard, BorderLayout.CENTER);
		this.pnlTextChat = new TextChat();	
		add(this.pnlTextChat, BorderLayout.SOUTH);
		this.pnlUserListDisplay = new UserListDisplay(maxUsersPerRoom);
		add(this.pnlUserListDisplay, BorderLayout.EAST);
	}
	
	final public void addListeners(AudioStepConnection audioStepConnection, TextStepConnection textStepConnection, WhiteboardStepConnection whiteboardStepConnection, UserListStepConnection userListStepConnection)
	{
		this.m_audioListener = new AudioListener(this.pnlAudio, audioStepConnection);
		this.pnlAudio.addAudioListener(this.m_audioListener);
		this.pnlTextChat.addTextChatListener(new TextChatListener(this.pnlTextChat, textStepConnection));
		this.pnlWhiteboard.addListeners(whiteboardStepConnection);
		this.pnlUserListDisplay.addListeners(userListStepConnection);
	}
	
	final public Playback getAudioPlayback()
	{
		return this.m_audioListener.getPlayback();
	}
	
	final public TextChat getTextChatInterface()
	{
		return this.pnlTextChat;
	}
	
	final public WhiteboardCanvas getWhiteboardCanvas()
	{
		return this.pnlWhiteboard.getCanvas();
	}
	
	final public UserListDisplay getUserListDisplay()
	{
		return this.pnlUserListDisplay;
	}
}
