/*
 * $Id: DeleteAccessControlRuleAction.java,v 1.2 2007/06/11 13:10:59 vtschopp Exp $
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

/**
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.2 $
 */
public class DeleteAccessControlRuleAction extends AbstractAction {

    /** Logging */
    private static Log LOG = LogFactory.getLog(DeleteAccessControlRuleAction.class);

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.struts.action.AbstractAction#executeAction(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    protected ActionForward executeAction(ActionMapping mapping,
            ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        if (isDeleteRuleAction(request)) {
            int ruleId = getDeleteRuleId(request);
            LOG.info("deleting rule id=" + ruleId);
            if (ruleId > -1) {
                // get rule
                AccessControlListEditor editor = AccessControlListEditorFactory.getInstance();
                AccessControlRule rule = editor.getAccessControlRule(ruleId);
                if (rule != null) {
                    String ruleGroup = rule.getGroupName();
                    // checks if user can delete it
                    List userAttributes = getUserAttributes(request);
                    GroupManager groupManager = GroupManagerFactory.getInstance();
                    if (groupManager.inGroup(ruleGroup, userAttributes)
                            || groupManager.isAdministrator(userAttributes)) {
                        LOG.info("delete: " + rule);
                        editor.removeAccessControlRule(ruleId);
                    }
                    else {
                        // TODO: use ActionMessage for error...
                        LOG.error("User: " + userAttributes
                                + " is not a member of group: " + ruleGroup);
                    }
                }
                else {
                    // TODO: use ActionMessage for error...
                    LOG.error("Rule[ " + ruleId + " ] is null");
                }
                return mapping.findForward("admin.go.listRules");
            }
        }

        LOG.error("Unknown action!");
        return mapping.findForward("admin.go.home");

    }

}
