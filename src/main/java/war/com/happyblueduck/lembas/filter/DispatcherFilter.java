package com.happyblueduck.lembas.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: anlcan
 * Date: 2/14/12
 * Time: 5:18 PM
 */
public class DispatcherFilter implements Filter {

    public static final Logger logger = Logger.getLogger(DispatcherFilter.class.getName());

    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {

        HttpServletRequest httpRequest = (HttpServletRequest) req;
        /*
        * OPEN THIS IF YOU NEED DEBUGGING ON EVERY REQUEST
        Enumeration headerNames = httpRequest.getHeaderNames();

        while(headerNames.hasMoreElements()) {
            String headerName = (String)headerNames.nextElement();
            //logger.info("Found header : "+ headerName +  ":"+ httpRequest.getHeader(headerName));
        }

        */
         String objectName = httpRequest.getRequestURI().substring(httpRequest.getRequestURI().lastIndexOf("/") + 1);

        req.setAttribute("request", objectName);


        chain.doFilter(req, resp);


    }

    public void init(FilterConfig config) throws ServletException {

    }

}
