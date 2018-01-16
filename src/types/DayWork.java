package types;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import tasks.Data;
import tasks.Item;
import tasks.Data.infoBits;

public class DayWork extends Item implements ReadableWritableItem
{
	Task task;
	public long dayDate;
	public long repeat;
	public long counting;
	
	@Override
	public void readFromStream(byte infoFlag, DataInputStream stream) throws IOException 
	{
		Data.itemIdList.put(stream.readInt(), this);
		dayDate = stream.readLong();
		if ((infoFlag & infoBits.dayworkRepeat) != 0)
		{
			repeat = stream.readLong();
		}
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
		if (counting != 0)
		{
			stream.writeLong(counting);
		}
	}
	
	
}
