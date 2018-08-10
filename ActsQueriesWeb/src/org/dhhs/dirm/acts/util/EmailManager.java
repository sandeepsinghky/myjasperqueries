

package org.dhhs.dirm.acts.util;

import java.io.PrintStream;
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Vector;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Service;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class EmailManager implements Constants
{
	private Vector				to;
	private String				from;
	private Vector				bcc;
	private Vector				cc;
	private String				subject;
	private String				body;
	private String				host;
	private boolean				debug;
	private String				user;
	private String				password;
	private boolean				attach;
	private Vector				attachments;
	private static final String	className				= "EmailManager";
	private String				methodName				= "";
	private static final String	CONFIG_BUNDLE_NAME		= "org.dhhs.dirm.acts.util.ApplicationConfig";
	public static final String	ERR_TO_ADDRESS			= "Error: To Address Missing";
	public static final String	ERR_FROM_ADDRESS		= "Error: From Address Missing";
	public static final String	ERR_INVALID_HOST_NAME	= "Error: Invalid Host Name";
	public static final String	ERR_WITH_ATTACHMENTS	= "Error: Failed to Load Attachments";
	public static final String	ERR_SEND_FAILED			= "Error: EmailManager failed to send email";
	public static final String	ERR_PROPERTIES			= "Error: Failed to read properties file";
	public static final String	ERR_MULTIPLE			= "Error: Failed to set either from, to, cc or bcc";

	public EmailManager() throws ApplicationException
	{
		this.methodName = "EmailManager()";
		try
		{
			PropertyResourceBundle configBundle = (PropertyResourceBundle) ResourceBundle.getBundle("org.dhhs.dirm.acts.util.ApplicationConfig");
			this.host = configBundle.getString("mailServer.host");
			this.user = configBundle.getString("mailServer.user");
			this.password = configBundle.getString("mailServer.password");
			this.from = configBundle.getString("mailServer.from");

			this.subject = "";
			this.body = "";

			this.to = new Vector();
			this.bcc = new Vector();
			this.cc = new Vector();
			this.attachments = new Vector();
		} catch (Exception e)
		{
			throw new ApplicationException("Error: EmailManager failed to send email", e);
		}
	}

	public void addAttachments(String s)
	{
		this.attachments.addElement(s);
	}

	public void addBcc(String s)
	{
		this.bcc.addElement(s);
	}

	public void addCc(String s)
	{
		this.cc.addElement(s);
	}

	public void addTo(String s)
	{
		this.to.addElement(s);
	}

	public String getBody()
	{
		return this.body;
	}

	public String getFrom()
	{
		return this.from;
	}

	public String getHost()
	{
		return this.host;
	}

	public String getSubject()
	{
		return this.subject;
	}

	public boolean isAttach()
	{
		return this.attach;
	}

	public boolean isDebug()
	{
		return this.debug;
	}

	public static void main(String[] args)
	{
		try
		{
			EmailManager em = new EmailManager();
			em.setAttach(true);

			em.addTo("rkodumagulla@sysrad.com");

			em.sendMail();
		} catch (ApplicationException ae)
		{
			ae.printStackTrace();
			System.out.println(ae.getMessage());
		}
	}

	private void processAttachments(Multipart mp) throws MessagingException
	{
		for (int i = 0; i < this.attachments.size(); i++)
		{
			MimeBodyPart mbpAttachment = new MimeBodyPart();

			String attachment = (String) this.attachments.elementAt(i);
			if (!attachment.equals(""))
			{
				MimeBodyPart mbpMessage = new MimeBodyPart();
				mbpMessage.setText("The following file is attached - " + attachment);
				mp.addBodyPart(mbpMessage);

				FileDataSource fds = new FileDataSource(attachment);

				mbpAttachment.setDataHandler(new DataHandler(fds));
				mbpAttachment.setFileName(fds.getName());
				mp.addBodyPart(mbpAttachment);
			}
		}
	}

	public void sendMail() throws ApplicationException
	{
		this.methodName = "sendMail";
		try
		{
			Properties props = new Properties();
			if (this.to == null)
			{
				ErrorDescriptor ed = new ErrorDescriptor("EmailManager", this.methodName, "Error: To Address Missing", null);
				throw new ApplicationException("Error: To Address Missing", ed);
			}
			if (this.from == null)
			{
				ErrorDescriptor ed = new ErrorDescriptor("EmailManager", this.methodName, "Error: From Address Missing", null);
				throw new ApplicationException("Error: From Address Missing", ed);
			}
			if (this.host == null)
			{
				ErrorDescriptor ed = new ErrorDescriptor("EmailManager", this.methodName, "Error: Invalid Host Name", null);
				throw new ApplicationException("Error: Invalid Host Name", ed);
			}
			props.put("mail.smtp.host", this.host);
			props.put("mail.smtp.auth", "true");
			if (this.debug)
			{
				props.put("mail.debug", "true");
			}
			Session session = Session.getDefaultInstance(props, null);
			session.setDebug(this.debug);

			MimeMessage msg = new MimeMessage(session);
			try
			{
				msg.setFrom(new InternetAddress(this.from));
				for (int i = 0; i < this.to.size(); i++)
				{
					InternetAddress[] address = {new InternetAddress((String) this.to.elementAt(i))};
					msg.addRecipients(Message.RecipientType.TO, address);
				}
				for (int i = 0; i < this.cc.size(); i++)
				{
					InternetAddress[] address = {new InternetAddress((String) this.cc.elementAt(i))};
					msg.addRecipients(Message.RecipientType.CC, address);
				}
				for (int i = 0; i < this.bcc.size(); i++)
				{
					InternetAddress[] address = {new InternetAddress((String) this.bcc.elementAt(i))};
					msg.addRecipients(Message.RecipientType.BCC, address);
				}
				msg.setSubject(this.subject);

				msg.setSentDate(new Date());
			} catch (MessagingException mex)
			{
				ErrorDescriptor ed = new ErrorDescriptor("EmailManager", this.methodName, "Error: Failed to set either from, to, cc or bcc", mex);
				throw new ApplicationException("Error: Failed to set either from, to, cc or bcc", mex, ed);
			}
			Multipart mp = new MimeMultipart();

			MimeBodyPart mbp = new MimeBodyPart();
			mbp.setText(this.body);

			mp.addBodyPart(mbp);
			try
			{
				if (this.attach)
				{
					processAttachments(mp);
				}
			} catch (MessagingException mex)
			{
				ErrorDescriptor ed = new ErrorDescriptor("EmailManager", this.methodName, "Error: Failed to Load Attachments", mex);
				throw new ApplicationException("Error: Failed to Load Attachments", mex, ed);
			}
			msg.setContent(mp);
			try
			{
				Transport transport = session.getTransport("smtp");
				transport.connect(this.host, 25, this.user, this.password);
				msg.saveChanges();
				transport.sendMessage(msg, msg.getAllRecipients());
			} catch (MessagingException mex)
			{
				ErrorDescriptor ed = new ErrorDescriptor("EmailManager", this.methodName, "Error: EmailManager failed to send email", mex);
				throw new ApplicationException("Error: EmailManager failed to send email", mex, ed);
			}
		} catch (Exception e)
		{
			throw new ApplicationException("Unknown Exception", e);
		}
	}

	public void sendMailHTML() throws ApplicationException
	{
		this.methodName = "sendMail";
		try
		{
			Properties props = new Properties();
			if (this.to == null)
			{
				ErrorDescriptor ed = new ErrorDescriptor("EmailManager", this.methodName, "Error: To Address Missing", null);
				throw new ApplicationException("Error: To Address Missing", ed);
			}
			if (this.from == null)
			{
				ErrorDescriptor ed = new ErrorDescriptor("EmailManager", this.methodName, "Error: From Address Missing", null);
				throw new ApplicationException("Error: From Address Missing", ed);
			}
			if (this.host == null)
			{
				ErrorDescriptor ed = new ErrorDescriptor("EmailManager", this.methodName, "Error: Invalid Host Name", null);
				throw new ApplicationException("Error: Invalid Host Name", ed);
			}
			props.put("mail.smtp.host", this.host);
			props.put("mail.smtp.auth", "true");
			if (this.debug)
			{
				props.put("mail.debug", "true");
			}
			Session session = Session.getDefaultInstance(props, null);
			session.setDebug(this.debug);

			MimeMessage msg = new MimeMessage(session);
			try
			{
				msg.setFrom(new InternetAddress(this.from));
				for (int i = 0; i < this.to.size(); i++)
				{
					InternetAddress[] address = {new InternetAddress((String) this.to.elementAt(i))};
					msg.addRecipients(Message.RecipientType.TO, address);
				}
				for (int i = 0; i < this.cc.size(); i++)
				{
					InternetAddress[] address = {new InternetAddress((String) this.cc.elementAt(i))};
					msg.addRecipients(Message.RecipientType.CC, address);
				}
				for (int i = 0; i < this.bcc.size(); i++)
				{
					InternetAddress[] address = {new InternetAddress((String) this.bcc.elementAt(i))};
					msg.addRecipients(Message.RecipientType.BCC, address);
				}
				msg.setSubject(this.subject);

				msg.setSentDate(new Date());
			} catch (MessagingException mex)
			{
				ErrorDescriptor ed = new ErrorDescriptor("EmailManager", this.methodName, "Error: Failed to set either from, to, cc or bcc", mex);
				throw new ApplicationException("Error: Failed to set either from, to, cc or bcc", mex, ed);
			}
			Multipart mp = new MimeMultipart();

			MimeBodyPart mbp = new MimeBodyPart();
			mbp.setText(this.body);
			mbp.setHeader("Content-Type", "text/html;");
			mbp.setHeader("Content-Transfer-Encoding", "base64");
			mbp.setHeader("charset", "\"gb2312\"");

			mp.addBodyPart(mbp);
			try
			{
				if (this.attach)
				{
					processAttachments(mp);
				}
			} catch (MessagingException mex)
			{
				ErrorDescriptor ed = new ErrorDescriptor("EmailManager", this.methodName, "Error: Failed to Load Attachments", mex);
				throw new ApplicationException("Error: Failed to Load Attachments", mex, ed);
			}
			msg.setContent(mp);
			try
			{
				Transport transport = session.getTransport("smtp");
				transport.connect(this.host, 25, this.user, this.password);
				msg.saveChanges();
				transport.sendMessage(msg, msg.getAllRecipients());
			} catch (MessagingException mex)
			{
				ErrorDescriptor ed = new ErrorDescriptor("EmailManager", this.methodName, "Error: EmailManager failed to send email", mex);
				throw new ApplicationException("Error: EmailManager failed to send email", mex, ed);
			}
		} catch (Exception e)
		{
			throw new ApplicationException("Unknown Exception", e);
		}
	}

	public void setAttach(boolean newAttach)
	{
		this.attach = newAttach;
	}

	public void setBody(String newBody)
	{
		this.body = newBody;
	}

	public void setDebug(boolean newDebug)
	{
		this.debug = newDebug;
	}

	public void setFrom(String newFrom)
	{
		this.from = newFrom;
	}

	public void setHost(String newHost)
	{
		this.host = newHost;
	}

	public void setSubject(String newSubject)
	{
		this.subject = newSubject;
	}
}
