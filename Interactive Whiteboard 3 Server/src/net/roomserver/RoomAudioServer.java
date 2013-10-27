package net.roomserver;

import java.io.IOException;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

import util.CommonMethods;
import util.Text;

import database.ConnectionEndedException;

import main.server.ServerUIListener;
import net.MESSAGES;
import net.Server;
import net.ServerSideErrorException;
import net.TamperedClientException;

public class RoomAudioServer extends RoomFeatureServer
{

	final private RoomUserListServer m_userList;
	public RoomAudioServer(int stepPort, int concurrencyPort, int roomID, String modificationPassword, String joinPassword, RoomUserListServer userList) throws IOException 
	{
		super(stepPort, concurrencyPort, roomID, modificationPassword, joinPassword);
		super.m_clients = new RoomAudioSubServer[Server.MAX_CLIENTS];
		this.m_userList = userList;
	}

	@Override
	protected SubServer assignClientToSubServer(Socket aClientConnection, Socket aClientConcurrencyConnection) throws IOException 
	{
		return new RoomAudioSubServer(aClientConnection, aClientConcurrencyConnection);
	}

	@Override
	final protected boolean isClientLoggedIn(String username)
	{
		for (int clientIndex = 0; clientIndex < this.m_clients.length; clientIndex++)
		{
			RoomAudioSubServer aClient = (RoomAudioSubServer) this.m_clients[clientIndex];
			if (aClient != null)
			{
				if (aClient.getUsername().equals(username))
				{
					if (aClient.isLoggedIn())
					{
						System.out.println("Audi Server Still logged in");
						return true;
					}
				}
			}
		}
		return false;
	}
	
	final protected void sendAudioDataToAllClients(byte[] data, RoomAudioSubServer sender)
	{
		//check that the client is allowed to participate in audio chat
		if (this.m_userList.doesUserHaveAudioParticipation(sender.getUsername()) == true)
		{
			for (int clientIndex = 0; clientIndex < this.m_clients.length; clientIndex++)
			{
				RoomAudioSubServer aClient = (RoomAudioSubServer) this.m_clients[clientIndex];
				if (aClient != null)
				{
					//send audio data to everyone except for the person sending it
					if (aClient.getSubServerID() != sender.getSubServerID())
					{
						//check that the client is allowed to listen to audio chat
						if (this.m_userList.doesUserHaveAudioListening(aClient.getUsername()) == true)
						{
							aClient.sendAudioData(data);
						}
					}
				}
			}
		}
	}

	@Override
	public void closeConnectionWithClient(String username)
	{
		for (int clientIndex = 0; clientIndex < this.m_clients.length; clientIndex++)
		{
			RoomAudioSubServer aClient = (RoomAudioSubServer) this.m_clients[clientIndex];
			if (aClient != null)
			{
				if (aClient.getUsername().equals(username))
				{
					aClient.closeAndStop();
					this.removeAClient(aClient.getSubServerID());
					return;
				}
			}
		}
	}

	final private class RoomAudioSubServer extends RoomFeatureSubServer
	{

		final public static int BUFFER_SIZE = 8000;
		public RoomAudioSubServer(Socket s, Socket concurrentConnection) throws IOException 
		{
			super(s, concurrentConnection);
		}
		
		@Override
		final public void run()
		{
			//first read in the login
			while (!this.loggedIn)
			{
				try 
				{
					String messageFromClient = read();
					try 
					{
						decode(messageFromClient);
					} catch (NoSuchElementException e) 
					{
						//ignore
					} catch (ConnectionEndedException e)
					{
						//log an error and close connections
						if (e.getErrorID() == ConnectionEndedException.BAD_CONNECTION_PASSWORD)
						{
							CommonMethods.logConnectionMessage(Text.SUBSERVER.CONNECTION_ENDED_BAD_CONNECTION_PASSWORD_LOG_MESSAGE);
						} else if (e.getErrorID() == ConnectionEndedException.IOEXCEPTION)
						{
							CommonMethods.logConnectionMessage(Text.SUBSERVER.CONNECTION_ENDED_IO_EXCEPTION_LOG_MESSAGE);
						} else
						{
							CommonMethods.logConnectionMessage(Text.SUBSERVER.CONNECTION_ENDED_UNKNOWN_LOG_MESSAGE);
						}
						ServerUIListener.emergencyStop();
						break;
					} catch (ServerSideErrorException e)
					{
						if (e.getErrorID() == ServerSideErrorException.DATABASE_MISCOMMUNICATION_ERROR)
						{
							closeAndStop(MESSAGES.CONNECTION.CLOSING_SERVER_SIDE_ERROR);
							break;
						}
					} catch (TamperedClientException e)
					{
						try
						{
							closeBadConnection(this.m_username);
							break;
						} catch (ConnectionEndedException e2)
						{
							if (e2.getErrorID() == ConnectionEndedException.IOEXCEPTION)
							{
								CommonMethods.logConnectionMessage(Text.SUBSERVER.CONNECTION_ENDED_IO_EXCEPTION_LOG_MESSAGE);
							} else
							{
								CommonMethods.logConnectionMessage(Text.SUBSERVER.CONNECTION_ENDED_UNKNOWN_LOG_MESSAGE);
							}
						}
					}
				} catch (IOException e) 
				{
					this.closeAndStop();
					return;
				}
				
			}
			while (true)
			{
				try 
				{
					byte[] data = new byte[BUFFER_SIZE];
					//read in audio data
					this.m_in.read(data);
					//send data to all others
					RoomAudioServer.this.sendAudioDataToAllClients(data, this);
				} catch (IOException e) 
				{
					//if connection ended, stop running
					System.out.println("lost connection with client");
					closeAndStop();
					break;
				}
			}
			handleTerminatingConnection();
		}

		@Override
		protected void handleTerminatingConnection() 
		{
			this.closeAndStop();
			RoomAudioServer.this.removeAClient(this.m_subServerID);
		}

		@Override
		protected void decode(String messageFromClient) throws ConnectionEndedException, ServerSideErrorException, TamperedClientException, NoSuchElementException 
		{
			//wait for a login
			Scanner scanMessage = new Scanner(messageFromClient);
			String command = scanMessage.next();
			if (command.equals(MESSAGES.GENERAL.LOGIN))
			{
				//expect a username, password and join password
				String username = scanMessage.next();
				String password = scanMessage.next();
				String joinPassword = scanMessage.next();
				login(username, password, joinPassword);
			}
			scanMessage.close();
		}
		
		final protected void sendAudioData(byte[] data)
		{
			try 
			{
				this.m_concurrencyOut.write(data);
			} catch (IOException e) 
			{
				//ignore
			}
		}
		
		@Override
		final protected String read() throws IOException
		{
			String message = super.read();
			System.out.println("Room Audio server received from client: " + message);
			return message;
		}
		
		@Override
		final protected void write(String message)
		{
			System.out.println("Room Audio server wrote to client: " + message);
			super.write(message);
		}
		
	}
}
