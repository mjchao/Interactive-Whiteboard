package main;

import java.io.IOException;

import managers.blockip.BlockIPManager;
import managers.roomdata.RoomDataManager;
import managers.roomdata.RoomListManager;
import managers.userdata.UserListManager;

import util.CommonMethods;
import util.Text;

/**
 * TODO For next time:
 * 
 * implement delete and modify rooms
 *
 */
final public class Main
{
	final public static void main(String[] args)
	{
		//Try to start the program
		//First load all managers
		try
		{
			//blocked ip data
			BlockIPManager.load();
			//user data
			UserListManager.load();
			//room data
			RoomListManager.load();
			RoomDataManager.load();
		} catch (IOException e)
		{
			CommonMethods.displayErrorMessage(Text.MAIN.BLOCK_IPS_NOT_FOUND_ERROR_MESSAGE);
			CommonMethods.logInternalMessage(Text.MAIN.BLOCK_IPS_NOT_FOUND_LOG_MESSAGE);
		}
		//Then load user interface
		UI ui = null;
		try
		{
			ui = new UI();
			ui.addUIListener(new UIListener(ui));
		} catch (IOException e)
		{
			CommonMethods.displayErrorMessage(Text.MAIN.IP_NOT_FOUND_ERROR_MESSAGE);
			CommonMethods.logConnectionMessage(Text.MAIN.IP_NOT_FOUND_LOG_MESSAGE);
			CommonMethods.terminate();
		}
	}
}
