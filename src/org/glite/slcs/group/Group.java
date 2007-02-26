package org.glite.slcs.group;

import java.util.ArrayList;
import java.util.List;

import org.glite.slcs.Attribute;

/**
 * Named group defined by a list of {@link Attribute}s. A user belong to a
 * group when all his attributes match the group attributes.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.1 $
 */
public class Group {

    /** Group name */
    private String name_ = null;

    /** List of attributes defining the group */
    private List attributes_ = null;

    /**
     * Constructor
     * 
     * @param name
     *            The group name.
     */
    public Group(String name) {
        name_ = name;
        attributes_ = new ArrayList();
    }

    /**
     * Constructor
     * 
     * @param name
     *            The group name.
     * @param attributes
     *            List of {@link Attribute}s defining the group.
     */
    public Group(String name, List attributes) {
        name_ = name;
        attributes_ = attributes;
    }

    /**
     * @return the attributes
     */
    public List getAttributes() {
        return attributes_;
    }

    /**
     * @param attributes
     *            the attributes to set
     */
    public void setAttributes(List attributes) {
        attributes_ = attributes;
    }

    /**
     * Adds an {@link Attribute} to the group attributes list.
     * 
     * @param attribute
     *            The attribute to add to the list.
     */
    public void addAttribute(Attribute attribute) {
        attributes_.add(attribute);
    }

    /**
     * @return the name
     */
    public String getName() {
        return name_;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        name_ = name;
    }

    /**
     * Checks if the user, identified by his {@link Attribute}s, belong to this
     * group.
     * 
     * @param userAttributes
     *            The user {@link Attribute}s
     * @return <code>true</code> if the user belong to this group.
     */
    public boolean matches(List userAttributes) {
        return userAttributes.containsAll(attributes_);
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
        sb.append(attributes_);
        sb.append("]");
        return sb.toString();
    }

}
