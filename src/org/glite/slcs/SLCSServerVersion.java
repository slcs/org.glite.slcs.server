/*
 * $Id: SLCSServerVersion.java,v 1.20 2008/07/21 08:41:28 vtschopp Exp $
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
 * @version $Revision: 1.20 $
 */
public class SLCSServerVersion {

    /** Major version number */
    static public final int MAJOR= 1;
    /** Minor version number */
    static public final int MINOR= 4;
    /** Revision version number */
    static public final int REVISION= 1;    
    /** Build number */
    static public final int BUILD= 1;
    
    /** Copyright */
    static public final String COPYRIGHT= "Copyright (c) 2007-2008 Members of the EGEE Collaboration";
    
    private SLCSServerVersion() {}
    
    /**
     * @return The version of the server in format MAJOR.MINOR.REVISION-BUILD
     */
    static public String getVersion() {
        StringBuffer sb= new StringBuffer();
        sb.append(MAJOR).append('.');
        sb.append(MINOR).append('.');
        sb.append(REVISION).append('-');
        sb.append(BUILD);
        return sb.toString();
    }
    
    /**
     * @return The copyright
     */
    static public String getCopyright() {
        return COPYRIGHT;
    }


}
