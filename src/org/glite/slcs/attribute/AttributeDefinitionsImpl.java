package org.glite.slcs.attribute;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glite.slcs.SLCSConfigurationException;
import org.glite.slcs.config.SLCSConfiguration;

/**
 * Helper class for the AttributeDefintions.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.1 $
 */
public class AttributeDefinitionsImpl extends SLCSConfiguration implements
        AttributeDefinitions {

    /** Logging */
    static private Log LOG = LogFactory.getLog(AttributeDefinitionsImpl.class);

    /** Map of name - {@link AttributeDefinition}s */
    private Map attributeDefinitions_ = null;

    private List requiredAttributeHeaders_ = null;

    private List definedAttributeHeaders_ = null;

    /**
     * Constructor called by factory
     * 
     * @param filename
     *            The attribute definitions XML filename
     * @throws SLCSConfigurationException
     */
    protected AttributeDefinitionsImpl(String filename)
            throws SLCSConfigurationException {
        super();
        LOG.debug("filename: " + filename);
        FileConfiguration configuration = loadConfiguration(filename);
        // setFileConfiguration call checkConfiguration...
        setFileConfiguration(configuration);

        initAttributeDefinitions();

    }

    private void initAttributeDefinitions() throws SLCSConfigurationException {
        // create the map and lists
        attributeDefinitions_ = new HashMap();
        requiredAttributeHeaders_ = new ArrayList();
        definedAttributeHeaders_ = new ArrayList();

        // populate the map and lists
        List attributeDefinitionNames = getList("AttributeDefinition[@name]");
        int nAttributeDefinitions = attributeDefinitionNames.size();
        for (int i = 0; i < nAttributeDefinitions; i++) {
            String name = (String) attributeDefinitionNames.get(i);
            String prefix = "AttributeDefinition(" + i + ")";
            // get header element
            String header = getString(prefix + "[@header]");
            // required is optional (default: false)
            String required = getString(prefix + "[@required]", false);
            if (required != null && required.equals("true")) {
                // add to the required attribute names list
                requiredAttributeHeaders_.add(header);
            }

            // add to the valid attribute names list
            definedAttributeHeaders_.add(header);

            // create a new attribute definition (name and header) with the
            // optional displayName
            String displayName = getString(prefix + "[@displayName]", false);
            AttributeDefinition attributeDef = new AttributeDefinition(name, header, displayName);
            if (required != null && required.equals("true")) {
                attributeDef.setRequired(true);
            }
            // add in the attribute's definitions map
            attributeDefinitions_.put(attributeDef.getName(), attributeDef);

        }
        LOG.info("AttributeDefinitions=" + attributeDefinitions_);
        LOG.info("DefinedAttributeHeaders=" + definedAttributeHeaders_);
        LOG.info("RequiredAttributeHeaders=" + requiredAttributeHeaders_);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.attribute.AttributeDefinitions#getAttributeDefinitions()
     */
    public List getAttributeDefinitions() {
        Collection mapValues = attributeDefinitions_.values();
        List definitions = new ArrayList(mapValues);
        return definitions;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.attribute.AttributeDefinitions#getDisplayName(org.glite.slcs.attribute.Attribute)
     */
    public String getDisplayName(Attribute attribute) {
        String attrName = attribute.getName();
        if (attributeDefinitions_.containsKey(attrName)) {
            AttributeDefinition attrDef = (AttributeDefinition) attributeDefinitions_.get(attrName);
            return attrDef.getDisplayName();
        }
        return null;
    }

    public String getHeader(Attribute attribute) {
        String attrName = attribute.getName();
        if (attributeDefinitions_.containsKey(attrName)) {
            AttributeDefinition attrDef = (AttributeDefinition) attributeDefinitions_.get(attrName);
            return attrDef.getHeader();
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.attribute.AttributeDefinitions#isRequired(org.glite.slcs.attribute.Attribute)
     */
    public boolean isRequired(Attribute attribute) {
        String attrName = attribute.getName();
        if (attributeDefinitions_.containsKey(attrName)) {
            AttributeDefinition attrDef = (AttributeDefinition) attributeDefinitions_.get(attrName);
            return attrDef.isRequired();
        }
        else {
            return false;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.attribute.AttributeDefinitions#setDisplayNames(java.util.List)
     */
    public void setDisplayNames(List attributes) {
        Iterator iter = attributes.iterator();
        while (iter.hasNext()) {
            Attribute attr = (Attribute) iter.next();
            String displayName = getDisplayName(attr);
            attr.setDisplayName(displayName);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.attribute.AttributeDefinitions#getDefinedAttributeHeaders()
     */
    public List getDefinedAttributeHeaders() {
        return definedAttributeHeaders_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.attribute.AttributeDefinitions#getRequiredAttributeHeaders()
     */
    public List getRequiredAttributeHeaders() {
        return requiredAttributeHeaders_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.config.SLCSConfiguration#checkConfiguration()
     */
    protected void checkConfiguration() throws SLCSConfigurationException {
        // TODO Auto-generated method stub
        // TODO like SLCSServerConfiguration

    }

}
