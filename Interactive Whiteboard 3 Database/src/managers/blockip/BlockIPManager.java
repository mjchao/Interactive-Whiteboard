package managers.blockip;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import net.Networking;

/**
 * Records IP addresses that should be refused connection. Note that if any part of an IP address is
 * in this list, then the IP address is blocked. for example, if the user blocks 10.10, then 
 * 10.10.10.10 and 10.10.10.11 and 1.10.10.1 (and so on) are all blocked.
 */
final public class BlockIPManager
{
	/**
	 * stores blocked IP addresses
	 */
	final private static ArrayList<String> m_blockedIPs = new ArrayList<String>();
	
	final private static int MAXIMUM_NUMERICAL_PART_OF_IP_VALUE = 255;
	final private static int MINIMUM_NUMERICAL_PART_OF_IP_VALUE = 0;
	
	/**
	 * name of the file where blocked IP addresses are stored
	 */
	final private static String BLOCKED_IP_FILENAME = "blocked.in";
	
	/**
	 * loads a saved list of blocked IP addresses. 
	 * 
	 * @throws IOException			if the file where theb locked IP addresses are stored cannot be loaded
	 * @see							#BLOCKED_IP_FILENAME
	 */
	final public static void load() throws IOException
	{
		//look for a file "blocked.in"
		FileReader blockedFileReader = new FileReader(BLOCKED_IP_FILENAME);
		BufferedReader blockedBufferedReader = new BufferedReader(blockedFileReader);
		Scanner s = new Scanner(new BufferedReader(blockedBufferedReader));
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
	
	/**
	 * determines if a given IP address is blocked.
	 * 
	 * @param ip			a String, the IP address to check to see if it is blocked
	 * @return				if the given IP address is blocked
	 */
	final public static boolean isIPInList(String ip)
	{
		//we do not block localhost.
		if (ip.equals(Networking.LOCALHOST))
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
	
	/**
	 * determines if a given IP is valid. (e.g. hi.hi.hi.hi.hi is not a valid IP address)
	 * 
	 * @param iP			a String, the IP address to see if it is valid or not.
	 * @return				if the given IP address is valid.
	 */
	final public static boolean isValidIPAddress(String ip)
	{
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
	
	/**
	 * determines if a given part of an IP address can be a part of an IP address. for example,
	 * 255 might be part of the address 10.187.1.255 but "hi" will never be a part of an IP address.
	 * 
	 * @param numericalPartOfIP			a part of an IP address
	 * @return							if the part of the IP address can be a part of an IP address.
	 */
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
	
	/**
	 * adds an IP o the blocked IP list if it is not already on there.
	 * assumes:<br>
	 * 1) the IP has been checked and is valid
	 * 
	 * @param ip		the IP address to add.
	 */
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
	
	/**
	 * removes an IP from the blocked list if it is there. removes any IP addresses that contain the 
	 * given sequence. for example, if asked to remove 192, 192.168.1.98 and 192.168.1.101 will be removed
	 * and so will 10.187.1.192
	 * 
	 * @param ip		a String, the IP address(es) to be removed.
	 */
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
	
	/**
	 * saves the blocked list.
	 * 
	 * @throws IOException		if the file cannot be written to.
	 */
	final public static void save() throws IOException
	{
		//open the file "blocked.in" to be ready for writing to.
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(BLOCKED_IP_FILENAME)));
		//each line will contain 1 blocked IP Address, so on each line, print one IP address
		for (int i = 0; i < m_blockedIPs.size(); i++)
		{
			out.println(m_blockedIPs.get(i));
		}
		//close the file and we are done
		out.close();
	}
}
