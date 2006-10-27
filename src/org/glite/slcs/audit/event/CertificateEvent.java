/*
 * $Id: CertificateEvent.java,v 1.1 2006/10/27 12:11:23 vtschopp Exp $
 * 
 * Created on Sep 6, 2006 by Valery Tschopp <tschopp@switch.ch>
 *
 * Copyright (c) 2006 SWITCH - http://www.switch.ch/
 */
package org.glite.slcs.audit.event;

import java.util.Map;

/**
 * CertificateEvent is an AuditEvent of type CERTIFICATE.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.1 $
 */
public class CertificateEvent extends AuditEvent {

    /**
     * Constructor. Level is INFO.
     * 
     * @param message
     *            The event message.
     */
    public CertificateEvent(String message) {
        super(AuditEvent.TYPE_CERTIFICATE, AuditEvent.LEVEL_INFO, message, null);
    }

    /**
     * Constructor. Level is INFO.
     * 
     * @param message
     *            The event message.
     * @param userInformation
     *            The user information map.
     */
    public CertificateEvent(String message, Map userInformation) {
        super(AuditEvent.TYPE_CERTIFICATE,
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
    public CertificateEvent(int level, String message) {
        super(AuditEvent.TYPE_CERTIFICATE, level, message, null);
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
    public CertificateEvent(int level, String message, Map userInformation) {
        super(AuditEvent.TYPE_CERTIFICATE, level, message, userInformation);
    }
}
