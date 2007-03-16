/*
 * $Id: AttributeDefinitions.java,v 1.1 2007/03/16 14:33:23 vtschopp Exp $
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html 
 */
package org.glite.slcs.attribute;

import java.util.List;

/**
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision: 1.1 $
 */
public interface AttributeDefinitions {

    /**
     * @return The list of {@link AttributeDefinition}s
     */
    public abstract List getAttributeDefinitions();

    /**
     * Gets the display name, as defined by the {@link AttributeDefinition}s,
     * for the given attribute. If the display name is not defined, the
     * attribute name is returned.
     * 
     * @param attribute
     *            The attribute.
     * @return The display name for this attribute
     */
    public abstract String getDisplayName(Attribute attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public abstract String getHeader(Attribute attribute);
    
    /**
     * Checks if the attribute is required as defined by the attribute
     * definitions.
     * 
     * @param attribute
     *            The attribute to check
     * @return <code>true</code> if the attribute is required
     */
    public abstract boolean isRequired(Attribute attribute);

    /**
     * Sets the display name, as defined in the {@link AttributeDefinition}s,
     * for all attributes in the list.
     * 
     * @param attributes
     *            The list of attributes to set display name.
     */
    public abstract void setDisplayNames(List attributes);

    /**
     * @return the definedAttributeHeaders
     */
    public abstract List getDefinedAttributeHeaders();

    /**
     * @return the requiredAttributeHeaders
     */
    public abstract List getRequiredAttributeHeaders();

}