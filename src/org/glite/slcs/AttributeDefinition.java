package org.glite.slcs;

/**
 * Defines an Attribute with a name, a displayName and a required flag.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.2 $
 */
public class AttributeDefinition {

    /** The attribute name */
    protected String name_ = null;

    /** The attribute human-readable name */
    private String displayName_ = null;

    /** Is the attribute required */
    private boolean required_ = false;

    /**
     * Named constructor.
     * 
     * @param name
     *            The attribute name
     * @param displayName
     *            The attribute human-readable name
     */
    public AttributeDefinition(String name, String displayName) {
        setName(name);
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
    public void setDisplayName(String displayName) {
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
     * @return <code>true</code> iff the name is set
     */
    public boolean hasName() {
        return name_ != null && !name_.equals("");
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
    public void setRequired(boolean required) {
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
        } else {
            sb.append("Undefined");
        }
        if (displayName_ != null) {
            sb.append("(").append(displayName_).append(")");
        }
        sb.append("]");
        return sb.toString();
    }

}
