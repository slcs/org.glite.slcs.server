/*
 * $Id: CMCClient.java,v 1.4 2007/07/25 07:21:24 vtschopp Exp $
 * 
 * Created on Jun 14, 2006 by tschopp
 *
 * Copyright (c) 2006 SWITCH - http://www.switch.ch/
 */
package org.glite.slcs.caclient.impl;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glite.slcs.SLCSException;
import org.glite.slcs.caclient.CAClient;
import org.glite.slcs.caclient.CAConnection;
import org.glite.slcs.config.SLCSServerConfiguration;
import org.glite.slcs.httpclient.ssl.ExtendedProtocolSocketFactory;

/**
 * CMCClient is a RFC2797 compliant online CA client. This implementation uses a
 * HttpClient with clientAuth capabilities to send the requests and get the
 * reponses.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.4 $
 */
public class CMCClient implements CAClient {

    /** Logging */
    static private Log LOG= LogFactory.getLog(CMCClient.class);

    /** The Http client use to request the request */
    private HttpClient httpClient_;

    /** the CA server URL to submit the request */
    private String cmcServerURL_;

    /**
     * 
     * @param cmcServerURL
     * @param keystorePath
     * @param keystorePassword
     * @param truststorePath
     * @throws SLCSException
     */
    public CMCClient(String cmcServerURL, String keystorePath,
            String keystorePassword, String truststorePath)
            throws SLCSException {
        super();
        try {
            this.httpClient_= createHttpClient(keystorePath,
                                               keystorePassword,
                                               truststorePath);
        } catch (Exception e) {
            LOG.error(e);
            throw new SLCSException("Failed to create embedded HttpClient", e);
        }
        this.cmcServerURL_= cmcServerURL;
    }

    /**
     * Constructor
     */
    public CMCClient() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.SLCSServerComponent#init(org.glite.slcs.config.SLCSServerConfiguration)
     */
    public void init(SLCSServerConfiguration config) throws SLCSException {
        // read config param from SLCSServerConfiguration
        // read and check validity of config
        String caURL= config.getString(SLCSServerConfiguration.COMPONENTSCONFIGURATION_PREFIX + ".CAClient.CAUrl");
        LOG.info("CAClient.CAUrl=" + caURL);
        String keystoreFilename= config.getString(SLCSServerConfiguration.COMPONENTSCONFIGURATION_PREFIX + ".CAClient.KeyStoreFile");
        LOG.info("CAClient.KeyStoreFile=" + keystoreFilename);
        String keystorePassword= config.getString(SLCSServerConfiguration.COMPONENTSCONFIGURATION_PREFIX + ".CAClient.KeyStorePassword");
        LOG.info("CAClient.KeyStorePassword=" + keystorePassword);
        String truststoreFilename= config.getString(SLCSServerConfiguration.COMPONENTSCONFIGURATION_PREFIX + ".CAClient.TrustStoreFile");
        LOG.info("CAClient.TrustStoreFile=" + truststoreFilename);

        // init the vars
        try {
            this.httpClient_= createHttpClient(keystoreFilename,
                                               keystorePassword,
                                               truststoreFilename);
        } catch (Exception e) {
            // e.printStackTrace();
            LOG.error(e);
            throw new SLCSException("Failed to create embedded HttpClient", e);
        }
        this.cmcServerURL_= caURL;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.SLCSServerComponent#shutdown()
     */
    public void shutdown() {
        httpClient_= null;
    }

    /**
     * Creates a multi-threaded Http Client.
     * 
     * @param keystorePath
     * @param keystorePassword
     * @param truststorePath
     * @return
     * @throws IOException
     */
    static private HttpClient createHttpClient(String keystorePath,
            String keystorePassword, String truststorePath) throws Exception {
        // add client auth and trust support to https, throws Exception
        ExtendedProtocolSocketFactory psf= new ExtendedProtocolSocketFactory(keystorePath,
                                                                             keystorePassword,
                                                                             truststorePath);
        // register HTTPS extended protocol
        Protocol https= new Protocol("https", psf, 443);
        Protocol.registerProtocol("https", https);
        // create HTTP client
        MultiThreadedHttpConnectionManager connectionManager= new MultiThreadedHttpConnectionManager();
        HttpClient client= new HttpClient(connectionManager);
        return client;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.caclient.CAClient#getCAConnection()
     */
    public CAConnection getConnection() {
        return new CMCConnection(this, cmcServerURL_);
    }

    /**
     * @return the underlying HttpClient
     */
    protected HttpClient getHttpClient() {
        return this.httpClient_;
    }

}
