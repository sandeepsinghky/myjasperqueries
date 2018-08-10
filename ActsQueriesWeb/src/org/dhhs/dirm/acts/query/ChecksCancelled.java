

package org.dhhs.dirm.acts.query;

import dori.jasper.engine.JRException;
import dori.jasper.engine.JRExporterParameter;
import dori.jasper.engine.JRResultSetDataSource;
import dori.jasper.engine.JasperExportManager;
import dori.jasper.engine.JasperFillManager;
import dori.jasper.engine.JasperPrint;
import dori.jasper.engine.export.JRCsvExporter;
import dori.jasper.engine.export.JRXlsExporter;
import dori.jasper.engine.export.JRXlsExporterParameter;
import dori.jasper.engine.util.JRLoader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import javax.sql.DataSource;
import org.dhhs.dirm.acts.util.ApplicationException;
import org.dhhs.dirm.acts.util.DBConnectManager;
import org.dhhs.dirm.acts.util.EmailManager;
import org.dhhs.dirm.acts.util.ErrorDescriptor;

public class ChecksCancelled implements QueryInterface
{
	//private Connection		connection;
	private String			owner;
	private String			owneremail;
	private String			ccemail1;
	private String			ccemail2;
	private String			bcc;
	//private String			cd3ptyType;
	//private String			cdDisbType;
	private java.sql.Date	fromDate;
	private java.sql.Date	toDate;
	private String			format;
	private int				range;
	private QueryDateBean	qdb;
	private DataSource		ds;
	private String			className	= QueryOne.class.getName();
	private String			path		= "";
	private String			fileName	= "";
	@SuppressWarnings("rawtypes")
	private Hashtable		hashtable;
	private boolean			pdfRequired;
	private boolean			xmlRequired;
	private boolean			htmlRequired;
	private boolean			xlsRequired;
	private boolean			csvRequired;
	//private String			reportFileName;
	private String			emailMsg;
	private boolean			success		= true;

	public Connection getConnection() throws SQLException
	{
		return this.ds.getConnection();
	}

	public void closeConnection(Connection connection) throws SQLException
	{
		if (!connection.isClosed())
		{
			connection.close();
		}
	}

	private void log(String method, String message)
	{
		System.out.println("[" + new java.util.Date() + "] Class:" + getClass().getName() + " method:" + method + " " + message + "\n");
	}

	public String getQueryString()
	{
		String sql = "SELECT   CD_DISB_TYPE as Type,  SUM(AM_DISB) as Disbursement   FROM " +

		DBConnectManager.getQueryRegion() + ".FKKT_CHECK_COMP " + " WHERE ((DT_CHK_STAT BETWEEN '" + this.fromDate + "'" + " AND '" + this.toDate + "'" + "   AND DT_CHK_PRT <= '" + this.toDate + "'" + "   AND CD_CHK_STAT IN ('RCKC','RCKS'))" + "    OR (DT_CHK_STAT_CHG BETWEEN '" + this.fromDate + "'" + " AND '" + this.toDate + "'" + "   AND CD_CHK_STAT IN ('RCKD','RCKF'))" + ")" + " GROUP BY CD_DISB_TYPE";

		return sql;
	}

	public void formatReport(DataOutputStream dos, String s) throws IOException
	{
		dos.writeBytes(s);
	}

