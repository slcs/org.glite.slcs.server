/*
 * $Id: SLCSServerComponent.java,v 1.2 2007/03/14 13:26:34 vtschopp Exp $
 * 
 * Created on Aug 6, 2006 by tschopp
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html
 */
package org.glite.slcs;

import org.glite.slcs.config.SLCSServerConfiguration;

/**
 * SLCSServerComponent is the interface that all SLCS server components must
 * implement.
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision: 1.2 $
 */
public interface SLCSServerComponent {

    /**
     * Checks the configuration and initializes the necessary resources.
     * 
     * @param config
     *            The SLCSServerConfiguration object
     * @throws SLCSException
     *             If a configuration or an initialization error occurs.
     */
    public void init(SLCSServerConfiguration config) throws SLCSException;

    /**
     * Shutdowns the resources allocated in init.
     */
    public void shutdown();
}
