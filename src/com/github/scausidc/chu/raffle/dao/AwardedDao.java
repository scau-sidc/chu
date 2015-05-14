package com.github.scausidc.chu.raffle.dao;

import com.github.cuter44.nyafx.dao.DaoBase;
import com.github.cuter44.nyafx.crypto.*;
import org.hibernate.criterion.*;

import com.github.scausidc.chu.raffle.model.*;

public class AwardedDao extends DaoBase<Awarded>
{
    protected CryptoBase crypto;

  // CONSTRUCT
    public AwardedDao()
    {
        super();

        this.crypto = CryptoBase.getInstance();
    }

  // SINGLETON
    private static class Singleton
    {
        public static AwardedDao instance = new AwardedDao();
    }

    public static AwardedDao getInstance()
    {
        return(Singleton.instance);
    }

  // GET
    @Override
    public Class classOfT()
    {
        return(Awarded.class);
    }

  // EXTENDED
    public Awarded create(Long raffleId, int award)
    {
        Awarded a = new Awarded(raffleId, award);

        a.setPasscode(this.crypto.randomBytes(8));

        this.save(a);

        return(a);
    }

}