	public void sendEmail() throws QueryException
	{
		try
		{
			EmailManager em = new EmailManager();

			em.setAttach(true);
			em.addTo(this.owneremail);
			if (!this.ccemail1.equals(""))
			{
				em.addCc(this.ccemail1);
			}
			if (!this.ccemail2.equals(""))
			{
				em.addCc(this.ccemail2);
			}
			if (!this.bcc.equals(""))
			{
				em.addBcc(this.bcc);
			}
			em.setSubject("Acts - Automated Query Request (Checks Cancelled or Stopped) Requested by:" + this.owner);
			if (this.pdfRequired)
			{
				log("sendEmail", "Attaching File: " + this.path + System.getProperty("file.separator") + this.fileName + ".pdf");
				em.addAttachments(this.path + System.getProperty("file.separator") + this.fileName + ".pdf");
			}
			if (this.xmlRequired)
			{
				log("sendEmail", "Attaching File: " + this.path + System.getProperty("file.separator") + this.fileName + ".xml");
				em.addAttachments(this.path + System.getProperty("file.separator") + this.fileName + ".xml");
			}
			if (this.htmlRequired)
			{
				log("sendEmail", "Attaching File: " + this.path + System.getProperty("file.separator") + this.fileName + ".html");
				em.addAttachments(this.path + System.getProperty("file.separator") + this.fileName + ".html");
			}
			if (this.xlsRequired)
			{
				log("sendEmail", "Attaching File: " + this.path + System.getProperty("file.separator") + this.fileName + ".xls");
				em.addAttachments(this.path + System.getProperty("file.separator") + this.fileName + ".xls");
			}
			if (this.csvRequired)
			{
				log("sendEmail", "Attaching File: " + this.path + System.getProperty("file.separator") + this.fileName + ".csv");
				em.addAttachments(this.path + System.getProperty("file.separator") + this.fileName + ".csv");
			}
			em.setBody(this.emailMsg);

			em.sendMail();
			return;
		} catch (ApplicationException e)
		{
			ErrorDescriptor ed = new ErrorDescriptor(this.className, "sendEmail");
			ed.setErrLevel(0);
			new ApplicationException(e.getMessage(), e, ed);
			throw new QueryException(e.getMessage());
		}
	}

	public void loadProperties() throws QueryException
	{
		try
		{
			String CONFIG_BUNDLE_NAME = getClass().getName();

			PropertyResourceBundle configBundle = (PropertyResourceBundle) ResourceBundle.getBundle(CONFIG_BUNDLE_NAME);
			this.owner = configBundle.getString("QueryClass.owner");
			this.owneremail = configBundle.getString("QueryClass.owneremail");
			this.ccemail1 = configBundle.getString("QueryClass.ccemail1");
			this.ccemail2 = configBundle.getString("QueryClass.ccemail2");

			this.bcc = configBundle.getString("QueryClass.bcc");

			this.range = Integer.parseInt(configBundle.getString("QueryClass.range"));
			this.format = configBundle.getString("QueryClass.output");
		} catch (Exception e)
		{
			log("loadProperties", "Properties file exception: " + e.getMessage());
			ErrorDescriptor ed = new ErrorDescriptor(this.className, "loadProperties");
			ed.setErrLevel(0);

			new ApplicationException(e.getMessage(), e, ed);
			throw new QueryException(e.getMessage());
		}
	}

	@SuppressWarnings({"rawtypes", "unchecked", "unused"})
	public void handleConditions() throws QueryException
	{
		if ((this.format != null) && (!this.format.equals("")))
		{
			StringTokenizer tokenizer = new StringTokenizer(this.format, ",");

			this.hashtable = new Hashtable();
			while (tokenizer.hasMoreElements())
			{
				String tmp = tokenizer.nextToken();
				this.hashtable.put(tmp, tmp);
			}
		}
		if ((this.range > 1) && (this.range <= 12))
		{
			Calendar rightNow = Calendar.getInstance();

			java.util.Date now = rightNow.getTime();
			java.sql.Date runDate = new java.sql.Date(now.getTime());

			rightNow.add(2, -1);
			rightNow.getTime();

			int daysInMonth = rightNow.getActualMaximum(5);

			rightNow.set(5, daysInMonth);

			java.util.Date to = rightNow.getTime();
			java.sql.Date toDate = new java.sql.Date(to.getTime());
			log("handleConditions", "To Date: " + toDate);

			rightNow.add(2, -this.range);
			rightNow.set(5, 1);
			java.util.Date from = rightNow.getTime();
			java.sql.Date fromDate = new java.sql.Date(from.getTime());

			log("handleConditions", "From Date: " + fromDate);
		} else
		{
			this.toDate = this.qdb.getToDate();
			this.fromDate = this.qdb.getFromDate();
		}
	}

