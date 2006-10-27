/*
 * $Id: ShibbolethAccessControlList.java,v 1.1 2006/10/27 12:11:23 vtschopp Exp $
 * 
 * Created on Aug 18, 2006 by Valery Tschopp <tschopp@switch.ch>
 *
 * Copyright (c) 2006 SWITCH - http://www.switch.ch/
 */
package org.glite.slcs.acl;

import java.util.Map;
import java.util.Set;

import javax.servlet.FilterConfig;

import org.glite.slcs.SLCSException;

/**
 * 
 * ShibbolethAccessControlList is an interface for ACL based on Shibboleth
 * attributes.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.1 $
 */
public interface ShibbolethAccessControlList {

    /**
     * Grant access to user based on his Shibboleth attributes.
     * 
     * @param attributes
     *            Map of Shibboleth user's attribute name-value
     * @return <code>true</code> if the user is authorized
     */
    public boolean isAuthorized(Map attributes);

    /**
     * Returns a Set of Shibboleth attribute names required for the
     * authorization decision.
     * 
     * @return the Set of Shibboleth attribute names.
     */
    public Set getAuthorizationAttributeNames();

    /**
     * Initializes the necessary resources.
     * 
     * @param config
     *            The FilterConfig object
     * @throws SLCSException
     *             If an initialization error occurs
     */
    public void init(FilterConfig filterConfig) throws SLCSException;

    /**
     * Shutdowns the additional resources.
     */
    public void shutdown();

}
