/*
 * $Id: AttributeDefinitions.java,v 1.2 2007/03/19 09:05:53 vtschopp Exp $
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html 
 */
package org.glite.slcs.attribute;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision: 1.2 $
 */
public interface AttributeDefinitions {

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
     * @return The list of {@link AttributeDefinition}s
     */
    public List getAttributeDefinitionsList();

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
     * Returns the HTTP request header name for the given attribute
     * 
     * @param attribute
     *            The Attribute object
     * @return The header name.
     */
    public String getAttributeHeader(Attribute attribute);

    /**
     * Returns the defined attribute name corresponding to the given header
     * name.
     * 
     * @param header
     *            The HTTP request header name.
     * @return The defined attribute name or <code>null</code> if not defined.
     */
    public String getAttributeName(String header);

    /**
     * Checks if the attribute is required as defined by the attribute
     * definitions.
     * 
     * @param attribute
     *            The attribute to check
     * @return <code>true</code> if the attribute is required
     */
    public boolean isAttributeRequired(Attribute attribute);

    /**
     * Sets the display name, as defined in the {@link AttributeDefinition}s,
     * for all attributes in the list.
     * 
     * @param attributes
     *            The list of attributes to set display name.
     */
    public void setDisplayNames(List attributes);

    /**
     * Returns the mapping between HTTP request header name and Attribute name.
     * 
     * @return the map of header - name
     */
    public Map getAttributesHeaderNameMapping();

    /**
     * Returns a list of attribute names defined as required.
     * 
     * @return the list of required attribute name
     */
    public List getRequiredAttributeNames();

}