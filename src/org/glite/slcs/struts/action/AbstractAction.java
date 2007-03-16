/*
 * $Id: AbstractAction.java,v 1.2 2007/03/16 10:03:19 vtschopp Exp $
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html 
 */
package org.glite.slcs.struts.action;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.glite.slcs.Attribute;
import org.glite.slcs.config.AttributeDefintionsHelper;
import org.glite.slcs.config.SLCSServerConfiguration;
import org.glite.slcs.util.Utils;

public abstract class AbstractAction extends Action {

    /** Logging */
    static private Log LOG = LogFactory.getLog(AbstractAction.class);

    static private String SAVE_RULE_ACTION_KEY = "org.glite.slcs.struts.action.SAVE_RULE";

    static private String CREATE_RULE_ACTION_KEY = "org.glite.slcs.struts.action.CREATE_RULE";

    static private String EDIT_RULE_ACTION_KEY = "org.glite.slcs.struts.action.EDIT_RULE";

    static private String DELETE_RULE_ACTION_KEY = "org.glite.slcs.struts.action.DELETE_RULE";

    static private String ADD_RULE_ATTRIBUTE_ACTION_KEY = "org.glite.slcs.struts.action.ADD_ATTRIBUTE";

    static private String DELETE_RULE_ATTRIBUTE_ACTION_KEY = "org.glite.slcs.struts.action.DELETE_ATTRIBUTE";

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        beforeAction();

        ActionForward forward = executeAction(mapping, form, request, response);

        afterAction();

