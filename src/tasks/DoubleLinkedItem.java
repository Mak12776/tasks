package tasks;


import exceptions.FinishedCreatorException;
import exceptions.InvalidIndexException;
import exceptions.NoSuchItemException;

public class DoubleLinkedItem<T extends Item>
{
	class Node
	{
		private Item[] array;
		private int length;
		private Node prev = null;
		private Node next = null;
		
		public Node()
		{
			arrayNumber++;
			array = new Item[arraySize];
			length = 0;
		}
		
		public Node(Node prev)
		{
			this();
			this.prev = prev;
		}
		
		public Node(T item)
		{
			arrayNumber++;
			array = new Item[arraySize];
			array[0] = item;
			length = 1;
			itemNumber++;
		}
		
		public Node(Node prev, T item)
		{
			this(item);
			this.prev = prev;
		}
		
		public boolean add(int baseIndex, Item item)
		{
			if (length == arraySize)
			{
				return false;
			}
			for (int i = 0; i < arraySize; i++)
			{
				if (array[i] == null)
				{
					item.id = baseIndex + i;
					array[i] = item;
					length++;
					itemNumber++;
					break;
				}
			}
			return true;
		}
		
		public void set(int index, Item item)
		{
			if (array[index] == null)
			{
				length++;
				itemNumber++;
			}
			array[index] = item;
		}
		
		public Item get(int index) throws NoSuchItemException
		{
			if (array[index] == null)
			{
				throw new NoSuchItemException();
			}
			return array[index];
		}
		
		public boolean del(int index)
		{
			if (array[index] == null)
			{
				return false;
			}
			array[index] = null;
			itemNumber--;
			length--;
			return true;
		}
	}
	
	private Node first;
	private int arraySize;
	private int itemNumber;
	private int arrayNumber;
	
	public DoubleLinkedItem()
	{
		arraySize = 256;
		first = null;
		itemNumber = 0;
		arrayNumber = 0;
	}
	
	public DoubleLinkedItem(int arraySize)
	{
		this.arraySize = arraySize;
		first = null;
		itemNumber = 0;
		arrayNumber = 0;
	}
	
	public void addItem(T item)
	{
		if (first == null)
		{
			item.id = 0;
			first = new Node(item);
		}
		else
		{
			Node temp = first;
			if (temp.add(0, item))
			{
				return;
			}
			int baseIndex = 0;
			while (temp.next != null)
			{
				baseIndex += arraySize;
				temp = temp.next;
				if (temp.add(baseIndex, item))
				{
					return;
				}
			}
			item.id = baseIndex + arraySize;
			temp.next = new Node(temp, item);
		}
	}
	
	public void setItem(int index, T item)
	{
		if (item == null)
		{
			throw new NullPointerException("item argument is null.");
		}
		int base = index / arraySize;
		if (base < arrayNumber)
		{
			Node temp = first;
			while (base != 0)
			{
				temp = temp.next;
				base--;
			}
			item.id = index;
			temp.set(index % arraySize, item);
		}
		else
		{
			throw new InvalidIndexException(index);
		}
	}
	
	public Object getItem(int index) throws NoSuchItemException
	{
		int base = index / arraySize;
		if (base < arrayNumber)
		{
			Node temp = first;
			while (base != 0)
			{
				temp = temp.next;
				base--;
			}
			return temp.get(index % arraySize);
		}
		else
		{
			throw new InvalidIndexException(index);
		}
	}
	
	public boolean removeItem(int index)
	{
		int base = index / arraySize;
		if (base < arrayNumber)
		{
			Node temp = first;
			while (base != 0)
			{
				temp = temp.next;
				base --;
			}
			return temp.del(index % arraySize);
		}
		else
		{
			throw new InvalidIndexException(index);
		}
	}
	
	public int getLength()
	{
		return itemNumber;
	}
	
	public int getArraySize()
	{
		return arraySize;
	}
	
	public int getArrayNumber()
	{
		return arrayNumber;
	}
	
	public void resetIds()
	{
		Node temp = first;
		int baseIndex = 0;
		while (temp != null)
		{
			for (int i = 0; i < arraySize; i++)
			{
				if (temp.array[i] != null)
				{
					temp.array[i].id = baseIndex + i;
				}
			}
			temp = temp.next;
			baseIndex += arraySize;
		}
	}
	
	public int optimizeEffect()
	{
		if (first == null)
		{
			return 0;
		}
		return ((arrayNumber * arraySize) - itemNumber) / arraySize;
	}
	
	public void optimize()
	{
		if (first == null)
		{
			return;
		}
		int remaining = (arrayNumber * arraySize) - itemNumber; 
		if (remaining < arraySize)
		{
			return;
		}
		int index = 0, maxIndex = arraySize - 1, lastIndex = maxIndex;
		Node firstNode = first, lastNode = first;
		while (lastNode.next != null)
		{
			lastNode = lastNode.next;
		}
		outerWhileLoop:
		while (firstNode != lastNode)
		{
			for (; index < arraySize; index++)
			{
				if (firstNode.array[index] == null)
				{
					while (lastNode.array[lastIndex] == null)
					{
						if (--lastIndex < 0)
						{
							lastNode = lastNode.prev;
							if (firstNode == lastNode)
							{
								break outerWhileLoop;
							}
							lastIndex = maxIndex;
						}
					}
					firstNode.array[index] = lastNode.array[lastIndex];
					lastNode.array[lastIndex] = null;
					firstNode.length++;
					lastNode.length--;
				}
			}
			firstNode = firstNode.next;
			index = 0;
		}
		lastIndex = maxIndex;
		outerWhileLoop:
		while (index < lastIndex)
		{
			if (firstNode.array[index] == null)
			{
				while (firstNode.array[lastIndex] == null)
				{
					if (--lastIndex == index)
					{
						break outerWhileLoop;
					}
				}
				firstNode.array[index] = firstNode.array[lastIndex];
				firstNode.array[lastIndex] = null;
			}
			index++;
		}
		if (firstNode.length == 0)
		{
			firstNode.prev.next = null;
			firstNode.prev = null;
		}
		else if (firstNode.next != null)
		{
			firstNode.next.prev = null;
			firstNode.next = null;
		}
		arrayNumber -= remaining / arraySize;
		resetIds();
	}
	
