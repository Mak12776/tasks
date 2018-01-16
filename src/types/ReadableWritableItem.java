package types;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import exceptions.AsciiStringException;

public interface ReadableWritableItem
{
	public void readFromStream(byte infoFlag, DataInputStream stream) throws IOException, AsciiStringException;
	public void writeToStream(int skipNumber, DataOutputStream stream) throws IOException;
}
