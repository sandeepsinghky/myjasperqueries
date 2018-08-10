

package org.dhhs.dirm.acts.util;

import java.util.Hashtable;

@SuppressWarnings("unused")
public class ExceptionStack
{
	@SuppressWarnings("rawtypes")
	static Hashtable stack;

	static
	{
		ExceptionStack e = new ExceptionStack();
	}

	@SuppressWarnings("rawtypes")
	public ExceptionStack()
	{
		stack = new Hashtable();
	}

	public static void resetStack()
	{
		stack.clear();
	}

	public static boolean queryStack(String key)
	{
		if (stack.containsKey(key))
		{
			return true;
		}
		return false;
	}

	public static ExceptionStackItem getItem(String key)
	{
		if (queryStack(key))
		{
			return (ExceptionStackItem) stack.get(key);
		}
		return null;
	}

	public static void removeFromStack(String key)
	{
		if (queryStack(key))
		{
			stack.remove(key);
		}
	}

	@SuppressWarnings("unchecked")
	public static void addToStack(String key)
	{
		if (!queryStack(key))
		{
			ExceptionStackItem item = new ExceptionStackItem(key);
			stack.put(key, item);
		} else
		{
			ExceptionStackItem item = (ExceptionStackItem) stack.get(key);
			item.incrementItem();
		}
	}

	public static void resetStackItemSupressFlag(String key, boolean b)
	{
		if (queryStack(key))
		{
			ExceptionStackItem item = (ExceptionStackItem) stack.get(key);
			if (b)
			{
				item.enableItem(b);
			} else
			{
				item.supressItem(b);
			}
		}
	}

	public static void modifyStackItemSupressCount(String key, int count)
	{
		if (queryStack(key))
		{
			ExceptionStackItem item = (ExceptionStackItem) stack.get(key);
			item.setSupressCount(count);
		}
	}

	@SuppressWarnings("rawtypes")
	public static Hashtable obtainStack()
	{
		return stack;
	}

	@SuppressWarnings("rawtypes")
	public static void main(String[] args)
	{
		String key1 = "hit.db2sql.ck: I/O exception while talking to the server, java.io.IOException: Unexpected EOF";
		String key2 = "java.sql.SQLException: [EXECUTE] Dist env rollback: \"00C9008E?00000200?FKFD000 .FKKS168\"";
		for (int i = 0; i < 5; i++)
		{
			addToStack(key1);
		}
		addToStack(key2);

		Hashtable h = obtainStack();

		System.out.println("Before");
		System.out.println("Stack Entry Number :1 \nObject:" + ((ExceptionStackItem) h.get(key1)).toString());
		System.out.println("Stack Entry Number :2 \nObject:" + ((ExceptionStackItem) h.get(key2)).toString());

		ExceptionStackItem item = (ExceptionStackItem) h.get(key1);
		item.setSupressCount(10);
		item.supressItem(true);

		System.out.println("After");
		System.out.println("Stack Entry Number :1 \nObject:" + ((ExceptionStackItem) h.get(key1)).toString());

		modifyStackItemSupressCount(key2, 50);
		System.out.println("Stack Entry Number :2 \nObject:" + ((ExceptionStackItem) h.get(key2)).toString());

		removeFromStack(key1);
		System.out.println("Remove one item and now size is :" + h.size());

		resetStack();

		System.out.println("Reset Stack Completely and size is: " + h.size());
	}
}
