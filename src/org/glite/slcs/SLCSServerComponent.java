/*
 * $Id: SLCSServerComponent.java,v 1.1 2006/10/27 12:11:23 vtschopp Exp $
 * 
 * Created on Aug 6, 2006 by tschopp
 *
 * Copyright (c) 2006 SWITCH - http://www.switch.ch/
 */
package org.glite.slcs;

import org.glite.slcs.config.SLCSServerConfiguration;

/**
 * SLCSServerComponent
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.1 $
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
     * Shutdowns the allocated resources.
     */
    public void shutdown();
}
