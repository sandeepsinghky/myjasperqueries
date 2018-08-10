

package org.dhhs.dirm.acts.query;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import javax.sql.DataSource;
import org.dhhs.dirm.acts.util.ApplicationException;
import org.dhhs.dirm.acts.util.DBConnectManager;
import org.dhhs.dirm.acts.util.EmailManager;
import org.dhhs.dirm.acts.util.ErrorDescriptor;

public class QueryManager
{
	private String			taskFile;
	private DataSource		dataSource;
	private String			userID;
	private String			password;
	private String			queryRegion;
	private QueryDateBean	qdb;
	private Hashtable		taskTable;
	private String			admin;
	private String			workingDir;
	private String			className	= QueryManager.class.getName();
	StringTokenizer			stk;

	public QueryManager(String taskFile, boolean forceRun)
	{
		String methodName = "QueryManager";

		log(methodName, "Starting QueryManager.....");

		sendEmail("Started");
		if (forceRun)
		{
			DBConnectManager connectManager = new DBConnectManager();

			this.dataSource = connectManager.getDataSource();
			this.userID = connectManager.getUserID();
			this.password = connectManager.getPassword();
			this.queryRegion = DBConnectManager.getQueryRegion();
			if (taskFile != null)
			{
				this.taskFile = taskFile;
			} else
			{
				this.taskFile = (QueryPropertyLoader.getPath() + System.getProperty("file.separator") + "QueryManagerTasks.qry");
			}
			this.taskTable = new Hashtable();

			createDateBean();

			parseFile();

			loadClasses();
		}
		log(methodName, "QueryManager Completed.");

		sendEmail("Completed");
	}

	private void parseFile()
	{
		try
		{
			FileInputStream fis = new FileInputStream(this.taskFile);

			BufferedReader br = new BufferedReader(new InputStreamReader(fis));

			String str = null;
			int count = 0;
			while ((str = br.readLine()) != null)
			{
				if (!str.startsWith("#"))
				{
					StringTokenizer token = new StringTokenizer(str, ";");

					String queryClass = null;
					StringTokenizer stk;
					for (; token.hasMoreTokens(); stk.hasMoreTokens())
					{
						String query = token.nextToken();

						stk = new StringTokenizer(query, "=");

						// have to review this code block, commenting out below line to fix the compile time errors
						//continue;
						String parm = stk.nextToken();
						String value = stk.nextToken();

						if (parm.equalsIgnoreCase("QueryClass"))
						{
							queryClass = value;
						} else if ((parm.equalsIgnoreCase("Enabled")) && (value.equalsIgnoreCase("True")))
						{
							this.taskTable.put(new Integer(count), queryClass);
							count++;
						}
					}
				}
			}
		} catch (IOException ioe)
		{
			ioe.printStackTrace(System.out);
		}
	}

	private void loadClasses()
	{
		for (Enumeration e = this.taskTable.elements(); e.hasMoreElements();)
		{
			String queryClass = (String) e.nextElement();
			try
			{
				QueryInterface qi = (QueryInterface) Class.forName(queryClass).newInstance();
				qi.invoke(this.dataSource, this.qdb);
			} catch (ClassNotFoundException cnfe)
			{
				cnfe.printStackTrace(System.out);

				ErrorDescriptor ed = new ErrorDescriptor(this.className, "loadClasses");
				ed.setErrNotify(true);
				ed.setErrLevel(0);

				new ApplicationException(cnfe.getMessage(), cnfe, ed);
			} catch (IllegalAccessException iae)
			{
				iae.printStackTrace(System.out);

				ErrorDescriptor ed = new ErrorDescriptor(this.className, "loadClasses");
				ed.setErrNotify(true);
				ed.setErrLevel(0);

				new ApplicationException(iae.getMessage(), iae, ed);
			} catch (InstantiationException ie)
			{
				ie.printStackTrace(System.out);

				ErrorDescriptor ed = new ErrorDescriptor(this.className, "loadClasses");
				ed.setErrNotify(true);
				ed.setErrLevel(0);

				new ApplicationException(ie.getMessage(), ie, ed);
			} catch (QueryException qe)
			{
				qe.printStackTrace();

				ErrorDescriptor ed = new ErrorDescriptor(this.className, "loadClasses");
				ed.setErrNotify(true);
				ed.setErrLevel(0);

				new ApplicationException(qe.getMessage(), qe, ed);
			}
		}
	}

