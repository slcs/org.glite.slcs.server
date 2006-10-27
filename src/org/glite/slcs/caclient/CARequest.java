/*
 * $Id: CARequest.java,v 1.1 2006/10/27 12:11:23 vtschopp Exp $
 * 
 * Created on Aug 4, 2006 by tschopp
 *
 * Copyright (c) 2006 SWITCH - http://www.switch.ch/
 */
package org.glite.slcs.caclient;

import org.glite.slcs.pki.CertificateRequest;

/**
 * CARequest is an interface for the generic online CA server request.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.1 $
 */
public interface CARequest {

    /**
     * Depending of the request, adds a CertificateRequest to the request, or
     * set the CertificateRequest of the request.
     * 
     * @param csr
     *            The CertificateRequest to send as request
     */
    public void addCertificateRequest(CertificateRequest csr);

    /**
     * @return The DER encoded bytes of the CertificateRequest(s).
     */
    public byte[] getDEREncoded();

}
