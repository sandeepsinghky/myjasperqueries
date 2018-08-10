

package org.dhhs.dirm.acts.util;

public class NextNumber
{
	private static int stackID = 0;

	static
	{
		NextNumber n = new NextNumber();
	}

	public NextNumber()
	{
		stackID = 0;
	}

	public static int getNextStackID()
	{
		stackID += 1;
		return stackID;
	}
}
