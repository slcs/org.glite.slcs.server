/*
 * $Id: AttributesAuthorizationFilter.java,v 1.3 2007/03/19 15:37:53 vtschopp Exp $
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html 
 */
package org.glite.slcs.filter;

import java.io.IOException;
import java.util.List;

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
import org.glite.slcs.attribute.AttributeDefinitions;
import org.glite.slcs.attribute.AttributeDefinitionsFactory;

/**
 * AttributesAuthorizationFilter is an ACL filter based on Shibboleth
 * attributes. The filter uses the underlying AccessControlList implementation
 * to checks if the user is authorized.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.3 $
 * @see org.glite.slcs.acl.AccessControlList
 */
public class AttributesAuthorizationFilter implements Filter {

    /** Logging */
    private static Log LOG = LogFactory.getLog(AttributesAuthorizationFilter.class);

    /** Attributes ACL */
    private AccessControlList accessControlList_ = null;

    /** Attribute definitions */
    private AttributeDefinitions attributeDefinitions_ = null;

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    public void init(FilterConfig filterConfig) throws ServletException {
        try {
            LOG.info("create and initialize new AccessControlList");
            accessControlList_ = AccessControlListFactory.newInstance(filterConfig);
        } catch (SLCSException e) {
            LOG.error("Failed to instantiate and initalize AccessControlList", e);
            throw new ServletException("Failed to instantiate and initalize AccessControlList: "
                    + e, e);
        }
        String attributeDefinitionFile = filterConfig.getInitParameter("AttributeDefinitions");
        try {
            attributeDefinitions_ = AttributeDefinitionsFactory.getInstance(attributeDefinitionFile);
        } catch (SLCSException e) {
            LOG.error("Failed to instantiate AttributeDefinitions("
                    + attributeDefinitionFile + ")", e);
            throw new ServletException("Failed to instantiate AttributeDefinitions("
                    + attributeDefinitionFile + "): " + e, e);
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
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Based on your attributes, you are not authorized to access this service");
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
     * Reads the required authorization attributes from the request headers.
     * 
     * @param req
     *            The HttpServletRequest to read attributes from.
     * @return A List of attributes
     * @see org.glite.slcs.acl.AccessControlList#getAuthorizationAttributeNames()
     */
    private List getUserAttributes(HttpServletRequest req) {
        // uses the AttributeDefinitions engine to read the attributes from
        // the request
        List userAttributes = attributeDefinitions_.getUserAttributes(req);
        return userAttributes;
    }

}