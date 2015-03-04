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
import java.util.*;

import org.luwrain.core.Registry;

class NewsStoringSql extends NewsStoringRegistry
{
    private Connection con;

    public NewsStoringSql(Registry registry, Connection con)
    {
	super(registry);
	this.con = con;
	if (con == null)
	    throw new NullPointerException("con may not be null");
    }

    @Override public void saveNewsArticle(StoredNewsGroup newsGroup, NewsArticle article) throws SQLException
    {
	if (newsGroup == null)
	    throw new NullPointerException("newsGrroup may not be null");
	if (!(newsGroup instanceof StoredNewsGroupRegistry))
	    throw new IllegalArgumentException("newsGroup is not an instance of StoredNewsGroupRegistry");
	StoredNewsGroupRegistry g = (StoredNewsGroupRegistry)newsGroup;
	PreparedStatement st = con.prepareStatement("INSERT INTO news_article (news_group_id,state,source_url,source_title,uri,title,ext_title,url,descr,author,categories,published_date,updated_date,content) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?);");
	st.setLong(1, g.id);
	st.setInt(2, NewsArticle.NEW);
	st.setString(3, article.sourceUrl);
	st.setString(4, article.sourceTitle);
	st.setString(5, article.uri);
	st.setString(6, article.title);
	st.setString(7, article.extTitle);
	st.setString(8, article.url);
	st.setString(9, article.descr);
	st.setString(10, article.author);
	st.setString(11, article.categories);
	st.setDate(12, new java.sql.Date(article.publishedDate.getTime()));
	st.setDate(13, new java.sql.Date(article.updatedDate.getTime()));
	st.setString(14, article.content);
	st.executeUpdate();
    }

    @Override public     StoredNewsArticle[] loadNewsArticlesOfGroup(StoredNewsGroup newsGroup) throws SQLException
    {
	StoredNewsGroupRegistry g = (StoredNewsGroupRegistry)newsGroup;
	PreparedStatement st = con.prepareStatement("SELECT id,news_group_id,state,source_url,source_title,uri,title,ext_title,url,descr,author,categories,published_date,updated_date,content FROM news_article WHERE news_group_id = ?;");
	st.setLong(1, g.id);
	ResultSet rs = st.executeQuery();
	Vector<StoredNewsArticleSql> articles = new Vector<StoredNewsArticleSql>();
	while (rs.next())
	{
	    StoredNewsArticleSql a = new StoredNewsArticleSql(con);
	    a.id = rs.getLong(1);
	    a.groupId = rs.getLong(2);
	    a.state = rs.getInt(3);
	    a.sourceUrl = rs.getString(4).trim();
	    a.sourceTitle = rs.getString(5).trim();
	    a.uri = rs.getString(6).trim();
	    a.title = rs.getString(7).trim();
	    a.extTitle = rs.getString(8).trim();
	    a.url = rs.getString(9).trim();
	    a.descr = rs.getString(10).trim();
	    a.author = rs.getString(11).trim();
	    a.categories = rs.getString(12).trim();
	    a.publishedDate = new java.util.Date(rs.getDate(13).getTime() + rs.getTime(13).getTime());
	    a.updatedDate = new java.util.Date(rs.getDate(14).getTime() + rs.getTime(14).getTime());
	    a.content = rs.getString(15).trim();
	    articles.add(a);
	}
	return articles.toArray(new StoredNewsArticle[articles.size()]);
    }

    @Override public     StoredNewsArticle[] loadNewsArticlesInGroupWithoutRead(StoredNewsGroup newsGroup) throws SQLException
    {
	StoredNewsGroupRegistry g = (StoredNewsGroupRegistry)newsGroup;
	PreparedStatement st = con.prepareStatement("SELECT id,news_group_id,state,source_url,source_title,uri,title,ext_title,url,descr,author,categories,published_date,updated_date,content FROM news_article WHERE news_group_id = ? AND state <> 1;");
	st.setLong(1, g.id);
	ResultSet rs = st.executeQuery();
	Vector<StoredNewsArticleSql> articles = new Vector<StoredNewsArticleSql>();
	while (rs.next())
	{
	    StoredNewsArticleSql a = new StoredNewsArticleSql(con);
	    a.id = rs.getLong(1);
	    a.groupId = rs.getLong(2);
	    a.state = rs.getInt(3);
	    a.sourceUrl = rs.getString(4).trim();
	    a.sourceTitle = rs.getString(5).trim();
	    a.uri = rs.getString(6).trim();
	    a.title = rs.getString(7).trim();
	    a.extTitle = rs.getString(8).trim();
	    a.url = rs.getString(9).trim();
	    a.descr = rs.getString(10).trim();
	    a.author = rs.getString(11).trim();
	    a.categories = rs.getString(12).trim();
	    //	    a.publishedDate = rs.getDate(13);
	    //	    a.updatedDate = rs.getDate(14);
	    a.publishedDate = new java.util.Date(rs.getDate(13).getTime() + rs.getTime(13).getTime());
	    a.updatedDate = new java.util.Date(rs.getDate(14).getTime() + rs.getTime(14).getTime());
	    a.content = rs.getString(15).trim();
	    articles.add(a);
	}
	return articles.toArray(new StoredNewsArticle[articles.size()]);
    }

