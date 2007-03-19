/*
 * $Id: AccessControlList.java,v 1.2 2007/03/19 13:56:44 vtschopp Exp $
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html 
 */
package org.glite.slcs.acl;

import java.util.List;

import javax.servlet.FilterConfig;

import org.glite.slcs.SLCSException;
import org.glite.slcs.attribute.Attribute;

/**
 * 
 * AccessControlList is an interface for ACL based on Shibboleth
 * attributes.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.2 $
 */
public interface AccessControlList {

    /**
     * Grant access to user based on his Shibboleth attributes.
     * 
     * @param attributes
     *            List of user's {@link Attribute}s
     * @return <code>true</code> iff the user is authorized
     * @see org.glite.slcs.attribute.Attribute
     */
    public boolean isAuthorized(List attributes);

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
