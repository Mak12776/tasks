package types;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import exceptions.AsciiStringException;
import tasks.Data;
import tasks.Data.infoBits;
import types.interfaces.ReadableWritableItem;

public class Task extends Item implements ReadableWritableItem 
{
	public Metadata metadata;
	public String title;
	public String descriptionFileName;
	public int linkNumber;
	
	public Task()
	{
		// create new metadata
		metadata = new Metadata();
	}
	
	@Override
	public void readFromStream(byte infoFlag, DataInputStream stream) throws IOException, AsciiStringException 
	{
		// read metadata
		metadata.readFromStream(stream);
		// read title
		title = Data.readAsciiString(stream);
		// read descriptionFileName
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
		// write byte
		if (skipNumber != 0)
		{
			stream.writeByte(infoFlag | infoBits.skipIndex);
			stream.writeInt(skipNumber);
		}
		else
		{
			stream.writeByte(infoFlag);
		}
		metadata.writeToStream(stream);
		Data.writeAsciiString(stream, title);
		if (descriptionFileName != null)
		{
			Data.writeAsciiString(stream, descriptionFileName);
		}
	}
}
