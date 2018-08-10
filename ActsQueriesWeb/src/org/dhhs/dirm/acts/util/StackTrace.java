

package org.dhhs.dirm.acts.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.StringTokenizer;
import java.util.Vector;

public class StackTrace
{
	@SuppressWarnings("unused")
	private StackLine	_currLine;
	@SuppressWarnings("rawtypes")
	private Vector		_lines	= new Vector();
	private Throwable	_throwable;
	int					_index	= -1;

	protected StackTrace(Throwable th)
	{
		this._throwable = th;
		parseStack();
	}

	public static StackTrace getStackTrace(Throwable th)
	{
		return new StackTrace(th);
	}

	@SuppressWarnings("unchecked")
	protected void parseStack()
	{
		String trace = toString();
		StringTokenizer st = new StringTokenizer(trace);
		trace = "";
		while (st.hasMoreTokens())
		{
			String token = st.nextToken().trim();
			do
			{
				token = st.nextToken().trim();
				if (!st.hasMoreElements())
				{
					break;
				}
			} while (!token.equals("at"));
			while ((token != null) && ((token.equals("at")) || (token.equals(""))))
			{
				token = st.nextToken().trim();
			}
			trace = trace + token + "\n";
		}
		st = new StringTokenizer(trace);
		String token = null;
		while (st.hasMoreTokens())
		{
			token = st.nextToken().trim();
			if (token.endsWith("(Compiled"))
			{
				token = token + ")";
			}
			_StackLine line = new _StackLine();
			int index1 = token.indexOf("(");
			int index2 = token.indexOf(")");
			if (index2 == -1)
			{
				index2 = token.length() - 1;
			}
			String tmpString = token.substring(0, index1);
			int dotIndex = tmpString.lastIndexOf(".");
			line.setMethodName(tmpString.substring(dotIndex + 1));
			tmpString = token.substring(0, dotIndex);
			dotIndex = tmpString.lastIndexOf(".");
			line.setClassName(tmpString.substring(dotIndex + 1));
			if (dotIndex != -1)
			{
				tmpString = token.substring(0, dotIndex);
				line.setPackage(tmpString);
			}
			tmpString = token.substring(index1 + 1, index2);

			int colonIndex = tmpString.indexOf(":");
			if (colonIndex != -1)
			{
				line.setSourceFilename(tmpString.substring(0, colonIndex));
				String lineNum = tmpString.substring(colonIndex + 1, tmpString.length());
				line.setSourceFileLineNumber(Integer.parseInt(lineNum));
			}
			this._lines.addElement(line);
		}
	}

	public StackLine getStackLine(int i)
	{
		return (StackLine) this._lines.elementAt(i);
	}

	public int getStackDepth()
	{
		return this._lines.size();
	}

	public String toString()
	{
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		this._throwable.printStackTrace(pw);
		pw.flush();
		return sw.toString();
	}
}
