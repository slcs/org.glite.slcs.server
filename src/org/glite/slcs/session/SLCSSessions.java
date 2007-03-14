/*
 * $Id: SLCSSessions.java,v 1.2 2007/03/14 13:58:10 vtschopp Exp $
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html 
 */
package org.glite.slcs.session;

import java.util.List;

import org.glite.slcs.Attribute;
import org.glite.slcs.SLCSServerComponent;

/**
 * SLCSSessions interface define the operation needed for the SLCS sessions.
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision: 1.2 $
 */
public interface SLCSSessions extends SLCSServerComponent {

    /**
     * Creates a new session for the (token,DN) pair and returns the random
     * token.
     * 
     * @param dn
     *            The subject DN to add in the sessions.
     * @return The authorization token assciated with this session.
     */
    public String createSession(String dn);

    /**
     * Unregister the session.
     * 
     * @param token
     *            The authorization token associated with this session.
     */
    public void removeSession(String token);

    /**
     * Checks if the session exists for this (token,DN) pair.
     * 
     * @param token
     *            The authorization token of the session.
     * @param dn
     *            The subject DN associated with the token.
     * 
     * @return <code>true</code> iff this session exists.
     */
    public boolean sessionExists(String token, String dn);

    /**
     * Checks iff the session exists and is not expired.
     * 
     * @param token
     *            The authorization token of the session.
     * @param dn
     *            The subject DN associated with the token.
     * @return <code>true</code> iff the session exists and is not expired.
     */
    public boolean isSessionValid(String token, String dn);

    /**
     * Return the {@link Attribute}s list associated with the session.
     * 
     * @param token
     *            The authorization token.
     * @return The attributes list or <code>null</code> if the session doesn't
     *         exists.
     */
    public List getAttributes(String token);

    /**
     * Sets the attributes associated with an existing session.
     * 
     * @param token
     *            The authorization token
     * @param attributes
     *            The attributes list to associated with the session.
     */
    public void setAttributes(String token, List attributes);

}