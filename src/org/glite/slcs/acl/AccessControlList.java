/*
 * $Id: AccessControlList.java,v 1.1 2007/01/30 13:38:33 vtschopp Exp $
 * 
 * Created on Aug 18, 2006 by Valery Tschopp <tschopp@switch.ch>
 *
 * Copyright (c) 2006 SWITCH - http://www.switch.ch/
 */
package org.glite.slcs.acl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.FilterConfig;

import org.glite.slcs.Attribute;
import org.glite.slcs.SLCSException;

/**
 * 
 * AccessControlList is an interface for ACL based on Shibboleth
 * attributes.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.1 $
 */
public interface AccessControlList {

    /**
     * Grant access to user based on his Shibboleth attributes.
     * 
     * @param attributes
     *            Map of Shibboleth user's attribute name-value
     * @return <code>true</code> if the user is authorized
     */
    public boolean isAuthorized(Map attributes);

    /**
     * Grant access to user based on his Shibboleth attributes.
     * 
     * @param attributes
     *            List of user's {@link Attribute}s
     * @return <code>true</code> iff the user is authorized
     * @see org.glite.slcs.Attribute
     */
    public boolean isAuthorized(List attributes);

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
     *            The {@link FilterConfig} object
     * @throws SLCSException
     *             If an initialization error occurs
     */
    public void init(FilterConfig filterConfig) throws SLCSException;

    /**
     * Shutdowns the additional resources.
     */
    public void shutdown();

}
