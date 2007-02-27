package org.glite.slcs.config;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.glite.slcs.Attribute;
import org.glite.slcs.AttributeDefinition;

/**
 * Helper class for the AttributeDefintions.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.1 $
 */
public class AttributeDefintionsHelper {

    /** Map of name - {@link AttributeDefinition}s */
    Map attributeDefinitions_ = null;

    /**
     * Constructor.
     * 
     * @param attributeDefinitions
     *            The list of {@link AttributeDefinition}s.
     */
    protected AttributeDefintionsHelper(List attributeDefinitions) {
        attributeDefinitions_ = new HashMap();
        Iterator iter = attributeDefinitions.iterator();
        while (iter.hasNext()) {
            AttributeDefinition attrDef = (AttributeDefinition) iter.next();
            attributeDefinitions_.put(attrDef.getName(), attrDef);
        }
    }

    /**
     * Gets the display name, as defined by the {@link AttributeDefinition}s,
     * for the given attribute. If the display name is not defined, the
     * attribute name is returned.
     * 
     * @param attribute
     *            The attribute.
     * @return The display name for this attribute
     */
    public String getDisplayName(Attribute attribute) {
        String attrName = attribute.getName();
        if (attributeDefinitions_.containsKey(attrName)) {
            AttributeDefinition attrDef = (AttributeDefinition) attributeDefinitions_.get(attrName);
            return attrDef.getDisplayName();
        }
        else if (attribute.hasDisplayName()) {
            return attribute.getDisplayName();
        }
        else {
            return attrName;
        }
    }

    /**
     * Checks if the attribute is required as defined by the attribute
     * definitions.
     * 
     * @param attribute
     *            The attribute to check
     * @return <code>true</code> if the attribute is required
     */
    public boolean isRequired(Attribute attribute) {
        String attrName = attribute.getName();
        if (attributeDefinitions_.containsKey(attrName)) {
            AttributeDefinition attrDef = (AttributeDefinition) attributeDefinitions_.get(attrName);
            return attrDef.isRequired();
        }
        else {
            return false;
        }
    }

    /**
     * Sets the display name, as defined in the {@link AttributeDefinition}s,
     * for all attributes in the list.
     * 
     * @param attributes
     *            The list of attributes to set display name.
     */
    public void setDisplayNames(List attributes) {
        Iterator iter = attributes.iterator();
        while (iter.hasNext()) {
            Attribute attr = (Attribute) iter.next();
            String displayName = getDisplayName(attr);
            attr.setDisplayName(displayName);
        }
    }

}
