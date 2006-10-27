/*
 * $Id: SimplePatternBuilder.java,v 1.1 2006/10/27 12:11:24 vtschopp Exp $
 * 
 * Created on Aug 1, 2006 by Valery Tschopp <tschopp@switch.ch>
 *
 * Copyright (c) 2006 SWITCH - http://www.switch.ch/
 */
package org.glite.slcs.dn.impl;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.glite.slcs.SLCSConfigurationException;
import org.glite.slcs.SLCSException;
import org.glite.slcs.ServiceException;
import org.glite.slcs.config.SLCSServerConfiguration;
import org.glite.slcs.dn.DNBuilder;
import org.glite.slcs.util.Utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * SimplePatternBuilder builds a DN based on a string pattern, containing
 * <code>${Shibboleth-attributeName}</code> variables. These variables will be
 * replaced by the corresponding Shibboleth attribute value.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.1 $
 */
public class SimplePatternBuilder implements DNBuilder {

    /** Logging */
    static private Log LOG= LogFactory.getLog(SimplePatternBuilder.class);

    /** The DN pattern with ${...} variable place holders */
    private String pattern_= null;

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
        this.pattern_= config.getString("SLCSComponentConfiguration.DNBuilder.DNPattern");
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
        String dn= getPattern();
        Set attributeNames= attributes.keySet();
        Iterator names= attributeNames.iterator();
        while (names.hasNext()) {
            String name= (String) names.next();
            String placeholder= "${" + name + "}";
            if (pattern_.indexOf(placeholder) != -1) {
                String value= (String) attributes.get(name);
                // replace accentuated chars
                value= Utils.filterUnicodeAccentuedString(value);
                // replace placeholder with attribute value
                // REGEX 1.4
                String replace= "\\$\\{" + name + "\\}";
                if (LOG.isDebugEnabled()) {
                    LOG.debug("DNPattern replace regex: " + replace + " by: "
                            + value);
                }
                dn= dn.replaceAll(replace, value);
            }
        }

        if (dn.indexOf("${") != -1) {
            LOG.error("DN still contains not substitued placeholders: " + dn);
            throw new ServiceException("Missing or wrong Shibboleth attributes: DN still contains placeholders: "
                    + dn);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("DN: " + dn);
        }

        return dn;
    }

    /**
     * Sets the DN pattern. Use <code>${Shibboleth-AttributeName}</code> place
     * holder for variable substitution.
     * 
     * @param pattern
     *            The pattern to set.
     */
    public void setPattern(String pattern) {
        this.pattern_= pattern;
    }

    /**
     * @return the DN pattern
     */
    public String getPattern() {
        return pattern_;
    }
}
