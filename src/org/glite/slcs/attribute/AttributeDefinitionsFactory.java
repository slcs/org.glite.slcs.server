/*
 * $Id: AttributeDefinitionsFactory.java,v 1.5 2007/11/13 14:33:37 vtschopp Exp $
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html 
 */
package org.glite.slcs.attribute;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glite.slcs.SLCSConfigurationException;
import org.glite.slcs.SLCSException;

public class AttributeDefinitionsFactory {

    /**
     * Parameter name in the servlet context or in the web.xml file
     */
    public static String ATTRIBUTE_DEFINITIONS_FILE_KEY = "AttributeDefinitionsFile";

    /** Singleton */
    static private AttributeDefinitions SINGLETON = null;

    /** Logging */
    static private Log LOG = LogFactory.getLog(AttributeDefinitionsFactory.class);

    /**
     * Initializes the factory using the servlet context parameter
     * <code>AttributeDefintionsFile</code>.
     * 
     * @param context
     *            The servlet context.
     * @throws SLCSException
     *             If the servlet context parameter is missing or if an error
     *             occurs.
     */
    static public synchronized void initialize(ServletContext context)
            throws SLCSException {
        LOG.debug("initialize AttributeDefintionsFactory(ServletContext)");
        String filename = context.getInitParameter(ATTRIBUTE_DEFINITIONS_FILE_KEY);
        if (filename == null) {
            LOG.error("Parameter " + ATTRIBUTE_DEFINITIONS_FILE_KEY
                    + " not found in the servlet context");
            throw new SLCSConfigurationException("Parameter "
                    + ATTRIBUTE_DEFINITIONS_FILE_KEY
                    + " not found in the servlet context");
        }
        initialize(filename);
    }

    /**
     * Initializes the factory with the given attribute definitions XML file.
     * 
     * @param filename
     *            The attribute definitions XML absolute filename.
     * @throws SLCSException
     *             If an error occurs.
     */
    static public synchronized void initialize(String filename)
            throws SLCSException {
        if (SINGLETON == null) {
            LOG.info("create new AttributeDefinitions: " + filename);
            SINGLETON = newInstance(filename);
        }
        else {
            LOG.info("AttributeDefinitions already initialized");
        }
    }

    /**
     * Gets the initialized singleton instance of the attribute definitions.
     * 
     * @return the {@link AttributeDefinitions}.
     * @throws IllegalStateException
     *             if the getInstance method is called on a uninitialized
     *             factory.
     */
    static public synchronized AttributeDefinitions getInstance() {
        if (SINGLETON == null) {
            throw new IllegalStateException(
                    "Not initialized: call AttributeDefinitionsFactory.initialize(...) first.");
        }
        return SINGLETON;

    }

    /**
     * Creates a new instance of the implementing class.
     * 
     * @param filename
     *            The attribute definitions XML filename
     * @return a new {@link AttributeDefinitions} instance.
     * @throws SLCSException
     */
    static protected AttributeDefinitions newInstance(String filename)
            throws SLCSException {
        LOG.info("AttributeDefinitions filename=" + filename);
        AttributeDefinitions impl = new AttributeDefinitionsImpl(filename);
        return impl;
    }

    /**
     * Prevents instantiation of factory.
     */
    private AttributeDefinitionsFactory() {
    }

}
