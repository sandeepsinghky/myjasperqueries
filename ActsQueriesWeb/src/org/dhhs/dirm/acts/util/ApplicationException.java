

package org.dhhs.dirm.acts.util;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Vector;
import javax.mail.MessagingException;
import org.apache.log4j.Category;
import org.apache.log4j.PropertyConfigurator;
import org.dhhs.dirm.acts.query.QueryPropertyLoader;

public class ApplicationException extends Exception implements Constants
{
	private String				message;
	private int					errorLevel;
	private int					errSQLCode;
	private String				errSQLState;
	private Throwable			cause						= null;
	private ErrorDescriptor		errorDescriptor				= null;
	private static final String	CONFIG_BUNDLE_NAME			= "org.dhhs.dirm.acts.util.ApplicationConfig";
	private static final String	LOG4J_CONFIG_BUNDLE_NAME	= QueryPropertyLoader.getPath() + System.getProperty("file.separator") + "Log4JConfig.properties";
	private static boolean		logEnabled;
	private static boolean		emailEnabled;
	private static boolean		instantMsgEnabled;
	private static String		adminEmail					= "";
	private static int			emailMsgLevel;
	private static String		instantMsgNotify			= "";
	private static int			instantMsgLevel;
	private static String		applicationName				= "";
	private static String		applicationURL				= "";
	private int					error_code					= -1;
	private static Category		thisCategory				= Category.getInstance("org.dhhs.dirm.acts.util.ApplicationException");

	static
	{
		try
		{
			PropertyResourceBundle configBundle = (PropertyResourceBundle) ResourceBundle.getBundle("org.dhhs.dirm.acts.util.ApplicationConfig");
			String enableLog = configBundle.getString("application.logging");

			String enableEmail = configBundle.getString("application.emailMsg");
			adminEmail = configBundle.getString("application.emailMsgNotify");
			emailMsgLevel = Integer.parseInt(configBundle.getString("application.emailMsgLevel"));
			if (enableEmail.equalsIgnoreCase("TRUE"))
			{
				emailEnabled = true;
			} else
			{
				emailEnabled = false;
			}
			String enableInstantMsg = configBundle.getString("application.instantMsg");
			instantMsgNotify = configBundle.getString("application.instantMsgNotify");
			instantMsgLevel = Integer.parseInt(configBundle.getString("application.instantMsgLevel"));

			applicationName = configBundle.getString("application.appName");
			applicationURL = configBundle.getString("application.primaryURL");
			if (enableInstantMsg.equalsIgnoreCase("TRUE"))
			{
				instantMsgEnabled = true;
			} else
			{
				instantMsgEnabled = false;
			}
			if (enableLog.equalsIgnoreCase("TRUE"))
			{
				logEnabled = true;
			} else
			{
				logEnabled = false;
			}
			if (logEnabled)
			{
				PropertyConfigurator.configure(LOG4J_CONFIG_BUNDLE_NAME);
			}
		} catch (Exception e)
		{
			thisCategory.error("Properties File Exception. Failed to Initialize ApplicationException Class");
		}
	}

	public ApplicationException(String s, ErrorDescriptor errorDescriptor)
	{
		super(s);

		this.cause = null;

		this.errorDescriptor = errorDescriptor;

		analyzeException();
	}

	public ApplicationException(String s, Throwable cause)
	{
		super(s);

		this.cause = cause;

		analyzeException();
	}

	public ApplicationException(String s, Throwable cause, ErrorDescriptor errorDescriptor)
	{
		super(s);

		this.cause = cause;

		this.errorDescriptor = errorDescriptor;

		analyzeException();
	}

	private void analyzeException()
	{
		boolean notify = true;

		this.errorLevel = 4;
		if (this.cause != null)
		{
			if ((this.cause instanceof SQLException))
			{
				AnalyzeSQLException ae = new AnalyzeSQLException((SQLException) this.cause);
				if (ae.sqlException())
				{
					this.error_code = 3;
				} else if (ae.socketException())
				{
					this.error_code = 4;
				} else if (ae.driverException())
				{
					this.error_code = 8;
				} else
				{
					this.error_code = 9;
				}
			} else if ((this.cause instanceof MessagingException))
			{
				notify = false;

				thisCategory.fatal("EmailManager failed with MessagingException", this.cause);
			} else if ((this.cause instanceof ApplicationException))
			{
				notify = false;

				thisCategory.info("ApplicationException. May already been handled");
			} else
			{
				this.error_code = 9;
				this.cause.printStackTrace();
			}
		} else if (this.errorDescriptor != null)
		{
			this.error_code = 6;
		}
		if (this.errorDescriptor != null)
		{
			this.errorLevel = this.errorDescriptor.getErrLevel();
		}
		switch (this.error_code)
		{
			case 6 :
				this.message = this.errorDescriptor.getErrMessage();
				break;
			case 3 :
				this.message = (this.cause != null ? this.cause.toString() : this.errorDescriptor.getErrMessage());
				if (this.cause != null)
				{
					this.errSQLCode = ((SQLException) this.cause).getErrorCode();
					this.errSQLState = ((SQLException) this.cause).getSQLState();
				}
				break;
			case 4 :
			case 5 :
			default :
				this.message = (this.cause != null ? this.cause.toString() : this.errorDescriptor.getErrMessage());
		}
		ExceptionStack.addToStack(this.message);
		if (notify)
		{
			if (this.errorLevel <= emailMsgLevel)
			{
				ExceptionStackItem item = ExceptionStack.getItem(this.message);
				if ((item != null) && (!item.getItemState()))
				{
					initiateEmailNotification();
				}
			}
			if (this.errorLevel <= instantMsgLevel)
			{
				ExceptionStackItem item = ExceptionStack.getItem(this.message);
				if ((item != null) && (!item.getItemState()))
				{
					initiateInstantMsg();
				}
			}
		}
		logError();
	}

