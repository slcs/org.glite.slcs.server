/*
 * $Id: CertificatePolicy.java,v 1.1 2006/10/27 12:11:24 vtschopp Exp $
 * 
 * Created on Sep 6, 2006 by Valery Tschopp <tschopp@switch.ch>
 *
 * Copyright (c) 2006 SWITCH - http://www.switch.ch/
 */
package org.glite.slcs.policy;

import java.util.List;
import java.util.Map;

import org.glite.slcs.SLCSException;
import org.glite.slcs.SLCSServerComponent;
import org.glite.slcs.pki.CertificateRequest;

/**
 * CertificatePolicy interface defines the policy applied to the certificate
 * request.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.1 $
 */
public interface CertificatePolicy extends SLCSServerComponent {

    /**
     * Validates a certificate request against the certificate policy.
     * 
     * @param request
     *            The CertificateRequest object to validate.
     * @param attributes
     *            The user attributes perhap's needed to verfiy user dependent
     *            extensions (SubjectAltName, ...).
     * @return <code>true</code> if and only if the certificate request is
     *         valid.
     * @throws SLCSException
     *             If an error occurs while validating the CSR.
     * @see org.glite.slcs.pki.CertificateRequest
     */
    public boolean isCertificateRequestValid(CertificateRequest request,
            Map attributes) throws SLCSException;

    /**
     * Returns a List of CertificateExtensions required by the policy. As some
     * extensions are parametrized, the user's Shibboleth are needed.
     * 
     * @param attributes
     *            The user's Shibboleth attributes Map.
     * @return The List of required cerificate extension.
     * @see org.glite.slcs.pki.CertificateExtension
     */
    public List getRequiredCertificateExtensions(Map attributes);
}
