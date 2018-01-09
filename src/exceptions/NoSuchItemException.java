package exceptions;

@SuppressWarnings("serial")
public class NoSuchItemException extends TaskException
{
	public NoSuchItemException()
	{
		super();
	}
	public NoSuchItemException(String message) 
	{
		super(message);
	}
}
