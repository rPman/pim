/*
   Copyright 2012-2015 Michael Pozhidaev <msp@altlinux.org>

   This file is part of the Luwrain.

   Luwrain is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   Luwrain is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.extensions.pim;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.Flags.Flag;
import javax.mail.Message.RecipientType;
import javax.mail.internet.*;

import com.sun.mail.imap.IMAPMessage;
import com.sun.mail.pop3.POP3Message;

import org.luwrain.core.Registry;

class EmailStoringSql extends EmailStoringRegistry
{
    private Connection con;
    private String url = "";
    private String login = "";
    private String passwd = "";
    
    public enum Condition {ALL,UNREAD};

    public EmailStoringSql(Registry registry,Connection con,String url,String login,String passwd)
    {
		super(registry);
		this.con = con;
		this.url = url;
		this.login = login;
		this.passwd = passwd;
		if (con == null)
			throw new NullPointerException("con may not be null");
		if (url == null)
			throw new NullPointerException("url may not be null");
		if (login == null)
			throw new NullPointerException("login may not be null");
		if (passwd == null)
			throw new NullPointerException("passwd may not be null");
    }

    public static String SimpleArraySerialize(String[] list)
    {
    	// FIXME: check list contains ';' char or change method to save simple lists of file names and email address
    	return String.join(";", list);
    }
    
    public static String[] SimpleArrayDeSerialize(String str)
    {
    	return str.split(";");
    }
    
    @Override public void saveEmailMessage(EmailMessage message) throws SQLException
    {
    	PreparedStatement st = con.prepareStatement("INSERT INTO email_message (id,message_id,subject,from,to,cc,bcc,is_readed,is_marked,sent_date,received_date,body,mime_body,raw) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?);");
		st.setLong(1, 0); // FIXME: get id for new message in sql database
		st.setString(2, message.messageId);
		st.setString(3, message.subject);
		st.setString(4, message.from);
		st.setString(5, EmailStoringSql.SimpleArraySerialize(message.to));
		st.setString(6, EmailStoringSql.SimpleArraySerialize(message.cc));
		st.setString(7, EmailStoringSql.SimpleArraySerialize(message.bcc));
		st.setBoolean(8, message.isReaded);
		st.setBoolean(9, message.isMarked);
		st.setDate(10, new java.sql.Date(message.sentDate.getTime()));
		st.setDate(11, new java.sql.Date(message.receivedDate.getTime()));
		st.setString(12, message.baseContent);
		st.setString(13, message.mimeContentType);
		st.setBytes(14, message.rawEmail);
		st.executeUpdate();
    }
    
    @Override public StoredEmailMessage[] loadEmailMessages(boolean withRaw,Condition cond) throws SQLException
    {
    	String whereCondStr="";
    	if(cond==Condition.UNREAD) whereCondStr=" WHERE is_readed = 1";
    	PreparedStatement st = con.prepareStatement("SELECT id,message_id,subject,from,to,cc,bcc,is_readed,is_marked,sent_date,received_date,body,mime_body"+(withRaw?",raw":"")+whereCondStr+" FROM email_message;");
    	//st.setLong(1, g.id);
    	ResultSet rs = st.executeQuery();
    	Vector<StoredEmailMessageSql> messages = new Vector<StoredEmailMessageSql>();
    	while (rs.next())
    	{
    		StoredEmailMessageSql message=new StoredEmailMessageSql(con);
    		message.id=rs.getLong(1);
    		message.messageId=rs.getString(2);
    		message.subject=rs.getString(3);
    		message.from=rs.getString(4);
    		message.to=EmailStoringSql.SimpleArrayDeSerialize(rs.getString(5));
    		message.cc=EmailStoringSql.SimpleArrayDeSerialize(rs.getString(6));
    		message.bcc=EmailStoringSql.SimpleArrayDeSerialize(rs.getString(7));
    		message.isReaded=rs.getBoolean(8);
    		message.isMarked=rs.getBoolean(9);
    		message.sentDate=new java.util.Date(rs.getDate(10).getTime());
    		message.receivedDate=new java.util.Date(rs.getDate(11).getTime());
    		message.baseContent=rs.getString(12);
    		message.mimeContentType=rs.getString(13);
    		if(withRaw) message.rawEmail=rs.getBytes(14);
    		
    	}
    	return messages.toArray(new StoredEmailMessage[messages.size()]);
    }

    @Override public Object clone()
    {
		Connection newCon = null;
		try {
		    newCon = DriverManager.getConnection (url, login, passwd);
		}
		catch (SQLException e)
		{
		    e.printStackTrace();
		    return null;
		}
		return new EmailStoringSql(registry, newCon, url, login, passwd);
    }

	public Message message;
	
	public void setOnlineMessageObject(Message message){this.message=message;}
	
	// make MimeMessage from class fields
	@Override public void makeJavamailMessage(EmailMessage msg) throws Exception
	{
		message=new MimeMessage(session);
		message.setSubject(msg.subject);
		if(msg.from!=null)
		{
			message.setFrom(new InternetAddress(msg.from));
		}
		if(msg.to!=null&&msg.to.length>0)
		{
			int i=0;
			InternetAddress[] addr_to=new InternetAddress[msg.to.length];  
			for(String addr:msg.to) addr_to[i++]=new InternetAddress(addr);
			message.setRecipients(RecipientType.TO, addr_to);
		}
		if(msg.cc!=null&&msg.cc.length>0)
		{
			int i=0;
			InternetAddress[] addr_cc=new InternetAddress[msg.cc.length];  
			for(String addr:msg.cc) addr_cc[i++]=new InternetAddress(addr);
			message.setRecipients(RecipientType.CC, addr_cc);
		}
		if(msg.bcc!=null&&msg.bcc.length>0)
		{
			int i=0;
			InternetAddress[] addr_bcc=new InternetAddress[msg.bcc.length];  
			for(String addr:msg.bcc) addr_bcc[i++]=new InternetAddress(addr);
			message.setRecipients(RecipientType.BCC, addr_bcc);
		}
		if(msg.sentDate!=null) message.setSentDate(msg.sentDate);
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
			message.setContent(mp);
		} else
		{
			if(msg.mimeContentType==null)
			{ // simple text email body
				message.setText(msg.baseContent);
			} else
			{ // for example utf8 html - mimeContentType="text/html; charset=utf-8"
				message.setContent(msg.baseContent,msg.mimeContentType);
			}
		}
		//
		//
		//message.setContent(part);
	}
	
	// used to fill standart simple mime mail message fields (message can be Mime..., POP3... or IMAP... Message class)
	@Override public void readJavamailMessageBaseFields(EmailMessage msg) throws Exception
	{
		msg.subject=message.getSubject();
		if(message.getFrom()!=null)
		{
			msg.from=message.getFrom().toString();
		} else msg.from=null;
		if(message.getRecipients(RecipientType.TO)!=null)
		{
			Vector<String> to=new Vector<String>();
			for(Address addr:message.getRecipients(RecipientType.TO)) to.add(addr.toString());
			msg.to=to.toArray(new String[to.size()]);
		} else msg.to=null;
		if(message.getRecipients(RecipientType.CC)!=null)
		{
			Vector<String> to=new Vector<String>();
			for(Address addr:message.getRecipients(RecipientType.CC)) to.add(addr.toString());
			msg.cc=to.toArray(new String[to.size()]);
		} else msg.cc=null;
		if(message.getRecipients(RecipientType.BCC)!=null)
		{
			Vector<String> to=new Vector<String>();
			for(Address addr:message.getRecipients(RecipientType.BCC)) to.add(addr.toString());
			msg.bcc=to.toArray(new String[to.size()]);
		} else msg.bcc=null;
		msg.isReaded=!message.getFlags().contains(Flag.SEEN);
		msg.sentDate=message.getSentDate();
		msg.receivedDate=message.getReceivedDate();
		// message body
		if(message.getContent().getClass()==MimeMultipart.class)
		{
			Multipart content =(Multipart)message.getContent();
			MimeBodyPart file = (MimeBodyPart) content.getBodyPart(0); // first file of multipart is a message body 
			msg.baseContent=file.getContent().toString();
			// get attachments
			for(int i=1;i<content.getCount();i++)
			{
				file = (MimeBodyPart) content.getBodyPart(i);
				file.getContentID();
				file.getFileName();
			}
		}
		{
			msg.baseContent=message.getContent().toString();
		}
		msg.mimeContentType=message.getContentType();
	}
	
	// used to load addition fields from Message POP3 or IMAP online
	@Override public void readJavamailMessageOnline(EmailMessage msg) throws Exception
	{
		if(message.getClass()==IMAPMessage.class)
		{
			IMAPMessage imessage=((IMAPMessage)message);
			msg.messageId=imessage.getMessageID();
		} else if(message.getClass()==POP3Message.class)
		{
			POP3Message pmessage=((POP3Message)message);
			msg.messageId=pmessage.getMessageID();
		}
		throw new Exception("Unknown email Message class "+message.getClass().getName()); // TODO: check that it will never happend
	}
	public void readJavamailMessageContent(EmailMessage msg) throws Exception
	{
		File temp = File.createTempFile("email-"+String.valueOf(message.hashCode()), ".tmp");
		FileOutputStream fs=new FileOutputStream(temp);
		saveEmailToFile(msg,fs);
		fs.close();
	
	}
	
	Session session=Session.getDefaultInstance(new Properties(), null); // by default was used empty session for working .eml files
	// used to fill fields via .eml file stream
	@Override public EmailMessage loadEmailFromFile(FileInputStream fs) throws Exception
	{
		EmailMessage msg=new EmailMessage();
		message=new MimeMessage(session,fs);
		fs.close();
		readJavamailMessageBaseFields(msg);
		return msg;
	}

	// used to save fields to .eml field stream
	@Override public void saveEmailToFile(EmailMessage msg,FileOutputStream fs) throws Exception
	{
		makeJavamailMessage(msg);
		message.writeTo(fs);
		fs.flush();
		fs.close();
	}
}
