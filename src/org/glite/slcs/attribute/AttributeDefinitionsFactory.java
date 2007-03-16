/*
 * $Id: AttributeDefinitionsFactory.java,v 1.2 2007/03/16 14:33:23 vtschopp Exp $
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

    /** Logging */
    static private Log LOG = LogFactory.getLog(AttributeDefinitionsFactory.class);

    /** Singleton */
    static private AttributeDefinitions SINGELTON = null;

    /**
     * @param filename
     * @return
     * @throws SLCSException
     */
    static public synchronized AttributeDefinitions getInstance(String filename)
            throws SLCSException {
        if (SINGELTON == null) {
            SINGELTON = newInstance(filename);
        }
        return SINGELTON;
    }

    /**
     * @param filename
     *            The XML filename
     * @return
     * @throws SLCSException
     */
    static protected AttributeDefinitions newInstance(String filename)
            throws SLCSException {
        LOG.info("instantiate AttributeDefinitions(" + filename + ")...");
        AttributeDefinitions impl = new AttributeDefinitionsImpl(filename);
        return impl;
    }

    /**
     * Prevents instantiation of factory.
     */
    private AttributeDefinitionsFactory() {
    }

}
