/*
 * $Id: AccessControlRuleComparator.java,v 1.1 2007/10/05 08:33:12 vtschopp Exp $
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html 
 */
package org.glite.slcs.acl.impl;

import java.util.Comparator;

import org.glite.slcs.acl.AccessControlRule;

/**
 * Comparator to sort a list of AccessControlRule. The groupName and the groupId
 * are used as sorting key.
 * 
 * @see Collections#sort(java.util.List, Comparator)
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision: 1.1 $
 */
public class AccessControlRuleComparator implements Comparator {

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(Object obj1, Object obj2) {
        Class ruleClass = AccessControlRule.class;
        if (ruleClass != obj1.getClass() || ruleClass != obj2.getClass()) {
            throw new ClassCastException(
                    "Object are not of type AccessControlRule");
        }
        final AccessControlRule rule1 = (AccessControlRule) obj1;
        final AccessControlRule rule2 = (AccessControlRule) obj2;
        return compare(rule1, rule2);
    }

    /**
     * Compares 2 access control rule by their group name and id.
     * <p>
     * Note: this comparator imposes orderings that are inconsistent with equals
     * because the id is also checked.
     * 
     * @param rule1
     *            The first {@link AccessControlRule}.
     * @param rule2
     *            The second {@link AccessControlRule}.
     * @return
     */
    public int compare(AccessControlRule rule1, AccessControlRule rule2) {
        if (rule1 == rule2) {
            return 0;
        }
        String groupName1 = rule1.getGroupName();
        String groupName2 = rule2.getGroupName();
        int compareGroupName = groupName1.compareTo(groupName2);
        if (compareGroupName == 0) {
            // same group name, sort by ???
            int id1 = rule1.getId();
            int id2 = rule2.getId();
            if (id1 < id2)
                return -1;
            else if (id1 > id2)
                return 1;
            else
                return 0;
        }
        else {
            return compareGroupName;
        }
    }
}
