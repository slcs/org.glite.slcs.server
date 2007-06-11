/*
 * $Id: SWITCHSLCSCertificatePolicy.java,v 1.4 2007/06/11 12:48:05 vtschopp Exp $
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html 
 */
package org.glite.slcs.policy.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glite.slcs.SLCSException;
import org.glite.slcs.attribute.Attribute;
import org.glite.slcs.config.SLCSServerConfiguration;
import org.glite.slcs.pki.CertificateExtension;
import org.glite.slcs.pki.CertificateExtensionFactory;
import org.glite.slcs.pki.CertificateRequest;
import org.glite.slcs.policy.CertificatePolicy;

/**
 * SWITCHSLCSCertificatePolicy implements the SWITCH SLCS certificate policy.
 * <p>
 * The SWITCH SLCS policy defines the following certificate extensions as
 * mandatory and exclusives:
 * <ul>
 * <li><code>KeyUsage(CRITICAL): Digital Signature + Key Encipherment </code>
 * <li><code>ExtendedKeyUsage: Client Authentication</code>
 * <li><code>CertificatePolicies: POLICY_OID</code>
 * <li><code>SubjectAlternativeName: email:EMAIL_ADDRESS</code>
 * </ul>
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision: 1.4 $
 */
public class SWITCHSLCSCertificatePolicy implements CertificatePolicy {

    /** Logging */
    private static Log LOG = LogFactory.getLog(SWITCHSLCSCertificatePolicy.class);

    /** Map of name-values certificate extension required by the policy */
    private Map requiredCertificateExtensionsMap_ = null;

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.SLCSServerComponent#init(org.glite.slcs.config.SLCSServerConfiguration)
     */
    public void init(SLCSServerConfiguration config) throws SLCSException {
        // read the extensions from the config
        requiredCertificateExtensionsMap_ = new HashMap();
        List certificateExtensionNames = config.getList(SLCSServerConfiguration.COMPONENTSCONFIGURATION_PREFIX
                + ".CertificatePolicy.CertificateExtensions.CertificateExtension[@id]");
        Iterator extensionNames = certificateExtensionNames.iterator();
        for (int i = 0; extensionNames.hasNext(); i++) {
            String extensionName = (String) extensionNames.next();
            String extensionValues = config.getString(SLCSServerConfiguration.COMPONENTSCONFIGURATION_PREFIX
                    + ".CertificatePolicy.CertificateExtensions.CertificateExtension("
                    + i + ")");
            requiredCertificateExtensionsMap_.put(extensionName, extensionValues);
        }
        LOG.info("RequiredCertificateExtensions="
                + requiredCertificateExtensionsMap_);
    }

    /**
     * Returns a list of required {@link CertificateExtension}s, already evaluated with
     * the corresponding user attribute values.
     * 
     * @param attributes Map of user attributes.
     * @return List of {@link CertificateExtension}s
     */
    private List getRequiredCertificateExtensions(Map attributes) {
        List requiredCertificateExtensions = new ArrayList();
        Iterator extensionNames = requiredCertificateExtensionsMap_.keySet().iterator();
        while (extensionNames.hasNext()) {
            String extensionName = (String) extensionNames.next();
            String extensionValues = (String) requiredCertificateExtensionsMap_.get(extensionName);
            if (extensionValues.indexOf("${") != -1) {
                // values contains Shibboleth attribute variable(s), substitute.
                Iterator attributeNames = attributes.keySet().iterator();
                while (attributeNames.hasNext()) {
                    String attributeName = (String) attributeNames.next();
                    String placeholder = "${" + attributeName + "}";
                    if (extensionValues.indexOf(placeholder) != -1) {
                        String replace = "\\$\\{" + attributeName + "\\}";
                        String attributeValue = (String) attributes.get(attributeName);
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("CertificateExtension values:"
                                    + extensionValues + " replace regex:"
                                    + replace + " by:" + attributeValue);
                        }
                        extensionValues = extensionValues.replaceAll(replace, attributeValue);
                    }
                }
            }
            // create the extension with the values expanded.
            CertificateExtension extension = CertificateExtensionFactory.createCertificateExtension(extensionName, extensionValues);
            requiredCertificateExtensions.add(extension);
        }
        return requiredCertificateExtensions;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.policy.CertificatePolicy#getRequiredCertificateExtensions(java.util.List)
     */
    public List getRequiredCertificateExtensions(List attributes) {
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
        return getRequiredCertificateExtensions(attributesMap);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.policy.CertificatePolicy#isCertificateRequestValid(org.glite.slcs.pki.CertificateRequest,
     *      java.util.Map)
     */
    public boolean isCertificateRequestValid(CertificateRequest request,
            List attributes) throws SLCSException {
        // check validity of extensions
        List requiredCertificateExtensions = getRequiredCertificateExtensions(attributes);
        List certificateRequestExtensions = request.getCertificateExtensions();
        if (!certificateRequestExtensions.containsAll(requiredCertificateExtensions)) {
            LOG.error("CertificateSigningRequest does not contain all required CertificateExtensions");
            return false;

        }
        else if (!requiredCertificateExtensions.containsAll(certificateRequestExtensions)) {
            LOG.error("CertificateSigningRequest contains more CertificateExtensions than required");
            return false;
        }

        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.SLCSServerComponent#shutdown()
     */
    public void shutdown() {
    }

}
