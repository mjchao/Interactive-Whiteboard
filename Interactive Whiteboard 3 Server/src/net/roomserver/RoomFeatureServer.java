package net.roomserver;

import java.io.IOException;
import java.net.Socket;
import java.util.NoSuchElementException;

import database.ConnectionEndedException;

import net.Server;
import net.ServerSideErrorException;
import net.TamperedClientException;

/**
 * 
 * Server for a specific part of a room: audio, whiteboard, text or userlist
 * 
 */
abstract public class RoomFeatureServer extends Server
{

	final protected int m_roomID;
	protected String m_joinPassword;
	protected String m_modificationPassword;
	
	public RoomFeatureServer(int stepPort, int concurrencyPort, int roomID, String modificationPassword, String joinPassword) throws IOException 
	{
		super(stepPort, concurrencyPort);
		this.m_roomID = roomID;
		this.m_modificationPassword = modificationPassword;
		this.m_joinPassword = joinPassword;
	}

	final protected int getRoomID()
	{
		return this.m_roomID;
	}
	
	final protected String getModificationPassword()
	{
		return this.m_modificationPassword;
	}
	
	final protected String getJoinPassword()
	{
		return this.m_joinPassword;
	}
	
	final public void updateJoinPassword(String newJoinPassword)
	{
		this.m_joinPassword = newJoinPassword;
	}
	
	abstract public void closeConnectionWithClient(String username);
	
	
	abstract public class RoomFeatureSubServer extends SubServer
	{
		
		/**
		 * The join password the client provided when s/he connected to the room. We keep this 
		 * because the join password may change while the client is connected to this room, which
		 * could potentially cause the client to be kicked out because s/he does't have the correct
		 * join password stored.
		 */
		protected String m_joinPasswordProvidedAtLogin;
		
		public RoomFeatureSubServer(Socket s, Socket concurrentConnection) throws IOException 
		{
			super(s, concurrentConnection);
		}

		final protected void login(String username, String password, String joinPassword) throws ConnectionEndedException, ServerSideErrorException
		{
			if (RoomFeatureServer.this.getJoinPassword().equals(joinPassword))
			{
				this.m_joinPasswordProvidedAtLogin = joinPassword;
				super.login(username, password);
			} else
			{
				//join password should be correct or else the room server would
				//never have allowed the client to log in to this server
				this.closeClientPerformingUnauthorizedActions();
			}
		}
		
		@Override
		abstract protected void handleTerminatingConnection();

		@Override
		abstract protected void decode(String messageFromClient) throws ConnectionEndedException, ServerSideErrorException, TamperedClientException, NoSuchElementException;
		
		final protected boolean isModificationPasswordCorrect(String modificationPassword)
		{
			return RoomFeatureServer.this.getModificationPassword().equals(modificationPassword);
		}
		
		final protected void assertJoinPasswordIsCorrect(String joinPassword) throws TamperedClientException
		{
			String correctJoinPassword = this.m_joinPasswordProvidedAtLogin;
			if (!correctJoinPassword.equals(joinPassword))
			{
				throw new TamperedClientException(TamperedClientException.INVALID_JOIN_PASSWORD, correctJoinPassword, joinPassword, this.m_ip);
			}
		}
		
	}
}
