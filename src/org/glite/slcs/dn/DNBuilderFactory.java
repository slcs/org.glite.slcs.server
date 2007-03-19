/*
 * $Id: DNBuilderFactory.java,v 1.3 2007/03/19 15:39:30 vtschopp Exp $
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html 
 */
package org.glite.slcs.dn;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glite.slcs.SLCSException;
import org.glite.slcs.config.SLCSServerConfiguration;

/**
 * DNBuilderFactory is a factory to get the singleton DNBuilder implementation
 * based on the SLCSServerConfiguration.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.3 $
 */
public class DNBuilderFactory {

    /** Logging */
    private static Log LOG = LogFactory.getLog(DNBuilderFactory.class);

    /** Singleton pattern */
    private static DNBuilder SINGLETON = null;

    /**
     * Factory method to get the singleton instance of the
     * <code>DNBuilder</code> implementation. The singleton implementation and
     * initialization is defined in the <code>SCLSServerConfiguration</code>.
     * 
     * @return The BNBuilder implementation
     * @throws SLCSException
     *             If a configuration error and an instantiation error occurs.
     */
    static synchronized public DNBuilder getInstance() throws SLCSException {
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
     * @return The BNBuilder implementation
     * @throws SLCSException
     *             If a configuration error or an instantiation error occurs.
     */
    protected static DNBuilder newInstance(SLCSServerConfiguration config)
            throws SLCSException {
        DNBuilder builder = null;
        String className = config
                .getString(SLCSServerConfiguration.COMPONENTSCONFIGURATION_PREFIX
                        + ".DNBuilder[@implementation]");
        LOG.info("DNBuilder implementation=" + className);
        try {
            builder = (DNBuilder) Class.forName(className).newInstance();
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
    private DNBuilderFactory() {
    }

}
