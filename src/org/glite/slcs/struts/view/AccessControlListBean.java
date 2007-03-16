/*
 * $Id: AccessControlListBean.java,v 1.1 2007/03/16 08:59:12 vtschopp Exp $
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html 
 */
package org.glite.slcs.struts.view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.glite.slcs.acl.AccessControlRule;
import org.glite.slcs.config.AttributeDefintionsHelper;

public class AccessControlListBean {

    private List rules_ = null;

    private String filename_ = null;

    public AccessControlListBean() {
        rules_ = new ArrayList();
    }

    public AccessControlListBean(List rules) {
        rules_ = rules;
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

    public void setAttributeDisplayNames(AttributeDefintionsHelper helper) {
        Iterator iterator= rules_.iterator();
        while (iterator.hasNext()) {
            AccessControlRule rule = (AccessControlRule) iterator.next();
            List ruleAttributes= rule.getAttributes();
            helper.setDisplayNames(ruleAttributes);
        }
    }
    
}
