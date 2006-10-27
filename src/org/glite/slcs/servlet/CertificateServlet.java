package org.glite.slcs.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.GeneralSecurityException;
import java.security.Principal;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.glite.slcs.session.SLCSSessions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Servlet implementation class for Servlet: CertificateServlet
 * 
 * @web.servlet name="CertificateServlet" display-name="CertificateServlet"
 * 
 * @web.servlet-mapping url-pattern="/certificate"
 * 
 */
public class CertificateServlet extends AbstractSLCSServlet {

    /** serial version */
    private static final long serialVersionUID= -8237981418682802857L;

    /** Logging */
    private static Log LOG= LogFactory.getLog(CertificateServlet.class);

    /** Online CA client */
    private CAClient onlineCA_= null;

    /** Certificate policy */
    private CertificatePolicy policy_= null;

    /**
     * Default constructor.
     */
    public CertificateServlet() {
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
            // get the online CA client
            LOG.info("instantiate CAClient...");
            onlineCA_= CAClientFactory.getInstance();
            registerSLCSServerComponent(onlineCA_);

            LOG.info("instantiate CertificatePolicy...");
            policy_= CertificatePolicyFactory.getInstance();
            registerSLCSServerComponent(policy_);

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
            String authorizationToken= getSLCSCertificateRequestParameter(request,
                                                                          "AuthorizationToken");
            String pemCertificateRequest= getSLCSCertificateRequestParameter(request,
                                                                             "CertificateSigningRequest");
            LOG.info("AuthorizationToken=" + authorizationToken);
            LOG.info("CertificateSigningRequest=\n" + pemCertificateRequest);

            // decode PEM certificate signing request
            CertificateRequest certificateRequest= null;
            try {
                StringReader reader= new StringReader(pemCertificateRequest);
                certificateRequest= CertificateRequest.readPEM(reader);
            } catch (GeneralSecurityException e) {
                LOG.error("Failed to rebuild the PEM CertificateSigningRequest",
                          e);
                throw new SLCSException("Failed to decode PEM CertificateSigningRequest",
                                        e);
            }

            // check SLCS sessions
            Principal principal= certificateRequest.getPrincipal();
            String certificateSubject= principal.getName();
            LOG.debug("Subject=" + certificateSubject);

            AuditEvent newRequest= new CertificateEvent("New certificate request: "
                    + certificateSubject);
            getAuditor().logEvent(newRequest);

            LOG.debug("check session...");
            SLCSSessions sessions= getSLCSSessions();
            if (sessions.isSessionValid(authorizationToken, certificateSubject)) {
                LOG.debug("SLCSSession(" + authorizationToken + ","
                        + certificateSubject + ") is valid");

                // check certificate against policy
                LOG.debug("check certificate request against policy...");
                Map userAttributes= sessions.getAttributes(authorizationToken);
                if (policy_.isCertificateRequestValid(certificateRequest,
                                                      userAttributes)) {

                    // request certificate
                    LOG.debug("get CA connection");
                    CAConnection connection= onlineCA_.getConnection();
                    LOG.debug("create CA request");
                    CARequest csrRequest= connection.createRequest(certificateRequest);
                    LOG.debug("send CA request");
                    connection.sendRequest(csrRequest);
                    LOG.debug("get CA response");
                    CAResponse csrResponse= connection.getResponse();
                    LOG.debug("get certificate");
                    Certificate cert= csrResponse.getCertificate(principal);

                    // send response
                    sendXMLCerfificateResponse(response,
                                               certificateSubject,
                                               cert);
                }
                else {
                    throw new SLCSException("CertificateSigningRequest is not conform to CertificatePolicy");
                }
            }
            else {
                throw new SLCSException("SLCSSession:" + authorizationToken
                        + "," + certificateSubject + " is not valid");
            }

        } catch (SLCSException e) {
            LOG.error(e);
            sendXMLErrorResponse(response,
                                 "SLCSCertificateResponse",
                                 e.getMessage(),
                                 e);
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
        String value= request.getParameter(name);
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
     * @param res
     *            The HttpServletResponse object.
     * @param subject
     *            The certificate subject (DN).
     * @param certificate
     *            The Certificate object.
     * @throws IOException
     *             If an error occurs while sending the response.
     */
    protected void sendXMLCerfificateResponse(HttpServletResponse res,
            String dn, Certificate certificate) throws IOException {
        // build response
        StringWriter sw= new StringWriter();
        PrintWriter pw= new PrintWriter(sw);
        pw.println(getXMLDeclaration());
        pw.println("<SLCSCertificateResponse>");
        pw.println("<Status>Success</Status>");
        pw.println("<Subject>" + dn + "</Subject>");
        String pemCertificate= certificate.getPEM();
        pw.println("<Certificate format=\"PEM\">" + pemCertificate
                + "</Certificate>");
        pw.println("</SLCSCertificateResponse>");

        LOG.info("SLCSCertificateResponse:\n" + sw.getBuffer().toString());

        // write response back to client
        res.setContentType("text/xml");
        PrintWriter out= res.getWriter();
        out.println(sw.getBuffer().toString());
        out.close();

    }

}