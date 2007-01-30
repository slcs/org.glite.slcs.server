package org.glite.slcs.acl.impl;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glite.slcs.Attribute;
import org.glite.slcs.acl.AccessControlRule;

public class ListAccessControlRulesXMLOperation extends XMLOperation {

    private static Log LOG = LogFactory
            .getLog(ListAccessControlRulesXMLOperation.class);

    private List accessControlRules_ = null;

    public ListAccessControlRulesXMLOperation(String group) {
        super(group);
        accessControlRules_ = new LinkedList();
    }

    public void process(XMLConfiguration config) {
        
        String myGroup = getGroup();        
        LOG.info("ListAccessControlRules: " + myGroup);

        
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

            if (myGroup != null && !ruleGroup.equals(myGroup)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(rulePrefix + "[@group]=" + ruleGroup
                            + " skipping...");
                }
                // other group, skipping
                continue;

            }
            int ruleId= config.getInt(rulePrefix + "[@id]");
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
                Attribute attribute = new Attribute(name, value);
                rule.addAttribute(attribute);
            }

            accessControlRules_.add(rule);
            
        }

        setDone(true);
    }

    public List getAccessControlRules() {
        return accessControlRules_;
    }

}
