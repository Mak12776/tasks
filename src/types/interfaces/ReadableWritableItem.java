package types.interfaces;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import exceptions.AsciiStringException;
import exceptions.CorruptedFileException;

public interface ReadableWritableItem
{
	public void readFromStream(byte infoFlag, DataInputStream stream) throws IOException, AsciiStringException, CorruptedFileException;
	public void writeToStream(int skipNumber, DataOutputStream stream) throws IOException;
}
