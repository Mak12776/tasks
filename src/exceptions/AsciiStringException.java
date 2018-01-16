package exceptions;

@SuppressWarnings("serial")
public class AsciiStringException extends TaskException 
{
	public AsciiStringException() 
	{
		super();
	}
	public AsciiStringException(String message)
	{
		super(message);
	}
}
