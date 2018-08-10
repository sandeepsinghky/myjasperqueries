

package org.dhhs.dirm.acts.query;

import dori.jasper.engine.JRException;
import dori.jasper.engine.JasperCompileManager;
import java.io.PrintStream;
import org.apache.commons.logging.LogFactory;
public class JasperCompiler
{
	private static final String	TASK_COMPILE	= "compile";
	private static final String	TASK_FILL		= "fill";
	private static final String	TASK_PRINT		= "print";
	private static final String	TASK_PDF		= "pdf";
	private static final String	TASK_XML		= "xml";
	private static final String	TASK_XML_EMBED	= "xmlEmbed";
	private static final String	TASK_HTML		= "html";
	private static final String	TASK_XLS		= "xls";
	private static final String	TASK_CSV		= "csv";
	private static final String	TASK_RUN		= "run";

	public static void main(String[] args)
	{
		String fileName = null;
		String taskName = null;

		taskName = "compile";

		fileName = "C:\\Users\\ssingh3\\Desktop\\temp\\jasper\\ChecksCancelledDef.xml";

		System.setProperty("jasper.reports.compile.class.path", "C:\\temp\\ActQueriesJasper\\jasperreports.jar");

		System.setProperty("jasper.reports.compile.temp", "C:\\temp\\ActQueriesJasper\\templates");

		String oldSaxDriver = System.setProperty("org.xml.sax.driver", "org.apache.xerces.parsers.SAXParser");
		if (oldSaxDriver != null)
		{
			System.setProperty("org.xml.sax.driver", oldSaxDriver);
		}
		try
		{
			long start = System.currentTimeMillis();
			if ("compile".equals(taskName))
			{
				JasperCompileManager.compileReportToFile(fileName);
				System.err.println("Compile time : " + (System.currentTimeMillis() - start));
				System.exit(0);
			}
		} catch (JRException e)
		{
			e.printStackTrace();
			System.exit(1);
		} catch (Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}

	private static void usage()
	{
		System.out.println("QueryApp usage:");
		System.out.println("\tjava QueryApp -Ttask -Ffile");
		System.out.println("\tTasks : compile | fill1 | fill2 | fill3 | fill4 | print | pdf | xml | xmlEmbed | html | xls | csv | run");
	}
}
