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

public interface NewsStoring extends Cloneable
{
    StoredNewsGroup[] loadNewsGroups() throws Exception;
    void saveNewsArticle(StoredNewsGroup newsGroup, NewsArticle article) throws Exception;
    StoredNewsArticle[] loadNewsArticlesOfGroup(StoredNewsGroup newsGroup) throws Exception;
    StoredNewsArticle[] loadNewsArticlesInGroupWithoutRead(StoredNewsGroup newsGroup) throws Exception;
    int countArticlesByUriInGroup(StoredNewsGroup newsGroup, String uri) throws Exception;
    int countNewArticleInGroup(StoredNewsGroup group) throws Exception;
    int[] countNewArticlesInGroups(StoredNewsGroup[] groups) throws Exception;
    int[] countMarkedArticlesInGroups(StoredNewsGroup[] groups) throws Exception;
    Object clone();
}
