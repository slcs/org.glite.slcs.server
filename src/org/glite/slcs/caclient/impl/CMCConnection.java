/*
 * $Id: CMCConnection.java,v 1.1 2006/10/27 12:11:23 vtschopp Exp $
 * 
 * Created on Jun 21, 2006 by tschopp
 *
 * Copyright (c) 2006 SWITCH - http://www.switch.ch/
 */
package org.glite.slcs.caclient.impl;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.glite.slcs.SLCSException;
import org.glite.slcs.caclient.CARequest;
import org.glite.slcs.caclient.CAResponse;
import org.glite.slcs.pki.CertificateRequest;
import org.glite.slcs.util.Base64;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.multipart.ByteArrayPartSource;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.PartSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * CMCConnection implements the CAConnection and represents a connection
 * (session) used to create, send and receive request to a RFC2797 compliant
 * online CA server.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.1 $
 */
public class CMCConnection implements PKIConnection {

    /** Logging */
    static private Log LOG= LogFactory.getLog(CMCConnection.class);

    /** the CMCClient associated with this connection */
    private CMCClient cmcClient_;

    /** the HTTP POST method used to submit the request */
    private PostMethod cmcServerPost_;

    /** The request being send */
    private CARequest request_= null;

    /** The response for the request */
    private CAResponse response_= null;

    /**
     * Constructor
     * 
     * @param client
     *            The CMCClient object.
     * @param url
     *            The URL for the POST method.
     */
    protected CMCConnection(CMCClient client, String url) {
        this.cmcClient_= client;
        this.cmcServerPost_= createPostMethod(url);
    }

    /**
     * Creates a Http POST method for the given URL.
     * 
     * @param url
     *            The URL to POST to.
     * @return The new Http POST method.
     */
    static private PostMethod createPostMethod(String url) {
        return new PostMethod(url);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.caclient.CAConnection#createRequest(org.glite.slcs.pki.CertificateRequest)
     */
    public CARequest createRequest(CertificateRequest csr) throws SLCSException {
        return new SimplePKIRequest(csr);
    }

    private int postPKIRequest() throws IOException {
        HttpClient httpClient= cmcClient_.getHttpClient();
        PostMethod postRequestMethod= getPostMethod();
        int status= httpClient.executeMethod(postRequestMethod);
        return status;
    }

    /**
     * @return The Http POST method used to submit the request.
     */
    private PostMethod getPostMethod() {
        return cmcServerPost_;
    }

    /**
     * Releases the connection associated with the Http POST method.
     */
    private void releaseHttpMethod() {
        LOG.trace("release POST method connection");
        getPostMethod().releaseConnection();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.caclient.CAConnection#getResponse()
     */
    public CAResponse getResponse() throws CMCException {
        return response_;
    }

    /**
     * @return The request object.
     */
    private CARequest getRequest() {
        return request_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.caclient.CAConnection#sendRequest(org.glite.slcs.caclient.CARequest)
     */
    public void sendRequest(CARequest request) throws CMCException {

        this.request_= request;

        try {

            preparePKIRequest();

            try {
                postPKIRequest();
            } catch (IOException e) {
                LOG.error("Failed to POST the PKIRequest", e);
                throw new CMCException("Failed to POST the PKIRequest", e);
            }

            checkResponseHeaders();

            // TODO: checkResponseContent();

            extractPKIResponse();

        } finally {
            // release the underlying HttpClient connection.
            releaseHttpMethod();
        }
    }

    /**
     * Exctracts the Simple Enrollment Reponse from the online CA server
     * reponse.
     * 
     * @throws CMCException
     *             If an error occurs while parsing the reponse.
     */
    private void extractPKIResponse() throws CMCException {
        try {
            // read response body
            byte[] body= getPostMethod().getResponseBody();
            if (body == null || body.length == 0) {
                LOG.error("Body is empty");
                throw new CMCException("Response body is empty");
            }
            // decode base64
            byte[] pkiResponseBytes= Base64.decode(body);
            // create PKI response
            this.response_= new SimplePKIResponse(pkiResponseBytes);
        } catch (IOException e) {
            LOG.error("Can not read response body", e);
            throw new CMCException("Failed to extract the PKIResponse", e);
        } catch (GeneralSecurityException e) {
            LOG.error("Can not create SimplePKIResponse", e);
            throw new CMCException("Failed to create a SimplePKIResponse", e);
        }

    }

    /**
     * Prepares the request for Http POST method. Sends the request as a RFC2797
     * compliant multi-part request.
     * 
     * @throws CMCException
     *             If an error occurs while preparing the request.
     */
    private void preparePKIRequest() throws CMCException {
        // multi-part rfc2797 PKIX-CMC request
        // SMIME format
        CARequest request= getRequest();
        if (request instanceof PKIRequest) {

            byte[] derBytes= request.getDEREncoded();
            // SMIME is Base64 encoded
            String base64String= Base64.encode(derBytes);
            // multi part request
            byte[] base64Bytes= base64String.getBytes();
            String filename= "cmc_req"
                    + ((PKIRequest) request).getFilenameExtension();
            PartSource source= new ByteArrayPartSource(filename, base64Bytes);
            FilePart fp= new FilePart("rfc2797",
                                      source,
                                      ((PKIRequest) request).getMimeType(),
                                      null);
            fp.setTransferEncoding("base64");
            Part[] parts= { fp };
            PostMethod post= getPostMethod();
            RequestEntity re= new MultipartRequestEntity(parts,
                                                         post.getParams());
            post.setRequestEntity(re);
        }
        else {
            LOG.error("Unsupported CARequest: " + request.getClass().getName());
            throw new CMCException("Unsupported CARequest: "
                    + request.getClass().getName());
        }
    }

    /**
     * Checks the online CA server reponse for a valid Simple Enrollment
     * Response (RFC2797). The reponse headers are being check for Content-Type,
     * Content-Disposition and Content-Transfert-Encoding.
     * 
     * @throws CMCException
     *             If the reponse doesn't contain a valid RFC2797 Simple
     *             Enrollment Response.
     */
    private void checkResponseHeaders() throws CMCException {
        // check Content-Type: application/pkcs7-mime
        Header contentType= getPostMethod().getResponseHeader("Content-Type");
        if (contentType == null) {
            LOG.error("HTTP Response doesn't contain header: Content-Type");
            throw new CMCException("HTTP Response doesn't contain header: Content-Type");
        }
        String type= contentType.getValue();
        LOG.debug("Content-Type: " + type);
        if (!type.startsWith(SimplePKIResponse.MIME_TYPE)) {
            LOG.error("Invalid Content-Type in HTTP response header: " + type);
            throw new CMCException("Invalid Content-Type in HTTP response header: "
                    + type);
        }
        // TODO: check filename extension in Content-Type or Content-Disposition

        // TODO: check Content-Transfer-Encoding: base64
        // Header contentTransferEncoding=
        // getPostMethod().getResponseHeader("Content-Transfer-Encoding");
        // if (contentTransferEncoding == null) {
        // throw new CMCException("HTTP Response doesn't contain header:
        // Content-Transfer-Encoding");
        // }
        // String encoding= contentTransferEncoding.getValue();
        // if (!encoding.equals("base64")) {
        // throw new CMCException("Invalid Content-Transfer-Encoding in HTTP
        // response header: "
        // + encoding);
        // }

    }

}
