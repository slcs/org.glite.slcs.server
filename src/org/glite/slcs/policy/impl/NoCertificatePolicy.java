/*
 * $Id: NoCertificatePolicy.java,v 1.1 2006/10/27 12:11:24 vtschopp Exp $
 * 
 * Created on Sep 13, 2006 by Valery Tschopp <tschopp@switch.ch>
 *
 * Copyright (c) 2006 SWITCH - http://www.switch.ch/
 */
package org.glite.slcs.policy.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.glite.slcs.SLCSException;
import org.glite.slcs.config.SLCSServerConfiguration;
import org.glite.slcs.pki.CertificateRequest;
import org.glite.slcs.policy.CertificatePolicy;

/**
 * NoCertificatePolicy implements a dummy empty certificate policy.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.1 $
 */
public class NoCertificatePolicy implements CertificatePolicy {

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.policy.CertificatePolicy#getRequiredCertificateExtensions(java.util.Map)
     */
    public List getRequiredCertificateExtensions(Map attributes) {
        // no required certificate extensions
        return new ArrayList();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.policy.CertificatePolicy#isCertificateRequestValid(org.glite.slcs.pki.CertificateRequest,
     *      java.util.Map)
     */
    public boolean isCertificateRequestValid(CertificateRequest request,
            Map attributes) throws SLCSException {
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
