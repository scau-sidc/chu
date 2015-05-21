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
import com.github.scausidc.chu.raffle.model.*;

@WebServlet("/raffle/dynamic/get.api")
public class RaffleDynamicGet extends HttpServlet
{
    private static final String ID = "id";

    protected RaffleDynamicDao rdDao;

    @Override
    public void init()
    {
        this.rdDao      = RaffleDynamicDao.getInstance();

        return;
    }

    /** 取得当前的抽奖活动数据
     * <br />
     * 取得的内容依据 SerializeDaemon 的写入间隔会有所延迟, 默认的间隔为 15s
     *
     * <pre style="font-size:12px">

       <strong>请求</strong>
        POST /raffle/dynamic/get.api

       <strong>参数</strong>
        id      :long       , 必需, ID
       <i>鉴权</i>
        uid     :long       , uid
        s       :hex        , 签名

       <strong>响应</strong>
        application/json; charset=utf-8; class={@link com.github.scausidc.chu.raffle.model.RaffleDynamic RaffleDynamic}

       </pre>
     *
     */
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException
    {
        req.setCharacterEncoding("utf-8");
        try
        {
            this.rdDao.begin();

            Long id = needLong(req, ID);

            RaffleDynamic rd = (RaffleDynamic)entFound(this.rdDao.get(id));

            rdDao.commit();

            write(resp, jsonize(null, rd, null));
        }
        catch (Exception ex)
        {
            req.setAttribute(ATTR_KEY_EXCEPTION, ex);
            req.getRequestDispatcher(URI_ERROR_HANDLER).forward(req, resp);
        }
        finally
        {
            this.rdDao.close();
        }

        return;
    }

}
