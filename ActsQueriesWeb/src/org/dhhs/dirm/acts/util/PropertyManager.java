

package org.dhhs.dirm.acts.util;

import java.io.PrintStream;
import java.util.Date;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class PropertyManager
{
	static String				primaryURL				= null;
	static String				secondaryURL			= null;
	static String				otherURL				= null;
	static String				primaryEmail			= null;
	static String				secondaryEmail			= null;
	static String				otherEmail				= null;
	static int					dbTimeToWait			= 0;
	static int					dbClassificationToWait	= 1;
	static int					msTimeToWait			= 0;
	static int					msClassificationToWait	= 1;
	private static final String	CONFIG_BUNDLE_NAME		= "corp.sysrad.acts.ConPool.ApplicationConfig";

	static
	{
		PropertyManager p = new PropertyManager();
	}

	public PropertyManager()
	{
		Date now = new Date();
		try
		{
			PropertyResourceBundle configBundle = (PropertyResourceBundle) ResourceBundle.getBundle("corp.sysrad.acts.ConPool.ApplicationConfig");
			primaryURL = configBundle.getString("application.primaryURL");
			secondaryURL = configBundle.getString("application.secondaryURL");
			otherURL = configBundle.getString("application.otherURL");
			primaryEmail = configBundle.getString("application.primaryEmail");
			secondaryEmail = configBundle.getString("application.secondaryEmail");
			otherEmail = configBundle.getString("application.otherEmail");
			dbTimeToWait = Integer.parseInt(configBundle.getString("application.dbTimeToWait"));
			dbClassificationToWait = Integer.parseInt(configBundle.getString("application.dbClassificationToWait"));

			msTimeToWait = Integer.parseInt(configBundle.getString("application.msTimeToWait"));
			msClassificationToWait = Integer.parseInt(configBundle.getString("application.msClassificationToWait"));

			System.out.println("PropertyManager Parameters - Start\n");
			System.out.println("Database Time To Wait: " + dbTimeToWait);
			System.out.println("Database Classification To Wait: " + dbClassificationToWait);
			System.out.println("Maintanied Servlet Time To Wait: " + msTimeToWait);
			System.out.println("Maintained Servlet Classification To Wait: " + msClassificationToWait);
			System.out.println("PropertyManager Parameters - End\n");
		} catch (Exception e)
		{
			System.out.println("Properties file exception: " + e.getMessage());
		}
	}

	public static int getDbClassificationToWait()
	{
		return dbClassificationToWait;
	}

	public static int getDbTimeToWait()
	{
		return dbTimeToWait;
	}

	public static int getMsClassificationToWait()
	{
		return msClassificationToWait;
	}

	public static int getMsTimeToWait()
	{
		return msTimeToWait;
	}

	public static String getOtherEmail()
	{
		return otherEmail;
	}

	public static String getOtherURL()
	{
		return otherURL;
	}

	public static String getPrimaryEmail()
	{
		return primaryEmail;
	}

	public static String getPrimaryURL()
	{
		return primaryURL;
	}

	public static String getSecondaryEmail()
	{
		return secondaryEmail;
	}

	public static String getSecondaryURL()
	{
		return secondaryURL;
	}

	public static void setDbClassificationToWait(int newDbClassificationToWait)
	{
		dbClassificationToWait = newDbClassificationToWait;
	}

	public static void setDbTimeToWait(int newDbTimeToWait)
	{
		dbTimeToWait = newDbTimeToWait;
	}

	public static void setMsClassificationToWait(int newMsClassificationToWait)
	{
		msClassificationToWait = newMsClassificationToWait;
	}

	public static void setMsTimeToWait(int newMsTimeToWait)
	{
		msTimeToWait = newMsTimeToWait;
	}

	public static void setOtherEmail(String newOtherEmail)
	{
		otherEmail = newOtherEmail;
	}

	public static void setOtherURL(String newOtherURL)
	{
		otherURL = newOtherURL;
	}

	public static void setPrimaryEmail(String newPrimaryEmail)
	{
		primaryEmail = newPrimaryEmail;
	}

	public static void setPrimaryURL(String newPrimaryURL)
	{
		primaryURL = newPrimaryURL;
	}

	public static void setSecondaryEmail(String newSecondaryEmail)
	{
		secondaryEmail = newSecondaryEmail;
	}

	public static void setSecondaryURL(String newSecondaryURL)
	{
		secondaryURL = newSecondaryURL;
	}
}
