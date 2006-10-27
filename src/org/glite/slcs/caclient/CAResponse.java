/*
 * $Id: CAResponse.java,v 1.1 2006/10/27 12:11:23 vtschopp Exp $
 * 
 * Created on Aug 4, 2006 by tschopp
 *
 * Copyright (c) 2006 SWITCH - http://www.switch.ch/
 */
package org.glite.slcs.caclient;

import java.security.Principal;

import org.glite.slcs.SLCSException;
import org.glite.slcs.pki.Certificate;

/**
 * CAResponse is an interface for a generic online CA server response.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.1 $
 */
public interface CAResponse {

    /**
     * Gets the Certificate (with its chain) for the given Principal.
     * 
     * @param subject
     *            The Principal (DN).
     * @return The Certificate object.
     * @throws SLCSException
     *             If an error occurs.
     */
    public Certificate getCertificate(Principal subject) throws SLCSException;

}
