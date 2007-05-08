/*
 * $Id: SLCSServerVersion.java,v 1.4 2007/05/08 09:03:32 vtschopp Exp $
 * 
 * Created on May 5, 2006 by tschopp
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html
 */
package org.glite.slcs;

/**
 * SLCSServerVersion and Copyright constants.
 *
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision: 1.4 $
 */
public class SLCSServerVersion {
    /** SLCSServerVersion as String */
    static public final String VERSION= "1.0.0";
    /** Major version number */
    static public final int MAJOR= 1;
    /** Minor version number */
    static public final int MINOR= 0;
    /** Revision version number */
    static public final int REVISION= 0;
    
    /** Build number */
    static public final String BUILD= "@BUILD.NUMBER@";
    
    /** Copyright */
    static public final String COPYRIGHT= "Copyright (c) Members of the EGEE Collaboration";
    
    private SLCSServerVersion() {}
    
    /**
     * @return The version of the server in format MAJOR.MINOR.REVISION
     */
    static public String getVersion() {
        StringBuffer sb= new StringBuffer();
        sb.append(MAJOR).append('.');
        sb.append(MINOR).append('.');
        sb.append(REVISION);
        return sb.toString();
    }

    /**
     * @return The full version of the server in format MAJOR.MINOR.REVISION.BUILD
     */
    static public String getFullVersion() {
        StringBuffer sb= new StringBuffer();
        sb.append(MAJOR).append('.');
        sb.append(MINOR).append('.');
        sb.append(REVISION).append('.');
        sb.append(BUILD);
        return sb.toString();
    }

}
