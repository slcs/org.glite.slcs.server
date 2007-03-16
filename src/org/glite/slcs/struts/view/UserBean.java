/*
 * $Id: UserBean.java,v 1.1 2007/03/16 08:59:12 vtschopp Exp $
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html 
 */
package org.glite.slcs.struts.view;

import java.util.ArrayList;
import java.util.List;

public class UserBean {

    List attributes_ = null;

    List groups_ = null;

    boolean isAdministrator_ = false;

    public UserBean() {
        attributes_ = new ArrayList();
        groups_ = new ArrayList();
    }

    /**
     * @return the attributes
     */
    public List getAttributes() {
        return attributes_;
    }

    /**
     * @param attributes
     *            the attributes to set
     */
    public void setAttributes(List attributes) {
        attributes_ = attributes;
    }

    /**
     * @return the groups
     */
    public List getGroups() {
        return groups_;
    }

    /**
     * @param groups
     *            the groups to set
     */
    public void setGroups(List groups) {
        groups_ = groups;
    }

    /**
     * Sets the admin flag
     * 
     * @param isAdmin
     */
    public void setAdministrator(boolean isAdmin) {
        isAdministrator_ = isAdmin;
    }

    /**
     * @return <code>true</code> if the user is member of the admin group
     */
    public boolean isAdministrator() {
        return isAdministrator_;
    }

}
