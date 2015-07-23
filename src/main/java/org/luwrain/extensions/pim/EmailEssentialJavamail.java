package org.luwrain.extensions.pim;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;

import org.luwrain.extensions.pim.EmailStoringSql.Condition;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.Flags.Flag;
import javax.mail.Message.RecipientType;
import javax.mail.internet.*;

import com.sun.mail.imap.IMAPMessage;
import com.sun.mail.pop3.POP3Message;

import java.sql.*;

public class EmailEssentialJavamail implements EmailEssential
{
	public Message jmailmsg;
	
	// make MimeMessage from class fields
	@Override public void PrepareInternalStore(EmailMessage msg) throws Exception
	{
		jmailmsg=new MimeMessage(session);
		jmailmsg.setSubject(msg.subject);
		if(msg.from!=null&&msg.from.length>0)
		{
			int i=0;
			InternetAddress[] addr_from=new InternetAddress[msg.from.length];  
			for(String addr:msg.from) addr_from[i++]=new InternetAddress(addr);
			jmailmsg.setFrom(new InternetAddress(msg.from[0])); // FIXME: 
		}
		if(msg.to!=null&&msg.to.length>0)
		{
			int i=0;
			InternetAddress[] addr_to=new InternetAddress[msg.to.length];  
			for(String addr:msg.to) addr_to[i++]=new InternetAddress(addr);
			jmailmsg.setRecipients(RecipientType.TO, addr_to);
		}
		if(msg.cc!=null&&msg.cc.length>0)
		{
			int i=0;
			InternetAddress[] addr_cc=new InternetAddress[msg.cc.length];  
			for(String addr:msg.cc) addr_cc[i++]=new InternetAddress(addr);
			jmailmsg.setRecipients(RecipientType.CC, addr_cc);
		}
		if(msg.bcc!=null&&msg.bcc.length>0)
		{
			int i=0;
			InternetAddress[] addr_bcc=new InternetAddress[msg.bcc.length];  
			for(String addr:msg.bcc) addr_bcc[i++]=new InternetAddress(addr);
			jmailmsg.setRecipients(RecipientType.BCC, addr_bcc);
		}
		if(msg.sentDate!=null) jmailmsg.setSentDate(msg.sentDate);
		// attachments and message body
		if(!msg.attachments.isEmpty())
		{
			Multipart mp = new MimeMultipart();
			MimeBodyPart part = new MimeBodyPart();
			part.setText(msg.baseContent);
			// TODO: need to repair - in multipart message mimeContentType of baseContent was ignored
			mp.addBodyPart(part);
			for(String fn:msg.attachments)
			{
				part = new MimeBodyPart();
				Path pfn=Paths.get(fn);
				part.setFileName(pfn.getFileName().toString());
				FileDataSource fds = new FileDataSource(fn);
				part.setDataHandler(new DataHandler(fds));
				mp.addBodyPart(part);
			}
			jmailmsg.setContent(mp);
		} else
		{
			if(msg.mimeContentType==null)
			{ // simple text email body
				jmailmsg.setText(msg.baseContent);
			} else
			{ // for example utf8 html - mimeContentType="text/html; charset=utf-8"
				jmailmsg.setContent(msg.baseContent,msg.mimeContentType);
			}
		}
	}
	
	// used to fill standart simple mime mail message fields (message can be Mime..., POP3... or IMAP... Message class)
	public void readJavamailMessageBaseFields(EmailMessage msg) throws Exception
	{
		msg.subject=jmailmsg.getSubject();
		if(jmailmsg.getFrom()!=null)
		{
			Vector<String> from=new Vector<String>();
			for(Address addr:jmailmsg.getFrom()) from.add(addr.toString());
			msg.from=from.toArray(new String[from.size()]);
		} else msg.from=null;
		if(jmailmsg.getRecipients(RecipientType.TO)!=null)
		{
			Vector<String> to=new Vector<String>();
			for(Address addr:jmailmsg.getRecipients(RecipientType.TO)) to.add(addr.toString());
			msg.to=to.toArray(new String[to.size()]);
		} else msg.to=null;
		if(jmailmsg.getRecipients(RecipientType.CC)!=null)
		{
			Vector<String> to=new Vector<String>();
			for(Address addr:jmailmsg.getRecipients(RecipientType.CC)) to.add(addr.toString());
			msg.cc=to.toArray(new String[to.size()]);
		} else msg.cc=null;
		if(jmailmsg.getRecipients(RecipientType.BCC)!=null)
		{
			Vector<String> to=new Vector<String>();
			for(Address addr:jmailmsg.getRecipients(RecipientType.BCC)) to.add(addr.toString());
			msg.bcc=to.toArray(new String[to.size()]);
		} else msg.bcc=null;
		msg.isReaded=!jmailmsg.getFlags().contains(Flag.SEEN);
		msg.sentDate=jmailmsg.getSentDate();
		msg.receivedDate=jmailmsg.getReceivedDate();
		// message body
		if(jmailmsg.getContent().getClass()==MimeMultipart.class)
		{
			Multipart content =(Multipart)jmailmsg.getContent();
			MimeBodyPart file = (MimeBodyPart) content.getBodyPart(0); // first file of multipart is a message body 
			msg.baseContent=file.getContent().toString();
			// get attachments
			for(int i=1;i<content.getCount();i++)
			{
				file = (MimeBodyPart) content.getBodyPart(i);
				file.getContentID();
				file.getFileName();
			}
		} else
		{
			msg.baseContent=jmailmsg.getContent().toString();
		}
		msg.mimeContentType=jmailmsg.getContentType();
	}
	
	// used to load addition fields from Message POP3 or IMAP online
	public void readJavamailMessageOnline(EmailMessage msg) throws Exception
	{
		if(jmailmsg.getClass()==IMAPMessage.class)
		{
			IMAPMessage imessage=((IMAPMessage)jmailmsg);
			msg.messageId=imessage.getMessageID();
		} else if(jmailmsg.getClass()==POP3Message.class)
		{
			POP3Message pmessage=((POP3Message)jmailmsg);
			msg.messageId=pmessage.getMessageID();
		}
		throw new Exception("Unknown email Message class "+jmailmsg.getClass().getName()); // TODO: check that it will never happend
	}
	public void readJavamailMessageContent(EmailMessage msg) throws Exception
	{
		File temp = File.createTempFile("email-"+String.valueOf(jmailmsg.hashCode()), ".tmp");
		FileOutputStream fs=new FileOutputStream(temp);
		SaveEmailToFile(msg,fs);
		fs.flush();
		msg.rawEmail=new byte[(int)fs.getChannel().size()]; // TODO: long to int cas, here is limit for 2Gb raw email size, needed to be checked?
		fs.close();
	}
	
	Session session=Session.getDefaultInstance(new Properties(), null); // by default was used empty session for working .eml files
	// used to fill fields via .eml file stream
	@Override public EmailMessage LoadEmailFromFile(FileInputStream fs) throws Exception
	{
		EmailMessage msg=new EmailMessage();
		jmailmsg=new MimeMessage(session,fs);
		fs.close();
		readJavamailMessageBaseFields(msg);
		return msg;
	}

	// used to save fields to .eml field stream
	@Override public void SaveEmailToFile(EmailMessage msg,FileOutputStream fs) throws Exception
	{
		PrepareInternalStore(msg);
		jmailmsg.writeTo(fs);
		fs.flush();
		fs.close();
	}
}
