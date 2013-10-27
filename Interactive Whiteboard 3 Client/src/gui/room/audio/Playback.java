package gui.room.audio;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import util.CommonMethods;
import util.Text;

public class Playback extends Thread
{
	final public AudioFormat format = Capture.FORMAT;
	
	private SourceDataLine line;
		final private int BUFFER_SIZE = 16384;
	private DataLine.Info info;
	
	public Playback()
	{

	}
	
	private boolean m_started = false;
	@Override
	final public synchronized void start()
	{
		this.m_playing = true;
		this.m_started = true;
		super.start();
	}
	
	final public boolean isStarted()
	{
		return this.m_started;
	}
	
	final public void setPlaying(boolean b)
	{
		this.m_playing = b;
	}
	
	final public boolean isPlaying()
	{
		return this.m_playing;
	}
	
	/**
	 * true if we are playing sounds received from the server, false otherwise
	 */
	private boolean m_playing = true;
	private byte[] m_soundDataToPlay = null;
	
	@Override
	final public void run()
	{
		this.info = new DataLine.Info(SourceDataLine.class, this.format);
		if (!AudioSystem.isLineSupported(this.info))
		{
			CommonMethods.displayErrorMessage(Text.GUI.ROOM.AUDIO.FORMAT_NOT_SUPPORTED_ERROR_MESSAGE);
			return;
		}
		try
		{
			this.line = (SourceDataLine) AudioSystem.getLine(this.info);
			this.line.open(this.format, this.BUFFER_SIZE);
		} catch (LineUnavailableException e)
		{
			CommonMethods.displayErrorMessage(Text.GUI.ROOM.AUDIO.LINE_UNAVAILABLE_ERROR_MESSAGE);
			return;
		} catch (SecurityException e)
		{
			CommonMethods.displayErrorMessage(Text.GUI.ROOM.AUDIO.SECURITY_EXCEPTION_ERROR_MESSAGE);
			return;
		} catch (IllegalArgumentException e)
		{
			CommonMethods.displayErrorMessage(Text.GUI.ROOM.AUDIO.FORMAT_NOT_SUPPORTED_ERROR_MESSAGE);
			return;
		} catch (Exception e)
		{
			CommonMethods.displayErrorMessage(Text.GUI.ROOM.AUDIO.getUnexpectedErrorMessage(e.getMessage()));
			return;
		}
		int frameSizeInBytes = this.format.getFrameSize();
		this.line.start();
		while (true)
		{
			if (this.m_playing == true)
			{
				try
				{
					if (this.m_soundDataToPlay != null)
					{
						ByteArrayInputStream forPlayback = new ByteArrayInputStream(this.m_soundDataToPlay);
						AudioInputStream stream = new AudioInputStream(forPlayback, this.format, 
														this.m_soundDataToPlay.length/frameSizeInBytes);
						stream.reset();
						AudioInputStream playbackInputStream = AudioSystem.getAudioInputStream(this.format, stream);
						int numBytesRead = playbackInputStream.read(this.m_soundDataToPlay);
						int numBytesRemaining = numBytesRead;
						while (numBytesRemaining > 0)
						{
							if (this.m_soundDataToPlay != null)
							{
								numBytesRemaining -= this.line.write(this.m_soundDataToPlay, 0, numBytesRemaining);
							}
						}
						playbackInputStream.close();
						this.m_soundDataToPlay = null;
					}
				} catch (IOException e)
				{
					//continue
				}
			}
		}
	}
	
	final public void playSoundData(byte[] soundData)
	{
		this.m_soundDataToPlay = soundData.clone();
	}
	
	//DEBUG
	/*
	final public static void main(String[] args) throws InterruptedException
	{
		Playback playback = new Playback();
		playback.start();
		for (int i = 0; i < 25; i++)
		{
			byte[] bytes = {0, 1, 2, 3, 4, 5};
			playback.playSoundData(bytes);
			Thread.sleep(100);
			System.out.println(i);
		}
		Thread.sleep(5000);
		byte[] bytes = {1, 2, 3, 4, 5};
		playback.playSoundData(bytes);
	}//*/
}