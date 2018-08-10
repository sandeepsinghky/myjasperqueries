

package org.dhhs.dirm.acts.util;

class _StackLine implements StackLine
{
	private String	_package;
	private String	_className;
	private String	_sourceFilename;
	private int		_sourceLineNumber;
	private String	_methodName;

	public String getPackage()
	{
		return this._package;
	}

	public void setPackage(String p)
	{
		this._package = p;
	}

	public int getSourceFileLineNumber()
	{
		return this._sourceLineNumber;
	}

	public void setSourceFileLineNumber(int num)
	{
		this._sourceLineNumber = num;
	}

	public String getSourceFilename()
	{
		return this._sourceFilename;
	}

	public void setSourceFilename(String filename)
	{
		this._sourceFilename = filename;
	}

	public String getClassName()
	{
		return this._className;
	}

	public void setClassName(String className)
	{
		this._className = className;
	}

	public String getFullClassName()
	{
		String pkg = getPackage();
		String fullClassName = getClassName();
		if (pkg != null)
		{
			fullClassName = pkg + "." + fullClassName;
		}
		return fullClassName;
	}

	public String getMethodName()
	{
		return this._methodName;
	}

	public void setMethodName(String method)
	{
		this._methodName = method;
	}

	public String toString()
	{
		StringBuffer buf = new StringBuffer();
		buf.append(getPackage());
		buf.append(".");
		buf.append(getClassName());
		buf.append(".");
		buf.append(getMethodName());
		buf.append("(");
		buf.append(getSourceFilename());
		buf.append(":");
		buf.append(new Integer(getSourceFileLineNumber()));
		buf.append(")");
		return buf.toString();
	}
}
