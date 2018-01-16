package tasks;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import exceptions.AsciiStringException;
import exceptions.CorruptedFileException;
import exceptions.UnsupportedVersionException;
import types.DayWork;
import types.Job;
import types.Project;
import types.ReadableWritableItem;
import types.Task;
import types.Todo;

public class Data 
{
	public static final byte[] dataSing = {'.', 't', 'a', 's', 'k', 's' };
	public static final short curVers = 1;
	
	public static HashMap<Integer, Item> itemIdList = new HashMap<>();
	
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
		public static final byte taskType = 0x00;
		public static final byte jobType = 0x20;
		public static final byte todoType = 0x40;
		public static final byte dayworkType = 0x60;
		public static final byte projectType = (byte)0x80;
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
	
	private static Item readItem(byte infoFlag, DataInputStream stream) throws IOException, AsciiStringException
	{
		switch(infoFlag & 0xE0)
		{
		case infoBits.taskType:
			Task task = new Task();
			task.readFromStream(infoFlag, stream);
			return task;
		case infoBits.jobType:
			Job job = new Job();
			job.readFromStream(infoFlag, stream);
			return job;
		case infoBits.todoType:
			Todo todo = new Todo();
			todo.readFromStream(infoFlag, stream);
			return todo;
		case infoBits.dayworkType:
			DayWork daywork = new DayWork();
			daywork.readFromStream(infoFlag, stream);
			return daywork;
		case infoBits.projectType:
			Project project = new Project();
			project.readFromStream(infoFlag, stream);
			return project;
		default:
			return null;
		}
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
	
	public static void LoadData(File file, TaskData taskData) throws FileNotFoundException, IOException, CorruptedFileException
	{
		DataInputStream inStream = null;
		Reference<DoubleLinkedItem<Item>> itemsReference = new Reference<DoubleLinkedItem<Item>>(null);
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
			readItems(inStream, itemsReference);
		}
		catch(EOFException e)
		{
			throw new CorruptedFileException("End of file while reading");
		}
		catch(CorruptedFileException e)
		{
			
		}
		finally
		{
			if (inStream != null)
			{
				inStream.close();
			}
		}
	}
	
	public static void SaveData(File file, TaskData taskData) throws FileNotFoundException, IOException
	{
		DataOutputStream outStream = null;
		int lastId = 0;
		int skipNumber = 0;
		try
		{
			outStream = new DataOutputStream(new FileOutputStream(file));
			outStream.write(dataSing);
			outStream.writeShort(curVers);
			outStream.writeInt(taskData.items.length());
			for(Item item : taskData.items)
			{
				skipNumber = item.id - lastId;
				lastId = item.id;
				if (item instanceof ReadableWritableItem)
				{
					((ReadableWritableItem) item).writeToStream(skipNumber, outStream);
				}
				else
				{
					throw new ClassCastException("item is not type of " + ReadableWritableItem.class.getName() + ": " + item.getClass().getName());
				}
			}
		}
		finally
		{
			if (outStream != null)
			{
				outStream.close();
			}
		}
	}
}
