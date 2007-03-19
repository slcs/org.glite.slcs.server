package org.glite.slcs.attribute;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glite.slcs.SLCSConfigurationException;
import org.glite.slcs.config.SLCSConfiguration;
import org.glite.slcs.util.Utils;

/**
 * Helper class for the AttributeDefintions.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.2 $
 */
public class AttributeDefinitionsImpl extends SLCSConfiguration implements
        AttributeDefinitions {

    /** Logging */
    static private Log LOG = LogFactory.getLog(AttributeDefinitionsImpl.class);

    /** Map of name - {@link AttributeDefinition}s */
    private Map attributeDefinitions_ = null;

    /** List of required attribute names */
    private List requiredAttributeNames_ = null;

    /** List of all defined attribute header names */
    private Map headerNameMapping_ = null;

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

        parseAttributeDefinitions();

    }

    private void parseAttributeDefinitions() throws SLCSConfigurationException {
        // create the map and lists
        attributeDefinitions_ = new HashMap();
        headerNameMapping_ = new HashMap();
        requiredAttributeNames_ = new ArrayList();

        // populate the map and lists
        List attributeDefinitionNames = getList("AttributeDefinition[@name]");
        int nAttributeDefinitions = attributeDefinitionNames.size();
        for (int i = 0; i < nAttributeDefinitions; i++) {
            String name = (String) attributeDefinitionNames.get(i);
            String prefix = "AttributeDefinition(" + i + ")";
            // get HTTP header name
            String header = getString(prefix + "[@header]");
            // get the display name
            String displayName = getString(prefix + "[@displayName]");
            // required is optional (default: false)
            String required = getString(prefix + "[@required]", false);
            if (required != null && required.equals("true")) {
                // add to the required attribute names list
                requiredAttributeNames_.add(name);
            }
            // add the attribute header - names in the mapping table
            headerNameMapping_.put(header, name);
            // create a new attribute definition
            AttributeDefinition attributeDef = new AttributeDefinition(name, header, displayName);
            if (required != null && required.equals("true")) {
                attributeDef.setRequired(true);
            }
            // add in the attribute's definitions map
            attributeDefinitions_.put(name, attributeDef);

        }
        LOG.info("AttributeDefinitions=" + attributeDefinitions_);
        LOG.info("HeaderNameMapping=" + headerNameMapping_);
        LOG.info("RequiredAttributeNames=" + requiredAttributeNames_);
    }

    public List getUserAttributes(HttpServletRequest request) {
        // list of user attributes
        List attributes = new ArrayList();
        Enumeration headers = request.getHeaderNames();
        while (headers.hasMoreElements()) {
            String headerName = (String) headers.nextElement();
            if (headerNameMapping_.containsKey(headerName)) {
                String headerValue = request.getHeader(headerName);
                // add only not null and not empty attributes
                if (headerValue != null && !headerValue.equals("")) {
                    // if (LOG.isDebugEnabled()) {
                    // LOG.debug("Header: " + headerName + "=" + headerValue);
                    // }
                    String decodedValue = Utils.convertShibbolethUTF8ToUnicode(headerValue);
                    // multi-value attributes are stored as multiple attributes
                    String[] attrValues = decodedValue.split(";");
                    for (int i = 0; i < attrValues.length; i++) {
                        String attrName = getAttributeName(headerName);
                        String attrValue = attrValues[i];
                        attrValue = attrValue.trim();
                        Attribute attribute = new Attribute(attrName, attrValue);
                        attributes.add(attribute);
                    }
                }

            }
        }
        // set all display name as defined by the attribute definitions
        setDisplayNames(attributes);

        if (LOG.isDebugEnabled()) {
            LOG.debug("attributes=" + attributes);
        }
        return attributes;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.attribute.AttributeDefinitions#getAttributeDefinitionsList()
     */
    public List getAttributeDefinitionsList() {
        Collection definitions = attributeDefinitions_.values();
        List attributeDefinitions = new ArrayList(definitions);
        // XXX: sort by display name
        Collections.sort(attributeDefinitions, new AttributeDefinitionComparator());
        return attributeDefinitions;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.attribute.AttributeDefinitions#getDisplayName(org.glite.slcs.attribute.Attribute)
     */
    public String getAttributeDisplayName(Attribute attribute) {
        String attrName = attribute.getName();
        if (attributeDefinitions_.containsKey(attrName)) {
            AttributeDefinition attrDef = (AttributeDefinition) attributeDefinitions_.get(attrName);
            return attrDef.getDisplayName();
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.attribute.AttributeDefinitions#getHeader(org.glite.slcs.attribute.Attribute)
     */
    public String getAttributeHeader(Attribute attribute) {
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
     * @see org.glite.slcs.attribute.AttributeDefinitions#getName(java.lang.String)
     */
    public String getAttributeName(String header) {
        if (headerNameMapping_.containsKey(header)) {
            String name = (String) headerNameMapping_.get(header);
            return name;
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.attribute.AttributeDefinitions#isRequired(org.glite.slcs.attribute.Attribute)
     */
    public boolean isAttributeRequired(Attribute attribute) {
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
            String displayName = getAttributeDisplayName(attr);
            attr.setDisplayName(displayName);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.attribute.AttributeDefinitions#getDefinedAttributeHeaders()
     */
    public Map getAttributesHeaderNameMapping() {
        return headerNameMapping_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.attribute.AttributeDefinitions#getRequiredAttributeNames()
     */
    public List getRequiredAttributeNames() {
        return requiredAttributeNames_;
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
