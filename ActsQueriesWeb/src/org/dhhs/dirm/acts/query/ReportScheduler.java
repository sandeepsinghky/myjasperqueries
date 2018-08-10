

package org.dhhs.dirm.acts.query;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.dhhs.dirm.acts.util.ApplicationException;
import org.dhhs.dirm.acts.util.ErrorDescriptor;
import org.dhhs.dirm.acts.util.MaintainedServlet;
import org.dhhs.dirm.acts.util.PropertyManager;
import org.dhhs.dirm.acts.util.ServletMaintenance;

public class ReportScheduler extends HttpServlet implements MaintainedServlet
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ServletMaintenance	maintenanceThread;
	private static final String	className	= "ReportScheduler";
	private static boolean		executing;
	private String				taskFile;
	private String				runMonthYear;
	private int					dayStart	= 20;
	private int					dayEnd		= 27;
	private int					timeStart	= 10;
	private int					timeEnd		= 11;

	public void destroy()
	{
		System.out.println("Shutting Down ReportScheduler Thread ... ");

		this.maintenanceThread.shutDown();
		this.maintenanceThread = null;

		System.out.println("ReportScheduler Thread Shutdown");
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		performTask(req, res);
	}

	@SuppressWarnings("unused")
	public void doMaintenance()
	{
		Date now = new Date();

		String methodName = "doMaintenance";

		System.out.println("Executing ReportScheduler Servlet....@" + now);

		new QueryManager(null, true);

		updateFileForRunDate(this.runMonthYear);

		destroy();
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		performTask(req, res);
	}

	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);

		String methodName = "init";

		log(methodName, "Servlet ReportScheduler Initiated.");
		try
		{
			loadProperties();
		} catch (Exception e)
		{
			e.printStackTrace();
			return;
		}
		if (isQualifiedRunDay())
		{
			log(methodName, "Check current run time");

			int timeToSleep = getTimeToSleep();
			if (timeToSleep >= 0)
			{
				this.maintenanceThread = new ServletMaintenance(this, timeToSleep, 0);
			}
		}
	}

	private int getTimeToSleep()
	{
		String methodName = "getTimeToSleep";
		int timeToSleep = -1;

		log(methodName, "Checking time for qualification");

		Calendar rightNow = Calendar.getInstance();

		int hod = rightNow.get(11);

		log(methodName, "Hour of the Day is :" + hod);
		log(methodName, "Start Time:" + this.timeStart);
		log(methodName, "End Time:" + this.timeEnd);
		if (hod < this.timeStart)
		{
			timeToSleep = (this.timeStart - hod) * 60 * 60;

			log(methodName, "It's too early, the thread will sleep for " + timeToSleep + " seconds.");
		} else if ((hod >= this.timeStart) && (hod <= this.timeEnd))
		{
			timeToSleep = 1;

			log(methodName, "Time's Up. The Thread will run in " + timeToSleep + " second.");
		} else
		{
			log(methodName, "The time does not qualify for run");
		}
		return timeToSleep;
	}

	private boolean isQualifiedRunDay()
	{
		String methodName = "isQualifiedRunDay";

		boolean dayQualifies = false;

		Calendar rightNow = Calendar.getInstance();

		int day = rightNow.get(5);
		int dow = rightNow.get(7);
		int month = rightNow.get(2);
		int year = rightNow.get(1);

		this.runMonthYear = (month + "/" + year);

		this.taskFile = (QueryPropertyLoader.getPath() + System.getProperty("file.separator") + "ReportSchedulerTask.txt");
		if (scanFileForRunDate(this.runMonthYear))
		{
			log(methodName, "Job already run this month");
			return false;
		}
		log(methodName, "Evaluating Qualifying Criteria.");
		if ((dow != 7) || (dow != 1))
		{
			log(methodName, "Today is a WeekDay");
			if ((day >= this.dayStart) && (day < this.dayEnd))
			{
				log(methodName, "Today falls within the Start & End Date Criteria.");

				Calendar c = Calendar.getInstance();
				c.set(5, this.dayStart + 1);

				int dayOfWeek = c.get(7);
				for (;;)
				{
					if ((dayOfWeek == 7) || (dayOfWeek == 1))
					{
						if (c.get(5) < this.dayEnd)
						{
							c.set(5, c.get(5) + 1);
						} else
						{
							log(methodName, "Today does not Qualify as a run day.");
							break;
						}
					} else
					{
						log(methodName, "Today is a Qualified Day to run the Queries.");
						dayQualifies = true;
						break;
					}
					dayOfWeek = c.get(7);
				}
			} else
			{
				log(methodName, "Today does not fall within the Start & End Date Criteria.");
			}
		}
		return dayQualifies;
	}

	@SuppressWarnings("unused")
	public void performTask(HttpServletRequest req, HttpServletResponse res)
	{
		Date now = new Date();

		String methodName = "performTask";

		System.out.println("Requested ReportScheduler Servlet....@" + now);
		if (this.maintenanceThread == null)
		{
			System.out.println("Starting Thread....@" + now);

			this.maintenanceThread = new ServletMaintenance(this, PropertyManager.getDbTimeToWait(), PropertyManager.getDbClassificationToWait());

			System.out.println("Started Thread....@" + now);
		} else
		{
			System.out.println("Thread already active....@" + now);
		}
	}

	private void loadProperties() throws Exception
	{
		try
		{
			String CONFIG_BUNDLE_NAME = getClass().getName();

			PropertyResourceBundle configBundle = (PropertyResourceBundle) ResourceBundle.getBundle(CONFIG_BUNDLE_NAME);
			this.dayStart = Integer.parseInt(configBundle.getString("ReportScheduler.startDate"));
			this.dayEnd = Integer.parseInt(configBundle.getString("ReportScheduler.endDate"));
			this.timeStart = Integer.parseInt(configBundle.getString("ReportScheduler.startTime"));
			this.timeEnd = Integer.parseInt(configBundle.getString("ReportScheduler.endTime"));
		} catch (Exception e)
		{
			log("loadProperties", "Properties file exception: " + e.getMessage());
			ErrorDescriptor ed = new ErrorDescriptor("ReportScheduler", "loadProperties");
			ed.setErrLevel(0);

			new ApplicationException(e.getMessage(), e, ed);
			throw new Exception(e.getMessage());
		}
	}

	private boolean scanFileForRunDate(String runMonthYear)
	{
		String methodName = "scanFileForRunDate";

		boolean ranThisMonth = false;
		try
		{
			FileInputStream fis = new FileInputStream(this.taskFile);

			BufferedReader br = new BufferedReader(new InputStreamReader(fis));

			String str = null;
			while ((str = br.readLine()) != null)
			{
				log(methodName, str + " " + runMonthYear);
				if (str.equalsIgnoreCase(runMonthYear))
				{
					log(methodName, "equals");
					ranThisMonth = true;
					break;
				}
			}
			fis.close();
		} catch (IOException ioe)
		{
			ioe.printStackTrace(System.out);
			log(methodName, ioe.getMessage());
			ranThisMonth = true;
		}
		return ranThisMonth;
	}

	private void updateFileForRunDate(String runMonthYear)
	{
		try
		{
			FileOutputStream fos = new FileOutputStream(this.taskFile);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

			bw.write(runMonthYear + "\n");
			bw.flush();

			fos.close();
		} catch (IOException ioe)
		{
			ioe.printStackTrace(System.out);
		}
	}

	private void log(String method, String message)
	{
		System.out.println("[" + new Date() + "] Class:" + getClass().getName() + " method:" + method + " " + message + "\n");
	}
}
