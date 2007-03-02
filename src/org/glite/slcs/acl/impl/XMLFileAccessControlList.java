/*
 * $Id: XMLFileAccessControlList.java,v 1.2 2007/03/02 17:24:34 vtschopp Exp $
 * 
 * Created on Aug 18, 2006 by Valery Tschopp <tschopp@switch.ch>
 *
 * Copyright (c) 2006 SWITCH - http://www.switch.ch/
 */
package org.glite.slcs.acl.impl;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.FilterConfig;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glite.slcs.Attribute;
import org.glite.slcs.SLCSConfigurationException;
import org.glite.slcs.SLCSException;
import org.glite.slcs.acl.AccessControlList;
import org.glite.slcs.acl.AccessControlRule;
import org.glite.slcs.config.FileConfigurationEvent;
import org.glite.slcs.config.FileConfigurationListener;
import org.glite.slcs.config.FileConfigurationMonitor;

/**
 * XMLFileAccessControlList implements a XML file based Shibboleth ACL. This
 * implementation use a FileConfigurationMonitor to track the file modications
 * and reload it on changes.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.2 $
 * @see org.glite.slcs.acl.AccessControlList
 * @see org.glite.slcs.config.FileConfigurationListener
 */
public class XMLFileAccessControlList implements AccessControlList,
        FileConfigurationListener {

    /** Logging */
    private static Log LOG = LogFactory.getLog(XMLFileAccessControlList.class);

    /** XML file based authorization */
    private FileConfiguration aclConfiguration_ = null;

    /** Shibboleth attribute names involved in authorization decision */
    private Set aclAuthorizationAttributeNames_ = null;

    /** List of Access Control Rules */
    private List aclAccessControlRules_ = null;

    /** ACL file change monitor */
    private FileConfigurationMonitor aclConfigurationMonitor_ = null;

    /**
     * Constructor called by the factory.
     */
    public XMLFileAccessControlList() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.acl.AccessControlList#init(javax.servlet.FilterConfig)
     */
    public void init(FilterConfig filterConfig) throws SLCSException {
        String filename = filterConfig.getInitParameter("ACLFile");
        LOG.info("ACLFile=" + filename);
        if (filename == null || filename.equals("")) {
            throw new SLCSConfigurationException("Filter parameter ACLFile is missing or empty");
        }

        // load the XML file
        aclConfiguration_ = createACLConfiguration(filename);

        // create the authorization attribute list
        aclAuthorizationAttributeNames_ = createACLAuthorizationAttributeNames(aclConfiguration_);

        // create the access control rules list
        aclAccessControlRules_ = createACLAccessControlRules(aclConfiguration_);

        // deals with the FileConfigurationMonitor
        String monitoringInterval = filterConfig.getInitParameter("ACLFileMonitoringInterval");
        if (monitoringInterval != null) {
            LOG.info("ACLFileMonitoringInterval=" + monitoringInterval);
            aclConfigurationMonitor_ = FileConfigurationMonitor.createFileConfigurationMonitor(aclConfiguration_, monitoringInterval, this);
            // and start
            aclConfigurationMonitor_.start();
        }
    }

    /**
     * Create the Set of Shibboleth attribute names involved in the
     * authorization decision.
     * 
     * @param fileConfiguration
     *            The ACL FileConfiguration.
     * @return The new Set of Shibboleth attribute names.
     */
    static private Set createACLAuthorizationAttributeNames(
            FileConfiguration fileConfiguration) {
        HashSet aclAuthorizationAttributeNames = new HashSet();
        // get all attribute names from configuration
        List aclAttributeNames = fileConfiguration.getList("AccessControlRule.Attribute[@name]");
        Iterator names = aclAttributeNames.iterator();
        while (names.hasNext()) {
            String attributeName = (String) names.next();
            aclAuthorizationAttributeNames.add(attributeName);
        }
        return aclAuthorizationAttributeNames;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.acl.AccessControlList#getAuthorizationAttributeNames()
     */
    public Set getAuthorizationAttributeNames() {
        return aclAuthorizationAttributeNames_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.acl.AccessControlList#isAuthorized(java.util.Map)
     */
    public boolean isAuthorized(Map attributesMap) {
        // create list of user's attributes
        List attributes = new LinkedList();
        Iterator attributeNames = attributesMap.keySet().iterator();
        while (attributeNames.hasNext()) {
            String attributeName = (String) attributeNames.next();
            String attributeValue = (String) attributesMap.get(attributeName);
            Attribute attribute = new Attribute(attributeName, attributeValue);
            attributes.add(attribute);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("attributes=" + attributes);
        }
        return isAuthorized(attributes);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.acl.AccessControlList#isAuthorized(java.util.List)
     */
    public boolean isAuthorized(List userAttributes) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("userAttributes=" + userAttributes);
        }
        boolean authorized = false;
        Iterator rules = aclAccessControlRules_.iterator();
        while (!authorized && rules.hasNext()) {
            AccessControlRule rule = (AccessControlRule) rules.next();
            List ruleAttributes = rule.getAttributes();
            if (LOG.isDebugEnabled()) {
                LOG.debug("checking rule:" + rule);
            }
            if (userAttributes.containsAll(ruleAttributes)) {
                authorized = true;
                LOG.info("User authorized by rule: " + rule);
            }
        }

        if (!authorized) {
            LOG.warn("User not authorized: " + userAttributes);
        }

        return authorized;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.acl.AccessControlList#shutdown()
     */
    public void shutdown() {
        // shutdown the FileConfigurationMonitor
        if (aclConfigurationMonitor_ != null) {
            LOG.info("shutdown ACL file monitor");
            aclConfigurationMonitor_.removeFileConfigurationListener(this);
            aclConfigurationMonitor_.shutdown();
            aclConfigurationMonitor_ = null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.config.FileConfigurationListener#fileConfigurationChanged(org.glite.slcs.config.FileConfigurationEvent)
     */
    public void fileConfigurationChanged(FileConfigurationEvent event) {
        if (event.getType() == FileConfigurationEvent.FILE_MODIFIED) {
            LOG.debug("reload ACL configuration");
            reloadACLConfiguration();
        }
    }

    /**
     * The XML file have changed, then reload the file configuration and
     * recreate all dependent parameters.
     */
    private synchronized void reloadACLConfiguration() {
        LOG.info("reload file: " + aclConfiguration_.getFileName());
        // reload the FileConfiguration
        aclConfiguration_.reload();
        // recreate the authorization attributes list
        aclAuthorizationAttributeNames_ = createACLAuthorizationAttributeNames(aclConfiguration_);
        // recreate the ACL access control rules
        aclAccessControlRules_ = createACLAccessControlRules(aclConfiguration_);
    }

    /**
     * Loads the ACL XML FileConfiguration.
     * 
     * @param filename
     *            The ACL XML filename to load.
     * @return The FileConfiguration object.
     * @throws SLCSConfigurationException
     *             If an configration error occurs while loading the file.
     */
    static private FileConfiguration createACLConfiguration(String filename)
            throws SLCSConfigurationException {
        FileConfiguration config = null;
        try {
            LOG.info("XMLConfiguration file=" + filename);
            config = new XMLConfiguration(filename);
            if (LOG.isDebugEnabled()) {
                File configFile = config.getFile();
                LOG.debug("XMLConfiguration file="
                        + configFile.getAbsolutePath());
            }
        } catch (ConfigurationException e) {
            LOG.error("Failed to create XMLConfiguration: " + filename, e);
            throw new SLCSConfigurationException("Failed to create XMLConfiguration: "
                    + filename, e);
        }
        return config;
    }

    /**
     * Creates a list of {@link AccessControlRule}s loaded from the
     * {@link FileConfiguration}.
     * 
     * @param config
     *            The ACL FileConfiguration object
     * @return A {@link List} of {@link AccessControlRule}s
     */
    static private List createACLAccessControlRules(FileConfiguration config) {
        List accessControlRules = new LinkedList();
        // list all rules
        int i = 0;
        while (true) {
            String rulePrefix = "AccessControlRule(" + i + ")";
            i++;
            // get the name and id of the rule
            String ruleGroup = config.getString(rulePrefix + "[@group]");
            if (ruleGroup == null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(rulePrefix + ": no more rules");
                }
                // no more ACL rule to read, exit while loop
                break;
            }
            int ruleId = config.getInt(rulePrefix + "[@id]");
            // create an empty rule
            AccessControlRule rule = new AccessControlRule(ruleId, ruleGroup);
            // get the attributes name-value for the rule
            List attributeNames = config.getList(rulePrefix
                    + ".Attribute[@name]");
            if (attributeNames.isEmpty()) {
                LOG.error(rulePrefix + ": no attribute in rule, skipping...");
                // error, skipping
                continue;
            }
            List attributeValues = config.getList(rulePrefix + ".Attribute");
            for (int j = 0; j < attributeNames.size(); j++) {
                String name = (String) attributeNames.get(j);
                String value = (String) attributeValues.get(j);
                Attribute attribute = new Attribute(name, value);
                // add attribute to the rule
                rule.addAttribute(attribute);
            }
            // add the rule to the list
            accessControlRules.add(rule);

        } // while

        return accessControlRules;
    }
}
