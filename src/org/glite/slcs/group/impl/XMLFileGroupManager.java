/*
 * $Id: XMLFileGroupManager.java,v 1.4 2007/03/14 13:56:25 vtschopp Exp $
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html 
 */
package org.glite.slcs.group.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glite.slcs.Attribute;
import org.glite.slcs.SLCSConfigurationException;
import org.glite.slcs.SLCSException;
import org.glite.slcs.config.FileConfigurationEvent;
import org.glite.slcs.config.FileConfigurationListener;
import org.glite.slcs.config.FileConfigurationMonitor;
import org.glite.slcs.config.SLCSServerConfiguration;
import org.glite.slcs.group.Group;
import org.glite.slcs.group.GroupManager;
import org.glite.slcs.group.GroupMember;

/**
 * GroupManager implementation, based on XML file.
 * 
 * TODO: describe XML format
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.4 $
 */
public class XMLFileGroupManager implements GroupManager,
        FileConfigurationListener {

    /** Logging */
    static private Log LOG = LogFactory.getLog(XMLFileGroupManager.class);

    /** Name of the special administrator group */
    static private String DEFAULT_ADMIN_GROUPNAME = "admin";

    /** XML file */
    private XMLConfiguration groupsConfiguration_ = null;

    /** Name of the sepcial admin group */
    private String adminGroupName_ = DEFAULT_ADMIN_GROUPNAME;

    /** List of {@link Group}s */
    private List groups_ = null;

    /** File change monitor */
    private FileConfigurationMonitor groupsFileMonitor_ = null;

    /**
     * Loads the groups XMLConfiguration file.
     * 
     * @param filename
     *            The groups XML filename to load.
     * @return The loaded {@link XMLConfiguration} object.
     * @throws SLCSConfigurationException
     *             If an configration error occurs while loading the file.
     */
    static private XMLConfiguration createGroupsConfiguration(String filename)
            throws SLCSConfigurationException {
        XMLConfiguration config = null;
        try {
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
     * Creates the list of groups defined in the FileConfiguration.
     * 
     * @param config
     *            The FileConfiguration object.
     * @return The list of {@link Group}s.
     */
    static private List createGroups(FileConfiguration config) {
        List groups = new LinkedList();
        // list all groups
        int i = 0;
        boolean moreGroup = true;
        while (moreGroup) {
            String groupPrefix = "Group(" + i + ")";
            i++;
            // get the name of the group
            String groupName = config.getString(groupPrefix + "[@name]");
            if (groupName != null) {
                // create an empty named group
                Group group = new Group(groupName);
                // list all members of this group
                int j = 0;
                boolean moreMember = true;
                while (moreMember) {
                    String memberPrefix = groupPrefix + ".GroupMember(" + j
                            + ")";
                    j++;
                    // get the attributes name and value for the group member
                    List attributeNames = config.getList(memberPrefix
                            + ".Attribute[@name]");
                    if (!attributeNames.isEmpty()) {
                        // create an empty member
                        GroupMember member = new GroupMember();
                        // list and add the attributes
                        List attributeValues = config.getList(memberPrefix
                                + ".Attribute");
                        for (int k = 0; k < attributeNames.size(); k++) {
                            String name = (String) attributeNames.get(k);
                            String value = (String) attributeValues.get(k);
                            Attribute attribute = new Attribute(name, value);
                            // add attribute to the group membership
                            member.addAttribute(attribute);
                        }
                        // add the member to the group
                        group.addGroupMember(member);
                    }
                    else {
                        moreMember = false;
                        if (LOG.isDebugEnabled()) {
                            LOG.debug(memberPrefix + ": no more group members");
                        }
                    }
                } // while all members

                // get all constrained attributes for the group ACL rules
                String constraintPrefix = groupPrefix
                        + ".AccessControlRuleConstraint";
                // get the attributes name and value for the group ACL rule
                // constraint
                List attributeNames = config.getList(constraintPrefix
                        + ".Attribute[@name]");
                if (!attributeNames.isEmpty()) {
                    // list and add the ACL rule attributes constraints
                    List attributeValues = config.getList(constraintPrefix
                            + ".Attribute");
                    for (int k = 0; k < attributeNames.size(); k++) {
                        String name = (String) attributeNames.get(k);
                        String value = (String) attributeValues.get(k);
                        Attribute attribute = new Attribute(name, value);
                        // add attribute to the group ACL rule constraint list
                        group.addRuleConstraint(attribute);
                    }
                }

                // add the group to the list
                // TODO: check for group without members
                groups.add(group);

            }
            else {
                moreGroup = false;
                if (LOG.isDebugEnabled()) {
                    LOG.debug(groupPrefix + ": no more groups");
                }
            }

        } // while all groups

        return groups;
    }

    /**
     * Default constructor called only by the factory.
     */
    public XMLFileGroupManager() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.group.GroupManager#getGroups(java.util.List)
     */
    public List getGroups(List userAttributes) {
        List userGroups = new ArrayList();
        Iterator iter = groups_.iterator();
        while (iter.hasNext()) {
            Group group = (Group) iter.next();
            if (group.isMember(userAttributes)) {
                userGroups.add(group);
            }
        }
        return userGroups;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.group.GroupManager#getGroups(java.util.List)
     */
    public List getGroupNames(List userAttributes) {
        List groupNames = new ArrayList();
        Iterator iter = groups_.iterator();
        while (iter.hasNext()) {
            Group group = (Group) iter.next();
            if (group.isMember(userAttributes)) {
                String groupName = group.getName();
                groupNames.add(groupName);
            }
        }
        // sort the group names list
        Collections.sort(groupNames);

        return groupNames;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.group.GroupManager#inGroup(java.lang.String,
     *      java.util.List)
     */
    public boolean inGroup(String groupName, List userAttributes) {
        Iterator iter = groups_.iterator();
        while (iter.hasNext()) {
            Group group = (Group) iter.next();
            String name = group.getName();
            if (name.equals(groupName)) {
                if (group.isMember(userAttributes)) {
                    return true;
                }
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.group.GroupManager#isAdministrator(java.util.List)
     */
    public boolean isAdministrator(List userAttributes) {
        return inGroup(adminGroupName_, userAttributes);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.group.GroupManager#getGroup(java.lang.String)
     */
    public Group getGroup(String name) {
        Iterator iter = groups_.iterator();
        while (iter.hasNext()) {
            Group group = (Group) iter.next();
            String groupName = group.getName();
            if (groupName.equals(name)) {
                return group;
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.config.FileConfigurationListener#fileConfigurationChanged(org.glite.slcs.config.FileConfigurationEvent)
     */
    public void fileConfigurationChanged(FileConfigurationEvent event) {
        if (event.getType() == FileConfigurationEvent.FILE_MODIFIED) {
            LOG.debug("reload XML groups configuration");
            reloadGroupsConfiguration();
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.SLCSServerComponent#init(org.glite.slcs.config.SLCSServerConfiguration)
     */
    public void init(SLCSServerConfiguration config) throws SLCSException {
        // read config param from SLCSServerConfiguration
        String groupsFile = config.getString(SLCSServerConfiguration.COMPONENTSCONFIGURATION_PREFIX
                + ".GroupManager.GroupsFile");
        LOG.info("GroupsFile=" + groupsFile);
        String groupsFileMonitoringInterval = config.getString(SLCSServerConfiguration.COMPONENTSCONFIGURATION_PREFIX
                + ".GroupManager.GroupsFileMonitoringInterval");
        LOG.info("GroupsFileMonitoringInterval=" + groupsFileMonitoringInterval);
        // get admin group name
        adminGroupName_ = config.getString(SLCSServerConfiguration.COMPONENTSCONFIGURATION_PREFIX
                + "GroupManager.AdministratorGroup", false);
        if (adminGroupName_ == null) {
            adminGroupName_ = DEFAULT_ADMIN_GROUPNAME;
        }
        LOG.info("AdministratorGroup=" + adminGroupName_);

        // create the XML group configuration file
        groupsConfiguration_ = createGroupsConfiguration(groupsFile);
        // create the groups list
        groups_ = createGroups(groupsConfiguration_);

        // create and start the file monitor
        groupsFileMonitor_ = FileConfigurationMonitor.createFileConfigurationMonitor(groupsConfiguration_, groupsFileMonitoringInterval, this);
        groupsFileMonitor_.start();
    }

    /**
     * Reloads the {@link XMLConfiguration} file
     */
    private synchronized void reloadGroupsConfiguration() {
        LOG.info("reload file: " + groupsConfiguration_.getFileName());
        // reload the FileConfiguration
        groupsConfiguration_.reload();
        // recreate the groups list
        groups_ = createGroups(groupsConfiguration_);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.SLCSServerComponent#shutdown()
     */
    public void shutdown() {
        if (groupsFileMonitor_ != null) {
            LOG.info("shutdown XML groups file monitor");
            groupsFileMonitor_.removeFileConfigurationListener(this);
            groupsFileMonitor_.shutdown();
            groupsFileMonitor_ = null;
        }
    }

}
