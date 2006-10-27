/*
 * $Id: CMCPKIResponse.java,v 1.1 2006/10/27 12:11:24 vtschopp Exp $
 * 
 * Created on Jun 14, 2006 by tschopp
 *
 * Copyright (c) 2006 SWITCH - http://www.switch.ch/
 */
package org.glite.slcs.pki.bouncycastle;

import java.security.GeneralSecurityException;
import java.security.Security;
import java.security.cert.CertStore;
import java.security.cert.X509CertSelector;
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
 * @version $Revision: 1.1 $
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
    public Collection getX509Certificates() throws GeneralSecurityException {
        X509CertSelector selector= new X509CertSelector();
        Collection certs= certificatesStore_.getCertificates(selector);
        return certs;
    }

}
