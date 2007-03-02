package org.glite.slcs.acl.impl;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glite.slcs.Attribute;
import org.glite.slcs.acl.AccessControlRule;

/**
 * Replace an existing rule, identified by its id, with a new one.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.1 $
 */
public class ReplaceAccessControlRuleXMLOperation extends XMLOperation {

    /** Logging */
    private static Log LOG = LogFactory.getLog(ReplaceAccessControlRuleXMLOperation.class);

    /**
     * The new AccessControlRule
     */
    private AccessControlRule rule_ = null;

    /**
     * Constructor
     * 
     * @param rule
     *            The AccessControlRule to replace.
     */
    public ReplaceAccessControlRuleXMLOperation(AccessControlRule rule) {
        super();
        rule_ = rule;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.acl.impl.XMLOperation#doProcessing(org.apache.commons.configuration.XMLConfiguration)
     */
    protected void doProcessing(XMLConfiguration config) {

        if (LOG.isDebugEnabled())
            LOG.debug("replacing: " + rule_);

        // find index
        int i = 0;
        String ruleId = null;
        String ruleKey = null;
        while (true) {
            String rulePrefix = "AccessControlRule(" + i + ")";
            i++;
            // read current id
            ruleId = config.getString(rulePrefix + "[@id]");
            if (ruleId == null) {
                // no more rules
                break;
            }
            int id = -1;
            try {
                id = Integer.parseInt(ruleId);
            } catch (Exception e) {
                LOG.error(e);
            }
            if (id == rule_.getId()) {
                ruleKey = rulePrefix;
                // rule found
                break;
            }
        }

        if (ruleKey != null) {
            LOG.debug("replace AccessControlRule[" + ruleId + "]: " + rule_);
            // replace group
            config.setProperty(ruleKey + "[@group]", rule_.getGroup());
            // clear all existing rule attributes
            config.clearTree(ruleKey + ".Attribute");
            // add new rule attributes
            List ruleAttributes = rule_.getAttributes();
            Iterator attributes = ruleAttributes.iterator();
            while (attributes.hasNext()) {
                Attribute attribute = (Attribute) attributes.next();
                config.addProperty(ruleKey + ".Attribute(-1)", attribute.getValue());
                config.addProperty(ruleKey + ".Attribute[@name]", attribute.getName());
            }
            // save the XML file
            save(config);
            // success
            setStatus(true);

        }
        else {
            LOG.error("rule to replace: " + rule_ + " not found!");
        }

    }

}
