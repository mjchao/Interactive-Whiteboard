package util.log;

final public class ConnectionLog extends LogParent
{
	final private static String filename = "connections.log";
	
	final public static void log(String message)
	{
		log(message, filename);
	}
}
