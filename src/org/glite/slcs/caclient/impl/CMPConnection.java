/**
 * $Id: CMPConnection.java,v 1.2 2007/11/16 15:03:15 mikkonen Exp $
 *
 * Created on 11/07/2007 by Henri Mikkonen <henri.mikkonen@hip.fi>
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html
 */
package org.glite.slcs.caclient.impl;

import java.io.InputStream;
import java.io.IOException;

import org.glite.slcs.SLCSException;
import org.glite.slcs.caclient.CAConnection;
import org.glite.slcs.caclient.CARequest;
import org.glite.slcs.caclient.CAResponse;
import org.glite.slcs.pki.CertificateRequest;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * CMPConnection implements the CAConnection and represents a connection
 * (session) used to create, send and receive request to a RFC4210 compliant
 * online CA server.
 * 
 * @author Henri Mikkonen <henri.mikkonen@hip.fi>
 */
public class CMPConnection implements CAConnection {
	
    /** Logging */
    private static Log log = LogFactory.getLog(CMPConnection.class);
    
    /** the CMPClient associated with this connection */
    private CMPClient cmpClient;
    
    /** the HTTP POST method used to submit the request */
    private PostMethod cmpServerPost;

    /*
     * Constructs a <code>CMPConnection</code>
     * @param client the <code>CMPClient</code> to this connection
     * @param url the URL where to post the CMP request
     */
    protected CMPConnection(CMPClient client, String url) {
        this.cmpClient= client;
        this.cmpServerPost = new PostMethod(url);
    }

    /* (non-Javadoc)
     * @see org.glite.slcs.caclient.CAConnection#createRequest(org.glite.slcs.pki.CertificateRequest)
     */
    public CARequest createRequest(CertificateRequest certRequest)
        throws SLCSException {
        return new CMPRequest(certRequest, cmpClient.getCMPProperties());
    }

    /* (non-Javadoc)
     * @see org.glite.slcs.caclient.CAConnection#getResponse()
     */
    public CAResponse getResponse() throws SLCSException {
        InputStream postResult = null;
        try {
            postResult = this.cmpServerPost.getResponseBodyAsStream();
        } catch (IOException e) {
            log.error("Error while reading the response from online CA!", e);
            throw new SLCSException(e);
        }
        return new CMPResponse(postResult, this.cmpServerPost);
    }

    /* (non-Javadoc)
     * @see org.glite.slcs.caclient.CAConnection#sendRequest(org.glite.slcs.caclient.CARequest)
     */
    public void sendRequest(CARequest cmpRequest) throws SLCSException {
        byte[] requestBytes = cmpRequest.getDEREncoded(); 
        RequestEntity entity = new ByteArrayRequestEntity(requestBytes);
        this.cmpServerPost.setRequestEntity(entity);
        HttpClient httpClient = this.cmpClient.getHttpClient();
        int statusCode = 0;
        try {
            log.debug("Sending the request to the online CA...");
            statusCode = httpClient.executeMethod(this.cmpServerPost);
            log.debug("Status code = " + statusCode);
        } catch (HttpException e) {
            log.error("Http related error while sending the request to the online CA.", e);
            throw new SLCSException(e);
        } catch (IOException e) {
            log.error("IO error while sending the request to the online CA.", e);
            throw new SLCSException(e);
        } 
        if (statusCode != 200) { // not ok
            log.error("Status code " + statusCode + " is unexcpected!");
            throw new SLCSException("Unexpected status code from the online CA (" + statusCode + ")!");
        }
    }
}
