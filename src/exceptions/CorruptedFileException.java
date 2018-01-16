package exceptions;

import tasks.TaskData;

@SuppressWarnings("serial")
public class CorruptedFileException extends TaskException 
{
	public TaskData data;
	public CorruptedFileException(String message)
	{
		super(message);
		data = null;
	}
	
	public CorruptedFileException(String message, TaskData data)
	{
		super(message);
		this.data = data;
	}
}
