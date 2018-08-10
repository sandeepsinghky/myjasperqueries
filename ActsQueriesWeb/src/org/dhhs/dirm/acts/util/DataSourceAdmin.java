

package org.dhhs.dirm.acts.util;

import hit.db2.Db2ConnectionPoolDataSource;
import hit.db2.Db2PooledDataSource;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.sql.DriverManager;
import java.util.Hashtable;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import javax.naming.Context;
import javax.naming.InitialContext;

public class DataSourceAdmin
{
	private static final String	CONFIG_BUNDLE_NAME	= "org.dhhs.dirm.acts.util.ConnPoolStrings";
	private static String		ipAddress;
	private static int			portNumber;
	private static String		rdbName;
	private static String		commitSelect;
	private static String		collectionID;
	private static String		db2Region;
	private static String		userID;
	private static String		password;
	private static String		parent;
	private static String		source;
	private static String		trace;
	private static int			maxConnections;
	private static int			minConnections;
	private static String		jobName;
	private static int			connectionTimeout;
	private static int			retryCount;
	private static int			retryInterval;
	private static String		autoCreatePkgs;
	private static int			traceLevel;
	private static String		remoteMonitorAddr;

	@SuppressWarnings({"rawtypes", "unchecked"})
	public DataSourceAdmin()
	{
		try
		{
			PropertyResourceBundle configBundle = (PropertyResourceBundle) ResourceBundle.getBundle("org.dhhs.dirm.acts.util.ConnPoolStrings");
			ipAddress = configBundle.getString("poolServlet.ip");
			portNumber = Integer.parseInt(configBundle.getString("poolServlet.port"));
			collectionID = configBundle.getString("poolServlet.collid");
			rdbName = configBundle.getString("poolServlet.rdbname");
			commitSelect = configBundle.getString("poolServlet.commitSelect");

			userID = configBundle.getString("poolServlet.user");
			password = configBundle.getString("poolServlet.password");

			parent = configBundle.getString("poolServlet.parent");

			trace = configBundle.getString("poolServlet.trace");

			autoCreatePkgs = configBundle.getString("poolServlet.autoCreatePkgs");
			traceLevel = Integer.parseInt(configBundle.getString("poolServlet.traceLevel"));
			remoteMonitorAddr = configBundle.getString("poolServlet.remoteMonitorAddr");

			maxConnections = Integer.parseInt(configBundle.getString("poolServlet.maxConnections"));

			minConnections = Integer.parseInt(configBundle.getString("poolServlet.minConnections"));

			jobName = configBundle.getString("poolServlet.jobName");

			connectionTimeout = Integer.parseInt(configBundle.getString("poolServlet.connectionTimeout"));

			retryCount = Integer.parseInt(configBundle.getString("poolServlet.retryCount"));
			retryInterval = Integer.parseInt(configBundle.getString("poolServlet.retryCount"));

			parent = "jdbc/" + parent;

			source = configBundle.getString("poolServlet.source");
		} catch (Exception e)
		{
			System.err.println("Properties file exception: " + e.getMessage());
		}
		try
		{
			if (trace.equals("ON"))
			{
				System.out.print("Database Activity Trace is " + trace);

				DriverManager.setLogWriter(new PrintWriter(new FileOutputStream("hrcse106jdbc.log")));
			}
			Hashtable parms = new Hashtable();

			parms.put("java.naming.factory.initial", "hit.jndi.jdbccontext.JDBCNameContextFactory");
			Context ctx = new InitialContext(parms);

			Db2ConnectionPoolDataSource cpds = new Db2ConnectionPoolDataSource();

			cpds.setServerName(ipAddress);
			cpds.setPortNumber(portNumber);
			cpds.setDatabaseName(rdbName);
			cpds.setUser(userID);
			cpds.setPassword(password);

			String connectionOptions = "";
			if (collectionID != null)
			{
				connectionOptions = connectionOptions + "package_collection_id=" + collectionID + ";";
			}
			if (commitSelect != null)
			{
				connectionOptions = connectionOptions + "commit_select=" + commitSelect + ";";
			}
			if (trace.equals("ON"))
			{
				connectionOptions = connectionOptions + "trace_level=" + traceLevel + ";";
			}
			if (jobName != null)
			{
				connectionOptions = connectionOptions + "job_name=" + jobName + ";";
			}
			if (connectionTimeout > 0)
			{
				connectionOptions = connectionOptions + "connection_timeout=" + connectionTimeout + ";";
			}
			if (autoCreatePkgs != null)
			{
				connectionOptions = connectionOptions + "auto_create_packages=" + autoCreatePkgs + ";";
			}
			if (remoteMonitorAddr != null)
			{
				connectionOptions = connectionOptions + "remote_monitor_address=" + remoteMonitorAddr + ";";
			}
			cpds.setConnectionOptions(connectionOptions);
			if (cpds.getServerName().equals("1.1.1.1"))
			{
				System.err.println("Please edit this application to specify your own database properties before running!!!");
				throw new Exception("No good database properties");
			}
			try
			{
				ctx.rebind(parent, cpds);
			} catch (Exception be)
			{
				System.err.println(be);
			}
			System.out.println("jdbc/sample first rebind successful");

			Db2PooledDataSource pooledDS = new Db2PooledDataSource(parms);

			System.out.println("created hit.db2.Db2PooledDataSource Object" + pooledDS);

			pooledDS.setDataSourceName(parent);

			System.out.println("setDataSourceName successful");
			System.out.println("Max Number of Connections in the pool " + maxConnections);
			System.out.println("Min Number of Connections in the pool " + minConnections);

			pooledDS.setMaxPoolSize(maxConnections);

			pooledDS.setMinPoolSize(minConnections);
			pooledDS.setInitialPoolSize(minConnections);

			pooledDS.setMaxIdleTime(20);

			System.out.println("jdbc/sample set datasource successful");
			System.out.println("DataSource Name to rebind is " + source);
			try
			{
				ctx.rebind(source, pooledDS);
				System.out.println("Rebind Successful");
			} catch (Exception e)
			{
				System.err.println(e);
				e.printStackTrace();
			}
		} catch (Exception e)
		{
			System.err.println("Exception: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
