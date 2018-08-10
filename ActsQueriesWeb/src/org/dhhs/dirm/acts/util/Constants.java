

package org.dhhs.dirm.acts.util;

public abstract interface Constants
{
	public static final int		FATAL					= 0;
	public static final int		ERROR					= 1;
	public static final int		WARNING					= 2;
	public static final int		INFO					= 3;
	public static final int		DEBUG					= 4;
	public static final int		EXCEPTION				= 0;
	public static final int		IOEXCEPTION				= 1;
	public static final int		NAMINGEXCEPTION			= 2;
	public static final int		SQLEXCEPTION			= 3;
	public static final int		SOCKETEXCEPTION			= 4;
	public static final int		MESSAGINGEXCEPTION		= 5;
	public static final int		APPLICATIONEXCEPTION	= 6;
	public static final int		SERVLETEXCEPTION		= 7;
	public static final int		JDBCDRIVEREXCEPTION		= 8;
	public static final int		OTHEREXCEPTION			= 9;
	public static final String	DATABASE_EXCEPTION		= "A Severe Database Exception has occurred. Your login has been invalidated. Please try to Login after a few minutes.";
	public static final String	OTHER_EXCEPTION			= "Our WebSite is experiencing some technical difficulties. Please try to Login after a few minutes.";
	public static final String	APPLICATION_ERROR_PAGE	= "ApplicationError.jsp";
}
