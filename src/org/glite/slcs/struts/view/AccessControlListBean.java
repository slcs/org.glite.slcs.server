/*
 * $Id: AccessControlListBean.java,v 1.3 2007/11/01 14:32:46 vtschopp Exp $
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html 
 */
package org.glite.slcs.struts.view;

import java.util.List;

public class AccessControlListBean {

    private List rules_ = null;
    private String groupName_ = null;
    private String filename_ = null;

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
    
    public void setGroupName(String groupName) {
        groupName_= groupName;
    }
    
    public String getGroupName() {
        return groupName_;
    }
        
}
