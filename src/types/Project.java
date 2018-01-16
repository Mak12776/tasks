package types;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import exceptions.InvalidIndexException;
import tasks.Item;
import tasks.Data;
import tasks.Data.infoBits;

public class Project extends Item implements ReadableWritableItem
{
	public Task task;
	public boolean ended;
	public long startDay;
	public ArrayList<Long> pauses;
	public long endDay;
	
	public Project()
	{
		pauses = new ArrayList<>(10);
	}
	
	@Override
	public void readFromStream(byte infoFlag, DataInputStream stream) throws IOException 
	{
		int pauseNumber;
		Data.itemIdList.put(stream.readInt(), this);
		startDay = stream.readLong();
		if ((infoFlag & infoBits.projectPause) != 0)
		{
			pauseNumber = stream.readInt();
			if (pauseNumber <= 0)
			{
				throw new InvalidIndexException("invalid project pauses number: " + pauseNumber);
			}
			while (pauseNumber != 0)
			{
				pauses.add(stream.readLong());
				pauseNumber--;
			}
		}
		if ((infoFlag & infoBits.projectEnd) != 0)
		{
			ended = true;
			endDay = stream.readLong();
		}
		else
		{
			ended = false;
		}
	}
	
	@Override
	public void writeToStream(int skipNumber, DataOutputStream stream) throws IOException 
	{
		byte infoFlag = infoBits.projectType;
		if (ended)
		{
			infoFlag |= infoBits.projectEnd;
		}
		if (pauses.size() != 0)
		{
			infoFlag |= infoBits.projectPause;
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
		stream.writeLong(startDay);
		if (pauses.size() != 0)
		{
			stream.writeInt(pauses.size());
			for (Long date : pauses)
			{
				stream.writeLong(date);
			}
		}
		if (ended)
		{
			stream.writeLong(endDay);
		}
	}
}
