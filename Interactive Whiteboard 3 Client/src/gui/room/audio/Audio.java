package gui.room.audio;

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

import util.Text;

final public class Audio extends JPanel 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	final private FlowLayout AUDIO_LAYOUT = new FlowLayout(FlowLayout.CENTER);
	final private JButton cmdParticipation = new JButton();
	final private JButton cmdListening = new JButton();
	
	public Audio()
	{
		//define graphical properties
		this.cmdParticipation.setText(Text.GUI.ROOM.AUDIO.START_PARTICIPATION_STRING);
		this.cmdListening.setText(Text.GUI.ROOM.AUDIO.START_LISTENING_STRING);
		//add graphical components
		setLayout(this.AUDIO_LAYOUT);
		add(this.cmdParticipation);
		add(this.cmdListening);
	}

	final public void addAudioListener(AudioListener l)
	{
		this.cmdParticipation.addActionListener(l);
		this.cmdListening.addActionListener(l);
	}
	
	final public void reflectAudioListeningOn()
	{
		this.cmdListening.setText(Text.GUI.ROOM.AUDIO.STOP_LISTENING_STRING);
	}
	
	final public void reflectAudioListeningOff()
	{
		this.cmdListening.setText(Text.GUI.ROOM.AUDIO.START_LISTENING_STRING);
	}
	
	final public void reflectAudioParticipationOn()
	{
		this.cmdParticipation.setText(Text.GUI.ROOM.AUDIO.STOP_PARTICIPATION_STRING);
	}
	
	final public void reflectAudioParticipationOff()
	{
		this.cmdParticipation.setText(Text.GUI.ROOM.AUDIO.START_PARTICIPATION_STRING);
	}
}
