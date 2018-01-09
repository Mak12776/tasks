package tasks;

public class Task extends Item 
{
	public Task(String title)
	{
		this.title = title; 
	}
	
	public long creationTime;
	public long lastModificationTime;
	public String title;
	public String descriptionFileName;
	public int linkNumber;
}
