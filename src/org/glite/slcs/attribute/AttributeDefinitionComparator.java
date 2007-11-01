/*
 * $Id: AttributeDefinitionComparator.java,v 1.3 2007/11/01 14:35:11 vtschopp Exp $
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html 
 */
package org.glite.slcs.attribute;

import java.util.Comparator;

/**
 * Comparator to sort a list of AttributeDefinition. The displayName (human
 * readable) is used as sorting key.
 * 
 * @see Collections#sort(java.util.List, Comparator)
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision: 1.3 $
 */
public class AttributeDefinitionComparator implements Comparator {

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(Object obj1, Object obj2) {
        Class attributeClass = AttributeDefinition.class;
        if (attributeClass != obj1.getClass()
                || attributeClass != obj2.getClass()) {
            throw new ClassCastException(
                    "Object are not of type AttributeDefinition");
        }
        final AttributeDefinition attribute1 = (AttributeDefinition) obj1;
        final AttributeDefinition attribute2 = (AttributeDefinition) obj2;
        return compare(attribute1, attribute2);
    }

    /**
     * Compare two AttributeDefinition Display name
     */
    public int compare(AttributeDefinition attribute1,
            AttributeDefinition attribute2) {
        if (attribute1 == attribute2) {
            return 0;
        }
        String displayName1 = attribute1.getDisplayName();
        String displayName2 = attribute2.getDisplayName();
        return displayName1.compareTo(displayName2);
    }
}
