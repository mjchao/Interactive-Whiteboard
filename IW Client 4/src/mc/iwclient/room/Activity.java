package mc.iwclient.room;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EtchedBorder;

import mc.iwclient.util.Text;

/**
 * A panel that can be dynamically resized and dragged around in a parent
 * container. It can also contain multiple activities as children
 * 
 * @author mjchao
 *
 */
//TODO bring to front, send to back
public class Activity extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String m_activityName;
	
	int m_mousePressedX;
	
	int m_mousePressedY;
	
	boolean m_isDraggable = true;
	
	boolean m_isResizable = true;
	
	final public static Cursor DEFAULT_CURSOR = new Cursor( Cursor.DEFAULT_CURSOR );

	final public static int MINIMUM_ACTIVITY_WIDTH = 100;
	
	final public static int MINIMUM_ACTIVITY_HEIGHT = 100;
	
	/**
	 * an extra border around activities to ensure that they can still
	 * be resized when the main frame is maximizes
	 */
	final public static int EXTRA_ACTIVITY_RESIZE_SPACE = 10;
	
	//Inside offset is how much the cursor can be inside the activity panel (in pixels)
	
	//north has a minimum and maximum offset because the panel title
	//takes up extra space north of the panel border outline
	//and we only want the user to be able to resize by dragging the panel
	//outline
	final public static int RESIZE_NORTH_MAXIMUM_OUTSIDE_OFFSET = 3;
	final public static int RESIZE_NORTH_MAXIMUM_INSIDE_OFFSET = 3;
	
	final public static int RESIZE_SOUTH_INSIDE_OFFSET = 5;
	
	final public static int RESIZE_EAST_INSIDE_OFFSET = 5;
	
	final public static int RESIZE_WEST_INSIDE_OFFSET = 5;
	
	ArrayList< Activity > m_childrenActivities;
	
	/**
	 * creates a default activity by setting up the resize and drag functionality.
	 */
	private Activity() {
		this.m_childrenActivities = new ArrayList< Activity >();
		setLayout( null );
		
		Activity.this.addRequestBringToFrontListener( this , this );
		this.addMouseListener( new MouseAdapter() {
			
			@Override
			public void mousePressed( MouseEvent e ) {
				Activity.this.m_mousePressedX = e.getX();
				Activity.this.m_mousePressedY = e.getY();
			}
		});
		
		this.addMouseMotionListener( new MouseAdapter() {
			
			@Override
			public void mouseMoved( MouseEvent e ) {
				
				setCursor( DEFAULT_CURSOR );
				
				//check for resize, and if we should display resize cursor
				if ( Activity.this.m_isResizable ) {
					boolean northResize = Activity.this.isWithinNorthResizeBounds( e.getY() );
					boolean southResize = Activity.this.isWithinSouthResizeBounds( e.getY() );
					boolean eastResize = Activity.this.isWithinEastResizeBounds( e.getX() );
					boolean westResize = Activity.this.isWithinWestResizeBounds( e.getX() );
	
					Cursor resizeCursor = DEFAULT_CURSOR;
					if ( ( eastResize || westResize ) && ( northResize || southResize ) ) {
						
						if ( northResize && eastResize ) {
							resizeCursor = new Cursor( Cursor.NE_RESIZE_CURSOR );
						}
						else if ( southResize && eastResize ) {
							resizeCursor = new Cursor( Cursor.SE_RESIZE_CURSOR );
						}
						else if ( northResize && westResize ) {
							resizeCursor = new Cursor( Cursor.NW_RESIZE_CURSOR );
						}
						else {
							resizeCursor = new Cursor( Cursor.SW_RESIZE_CURSOR );
						}
					}
					else {
						if ( northResize || southResize ) {
							resizeCursor = new Cursor( Cursor.N_RESIZE_CURSOR );
						}
						if ( eastResize || westResize ) {
							resizeCursor = new Cursor( Cursor.E_RESIZE_CURSOR );
						}
					}
					setCursor( resizeCursor );
				}
				
				if ( Activity.this.m_isDraggable ) {
					if ( Activity.this.isWithinDraggableBounds( e.getX() , e.getY() ) ) {
						setCursor( new Cursor( Cursor.HAND_CURSOR ) );
					}
				}
			}
			
			@Override
			public void mouseDragged( MouseEvent e ) {
				
				//check for resize
				boolean resized = false;
				if ( Activity.this.m_isResizable ) {
					int deltaX = 0;
					int deltaY = 0;
					int deltaWidth = 0;
					int deltaHeight = 0;
					
					if ( Activity.this.isWithinNorthResizeBounds( Activity.this.m_mousePressedY ) ) {
						deltaY = ( e.getY()-Activity.this.m_mousePressedY );
						deltaHeight = -(e.getY()-Activity.this.m_mousePressedY );
					}
					if ( Activity.this.isWithinSouthResizeBounds( Activity.this.m_mousePressedY ) ) {
						deltaHeight = ( e.getY()-Activity.this.m_mousePressedY );
					}
					if ( Activity.this.isWithinEastResizeBounds( Activity.this.m_mousePressedX ) ) {
						deltaWidth = ( e.getX()-Activity.this.m_mousePressedX );
					}
					if ( Activity.this.isWithinWestResizeBounds( Activity.this.m_mousePressedX ) ) {
						deltaX = ( e.getX()-Activity.this.m_mousePressedX );
						deltaWidth = -( e.getX()-Activity.this.m_mousePressedX );
					} 
					
					if ( deltaWidth != 0 || deltaHeight != 0 ) {
						resized = true;
						
						//prevent the user from making the Activity too small
						int newWidth = Activity.this.getWidth() + deltaWidth;
						int newHeight = Activity.this.getHeight() + deltaHeight;
						if ( newWidth < MINIMUM_ACTIVITY_WIDTH ) {
							
							//if the x coordinate is changing, then move the panel
							//as far horizontally as it can without eating into the minimum
							//activity width
							if ( deltaX != 0 ) {
								deltaX = ( Activity.this.getWidth()-MINIMUM_ACTIVITY_WIDTH );
							}
							
							//prevent the width from getting too small
							newWidth = MINIMUM_ACTIVITY_WIDTH;
						} else{
							
							//a necessary update to the mouse listener
							//if we are resizing on the east border
							if ( isWithinEastResizeBounds( Activity.this.m_mousePressedX ) ) {
								Activity.this.m_mousePressedX = e.getX();
							}
						}
						if ( newHeight < MINIMUM_ACTIVITY_HEIGHT ) {
							
							//if the y coordinate is changing, then move the panel as far
							//vertically as it can without eating into the minimum activity
							//height
							if ( deltaY != 0 ) {
								deltaY = ( Activity.this.getHeight()-MINIMUM_ACTIVITY_HEIGHT );
							}
							
							//prevent the height from getting too small
							newHeight = MINIMUM_ACTIVITY_HEIGHT;
						} else {
							
							//a necessary update to the mouse listener
							//if we are resizing on the south border
							if ( isWithinSouthResizeBounds( Activity.this.m_mousePressedY ) ) {
								Activity.this.m_mousePressedY = e.getY();
							}
						}
						int newX = Activity.this.getLocation().x + deltaX;
						int newY = Activity.this.getLocation().y + deltaY;
						
						//update this Activity's size
						requestResize( newX , newY , newWidth , newHeight );
					}
				}

				//we don't want to drag and resize at the same time
				//note that when the user drags, s/he is able to move the cursor
				//into the draggable area, and thus potentially trigger a 
				//drag event
				if ( !resized ) {
					if ( isWithinDraggableBounds( Activity.this.m_mousePressedX , Activity.this.m_mousePressedY ) ) {
						if ( Activity.this.m_isDraggable ) {
							requestDrag( e );
						}
					}
				}
			}
			
			/**
			 * sends a request to the server to resize the activity with the
			 * given constraints
			 * 
			 * @param newX
			 * @param newY
			 * @param newWidth
			 * @param newHeight
			 */
			private void requestResize( int newX , int newY , int newWidth , int newHeight ) {
				resize( newX , newY , newWidth , newHeight );
			}
			
			/**
			 * resizes this activity to the given constraints
			 * 
			 * @param newX
			 * @param newY
			 * @param newWidth
			 * @param newHeight
			 */
			public void resize( int newX , int newY , int newWidth , int newHeight ) {
				Activity.this.setBounds( newX , newY , newWidth , newHeight );
				Activity.this.revalidate();
				Activity.this.repaint();
				
				//make sure the parent activity still contains this activity
				Activity.this.resizeParentActivity();
			}
			
			/**
			 * sends a request to drag this activity to the server
			 * 
			 * @param e
			 */
			private void requestDrag( MouseEvent e ) {
				//TODO
				drag( e );
			}
			/**
			 * moves the panel, assuming the user means to drag it
			 * 
			 * @param e			the user's drag mouse event
			 */
			private void drag( MouseEvent e ) {
				int newX = Activity.this.getLocation().x + ( e.getX()-Activity.this.m_mousePressedX );
				
				//prevent user from dragging activity off the screen
				if ( newX < 0 ) {
					newX = 0;
				}
				
				int newY = Activity.this.getLocation().y + ( e.getY()-Activity.this.m_mousePressedY );
				
				//prevent user from dragging activity off the screen
				if ( newY < 0 ) {
					newY = 0;
				}
				if ( newX >= 0 && newY >= 0 ) {
					Activity.this.setLocation( newX , newY );
				}
				
				//make sure the parent activity still contains this activity
				Activity.this.resizeParentActivity();
			}
		});
	}
	
	/**
	 * creates an activity with the specified activity name
	 * 
	 * @param activityName			the name of the activity
	 */
	private Activity( String activityName ) {
		this();
		setActivityName( activityName );
	}
	
	/**
	 * creates an activity with the specified activity name
	 * and optionally hides the activity name from the title of this
	 * activty panel
	 * 
	 * @param activityName			the name of the activity
	 * @param isNameVisible			whether or not to hide the name of the activity
	 * 								in the title of the activity panel
	 */
	public Activity( String activityName , boolean isNameVisible ) {
		this( activityName );
		if ( !isNameVisible ) {
			this.setBorder( null );
		}
	}
	
	@SuppressWarnings("static-method")
	//TODO cannot make static right now b/c networking in the future!
	final public void addRequestBringToFrontListener( Component c , Activity a ) {
		c.addMouseListener( new RequestBringToFrontListener( a ) );
	}
	
	@Override
	public Dimension getPreferredSize() {
		int preferredWidth = Math.max( MINIMUM_ACTIVITY_WIDTH , super.getPreferredSize().width );
		int preferredHeight = Math.max( MINIMUM_ACTIVITY_HEIGHT , super.getPreferredSize().height );
		return new Dimension( preferredWidth , preferredHeight );
	}
	
	@Override
	public boolean isOptimizedDrawingEnabled() {
		return false;
	}
	
	/**
	 * sets the name of this activity and displays it as the title of this Activity
	 * 
	 * @param activityName			the name of this activity
	 */
	public void setActivityName( String activityName ) {
		Border resizeBorder = BorderFactory.createEtchedBorder( EtchedBorder.LOWERED );
		Border titleBorder = BorderFactory.createTitledBorder( BorderFactory.createEmptyBorder() , activityName );
		this.setBorder( new CompoundBorder( resizeBorder , titleBorder ) );
		this.m_activityName = activityName;
	}
	
	/**
	 * @return		the name of this activity
	 */
	public String getActivityName() {
		return this.m_activityName;
	}
	
	/**
	 * adds a child activity to this Activity panel
	 * 
	 * @param x 		x coordinate of where to place the child activity in this activity panel
	 * @param y 		y coordinate of where to place the child activity in this activity panel
	 * @param a			the child activity to add
	 */
	public void addChildActivity( int x , int y , Activity a ) {
		this.m_childrenActivities.add( a );
		a.setBounds( x , y , a.getPreferredSize().width , a.getPreferredSize().height );
		a.setComponentPopupMenu( new ActivityPopupMenu( a ) );
		this.add( a );
		this.revalidate();
		this.repaint();
	}
	
	/**
	 * ensures that all child activities contained by this activity can be seen
	 * by the user (or provides scrollbars if necessary)
	 */
	public void ensureContainsChildActivities() {
		
		int largestXCoordinate = 0;
		int largestYCoordinate = 0;
		for ( Activity childActivity : this.m_childrenActivities ) {
			int childLargestXCoordinate = childActivity.getLocation().x + childActivity.getWidth();
			int childLargestYCoordinate = childActivity.getLocation().y + childActivity.getHeight();
			largestXCoordinate = Math.max( largestXCoordinate , childLargestXCoordinate );
			largestYCoordinate = Math.max( largestYCoordinate , childLargestYCoordinate );
		}
		this.setPreferredSize( new Dimension( largestXCoordinate+EXTRA_ACTIVITY_RESIZE_SPACE , largestYCoordinate+EXTRA_ACTIVITY_RESIZE_SPACE ) );
	}
	
	/**
	 * makes sure that the parent of this activity contains
	 * this activity completely
	 */
	public void resizeParentActivity() {
		if ( this.getParent() != null ) {
			if ( this.getParent() instanceof Activity ) {
				Activity parentActivity = ( Activity ) this.getParent();
				parentActivity.ensureContainsChildActivities();
				parentActivity.revalidate();
				parentActivity.repaint();
			}
		}
	}
	
	/**
	 * @param cursorY		y-coordinate location of the cursor
	 * @return				if the user has moved the cursor to the northern
	 * 						border of the activity panel, thus indicating that
	 * 						s/he would like to resize the panel
	 */
	@SuppressWarnings("static-method")
	public boolean isWithinNorthResizeBounds( int cursorY ) {
		return -1*RESIZE_NORTH_MAXIMUM_OUTSIDE_OFFSET <= cursorY && cursorY <= RESIZE_NORTH_MAXIMUM_INSIDE_OFFSET;
	}
	
	/**
	 * @param cursorY		y-coordinate location of the cursor
	 * @return				if the user has moved the cursor to the southern
	 * 						border of the activity panel, thus indicating that
	 * 						s/he would like to resize the panel
	 */
	public boolean isWithinSouthResizeBounds( int cursorY ) {
		return cursorY >= getHeight()-RESIZE_SOUTH_INSIDE_OFFSET;
	}
	
	/**
	 * @param cursorX		x-coordinate location of the cursor
	 * @return				if the user has moved the cursor to the eastern
	 * 						border of the activity panel, thus indicating that
	 * 						s/he would like to resize the panel
	 */
	public boolean isWithinEastResizeBounds( int cursorX ) {
		return cursorX >= getWidth()-RESIZE_EAST_INSIDE_OFFSET;
	}
	
	/**	
	 * @param cursorX		x coordinate of the cursor
	 * @return				if the user has moved the cursor to the western
	 * 						border of the activity panel, thus indicating that
	 * 						s/he would like to resize the panel
	 */	
	@SuppressWarnings("static-method")
	public boolean isWithinWestResizeBounds( int cursorX ) {
		return cursorX <= RESIZE_WEST_INSIDE_OFFSET;
	}
	
	/**
	 * @param cursorX		x-coordinate of the cursor
	 * @param cursorY		y-coordinate of the cursor
	 * @return				if the user has moved the cursor to a location
	 * 						indicating that the panel should be dragged
	 */
	public boolean isWithinDraggableBounds( int cursorX , int cursorY ) {
		return !isWithinNorthResizeBounds( cursorY ) && !isWithinSouthResizeBounds( cursorY ) &&
				!isWithinEastResizeBounds( cursorX ) && !isWithinWestResizeBounds( cursorX );
	}

	/**
	 * sets whether this Activity can be dragged around on the screen
	 * 
	 * @param b
	 */
	public void setDraggable( boolean b ) {
		this.m_isDraggable = b;
	}
	
	/**
	 * sets whether this Activity can be resized
	 * 
	 * @param b
	 */
	public void setResizable( boolean b ) {
		this.m_isResizable = b;
	}
	
	/**
	 * brings the specified child activity in front of all other child activities
	 * 
	 * @param childActivity			the activity to bring to the front
	 */
	protected void bringChildToFront( Activity childActivity ) {
		if ( this.m_childrenActivities.contains( childActivity ) ) {
			this.setComponentZOrder( childActivity , 0 );
			revalidate();
			repaint();
		}
	}
	
	/**
	 * requests to the server that this activity be brought in front
	 * of all sibling activities
	 */
	public void requestBringToFront() {
		//TODO networking
		bringToFront();
	}
	
	/**
	 * brings this activity in front of all other sibling activities
	 */
	public void bringToFront() {
		if ( this.getParent() instanceof Activity ) {
			Activity parent = (Activity) this.getParent();
			parent.bringChildToFront( this );
			parent.revalidate();
			parent.repaint();
		}
	}
	
	/**
	 * requests that the server delete this activity
	 */
	public void requestDelete( Activity childActivity ) {
		//TODO
		delete( childActivity );
	}
	
	/**
	 * closes any resources this activity might be using
	 */
	public void closeResources() {
		//TODO
		for ( Activity child : this.m_childrenActivities ) {
			child.closeResources();
		}
	}
	
	/**
	 * deletes the given child activity from this activity
	 * 
	 * @param childActivity			the child activity to delete
	 */
	public void delete( Activity childActivity ) {
		childActivity.closeResources();
		this.m_childrenActivities.remove( childActivity );
		this.remove( childActivity );
		System.out.println( "deleted" );
		revalidate();
		repaint();
	}
	
	/**
	 * popup menu that allows the user to change the z-order of the children
	 * activities of this activity (i.e. bring child activities to the front
	 * or send them to the back), and other operations associated with this
	 * activity
	 * @author mjchao
	 *
	 */
	private class ActivityPopupMenu extends JPopupMenu {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		final protected Activity m_mainGui;
		
		final private JMenuItem itmBringToFront;
		
		final private JMenuItem itmSendToBack;
		
		final private JMenuItem itmDelete;
		
		public ActivityPopupMenu( Activity mainGui ) {
			this.m_mainGui = mainGui;
			this.itmBringToFront = new JMenuItem( Text.Room.Activity.BRING_TO_FRONT_COMMAND );
			add( this.itmBringToFront );
			this.itmSendToBack = new JMenuItem( Text.Room.Activity.SEND_TO_BACK_COMMAND );
			add( this.itmSendToBack );
			this.itmDelete = new JMenuItem( Text.Room.Activity.DELETE_COMMAND );
			add( this.itmDelete );
			
			ActionListener l = new ActionListener() {

				@Override
				public void actionPerformed( ActionEvent e ) {
					String command = e.getActionCommand();
					if ( command.equals( Text.Room.Activity.BRING_TO_FRONT_COMMAND ) ) {
						requestBringToFront();
					}
					else if ( command.equals( Text.Room.Activity.SEND_TO_BACK_COMMAND ) ) {
						requestSendToBack();
					}
					else if ( command.equals( Text.Room.Activity.DELETE_COMMAND ) ){
						delete();
					}
				}
			};
			this.itmBringToFront.addActionListener( l );
			this.itmSendToBack.addActionListener( l );
			this.itmDelete.addActionListener( l );
		}
		
		/**
		 * requests the server bring the activity associated with this popup menu
		 * in front of all other sibling activities
		 */
		public void requestBringToFront() {
			//TODO
			bringToFront();
		}
		
		/**
		 * brings the Activity associated with this popup menu in front of all other
		 * sibling Activities
		 */
		public void bringToFront() {
			Activity.this.setComponentZOrder( this.m_mainGui , 0 );
			Activity.this.revalidate();
			Activity.this.repaint();
		}
		
		/**
		 * requests the server send the activity associated with this popup menu
		 * behind all other sibling activities
		 */
		public void requestSendToBack() {
			//TODO
			sendToBack();
		}
		
		/**
		 * sends the Activity associated with this popup menu behind all other
		 * sibling Activities
		 */
		public void sendToBack() {
			Activity.this.setComponentZOrder( this.m_mainGui , Activity.this.m_childrenActivities.size()-1 );
			Activity.this.revalidate();
			Activity.this.repaint();
		}
		
		public void delete() {
			Activity.this.requestDelete( this.m_mainGui );
		}
	}
	
	private static class RequestBringToFrontListener extends MouseAdapter {
		
		final private Activity m_gui;
		/**
		 * creates a request bring to front listener that brings the specified 
		 * activity to the front whenever components to which this listener
		 * is registered are pressed by the mouse
		 * 
		 * @param a
		 */
		public RequestBringToFrontListener( Activity a ) {
			this.m_gui = a;
		}
		
		@Override
		public void mousePressed( MouseEvent e ) {
			this.m_gui.requestBringToFront();
		}
	}
}
