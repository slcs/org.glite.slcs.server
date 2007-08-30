/*
 * $Id: AccessControlRule.java,v 1.5 2007/08/30 11:41:35 vtschopp Exp $
 * 
 * Created on Aug 30, 2006 by Valery Tschopp <tschopp@switch.ch>
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html 
 */
package org.glite.slcs.acl;

import java.util.ArrayList;
import java.util.List;

import org.glite.slcs.attribute.Attribute;

/**
 * Access control rule is a list of attributes which define access rule if the
 * user attributes match all the rule attributes.
 * 
 * <pre>
 *  &lt;AccessControlRule id=&quot;2&quot; group=&quot;SWITCH&quot;&gt;
 *     &lt;Attribute name=&quot;Shib-SwissEP-HomeOrganization&quot;&gt;switch.ch&lt;/Attribute&gt;
 *     &lt;Attribute name=&quot;Shib-EP-Affiliation&quot;&gt;staff&lt;/Attribute&gt;
 *  &lt;/AccessControlRule&gt;
 * </pre>
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision: 1.5 $
 */
public class AccessControlRule {

    /** The rule id */
    private int id_ = -1;

    /** The rule group name */
    private String groupName_ = null;

    /** The attributes in the rule */
    private List attributes_ = null;

    /**
     * Constructor of a empty rule.
     * 
     * @param groupName
     *            The rule group name.
     */
    public AccessControlRule(String groupName) {
        this(-1, groupName);
    }

    /**
     * Constructor of a empty rule.
     * 
     * @param id
     *            The rule id.
     * @param groupName
     *            The rule group name.
     */
    public AccessControlRule(int id, String groupName) {
        id_ = id;
        groupName_ = groupName;
        attributes_ = new ArrayList();
    }

    /**
     * Returns the list of Shibboleth attributes in the rule.
     * 
     * @return The List of {@link Attribute}s
     * @see org.glite.slcs.attribute.Attribute
     */
    public List getAttributes() {
        return attributes_;
    }

    /**
     * Adds an attribute in the rule.
     * 
     * @param attribute
     *            The attribute to add.
     */
    public void addAttribute(Attribute attribute) {
        attributes_.add(attribute);
    }

    /**
     * Sets the attributes of the rule.
     * 
     * @param attributes
     *            A List of attributes.
     */
    public void setAttributes(List attributes) {
        attributes_ = attributes;
    }

    /**
     * Returns the group of the rule.
     * 
     * @return The group of the rule.
     */
    public String getGroupName() {
        return groupName_;
    }

    /**
     * Sets the group of the rule.
     * 
     * @param groupName
     *            The group name of the rule.
     */
    public void setGroupName(String groupName) {
        groupName_ = groupName;
    }

    /**
     * Returns the rule id.
     * 
     * @return The rule id. -1 if not defined.
     */
    public int getId() {
        return id_;
    }

    /**
     * Sets the rule id.
     * 
     * @param id
     *            The rule id.
     */
    public void setId(int id) {
        id_ = id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("AccessControlRule[");
        sb.append(id_);
        sb.append(":");
        sb.append(groupName_);
        sb.append(":");
        sb.append(attributes_);
        sb.append("]");
        return sb.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result
                + ((attributes_ == null) ? 0 : attributes_.hashCode());
        result = PRIME * result + ((groupName_ == null) ? 0 : groupName_.hashCode());
        result = PRIME * result + id_;
        return result;
    }

    /**
     * Checks if equals. The attributes order is also significatif.
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final AccessControlRule other = (AccessControlRule) obj;
        if (attributes_ == null) {
            if (other.attributes_ != null)
                return false;
        }
        else if (!attributes_.equals(other.attributes_))
            return false;
        if (groupName_ == null) {
            if (other.groupName_ != null)
                return false;
        }
        else if (!groupName_.equals(other.groupName_))
            return false;
        return true;
    }

}
