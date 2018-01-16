package exceptions;

@SuppressWarnings("serial")
public class ArgumentParsingException extends TaskException
{
	public ArgumentParsingException(String message)
	{
		super(message);
	}
}
