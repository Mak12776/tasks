package types;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import tasks.Data;
import tasks.Item;
import tasks.Data.infoBits;

public class Todo extends Item implements ReadableWritableItem
{
	public Task task;
	public boolean isLimited;
	public long startTime;
	public long endTime;
	
	@Override
	public void readFromStream(byte infoFlag, DataInputStream stream) throws IOException
	{
		Data.itemIdList.put(stream.readInt(), this);
		task = null;
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
		if (isLimited)
		{
			stream.writeLong(startTime);
			stream.writeLong(endTime);
		}
	}
}
