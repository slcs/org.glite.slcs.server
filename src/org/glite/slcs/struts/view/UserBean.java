/*
 * $Id: UserBean.java,v 1.2 2007/06/11 13:10:59 vtschopp Exp $
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html 
 */
package org.glite.slcs.struts.view;

import java.util.List;

import org.glite.slcs.SLCSException;
import org.glite.slcs.attribute.Attribute;
import org.glite.slcs.group.Group;
import org.glite.slcs.group.GroupManager;
import org.glite.slcs.group.GroupManagerFactory;

public class UserBean {

    List attributes_ = null;
    List groups_ = null;
    GroupManager groupManager_ = null;
    
    boolean isAdministrator_ = false;

    public UserBean(List userAttributes) throws SLCSException {
        attributes_ = userAttributes;
        groupManager_= GroupManagerFactory.getInstance();
        isAdministrator_= groupManager_.isAdministrator(attributes_);
        if (isAdministrator_) {
            groups_= groupManager_.getGroups();
        }
        else {
            groups_= groupManager_.getGroups(attributes_);
        }
       
    }

    /**
     * @return the user {@link Attribute}s
     */
    public List getAttributes() {
        return attributes_;
    }

    /**
     * @return the user {@link Group}s
     */
    public List getGroups() {
        return groups_;
    }

    /**
     * @return <code>true</code> if the user is member of the admin group
     */
    public boolean isAdministrator() {
        return isAdministrator_;
    }

}
