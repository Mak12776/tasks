package exceptions;

@SuppressWarnings("serial")
public class TaskRuntimeException extends RuntimeException 
{
	public TaskRuntimeException()
	{
		super();
	}
	public TaskRuntimeException(String message)
	{
		super(message);
	}
}
