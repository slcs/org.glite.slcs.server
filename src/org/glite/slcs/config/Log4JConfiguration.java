/*
 * Copyright (c) Members of the EGEE Collaboration. 2004. 
 * See http://www.eu-egee.org/partners/ for details on the copyright
 * holders.  
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0 
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 * 
 * $Id: Log4JConfiguration.java,v 1.1 2007/09/26 14:32:43 vtschopp Exp $ 
 */
package org.glite.slcs.config;

import javax.servlet.ServletContext;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * Log4J configuration engine.
 * <p>
 * Loads the configuration file specified in the web application context as
 * <code>Log4JConfigurationFile</code> and configure the Log4J engine with it.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.1 $
 */
public class Log4JConfiguration {

    /**
     * Parameter name in the context or in the web.xml file
     */
    static private String LOG4J_CONFIGURATION_FILE_KEY = "Log4JConfigurationFile";

    /**
     * Watchdog to prevent multiple load
     */
    static private boolean LOG4J_CONFIGURED = false;

    /**
     * Configures the Log4J engine with an external <code>log4j.xml</code>
     * file. The Servlet context must define a
     * <code>Log4JConfigurationFile</code> parameter with the absolute path of
     * the Log4J config file.
     * 
     * @param ctxt
     *            The {@link ServletContext} object.
     */
    public static void configure(ServletContext ctxt) {
        if (!LOG4J_CONFIGURED) {
            // try to configure log4j
            if (ctxt.getInitParameter(LOG4J_CONFIGURATION_FILE_KEY) != null) {
                String log4jConfig = ctxt.getInitParameter(LOG4J_CONFIGURATION_FILE_KEY);
                System.out.println("INFO: "
                        + Log4JConfiguration.class.getName() + ": load "
                        + LOG4J_CONFIGURATION_FILE_KEY + "=" + log4jConfig);
                if (log4jConfig.endsWith(".xml")) {
                    DOMConfigurator.configure(log4jConfig);
                }
                else {
                    PropertyConfigurator.configure(log4jConfig);
                }
            }
            else {
                System.err.println("WARN: "
                        + Log4JConfiguration.class.getName() + ": "
                        + LOG4J_CONFIGURATION_FILE_KEY
                        + " not found in ServletContext.");

            }
            LOG4J_CONFIGURED = true;
        }
    }
}
