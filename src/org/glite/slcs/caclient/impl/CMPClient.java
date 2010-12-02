/**
 * $Id: CMPClient.java,v 1.3 2010/12/02 16:14:04 vtschopp Exp $
 *
 * Created on 13/06/2007 by Henri Mikkonen <henri.mikkonen@hip.fi>
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html
 */
package org.glite.slcs.caclient.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.glite.slcs.SLCSException;
import org.glite.slcs.caclient.CAClient;
import org.glite.slcs.caclient.CAConnection;
import org.glite.slcs.config.SLCSServerConfiguration;
import org.glite.slcs.httpclient.ssl.ExtendedProtocolSocketFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.protocol.DefaultProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.novosec.pkix.asn1.cmp.CMPObjectIdentifiers;

/**
 * <p>CMPClient is a RFC4210 compliant online CA client. This implementation uses a
 * HttpClient with clientAuth capabilities to send the requests and get the
 * responses.</p>
 * 
 * <p>Required configuration variables:
 * <ul>
 * <li><code>CAClient.CAUrl</code> The URL for the online CA's CMP service
 * <li><code>CAClient.KeyStoreFile</code> Keystore used for the client authentication
 * <li><code>CAClient.KeyStorePassword</code>
 * <li><code>CAClient.TrustStoreFile</code> Truststore for verifying the server certificate
 * <li><code>CAClient.CADN</code> Online CA's subject DN
 * <li><code>CAClient.SenderDN</code> RA's subject DN
 * <li><code>CAClient.RecipientDN</code> Online CA's CMP service subject DN
 * <li><code>CAClient.SenderKID</code> RA's key identifier
 * <li><code>CAClient.SharedSecret</code> Shared secret between the RA and the online CA
 * </ul>
 * </p>
 * 
 * <p>Optional variables:
 * <ul>
 * <li><code>CAClient.OwfAlgId</code> (default: 1.3.14.3.2.26)
 * <li><code>CAClient.IterCount</code> (default: 1)
 * <li><code>CAClient.MacAlgId</code> (default: 1.3.6.1.5.5.8.1.2)
 * <li><code>CAClient.SaltString</code> (default: empty string)
 * <li><code>CAClient.ProtectionAlgId</code> (default: 1.2.840.113533.7.66.13 (passwordBasedMac))
 * </ul>
 * </p>
 * 
 * @author Henri Mikkonen <henri.mikkonen@hip.fi>
 */
public class CMPClient implements CAClient {
	
    public static final String CA_DN_IDENTIFIER = "CADN";
    public static final String SENDER_DN_IDENTIFIER = "SenderDN";
    public static final String RECIPIENT_DN_IDENTIFIER = "RecipientDN";
    public static final String SENDER_KID_IDENTIFIER = "SenderKID";
    public static final String SHARED_SECRET_IDENTIFIER = "SharedSecret";
    public static final String OWF_ALG_ID_IDENTIFIER = "OwfAlgId";
    public static final String ITERATION_COUNT_IDENTIFIER = "IterCount";
    public static final String MAC_ALG_ID_IDENTIFIER = "MacAlgId";
    public static final String SALT_STRING_IDENTIFIER = "SaltString";
    public static final String PROTECTION_ALG_ID_IDENTIFIER = "ProtectionAlgId";
    
    public static final String DEFAULT_OWF_ALGID = "1.3.14.3.2.26";
    public static final String DEFAULT_ITERATION_COUNT = "1";
    public static final String DEFAULT_MAC_ALGID = "1.3.6.1.5.5.8.1.2";
    public static final String DEFAULT_PROTECTION_ALGID = CMPObjectIdentifiers.passwordBasedMac.toString();

    /** Logging */
    static private Log log = LogFactory.getLog(CMPClient.class);
    
    private HttpClient httpClient_;
    private String relativeUrl_;
    
    /** CMP configuration variables */
    private Properties cmpProperties_;
    
    /*
     * Constructs a <code>CMPClient</code>
     */
    public CMPClient() {
        super();
    }

    /* (non-Javadoc)
     * @see org.glite.slcs.caclient.CAClient#getConnection()
     */
    public CAConnection getConnection() throws SLCSException {
        return new CMPConnection(this, relativeUrl_);
    }

    /* (non-Javadoc)
     * @see org.glite.slcs.SLCSServerComponent#init(org.glite.slcs.config.SLCSServerConfiguration)
     */
    public void init(SLCSServerConfiguration config) throws SLCSException {
        log.debug("Reading HTTP Client related variables from the configuration:");
        initHttpClientVars(config);
        cmpProperties_ = new Properties();
        log.debug("Reading general CMP variables from the configuration:");
        initGeneralCMPVars(config);
        log.debug("Reading crypto related CMP variables from the configuration:");
        initCryptoCMPVars(config);
    }
	
