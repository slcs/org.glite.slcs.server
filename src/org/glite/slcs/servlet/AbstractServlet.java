/*
 * $Id: AbstractServlet.java,v 1.1 2007/03/14 14:04:01 vtschopp Exp $
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html 
 */
package org.glite.slcs.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glite.slcs.Attribute;
import org.glite.slcs.SLCSException;
import org.glite.slcs.SLCSServerComponent;
import org.glite.slcs.audit.Auditor;
import org.glite.slcs.audit.AuditorFactory;
import org.glite.slcs.config.SLCSServerConfiguration;
import org.glite.slcs.session.SLCSSessions;
import org.glite.slcs.session.SLCSSessionsFactory;
import org.glite.slcs.util.Utils;

/**
 * AbstractServlet is the base class for the SLCS servlets.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.1 $
 */
public abstract class AbstractServlet extends HttpServlet {

    /** Logging */
    private static Log LOG = LogFactory.getLog(AbstractServlet.class);

    /** SLCS Server configuration */
    private SLCSServerConfiguration configuration_ = null;

    /** SCLS Sessions */
    private SLCSSessions sessions_ = null;

    /** Auditor */
    private Auditor auditor_ = null;

    /** List of registered {@link SLCSServerComponent}s */
    private Vector components_ = new Vector();

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
     */
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        try {
            // initialize the SLCS config
            LOG.info("initialize and instantiate SLCSServerConfiguration...");
            SLCSServerConfiguration.initialize(this.getServletContext());
            configuration_ = SLCSServerConfiguration.getInstance();

            LOG.info("instantiate SCLSSessions...");
            sessions_ = SLCSSessionsFactory.getInstance();
            registerSLCSServerComponent(sessions_);

            LOG.info("instantiate Auditor...");
            auditor_ = AuditorFactory.getInstance();
            registerSLCSServerComponent(auditor_);

        } catch (SLCSException e) {
            LOG.error("Servlet init failed", e);
            throw new ServletException(e);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.GenericServlet#destroy()
     */
    public void destroy() {
        super.destroy();
        LOG.info("shutdown all registered SLCS components...");
        Enumeration serverComponents = components_.elements();
        while (serverComponents.hasMoreElements()) {
            SLCSServerComponent component = (SLCSServerComponent) serverComponents.nextElement();
            component.shutdown();
        }
    }

    /**
     * Register a SLCS server component. All server components will be
     * shutdowned when the servlet is destroyed.
     * 
     * @param component
     *            A SLCS server component object.
     */
    protected void registerSLCSServerComponent(SLCSServerComponent component) {
        components_.add(component);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        doProcess(request, response);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        doProcess(request, response);
    }

    /**
     * Processes the POST or GET request.
     * 
     * @param request
     *            The HttpServletRequest object.
     * @param response
     *            The HttpServletRespose object.
     * @throws IOException
     * @throws ServletException
     */
    protected abstract void doProcess(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException;

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

    /**
     * Return the list of required user's {@link Attribute}s. The required
     * attributes are defined in a list of required attribute names.
     * 
     * @param req
     *            The HttpServletRequest object
     * @param requiredShibbolethAttributeNames
     *            List of required Shibboleth attribute names.
     * @return The list of user's attributes
     */
    protected List getShibbolethAttributes(HttpServletRequest req,
            List requiredShibbolethAttributeNames) {
        List userAttributes = new ArrayList();
        Enumeration headers = req.getHeaderNames();
        while (headers.hasMoreElements()) {
            String header = (String) headers.nextElement();
            if (requiredShibbolethAttributeNames.contains(header)) {
                String shibUTF8 = req.getHeader(header);
                if (shibUTF8 != null && !shibUTF8.equals("")) {
                    // convert Shibboleth UTF8 to unicode
                    String value = Utils.convertShibbolethUTF8ToUnicode(shibUTF8);
                    // multi-value attributes
                    String[] attrValues = value.split(";");
                    for (int i = 0; i < attrValues.length; i++) {
                        String attrName = header;
                        String attrValue = attrValues[i];
                        attrValue = attrValue.trim();
                        Attribute attribute = new Attribute(attrName, attrValue);
                        // String displayName =
                        // helper.getDisplayName(attribute);
                        // attribute.setDisplayName(displayName);
                        userAttributes.add(attribute);
                    }
                }

            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("attributes: " + userAttributes);
        }
        return userAttributes;
    }

    /**
     * Returns the User-Agent header as Attribute.
     * 
     * @param req
     *            The HttpServletRequest object
     * @return The User-Agent attribute or <code>null</code> if not set.
     */
    protected Attribute getUserAgentAttribute(HttpServletRequest req) {
        String userAgent = req.getHeader("User-Agent");
        if (userAgent != null) {
            return new Attribute("UserAgent", userAgent);
        }
        else {
            return null;
        }
    }

    /**
     * Returns the Remote-Address header as Attribute
     * 
     * @param req
     *            The {@link HttpServletRequest} object
     * @return The Remote-Address attribute or <code>null</code> if not set.
     */
    protected Attribute getRemoteAddressAttribute(HttpServletRequest req) {
        String remoteAddress = req.getRemoteAddr();
        if (remoteAddress != null) {
            return new Attribute("RemoteAddress", remoteAddress);
        }
        else {
            return null;
        }

    }

    /**
     * Sends a XML error message back to the client.
     * 
     * @param res
     *            The HttpServletResponse object.
     * @param type
     *            The type of the response.
     * @param message
     *            The error message.
     * @param cause
     *            The error cause.
     * @throws IOException
     *             If an error occurs while sending the error.
     */
    protected void sendXMLErrorResponse(HttpServletRequest req,
            HttpServletResponse res, String type, String message,
            Throwable cause) throws IOException {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.println(getXMLDeclaration());
        pw.print("<");
        pw.print(type);
        pw.println(">");
        pw.println("<Status>Error</Status>");
        pw.print("<Error>");
        pw.print(message);
        pw.println("</Error>");
        if (cause != null) {
            pw.print("<StackTrace>");
            cause.printStackTrace(pw);
            pw.print("</StackTrace>");
        }
        pw.print("</");
        pw.print(type);
        pw.println(">");

        LOG.info("sending " + type + ":\n" + sw.getBuffer().toString());

        // send response
        res.setContentType("text/xml");
        PrintWriter out = res.getWriter();
        out.println(sw.getBuffer().toString());
        out.close();
    }

    /**
     * @return List of required Shibboleth attribute names.
     */
    protected List getRequiredShibbolethAttributeNames() {
        return configuration_.getRequiredAttributeNames();
    }

    /**
     * Checks if all required Shibboleth attributes are present.
     * 
     * @param requiredShibbolethAttributeNames
     *            List of required Shibboleth attribute name.
     * @param userAttributes
     *            List of user's attributes.
     * @throws SLCSException
     *             If a required Shibboleth attribute is missing
     */
    protected void checkRequiredShibbolethAttributes(
            List requiredShibbolethAttributeNames, List userAttributes)
            throws SLCSException {
        // the list of user attribute names
        List attributeNames= new ArrayList();
        Iterator attributes= userAttributes.iterator();
        while (attributes.hasNext()) {
            Attribute attribute = (Attribute) attributes.next();
            String attributeName= attribute.getName();
            attributeNames.add(attributeName);
        }
        // compare with the required attribute names
        Iterator requiredAttributeNames = requiredShibbolethAttributeNames.iterator();
        while (requiredAttributeNames.hasNext()) {
            String requiredAttributeName = (String) requiredAttributeNames.next();
            if (!attributeNames.contains(requiredAttributeName)) {
                LOG.error("Required Shibboleth attribute "
                        + requiredAttributeName + " missing");
                throw new SLCSException("Required Shibboleth attribute "
                        + requiredAttributeName + " missing");
            }
        }
    }

    /**
     * @return the auditor.
     */
    protected Auditor getAuditor() {
        return auditor_;
    }

    /**
     * @return the SLCS server configuration.
     */
    protected SLCSServerConfiguration getSLCSServerConfiguration() {
        return configuration_;
    }

    /**
     * @return the SLCS sessions.
     */
    protected SLCSSessions getSLCSSessions() {
        return sessions_;
    }

    /**
     * Returns the XML declaration header:
     * 
     * <pre>
     *      &lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot; ?&gt;
     * </pre>
     * 
     * @return The XML declation header.
     */
    protected String getXMLDeclaration() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
    }
}
