/*
 * $Id: EditAccessControlRuleAction.java,v 1.2 2007/06/11 13:10:59 vtschopp Exp $
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
import org.glite.slcs.acl.AccessControlListEditor;
import org.glite.slcs.acl.AccessControlListEditorFactory;
import org.glite.slcs.acl.AccessControlRule;
import org.glite.slcs.attribute.Attribute;
import org.glite.slcs.group.Group;
import org.glite.slcs.group.GroupManager;
import org.glite.slcs.group.GroupManagerFactory;
import org.glite.slcs.struts.form.AccessControlRuleForm;
import org.glite.slcs.struts.view.AccessControlRuleBean;

public class EditAccessControlRuleAction extends
        AbstractAccessControlRuleAction {

    /** Logging */
    static Log LOG = LogFactory.getLog(EditAccessControlRuleAction.class);

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
            return mapping.findForward("admin.go.listRules");
        }

        if (isEditRuleAction(request)) {
            LOG.info("edit rule");
            AccessControlListEditor editor = AccessControlListEditorFactory.getInstance();
            int ruleId = getEditRuleId(request);
            AccessControlRule rule = editor.getAccessControlRule(ruleId);
            if (rule != null) {
                GroupManager groupManager = GroupManagerFactory.getInstance();
                String groupName= rule.getGroupName();
                Group group= groupManager.getGroup(groupName);
                List attributesConstraint= group.getRuleAttributesConstraint();
                AccessControlRuleBean ruleBean = new AccessControlRuleBean();
                ruleBean.setGroupName(rule.getGroupName());
                ruleBean.setId(rule.getId());
                ruleBean.setAttributes(rule.getAttributes());
                ruleBean.addConstrainedAttributes(attributesConstraint);
                ruleBean.updateAttributesDiplayName();
                request.setAttribute("ruleBean", ruleBean);
                // forward/send response
                return mapping.findForward("admin.page.editRule");
            }
            // TODO: error rule doesn't exist!!!!
        }
        else if (isAddRuleAttributeAction(request)) {
            LOG.info("add rule attribute");
            AccessControlRuleForm ruleForm = (AccessControlRuleForm) form;
            AccessControlRuleBean ruleBean = addRuleAttribute(ruleForm, request);
            request.setAttribute("ruleBean", ruleBean);
            // forward/send response
            return mapping.findForward("admin.page.editRule");
        }
        else if (isDeleteRuleAttributeAction(request)) {
            LOG.debug("delete rule attribute");
            AccessControlRuleForm ruleForm = (AccessControlRuleForm) form;
            AccessControlRuleBean ruleBean = deleteRuleAttribute(ruleForm, request);
            request.setAttribute("ruleBean", ruleBean);
            // forward/send response
            return mapping.findForward("admin.page.editRule");
        }
        else if (isSaveRuleAction(request)) {
            LOG.info("save rule");
            List userAttributes = getUserAttributes(request);
            GroupManager groupManager = GroupManagerFactory.getInstance();
            // read rule group and attributes from form
            AccessControlRuleForm ruleForm = (AccessControlRuleForm) form;
            String ruleGroup = ruleForm.getGroupName();
            List ruleAttributes = getValidRuleAttributes(ruleForm);
            if (groupManager.inGroup(ruleGroup, userAttributes)
                    || groupManager.isAdministrator(userAttributes)) {

                int ruleId = getSaveRuleId(request);
                // check the rule constraint
                Group group = groupManager.getGroup(ruleGroup);
                List attributesContraint = group.getRuleAttributesConstraint();
                if (ruleAttributes.containsAll(attributesContraint)) {
                    AccessControlListEditor editor = AccessControlListEditorFactory.getInstance();
                    AccessControlRule rule = editor.getAccessControlRule(ruleId);
                    if (rule != null) {
                        rule.setGroupName(ruleGroup);
                        rule.setAttributes(ruleAttributes);
                        LOG.info("replace rule: " + rule);
                        editor.replaceAccessControlRule(rule);
                    }
                    else {
                        // someone deleted the rule while another edited it
                        rule = new AccessControlRule(ruleGroup);
                        rule.setAttributes(ruleAttributes);
                        LOG.info("replace/save rule: " + rule);
                        editor.addAccessControlRule(rule);
                    }
                    return mapping.findForward("admin.go.listRules");
                }
                else {
                    // use ActionMessage for error...
                    LOG.warn("rule did not contain the mandatory attributes constraint: "
                            + attributesContraint);
                    ActionMessages messages = new ActionMessages();
                    StringBuffer messageText = new StringBuffer();
                    messageText.append("<ul>");
                    Iterator attributes = attributesContraint.iterator();
                    while (attributes.hasNext()) {
                        Attribute attribute = (Attribute) attributes.next();
                        messageText.append("<li>Attribute ").append(attribute.getDisplayName());
                        messageText.append(" = ").append(attribute.getValue());
                        messageText.append(" was added</li>");
                    }
                    messageText.append("</ul>");
                    ActionMessage warn = new ActionMessage("rule.error.save.constraint.missing", ruleGroup, messageText);
                    messages.add(ActionMessages.GLOBAL_MESSAGE, warn);
                    saveErrors(request, messages);
                    
                    // set the rule bean again
                    List userGroupNames = groupManager.getGroupNames(userAttributes);
                    AccessControlRuleBean ruleBean = new AccessControlRuleBean();
                    ruleBean.setId(ruleId);
                    ruleBean.setUserGroupNames(userGroupNames);
                    ruleBean.setGroupName(ruleGroup);
                    ruleBean.setAttributes(ruleAttributes);
                    // add the missing constrained attribute
                    ruleBean.addConstrainedAttributes(attributesContraint);
                    request.setAttribute("ruleBean", ruleBean);
                    // forward/send response
                    return mapping.findForward("admin.page.editRule");
                }
            }
            else {
                // TODO: use ActionMessage for error...
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
}