        return forward;
    }

    protected abstract ActionForward executeAction(ActionMapping mapping,
            ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception;

    protected void beforeAction() {
    }

    protected void afterAction() {
    }

    /**
     * @return The ServletContext obj.
     */
    protected ServletContext getServletContext() {
        return getServlet().getServletContext();
    }

    /**
     * Return the list of all Shibboleth attributes present in the HTTP request.
     * The Shibboleth attributes are prefixed with <code>Shib-</code>.
     * Multi-value attributes are returned in the list as multiple attributes.
     * 
     * @param request
     *            The {@link HttpServletRequest} object.
     * @return The List of {@link Attribute}.
     */
    protected List getUserAttributes(HttpServletRequest request) {
        // list of valid attribute names read from config
        SLCSServerConfiguration config = SLCSServerConfiguration.getInstance();
        List definedAttributeNames = config.getDefinedAttributeNames();
        AttributeDefintionsHelper helper = config.getAttributeDefintionsHelper();
        // list of user attributes
        List attributes = new ArrayList();
        Enumeration headers = request.getHeaderNames();
        while (headers.hasMoreElements()) {
            String headerName = (String) headers.nextElement();
            if (definedAttributeNames.contains(headerName)) {
                String headerValue = request.getHeader(headerName);
                // add only not null and not empty attributes
                if (headerValue != null && !headerValue.equals("")) {
                    // if (LOG.isDebugEnabled()) {
                    // LOG.debug("Header: " + headerName + "=" + headerValue);
                    // }
                    String decodedValue = Utils.convertShibbolethUTF8ToUnicode(headerValue);
                    // multi-value attributes are stored as multiple attributes
                    String[] attrValues = decodedValue.split(";");
                    for (int i = 0; i < attrValues.length; i++) {
                        String attrName = headerName;
                        String attrValue = attrValues[i];
                        attrValue = attrValue.trim();
                        Attribute attribute = new Attribute(attrName, attrValue);
                        String displayName = helper.getDisplayName(attribute);
                        attribute.setDisplayName(displayName);
                        attributes.add(attribute);
                    }
                }

            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Attributes: " + attributes);
        }
        return attributes;
    }

    /**
     * Returns the <code>User-Agent</code> request header as {@link Attribute}.
     * 
     * @param req
     *            The {@link HttpServletRequest} object
     * @return The User-Agent {@link Attribute} or <code>null</code> if not
     *         set.
     */
    protected Attribute getUserAgentAttribute(HttpServletRequest req) {
        String userAgent = req.getHeader("User-Agent");
        if (userAgent != null) {
            return new Attribute("User-Agent", userAgent);
        }
        else {
            return null;
        }
    }

    /**
     * Returns the request remote address as {@link Attribute} named
     * <code>Remote-Address</code>.
     * 
     * @param req
     *            The {@link HttpServletRequest} object
     * @return The Remote-Address {@link Attribute} or <code>null</code> if
     *         not set.
     */
    protected Attribute getRemoteAddressAttribute(HttpServletRequest req) {
        String remoteAddress = req.getRemoteAddr();
        if (remoteAddress != null) {
            return new Attribute("Remote-Address", remoteAddress);
        }
        else {
            return null;
        }
    }

    /**
     * Returns the List of required Shibboleth attributes as defined in the SLCS
     * server configuration file under element
     * ShibbolethConfiguration.RequiredAttributes
     * 
     * @return List of attribute names.
     */
    protected List getRequiredAttributeNames() {
        SLCSServerConfiguration config = SLCSServerConfiguration.getInstance();
        List requiredAttributeNames = config.getRequiredAttributeNames();
        return requiredAttributeNames;
    }

    /**
     * Return the servlet URL for the servlet path. The servlet path must starts
     * with '/' and is relative the the context root.
     * 
     * @param req
     *            The HttpServletRequest object.
     * @param servletPath
     *            The servlet path relative to the context root.
     * @return The URL of the servlet.
     */
    protected String getContextUrl(HttpServletRequest req, String servletPath) {
        StringBuffer sb = new StringBuffer();
        sb.append(req.getScheme()).append("://");
        sb.append(req.getServerName()).append(':').append(req.getServerPort());
        sb.append(req.getContextPath());
        sb.append(servletPath);
        return sb.toString();
    }

    protected boolean isSaveRuleAction(HttpServletRequest request) {
        return isIndexedParameter(request, SAVE_RULE_ACTION_KEY);
        // return (req.getParameter(SAVE_RULE_ACTION_KEY) != null);
    }

    protected int getSaveRuleId(HttpServletRequest request) {
        return getIndexedParameterIndex(request, SAVE_RULE_ACTION_KEY);
    }

    protected boolean isCreateRuleAction(HttpServletRequest req) {
        return (req.getParameter(CREATE_RULE_ACTION_KEY) != null);
    }

    protected boolean isEditRuleAction(HttpServletRequest request) {
        return isIndexedParameter(request, EDIT_RULE_ACTION_KEY);
        // return (req.getParameter(EDIT_RULE_ACTION_KEY) != null);
    }

    protected int getEditRuleId(HttpServletRequest request) {
        return getIndexedParameterIndex(request, EDIT_RULE_ACTION_KEY);
    }

    protected boolean isDeleteRuleAction(HttpServletRequest req) {
        return isIndexedParameter(req, DELETE_RULE_ACTION_KEY);
    }

    protected int getDeleteRuleId(HttpServletRequest request) {
        return getIndexedParameterIndex(request, DELETE_RULE_ACTION_KEY);
    }

    protected boolean isAddRuleAttributeAction(HttpServletRequest req) {
        return (req.getParameter(ADD_RULE_ATTRIBUTE_ACTION_KEY) != null);
    }

    protected boolean isDeleteRuleAttributeAction(HttpServletRequest req) {
        return isIndexedParameter(req, DELETE_RULE_ATTRIBUTE_ACTION_KEY);
    }

    /**
     * Returns the index of the rule attribute to delete
     * 
     * @param request
     *            The {@link HttpServletRequest} object.
     * @return The attribute index or <code>-1</code> if the index is not
     *         defined.
     */
    protected int getDeleteRuleAttributeIndex(HttpServletRequest request) {
        return getIndexedParameterIndex(request, DELETE_RULE_ATTRIBUTE_ACTION_KEY);
    }

    /**
     * @param request
     * @param parameterName
     * @return
     */
    private boolean isIndexedParameter(HttpServletRequest request,
            String parameterName) {
        Enumeration parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String name = (String) parameterNames.nextElement();
            if (name.startsWith(parameterName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param request
     * @param parameterName
     * @return
     */
    private int getIndexedParameterIndex(HttpServletRequest request,
            String parameterName) {
        int i = -1;
        String index = null;
        if (LOG.isDebugEnabled())
            LOG.debug("parameterName=" + parameterName);
        Enumeration parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String name = (String) parameterNames.nextElement();
            if (name.startsWith(parameterName)) {
                // read index from parameterName[i]
                int begin = parameterName.length() + 1;
                int end = name.length() - 1;
                if (LOG.isDebugEnabled())
                    LOG.debug("[" + begin + "," + end + "] of " + name);
                index = name.substring(begin, end);
            }
        }
        if (LOG.isDebugEnabled())
            LOG.debug("index= " + index);
        try {
            i = Integer.parseInt(index);
        } catch (Exception e) {
            LOG.warn(e);
        }
        return i;
    }

}
