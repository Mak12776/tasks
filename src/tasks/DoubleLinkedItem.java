package tasks;


import java.util.Iterator;
import java.util.NoSuchElementException;

import exceptions.FinishedCreatorException;
import exceptions.InvalidIndexException;
import exceptions.NoSuchItemException;
import types.Item;

public class DoubleLinkedItem<T extends Item> implements Iterable<Item>
{
	class Node
	{
		private Item[] array;
		private int length;
		private Node prev = null;
		private Node next = null;
		
		public Node()
		{
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
			array = new Item[arraySize];
			array[0] = item;
			length = 1;
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
					break;
				}
			}
			return true;
		}
		
		public boolean set(int index, Item item)
		{
			if (array[index] == null)
			{
				length++;
				return true;
			}
			array[index] = item;
			return false;
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
			arrayNumber++;
			itemNumber++;
		}
		else if (first.add(0, item))
		{
			itemNumber++;
			return;
		}
		else
		{
			Node temp = first;
			int baseIndex = arraySize;
			while (temp.next != null)
			{
				temp = temp.next;
				if (temp.add(baseIndex, item))
				{
					itemNumber++;
					return;
				}
				baseIndex += arraySize;
			}
			item.id = baseIndex;
			temp.next = new Node(temp, item);
			arrayNumber++;
			itemNumber++;
		}
	}
	
	public void setItem(int index, T item)
	{
		if (item == null)
		{
			throw new NullPointerException("item argument is null.");
		}
		int baseIndex = index / arraySize;
		if (baseIndex >= arrayNumber)
		{
			throw new InvalidIndexException(index);
		}
		Node temp = first;
		while (baseIndex != 0)
		{
			temp = temp.next;
			baseIndex--;
		}
		item.id = index;
		if (temp.set(index % arraySize, item))
		{
			itemNumber++;
		}
	}
	
	public Item getItem(int index) throws NoSuchItemException
	{
		int base = index / arraySize;
		if (base >= arrayNumber)
		{
			throw new InvalidIndexException(index);
		}
		Node temp = first;
		while (base != 0)
		{
			temp = temp.next;
			base--;
		}
		return temp.get(index % arraySize);
	}
	
	public boolean removeItem(int index)
	{
		int base = index / arraySize;
		if (base >= arrayNumber)
		{
			throw new InvalidIndexException(index);
		}
		Node temp = first;
		while (base != 0)
		{
			temp = temp.next;
			base--;
		}
		if (temp.del(index % arraySize))
		{
			itemNumber--;
			return true;
		}
		return false;
	}
	
	public int length()
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
		private int baseIndex;
		private int skippedIndex;
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
		
		public void add(E item)
		{
			if (finished)
			{
				throw new FinishedCreatorException();
			}
			if (lastNode == null)
			{
				lastNode = (result.first = result.new Node());
				lastNode.array[0] = item;
				item.id = 0;
				index = 1;
			}
			else
			{
				if (index == result.arraySize)
				{
					lastNode.length = index - skippedIndex;
					skippedIndex = 0;
					lastNode = (lastNode.next = result.new Node(lastNode));
					lastNode.array[0] = item;
					item.id = (++baseIndex) * result.arraySize;
					index = 1;
				}
				else
				{
					lastNode.array[index] = item;
					item.id = (baseIndex * result.arraySize) + (index++);
				}
			}
		}
		
		public void skip(int num)
		{
			if (num <= 0)
			{
				throw new IllegalArgumentException("invalid num argument: " + num);
			}
			if (finished)
			{
				throw new FinishedCreatorException();
			}
			if (result.first == null)
			{
				baseIndex = num / result.arraySize;
				skippedIndex = (index = num % result.arraySize);
				allSkippedNumber = num;
				lastNode = (result.first = result.new Node());
				int tempIndex = baseIndex;
				while (tempIndex != 0)
				{
					lastNode.next = (lastNode = result.new Node(lastNode));
					tempIndex--;
				}
			}
			else
			{
				int tempIndex;
				lastNode.length = index - skippedIndex;
				index += num;
				baseIndex += (tempIndex = index / result.arraySize);
				index %= result.arraySize;
				while (tempIndex != 0)
				{
					lastNode.next = (lastNode = result.new Node(lastNode));
					tempIndex--;
				}
				allSkippedNumber += num;
				skippedIndex = index - lastNode.length;
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
				lastNode.length = index - skippedIndex;
				result.itemNumber = (baseIndex * result.arraySize) + index - allSkippedNumber;
				result.arrayNumber = baseIndex + 1;
			}
			finished = true;
		}
		
		public DoubleLinkedItem<E> getResult()
		{
			if (finished)
			{
				return result;
			}
			return null;
		}
	}
	
	@Override
	public Iterator<Item> iterator() 
	{
		return new ClassIterator();
	}
	
	class ClassIterator implements Iterator<Item>
	{
		int index;
		Node tempNode;
		
		public ClassIterator() 
		{
			tempNode = first;
		}
		
		@Override
		public boolean hasNext()
		{
			if (tempNode == null)
			{
				return false;
			}
			if (index == arraySize)
			{
				tempNode = tempNode.next;
				if (tempNode == null)
				{
					return false;
				}
				index = 0;
			}
			while (tempNode.array[index] == null)
			{
				if ((++index) == arraySize)
				{
					tempNode = tempNode.next;
					if (tempNode == null)
					{
						return false;
					}
					index = 0;
				}
			}
			return true;
		}

		@Override
		public Item next()
		{
			if (hasNext())
			{
				return tempNode.array[index++];
			}
			throw new NoSuchElementException();
		}
	}
	
	@SuppressWarnings("Trial")
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
					continue;
				}
				System.out.println("\t[" + String.valueOf(i) + "]: " + String.valueOf(temp.array[i].id) + "; " + String.valueOf(temp.array[i]));
			}
			temp = temp.next;
			nodeNum++;
		}
		System.out.println("--- info ---");
		System.out.println("array size: " + String.valueOf(arraySize));
		System.out.println("array number: " + String.valueOf(arrayNumber));
		System.out.println("item number: " + String.valueOf(itemNumber));
	}
	
	@SuppressWarnings("Trial")
	public String test()
	{
		String errors = "";
		String indexErrors = "";
		int nodeNum = 0, itemNum = 0;
		Node temp = first, prevNode = null;
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
					if (temp.array[i].id != (nodeNum * arraySize) + i)
					{
						indexErrors += "Node[" + nodeNum + "].array[" + i + "].id: " + temp.array[i].id + " != actual id: " +  ((nodeNum * arraySize) + i) + "\n"; 
					}
					nodeItemNum++;
					itemNum++;
				}
			}
			if (nodeItemNum != temp.length)
			{
				errors += "Node[" + nodeNum + "].length: " + temp.length + " != actual number: " + nodeItemNum + "\n";
			}
			if (prevNode != temp.prev)
			{
				errors += "Node[" + nodeNum + "].prev: " + temp.prev + " != actual node: " + prevNode + "\n"; 
			}
			prevNode = temp;
			temp = temp.next;
			nodeNum++;
		}
		if (nodeNum != arrayNumber)
		{
			errors += "arrayNumber: " + arrayNumber + " != actual number: " + nodeNum + "\n";
		}
		if (itemNum != itemNumber)
		{
			errors += "itemNumber: " + itemNumber + " != actual number " + itemNum + "\n";
		}
		
		if (!indexErrors.isEmpty())
		{
			errors += "index errors:\n" + indexErrors;
		}
		if (errors.isEmpty())
		{
			return null;
		}
		return errors;
	}	
}
