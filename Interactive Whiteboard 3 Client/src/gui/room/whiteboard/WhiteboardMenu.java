package gui.room.whiteboard;

import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import util.Text;

public class WhiteboardMenu extends JPanel
{
	private static final long serialVersionUID = 1L;

	final public static Color DEFAULT_COLOR = new Color(250, 250, 250);
	final public static Color DEFAULT_MARKER_COLOR = new Color(0, 0, 0);
	
	final private JLabel lblBrushType;
	final private JButton cmdSelectMarker;
	final private JButton cmdSelectEraser;
	final private JLabel lblColor;
	final private JTextField txtRed;
	final private JTextField txtGreen;
	final private JTextField txtBlue;
	final private JButton cmdSetColor;
		
	private Color lastMarkerColor = DEFAULT_MARKER_COLOR;
	private Color color = DEFAULT_MARKER_COLOR;
	
	public WhiteboardMenu()
	{
		this.lblBrushType = new JLabel(Text.GUI.ROOM.WHITEBOARD.DEFAULT_BRUSH_TYPE_STRING);
		this.cmdSelectMarker = new JButton(Text.GUI.ROOM.WHITEBOARD.SELECT_MARKER_STRING);
		this.cmdSelectEraser = new JButton(Text.GUI.ROOM.WHITEBOARD.SELECT_ERASER_STRING);
		this.lblColor = new JLabel(Text.GUI.ROOM.WHITEBOARD.DEFAULT_COLOR_STRING);
		this.txtRed = new JTextField(Text.GUI.ROOM.WHITEBOARD.DEFAULT_RED_STRING);
		this.txtRed.setColumns(3);
		this.txtGreen = new JTextField(Text.GUI.ROOM.WHITEBOARD.DEFAULT_GREEN_STRING);
		this.txtGreen.setColumns(3);
		this.txtBlue = new JTextField(Text.GUI.ROOM.WHITEBOARD.DEFAULT_BLUE_STRING);
		this.txtBlue.setColumns(3);
		this.cmdSetColor = new JButton(Text.GUI.ROOM.WHITEBOARD.SET_COLOR_STRING);
		
		setLayout(new FlowLayout(FlowLayout.CENTER));
		add(this.lblBrushType);
		add(this.cmdSelectMarker);
		add(this.cmdSelectEraser);
		add(this.lblColor);
		add(this.txtRed);
		add(this.txtGreen);
		add(this.txtBlue);
		add(this.cmdSetColor);
	}
	
	final public void addWhiteboardMenuListener(WhiteboardMenuListener l)
	{
		this.cmdSelectMarker.addActionListener(l);
		this.cmdSelectEraser.addActionListener(l);
		this.cmdSetColor.addActionListener(l);
		this.txtRed.addFocusListener(l);
		this.txtGreen.addFocusListener(l);
		this.txtBlue.addFocusListener(l);
	}
	
	final public boolean isTxtRed(Object c)
	{
		return c == this.txtRed;
	}
	
	final public String getRedInput()
	{
		return this.txtRed.getText();
	}
	
	final public void setRedText(String str)
	{
		this.txtRed.setText(str);
	}
	
	final public boolean isTxtGreen(Object c)
	{
		return c == this.txtGreen;
	}
	
	final public String getGreenInput()
	{
		return this.txtGreen.getText();
	}
	
	final public void setGreenText(String str)
	{
		this.txtGreen.setText(str);
	}
	
	final public boolean isTxtBlue(Object c)
	{
		return c == this.txtBlue;
	}
	
	final public String getBlueInput()
	{
		return this.txtBlue.getText();
	}
	
	final public void setBlueText(String str)
	{
		this.txtBlue.setText(str);
	}
	
	final public void updateSelectedColor()
	{
		int r = Integer.parseInt(getRedInput());
		int g = Integer.parseInt(getGreenInput());
		int b = Integer.parseInt(getBlueInput());
		this.color = new Color(r, g, b);
		if (!this.color.equals(DEFAULT_COLOR))
		{
			this.lastMarkerColor = this.color;
		}
		this.lblColor.setText(Text.GUI.ROOM.WHITEBOARD.getColorString(r, g, b));
	}
	
	final public void setEraserSelected()
	{
		this.color = DEFAULT_COLOR;
		this.lblBrushType.setText("Brush Type: Eraser");
		setRedText(String.valueOf(this.color.getRed()));
		setGreenText(String.valueOf(this.color.getGreen()));
		setBlueText(String.valueOf(this.color.getBlue()));
	}
	
	final public void setMarkerSelected()
	{
		this.color = this.lastMarkerColor;
		this.lblBrushType.setText("Brush Type: Marker");
		setRedText(String.valueOf(this.color.getRed()));
		setGreenText(String.valueOf(this.color.getGreen()));
		setBlueText(String.valueOf(this.color.getBlue()));
	}
	
	final public void setColorAsLastMarkerColor()
	{
		this.color = this.lastMarkerColor;
	}
	
	final public Color getSelectedColor()
	{
		return this.color;
	}
}
