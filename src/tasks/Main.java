
package tasks;


public class Main
{
	public static void commandLine()
	{
		
	}
	
	public static void main(String[] args)
	{
		DoubleLinkedItem<Task> tasks = new DoubleLinkedItem<>(4);
		tasks.print();
		tasks.test();
		int num = 0;
		for(Item task : tasks)
		{
			System.out.println(num + ": " + task);
			num++;
		}
		
	}
}