	private boolean calculateRunDate()
	{
		String methodName = "calculateRunDate";

		Calendar rightNow = Calendar.getInstance();

		int day = rightNow.get(5);
		int month = rightNow.get(2);
		int year = rightNow.get(1);

		Calendar firstSunday = firstSundayOfMonth();
		int dayOfFirstSunday = firstSunday.get(5);
		int monthOfFirstSunday = firstSunday.get(2);
		int yearOfFirstSunday = firstSunday.get(1);
		if (day == dayOfFirstSunday)
		{
			log(methodName, "Query meets the criteria to run today");
			return true;
		}
		log(methodName, "Query does not meet the criteria to run today");
		return false;
	}

	private Calendar firstSundayOfMonth()
	{
		String methodName = "firstSundayOfMonth";

		Calendar cal = Calendar.getInstance();

		cal.set(5, 1);
		int numberOfSundays = 0;
		while (numberOfSundays < 1)
		{
			if (cal.get(7) == 1)
			{
				numberOfSundays++;
				break;
			}
			cal.set(5, cal.get(5) + 1);
		}
		log(methodName, "The First Sunday of the month is:" + cal.getTime());
		return cal;
	}

	private void createDateBean()
	{
		this.qdb = new QueryDateBean();
		if (QueryPropertyLoader.isOverRide())
		{
			log("createDateBean", "Query Manager has an Override Date Set");
			log("createDateBean", "To Date: " + QueryPropertyLoader.getToDate());
			log("createDateBean", "From Date: " + QueryPropertyLoader.getFromDate());
			log("createDateBean", "Run Date: " + QueryPropertyLoader.getRunDate());

			this.qdb.setFromDate(QueryPropertyLoader.getFromDate());
			this.qdb.setToDate(QueryPropertyLoader.getToDate());
			this.qdb.setRunDate(QueryPropertyLoader.getRunDate());
			this.qdb.setMonth(QueryPropertyLoader.getMonth());
			this.qdb.setYear(QueryPropertyLoader.getYear());
		} else
		{
			Calendar rightNow = Calendar.getInstance();

			java.util.Date now = rightNow.getTime();
			java.sql.Date runDate = new java.sql.Date(now.getTime());

			rightNow.add(2, -1);
			rightNow.getTime();

			int daysInMonth = rightNow.getActualMaximum(5);

			rightNow.set(5, daysInMonth);

			java.util.Date to = rightNow.getTime();
			java.sql.Date toDate = new java.sql.Date(to.getTime());
			log("createDateBean", "To Date: " + toDate);

			rightNow.set(5, 1);
			java.util.Date from = rightNow.getTime();
			java.sql.Date fromDate = new java.sql.Date(from.getTime());
			log("createDateBean", "From Date: " + fromDate);

			int month = rightNow.get(2);
			int year = rightNow.get(1);

			this.qdb.setFromDate(fromDate);
			this.qdb.setToDate(toDate);
			this.qdb.setRunDate(runDate);
			this.qdb.setMonth(month);
			this.qdb.setYear(year);
		}
	}

	private void log(String method, String message)
	{
		System.out.println("[" + new java.util.Date() + "] Class:" + getClass().getName() + " method:" + method + " " + message + "\n");
	}

	private void sendEmail(String msg)
	{
		try
		{
			EmailManager em = new EmailManager();

			em.addTo(QueryPropertyLoader.getAdminEmail());

			em.setSubject("Acts - Automated Query Request:" + msg);

			StringBuffer buf = new StringBuffer();

			buf.append("NOTE: PLEASE DO NOT RESPOND DIRECTLY TO THIS E-MAIL MESSAGE. THIS ADDRESS IS NOT MONITORED.\n\n");

			buf.append("This message has been sent as a notification for Monthly Acts Queries. If you did not request this service or believe this message has been sent to you in error, please contact ACTS Help Desk..\n\n");

			em.setBody(buf.toString());

			em.sendMail();
			return;
		} catch (ApplicationException e)
		{
			ErrorDescriptor ed = new ErrorDescriptor(this.className, "sendEmail");
			ed.setErrLevel(0);
			new ApplicationException(e.getMessage(), e, ed);
		}
	}

	public static void main(String[] args)
	{
		QueryManager qm = new QueryManager(null, true);
		System.exit(0);
	}
}
