package gui.messaging.pests;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.event.AncestorEvent;

import util.CommonMethods;
import util.Text;

import net.InvalidMessageException;
import net.OperationFailedException;
import net.messagingserver.MessagingServerStepConnection;

import gui.login.Login;
import gui.messaging.List;

public class PestsList extends List
{
	private static final long serialVersionUID = 1L;
	
	final private static int MAX_PESTS = 1000;
	
	final private JButton cmdAddPest = new JButton();
	final private JButton cmdRelist = new JButton();
	
	final private PestComparator comparator = new PestComparator();
	
	//networking objects used:
	final protected MessagingServerStepConnection m_messagingServerStepConnection;
	
	public PestsList(MessagingServerStepConnection messagingServerStepConnection)
	{
		super();
		super.elements = new Pest[MAX_PESTS];
		
		//define some textual properties
		this.cmdAddPest.setText(Text.GUI.MESSAGING.PESTS.ADD_PEST_STRING);
		this.cmdRelist.setText(Text.GUI.MESSAGING.PESTS.RELIST_STRING);
		//add some components
		this.pnlCommands.add(this.cmdAddPest);
		this.pnlCommands.add(this.cmdRelist);
		//remember the device we will use to send messages to the server
		this.m_messagingServerStepConnection = messagingServerStepConnection;
	}
	
	final public void addPestsListListener(PestsListListener l)
	{
		this.cmdAddPest.addActionListener(l);
		this.cmdRelist.addActionListener(l);
	}
	
	final public void addPest(String username)
	{
		Pest newPest = new Pest(username);
		newPest.addPestListener(new PestListener(newPest));
		if (super.addElementToGUI(newPest, this.comparator))
		{
			//great - do nothing
		} else
		{
			CommonMethods.displayErrorMessage(Text.GUI.MESSAGING.PESTS.PESTS_LIST_FULL_ERROR_MESSAGE);
		}
	}
	
	final public void updatePests()
	{
		//we only load the pests list once - the first time the user loads it
		try 
		{
			String[] pestsList = this.m_messagingServerStepConnection.getPestsList(Login.m_username, Login.m_password);
			this.elements = new ListElement[MAX_PESTS];
			for (int pestsAdded = 0; pestsAdded < pestsList.length; pestsAdded++)
			{
				Pest pestToAdd = new Pest(pestsList[pestsAdded]);
				pestToAdd.addPestListener(new PestListener(pestToAdd));
				this.elements[pestsAdded] = pestToAdd;
			}
		} catch (IOException e)
		{
			this.m_messagingServerStepConnection.displayConnectionLostMessage();
		} catch (OperationFailedException e) 
		{
			this.m_messagingServerStepConnection.handleOperationFailedException(e);
		} catch (InvalidMessageException e) 
		{
			this.m_messagingServerStepConnection.handleInvalidMessageException(e);
		}
		refreshList();
	}
	
	final void removePest(Pest aPest)
	{
		super.removeElement(aPest, this.comparator);
	}
	
	final public void refreshList()
	{
		super.refreshList(this.comparator);
	}
	
	final private class Pest extends ListElement
	{
		private static final long serialVersionUID = 1L;
		
		//panel properties
		final private FlowLayout PEST_LAYOUT = new FlowLayout(FlowLayout.CENTER);
		private boolean isFloating = false;
		//components
		final private JLabel lblPestName = new JLabel();
		final private JButton cmdRemove = new JButton();
		//other information
		final private String m_username;
		final private String m_displayName;
		
		public Pest(String username)
		{
			this.m_username = username;
			this.m_displayName = PestsList.this.m_messagingServerStepConnection.getDisplayNameOfUser(Login.m_username, Login.m_password, this.m_username);
			this.lblPestName.setText(this.m_displayName);
			this.cmdRemove.setText(Text.GUI.MESSAGING.PESTS.REMOVE_PEST_STRING);
			
			//add some components
			setLayout(this.PEST_LAYOUT);
			add(this.lblPestName);
			add(this.cmdRemove);
			
		}
		
		final public void addPestListener(PestListener l)
		{
			this.cmdRemove.addActionListener(l);
			this.addAncestorListener(l);
		}
		
		final public String getUsername()
		{
			return this.m_username;
		}
		
		final public String getDisplayName()
		{
			return this.m_displayName;
		}
		
		final void setIsFloating(boolean b)
		{
			this.isFloating = b;
		}
		
		final boolean isFloating()
		{
			return this.isFloating;
		}
	}
	
	final private class PestListener extends ListElementListener
	{
		final private Pest m_pestGui;
		public PestListener(ListElement gui) 
		{
			super(gui);
			this.m_pestGui = (Pest) gui;
		}

		@Override
		public void actionPerformed(ActionEvent e) 
		{
			String command = e.getActionCommand();
			if (command.equals(Text.GUI.MESSAGING.PESTS.REMOVE_PEST_STRING))
			{
				try
				{
					PestsList.this.m_messagingServerStepConnection.removePest(Login.m_username, Login.m_password, this.m_pestGui.getUsername());
					PestsList.this.removePest(this.m_pestGui);
				} catch (IOException connectionLost)
				{
					CommonMethods.displayErrorMessage(Text.NET.getConnectionLostMessage(Text.NET.MESSAGING_SERVER_NAME));
				} catch (OperationFailedException operationFailed)
				{
					//ignore - this means the client was tampered with, so we don't care.
				} catch (InvalidMessageException badMessage)
				{
					PestsList.this.m_messagingServerStepConnection.handleInvalidMessageException(badMessage);
				}
			}
		}

		@Override
		public void ancestorAdded(AncestorEvent e)
		{
			if (e.getAncestor().getName() == null)
			{
				//we reach this block if the conversation this observes was 
				//placed into the original panel it was in or if it was added to the 
				//panel pnlList.
				//this first block is called when the conversation was dragged from outside the panel and
				//placed into the panel. Since we do want to refresh the conversation,
				//for the user's convenience, we do that here.
				//however, if this conversation was just created and it being added, we do not want to
				//refresh the list, or we will trigger a stackoverflow exception. that's when the other
				//part of the if block is used. everything conversation starts as non-floating, so
				//the first block is only called after the user as made it floating.
				if (this.m_pestGui.isFloating())
				{
					this.m_pestGui.setIsFloating(false);
					PestsList.this.refreshList();
				} else
				{
					this.m_pestGui.setIsFloating(false);
				}
			} else
			{
				//System.out.println(e.getAncestor());
				this.m_pestGui.setIsFloating(true);
			}
		}

		@Override
		public void ancestorMoved(AncestorEvent event)
		{
			//ignore - here for future updates
		}

		@Override
		public void ancestorRemoved(AncestorEvent event)
		{
			//ignore - here for future updates
		}
		
	}
	
	final private class PestComparator extends ListElementComparator
	{

		public PestComparator()
		{
			//nothing needs to be done as of right now
		}

		@Override
		public int compare(ListElement arg0, ListElement arg1)
		{
			if (arg0 != null && arg1 != null)
			{
				return ((Pest) arg0).getDisplayName().compareTo(((Pest) arg1).getDisplayName());
			} else if (arg0 != null && arg1 == null)
			{
				return 1;
			} else if (arg0 == null && arg1 != null)
			{
				return -1;
			} else
			{
				return 0;
			}
		}
		
	}
}
