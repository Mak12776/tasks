package tasks;

import java.time.LocalDateTime;

public class Job extends Item 
{
	public Task task;
	public boolean isDone;
	public LocalDateTime completionDate;
}
