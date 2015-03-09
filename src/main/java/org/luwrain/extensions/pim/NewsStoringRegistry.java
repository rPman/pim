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

import org.luwrain.core.Registry;
import org.luwrain.util .RegistryAutoCheck;

abstract class NewsStoringRegistry implements NewsStoring
{
    protected Registry registry;

    public NewsStoringRegistry(Registry registry)
    {
	this.registry = registry;
	if (registry == null)
	    throw new NullPointerException("registry may not be null");
    }

    @Override public StoredNewsGroup[] loadNewsGroups() throws Exception
    {
	RegistryKeys keys = new RegistryKeys();
	String[] groupsNames = registry.getDirectories(keys.newsGroups());
	if (groupsNames == null || groupsNames.length < 1)
	    return new StoredNewsGroup[0];
	Vector<StoredNewsGroup> groups = new Vector<StoredNewsGroup>();
	for(String s: groupsNames)
	{
	    if (s == null || s.isEmpty())
		continue;
	    StoredNewsGroupRegistry g = readNewsGroup(s);
	    if (g != null)
		groups.add(g);
	}
	System.out.println("groups " + groups.size());
	return groups.toArray(new StoredNewsGroup[groups.size()]);
    }

    private StoredNewsGroupRegistry readNewsGroup(String name)
    {
	if (name == null)
	    throw new NullPointerException("name may not be null");
	if (name.isEmpty())
	    throw new IllegalArgumentException("name may not be empty");
	StoredNewsGroupRegistry g = new StoredNewsGroupRegistry(registry);
	try {
	    g.id = Integer.parseInt(name.trim());
	}
	catch(NumberFormatException e)
	{
	    e.printStackTrace();
	    return null;
	}
	RegistryKeys keys = new RegistryKeys();
	RegistryAutoCheck check = new RegistryAutoCheck(registry);
	final String path = keys.newsGroups() + "/" + name;
	g.name = check.stringNotEmpty(path + "/name", "");
	if (g.name.isEmpty())
	    return null;
	g.expireAfterDays = check.intPositive(path + "/expire-days", -1);
	if (g.expireAfterDays < 0)
	    g.expireAfterDays = 0;
	g.orderIndex = check.intPositive(path + "/order-index", -1);
	if (g.orderIndex < 0)
	    g.orderIndex = 0;
	g.mediaContentType = check.stringAny(path + "media-content-type", "");
	String[] values = registry.getValues(path);
	if (values == null)
	    return null;
	Vector<String> urls = new Vector<String>();
	for(String s: values)
	{
	    if (s == null || s.indexOf("url") < 0)
		continue;
	    if (registry.getTypeOf(path + "/" + s) != Registry.STRING)
		continue;
	    final String value = registry.getString(path + "/" + s);
	    if (!value.trim().isEmpty())
		urls.add(value);
	}
	g.urls = urls.toArray(new String[urls.size()]);
	return g;
    }

    @Override public Object clone()
    {
	return null;
    }
}
