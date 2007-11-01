/*
 * $Id: ListAccessControlRulesXMLOperation.java,v 1.4 2007/11/01 14:35:11 vtschopp Exp $
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html 
 */
package org.glite.slcs.acl.impl;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glite.slcs.acl.AccessControlRule;
import org.glite.slcs.attribute.Attribute;
import org.glite.slcs.attribute.AttributeDefinitions;
import org.glite.slcs.attribute.AttributeDefinitionsFactory;

/**
 * XMLOperation to list the AccessControlRules from the XML ACL file.
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision: 1.4 $
 */
public class ListAccessControlRulesXMLOperation extends XMLOperation {

    /**
     * Logging
     */
    private static Log LOG = LogFactory.getLog(ListAccessControlRulesXMLOperation.class);

    /**
     * Result list of rules
     */
    private List accessControlRules_ = null;

    /**
     * The rules group to list
     */
    private String group_ = null;

    /** The attribute definitions */
    private AttributeDefinitions attributeDefinitions_ = null;

    
    /**
     * Constructor
     * 
     * @param group
     *            The group to list, use <code>null</code> for all group.
     */
    public ListAccessControlRulesXMLOperation(String group) {
        super();
        group_ = group;
        accessControlRules_ = new LinkedList();
        attributeDefinitions_ = AttributeDefinitionsFactory.getInstance();
    }

    protected void doProcessing(XMLConfiguration config) {

        if (LOG.isDebugEnabled()) {
            LOG.debug("list rules(" + group_ + ")");
        }
        // list all rules
        // populate the List with attribute values
        int i = 0;
        while (true) {
            String rulePrefix = "AccessControlRule(" + i + ")";
            i++;
            String ruleGroup = config.getString(rulePrefix + "[@group]");
            if (ruleGroup == null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(rulePrefix + ": no more rules");
                }
                // no more ACL rule to read, exit while loop
                break;
            }

            if (group_ != null && !ruleGroup.equals(group_)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(rulePrefix + "[@group]=" + ruleGroup
                            + " skipping...");
                }
                // other group, skipping
                continue;

            }
            int ruleId = config.getInt(rulePrefix + "[@id]");
            AccessControlRule rule = new AccessControlRule(ruleId, ruleGroup);

            List attributeNames = config.getList(rulePrefix
                    + ".Attribute[@name]");
            if (attributeNames.isEmpty()) {
                LOG.warn(rulePrefix + ": no attribute in rule, skipping...");
                // error, skipping
                continue;
            }
            List attributeValues = config.getList(rulePrefix + ".Attribute");
            for (int j = 0; j < attributeNames.size(); j++) {
                String name = (String) attributeNames.get(j);
                String value = (String) attributeValues.get(j);
                Attribute attribute = attributeDefinitions_.createAttribute(name, value);
                rule.addAttribute(attribute);
            }

            accessControlRules_.add(rule);

            // success, at least one rule
            setStatus(true);

        }
    }

    /**
     * Oonce done, the list of rules can be retrieved.
     * 
     * @return List of rules
     */
    public List getResults() {
        return accessControlRules_;
    }

}
