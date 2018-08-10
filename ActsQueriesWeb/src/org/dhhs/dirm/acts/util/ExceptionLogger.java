

package org.dhhs.dirm.acts.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class ExceptionLogger
{
	public static PrintWriter	pw;
	private static final String	CONFIG_BUNDLE_NAME	= "corp.sysrad.acts.ConPool.ApplicationConfig";

	static
	{
		try
		{
			PropertyResourceBundle configBundle = (PropertyResourceBundle) ResourceBundle.getBundle("corp.sysrad.acts.ConPool.ApplicationConfig");
			String logFile = configBundle.getString("application.logFile");

			ExceptionLogger ex = new ExceptionLogger(logFile);
		} catch (Exception ex)
		{
			// ExceptionLogger e;
			System.err.println("Properties File Exception. Failed to Initialize ExceptionLogger Class");
		}
	}

	public ExceptionLogger(String fileName) throws IOException
	{
		pw = new PrintWriter(new FileWriter(fileName));
	}

	public static synchronized void log(Throwable ex)
	{
		ex.printStackTrace(pw);
		pw.print("\n");
		pw.flush();
	}

	public static void main(String[] args)
	{
	}
}
