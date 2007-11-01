/*
 * $Id: AttributeDefinitionsFactory.java,v 1.4 2007/11/01 14:35:11 vtschopp Exp $
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html 
 */
package org.glite.slcs.attribute;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glite.slcs.SLCSException;

public class AttributeDefinitionsFactory {

    /** Singleton */
    static private AttributeDefinitions SINGLETON = null;

    /** Logging */
    static private Log LOG = LogFactory.getLog(AttributeDefinitionsFactory.class);

    /**
     * Initializes the factory with the given attribute definitions XML file.
     * 
     * @param filename
     * @throws SLCSException
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
