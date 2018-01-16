
package tasks;

import java.io.File;

public class Core 
{
	public static final String dataFileName = "tasks.data";
	public static final String descFolderName = "tasks.desc";
	
	private static TaskData tasksData;
	private static File dataFile;
	private static File descFolder;
	
	public static void initializeData(boolean force)
	{
		dataFile = new File(dataFileName);
		descFolder = new File(descFolderName);
	}
}
