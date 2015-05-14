package com.github.scausidc.chu.nyafx.dao;

import com.github.cuter44.nyafx.dao.*;

public class CommonDao extends DaoBase<Object>
{
    public CommonDao()
    {
        return;
    }

    private static final class Singleton
    {
        public static CommonDao instance = new CommonDao();
    }

    public static CommonDao getInstance()
    {
        return(Singleton.instance);
    }

    @Override
    public Class classOfT()
    {
        throw(new UnsupportedOperationException("CommonDao does not delegate concrete entity."));
    }
}
