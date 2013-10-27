package gui.room.whiteboard;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import util.Text;

public class WhiteboardMenuListener implements ActionListener, FocusListener
{

	final private WhiteboardMenu m_gui;
	
	private String lastRed = Text.GUI.ROOM.WHITEBOARD.DEFAULT_RED_STRING;
	private String lastGreen = Text.GUI.ROOM.WHITEBOARD.DEFAULT_GREEN_STRING;
	private String lastBlue = Text.GUI.ROOM.WHITEBOARD.DEFAULT_BLUE_STRING;
	
	public WhiteboardMenuListener(WhiteboardMenu gui)
	{
		this.m_gui = gui;
	}
	
	@Override
	public void focusGained(FocusEvent e)
	{
		Object c = e.getSource();
		if (this.m_gui.isTxtRed(c))
		{
			this.lastRed = this.m_gui.getRedInput();
			this.m_gui.setRedText("");
		} else if (this.m_gui.isTxtGreen(c))
		{
			this.lastGreen = this.m_gui.getGreenInput();
			this.m_gui.setGreenText("");
		} else if (this.m_gui.isTxtBlue(c))
		{
			this.lastBlue = this.m_gui.getBlueInput();
			this.m_gui.setBlueText("");
		}
	}

	@Override
	public void focusLost(FocusEvent e)
	{
		Object c = e.getSource();
		if (this.m_gui.isTxtRed(c))
		{
			if (!isValidRGB(this.m_gui.getRedInput()))
			{
				this.m_gui.setRedText(this.lastRed);
			}
		} else if (this.m_gui.isTxtGreen(c))
		{
			if (!isValidRGB(this.m_gui.getGreenInput()))
			{
				this.m_gui.setGreenText(this.lastGreen);
			}
		} else if (this.m_gui.isTxtBlue(c))
		{
			if (!isValidRGB(this.m_gui.getBlueInput()))
			{
				this.m_gui.setBlueText(this.lastBlue);
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		String c = e.getActionCommand();
		if (c.equals(Text.GUI.ROOM.WHITEBOARD.SELECT_ERASER_STRING))
		{
			this.m_gui.setEraserSelected();
			this.m_gui.updateSelectedColor();
		} else if (c.equals(Text.GUI.ROOM.WHITEBOARD.SELECT_MARKER_STRING))
		{
			this.m_gui.setMarkerSelected();
			this.m_gui.updateSelectedColor();
		} else if (c.equals(Text.GUI.ROOM.WHITEBOARD.SET_COLOR_STRING))
		{
			this.m_gui.updateSelectedColor();
		}
	}
	
	final private static boolean isValidRGB(String input)
	{
		try
		{
			int val = Integer.parseInt(input);
			if (val < 0 || val > 255)
			{
				return false;
			}
		} catch (NumberFormatException e)
		{
			return false;
		}
		return true;
	}

}
