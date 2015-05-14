package com.github.scausidc.chu.user.dao;

import java.util.Date;

import com.github.cuter44.nyafx.dao.DaoBase;
import org.hibernate.criterion.*;

import com.github.scausidc.chu.user.model.*;

public class AdminDao extends DaoBase<Admin>
{
  // CONSTRUCT
    public AdminDao()
    {
        super();
    }

  // SINGLETON
    private static class Singleton
    {
        public static AdminDao instance = new AdminDao();
    }

    public static AdminDao getInstance()
    {
        return(Singleton.instance);
    }

  // GET
    @Override
    public Class classOfT()
    {
        return(Admin.class);
    }

  // EXTENDED
    public Admin create(String uname)
    {
        Admin a = new Admin();

        a.setUname(uname);
        a.setMail(uname+"@localhost");
        a.setStatus(User.STATUS_ACTIVATED);
        a.setRegDate(new Date(/*now*/));

        this.save(a);

        return(a);
    }

    public Admin forUname(String uname)
    {
        DetachedCriteria dc = DetachedCriteria.forClass(Admin.class)
            .add(Restrictions.eq("uname", uname));

        return(
            this.get(dc)
        );
    }
}
