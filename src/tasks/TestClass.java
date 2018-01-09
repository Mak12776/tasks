package tasks;

import java.util.Random;
import java.util.Map.Entry;

import exceptions.ArgumentParsingException;

public class TestClass
{
	public static void TestParser(String[] args)
	{
		try
		{
			if (!Parser.parseArguments(args))
			{
				return;
			}
		}
		catch(ArgumentParsingException e)
		{
			System.err.println("error: " + e.getMessage());
			return;
		}
		System.out.println("force is " + Parser.force);
		System.out.println("yes is " + Parser.yes);
		System.out.println("quiet is " + Parser.quiet);
		System.out.println("run command line is " + Parser.runCommandLine);
		System.out.println("---------------------------------------------");
		System.out.println("command is [" + Parser.command + "]");
		System.out.println("item type is [" + Parser.itemType + "]");
		System.out.println("---------------------------------------------");
		System.out.println("item infos:");
		for (Entry<String, Object> entry : Parser.itemInfos.entrySet())
		{
			System.out.println("\t[" + entry.getKey() + "]: " + entry.getValue());
		}
	}
	
	private static final String[] things = {"Hello", "Bye", "Good", "Summer", "Amin", "Armin", "Ali"};
	private static Random rand = new Random();
	public static void testCreator()
	{
		Random rand = new Random();
		DoubleLinkedItem.Creator<Task> taskCreator = new DoubleLinkedItem.Creator<Task>(2 + rand.nextInt(12));
		for (int i = 0; i < 10; i++)
		{
			taskCreator.skip(rand.nextInt(6));
			taskCreator.add(new Task(things[rand.nextInt(things.length)]));
			for (int j = 0, end = rand.nextInt(3); j < end; j++)
			{
				taskCreator.add(new Task(things[rand.nextInt(things.length)]));
			}
		}
		taskCreator.finish();
		System.out.println(taskCreator.getResult().test());
	}
	
	public static void testDoubleLinkedArrays()
	{
		DoubleLinkedItem<Task> tasks = new DoubleLinkedItem<Task>(5);
		for (int i = 0; i < 28; i++)
		{
			tasks.addItem(new Task("Item in " + String.valueOf(i)));
		}
		int end = (tasks.getLength() / 2);
		for (int j = 0; j < end; j++)
		{
			tasks.removeItem(rand.nextInt(tasks.getLength()));
		}
		tasks.optimize();
		System.out.println(tasks.test());
	}
}
