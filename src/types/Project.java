package types;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import exceptions.CorruptedFileException;
import javafx.util.Pair;
import tasks.Data;
import tasks.Data.infoBits;
import types.interfaces.ReadableWritableItem;
import types.interfaces.Taskable;

public class Project extends Item implements ReadableWritableItem, Taskable
{
	public Task task = null;
	public Metadata metadata;
	public boolean ended;
	public long startDay;
	public ArrayList<Long> pauses;
	public long endDay;
	
	public Project()
	{
		// create new Metadata & ArrayList
		metadata = new Metadata();
		pauses = new ArrayList<>(10);
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
	public void readFromStream(byte infoFlag, DataInputStream stream) throws IOException, CorruptedFileException 
	{
		int pauseNumber;
		// read metadata
		metadata.readFromStream(stream);
		// read task id
		Data.itemIdList.add(new Pair<Taskable, Integer>(this, stream.readInt()));
		// read start day
		startDay = stream.readLong();
		// read pauses
		if ((infoFlag & infoBits.projectPause) != 0)
		{
			pauseNumber = stream.readInt();
			if (pauseNumber <= 0)
			{
				throw new CorruptedFileException("invalid Project item pauses number: " + pauseNumber);
			}
			while (pauseNumber != 0)
			{
				pauses.add(stream.readLong());
				pauseNumber--;
			}
		}
		// read end day
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
		// write infoFalg
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
		// write start day
		stream.writeLong(startDay);
		// write pauses
		if (pauses.size() != 0)
		{
			stream.writeInt(pauses.size());
			for (Long date : pauses)
			{
				stream.writeLong(date);
			}
		}
		// write end day
		if (ended)
		{
			stream.writeLong(endDay);
		}
	}
}
