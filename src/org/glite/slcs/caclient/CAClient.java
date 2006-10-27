/*
 * $Id: CAClient.java,v 1.1 2006/10/27 12:11:23 vtschopp Exp $
 * 
 * Created on Aug 4, 2006 by tschopp
 *
 * Copyright (c) 2006 SWITCH - http://www.switch.ch/
 */
package org.glite.slcs.caclient;

import org.glite.slcs.SLCSException;
import org.glite.slcs.SLCSServerComponent;

/**
 * Interface CAClient defines the operations needed for a generic online CA
 * client.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Version$
 */
public interface CAClient extends SLCSServerComponent {

    /**
     * Returns the CAConnection (session) to process the request/response.
     * 
     * @return The connection the the CA server.
     * @throws Exception
     *             If an error occurs.
     */
    public CAConnection getConnection() throws SLCSException;

}
