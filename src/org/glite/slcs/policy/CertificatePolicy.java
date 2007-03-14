/*
 * $Id: CertificatePolicy.java,v 1.3 2007/03/14 13:52:14 vtschopp Exp $
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html 
 */
package org.glite.slcs.policy;

import java.util.List;

import org.glite.slcs.Attribute;
import org.glite.slcs.SLCSException;
import org.glite.slcs.SLCSServerComponent;
import org.glite.slcs.pki.CertificateExtension;
import org.glite.slcs.pki.CertificateRequest;

/**
 * CertificatePolicy interface defines the policy applied to the certificate
 * request.
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision: 1.3 $
 */
public interface CertificatePolicy extends SLCSServerComponent {

    /**
     * Validates a certificate request against the certificate policy.
     * 
     * @param request
     *            The CertificateRequest object to validate.
     * @param attributes
     *            The user attributes perhap's needed to verfiy user dependent
     *            extension values (SubjectAltName, ...).
     * @return <code>true</code> if and only if the certificate request is
     *         valid.
     * @throws SLCSException
     *             If an error occurs while validating the CSR.
     * @see org.glite.slcs.pki.CertificateRequest
     */
    public boolean isCertificateRequestValid(CertificateRequest request,
            List attributes) throws SLCSException;

    /**
     * Returns a List of {@link CertificateExtension}s required by the policy.
     * As some extensions could be parametrized, the lsit user's Shibboleth
     * attributes are needed.
     * 
     * @param attributes
     *            The List of user {@link Attribute}s.
     * @return The List of required cerificate extension.
     * @see org.glite.slcs.pki.CertificateExtension
     */
    public List getRequiredCertificateExtensions(List attributes);

}
