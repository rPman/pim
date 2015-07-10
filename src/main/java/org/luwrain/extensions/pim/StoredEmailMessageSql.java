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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;



class StoredEmailMessageSql extends EmailMessage implements StoredEmailMessage, Comparable
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
    	PreparedStatement st = con.prepareStatement("UPDATE email_message SET message_id = ? WHERE id = ?;");
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
    @Override public void setFrom(String from) throws SQLException
	{
		PreparedStatement st = con.prepareStatement("UPDATE email_message SET from = ? WHERE id = ?;");
		st.setString(1, from);
		st.setLong(2, id);
		st.executeUpdate();
		this.from = from;
    }
    @Override public String[] getTo() {return to;}
    @Override public void setTo(String[] to) throws SQLException
	{
    	PreparedStatement st = con.prepareStatement("UPDATE email_message SET to = ? WHERE id = ?;");
    	st.setString(1, EmailStoringSql.SimpleArraySerialize(to));
    	st.setLong(2, id);
    	st.executeUpdate();
    	this.to = to;
	}
	@Override public String[] getCc() {return cc;}
	@Override public void setCc(String[] cc) throws SQLException
    {
    	PreparedStatement st = con.prepareStatement("UPDATE email_message SET cc = ? WHERE id = ?;");
    	st.setString(1, EmailStoringSql.SimpleArraySerialize(cc));
    	st.setLong(2, id);
    	st.executeUpdate();
    	this.cc = cc;
	}
    @Override public String[] getBcc() {return bcc;}
    @Override public void setBcc(String[] bcc) throws SQLException
    {
    	PreparedStatement st = con.prepareStatement("UPDATE email_message SET bcc = ? WHERE id = ?;");
    	st.setString(1, EmailStoringSql.SimpleArraySerialize(bcc));
    	st.setLong(2, id);
    	st.executeUpdate();
    	this.bcc = bcc;
	}
    @Override public Boolean getIsReaded() {return isReaded;}
    @Override public void setIsReaded(Boolean isReaded) throws SQLException
	{
		PreparedStatement st = con.prepareStatement("UPDATE email_message SET is_readed = ? WHERE id = ?;");
		st.setBoolean(1, isReaded);
		st.setLong(2, id);
		st.executeUpdate();
		this.isReaded = isReaded;
    }
    @Override public Boolean getIsMarked() {return isMarked;}
	@Override public void setIsMarked(Boolean isMarked) throws SQLException
	{
		PreparedStatement st = con.prepareStatement("UPDATE email_message SET is_marked = ? WHERE id = ?;");
		st.setBoolean(1, isMarked);
		st.setLong(2, id);
		st.executeUpdate();
		this.isMarked = isMarked;
    }
    @Override public Date getSentDate() {return sentDate;}
    @Override public void setSentDate(Date sentDate) throws SQLException
	{
		PreparedStatement st = con.prepareStatement("UPDATE email_message SET sent_date = ? WHERE id = ?;");
		st.setDate(1, new java.sql.Date(sentDate.getTime()));
		st.setLong(2, id);
		st.executeUpdate();
		this.sentDate = sentDate;
    }
    @Override public Date getReceivedDate() {return receivedDate;}
    @Override public void setReceivedDate(Date receivedDate) throws SQLException
	{
		PreparedStatement st = con.prepareStatement("UPDATE email_message SET received_date = ? WHERE id = ?;");
		st.setDate(1, new java.sql.Date(receivedDate.getTime()));
		st.setLong(2, id);
		st.executeUpdate();
		this.receivedDate = receivedDate;
    }
    @Override public String getBaseContent() {return baseContent;}
    @Override public void setBaseContent(String baseContent) throws SQLException
    {
    	PreparedStatement st = con.prepareStatement("UPDATE email_message SET body = ? WHERE id = ?;");
    	st.setString(1, baseContent);
    	st.setLong(2, id);
    	st.executeUpdate();
    	this.baseContent = baseContent;
	}
    @Override public String getMimeContentType() {return mimeContentType;}
    @Override public void setMimeContentType(String mimeContentType) throws SQLException
    {
    	PreparedStatement st = con.prepareStatement("UPDATE email_message SET body = ? WHERE id = ?;");
    	st.setString(1, mimeContentType);
    	st.setLong(2, id);
    	st.executeUpdate();
    	this.mimeContentType = mimeContentType;
	}
    @Override public byte[] getRawEmail() throws SQLException
    {
    	if(rawEmail==null)
    	{
        	PreparedStatement st = con.prepareStatement("SELECT raw FROM email_message WHERE id = ?;");
        	st.setLong(1, this.id);
        	ResultSet rs = st.executeQuery();
        	if(rs.next()) this.rawEmail=rs.getBytes(1);
    	}
    	return rawEmail;
   	}
    @Override public void setRawEmail(byte[] rawEmail) throws SQLException
    {
    	PreparedStatement st = con.prepareStatement("UPDATE email_message SET raw = ? WHERE id = ?;");
    	st.setBytes(1, rawEmail);
    	st.setLong(2, id);
    	st.executeUpdate();
    	this.rawEmail = rawEmail;
	}

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
}
