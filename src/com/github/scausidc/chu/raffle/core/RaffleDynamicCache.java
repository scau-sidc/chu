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
    protected long lastSerialization = 0;
    protected Map<Long, Long> lastAccess;

    public RaffleDynamicCache()
    {
        this.raffleDao = RaffleDao.getInstance();
        this.rdDao = RaffleDynamicDao.getInstance();
        this.raffles = new HashMap<Long, RaffleDynamic>();
        this.enableds = new HashMap<Long, Boolean>();
        this.lastAccess = new HashMap<Long, Long>();

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

  // SERIALIZE

    public void serialize(long id)
    {
        RaffleDynamic rd = this.raffles.get(id);
        if (rd == null)
            return;

        this.rdDao.getThisSession().merge(rd);

        return;
    }

    public void serialize()
    {
        long t = System.currentTimeMillis();

        for (long id:this.lastAccess.keySet())
            if (this.lastAccess.get(id) > this.lastSerialization)
                this.serialize(id);

        this.lastSerialization = t;

        return;
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

            this.serialize(id);
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
        // {
            this.lastAccess.put(id, System.currentTimeMillis());
            return(this.raffles.get(id));
        // }

    }

}