	public static class Creator<E extends Item>
	{
		private DoubleLinkedItem<E> result;
		private DoubleLinkedItem<E>.Node lastNode = null;
		private int index;
		private int skippedNumber;
		private int allSkippedNumber;
		private boolean finished;
		
		public Creator()
		{
			result = new DoubleLinkedItem<E>();
			finished = false;
		}
		
		public Creator(int arraySize)
		{
			result = new DoubleLinkedItem<E>(arraySize);
			finished = false;
		}
		
		@SuppressWarnings("Item.id")
		public void add(E item)
		{
			if (finished)
			{
				throw new FinishedCreatorException();
			}
			if (lastNode != null)
			{
				if (index == result.arraySize)
				{
					lastNode.length = index - skippedNumber;
					skippedNumber = 0;
					lastNode = (lastNode.next = result.new Node(lastNode));
					lastNode.array[0] = item;
					index = 1;
				}
				else
				{
					lastNode.array[index++] = item;
				}
			}
			else
			{
				lastNode = (result.first = result.new Node());
				lastNode.array[0] = item;
				index = 1;
			}
		}
		
		public void skip(int num)
		{
			if (num <= 0)
			{
				// TODO: throw exception instead of return
				return;
			}
			if (finished)
			{
				throw new FinishedCreatorException();
			}
			if (lastNode == null)
			{
				int baseIndex = num / result.arraySize;
				lastNode = (result.first = result.new Node());
				while (baseIndex != 0)
				{
					baseIndex--;
					lastNode = (lastNode.next = result.new Node());
				}
				skippedNumber = (index = num % result.arraySize);
				allSkippedNumber = num;
			}
			else
			{
				int baseIndex = (index + num) / result.arraySize;
				lastNode.length = index - skippedNumber;
				index += num;
				while (baseIndex != 0)
				{
					baseIndex--;
					lastNode = (lastNode.next = result.new Node());
				}
				skippedNumber = (index = index % result.arraySize) - lastNode.length;
				allSkippedNumber += num;
			}
		}
		
		public void finish()
		{
			if (finished)
			{
				return;
			}
			if (lastNode != null)
			{
				lastNode.length = index - skippedNumber;
				result.itemNumber = ((result.arrayNumber-1) * result.arraySize) + index - allSkippedNumber;
			}
			finished = true;
		}
		
		public DoubleLinkedItem<E> getResult()
		{
			if (!finished)
			{
				return null;
			}
			return result;
		}
	}
	
	public void print()
	{
		int nodeNum = 0;
		Node temp = first;
		while (temp != null)
		{
			System.out.println("Node " + String.valueOf(nodeNum) + " [length: " + String.valueOf(temp.length) + "]");
			for (int i = 0; i < arraySize; i++)
			{
				if (temp.array[i] == null)
				{
					System.out.println("\t[" + String.valueOf(i) + "]: null");
					continue;
				}
				System.out.println("\t[" + String.valueOf(i) + "]: " + String.valueOf(temp.array[i].id) + "; " + String.valueOf(temp.array[i]));
			}
			temp = temp.next;
			nodeNum++;
		}
		System.out.println("array size: " + String.valueOf(arraySize));
		System.out.println("array number: " + String.valueOf(arrayNumber));
		System.out.println("item number: " + String.valueOf(itemNumber));
	}
	
	public String test()
	{
		String errors = "";
		int nodeNum = 0, itemNum = 0;
		Node temp = first;
		while (temp != null)
		{
			if (temp.array.length != arraySize)
			{
				errors += "Node[" + nodeNum + "].array.length: " + temp.array.length + " != arraySize " + arraySize + "\n";
			}
			int nodeItemNum = 0;
			for (int i = 0; i < temp.array.length; i++)
			{
				if (temp.array[i] != null)
				{
					nodeItemNum++;
					itemNum++;
				}
			}
			if (nodeItemNum != temp.length)
			{
				errors += "Node[" + nodeNum + "].length: " + temp.length + " != actual number: " + nodeItemNum + "\n";
			}
			temp = temp.next;
			nodeNum++;
		}
		if (nodeNum != arrayNumber)
		{
			errors += "arrayNumber: " + arrayNumber + " != actual number: " + nodeNum + "\n";
		}
		if (itemNum != itemNumber)
		{
			errors += String.format("itemNumber: %d != actual number: %d", itemNumber, itemNum);
			errors += "itemNumber: " + itemNumber + " != actual number " + itemNum + "\n";
		}
		if (errors.isEmpty())
		{
			return "no error";
		}
		return errors; 
	}
	
}
