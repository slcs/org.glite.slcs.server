/*
 * $Id: SLCSServerComponentsPlugin.java,v 1.2 2007/06/11 13:10:59 vtschopp Exp $
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html 
 */
package org.glite.slcs.struts;

import java.util.Enumeration;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.action.PlugIn;
import org.apache.struts.config.ModuleConfig;
import org.glite.slcs.SLCSException;
import org.glite.slcs.SLCSServerComponent;
import org.glite.slcs.acl.AccessControlListEditor;
import org.glite.slcs.acl.AccessControlListEditorFactory;
import org.glite.slcs.audit.Auditor;
import org.glite.slcs.audit.AuditorFactory;
import org.glite.slcs.caclient.CAClient;
import org.glite.slcs.caclient.CAClientFactory;
import org.glite.slcs.config.SLCSServerConfiguration;
import org.glite.slcs.dn.DNBuilder;
import org.glite.slcs.dn.DNBuilderFactory;
import org.glite.slcs.group.GroupManager;
import org.glite.slcs.group.GroupManagerFactory;
import org.glite.slcs.policy.CertificatePolicy;
import org.glite.slcs.policy.CertificatePolicyFactory;
import org.glite.slcs.session.SLCSSessions;
import org.glite.slcs.session.SLCSSessionsFactory;

/**
 * The PlugIn initializes and instantiates the necessary {@link SLCSServerComponent}s
 * and shutdowns them when destroyed.
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision: 1.2 $
 */
public class SLCSServerComponentsPlugin implements PlugIn {

    /** Logging */
    static private Log LOG = LogFactory
            .getLog(SLCSServerComponentsPlugin.class);

    /** List of registered {@link SLCSServerComponent}s */
    private Vector components_ = new Vector();

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.struts.action.PlugIn#init(org.apache.struts.action.ActionServlet,
     *      org.apache.struts.config.ModuleConfig)
     */
    public void init(ActionServlet servlet, ModuleConfig moduleConfig)
            throws ServletException {
        try {
            ServletContext context= servlet.getServletContext();
            
            // initialize the SLCS config
            LOG.info("initialize and instantiate SLCSServerConfiguration...");
            SLCSServerConfiguration.initialize(context);
            SLCSServerConfiguration.getInstance();
            
            LOG.info("instantiate SCLSSessions...");
            SLCSSessions sessions = SLCSSessionsFactory.getInstance();
            registerSLCSServerComponent(sessions);

            LOG.info("instantiate Auditor...");
            Auditor auditor = AuditorFactory.getInstance();
            registerSLCSServerComponent(auditor);

            LOG.info("instantiate DNBuilder...");
            DNBuilder dnBuilder = DNBuilderFactory.getInstance();
            registerSLCSServerComponent(dnBuilder);

            LOG.info("instantiate CertificatePolicy...");
            CertificatePolicy policy = CertificatePolicyFactory.getInstance();
            registerSLCSServerComponent(policy);

            LOG.info("instantiate CAClient...");
            CAClient caClient = CAClientFactory.getInstance();
            registerSLCSServerComponent(caClient);

            LOG.info("instantiate AccessControlListEditor...");
            AccessControlListEditor editor= AccessControlListEditorFactory.getInstance();
            registerSLCSServerComponent(editor);

            LOG.info("instantiate GroupManager");
            GroupManager groupManager= GroupManagerFactory.getInstance();
            registerSLCSServerComponent(groupManager);
            
        } catch (SLCSException e) {
            LOG.error("Failed to initialize the SLCS components", e);
            throw new ServletException(e);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.struts.action.PlugIn#destroy()
     */
    public void destroy() {
        LOG.info("Shutdown all SLCS Server Components...");
        Enumeration serverComponents = components_.elements();
        while (serverComponents.hasMoreElements()) {
            SLCSServerComponent component = (SLCSServerComponent) serverComponents
                    .nextElement();
            component.shutdown();
        }
    }

    /**
     * Register a {@link SLCSServerComponent}. All server components will be
     * shutdowned when the servlet is destroyed.
     * 
     * @param component
     *            A SLCS server component object.
     * @see SLCSServerComponent
     */
    protected void registerSLCSServerComponent(SLCSServerComponent component) {
        components_.add(component);
        
    }

}
