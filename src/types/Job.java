package types;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import tasks.Data;
import tasks.Item;
import tasks.Data.infoBits;

public class Job extends Item implements ReadableWritableItem 
{
	public Task task;
	public boolean isDone;
	public long completionDate;
	
	@Override
	public void readFromStream(byte infoFlag, DataInputStream stream) throws IOException 
	{
		Data.itemIdList.put(stream.readInt(), this);
		task = null;
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
		if (skipNumber != 0)
		{
			stream.writeByte(infoFlag | infoBits.skipIndex);
			stream.writeInt(skipNumber);
		}
		else
		{
			stream.writeByte(infoFlag);
		}
		stream.writeInt(task.id);
		if (isDone)
		{
			stream.writeLong(completionDate);
		}
	}
}
