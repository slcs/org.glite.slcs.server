/*
 * $Id: XMLFileAccessControlListEditor.java,v 1.1 2007/01/30 13:40:06 vtschopp Exp $
 * 
 * Created on Aug 18, 2006 by Valery Tschopp <tschopp@switch.ch>
 *
 * Copyright (c) 2006 SWITCH - http://www.switch.ch/
 */
package org.glite.slcs.acl.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glite.slcs.SLCSException;
import org.glite.slcs.acl.AccessControlListEditor;
import org.glite.slcs.acl.AccessControlRule;
import org.glite.slcs.config.SLCSServerConfiguration;

/**
 * XMLFileAccessControlList implements a XML file based Shibboleth ACL. This
 * implementation use a FileConfigurationMonitor to track the file modications
 * and reload it on changes.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.1 $
 * 
 * @see org.glite.slcs.acl.AccessControlListEditor
 */
public class XMLFileAccessControlListEditor implements
        AccessControlListEditor {

    /** Logging */
    private static Log LOG = LogFactory
            .getLog(XMLFileAccessControlListEditor.class);

    /** XML file processor */
    private XMLFileProcessor xmlProcessor_ = null;

    /**
     * Constructor called by the factory.
     */
    public XMLFileAccessControlListEditor() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.acl.AccessControlListEditor#addAccessControlRule(org.glite.slcs.acl.AccessControlRule)
     */
    public List addAccessControlRule(AccessControlRule rule) {
        xmlProcessor_.addAccessControlRule(rule);
        return getAccessControlRules(rule.getGroup());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.acl.AccessControlListEditor#getAccessControlRules(java.lang.String)
     */
    public List getAccessControlRules(String group) {
        List rules = xmlProcessor_.getAccessControlRules(group);
        return rules;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.acl.AccessControlListEditor#removeAccessControlRule(org.glite.slcs.acl.AccessControlRule)
     */
    public List removeAccessControlRule(AccessControlRule rule) {
        xmlProcessor_.removeAccessControlRule(rule);
        return getAccessControlRules(rule.getGroup());
    }

    /*
     * Checks the configuration and initializes the filename referenced by the
     * XML element <code>ACLFile</code>.
     * 
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.SLCSServerComponent#init(org.glite.slcs.config.SLCSServerConfiguration)
     */
    public void init(SLCSServerConfiguration config) throws SLCSException {
        init(config, "ACLFile");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.acl.AccessControlListEditor#init(org.glite.slcs.config.SLCSServerConfiguration,
     *      java.lang.String)
     */
    public void init(SLCSServerConfiguration config, String fileElementName)
            throws SLCSException {
        String xmlFilename = config
                .getString( SLCSServerConfiguration.COMPONENTSCONFIGURATION_PREFIX + ".AccessControlListEditor."
                        + fileElementName);
        LOG.info("AccessControlListEditor." + fileElementName + "="
                + xmlFilename);

        // create the XML processor
        xmlProcessor_ = new XMLFileProcessor(xmlFilename);

    }

    
    
    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.SLCSServerComponent#shutdown()
     */
    public void shutdown() {
        // shutdown the FileConfigurationMonitor
        if (xmlProcessor_ != null) {
            LOG.info("shutdown XML processor");
            xmlProcessor_.shutdown();
            xmlProcessor_ = null;
        }
    }

    public String getACLFilename() {
        return xmlProcessor_.getFilename();
    }
}
