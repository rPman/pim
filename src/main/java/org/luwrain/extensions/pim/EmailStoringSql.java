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

import java.sql.*;
import java.util.Vector;

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
    { // FIXME: check list contains ';' char or change method to save simple lists of file names and email address
    	return String.join(";", list);
    }
    
    public static String[] SimpleArrayDeSerialize(String str)
    {
    	return str.split(";");
    }

    @Override public void saveEmailMessage(EmailMessage message) throws SQLException
    {
    	PreparedStatement st = con.prepareStatement("INSERT INTO email_message (id,message_id,subject,from,to,cc,bcc,is_readed,is_marked,sent_date,received_date,body,mime_body,raw) VALUES (null,?,?,?,?,?,?,?,?,?,?,?,?,?);",Statement.RETURN_GENERATED_KEYS);
		st.setString(1, message.messageId);
		st.setString(2, message.subject);
		st.setString(3, EmailStoringSql.SimpleArraySerialize(message.from));
		st.setString(4, EmailStoringSql.SimpleArraySerialize(message.to));
		st.setString(5, EmailStoringSql.SimpleArraySerialize(message.cc));
		st.setString(6, EmailStoringSql.SimpleArraySerialize(message.bcc));
		st.setBoolean(7, message.isReaded);
		st.setBoolean(8, message.isMarked);
		st.setDate(9, new java.sql.Date(message.sentDate.getTime()));
		st.setDate(10, new java.sql.Date(message.receivedDate.getTime()));
		st.setString(11, message.baseContent);
		st.setString(12, message.mimeContentType);
		st.setBytes(13, message.rawEmail);
		int updatedCount=st.executeUpdate();
		/*
		if(updatedCount==1)
		{ // get generated id
			ResultSet generatedKeys = st.getGeneratedKeys();
			if (generatedKeys.next()) message.id = generatedKeys.getLong(1);
		}
		*/
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
    		message.from=EmailStoringSql.SimpleArrayDeSerialize(rs.getString(4));
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
}
