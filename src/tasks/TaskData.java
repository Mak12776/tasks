package tasks;

import java.util.LinkedList;

import types.DayWork;
import types.Item;
import types.Job;
import types.Metadata;
import types.Project;
import types.Task;
import types.Todo;

public class TaskData
{
	public Metadata metadata;
	public DoubleLinkedItem<Item> items;
	public LinkedList<Task> tasks;
	public LinkedList<Job> jobs;
	public LinkedList<Todo> todos;
	public LinkedList<DayWork> dayWorks;
	public LinkedList<Project> projects;
}
