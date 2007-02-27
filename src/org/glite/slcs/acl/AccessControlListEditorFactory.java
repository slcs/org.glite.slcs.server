/*
 * $Id: AccessControlListEditorFactory.java,v 1.3 2007/02/27 13:24:10 vtschopp Exp $
 * 
 * Created on Aug 18, 2006 by Valery Tschopp <tschopp@switch.ch>
 *
 * Copyright (c) 2006 SWITCH - http://www.switch.ch/
 */
package org.glite.slcs.acl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glite.slcs.SLCSException;
import org.glite.slcs.config.SLCSServerConfiguration;

/**
 * Factory for the Shibboleth access control list user and admin editors.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.3 $
 */
public class AccessControlListEditorFactory {

    /** Logging */
    static private Log LOG = LogFactory
            .getLog(AccessControlListEditorFactory.class);

    /** User ACL editor singleton */
    static private AccessControlListEditor SINGLETON = null;

    /**
     * XML element name in the configuration for the login ACL file.
     * See configuration file AccessControlListEditor.ACLFile element. 
     */
    static private String ACLFILE_KEY= "ACLFile";

    /** User ACL editor singleton */
    static private AccessControlListEditor ADMIN_SINGLETON = null;

    /**
     * XML element name in the configuration for the admin ACL file.
     * See configuration file AccessControlListEditor.AdminACLFile element. 
     */
    static private String ADMIN_ACLFILE_KEY= "AdminACLFile";

    
    /**
     * Gets the current implementation of the Shibboleth access control list
     * editor for the users ACL referenced in the config by
     * <code>ACLFile</code>.
     * 
     * @return The {@link AccessControlListEditor} interface of the
     *         implementation.
     * @throws SLCSException
     *             If an error occurs.
     * @deprecated Use {@link #getInstance()} instead
     */
    static public synchronized AccessControlListEditor getUserInstance()
            throws SLCSException {
                return getInstance();
            }

    /**
     * Gets the current implementation of the Shibboleth access control list
     * editor for the login ACL referenced in the config by
     * <code>ACLFile</code>.
     * 
     * @return The {@link AccessControlListEditor} interface of the
     *         implementation.
     * @throws SLCSException
     *             If an error occurs.
     */
    static public synchronized AccessControlListEditor getInstance()
            throws SLCSException {
        if (SINGLETON == null) {
            SLCSServerConfiguration config = SLCSServerConfiguration
                    .getInstance();
            SINGLETON = AccessControlListEditorFactory
                    .newInstance(config, ACLFILE_KEY);
        }
        return SINGLETON;
    }

    /**
     * Gets the current implementation of the Shibboleth access control list
     * editor for the admin ACL referenced in the config by
     * <code>AdminACLFile</code>.
     * 
     * @return The {@link AccessControlListEditor} interface of the
     *         implementation.
     * @throws SLCSException
     *             If an error occurs.
     */
    static public synchronized AccessControlListEditor getAdminInstance()
            throws SLCSException {
        if (ADMIN_SINGLETON == null) {
            SLCSServerConfiguration config = SLCSServerConfiguration
                    .getInstance();
            ADMIN_SINGLETON = AccessControlListEditorFactory
                    .newInstance(config, ADMIN_ACLFILE_KEY);
        }
        return ADMIN_SINGLETON;
    }

    /**
     * Creates a new instance of the access control list editor implementation.
     * 
     * @param config
     *            The {@link SLCSServerConfiguration} object.
     * @param fileElementName
     *            The XML element name of the XML file to edit.
     * @return The {@link AccessControlListEditor} interface of the
     *         implementation.
     * @throws SLCSException If an error occurs.
     */
    static protected AccessControlListEditor newInstance(
            SLCSServerConfiguration config, String fileElementName)
            throws SLCSException {
        AccessControlListEditor impl = null;
        String className = config
                .getString(SLCSServerConfiguration.COMPONENTSCONFIGURATION_PREFIX + ".AccessControlListEditor[@implementation]");
        LOG.info("AccessControlListEditor implementation=" + className);
        try {
            impl = (AccessControlListEditor) Class.forName(className)
                    .newInstance();
            impl.init(config, fileElementName);
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
    private AccessControlListEditorFactory() {
    }
}
