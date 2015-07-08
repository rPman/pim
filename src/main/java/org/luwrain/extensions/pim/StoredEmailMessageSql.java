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

import java.util.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Flags.Flag;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeMultipart;

import com.sun.mail.imap.IMAPMessage;
import com.sun.mail.pop3.POP3Message;

class StoredEmailMessageSql extends EmailMessage implements StoredEmailMessage, EmailStoring, Comparable
{
    Connection con;
    public long id = 0;
    public int state = 0;

    @Override public int getState() {return state;}
    public StoredEmailMessageSql(Connection con)
    {
    	this.con = con;
    	if (con == null) throw new NullPointerException("con may not be null");
    }


    @Override public void setState(int state) throws SQLException
    {
    	PreparedStatement st = con.prepareStatement("UPDATE email_message SET state = ? WHERE id = ?;");
    	st.setInt(1, state);
    	st.setLong(2, id);
    	st.executeUpdate();
    	this.state = state;
    }

    @Override public String getMessageId() {return messageId;}
    @Override public void setMessageId(String messageId) throws SQLException
    {
    	PreparedStatement st = con.prepareStatement("UPDATE email_message SET messageid = ? WHERE id = ?;");
    	st.setString(1, messageId);
    	st.setLong(2, id);
    	st.executeUpdate();
    	this.messageId = messageId;
    }
    @Override public String getSubject() {return subject;}
    @Override public void setSubject(String subject) throws SQLException
    {
    	PreparedStatement st = con.prepareStatement("UPDATE email_message SET subject = ? WHERE id = ?;");
    	st.setString(1, subject);
    	st.setLong(2, id);
    	st.executeUpdate();
    	this.subject = subject;
    }
    @Override public String getFrom() {return from;}
    @Override public void setFrom(String from) {this.from = from;throw new UnsupportedOperationException("Not implemented, yet");}
    @Override public String[] getTo() {return to;}
    @Override public void setTo(String[] to) {this.to = to;throw new UnsupportedOperationException("Not implemented, yet");}
    @Override public String[] getCc() {return cc;}
    @Override public void setCc(String[] cc) {this.cc = cc;throw new UnsupportedOperationException("Not implemented, yet");}
    @Override public String[] getBcc() {return bcc;}
    @Override public void setBcc(String[] bcc) {this.bcc = bcc;throw new UnsupportedOperationException("Not implemented, yet");}
    @Override public Boolean getIsReaded() {return isReaded;}
    @Override public void setIsReaded(Boolean isReaded) {this.isReaded = isReaded;throw new UnsupportedOperationException("Not implemented, yet");}
    @Override public Boolean getIsMarked() {return isMarked;}
    @Override public void setIsMarked(Boolean isMarked) {this.isMarked = isMarked;throw new UnsupportedOperationException("Not implemented, yet");}
    @Override public Date getSentDate() {return sentDate;}
    @Override public void setSentDate(Date sentDate) {this.sentDate = sentDate;throw new UnsupportedOperationException("Not implemented, yet");}
    @Override public Date getReceivedDate() {return receivedDate;}
    @Override public void setReceivedDate(Date receivedDate) {this.receivedDate = receivedDate;throw new UnsupportedOperationException("Not implemented, yet");}
    @Override public String getBaseContent() {return baseContent;}
    @Override public void setBaseContent(String baseContent) {this.baseContent = baseContent;throw new UnsupportedOperationException("Not implemented, yet");}
    @Override public String getMimeContentType() {return mimeContentType;}
    @Override public void setMimeContentType(String mimeContentType) {this.mimeContentType = mimeContentType;throw new UnsupportedOperationException("Not implemented, yet");}

