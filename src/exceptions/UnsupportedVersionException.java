package exceptions;

@SuppressWarnings("serial")
public class UnsupportedVersionException extends CorruptedFileException
{
	public UnsupportedVersionException(String message)
	{
		super(message);
	}
}
