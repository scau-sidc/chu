package com.github.scausidc.chu.raffle.core;

import java.util.Arrays;

import org.hibernate.criterion.*;
import static com.github.cuter44.nyafx.dao.EntityNotFoundException.entFound;

import com.github.scausidc.chu.raffle.model.*;
import com.github.scausidc.chu.raffle.dao.*;


public class AwardedMgr
{
  // CONSTRUCT
    protected AwardedDao awardedDao;

    public AwardedMgr()
    {
        this.awardedDao = AwardedDao.getInstance();

        return;
    }

  // SINGLETON
    private static class Singleton
    {
        public static AwardedMgr instance = new AwardedMgr();
    }

    public static AwardedMgr getInstance()
    {
        return(Singleton.instance);
    }

  // REDEEM
    public Awarded redeem(Long id, byte[] passcode)
    {
        Awarded a = (Awarded)entFound(this.awardedDao.get(id));

        if (!Arrays.equals(passcode, a.getPasscode()))
            throw(new IllegalArgumentException("Incorrect passcode."));

        a.setRedeemed(true);

        this.awardedDao.update(a);

        return(a);
    }

}
