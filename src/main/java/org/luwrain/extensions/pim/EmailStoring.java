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

import javax.mail.MessagingException;

public interface EmailStoring extends Cloneable
{
	public void makeJavamailMessage() throws Exception;
	public void readJavamailMessageBaseFields() throws Exception;
	public void readJavamailMessageOnline() throws Exception;
	public void loadEmailFromFile(FileInputStream fs) throws Exception;
	public void saveEmailToFile(FileOutputStream fs) throws Exception;

    //Object clone();
    
}
