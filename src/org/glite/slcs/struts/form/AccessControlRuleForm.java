/*
 * $Id: AccessControlRuleForm.java,v 1.1 2007/03/16 08:59:33 vtschopp Exp $
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html 
 */
package org.glite.slcs.struts.form;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.Factory;
import org.apache.commons.collections.list.LazyList;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.glite.slcs.Attribute;

public class AccessControlRuleForm extends ActionForm implements Factory {

    /**
     * Serial version id
     */
    private static final long serialVersionUID = -1339375224629253586L;

    /**
     * Rule group
     */
    private String group_ = null;

    /**
     * Rule id
     */
    private String id_ = null;

    /**
     * Rule attributes (Auto growing lazy list)
     */
    private List attributes_ = null;

    public AccessControlRuleForm() {
        attributes_= LazyList.decorate(new ArrayList(), this);
    }

    /**
     * Returns the {@link Attribute} at the given index. The list will grow as
     * needed.
     * 
     * @param i
     *            The index
     * @return the Attribute
     */
    public Attribute getAttribute(int i) {
        return (Attribute) attributes_.get(i);
    }

    
    /**
     * @return the attributes
     */
    public List getAttributes() {
        return attributes_;
    }


    /**
     * @return the group
     */
    public String getGroup() {
        return group_;
    }

    /**
     * @param group
     *            the group to set
     */
    public void setGroup(String group) {
        group_ = group;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id_;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(String id) {
        id_ = id;
    }

    /**
     * Factory method for the {@link LazyList} to auto grow
     */
    public Object create() {
        return new Attribute(null);
    }

    /* (non-Javadoc)
     * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
     */
    public void reset(ActionMapping arg0, HttpServletRequest arg1) {
        super.reset(arg0, arg1);
        attributes_= null;
        attributes_ = LazyList.decorate(new ArrayList(), this);
    }

    
    
    
}
