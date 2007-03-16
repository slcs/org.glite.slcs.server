/*
 * $Id: ListAccessControlRulesAction.java,v 1.1 2007/03/16 08:58:33 vtschopp Exp $
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
import org.glite.slcs.config.AttributeDefintionsHelper;
import org.glite.slcs.config.SLCSServerConfiguration;
import org.glite.slcs.group.GroupManager;
import org.glite.slcs.group.GroupManagerFactory;
import org.glite.slcs.struts.view.AccessControlListBean;

public class ListAccessControlRulesAction extends AbstractAction {

    static private Log LOG= LogFactory.getLog(ListAccessControlRulesAction.class);
    
    protected ActionForward executeAction(ActionMapping mapping,
            ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        List userAttributes= getUserAttributes(request);
        
        // determine admin group(s) of user
        GroupManager groupManager= GroupManagerFactory.getInstance();
        List groupNames= groupManager.getGroupNames(userAttributes);
        
        LOG.debug("groupNames=" + groupNames);
        
        // 2. get list of rules
        AccessControlListEditor editor= AccessControlListEditorFactory.getInstance();
        List rules= editor.getAccessControlRules(groupNames);
        LOG.debug("rules=" + rules);
        
        // 3. create bean and store in request
        AccessControlListBean rulesBean= new AccessControlListBean(rules);
        SLCSServerConfiguration config= SLCSServerConfiguration.getInstance();
        AttributeDefintionsHelper helper= config.getAttributeDefintionsHelper();
        rulesBean.setAttributeDisplayNames(helper);
        rulesBean.setFilename(editor.getACLFilename());
        request.setAttribute("rulesBean", rulesBean);
        
        // 4. forward/send response
        ActionForward forward = mapping.findForward("admin.page.listRules");
        return forward;
    }

}
