/*
 * $Id: RemoveAccessControlRuleXMLOperation.java,v 1.4 2007/03/19 14:05:50 vtschopp Exp $
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html 
 */
package org.glite.slcs.acl.impl;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glite.slcs.acl.AccessControlRule;

/**
 * XMLOpertaion to remove an AccessControlRule from the XML ACL based on its
 * ruleId.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.4 $
 */
public class RemoveAccessControlRuleXMLOperation extends XMLOperation {

    /** Logging */
    private static Log LOG = LogFactory.getLog(RemoveAccessControlRuleXMLOperation.class);

    /**
     * The AccessControlRule ID to remove
     */
    private int ruleId_ = -1;

    /**
     * Constructor
     * 
     * @param rule
     *            The AccessControlRule to remove.
     */
    public RemoveAccessControlRuleXMLOperation(AccessControlRule rule) {
        super();
        ruleId_ = rule.getId();
    }

    /**
     * Constructor
     * 
     * @param ruleId
     *            The AccessControlRule ID to remove
     */
    public RemoveAccessControlRuleXMLOperation(int ruleId) {
        super();
        ruleId_ = ruleId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.acl.impl.XMLOperation#doProcessing(org.apache.commons.configuration.XMLConfiguration)
     */
    protected void doProcessing(XMLConfiguration config) {

        if (LOG.isDebugEnabled())
            LOG.debug("remove AccessControlRule[id=" + ruleId_ + "]");

        // find index
        int i = 0;
        String ruleKey = null;
        while (true) {
            String rulePrefix = "AccessControlRule(" + i + ")";
            i++;
            String ruleId = config.getString(rulePrefix + "[@id]");
            if (ruleId == null) {
                // no more rules
                break;
            }
            else {
                int id = Integer.parseInt(ruleId);
                if (id == ruleId_) {
                    ruleKey = rulePrefix;
                    break;
                }
            }
        }

        if (ruleKey != null) {
            LOG.debug("delete AccessControlRule[id=" + ruleId_ + "]");
            // clear property/tree
            config.clearTree(ruleKey);
            // save
            save(config);
            // success
            setStatus(true);

        }
        else {
            LOG.error("rule to delete not found: AccessControlRule[id="
                    + ruleId_ + "]");
        }

    }

}
