package gui.room.audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import util.CommonMethods;
import util.Text;

import net.roomserver.audio.AudioStepConnection;

final public class Capture extends Thread
{
	//Format: 
	//Encoding:
		final private static AudioFormat.Encoding ENCODING = AudioFormat.Encoding.PCM_SIGNED;
	//Sample rate: (number of samples per second)
		final private static float SAMPLE_RATE = 8000;
	//Sample size in bits:
		final private static int SAMPLE_SIZE = 16;
	//Channels: (Stereo)
		final private static int CHANNELS = 2;
	//Frame Size: (SAMPLE_SIZE/8) * CHANNELS 
		final private static int FRAME_SIZE = 4;
	//Frame Rate:
		final private static float FRAME_RATE = 8000;
	//Big Endian/Little Endian
		final private static boolean USE_BIG_ENDIAN = true;
	final public static AudioFormat FORMAT = new AudioFormat
	(
		ENCODING, SAMPLE_RATE, SAMPLE_SIZE, CHANNELS, FRAME_SIZE, FRAME_RATE, USE_BIG_ENDIAN	
	);
	private TargetDataLine m_line;
	private DataLine.Info m_info;
	
	final private AudioStepConnection m_audioStepConnection;
	
	public Capture(AudioStepConnection audioStepConnection)
	{
		this.m_audioStepConnection = audioStepConnection;
	}
	
	
	@Override
	final public synchronized void start()
	{
		this.m_done = false;
		super.start();
	}
	
	private boolean m_done = true;
	
	@Override
	final public void run()
	{
		this.m_info = new DataLine.Info(TargetDataLine.class, FORMAT);
		if (!AudioSystem.isLineSupported(this.m_info))
		{
			CommonMethods.displayErrorMessage(Text.GUI.ROOM.AUDIO.FORMAT_NOT_SUPPORTED_ERROR_MESSAGE);
			return;
		}
		try
		{
			this.m_line = (TargetDataLine)(AudioSystem.getLine(this.m_info));
			this.m_line.open(FORMAT, this.m_line.getBufferSize());
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
		
		int frameSizeInBytes = FORMAT.getFrameSize();
		int bufferLengthInFrames = this.m_line.getBufferSize() / 8;
		int bufferLengthInBytes = frameSizeInBytes * bufferLengthInFrames;
		byte[] soundData = new byte[bufferLengthInBytes];
		this.m_line.start();
		
		while (!this.m_done)
		{
			int numBytesRead = this.m_line.read(soundData, 0, bufferLengthInBytes);
			if (numBytesRead == -1)
			{
				break;
			}
			this.m_audioStepConnection.writeBytes(soundData);
		}
	}
	
	final public void stopCapturing()
	{
		this.m_done = true;
		if (this.m_line != null)
		{
			this.m_line.stop();
			this.m_line.close();
		}
	}
	
	//DEBUG
	/*
	final public static void main(String[] args) throws InterruptedException
	{
		Capture capture = new Capture(null);
		capture.start();
		Thread.sleep(3000);
	}//*/
}
