/*
 * $Id: AccessControlListEditorFactory.java,v 1.1 2007/01/30 13:38:33 vtschopp Exp $
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
 * @version $Revision: 1.1 $
 */
public class AccessControlListEditorFactory {

    /** Logging */
    static private Log LOG = LogFactory
            .getLog(AccessControlListEditorFactory.class);

    /** User ACL editor singleton */
    static private AccessControlListEditor LOGIN_SINGLETON = null;

    /**
     * XML element name in the configuration for the login ACL file.
     * See configuration file AccessControlListEditor.LoginACLFile element. 
     */
    static private String LOGIN_ACLFILE_KEY= "LoginACLFile";

    /** User ACL editor singleton */
    static private AccessControlListEditor ADMIN_SINGLETON = null;

    /**
     * XML element name in the configuration for the admin ACL file.
     * See configuration file AccessControlListEditor.AdminACLFile element. 
     */
    static private String ADMIN_ACLFILE_KEY= "AdminACLFile";

    
    /**
     * Gets the current implementation of the Shibboleth access control list
     * editor for the login ACL referenced in the config by
     * <code>LoginACLFile</code>.
     * 
     * @return The {@link AccessControlListEditor} interface of the
     *         implementation.
     * @throws SLCSException
     *             If an error occurs.
     */
    static public synchronized AccessControlListEditor getLoginInstance()
            throws SLCSException {
        if (LOGIN_SINGLETON == null) {
            SLCSServerConfiguration config = SLCSServerConfiguration
                    .getInstance();
            LOGIN_SINGLETON = AccessControlListEditorFactory
                    .newInstance(config, LOGIN_ACLFILE_KEY);
        }
        return LOGIN_SINGLETON;
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