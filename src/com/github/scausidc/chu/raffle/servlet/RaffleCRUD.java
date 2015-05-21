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
import static com.github.scausidc.chu.nyafx.hj.Parser.*;
import com.github.scausidc.chu.raffle.dao.*;
import com.github.scausidc.chu.raffle.model.*;

@WebServlet("/raffle/raffle.api")
public class RaffleCRUD extends HttpServlet
{
    private static final String ID              = "id";
    private static final String AWARD_NAME      = "awardName";
    private static final String AWARD_IMAGE     = "awardImage";
    private static final String AWARD_CAPACITY  = "awardCapacity";
    private static final String AWARD_SHEWING   = "awardShewing";
    private static final String AWARD_SLICING   = "awardSlicing";

    protected RaffleDao raffleDao;

    protected JSONObject inProps;

    @Override
    public void init()
    {
        this.raffleDao      = RaffleDao.getInstance();

        this.inProps = JSON.parseObject("{'.':4, 'title':0, 'metainfo':0, 'disableOnExhausted':16, 'fallbackEnabled':16, 'peerPending':0}");

    }

    /** 创建/修改抽奖
     * <pre style="font-size:12px">

       <strong>请求</strong>
        POST /raffle/raffle.api

       <strong>参数</strong>
        id                  :long           , ID, 缺省为创建.
        title               :string(63)     , 标题
        metainfo            :json(32766)    , 自定义 meta
        disableOnExhausted  :boolean        , 超过预定人次时自动停止
        fallbackEnabled     :boolean        , 记录保底奖
        <i>警告: 变更以下参数将扰乱已有抽奖进程</i>
        <i>警告: 违反指引设定值会造成意想不到的效果</i>
        peerPending         :int                        , 预定抽奖人次
        awardName           :string[1+]                 , 奖级名称, 参数的元素个数决定奖级数,
                                                          奖级数量在抽奖初始化后不可变更, 奖级不可调整顺序,
                                                          对于 awardName[i], 珍稀度按 0 → +∞ 递增,
                                                          awardName[0] 必需, 在 fallbackEnabled = true 时作为保底奖, fall... = false 时作为"谢谢惠顾"
        awardImage          :string(32)[]               , 奖品图片的 hash 值, 使用喵咕噜图片散列算法求出的 hash 值
        awardCapacity       :int[awardName.length]      , 必需, 奖品数量, 参数的元素个数必需大于等于奖级数,
                                                          awardCapacity[0] 的值不起作用.
        awardShewing        :int[awardName.length]      , 必需, shewing, 决定对应奖级 i 在人次 awardShewing[i] 前不可被抽中
                                                          建议值为 0,
                                                          awardShewing[0] 的值不起作用
        awardSlicing        :int[awardName.length]      , 必需, slicing, 决定同奖级中奖的分布密度
                                                          建议值为 (peerPending - awardShewing[i])/awardCapacity[i], peerPending 在 1e5 及以下量级时向下调整
                                                          最小值为 1,
                                                          awardSlicing[0] 的值不起作用
        <del>awardDoppler        :int[awardName.length]</del>   , 未支持, doppler, 决定同奖级中奖的多普勒分布
                                                          建议值为 awardSlicing[i], 最小值为 1, 最大值为 awardSlicing[i]*2-1
                                                          为 0 则不使用多普勒分布

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

            Long id = getLong(req, ID);
            Raffle raffle = null;

            if (id == null)
            {
                raffle = this.raffleDao.create();
            }
            else
            {
                raffle = (Raffle)entFound(this.raffleDao.get(id));
            };

            raffle = (Raffle)parse(raffle, Raffle.class, req, this.inProps);

            String[] awardName = getStringArray(req, AWARD_NAME);
            if (awardName != null)
                raffle.setAwardName(awardName);

            String[] awardImage = getStringArray(req, AWARD_IMAGE);
            if (awardImage != null)
                raffle.setAwardImage(awardImage);

            Integer[] awardCapacity = getIntArray(req, AWARD_CAPACITY);
            if (awardCapacity != null)
                raffle.setAwardCapacity(awardCapacity);

            Integer[] awardShewing = getIntArray(req, AWARD_SHEWING);
            if (awardShewing != null)
                raffle.setAwardShewing(awardShewing);

            Integer[] awardSlicing = getIntArray(req, AWARD_SLICING);
            if (awardSlicing != null)
                raffle.setAwardSlicing(awardSlicing);

            this.raffleDao.update(raffle);

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

    /** 删除抽奖
     * <pre style="font-size:12px">

       <strong>请求</strong>
        DELETE /raffle/raffle.api

       <strong>参数</strong>
        id  :long   , ID.
       <i>鉴权</i>
        uid     :long       , uid
        s       :hex        , 签名

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
