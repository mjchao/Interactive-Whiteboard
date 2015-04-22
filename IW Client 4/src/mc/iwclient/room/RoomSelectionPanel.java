package mc.iwclient.room;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.AbstractCellEditor;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import mc.iwclient.account.AccountUI;
import mc.iwclient.structs.RoomData;
import mc.iwclient.uitemplates.CommandMenu;
import mc.iwclient.uitemplates.FormattedTextArea;
import mc.iwclient.uitemplates.InputField;
import mc.iwclient.util.Text;

//TODO sorting tables, documenting code
public class RoomSelectionPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	final protected AccountUI m_mainFrame;
	
	final private JTable tblRoomData;
	
	final protected RoomInfoDialog dlgRoomInfo;
	
	public RoomSelectionPanel( AccountUI mainFrame ) {
		this.m_mainFrame = mainFrame;
		
		setLayout( new BorderLayout() );
		
		ArrayList< RoomDataTableRow > roomData = new ArrayList< RoomDataTableRow >();
		for ( int i=0 ; i<10 ; i++ ) {
			RoomData r = new RoomData();
			r.m_id = "000"+i;
			r.m_roomName = "Room " + i;
			r.m_creatorDisplayName = "Creator " + i;
			RoomCommandsPanel roomCommands = new RoomCommandsPanel( r );
			roomCommands.addRoomCommandsPanelListener( new RoomCommandsPanelListener( roomCommands ) );
			roomData.add( new RoomDataTableRow( r , roomCommands ) );
		}
		RoomDataTableModel model = new RoomDataTableModel( roomData );
		model.addColumn( Text.Room.ROOM_ID_LABEL );
		model.addColumn( Text.Room.ROOM_NAME_LABEL );
		model.addColumn( Text.Room.ROOM_CREATOR_LABEL );
		model.addColumn( Text.Room.ROOM_COMMANDS_LABEL );
		this.tblRoomData = new JTable( model );
		this.tblRoomData.setDefaultRenderer( JPanel.class , new RoomDataTableCellRenderer( roomData ) );
		this.tblRoomData.setRowHeight( roomData.get( 0 ).pnlCommands.getPreferredSize().height );
		this.tblRoomData.setDefaultEditor( JPanel.class , new RoomDataTableCellEditor( roomData ) );
		this.tblRoomData.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		this.tblRoomData.setRowSelectionAllowed( false );
		this.tblRoomData.getColumn( Text.Room.ROOM_COMMANDS_LABEL ).setPreferredWidth( roomData.get( 0 ).pnlCommands.getPreferredSize().width );
		
		add( new JScrollPane( this.tblRoomData ) , BorderLayout.CENTER );
		
		this.dlgRoomInfo = new RoomInfoDialog();
	}
	
	/**
	 * attempts to join the room given by the specified room properties
	 * 
	 * @param roomData			the properties of the room the user wants to join
	 */
	public void joinRoom( RoomData roomData ) {
		//TODO
		RoomUI.showUI();
	}
	
	/**
	 * attempts to modify the room using the specified room properties
	 * 
	 * @param newRoomData		the new room properties for the room the user
	 * 							wants to modify
	 */
	public void modifyRoom( RoomData newRoomData ) {
		//TODO
	}
	
	/**
	 * shows additional room data that the room data table may not show
	 * 
	 * @param roomData			the properties of the room to display
	 */
	public void showRoomData( RoomData roomData ) {
		this.dlgRoomInfo.loadRoomData( roomData );
		this.dlgRoomInfo.setVisible( true );
	}
	
	private class RoomCommandsPanel extends JPanel {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		final private RoomData m_roomData;
		
		final private JButton cmdMoreInfo;
		
		final private JButton cmdJoin;
		
		public RoomCommandsPanel( RoomData roomData ) {
			this.m_roomData = roomData;
			
			setLayout( new FlowLayout( FlowLayout.CENTER ) );
			this.cmdMoreInfo = new JButton( Text.Room.MORE_INFO_COMMAND );
			add( this.cmdMoreInfo );
			
			this.cmdJoin = new JButton( Text.Room.JOIN_COMMAND );
			add( this.cmdJoin );

		}
		
		public void addRoomCommandsPanelListener( RoomCommandsPanelListener l ) {
			this.cmdMoreInfo.addActionListener( l );
			this.cmdJoin.addActionListener( l );
		}
		
		public RoomData getRoomData() {
			return this.m_roomData;
		}
		
		public void showFullRoomData() {
			showRoomData( this.m_roomData );
		}
	}
	
	private class RoomCommandsPanelListener implements ActionListener {
		
		final private RoomCommandsPanel m_gui;
		
		public RoomCommandsPanelListener( RoomCommandsPanel gui ) {
			this.m_gui = gui;
		}

		@Override
		public void actionPerformed( ActionEvent e ) {
			String command = e.getActionCommand();
			if ( command.equals( Text.Room.MORE_INFO_COMMAND ) ) {
				this.m_gui.showFullRoomData();
			}
			else if ( command.equals( Text.Room.JOIN_COMMAND ) ) {
				joinRoom( this.m_gui.getRoomData() );
			}
		}
	}
	
	private class RoomInfoDialog extends JDialog {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		protected RoomData m_roomData;
		
		final private InputField descId;
		
		final private InputField descName;
		
		final private InputField descCreator;
		
		final private InputField descCreationDate;
		
		final private InputField descPasswordProtection;
		
		final private InputField descJoinPassword;
		
		final private JPanel pnlCreatorDescription;
			final private JLabel lblCreatorDescription;
			final private FormattedTextArea txtCreatorDescription;
			
		final private CommandMenu pnlCommands;
		
		public RoomInfoDialog() {
			super( RoomSelectionPanel.this.m_mainFrame , "" , Dialog.ModalityType.MODELESS );
			
			setLayout( new BoxLayout( this.getContentPane() , BoxLayout.Y_AXIS ) );
			
			this.descId = new InputField( Text.Room.ROOM_ID_DESCRIPTION );
			add( this.descId );
			
			this.descName = new InputField( Text.Room.ROOM_NAME_DESCRIPTION );
			add( this.descName );
			
			this.descCreator = new InputField( Text.Room.ROOM_CREATOR_DESCRIPTION );
			add( this.descCreator );
			
			this.descCreationDate = new InputField( Text.Room.ROOM_CREATION_DATE_DESCRIPTION );
			add( this.descCreationDate );
			
			this.descPasswordProtection = new InputField( Text.Room.PASSWORD_PROTECTION_DESCRIPTION );
			add( this.descPasswordProtection );
			
			this.descJoinPassword = new InputField( Text.Room.JOIN_PASSWORD_DESCRIPTION );
			add( this.descJoinPassword );
			
			this.pnlCreatorDescription = new JPanel();
			this.pnlCreatorDescription.setLayout( new BoxLayout( this.pnlCreatorDescription , BoxLayout.X_AXIS ) );
				this.lblCreatorDescription = new JLabel( Text.Room.CREATOR_DESCRIPTION_DESCRIPTION );
				this.pnlCreatorDescription.add( this.lblCreatorDescription );
				
				this.txtCreatorDescription = new FormattedTextArea();
				this.pnlCreatorDescription.add( new JScrollPane( this.txtCreatorDescription ) );
			add( this.pnlCreatorDescription );
			
			this.pnlCommands = new CommandMenu( false );
			this.pnlCommands.addCommand( Text.Room.CLOSE_STRING );
			this.pnlCommands.addCommand( Text.Room.MODIFY_STRING );
			add( this.pnlCommands );
			
			this.pnlCommands.addActionListener( new ActionListener() {

				@Override
				public void actionPerformed( ActionEvent e ) {
					String command = e.getActionCommand();
					if ( command.equals( Text.Room.CLOSE_STRING ) ) {
						RoomInfoDialog.this.setVisible( false );
					}
					else if ( command.equals( Text.Room.MODIFY_STRING ) ) {
						RoomSelectionPanel.this.modifyRoom( RoomInfoDialog.this.m_roomData );
					}
				}
			});
		}
		
		/**
		 * loads the specified room's properties into this room info window
		 * 
		 * @param roomData			the room properties to load
		 */
		public void loadRoomData( RoomData roomData ) {
			this.m_roomData = roomData;
			this.descId.setInput( roomData.m_id );
			this.descName.setInput( roomData.m_roomName );
			this.descCreator.setInput( roomData.m_creatorDisplayName );
			this.descCreationDate.setInput( roomData.m_creationDate );
			this.descPasswordProtection.setInput( RoomData.getPasswordProtectionString( roomData.m_isPasswordProtected ) );
			this.descJoinPassword.setInput( roomData.m_joinPassword );
			this.txtCreatorDescription.setText( roomData.m_description );
			this.pack();
		}
	}
	
	private class RoomDataTableRow {
		
		final public RoomData m_roomData;
		
		final public JLabel lblRoomID;
		
		final public JLabel lblRoomName;
		
		final public JLabel lblCreatorName;
		
		final public RoomCommandsPanel pnlCommands;
		
		public RoomDataTableRow( RoomData data , RoomCommandsPanel commands ) {
			this.m_roomData = data;
			this.lblRoomID = new JLabel( data.m_id );
			this.lblRoomName = new JLabel( data.m_roomName );
			this.lblCreatorName = new JLabel( data.m_creatorDisplayName );
			this.pnlCommands = commands;
		}
	}
	
	private class RoomDataTableModel extends DefaultTableModel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		final private ArrayList< RoomDataTableRow > m_roomData;
		
		public RoomDataTableModel( ArrayList< RoomDataTableRow > roomData ) {
			this.m_roomData = roomData;
		}
		
		@Override
		public Class getColumnClass( int col ) {
			switch ( col ) {
				case 0:
					return String.class;
				case 1:
					return String.class;
				case 2:
					return String.class;
				case 3:
					return RoomCommandsPanel.class;
				default:
					throw new RuntimeException();
			}
		}
		
		@Override
		public int getColumnCount() {
			return 4;
		}

		@Override
		public int getRowCount() {
			
			//null check must be here because of getRowCount() method 
			//in DefaultTableModel
			if ( this.m_roomData == null ) {
				return 0;
			} 
			else {
				return this.m_roomData.size();
			}
		}

		@Override
		public Object getValueAt( int row , int column ) {
			RoomDataTableRow rowData = this.m_roomData.get( row );
			RoomData room = rowData.m_roomData;
			switch ( column ) {
				case 0:
					return new Integer( room.m_id );
				case 1:
					return room.m_roomName;
				case 2:
					return room.m_creatorDisplayName;
				case 3:
					return rowData.pnlCommands;
				default:
					throw new RuntimeException();
			}
		}
		
		@Override
		public boolean isCellEditable( int row , int column ) {
			if ( column == 3 ) {
				return true;
			}
			else {
				return false;
			}
		}
	}
	
	private class RoomDataTableCellRenderer implements TableCellRenderer {

		final private ArrayList< RoomDataTableRow > m_data;
		
		public RoomDataTableCellRenderer( ArrayList< RoomDataTableRow > data ) {
			this.m_data = data;
		}
		
		@Override
		public Component getTableCellRendererComponent( JTable table ,
				Object value , boolean isSelected , boolean hasFocus , int row , int col ) {
			RoomDataTableRow data = this.m_data.get( row );
			switch( col ) {
				case 0:
					return data.lblRoomID;
				case 1:
					return data.lblRoomName;
				case 2:
					return data.lblCreatorName;
				case 3:
					return data.pnlCommands;
				default:
					throw new RuntimeException();
			}
		}
	}
	
	private class RoomDataTableCellEditor extends AbstractCellEditor implements TableCellEditor {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		final private ArrayList< RoomDataTableRow > m_data;
		
		public RoomDataTableCellEditor( ArrayList< RoomDataTableRow > data ) {
			this.m_data = data;
		}
		
		@Override
		public Object getCellEditorValue() {
			return null;
		}

		@Override
		public Component getTableCellEditorComponent( JTable table , Object value ,
				boolean isSelected , int row , int col ) {
			RoomDataTableRow data = this.m_data.get( row );
			switch( col ) {
				case 0:
					return data.lblRoomID;
				case 1:
					return data.lblRoomName;
				case 2:
					return data.lblCreatorName;
				case 3:
					return data.pnlCommands;
				default:
					throw new RuntimeException();
			}
		}
		
	}

}
