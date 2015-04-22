package mc.iwclient.messaging;

/**
 * a user list that provides filtering options for filtering out
 * specific users
 * 
 * @author mjchao
 *
 */
public class FilterableUserGraphicList extends UserGraphicList {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FilterableUserGraphicList() {
		
	}
	
	/**
	 * filters the users in the list using the given phrase and updates
	 * the graphics of the list
	 * 
	 * @param phrase			phrase with which to filter
	 */
	public void filter( String phrase ) {
		for ( int i=0 ; i<this.getListSize() ; i++ ) {
			GraphicListElement currentElement = super.get( i );
			if ( super.getGraphics( i ).getDisplayName().contains( phrase )) {
				currentElement.setVisible( true );
			}
			else {
				currentElement.setVisible( false );
			}
		}
		super.refresh();
	}
	
	/**
	 * removes any filters from the list
	 */
	public void removeFilter() {
		//packing the main frame without making any changes to the frame
		//will result in disappearing scrollbars, which we must avoid
		boolean needsRefresh = false;
		for ( int i=0 ; i<this.getListSize() ; i++ ) {
			GraphicListElement currentElement = super.get( i );
			if ( currentElement.isVisible() == false ) {
				needsRefresh = true;
			}
			currentElement.setVisible( true );
		}
		if ( needsRefresh ) {
			super.refresh();
		}
	}
}