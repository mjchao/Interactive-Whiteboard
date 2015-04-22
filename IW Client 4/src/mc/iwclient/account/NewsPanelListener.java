package mc.iwclient.account;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class NewsPanelListener implements HyperlinkListener {
	
	public NewsPanelListener() {
		
	}
	
	@Override
	public void hyperlinkUpdate( HyperlinkEvent e ) {
		if ( e.getEventType() == HyperlinkEvent.EventType.ACTIVATED ) {
			if ( Desktop.isDesktopSupported() ) {
				try {
					Desktop.getDesktop().browse( new URI( e.getDescription() ) );
				} 
				catch ( IOException browserError ) {
					
					//ignore if the user can't browse this website
					browserError.printStackTrace();
				} 
				catch ( URISyntaxException badURL ) {
					
					//ignore, this error should not happen, assuming
					//that the hyperlinks in the news are all correct
					badURL.printStackTrace();
				}
			}
		}
	}
}
