package org.glite.slcs.acl.impl;

import java.util.List;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glite.slcs.Attribute;
import org.glite.slcs.acl.AccessControlRule;

public class GetAccessControlRuleXMLOperation extends XMLOperation {

    /**
     * Logging
     */
    private static Log LOG = LogFactory.getLog(GetAccessControlRuleXMLOperation.class);

    /**
     * The rule id to search
     */
    private int ruleId_ = -1;

    /**
     * The rule to search
     */
    private AccessControlRule accessControlRule_ = null;

    /**
     * Constructor
     * 
     * @param ruleId
     *            The rule id to search
     */
    public GetAccessControlRuleXMLOperation(int ruleId) {
        super();
        ruleId_ = ruleId;
    }

    protected void doProcessing(XMLConfiguration config) {

        if (LOG.isDebugEnabled())
            LOG.debug("get AccessControlRule[id=" + ruleId_ + "]");

        // search the rule id
        int i = 0;
        String ruleKey = null;
        while (true) {
            String rulePrefix = "AccessControlRule(" + i + ")";
            i++;
            // read current id
            String ruleId = config.getString(rulePrefix + "[@id]");
            if (ruleId == null) {
                // no more rules
                break;
            }
            int id = Integer.parseInt(ruleId);
            if (id == ruleId_) {
                ruleKey = rulePrefix;
                // rule found
                break;
            }
        }

        if (ruleKey != null) {
            // rule found
            LOG.debug("found AccessControlRule[id=" + ruleId_ + "]");
            // create rule object
            String ruleGroup = config.getString(ruleKey + "[@group]");
            AccessControlRule rule = new AccessControlRule(ruleId_, ruleGroup);
            List attributeNames = config.getList(ruleKey + ".Attribute[@name]");
            if (attributeNames.isEmpty()) {
                // error, skipping
                LOG.warn(ruleKey + ": no attribute in rule, skipping...");
            }
            else {
                List attributeValues = config.getList(ruleKey + ".Attribute");
                for (int j = 0; j < attributeNames.size(); j++) {
                    String name = (String) attributeNames.get(j);
                    String value = (String) attributeValues.get(j);
                    Attribute attribute = new Attribute(name, value);
                    rule.addAttribute(attribute);
                }

                accessControlRule_ = rule;

                // success
                setStatus(true);

            }
        }
        else {
            LOG.error("rule not found: AccessControlRule[id=" + ruleId_ + "]");
        }
    }

    /**
     * Once done, the rule can be retrieved.
     * 
     * @return the matching {@link AccessControlRule} or <code>null</code> if
     *         not found.
     */
    public Object getResult() {
        return accessControlRule_;
    }

}
