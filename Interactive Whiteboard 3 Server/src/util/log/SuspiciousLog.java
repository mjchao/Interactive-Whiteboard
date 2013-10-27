package util.log;

public class SuspiciousLog extends LogParent 
{
	final private static String filename = "suspicious.log";
	
	final public static void log(String message)
	{
		log(message, filename);
	}
}
