/*
 * $Id: ShibbolethAuthorizationFilter.java,v 1.1 2006/10/27 12:11:24 vtschopp Exp $
 * 
 * Created on Aug 18, 2006 by tschopp
 *
 * Copyright (c) 2006 SWITCH - http://www.switch.ch/
 */
package org.glite.slcs.filter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.glite.slcs.SLCSException;
import org.glite.slcs.acl.ShibbolethAccessControlList;
import org.glite.slcs.acl.ShibbolethAccessControlListFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ShibbolethAuthorizationFilter is an ACL filter based on Shibboleth
 * attributes. The filter uses the underlying ShibbolethAccessControlList
 * implementation to checks if the user is authorized.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.1 $
 * @see org.glite.slcs.acl.ShibbolethAccessControlList
 */
public class ShibbolethAuthorizationFilter implements Filter {

    /** Logging */
    private static Log LOG= LogFactory.getLog(ShibbolethAuthorizationFilter.class);

    /** Shibboleth ACL */
    private ShibbolethAccessControlList shibbolethACL_= null;

    /*
     * (non-Javadoc)
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    public void init(FilterConfig filterConfig) throws ServletException {
        try {
            LOG.info("instantiate and initialize ShibbolethAccessControlList");
            shibbolethACL_= ShibbolethAccessControlListFactory.newInstance(filterConfig);
        } catch (SLCSException e) {
            LOG.error("Failed to instantiate and initalize ShibbolethAccessControlList",
                      e);
            throw new ServletException(e);
        }

    }

    /**
     * Checks if the user Shibboleth attributes grant him access.
     */
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("check authorization...");
        }
        boolean authorized= true;
        if (request instanceof HttpServletRequest) {
            authorized= false;
            HttpServletRequest httpRequest= (HttpServletRequest) request;
            // get shib user attributes
            Map shibAttributes= getShibbolethAttributes(httpRequest);
            // check if user is authorized
            authorized= shibbolethACL_.isAuthorized(shibAttributes);

        }
        if (!authorized) {
            String remoteAddress= request.getRemoteAddr();
            LOG.error("401: User in not authorized (IP:" + remoteAddress + ")" );
            // TODO: custom 401 error page
            HttpServletResponse httpResponse= (HttpServletResponse) response;
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                                   "Based on your Shibboleth attributes, you are not authorized to access this service");
        }
        else {
            // nothing to do, continue
            chain.doFilter(request, response);
        }
    }

    /**
     * Releases all resources
     */
    public void destroy() {
        LOG.info("shutdown Shibboleth ACL implementation");
        shibbolethACL_.shutdown();
    }

    /**
     * Reads the required Shibboleth attributes from the request headers.
     * 
     * @param req
     *            The HttpServletRequest to read attributes from.
     * @return A Map of Shibboleth attributes name-value
     * @see org.glite.slcs.acl.ShibbolethAccessControlList#getAuthorizationAttributeNames()
     */
    private Map getShibbolethAttributes(HttpServletRequest req) {
        // optimization: read only Shibboleth attributes need for
        // authorization decision
        Set authorizationAttributeNames= shibbolethACL_.getAuthorizationAttributeNames();
        if (LOG.isDebugEnabled()) {
            LOG.debug("ShibbolethAuthorizationAttributeNames="
                    + authorizationAttributeNames);
        }
        Map attributes= new HashMap();
        Enumeration headers= req.getHeaderNames();
        while (headers.hasMoreElements()) {
            String header= (String) headers.nextElement();
            if (authorizationAttributeNames.contains(header)) {
                String value= req.getHeader(header);
                // add only not null and not empty attributes
                if (value != null && !value.equals("")) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Shibboleth attribute: " + header + "="
                                + value);
                    }
                    attributes.put(header, value);
                }

            }
        }
        return attributes;
    }

}
