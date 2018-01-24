package tasks;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import exceptions.AsciiStringException;
import exceptions.CorruptedFileException;
import exceptions.NoSuchItemException;
import exceptions.TaskRuntimeException;
import exceptions.UnsupportedVersionException;
import javafx.util.Pair;
import types.DayWork;
import types.Item;
import types.Job;
import types.Metadata;
import types.Project;
import types.Task;
import types.Todo;
import types.interfaces.ReadableWritableItem;
import types.interfaces.Taskable;

public class Data 
{
	public static final byte[] dataSing = {'.', 't', 'a', 's', 'k', 's' };
	public static final short curVers = 1;
	
	public static LinkedList<Pair<Taskable, Integer>> itemIdList = new LinkedList<>();
	
	public static class infoBits
	{
		public static final byte taskDescriptor = 0x01;
		public static final byte todoLimited = 0x01;
		public static final byte jobDone = 0x01;
		public static final byte dayworkRepeat = 0x01;
		public static final byte daywrokCount = 0x02;
		public static final byte projectEnd = 0x01;
		public static final byte projectPause = 0x02;
		
		public static final byte skipIndex = 0x10;
		public static final byte typeMask = (byte)0xE0;
		public static final byte taskType = 0x00;
		public static final byte jobType = 0x20;
		public static final byte todoType = 0x40;
		public static final byte dayworkType = 0x60;
		public static final byte projectType = (byte)0x80;
	}
	
	public static void fixItemId(DoubleLinkedItem<Item> items)
	{
		Pair<Taskable, Integer> pair;
		Item item;
		Iterator<Pair<Taskable, Integer>> iterator = itemIdList.iterator(); 
		while (iterator.hasNext())
		{
			pair = iterator.next();
			try
			{
				item = items.getItem(pair.getValue());
			}
			catch (NoSuchItemException e)
			{
				continue;
			}
			if (item instanceof Task)
			{
				pair.getKey().setTask((Task) item);
				iterator.remove();
			}
		}
	}
	
	private static void fixTaskData(TaskData data)
	{
		for (Item item : data.items)
		{
			if (item instanceof Task)
			{
				data.tasks.add((Task) item);
			}
			else if (item instanceof Job)
			{
				data.jobs.add((Job) item);
			}
			else if (item instanceof Todo)
			{
				data.todos.add((Todo) item);
			}
			else if (item instanceof DayWork)
			{
				data.dayWorks.add((DayWork) item);
			}
			else if (item instanceof Project)
			{
				data.projects.add((Project) item);
			}
			else
			{
				throw new ClassCastException("unknown item type: " + item.getClass().getName());
			}
		}
	}
	
	public static void writeAsciiString(DataOutputStream stream, String string) throws IOException
	{
		for (int i = 0; i < string.length(); i++)
		{
			stream.writeByte(0xFF & string.charAt(i));
		}
		stream.writeByte(0);
	}
	
	public static String readAsciiString(DataInputStream stream) throws EOFException, IOException, AsciiStringException
	{
		byte b;
		StringBuilder result = new StringBuilder();
		b = stream.readByte();
		if ((b < (byte)' ') || (b > (byte)'~'))
		{
			throw new AsciiStringException("invalid ascii byte: " + b);
		}
		while (true)
		{
			b = stream.readByte();
			if (b == 0)
			{
				return result.toString();
			}
			if ((b < (byte)' ') || (b > (byte)'~'))
			{
				throw new AsciiStringException("invalid ascii byte: " + b);
			}
			result.append((char)b);
		}
	}
	
	private static Item readItem(byte infoFlag, DataInputStream stream) throws IOException, AsciiStringException, CorruptedFileException
	{
		ReadableWritableItem result;
		switch(infoFlag & infoBits.typeMask)
		{
		case infoBits.taskType:
			result = new Task();
			break;
		case infoBits.jobType:
			result = new Job();
			break;
		case infoBits.todoType:
			result = new Todo();
			break;
		case infoBits.dayworkType:
			result = new DayWork();
			break;
		case infoBits.projectType:
			result = new Project();
			break;
		default:
			throw new CorruptedFileException("invalid item type flag: " + (infoFlag >> 5));
		}
		result.readFromStream(infoFlag, stream);
		return (Item)result;
	}
	
