

package org.dhhs.dirm.acts.util;

public class ServletMaintenance extends Thread
{
	public static final int		SECONDS	= 0;
	public static final int		MINUTES	= 1;
	public static final int		HOURS	= 2;
	public static final int		DAYS	= 3;
	private MaintainedServlet	servletReference;
	private boolean				done	= false;
	private int					secondsToWait;
	private int					minutesToWait;
	private int					hoursToWait;
	private int					daysToWait;
	private final long			SECOND	= 1000L;
	private final long			MINUTE	= 60000L;
	private final long			HOUR	= 3600000L;
	private final long			DAY		= 86400000L;

	public ServletMaintenance(MaintainedServlet reference, int lengthTime, int timeType)
	{
		this.servletReference = reference;
		this.secondsToWait = 0;
		this.minutesToWait = 0;
		this.hoursToWait = 0;
		this.daysToWait = 0;
		if (timeType == 0)
		{
			this.secondsToWait = lengthTime;
		} else if (timeType == 1)
		{
			this.minutesToWait = lengthTime;
		} else if (timeType == 2)
		{
			this.hoursToWait = lengthTime;
		} else if (timeType == 3)
		{
			this.daysToWait = lengthTime;
		}
		start();
	}

	public int getDaysToWait()
	{
		return this.daysToWait;
	}

	public int getHoursToWait()
	{
		return this.hoursToWait;
	}

	public int getMinutesToWait()
	{
		return this.minutesToWait;
	}

	public int getSecondsToWait()
	{
		return this.secondsToWait;
	}

	public boolean isDone()
	{
		return this.done;
	}

	public void run()
	{
		while (!this.done)
		{
			try
			{
				long timeToWait = this.secondsToWait * 1000L;
				timeToWait += this.minutesToWait * 60000L;
				timeToWait += this.hoursToWait * 3600000L;
				timeToWait += this.daysToWait * 86400000L;
				Thread.sleep(timeToWait);
				if (!this.done)
				{
					this.servletReference.doMaintenance();
				}
			} catch (Exception localException)
			{
			}
		}
	}

	public void setDaysToWait(int newDaysToWait)
	{
		this.daysToWait = newDaysToWait;
	}

	public void setDone(boolean newDone)
	{
		this.done = newDone;
	}

	public void setHoursToWait(int newHoursToWait)
	{
		this.hoursToWait = newHoursToWait;
	}

	public void setMinutesToWait(int newMinutesToWait)
	{
		this.minutesToWait = newMinutesToWait;
	}

	public void setSecondsToWait(int newSecondsToWait)
	{
		this.secondsToWait = newSecondsToWait;
	}

	public void shutDown()
	{
		this.done = true;
	}
}
