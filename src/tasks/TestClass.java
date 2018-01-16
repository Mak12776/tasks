package tasks;

import java.util.Random;
import java.util.Map.Entry;

import exceptions.ArgumentParsingException;
import types.Task;

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
	
	private static final String[] things = {"Jack", "Javad", "Akbar", "Hamid", "Amin", "Armin", "Ali"};
	private static Random rand = new Random();
	
	public static String randomName()
	{
		return things[rand.nextInt(things.length)];
	}
	
	public static Task newRandomTask()
	{
		Task result = new Task();
		result.title = randomName();
		return result;
	}
	
	public static void testDoubleLinkedItemsCreator(int seed)
	{
		if (seed == 0)
		{
			seed = rand.nextInt();
		}
		rand.setSeed(seed);
		DoubleLinkedItem.Creator<Task> taskCreator = new DoubleLinkedItem.Creator<Task>(256);
		for (int i = 0; i < 50; i++)
		{
			taskCreator.skip(rand.nextInt(11) + 1);
			taskCreator.add(newRandomTask());
			for (int j = 0, end = rand.nextInt(3); j < end; j++)
			{
				taskCreator.add(newRandomTask());
			}
		}
		taskCreator.finish();
		taskCreator.getResult().print();
		taskCreator.getResult().test();
		System.out.println("-------------------------- random --------------------------");
		System.out.println("seed of random is " + seed);
	}
	
	public static void testDoubleLinkedItemsMethods()
	{
		DoubleLinkedItem<Task> tasks = new DoubleLinkedItem<Task>(5);
		for (int i = 0; i < 20; i++)
		{
			tasks.addItem(newRandomTask());
		}
		
		tasks.test();
	}
	public static void testDoubleLinkedItemsOptimize()
	{
		DoubleLinkedItem.Creator<Task> taskCreator = new DoubleLinkedItem.Creator<Task>(30);
		for (int i = 0; i < 50; i++)
		{
			taskCreator.skip(rand.nextInt(11) + 1);
			taskCreator.add(newRandomTask());
			for (int j = 0, end = rand.nextInt(3); j < end; j++)
			{
				taskCreator.add(newRandomTask());
			}
		}
		taskCreator.finish();
		taskCreator.getResult().print();
		System.out.println("AFTER OPTIMIZATION:");
		System.out.println("OPTIMIZATION EFFECT:" + taskCreator.getResult().optimizeEffect());
		taskCreator.getResult().optimize();
		taskCreator.getResult().print();
	}
}
