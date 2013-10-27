package gui.messaging;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

abstract public class List extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	final private BorderLayout LAYOUT = new BorderLayout();
	
	final private GridLayout LIST_LAYOUT = new GridLayout(25, 0);
	final protected JPanel pnlList = new JPanel(this.LIST_LAYOUT);
	final private JScrollPane scrollList = new JScrollPane(this.pnlList);
	
	final private FlowLayout COMMANDS_LAYOUT = new FlowLayout(FlowLayout.CENTER);
	final protected JPanel pnlCommands = new JPanel(this.COMMANDS_LAYOUT);
	
	protected ListElement[] elements;
	protected ListElement[] copyOfElements;
	
	public List()
	{
		setLayout(this.LAYOUT);
		add(this.scrollList, BorderLayout.CENTER);
		add(this.pnlCommands, BorderLayout.SOUTH);
	}
	
	/**
	 * Attempts to add an element to the list
	 * @param e						a <code>ListElement</code>, something to be added to the list
	 * @param comparator			a <code>ListElementComparator</code>, something that gives instructions on
	 * 								how to sort the list
	 * @return						true if the element was successfully added, false if the element could not
	 * 								be added (i.e. list is full)
	 */
	//this method attempts to add an element to the list. 
	//it return true if the element was successfully added and false if it could not be added
	final public boolean addElementToGUI(ListElement e, ListElementComparator comparator)
	{
		//first, make sure the element is not already in data
		for (int elementIndex = 0; elementIndex < this.elements.length; elementIndex++)
		{
			if (this.elements[elementIndex] != null)
			{
				if (this.elements[elementIndex].equals(e))
				{
					//if already stored in data, just refresh the list
					refreshList(comparator);
				}
			}
		}
		//go through the list
		for (int elementIndex = 0; elementIndex < this.elements.length; elementIndex++)
		{
			//search for an empty spot
			if (this.elements[elementIndex] == null)
			{
				//assign the empty spot to the list element
				this.elements[elementIndex] = e;
				refreshList(comparator);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Attempts to add an element to the list but does not refresh the display
	 * @param e						a <code>ListElement</code>, something to be added to the list
	 * @param comparator			a <code>ListElementComparator</code>, something that gives instructions on
	 * 								how to sort the list
	 * @return						true if the element was successfully added, false if the element could not
	 * 								be added (i.e. list is full)
	 */
	//this method attempts to add an element to the list. 
	//it return true if the element was successfully added and false if it could not be added
	final public boolean addElementToData(ListElement e, ListElementComparator comparator)
	{
		//go through the list
		for (int elementIndex = 0; elementIndex < this.elements.length; elementIndex++)
		{
			//search for an empty spot
			if (this.elements[elementIndex] == null)
			{
				//assign the empty spot to the list element
				this.elements[elementIndex] = e;
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Removes the given element from the list.
	 * 
	 * @param e						a <code>ListElement</code>, what to be removed
	 * @param comparator			a <code>ListElementComparator</code>, how to sort the element of the list
	 */
	public final void removeElement(ListElement e, ListElementComparator comparator)
	{
		//go through the list
		for (int elementIndex = 0; elementIndex < this.elements.length; elementIndex++)
		{
			if (this.elements[elementIndex] != null)
			{
				//search for the given element
				if (this.elements[elementIndex].equals(e))
				{
					this.elements[elementIndex].setVisible(false);
					this.elements[elementIndex].setUI(null);
					this.elements[elementIndex] = null;
					if (comparator != null)
					{
						refreshList(comparator);
					}
				}
			}
		}
	}
	
	/**
	 * Removes everything from the list, sorts it, then adds everything back. If <code>null</code> is provided
	 * as the <code>ListElementComparator</code>, then no sorting happens, but everything is added back.
	 * 
	 * @param comparator		a <code>ListElementComparator</code> how to sort the elements
	 */
	final protected void refreshList(ListElementComparator comparator)
	{
		//remove everything from the list
		this.pnlList.removeAll();
		//resort the list elements in order
		this.copyOfElements = this.elements.clone();
		if (comparator != null)
		{
			Arrays.sort(this.copyOfElements, comparator);
		}
		//add all elements to the list again
		for (int elementIndex = 0; elementIndex < this.copyOfElements.length; elementIndex++)
		{
			if (this.copyOfElements[elementIndex] != null)
			{
				this.pnlList.add(this.copyOfElements[elementIndex]);
			}
		}
		//refresh the display
		this.pnlList.revalidate();
		this.pnlList.repaint();
	}
	
	final public int getNumElements()
	{
		return this.elements.length;
	}
	
	final public ListElement getElementAt(int index)
	{
		return this.elements[index];
	}

	abstract protected class ListElement extends JToolBar
	{
		private static final long serialVersionUID = 1L;
		
	}
	
	abstract protected class ListElementListener implements ActionListener, AncestorListener
	{
		
		final protected ListElement m_gui;
		public ListElementListener(ListElement gui)
		{
			this.m_gui = gui;
		}
		
		@Override
		abstract public void actionPerformed(ActionEvent e);
			
		@Override
		abstract public void ancestorAdded(AncestorEvent e);
		
		@Override
		abstract public void ancestorMoved(AncestorEvent e);
		
		@Override
		abstract public void ancestorRemoved(AncestorEvent e);
		
	}
	
	abstract protected class ListElementComparator implements Comparator<ListElement>
	{
		@Override
		abstract public int compare(ListElement arg0, ListElement arg1);
	}
}
