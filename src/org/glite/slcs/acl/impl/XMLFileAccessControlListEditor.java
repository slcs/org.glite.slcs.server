/*
 * $Id: XMLFileAccessControlListEditor.java,v 1.4 2007/03/19 14:05:50 vtschopp Exp $
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html 
 */
package org.glite.slcs.acl.impl;

import java.util.ArrayList;
import java.util.Iterator;
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
 * @version $Revision: 1.4 $
 * @see org.glite.slcs.acl.AccessControlListEditor
 */
public class XMLFileAccessControlListEditor implements AccessControlListEditor {

    /** Logging */
    private static Log LOG = LogFactory.getLog(XMLFileAccessControlListEditor.class);

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
    public boolean addAccessControlRule(AccessControlRule rule) {
        XMLOperation operation = new AddAccessControlRuleXMLOperation(rule);
        xmlProcessor_.process(operation);
        return operation.getStatus();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.acl.AccessControlListEditor#getAccessControlRules()
     */
    public List getAccessControlRules() {
        XMLOperation operation = new ListAccessControlRulesXMLOperation(null);
        xmlProcessor_.process(operation);
        List rules = operation.getResults();
        return rules;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.acl.AccessControlListEditor#getAccessControlRules(java.lang.String)
     */
    public List getAccessControlRules(String groupName) {
        XMLOperation operation = new ListAccessControlRulesXMLOperation(groupName);
        xmlProcessor_.process(operation);
        List rules = operation.getResults();
        return rules;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.acl.AccessControlListEditor#getAccessControlRules(java.util.List)
     */
    public List getAccessControlRules(List groupNames) {
        List allRules = new ArrayList();
        Iterator iterator = groupNames.iterator();
        while (iterator.hasNext()) {
            String groupName = (String) iterator.next();
            List rules = getAccessControlRules(groupName);
            allRules.addAll(rules);
        }
        return allRules;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.acl.AccessControlListEditor#getAccessControlRule(int)
     */
    public AccessControlRule getAccessControlRule(int ruleId) {
        XMLOperation operation = new GetAccessControlRuleXMLOperation(ruleId);
        xmlProcessor_.process(operation);
        AccessControlRule rule = (AccessControlRule) operation.getResult();
        return rule;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.acl.AccessControlListEditor#removeAccessControlRule(int,
     *      java.lang.String)
     */
    public boolean removeAccessControlRule(int ruleId) {
        XMLOperation operation = new RemoveAccessControlRuleXMLOperation(ruleId);
        xmlProcessor_.process(operation);
        return operation.getStatus();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.acl.AccessControlListEditor#replaceAccessControlRule(org.glite.slcs.acl.AccessControlRule)
     */
    public boolean replaceAccessControlRule(AccessControlRule rule) {
        XMLOperation operation = new ReplaceAccessControlRuleXMLOperation(rule);
        xmlProcessor_.process(operation);
        return operation.getStatus();
    }

    /*
     * Checks the configuration and initializes the filename referenced by the
     * XML element <code>ACLFile</code>. (non-Javadoc)
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
        String xmlFilename = config.getString(SLCSServerConfiguration.COMPONENTSCONFIGURATION_PREFIX
                + ".AccessControlListEditor." + fileElementName);
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