	private static void readItems(DataInputStream stream, Reference<DoubleLinkedItem<Item>> result) throws CorruptedFileException, IOException 
	{
		DoubleLinkedItem.Creator<Item> resultCreator = new DoubleLinkedItem.Creator<>();
		int itemNumber;
		byte infoFlag;
		Item item;
		int skipNumber;
		try
		{
			itemNumber = stream.readInt();
			if (itemNumber < 0)
			{
				throw new CorruptedFileException("invalid item number");
			}
			while (itemNumber != 0)
			{
				infoFlag = stream.readByte();
				if ((infoFlag & infoBits.skipIndex) != 0)
				{
					skipNumber = stream.readInt();
					if (skipNumber <= 0)
					{
						throw new CorruptedFileException("invalid skip number");
					}
					resultCreator.skip(skipNumber);
				}
				item = readItem(infoFlag, stream);
				resultCreator.add(item);
				itemNumber--;
			}
		}
		catch (EOFException e)
		{
			throw new CorruptedFileException("End of file while reading");
		}
		catch (AsciiStringException e)
		{
			throw new CorruptedFileException("invalid ascii string.");
		}
		finally
		{
			resultCreator.finish();
			result.value = resultCreator.getResult();
		}
	}
	
	public static TaskData LoadData(File file) throws FileNotFoundException, IOException, CorruptedFileException
	{
		DataInputStream inStream = null;
		Reference<DoubleLinkedItem<Item>> itemsReference = new Reference<DoubleLinkedItem<Item>>(null);
		Metadata tempMetadata = new Metadata();
		TaskData result;
		byte [] sign = new byte[dataSing.length];
		short version;
		try
		{
			inStream = new DataInputStream(new FileInputStream(file));
			inStream.readFully(sign);
			if (!sign.equals(dataSing))
			{
				throw new CorruptedFileException("file signature mismatch.");
			}
			version = inStream.readShort();
			if (version != curVers)
			{
				throw new UnsupportedVersionException("unsupported data version: " + version);
			}
			tempMetadata.readFromStream(inStream);
			try
			{
				readItems(inStream, itemsReference);
			}
			catch (CorruptedFileException e)
			{
				e.data = new TaskData();
				e.data.items = itemsReference.value;
				fixItemId(e.data.items);
				fixTaskData(e.data);
				throw e;
			}
		}
		catch(EOFException e)
		{
			throw new CorruptedFileException("End of file while reading");
		}
		finally
		{
			if (inStream != null)
			{
				try
				{
					inStream.close();
				}
				catch(IOException e)
				{
					
				}
			}
		}
		result = new TaskData();
		result.items = itemsReference.value;
		fixItemId(result.items);
		fixTaskData(result);
		return result;
		
	}
	
	private static void writeItems(DataOutputStream stream, DoubleLinkedItem<Item> items) throws IOException
	{
		int skipNumber = 0;
		int lastId = 0;
		stream.writeInt(items.length());
		for (Item item : items)
		{
			skipNumber = item.id - lastId;
			lastId = item.id;
			if (item instanceof ReadableWritableItem)
			{
				((ReadableWritableItem) item).writeToStream(skipNumber, stream);
			}
			else
			{
				throw new ClassCastException("item type is not " + ReadableWritableItem.class.getName() + ": " + item.getClass().getName());
			}
		}
	}
	
	public static void SaveData(File file, TaskData taskData) throws IOException
	{
		DataOutputStream outStream = null;
		try
		{
			outStream = new DataOutputStream(new FileOutputStream(file));
			outStream.write(dataSing);
			outStream.writeShort(curVers);
			taskData.metadata.writeToStream(outStream);
			writeItems(outStream, taskData.items);
		}
		finally
		{
			if (outStream != null)
			{
				outStream.close();
			}
		}
	}
	
	public static TaskData emptyData()
	{
		TaskData result = new TaskData();
		result.metadata = Metadata.currentMetadata();
		result.items = new DoubleLinkedItem<Item>();
		result.todos = new LinkedList<>();
		result.jobs = new LinkedList<>();
		result.dayWorks = new LinkedList<>();
		result.projects = new LinkedList<>();
		return result;
	}
	
	public static void deleteFile(File file)
	{
		if (!file.delete())
		{
			
		}
	}
	
	public static void backupFile(File file)
	{
		int number = 0;
		List<String> fileNames = Arrays.asList(file.getParentFile().list());
		String backupName = file.getName() + ".bak" + number;
		while (fileNames.contains(backupName))
		{
			backupName = file.getName() + ".bak" + (++number);
		}
		if (!file.renameTo(new File(backupName)))
		{
			throw new TaskRuntimeException("Can't rename file.");
		}
	}
}
