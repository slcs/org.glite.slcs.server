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
