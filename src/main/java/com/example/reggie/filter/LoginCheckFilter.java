package com.example.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.example.reggie.common.BaseContext;
import com.example.reggie.common.Response;
import com.example.reggie.entity.Employee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * 检查用户是否已经完成登录
 */
@Slf4j
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    //路径匹配器，支持通配符的匹配
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //1.获取本次请求的路径
        String uri = request.getRequestURI();
        log.info("拦截到本次请求 {}", uri);
        //2.判断本次请求是否需要处理
        String[] urls = new String[]{   //不需要处理的请求路径
                "/employee/login",  //路径必须是以/开头
                "/employee/logout",
                "/backend/**",  //backend下的index.html可以访问，但是没有数据
                "/front/**"  //front下的index.html可以访问，但是没有数据
        };
        //3.如果不需要处理，则直接放行
        if (check(urls, uri)){
            log.info("本次请求{}不需要处理", uri);
            filterChain.doFilter(request, response);
            return;
        }
        //4.判断登录状态，如果已登录，则直接放行
        if(request.getSession().getAttribute("employee") != null){
            log.info("用户{}已登录", request.getSession().getAttribute("employee"));

            //设置ThreadLocal的值
            BaseContext.setCurrentId((Long) request.getSession().getAttribute("employee"));

            filterChain.doFilter(request, response);
            return;
        }
        //5.如果未登录则返回未登录结果，通过输出流的方式向客户端响应数据
        //与backend/js/requests.js对应,状态码是0且错误信息是NOTLOGIN会发送/backend/page/login/login.html请求
        log.info("用户未登录，跳转到登录界面");
        response.getWriter().write(JSON.toJSONString(Response.error("NOTLOGIN")));
    }

    /**
     * 路径匹配，检查本次请求是否需要放行
     * @param urls
     * @param uri
     * @return
     */
    public boolean check(String[] urls, String uri){
        for (String url : urls) {
            if (PATH_MATCHER.match(url, uri))
                return true;
        }
        return false;
    }
}
