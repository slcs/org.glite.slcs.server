/*
 * $Id: SLCSServerConfiguration.java,v 1.2 2007/01/30 15:05:29 vtschopp Exp $
 * 
 * Created on Jul 28, 2006 by tschopp
 *
 * Copyright (c) 2006 SWITCH - http://www.switch.ch/
 */
package org.glite.slcs.config;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glite.slcs.Attribute;
import org.glite.slcs.SLCSConfigurationException;

/**
 * SLCSServerConfiguration is the singleton instance of the SLCS server
 * configuration. It must be configured as the WebAppliation Context parameters
 * in the <code>web.xml</code> file.
 * 
 * <pre>
 *        &lt;web-app id=&quot;SLCS&quot; version=&quot;2.4&quot;&gt;
 *        &lt;display-name&gt;SLCS&lt;/display-name&gt;
 *        &lt;!-- webapps context parameters --&gt;
 *        &lt;context-param&gt;
 *             &lt;!-- MANDATORY SLCSServerConfigurationFile: absolute filename or file in classpath --&gt;
 *             &lt;param-name&gt;SLCSServerConfigurationFile&lt;/param-name&gt;
 *             &lt;param-value&gt;/etc/glite/slcs.xml&lt;/param-value&gt;
 *        &lt;/context-param&gt;
 *        ...
 * </pre>
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.2 $
 */
public class SLCSServerConfiguration extends SLCSConfiguration {

    /** Logger */
    public static Log LOG = LogFactory.getLog(SLCSServerConfiguration.class);

    /**
     * Parameter name in the context of the web.xml file
     */
    static private String CONFIGURATION_FILE_KEY = "SLCSServerConfigurationFile";

    /**
     * Default server configuration filename
     */
    static private String DEFAULT_CONFIGURATION_FILE = "slcs.xml";

    /**
     * Key prefix for the attributes configuration in the configuration file
     */
    static public String ATTRIBUTESCONFIGURATION_PREFIX = "AttributesConfiguration";

    /**
     * Key prefix for a server components configuration in the configuration
     * file
     */
    static public String COMPONENTSCONFIGURATION_PREFIX = "SLCSComponentsConfiguration";

    /**
     * Singelton pattern
     */
    static private SLCSServerConfiguration SINGLETON = null;

    /**
     * List of required attribute names
     */
    private List requiredAttributeNames_ = null;

    /**
     * List of valid attribute names
     */
    private List validAttributeNames_ = null;

