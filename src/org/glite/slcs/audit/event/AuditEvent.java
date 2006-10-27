/*
 * $Id: AuditEvent.java,v 1.1 2006/10/27 12:11:23 vtschopp Exp $
 * 
 * Created on Aug 30, 2006 by Valery Tschopp <tschopp@switch.ch>
 *
 * Copyright (c) 2006 SWITCH - http://www.switch.ch/
 */
package org.glite.slcs.audit.event;

import java.util.HashMap;
import java.util.Map;

/**
 * AuditEvent is an abstract audit event to be log be the Auditor.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.1 $
 */
abstract public class AuditEvent {

    /** Event level INFO */
    static public final int LEVEL_INFO= 1;

    /** Event level WARN */
    static public final int LEVEL_WARN= 2;

    /** Event level ERROR */
    static public final int LEVEL_ERROR= 3;

    /** Event type AUTHORIZATION */
    static public final int TYPE_AUTHORIZATION= 100;

    /** Event type CERIFICATE */
    static public final int TYPE_CERTIFICATE= 200;

    /** Event type SYSTEM */
    static public final int TYPE_SYSTEM= 300;

    /** Level: INFO, WARN or ERROR */
    private int level_;

    /** Type: AUTHORIZATION, CERTIFICATE or SYSTEM */
    private int type_;

    /** The audit message */
    private String message_= null;

    /** User information */
    private Map userInformation_= null;

    /**
     * Constructor
     * 
     * @param type
     *            The event type.
     * @param level
     *            The event level
     * @param message
     *            The message
     * @param userInformation
     *            The user information. If <code>null</code> an empty map is
     *            created.
     */
    public AuditEvent(int type, int level, String message, Map userInformation) {
        type_= type;
        level_= level;
        message_= message;
        if (userInformation == null) {
            userInformation_= new HashMap();
        }
        else {
            userInformation_= userInformation;
        }
    }

    /**
     * @return the level
     */
    public int getLevel() {
        return level_;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message_;
    }

    /**
     * @return the type
     */
    public int getType() {
        return type_;
    }

    /**
     * @return the userInformation. Garanteed to be not null.
     */
    public Map getUserInformation() {
        return userInformation_;
    }

    /**
     * @param userInformation
     *            the userInformation to set
     */
    public void setUserInformation(Map userInformation) {
        userInformation_= userInformation;
    }

}
