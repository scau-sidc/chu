package com.github.scausidc.chu.raffle.dao;

import com.github.cuter44.nyafx.dao.DaoBase;
import org.hibernate.criterion.*;

import com.github.scausidc.chu.raffle.model.*;

public class RaffleDynamicDao extends DaoBase<RaffleDynamic>
{
  // CONSTRUCT
    public RaffleDynamicDao()
    {
        super();
    }

  // SINGLETON
    private static class Singleton
    {
        public static RaffleDynamicDao instance = new RaffleDynamicDao();
    }

    public static RaffleDynamicDao getInstance()
    {
        return(Singleton.instance);
    }

  // GET
    @Override
    public Class classOfT()
    {
        return(RaffleDynamic.class);
    }

  // EXTENDED
    public RaffleDynamic createTransient(Raffle r)
    {
        RaffleDynamic rd = new RaffleDynamic(r);

        return(rd);
    }

    public RaffleDynamic create(Raffle r)
    {
        RaffleDynamic rd = this.createTransient(r);

        this.save(rd);

        return(rd);
    }

}
