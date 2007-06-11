/*
 * $Id: CertificateEvent.java,v 1.2 2007/06/11 12:49:27 vtschopp Exp $
 * 
 * Created on Sep 6, 2006 by Valery Tschopp <tschopp@switch.ch>
 *
 * Copyright (c) 2006 SWITCH - http://www.switch.ch/
 */
package org.glite.slcs.audit.event;

import java.util.List;

/**
 * CertificateEvent is an AuditEvent of type CERTIFICATE.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.2 $
 */
public class CertificateEvent extends AuditEvent {

    /**
     * Constructor. Level is INFO.
     * 
     * @param message
     *            The event message.
     */
    public CertificateEvent(String message) {
        super(AuditEvent.TYPE_CERTIFICATE, AuditEvent.LEVEL_INFO, message);
    }

    /**
     * Constructor. Level is INFO.
     * 
     * @param message
     *            The event message.
     * @param userInformation
     *            The user information map.
     */
    public CertificateEvent(String message, List userAttributes) {
        super(AuditEvent.TYPE_CERTIFICATE, AuditEvent.LEVEL_INFO, message, userAttributes);
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
        super(AuditEvent.TYPE_CERTIFICATE, level, message);
    }
}
