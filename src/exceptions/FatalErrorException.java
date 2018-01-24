package exceptions;

@SuppressWarnings("serial")
public class FatalErrorException extends TaskException 
{
	public FatalErrorException(String message)
	{
		super(message);
	}
}
