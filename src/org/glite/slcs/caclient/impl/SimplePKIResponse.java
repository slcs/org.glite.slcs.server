/*
 * $Id: SimplePKIResponse.java,v 1.1 2006/10/27 12:11:23 vtschopp Exp $
 * 
 * Created on Jun 14, 2006 by tschopp
 *
 * Copyright (c) 2006 SWITCH - http://www.switch.ch/
 */
package org.glite.slcs.caclient.impl;

import java.security.GeneralSecurityException;
import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.security.auth.x500.X500Principal;

import org.glite.slcs.SLCSException;
import org.glite.slcs.pki.Certificate;
import org.glite.slcs.pki.bouncycastle.CMCPKIResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * RCF2797 compliant Simple Enrollment Response
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.1 $
 */
public class SimplePKIResponse implements PKIResponse {

    /** Logging */
    private static Log LOG= LogFactory.getLog(SimplePKIResponse.class);

    /**
     * File name extension for the Content-Type or Content-Disposition filname
     * parameter
     */
    public static final String FILENAME_EXTENSION= ".p7c";

    /** MIME type of a simple enrollement response */
    public static final String MIME_TYPE= "application/pkcs7-mime"; // ";
                                                                    // smime-type=certs-only";

    /**
     * All X509 certificates contained in response
     */
    private Collection x509Certificates_;

    /**
     * Constuctor
     * 
     * @param signedData
     *            The received SignedData bytes
     * @throws GeneralSecurityException
     *             If the SignedData bytes are invalid
     */
    protected SimplePKIResponse(byte[] signedData)
            throws GeneralSecurityException {
        // reconstruct CMSSignedData
        CMCPKIResponse response= new CMCPKIResponse(signedData);
        this.x509Certificates_= response.getX509Certificates();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.caclient.CAResponse#getCertificate(java.security.Principal)
     */
    public Certificate getCertificate(Principal principal) throws SLCSException {
        // find the certificate and the chain based on the subject
        LOG.debug("Looking for: " + principal);
        String searchedSubject= principal.getName();

        X509Certificate cert= null;
        Vector certChain= new Vector();
        Iterator iter= x509Certificates_.iterator();
        while (iter.hasNext()) {
            X509Certificate x509= (X509Certificate) iter.next();
            X500Principal x509Principal= x509.getSubjectX500Principal();
            if (LOG.isDebugEnabled()) {
                LOG.debug("X509 Subject: " + x509Principal);
            }
            // first try object match
            if (x509Principal.equals(principal)) {
                cert= x509;
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Match object: " + x509Principal);
                }
            }
            else {
                // try string match (RFC2253)
                String x509Subject= x509Principal.getName(X500Principal.RFC2253);
                StringTokenizer st= new StringTokenizer(x509Subject,",");
                boolean found= true;
                while (st.hasMoreElements()) {
                    String element= (String) st.nextElement();
                    element= element.trim();
                    if (searchedSubject.indexOf(element) == -1) {
                        found= false;
                    }
                }
                if (found) {
                    cert= x509;
                }
                else {
                    // cert is a chain part
                    certChain.add(x509);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Chain element: " + x509Subject);
                    }
                }
            }
        }
        // check not null and chain size!!!
        if (cert == null) {
            LOG.error("Not matching X509 certificate found: " + principal);
            throw new CMCException("Not matching X509 certificate found: "
                    + principal);
        }
        int size= certChain.size();
        if (size < 1) {
            LOG.warn("X509 certificate chain is empty");
        }
        X509Certificate[] chain= new X509Certificate[size];
        for (int i= 0; i < chain.length; i++) {
            chain[i]= (X509Certificate) certChain.get(i);
        }
        // create and return a new Certificate
        Certificate certificate= null;
        try {
            certificate= new Certificate(cert, chain);
        } catch (GeneralSecurityException e) {
            LOG.error("Failed to create the certificate", e);
            throw new CMCException("Failed to create the certificate", e);
        }
        return certificate;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.caclient.impl.PKIResponse#getMimeType()
     */
    public String getMimeType() {
        return MIME_TYPE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.caclient.impl.PKIResponse#getFilenameExtension()
     */
    public String getFilenameExtension() {
        return FILENAME_EXTENSION;
    }
}
