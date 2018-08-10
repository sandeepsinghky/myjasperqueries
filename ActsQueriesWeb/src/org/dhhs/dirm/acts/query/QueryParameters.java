

package org.dhhs.dirm.acts.query;

import java.util.Calendar;

public class QueryParameters
{
	private static String	batchStartDt;
	private static String	batchEndDt;
	private static String	batchRunDt;

	static
	{
		QueryParameters qp = new QueryParameters();
	}

	public QueryParameters()
	{
		String methodName = "QueryParameters";

		Calendar rightNow = Calendar.getInstance();

		int day = rightNow.get(5);
		int month = rightNow.get(2);
		int year = rightNow.get(1);
		if (month == 0)
		{
			year--;
			month = 12;
		} else
		{
			month++;
		}
		batchStartDt = year + "-" + month + "-01";

		int lastDay = rightNow.getActualMaximum(5);
	}
}
