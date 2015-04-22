package mc.iwclient.uitemplates;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JPanel;

import mc.iwclient.messaging.UserGraphicCommandPanel;


/**
 * a vertically stacked list of elements and their graphical representations
 * 
 * @author mjchao
 *
 * @param <E>			the type of element in this list
 * @param <R>			the type of graphical representation of the element in this list
 */
public class GraphicList< E , R extends JComponent > extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	final public static int DEFAULT_MINIMUM_ROWS = 10;
	/**
	 * stores the elements in this list
	 */
	final private ArrayList< GraphicListElement > m_list = new ArrayList< GraphicListElement >();
	
	private int m_minRows = DEFAULT_MINIMUM_ROWS;
	
	/**
	 * creates a default graphic list with a default preferred component size
	 */
	public GraphicList() {
		this( DEFAULT_MINIMUM_ROWS );
	}
	
	/**
	 * creates a graphic list and attempts to make the graphical representations
	 * of all elements have the specified preferred size
	 * 
	 * @param minRows							minimum number of rows in this list
	 * 											which makes each entry look nicer
	 * 											of all the graphical elements in the list
	 */
	public GraphicList( int minRows ) {
		this.m_minRows = minRows;
		setLayout( new GridLayout( 0 , 1 ) );
	}
	
	/**
	 * creates a graphic list with default preferred component size and
	 * the specified initial data
	 * 
	 * @param data				data representing elements of the list
	 * @param graphics			graphical representations of elements of the list
	 */
	public GraphicList( E[] data , R[] graphics ) {
		this( DEFAULT_MINIMUM_ROWS , data , graphics );
	}
	
	/**
	 * creates a graphic list with the specified preferred component size
	 * and initial data
	 * 
	 * @param minRows						minimum number of rows in this list
	 * 										which makes each entry look nicer
	 * @param data							data representing elements of the list
	 * @param graphics						graphical representations of the elements of the list
	 */
	public GraphicList( int minRows , E[] data , R[] graphics ) {
		this.m_minRows = minRows;
		setLayout( new GridLayout( 0 , 1 ) );
		for ( int i=0 ; i<data.length ; i++ ) {
			addElementWithoutRefresh( data[ i ] , graphics[ i ] );
		}
	}
	
	/**
	 * @return			the number of elements in this list
	 */
	public int getListSize() {
		return this.m_list.size();
	}
	
	/**
	 * @param index
	 * @return			all the data and graphics associated with the element at
	 * 					the specified index
	 */
	protected GraphicListElement get( int index ) {
		return this.m_list.get( index );
	}
	
	/**
	 * @param index
	 * @return				data associated with the element at the specified index
	 */
	public E getData( int index ) {
		return this.m_list.get( index ).getData();
	}
	
	/**
	 * @param index
	 * @return				graphics associated with the elemnt at the specified index
	 */
	public R getGraphics( int index ) {
		return this.m_list.get( index ).getGraphics();
	}
	
	/**
	 * adds the specified element and its graphical representation to this list,
	 * but does not update the user interface to reflect these changes. use this
	 * method when adding many things in succession. 
	 * 
	 * @param data					the data representing the element
	 * @param graphics				the graphical representation of the element
	 */
	public void addElementWithoutRefresh( E data , R graphics ) {
		GraphicListElement elementToAdd = new GraphicListElement( data , graphics , true );
		this.m_list.add( elementToAdd );
		
		add( elementToAdd.getGraphics() );
	}
	
	/**
	 * adds an element already in this list to the graphical user interface, but
	 * does not redraw the user interface to udpate this new element
	 * 
	 * @param e			the element to show in the user interface
	 */
	protected void addElementWithoutRefresh( GraphicListElement e ) {
		add( e.getGraphics() );
	}
	
	/**
	 * adds the specified element and its graphical representation to this list
	 * and then updates the user interface to reflect these changes
	 * 
	 * @param data				data representing the element
	 * @param graphics			the graphical representation of the element
	 */
	public void addElement( E data , R graphics ) {
		addElementWithoutRefresh( data , graphics );
		refresh();
	}
	
	/**
	 * adds an element already in this list to the graphical user interface
	 * and redraws the user interface
	 * 
	 * @param e			the element to show in the user interface
	 */
	protected void addElement( GraphicListElement e ) {
		addElementWithoutRefresh( e );
		revalidate();
		repaint();
	}
	
	/**
	 * removes one element that corresponds to the given data identifier.
	 * this removes using a compare-by-value
	 * 
	 * @param data			data identifier for an element in the list
	 */
	public void remove( E data ) {
		for ( int i=0 ; i<this.m_list.size() ; i++ ) {
			if ( this.m_list.get( i ).getData().equals( data ) ) {
				this.m_list.remove( i );
				break;
			}
		}
		refresh();
	}
	
	/**
	 * removes one element that corresponds to the given graphic identifier.
	 * this removes using a compare-by-pointer
	 * 
	 * @param graphic		graphic identifying an element in this list
	 */
	public void remove( R graphic ) {
		for ( int i=0 ; i<this.m_list.size() ; i++ ) {
			if ( this.m_list.get( i ).getGraphics() == graphic ) {
				this.m_list.remove( i );
				break;
			}
		}
		refresh();
	}
	
	/**
	 * refreshes the graphics display by removing all the graphics and then
	 * adding them all again
	 */
	public void refresh() {
		this.removeAll();
		
		int rowsAdded = 0;
		for ( GraphicListElement e : this.m_list ) {
			if ( e.isVisible() ) {
				add( e.getGraphics() );
				rowsAdded++;
			}
		}

		int extraRowsToAdd = this.m_minRows;
		
		//if there are no elements in the list, we need to specify a minimum size
		//for each of our extra rows
		if ( rowsAdded == 0 ) {
			add( Box.createRigidArea( UserGraphicCommandPanel.MINIMUM_SIZE ) );
			extraRowsToAdd--;
		}

		//add extra rows to maintain the minimum number of rows in the list
		extraRowsToAdd -= rowsAdded;
		for ( int i=0; i<extraRowsToAdd ; i++ ) {
			add( Box.createHorizontalGlue() );
		}

		this.repaint();
		this.revalidate();
	}
	
	/**
	 * sorts all the elements in the list and refreshes the display
	 */
	public void sort( final Comparator< E > dataComparator ) {
		Comparator< GraphicListElement > listComparator = new Comparator< GraphicListElement >() {

			@Override
			public int compare( GraphicListElement o1 , GraphicListElement o2 ) {
				return dataComparator.compare( o1.getData() ,  o2.getData() );
			}
			
		};
		Collections.sort( this.m_list , listComparator );
		refresh();
	}
	
	/**
	 * represents an element in the list
	 * 
	 * @author mjchao
	 *
	 */
	protected class GraphicListElement {
		
		final private E m_elementData;
		
		final private R m_elementGraphics;
		
		protected boolean m_visible;
		
		/**
		 * creates a GraphicListElement that contains data representing the elements
		 * and graphics that visually represent the element to the user 
		 * 
		 * @param elementData				data representing the element
		 * @param elementGraphics			graphics representing the element
		 * @param visible					whether or not this element should be displayed in
		 * 									the list
		 */
		public GraphicListElement( E elementData , R elementGraphics , boolean visible ) {
			this.m_elementData = elementData;
			this.m_elementGraphics = elementGraphics;
			this.m_visible = visible;
		}
		
		/**
		 * @return			the data representation of this list element
		 */
		public E getData() {
			return this.m_elementData;
		}
		
		/**
		 * @return			the graphical representation of this list element
		 */
		public R getGraphics() {
			return this.m_elementGraphics;
		}
		
		/**
		 * @return			whether or not this element is displayed in the list
		 */
		public boolean isVisible() {
			return this.m_visible;
		}
		
		/**
		 * sets whether or not this element should be displayed in the list
		 * 
		 * @param visibility
		 */
		public void setVisible( boolean visibility ) {
			this.m_visible = visibility;
		}
	}
}
