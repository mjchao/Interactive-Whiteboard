package util.log;

public class InternalLog extends LogParent
{
	final private static String filename = "internal.log";
	
	final public static void log(String message)
	{
		log(message, filename);
	}
}
