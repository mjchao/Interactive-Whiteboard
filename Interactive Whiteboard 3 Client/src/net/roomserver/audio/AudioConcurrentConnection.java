package net.roomserver.audio;

import gui.room.audio.Playback;

import java.io.IOException;

import net.ConcurrentConnection;

public class AudioConcurrentConnection extends ConcurrentConnection
{

	private Playback m_playback;
	
	public AudioConcurrentConnection(String ip, int port) throws IOException 
	{
		super(ip, port);
		// TODO Auto-generated constructor stub
	}

	@Override
	final public void run()
	{
		byte[] data = new byte[16000];
		while (this.isConnected)
		{
			if (this.m_playback.isPlaying())
				try 
				{
					this.m_in.read(data);
					this.m_playback.playSoundData(data);
				} catch (IOException e) 
				{
					break;
				}
		}
	}
	
	final public void setPlaybackDevice(Playback playback)
	{
		this.m_playback = playback;
	}
	
	@Override
	protected void decode(String message) 
	{
		// TODO Auto-generated method stub
		
	}

}
