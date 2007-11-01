/*
 * $Id: GroupManager.java,v 1.5 2007/11/01 14:35:11 vtschopp Exp $
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html 
 */
package org.glite.slcs.group;

import java.util.List;

import org.glite.slcs.SLCSServerComponent;
import org.glite.slcs.attribute.Attribute;

/**
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision: 1.5 $
 */
public interface GroupManager extends SLCSServerComponent {

    /**
     * Returns the {@link Group} by its name.
     * 
     * @param name
     *            The group name.
     * @return The group or <code>null</code> if not found.
     */
    public Group getGroup(String name);

    /**
     * Returns the list of all {@link Group}s.
     * 
     * @return The list of {@link Group}.
     */
    public List getGroups();
    
    /**
     * Returns the list of {@link Group} where the user, identified by his
     * {@link Attribute}s list, belong to.
     * 
     * @param userAttributes
     *            The list of user {@link Attribute}.
     * @return The list of {@link Group}.
     */
    public List getGroups(List userAttributes);

    /**
     * Returns the list of all group names.
     * 
     * @return The list of group names.
     */
    public List getGroupNames();

    /**
     * Returns the list of group names where the user, identified by his
     * {@link Attribute}s list, belong.
     * 
     * @param userAttributes
     *            The list of user {@link Attribute}s.
     * @return The list of group names.
     */
    public List getGroupNames(List userAttributes);

    /**
     * Checks if the user, identified by his {@link Attribute}s list, belong to
     * the group identified by its name.
     * 
     * @param groupName
     *            The group name.
     * @param userAttributes
     *            The list of user {@link Attribute}s.
     * @return <code>true</code> iff the user belong to the group.
     */
    public boolean inGroup(String groupName, List userAttributes);

    /**
     * Checks if the user, identified by his {@link Attribute}s list, is an
     * administrator (like root).
     * 
     * @param userAttributes
     * @return <code>true</code> if the user belong to the administrator
     *         group.
     */
    public boolean isAdministrator(List userAttributes);
}
