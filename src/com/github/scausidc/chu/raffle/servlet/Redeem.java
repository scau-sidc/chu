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

@WebServlet("/awarded/redeemed.api")
public class Redeem extends HttpServlet
{
    private static final String ID          = "id";
    private static final String PASSCODE    = "passcode";

    protected AwardedDao awardedDao;
    protected AwardedMgr awardedMgr;
    protected JSONObject JSONIZER_CONFIG_AWARDED  = (JSONObject)JSONObject.parse("{'.':8, 'redeemed':1, 'passcode':1}");


    @Override
    public void init()
    {
        this.awardedDao = AwardedDao.getInstance();
        this.awardedMgr = AwardedMgr.getInstance();

        return;
    }

    /** 兑奖
     * <br />
     * 字段 redeemed 为调用此接口之前的状态, i.e. 初次兑奖时该字段为 false,
     * 并在调用完成后置位(但不写入到 response 中), 后续对该 Awarded 的查询均表现为 redeemed = true;
     *
     * <pre style="font-size:12px">

       <strong>请求</strong>
        POST /awarded/redeemed.api

       <strong>参数</strong>
        id          :long       , 必需, Awarded ID
        passcode    :hex        , 必需, passcode
       <i>鉴权</i>
        uid     :long       , uid
        s       :hex        , 签名

       <strong>响应</strong>
        application/json; charset=utf-8; class={@link com.github.scausidc.chu.raffle.model.Awarded Awarded}

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
            Long    id       = needLong(req, ID);
            byte[]  passcode = needByteArray(req, PASSCODE);

            this.awardedDao.begin();

            Awarded a = (Awarded)entFound(this.awardedDao.get(id));
            boolean redeemed = a.isRedeemed();

            this.awardedMgr.redeem(id, passcode);

            this.awardedDao.commit();

            JSONObject json = (JSONObject)jsonize(null, a, JSONIZER_CONFIG_AWARDED);

            json.put("redeemed", redeemed);

            write(resp, json);
        }
        catch (Exception ex)
        {
            req.setAttribute(ATTR_KEY_EXCEPTION, ex);
            req.getRequestDispatcher(URI_ERROR_HANDLER).forward(req, resp);
        }
        finally
        {
            this.awardedDao.close();
        }

        return;
    }

}
