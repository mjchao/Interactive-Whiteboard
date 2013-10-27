package net.roomserver;

import java.io.IOException;

import net.ConcurrentConnection;

final public class RoomServerConcurrentConnection extends ConcurrentConnection 
{

	public RoomServerConcurrentConnection(String ip, int port) throws IOException 
	{
		super(ip, port);
	}

	@Override
	protected void decode(String message) 
	{
		//do nothing
		//no concurrent messages sent by room server for now
	}

}
