/*
 * $Id: CertificatePolicyFactory.java,v 1.3 2007/03/14 13:52:14 vtschopp Exp $
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html 
 */
package org.glite.slcs.policy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glite.slcs.SLCSException;
import org.glite.slcs.config.SLCSServerConfiguration;

/**
 * CertificatePolicyFactory is a factory to get the singleton CertificatePolicy
 * implementation based on the SLCSServerConfiguration.
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision: 1.3 $
 */
public class CertificatePolicyFactory {

    /** Logging */
    private static Log LOG = LogFactory.getLog(CertificatePolicyFactory.class);

    /** Singleton pattern */
    private static CertificatePolicy SINGLETON = null;

    /**
     * Factory method to get the singleton instance of the
     * <code>CertificatePolicy</code> implementation. The singleton
     * implementation and initialization is defined in the
     * <code>SCLSServerConfiguration</code>.
     * 
     * @return The CertificatePolicy implementation
     * @throws SLCSException
     *             If a configuration error and an instantiation error occurs.
     */
    static synchronized public CertificatePolicy getInstance()
            throws SLCSException {
        if (SINGLETON != null) {
            return SINGLETON;
        }
        SLCSServerConfiguration config = SLCSServerConfiguration.getInstance();
        SINGLETON = newInstance(config);
        return SINGLETON;
    }

    /**
     * Factory method to create a new DNBuilder instance.
     * 
     * @param config
     *            The <code>SLCSServerConfiguration</code> defining the
     *            implementation and parameters.
     * @return The CertificatePolicy implementation
     * @throws SLCSException
     *             If a configuration error or an instantiation error occurs.
     */
    protected static CertificatePolicy newInstance(
            SLCSServerConfiguration config) throws SLCSException {
        CertificatePolicy builder = null;
        String className = config
                .getString(SLCSServerConfiguration.COMPONENTSCONFIGURATION_PREFIX
                        + ".CertificatePolicy[@implementation]");
        LOG.info("CertificatePolicy implementation=" + className);
        try {
            builder = (CertificatePolicy) Class.forName(className)
                    .newInstance();
            builder.init(config);
        } catch (InstantiationException e) {
            LOG.error("Can not instantiate class: " + className, e);
            throw new SLCSException("Can not instantiate class: " + className,
                    e);
        } catch (IllegalAccessException e) {
            LOG.error("Illegal access for class: " + className, e);
            throw new SLCSException("Illegal access for class: " + className, e);
        } catch (ClassNotFoundException e) {
            LOG.error("Implementation not found: " + className, e);
            throw new SLCSException("Implementation not found: " + className, e);
        }
        return builder;

    }

    /**
     * Prevents instantiation of the factory.
     */
    private CertificatePolicyFactory() {
    }

}
