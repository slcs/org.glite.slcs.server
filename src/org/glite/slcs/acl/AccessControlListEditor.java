/*
 * $Id: AccessControlListEditor.java,v 1.1 2007/01/30 13:38:33 vtschopp Exp $
 * 
 * Created on Aug 18, 2006 by Valery Tschopp <tschopp@switch.ch>
 *
 * Copyright (c) 2006 SWITCH - http://www.switch.ch/
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
 * @version $Revision: 1.1 $
 */
public interface AccessControlListEditor extends SLCSServerComponent {

    public String getACLFilename();
    
    /**
     * Returns the access control rules for the given group.
     * 
     * @param group
     *            The rules group. Use <code>null</code> for all rules.
     * @return The list of {@link AccessControlRule}s for this group.
     * 
     * @see org.glite.slcs.acl.AccessControlRule
     */
    public List getAccessControlRules(String group);

    /**
     * Adds an access control rule in the access control list.
     * 
     * @param rule
     *            The rule to add.
     * @return The list of {@link AccessControlRule}s after the addition.
     */
    public List addAccessControlRule(AccessControlRule rule);

    /**
     * Removes a rule from the access control list.
     * 
     * @param rule
     *            The rule to remove.
     * @return The list of {@link AccessControlRule}s after the deletion.
     */
    public List removeAccessControlRule(AccessControlRule rule);

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
