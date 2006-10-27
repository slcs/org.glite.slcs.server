/*
 * $Id: SLCSSessions.java,v 1.1 2006/10/27 12:11:24 vtschopp Exp $
 * 
 * Created on Aug 4, 2006 by tschopp
 *
 * Copyright (c) 2006 SWITCH - http://www.switch.ch/
 */
package org.glite.slcs.session;

import java.util.Map;

import org.glite.slcs.SLCSServerComponent;

/**
 * SLCSSessions interface define the operation needed for the SLCS sessions.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.1 $
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
     * Return the attributes Map associated with the session.
     * 
     * @param token
     *            The authorization token.
     * @return The attributes Map or <code>null</code> if the session doesn't
     *         exists.
     */
    public Map getAttributes(String token);

    /**
     * Sets the attributes associated with an existing session.
     * 
     * @param token
     *            The authorization token
     * @param attributes
     *            The attributes Map to associated with the session.
     */
    public void setAttributes(String token, Map attributes);

}