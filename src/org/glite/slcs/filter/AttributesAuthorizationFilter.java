/*
 * $Id: AttributesAuthorizationFilter.java,v 1.1 2007/03/16 13:05:19 vtschopp Exp $
 * 
 * Created on Aug 18, 2006 by tschopp
 *
 * Copyright (c) 2006 SWITCH - http://www.switch.ch/
 */
package org.glite.slcs.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glite.slcs.SLCSException;
import org.glite.slcs.acl.AccessControlList;
import org.glite.slcs.acl.AccessControlListFactory;
import org.glite.slcs.attribute.Attribute;
import org.glite.slcs.util.Utils;

/**
 * AttributesAuthorizationFilter is an ACL filter based on Shibboleth
 * attributes. The filter uses the underlying AccessControlList implementation
 * to checks if the user is authorized.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.1 $
 * 
 * @see org.glite.slcs.acl.AccessControlList
 */
public class AttributesAuthorizationFilter implements Filter {

    /** Logging */
    private static Log LOG = LogFactory
            .getLog(AttributesAuthorizationFilter.class);

    /** Shibboleth ACL */
    private AccessControlList accessControlList_ = null;

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    public void init(FilterConfig filterConfig) throws ServletException {
        try {
            LOG.info("create and initialize new AccessControlList");
            accessControlList_ = AccessControlListFactory
                    .newInstance(filterConfig);
        } catch (SLCSException e) {
            LOG.error("Failed to instantiate and initalize AccessControlList",
                    e);
            throw new ServletException(e);
        }

    }

    /**
     * Checks if the user Shibboleth attributes grant him access.
     */
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        boolean authorized = true;
        List userAttributes = null;
        if (request instanceof HttpServletRequest) {
            authorized = false;
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            LOG.info("check authorization: " + httpRequest.getRequestURI());
            // get shib user attributes
            userAttributes = getUserAttributes(httpRequest);
            // check if user is authorized
            authorized = accessControlList_.isAuthorized(userAttributes);

        }
        if (!authorized) {
            String remoteAddress = request.getRemoteAddr();
            LOG.error(HttpServletResponse.SC_UNAUTHORIZED + ": User (IP:"
                    + remoteAddress + ") is not authorized: " + userAttributes);
            // TODO: custom 401 error page
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse
                    .sendError(HttpServletResponse.SC_UNAUTHORIZED,
                            "Based on your attributes, you are not authorized to access this service");
        }
        // nothing to do, continue
        chain.doFilter(request, response);
    }

    /**
     * Releases all resources
     */
    public void destroy() {
        LOG.info("shutdown ACL implementation");
        accessControlList_.shutdown();
    }

    /**
     * Reads the required authorization Shibboleth attributes from the request
     * headers.
     * 
     * @param req
     *            The HttpServletRequest to read attributes from.
     * @return A List of Shibboleth attributes
     * 
     * @see org.glite.slcs.acl.AccessControlList#getAuthorizationAttributeNames()
     */
    private List getUserAttributes(HttpServletRequest req) {
        // optimization: read only Shibboleth attributes need for
        // authorization decision
        Set authorizationAttributeNames = accessControlList_
                .getAuthorizationAttributeNames();
        if (LOG.isDebugEnabled()) {
            LOG.debug("AuthorizationAttributeNames="
                    + authorizationAttributeNames);
        }
        List attributes = new ArrayList();
        Enumeration headers = req.getHeaderNames();
        while (headers.hasMoreElements()) {
            String header = (String) headers.nextElement();
            if (authorizationAttributeNames.contains(header)) {
                String headerValue = req.getHeader(header);
                // add only not null and not empty attributes
                if (headerValue != null && !headerValue.equals("")) {
                    String decodedValue = Utils
                            .convertShibbolethUTF8ToUnicode(headerValue);
                    // multi-value attributes
                    String[] attrValues = decodedValue.split(";");
                    for (int i = 0; i < attrValues.length; i++) {
                        String attrName = header;
                        String attrValue = attrValues[i];
                        attrValue = attrValue.trim();
                        Attribute attribute = new Attribute(attrName, attrValue);
                        attributes.add(attribute);
                    }
                }

            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("UserAttributes=" + attributes);
        }
        return attributes;
    }

}
