package com.github.scausidc.chu.nyafx.servlet;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

import com.github.cuter44.nyafx.dao.*;
import com.github.cuter44.nyafx.servlet.*;

/**
 * 公共错误解析接口
 * 该接口接受从其他接口转发的请求, 并试图为其抛出的异常作出解释, 以便客户端解析
 *
 * <pre style="font-size:12px">

   <strong>请求</strong>
   /sys/exception.api
   <i>仅接受从其他接口转发的请求</i>

   <strong>响应</strong>
   application/json:
   error    :string , 机器可读的错误原因
   msg      :string , 用于调试的错误信息, 大部分情况下返回 Exception.getMessage();
   <i>error字段可能的取值如下
   <ul>
   <li>ok           : 参见<i>异常</i>部分
   <li>!parameter   : MissingParameterException , 缺少必需的参数, 通常在 msg 中指出缺少的参数名
   <li>!notfound    : EntityNotFoundException   , id 类参数指定的实体不存在, 通常在 msg 中指出抛出该异常的代码位置
   <li>!refered     : EntityReferedException    , 删除操作时实体无法被解引用, 例如删除书籍时所删除的书籍正处于交易当中
   <li>!duplicated  : EntityDuplicatedException , 创建操作违反唯一约束, 例如注册时使用了重合的用户名
   <li>!state       : IlllegalStateException    , 试图在不符合操作状态的情况下操作, 比如未付款但确认收款
   <li>!logged-out  : LoggedOutException        , 未登录
   <li>!login-block : LogginBlockedException    , 账户已禁止使用
   <li>!unauthorized: UnauthorizedEception      , 密码不正确/越权操作
   <li>!error       : 无法解析的错误
   <li>//TODO
   </ul>
   </i>

   <strong>异常</strong>
   直接调用该接口时, 返回 {"error":"ok", "msg":"(´☉ ∀ ☉)ﾉ ?"}

 * </pre>
 */
@WebServlet("/sys/exception.api")
public class ExceptionHandler extends HttpServlet
{
    @Override
    public void service(HttpServletRequest req, HttpServletResponse resp)
        throws IOException
    {
        Exception ex = (Exception)req.getAttribute("exception");
        this.getServletContext().log("", ex);

        String error = "ok";
        String msg = "(´☉ ∀ ☉)ﾉ ?";

        if (ex != null)
        {
            // default
            resp.setStatus(500);
            error = "!error";
            msg = ex.getMessage();

            if (ex instanceof com.github.cuter44.nyafx.dao.EntityNotFoundException)
            {
                resp.setStatus(400);
                error = "!notfound";
            }
            if (ex instanceof com.github.cuter44.nyafx.dao.EntityReferencedException)
            {
                resp.setStatus(400);
                error = "!refered";
            }
            if (ex instanceof com.github.cuter44.nyafx.dao.EntityDuplicatedException)
            {
                resp.setStatus(400);
                error = "!duplicated";
            }
            if (ex instanceof com.github.cuter44.nyafx.servlet.MissingParameterException)
            {
                resp.setStatus(400);
                error = "!parameter";
            }
            //if (ex instanceof com.gufangchan.user.exception.UnauthorizedException)
            //{
                //resp.setStatus(403);
                //error = "!unauthorized";
            //}
            //if (ex instanceof com.gufangchan.user.exception.LoggedOutException)
            //{
                //resp.setStatus(403);
                //error = "!logged-out";
            //}
            //if (ex instanceof com.gufangchan.user.exception.LoginBlockedException)
            //{
                //resp.setStatus(403);
                //error = "!login-block";
            //}
        }

        resp.setContentType("application/json; charset=utf-8");
        PrintWriter out = resp.getWriter();

        out.println("{\"error\":\""+error+"\",\"msg\":\""+msg+"\"}");

        return;
    }
}
