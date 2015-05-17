package com.github.scausidc.chu.raffle.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

import com.github.scausidc.chu.raffle.dao.*;
import com.github.scausidc.chu.raffle.core.*;

public class RaffleDynamicSerializeListener implements ServletContextListener
{
  // DAEMON
    private class SerializeDaemon extends Thread
    {
        @Override
        public void run()
        {
            RaffleDynamicDao    rfDao   = RaffleDynamicDao.getInstance();
            RaffleDynamicCache  rfCache = RaffleDynamicCache.getInstance();

            try
            {
                while (true)
                {
                    rfDao.begin();

                    rfCache.serialize();
                    Thread.sleep(15000);

                    rfDao.commit();

                    rfDao.close();
                }
            }
            catch (InterruptedException ex)
            {
                rfDao.begin();

                rfCache.serialize();

                rfDao.commit();

                rfDao.close();
            }
        }
    }

  // WEB LISTENER
    protected SerializeDaemon daemon;

    @Override
    public void contextDestroyed(ServletContextEvent sce)
    {
        this.daemon.interrupt();

        return;
    }

    @Override
    public void contextInitialized(ServletContextEvent sce)
    {
        this.daemon = new SerializeDaemon();

        this.daemon.setName("RaffleDynamicSerializeDaemon");
        this.daemon.setDaemon(true);

        this.daemon.start();

        return;
    }
}
