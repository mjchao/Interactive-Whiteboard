package mc.iwclient.account;

import java.awt.GridLayout;

import javax.swing.JEditorPane;
import javax.swing.JPanel;

public class NewsPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * displays interactive whiteboard news for the user.
	 */
	final private JEditorPane lblNews;
	
	public NewsPanel() {
		
		setLayout( new GridLayout( 1 , 1 ) );
		this.lblNews = new JEditorPane();
		this.lblNews.setEditable( false );
		this.lblNews.setContentType( "text/html" );
		this.lblNews.setText( "<html><h1>Welcome</h1> <p>This is the interactive whiteboard client</p> <br><br> <a href=\"http://www.google.com\">try this link</a></html>" );
		this.lblNews.addHyperlinkListener( new NewsPanelListener() );
		add( this.lblNews );
	}
	
	public void addNewsPanelListener( NewsPanelListener l ) {
		this.lblNews.addHyperlinkListener( l );
	}
	
	/**
	 * sets the content to be displayed on the news panel
	 * 
	 * @param text				what to display on the news panel
	 */
	public void setNews( String text ) {
		this.lblNews.setText( text );
	}
}
