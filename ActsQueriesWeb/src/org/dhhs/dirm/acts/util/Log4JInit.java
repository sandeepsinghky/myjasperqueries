

package org.dhhs.dirm.acts.util;

import java.io.IOException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.PropertyConfigurator;

public class Log4JInit extends HttpServlet
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String	CONFIG_BUNDLE_NAME			= "org.dhhs.dirm.acts.util.ApplicationConfig";
	private static final String	LOG4J_CONFIG_BUNDLE_NAME	= "Log4JConfig.properties";

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
	}

	public String getServletInfo()
	{
		return super.getServletInfo();
	}

	public void init()
	{
		String prefix = getServletContext().getRealPath("/");

		PropertyResourceBundle configBundle = (PropertyResourceBundle) ResourceBundle.getBundle("org.dhhs.dirm.acts.util.ApplicationConfig");
		String enableLog = configBundle.getString("application.logging");
		if (enableLog.equalsIgnoreCase("TRUE"))
		{
			PropertyConfigurator.configure(prefix + System.getProperty("file.separator") + "Log4JConfig.properties");
		}
	}
}