    /**
     * List of valid attributes
     */
    private List validAttributes_ = null;

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
            String filename = DEFAULT_CONFIGURATION_FILE;
            if (ctxt.getInitParameter(CONFIGURATION_FILE_KEY) != null) {
                filename = ctxt.getInitParameter(CONFIGURATION_FILE_KEY);
            }
            LOG.info(CONFIGURATION_FILE_KEY + "=" + filename);
            SINGLETON = new SLCSServerConfiguration(filename);
        } else {
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
            throw new IllegalStateException(
                    "Not initialized: call SLCSServerConfiguration.initialize(ServletContext ctx) first.");
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
    protected SLCSServerConfiguration(String filename)
            throws SLCSConfigurationException {
        super();
        LOG.debug("filename: " + filename);
        FileConfiguration configuration = loadConfiguration(filename);
        // setFileConfiguration call checkConfiguration...
        setFileConfiguration(configuration);

        initAttributesLists();
    }

    private void initAttributesLists() throws SLCSConfigurationException {
        // create the attribute names lists
        requiredAttributeNames_ = new ArrayList();
        validAttributeNames_ = new ArrayList();
        validAttributes_= new ArrayList();

        // populate both list
        List attributeNames = getList(ATTRIBUTESCONFIGURATION_PREFIX
                + ".Attribute[@name]");
        int nAttributes = attributeNames.size();
        for (int i = 0; i < nAttributes; i++) {
            String name = (String) attributeNames.get(i);
            // get required element, never throw exception (safe==false)
            String prefix = ATTRIBUTESCONFIGURATION_PREFIX + ".Attribute(" + i
                    + ")";
            String required = getString(prefix + "[@required]", false);
            if (required != null && required.equals("true")) {
                // add to the required attribute names list
                requiredAttributeNames_.add(name);
            }

            // add to the valid attribute names list
            validAttributeNames_.add(name);
            
            // create a new named attribute
            Attribute attribute = new Attribute(name);
            String displayName = getString(prefix + "[@displayName]", false);
            if (displayName != null && !displayName.equals("")) {
                attribute.setDisplayName(displayName);
            }

            // add in the valid attributes list
            validAttributes_.add(attribute);

        }
        LOG.info("RequiredAttributeNames=" + requiredAttributeNames_);
        LOG.info("ValidAttributeNames=" + validAttributeNames_);
        LOG.info("ValidAttributes=" + validAttributes_);
    }

    public List getRequiredAttributeNames() {
        return requiredAttributeNames_;
    }

    public List getValidAttributeNames() {
        return validAttributeNames_;
    }

    public List getValidAttributes() {
        return validAttributes_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.config.SLCSConfiguration#checkConfiguration()
     */
    protected void checkConfiguration() throws SLCSConfigurationException {
        String filename = getFilename();
        // Shibboleth configuration: Required attributes
        if (!contains(ATTRIBUTESCONFIGURATION_PREFIX + ".Attribute[@name]")) {
            LOG.error("SLCSServerConfiguration(" + filename + "): no "
                    + ATTRIBUTESCONFIGURATION_PREFIX + ".Attribute defined");
            throw new SLCSConfigurationException("Element(s) "
                    + ATTRIBUTESCONFIGURATION_PREFIX
                    + ".Attribute not defined in " + filename);
        }
        // DNBuilder
        if (!contains(COMPONENTSCONFIGURATION_PREFIX
                + ".DNBuilder[@implementation]")) {
            LOG.error("SLCSServerConfiguration(" + filename + "): "
                    + COMPONENTSCONFIGURATION_PREFIX
                    + ".DNBuilder[@implementation] missing");
            throw new SLCSConfigurationException("Element "
                    + COMPONENTSCONFIGURATION_PREFIX
                    + ".DNBuilder[@implementation] not defined in " + filename);
        }
        // SLCSSessions
        if (!contains(COMPONENTSCONFIGURATION_PREFIX
                + ".SLCSSessions[@implementation]")) {
            LOG.error("SLCSServerConfiguration(" + filename + "): "
                    + COMPONENTSCONFIGURATION_PREFIX
                    + ".SLCSSessions[@implementation] missing");
            throw new SLCSConfigurationException("Element "
                    + COMPONENTSCONFIGURATION_PREFIX
                    + ".SLCSSessions[@implementation] not defined in "
                    + filename);
        }
        // CAClient
        if (!contains(COMPONENTSCONFIGURATION_PREFIX
                + ".CAClient[@implementation]")) {
            LOG.error("SLCSServerConfiguration(" + filename + "): "
                    + COMPONENTSCONFIGURATION_PREFIX
                    + ".CAClient[@implementation] missing");
            throw new SLCSConfigurationException("Element "
                    + COMPONENTSCONFIGURATION_PREFIX
                    + ".CAClient[@implementation] not defined in " + filename);
        }
        // CertificatePolicy
        if (!contains(COMPONENTSCONFIGURATION_PREFIX
                + ".CertificatePolicy[@implementation]")) {
            LOG.error("SLCSServerConfiguration(" + filename + "): "
                    + COMPONENTSCONFIGURATION_PREFIX
                    + ".CertificatePolicy[@implementation] missing");
            throw new SLCSConfigurationException("Element "
                    + COMPONENTSCONFIGURATION_PREFIX
                    + ".CertificatePolicy[@implementation] not defined in "
                    + filename);
        }
        // Auditor
        if (!contains(COMPONENTSCONFIGURATION_PREFIX
                + ".Auditor[@implementation]")) {
            LOG.error("SLCSServerConfiguration(" + filename + "): "
                    + COMPONENTSCONFIGURATION_PREFIX
                    + ".Auditor[@implementation] missing");
            throw new SLCSConfigurationException("Element "
                    + COMPONENTSCONFIGURATION_PREFIX
                    + ".Auditor[@implementation] not defined in " + filename);
        }
        // AccessControlListEditor
        if (!contains(COMPONENTSCONFIGURATION_PREFIX
                + ".AccessControlListEditor[@implementation]")) {
            LOG.error("SLCSServerConfiguration(" + filename + "): "
                    + COMPONENTSCONFIGURATION_PREFIX
                    + ".AccessControlListEditor[@implementation] missing");
            throw new SLCSConfigurationException(
                    "Element "
                            + COMPONENTSCONFIGURATION_PREFIX
                            + ".AccessControlListEditor[@implementation] not defined in "
                            + filename);
        }

    }
}
