/*
 * $Id: SystemEvent.java,v 1.1 2006/10/27 12:11:23 vtschopp Exp $
 * 
 * Created on Sep 6, 2006 by Valery Tschopp <tschopp@switch.ch>
 *
 * Copyright (c) 2006 SWITCH - http://www.switch.ch/
 */
package org.glite.slcs.audit.event;

import java.util.Map;

/**
 * SystemEvent is an AuditEvent of type SYSTEM.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.1 $
 */
public class SystemEvent extends AuditEvent {

    /**
     * Constructor. Level is INFO.
     * 
     * @param message
     *            The event message.
     */
    public SystemEvent(String message) {
        super(AuditEvent.TYPE_SYSTEM, AuditEvent.LEVEL_INFO, message, null);
    }

    /**
     * Constructor. Level is INFO.
     * 
     * @param message
     *            The event message.
     * @param userInformation
     *            The user information map.
     */
    public SystemEvent(String message, Map userInformation) {
        super(AuditEvent.TYPE_SYSTEM,
              AuditEvent.LEVEL_INFO,
              message,
              userInformation);
    }

    /**
     * Constructor.
     * 
     * @param level
     *            The event level.
     * @param message
     *            The event message.
     */
    public SystemEvent(int level, String message) {
        super(AuditEvent.TYPE_SYSTEM, level, message, null);
    }

    /**
     * Constructor.
     * 
     * @param level
     *            The event level.
     * @param message
     *            The event message.
     * @param userInformation
     *            The user information map.
     */
    public SystemEvent(int level, String message, Map userInformation) {
        super(AuditEvent.TYPE_SYSTEM, level, message, userInformation);
    }

}
