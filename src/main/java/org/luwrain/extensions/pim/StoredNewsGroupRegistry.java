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

import org.luwrain.core.Registry;

class StoredNewsGroupRegistry implements StoredNewsGroup, Comparable
{
    private Registry registry;
    public int id = 0;
    public String name = "";
    public String[] urls = new String[0];
    public String mediaContentType = "";
    public int orderIndex = 0;
    public int expireAfterDays = 30;

    public StoredNewsGroupRegistry(Registry registry)
    {
	this.registry = registry;
	if (registry == null)
	    throw new NullPointerException("registry may not be null");
    }

    @Override public String getName()
    {
	return name;
    }

    @Override public void setName(String name) throws Exception
    {
	ensureValid();
	if (name == null || name.trim().isEmpty())
	    throw new ValidityException("Trying to set empty name of the news group");
	//FIXME:	RegistryUpdateWrapper.setString(registry, NewsStoringRegistry.GROUPS_PATH + id + "/name", name);
	this.name = name;
    }

    @Override public String[] getUrls()
    {
	return urls != null?urls:new String[0];
    }

    @Override public void setUrls(String[] urls) throws Exception
    {
	ensureValid();
	/*
	if (urls == null)
	    throw new ValidityException("Trying to set to null the list of URLs of the news group \'" + name + "\' (id=" + id + ")");
	String[] oldValues = registry.getValues(NewsStoringRegistry.GROUPS_PATH + id);
	if (oldValues != null)
	    for(String s: oldValues)
		if (s.indexOf("url") == 0)
		    //FIXME:		    RegistryUpdateWrapper.deleteValue(registry, NewsStoringRegistry.GROUPS_PATH + id + "/" + s);
	for(int i = 0;i < urls.length;++i)
	    if (!urls[i].trim().isEmpty())
		RegistryUpdateWrapper.setString(registry, NewsStoringRegistry.GROUPS_PATH + id + "/url" + i, urls[i]);
	this.urls = urls;
	*/
    }

    @Override public String getMediaContentType()
    {
	return mediaContentType != null?mediaContentType:"";
    }

    @Override public void setMediaContentType(String value) throws Exception
    {
	ensureValid();
	if (value == null)
	    throw new ValidityException("Trying to set null value to media content type of the news group \'" + name + "\' (id=" + id + ")");
	//FIXME:	RegistryUpdateWrapper.setString(registry, NewsStoringRegistry.GROUPS_PATH + id + "/media-content-type", value);
	this.mediaContentType = value;
    }

    @Override public int getOrderIndex()
    {
	return orderIndex;
    }

    @Override public void setOrderIndex(int index) throws Exception
    {
	ensureValid();
	//FIXME:	RegistryUpdateWrapper.setInteger(registry, NewsStoringRegistry.GROUPS_PATH + id + "/order-index", index);
	this.orderIndex = index;
    }

    @Override public int getExpireAfterDays()
    {
	return expireAfterDays;
    }

    @Override public void setExpireAfterDays(int count) throws Exception
    {
	ensureValid();
	//FIXME:	RegistryUpdateWrapper.setInteger(registry, NewsStoringRegistry.GROUPS_PATH + id + "/expire-days", count);
	this.expireAfterDays = count;
    }

    @Override public String toString()
    {
	return getName();
    }

    private void ensureValid() throws ValidityException
    {
	if (id < 0)
	    throw new ValidityException("trying to change the state of a news group which is not associated with a storage");
    }

    @Override public int compareTo(Object o)
    {
	if (o == null || !(o instanceof StoredNewsGroupRegistry))
	    return 0;
	StoredNewsGroupRegistry g = (StoredNewsGroupRegistry)o;
	if (orderIndex < g.orderIndex)
	    return -1;
	if (orderIndex > g.orderIndex)
	    return 1;
	return 0;
    }
}
