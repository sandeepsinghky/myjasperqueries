

package org.dhhs.dirm.acts.util;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.DriverManager;
import java.util.Date;
import java.util.Hashtable;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class DBConnectManager
{
	private static DataSource	dataSource			= null;
	private static String		user				= null;
	private static String		password			= null;
	private static String		owner				= null;
	private static String		source				= null;
	private static String		region				= null;
	private static String		queryregion			= null;
	private static final String	CONFIG_BUNDLE_NAME	= "org.dhhs.dirm.acts.util.ConnPoolStrings";

	public DBConnectManager()
	{
		Date now = new Date();
		Context ctx = null;
		try
		{
			PropertyResourceBundle configBundle = (PropertyResourceBundle) ResourceBundle.getBundle("org.dhhs.dirm.acts.util.ConnPoolStrings");
			user = configBundle.getString("poolServlet.user");
			password = configBundle.getString("poolServlet.password");
			source = configBundle.getString("poolServlet.source");
			region = configBundle.getString("poolServlet.region");
			queryregion = configBundle.getString("poolServlet.queryregion");
		} catch (Exception e)
		{
			System.out.println("Properties file exception: " + e.getMessage());
		}
		try
		{
			DriverManager.setLogWriter(new PrintWriter(new FileOutputStream("DBConnectManager.log")));

			Hashtable parms = new Hashtable();

			parms.put("java.naming.factory.initial", "hit.jndi.jdbccontext.JDBCNameContextFactory");

			ctx = new InitialContext(parms);

			System.out.print("[" + now + "] Looking up data source name " + source + "\n");
			dataSource = (DataSource) ctx.lookup(source);
			System.out.print("[" + now + "] Found data source name " + dataSource + "\n");
		} catch (Exception ex)
		{
			System.err.println("DBConnectManager Lookup Exception: " + ex.getMessage());
		}
	}

	public DataSource getDataSource()
	{
		return dataSource;
	}

	public String getPassword()
	{
		return password;
	}

	public static String getRegion()
	{
		return region;
	}

	public static String getQueryRegion()
	{
		return queryregion;
	}

	public static String getSource()
	{
		return source;
	}

	public String getUserID()
	{
		return user;
	}

	public static void setRegion(String newRegion)
	{
		region = newRegion;
	}

	public static void setSource(String newSource)
	{
		source = newSource;
	}
}
