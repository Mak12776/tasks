package types;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import exceptions.AsciiStringException;
import tasks.Data;
import tasks.Item;
import tasks.Data.infoBits;

public class Task extends Item implements ReadableWritableItem 
{
	public long creationTime;
	public long lastModificationTime;
	public String title;
	public String descriptionFileName;
	public int linkNumber;
	
	@Override
	public void readFromStream(byte infoFlag, DataInputStream stream) throws IOException, AsciiStringException 
	{
		creationTime = stream.readLong();
		lastModificationTime = stream.readLong();
		title = Data.readAsciiString(stream);
		if ((infoFlag & infoBits.taskDescriptor) != 0)
		{
			descriptionFileName = Data.readAsciiString(stream);
		}
	}
	
	@Override
	public void writeToStream(int skipNumber, DataOutputStream stream) throws IOException
	{
		byte infoFlag = infoBits.taskType;
		if (descriptionFileName != null)
		{
			infoFlag |= infoBits.taskDescriptor;
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
		stream.writeLong(creationTime);
		stream.writeLong(lastModificationTime);
		Data.writeAsciiString(stream, title);
		if (descriptionFileName != null)
		{
			Data.writeAsciiString(stream, descriptionFileName);
		}
	}
}
