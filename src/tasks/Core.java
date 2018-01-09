
package tasks;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Core 
{
	public static final String dataFileName = "tasks.data";
	public static final String descFolderName = "tasks.desc";
	public static final byte[] dataSing = { 't', 'a', 's', 'k', 's' };
	private static TaskData data;
	private static File dataFile;
	private static File descFolder;
	
	public static void initializeData(boolean force)
	{
		dataFile = new File(dataFileName);
		descFolder = new File(descFolderName);
	}
	
	public static void LoadData(File file, TaskData data) throws FileNotFoundException, IOException
	{
		DataInputStream inFile = new DataInputStream(new FileInputStream(file));
		inFile.close();
	}
	
	public static void SaveData(String fileName, TaskData data) throws FileNotFoundException
	{
		FileOutputStream outStream = new FileOutputStream(new File(fileName));
		
	}
}
