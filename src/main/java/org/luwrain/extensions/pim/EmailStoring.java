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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.SQLException;

import javax.mail.Message;
import javax.mail.MessagingException;

import org.luwrain.extensions.pim.EmailStoringSql.Condition;

public interface EmailStoring extends Cloneable
{
	public void setOnlineMessageObject(Message message); // TODO: reorganise project structure to hide javamail usage inside
	public void makeJavamailMessage(EmailMessage msg) throws Exception;
	public void readJavamailMessageBaseFields(EmailMessage msg) throws Exception;
	public void readJavamailMessageOnline(EmailMessage msg) throws Exception;
	public void readJavamailMessageContent(EmailMessage msg) throws Exception;
	public EmailMessage loadEmailFromFile(FileInputStream fs) throws Exception;
	public void saveEmailToFile(EmailMessage msg,FileOutputStream fs) throws Exception;
    void saveEmailMessage(EmailMessage message) throws Exception;
	StoredEmailMessage[] loadEmailMessages(boolean withRaw, Condition cond) throws SQLException;

	//Object clone();
    
}
