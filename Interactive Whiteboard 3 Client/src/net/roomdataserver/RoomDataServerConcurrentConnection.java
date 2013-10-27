package net.roomdataserver;

import gui.roomdata.RoomInfoInterface;

import java.io.IOException;

import net.ConcurrentConnection;

public class RoomDataServerConcurrentConnection extends ConcurrentConnection
{

	@SuppressWarnings("unused")	//for future updates
	final private RoomInfoInterface m_gui;
	
	public RoomDataServerConcurrentConnection(RoomInfoInterface gui, String ip, int port) throws IOException 
	{
		super(ip, port);
		// TODO Auto-generated constructor stub
		this.m_gui = gui;
	}

	@Override
	protected void decode(String message)
	{
		// TODO Auto-generated method stub
		
	}

}
