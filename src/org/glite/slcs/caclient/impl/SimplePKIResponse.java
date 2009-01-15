/*
 * Copyright (c) 2007-2009. Members of the EGEE Collaboration.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * $Id: SimplePKIResponse.java,v 1.2 2009/01/15 12:26:15 vtschopp Exp $
 */
package org.glite.slcs.caclient.impl;

import java.security.GeneralSecurityException;
import java.security.Principal;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import javax.security.auth.x500.X500Principal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.jce.PrincipalUtil;
import org.bouncycastle.jce.X509Principal;
import org.glite.slcs.SLCSException;
import org.glite.slcs.pki.Certificate;
import org.glite.slcs.pki.bouncycastle.CMCPKIResponse;

/**
 * RCF2797 compliant Simple Enrollment Response
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.2 $
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
    private Collection<X509Certificate> x509Certificates_;

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
        LOG.debug("Looking for: " + principal.getName());
        
        X509Certificate cert= null;
        Vector<X509Certificate> certChain= new Vector<X509Certificate>();
        Iterator<X509Certificate> iter= x509Certificates_.iterator();
        while (iter.hasNext()) {
            X509Certificate x509= iter.next();
            X509Principal x509Principal;
            try {
                x509Principal = PrincipalUtil.getSubjectX509Principal(x509);
            } catch (CertificateEncodingException e) {
                LOG.error("Failed to extract X509 subject from certificate", e);
                throw new CMCException("Failed to extract X509 subject from certificate", e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("X509 cert: " + x509Principal.getName());
            }
            // first try object match
            if (x509Principal.equals(principal)) {
                cert= x509;
                if (LOG.isDebugEnabled()) {
                    LOG.debug("X509 cert matches (object): " + principal.getName());
                }
            }
            else {
                // then try X500Principal match (RFC 2253)
                X500Principal principalX500= new X500Principal(principal.getName());
                X500Principal x509PrincipalX500= new X500Principal(x509Principal.getName());
                if (principalX500.getName().equals(x509PrincipalX500.getName())) {
                    cert= x509;
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("X509 cert matches (X500): " + x509PrincipalX500.getName());
                    }
                }
                // otherwise its a chain element
                else {
                    // cert is a chain part
                    certChain.add(x509);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("X509 cert is a chain element: " + x509Principal.getName());
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
