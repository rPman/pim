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

abstract class EmailStoringRegistry implements EmailStoring
{
    protected Registry registry;

    public EmailStoringRegistry(Registry registry)
    {
    	this.registry = registry;
    	if (registry == null)
    		throw new NullPointerException("registry may not be null");
    }

    @Override public Object clone()
    {
    	return null;
    }
}
