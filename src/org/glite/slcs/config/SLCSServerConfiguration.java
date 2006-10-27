/*
 * $Id: SLCSServerConfiguration.java,v 1.1 2006/10/27 12:11:23 vtschopp Exp $
 * 
 * Created on Jul 28, 2006 by tschopp
 *
 * Copyright (c) 2006 SWITCH - http://www.switch.ch/
 */
package org.glite.slcs.config;

import javax.servlet.ServletContext;

import org.glite.slcs.SLCSConfigurationException;

import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * SLCSServerConfiguration is the singleton instance of the SLCS server
 * configuration. It must be configured as the WebAppliation Context parameters
 * in the <code>web.xml</code> file.
 * 
 * <pre>
 *     &lt;web-app id=&quot;SLCS&quot; version=&quot;2.4&quot;&gt;
 *     &lt;display-name&gt;SLCS&lt;/display-name&gt;
 *     &lt;!-- webapps context parameters --&gt;
 *     &lt;context-param&gt;
 *          &lt;!-- MANDATORY SLCSServerConfigurationFile: absolute filename or file in classpath --&gt;
 *          &lt;param-name&gt;SLCSServerConfigurationFile&lt;/param-name&gt;
 *          &lt;param-value&gt;/etc/glite/slcs.xml&lt;/param-value&gt;
 *     &lt;/context-param&gt;
 *     ...
 * </pre>
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.1 $
 */
public class SLCSServerConfiguration extends SLCSConfiguration {

    /** Logger */
    public static Log LOG= LogFactory.getLog(SLCSServerConfiguration.class);

    static private String CONFIGURATION_FILE_KEY= "SLCSServerConfigurationFile";

    static private String DEFAULT_CONFIGURATION_FILE= "slcs.xml";

    /**
     * Singelton pattern
     */
    static private SLCSServerConfiguration SINGLETON= null;

    /**
     * Initialize the singleton instance of the SLCSServerConfiguration.
     * 
     * @param ctxt
     *            The ServletContext
     * @throws SLCSConfigurationException
     *             If a configuration error occurs.
     */
    static public synchronized void initialize(ServletContext ctxt)
            throws SLCSConfigurationException {
        if (SINGLETON == null) {
            LOG.debug("create new SLCSServerConfiguration...");
            String filename= DEFAULT_CONFIGURATION_FILE;
            if (ctxt.getInitParameter(CONFIGURATION_FILE_KEY) != null) {
                filename= ctxt.getInitParameter(CONFIGURATION_FILE_KEY);
            }
            LOG.info(CONFIGURATION_FILE_KEY + "=" + filename);
            SINGLETON= new SLCSServerConfiguration(filename);
        }
        else {
            LOG.info("SLCSServerConfiguration already initialized");
        }
    }

    /**
     * Returns the singleton instance of the SLCSServerConfiguration.
     * 
     * @return The SLCSServerConfiguration singleton.
     */
    static public synchronized SLCSServerConfiguration getInstance() {
        if (SINGLETON == null) {
            throw new IllegalStateException("Not initialized: call SLCSServerConfiguration.initialize(ServletContext ctx) first.");
        }
        return SINGLETON;
    }

    /**
     * DO NOT USE directly the constructor. Factory pattern. Only use
     * initialize() and getInstance().
     * 
     * @param filename
     *            The XML file based configuration file.
     * @throws SLCSConfigurationException
     *             If a configuration error occurs while loading the
     *             configuration file or checking the configuration.
     * @see #initialize(ServletContext)
     * @see #getInstance()
     */
    public SLCSServerConfiguration(String filename)
            throws SLCSConfigurationException {
        super();
        LOG.debug("filename: " + filename);
        FileConfiguration configuration= loadConfiguration(filename);
        setFileConfiguration(configuration);

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.config.SLCSConfiguration#checkConfiguration()
     */
    protected void checkConfiguration() throws SLCSConfigurationException {
        // Shibboleth configuration: Required attributes
        String filename= getFilename();
        if (!contains("ShibbolethConfiguration.RequiredAttributeName")) {
            LOG.error("SLCSServerConfiguration("
                    + filename
                    + "): ShibbolethConfiguration.RequiredAttributeName missing");
            throw new SLCSConfigurationException("Elements ShibbolethConfiguration.RequiredAttributeName is not defined in "
                    + filename);
        }
        // DNBuilder
        if (!contains("SLCSComponentConfiguration.DNBuilder[@implementation]")) {
            LOG.error("SLCSServerConfiguration("
                    + filename
                    + "): SLCSComponentConfiguration.DNBuilder[@implementation] missing");
            throw new SLCSConfigurationException("Element SLCSComponentConfiguration.DNBuilder[@implementation] not defined in "
                    + filename);
        }
        // SLCSSessions
        if (!contains("SLCSComponentConfiguration.SLCSSessions[@implementation]")) {
            LOG.error("SLCSServerConfiguration("
                    + filename
                    + "): SLCSComponentConfiguration.SLCSSessions[@implementation] missing");
            throw new SLCSConfigurationException("Element SLCSComponentConfiguration.SLCSSessions[@implementation] not defined in "
                    + filename);
        }
        // CAClient
        if (!contains("SLCSComponentConfiguration.CAClient[@implementation]")) {
            LOG.error("SLCSServerConfiguration("
                    + filename
                    + "): SLCSComponentConfiguration.CAClient[@implementation] missing");
            throw new SLCSConfigurationException("Element SLCSComponentConfiguration.CAClient[@implementation] not defined in "
                    + filename);
        }
        // CertificatePolicy
        if (!contains("SLCSComponentConfiguration.CertificatePolicy[@implementation]")) {
            LOG.error("SLCSServerConfiguration("
                    + filename
                    + "): SLCSComponentConfiguration.CertificatePolicy[@implementation] missing");
            throw new SLCSConfigurationException("Element SLCSComponentConfiguration.CertificatePolicy[@implementation] not defined in "
                    + filename);
        }
        // Auditor
        if (!contains("SLCSComponentConfiguration.Auditor[@implementation]")) {
            LOG.error("SLCSServerConfiguration("
                    + filename
                    + "): SLCSComponentConfiguration.Auditor[@implementation] missing");
            throw new SLCSConfigurationException("Element SLCSComponentConfiguration.Auditor[@implementation] not defined in "
                    + filename);
        }

    }
}
