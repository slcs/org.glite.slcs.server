/*
 * $Id: CreateAccessControlRuleAction.java,v 1.1 2007/03/16 10:03:19 vtschopp Exp $
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html 
 */
package org.glite.slcs.struts.action;

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
import org.glite.slcs.acl.AccessControlListEditor;
import org.glite.slcs.acl.AccessControlListEditorFactory;
import org.glite.slcs.acl.AccessControlRule;
import org.glite.slcs.group.Group;
import org.glite.slcs.group.GroupManager;
import org.glite.slcs.group.GroupManagerFactory;
import org.glite.slcs.struts.form.AccessControlRuleForm;
import org.glite.slcs.struts.view.AccessControlRuleBean;

public class CreateAccessControlRuleAction extends AbstractAccessControlRuleAction {

    /**
     * Logging
     */
    static private Log LOG = LogFactory.getLog(CreateAccessControlRuleAction.class);

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.struts.action.AbstractAccessControlRuleAction#executeAction(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    protected ActionForward executeAction(ActionMapping mapping,
            ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        // cancel clicked on addRule.jsp?
        if (isCancelled(request)) {
            LOG.debug("cancelled");
            return mapping.findForward("admin.go.listRules");
        }

        if (isCreateRuleAction(request)) {
            LOG.info("new rule");
            AccessControlRuleForm ruleForm = (AccessControlRuleForm) form;
            AccessControlRuleBean ruleBean = newRule(ruleForm, request);
            request.setAttribute("ruleBean", ruleBean);
            // forward/send response
            return mapping.findForward("admin.page.addRule");
        }
        else if (isAddRuleAttributeAction(request)) {
            LOG.info("adding rule attribute");
            AccessControlRuleForm ruleForm = (AccessControlRuleForm) form;
            AccessControlRuleBean ruleBean = addRuleAttribute(ruleForm, request);
            request.setAttribute("ruleBean", ruleBean);
            // forward/send response
            return mapping.findForward("admin.page.addRule");
        }
        else if (isDeleteRuleAttributeAction(request)) {
            LOG.debug("delete rule attribute");
            AccessControlRuleForm ruleForm = (AccessControlRuleForm) form;
            AccessControlRuleBean ruleBean = deleteRuleAttribute(ruleForm, request);
            request.setAttribute("ruleBean", ruleBean);
            // forward/send response
            return mapping.findForward("admin.page.addRule");
        }
        else if (isSaveRuleAction(request)) {
            LOG.info("save rule");
            // read rule group and attributes from form
            AccessControlRuleForm ruleForm = (AccessControlRuleForm) form;
            GroupManager groupManager = GroupManagerFactory.getInstance();
            List userAttributes = getUserAttributes(request);
            List ruleAttributes = getValidRuleAttributes(ruleForm);
            String ruleGroup = ruleForm.getGroup();
            if (groupManager.inGroup(ruleGroup, userAttributes)) {
                // TODO: check group ACL rule constraint
                Group group = groupManager.getGroup(ruleGroup);
                List attributesContraint = group.getRuleConstraints();
                if (ruleAttributes.containsAll(attributesContraint)) {
                    AccessControlRule rule = new AccessControlRule(ruleGroup);
                    rule.setAttributes(ruleAttributes);
                    LOG.info("save rule: " + rule);
                    AccessControlListEditor editor = AccessControlListEditorFactory.getInstance();
                    editor.addAccessControlRule(rule);
                    return mapping.findForward("admin.go.listRules");
                }
                else {
                    // use ActionMessage for error...
                    LOG.warn("rule does not contain the mandatory attributes constraint: "
                            + attributesContraint);
                    ActionMessages messages = new ActionMessages();
                    StringBuffer messageText = new StringBuffer();
                    messageText.append("<ul>");
                    Iterator attributes = attributesContraint.iterator();
                    while (attributes.hasNext()) {
                        Attribute attribute = (Attribute) attributes.next();
                        messageText.append("<li>").append(attribute.getDisplayName());
                        messageText.append(" = ").append(attribute.getValue());
                        messageText.append("</li>");
                    }
                    messageText.append("</ul>");
                    ActionMessage warn = new ActionMessage("rule.error.save.constraint.missing", ruleGroup, messageText);
                    messages.add(ActionMessages.GLOBAL_MESSAGE, warn);
                    saveErrors(request, messages);
                    AccessControlRuleBean ruleBean = new AccessControlRuleBean();
                    List userGroupNames = groupManager.getGroupNames(userAttributes);
                    ruleBean.setUserGroups(userGroupNames);
                    ruleBean.setGroup(ruleGroup);
                    ruleBean.setAttributes(ruleAttributes);
                    // add a new empty attributes
                    ruleBean.addEmptyAttribute();
                    request.setAttribute("ruleBean", ruleBean);
                    // forward/send response
                    return mapping.findForward("admin.page.addRule");
                }
            }
            else {
                // use ActionMessage for error...
                LOG.error("User: " + userAttributes
                        + " is not a member of group: " + ruleGroup);
                ActionMessages messages = new ActionMessages();
                ActionMessage error = new ActionMessage("user.error.notmember", userAttributes, ruleGroup);
                messages.add(ActionMessages.GLOBAL_MESSAGE, error);
                saveErrors(request, messages);
            }

        }
        LOG.warn("Unknown action");
        return mapping.findForward("admin.go.home");
    }

    /**
     * @param ruleForm
     * @param request
     * @return
     * @throws SLCSException
     */
    protected AccessControlRuleBean newRule(AccessControlRuleForm ruleForm,
            HttpServletRequest request) throws SLCSException {
        // get user dependent info
        List userAttributes = getUserAttributes(request);
        GroupManager groupManager = GroupManagerFactory.getInstance();
        List userGroupNames = groupManager.getGroupNames(userAttributes);
        String ruleGroup = ruleForm.getGroup();
        if (ruleGroup == null && !userGroupNames.isEmpty()) {
            ruleGroup = (String) userGroupNames.get(0);
        }
        // get the group ACL rule constraint
        Group group = groupManager.getGroup(ruleGroup);
        List attributesContraint = group.getRuleConstraints();
        // create new bean
        AccessControlRuleBean ruleBean = new AccessControlRuleBean();
        ruleBean.setGroup(ruleGroup);
        ruleBean.setUserGroups(userGroupNames);
        // add the default ACL rule constaint
        ruleBean.addAttributes(attributesContraint);
        // add a new empty attributes
        ruleBean.addEmptyAttribute();
        return ruleBean;
    }

}
