package com.github.scausidc.chu.nyafx.hj;

import java.util.Collection;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import com.alibaba.fastjson.*;
import com.github.cuter44.nyafx.hj.*;

import com.github.scausidc.chu.nyafx.dao.*;

public class Jsonizer
{
    protected static HibernateJsonizer jsonizer =
        new HibernateJsonizer(
            new SessionFactoryClassMetaNarrator(
                CommonDao.getInstance().getSessionFactory()
        ));

    public static void write(HttpServletResponse resp, JSON json)
        throws IOException
    {
        resp.setContentType("application/json; charset=utf-8");
        resp.setCharacterEncoding("utf-8");
        PrintWriter out = resp.getWriter();

        out.println(
            json.toJSONString()
        );

        return;
    }

    public static JSON jsonize(JSON json, Object o, JSONObject conf)
    {
        if (o.getClass().isArray())
            return(
                jsonizer.jsonizeArray((JSONArray)json, (Object[])o, conf)
            );

        if (Collection.class.isAssignableFrom(o.getClass()))
            return(
                jsonizer.jsonizeCollection((JSONArray)json, (Collection)o, conf)
            );

        return(
            jsonizer.jsonizeObject((JSONObject)json, o, conf)
        );
    }


  // CONFIG
    public static final JSONObject JSONIZER_CONFIG_USER     = (JSONObject)JSONObject.parse("{'.':8, 'salt':1, 'pass':1, 'secret':1}");
    public static final JSONObject JSONIZER_CONFIG_AWARDED  = (JSONObject)JSONObject.parse("{'.':8, 'passcode':1}");
}
