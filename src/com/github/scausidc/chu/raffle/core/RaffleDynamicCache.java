package com.github.scausidc.chu.raffle.core;

import java.util.Map;
import java.util.HashMap;

import org.hibernate.criterion.*;
import static com.github.cuter44.nyafx.dao.EntityNotFoundException.entFound;

import com.github.scausidc.chu.raffle.model.*;
import com.github.scausidc.chu.raffle.dao.*;


public class RaffleDynamicCache
{
  // CONSTRUCT
    protected RaffleDao raffleDao;
    protected RaffleDynamicDao rdDao;
    protected Map<Long, RaffleDynamic> raffles;
    protected Map<Long, Boolean> enableds;

    public RaffleDynamicCache()
    {
        this.raffleDao = RaffleDao.getInstance();
        this.rdDao = RaffleDynamicDao.getInstance();
        this.raffles = new HashMap<Long, RaffleDynamic>();
        this.enableds = new HashMap<Long, Boolean>();

        return;
    }

  // SINGLETON
    private static class Singleton
    {
        public static RaffleDynamicCache instance = new RaffleDynamicCache();
    }

    public static RaffleDynamicCache getInstance()
    {
        return(Singleton.instance);
    }

  // CACHE
    public boolean isEnabled(Long id)
    {
        return(
            Boolean.TRUE.equals(this.enableds.get(id))
        );
    }

    public void load(Long id)
    {
        Raffle          r   = (Raffle)entFound(this.raffleDao.get(id));
        RaffleDynamic   rd  = this.rdDao.get(id);

        if (rd == null)
        {
            this.enableds.put(id, false);

            return;
        }

        this.raffles.put(id, rd);
        this.enableds.put(id, r.isEnabled());

        return;
    }

    /** Serialize changes and forbid new access.
     */
    public void disable(Long id)
    {
        synchronized(this.enableds)
        {
            this.enableds.put(id, Boolean.FALSE);

            RaffleDynamic rd = this.raffles.get(id);
            if (rd != null)
                this.rdDao.getThisSession().merge(rd);
        }

        return;
    }

    public RaffleDynamic get(Long id)
    {
        Boolean enabled = this.enableds.get(id);
        if (Boolean.FALSE.equals(enabled))
            throw(new IllegalStateException("Raffle disabled. id="+id));

        if (enabled == null)
            synchronized(this.enableds)
            {
                if (this.enableds.get(id) == null)
                    this.load(id);
                return(this.get(id));
            }

        // if (Boolean.TRUE.equals(enabled))
            return(this.raffles.get(id));

    }

}