	public boolean equals(Object obj)
	{
		return super.equals(obj);
	}

	protected void finalize() throws Throwable
	{
		super.finalize();
	}

	private String getStackTrace1()
	{
		StringWriter sw = new StringWriter();

		PrintWriter pw = new PrintWriter(sw);

		this.cause.printStackTrace(pw);

		return sw.toString();
	}

	public int hashCode()
	{
		return super.hashCode();
	}

	public void initiateEmailNotification()
	{
		boolean sendMail = false;
		String emailTitle = "";
		if (emailEnabled)
		{
			if (this.errorDescriptor != null)
			{
				if (this.errorDescriptor.isErrNotify())
				{
					sendMail = true;
				}
			} else
			{
				sendMail = true;
			}
		}
		String strSubjectLine = this.message;

		String errStackTrace = getStackTrace1();

		String errDetails = applicationName + "@" + applicationURL;
		String errClass = this.errorDescriptor.getErrClass();
		String errMethod = this.errorDescriptor.getErrMethod();
		String errMessage = strSubjectLine;

		String emailHeader = "<html>                                                                                                          \n<head>                                                                                                          \n<title>" +

		emailTitle + "</title>                                                                                \n" + "<meta http-equiv='Content-Type' content='text/html; charset=iso-8859-1'>                                        \n" + "</head>                                                                                                         \n" + "                                                                                                                \n" + "<body bgcolor='#FFFFFF' text='#000000'>                                                                         \n";

		String emailBody = "<table width='75%' border='1' cellpadding='0' cellspacing='0' align='center'>                                   \n  <tr bgcolor='#CCCCCC'>                                                                                        \n    <td colspan='2'>                                                                                            \n      <div align='center'><font face='Verdana, Arial, Helvetica, sans-serif' size='2'><b>eChild                 \n        Support Application Error Description</b></font></div>                                                  \n    </td>                                                                                                       \n  </tr>                                                                                                         \n  <tr>                                                                                                          \n    <td width='35%'><font face='Verdana, Arial, Helvetica, sans-serif' size='2'>&nbsp</font></td>                    \n    <td width='65%'><font face='Verdana, Arial, Helvetica, sans-serif' size='2'>&nbsp</font></td>                    \n  </tr>                                                                                                         \n  <tr>                                                                                                          \n    <td width='35%' bgcolor='#CCCCCC'><font face='Verdana, Arial, Helvetica, sans-serif' size='2'>Application   \n      Details: </font></td>                                                                                     \n    <td width='65%'><font face='Verdana, Arial, Helvetica, sans-serif' size='2'> " +

		errDetails + "</font></td>  \n" + "  </tr>                                                                                                         \n" + "  <tr>                                                                                                          \n" + "    <td width='35%'><font face='Verdana, Arial, Helvetica, sans-serif' size='2'>&nbsp</font></td>                    \n" + "    <td width='65%'><font face='Verdana, Arial, Helvetica, sans-serif' size='2'>&nbsp</font></td>                    \n" + "  </tr>                                                                                                         \n" + "  <tr>                                                                                                          \n" + "    <td width='35%' bgcolor='#CCCCCC'><font face='Verdana, Arial, Helvetica, sans-serif' size='2'>Error         \n" + "      Class Name:</font></td>                                                                                   \n" + "    <td width='65%'><font face='Verdana, Arial, Helvetica, sans-serif' size='2'>" + errClass + "</font></td>    \n" + "  </tr>                                                                                                         \n" + "  <tr>                                                                                                          \n" + "    <td width='35%'><font face='Verdana, Arial, Helvetica, sans-serif' size='2'>&nbsp</font></td>                    \n" + "    <td width='65%'><font face='Verdana, Arial, Helvetica, sans-serif' size='2'>&nbsp</font></td>                    \n" + "  </tr>                                                                                                         \n" + "  <tr>                                                                                                          \n" + "    <td width='35%' bgcolor='#CCCCCC'><font face='Verdana, Arial, Helvetica, sans-serif' size='2'>Error         \n" + "      Method Name:</font></td>                                                                                  \n" + "    <td width='65%'><font face='Verdana, Arial, Helvetica, sans-serif' size='2'>" + errMethod + "</font></td>   \n" + "  </tr>                                                                                                         \n" + "  <tr>                                                                                                          \n" + "    <td width='35%'><font face='Verdana, Arial, Helvetica, sans-serif' size='2'>&nbsp</font></td>                    \n" + "    <td width='65%'><font face='Verdana, Arial, Helvetica, sans-serif' size='2'>&nbsp</font></td>                    \n" + "  </tr>                                                                                                         \n" + "  <tr>                                                                                                          \n" + "    <td width='35%' bgcolor='#CCCCCC'><font face='Verdana, Arial, Helvetica, sans-serif' size='2'>Error         \n" + "      Message: </font></td>                                                                                     \n" + "    <td width='65%'><font face='Verdana, Arial, Helvetica, sans-serif' size='2'>" + errMessage + "</font></td>  \n" + "  </tr>                                                                                                         \n" + "  <tr>                                                                                                          \n" + "    <td width='35%'><font face='Verdana, Arial, Helvetica, sans-serif' size='2'>&nbsp</font></td>                    \n" + "    <td width='65%'><font face='Verdana, Arial, Helvetica, sans-serif' size='2'>&nbsp</font></td>                    \n" + "  </tr>                                                                                                         \n" + "  <tr>                                                                                                          \n" + "    <td width='35%' bgcolor='#CCCCCC'><font face='Verdana, Arial, Helvetica, sans-serif' size='2'>SQL           \n" + "      Code: </font></td>                                                                                        \n" + "    <td width='65%'><font face='Verdana, Arial, Helvetica, sans-serif' size='2'>" + this.errSQLCode + "</font></td>  \n" + "  </tr>                                                                                                         \n" + "  <tr>                                                                                                          \n" + "    <td width='35%'><font face='Verdana, Arial, Helvetica, sans-serif' size='2'>&nbsp</font></td>                    \n" + "    <td width='65%'><font face='Verdana, Arial, Helvetica, sans-serif' size='2'>&nbsp</font></td>                    \n" + "  </tr>                                                                                                         \n" + "  <tr>                                                                                                          \n" + "    <td width='35%' bgcolor='#CCCCCC'><font face='Verdana, Arial, Helvetica, sans-serif' size='2'>SQL           \n" + "      State: </font></td>                                                                                       \n" + "    <td width='65%'><font face='Verdana, Arial, Helvetica, sans-serif' size='2'>" + this.errSQLState + "</font></td> \n" + "  </tr>                                                                                                         \n" + "  <tr>                                                                                                          \n" + "    <td width='35%'><font face='Verdana, Arial, Helvetica, sans-serif' size='2'>&nbsp</font></td>                    \n" + "    <td width='65%'><font face='Verdana, Arial, Helvetica, sans-serif' size='2'>&nbsp</font></td>                    \n" + "  </tr>                                                                                                         \n" + "  <tr>                                                                                                          \n" + "    <td width='35%' bgcolor='#CCCCCC'><font face='Verdana, Arial, Helvetica, sans-serif' size='2'>Stack         \n" + "      Trace: </font></td>                                                                                       \n" + "    <td width='65%'>&nbsp;</td>                                                                                       \n" + "  </tr>                                                                                                         \n" + "  <tr>                                                                                                          \n" + "    <td colspan='2'><font face='Verdana, Arial, Helvetica, sans-serif' size='2'> " + errStackTrace + "</font></td> \n" + "  </tr>                                                                                                         \n" + "</table>                                                                                                        \n" + "<br>                                                                                                            \n";

		String emailFooter = "</body>                                                                                                         \n</html>                                                                                                         \n";

		String appVariableHeader = "<table width='75%' border='1' cellpadding='0' cellspacing='0' align='center'>                                   \n  <tr bgcolor='#CCCCCC'>                                                                                        \n    <td colspan='2'>                                                                                            \n      <div align='center'><font face='Verdana, Arial, Helvetica, sans-serif' size='2'><b>eChild                 \n        Support Application Variables</b></font></div>                                                          \n    </td>                                                                                                       \n  </tr>                                                                                                         \n";

		String appVariableFooter = "</table>  \t                                                                                                   \n";
		if (sendMail)
		{
			try
			{
				EmailManager em = new EmailManager();

				em.addTo(adminEmail);

				em.setSubject(strSubjectLine);

				String body = emailHeader;

				body = body + emailBody;

				Vector appVariables = this.errorDescriptor.getAppVariables();
				if (appVariables.size() > 0)
				{
					body = body + appVariableHeader;
				}
				for (int i = 0; i < appVariables.size(); i++)
				{
					String appVariable = (String) appVariables.elementAt(i);
					String appVariableBody = "  <tr>                                                                                                          \n    <td width='35%'><font face='Verdana, Arial, Helvetica, sans-serif' size='2'>&nbsp</font></td>               \n    <td width='65%'><font face='Verdana, Arial, Helvetica, sans-serif' size='2'>&nbsp</font></td>               \n  </tr>                                                                                                         \n  <tr>                                                                                                          \n    <td width='35%' bgcolor='#CCCCCC'><font face='Verdana, Arial, Helvetica, sans-serif' size='2'>Application   \n      Trace: </font></td>                                                                                       \n    <td width='65%'>&nbsp;</td>                                                                                 \n  </tr>                                                                                                         \n  <tr>                                                                                                          \n    <td colspan='2'><font face='Verdana, Arial, Helvetica, sans-serif' size='2'> " +

					appVariable + "</font></td>\n" + "  </tr>                                                                                                         \n";
					body = body + appVariableBody;
				}
				if (appVariables.size() > 0)
				{
					body = body + appVariableFooter;
				}
				body = body + emailFooter;

				em.setBody(body);
				em.sendMailHTML();
			} catch (ApplicationException ae)
			{
				thisCategory.fatal("EmailManager Failed to notify Administrator", ae);
			}
		}
	}

