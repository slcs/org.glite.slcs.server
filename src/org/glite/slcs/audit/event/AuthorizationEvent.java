/*
 * $Id: AuthorizationEvent.java,v 1.2 2007/06/11 12:49:27 vtschopp Exp $
 * 
 * Created on Sep 6, 2006 by Valery Tschopp <tschopp@switch.ch>
 *
 * Copyright (c) 2006 SWITCH - http://www.switch.ch/
 */
package org.glite.slcs.audit.event;

import java.util.List;

/**
 * AuthorizationEvent is an AuditEvent of type AUTHORIZATION.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.2 $
 */
public class AuthorizationEvent extends AuditEvent {

    /**
     * Constructor. Level is INFO.
     * 
     * @param message
     *            The event message.
     */
    public AuthorizationEvent(String message) {
        super(AuditEvent.TYPE_AUTHORIZATION, AuditEvent.LEVEL_INFO, message);
    }

    /**
     * Constructor. Level is INFO.
     * 
     * @param message
     *            The event message.
     * @param userAttributes
     *            The user information map.
     */
    public AuthorizationEvent(String message, List userAttributes) {
        super(AuditEvent.TYPE_AUTHORIZATION, AuditEvent.LEVEL_INFO, message, userAttributes);
    }

    /**
     * Constructor.
     * 
     * @param level
     *            The event level.
     * @param message
     *            The event message.
     */
    public AuthorizationEvent(int level, String message) {
        super(AuditEvent.TYPE_AUTHORIZATION, level, message);
    }

    /**
     * Constructor.
     * 
     * @param level
     *            The event level.
     * @param message
     *            The event message.
     * @param attributes
     *            The List of user Attribute.
     */
    public AuthorizationEvent(int level, String message, List userAttributes) {
        super(AuditEvent.TYPE_AUTHORIZATION, level, message, userAttributes);
    }

}
