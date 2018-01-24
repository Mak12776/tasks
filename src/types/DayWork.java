package types;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javafx.util.Pair;
import tasks.Data;
import tasks.Data.infoBits;
import types.interfaces.ReadableWritableItem;
import types.interfaces.Taskable;

public class DayWork extends Item implements ReadableWritableItem, Taskable
{
	public Task task = null;
	public Metadata metadata;
	public long dayDate;
	public long repeat;
	public long counting;
	
	public DayWork() 
	{
		// create new Metadata
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
		// read metadata
		metadata.readFromStream(stream);
		// read task id
		Data.itemIdList.add(new Pair<Taskable, Integer>(this, stream.readInt()));
		// read day date
		dayDate = stream.readLong();
		// read repeat
		if ((infoFlag & infoBits.dayworkRepeat) != 0)
		{
			repeat = stream.readLong();
		}
		// read count
		if ((infoFlag & infoBits.daywrokCount) != 0)
		{
			counting = stream.readLong();
		}
	}
	
	@Override
	public void writeToStream(int skipNumber, DataOutputStream stream) throws IOException 
	{
		byte infoFlag = infoBits.dayworkType;
		if (repeat != 0)
		{
			infoFlag |= infoBits.dayworkRepeat;
		}
		if (counting != 0)
		{
			infoFlag |= infoBits.daywrokCount;
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
		// write repeat
		if (repeat != 0)
		{
			stream.writeLong(repeat);
		}
		// write count
		if (counting != 0)
		{
			stream.writeLong(counting);
		}
	}
	
	
}
