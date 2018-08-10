

package org.dhhs.dirm.acts.util;

public class ExceptionStackItem
{
	private int		itemID;
	private int		itemCount		= 0;
	private int		supressCount	= 1;
	private String	itemDesc;
	private boolean	itemSupress;

	public ExceptionStackItem(String desc)
	{
		this.itemID = NextNumber.getNextStackID();

		this.itemCount += 1;

		this.itemDesc = desc;

		this.itemSupress = false;
	}

	public int getItemID()
	{
		return this.itemID;
	}

	public void supressItem(boolean b)
	{
		this.itemSupress = b;
	}

	public void enableItem(boolean b)
	{
		this.itemSupress = b;
	}

	public String getItemDesc()
	{
		return this.itemDesc;
	}

	public boolean getItemState()
	{
		return this.itemSupress;
	}

	public int getSupressCount()
	{
		return this.supressCount;
	}

	public int getItemCount()
	{
		return this.itemCount;
	}

	public void setSupressCount(int count)
	{
		this.supressCount = count;
		if (this.supressCount >= count)
		{
			this.itemSupress = false;
		}
	}

	public void incrementItem()
	{
		this.itemCount += 1;
		if (this.itemCount > this.supressCount)
		{
			this.itemSupress = true;
		}
	}

	public void decrementItem()
	{
		if (this.itemCount > 0)
		{
			this.itemCount -= 1;
		} else
		{
			this.itemCount = 0;
		}
		if (this.itemCount < this.supressCount)
		{
			this.itemSupress = false;
		}
	}

	public void resetItem()
	{
		this.itemCount = 0;
	}

	public String toString()
	{
		return "ID: " + this.itemID + " Count: " + this.itemCount + "\nSupress Count: " + this.supressCount + "\nDesc:" + this.itemDesc + "\nStatus:" + this.itemSupress;
	}
}
