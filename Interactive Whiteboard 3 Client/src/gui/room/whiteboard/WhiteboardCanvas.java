package gui.room.whiteboard;

import java.awt.Color;
import java.awt.Graphics;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.JPanel;

final public class WhiteboardCanvas extends JPanel
{
	final public static int DEFAULT_BRUSH_SIZE = 10;
	
	final public static Color DEFAULT_COLOR = WhiteboardMenu.DEFAULT_COLOR;
	final public static BigInteger DEFAULT_PRIORITY = new BigInteger("-1000000000");
	private static final long serialVersionUID = 1L;
	
	private Pixel[] pixels;
	final private PixelComparator pixelComparator = new PixelComparator();
	
	private BigInteger highestPriority = DEFAULT_PRIORITY;
	final public static BigInteger ONE = new BigInteger("1");
	
	
	private int m_whiteboardLength;
	private int m_whiteboardWidth;
	
	public WhiteboardCanvas(int whiteboardLength, int whiteboardWidth)
	{
		this.m_whiteboardLength = whiteboardLength;
		this.m_whiteboardWidth = whiteboardWidth;
		this.pixels = new Pixel[this.m_whiteboardLength * this.m_whiteboardWidth];
		this.setBackground(DEFAULT_COLOR);
		for (int i = 0; i < whiteboardLength; i++)
		{
			for (int j = 0; j < whiteboardWidth; j++)
			{
				this.pixels[i * whiteboardLength + j] = new Pixel(i, j, DEFAULT_COLOR);
			}
		}
	}
	
	final public BigInteger getNextHighestPriority()
	{
		return this.highestPriority.add(ONE);
	}
	
	final public void setPixel(int x, int y, int brushSize, int red, int green, int blue, BigInteger priority, boolean drawImmediately)
	{
		Pixel targetPixel = this.pixels[(x - brushSize/2) * this.m_whiteboardLength + y];
		targetPixel.setPriority(priority);
		if (priority.compareTo(this.highestPriority) > 0)
		{
			this.highestPriority = priority;
		}
		targetPixel.setPixelColor(new Color(red, green, blue));
		this.needToResortPixels = true;
		if (drawImmediately)
		{
			this.drawPixel(targetPixel);
		}
	}
	
	final public void addWhiteboardCanvasListener(WhiteboardCanvasListener l)
	{
		addMouseListener(l);
		addMouseMotionListener(l);
	}
	
	/**
	 * true if some new pixels were drawn after the last sort
	 * false if not
	 */
	private boolean needToResortPixels = true;
	private Pixel[] pixelsCopy;
	@Override
	final public void paint(Graphics g)
	{
		g.setColor(WhiteboardMenu.DEFAULT_COLOR);
		g.fillRect(0, 0, this.m_whiteboardLength, this.m_whiteboardWidth);
		try
		{
			if (this.needToResortPixels)
			{
				this.pixelsCopy = this.pixels.clone();
				Arrays.sort(this.pixelsCopy, this.pixelComparator);
				this.needToResortPixels = false;
			}
			for (int pixelIndex = 0; pixelIndex < this.pixelsCopy.length; pixelIndex++)
			{
				Pixel aPixel = this.pixelsCopy[pixelIndex];
				if (!aPixel.getPixelColor().equals(DEFAULT_COLOR) || aPixel.getPriority().compareTo(DEFAULT_PRIORITY) != 0)
				{
					int x = aPixel.getPixelX();
					int y = aPixel.getPixelY();
					Color color = aPixel.getPixelColor();
					g.setColor(color);
					g.fillOval(x, y, DEFAULT_BRUSH_SIZE, DEFAULT_BRUSH_SIZE);
				}
			}
		} catch (IllegalArgumentException e)
		{
			//ignore
		}
	}
	
	final public void drawPixel(Pixel pixelToDraw)
	{
		int x = pixelToDraw.getPixelX();
		int y = pixelToDraw.getPixelY();
		Color color = pixelToDraw.getPixelColor();
		Graphics g = this.getGraphics();
		if (g != null)
		{
			g.setColor(color);
			g.fillOval(x, y, DEFAULT_BRUSH_SIZE, DEFAULT_BRUSH_SIZE);
		}
		this.needToResortPixels = true;
	}
	
	final private class Pixel
	{
		final protected int m_pixelX;
		final protected int m_pixelY;
		protected Color m_pixelColor;
		protected BigInteger m_priority = DEFAULT_PRIORITY;
		
		public Pixel(int x, int y, Color color)
		{
			this.m_pixelX = x;
			this.m_pixelY = y;
			this.m_pixelColor = color;
		}
		
		final public int getPixelX()
		{
			return this.m_pixelX;
		}
		
		final public int getPixelY()
		{
			return this.m_pixelY;
		}
		
		final public Color getPixelColor()
		{
			return this.m_pixelColor;
		}
		
		final public BigInteger getPriority()
		{
			return this.m_priority;
		}
		
		final public void setPriority(BigInteger newPriority)
		{
			this.m_priority = newPriority;
		}
		
		final public void setPixelColor(Color color)
		{
			this.m_pixelColor = color;
		}
	}
	
	final protected class PixelComparator implements Comparator<Pixel>
	{
		@Override
		final public int compare(Pixel pixel1, Pixel pixel2)
		{
			return pixel1.m_priority.compareTo(pixel2.m_priority);
		}
	}
}