	public void initiateInstantMsg()
	{
		boolean sendInstantMsg = false;
		if (instantMsgEnabled)
		{
			if (this.errorDescriptor != null)
			{
				if (this.errorDescriptor.isErrNotify())
				{
					sendInstantMsg = true;
				}
			} else
			{
				sendInstantMsg = true;
			}
		}
		String errDetails = applicationURL;
		String errClass = this.errorDescriptor.getErrClass();
		String errMethod = this.errorDescriptor.getErrMethod();

		StringBuffer instantMsg = new StringBuffer();
		instantMsg.append("App:" + errDetails + "\n");
		instantMsg.append("SQLCode:" + this.errSQLCode + "\n");
		instantMsg.append("SQLState:" + this.errSQLState + "\n");
		if (sendInstantMsg)
		{
			try
			{
				EmailManager em = new EmailManager();

				em.addTo(instantMsgNotify);

				em.setSubject(this.message);

				em.setBody(instantMsg.toString());
				em.sendMail();
			} catch (ApplicationException ae)
			{
				thisCategory.fatal("EmailManager Failed to notify Administrator", ae);
			}
		}
	}

	private void logError()
	{
		String log4jCategory = "com.sysrad.util.ApplicationException";

		int log4jLevel = 0;

		boolean log = false;
		if (logEnabled)
		{
			if (this.errorDescriptor != null)
			{
				log4jCategory = this.errorDescriptor.getErrClass();
				log4jLevel = this.errorDescriptor.getErrLevel();
				if (this.errorDescriptor.isErrLogRequired())
				{
					log = true;
				}
			} else
			{
				log = true;
			}
		} else
		{
			log = false;
		}
		if (log)
		{
			Category category = Category.getInstance(log4jCategory);
			switch (log4jLevel)
			{
				case 0 :
					category.fatal(this.message, this.cause);
					break;
				case 1 :
					category.error(this.message, this.cause);
					break;
				case 2 :
					category.warn(this.message, this.cause);
					break;
				case 3 :
					category.info(this.message, this.cause);
					break;
				case 4 :
					category.debug(this.message, this.cause);
			}
		}
	}

	public static void main(String[] args)
	{
		for (int i = 0; i < 5; i++)
		{
			try
			{
				throw new SQLException("Resource Unavailable", "This is a test", -904);
			} catch (SQLException e)
			{
				ErrorDescriptor ed = new ErrorDescriptor("ApplicationException", "main");
				ed.setErrLevel(0);

				new ApplicationException(e.getMessage(), e, ed);
			}
		}
	}

	public void printStackTrace()
	{
		super.printStackTrace();
		if (this.cause != null)
		{
			this.cause.printStackTrace();
		}
	}

	public void printStackTrace(PrintStream ps)
	{
		super.printStackTrace();
		if (this.cause != null)
		{
			this.cause.printStackTrace();
		}
	}

	public void printStackTrace(PrintWriter pw)
	{
		super.printStackTrace();
		if (this.cause != null)
		{
			this.cause.printStackTrace();
		}
	}

	public String toString()
	{
		return super.toString();
	}
}
