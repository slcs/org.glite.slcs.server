/*
 * $Id: CertificatePolicy.java,v 1.2 2007/02/13 15:50:39 vtschopp Exp $
 * 
 * Created on Sep 6, 2006 by Valery Tschopp <tschopp@switch.ch>
 *
 * Copyright (c) 2006 SWITCH - http://www.switch.ch/
 */
package org.glite.slcs.policy;

import java.util.List;
import java.util.Map;

import org.glite.slcs.Attribute;
import org.glite.slcs.SLCSException;
import org.glite.slcs.SLCSServerComponent;
import org.glite.slcs.pki.CertificateExtension;
import org.glite.slcs.pki.CertificateRequest;

/**
 * CertificatePolicy interface defines the policy applied to the certificate
 * request.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.2 $
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
     * Returns a List of {@link CertificateExtension}s required by the policy.
     * As some extensions could be parametrized, the user's Shibboleth
     * attributes are needed.
     * 
     * @param attributes
     *            The user's Shibboleth attributes Map.
     * @return The List of required cerificate extension.
     * @see org.glite.slcs.pki.CertificateExtension
     */
    public List getRequiredCertificateExtensions(Map attributes);

    /**
     * Returns a List of {@link CertificateExtension}s required by the policy.
     * As some extensions could be parametrized, the user's Shibboleth
     * attributes are needed.
     * 
     * @param attributes
     *            The List of user {@link Attribute}s.
     * @return The List of required cerificate extension.
     * @see org.glite.slcs.pki.CertificateExtension
     */
    public List getRequiredCertificateExtensions(List attributes);

}
