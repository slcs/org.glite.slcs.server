/*
 * $Id: AbstractSLCSServlet.java,v 1.1 2006/10/27 12:11:24 vtschopp Exp $
 * 
 * Created on Sep 17, 2006 by Valery Tschopp <tschopp@switch.ch>
 *
 * Copyright (c) 2006 SWITCH - http://www.switch.ch/
 */
package org.glite.slcs.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.glite.slcs.SLCSException;
import org.glite.slcs.SLCSServerComponent;
import org.glite.slcs.audit.Auditor;
import org.glite.slcs.audit.AuditorFactory;
import org.glite.slcs.config.SLCSServerConfiguration;
import org.glite.slcs.session.SLCSSessions;
import org.glite.slcs.session.SLCSSessionsFactory;
import org.glite.slcs.util.Utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * AbstractSLCSServlet
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.1 $
 */
public abstract class AbstractSLCSServlet extends HttpServlet {

    /** Logging */
    private static Log LOG= LogFactory.getLog(AbstractSLCSServlet.class);

    /** SLCS Server configuration */
    private SLCSServerConfiguration configuration_= null;

    /** SCLS Sessions */
    private SLCSSessions sessions_= null;

    /** Auditor */
    private Auditor auditor_= null;

    /** List of registered SLCSComponents */
    private Vector components_= new Vector();

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
            configuration_= SLCSServerConfiguration.getInstance();

            LOG.info("instantiate SCLSSessions...");
            sessions_= SLCSSessionsFactory.getInstance();
            registerSLCSServerComponent(sessions_);

            LOG.info("instantiate Auditor...");
            auditor_= AuditorFactory.getInstance();
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
        Enumeration serverComponents= components_.elements();
        while (serverComponents.hasMoreElements()) {
            SLCSServerComponent component= (SLCSServerComponent) serverComponents.nextElement();
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
    protected String getServletUrl(HttpServletRequest req, String servletPath) {
        StringBuffer sb= new StringBuffer();
        sb.append(req.getScheme()).append("://");
        sb.append(req.getServerName()).append(':').append(req.getServerPort());
        sb.append(req.getContextPath());
        sb.append(servletPath);
        return sb.toString();
    }

    /**
     * Return the Map of required user's Shibboleth attributes name and value.
     * The required attributes are defined in a List of required attribute
     * names.
     * 
     * @param req
     *            The HttpServletRequest object
     * @param requiredShibbolethAttributeNames
     *            List of required Shibboleth attribute names.
     * @return The user's Shibboleth attributes name-value map
     */
    protected Map getShibbolethAttributes(HttpServletRequest req,
            List requiredShibbolethAttributeNames) {
        Map requiredAttributes= new HashMap();
        Enumeration headers= req.getHeaderNames();
        while (headers.hasMoreElements()) {
            String header= (String) headers.nextElement();
            if (requiredShibbolethAttributeNames.contains(header)) {
                String shibUTF8= req.getHeader(header);
                if (shibUTF8 != null && !shibUTF8.equals("")) {
                    // convert Shibboleth UTF8 to unicode
                    String value= Utils.convertShibbolethUTF8ToUnicode(shibUTF8);
                    requiredAttributes.put(header, value);
                    LOG.debug("Shibboleth attribute: " + header + "=" + value);
                }

            }
        }
        return requiredAttributes;
    }

    /**
     * Returns a Map of all user's Shibboleth attribute name and value. A
     * Shibboleth attribute name is prefixed with 'Shib-'.
     * 
     * @param req
     *            The HttpServletRequest object.
     * @return The Map of all Shibboleth attributes.
     */
    protected Map getShibbolethAttributes(HttpServletRequest req) {
        Map allAttributes= new HashMap();
        Enumeration headers= req.getHeaderNames();
        while (headers.hasMoreElements()) {
            String header= (String) headers.nextElement();
            if (header.startsWith("Shib-")) {
                String shibValue= req.getHeader(header);
                if (shibValue != null && !shibValue.equals("")) {
                    // convert Shibboleth UTF8 to unicode
                    String value= Utils.convertShibbolethUTF8ToUnicode(shibValue);
                    LOG.info("Shibboleth attribute: " + header + "=" + value);
                    allAttributes.put(header, value);
                }

            }
        }
        return allAttributes;
    }

    /**
     * Returns the User-Agent header.
     * 
     * @param req
     *            The HttpServletRequest object
     * @return The User-Agent string or <code>null</code> if not set.
     */
    protected String getUserAgent(HttpServletRequest req) {
        return req.getHeader("User-Agent");
    }

    /**
     * Sends a XML error message back to the client.
     * 
     * @param res
     *            The HttpServletResponse object.
     * @param name
     *            The name of the response.
     * @param message
     *            The error message.
     * @param cause
     *            The error cause.
     * @throws IOException
     *             If an error occurs while sending the error.
     */
    protected void sendXMLErrorResponse(HttpServletResponse res, String name,
            String message, Throwable cause) throws IOException {
        StringWriter sw= new StringWriter();
        PrintWriter pw= new PrintWriter(sw);
        pw.println(getXMLDeclaration());
        pw.print("<");
        pw.print(name);
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
        pw.print(name);
        pw.println(">");
        
        LOG.info("sending " + name + ":\n" + sw.getBuffer().toString());

        // send response
        res.setContentType("text/xml");
        PrintWriter out= res.getWriter();
        out.println(sw.getBuffer().toString());
        out.close();
    }

    /**
     * @return List of required Shibboleth attribute names.
     */
    protected List getRequiredShibbolethAttributeNames() {
        List requiredAttributeNames= configuration_.getList("ShibbolethConfiguration.RequiredAttributeName");
        return requiredAttributeNames;
    }

    /**
     * Checks if all required Shibboleth attributes are present.
     * 
     * @param requiredShibbolethAttributeNames
     *            List of required Shibboleth attribute name.
     * @param shibbolethAttributes
     *            Map of user's Shibboleth attributes.
     * @throws SLCSException
     *             If a required Shibboleth attribute is missing
     */
    protected void checkRequiredShibbolethAttributes(
            List requiredShibbolethAttributeNames, Map shibbolethAttributes)
            throws SLCSException {
        Iterator requiredAttributeNames= requiredShibbolethAttributeNames.iterator();
        while (requiredAttributeNames.hasNext()) {
            String requiredAttributeName= (String) requiredAttributeNames.next();
            if (!shibbolethAttributes.containsKey(requiredAttributeName)) {
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
     * &lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot; ?&gt;
     * </pre>
     * 
     * @return The XML declation header.
     */
    protected String getXMLDeclaration() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
    }
}
