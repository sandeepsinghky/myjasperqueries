

package org.dhhs.dirm.acts.util;

import java.sql.SQLException;

public class AnalyzeSQLException
{
	private SQLException		exception;
	private int					sqlcode;
	private String				sqlstate;
	private String				reason;
	private static final String	SOCKET_EXCEPTION		= "java.net.SocketException";
	private static final String	HITDB2_EXCEPTION		= "hit.db2";
	private static final String	HITLICENSE_EXCEPTION	= "hit.license";
	private static final String	HIT_EXCEPTION			= "hit";
	private static final int	HITDB2					= 0;
	private static final int	HITLICENSE				= 1;
	private static final int	HIT						= 2;
	private int					errorcode;

	public AnalyzeSQLException()
	{
	}

	public AnalyzeSQLException(SQLException e)
	{
		this.exception = e;
		this.sqlcode = e.getErrorCode();
		this.sqlstate = e.getSQLState();
		this.reason = e.toString();
	}

	public boolean driverException()
	{
		boolean rtn = false;
		if ((this.reason != null) && (this.reason.length() > 0))
		{
			if (this.reason.indexOf("hit.db2") > 0)
			{
				this.errorcode = 0;
				return true;
			}
			if (this.reason.indexOf("hit.license") > 0)
			{
				this.errorcode = 1;
				return true;
			}
			if (this.reason.indexOf("hit") > 0)
			{
				this.errorcode = 2;
				return true;
			}
		}
		return rtn;
	}

	public boolean socketException()
	{
		boolean rtn = false;
		if ((this.reason != null) && (this.reason.length() > 0) && (this.reason.indexOf("java.net.SocketException") > 0))
		{
			return true;
		}
		return rtn;
	}

	public boolean sqlException()
	{
		boolean rtn = true;
		switch (this.sqlcode)
		{
			case 0 :
				if (this.sqlstate == null)
				{
					rtn = false;
				}
				break;
		}
		return rtn;
	}
}
