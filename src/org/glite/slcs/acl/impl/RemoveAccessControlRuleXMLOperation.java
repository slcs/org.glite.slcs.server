package org.glite.slcs.acl.impl;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glite.slcs.acl.AccessControlRule;

public class RemoveAccessControlRuleXMLOperation extends XMLOperation {

    /** Logging */
    private static Log LOG = LogFactory
            .getLog(RemoveAccessControlRuleXMLOperation.class);

    /**
     * The AccessControlRule to remove
     */
    private AccessControlRule rule_ = null;

    /**
     * Constructor
     * 
     * @param rule
     *            The AccessControlRule to remove.
     */
    public RemoveAccessControlRuleXMLOperation(AccessControlRule rule) {
        super(rule.getGroup());
        rule_ = rule;
    }

    public void process(XMLConfiguration config) {
        
        LOG.info("RemoveAccessControlRule: " + rule_);
        
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
            String ruleGroup = config.getString(rulePrefix + "[@group]");
            if (ruleGroup.equals(rule_.getGroup())) {
                int id = Integer.parseInt(ruleId);
                if (id == rule_.getId()) {
                    ruleKey = rulePrefix;
                    break;
                }
            }
        }

        if (ruleKey != null) {
            LOG.info("delete " + ruleKey);
            // clear property/tree
            config.clearTree(ruleKey);
        
            // save
            save(config);
        }

        

        // signal done
        setDone(true);
    }

}
