/*
 * $Id: LoginAction.java,v 1.1 2007/03/16 08:58:33 vtschopp Exp $
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html 
 */
package org.glite.slcs.struts.action;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.glite.slcs.Attribute;
import org.glite.slcs.AuthException;
import org.glite.slcs.audit.Auditor;
import org.glite.slcs.audit.AuditorFactory;
import org.glite.slcs.audit.event.AuditEvent;
import org.glite.slcs.audit.event.AuthorizationEvent;
import org.glite.slcs.dn.DNBuilder;
import org.glite.slcs.dn.DNBuilderFactory;
import org.glite.slcs.policy.CertificatePolicy;
import org.glite.slcs.policy.CertificatePolicyFactory;
import org.glite.slcs.session.SLCSSessions;
import org.glite.slcs.session.SLCSSessionsFactory;
import org.glite.slcs.struts.view.LoginResponseBean;

public class LoginAction extends AbstractAction {

    /** Logging */
    static private Log LOG = LogFactory.getLog(LoginAction.class);

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.struts.AbstractAction#executeAction(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    protected ActionForward executeAction(ActionMapping mapping,
            ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        // 1. get user's attributes
        // 1.1 Shibboleth attributes
        List attributes = getUserAttributes(request);
        // 1.2 User-Agent
        Attribute userAgent = getUserAgentAttribute(request);
        if (userAgent != null) {
            attributes.add(userAgent);
        }
        // 1.2 Remote address
        Attribute remoteAddress = getRemoteAddressAttribute(request);
        if (remoteAddress != null) {
            attributes.add(remoteAddress);
        }
        // 2. check required attributes
        checkRequiredAttributes(attributes);
        AuditEvent login = new AuthorizationEvent("User login", attributes);
        Auditor auditor = AuditorFactory.getInstance();
        auditor.logEvent(login);

        // 3. create DN
        DNBuilder dnBuilder = DNBuilderFactory.getInstance();
        String subject = dnBuilder.createDN(attributes);

        // 4. store in sessions and create token
        SLCSSessions sessions = SLCSSessionsFactory.getInstance();
        String authorizationToken = sessions.createSession(subject);

        // 5. get policy
        CertificatePolicy policy = CertificatePolicyFactory.getInstance();
        List certificateExtensions = policy
                .getRequiredCertificateExtensions(attributes);

        // 6. create response bean and store in request
        LoginResponseBean loginResponseBean = new LoginResponseBean();
        loginResponseBean.setAuthorizationToken(authorizationToken);
        loginResponseBean.setCertificateExtensions(certificateExtensions);
        loginResponseBean.setSubject(subject);
        String certificateRequestUrl= getContextUrl(request, "/certificate");
        loginResponseBean.setRequestURL(certificateRequestUrl);
        // store in request
        request.setAttribute("loginResponse", loginResponseBean);

        // 7. forward/send response
        ActionForward forward = mapping.findForward("success");
        return forward;
    }

    /**
     * Checks if all required attributes are present.
     * 
     * @param attributes
     *            List of user's attributes.
     * @throws AuthException
     *             If a required attribute is missing
     * 
     * @see AbstractAction#getRequiredAttributeNames()
     */
    protected void checkRequiredAttributes(List userAttributes)
            throws AuthException {
        // get list of required attribute names
        List requiredAttributeNames = getRequiredAttributeNames();
        // get list of user attribute names
        Set userAttributeNames = new HashSet();
        Iterator attributes = userAttributes.iterator();
        while (attributes.hasNext()) {
            Attribute attribute = (Attribute) attributes.next();
            String attributeName = attribute.getName();
            userAttributeNames.add(attributeName);
        }
        // compare with user attribute names
        if (!userAttributeNames.containsAll(requiredAttributeNames)) {
            Set missingAttributeNames = new HashSet();
            Iterator requiredNames = requiredAttributeNames.iterator();
            while (requiredNames.hasNext()) {
                String requiredName = (String) requiredNames.next();
                if (!userAttributeNames.contains(requiredName)) {
                    missingAttributeNames.add(requiredName);
                }
            }
            LOG
                    .error("Missing required attribute(s): "
                            + missingAttributeNames);
            throw new AuthException("Missing required attribute(s): "
                    + missingAttributeNames);
        }
    }

}
