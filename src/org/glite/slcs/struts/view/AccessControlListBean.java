/*
 * $Id: AccessControlListBean.java,v 1.2 2007/06/11 13:10:59 vtschopp Exp $
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html 
 */
package org.glite.slcs.struts.view;

import java.util.Iterator;
import java.util.List;

import org.glite.slcs.acl.AccessControlRule;
import org.glite.slcs.attribute.AttributeDefinitions;
import org.glite.slcs.config.SLCSServerConfiguration;

public class AccessControlListBean {

    private List rules_ = null;
    private String groupName_ = null;
    private String filename_ = null;

    public AccessControlListBean(List rules) {
        rules_ = rules;
        setRuleAttributesDisplayNames();
    }

    public List getAccessControlRules() {
        return rules_;
    }

    public void setAccessControlRules(List rules) {
        rules_ = rules;
    }

    public void setFilename(String filename) {
        filename_ = filename;
    }

    public String getFilename() {
        return filename_;
    }
    
    public void setGroupName(String groupName) {
        groupName_= groupName;
    }
    
    public String getGroupName() {
        return groupName_;
    }
    
    
    
    private void setRuleAttributesDisplayNames() {
        // set the DisplayName for all attributes in all rules
        SLCSServerConfiguration config = SLCSServerConfiguration.getInstance();
        AttributeDefinitions attributeDefinitions = config.getAttributeDefinitions();
        Iterator iter= rules_.iterator();
        while (iter.hasNext()) {
            AccessControlRule rule = (AccessControlRule) iter.next();
            List ruleAttributes= rule.getAttributes();
            attributeDefinitions.setDisplayNames(ruleAttributes);
        }        
    }
}
