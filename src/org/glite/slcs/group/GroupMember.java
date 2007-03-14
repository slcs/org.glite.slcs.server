/*
 * $Id: GroupMember.java,v 1.1 2007/03/14 13:49:05 vtschopp Exp $
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html 
 */
package org.glite.slcs.group;

import java.util.ArrayList;
import java.util.List;

import org.glite.slcs.Attribute;

/**
 * GroupMember defines the membership {@link Attribute}s list needed to be
 * member of a group.
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision: 1.1 $
 */
public class GroupMember {

    /**
     * List of attributes defining the group membership
     */
    private List attributes_ = null;

    /**
     * Default constructor
     */
    public GroupMember() {
        attributes_ = new ArrayList();
    }

    /**
     * Constructor with an {@link Attribute} list.
     * 
     * @param attributes
     *            The attributes list which define the membership.
     */
    public GroupMember(List attributes) {
        attributes_ = attributes;
    }

    /**
     * Returns the list of {@link Attribute}s defining the group membership.
     * 
     * @return the attributes
     */
    public List getAttributes() {
        return attributes_;
    }

    /**
     * Sets the {@link Attribute}s list defining the group membership.
     * 
     * @param attributes
     *            the attributes to set
     */
    public void setAttributes(List attributes) {
        attributes_ = attributes;
    }

    /**
     * Adds an {@link Attribute} to the group membership definition.
     * 
     * @param attribute
     *            The attribute to add.
     */
    public void addAttribute(Attribute attribute) {
        attributes_.add(attribute);
    }
}
