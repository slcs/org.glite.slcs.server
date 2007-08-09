/*
 * $Id: LoginServlet.java,v 1.5 2007/08/09 13:17:54 vtschopp Exp $
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html 
 */
package org.glite.slcs.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glite.slcs.SLCSException;
import org.glite.slcs.attribute.Attribute;
import org.glite.slcs.audit.event.AuditEvent;
import org.glite.slcs.audit.event.AuthorizationEvent;
import org.glite.slcs.audit.event.SystemEvent;
import org.glite.slcs.dn.DNBuilder;
import org.glite.slcs.dn.DNBuilderFactory;
import org.glite.slcs.pki.CertificateExtension;
import org.glite.slcs.policy.CertificatePolicy;
import org.glite.slcs.policy.CertificatePolicyFactory;
import org.glite.slcs.session.SLCSSession;
import org.glite.slcs.session.SLCSSessions;

/**
 * Servlet implementation class for Servlet: LoginServlet
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision: 1.5 $
 */
public class LoginServlet extends AbstractServlet implements
        javax.servlet.Servlet {

    private static final long serialVersionUID = -2351607540747614544L;

    /** Logging */
    static final Log LOG = LogFactory.getLog(LoginServlet.class);

    /** DNBuilder */
    private DNBuilder dnBuilder_ = null;

    /** Certificate Policy */
    private CertificatePolicy certificatePolicy_ = null;

    /**
     * Default constructor.
     */
    public LoginServlet() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.servlet.AbstractServlet#init(javax.servlet.ServletConfig)
     */
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        try {

            LOG.info("instantiate DNBuilder...");
            dnBuilder_ = DNBuilderFactory.getInstance();
            registerSLCSServerComponent(dnBuilder_);

            LOG.info("instantiate CertificatePolicy...");
            certificatePolicy_ = CertificatePolicyFactory.getInstance();
            registerSLCSServerComponent(certificatePolicy_);

        } catch (SLCSException e) {
            LOG.error("Servlet init failed", e);
            throw new ServletException(e);
        }

    }

    protected void doProcess(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        LOG.debug("doProcess...");

        // read AAI Shibboleth attributes
        List userAttributes = getUserAttributes(req);
        // read the remote IP and UserAgent
        Attribute remoteAddress = getRemoteAddressAttribute(req);
        userAttributes.add(remoteAddress);
        Attribute userAgent = getUserAgentAttribute(req);
        userAttributes.add(userAgent);

        try {
            // check required attributes
            checkRequiredAttributes(userAttributes);

            AuditEvent login = new AuthorizationEvent("User login", userAttributes);
            getAuditor().logEvent(login);

            // create a new DN
            DNBuilder builder = getDNBuilder();
            String dn = builder.createDN(userAttributes);
            // store the new DN in sessions and get authorization token
            SLCSSessions sessions = getSLCSSessions();
            SLCSSession session = sessions.createSession(dn);
            // store attributes in session
            session.setAttributes(userAttributes);

            if (LOG.isDebugEnabled()) {
                LOG.debug("Session created: " + session);
            }

            String authToken = session.getToken();
            CertificatePolicy policy = getCertificatePolicy();
            List extensions = policy.getRequiredCertificateExtensions(userAttributes);
            String reqUrl = getContextUrl(req, "/certificate");
            sendLoginResponse(req, res, authToken, reqUrl, dn, extensions);

        } catch (SLCSException e) {
            LOG.error("Processing error: " + e);
            sendXMLErrorResponse(req, res, "SLCSLoginResponse", e.getMessage(), e);

            try {
                SystemEvent error = new SystemEvent(AuditEvent.LEVEL_ERROR, e.getMessage(), userAttributes);
                getAuditor().logEvent(error);
            } catch (SLCSException e1) {
                LOG.error("Audit error: " + e1);
            }

        }

    }

    /**
     * @return The implementation of the {@link DNBuilder} interface
     */
    protected DNBuilder getDNBuilder() {
        return dnBuilder_;
    }

    /**
     * @return The implementation of the {@link CertificatePolicy} interface
     */
    protected CertificatePolicy getCertificatePolicy() {
        return certificatePolicy_;
    }

    /**
     * Sends a XML SLCSLoginResponse back to the client.
     * 
     * <pre>
     *     &lt;SLCSLoginResponse&gt;
     *        &lt;Status&gt;Success&lt;/Status&gt;
     *        &lt;AuthorizationToken&gt;401B4A42F472565E84194BA03C0854B5DF44D22E8F34046810D605A03FAB8D85&lt;/AuthorizationToken&gt;
     *        &lt;CertificateRequest url=&quot;https://hestia.switch.ch:443/SLCS/certificate&quot;&gt;
     *           &lt;Subject&gt;CN=Tschopp Valery 9FEE5EE3,O=Switch - Teleinformatikdienste fuer Lehre und Forschung,C=CH&lt;/Subject&gt;
     *           &lt;CertificateExtension name=&quot;CertificatePolicies&quot; oid=&quot;2.5.29.32&quot; critical=&quot;false&quot;&gt;2.16.756.1.2.6.3&lt;/CertificateExtension&gt;
     *           &lt;CertificateExtension name=&quot;ExtendedKeyUsage&quot; oid=&quot;2.5.29.37&quot; critical=&quot;false&quot;&gt;ClientAuth&lt;/CertificateExtension&gt;
     *           &lt;CertificateExtension name=&quot;KeyUsage&quot; oid=&quot;2.5.29.15&quot; critical=&quot;true&quot;&gt;DigitalSignature,KeyEncipherment&lt;/CertificateExtension&gt;
     *           &lt;CertificateExtension name=&quot;SubjectAltName&quot; oid=&quot;2.5.29.17&quot; critical=&quot;false&quot;&gt;email:tschopp@switch.ch&lt;/CertificateExtension&gt;
     *        &lt;/CertificateRequest&gt;
     *     &lt;/SLCSLoginResponse&gt;
     * </pre>
     * 
     * @param req
     *            The HttpServletRequest object
     * @param res
     *            The HttpServletResponse object
     * @param authToken
     *            The authorization token
     * @param certDN
     *            The certificate subject (DN)
     * @param certExtensions
     *            List of CertificateExtension required by the CertificatePolicy
     * @throws IOException
     *             If an error occurs while writing the response.
     */
    protected void sendLoginResponse(HttpServletRequest req,
            HttpServletResponse res, String authToken, String requestURL,
            String certDN, List certExtensions) throws IOException,
            ServletException {
        // build response
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.println(getXMLDeclaration());

        // send response
        pw.println("<SLCSLoginResponse>");
        pw.println("<Status>Success</Status>");
        pw.print("<AuthorizationToken>");
        pw.print(authToken);
        pw.println("</AuthorizationToken>");
        // request URL
        pw.print("<CertificateRequest url=\"");
        pw.print(requestURL);
        pw.println("\">");
        pw.print("<Subject>");
        pw.print(certDN);
        pw.println("</Subject>");

        if (certExtensions != null && !certExtensions.isEmpty()) {
            // add certificate extensions
            Iterator extensions = certExtensions.iterator();
            while (extensions.hasNext()) {
                CertificateExtension extension = (CertificateExtension) extensions.next();
                pw.println(extension.toXML());
            }
        }
        pw.println("</CertificateRequest>");
        pw.println("</SLCSLoginResponse>");
        if (LOG.isDebugEnabled()) {
            LOG.debug("sending SLCSLoginResponse:\n" + sw.getBuffer().toString());
        }
        // write response back to client
        res.setContentType("text/xml");
        PrintWriter out = res.getWriter();
        out.println(sw.getBuffer().toString());
        out.close();
    }

}