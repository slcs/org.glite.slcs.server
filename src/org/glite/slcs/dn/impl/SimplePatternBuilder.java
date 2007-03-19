/*
 * $Id: SimplePatternBuilder.java,v 1.4 2007/03/19 15:45:55 vtschopp Exp $
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html 
 */
package org.glite.slcs.dn.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glite.slcs.SLCSConfigurationException;
import org.glite.slcs.SLCSException;
import org.glite.slcs.ServiceException;
import org.glite.slcs.attribute.Attribute;
import org.glite.slcs.config.SLCSServerConfiguration;
import org.glite.slcs.dn.DNBuilder;
import org.glite.slcs.util.Utils;

/**
 * SimplePatternBuilder builds a DN based on a string pattern, containing
 * <code>${attribute-name}</code> variables. These variables will be
 * replaced by the corresponding attribute value.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.4 $
 */
public class SimplePatternBuilder implements DNBuilder {

    /** Logging */
    static private Log LOG = LogFactory.getLog(SimplePatternBuilder.class);

    /** The DN pattern with ${...} variable place holders */
    private String pattern_ = null;

    /**
     * Constructor accessed by the factory.
     */
    public SimplePatternBuilder() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.SLCSServerComponent#init(org.glite.slcs.config.SLCSServerConfiguration)
     */
    public void init(SLCSServerConfiguration config)
            throws SLCSConfigurationException {
        // read DNPattern from SLCSServerConfiguration
        this.pattern_ = config
                .getString(SLCSServerConfiguration.COMPONENTSCONFIGURATION_PREFIX + ".DNBuilder.DNPattern");
        LOG.info("DNBuilder.DNPattern=" + getPattern());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.SLCSServerComponent#shutdown()
     */
    public void shutdown() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.dn.DNBuilder#createDN(java.util.Map)
     */
    public String createDN(Map attributes) throws SLCSException {

        // parse pattern and match with attributes
        String dn = getPattern();
        Set attributeNames = attributes.keySet();
        Iterator names = attributeNames.iterator();
        while (names.hasNext()) {
            String name = (String) names.next();
            String placeholder = "${" + name + "}";
            if (pattern_.indexOf(placeholder) != -1) {
                String value = (String) attributes.get(name);
                // replace accentuated chars
                value = Utils.filterUnicodeAccentuedString(value);
                // check multi-value
                if (value.indexOf(';') != -1) {
                    LOG.error("Attribute " + name + " is multi-valued: "
                            + value);
                    throw new ServiceException(
                            "Ambiguous muli-valued attribute: " + name + "="
                                    + value);

                }
                // replace placeholder with attribute value
                // REGEX 1.4
                String replace = "\\$\\{" + name + "\\}";
                if (LOG.isDebugEnabled()) {
                    LOG.debug("DNPattern replace regex: " + replace + " by: "
                            + value);
                }
                dn = dn.replaceAll(replace, value);
            }
        }

        if (dn.indexOf("${") != -1) {
            LOG.error("DN still contains not substitued placeholders: " + dn);
            throw new ServiceException(
                    "Missing or wrong Shibboleth attributes: DN still contains placeholders: "
                            + dn);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("DN: " + dn);
        }

        return dn;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.dn.DNBuilder#createDN(java.util.List)
     */
    public String createDN(List attributes) throws SLCSException {
        // convert List to Map
        Map attributesMap = new HashMap();
        Iterator attributesIter = attributes.iterator();
        while (attributesIter.hasNext()) {
            Attribute attribute = (Attribute) attributesIter.next();
            String name = attribute.getName();
            String value = attribute.getValue();
            if (attributesMap.containsKey(name)) {
                // aggregate multi-valued attributes with ;
                String oldValue = (String) attributesMap.get(name);
                value = oldValue + ";" + value;
            }
            attributesMap.put(name, value);
        }
        return createDN(attributesMap);
    }

    /**
     * Sets the DN pattern. Use <code>${Shibboleth-AttributeName}</code> place
     * holder for variable substitution.
     * 
     * @param pattern
     *            The pattern to set.
     */
    public void setPattern(String pattern) {
        this.pattern_ = pattern;
    }

    /**
     * @return the DN pattern
     */
    public String getPattern() {
        return pattern_;
    }
}
