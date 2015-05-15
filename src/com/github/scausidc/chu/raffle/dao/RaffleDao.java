package com.github.scausidc.chu.raffle.dao;

import com.github.cuter44.nyafx.dao.DaoBase;
import org.hibernate.criterion.*;

import com.github.scausidc.chu.raffle.model.*;

public class RaffleDao extends DaoBase<Raffle>
{
  // CONSTRUCT
    public RaffleDao()
    {
        super();
    }

  // SINGLETON
    private static class Singleton
    {
        public static RaffleDao instance = new RaffleDao();
    }

    public static RaffleDao getInstance()
    {
        return(Singleton.instance);
    }

  // GET
    @Override
    public Class classOfT()
    {
        return(Raffle.class);
    }

  // EXTENDED
    public Raffle create()
    {
        Raffle r = new Raffle();

        r.setEnabled(Boolean.FALSE);
        r.setDisableOnExhausted(Boolean.TRUE);
        r.setFallbackEnabled(Boolean.FALSE);

        this.save(r);

        return(r);
    }

}
