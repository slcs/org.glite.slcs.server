/*
 * $Id: AccessControlRuleBean.java,v 1.1 2007/03/16 08:59:12 vtschopp Exp $
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html 
 */
package org.glite.slcs.struts.view;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glite.slcs.Attribute;
import org.glite.slcs.config.SLCSServerConfiguration;

public class AccessControlRuleBean {

    static private Log LOG = LogFactory.getLog(AccessControlRuleBean.class);
    
    int ruleId_= -1;
    String ruleGroup_= null;
    List ruleAttributes_ = null;
    List userGroups_= null;
    
    public AccessControlRuleBean() {
        ruleAttributes_= new ArrayList();
    }

    /**
     * @return the rule attributes
     */
    public List getAttributes() {
        return ruleAttributes_;
    }

    public void setId(int ruleId) {
        ruleId_= ruleId;
    }
    
    public void setId(String ruleId) {
        ruleId_= -1;
        try {
            ruleId_ = Integer.parseInt(ruleId);
        } catch (Exception e) {
            LOG.error(e);
        }        
    }
    
    public int getId() {
        return ruleId_;
    }
    
    public String getGroup() {
        return ruleGroup_;
    }

    public void setGroup(String group) {
        ruleGroup_= group;
    }
    
    public List getUserGroups() {
        return userGroups_;
    }

    public void setUserGroups(List groups) {
        userGroups_= groups;
    }
    
    public List getAttributeDefinitions() {
        SLCSServerConfiguration config = SLCSServerConfiguration.getInstance();
        List attributeDefinitions = config.getAttributeDefinitions();
        return attributeDefinitions;
// add a 'Select Attribute...' in top of the drop down list
//        AttributeDefinition select= new AttributeDefinition("","Select Attribute...");
//        LinkedList list= new LinkedList(attributeDefinitions);
//        list.addFirst(select);
//        if (LOG.isDebugEnabled()) {
//            LOG.debug("attributeDefinitions=" + list);
//        }
//        return list;
    }

    public void setAttributes(List attributes) {
        ruleAttributes_= attributes;
    }
    
    public void addAttributes(List attributes) {
        ruleAttributes_.addAll(attributes);
    }

    public void addEmptyAttribute() {
        ruleAttributes_.add( new Attribute(null) );
    }

}
