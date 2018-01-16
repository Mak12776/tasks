package tasks;

import java.util.LinkedList;

import types.DayWork;
import types.Job;
import types.Project;
import types.Task;
import types.Todo;

public class TaskData
{
	public DoubleLinkedItem<Item> items;
	public LinkedList<Task> tasks;
	public LinkedList<Job> jobs;
	public LinkedList<Todo> todos;
	public LinkedList<DayWork> dayWorks;
	public LinkedList<Project> projects;
}
