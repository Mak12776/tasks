package tasks;


import java.util.HashMap;
import java.util.LinkedList;

import exceptions.ArgumentParsingException;

public class Parser
{
	private static void printHelp()
	{
		// TODO: here we write help info to stdout
		System.out.println("help of program");
	}
	
	public static boolean force = false;
	public static boolean runCommandLine = false;
	public static boolean quiet = false;
	public static boolean yes = false;
	
	public enum CommandOrder
	{
		none,
		add, delete, edit, clear,
		show, list,
		optimize, pruge, purgeAndOptimze,
	}
	public static CommandOrder command = CommandOrder.none;
	
	public enum ItemType {none, task, job, todo, daywork}
	public static ItemType itemType = ItemType.none;
	private static final String[] longTypeNames = {"task", "job", "todo", "daywork"};
	private static final String[] shortTypeNames = {"ts", "jb", "td", "dw"};
	
	private enum ArgType
	{
		itemType,
		title,
		identity,
		date,
		from_date,
		until_date,
		repeat,
		count,
	}
	private static LinkedList<ArgType> nextArgTypes = new LinkedList<ArgType>();
	public static HashMap<String, Object> itemInfos = new HashMap<String, Object>();
	
	private static void addItemInfo(String key, Object value) throws ArgumentParsingException
	{
		if (itemInfos.containsKey(key))
		{
			throw new ArgumentParsingException("double info for " + key + ": " + itemInfos.get(key) + ", " + value);
		}
		itemInfos.put(key, value);
	}
	
	private static void setItemInfo(String key, Object value)
	{
		itemInfos.put(key, value);
	}
	
	private static void setCommandOrder(CommandOrder cmd) throws ArgumentParsingException
	{
		if (command != CommandOrder.none)
		{
			throw new ArgumentParsingException("Double command: " + command + ", " + cmd);
		}
		command = cmd;
	}
	
	private static void setItemType(ItemType type) throws ArgumentParsingException
	{
		if (itemType != ItemType.none)
		{
			throw new ArgumentParsingException("Double item type: " + itemType + ". " + type);
		}
		itemType = type;
	}
	
	private static void setItemType(int i) throws ArgumentParsingException
	{
		setItemType(ItemType.values()[i]);
	}
	
	private static boolean checkLongTypeNames(String str) throws ArgumentParsingException
	{
		for (int i = 0; i < longTypeNames.length; i++)
		{
			if (str.equals(longTypeNames[i]))
			{
				setItemType(i + 1);
				return true;
			}
		}
		return false;
	}
	
	private static boolean checkShortTypeNames(String str) throws ArgumentParsingException
	{
		for (int i = 0; i < shortTypeNames.length; i++)
		{
			if (str.equals(shortTypeNames[i]))
			{
				setItemType(i + 1);
				return true;
			}
		}
		return false;
	}
	
	private static boolean checkLongGlobalOption(String option)
	{
		if (option.equals("force"))
		{
			force = true;
			return true;
		}
		if (option.equals("quiet"))
		{
			quiet = true;
			return true;
		}
		if (option.equals("command-line"))
		{
			runCommandLine = true;
			return true;
		}
		if (option.equals("yes"))
		{
			yes = true;
			return true;
		}
		return false;
	}
	
	private static boolean checkShortGlobalOption(char ch)
	{
		if (ch == 'F')
		{
			force = true;
			return true;
		}
		if (ch == 'Q')
		{
			quiet = true;
			return true;
		}
		if (ch == 'C')
		{
			runCommandLine = true;
			return true;
		}
		if (ch == 'Y')
		{
			yes = true;
			return true;
		}
		return false;
	}
	
	private static boolean checkLongInfoOption(String option)
	{
		switch(itemType)
		{
		case task:
			if (option.equals("title"))
			{
				nextArgTypes.add(ArgType.title);
				return true;
			}
		case job:
			if (option.equals("identity"))
			{
				nextArgTypes.add(ArgType.identity);
				return true;
			}
			if (option.equals("title"))
			{
				nextArgTypes.add(ArgType.title);
				return true;
			}
			if (option.equals("completed"))
			{
				setItemInfo("completed", true);
				return true;
			}
			if (option.equals("date"))
			{
				nextArgTypes.add(ArgType.date);
				return true;
			}
			break;
		case todo:
			if (option.equals("identity"))
			{
				nextArgTypes.add(ArgType.identity);
				return true;
			}
			if (option.equals("title"))
			{
				nextArgTypes.add(ArgType.title);
				return true;
			}
			if (option.equals("since"))
			{
				nextArgTypes.add(ArgType.from_date);
				return true;
			}
			if (option.equals("until"))
			{
				nextArgTypes.add(ArgType.until_date);
				return true;
			}
			break;
		case daywork:
			if (option.equals("identity"))
			{
				nextArgTypes.add(ArgType.identity);
				return true;
			}
			if (option.equals("title"))
			{
				nextArgTypes.add(ArgType.title);
				return true;
			}
			if (option.equals("repeat"))
			{
				nextArgTypes.add(ArgType.repeat);
				return true;
			}
			if (option.equals("count"))
			{
				nextArgTypes.add(ArgType.count);
				return true;
			}
		case none:
			throw new RuntimeException("wrong behavior on option Argument");
		}
		return false;
	}
	
