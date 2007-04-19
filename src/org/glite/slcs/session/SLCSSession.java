/*
 * $Id: SLCSSession.java,v 1.1 2007/04/19 15:58:42 vtschopp Exp $
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html 
 * 
 */
package org.glite.slcs.session;

import java.util.List;

import org.glite.slcs.attribute.Attribute;

/**
 * SLCSSession interface. A SLCS session is identified by the token-DN pair.
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision: 1.1 $
 */
public interface SLCSSession {

    /**
     * @return The authorization token associated with this session
     */
    public String getToken();

    /**
     * @return The certificate distinguished name (DN) associated with this
     *         session
     */
    public String getDN();

    /**
     * Checks if the session is not expired.
     * 
     * @return <code>true</code> if the session is not expired.
     */
    public boolean isValid();

    /**
     * Sets the {@link Attribute}s associated to this session.
     * 
     * @param attributes
     *            List of user's attributes.
     */
    public void setAttributes(List attributes);

    /**
     * Gets the list of user's {@link Attribute}s associated with this session.
     * 
     * @return The list of user's attributes.
     */
    public List getAttributes();
}
