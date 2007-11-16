/**
 * $Id: CMPClient.java,v 1.1 2007/11/16 10:10:39 mikkonen Exp $
 *
 * Created on 13/06/2007 by Henri Mikkonen <henri.mikkonen@hip.fi>
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html
 */
package org.glite.slcs.caclient.impl;

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
    
    private HttpClient httpClient;
    private String relativeUrl;
    
    private Properties cmpProperties;
    
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
		return new CMPConnection(this, this.relativeUrl);
	}

	/* (non-Javadoc)
	 * @see org.glite.slcs.SLCSServerComponent#init(org.glite.slcs.config.SLCSServerConfiguration)
	 */
	public void init(SLCSServerConfiguration config) throws SLCSException {
		log.debug("Reading HTTP Client related variables from the configuration:");
        this.initHttpClientVars(config);
        this.cmpProperties = new Properties();
        log.debug("Reading general CMP variables from the configuration:");
        this.initGeneralCMPVars(config);
        log.debug("Reading crypto related CMP variables from the configuration:");
        this.initCryptoCMPVars(config);
	}
	
	private void initHttpClientVars(SLCSServerConfiguration config) throws SLCSException {
        String caURL= config.getString(SLCSServerConfiguration.COMPONENTSCONFIGURATION_PREFIX + ".CAClient.CAUrl");
        log.info("CAClient.CAUrl=" + caURL);
        String keystoreFilename= config.getString(SLCSServerConfiguration.COMPONENTSCONFIGURATION_PREFIX + ".CAClient.KeyStoreFile");
        log.info("CAClient.KeyStoreFile=" + keystoreFilename);
        String keystorePassword= config.getString(SLCSServerConfiguration.COMPONENTSCONFIGURATION_PREFIX + ".CAClient.KeyStorePassword");
        log.info("CAClient.KeyStorePassword=" + keystorePassword);
        String truststoreFilename= config.getString(SLCSServerConfiguration.COMPONENTSCONFIGURATION_PREFIX + ".CAClient.TrustStoreFile");
        log.info("CAClient.TrustStoreFile=" + truststoreFilename);

        // init the vars
        try {
        	URL serverUrl = new URL(caURL);
            this.httpClient= createHttpClient(keystoreFilename,
                                              keystorePassword,
                                              truststoreFilename,
                                              serverUrl);
            this.relativeUrl = serverUrl.getPath();
        } catch (Exception e) {
            log.error(e);
            throw new SLCSException("Failed to create embedded HttpClient", e);
        }
	}
	
	private void initGeneralCMPVars(SLCSServerConfiguration config) throws SLCSException {
		this.readConfigurationVariable(config, CA_DN_IDENTIFIER, null);
		this.readConfigurationVariable(config, SENDER_DN_IDENTIFIER, null);
		this.readConfigurationVariable(config, RECIPIENT_DN_IDENTIFIER, null);
	}
	
	private void initCryptoCMPVars(SLCSServerConfiguration config) throws SLCSException {
		this.readConfigurationVariable(config, SENDER_KID_IDENTIFIER, null);
		this.readConfigurationVariable(config, SHARED_SECRET_IDENTIFIER, null);
		this.readConfigurationVariable(config, OWF_ALG_ID_IDENTIFIER, DEFAULT_OWF_ALGID);
		this.readConfigurationVariable(config, ITERATION_COUNT_IDENTIFIER, DEFAULT_ITERATION_COUNT);
		this.readConfigurationVariable(config, MAC_ALG_ID_IDENTIFIER, DEFAULT_MAC_ALGID);
		this.readConfigurationVariable(config, SALT_STRING_IDENTIFIER, "");
		this.readConfigurationVariable(config, PROTECTION_ALG_ID_IDENTIFIER, DEFAULT_PROTECTION_ALGID);
	}
	
	private void readConfigurationVariable(SLCSServerConfiguration config, String variable, String defaultValue) throws SLCSException {
		String str = config.getString(SLCSServerConfiguration.COMPONENTSCONFIGURATION_PREFIX + ".CAClient." + variable, false);
		if (str == null || str.equals("")) {
			if (defaultValue == null) {
				throw new SLCSException(SLCSServerConfiguration.COMPONENTSCONFIGURATION_PREFIX + ".CAClient." + variable + " is a required variable!");
			} else {
				log.info("CAClient." + variable + "='" + defaultValue + "' (was null, using default)");
				this.cmpProperties.setProperty(variable, defaultValue);
			}
		} else {
			this.cmpProperties.setProperty(variable, str);
			log.info("CAClient." + variable + "=" + str);
		}
	}

	private HttpClient createHttpClient(String keystorePath, String keystorePassword, String truststorePath, URL caURL) throws SLCSException {
		Protocol protocol = null;
		int port = caURL.getPort();
		if (caURL.getProtocol().equals("http")) {
			if (port == -1) {
				port = 80;
			}
			protocol = new Protocol("http", new DefaultProtocolSocketFactory(), port);
		} else if (caURL.getProtocol().equals("https")) {
			if (port == -1) {
				port = 443;
			}
	        try {
				ExtendedProtocolSocketFactory psf= new ExtendedProtocolSocketFactory(keystorePath, keystorePassword, truststorePath);
				protocol = new Protocol("https", psf, port);
			} catch (Exception e) {
				throw new SLCSException("Error in generating the secure http client", e);
			}	
		} else {
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
		this.httpClient = null;
	}
	
    /**
     * @return the underlying HttpClient
     */
    protected HttpClient getHttpClient() {
        return this.httpClient;
    }
    
    /**
     * @return the CMP configuration variables
     */
    protected Properties getCMPProperties() {
    	return this.cmpProperties;
    }
}