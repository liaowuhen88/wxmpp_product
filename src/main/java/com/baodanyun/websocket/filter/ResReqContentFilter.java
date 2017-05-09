package com.baodanyun.websocket.filter;

import com.baodanyun.websocket.util.HttpResReqContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * ResReqContentFilter
 */
public class ResReqContentFilter implements Filter {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void destroy() {

    }

    public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2) throws IOException, ServletException {
            arg0.setCharacterEncoding("UTF-8");
            HttpResReqContext.setRequest((HttpServletRequest) arg0);
            HttpResReqContext.setResponse((HttpServletResponse) arg1);
            arg2.doFilter(arg0, arg1);

    }

    public void init(FilterConfig arg0) throws ServletException {

    }

}
