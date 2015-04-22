package mc.iwclient.room;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Stack;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import mc.iwclient.uitemplates.SelectButtonGroup;
import mc.iwclient.uitemplates.ToolBarSelectButton;
import mc.iwclient.util.Text;

public class WhiteboardActivity extends Activity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	final private ToolsPanel pnlTools;
	
	final private JPanel pnlContent;
	
	/**
	 * drawing area for this whiteboard activity
	 */
	final protected Canvas pnlCanvas;
	
	public WhiteboardActivity() {
		super( "Whiteboard" , true );
		setLayout( new BorderLayout() );
		this.pnlContent = new JPanel( new BorderLayout() );
			this.pnlTools = new ToolsPanel();
			this.pnlContent.add( this.pnlTools, BorderLayout.NORTH );
			
			this.pnlCanvas = new Canvas( this.pnlTools );
			this.pnlContent.add( this.pnlCanvas , BorderLayout.CENTER );
		JScrollPane scrollContent = new JScrollPane( this.pnlContent );
		scrollContent.setBorder( null );
		add( scrollContent , BorderLayout.CENTER );
		addLocalKeystrokeListenerTo( this );
	}
	
	public void addLocalKeystrokeListenerTo( JComponent c ) {
		AbstractAction undoAction = new AbstractAction() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed( ActionEvent e ) {
				WhiteboardActivity.this.pnlCanvas.requestUndo();
			}
		};
		c.getInputMap().put( KeyStroke.getKeyStroke( KeyEvent.VK_Z , Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() ) , "Undo" );
		c.getActionMap().put( "Undo" , undoAction );
		
		AbstractAction redoAction = new AbstractAction() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed( ActionEvent e ) {
				WhiteboardActivity.this.pnlCanvas.requestRedo();
			}
		};
		c.getInputMap().put( KeyStroke.getKeyStroke( KeyEvent.VK_Y , Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() ) , "Redo" );
		c.getInputMap().put( KeyStroke.getKeyStroke( KeyEvent.VK_Z , Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | InputEvent.SHIFT_DOWN_MASK ), "Redo" );
		c.getActionMap().put( "Redo" ,  redoAction );
	}
	
	@Override
	public void requestBringToFront() {
		super.requestBringToFront();
		this.requestFocus();
	}
	
	/**
	 * used to request this activity be brought to the front
	 * without the current component losing focus
	 */
	public void requestBringToFrontWithoutLosingFocus() {
		super.requestBringToFront();
	}
	
	private class Canvas extends JPanel {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		final public Cursor DRAW_CURSOR = new Cursor( Cursor.CROSSHAIR_CURSOR );
		/**
		 * extra space on the borders makes it easier for the user to erase
		 * stuff on the borders
		 */
		final public static int EXTRA_BORDER_DRAWING_SPACE = 25;
		final public static int DEFAULT_WIDTH = 100;
		final public static int DEFAULT_HEIGHT = 100;
		
		final private ToolsPanel m_toolsPanel;
		final private LineManager m_linesDrawnManager;
		
		final private ActionManager undoStack;
		final private ActionManager redoStack;
		
		private Action currentAction;
		
		public Canvas( ToolsPanel toolsPanel ) {
			this.undoStack = new ActionManager();
			this.redoStack = new ActionManager();
			this.currentAction = new Action();
			
			setBackground( Color.white );
			this.m_toolsPanel = toolsPanel;

			this.m_linesDrawnManager = new LineManager();
			
			addMouseMotionListener( new MouseAdapter() {

				private int m_lastX = Integer.MIN_VALUE;
				private int m_lastY = Integer.MIN_VALUE;
				
				@Override
				public void mouseDragged( MouseEvent e ) {
					handleDrag( this.m_lastX , this.m_lastY , e.getX() , e.getY() );
					this.m_lastX = e.getX();
					this.m_lastY = e.getY();
				}
				
				@Override
				public void mouseMoved( MouseEvent e ) {
					this.m_lastX = e.getX();
					this.m_lastY = e.getY();
					setCursor( Canvas.this.DRAW_CURSOR );
				}
			});
			
			addMouseListener( new MouseAdapter() {
				
				@Override
				public void mousePressed( MouseEvent e ) {
					//TODO remove and base clearing of the redo stack on networking
					clearRedoStack();
					requestBeginNewAction();
				}
				
				@Override
				public void mouseReleased( MouseEvent e ) {
					requestEndCurrentAction();
				}
			});

			WhiteboardActivity.this.addRequestBringToFrontListener( this , WhiteboardActivity.this );
			addLocalKeystrokeListenerTo( this );
		}
		
		@Override
		public Dimension getPreferredSize() {
			return new Dimension( DEFAULT_WIDTH , DEFAULT_HEIGHT );
		}
		
		public void handleDrag( int startX , int startY , int endX , int endY ) {
			if ( this.m_toolsPanel.isPencilDrawing() ) {
				requestDrawLine( startX , startY , endX , endY );
			}
			else if ( this.m_toolsPanel.isErasing() ) {
				requestEraseLine( startX , startY , endX , endY );
			}
		}
		
		/**
		 * sends a request to the server telling it to draw the line with
		 * the given properties
		 * 
		 * @param startX
		 * @param startY
		 * @param endX
		 * @param endY
		 */
		public void requestDrawLine( int startX , int startY , int endX , int endY ) {
			//TODO networking
			drawLine( startX , startY , endX , endY );
		}
		
		/**
		 * draws the line with the given starting and ending coordinates on
		 * this canvas
		 * 
		 * @param startX
		 * @param startY
		 * @param endX
		 * @param endY
		 */
		public void drawLine( int startX , int startY , int endX , int endY ) {
			
			//do not draw points
			if ( startX == endX && startY == endY ) {
				return;
			}
			Graphics2D g2d = (Graphics2D) this.getGraphics();
			g2d.setColor( this.m_toolsPanel.getSelectedColor() );
			g2d.setStroke( new BasicStroke( this.m_toolsPanel.getThickness() ) );
			g2d.drawLine( startX , startY , endX , endY );
			Line lineToAdd = new Line( startX , startY , endX , endY , g2d.getColor() , this.m_toolsPanel.getThickness() );
			this.m_linesDrawnManager.addLine( lineToAdd );
			recordAction( Action.DRAW_ACTION , lineToAdd );
		}
		
		/**
		 * sends a request to the server telling it to erase the lines near
		 * this specified erase line with the provided properties
		 * 
		 * @param startX
		 * @param startY
		 * @param endX
		 * @param endY
		 */
		public void requestEraseLine( int startX , int startY , int endX , int endY ) {
			//TODO networking
			eraseLine( startX , startY , endX , endY );
		}
		
		/**
		 * erases any lines on this canvas that intersect with the specified
		 * erasure line
		 * 
		 * @param startX			starting x coordinate of the erasure line
		 * @param startY			starting y coordinate of the erasure line
		 * @param endX				ending x coordinate of the erasure line
		 * @param endY				ending y coordinate of the erasure line
		 */
		public void eraseLine( int startX , int startY , int endX , int endY ) {
			Line erasure = new Line( startX , startY , endX , endY , Color.white , this.m_toolsPanel.getThickness() );
			LineManager removedLines = this.m_linesDrawnManager.removeIntersectionsWith( erasure );
			repaint();
			for ( Line l : removedLines.getLines() ) {
				recordAction( Action.ERASE_ACTION , l );
			}
		}
		
		@Override
		public void repaint() {
			if ( this.m_linesDrawnManager != null ) {
				this.setPreferredSize( new Dimension( this.m_linesDrawnManager.getLargestX()+EXTRA_BORDER_DRAWING_SPACE , this.m_linesDrawnManager.getLargestY()+EXTRA_BORDER_DRAWING_SPACE ) );
			}
			super.repaint();
		}
		
		@Override
		public void paint( Graphics g ) {
			super.paint( g );
			Graphics2D g2d = ( Graphics2D ) g;
			for ( Line l : this.m_linesDrawnManager.getLines() ) {
				g2d.setColor( l.m_color );
				g2d.setStroke( new BasicStroke( l.m_thickness ) );
				g2d.drawLine( l.m_startX , l.m_startY , l.m_endX , l.m_endY );
			}
			super.paintComponents( g );
		}
		
		/**
		 * requests that the server begin recording a new undo/redo action
		 */
		protected void requestBeginNewAction() {
			//TODO
			beginNewAction();
		}
		
		/**
		 * starts recording a new undo/redo action
		 */
		protected void beginNewAction() {
			this.currentAction = new Action();
		}
		
		/**
		 * records the given action 
		 * 
		 * @param actionType
		 * @param l
		 */
		protected void recordAction( String actionType , Line l ) {
			if ( this.currentAction != null ) {
				this.currentAction.addLine( actionType , l );
			}
		}
		
		/**
		 * requests that the server end recording of its current action
		 */
		protected void requestEndCurrentAction() {
			//TODO
			endCurrentAction();
		}
		/**
		 * ends the recording of an undo/redo action
		 */
		protected void endCurrentAction() {
			this.undoStack.addLastAction( this.currentAction );
			this.currentAction = null;
		}
		
		/**
		 * sends an undo request to the server to undo a previous action
		 */
		public void requestUndo() {
			//TODO
			undo();
		}
		
		public void undo() {
			if ( this.undoStack.hasMoreActions() ) {
				Action actionToUndo = this.undoStack.popLastAction();
				executeOppositeAction( actionToUndo );
				this.redoStack.addLastAction( actionToUndo );
				repaint();
			}
		}
		
		/**
		 * clears the redo stack, which should be done whenever the user
		 * draws somewhere
		 */
		protected void clearRedoStack() {
			this.redoStack.clear();
		}
		
		/**
		 * sends a redo request to the server to redo a previous undone action
		 */
		public void requestRedo() {
			//TODO
			redo();
		}
		public void redo() {
			if ( this.redoStack.hasMoreActions() ) {
				Action actionToRedo = this.redoStack.popLastAction();
				executeAction( actionToRedo );
				this.undoStack.addLastAction( actionToRedo );
				repaint();
			}
		}
		
		private void executeOppositeAction( Action a ) {
			for ( ActionComponent c : a.getActionComponents() ) {
				if ( c.m_actionType.equals( Action.DRAW_ACTION ) ) {
					this.m_linesDrawnManager.removeLine( c.m_line );
				}
				else if ( c.m_actionType.equals( Action.ERASE_ACTION ) ) {
					this.m_linesDrawnManager.addLine( c.m_line );
				}
			}
		}
		
		private void executeAction( Action a ) {
			for ( ActionComponent c : a.getActionComponents() ) {
				if ( c.m_actionType.equals( Action.DRAW_ACTION ) ) {
					this.m_linesDrawnManager.addLine( c.m_line );
				}
				else if ( c.m_actionType.equals( Action.ERASE_ACTION ) ) {
					this.m_linesDrawnManager.removeLine( c.m_line );
				}
			}
		}
	}
	
	private class Line {
		
		final public int m_startX;
		final public int m_startY;
		final public int m_endX;
		final public int m_endY;
		final public Color m_color;
		final public int m_thickness;
		
		public Line( int startX , int startY , int endX , int endY , Color c , int thickness) {
			this.m_startX = startX;
			this.m_startY = startY;
			this.m_endX = endX;
			this.m_endY = endY;
			this.m_color = c;
			this.m_thickness = thickness;
		}
		
		/**
		 * determines the shortest distance from a given point to a line.
		 * algorithm credits: http://paulbourke.net/geometry/pointlineplane/
		 * 
		 * @param px		x coordinate of a point, p
		 * @param py		y coordinate of a point, p
		 * @param l			a line
		 * @return			shortest distance from p to the line
		 */
		private double pointToLineDistance( int px , int py , Line l ) {
			double xDelta = l.m_endX - l.m_startX;
			double yDelta = l.m_endY - l.m_startY;
			double u = ( (px - l.m_startX )*xDelta + (py - l.m_startY )*yDelta ) / ( xDelta*xDelta + yDelta*yDelta );
			double closestX;
			double closestY;
			if ( u<0 ) {
				closestX = l.m_startX;
				closestY = l.m_startY;
			}
			else if ( u>1 ) {
				closestX = l.m_endX;
				closestY = l.m_endY;
			}
			else {
				closestX = l.m_startX + u*xDelta;
				closestY = l.m_startY + u*yDelta;
			}
			
			double dx = px-closestX;
			double dy = py-closestY;
			return dx*dx + dy*dy;
		}
		
		/**
		 * determines if this line intersects with the other line.
		 * 
		 * @param anotherLine		another line
		 * @return					if this line intersects with the other line
		 */
		public boolean intersectsWith( Line anotherLine ) {
			double d1 = pointToLineDistance( this.m_startX , this.m_startY , anotherLine );
			double d2 = pointToLineDistance( this.m_endX , this.m_endY , anotherLine );
			double d3 = pointToLineDistance( anotherLine.m_startX , anotherLine.m_startY , this );
			double d4 = pointToLineDistance( anotherLine.m_endX , anotherLine.m_endY , this );
			double minDistance = Math.min( Math.min( d1 , d2 ) , Math.min( d3 , d4 ) );
			if ( minDistance < this.m_thickness + anotherLine.m_thickness ) {
				return true;
			}
			return false;
		}
		
		/*
		/**
		 * determines if this line intersects with the other line. algorithm credit
		 * to http://stackoverflow.com/questions/563198/how-do-you-detect-where-two-line-segments-intersect
		 * Gareth Rees
		 * 
		 * @param anotherLine		another line
		 * @return					if this line intersects with the other line
		 */
		/*
		public boolean intersectsWith( Line anotherLine ) {
			Vector2D p = new Vector2D( this.m_startX , this.m_startY );
			Vector2D q = new Vector2D( anotherLine.m_startX , anotherLine.m_startY );
			Vector2D r = new Vector2D( this.m_endX-this.m_startX , this.m_endY-this.m_startY );
			Vector2D s = new Vector2D( anotherLine.m_endX-anotherLine.m_startX , anotherLine.m_endY-anotherLine.m_startY );
			Vector2D qMinusP = q.subtract( p );
			double rCrossS = r.cross( s );
			double t = ( qMinusP ).cross( s ) / ( rCrossS );
			double u = ( qMinusP ).cross( r ) / ( rCrossS );
			if ( rCrossS != 0 ) {
				
				//check non-collinear, intersecting
				//the collinear, overlapping case is too unlikely
				if ( 0 <= t && t <= 1 ) {
					if ( 0 <= u && u <= 1) {
						return true;
					}
				}
			}

			return false;
		}//*/
		
		@Override
		public String toString() {
			return "(" + this.m_startX + ", " + this.m_startY + ") to (" + this.m_endX + ", " + this.m_endY + ")";
		}
	}
	
	private class LineManager {
		
		final private ArrayList< Line > m_lines;
		
		public LineManager() {
			this.m_lines = new ArrayList< Line >();
		}
		
		public void addLine( Line l ) {
			this.m_lines.add( l );
		}
		
		/**
		 * @return		the largest x coordinate of all lines in this line manager
		 */
		public int getLargestX() {
			int largestX = 0;
			for ( Line l : this.m_lines ) {
				largestX = Math.max( largestX , l.m_startX );
				largestX = Math.max( largestX , l.m_endX );
			}
			return largestX;
		}
		
		/**
		 * @return		the largest y coordinate of all lines in this line manager
		 */
		public int getLargestY() {
			int largestY = 0;
			for ( Line l : this.m_lines ) {
				largestY = Math.max( largestY , l.m_startY );
				largestY = Math.max( largestY , l.m_endY );
			}
			return largestY;
		}
		
		public void removeLine( Line l ) {
			this.m_lines.remove( l );
		}
		
		/**
		 * removes all lines stored by this line manager that intersect
		 * with the given line 
		 * 
		 * @param l
		 * @rtn 				a line manager containing all the lines that
		 * 						were removed
		 */
		public LineManager removeIntersectionsWith( Line l ) {
			LineManager rtn = new LineManager();
			for ( int i=0 ; i<this.m_lines.size() ; i++ ) {
				if ( this.m_lines.get( i ).intersectsWith( l ) ) {
					rtn.addLine( this.m_lines.remove( i ) );
					
					//have to decrement because removing the line
					//shifts indices of all other lines after this one down by 1
					i--;
				}
			}
			return rtn;
		}
		
		public Iterable< Line > getLines() {
			return this.m_lines;
		}
	}
	
	/**
	 * provides a list of tools from which the user can select (e.g. draw color,
	 * draw thickness)
	 * 
	 * @author mjchao
	 *
	 */
	private class ToolsPanel extends JToolBar {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		final public Cursor TOOLS_PANEL_CURSOR = new Cursor( Cursor.DEFAULT_CURSOR );
		final public static int COLOR_ROWS = 2;
		final public static int COLOR_COLUMNS = 6;
		final public static int COLOR_BUTTON_PADDING = 3;
		final public Color[] COLOR_OPTIONS = { Color.black , Color.white , Color.gray , Color.lightGray , Color.darkGray , Color.red , Color.green , Color.blue , Color.orange , Color.yellow , Color.pink , Color.cyan };
		
		final public static int MAX_THICKNESS = 15;
		final public Integer[] THICKNESS_OPTIONS = { new Integer( 1 ) , new Integer( 3 ) , new Integer( 5 ) , new Integer( 10 ) , new Integer( MAX_THICKNESS ) };
		final public int[] EXTRA_ERASER_THICKNESS = { 			  0   ,				 2   , 				3   , 				5   , 					10 		   };
		
		/**
		 * width of buttons in this tool bar
		 */
		final public static int BUTTON_WIDTH = 16;
		
		/**
		 * height of buttons in this tool bar
		 */
		final public static int BUTTON_HEIGHT = 16;
		
		private SelectButtonGroup drawingTools;
			protected ToolBarSelectButton cmdPencil;
			protected ToolBarSelectButton cmdErase;
			
			private JButton cmdUndo;
			private JButton cmdRedo;
		
		private SelectButtonGroup drawingColors;
		
		private JPanel pnlSelectThickness;
			protected JComboBox cboSelectThickness;
		
		public ToolsPanel() {
			WhiteboardActivity.this.addRequestBringToFrontListener( this , WhiteboardActivity.this );
			
			setLayout( new FlowLayout( FlowLayout.CENTER ) );
			
			JPanel pnlContainer = new JPanel( new GridBagLayout() );
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.VERTICAL;
			
			this.drawingTools = new SelectButtonGroup( Text.Room.WhiteboardActivity.TOOLS_TOOLBAR_DESCRIPTION );
				createAndAddPencilButton();
				createAndAddEraseButton();
				createAndAddUndoButton();
				createAndAddRedoButton();
			c.fill = GridBagConstraints.VERTICAL;
			pnlContainer.add( this.drawingTools , c );
			
			this.drawingColors = new SelectButtonGroup( Text.Room.WhiteboardActivity.COLORS_TOOLBAR_DESCRIPTION , COLOR_ROWS , COLOR_COLUMNS , COLOR_BUTTON_PADDING , COLOR_BUTTON_PADDING );
			createAndAddColors();
			pnlContainer.add( this.drawingColors , c );
			
			this.pnlSelectThickness = new JPanel( new FlowLayout( FlowLayout.CENTER ) );
			this.pnlSelectThickness.setBorder( BorderFactory.createTitledBorder( Text.Room.WhiteboardActivity.THICKNESS_TOOLBAR_DESCRIPTION ) );
			createAndAddLineThicknesses();
			pnlContainer.add( this.pnlSelectThickness , c );
			add( pnlContainer );
			
			this.addMouseMotionListener( new MouseAdapter() {
				
				@Override
				public void mouseMoved( MouseEvent e ) {
					setCursor( ToolsPanel.this.TOOLS_PANEL_CURSOR );
				}
			});
		}
		
		private void createAndAddPencilButton() {
			
			//draw icon credits: Brainleaf
			//http://www.iconarchive.com/show/free-pencil-icons-by-brainleaf/pencil-yellow-icon.html
			ImageIcon pencilIcon = new ImageIcon( "res/Activities/Whiteboard/pen.png" );
			this.cmdPencil = new ToolBarSelectButton( pencilIcon , BUTTON_WIDTH , BUTTON_HEIGHT );
			this.drawingTools.add( this.cmdPencil );
			WhiteboardActivity.this.addRequestBringToFrontListener( this.cmdPencil , WhiteboardActivity.this );
			addLocalKeystrokeListenerTo( this.cmdPencil );
		}
		
		private void createAndAddEraseButton() {
			
			//erase icon credits:
			//http://p.yusukekamiyamane.com/
			ImageIcon eraserIcon = new ImageIcon( "res/Activities/Whiteboard/eraser.png" );
			this.cmdErase = new ToolBarSelectButton( eraserIcon , BUTTON_WIDTH , BUTTON_HEIGHT );
			this.drawingTools.add( this.cmdErase );
			WhiteboardActivity.this.addRequestBringToFrontListener( this.cmdErase, WhiteboardActivity.this );
			addLocalKeystrokeListenerTo( this.cmdErase );
		}
		
		private void createAndAddColors() {
			for ( Color c : this.COLOR_OPTIONS ) {
				ToolBarSelectButton colorToAdd = new ToolBarSelectButton( BUTTON_WIDTH , BUTTON_HEIGHT );
				colorToAdd.setBackground( c );
				colorToAdd.setOpaque( true );
				this.drawingColors.add( colorToAdd );
				WhiteboardActivity.this.addRequestBringToFrontListener( colorToAdd , WhiteboardActivity.this );
				colorToAdd.addActionListener( new ActionListener() {

					@Override
					public void actionPerformed( ActionEvent e ) {
						ToolsPanel.this.cboSelectThickness.repaint();
					}
				});
				addLocalKeystrokeListenerTo( colorToAdd );
			}
		}
		
		private void createAndAddLineThicknesses() {
			this.cboSelectThickness = new JComboBox( this.THICKNESS_OPTIONS );
			this.cboSelectThickness.setPreferredSize( new Dimension( LineThicknessSample.PANEL_WIDTH , LineThicknessSample.PANEL_HEIGHT ) );
			this.cboSelectThickness.setRenderer( new LineThicknessSample() );
			this.cboSelectThickness.addPopupMenuListener( new PopupMenuListener() {

				@Override
				public void popupMenuCanceled(PopupMenuEvent arg0) {
					//ignore
				}

				@Override
				public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0) {
					//ignore
				}

				@Override
				public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {
					WhiteboardActivity.this.requestBringToFrontWithoutLosingFocus();
				}
				
			});
			addLocalKeystrokeListenerTo( this.cboSelectThickness );
			this.pnlSelectThickness.add( this.cboSelectThickness );
		}
		
		private void createAndAddUndoButton() {
			ImageIcon undoIcon = new ImageIcon( "res/Activities/Whiteboard/undo.png" );
			Image scaledImage = undoIcon.getImage().getScaledInstance( BUTTON_WIDTH , BUTTON_HEIGHT , Image.SCALE_SMOOTH );
			this.cmdUndo = new JButton( new ImageIcon( scaledImage ) );
			ActionListener l = new ActionListener() {

				@Override
				public void actionPerformed( ActionEvent e ) {
					WhiteboardActivity.this.pnlCanvas.requestUndo();
				}
				
			};
			this.cmdUndo.addActionListener( l );
			WhiteboardActivity.this.addRequestBringToFrontListener( this.cmdUndo , WhiteboardActivity.this );
			this.drawingTools.add( this.cmdUndo );
			addLocalKeystrokeListenerTo( this.cmdUndo );
		}
		
		private void createAndAddRedoButton() {
			ImageIcon redoIcon = new ImageIcon( "res/Activities/Whiteboard/redo.png" );
			Image scaledImage = redoIcon.getImage().getScaledInstance( BUTTON_WIDTH , BUTTON_HEIGHT , Image.SCALE_SMOOTH );
			this.cmdRedo = new JButton( new ImageIcon( scaledImage ) );
			this.cmdRedo.addActionListener( new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					WhiteboardActivity.this.pnlCanvas.requestRedo();
				}
			});
			WhiteboardActivity.this.addRequestBringToFrontListener( this.cmdRedo , WhiteboardActivity.this );
			addLocalKeystrokeListenerTo( this.cmdRedo );
			this.drawingTools.add( this.cmdRedo );
		}
			
			/**
			 * displays a sample line thickness for varying pixel width line
			 * thicknesses 
			 * 
			 * @author mjchao
			 *
			 */
			private class LineThicknessSample extends JPanel implements ListCellRenderer {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;
				
				final public static int LINE_WIDTH = 25;
				final public static int PANEL_WIDTH = 50 + LINE_WIDTH;
				final public static int PANEL_HEIGHT = MAX_THICKNESS+10;
				
				final public static int START_X = 5;
				final public static int START_Y = 10;
				
				private int m_currentThickness = 0;
				
				public LineThicknessSample() {

				}

				public void setSampleThickness( int lineThickness ) {
					this.m_currentThickness = lineThickness;
				}
				
				@Override
				public void paintComponent( Graphics g ) {
					Graphics2D g2d = ( Graphics2D ) g;
					g2d.setColor( ToolsPanel.this.getSelectedColor() );
					g2d.setStroke( new BasicStroke( this.m_currentThickness ) );
					g2d.drawLine( START_X , START_Y , START_X+LINE_WIDTH , START_Y );
				
				}
				
				@Override
				public Dimension getPreferredSize() {
					return new Dimension( PANEL_WIDTH , PANEL_HEIGHT );
				}
				
				@Override
				public Component getListCellRendererComponent( JList list , Object value , int index , boolean isSelected , boolean cellHasFocus ) {
					setSampleThickness( ( (Integer) value ).intValue() );
					return this;
				}
			}
		
		/**
		 * @return			if the user selected the pencil tool
		 */
		public boolean isPencilDrawing() {
			return this.cmdPencil.isSelected();
		}
		
		/**
		 * @return			if the user selected the eraser tool
		 */
		public boolean isErasing() {
			return this.cmdErase.isSelected();
		}
		
		/**
		 * @return			the currently selected color
		 */
		public Color getSelectedColor() {
			ToolBarSelectButton selectedColor = this.drawingColors.getSelected();
			if ( selectedColor != null ) {
				return this.drawingColors.getSelected().getBackground();
			} 
			else {
				
				//by default, return the standard black color
				//this block should never execute, but it's here for safety
				return Color.black;
			}
		}
		
		/**
		 * @return		the currently selected line thickness
		 */
		public int getThickness() {
			int selectedThickness = ( (Integer) this.cboSelectThickness.getSelectedItem() ).intValue();
			if ( this.isPencilDrawing() ) {
				return selectedThickness;
			} 
			else if ( this.isErasing() ) {
				return selectedThickness + this.EXTRA_ERASER_THICKNESS[ this.cboSelectThickness.getSelectedIndex() ];
			}
			else {
				return selectedThickness;
			}
		}
	}
	
	/**
	 * an undo/redo action
	 * 
	 * @author mjchao
	 *
	 */
	protected class Action {
		final public static String DRAW_ACTION = "Draw";
		final public static String ERASE_ACTION = "Erase";
		
		final private ArrayList< ActionComponent > actionComponents;
		
		public Action() {
			this.actionComponents = new ArrayList< ActionComponent >();
		}
		
		public void addLine( String actionType , Line l ) {
			ActionComponent componentToAdd = new ActionComponent( actionType , l );
			this.actionComponents.add( componentToAdd );
		}
		
		public Iterable< ActionComponent > getActionComponents() {
			return this.actionComponents;
		}
	}
	
	/**
	 * a line segment that is a component of an action
	 * 
	 * @author mjchao
	 *
	 */
	protected class ActionComponent {
		
		final public String m_actionType;
		final public Line m_line;
		/**
		 * 
		 * @param actionType			the type of action (e.g. draw, erase)
		 * @param l						the line associated with this action component
		 */
		public ActionComponent( String actionType , Line l ) {
			this.m_actionType = actionType;
			this.m_line = l;
		}
	}
	
	private class ActionManager {
	
		private Stack< Action > m_actions;
		
		public ActionManager() {
			this.m_actions = new Stack< Action >();
		}
		
		/**
		 * removes all actions associated with this action manager
		 */
		public void clear() {
			this.m_actions.clear();
		}
		
		/**
		 * adds the given action to this action manager
		 * 
		 * @param a				the action to add
		 */
		public void addLastAction( Action a ) {
			this.m_actions.push( a );
		}
		
		/**
		 * @return			if there are more actions contained in this action manager
		 */
		public boolean hasMoreActions() {
			return this.m_actions.isEmpty() == false;
		}
		
		/**
		 * @return			the last action on in this action manager
		 */
		public Action popLastAction() {
			return this.m_actions.pop();
		}
	}
}
