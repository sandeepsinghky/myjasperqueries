

package org.dhhs.dirm.acts.query;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class QueryPropertyLoader
{
	private static String			drive;
	private static String			adminEmail;
	private static String			workingDir;
	private static String			path;
	private static boolean			overRide;
	private static java.sql.Date	runDate;
	private static java.sql.Date	fromDate;
	private static java.sql.Date	toDate;
	private static int				month;
	private static int				year;

	static
	{
		QueryPropertyLoader qp = new QueryPropertyLoader();
	}

	public QueryPropertyLoader()
	{
		try
		{
			String CONFIG_BUNDLE_NAME = getClass().getName();

			PropertyResourceBundle configBundle = (PropertyResourceBundle) ResourceBundle.getBundle(CONFIG_BUNDLE_NAME);
			adminEmail = configBundle.getString("QueryManager.adminEmail");
			drive = configBundle.getString("QueryManager.drive");
			workingDir = configBundle.getString("QueryManager.workingDir");
			overRide = Boolean.valueOf(configBundle.getString("QueryManager.override")).booleanValue();
			System.out.println("Do we have an override? " + overRide);
			if (overRide)
			{
				String strYear = configBundle.getString("QueryManager.year");
				String strMonth = configBundle.getString("QueryManager.month");
				String strMonthEnd = configBundle.getString("QueryManager.monthEnd");

				runDate = java.sql.Date.valueOf(strYear + "-" + strMonth + "-" + strMonthEnd);
				fromDate = java.sql.Date.valueOf(strYear + "-" + strMonth + "-01");
				toDate = java.sql.Date.valueOf(strYear + "-" + strMonth + "-" + strMonthEnd);
				month = Integer.parseInt(strMonth);
				year = Integer.parseInt(strYear);
			}
		} catch (Exception e)
		{
			log("QueryPropertyLoader", "Properties file exception: " + e.getMessage());
		}
	}

	public static String getDrive()
	{
		return drive;
	}

	public static String getAdminEmail()
	{
		return adminEmail;
	}

	public static String getWorkingDir()
	{
		return workingDir;
	}

	public static String getPath()
	{
		return drive + ":" + System.getProperty("file.separator") + workingDir;
	}

	private void log(String method, String message)
	{
		System.out.println("[" + new java.util.Date() + "] Class:" + getClass().getName() + " method:" + method + " " + message + "\n");
	}

	public static java.sql.Date getFromDate()
	{
		return fromDate;
	}

	public static boolean isOverRide()
	{
		return overRide;
	}

	public static java.sql.Date getRunDate()
	{
		return runDate;
	}

	public static java.sql.Date getToDate()
	{
		return toDate;
	}

	public static void setFromDate(java.sql.Date fromDate)
	{
		fromDate = fromDate;
	}

	public static void setOverRide(boolean overRide)
	{
		overRide = overRide;
	}

	public static void setRunDate(java.sql.Date runDate)
	{
		runDate = runDate;
	}

	public static void setToDate(java.sql.Date toDate)
	{
		toDate = toDate;
	}

	public static int getMonth()
	{
		return month;
	}

	public static int getYear()
	{
		return year;
	}

	public static void setMonth(int month)
	{
		month = month;
	}

	public static void setYear(int year)
	{
		year = year;
	}
}
