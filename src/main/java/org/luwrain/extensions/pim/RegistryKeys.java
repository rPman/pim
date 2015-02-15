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

import java.util.Properties;
import java.net.URL;
import java.io.IOException;

class RegistryKeys
{
    private static final String REGISTRY_KEYS_RESOURCE = "org/luwrain/extensions/pim/registry-keys.properties";

    private Properties properties;

    public RegistryKeys()
    {
	URL url = ClassLoader.getSystemResource(REGISTRY_KEYS_RESOURCE);
	if (url == null)
	    return;
	properties = new Properties();
	try {
	    properties.load(url.openStream());
	}
	catch (IOException e)
	{
	    e.printStackTrace();
	}
    }

    public String newsType()
    {
	return getProperty("news.type");
    }

    public String newsDriver()
    {
	return getProperty("news.driver");
    }

    public String newsUrl()
    {
	return getProperty("news.turl");
    }

    public String newsLogin()
    {
	return getProperty("news.login");
    }

    public String newsPasswd()
    {
	return getProperty("news.passwd");
    }

    public String newsGroups()
    {
	return getProperty("news.groups");
    }

    private String getProperty(String name)
    {
	if (name == null)
	    throw new NullPointerException("name may not be null");
	if (name.isEmpty())
	    throw new IllegalArgumentException("name may not be empty");
	if (properties == null)
	    throw new NullPointerException("properties object is null");
	final String value = properties.getProperty(name);
	if (value == null)
	    throw new NullPointerException("property value is null");
	return value;
    }
};
