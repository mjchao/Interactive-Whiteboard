package util.log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;

import util.CommonMethods;
import util.Text;

//this class provides basic methods for all logs
public class LogParent
{
	final private static int MAX_LINES = 1000000;
	final protected static ArrayList<String> m_logHistory = new ArrayList<String>();
	
	final public static void log(String message, String filename)
	{
		try
		{
			load(filename);
			//log the time and message
			String time = Calendar.getInstance().getTime().toString();
			m_logHistory.add(time + ":          " + message);
			enforceMaxLines();
			save(filename);
		} catch (IOException e)
		{
			CommonMethods.logInternalMessage(Text.LOG.getFileNotFoundLogMessage(filename, message));
		}
	}
	
	final protected static void load(String filename) throws IOException
	{
		//clear all current history
		m_logHistory.clear();
		//prepare to read the file
		FileReader historyFileReader = new FileReader(filename);
		BufferedReader historyBufferedReader = new BufferedReader(historyFileReader);
		Scanner s = new Scanner(historyBufferedReader);
		//read every line
		while (s.hasNextLine())
		{
			m_logHistory.add(s.nextLine());
		}
		historyFileReader.close();
		historyBufferedReader.close();
		s.close();
	}
	
	final protected static void save(String filename) throws IOException
	{
		//prepare to write to the file
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filename)));
		//print every line in our log history
		for (int lineIndex = 0; lineIndex < m_logHistory.size(); lineIndex++)
		{
			out.println(m_logHistory.get(lineIndex));
		}
		//close the file
		out.close();
	}
	
	final protected static void enforceMaxLines()
	{
		while (m_logHistory.size() > MAX_LINES)
		{
			m_logHistory.remove(0);
		}
	}
}
