/*
 * $Id: HomeAction.java,v 1.2 2007/06/11 13:10:59 vtschopp Exp $
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html 
 */
package org.glite.slcs.struts.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionRedirect;
import org.glite.slcs.struts.view.UserBean;

public class HomeAction extends AbstractAction {

    protected ActionForward executeAction(ActionMapping mapping,
            ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        List userAttributes = getUserAttributes(request);
        UserBean userBean = new UserBean(userAttributes);
        request.setAttribute("userBean", userBean);

        ActionForward forward = mapping.findForward("admin.page.home");
        ActionRedirect redirect = new ActionRedirect(forward);
        return redirect;
    }

}
