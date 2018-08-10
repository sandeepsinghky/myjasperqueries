

package org.dhhs.dirm.acts.util;

import java.io.IOException;
import javax.servlet.GenericServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class HitConnectionPoolAdmin extends GenericServlet
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);

		DataSourceAdmin ds = new DataSourceAdmin();
	}

	public void service(ServletRequest arg1, ServletResponse arg2) throws ServletException, IOException
	{
	}
}
