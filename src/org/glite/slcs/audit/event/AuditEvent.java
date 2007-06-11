/*
 * $Id: AuditEvent.java,v 1.2 2007/06/11 12:49:27 vtschopp Exp $
 * 
 * Created on Aug 30, 2006 by Valery Tschopp <tschopp@switch.ch>
 *
 * Copyright (c) 2006 SWITCH - http://www.switch.ch/
 */
package org.glite.slcs.audit.event;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.glite.slcs.attribute.Attribute;

/**
 * AuditEvent is an abstract audit event to be log be the Auditor.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.2 $
 */
abstract public class AuditEvent {

    /** Event level INFO */
    static public final int LEVEL_INFO = 1;

    /** Event level WARN */
    static public final int LEVEL_WARN = 2;

    /** Event level ERROR */
    static public final int LEVEL_ERROR = 3;

    /** Event type AUTHORIZATION */
    static public final int TYPE_AUTHORIZATION = 100;

    /** Event type CERIFICATE */
    static public final int TYPE_CERTIFICATE = 200;

    /** Event type SYSTEM */
    static public final int TYPE_SYSTEM = 300;

    /** Level: INFO, WARN or ERROR */
    private int level_;

    /** Type: AUTHORIZATION, CERTIFICATE or SYSTEM */
    private int type_;

    /** The audit message */
    private String message_ = null;

    /** User attributes_ list */
    private List attributes_ = null;

    /** Event date */
    private Date date_ = null;

    /**
     * Constructor.
     * 
     * @param type
     *            The event type.
     * @param level
     *            The event level.
     * @param message
     *            The event message.
     */
    public AuditEvent(int type, int level, String message) {
        type_ = type;
        level_ = level;
        message_ = message;
        attributes_ = new ArrayList();
        date_ = new Date();
    }

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
     *            The user Attributes list
     */
    public AuditEvent(int type, int level, String message, List userInformation) {
        this(type, level, message);
        attributes_ = userInformation;
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
     * @return the date
     */
    public Date getDate() {
        return date_;
    }

    /**
     * @param date
     *            the date to set
     */
    public void setDate(Date date) {
        date_ = date;
    }

    /**
     * @return the user attributes list. Garanteed to be not null.
     */
    public List getAttributes() {
        return attributes_;
    }

    /**
     * Returns the user attributes as a Map (name,value)
     * 
     * @return The user attributes list as a Map.
     */
    public Map getAttributesMap() {
        Map attributesMap = new HashMap();
        Iterator iter= attributes_.iterator();
        while (iter.hasNext()) {
            Attribute attribute = (Attribute) iter.next();
            String name= attribute.getName();
            String value= attribute.getValue();
            if (attributesMap.containsKey(name)) {
                // aggregate multi-value with ;
                String previousValue= (String)attributesMap.get(name);
                value= previousValue + ";" + value;
            }
            attributesMap.put(name, value);
        }
        return attributesMap;
    }

    /**
     * Creates the userInformation map with a list of attributes_.
     * 
     * @param userAttributes
     *            The list of {@link Attribute}s
     */
    public void setAttributes(List userAttributes) {
        attributes_ = userAttributes;
    }

}
