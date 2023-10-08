package com.ww.filter;

import com.ww.entity.User;
import com.ww.utils.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.context.support.XmlWebApplicationContext;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebFilter(urlPatterns = "/order/*",
        initParams = {
                @WebInitParam(name = "charset",value = "utf-8")/*这里可以放一些初始化的参数*/
        })
public class UserFilter implements Filter {

    private final static Logger log = LoggerFactory.getLogger(UserFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response =(HttpServletResponse)servletResponse;

        //获取头参数
        String username = (String)request.getHeader("username");
        String password = (String)request.getHeader("password");
        log.info("user filter username="+username+",password="+password);

        /*
        不能直接通过@autowired方式获取redisUtil，也不能直接new一个，因为不能读取连接配置
        这里应该从context中获取
         */
        ServletContext sc = request.getSession().getServletContext();
        WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(sc);
        RedisUtil redisUtil = null;
        User user = null;
        if(ctx != null && ctx.getBean("redisUtil") != null ){
            redisUtil = (RedisUtil)ctx.getBean("redisUtil");
            user = (User)redisUtil.get(username);
        }

        if(user != null && user.getUsername().equals(username) && user.getPassword().equals(password)){
            log.info("user filter passed");
            filterChain.doFilter(servletRequest,servletResponse);
        }else{
            log.info("user filter denied");
            response.getWriter().write("Not currently logged in!");
        }
    }

    @Override
    public void destroy() {

    }
}