	private static boolean checkShortInfoOption(char ch)
	{
		switch(itemType)
		{
		case task:
			if (ch == 't')
			{
				nextArgTypes.add(ArgType.title);
				return true;
			}
		case job:
			if (ch == 't')
			{
				nextArgTypes.add(ArgType.title);
				return true;
			}
			if (ch == 'i')
			{
				nextArgTypes.add(ArgType.identity);
				return true;
			}
			if (ch == 'c')
			{
				setItemInfo("completed", true);
				return true;
			}
			if (ch == 'd')
			{
				nextArgTypes.add(ArgType.date);
				return true;
			}
		case todo:
			if (ch == 'i')
			{
				nextArgTypes.add(ArgType.identity);
				return true;
			}
			if (ch == 't')
			{
				nextArgTypes.add(ArgType.title);
				return true;
			}
			if (ch == 's')
			{
				nextArgTypes.add(ArgType.from_date);
				return true;
			}
			if (ch == 'u')
			{
				nextArgTypes.add(ArgType.until_date);
				return true;
			}
		case daywork:
			if (ch == 'i')
			{
				nextArgTypes.add(ArgType.title);
				return true;
				
			}
			if (ch == 't')
			{
				nextArgTypes.add(ArgType.title);
				return true;
			}
			if (ch == 'r')
			{
				nextArgTypes.add(ArgType.repeat);
				return true;
			}
			if (ch == 'c')
			{
				nextArgTypes.add(ArgType.count);
				return true;
			}
		case none:
			throw new RuntimeException("wrong behavior in ch argument");
		}
		return false;
	}
	
	public static boolean parseArguments(String[] args) throws ArgumentParsingException
	{
		String subStr;
		char Ch;
		for (int index = 0; index < args.length; index++)
		{
			if (!nextArgTypes.isEmpty())
			{
				switch(nextArgTypes.poll())
				{
				case itemType:
					if (checkLongTypeNames(args[index]))
					{
						continue;
					}
					if (checkShortTypeNames(args[index]))
					{
						continue;
					}
					throw new ArgumentParsingException("invalid item type: " + args[index]);
				case title:
					addItemInfo("title", args[index].trim());
					continue;
				}
			}
			if (args[index].length() == 0)
			{
				throw new ArgumentParsingException("empty argument");
			}
			switch(args[index].charAt(0))
			{
			case '-':
				if (args[index].length() == 1)
				{
					throw new ArgumentParsingException("unknown option: " + args[index]);
				}
				if (args[index].charAt(1) == '-')
				{
					subStr = args[index].substring(2).toLowerCase();
					if (subStr.isEmpty())
					{
						throw new ArgumentParsingException("unknown option: " + args[index]);
					}
					if (checkLongGlobalOption(subStr))
					{
						continue;
					}
					if (subStr.equals("help"))
					{
						printHelp();
						return false;
					}
					if (command == CommandOrder.add)
					{
						if (checkLongInfoOption(subStr))
						{
							continue;
						}
					}
					throw new ArgumentParsingException("invalid long option: " + subStr);
				} /*  if (args[index].charAt(1) == '-')  */
				else
				{
					subStr = args[index].substring(1);
					for (int k = 0; k < subStr.length(); k++)
					{
						Ch = subStr.charAt(k);
						if ('A' <= Ch && Ch <= 'Z')
						{
							if (checkShortGlobalOption(Ch))
							{
								continue;
							}
							if (Ch == 'H')
							{
								printHelp();
								return false;
							}
							throw new ArgumentParsingException("invalid short global option: " + Ch);
						}
						if ('a' <= Ch && Ch <= 'z')
						{
							if (command == CommandOrder.add)
							{
								if (checkShortInfoOption(Ch))
								{
									continue;
								}
							} 
							throw new ArgumentParsingException("invalid short option: " + Ch);
						} 
						throw new ArgumentParsingException("invalid short option character: " + Ch);
					} /* for (int k = 0; k < subStr.length(); k++) */
					continue;
				}
				
			case 'A':
			case 'a':
				subStr = args[index].substring(1).toLowerCase();
				setCommandOrder(CommandOrder.add);
				if (subStr.isEmpty() || subStr.equals("dd"))
				{
					nextArgTypes.add(ArgType.itemType);
					continue;
				}
				if (checkShortTypeNames(subStr))
				{
					continue;
				}
				break;
			} /* switch(args[index].charAt(0)) */
			
			throw new ArgumentParsingException("invalid command: " + args[index]);
		} /* for (int index = 0; index < args.length; index++) */
		return true;
	}
}
