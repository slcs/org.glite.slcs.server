/*
 * $Id: AttributeDefinitionsAction.java,v 1.1 2007/11/01 14:32:46 vtschopp Exp $
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html 
 */
package org.glite.slcs.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionRedirect;
import org.glite.slcs.attribute.AttributeDefinitions;

public class AttributeDefinitionsAction extends AbstractAction {

    protected ActionForward executeAction(ActionMapping mapping,
            ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        AttributeDefinitions attributeDefinitions = getAttributeDefinitions();
        request.setAttribute("attributeDefinitions", attributeDefinitions);

        ActionForward forward = mapping.findForward("admin.page.attributeDefinitions");
        ActionRedirect redirect = new ActionRedirect(forward);
        return redirect;
    }

}
