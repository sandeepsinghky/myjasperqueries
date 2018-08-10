

package org.dhhs.dirm.acts.util;

import java.util.StringTokenizer;

public class StackTraceParser
{
	public static void main(String[] args)
	{
		String stack = "java.sql.SQLException: Resource Unavailable java.lang.Throwable(java.lang.String) java.lang.Exception(java.lang.String) java.sql.SQLException(java.lang.String, java.lang.String, int) void com.sysrad.util.ApplicationException.main(java.lang.String [])";

		StackTraceParser stp = new StackTraceParser();
		System.out.println(stp.parseStack(stack));
	}

	public String parseStack(String stackTrace)
	{
		StringTokenizer st = new StringTokenizer(stackTrace, ")");

		String token = "";
		while (st.hasMoreTokens())
		{
			token = token + st.nextToken().trim() + ")\n";
		}
		return token;
	}
}
