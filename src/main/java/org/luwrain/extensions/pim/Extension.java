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

import org.luwrain.core.Registry;
import org.luwrain.core.Shortcut;
import org.luwrain.core.Command;
import org.luwrain.util.RegistryAutoCheck;

public class Extension implements org.luwrain.core.Extension
{
    private NewsStoring newsStoring;
    private Registry registry;
    private Connection newsJdbcCon;

    @Override public String init(String[] cmdLIne, Registry registry)
    {
	if (registry == null)
	    throw new NullPointerException("registry may not be null");
	this.registry = registry;
	return null;
    }

    @Override public Command[] getCommands()
    {
	return new Command[0];
    }

    @Override public Shortcut[] getShortcuts()
    {
	return new Shortcut[0];
    }

    private String initDefaultNewsCon()
    {
	RegistryKeys keys = new RegistryKeys();
	RegistryAutoCheck check = new RegistryAutoCheck(registry);
	final String type = check.stringNotEmpty(keys.newsType(), "");
	if (type.isEmpty())
	    return "no proper registry value " + keys.newsType();
	if (!type.equals("jdbc"))
	    return "unknown news storing \'" + type + "\'";
	final String driver = check.stringNotEmpty(keys.newsDriver(), "");
	if (driver.isEmpty())
	    return "no proper value " + keys.newsDriver();
	final String url = check.stringNotEmpty(keys.newsUrl(), "");
	if (url.isEmpty())
	    return "no proper value " + keys.newsUrl();
	final String login = check.stringAny(keys.newsLogin(), "");
	final String passwd = check.stringAny(keys.newsPasswd(), "");
	try {
	    Class.forName (driver).newInstance ();
	    newsJdbcCon = DriverManager.getConnection (url, login, passwd);
	}
	catch(Exception e)
	{
	    e.printStackTrace();
	    return "creating news JDBC connection:" + e.getMessage();
	}
	return null;
    }
}
