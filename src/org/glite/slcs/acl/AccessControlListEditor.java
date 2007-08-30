/*
 * $Id: AccessControlListEditor.java,v 1.5 2007/08/30 11:41:35 vtschopp Exp $
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html 
 */
package org.glite.slcs.acl;

import java.util.List;

import org.glite.slcs.SLCSException;
import org.glite.slcs.SLCSServerComponent;
import org.glite.slcs.config.SLCSServerConfiguration;

/**
 * Interface for the Shibboleth access control list editor.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.5 $
 */
public interface AccessControlListEditor extends SLCSServerComponent {

    /**
     * @return The ACL absolute filename.
     */
    public String getACLFilename();

    /**
     * Return the list of all {@link AccessControlRule}s.
     * 
     * @return The list of all {@link AccessControlRule}s
     */
    public List getAccessControlRules();

    /**
     * Returns the list of {@link AccessControlRule}s for the given group name.
     * 
     * @param groupName
     *            The rules group name. Use <code>null</code> for all rules.
     * @return The list of {@link AccessControlRule}s for this group.
     */
    public List getAccessControlRules(String groupName);

    /**
     * Return the list of {@link AccessControlRule}s for the given list of
     * group names.
     * 
     * @param groupNames
     *            The list of group names.
     * @return The list of {@link AccessControlRule}s for these group names.
     */
    public List getAccessControlRules(List groupNames);

    /**
     * Gets the rule identified by its ID.
     * 
     * @param ruleId
     *            The rule ID.
     * @return the {@link AccessControlRule} identified by the rule ID or
     *         <code>null</code> if the rule was not found.
     */
    public AccessControlRule getAccessControlRule(int ruleId);

    /**
     * Adds an access control rule in the access control list.
     * 
     * @param rule
     *            The rule to add.
     * @return <code>true</code> iff the operation succeed.
     */
    public boolean addAccessControlRule(AccessControlRule rule);

    /**
     * Replaces the existing rule identified by the ID and group.
     * 
     * @param rule
     *            The new rule replacing the old one
     * @return <code>true</code> iff the operation succeed.
     */
    public boolean replaceAccessControlRule(AccessControlRule rule);

    /**
     * Removes a rule from the access control list and returns the result list
     * of rules.
     * 
     * @param ruleId
     *            The rule ID to remove.
     * @return <code>true</code> iff the operation succeed.
     */
    public boolean removeAccessControlRule(int ruleId);

    /**
     * Checks the configuration and initializes the ACL file referenced in the
     * configuration by the fileElementName.
     * 
     * @param config
     *            The {@link SLCSServerConfiguration} object.
     * @param fileElementName
     *            The name of the ACL file XML element definition.
     * @throws SLCSException
     *             If a configuratin or an initialization error occurs.
     */
    public void init(SLCSServerConfiguration config, String fileElementName)
            throws SLCSException;
}
