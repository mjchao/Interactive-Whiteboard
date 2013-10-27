package main;

import gui.login.Login;
import gui.login.LoginListener;

import java.io.IOException;

import util.CommonMethods;
import util.Text;

final public class Main
{
	final public static void main(String[] args)
	{
		try
		{
			Login login = new Login();
			login.addLoginListener(new LoginListener(login));
		} catch (IOException e)
		{
			CommonMethods.displayErrorMessage(Text.GUI.LOGIN.IP_NOT_FOUND_ERROR_STRING);
			CommonMethods.terminate();
		}
	}
}
