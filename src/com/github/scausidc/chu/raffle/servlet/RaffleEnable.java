package com.github.scausidc.chu.raffle.servlet;

import java.io.IOException;
import java.util.List;
import java.util.Arrays;
import java.util.Date;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

import static com.github.cuter44.nyafx.dao.EntityNotFoundException.entFound;
import static com.github.cuter44.nyafx.servlet.Params.*;
import com.alibaba.fastjson.*;

import static com.github.scausidc.chu.Constants.*;
import static com.github.scausidc.chu.nyafx.hj.Jsonizer.*;
import com.github.scausidc.chu.raffle.core.*;
import com.github.scausidc.chu.raffle.dao.*;
import com.github.scausidc.chu.raffle.model.*;

@WebServlet("/raffle/enable.api")
public class RaffleEnable extends HttpServlet
{
    private static final String ID              = "id";

    protected RaffleDao             raffleDao;
    protected RaffleDynamicDao      rdDao;
    protected RaffleDynamicCache    rdCache;

    @Override
    public void init()
    {
        this.raffleDao  = RaffleDao.getInstance();
        this.rdDao      = RaffleDynamicDao.getInstance();
        this.rdCache    = RaffleDynamicCache.getInstance();

        return;
    }

    /** 启动/继续抽奖
     * <br />
     * 第一次启动时会根据 Raffle 生成 RaffleDynamic, 后继调用此接口会重载之前序列化的 RaffleDynamic.
     * <br />
     * 抽奖已在启动状态下调用此接口, 会重载 RaffleDynamic, 可能导致超出奖品数量. 该漏洞将在稍后修正.
     * <pre style="font-size:12px">

       <strong>请求</strong>
        POST /raffle/enable.api

       <strong>参数</strong>
        id                  :long           , 必需, ID

       <strong>响应</strong>
        application/json; charset=utf-8; class={@link com.github.scausidc.chu.raffle.model.Raffle Raffle}

       </pre>
     *
     */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException
    {
        req.setCharacterEncoding("utf-8");
        try
        {

            this.raffleDao.begin();

            Long id = needLong(req, ID);
            Raffle raffle = (Raffle)entFound(this.raffleDao.get(id));

            raffle.setEnabled(Boolean.TRUE);

            this.raffleDao.update(raffle);

            RaffleDynamic rd = this.rdDao.get(id);
            if (rd == null)
                this.rdDao.create(raffle);

            this.rdCache.load(id);

            this.rdDao.commit();

            write(resp, jsonize(null, raffle, null));
        }
        catch (Exception ex)
        {
            req.setAttribute(ATTR_KEY_EXCEPTION, ex);
            req.getRequestDispatcher(URI_ERROR_HANDLER).forward(req, resp);
        }
        finally
        {
            this.raffleDao.close();
        }

        return;
    }

    /** 删除抽奖
     * <pre style="font-size:12px">

       <strong>请求</strong>
        DELETE /raffle/raffle.api

       <strong>参数</strong>
        id  :long   , ID.

       <strong>响应</strong>
        204 No Content

       </pre>
     *
     */
    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException
    {
        req.setCharacterEncoding("utf-8");

        try
        {
            Long id = needLong(req, ID);

            this.raffleDao.begin();

            this.raffleDao.remove(id);

            this.raffleDao.commit();
        }
        catch(Exception ex)
        {
            this.raffleDao.rollback();

            req.setAttribute(ATTR_KEY_EXCEPTION, ex);
            req.getRequestDispatcher(URI_ERROR_HANDLER).forward(req, resp);
        }
        finally
        {
            this.raffleDao.close();
        }
    }
}