    @Override public int countArticlesByUriInGroup(StoredNewsGroup newsGroup, String uri) throws SQLException
    {
	StoredNewsGroupRegistry g = (StoredNewsGroupRegistry)newsGroup;
	PreparedStatement st = con.prepareStatement("SELECT count(*) FROM news_article WHERE news_group_id = ? AND uri = ?;");
	st.setLong(1, g.id);
	st.setString(2, uri);
	ResultSet rs = st.executeQuery();
	if (!rs.next())
	    return 0;
	return rs.getInt(1);
    }

    @Override public int countNewArticleInGroup(StoredNewsGroup group) throws Exception
    {
	if (group == null)
	    return 0;
	if (!(group instanceof StoredNewsGroupRegistry))
	    return 0;
	StoredNewsGroupRegistry g = (StoredNewsGroupRegistry)group;
	PreparedStatement st = con.prepareStatement("SELECT count(*) FROM news_article WHERE news_group_id=? AND state=?;");
	st.setLong(1, g.id);
	st.setLong(2, NewsArticle.NEW);
	ResultSet rs = st.executeQuery();
	if (!rs.next())
	    return 0;
	return rs.getInt(1);
    }

    @Override public int[] countNewArticlesInGroups(StoredNewsGroup[] groups) throws Exception
    {
	if (groups == null)
	    throw new NullPointerException("groups may not be null");
	StoredNewsGroupRegistry[] g = new StoredNewsGroupRegistry[groups.length];
	for(int i =- 0;i < groups.length;++i)
	{
	    if (groups[i] == null)
		throw new NullPointerException("groups[" + i + "] may not be null");
	    if (!(groups[i] instanceof StoredNewsGroupRegistry))
		throw new IllegalArgumentException("groups[" + i + "] must be an instance of StoredNewsGroupRegistry");
	    g[i] = (StoredNewsGroupRegistry)groups[i];
	}
	int[] res = new int[g.length];
	for(int i = 0;i < res.length;++i)
	    res[i] = 0;
	Statement st = con.createStatement();
	ResultSet rs = st.executeQuery("select news_group_id,count(*) from news_article where state=0 group by news_group_id;");
	while (rs.next())
	{
	    final long id = rs.getLong(1);
	    final int count = rs.getInt(2);
	    int k = 0;
	    while (k < g.length && g[k].id != id)
		++k;
	    if (k < g.length)
		res[k] = count;
	}
	return res;
    }

    @Override public int[] countMarkedArticlesInGroups(StoredNewsGroup[] groups) throws Exception
    {
	if (groups == null)
	    throw new NullPointerException("groups may not be null");
	StoredNewsGroupRegistry[] g = new StoredNewsGroupRegistry[groups.length];
	for(int i =- 0;i < groups.length;++i)
	{
	    if (groups[i] == null)
		throw new NullPointerException("groups[" + i + "] may not be null");
	    if (!(groups[i] instanceof StoredNewsGroupRegistry))
		throw new IllegalArgumentException("groups[" + i + "] must be an instance of StoredNewsGroupRegistry");
	    g[i] = (StoredNewsGroupRegistry)groups[i];
	}
	int[] res = new int[g.length];
	for(int i = 0;i < res.length;++i)
	    res[i] = 0;
	Statement st = con.createStatement();
	ResultSet rs = st.executeQuery("select news_group_id,count(*) from news_article where state=2 group by news_group_id;");
	while (rs.next())
	{
	    final long id = rs.getLong(1);
	    final int count = rs.getInt(2);
	    int k = 0;
	    while (k < g.length && g[k].id != id)
		++k;
	    if (k < g.length)
		res[k] = count;
	}
	return res;
    }
}
