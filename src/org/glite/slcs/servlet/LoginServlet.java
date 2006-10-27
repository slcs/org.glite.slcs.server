package org.glite.slcs.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.glite.slcs.SLCSException;
import org.glite.slcs.audit.event.AuditEvent;
import org.glite.slcs.audit.event.AuthorizationEvent;
import org.glite.slcs.audit.event.SystemEvent;
import org.glite.slcs.dn.DNBuilder;
import org.glite.slcs.dn.DNBuilderFactory;
import org.glite.slcs.pki.CertificateExtension;
import org.glite.slcs.policy.CertificatePolicy;
import org.glite.slcs.policy.CertificatePolicyFactory;
import org.glite.slcs.session.SLCSSessions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Servlet implementation class for Servlet: LoginServlet
 * 
 * @web.servlet name="LoginServlet" display-name="LoginServlet"
 * 
 * @web.servlet-mapping url-pattern="/login"
 * 
 */
public class LoginServlet extends AbstractSLCSServlet implements
        javax.servlet.Servlet {

    private static final long serialVersionUID= -2351607540747614544L;

    /** Logging */
    static final Log LOG= LogFactory.getLog(LoginServlet.class);

    /** Required Shibboleth attributes */
    List requiredShibbolethAttributeNames_= null;

    /** DNBuilder */
    private DNBuilder dnBuilder_= null;

    /** Certificate Policy */
    private CertificatePolicy policy_= null;

    /**
     * Default constructor.
     */
    public LoginServlet() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.servlet.AbstractSLCSServlet#init(javax.servlet.ServletConfig)
     */
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        try {

            LOG.info("instantiate DNBuilder...");
            dnBuilder_= DNBuilderFactory.getInstance();
            registerSLCSServerComponent(dnBuilder_);

            LOG.info("instantiate CertificatePolicy...");
            policy_= CertificatePolicyFactory.getInstance();
            registerSLCSServerComponent(policy_);

            requiredShibbolethAttributeNames_= getRequiredShibbolethAttributeNames();
            LOG.info("RequiredShibbolethAttributeNames="
                    + requiredShibbolethAttributeNames_);

        } catch (SLCSException e) {
            LOG.error("Servlet init failed", e);
            throw new ServletException(e);
        }

    }

    protected void doProcess(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        LOG.debug("doProcess...");

        // read AAI Shibboleth attributes
        Map userAttributes= getShibbolethAttributes(req,
                                                    requiredShibbolethAttributeNames_);
        // read the remote IP and UserAgent
        String remoteAddress= req.getRemoteAddr();
        String userAgent= getUserAgent(req);
        userAttributes.put("UserAgent", userAgent);
        userAttributes.put("RemoteAddress", remoteAddress);
        
        try {
            // check required attributes
            checkRequiredShibbolethAttributes(requiredShibbolethAttributeNames_,
                                              userAttributes);

            AuditEvent login= new AuthorizationEvent("User login", userAttributes);
            getAuditor().logEvent(login);

            // create a new DN
            String dn= dnBuilder_.createDN(userAttributes);
            // store the new DN in sessions and get authorization token
            SLCSSessions sessions= getSLCSSessions();
            String token= sessions.createSession(dn);
            // store attributes in session
            sessions.setAttributes(token, userAttributes);

            List extensions= policy_.getRequiredCertificateExtensions(userAttributes);
            String reqUrl= getServletUrl(req, "/certificate");

            sendXMLLoginResponse(res, token, reqUrl, dn, extensions);

        } catch (SLCSException e) {
            LOG.error("Processing error: " + e);
            sendXMLErrorResponse(res, "SLCSLoginResponse", e.getMessage(), e);

            try {
                SystemEvent error= new SystemEvent(AuditEvent.LEVEL_ERROR, e.getMessage(), userAttributes);
                getAuditor().logEvent(error);
            } catch (SLCSException e1) {
                LOG.error("Audit error: " + e1);
            }
            
        }

    }

    /**
     * Sends a XML SLCSLoginResponse back to the client.
     * 
     * <pre>
     *  &lt;SLCSLoginResponse&gt;
     *     &lt;Status&gt;Success&lt;/Status&gt;
     *     &lt;AuthorizationToken&gt;401B4A42F472565E84194BA03C0854B5DF44D22E8F34046810D605A03FAB8D85&lt;/AuthorizationToken&gt;
     *     &lt;CertificateRequest url=&quot;https://hestia.switch.ch:443/SLCS/certificate&quot;&gt;
     *        &lt;Subject&gt;CN=Tschopp Valery 9FEE5EE3,O=Switch - Teleinformatikdienste fuer Lehre und Forschung,C=CH&lt;/Subject&gt;
     *        &lt;CertificateExtension name=&quot;CertificatePolicies&quot; oid=&quot;2.5.29.32&quot; critical=&quot;false&quot;&gt;2.16.756.1.2.6.3&lt;/CertificateExtension&gt;
     *        &lt;CertificateExtension name=&quot;ExtendedKeyUsage&quot; oid=&quot;2.5.29.37&quot; critical=&quot;false&quot;&gt;ClientAuth&lt;/CertificateExtension&gt;
     *        &lt;CertificateExtension name=&quot;KeyUsage&quot; oid=&quot;2.5.29.15&quot; critical=&quot;true&quot;&gt;DigitalSignature,KeyEncipherment&lt;/CertificateExtension&gt;
     *        &lt;CertificateExtension name=&quot;SubjectAltName&quot; oid=&quot;2.5.29.17&quot; critical=&quot;false&quot;&gt;email:tschopp@switch.ch&lt;/CertificateExtension&gt;
     *     &lt;/CertificateRequest&gt;
     *  &lt;/SLCSLoginResponse&gt;
     * </pre>
     * 
     * @param res
     *            The HttpServerResponse object
     * @param authToken
     *            The authorization token
     * @param certDN
     *            The certificate subject (DN)
     * @param certExtensions
     *            List of CertificateExtension required by the CertificatePolicy
     * @throws IOException
     *             If an error occurs while writing the response.
     */
    protected void sendXMLLoginResponse(HttpServletResponse res,
            String authToken, String requestURL, String certDN,
            List certExtensions) throws IOException {
        // build response
        StringWriter sw= new StringWriter();
        PrintWriter pw= new PrintWriter(sw);
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
            Iterator extensions= certExtensions.iterator();
            while (extensions.hasNext()) {
                CertificateExtension extension= (CertificateExtension) extensions.next();
                pw.println(extension.toXML());
            }
        }
        pw.println("</CertificateRequest>");
        pw.println("</SLCSLoginResponse>");
        LOG.info("sending SLCSLoginResponse:\n" + sw.getBuffer().toString());

        // write response back to client
        res.setContentType("text/xml");
        PrintWriter out= res.getWriter();
        out.println(sw.getBuffer().toString());
        out.close();
    }

}