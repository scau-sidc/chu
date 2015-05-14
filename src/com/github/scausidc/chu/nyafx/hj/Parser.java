package com.github.scausidc.chu.nyafx.hj;

import java.util.Collection;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import com.alibaba.fastjson.*;
import com.github.cuter44.nyafx.hj.*;

import com.github.scausidc.chu.nyafx.dao.*;

public class Parser
{
    protected static HibernateParamParser parser =
        new HibernateParamParser(
            new SessionFactoryClassMetaNarrator(
                CommonDao.getInstance().getSessionFactory()
        ));

    public static final int PARSER_RETAIN_NAMED = HibernateParamParser.RETAIN_NAMED;


    public static Object parse(Object o, Class clazz, HttpServletRequest req, JSONObject conf)
        throws InstantiationException
    {
        return(
            parser.parse(o, clazz, req, conf)
        );
    }
}
