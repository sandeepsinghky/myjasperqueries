

package org.dhhs.dirm.acts.util;

import hit.db2.Db2PooledDataSource;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DataBaseStats extends HttpServlet
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DBConnectManager	connectManager;
	private Db2PooledDataSource	dataSource;
	private static final String	className	= "DatabaseStats";

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		performTask(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		performTask(request, response);
	}

	public String getServletInfo()
	{
		return super.getServletInfo();
	}

	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);

		this.connectManager = new DBConnectManager();

		this.dataSource = ((Db2PooledDataSource) this.connectManager.getDataSource());
	}

	public void performTask(HttpServletRequest request, HttpServletResponse response)
	{
		try
		{
			PrintWriter out = response.getWriter();
			out.println("<HTML><HEAD><TITLE>Database Connection Statistics</TITLE><META HTTP-EQUIV=REFRESH CONTENT='2;URL='/servlet/DataBaseStats'></HEAD>");
			out.println("<BODY>");
			out.println("<h1>Database Connection Statistics</h1>");
			out.println("<h2>Overall Statistics</h2>");
			out.println("<table border>");
			out.println("<TR><TD>Active Database Connections:</TD><TD>" + this.dataSource.getActiveConnectionCount() + "</TD></TR>");
			out.println("<TR><TD>Available Database Connections:</TD><TD>" + this.dataSource.getAvailableConnectionCount() + "</TD></TR>");
			out.println("<TR><TD>Maximum Database Connections</TD><TD>" + this.dataSource.getMaxConnections() + "</TD></TR>");
			out.println("<TR><TD>Optimal Database Connections</TD><TD>" + this.dataSource.getOptimalConnections() + "</TD></TR>");
			out.println("</table>");
			out.println("</BODY>");
			out.println("</HTML>");
		} catch (Throwable localThrowable)
		{
		}
	}
}
