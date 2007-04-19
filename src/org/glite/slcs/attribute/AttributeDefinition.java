/*
 * $Id: AttributeDefinition.java,v 1.2 2007/03/19 09:05:53 vtschopp Exp $
 * 
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html
 */
package org.glite.slcs.attribute;

/**
 * Defines an Attribute with a name, a HTTP request header name, a displayName
 * and a required flag.
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision: 1.2 $
 */
public class AttributeDefinition {

    /** The attribute name */
    protected String name_ = null;

    /** The attribute HTTP request header name */
    private String header_ = null;

    /** The attribute human-readable name */
    private String displayName_ = null;

    /** Is the attribute required */
    private boolean required_ = false;

    /**
     * Named constructor.
     * 
     * @param name
     *            The attribute name
     * @param header
     *            The HTTP request header name
     * @param displayName
     *            The attribute human-readable name
     */
    public AttributeDefinition(String name, String header, String displayName) {
        setName(name);
        setHeader(header);
        setDisplayName(displayName);
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
    protected void setDisplayName(String displayName) {
        displayName_ = null;
        if (displayName != null) {
            displayName_ = displayName.trim();
        }
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
        name_ = null;
        if (name != null) {
            name_ = name.trim();
        }
    }

    /**
     * @return The attribute HTTP request header name
     */
    public String getHeader() {
        return header_;
    }

    /**
     * @param header
     *            the HTTP request header name to set
     */
    protected void setHeader(String header) {
        header_ = null;
        if (header != null) {
            header_ = header.trim();
        }
    }

    /**
     * @return <code>true</code> iff the name is set
     */
    public boolean hasName() {
        return name_ != null && !name_.equals("");
    }

    /**
     * @return <code>true</code> iff the header name is set
     */
    public boolean hasHeader() {
        return header_ != null && !header_.equals("");
    }

    /**
     * @return <code>true</code> iff the display name is set
     */
    public boolean hasDisplayName() {
        return displayName_ != null && !displayName_.equals("");
    }

    /**
     * @return the required
     */
    public boolean isRequired() {
        return required_;
    }

    /**
     * @param required
     *            the required to set
     */
    protected void setRequired(boolean required) {
        required_ = required;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("AttributeDefinition[");
        if (hasName()) {
            sb.append(name_);
        }
        else {
            sb.append("null");
        }
        if (hasHeader()) {
            sb.append(":").append(header_);
        }
        else {
            sb.append(":null");            
        }
        if (hasDisplayName()) {
            sb.append(":").append(displayName_);
        }
        else {
            sb.append(":null");            
        }
        if (isRequired()) {
            sb.append(":true");
        }
        else {
            sb.append(":false");
        }
        sb.append("]");
        return sb.toString();
    }

}