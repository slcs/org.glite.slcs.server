/*
 * $Id: SimplePKIRequest.java,v 1.1 2006/10/27 12:11:23 vtschopp Exp $
 * 
 * Created on Jun 15, 2006 by tschopp
 *
 * Copyright (c) 2006 SWITCH - http://www.switch.ch/
 */
package org.glite.slcs.caclient.impl;

import org.glite.slcs.pki.CertificateRequest;

/**
 * Implements the RFC2797 Simple Enrollment Request
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.1 $
 */
public class SimplePKIRequest implements PKIRequest {

    /**
     * File name extension for the Content-Type or Content-Disposition filname
     * parameter
     */
    public static final String FILENAME_EXTENSION= ".p10";

    /** MIME type of a simple enrollement request */
    public static final String MIME_TYPE= "application/pkcs10";

    /** The request to send */
    private CertificateRequest certificateRequest_;

    /**
     * Constructor
     * 
     * @param csr
     *            The CertificateRequest to send in this request
     */
    protected SimplePKIRequest(CertificateRequest csr) {
        this.certificateRequest_= csr;
    }

    /*
     * (non-Javadoc)
     * @see org.glite.slcs.caclient.CARequest#addCertificateRequest(org.glite.slcs.pki.CertificateRequest)
     */
    public void addCertificateRequest(CertificateRequest csr) {
        this.certificateRequest_= csr;
    }

    /*
     *  (non-Javadoc)
     * @see org.glite.slcs.caclient.CARequest#getDEREncoded()
     */
    public byte[] getDEREncoded() {
        return this.certificateRequest_.getDEREncoded();
    }

    /*
     *  (non-Javadoc)
     * @see org.glite.slcs.caclient.impl.PKIRequest#getMimeType()
     */
    public String getMimeType() {
        return MIME_TYPE;
    }

    /*
     *  (non-Javadoc)
     * @see org.glite.slcs.caclient.impl.PKIRequest#getFilenameExtension()
     */
    public String getFilenameExtension() {
        return FILENAME_EXTENSION;
    }


}
