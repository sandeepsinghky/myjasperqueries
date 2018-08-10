

package org.dhhs.dirm.acts.util;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ExceptionStackServlet extends HttpServlet
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("rawtypes")
	private Hashtable hashtable;

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

	public void init()
	{
	}

	@SuppressWarnings({"deprecation", "rawtypes"})
	public void performTask(HttpServletRequest req, HttpServletResponse res)
	{
		try
		{
			this.hashtable = ExceptionStack.obtainStack();

			Enumeration enumeration = this.hashtable.elements();

			ExceptionStackItem item = null;

			int supressCount = Integer.parseInt(req.getParameter("supressCount"));

			String supress = req.getParameter("itemSupress").trim();

			boolean itemSupress = false;
			if (supress.equalsIgnoreCase("true"))
			{
				itemSupress = true;
			} else
			{
				itemSupress = false;
			}
			int itemID = Integer.parseInt(req.getParameter("itemID"));
			while (enumeration.hasMoreElements())
			{
				ExceptionStackItem itm = (ExceptionStackItem) enumeration.nextElement();
				if (itm.getItemID() == itemID)
				{
					item = itm;
					break;
				}
			}
			if (item == null)
			{
				String loginURL = "ExceptionStackTable.jsp?errorMsg=" + URLEncoder.encode("Update unsuccessful, Item not found in the stack.");
				getServletConfig().getServletContext().getRequestDispatcher(loginURL).forward(req, res);
				return;
			}
			item.setSupressCount(supressCount);
			item.supressItem(itemSupress);
			String loginURL = "ExceptionStackTable.jsp?errorMsg=" + URLEncoder.encode("Update successful, Stack Item Modified");
			getServletConfig().getServletContext().getRequestDispatcher(loginURL).forward(req, res);
			return;
		} catch (Throwable e)
		{
			ErrorDescriptor ed = new ErrorDescriptor("ExceptionStackServlet", "performTask");

			new ApplicationException(e.getMessage(), e, ed);
		}
	}
}
