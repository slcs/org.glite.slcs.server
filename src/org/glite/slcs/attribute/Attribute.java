/*
 * $Id: Attribute.java,v 1.4 2007/11/01 14:35:11 vtschopp Exp $
 * 
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html
 */
package org.glite.slcs.attribute;

/**
 * A simple Attribute is a name-value tuple.
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision: 1.4 $
 */
public class Attribute extends AttributeDefinition {

    /** The attribute value */
    String value_ = null;

    /**
     * <code>true</code> if the attribute is constrained by a rule constraint
     */
    private boolean constrained_ = false;

    /**
     * Constructor. Create a empty dummy attribute.
     */
    public Attribute() {
        super(null, null, null);
    }

    /**
     * Constructor
     * 
     * @param name
     *            The attribute name
     */
    protected Attribute(String name) {
        super(name, null, null);
    }

    /**
     * Constructor
     * 
     * @param name
     *            The attribute name
     * @param value
     *            The attribute value
     * @see AttributeDefinitions#createAttribute(String, String);
     */
    protected Attribute(String name, String value) {
        this(name);
        setValue(value);
    }

    /**
     * Sets the attribute value. Only not empty value will be set.
     * 
     * @param value
     *            the not empty value to set
     */
    public void setValue(String value) {
        value_ = null;
        if (value != null) {
            String newValue = value.trim();
            if (!newValue.equals("")) {
                value_ = newValue;
            }
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
        return value_ != null && !value_.equals("");
    }

    /**
     * @return <code>true</code> iff the name and the value is set
     */
    public boolean isValid() {
        return hasName() && hasValue();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.AttributeDefinition#toString()
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Attribute[");
        if (hasName()) {
            sb.append(getName());
        }
        else {
            sb.append("Undefined");
        }
        if (hasValue()) {
            sb.append("=").append(getValue());
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
     * Checks for equality. Two attributes are equal if the name and value are
     * equals. If the attribute is not case sensitive, then the value is
     * compared case insensitive.
     * 
     * @return <code>true</code> if the attribute name and value are equals.
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
        }
        else if (!name_.equals(other.name_))
            return false;
        if (value_ == null) {
            if (other.value_ != null)
                return false;
        }
        // case in/sensitive value checks
        if (caseSensitive_) {
            if (!value_.equals(other.value_))
                return false;
        }
        else {
            if (!value_.equalsIgnoreCase(other.value_))
                return false;
        }
        return true;
    }

    /**
     * @return <code>true</code> if the attribute is constrained by a group
     *         rule constraint.
     */
    public boolean isConstrained() {
        return this.constrained_;
    }

    /**
     * Sets the attribute constraint.
     * 
     * @param constrained
     */
    public void setConstrained(boolean constrained_) {
        this.constrained_ = constrained_;
    }

}