    private void initHttpClientVars(SLCSServerConfiguration config) throws SLCSException {
        String caURL= config.getString(SLCSServerConfiguration.COMPONENTSCONFIGURATION_PREFIX + ".CAClient.CAUrl");
        log.info("CAClient.CAUrl=" + caURL);
        URL serverUrl= null;
        try {
            serverUrl = new URL(caURL);
        } catch (MalformedURLException e1) {
            log.error(e1);
            throw new SLCSException(SLCSServerConfiguration.COMPONENTSCONFIGURATION_PREFIX + ".CAClient.CAUrl=" + caURL + " is not a valid URL", e1);
        }
        // check for HTTPS if KeyStore and co are needed
        boolean isSecure= serverUrl.getProtocol().equalsIgnoreCase("https");
        String keystoreFilename= config.getString(SLCSServerConfiguration.COMPONENTSCONFIGURATION_PREFIX + ".CAClient.KeyStoreFile",isSecure);
        log.info("CAClient.KeyStoreFile=" + keystoreFilename);
        String keystorePassword= config.getString(SLCSServerConfiguration.COMPONENTSCONFIGURATION_PREFIX + ".CAClient.KeyStorePassword",isSecure);
        log.info("CAClient.KeyStorePassword=" + keystorePassword);
        String truststoreFilename= config.getString(SLCSServerConfiguration.COMPONENTSCONFIGURATION_PREFIX + ".CAClient.TrustStoreFile",isSecure);
        log.info("CAClient.TrustStoreFile=" + truststoreFilename);

        // init the vars
        try {
            httpClient_= createHttpClient(serverUrl,
                                              keystoreFilename,
                                              keystorePassword,
                                              truststoreFilename);
            relativeUrl_ = serverUrl.getPath();
        } catch (Exception e) {
            log.error(e);
            throw new SLCSException("Failed to create embedded HttpClient", e);
        }
    }
	
    private void initGeneralCMPVars(SLCSServerConfiguration config) throws SLCSException {
        readConfigurationVariable(config, CA_DN_IDENTIFIER, null);
        readConfigurationVariable(config, SENDER_DN_IDENTIFIER, null);
        readConfigurationVariable(config, RECIPIENT_DN_IDENTIFIER, null);
    }
	
    private void initCryptoCMPVars(SLCSServerConfiguration config) throws SLCSException {
        readConfigurationVariable(config, SENDER_KID_IDENTIFIER, null);
        readConfigurationVariable(config, SHARED_SECRET_IDENTIFIER, null);
        readConfigurationVariable(config, OWF_ALG_ID_IDENTIFIER, DEFAULT_OWF_ALGID);
        readConfigurationVariable(config, ITERATION_COUNT_IDENTIFIER, DEFAULT_ITERATION_COUNT);
        readConfigurationVariable(config, MAC_ALG_ID_IDENTIFIER, DEFAULT_MAC_ALGID);
        readConfigurationVariable(config, SALT_STRING_IDENTIFIER, "");
        readConfigurationVariable(config, PROTECTION_ALG_ID_IDENTIFIER, DEFAULT_PROTECTION_ALGID);
    }
	
    private void readConfigurationVariable(SLCSServerConfiguration config, String variable, String defaultValue) throws SLCSException {
        String str = config.getString(SLCSServerConfiguration.COMPONENTSCONFIGURATION_PREFIX + ".CAClient." + variable, false);
        if (str == null || str.equals("")) {
            if (defaultValue == null) {
                throw new SLCSException(SLCSServerConfiguration.COMPONENTSCONFIGURATION_PREFIX + ".CAClient." + variable + " is a required variable!");
            }
            else {
                log.info("CAClient." + variable + "=" + defaultValue + " (was null, using default)");
                cmpProperties_.setProperty(variable, defaultValue);
            }
        } 
        else {
            cmpProperties_.setProperty(variable, str);
            log.info("CAClient." + variable + "=" + str);
        }
    }

    static private HttpClient createHttpClient(URL caURL, String keystorePath, String keystorePassword, String truststorePath) throws SLCSException {
        Protocol protocol = null;
        int port = caURL.getPort();
        if (caURL.getProtocol().equalsIgnoreCase("http")) {
            if (port == -1) {
                port = 80;
            }
            protocol = new Protocol("http", new DefaultProtocolSocketFactory(), port);
        } else if (caURL.getProtocol().equalsIgnoreCase("https")) {
            if (port == -1) {
                port = 443;
            }
            try {
                ProtocolSocketFactory psf= new ExtendedProtocolSocketFactory(keystorePath, keystorePassword, truststorePath);
                protocol = new Protocol("https", psf, port);
            } catch (Exception e) {
                throw new SLCSException("Error in generating the secure http client", e);
            }	
        } 
        else {
            throw new SLCSException ("Protocol defined in CAClient.CAUrl is not supported! Use http or https.");
        }
        // create HTTP client
        MultiThreadedHttpConnectionManager connectionManager= new MultiThreadedHttpConnectionManager();
        HttpClient client= new HttpClient(connectionManager);
        client.getHostConfiguration().setHost(caURL.getHost(), port, protocol);
        return client;
    }

    /* (non-Javadoc)
     * @see org.glite.slcs.SLCSServerComponent#shutdown()
     */
    public void shutdown() {
        httpClient_ = null;
    }
	
    /**
     * @return the underlying HttpClient
     */
    protected HttpClient getHttpClient() {
        return httpClient_;
    }
    
    /**
     * @return the CMP configuration variables
     */
    protected Properties getCMPProperties() {
        return cmpProperties_;
    }
}