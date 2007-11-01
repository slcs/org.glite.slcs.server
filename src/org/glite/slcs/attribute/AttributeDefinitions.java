/*
 * $Id: AttributeDefinitions.java,v 1.3 2007/11/01 14:35:11 vtschopp Exp $
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html 
 */
package org.glite.slcs.attribute;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * Helper class for the attribute definitions as defined by the XML file
 * attribute-defs.xml.
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision: 1.3 $
 */
public interface AttributeDefinitions {

    /**
     * Creates an {@link Attribute} with the given name and value. The attribute
     * properties <code>required</code>, <code>displayName</code> and
     * <code>caseSensitive</code> are set as defined by the attribute
     * definitions.
     * 
     * @param name
     *            The attribute name.
     * @param value
     *            The attribute value.
     * @return The new attribute with all properties set.
     */
    public Attribute createAttribute(String name, String value);

    /**
     * Return the list of user's {@link Attribute}s, as defined in the
     * attribute definitions, from the HTTP request.
     * 
     * @param request
     *            The HttpServletRequest object
     * @return The list of user's attributes
     */
    public List getUserAttributes(HttpServletRequest request);

    /**
     * @return The list of {@link AttributeDefinition}
     */
    public List getAttributeDefinitions();

    /**
     * Gets the display name, as defined by the {@link AttributeDefinition}s,
     * for the given attribute. If the display name is not defined, the
     * attribute name is returned.
     * 
     * @param attribute
     *            The attribute.
     * @return The display name for this attribute
     */
    public String getAttributeDisplayName(Attribute attribute);

    /**
     * Sets the display name, as defined in the {@link AttributeDefinition}s,
     * for all attributes in the list.
     * 
     * @param attributes
     *            The list of attributes to set display name.
     */
    public void setDisplayNames(List attributes);

    /**
     * Returns a list of attribute names defined as required.
     * 
     * @return the list of required attribute name
     */
    public List getRequiredAttributeNames();

}