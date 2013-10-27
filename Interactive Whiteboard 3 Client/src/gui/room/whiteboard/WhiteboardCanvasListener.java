package gui.room.whiteboard;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.math.BigInteger;

import net.InvalidMessageException;
import net.roomserver.whiteboard.WhiteboardStepConnection;

public class WhiteboardCanvasListener extends MouseAdapter
{
	
	final private WhiteboardMenu m_menu;
	final private WhiteboardCanvas m_gui;
	final private WhiteboardStepConnection m_whiteboardStepConnection;
	
	public WhiteboardCanvasListener(WhiteboardMenu menu, WhiteboardCanvas gui, WhiteboardStepConnection whiteboardStepConnection)
	{
		this.m_menu = menu;
		this.m_gui = gui;
		this.m_whiteboardStepConnection = whiteboardStepConnection;
	}
	
	@Override
	final public void mouseMoved(MouseEvent e)
	{
		if (e.getButton() == MouseEvent.BUTTON1)
		{
			setPixel(e);
		}
	}
	
	@Override
	final public void mouseClicked(MouseEvent e)
	{
		setPixel(e);
	}
	
	@Override
	final public void mouseDragged(MouseEvent e)
	{
		setPixel(e);
	}
	
	final public void setPixel(MouseEvent e)
	{
		int x = e.getX();
		int y = e.getY();
		int brushSize = WhiteboardCanvas.DEFAULT_BRUSH_SIZE;
		Color color = this.m_menu.getSelectedColor();
		BigInteger priority = this.m_gui.getNextHighestPriority();
		try 
		{
			if (this.m_whiteboardStepConnection.setPixel(x, y, color.getRed(), color.getGreen(), color.getBlue(), priority))
			{
				this.m_gui.setPixel(x, y, brushSize, color.getRed(), color.getGreen(), color.getBlue(), priority, true);
			}
		} catch (IOException e1) 
		{
			this.m_whiteboardStepConnection.displayConnectionLostMessage();
		} catch (InvalidMessageException e1) 
		{
			this.m_whiteboardStepConnection.handleInvalidMessageException(e1);
		}
	}
}
