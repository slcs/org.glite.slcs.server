/*
 * $Id: Auditor.java,v 1.1 2006/10/27 12:11:23 vtschopp Exp $
 * 
 * Created on Aug 30, 2006 by Valery Tschopp <tschopp@switch.ch>
 *
 * Copyright (c) 2006 SWITCH - http://www.switch.ch/
 */
package org.glite.slcs.audit;

import org.glite.slcs.SLCSException;
import org.glite.slcs.SLCSServerComponent;
import org.glite.slcs.audit.event.AuditEvent;

/**
 * Auditor is a generic audit service use to log AuditEvent.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.1 $
 */
public interface Auditor extends SLCSServerComponent {

    /**
     * Log the AuditEvent in the underlying audit data store.
     * 
     * @param event
     *            The AuditEvent to log.
     * @throws SLCSException If an error occurs when storing the AuditEvent
     */
    public void logEvent(AuditEvent event) throws SLCSException;

}