	@SuppressWarnings({"unused", "rawtypes", "unchecked"})
	public void invoke(DataSource ds, QueryDateBean qdb) throws QueryException
	{
		Connection connection = null;

		this.ds = ds;
		this.qdb = qdb;

		log("invoke", getClass().getName() + " Invoked");

		loadProperties();

		handleConditions();
		try
		{
			connection = getConnection();
		} catch (SQLException e)
		{
			ErrorDescriptor ed = new ErrorDescriptor(this.className, "getConnection");
			ed.setErrLevel(0);

			new ApplicationException(e.getMessage(), e, ed);
			return;
		}
		String sql = getQueryString();

		log("invoke", sql);

		StringBuffer buf = new StringBuffer();

		buf.append("NOTE: PLEASE DO NOT RESPOND DIRECTLY TO THIS E-MAIL MESSAGE. THIS ADDRESS IS NOT MONITORED.\n\n");

		buf.append("This message has been sent in response to your query request for Checks Issued Grouped by Disbursements. If you did not request this service or believe this message has been sent to you in error, please contact ACTS Help Desk..\n\n");

		this.emailMsg = buf.toString();

		File f = makeDirs();

		this.fileName = makeFile();
		try
		{
			Statement s = connection.createStatement();

			ResultSet r = s.executeQuery(sql);

			Map parameters = new HashMap();

			java.util.Date fromDt = new java.util.Date(this.fromDate.getTime());
			java.util.Date toDt = new java.util.Date(this.toDate.getTime());

			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");

			String reportPeriod = "Report Period: " + sdf.format(fromDt) + " thru " + sdf.format(toDt);
			parameters.put("ReportPeriod", reportPeriod);
			System.out.println("Report Period " + reportPeriod);
			try
			{
				String rf = this.path + System.getProperty("file.separator") + "ChecksCancelled.jasper";
				
				String df = this.path + System.getProperty("file.separator") + this.fileName;
				System.out.println("rf: " + rf + " df " + df);
				JasperFillManager.fillReportToFile(rf, df, parameters, new JRResultSetDataSource(r));
				if (this.hashtable.containsKey("pdf"))
				{
					this.pdfRequired = true;
					String pdf = this.path + System.getProperty("file.separator") + this.fileName + ".pdf";
					JasperExportManager.exportReportToPdfFile(df, pdf);
				}
				if (this.hashtable.containsKey("xml"))
				{
					this.xmlRequired = true;
					String xml = this.path + System.getProperty("file.separator") + this.fileName + ".xml";
					JasperExportManager.exportReportToXmlFile(df, xml, true);
				}
				if (this.hashtable.containsKey("html"))
				{
					this.htmlRequired = true;
					String html = this.path + System.getProperty("file.separator") + this.fileName + ".html";
					JasperExportManager.exportReportToHtmlFile(df, html);
				}
				if (this.hashtable.containsKey("xls"))
				{
					this.xlsRequired = true;

					File sourceFile = new File(df);

					JasperPrint jasperPrint = (JasperPrint) JRLoader.loadObject(sourceFile);

					File destFile = new File(df + ".xls");

					JRXlsExporter exporter = new JRXlsExporter();

					exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
					exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, destFile.toString());
					exporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);

					exporter.exportReport();
				}
				if (this.hashtable.containsKey("csv"))
				{
					this.csvRequired = true;

					File sourceFile = new File(df);

					JasperPrint jasperPrint = (JasperPrint) JRLoader.loadObject(sourceFile);

					File destFile = new File(df + ".csv");

					JRCsvExporter exporter = new JRCsvExporter();

					exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
					exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, destFile.toString());

					exporter.exportReport();
				}
			} catch (JRException jre)
			{
				jre.printStackTrace();
				this.success = false;
			}
			r.close();
			s.close();
		} catch (SQLException e)
		{
			ErrorDescriptor ed = new ErrorDescriptor(this.className, "invoke");
			ed.setErrLevel(0);

			new ApplicationException(e.getMessage(), e, ed);

			this.success = false;
		}
		try
		{
			closeConnection(connection);
		} catch (SQLException e)
		{
			ErrorDescriptor ed = new ErrorDescriptor(this.className, "closeConnection");
			ed.setErrLevel(0);

			new ApplicationException(e.getMessage(), e, ed);
			return;
		}
		if (this.success)
		{
			sendEmail();
		}
	}

	public File makeDirs()
	{
		this.path = (QueryPropertyLoader.getPath() + System.getProperty("file.separator") + "ChecksCancelled");

		File f = new File(this.path);
		if (!f.exists())
		{
			f.mkdirs();
		}
		return f;
	}

	public String makeFile()
	{
		return "R" + this.fromDate.toString() + "-" + this.toDate.toString();
	}
}
