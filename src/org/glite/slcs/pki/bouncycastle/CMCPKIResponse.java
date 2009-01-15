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
 * $Id: CMCPKIResponse.java,v 1.2 2009/01/15 12:29:42 vtschopp Exp $
 */
package org.glite.slcs.pki.bouncycastle;

import java.security.GeneralSecurityException;
import java.security.Security;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * CMCPKIResponse wrapper for the BouncyCastle CMSSignedData
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.2 $
 */
public class CMCPKIResponse {

    /** Logging */
    static private Log LOG= LogFactory.getLog(CMCPKIResponse.class);

    static {
        // add only once
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            LOG.info("add BouncyCastle security provider");
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    /** the certificates store */
    CertStore certificatesStore_;

    /**
     * Constructor. Decode the CMS signed data in the BC CMSSignedData object.
     * 
     * @param signedData
     *            The byte array of of the CMS Signed Data
     * @throws GeneralSecurityException
     *             If an error occurs.
     */
    public CMCPKIResponse(byte[] signedData) throws GeneralSecurityException {
        try {
            LOG.debug("decode CMSSignedData...");
            CMSSignedData cmsSignedData= new CMSSignedData(signedData);
            certificatesStore_= cmsSignedData.getCertificatesAndCRLs("Collection",
                                                                     BouncyCastleProvider.PROVIDER_NAME);
        } catch (CMSException e) {
            throw new GeneralSecurityException("CMSException: " + e);
        }
    }

    /**
     * Return all X509Certificate embedded in the response.
     * 
     * @return A Collection of X509Certificate (unordered).
     * @throws GeneralSecurityException
     */
    public Collection<X509Certificate> getX509Certificates() throws GeneralSecurityException {
        X509CertSelector selector= new X509CertSelector();
        Collection<? extends Certificate> certs= certificatesStore_.getCertificates(selector);
        return (Collection<X509Certificate>) certs;
    }

}
