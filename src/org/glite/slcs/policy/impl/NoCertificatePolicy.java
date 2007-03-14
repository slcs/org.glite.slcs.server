/*
 * $Id: NoCertificatePolicy.java,v 1.3 2007/03/14 13:54:08 vtschopp Exp $
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html 
 */
package org.glite.slcs.policy.impl;

import java.util.Collections;
import java.util.List;

import org.glite.slcs.SLCSException;
import org.glite.slcs.config.SLCSServerConfiguration;
import org.glite.slcs.pki.CertificateRequest;
import org.glite.slcs.policy.CertificatePolicy;

/**
 * NoCertificatePolicy implements a dummy empty certificate policy.
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision: 1.3 $
 */
public class NoCertificatePolicy implements CertificatePolicy {

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.policy.CertificatePolicy#getRequiredCertificateExtensions(java.util.List)
     */
    public List getRequiredCertificateExtensions(List attributes) {
        // no required certificate extensions
        return Collections.EMPTY_LIST;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.policy.CertificatePolicy#isCertificateRequestValid(org.glite.slcs.pki.CertificateRequest,
     *      java.util.List)
     */
    public boolean isCertificateRequestValid(CertificateRequest request,
            List attributes) throws SLCSException {
        // no checks, always true
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.SLCSServerComponent#init(org.glite.slcs.config.SLCSServerConfiguration)
     */
    public void init(SLCSServerConfiguration config) throws SLCSException {
        // no initialization
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.SLCSServerComponent#shutdown()
     */
    public void shutdown() {
    }

}
