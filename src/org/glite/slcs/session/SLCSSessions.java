/*
 * $Id: SLCSSessions.java,v 1.3 2007/04/19 15:58:42 vtschopp Exp $
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html 
 */
package org.glite.slcs.session;

import org.glite.slcs.SLCSServerComponent;

/**
 * SLCSSessions interface defines the SLCS sessions container operations.
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision: 1.3 $
 */
public interface SLCSSessions extends SLCSServerComponent {

    /**
     * Creates a new session for the (token,DN) pair. The authorisation token is
     * created automatically.
     * 
     * @param dn
     *            The subject DN to add in the sessions.
     * @return The new session with the token-DN pair defined.
     */
    public SLCSSession createSession(String dn);

    /**
     * Gets the session for this (token,dn) pair.
     * 
     * @param token
     *            The authorisation token.
     * @param dn
     *            The subject DN.
     * @return The session or <code>null</code> if the session doesn't exist.
     */
    public SLCSSession getSession(String token, String dn);

}