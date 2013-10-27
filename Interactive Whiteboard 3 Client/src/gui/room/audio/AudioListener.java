package gui.room.audio;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.roomserver.audio.AudioStepConnection;

import util.Text;

public class AudioListener implements ActionListener
{

	final private Audio m_gui;
	final private AudioStepConnection m_audioStepConnection;
	
	private Capture m_capture;
	final private Playback m_playback;
	
	public AudioListener(Audio gui, AudioStepConnection audioStepConnection)
	{
		this.m_gui = gui;
		this.m_audioStepConnection = audioStepConnection;
		this.m_playback = new Playback();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		String command = e.getActionCommand();
		if (command.equals(Text.GUI.ROOM.AUDIO.START_LISTENING_STRING))
		{
			if (!this.m_playback.isStarted())
			{
				this.m_playback.start();
			}
			this.m_playback.setPlaying(true);
			this.m_gui.reflectAudioListeningOn();
		} else if (command.equals(Text.GUI.ROOM.AUDIO.STOP_LISTENING_STRING))
		{
			this.m_playback.setPlaying(false);
			this.m_gui.reflectAudioListeningOff();
		} else if (command.equals(Text.GUI.ROOM.AUDIO.START_PARTICIPATION_STRING))
		{
			this.m_capture = new Capture(this.m_audioStepConnection);
			this.m_capture.start();
			this.m_gui.reflectAudioParticipationOn();
		} else if (command.equals(Text.GUI.ROOM.AUDIO.STOP_PARTICIPATION_STRING))
		{
			this.m_capture.stopCapturing();
			this.m_gui.reflectAudioParticipationOff();
		}
	}
	
	final public Playback getPlayback()
	{
		return this.m_playback;
	}

}
