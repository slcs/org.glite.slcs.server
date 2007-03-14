/*
 * $Id: Group.java,v 1.2 2007/03/14 13:49:05 vtschopp Exp $
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html 
 */
package org.glite.slcs.group;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glite.slcs.Attribute;
import org.glite.slcs.acl.AccessControlRule;

/**
 * Named group defined by a list of {@link GroupMember}s. A user is a group
 * member when all his attributes match one of the group member attributes list.
 * An optional {@link AccessControlRule} constraint, defining the mandatory
 * {@link Attribute}s list required for each rule, can be added.
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision: 1.2 $
 */
public class Group {

    /** Logging */
    static private Log LOG = LogFactory.getLog(Group.class);

    /** Group name */
    private String name_ = null;

    /** Group members */
    private List members_ = null;

    /** AccessControlRule constraints */
    private List ruleConstraints_ = null;

    /**
     * Constructor of a named group without member and without rule constraint.
     * 
     * @param name
     *            The group name.
     */
    public Group(String name) {
        name_ = name;
        members_ = new ArrayList();
        ruleConstraints_ = new ArrayList();
    }

    /**
     * Constructor
     * 
     * @param name
     *            The group name.
     * @param members
     *            List of {@link GroupMember}s of the group.
     */
    public Group(String name, List members) {
        name_ = name;
        members_ = members;
    }

    /**
     * @return the list of {@link GroupMember}s.
     */
    public List getGroupMembers() {
        return members_;
    }

    /**
     * @param members
     *            set the list of {@link GroupMember}s.
     */
    public void setAttributes(List members) {
        members_ = members;
    }

    /**
     * Adds an {@link GroupMember} to the group members list.
     * 
     * @param member
     *            The member to add.
     */
    public void addGroupMember(GroupMember member) {
        members_.add(member);
    }

    /**
     * @return the group name
     */
    public String getName() {
        return name_;
    }

    /**
     * @param name
     *            the group name to set
     */
    public void setName(String name) {
        name_ = name;
    }

    /**
     * Checks if the user, identified by his {@link Attribute}s, is member of
     * this group.
     * 
     * @param userAttributes
     *            The user {@link Attribute}s
     * @return <code>true</code> if the user is member of the group.
     */
    public boolean isMember(List userAttributes) {
        Iterator iterator = members_.iterator();
        while (iterator.hasNext()) {
            GroupMember member = (GroupMember) iterator.next();
            List memberAttributes = member.getAttributes();
            if (userAttributes.containsAll(memberAttributes)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Group " + name_ + " membership: "
                            + memberAttributes);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the {@link AccessControlRule} constraints defined for this group.
     * 
     * @return the list of attributes defining the rule constraints. An empty
     *         list if no constaint are defined.
     */
    public List getRuleConstraints() {
        return ruleConstraints_;
    }

    /**
     * Adds an {@link Attribute} to the list of AccessControlRule constraints
     * defined for this group.
     * 
     * @param attribute
     *            The attribute to add as rule constraint.
     */
    public void addRuleConstraint(Attribute attribute) {
        ruleConstraints_.add(attribute);
    }

    /**
     * @param ruleConstraints
     *            the ruleConstraints to set
     */
    public void setRuleConstraints(List ruleConstraints) {
        ruleConstraints_ = ruleConstraints;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Group[");
        sb.append(name_).append(":");
        sb.append(members_);
        sb.append("]");
        return sb.toString();
    }

}
