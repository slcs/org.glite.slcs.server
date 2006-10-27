/*
 * $Id: CAClientFactory.java,v 1.1 2006/10/27 12:11:23 vtschopp Exp $
 * 
 * Created on Aug 4, 2006 by tschopp
 *
 * Copyright (c) 2006 SWITCH - http://www.switch.ch/
 */
package org.glite.slcs.caclient;

import org.glite.slcs.SLCSException;
import org.glite.slcs.config.SLCSServerConfiguration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * CAClientFactory is a factory to get the singleton CAClient implementation
 * based on the SLCSServerConfiguration.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.1 $
 */
public class CAClientFactory {

    /** Logging */
    private static Log LOG= LogFactory.getLog(CAClientFactory.class);

    /** Singleton pattern */
    private static CAClient SINGLETON= null;

    /**
     * Factory method to get the singleton instance of the CAClient
     * implementation as defined in the SLCSServerConfiguration.
     * 
     * @return The singleton instance of the CAClient implementation.
     * @throws SLCSException
     */
    public static synchronized CAClient getInstance() throws SLCSException {
        if (SINGLETON != null) {
            return SINGLETON;
        }
        SLCSServerConfiguration config= SLCSServerConfiguration.getInstance();
        SINGLETON= newInstance(config);
        return SINGLETON;
    }

    /**
     * Factory method to create a new CAClient instance.
     * 
     * @param config
     *            The <code>SLCSServerConfiguration</code> defining the
     *            implementation and parameters.
     * @return The CAClient implementation
     * @throws SLCSException
     *             If a configuration error or an instantiation error occurs.
     */
    protected static CAClient newInstance(SLCSServerConfiguration config)
            throws SLCSException {
        CAClient impl= null;
        String className= config.getString("SLCSComponentConfiguration.CAClient[@implementation]");
        LOG.info("CAClient implementation=" + className);
        try {
            impl= (CAClient) Class.forName(className).newInstance();
            impl.init(config);
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
        return impl;
    }

    /**
     * Prevents instantiation.
     */
    private CAClientFactory() {
    }

}
