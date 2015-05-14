package com.github.scausidc.chu.raffle.core;

import java.util.Map;
import java.util.HashMap;
import java.util.Random;

import org.hibernate.criterion.*;
import static com.github.cuter44.nyafx.dao.EntityNotFoundException.entFound;

import com.github.scausidc.chu.raffle.model.*;
import com.github.scausidc.chu.raffle.dao.*;


public class Drawer
{
  // CONSTRUCT
    protected RaffleDao raffleDao;
    protected RaffleDynamicDao rdDao;
    protected RaffleDynamicCache rdCache;
    protected AwardedDao awardedDao;
    protected Random rand;

    public Drawer()
    {
        this.raffleDao  = RaffleDao.getInstance();
        this.rdDao      = RaffleDynamicDao.getInstance();
        this.rdCache    = RaffleDynamicCache.getInstance();
        this.awardedDao = AwardedDao.getInstance();
        this.rand       = new Random();

        return;
    }

  // SINGLETON
    private static class Singleton
    {
        public static Drawer instance = new Drawer();
    }

    public static Drawer getInstance()
    {
        return(Singleton.instance);
    }

  // DRAWER
    public int draw(Raffle rf, RaffleDynamic rd)
    {
        int peerConsumed = rd.getPeerConsumed() + 1;
        int length = rf.getAwardName().length;

        for (int i=1; i<length; i++)
        {
            if (rd.getAwardConsumed()[i] >= rf.getAwardCapacity()[i])
                continue;

            int r = rd.getAwardTorn()[i] - peerConsumed;
            if (r<=0)
                continue;

            int p = this.rand.nextInt(r);
            if (p == 0)
                return(i);
        }

        return(0);
    }


  // EXPOSED
    public Awarded draw(Long raffleId)
    {
        return(null);
    }

    /** evaluate raffle
     * @return pairs of #peer:#award, award#0 is skipped
     */
    public Map<Integer, Integer> evaluate(Long raffleId)
    {
        Raffle r = this.raffleDao.get(raffleId);
        RaffleDynamic rd = new RaffleDynamic(r);

        Integer[] awardCapacity = r.getAwardCapacity();
        int totalAwardCapacity = 0;
        for (int i=1; i<awardCapacity.length; i++)
            totalAwardCapacity += awardCapacity[i];

        Map<Integer, Integer> awardeds = new HashMap<Integer, Integer>(totalAwardCapacity);

        int p = r.getPeerPending();
        for (int i=0; i<p; i++)
        {
            int a = this.draw(r, rd);
            if (a!=0)
            {
                rd.getAwardConsumed()[a] += 1;
                rd.getAwardTorn()[a] += r.getAwardSlicing()[a];

                awardeds.put(i, a);
            }
        }

        return(awardeds);
    }


}
