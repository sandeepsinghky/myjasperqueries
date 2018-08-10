

package org.dhhs.dirm.acts.util;

public abstract interface StackLine
{
	public abstract String getPackage();

	public abstract int getSourceFileLineNumber();

	public abstract String getSourceFilename();

	public abstract String getClassName();

	public abstract String getFullClassName();

	public abstract String getMethodName();
}
