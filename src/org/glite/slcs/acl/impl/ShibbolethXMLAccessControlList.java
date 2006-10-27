/*
 * $Id: ShibbolethXMLAccessControlList.java,v 1.1 2006/10/27 12:11:23 vtschopp Exp $
 * 
 * Created on Aug 18, 2006 by Valery Tschopp <tschopp@switch.ch>
 *
 * Copyright (c) 2006 SWITCH - http://www.switch.ch/
 */
package org.glite.slcs.acl.impl;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.servlet.FilterConfig;

import org.glite.slcs.SLCSConfigurationException;
import org.glite.slcs.SLCSException;
import org.glite.slcs.acl.ShibbolethAccessControlList;
import org.glite.slcs.config.FileConfigurationEvent;
import org.glite.slcs.config.FileConfigurationListener;
import org.glite.slcs.config.FileConfigurationMonitor;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ShibbolethXMLAccessControlList implements a XML file based Shibboleth ACL.
 * This implementation use a FileConfigurationMonitor to track the file
 * modications and reload it on changes.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.1 $
 * 
 * @see org.glite.slcs.acl.ShibbolethAccessControlList
 * @see org.glite.slcs.config.FileConfigurationListener
 */
public class ShibbolethXMLAccessControlList implements
        ShibbolethAccessControlList, FileConfigurationListener {

    /** Logging */
    private static Log LOG= LogFactory.getLog(ShibbolethXMLAccessControlList.class);

    /** XML file based authorization */
    private FileConfiguration aclConfiguration_= null;

    /** Shibboleth attribute names involved in authorization decision */
    private Set aclAuthorizationAttributeNames_= null;

    /** ACL memory cache: Map(name,Set(values)) */
    private Map aclAttributesCache_= null;

    /** ACL file change monitor */
    private FileConfigurationMonitor aclConfigurationMonitor_= null;

    /**
     * Constructor called by the factory.
     */
    public ShibbolethXMLAccessControlList() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.acl.ShibbolethAccessControlList#init(javax.servlet.FilterConfig)
     */
    public void init(FilterConfig filterConfig) throws SLCSException {
        String filename= filterConfig.getInitParameter("ACLFile");
        LOG.info("ACLFile=" + filename);
        if (filename == null || filename.equals("")) {
            throw new SLCSConfigurationException("Filter parameter ACLFile is missing or empty");
        }

        // load the XML file
        aclConfiguration_= createACLConfiguration(filename);

        // create the authorization attribute liste
        aclAuthorizationAttributeNames_= createACLAuthorizationAttributeNames(aclConfiguration_);

        // create ACL memory cache
        aclAttributesCache_= createACLAttributesCache(aclConfiguration_,
                                                      aclAuthorizationAttributeNames_);

        // deals with the FileConfigurationMonitor
        String monitoringInterval= filterConfig.getInitParameter("ACLFileMonitoringInterval");
        if (monitoringInterval != null) {
            LOG.info("ACLFileMonitoringInterval=" + monitoringInterval);
            aclConfigurationMonitor_= createACLConfigurationMonitor(aclConfiguration_,
                                                                    monitoringInterval,
                                                                    this);
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
        HashSet aclAuthorizationAttributeNames= new HashSet();
        // get all attribute names from configuration
        List aclAttributeNames= fileConfiguration.getList("AccessControlAttribute.AttributeName");
        Iterator names= aclAttributeNames.iterator();
        while (names.hasNext()) {
            String attributeName= (String) names.next();
            aclAuthorizationAttributeNames.add(attributeName);
        }
        return aclAuthorizationAttributeNames;
    }

    /**
     * Creates a FileConfigurationMonitor for the given FileConfiguration.
     * 
     * @param fileConfiguration
     *            The FileConfiguration associated with the file to monitor.
     * @param monitoringInterval
     *            The time (is seconds) between to check.
     * @param listener
     *            The FileConfigurationListener (this).
     * @return The new FileConfigurationMonitor object.
     */
    static private FileConfigurationMonitor createACLConfigurationMonitor(
            FileConfiguration fileConfiguration, String monitoringInterval,
            FileConfigurationListener listener) {
        // parse ACLFileMonitoringInterval parameter
        long interval= FileConfigurationMonitor.DEFAULT_MONITORING_INTERVAL;
        try {
            // interval is in seconds
            interval= Integer.parseInt(monitoringInterval);
            interval*= 1000;
        } catch (NumberFormatException e) {
            LOG.warn("ACLFileMonitoringInterval does not contain a valid interval time (second). Using default: "
                    + interval);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("file: " + fileConfiguration.getFileName()
                    + " interval: " + interval + ")");
        }

        // create the FileConfigurationMontitor
        FileConfigurationMonitor fileConfigurationMonitor= new FileConfigurationMonitor(fileConfiguration,
                                                                                        interval);
        fileConfigurationMonitor.addFileConfigurationListener(listener);
        return fileConfigurationMonitor;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.acl.ShibbolethAccessControlList#getAuthorizationAttributeNames()
     */
    public Set getAuthorizationAttributeNames() {
        return aclAuthorizationAttributeNames_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.acl.ShibbolethAccessControlList#isAuthorized(java.util.Map)
     */
    public boolean isAuthorized(Map attributes) {
        boolean authorized= false;
        Iterator attributeNames= attributes.keySet().iterator();
        while (!authorized && attributeNames.hasNext()) {
            String attributeName= (String) attributeNames.next();
            String attributeValue= (String) attributes.get(attributeName);
            if (LOG.isDebugEnabled()) {
                LOG.debug("checking " + attributeName + "=" + attributeValue);
            }
            if (aclAttributesCache_.containsKey(attributeName)) {
                Collection aclAttributeValues= (Collection) aclAttributesCache_.get(attributeName);
                if (aclAttributeValues.contains(attributeValue)) {
                    authorized= true;
                    LOG.info("User authorized with: " + attributeName + "="
                            + attributeValue);
                }
            }
        }
        if (!authorized) {
            LOG.warn("User not authorized: "  + attributes);
        }
        return authorized;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.acl.ShibbolethAccessControlList#shutdown()
     */
    public void shutdown() {
        // shutdown the FileConfigurationMonitor
        LOG.info("shutdown file monitor");
        if (aclConfigurationMonitor_ != null) {
            aclConfigurationMonitor_.removeFileConfigurationListener(this);
            aclConfigurationMonitor_.shutdown();
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
        aclAuthorizationAttributeNames_= createACLAuthorizationAttributeNames(aclConfiguration_);
        // recreate the ACL memory cache
        aclAttributesCache_= createACLAttributesCache(aclConfiguration_,
                                                      aclAuthorizationAttributeNames_);
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
        FileConfiguration config= null;
        try {
            LOG.info("XMLConfiguration file=" + filename);
            config= new XMLConfiguration(filename);
            if (LOG.isDebugEnabled()) {
                File configFile= config.getFile();
                LOG.debug("XMLConfiguration file="
                        + configFile.getAbsolutePath());
            }
        } catch (ConfigurationException e) {
            LOG.error("Failed to create XMLConfiguration: " + filename, e);
            throw new SLCSConfigurationException("Failed to create XMLConfiguration: "
                                                         + filename,
                                                 e);
        }
        return config;
    }

    /**
     * Create a memory cache as a Map(name,Set(values)).
     * 
     * The implementation uses an optimized TreeMap(name,TreeSet(values)), with
     * a log(n) x log(n) access time (polylogarithmic)
     * 
     * @return
     */
    static private Map createACLAttributesCache(
            FileConfiguration fileConfiguration, Set authorizationAttributes) {
        // create the optimized Map(name,Set(values))
        Map aclAttributesMap= new TreeMap();
        Iterator iterator= authorizationAttributes.iterator();
        while (iterator.hasNext()) {
            String key= (String) iterator.next();
            // put the key and an empty TreeSet in the Map
            Set emptySet= new TreeSet();
            aclAttributesMap.put(key, emptySet);
        }

        // populate the Set with attribute values
        int i= 0;
        while (true) {
            String attributeName= fileConfiguration.getString("AccessControlAttribute("
                    + i + ").AttributeName");
            if (attributeName == null) {
                // no more ACL attribute to read, exit while loop
                break;
            }
            String attributeValue= fileConfiguration.getString("AccessControlAttribute("
                    + i + ").AttributeValue");
            if (LOG.isDebugEnabled()) {
                LOG.debug("add value: " + attributeValue + " into: "
                        + attributeName);
            }
            Set attributeValueSet= (Set) aclAttributesMap.get(attributeName);
            attributeValueSet.add(attributeValue);

            // read next ACL attribute name-value
            i++;
        }
        // get all values
        return aclAttributesMap;
    }
}
