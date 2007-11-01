/*
 * $Id: AccessControlRuleForm.java,v 1.3 2007/11/01 14:32:46 vtschopp Exp $
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html 
 */
package org.glite.slcs.struts.form;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.Factory;
import org.apache.commons.collections.list.LazyList;
import org.apache.struts.action.ActionForm;
import org.glite.slcs.attribute.Attribute;

public class AccessControlRuleForm extends ActionForm implements Factory {

    /**
     * Serial version id
     */
    private static final long serialVersionUID = -1339375224629253586L;

    /**
     * Rule group
     */
    private String groupName_ = null;

    /**
     * Rule id
     */
    private int id_ = -1;

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
     * Checks if the attributes are valid
     * @return a list of valid Attribute
     */
    public List getValidAttributes() {
        List validAttributes= new ArrayList();
        Iterator iter= attributes_.iterator();
        while (iter.hasNext()) {
            Attribute attribute = (Attribute) iter.next();
            if (attribute.isValid()) {
                validAttributes.add(attribute);
            }
        }
        return validAttributes;
    }
    
    /**
     * @return the group
     */
    public String getGroupName() {
        return groupName_;
    }

    /**
     * @param group
     *            the group to set
     */
    public void setGroupName(String groupName) {
        groupName_ = groupName;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id_;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(int id) {
        id_ = id;
    }

    /**
     * Factory method for the {@link LazyList} to auto grow
     */
    public Object create() {
        return new Attribute();
    }
    
    
    
}
