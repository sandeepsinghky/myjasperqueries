

package org.dhhs.dirm.acts.query;

import java.sql.Date;

public class QueryDateBean
{
	private Date	fromDate;
	private Date	toDate;
	private Date	runDate;
	private int		month;
	private int		year;

	public void setFromDate(Date s)
	{
		this.fromDate = s;
	}

	public void setToDate(Date s)
	{
		this.toDate = s;
	}

	public void setRunDate(Date s)
	{
		this.runDate = s;
	}

	public Date getFromDate()
	{
		return this.fromDate;
	}

	public Date getToDate()
	{
		return this.toDate;
	}

	public Date getRunDate()
	{
		return this.runDate;
	}

	public int getMonth()
	{
		return this.month;
	}

	public int getYear()
	{
		return this.year;
	}

	public void setMonth(int month)
	{
		this.month = month;
	}

	public void setYear(int year)
	{
		this.year = year;
	}
}
