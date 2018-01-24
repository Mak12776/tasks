package types;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javafx.util.Pair;
import tasks.Data;
import tasks.Data.infoBits;
import types.interfaces.ReadableWritableItem;
import types.interfaces.Taskable;

public class Todo extends Item implements ReadableWritableItem, Taskable
{
	public Metadata metadata;
	public Task task = null;
	public boolean isLimited;
	public long startTime;
	public long endTime;
	
	public Todo()
	{
		// create new metadata
		metadata = new Metadata();
	}
	
	@Override
	public Task getTask() 
	{
		return this.task;
	}
	
	@Override
	public void setTask(Task task) 
	{
		this.task = task;
	}
	
	@Override
	public void readFromStream(byte infoFlag, DataInputStream stream) throws IOException
	{
		// reading metadata
		metadata.readFromStream(stream);
		// reading task id
		Data.itemIdList.add(new Pair<Taskable, Integer>(this, stream.readInt()));
		// reading startTime, endTime
		if ((infoFlag & infoBits.todoLimited) != 0)
		{
			isLimited = true;
			startTime = stream.readLong();
			endTime = stream.readLong();
		}
		else
		{
			isLimited = false;
		}
	}
	
	@Override
	public void writeToStream(int skipNumber, DataOutputStream stream) throws IOException 
	{
		byte infoFlag = infoBits.todoType;
		if (isLimited)
		{
			infoFlag |= infoBits.todoLimited;
		}
		// write infoFlag
		if (skipNumber != 0)
		{
			stream.writeByte(infoFlag | infoBits.skipIndex);
			stream.writeInt(skipNumber);
		}
		else
		{
			stream.writeByte(infoFlag);
		}
		// write metadata
		metadata.writeToStream(stream);
		// write task id
		stream.writeInt(task.id);
		// write startTime, endTime
		if (isLimited)
		{
			stream.writeLong(startTime);
			stream.writeLong(endTime);
		}
	}
}
