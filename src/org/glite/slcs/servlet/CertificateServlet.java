/*
 * $Id: CertificateServlet.java,v 1.4 2007/08/09 13:32:04 vtschopp Exp $
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html 
 */
package org.glite.slcs.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.GeneralSecurityException;
import java.security.Principal;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glite.slcs.SLCSException;
import org.glite.slcs.audit.event.AuditEvent;
import org.glite.slcs.audit.event.CertificateEvent;
import org.glite.slcs.caclient.CAClient;
import org.glite.slcs.caclient.CAClientFactory;
import org.glite.slcs.caclient.CAConnection;
import org.glite.slcs.caclient.CARequest;
import org.glite.slcs.caclient.CAResponse;
import org.glite.slcs.pki.Certificate;
import org.glite.slcs.pki.CertificateRequest;
import org.glite.slcs.policy.CertificatePolicy;
import org.glite.slcs.policy.CertificatePolicyFactory;
import org.glite.slcs.session.SLCSSession;
import org.glite.slcs.session.SLCSSessions;

/**
 * Servlet implementation class for Servlet: CertificateServlet
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision: 1.4 $
 */
public class CertificateServlet extends AbstractServlet {

    /** serial version */
    private static final long serialVersionUID = -8237981418682802857L;

    /** Logging */
    private static Log LOG = LogFactory.getLog(CertificateServlet.class);

    /** Online CA client */
    private CAClient caClient_ = null;

    /** Certificate policy */
    private CertificatePolicy certificatePolicy_ = null;

    /**
     * Default constructor.
     */
    public CertificateServlet() {
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
            // get the online CA client
            LOG.info("instantiate CAClient...");
            caClient_ = CAClientFactory.getInstance();
            registerSLCSServerComponent(caClient_);

            LOG.info("instantiate CertificatePolicy...");
            certificatePolicy_ = CertificatePolicyFactory.getInstance();
            registerSLCSServerComponent(certificatePolicy_);

        } catch (SLCSException e) {
            LOG.error("Servlet init failed", e);
            throw new ServletException(e);
        }

    }

    /**
     * Processes the certificate request.
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void doProcess(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        LOG.debug("doProcess...");
        try {

            // get parameters
            String authorizationToken = getSLCSCertificateRequestParameter(request, "AuthorizationToken");
            String pemCertificateRequest = getSLCSCertificateRequestParameter(request, "CertificateSigningRequest");
            LOG.info("AuthorizationToken=" + authorizationToken);
            LOG.info("CertificateSigningRequest=\n" + pemCertificateRequest);

            // decode PEM certificate signing request
            CertificateRequest certificateRequest = null;
            try {
                StringReader reader = new StringReader(pemCertificateRequest);
                certificateRequest = CertificateRequest.readPEM(reader);
            } catch (GeneralSecurityException e) {
                LOG.error("Failed to rebuild the PEM CertificateSigningRequest", e);
                throw new SLCSException("Failed to decode PEM CertificateSigningRequest", e);
            }

            // check SLCS sessions
            Principal principal = certificateRequest.getPrincipal();
            String certificateSubject = principal.getName();
            LOG.debug("Subject=" + certificateSubject);

            AuditEvent newRequest = new CertificateEvent("New certificate request: "
                    + certificateSubject);
            getAuditor().logEvent(newRequest);

            LOG.debug("check session...");
            SLCSSessions sessions = getSLCSSessions();
            SLCSSession session = sessions.getSession(authorizationToken, certificateSubject);
            if (session != null && session.isValid()) {
                LOG.debug( session + " is valid");

                // check certificate against policy
                LOG.debug("check certificate request against policy...");
                List userAttributes = session.getAttributes();
                CertificatePolicy policy = getCertificatePolicy();
                if (policy.isCertificateRequestValid(certificateRequest, userAttributes)) {

                    // request certificate
                    LOG.debug("get CA connection");
                    CAClient onlineCA= getCAClient();
                    CAConnection connection = onlineCA.getConnection();
                    LOG.debug("create CA request");
                    CARequest csrRequest = connection.createRequest(certificateRequest);
                    LOG.info("send certificate request to CA server");
                    connection.sendRequest(csrRequest);
                    LOG.info("read CA server response");
                    CAResponse csrResponse = connection.getResponse();
                    LOG.debug("get certificate");
                    Certificate cert = csrResponse.getCertificate(principal);

                    // send response
                    sendXMLCerfificateResponse(request, response, certificateSubject, cert);
                }
                else {
                    throw new SLCSException("CertificateSigningRequest is not conform to CertificatePolicy");
                }
            }
            else {
                throw new SLCSException("SLCSSession: " + authorizationToken
                        + "," + certificateSubject + " does not exists or is expired");
            }

        } catch (SLCSException e) {
            LOG.error(e);
            
            //TODO: audit error
            
            sendXMLErrorResponse(request, response, "SLCSCertificateResponse", e.getMessage(), e);
        }

    }

    /**
     * Checks if the SLCSCertificateRequest contains a no empty parameter and
     * returns the value.
     * 
     * @param request
     *            The HttpServletRequest object.
     * @param name
     *            The request parameter name to check.
     * @return The parameter value.
     * @throws SLCSException
     *             If the parameter is not present or if the value is empty.
     */
    protected String getSLCSCertificateRequestParameter(
            HttpServletRequest request, String name) throws SLCSException {
        String value = request.getParameter(name);
        if (value == null || value.equals("")) {
            LOG.error("Request parameter " + name + " is missing or empty.");
            throw new SLCSException("Request parameter " + name
                    + " is missing or empty");
        }
        return value;
    }

    /**
     * Sends a SLCSCertificateResponse back to the client.
     * 
     * @param req
     *            The HttpServerRequest object
     * @param res
     *            The HttpServletResponse object.
     * @param subject
     *            The certificate subject (DN).
     * @param certificate
     *            The Certificate object.
     * @throws IOException
     *             If an error occurs while sending the response.
     */
    protected void sendXMLCerfificateResponse(HttpServletRequest req,
            HttpServletResponse res, String dn, Certificate certificate)
            throws IOException {
        // build response
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.println(getXMLDeclaration());
        pw.println("<SLCSCertificateResponse>");
        pw.println("<Status>Success</Status>");
        pw.println("<Subject>" + dn + "</Subject>");
        String pemCertificate = certificate.getPEM();
        pw.println("<Certificate format=\"PEM\">" + pemCertificate
                + "</Certificate>");
        pw.println("</SLCSCertificateResponse>");

        LOG.info("SLCSCertificateResponse:\n" + sw.getBuffer().toString());

        // write response back to client
        res.setContentType("text/xml");
        PrintWriter out = res.getWriter();
        out.println(sw.getBuffer().toString());
        out.close();

    }
    
    /**
     * @return The implementation of the {@link CertificatePolicy} interface
     */
    protected CertificatePolicy getCertificatePolicy() {
        return certificatePolicy_;
    }

    /**
     * @return The implementation of the {@link CAClient} interface
     */
    protected CAClient getCAClient() {
        return caClient_;
    }
    
    
}