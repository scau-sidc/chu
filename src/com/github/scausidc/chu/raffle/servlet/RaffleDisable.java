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
import com.github.scausidc.chu.raffle.dao.*;
import com.github.scausidc.chu.raffle.core.*;
import com.github.scausidc.chu.raffle.model.*;

@WebServlet("/raffle/disable.api")
public class RaffleDisable extends HttpServlet
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

    /** 中止抽奖
     * <br />
     * 中止前会保存 checkpoint, 从取得 checkpoint 到写入数据库期间的中奖会丢弃.
     * <br />
     * 中止后在重新开始前需要 cooldown, 以排空需要丢弃的中奖请求, 时长视乎服务器性能而定.
     *
     * <pre style="font-size:12px">

       <strong>请求</strong>
        POST /raffle/enable.api

       <strong>参数</strong>
        id      :long       , 必需, ID
       <i>鉴权</i>
        uid     :long       , uid
        s       :hex        , 签名

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

            raffle.setEnabled(Boolean.FALSE);

            this.raffleDao.update(raffle);

            this.rdCache.disable(id);

            this.raffleDao.commit();

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

}
