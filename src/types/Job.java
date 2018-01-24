package types;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javafx.util.Pair;
import tasks.Data;
import tasks.Data.infoBits;
import types.interfaces.ReadableWritableItem;
import types.interfaces.Taskable;

public class Job extends Item implements ReadableWritableItem, Taskable
{
	public Metadata metadata;
	public Task task = null;
	public boolean isDone;
	public long completionDate;
	
	public Job()
	{
		// create new metadata
		metadata = new Metadata();
	}
	
	@Override
	public Task getTask() 
	{
		return task;
	}
	
	@Override
	public void setTask(Task task) 
	{
		this.task = task;
	}
	
	@Override
	public void readFromStream(byte infoFlag, DataInputStream stream) throws IOException 
	{
		// read metadata
		metadata.readFromStream(stream);
		// read task id
		Data.itemIdList.add(new Pair<Taskable, Integer>(this, stream.readInt()));
		// read completion date
		if ((infoFlag & infoBits.jobDone) != 0)
		{
			isDone = true;
			completionDate = stream.readLong();
		}
		else
		{
			isDone = false;
		}
	}
	
	@Override
	public void writeToStream(int skipNumber, DataOutputStream stream) throws IOException 
	{
		byte infoFlag = infoBits.jobType;
		if (isDone)
		{
			infoFlag |= infoBits.jobDone;
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
		// write completion date
		if (isDone)
		{
			stream.writeLong(completionDate);
		}
	}
}
