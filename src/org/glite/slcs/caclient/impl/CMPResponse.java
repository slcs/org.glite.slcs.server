/**
 * $Id: CMPResponse.java,v 1.1 2007/11/16 10:10:39 mikkonen Exp $
 *
 * Created on 11/07/2007 by Henri Mikkonen <henri.mikkonen@hip.fi>
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html
 */
package org.glite.slcs.caclient.impl;

import java.io.IOException;
import java.io.InputStream;

import java.security.GeneralSecurityException;
import java.security.Principal;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;

import org.glite.slcs.SLCSException;
import org.glite.slcs.caclient.CAResponse;
import org.glite.slcs.pki.Certificate;

import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.x509.X509CertificateStructure;
import org.bouncycastle.jce.provider.X509CertificateObject;

import com.novosec.pkix.asn1.cmp.CertRepMessage;
import com.novosec.pkix.asn1.cmp.CertResponse;
import com.novosec.pkix.asn1.cmp.PKIMessage;

/**
 * CMPResponse is an implementation for RFC 4210 response.
 * 
 * @author Henri Mikkonen <henri.mikkonen@hip.fi>
 */
public class CMPResponse implements CAResponse {

    /** Logging */
    private static Log log = LogFactory.getLog(CMPResponse.class);
    
    /** PKIMessage parsed from the response */
    private PKIMessage pkiMessage = null;

    /*
     * Constructs a <code>CMPResponse</code> and releases the connection
     * @param input the response in an <code>InputStream</code>
     * @param cmpServerPost the <code>PostMethod</code> to be released
     * @throws CMPException
     */
	protected CMPResponse(InputStream input, PostMethod cmpServerPost) throws CMPException {
		if (input == null) {
			log.error("No input for the response!");
		} else {
			try {
				this.pkiMessage = PKIMessage.getInstance(new ASN1InputStream(input).readObject());
			} catch (IOException e) {
				log.error("Error while creating PKIMessage object! " + e.getMessage());
				throw new CMPException(e);
			} finally {
				cmpServerPost.releaseConnection();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.glite.slcs.caclient.CAResponse#getCertificate(java.security.Principal)
	 */
	public Certificate getCertificate(Principal principal) throws CMPException, SLCSException {
		CertRepMessage certRepMessage = this.pkiMessage.getBody().getCp();
		// Get the first message in the chain
		CertResponse certResp = certRepMessage.getResponse(0);
		if (certResp == null) {
			log.error("No certificates found from the response!");
			return null;
		}
		X509CertificateStructure certSt = certResp.getCertifiedKeyPair().getCertOrEncCert().getCertificate();
		X509CertificateObject certObject = null;
		try {
			// generate the certificate object
			certObject = new X509CertificateObject(certSt);
		} catch (Exception e) {
			log.error("Error while creating certObject: " + e);
			throw new CMPException(e);
		}
		if (certObject.getSubjectDN().equals(principal)) {
			log.info("The certificate subject matched with the requested one.");
			X509Certificate cert = (X509Certificate)certObject;
			try {
				return new Certificate(cert);
			} catch (GeneralSecurityException e) {
				throw new SLCSException(e);
			}
		} else {
			log.warn("The certificate subject did NOT match with the requested one!");
			return null;
		}
	}
}
