/*
 * $Id: Attribute.java,v 1.1 2007/01/30 13:34:14 vtschopp Exp $
 * 
 * Created on Aug 18, 2006 by Valery Tschopp <tschopp@switch.ch>
 *
 * Copyright (c) 2006 SWITCH - http://www.switch.ch/
 */
package org.glite.slcs;

/**
 * Shibobleth Attribute is a name-value tuple.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.1 $
 */
public class Attribute {

    /** The attribute name */
    private String name_ = null;

    /** The attribute human-readable name */
    private String displayName_ = null;

    /** The attribute value */
    private String value_ = null;

    /**
     * Constructor
     * 
     * @param name
     *            The attribute name
     */
    public Attribute(String name) {
        this.name_ = name;
    }

    /**
     * Constructor
     * 
     * @param name
     *            The attribute name
     * @param value
     *            The attribute value
     */
    public Attribute(String name, String value) {
        this.name_ = name;
        this.value_ = value;
    }

    /**
     * Returns the human-readable name
     * 
     * @return the display name
     */
    public String getDisplayName() {
        return displayName_;
    }

    /**
     * @param displayName
     *            the human-readable name to set
     */
    public void setDisplayName(String displayName) {
        displayName_ = displayName;
    }

    /**
     * @return The attribute name
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
     * Sets the attribute value. Only not empty value will be set.
     * 
     * @param value
     *            the not empty value to set
     */
    public void setValue(String value) {
        if (!value.equals("")) {
            value_ = value;
        }
    }

    /**
     * @return The attribute value
     */
    public String getValue() {
        return value_;
    }

    /**
     * @return <code>true</code> iff the value is set
     */
    public boolean hasValue() {
        return value_ != null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Attribute[");
        sb.append(name_);
        if (displayName_!=null) {
            sb.append(" (").append(displayName_).append(")");
        }
        if (hasValue()) {
            sb.append("=").append(value_);
        }
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
        result = PRIME * result + ((name_ == null) ? 0 : name_.hashCode());
        result = PRIME * result + ((value_ == null) ? 0 : value_.hashCode());
        return result;
    }

    /**
     * Checks for equality. Two attributes are equal iff they have the same name
     * and value.
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
        final Attribute other = (Attribute) obj;
        if (name_ == null) {
            if (other.name_ != null)
                return false;
        } else if (!name_.equals(other.name_))
            return false;
        if (value_ == null) {
            if (other.value_ != null)
                return false;
        } else if (!value_.equals(other.value_))
            return false;
        return true;
    }

}
