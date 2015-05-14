package com.github.scausidc.chu.user.dao;

import com.github.cuter44.nyafx.dao.DaoBase;
import org.hibernate.criterion.*;

import com.github.scausidc.chu.user.model.User;

public class UserDao extends DaoBase<User>
{
  // CONSTRUCT
    public UserDao()
    {
        super();
    }

  // SINGLETON
    private static class Singleton
    {
        public static UserDao instance = new UserDao();
    }

    public static UserDao getInstance()
    {
        return(Singleton.instance);
    }

  // GET
    @Override
    public Class classOfT()
    {
        return(User.class);
    }

  // EXTENDED
}
