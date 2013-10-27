package main;

import java.io.IOException;
import java.net.ServerSocket;

import util.CommonMethods;
import util.Text;

import main.init.StartUp;
import main.init.StartUpListener;

final public class Main 
{
	final public static void main(String[] args)
	{
		//test to make sure we can accept connections on port 9999
		try
		{
			ServerSocket test = new ServerSocket(9999);
			test.close();
			test = null;
		} catch (IOException e)
		{
			CommonMethods.displayErrorMessage(Text.MAIN.PORT9999_TAKEN_ERROR);
			CommonMethods.terminate();
		}
		//start the program
		StartUp startUp = new StartUp();
		startUp.addStartUpListener(new StartUpListener(startUp));
	}
}
