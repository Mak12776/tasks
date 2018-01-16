package exceptions;

@SuppressWarnings("serial")
public class InvalidIndexException extends TaskRuntimeException
{
	public InvalidIndexException()
	{
		super();
	}
	public InvalidIndexException(String message) 
	{
		super(message);
	}
	public InvalidIndexException(int index)
	{
		super("invalid index: " + index);
	}
}
