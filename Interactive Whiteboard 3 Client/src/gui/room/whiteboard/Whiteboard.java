package gui.room.whiteboard;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import net.roomserver.whiteboard.WhiteboardStepConnection;

public class Whiteboard extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//graphical components
	final private BorderLayout WHITEBOARD_LAYOUT = new BorderLayout();
	final private WhiteboardMenu pnlMenu = new WhiteboardMenu();
	final private WhiteboardCanvas pnlCanvas;
	//properties
	private int m_length;
	private int m_width;
	
	public Whiteboard(int length, int width)
	{
		this.m_length = length;
		this.m_width = width;
		this.pnlCanvas = new WhiteboardCanvas(this.m_length, this.m_width);
		//define graphical components
		setLayout(this.WHITEBOARD_LAYOUT);
		add(this.pnlMenu, BorderLayout.NORTH);
		add(this.pnlCanvas, BorderLayout.CENTER);
	}
	
	final public void addListeners(WhiteboardStepConnection whiteboardStepConnection)
	{
		this.pnlCanvas.addWhiteboardCanvasListener(new WhiteboardCanvasListener(this.pnlMenu, this.pnlCanvas, whiteboardStepConnection));
		this.pnlMenu.addWhiteboardMenuListener(new WhiteboardMenuListener(this.pnlMenu));
	}
	
	final public WhiteboardCanvas getCanvas()
	{
		return this.pnlCanvas;
	}
}
