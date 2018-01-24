
package tasks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.FactoryConfigurationError;

import exceptions.CorruptedFileException;
import exceptions.FatalErrorException;
import exceptions.UnsupportedVersionException;
import misc.DataErrorHandle;

public class Core 
{
	public static final String dataFileName = "tasks.data";
	public static final String descFolderName = "tasks.desc";
	
	private static TaskData tasksData = null;
	private static boolean saveData = false; 
	
	private static File dataFile;
	private static File descFolder;
	
	public static void initializeData(boolean force) throws UnsupportedVersionException, CorruptedFileException, FatalErrorException
	{
		dataFile = new File(dataFileName);
		descFolder = new File(descFolderName);
		int loadTry = 0;
		DataErrorHandle errorHandle = DataErrorHandle.none;
		while (true)
		{
			try
			{
				tasksData = Data.LoadData(dataFile);
			}
			catch (FileNotFoundException e)
			{
				tasksData = Data.emptyData();
				break;
			}
			catch (UnsupportedVersionException e)
			{
				if (force)
				{
					errorHandle = DataErrorHandle.backup;
					break;
				}
				else
				{
					throw e;
				}
			}
			catch(CorruptedFileException e)
			{
				if (force)
				{
					errorHandle = DataErrorHandle.delete;
					break;
				}
				else
				{
					throw e;
				}
			}
			catch (IOException e) 
			{
				if (loadTry == 3)
				{
					throw new FatalErrorException("reading data file error: " + e.getMessage());
				}
				loadTry++;
				continue;
			}
		}
		if (errorHandle == DataErrorHandle.backup)
		{
			Data.backupFile(dataFile);
			dataFile = new File(dataFileName);
			saveData = true;
		}
		else if (errorHandle == DataErrorHandle.delete)
		{
			
		}
	}
}
