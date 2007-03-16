/*
 * $Id: AbstractAccessControlRuleAction.java,v 1.1 2007/03/16 08:58:33 vtschopp Exp $
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html 
 */
package org.glite.slcs.struts.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.glite.slcs.Attribute;
import org.glite.slcs.SLCSException;
import org.glite.slcs.group.Group;
import org.glite.slcs.group.GroupManager;
import org.glite.slcs.group.GroupManagerFactory;
import org.glite.slcs.struts.form.AccessControlRuleForm;
import org.glite.slcs.struts.view.AccessControlRuleBean;

public abstract class AbstractAccessControlRuleAction extends AbstractAction {

    /** Logging */
    static private Log LOG = LogFactory.getLog(AbstractAccessControlRuleAction.class);

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.struts.action.AbstractAction#executeAction(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    abstract protected ActionForward executeAction(ActionMapping mapping,
            ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception;

    /**
     * Reads the attributes from the {@link AccessControlRuleForm} and returns
     * the list of valid (with name and value) Attributes.
     * 
     * @param form
     *            The {@link AccessControlRuleForm} form
     * @return The list of valid attributes.
     */
    protected List getValidRuleAttributes(AccessControlRuleForm form) {
        List attributes = form.getAttributes();
        LOG.debug("formAttributes=" + attributes);
        List ruleAttributes = new ArrayList();
        Iterator iter = attributes.iterator();
        while (iter.hasNext()) {
            Attribute attribute = (Attribute) iter.next();
            if (attribute.isValid()) {
                ruleAttributes.add(attribute);
            }
        }
        LOG.debug("ruleAttributes=" + ruleAttributes);
        return ruleAttributes;
    }

    /**
     * @param ruleForm
     * @param request
     * @return
     * @throws SLCSException
     */
    protected AccessControlRuleBean deleteRuleAttribute(
            AccessControlRuleForm ruleForm, HttpServletRequest request)
            throws SLCSException {
        int index = getDeleteRuleAttributeIndex(request);
        LOG.debug("delete rule attribute: " + index);
        // read rule group and attributes from form
        String ruleGroup = ruleForm.getGroup();
        List ruleAttributes = ruleForm.getAttributes();
        // can not delete constrainted attributes
        Attribute attribute = (Attribute) ruleAttributes.get(index);
        GroupManager groupManager = GroupManagerFactory.getInstance();
        Group group = groupManager.getGroup(ruleGroup);
        List attributesConstraint = group.getRuleConstraints();
        if (!attributesConstraint.contains(attribute)) {
            // delete the attribute iff the attribute is not constrained
            ruleAttributes.remove(index);
        }
        else {
            LOG.warn("can not delete constrained attribute: " + attribute);
            ActionMessages messages = new ActionMessages();
            ActionMessage warn = new ActionMessage("rule.warning.delete.constrained.attribute", attribute.getDisplayName(), attribute.getValue());
            messages.add(ActionMessages.GLOBAL_MESSAGE, warn);
            saveErrors(request, messages);
        }
        List userAttributes = getUserAttributes(request);
        List userGroupNames = groupManager.getGroupNames(userAttributes);
        // create and initialize the rule bean
        AccessControlRuleBean ruleBean = new AccessControlRuleBean();
        ruleBean.setUserGroups(userGroupNames);
        ruleBean.setGroup(ruleGroup);
        if (ruleAttributes.isEmpty()) {
            ruleBean.addEmptyAttribute();
        }
        else {
            ruleBean.setAttributes(ruleAttributes);
        }
        return ruleBean;
    }

    /**
     * Creates and returns an {@link AccessControlRuleBean} with the existing
     * attributes from the {@link AccessControlRuleForm} and a new empty
     * attribute.
     * 
     * @param ruleForm
     *            The input {@link AccessControlRuleForm}
     * @param request
     *            The {@link HttpServletRequest} object.
     * @return The new AccessControlRuleBean extended with a new empty
     *         attribute.
     * @throws SLCSException
     */
    protected AccessControlRuleBean addRuleAttribute(
            AccessControlRuleForm ruleForm, HttpServletRequest request)
            throws SLCSException {
        // get user dependent information
        List userAttributes = getUserAttributes(request);
        GroupManager groupManager = GroupManagerFactory.getInstance();
        List userGroupNames = groupManager.getGroupNames(userAttributes);
        // get form information
        String ruleGroup = ruleForm.getGroup();
        List ruleAttributes = ruleForm.getAttributes();
        // create and initialize the rule bean
        AccessControlRuleBean ruleBean = new AccessControlRuleBean();
        ruleBean.setUserGroups(userGroupNames);
        ruleBean.setGroup(ruleGroup);
        ruleBean.setAttributes(ruleAttributes);
        // add a new empty attributes
        ruleBean.addEmptyAttribute();
        return ruleBean;
    }

}
