package types;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class Metadata 
{
	public long creationTime;
	public long lastModificationTime;
	
	public static Metadata currentMetadata()
	{
		Metadata result = new Metadata();
		result.lastModificationTime = (result.creationTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
		return result;
	}
	
	public void readFromStream(DataInputStream stream) throws IOException
	{
		creationTime = stream.readLong();
		lastModificationTime = stream.readLong();
	}
	public void writeToStream(DataOutputStream stream) throws IOException
	{
		stream.writeLong(creationTime);
		stream.writeLong(lastModificationTime);
	}
}
