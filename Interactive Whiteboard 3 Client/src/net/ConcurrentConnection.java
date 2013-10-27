package net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

//this connection allows concurrency with threads
//the purpose of this type of connection is to allow constant updating of the UI
//these connections do not send anything to the server - that is the job of the step connection
//instead, these connections constantly wait for the server to send something and then act appropriately
//and because of this purpose, these connections must be constructed (just like the UIs), because this connection
//depends on the UI it is assigned to.
abstract public class ConcurrentConnection extends Thread
{

	//non-changing networking properties and objects:
	final protected String m_ip;
	final protected int m_port;
	final protected Socket m_socket;
	final protected DataInputStream m_in;
	final protected DataOutputStream m_out;
	//networking properties that may change:
	protected boolean isConnected = false;
	
	public ConcurrentConnection(String ip, int port) throws IOException
	{
		this.m_ip = ip;
		this.m_port = port;
		this.m_socket = new Socket(ip, port);
		this.m_in = new DataInputStream(this.m_socket.getInputStream());
		this.m_out = new DataOutputStream(this.m_socket.getOutputStream());
		this.isConnected = true;
	}
	
	@Override
	public void run()
	{
		while (this.isConnected)
		{
			try
			{
				String message = read();
				decode(message);
			} catch (IOException e)
			{
				this.isConnected = false;
				break;
			}
		}
	}
	
	final protected String read() throws IOException
	{
		return this.m_in.readUTF();
	}
	
	final protected void write(String message)
	{
		try
		{
			this.m_out.writeUTF(message);
		} catch (IOException e)
		{
			//ignore - we don't care if the message couldn't be sent
		}
	}
	
	abstract protected void decode(String message);
	
	final public void close()
	{
		try
		{
			this.m_in.close();
		} catch (IOException e)
		{
			//ignore
		}
		try
		{
			this.m_out.close();
		} catch (IOException e)
		{
			//ignore
		}
		try
		{
			this.m_socket.close();
		} catch (IOException e)
		{
			//ignore
		}
	}
}
