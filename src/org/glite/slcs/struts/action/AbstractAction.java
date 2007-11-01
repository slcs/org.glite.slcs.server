/*
 * $Id: AbstractAction.java,v 1.4 2007/11/01 14:32:46 vtschopp Exp $
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html 
 */
package org.glite.slcs.struts.action;

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
import org.glite.slcs.SLCSServerVersion;
import org.glite.slcs.attribute.Attribute;
import org.glite.slcs.attribute.AttributeDefinitions;
import org.glite.slcs.config.SLCSServerConfiguration;

public abstract class AbstractAction extends Action {

    /** Logging */
    static private Log LOG = LogFactory.getLog(AbstractAction.class);

    static private String LIST_RULES_ACTION_KEY = "org.glite.slcs.struts.action.LIST_RULES";
    
    static private String SAVE_RULE_ACTION_KEY = "org.glite.slcs.struts.action.SAVE_RULE";

    static private String CREATE_RULE_ACTION_KEY = "org.glite.slcs.struts.action.CREATE_RULE";

    static private String EDIT_RULE_ACTION_KEY = "org.glite.slcs.struts.action.EDIT_RULE";

    static private String DELETE_RULE_ACTION_KEY = "org.glite.slcs.struts.action.DELETE_RULE";

    static private String ADD_RULE_ATTRIBUTE_ACTION_KEY = "org.glite.slcs.struts.action.ADD_ATTRIBUTE";

    static private String DELETE_RULE_ATTRIBUTE_ACTION_KEY = "org.glite.slcs.struts.action.DELETE_ATTRIBUTE";

    static private String CHANGE_RULE_GROUP_ACTION_KEY = "org.glite.slcs.struts.action.CHANGE_GROUP";

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

        beforeAction(form, request, response);

        ActionForward forward = executeAction(mapping, form, request, response);

        afterAction();

        return forward;
    }

    protected void beforeAction(ActionForm form,
            HttpServletRequest request, HttpServletResponse response) {
        List userAttributes = getUserAttributes(request);
        request.setAttribute("userAttributes", userAttributes);
        request.setAttribute("serverVersion", SLCSServerVersion.getVersion());
    }

    protected abstract ActionForward executeAction(ActionMapping mapping,
            ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception;

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
        // get the AttributeDefinitions engine to process the request
        SLCSServerConfiguration config = SLCSServerConfiguration.getInstance();
        AttributeDefinitions attributeDefinitions = config.getAttributeDefinitions();
        // get the user attributes from the request
        List userAttributes = attributeDefinitions.getUserAttributes(request);
        if (LOG.isDebugEnabled()) {
            LOG.debug("userAttributes=" + userAttributes);
        }
        return userAttributes;
    }

    protected AttributeDefinitions getAttributeDefinitions() {
        SLCSServerConfiguration config = SLCSServerConfiguration.getInstance();
        return config.getAttributeDefinitions();
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

    protected boolean isListRulesAction(HttpServletRequest request) {
        return isIndexedParameter(request, LIST_RULES_ACTION_KEY);        
    }
    
    protected String getListRulesGroup(HttpServletRequest request) {
        return getIndexedParameterString(request, LIST_RULES_ACTION_KEY);
    }
    
    protected boolean isSaveRuleAction(HttpServletRequest request) {
        return isIndexedParameter(request, SAVE_RULE_ACTION_KEY);
    }

    protected int getSaveRuleId(HttpServletRequest request) {
        return getIndexedParameterIndex(request, SAVE_RULE_ACTION_KEY);
    }

    protected boolean isCreateRuleAction(HttpServletRequest req) {
        return (req.getParameter(CREATE_RULE_ACTION_KEY) != null);
    }

    protected boolean isEditRuleAction(HttpServletRequest request) {
        return isIndexedParameter(request, EDIT_RULE_ACTION_KEY);
    }

    protected int getEditRuleId(HttpServletRequest request) {
        return getIndexedParameterIndex(request, EDIT_RULE_ACTION_KEY);
    }

    protected boolean isDeleteRuleAction(HttpServletRequest req) {
        return isIndexedParameter(req, DELETE_RULE_ACTION_KEY);
    }

    protected boolean isChangeRuleGroupAction(HttpServletRequest req) {
        return isIndexedParameter(req, CHANGE_RULE_GROUP_ACTION_KEY);
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
     * Checks if the HTTP request parameter name contains an index in the form:
     * PARAMETER_NAME[index].
     * 
     * @param request
     *            The HTTP request object.
     * @param parameterName
     *            The request parameter name to check
     * @return <code>true</code> if the parameter name is indexed.
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
     * Returns the index of an indexed HTTP request parameter name.
     * 
     * @param request
     *            The HTTP request object
     * @param parameterName
     *            The request parameter name
     * @return the index value or <code>-1</code> if no index can be found.
     */
    private int getIndexedParameterIndex(HttpServletRequest request,
            String parameterName) {
        int i = -1;
        String index = getIndexedParameterString(request, parameterName);
        if (index != null) {
            try {
                i = Integer.parseInt(index);
            } catch (Exception e) {
                LOG.warn(e);
            }
        }
        return i;
    }

    /**
     * Gets the indexed string value of the request parameter formated like
     * PARAMETER_NAME[INDEX].
     * 
     * @param request
     *            The {@link HttpServletRequest} object.
     * @param parameterName
     *            The request parameter name.
     * @return The string value of the index or <code>null</code> if not
     *         defined.
     */
    private String getIndexedParameterString(HttpServletRequest request,
            String parameterName) {
        String index = null;
        if (LOG.isDebugEnabled()) {
            LOG.debug("parameterName=" + parameterName);
        }
        Enumeration parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String name = (String) parameterNames.nextElement();
            if (name.startsWith(parameterName)) {
                // read index from parameterName[i]
                int begin = parameterName.length() + 1;
                int end = name.length() - 1;
                if (LOG.isDebugEnabled()) {
                    LOG.debug("[" + begin + "," + end + "] of " + name);
                }
                index = name.substring(begin, end);
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("index= " + index);
        }
        return index;
    }

}
