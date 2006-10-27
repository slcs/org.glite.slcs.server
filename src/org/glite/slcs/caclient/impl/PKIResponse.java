/*
 * $Id: PKIResponse.java,v 1.1 2006/10/27 12:11:23 vtschopp Exp $
 * 
 * Created on Jun 15, 2006 by tschopp
 *
 * Copyright (c) 2006 SWITCH - http://www.switch.ch/
 */
package org.glite.slcs.caclient.impl;

import org.glite.slcs.caclient.CAResponse;

/**
 * PKIResponse is an interface for a RFC2797 request.
 *
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.1 $
 */
public interface PKIResponse extends CAResponse {

    /**
     * Returns the mandatory MIME type (including the SMIME type extension) of
     * the PKI response that should be in the Content-Type response header.
     * 
     * @return The Content-Type: MIME type of the request.
     */
    public String getMimeType();

    /**
     * Returns the mandatory file name extension of the PKI response that should be
     * in the Content-Disposition or in the Content-Type response
     * header.
     * 
     * @return The file name extension of the response.
     */
    public String getFilenameExtension();

}
