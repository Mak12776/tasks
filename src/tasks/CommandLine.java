package tasks;

import java.util.Scanner;

import exceptions.CorruptedFileException;
import misc.DataErrorHandle;

public class CommandLine 
{
	private static Scanner input = new Scanner(System.in);
	
	public static void println(String string)
	{
		System.out.println("    " + string);
	}
	
	public static void println(char symbol, String string)
	{
		System.out.println("[" + symbol + "] " + string);
	}
	
	public static String getInput(String prompt)
	{
		System.out.print(prompt);
		return input.next();
	}
	
	public static String Input()
	{
		return input.next().trim();
	}
	
	public static DataErrorHandle corruptionQuestion(CorruptedFileException e)
	{
		println('*', "data file is corrupted.");
		println("detail: " + e.getMessage());
		String answr;
		char c;
		while (true)
		{
			println("choose an action:");
			println("[R]ecover, [B]ackup, [D]elete, [E]xit");
			answr = Input().toLowerCase();
			if (answr.length() == 1)
			{
				c = answr.charAt(0);
				if (c == 'r')
				{
					return DataErrorHandle.recover;
				}
				else if (c == 'b')
				{
					return DataErrorHandle.backup;
				}
				else if (c == 'd')
				{
					return DataErrorHandle.delete;
				}
				else if (c == 'e')
				{
					return DataErrorHandle.exit;
				}
				
				continue;
			}
			else
			{
				if (answr.equals("recover"))
				{
					return DataErrorHandle.recover;
				}
				else if (answr.equals("delete"))
				{
					return DataErrorHandle.delete;
				}
				else if (answr.equals("backup"))
				{
					return DataErrorHandle.backup;
				}
				else if (answr.equals("exit"))
				{
					return DataErrorHandle.exit;
				}
			}
			println('!', "invalid action");
		}
	}
}
