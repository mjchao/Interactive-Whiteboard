package mc.iwclient.messaging;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Comparator;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import mc.iwclient.util.Text;

abstract public class SearchableGraphicList extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	final public static int MINIMUM_SEARCH_FILTER_SIZE = 1;
	
	/**
	 * comparator used for sorting lists by strings
	 */
	final public static Comparator< String > STRING_COMPARATOR = new Comparator< String >() {

		@Override
		public int compare(String o1, String o2) {
			return o1.compareTo( o2 );
		}
		
	};
	
	final protected SearchPanel pnlSearch;
	
	final private JPanel pnlSearchDisplay;
	
	
	public SearchableGraphicList() {
		setLayout( new BorderLayout() );
		
		this.pnlSearch = createCustomSearchPanel();
		this.pnlSearch.addSearchPanelListener( new SearchPanelListener( this.pnlSearch ) );
		add( this.pnlSearch , BorderLayout.NORTH );
		
		this.pnlSearchDisplay = new JPanel();
		this.pnlSearchDisplay.setLayout( new GridLayout( 1 , 1 ) );
		add( new JScrollPane( this.pnlSearchDisplay ) , BorderLayout.CENTER );
	}
	
	/**
	 * allows the user to create a custom search panel for this searchable graphic
	 * list. by default, this method creates a default-built search panel
	 * and adds a default SearchPanelListener
	 * 
	 * @return				the search panel for this searchable graphic list
	 */
	protected SearchPanel createCustomSearchPanel() {
		SearchPanel rtn = new SearchPanel();
		rtn.addSearchPanelListener( new SearchPanelListener( rtn ) );
		return rtn;
	}
	
	/**
	 * loads the given graphical component in the main display panel of this 
	 * user interface
	 * 
	 * @param c				the graphical component to display
	 */
	protected void loadInDisplay( Component c ) {
		this.pnlSearchDisplay.removeAll();
		this.pnlSearchDisplay.add( c );
		this.pnlSearchDisplay.revalidate();
		this.pnlSearchDisplay.repaint();
	}
	
	/**
	 * panel in which the user searches for specific users
	 * 
	 * @author mjchao
	 *
	 */
	protected class SearchPanel extends JPanel {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		
		
		protected JPanel pnlSearchFilter;
			protected JTextField txtSearchFilter;
			protected JButton cmdSearch;
			protected JButton cmdCancel;
		
		public SearchPanel() {
			
			setLayout( new GridLayout( 0 , 1 ) );
			setupSearchPanel();
		}
		
		/**
		 * allows for any user modifications of the search panel before the 
		 * search filter panel is added. by default, this method sets up
		 * a default search panel with a search field and search and cancel
		 * commands
		 */
		protected void setupSearchPanel() {
			this.pnlSearchFilter = new JPanel( new FlowLayout( FlowLayout.CENTER ) );
			this.txtSearchFilter = new JTextField( 10 );
			this.pnlSearchFilter.add( this.txtSearchFilter );
			
			this.cmdSearch = new JButton( Text.Messaging.SearchableGraphicList.SEARCH_COMMAND );
			this.pnlSearchFilter.add( this.cmdSearch );
			
			this.cmdCancel = new JButton( Text.Messaging.SearchableGraphicList.CANCEL_SEARCH_COMMAND );
			this.pnlSearchFilter.add( this.cmdCancel );
			add( this.pnlSearchFilter );
		}
		
		public void addSearchPanelListener( SearchPanelListener l ) {
			
			//check to make sure the search and cancel components exist,
			//as we may have overriden the standard setup, and thus
			//not created those buttons
			if ( this.cmdSearch != null ) {
				this.cmdSearch.addActionListener( l );
			}
			if ( this.cmdCancel != null ) {
				this.cmdCancel.addActionListener( l );
			}
		}
		
		/**
		 * @return			the phrase by which to filter search results
		 */
		public String getSearchFilter() {
			return this.txtSearchFilter.getText();
		}
	}
	
	/**
	 * @return			returns the search panel of this SearchableGraphicList
	 * 					but should only be used to get certain propertiest
	 * 					and should not be used to modify the search panel
	 */
	protected SearchPanel getSearchPanel() {
		return this.pnlSearch;
	}
	
	protected class SearchPanelListener implements ActionListener {
		
		final protected SearchPanel m_gui;
		
		public SearchPanelListener( SearchPanel gui ) {
			this.m_gui = gui;
		}

		@Override
		public void actionPerformed( ActionEvent e ) {
			String command = e.getActionCommand();
			if ( command.equals( Text.Messaging.SearchableGraphicList.SEARCH_COMMAND ) ) {
				handleSearchCommand( this.m_gui );
			}
			else if ( command.equals( Text.Messaging.SearchableGraphicList.CANCEL_SEARCH_COMMAND ) ) {
				handleCancelSearchCommand( this.m_gui );
			}
		}
	}
	
	/**
	 * processes the user's request to search for specific things in this
	 * searchable graphic list
	 * 
	 * @param gui				the user interface to be updated, if necessary
	 */
	abstract protected void handleSearchCommand( SearchPanel gui );
	
	/**
	 * processes the user's request to cancel a search in this searchable graphic list
	 * @param gui
	 */
	abstract protected void handleCancelSearchCommand( SearchPanel gui );

}
