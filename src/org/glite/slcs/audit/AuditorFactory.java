/*
 * $Id: AuditorFactory.java,v 1.1 2006/10/27 12:11:23 vtschopp Exp $
 * 
 * Created on Aug 30, 2006 by Valery Tschopp <tschopp@switch.ch>
 *
 * Copyright (c) 2006 SWITCH - http://www.switch.ch/
 */
package org.glite.slcs.audit;

import org.glite.slcs.SLCSException;
import org.glite.slcs.config.SLCSServerConfiguration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * AuditorFactory is a factory to get the singleton instance implementing the
 * Auditor.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.1 $
 */
public class AuditorFactory {

    /** Logging */
    private static Log LOG= LogFactory.getLog(AuditorFactory.class);

    /** Singleton pattern */
    private static Auditor SINGLETON= null;

    /**
     * Factory method to get the singleton instance of the Auditor
     * implementation as defined in the SLCSServerConfiguration.
     * 
     * @return The Auditor singleton instance.
     * @throws SLCSException
     *             If an error occurs while instantiation or initializing the
     *             instance.
     */
    public static synchronized Auditor getInstance() throws SLCSException {
        if (SINGLETON != null) {
            return SINGLETON;
        }
        SLCSServerConfiguration config= SLCSServerConfiguration.getInstance();
        SINGLETON= newInstance(config);
        return SINGLETON;
    }

    /**
     * Creates a new Auditor implementation instance as define in the
     * SLCSServerConfiguration.
     * 
     * @param config
     *            The SLCSServerConfiguration object.
     * @return The Auditor implementation instance. If an error occurs while
     *         instantiation or initializing the instance.
     */
    protected static Auditor newInstance(SLCSServerConfiguration config)
            throws SLCSException {
        Auditor impl= null;
        String className= config.getString("SLCSComponentConfiguration.Auditor[@implementation]");
        LOG.info("Auditor implementation=" + className);
        try {
            impl= (Auditor) Class.forName(className).newInstance();
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
    private AuditorFactory() {
    }

}
