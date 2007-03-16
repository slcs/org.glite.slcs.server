/*
 * $Id: EditAccessControlRuleAction.java,v 1.1 2007/03/16 08:58:33 vtschopp Exp $
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html 
 */
package org.glite.slcs.struts.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.glite.slcs.acl.AccessControlListEditor;
import org.glite.slcs.acl.AccessControlListEditorFactory;
import org.glite.slcs.acl.AccessControlRule;
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
            List userAttributes = getUserAttributes(request);
            GroupManager groupManager = GroupManagerFactory.getInstance();
            List userGroupNames = groupManager.getGroupNames(userAttributes);
            AccessControlListEditor editor = AccessControlListEditorFactory.getInstance();
            int ruleId = getEditRuleId(request);
            AccessControlRule rule = editor.getAccessControlRule(ruleId);
            if (rule != null) {
                AccessControlRuleBean ruleBean = new AccessControlRuleBean();
                ruleBean.setUserGroups(userGroupNames);
                ruleBean.setGroup(rule.getGroup());
                ruleBean.setId(rule.getId());
                ruleBean.setAttributes(rule.getAttributes());
                request.setAttribute("ruleBean", ruleBean);
                // forward/send response
                return mapping.findForward("admin.page.editRule");
            }
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
            String ruleGroup = ruleForm.getGroup();
            List ruleAttributes = getValidRuleAttributes(ruleForm);
            if (groupManager.inGroup(ruleGroup, userAttributes)) {
                if (!ruleAttributes.isEmpty()) {
                    AccessControlListEditor editor = AccessControlListEditorFactory.getInstance();
                    int ruleId = getSaveRuleId(request);
                    AccessControlRule rule = editor.getAccessControlRule(ruleId);
                    if (rule != null) {
                        rule.setGroup(ruleGroup);
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
                    LOG.warn("rule does not contain valid attributes...");
                    // TODO: use ActionMessage for error...
                    List userGroupNames = groupManager.getGroupNames(userAttributes);
                    AccessControlRuleBean ruleBean = new AccessControlRuleBean();
                    ruleBean.setUserGroups(userGroupNames);
                    ruleBean.setGroup(ruleGroup);
                    ruleBean.setAttributes(ruleAttributes);
                    // add a new empty attributes
                    ruleBean.addEmptyAttribute();
                    request.setAttribute("ruleBean", ruleBean);
                    // forward/send response
                    return mapping.findForward("admin.page.editRule");
                }
            }
            else {
                // TODO: use ActionMessage for error...
                LOG.error("User: " + userAttributes
                        + " is not a member of group: " + ruleGroup);
            }

        }
        LOG.warn("Unknown action");
        return mapping.findForward("admin.go.home");
    }
}
