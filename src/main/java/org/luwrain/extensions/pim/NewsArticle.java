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

public class NewsArticle
{
    public static final int NEW = 0;
    public static final int READ = 1;
    public static final int MARKED = 2;

    public String sourceUrl = "";
    public String sourceTitle = "";
    public String uri = "";
    public String title = "";
    public String extTitle = "";
    public String url = "";
    public String descr = "";
    public String author = "";
    public String categories = "";
    public Date publishedDate = new Date();
    public Date updatedDate = new Date();
    public String content = "";
}
