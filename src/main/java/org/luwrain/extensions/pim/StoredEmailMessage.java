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
import java.sql.SQLException;

import javax.mail.Message;

public interface StoredEmailMessage
{
    int getState();
    void setState(int state) throws SQLException;

    public String getMessageId() throws Exception; 
	public void setMessageId(String messageId) throws Exception;
	public String getSubject() throws Exception;
	public void setSubject(String subject) throws Exception;
	public String getFrom() throws Exception;
	public void setFrom(String from) throws Exception;
	public String[] getTo() throws Exception;
	public void setTo(String[] to) throws Exception;
	public String[] getCc() throws Exception;
	public void setCc(String[] cc) throws Exception;
	public String[] getBcc() throws Exception;
	public void setBcc(String[] bcc) throws Exception;
	public Boolean getIsReaded() throws Exception;
	public void setIsReaded(Boolean isReaded) throws Exception;
	public Boolean getIsMarked() throws Exception;
	public void setIsMarked(Boolean isMarked) throws Exception;
	public Date getSentDate() throws Exception;
	public void setSentDate(Date sentDate) throws Exception;
	public Date getReceivedDate() throws Exception;
	public void setReceivedDate(Date receivedDate) throws Exception;
	public String getBaseContent() throws Exception;
	public void setBaseContent(String baseContent) throws Exception;
	public String getMimeContentType() throws Exception;
	public void setMimeContentType(String mimeContentType) throws Exception;
	
	public byte[] getRawEmail() throws SQLException;
	public void setRawEmail(byte[] rawEmail) throws SQLException;
}
