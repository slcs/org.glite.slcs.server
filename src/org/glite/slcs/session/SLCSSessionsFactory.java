/*
 * $Id: SLCSSessionsFactory.java,v 1.2 2007/02/13 15:55:42 vtschopp Exp $
 * 
 * Created on Aug 4, 2006 by tschopp
 *
 * Copyright (c) 2006 SWITCH - http://www.switch.ch/
 */
package org.glite.slcs.session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glite.slcs.SLCSException;
import org.glite.slcs.config.SLCSServerConfiguration;

/**
 * SLCSSessionsFactory is a factory to get the singleton implementation instance
 * as defined in the SLCSServerConfiguration.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.2 $
 */
public class SLCSSessionsFactory {

    /** Logging */
    static private Log LOG = LogFactory.getLog(SLCSSessionsFactory.class);

    /** singleton pattern */
    static private SLCSSessions SINGLETON = null;

    /**
     * Gets the singleton instance implemented as defined in the
     * SLCSServerConfiguration.
     * 
     * @return The singleton implementation of the SLCSSessions.
     * @throws SLCSException
     *             If an error occurs while instantiating or initializing the
     *             implementation.
     */
    static synchronized public SLCSSessions getInstance() throws SLCSException {
        if (SINGLETON != null) {
            return SINGLETON;
        }
        SLCSServerConfiguration config = SLCSServerConfiguration.getInstance();
        SINGLETON = newInstance(config);
        return SINGLETON;
    }

    /**
     * Creates a new intance of the SLCSSessions implementation as defined in
     * the SLCSServerConfiguration.
     * 
     * @param config
     *            The SLCSServerConfiguration object.
     * @return The SLCSSessions implementation instance.
     * @throws SLCSException
     *             If an error occurs while instantiating and initializing the
     *             implementation instance.
     */
    protected static SLCSSessions newInstance(SLCSServerConfiguration config)
            throws SLCSException {
        SLCSSessions impl = null;
        // instantiate
        String className = config
                .getString(SLCSServerConfiguration.COMPONENTSCONFIGURATION_PREFIX
                        + ".SLCSSessions[@implementation]");
        LOG.info("SLCSSessions implementation=" + className);
        try {
            impl = (SLCSSessions) Class.forName(className).newInstance();
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
     * Prevents instantiation of the factory (utility pattern)
     */
    private SLCSSessionsFactory() {
    }

}
