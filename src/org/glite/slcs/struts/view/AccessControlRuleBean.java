/*
 * $Id: AccessControlRuleBean.java,v 1.3 2007/11/01 14:32:46 vtschopp Exp $
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html 
 */
package org.glite.slcs.struts.view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glite.slcs.attribute.Attribute;
import org.glite.slcs.attribute.AttributeDefinition;
import org.glite.slcs.attribute.AttributeDefinitions;
import org.glite.slcs.config.SLCSServerConfiguration;

public class AccessControlRuleBean {

    static private Log LOG = LogFactory.getLog(AccessControlRuleBean.class);

    int ruleId_ = -1;

    String ruleGroupName_ = null;

    List ruleAttributes_ = null;

    List userGroupNames_ = null;

    AttributeDefinitions attributeDefinitions_ = null;

    public AccessControlRuleBean() {
        ruleAttributes_ = new ArrayList();
        // get the attribute definitions from the SLCS server configuration
        SLCSServerConfiguration config = SLCSServerConfiguration.getInstance();
        attributeDefinitions_ = config.getAttributeDefinitions();
    }

    /**
     * @return the rule attributes
     */
    public List getAttributes() {
        return ruleAttributes_;
    }

    public void setId(int ruleId) {
        ruleId_ = ruleId;
    }

    public void setId(String ruleId) {
        ruleId_ = -1;
        try {
            ruleId_ = Integer.parseInt(ruleId);
        } catch (Exception e) {
            LOG.error(e);
        }
    }

    public int getId() {
        return ruleId_;
    }

    public String getGroupName() {
        return ruleGroupName_;
    }

    public void setGroupName(String groupName) {
        ruleGroupName_ = groupName;
    }

    public List getUserGroupNames() {
        return userGroupNames_;
    }

    public void setUserGroupNames(List groupNames) {
        userGroupNames_ = groupNames;
    }

    /**
     * Used by createRule.jsp and editRule.jsp for the attribute drop-down list.
     * 
     * @return The list of {@link AttributeDefinition}s.
     */
    public List getAttributeDefinitions() {
        return attributeDefinitions_.getAttributeDefinitions();
        // add a 'Select Attribute...' in top of the drop down list
        // AttributeDefinition select= new AttributeDefinition("","Select
        // Attribute...");
        // LinkedList list= new LinkedList(attributeDefinitions);
        // list.addFirst(select);
        // if (LOG.isDebugEnabled()) {
        // LOG.debug("attributeDefinitions=" + list);
        // }
        // return list;
    }

    public void setAttributes(List attributes) {
        ruleAttributes_ = attributes;
    }

    public void addAttributes(List attributes) {
        ruleAttributes_.addAll(attributes);
    }

    public void addConstrainedAttributes(List attributes) {
        Iterator iter = attributes.iterator();
        while (iter.hasNext()) {
            Attribute attribute = (Attribute) iter.next();
            if (!ruleAttributes_.contains(attribute)) {
                // add the constrained attribute to the list
                ruleAttributes_.add(attribute);
            }
            else {
                // set the required flag
                int i = ruleAttributes_.indexOf(attribute);
                Attribute a = (Attribute) ruleAttributes_.get(i);
                a.setConstrained(true);
            }
        }
    }

    public void addEmptyAttribute() {
        ruleAttributes_.add(new Attribute());
    }

    public void updateAttributesDiplayName() {
        attributeDefinitions_.setDisplayNames(ruleAttributes_);
    }

}
