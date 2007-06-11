/*
 * $Id: ListAccessControlRulesAction.java,v 1.2 2007/06/11 13:10:59 vtschopp Exp $
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
import org.glite.slcs.group.GroupManager;
import org.glite.slcs.group.GroupManagerFactory;
import org.glite.slcs.struts.view.AccessControlListBean;

/**
 * Struts action: List the access control rules
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision: 1.2 $
 */
public class ListAccessControlRulesAction extends AbstractAction {

    static private Log LOG = LogFactory.getLog(ListAccessControlRulesAction.class);

    protected ActionForward executeAction(ActionMapping mapping,
            ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        List userAttributes = getUserAttributes(request);

        GroupManager groupManager = GroupManagerFactory.getInstance();
        AccessControlListEditor editor = AccessControlListEditorFactory.getInstance();
        List rules = null;
        String groupName= null;
        
        
        // check if a particular group is given in the request
        if (isListRulesAction(request)) {
            groupName = getListRulesGroup(request);
            LOG.debug("groupName=" + groupName);
            if (groupManager.inGroup(groupName, userAttributes)
                    || groupManager.isAdministrator(userAttributes)) {
                rules = editor.getAccessControlRules(groupName);
            }
            else {
                LOG.warn("User " + userAttributes + " not in group "
                        + groupName);
            }

        }

        if (rules == null) {
            if (groupManager.isAdministrator(userAttributes)) {
                LOG.debug("user is administrator, list all rules");
                rules = editor.getAccessControlRules();
            }
            else {
                // determine admin group(s) of user
                List groupNames = groupManager.getGroupNames(userAttributes);
                LOG.debug("groupNames=" + groupNames);
                // 2. get list of rules
                rules = editor.getAccessControlRules(groupNames);
            }
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("rules=" + rules);
        }

        // 3. create bean and store in request
        AccessControlListBean rulesBean = new AccessControlListBean(rules);
        rulesBean.setFilename(editor.getACLFilename());
        rulesBean.setGroupName(groupName);
        request.setAttribute("rulesBean", rulesBean);

        // 4. forward/send response
        ActionForward forward = mapping.findForward("admin.page.listRules");
        return forward;
    }

}
