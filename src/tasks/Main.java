
package tasks;

import exceptions.ArgumentParsingException;
import exceptions.CorruptedFileException;
import exceptions.FatalErrorException;

public class Main
{
	public static void commandLine() throws Exception
	{
		
	}
	
	public static void main(String[] args)
	{
		try
		{
			if (Parser.parseArguments(args))
			{
				System.exit(0);
			}
		}
		catch (ArgumentParsingException e)
		{
			System.err.println("argument error: " + e.getMessage());
			System.exit(1);
		}
		try 
		{
			Core.initializeData(Parser.force);
		}
		catch (CorruptedFileException e) 
		{
			
		}
		catch (FatalErrorException e)
		{
			e.printStackTrace();
		}
	}
}
