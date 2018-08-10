

package org.dhhs.dirm.acts.util;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Vector;

public class ErrorDescriptor
{
	private String		errClass;
	private String		errMethod;
	private String		errMessage;
	private Object		errObject;
	private Timestamp	timestamp;
	private int			errLevel;
	private boolean		errNotify		= true;
	private boolean		errLogRequired	= true;
	private Vector		appVariables;

	public ErrorDescriptor(String errClass, String errMethod)
	{
		this.errClass = errClass;

		this.errMethod = errMethod;

		buildTimestamp();
	}

	public ErrorDescriptor(String errClass, String errMethod, String errMessage, Object errObject)
	{
		this.errClass = errClass;

		this.errMethod = errMethod;

		this.errMessage = errMessage;

		this.errObject = errObject;

		buildTimestamp();
	}

	public void addAppVariable(String var)
	{
		this.appVariables.addElement(var);
	}

	private void buildTimestamp()
	{
		Calendar c = Calendar.getInstance();

		java.util.Date now = c.getTime();

		java.sql.Date date = new java.sql.Date(now.getTime());

		this.timestamp = new Timestamp(date.getTime());

		this.errLogRequired = true;

		this.errNotify = true;

		this.errLevel = 1;

		this.appVariables = new Vector();
	}

	public boolean equals(Object obj)
	{
		return super.equals(obj);
	}

	public Vector getAppVariables()
	{
		return this.appVariables;
	}

	public String getErrClass()
	{
		return this.errClass;
	}

	public int getErrLevel()
	{
		return this.errLevel;
	}

	public String getErrMessage()
	{
		return this.errMessage;
	}

	public String getErrMethod()
	{
		return this.errMethod;
	}

	public Object getErrObject()
	{
		return this.errObject;
	}

	public Timestamp getTimestamp()
	{
		return this.timestamp;
	}

	public int hashCode()
	{
		return super.hashCode();
	}

	public boolean isErrLogRequired()
	{
		return this.errLogRequired;
	}

	public boolean isErrNotify()
	{
		return this.errNotify;
	}

	public void setAppVariables(Vector newAppVariables)
	{
		this.appVariables = newAppVariables;
	}

	public void setErrClass(String newErrClass)
	{
		this.errClass = newErrClass;
	}

	public void setErrLevel(int newErrLevel)
	{
		this.errLevel = newErrLevel;
	}

	public void setErrLogRequired(boolean newErrLogRequired)
	{
		this.errLogRequired = newErrLogRequired;
	}

	public void setErrMessage(String newErrMessage)
	{
		this.errMessage = newErrMessage;
	}

	public void setErrMethod(String newErrMethod)
	{
		this.errMethod = newErrMethod;
	}

	public void setErrNotify(boolean newErrNotify)
	{
		this.errNotify = newErrNotify;
	}

	public void setErrObject(Object newErrObject)
	{
		this.errObject = newErrObject;
	}
}
