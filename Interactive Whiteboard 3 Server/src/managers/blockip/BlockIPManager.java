package managers.blockip;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import net.Server;

//This class is responsible for recording IP Addresses that are to be refused connection. 
//We call an IP Address to be in the list if any part of the IP Address is already in the list.
//For example, the user can add the sequence 19 to block any IP Addresses with 19 in them.
final public class BlockIPManager
{
	final private static ArrayList<String> m_blockedIPs = new ArrayList<String>();
	final private static int MAXIMUM_NUMERICAL_PART_OF_IP_VALUE = 255;
	final private static int MINIMUM_NUMERICAL_PART_OF_IP_VALUE = 0;
	
	//This method loads a saved list of blocked IP addresses from a previous session.
	//It will not work if the file is not there or can't be read.
	final public static void load() throws IOException
	{
		//look for a file "blocked.in"
		FileReader blockedFileReader = new FileReader("blocked.in");
		BufferedReader blockedBufferedReader = new BufferedReader(blockedFileReader);
		Scanner s = new Scanner(blockedBufferedReader);
		//each line will contain an IP address, so we need to read each line
		while (s.hasNextLine())
		{
			String aBlockedIP = s.nextLine();
			//and then add the IP address to the blocked list
			m_blockedIPs.add(aBlockedIP);
		}
		blockedFileReader.close();
		blockedBufferedReader.close();
		s.close();
	}
	
	//This method checks to see if an IP Address is in the list or not
	final public static boolean isIPInList(String ip)
	{
		//we do not block localhost.
		if (ip.equals(Server.LOCALHOST_IP))
		{
			return false;
		}
		for (int i = 0; i < m_blockedIPs.size(); i++)
		{
			if (ip.contains(m_blockedIPs.get(i)))
			{
				return true;
			}
		}
		return false;
	}
	
	//This method checks to see if the input can be an IP Address or part of an IP Address
	//This method uses the IP Address system of 0.0.0.0 to 255.255.255.255
	final public static boolean isValidIPAddress(String copyOfIP)
	{
		String ip = copyOfIP;
		//First make sure the IP Address is not null or empty
		if (ip == null || ip.equals(""))
		{
			return false;
		}
		//Break the IP Address into individual numbers that are separated by the decimal points
		int lastDecimalIndex = -1;
		int numDecimalsCounted = 0;
		//Go through the whole inputed IP and look for where the decimal points are
		for (int i = 0; i < ip.length(); i++)
		{
			if (ip.charAt(i) == '.')
			{
				numDecimalsCounted++;
				//Take the parts between decimal points and make sure they are numbers and that
				//the numbers are not bigger than 255
				String aPartOfIPAddress = ip.substring(lastDecimalIndex + 1, i);
				if (!isAValidNumericalPartOfIP(aPartOfIPAddress))
				{
					return false;
				}
				lastDecimalIndex = i;
			}
		}
		//make sure the last part is valid as well 
		if (!isAValidNumericalPartOfIP(ip.substring(lastDecimalIndex + 1, ip.length())))
		{
			return false;
		}
		//Make sure there were no more than 3 decimal points counted
		if (numDecimalsCounted > 3)
		{
			return false;
		}
		//If no decimals were counted, make sure the user entered a number and it is less than 255
		if (numDecimalsCounted == 0)
		{
			try
			{
				int numericalValue = Integer.parseInt(ip);
				if (numericalValue > MAXIMUM_NUMERICAL_PART_OF_IP_VALUE || numericalValue < MINIMUM_NUMERICAL_PART_OF_IP_VALUE)
				{
					return false;
				}
			} catch (NumberFormatException e)
			{
				return false;
			}
		}
		return true;
	}
	
	final public static boolean isAValidNumericalPartOfIP(String numericalPartOfIP)
	{
		try
		{
			int numericalPart = Integer.parseInt(numericalPartOfIP);
			if (numericalPart > MAXIMUM_NUMERICAL_PART_OF_IP_VALUE || numericalPart < MINIMUM_NUMERICAL_PART_OF_IP_VALUE)
			{
				return false;
			}
		} catch (NumberFormatException e)
		{
			return false;
		}
		return true;
	}
	
	//This method adds an IP Address to the blocked list if it is not already on there
	//This method assumes the IP Address is not an empty String and that it is not null
	final public static void addIP(String ip)
	{
		//make sure the IP is not already in the list
		for (int i = 0; i < m_blockedIPs.size(); i++)
		{
			if (ip.contains(m_blockedIPs.get(i)))
			{
				return;
			}
		}
		//if the IP is not already in the list, then add it
		m_blockedIPs.add(ip);
	}
	
	//This method removes an IP Address from the blocked list if it is there
	//This method assumes the IP Address is not an empty String and that it is not null.
	//This method will remove any IP Addresses that contain the inputed sequence.
	final public static void removeIP(String ip)
	{
		//find a matching blocked IP
		for (int i = 0; i < m_blockedIPs.size(); i++)
		{
			if (m_blockedIPs.get(i).contains(ip))
			{
				m_blockedIPs.remove(i);
				//We have to subtract one from the index because when an element is removed
				//all the other element indices also go down by one
				if (i > 0)
				{
					i--;
				}
			}
		}
	}
	
	//This method saves the blocked list of the current session
	//This will not work if it cannot, for some reason, write to a file "blocked.in"
	final public static void save() throws IOException
	{
		//open the file "blocked.in" to be ready for writing to.
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("blocked.in")));
		//each line will contain 1 blocked IP Address, so on each line, print one IP address
		for (int i = 0; i < m_blockedIPs.size(); i++)
		{
			out.println(m_blockedIPs.get(i));
		}
		//close the file and we are done
		out.close();
	}
}
