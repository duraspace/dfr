/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duraspace.dfr.sync.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.duraspace.dfr.sync.service.SyncConfigurationManager;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * The root application configuration class.
 * 
 * @author Daniel Bernstein
 * 
 */
public class SetupCheckFilter implements Filter {

    private SyncConfigurationManager syncConfigurationManager;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        WebApplicationContext springContext =
            WebApplicationContextUtils.getWebApplicationContext(filterConfig.getServletContext());
        this.syncConfigurationManager =
            springContext.getBean(SyncConfigurationManager.class);
    }

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
        throws IOException,
            ServletException {

        HttpServletRequest hrequest = (HttpServletRequest) request;

        String path = hrequest.getRequestURI();

        String setupPath = hrequest.getContextPath() + "/setup";
        String ajax = hrequest.getContextPath() + "/ajax";
        
        
        
        if (this.syncConfigurationManager.isConfigurationComplete()
            || path.startsWith(setupPath) || path.startsWith(ajax)) {
            chain.doFilter(request, response);
        } else {
            HttpServletResponse hresponse = (HttpServletResponse) response;
            hresponse.sendRedirect(setupPath);
        }

    }

    @Override
    public void destroy() {
        // TODO Auto-generated method stub

    }

}
