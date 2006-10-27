/*
 * $Id: CAConnection.java,v 1.1 2006/10/27 12:11:23 vtschopp Exp $
 * 
 * Created on Aug 4, 2006 by tschopp
 *
 * Copyright (c) 2006 SWITCH - http://www.switch.ch/
 */
package org.glite.slcs.caclient;

import org.glite.slcs.SLCSException;
import org.glite.slcs.pki.CertificateRequest;

/**
 * CAConnection is a connection (session) to execute the request/response with
 * the online CA server.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.1 $
 */
public interface CAConnection {

    /**
     * Creates a CARequest object for the given PKCS10 certificate request.
     * 
     * @param csr
     *            The certificate request to be send on the CAConnection.
     * @return The CARequest to be send.
     * @throws Exception
     *             If an error occurs while creating the request object.
     */
    public CARequest createRequest(CertificateRequest csr) throws SLCSException;

    /**
     * Sends a CARequest to the online CA server.
     * 
     * @param request
     *            The CARequest to send.
     * @throws Exception
     *             If an error occurs while sending or processing the request.
     */
    public void sendRequest(CARequest request) throws SLCSException;

    /**
     * Gets the CAResponse which is the result of the request.
     * 
     * @return The CAReponse or <code>null</code> if there were no request
     *         before.
     * @throws Exception
     *             If an error occurs while processing the reponse.
     */
    public CAResponse getResponse() throws SLCSException;
}
