/*
 * $Id: AccessControlListFactory.java,v 1.1 2007/01/30 13:38:33 vtschopp Exp $
 * 
 * Created on Aug 18, 2006 by Valery Tschopp <tschopp@switch.ch>
 *
 * Copyright (c) 2006 SWITCH - http://www.switch.ch/
 */
package org.glite.slcs.acl;

import javax.servlet.FilterConfig;

import org.glite.slcs.SLCSConfigurationException;
import org.glite.slcs.SLCSException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * AccessControlListFactory is a factory to create new
 * AccessControlList implementation instance based on the
 * FilterConfiguration.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.1 $
 */
public class AccessControlListFactory {

    /** Logging */
    private static Log LOG= LogFactory.getLog(AccessControlListFactory.class);

    /**
     * Creates a new instance of AccessControlList implementation.
     * 
     * @param filterConfig
     *            The FilterConfig containing the ACLImplementation parameter.
     * @return A new instance of the implmenting AccessControlList
     * @throws SLCSException
     *             If the instantiation of the AccessControlList
     *             implementation failed.
     */
    public static AccessControlList newInstance(
            FilterConfig filterConfig) throws SLCSException {
        String className= filterConfig.getInitParameter("ACLImplementation");
        // check null or empty
        if (className == null || className.equals("")) {
            throw new SLCSConfigurationException("Filter parameter ACLImplementation is missing or empty");
        }
        LOG.info("AccessControlList implementation=" + className);
        // instantiate new
        AccessControlList impl= null;
        try {
            impl= (AccessControlList) Class.forName(className).newInstance();
            impl.init(filterConfig);
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
     * Prevents instantiation of the factory
     */
    private AccessControlListFactory() {
    }

}