	@Override public int compareTo(Object o)
    {
    	if (o == null || !(o instanceof StoredEmailMessageSql)) return 0;
    	StoredEmailMessageSql article = (StoredEmailMessageSql)o;
    	if (state != article.state)
    	{
    		if (state > article.state) return -1;
    		if (state < article.state) return 1;
    		return 0;
    	}
    	if (receivedDate == null || article.receivedDate == null) return 0;
    	// if receivedDate are equal, compare messages via sentDate
    	if(receivedDate==article.receivedDate&&sentDate!=null&&article.sentDate!=null) return -1 * sentDate.compareTo(article.sentDate);
    	return -1 * receivedDate.compareTo(article.receivedDate);
    }
	/////////////////////////////////////////////////////////////////////////////////////////////////
	private Message message;
	// make MimeMessage from class fields
	@Override public void makeJavamailMessage() throws Exception
	{
		message=new MimeMessage(session);
		message.setSubject(this.subject);
		if(this.from!=null)
		{
			message.setFrom(new InternetAddress(this.from));
		}
		if(this.to!=null&&this.to.length>0)
		{
			int i=0;
			InternetAddress[] addr_to=new InternetAddress[this.to.length];  
			for(String addr:this.to) addr_to[i++]=new InternetAddress(addr);
			message.setRecipients(RecipientType.TO, addr_to);
		}
		if(this.cc!=null&&this.cc.length>0)
		{
			int i=0;
			InternetAddress[] addr_cc=new InternetAddress[this.cc.length];  
			for(String addr:this.cc) addr_cc[i++]=new InternetAddress(addr);
			message.setRecipients(RecipientType.CC, addr_cc);
		}
		if(this.bcc!=null&&this.bcc.length>0)
		{
			int i=0;
			InternetAddress[] addr_bcc=new InternetAddress[this.bcc.length];  
			for(String addr:this.bcc) addr_bcc[i++]=new InternetAddress(addr);
			message.setRecipients(RecipientType.BCC, addr_bcc);
		}
		if(this.sentDate!=null) message.setSentDate(this.sentDate);
		// attachments and message body
		if(!this.attachments.isEmpty())
		{
			Multipart mp = new MimeMultipart();
			MimeBodyPart part = new MimeBodyPart();
			part.setText(this.baseContent);
			// TODO: need to repair - in multipart message mimeContentType of baseContent was ignored
			mp.addBodyPart(part);
			for(Map.Entry<String,String> fn:this.attachments.entrySet())
			{
				part = new MimeBodyPart();
				part.setFileName(fn.getKey());
				FileDataSource fds = new FileDataSource(fn.getValue());
				part.setDataHandler(new DataHandler(fds));
				mp.addBodyPart(part);
			}
			message.setContent(mp);
		} else
		{
			if(this.mimeContentType==null)
			{ // simple text email body
				message.setText(this.baseContent);
			} else
			{ // for example utf8 html - mimeContentType="text/html; charset=utf-8"
				message.setContent(this.baseContent,this.mimeContentType);
			}
		}
		//
		//
		//message.setContent(part);
	}
	
	// used to fill standart simple mime mail message fields (message can be Mime..., POP3... or IMAP... Message class)
	@Override public void readJavamailMessageBaseFields() throws Exception
	{
		this.subject=message.getSubject();
		if(message.getFrom()!=null)
		{
			this.from=message.getFrom().toString();
		} else this.from=null;
		if(message.getRecipients(RecipientType.TO)!=null)
		{
			Vector<String> to=new Vector<String>();
			for(Address addr:message.getRecipients(RecipientType.TO)) to.add(addr.toString());
			this.to=to.toArray(new String[to.size()]);
		} else this.to=null;
		if(message.getRecipients(RecipientType.CC)!=null)
		{
			Vector<String> to=new Vector<String>();
			for(Address addr:message.getRecipients(RecipientType.CC)) to.add(addr.toString());
			this.cc=to.toArray(new String[to.size()]);
		} else this.cc=null;
		if(message.getRecipients(RecipientType.BCC)!=null)
		{
			Vector<String> to=new Vector<String>();
			for(Address addr:message.getRecipients(RecipientType.BCC)) to.add(addr.toString());
			this.bcc=to.toArray(new String[to.size()]);
		} else this.bcc=null;
		this.isReaded=!message.getFlags().contains(Flag.SEEN);
		this.sentDate=message.getSentDate();
		this.receivedDate=message.getReceivedDate();
		// message body
		if(message.getContent().getClass()==MimeMultipart.class)
		{
			Multipart content =(Multipart)message.getContent();
			MimeBodyPart file = (MimeBodyPart) content.getBodyPart(0); // first file of multipart is a message body 
			this.baseContent=file.getContent().toString();
		}
		{
			this.baseContent=message.getContent().toString();
		}
		this.mimeContentType=message.getContentType();
	}
	
	// used to load addition fields from Message POP3 or IMAP online
	@Override public void readJavamailMessageOnline() throws Exception
	{
		if(message.getClass()==IMAPMessage.class)
		{
			IMAPMessage imessage=((IMAPMessage)message);
			this.messageId=imessage.getMessageID();
		} else if(message.getClass()==POP3Message.class)
		{
			POP3Message pmessage=((POP3Message)message);
			this.messageId=pmessage.getMessageID();
		}
		throw new Exception("Unknown email Message class "+message.getClass().getName()); // TODO: check that it will never happend
	}
	
	Session session=Session.getDefaultInstance(new Properties(), null); // by default was used empty session for working .eml files
	// used to fill fields via .eml file stream
	@Override public void loadEmailFromFile(FileInputStream fs) throws Exception
	{
		message=new MimeMessage(session,fs);
		fs.close();
		readJavamailMessageBaseFields();
	}

	// used to save fields to .eml field stream
	@Override public void saveEmailToFile(FileOutputStream fs) throws Exception
	{
		makeJavamailMessage();
		message.writeTo(fs);
		fs.flush();
		fs.close();
	}

}
