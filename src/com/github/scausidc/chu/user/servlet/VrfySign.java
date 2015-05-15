package com.github.scausidc.chu.user.servlet;

import java.io.*;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

import com.github.cuter44.nyafx.dao.*;
import com.github.cuter44.nyafx.servlet.*;
import com.github.cuter44.nyafx.crypto.*;
import static com.github.cuter44.nyafx.servlet.Params.notNull;
import static com.github.cuter44.nyafx.servlet.Params.needLong;
import static com.github.cuter44.nyafx.servlet.Params.needByteArray;

import static com.github.scausidc.chu.Constants.*;
import com.github.scausidc.chu.conf.*;
import com.github.scausidc.chu.user.model.*;
import com.github.scausidc.chu.user.core.*;
import com.github.scausidc.chu.user.exception.*;

/** 测试签名方法
 * 本身是个 no-op, 只是必需通过签名过滤器而已.
 * <pre style="font-size:12px">

   <strong>请求</strong>
   POST /security/test-sign.api

   <strong>参数</strong>
   *    :string , 验签的参数
   <i>身份验证</i>
   uid  :string , uid
   s    :hex    , 签名

   <strong>响应</strong>
   204 No Content

   <strong>例外</strong>
   parsed by {@link com.github.scausidc.chu.nyafx.servlet.ExceptionHandler ExceptionHandler}

   <strong>样例</strong>暂无
 * </pre>
 *
 */
@WebServlet("/security/vrfy-sign.api")
public class VrfySign extends HttpServlet
{
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException
    {
        doPost(req, resp);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException
    {
        req.setCharacterEncoding("utf-8");

        resp.setStatus(204);
    }
}
