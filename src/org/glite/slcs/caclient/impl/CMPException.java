/**
 * $Id: CMPException.java,v 1.2 2007/11/16 15:03:15 mikkonen Exp $
 *
 * Created on 11/07/2007 by Henri Mikkonen <henri.mikkonen@hip.fi>
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html
 */
package org.glite.slcs.caclient.impl;

import org.glite.slcs.SLCSException;

/**
 * CMPException is the exception type used by the CMP (RFC 4210) implementation.
 * 
 * @author Henri Mikkonen <henri.mikkonen@hip.fi>
 */
public class CMPException extends SLCSException {

    private static final long serialVersionUID = -1038235987633255148L;

    /*
     * Constructs a <code>CMPException</code>
     */
    public CMPException() {
        super();
    }

    /*
     * Constructs a <code>CMPException</code>
     * @param str
     */
    public CMPException(String str) {
        super(str);
    }

    /*
     * Constructs a <code>CMPException</code>
     * @param str
     * @param thrw
     */
    public CMPException(String str, Throwable thrw) {
        super(str, thrw);
    }

    /*
     * Constructs a <code>CMPException</code>
     * @param thrw
     */
    public CMPException(Throwable thrw) {
        super(thrw);
    }
}