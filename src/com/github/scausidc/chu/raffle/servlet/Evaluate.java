package com.github.scausidc.chu.raffle.servlet;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.util.Map;

import static com.github.cuter44.nyafx.dao.EntityNotFoundException.entFound;
import static com.github.cuter44.nyafx.servlet.Params.*;

import static com.github.scausidc.chu.Constants.*;
import com.github.scausidc.chu.raffle.core.*;
import com.github.scausidc.chu.raffle.dao.*;
import com.github.scausidc.chu.raffle.model.*;

@WebServlet("/raffle/evaluate.api")
public class Evaluate extends HttpServlet
{
    private static final String ID = "id";

    protected RaffleDao raffleDao;
    protected Drawer    drawer;

    @Override
    public void init()
    {
        this.raffleDao  = RaffleDao.getInstance();
        this.drawer     = Drawer.getInstance();

        return;
    }

    /** 模拟抽奖
     *
     * <pre style="font-size:12px">

       <strong>请求</strong>
        POST /raffle/evaluate.api

       <strong>参数</strong>
        id  :long   , 必需, ID

       <strong>响应</strong>

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

            Map<Integer, Integer> awardeds = this.drawer.evaluate(id);

            resp.setContentType("text/plain; charset=utf-8");
            PrintWriter out = resp.getWriter();

            for (Map.Entry<Integer, Integer> e:awardeds.entrySet())
                out.println(e.getKey()+":"+e.getValue());
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
