package net.roomserver.whiteboard;

import gui.room.whiteboard.WhiteboardCanvas;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Scanner;

import net.ConcurrentConnection;
import net.MESSAGES;

public class WhiteboardConcurrentConnection extends ConcurrentConnection
{

	private WhiteboardCanvas m_canvas;
	/**
	 * true if the canvas should draw the pixel immediately (if a user is drawing) or
	 * false if the canvas needs to wait to receive all the pixel data before drawing
	 * (if the server is updating our whiteboard right after joining)
	 */
	private boolean shouldDrawPixel = true;
	
	public WhiteboardConcurrentConnection(String ip, int port) throws IOException 
	{
		super(ip, port);
	}
	
	final public void setWhiteboardCanvas(WhiteboardCanvas canvas)
	{
		this.m_canvas = canvas;
	}

	@Override
	protected void decode(String message) 
	{
		Scanner scanMessage = new Scanner(message);
		//figure out what the server wants to do
		String command = scanMessage.next();
		if (command.equals(MESSAGES.ROOMSERVER.WHITEBOARD_SERVER.START_UPDATING))
		{
			this.shouldDrawPixel = false;
		} else if (command.equals(MESSAGES.ROOMSERVER.WHITEBOARD_SERVER.UPDATE_PIXEL))
		{
			//expect 6 integers: x, y, r, g, b, priority
			int x = scanMessage.nextInt();
			int y = scanMessage.nextInt();
			int r = scanMessage.nextInt();
			int g = scanMessage.nextInt();
			int b = scanMessage.nextInt();
			BigInteger priority = scanMessage.nextBigInteger();
			this.m_canvas.setPixel(x, y, WhiteboardCanvas.DEFAULT_BRUSH_SIZE, r, g, b, priority, this.shouldDrawPixel);
		} else if (command.equals(MESSAGES.ROOMSERVER.WHITEBOARD_SERVER.DONE_UPDATING))
		{
			this.shouldDrawPixel = true;
			this.m_canvas.repaint();
		}
		
	}

}
